/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package response;

import data.DataAccess;
import data.DataSort;
import data.DatabaseConnection;
import data.Statut;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SylvainPro
 */

public class MultipleResponse implements Serializable {
     
    private static final Integer NB_MAX = 20;
    private static long timeMax = 5000;
    
    private DatabaseConnection connection;
    
    private HeaderResponse header;
    private InfoResultsResponse infoResults;
    private HashMap<Integer,UniteLegaleResponse> results;
    
    /* ----- Recherche par nom ----- */
    
    public MultipleResponse(DatabaseConnection connection, String recherche, Integer numPage) {
        this.connection = connection;
        // Initialisation
        header = new HeaderResponse();
        infoResults = new InfoResultsResponse();
        infoResults.setLibelleRecherche(recherche);
        infoResults.setCurrentPage(numPage);
        results = new HashMap<>();
        
        if (!recherche.equals("")) {
            if (recherche.replace(" ","").matches("[0-9]{9}") | recherche.replace(" ","").matches("[0-9]{14}")) {
                try {
                    String siren = recherche.replace(" ","").substring(0,9);
                    UniteLegaleResponse uniteLegaleResponse = new UniteLegaleResponse(connection, siren);
                    if (uniteLegaleResponse!=null) {
                        results.put(1,uniteLegaleResponse);
                        header.setStatut(Statut.OK);
                    } else {
                        header.setStatut(Statut.NOT_FOUND);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(MultipleResponse.class.getName()).log(Level.SEVERE, null, ex);
                    header.setStatut(Statut.SERVER_ERROR);
                }
            
            } else {
                ExecutorService service = Executors.newFixedThreadPool(2);
                Callable<Void> taskMatch = new Callable<Void>() {
                    @Override
                    public Void call() {
                        setMapUnitesLegalesDeniominationLike();
                        return null;
                    }
                };
                Callable<Void> taskLike = new Callable<Void>() {
                    @Override
                    public Void call() {
                        setMapUnitesLegalesDenominationSimilarTo();
                        return null;
                    }
                };
                Collection<Callable<Void>> tasks = Arrays.asList(taskMatch,taskLike);
                try {
                    Collection<Future<Void>> futureMatch = service.invokeAll(tasks,timeMax,TimeUnit.MILLISECONDS);
                    if (!results.isEmpty()) {
                        header.setStatut(Statut.OK);
                    } else {
                        header.setStatut(Statut.NO_RESULT);
                    }
                } catch (InterruptedException ex) {
                    header.setStatut(Statut.TIME_OUT);
                    Logger.getLogger(MultipleResponse.class.getName()).log(Level.SEVERE, null, ex);
                }
            } 
        } else {
            header.setStatut(Statut.BAD_REQUEST);;
        }     
    }
    
    private void setMapUnitesLegalesDeniominationLike () {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Callable<HashMap<Integer,UniteLegaleResponse>> task = new Callable<HashMap<Integer,UniteLegaleResponse>>() {
            @Override
            public HashMap<Integer,UniteLegaleResponse> call() throws SQLException {
                HashMap<Integer,UniteLegaleResponse> mapUnitesLegales = new HashMap<>();
                ArrayList<UniteLegaleResponse> listUnitesLegales = UniteLegaleResponse.getListUnitesLegalesByExactName(connection, infoResults.getLibelleRecherche());
                UniteLegaleResponse[] arrayUnitesLegales = DataSort.getDefaultMatchingUnitesLegales(listUnitesLegales,infoResults.getLibelleRecherche());
                Integer ref = 1;
                for (UniteLegaleResponse uniteLegale : arrayUnitesLegales) {
                    mapUnitesLegales.put(ref,uniteLegale);
                    ref++;
                }
                return mapUnitesLegales;
            }
        };
        Future<HashMap<Integer,UniteLegaleResponse>> future = service.submit(task);
        try {
            HashMap<Integer,UniteLegaleResponse> mapUnitesLegales = future.get(timeMax,TimeUnit.MILLISECONDS);
            if (results.isEmpty()) {
                Integer numResult = NB_MAX*(infoResults.getCurrentPage()-1)+1;
                header.setStatut(Statut.NO_RESULT);
                while (numResult<=NB_MAX*infoResults.getCurrentPage() & mapUnitesLegales.containsKey(numResult)) {
                    results.put(numResult,mapUnitesLegales.get(numResult));
                    numResult++;
                }
                infoResults.setNbResults(mapUnitesLegales.size());
                infoResults.setNbPage(mapUnitesLegales.size()/NB_MAX+1);
            }
        } catch (final TimeoutException e) {
            header.setStatut(Statut.TIME_OUT);
        } catch (final InterruptedException | ExecutionException e) {
            header.setStatut(Statut.SERVER_ERROR);
        } finally {
            future.cancel(true);
            service.shutdown();
        }
    }
    private void setMapUnitesLegalesDenominationSimilarTo () {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Callable<HashMap<Integer,UniteLegaleResponse>> task = new Callable<HashMap<Integer,UniteLegaleResponse>>() {
            @Override
            public HashMap<Integer,UniteLegaleResponse> call() throws SQLException {
                HashMap<Integer,UniteLegaleResponse> mapUnitesLegales = new HashMap<>();
                ArrayList<UniteLegaleResponse> listUnitesLegales = UniteLegaleResponse.getListUnitesLegalesBySimilarName(connection, infoResults.getLibelleRecherche());
                UniteLegaleResponse[] arrayUnitesLegales = DataSort.getDefaultMatchingUnitesLegales(listUnitesLegales,infoResults.getLibelleRecherche());
                Integer ref = 1;
                for (UniteLegaleResponse uniteLegale : arrayUnitesLegales) {
                    mapUnitesLegales.put(ref,uniteLegale);
                    ref++;
                }
                return mapUnitesLegales;
            }
        };
        Future<HashMap<Integer,UniteLegaleResponse>> future = service.submit(task);
        try {
            HashMap<Integer,UniteLegaleResponse> mapUnitesLegales = future.get(timeMax,TimeUnit.MILLISECONDS);
            Integer numResult = NB_MAX*(infoResults.getCurrentPage()-1)+1;
            while (numResult<=NB_MAX*infoResults.getCurrentPage() & mapUnitesLegales.containsKey(numResult)) {
                results.put(numResult,mapUnitesLegales.get(numResult));
                numResult++;
            }
            infoResults.setNbResults(mapUnitesLegales.size());
            infoResults.setNbPage(mapUnitesLegales.size()/NB_MAX+1);
            //results.putAll(future.get(5,TimeUnit.SECONDS));
        } catch (final TimeoutException e) {
        } catch (final InterruptedException | ExecutionException e) {
        } finally {
            future.cancel(true);
            service.shutdown();
        }
    }
    
    /* ----- Ranking ----- */
    
    public MultipleResponse(final DatabaseConnection connection, String activite, String region, String departement) {
        this.connection = connection;
        header = new HeaderResponse();
        infoResults = new InfoResultsResponse();
        
        if (activite!=null & (region!=null | departement!=null)) {
            if(activite.matches("{0-9]{2}") & region.matches("[0-9]{2}") & (departement.matches("[0-9]{2}") | departement.matches("[0-9]{3}"))) {
                try {
                    ArrayList<UniteLegaleResponse> listUnitesLegales = UniteLegaleResponse.getListUnitesLegalesByCharacteristics(connection, activite, region, departement);
                    infoResults.setNbResults(listUnitesLegales.size());
                    ExecutorService service = Executors.newCachedThreadPool();
                    Collection<Callable<ProfilSocialResponse>> tasks = new ArrayList<>();
                    for (final UniteLegaleResponse uniteLegale : listUnitesLegales) {
                        tasks.add(new Callable<ProfilSocialResponse>(){
                    @Override
                    public ProfilSocialResponse call() {
                        try {
                            return new ProfilSocialResponse(connection, uniteLegale);
                        } catch (SQLException ex) {
                            Logger.getLogger(MultipleResponse.class.getName()).log(Level.SEVERE, null, ex);
                            return null;
                        }
                    }
                });
                    }
                    List<Future<ProfilSocialResponse>> futures = service.invokeAll(tasks);
                    List<ProfilSocialResponse> listProfilsSociaux = new ArrayList<>();
                   for (Future<ProfilSocialResponse> future : futures) {
                        listProfilsSociaux.add(future.get());
                    }
                    results = DataSort.getDefaultRankingUnitesLegales(listProfilsSociaux);
                    header.setStatut(Statut.OK);
                    
                } catch (SQLException ex) {
                    header.setStatut(Statut.SERVER_ERROR);
                    Logger.getLogger(MultipleResponse.class.getName()).log(Level.SEVERE, null, ex);
                }catch (InterruptedException | ExecutionException ex) {
                    header.setStatut(Statut.SERVER_ERROR);
                    Logger.getLogger(MultipleResponse.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                header.setStatut(Statut.BAD_REQUEST);
            }
        } else {
            header.setStatut(Statut.BAD_REQUEST);
        }

    }
    
    
    /* ----- GETTERS ---- */

    public HeaderResponse getHeader() {
        return header;
    }

    public InfoResultsResponse getInfoResults() {
        return infoResults;
    }
    
    public HashMap<Integer, UniteLegaleResponse> getResults() {
        return results;
    }
    
}
