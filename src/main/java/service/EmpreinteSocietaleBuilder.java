/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import data.DataAccess;
import data.DatabaseConnection;
import meta.Indicateur;
import meta.Flag;
import static data.DataAccess.getValue;
import data.DataResult;
import static java.lang.Math.min;
import static java.lang.Math.max;
import java.sql.SQLException;
import java.util.HashMap;
import response.IndicateurResponse;
import response.UniteLegaleResponse;

/**
 *
 * @author SylvainPro
 */
public class EmpreinteSocietaleBuilder {
    
    private DatabaseConnection connection;
    private UniteLegaleResponse uniteLegale;
    
    public EmpreinteSocietaleBuilder(DatabaseConnection connection) {
        this.connection = connection;
    }
    
    public HashMap<String,IndicateurResponse> buildEmpreinteSocietaleUniteLegale(UniteLegaleResponse uniteLegale) throws SQLException {
        this.uniteLegale = uniteLegale;
        HashMap<String,IndicateurResponse> empreinteSocietale = new HashMap<>();
        for (Indicateur indicateur : Indicateur.values()) {
            IndicateurResponse indicateurResponse = new IndicateurResponse(connection, indicateur, uniteLegale);
            if (indicateurResponse.getValue()!=null) {
                empreinteSocietale.put(indicateur.getCode(),indicateurResponse);
            } else if (indicateur.isIqve()) {
                indicateurResponse = getDefaultIndicateurResponse(indicateur);
                empreinteSocietale.put(indicateur.getCode(),indicateurResponse);
            }
        }
        return empreinteSocietale;
    }
    
    public HashMap<String,IndicateurResponse> buildEmpreinteSocietaleUniteLegale(String pays,String nace) throws SQLException {
        HashMap<String,IndicateurResponse> empreinteSocietale = new HashMap<>();
        for (Indicateur indicateur : Indicateur.values()) {
            if (indicateur.isIqve()) {
                DataResult rs = DataAccess.getDefaultData(connection, indicateur, pays, nace);
                IndicateurResponse indicateurResponse = new IndicateurResponse(
                        indicateur,
                        rs.value,
                        rs.flag,rs.uncertainty,
                        rs.time,rs.source,"");
                empreinteSocietale.put(indicateur.getCode(),indicateurResponse);
            }
        }
        return empreinteSocietale;
    }
    
    private IndicateurResponse getDefaultIndicateurResponse(Indicateur indicateur) throws SQLException {
        switch (indicateur) {
            case ECO:
                return getDefaultECO();
            case ART:
                return getDefaultART();
            case SOC:
                return getDefaultSOC();
            case KNW:
                return getDefaultKNW();
            case DIS:
                return getDefaultDIS();
            case GEQ:
                return getDefaultGEQ();
            case GHG:
                return getDefaultGHG();
            case MAT:
                return getDefaultMAT();
            case WAS:
                return getDefaultWAS();
            case NRG:
                return getDefaultNRG();
            case WAT:
                return getDefaultWAT();
            case HAZ:
                return getDefaultHAZ();
            default:
                return null;
        }
    }
    
    private IndicateurResponse getDefaultECO() throws SQLException {
        /*Double txVA = getValue("SELECT value FROM echo.dv_txva_nace_fr_cdv WHERE division = '"+uniteLegaleResponse.getActivitePrincipale().substring(0,2)+"';")/100.0;
        //Double txIMP = getValue("SELECT value FROM echo.dv_tximp_nace_fr_cdv WHERE division = '"+uniteLegaleResponse.getActivitePrincipale().substring(0,2)+"';")/100.0;
        Double tauxContributionDirecte = 1.0;
        Double nbEtablissements = getValue("SELECT COUNT(*) AS value FROM sirene.etablissements "
                + "WHERE siren = '"+uniteLegaleResponse.getIdentifiant()+"' "
                + "AND etatAdministratifEtablissement = 'A' AND statutDiffusionEtablissement = 'O';");
        if (nbEtablissements>0) {
            Double nbEtablissementsEtranger = getValue("SELECT COUNT(*) AS value FROM sirene.etablissements "
                    + "WHERE siren = '"+uniteLegaleResponse.getIdentifiant()+"' "
                    + "AND codePaysEtrangerEtablissement != '' "
                    + "AND etatAdministratifEtablissement = 'A' AND statutDiffusionEtablissement = 'O';");
            tauxContributionDirecte = nbEtablissementsEtranger/nbEtablissements;
        }*/
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.ECO, "FRA", uniteLegale.getActivitePrincipale().substring(0,2));
        return new IndicateurResponse(
                Indicateur.ECO,
                //txVA*tauxContributionDirecte*100.0+(1-txVA)*getDefaultECO("FR",null).getValue(),
                rs.value,
                rs.flag,rs.uncertainty,rs.time,
                rs.source,rs.info);
    }
    private IndicateurResponse getDefaultART() throws SQLException {
        Double txVA = getValue("SELECT value FROM echo.dv_txva_nace_fr_cdv WHERE division = '"+uniteLegale.getActivitePrincipale().substring(0,2)+"';")/100.0;
        Double tauxActiviteRegistreMetiers = 0.0;
        Double nbEtablissements = getValue("SELECT COUNT(*) AS value FROM sirene.etablissements "
                + "WHERE siren = '"+uniteLegale.getSiren()+"' "
                + "AND etatAdministratifEtablissement = 'A' AND statutDiffusionEtablissement = 'O';");
        if (nbEtablissements>0) {
            Double nbEtablissementsRegistreMetiers = getValue("SELECT COUNT(*) AS value FROM sirene.etablissements "
                    + "WHERE siren = '"+uniteLegale.getSiren()+"' "
                    + "AND activitePrincipaleRegistreMetiersEtablissement != '' "
                    + "AND etatAdministratifEtablissement = 'A' AND statutDiffusionEtablissement = 'O';");
            tauxActiviteRegistreMetiers = nbEtablissementsRegistreMetiers/nbEtablissements;
        }
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.ART, "FRA", null);
        Double value = txVA*tauxActiviteRegistreMetiers*100.0 + (1-txVA)*rs.value;
        Double max = txVA*min(tauxActiviteRegistreMetiers*1.5,1.0) + (1-txVA)*min(rs.value*(1 + rs.uncertainty/100.0),1.0);
        Double min = txVA*tauxActiviteRegistreMetiers*0.5+(1-txVA)*rs.value*(1-rs.uncertainty/100.0);
        Double uncertainty = max((max-value)/value,(value-min)/value)*100.0;
        return new IndicateurResponse(
                Indicateur.ART,
                value,
                Flag.ADJUSTED.getCode(),uncertainty,
                rs.time,rs.source+",SIRENE","");
    }
    private IndicateurResponse getDefaultSOC() throws SQLException {
        Double txVA = getValue("SELECT value FROM echo.dv_txva_nace_fr_cdv WHERE division = '"+uniteLegale.getActivitePrincipale().substring(0,2)+"';")/100.0;
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.SOC, "FRA",null);
        if (uniteLegale.getIsEconomieSocialeSolidaire()) {
            return new IndicateurResponse(
                    Indicateur.SOC,
                    txVA+100.0 + (1-txVA)*rs.value,
                    Flag.ADJUSTED.getCode(),txVA*25.0 + (1-txVA)*rs.uncertainty,
                    rs.time,rs.source+",SIRENE","");
        } else {
            return new IndicateurResponse(
                    Indicateur.SOC,
                    (1-txVA)*rs.value,
                    Flag.ADJUSTED.getCode(),txVA*25.0 + (1-txVA)*rs.uncertainty,
                    rs.time,rs.source+",SIRENE","");
        }
    }
    private IndicateurResponse getDefaultKNW() throws SQLException {
        if (uniteLegale.getActivitePrincipale().substring(0,2).equals("72") 
                || uniteLegale.getActivitePrincipale().substring(0,2).equals("85")) {
            Double txVA = getValue("SELECT value FROM echo.dv_txva_nace_fr_cdv WHERE division = '"+uniteLegale.getActivitePrincipale().substring(0,2)+"';")/100.0;
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.KNW, "FRA", null);
            return new IndicateurResponse(
                    Indicateur.KNW,
                    txVA*100.0 + (1-txVA)*rs.value,
                    Flag.STATISTIC.getCode(),txVA*25.0 + (1-txVA)*rs.uncertainty,
                    rs.time,rs.source+",SIRENE","");
        } else {
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.KNW, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2));
            return new IndicateurResponse(
                    Indicateur.KNW,
                    rs.value,
                    Flag.STATISTIC.getCode(),rs.uncertainty,
                    rs.time,rs.source+",SIRENE","");
        }
    }
    private IndicateurResponse getDefaultDIS() throws SQLException {
        if (uniteLegale.getTrancheEffectifs()==null 
                || uniteLegale.getTrancheEffectifs().equals("0") 
                || uniteLegale.getTrancheEffectifs().equals("1")) {
            Double txVA = getValue("SELECT value FROM echo.dv_txva_nace_fr_cdv WHERE division = '"+uniteLegale.getActivitePrincipale().substring(0,2)+"';")/100.0;
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.DIS, "FRA", null);
            return new IndicateurResponse(
                    Indicateur.DIS,
                    (1-txVA)*rs.value,
                    Flag.STATISTIC.getCode(),txVA*25.0 + (1-txVA)*rs.uncertainty,
                    rs.time,rs.source+",SIRENE","");
        } else {
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.DIS, "FRA", null);
            return new IndicateurResponse(
                    Indicateur.DIS,
                    rs.value,
                    rs.flag,rs.uncertainty,
                    rs.time,rs.source,"");
        }
    }
    private IndicateurResponse getDefaultGEQ() throws SQLException {
        if (uniteLegale.getTrancheEffectifs()==null 
                || uniteLegale.getTrancheEffectifs().equals("0") 
                || uniteLegale.getTrancheEffectifs().equals("1")) {
            Double txVA = getValue("SELECT value FROM echo.dv_txva_nace_fr_cdv WHERE division = '"+uniteLegale.getActivitePrincipale().substring(0,2)+"';")/100.0;
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.GEQ, "FRA", null);
            return new IndicateurResponse(
                    Indicateur.GEQ,
                    (1-txVA)*rs.value,
                    Flag.STATISTIC.getCode(),txVA*25.0 + (1-txVA)*rs.uncertainty,
                    rs.time,rs.source+",SIRENE","");
        } else {
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.GEQ, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2));
            return new IndicateurResponse(
                    Indicateur.GEQ,
                    rs.value,
                    rs.flag,rs.uncertainty,
                    rs.time,rs.source,"");
        }
    }
    private IndicateurResponse getDefaultGHG() throws SQLException {
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.GHG, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2));
        return new IndicateurResponse(
                Indicateur.GHG,
                rs.value,
                rs.flag,rs.uncertainty,
                rs.time,rs.source,"");
    }
    private IndicateurResponse getDefaultMAT() throws SQLException {
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.MAT, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2));
        return new IndicateurResponse(
                Indicateur.MAT,
                rs.value,
                rs.flag,rs.uncertainty,
                rs.time,rs.source,"");
    }
    private IndicateurResponse getDefaultWAS() throws SQLException {
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.WAS, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2));
        return new IndicateurResponse(
                Indicateur.WAS,
                rs.value,
                rs.flag,rs.uncertainty,
                rs.time,rs.source,"");
    }
    private IndicateurResponse getDefaultNRG() throws SQLException {
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.NRG, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2));
        return new IndicateurResponse(
                Indicateur.NRG,
                rs.value,
                rs.flag,rs.uncertainty,
                rs.time,rs.source,"");
    }
    private IndicateurResponse getDefaultWAT() throws SQLException {
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.WAT, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2));
        return new IndicateurResponse(
                Indicateur.WAT,
                rs.value,
                rs.flag,rs.uncertainty,
                rs.time,rs.source,"");
    }
    private IndicateurResponse getDefaultHAZ() throws SQLException {
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.HAZ, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2));
        return new IndicateurResponse(
                Indicateur.HAZ,
                rs.value,
                rs.flag,rs.uncertainty,
                rs.time,rs.source,"");
    }
    
}
