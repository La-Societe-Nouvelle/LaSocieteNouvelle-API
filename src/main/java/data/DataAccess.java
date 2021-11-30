/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import meta.Indicateur;
import response.UniteLegaleResponse;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author SylvainPro
 */

public class DataAccess {
     
    /* ----- UNITE LEGALE ----- */
    
    public static ResultSet getUniteLegaleData(DatabaseConnection connection, String siren) throws SQLException 
    {
        String query = 
                "SELECT uniteLegale.*, "
                    + "etablissement.codePostalEtablissement, "
                    + "etablissement.libelleCommuneEtablissement, "
                    + "activitePrincipale.libelle AS activitePrincipaleLibelle "
                + "FROM sirene.unitesLegales uniteLegale "
                + "LEFT JOIN sirene.etablissements etablissement "
                    + "ON etablissement.siret = (uniteLegale.siren || uniteLegale.nicSiegeUniteLegale) "
                + "LEFT JOIN sirene.activitePrincipale activitePrincipale "
                    + "ON activitePrincipale.code = uniteLegale.activitePrincipaleUniteLegale "
                + "WHERE uniteLegale.siren = '" + siren + "' "
                    + "AND uniteLegale.statutDiffusionUniteLegale = 'O';";
        
        ResultSet resultSet = connection.executeQuery(query);
        return resultSet;
    }
    
    public static ResultSet getEtablissementsData(DatabaseConnection connection, String siren) throws SQLException
    {
        String query =
                "SELECT "
                // isActivitesArtisanales
                    + "ROUND(SUM( "
                        + "CASE "
                            + "WHEN LENGTH(etablissement.activitePrincipaleRegistreMetiersEtablissement) > 0 THEN 1 "
                            + "ELSE 0 "
                        + "END) / SUM(1) * 100) = 100 "
                    + "AS isActivitesArtisanales, "
                // isLocalisationEtranger
                    + "ROUND(SUM( "
                        + "CASE "
                            + "WHEN etablissement.codePaysEtrangerEtablissement LIKE '99%' THEN 1 "
                            + "ELSE 0 "
                        + "END)) >  0 "
                    + "AS isLocalisationEtranger "
                // etablissements
                + "FROM sirene.etablissements etablissement "
                + "WHERE etablissement.siren = '" + siren + "' "
                    + "AND etablissement.statutDiffusionEtablissement = 'O' "
                    + "AND etablissement.etatAdministratifEtablissement = 'A';";
                
        ResultSet resultSet = connection.executeQuery(query);
        return resultSet;
    }
    
    public static ResultSet getUnitesLegalesDenominationLike (DatabaseConnection connection, String denomination) throws SQLException 
    {
        String recherche = Normalizer.normalize(denomination,Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]","").replace("'","''").toUpperCase();
        
        String query = 
                "SELECT uniteLegale.*,etablissement.*, activitePrincipale.libelle AS activitePrincipaleLibelle "
                + "FROM sirene.unitesLegales uniteLegale "
                + "JOIN sirene.etablissements etablissement "
                    + "ON etablissement.siret = (uniteLegale.siren || uniteLegale.nicSiegeUniteLegale) "
                + "JOIN sirene.activitePrincipale activitePrincipale "
                    + "ON activitePrincipale.code = uniteLegale.activitePrincipaleUniteLegale "
                + "WHERE "
                    + "( (uniteLegale.denominationUniteLegale LIKE '" + recherche + "') "
                        + "OR (uniteLegale.prenom1UniteLegale || ' ' || uniteLegale.nomUniteLegale LIKE '" + recherche + "') "
                        + "OR (uniteLegale.nomUniteLegale LIKE '" + recherche + "') "
                        + "OR (uniteLegale.denominationUsuelle1UniteLegale LIKE '" + recherche + "') "
                        + "OR (uniteLegale.sigleUniteLegale LIKE '" + recherche + "') ) "
                    + "AND uniteLegale.statutDiffusionUniteLegale = 'O' "
                    + "AND uniteLegale.etatAdministratifUniteLegale = 'A' "
                + "LIMIT 10000;";
        
        ResultSet resultSet = connection.executeQuery(query);
        return resultSet;
    }
    public static ResultSet getUnitesLegalesDenominationSimilarTo (DatabaseConnection connection, String denomination) throws SQLException 
    {
        String recherche = Normalizer.normalize(denomination,Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replace("'","''").toUpperCase();
        //String recherchePartitionnee = recherche.replace("[^a-zA-Z0-9]","%");
        
        String query = 
                "SELECT uniteLegale.*,etablissement.*, activitePrincipale.libelle AS activitePrincipaleLibelle "
                + "FROM sirene.unitesLegales uniteLegale "
                + "JOIN sirene.etablissements etablissement "
                    + "ON etablissement.siret = (uniteLegale.siren || uniteLegale.nicSiegeUniteLegale) "
                + "JOIN sirene.activitePrincipale activitePrincipale "
                    + "ON activitePrincipale.code = uniteLegale.activitePrincipaleUniteLegale "
                + "WHERE "
                    + "( (uniteLegale.denominationUniteLegale ~* '\\y" + recherche + "\\y') "
                        + "OR (uniteLegale.prenom1UniteLegale || ' ' || uniteLegale.nomUniteLegale ~* '\\y" + recherche + "\\y') "
                        //+ "OR (uniteLegale.denominationUsuelle1UniteLegale ~* '\\y" + recherche + "\\y') "
                        + "OR (uniteLegale.sigleUniteLegale = '" + recherche + "') "
                    + ") "
                    + "AND uniteLegale.statutDiffusionUniteLegale = 'O' "
                    + "AND uniteLegale.etatAdministratifUniteLegale = 'A' "
                    + "AND (uniteLegale.categorieJuridiqueUniteLegale LIKE '5%' "
                        + "OR uniteLegale.categorieJuridiqueUniteLegale = '1000' "
                        + "OR uniteLegale.categorieJuridiqueUniteLegale LIKE '31%' "
                    + ") "
                + "LIMIT 10000;";
        
        ResultSet resultSet = connection.executeQuery(query);
        return resultSet;
    }

    /* ----- RECHERCHE MULTICRITERES ----- */

    public static ResultSet getListUnitesLegales(DatabaseConnection connection, String activite,String region,String departement) throws SQLException
    {
        String query = 
                "SELECT uniteLegale.*,etablissement.*, activitePrincipale.libelle AS activitePrincipaleLibelle "
                + "FROM sirene.unitesLegales uniteLegale "
                + "JOIN sirene.etablissements etablissement "
                    + "ON etablissement.siret = (uniteLegale.siren || uniteLegale.nicSiegeUniteLegale) "
                + "JOIN sirene.activitePrincipale activitePrincipale "
                    + "ON activitePrincipale.code = uniteLegale.activitePrincipaleUniteLegale "
                + "JOIN sirene.communes commune "
                    + "ON commune.codeCommune = etablissement.codeCommuneEtablissement "
                + "WHERE "
                    + "uniteLegale.statutDiffusionUniteLegale = 'O' "
                    + "AND uniteLegale.etatAdministratifUniteLegale = 'A' "
                    + "AND etablissement.statutDiffusionEtablissement = 'O' "
                    + "AND etablissement.etatAdministratifEtablissement = 'A' ";
        // ajout conditions
        if (activite!=null) {
            query = query
                    + "AND (SUBSTRING(uniteLegale.activitePrincipaleUniteLegale,1," + activite.length() + ") = '" + activite + "' "
                    + "OR SUBSTRING(etablissement.activitePrincipaleEtablissement,1," + activite.length() + ") = '" + activite + "') ";
        }
        if (region!=null) {
            query = query
                    + "AND commune.codeRegion = '" + region + "' ";
        }
        if (departement!=null) {
            query = query
                    + "AND commune.codeDepartement = '" + departement + "' ";
        }
        // fin requête
        query = query
                    + "AND uniteLegale.siren IN (SELECT DISTINCT(siren) FROM donnees.unitesLegales) "
                + "LIMIT 1000;";
        
        ResultSet resultSet = connection.executeQuery(query);
        return resultSet;
    }
    
    /* ----- DONNEES PUBLIEES ----- */
    
    public static DataResult getIndicateurData(DatabaseConnection connection, Indicateur indicateur, UniteLegaleResponse uniteLegaleResponse) throws SQLException 
    {
        String query = "SELECT * "
                + "FROM donnees.unitesLegales donnee "
                + "WHERE donnee.siren = '"+uniteLegaleResponse.getSiren()+"' "
                    + "AND indic = '"+indicateur.getCode()+"' "
                + "ORDER BY donnee.year DESC;";
        
        ResultSet resultSet = connection.executeQuery(query);
        
        if (resultSet.next()) {
            DataResult result = new DataResult(resultSet);
            return result;
        } else {
            return null;
        }
    }
    
    /* ----- DONNEES PAR DEFAUT ----- */
        
    public static DataResult getDivisionData(DatabaseConnection connection, 
                                            Indicateur indicateur, 
                                            String area, 
                                            String division, 
                                            String flow) throws SQLException 
    {
        String query = "SELECT * "
            + "FROM echo.divisionsData "
            + "WHERE indic = '"+indicateur.getCode()+"' AND area = '"+area+"' AND division = '"+division+"' AND flow = '"+flow+"' "
            + "ORDER BY year DESC;";
        
        ResultSet resultSet = connection.executeQuery(query);
            
        if (resultSet.next()) 
        {
            DataResult result = new DataResult(resultSet);
            return result;
        } 
        else { return null; }
    }
    
    public static DataResult getBrancheData(DatabaseConnection connection, 
                                            Indicateur indicateur, 
                                            String area, 
                                            String branche, 
                                            String flow) throws SQLException 
    {
        String query = "SELECT * "
            + "FROM echo.branchesData "
            + "WHERE indic = '"+indicateur.getCode()+"' AND area = '"+area+"' AND branche = '"+branche+"' AND flow = '"+flow+"' "
            + "ORDER BY year DESC;";
            
        ResultSet resultSet = connection.executeQuery(query);
            
        if (resultSet.next()) 
        {
            DataResult result = new DataResult(resultSet);
            return result;
        } 
        else { return null; }
    }
    
    public static DataResult getAreaData(DatabaseConnection connection, 
                                         Indicateur indicateur, 
                                         String area, 
                                         String flow) throws SQLException 
    {
        String query = "SELECT * "
            + "FROM echo.areasData "
            + "WHERE indic = '"+indicateur.getCode()+"' AND area = '"+area+"' AND flow = '"+flow+"' "
            + "ORDER BY year DESC;";
            
        ResultSet resultSet = connection.executeQuery(query);
            
        if (resultSet.next()) 
        {
            DataResult result = new DataResult(resultSet);
            return result;
        } 
        else { return null; }
    }
        
    public static DataResult getDefaultData(DatabaseConnection connection, Indicateur indicateur) throws SQLException 
    {
        String query = "SELECT * "
            + "FROM echo.defaultData "
            + "WHERE indic = '"+indicateur.getCode()+"' AND geo = '_DV' AND nace = '00' AND flow = 'GAP' "
            + "ORDER BY year DESC;";
        
        ResultSet resultSet = connection.executeQuery(query);
        
        if (resultSet.next()) 
        {
            DataResult result = new DataResult(resultSet);
            return result;
        } 
        else { return null; }
    }
    
    /* ----- SERIES ----- */
    
    public static ResultSet getDataSerie(DatabaseConnection connection, Indicateur indicateur, String area, String flow) throws SQLException
    {
        String query = "SELECT * "
            + "FROM echo.seriesData "
            + "WHERE indic = '"+indicateur.getCode()+"' AND geo = '"+area+"' AND flow = '"+flow+"' "
            + "ORDER BY time;";
        
        ResultSet resultSet = connection.executeQuery(query);
        
        return resultSet;
    }
    
    /* ----- DATA RESULT ----- */
    
    public static Double getValue (String query) {
        Double value = null;
        try {
            Context context = new InitialContext();
            DataSource dataSource = (DataSource)context.lookup("java:comp/env/jdbc/postgres");
            try (Connection connection = dataSource.getConnection()) {
                try (Statement statement = connection.createStatement();ResultSet resultSet = statement.executeQuery(query)) {
                    resultSet.next();
                    value = resultSet.getDouble("value");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataAccess.class.getName()).log(Level.SEVERE, null, ex);
            value = null;
        } catch (NamingException ex) {
            Logger.getLogger(DataAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }
    
    
}
