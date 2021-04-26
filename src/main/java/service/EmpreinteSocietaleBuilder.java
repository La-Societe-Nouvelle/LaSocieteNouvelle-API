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
    
    private final DatabaseConnection connection;
    
    private UniteLegaleResponse uniteLegale;
    
    /* ---------- Constructor ---------- */
    
    public EmpreinteSocietaleBuilder(DatabaseConnection connection) {
        // Set the connection to the database
        this.connection = connection;
    }
    
    /* ---------- CSF Builders ---------- */
    
    // Build the CSF of a legal unit
    public HashMap<String,IndicateurResponse> buildEmpreinteSocietaleUniteLegale(UniteLegaleResponse uniteLegale) throws SQLException {
        
        // Set the legal unit for further references
        this.uniteLegale = uniteLegale;
        
        // Initialize the set of indicators
        HashMap<String,IndicateurResponse> empreinteSocietale = new HashMap<>();
        // Build the comparatives values
        HashMap<String,IndicateurResponse> empreinteSocietaleReference = buildEmpreinteSocietaleUniteLegale("FRA",uniteLegale.getActivitePrincipale().substring(0,2));
        
        for (Indicateur indicateur : Indicateur.values()) {
            // Try to get the value from the database (specific value for the legal unit)
            IndicateurResponse indicateurResponse = new IndicateurResponse(connection, indicateur, uniteLegale);
                        
            // Calculate the default value if there is no value published & the indicator is include in the CSF
            if (indicateurResponse.getValue()==null & indicateur.isIqve()) {
                indicateurResponse = getDefaultIndicateurResponse(indicateur);
            }
            
            // Set the comparative value for the CSF indicators (for which a comparative value is available)
            if (indicateur.isIqve()) {
                indicateurResponse.setReference(empreinteSocietaleReference.get(indicateur.getCode()));
                empreinteSocietale.put(indicateur.getCode(),indicateurResponse);
            }
            
            // Put the indicator in the corporate social footprint
            if (indicateurResponse.getValue()!=null) {
                empreinteSocietale.put(indicateur.getCode(),indicateurResponse);
            }
            
        }
        return empreinteSocietale;
    }
    
    // Build a default social footprint based on the country and the main activity
    public HashMap<String,IndicateurResponse> buildEmpreinteSocietaleUniteLegale(String pays,String nace) throws SQLException {
        
        // Initialize the set of indicators
        HashMap<String,IndicateurResponse> empreinteSocietale = new HashMap<>();
        
        for (Indicateur indicateur : Indicateur.values()) {
            
            // only get the value for CSF indicators
            if (indicateur.isIqve()) {
                
                // Get the data from the database
                DataResult rs = DataAccess.getDefaultData(connection, indicateur, pays, nace);
                
                // Build the indicator response
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
    
    /* ---------- Indicators Builders ---------- */
    
    // Redirect to the correct function for each indicator
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
    
    // Calculate the default data for the indicator ECO
    private IndicateurResponse getDefaultECO() throws SQLException {
        
        // Get the net value added rate (in relation to the production)
        DataResult NVA_rate = DataAccess.getDefaultData(connection, Indicateur.NVA, "FRA", uniteLegale.getActivitePrincipale().substring(0,2));
        // Get the net value added rate (in relation to the production)
        DataResult IMP_rate = DataAccess.getDefaultData(connection, Indicateur.IMP, "FRA", uniteLegale.getActivitePrincipale().substring(0,2));
        // Get the default data at the national level
        DataResult eco_fra = DataAccess.getDefaultData(connection, Indicateur.ECO, "FRA", null);
        
        // Estimate the net value added in France
        Double eco_nva = 100.0;
        Double nbEtablissements = getValue("SELECT COUNT(*) AS value FROM sirene.etablissements "
                + "WHERE siren = '"+uniteLegale.getSiren()+"' "
                + "AND etatAdministratifEtablissement = 'A';");
        if (nbEtablissements>0) {
            Double nbEtablissementsEtranger = getValue("SELECT COUNT(*) AS value FROM sirene.etablissements "
                    + "WHERE siren = '"+uniteLegale.getSiren()+"' "
                    + "AND codePaysEtrangerEtablissement != '' "
                    + "AND etatAdministratifEtablissement = 'A';");
            eco_nva = (nbEtablissements-nbEtablissementsEtranger)/nbEtablissements *100;
        }
        
        // Calculate the value
        Double value = (NVA_rate.value/100)*eco_nva + (1-(NVA_rate.value/100)-(IMP_rate.value/100))*eco_fra.value;
        // Calculate the uncertainty
        Double maxValue = (NVA_rate.value/100)*min(eco_nva*1.5,100.0) + (1-(NVA_rate.value/100)-(IMP_rate.value/100))*min(eco_fra.value*(1+eco_fra.uncertainty/100),100.0) ;
        Double minValue = (NVA_rate.value/100)*max(eco_nva*0.5,0.0)   + (1-(NVA_rate.value/100)-(IMP_rate.value/100))*max(eco_fra.value*(1-eco_fra.uncertainty/100),0.0) ;
        Double uncertainty = max(maxValue-value,value-minValue)/value *100.0;
        return new IndicateurResponse(
                Indicateur.ECO,
                value,
                eco_fra.flag,uncertainty,
                eco_fra.time,eco_fra.source,eco_fra.info);
    }
    
    // Calculate the default data for the indicator ART
    private IndicateurResponse getDefaultART() throws SQLException {
        
        // Get the net value added rate (in relation to the production)
        DataResult NVA_rate = DataAccess.getDefaultData(connection, Indicateur.NVA, "FRA", uniteLegale.getActivitePrincipale().substring(0,2));
        // Get the default data at the national level
        DataResult art_fra = DataAccess.getDefaultData(connection, Indicateur.ART, "FRA", null);
        
        // Estimate the value of the indicator for the net value added based on the localisation of the sites
        Double art_nva = 0.0;
        Double nbEtablissements = getValue("SELECT COUNT(*) AS value FROM sirene.etablissements "
                + "WHERE siren = '"+uniteLegale.getSiren()+"' "
                + "AND etatAdministratifEtablissement = 'A' AND statutDiffusionEtablissement = 'O';");
        if (nbEtablissements>0) {
            Double nbEtablissementsRegistreMetiers = getValue("SELECT COUNT(*) AS value FROM sirene.etablissements "
                    + "WHERE siren = '"+uniteLegale.getSiren()+"' "
                    + "AND activitePrincipaleRegistreMetiersEtablissement != '' "
                    + "AND etatAdministratifEtablissement = 'A' AND statutDiffusionEtablissement = 'O';");
            art_nva = nbEtablissementsRegistreMetiers/nbEtablissements *100;
        }
        
        // Calculate the value
        Double value = (NVA_rate.value/100)*art_nva + (1-(NVA_rate.value/100))*art_fra.value;
        // Calculate the uncertainty
        Double maxValue = (NVA_rate.value/100)*min(art_nva*1.5,100.0) + (1-(NVA_rate.value/100))*min(art_fra.value*(1+art_fra.uncertainty/100),100.0) ;
        Double minValue = (NVA_rate.value/100)*max(art_nva*0.5,0.0)   + (1-(NVA_rate.value/100))*max(art_fra.value*(1-art_fra.uncertainty/100),0.0) ;
        Double uncertainty = max(maxValue-value,value-minValue)/value *100.0;
        
        return new IndicateurResponse(
                Indicateur.ART,
                value,
                Flag.ADJUSTED_DATA.getCode(),uncertainty,
                art_fra.time,art_fra.source+",SIRENE","");
    }
    
    // Calculate the default data for the indicator SOC
    private IndicateurResponse getDefaultSOC() throws SQLException {
        
        // Get the net value added rate (in relation to the production)
        DataResult NVA_rate = DataAccess.getDefaultData(connection, Indicateur.NVA, "FRA", uniteLegale.getActivitePrincipale().substring(0,2));
        // Get the default data at the national level
        DataResult soc_fra = DataAccess.getDefaultData(connection, Indicateur.SOC, "FRA", null);
        
        // If the legal unit belong to the social economy (based on SIRENE)
        if (uniteLegale.getIsEconomieSocialeSolidaire()) {
            
            // Calculate the value
            Double value = (NVA_rate.value/100)*100.0 + (1-(NVA_rate.value/100))*soc_fra.value;
            // Calculate the uncertainty
            Double maxValue = (NVA_rate.value/100)*100.0*1.00 + (1-(NVA_rate.value/100))*min(soc_fra.value*(1+soc_fra.uncertainty/100),100.0) ;
            Double minValue = (NVA_rate.value/100)*100.0*0.75 + (1-(NVA_rate.value/100))*max(soc_fra.value*(1-soc_fra.uncertainty/100),0.0) ;
            Double uncertainty = max(maxValue-value,value-minValue)/value *100.0;
            
            return new IndicateurResponse(
                    Indicateur.SOC,
                    value,
                    Flag.ADJUSTED_DATA.getCode(),uncertainty,
                    soc_fra.time,soc_fra.source+", SIRENE","");
            
        } else {
            
            // Calculate the value
            Double value = (NVA_rate.value/100)*0.0 + (1-(NVA_rate.value/100))*soc_fra.value;
            // Calculate the uncertainty
            Double maxValue = (NVA_rate.value/100)*25.0 + (1-(NVA_rate.value/100))*min(soc_fra.value*(1+soc_fra.uncertainty/100),100.0) ;
            Double minValue = (NVA_rate.value/100)*00.0 + (1-(NVA_rate.value/100))*max(soc_fra.value*(1-soc_fra.uncertainty/100),0.0) ;
            Double uncertainty = max(maxValue-value,value-minValue)/value *100.0;
            
            return new IndicateurResponse(
                    Indicateur.SOC,
                    value,
                    Flag.ADJUSTED_DATA.getCode(),uncertainty,
                    soc_fra.time,soc_fra.source+", SIRENE","");
        }
    }
    
    // Calculate the default data for the indicator KNW
    private IndicateurResponse getDefaultKNW() throws SQLException {
        
        // If the legal unit main activity is equivalent to research or education
        if (uniteLegale.getActivitePrincipale().substring(0,2).equals("72") 
                || uniteLegale.getActivitePrincipale().substring(0,2).equals("85")) {
            
            // Get the net value added rate (in relation to the production)
            DataResult NVA_rate = DataAccess.getDefaultData(connection, Indicateur.NVA, "FRA", uniteLegale.getActivitePrincipale().substring(0,2));
            // Get the default data at the national level
            DataResult knw_fra = DataAccess.getDefaultData(connection, Indicateur.KNW, "FRA", null);
            
            // Calculate the value
            Double value = (NVA_rate.value/100)*100.0 + (1-(NVA_rate.value/100))*knw_fra.value;
            // Calculate the uncertainty
            Double maxValue = (NVA_rate.value/100)*100.0*1.00 + (NVA_rate.value/100)*min(knw_fra.value*(1+knw_fra.uncertainty/100),100.0) ;
            Double minValue = (NVA_rate.value/100)*100.0*0.75 + (NVA_rate.value/100)*max(knw_fra.value*(1-knw_fra.uncertainty/100),0.0) ;
            Double uncertainty = max(maxValue-value,value-minValue)/value *100.0;
            
            return new IndicateurResponse(
                    Indicateur.KNW,
                    value,
                    Flag.SECTOR_SPECIFIC_DATA.getCode(),uncertainty,
                    knw_fra.time,knw_fra.source+", SIRENE","");
        } else {
            
            // Get the default data assigned to the economic division of the legal unit
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.KNW, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2));
            
            return new IndicateurResponse(
                    Indicateur.KNW,
                    rs.value,
                    Flag.DEFAULT_DATA.getCode(),rs.uncertainty,
                    rs.time,rs.source+",SIRENE","");
        }
    }
    
    // Calculate the default data for the indicator DIS
    private IndicateurResponse getDefaultDIS() throws SQLException {
        
        // If the legal unit don't have more than one employee
        if (uniteLegale.getTrancheEffectifs()==null 
                || uniteLegale.getTrancheEffectifs().equals("0") 
                || uniteLegale.getTrancheEffectifs().equals("1")) {
            
            // Get the net value added rate (in relation to the production)
            DataResult NVA_rate = DataAccess.getDefaultData(connection, Indicateur.NVA, "FRA", uniteLegale.getActivitePrincipale().substring(0,2));
            // Get the default data at the national level
            DataResult dis_fra = DataAccess.getDefaultData(connection, Indicateur.DIS, "FRA", null);
            
            // Calculate the value
            Double value = (NVA_rate.value/100)*0.0 + (1-(NVA_rate.value/100))*dis_fra.value;
            // Calculate the uncertainty
            Double maxValue = (NVA_rate.value/100)*25.0 + (1-(NVA_rate.value/100))*min(dis_fra.value*(1+dis_fra.uncertainty/100),100.0) ;
            Double minValue = (NVA_rate.value/100)*00.0 + (1-(NVA_rate.value/100))*max(dis_fra.value*(1-dis_fra.uncertainty/100),0.0) ;
            Double uncertainty = max(maxValue-value,value-minValue)/value *100.0;
                        
            return new IndicateurResponse(
                    Indicateur.DIS,
                    value,
                    Flag.ADJUSTED_DATA.getCode(),uncertainty,
                    dis_fra.time,dis_fra.source+", SIRENE","");
        
        } else {
            
            // Get the default data assigned to the economic division of the legal unit
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.DIS, "FRA", null);
            
            return new IndicateurResponse(
                    Indicateur.DIS,
                    rs.value,
                    rs.flag,rs.uncertainty,
                    rs.time,rs.source,"");
        }
    }
    
    // Calculate the default data for the indicator GEQ
    private IndicateurResponse getDefaultGEQ() throws SQLException {
        
        // If the legal unit don't have more than one employee
        if (uniteLegale.getTrancheEffectifs()==null 
                || uniteLegale.getTrancheEffectifs().equals("0") 
                || uniteLegale.getTrancheEffectifs().equals("1")) {
            
            // Get the net value added rate (in relation to the production)
            DataResult NVA_rate = DataAccess.getDefaultData(connection, Indicateur.NVA, "FRA", uniteLegale.getActivitePrincipale().substring(0,2));
            // Get the default data at the national level
            DataResult geq_fra = DataAccess.getDefaultData(connection, Indicateur.GEQ, "FRA", null);
            
            // Calculate the value
            Double value = (NVA_rate.value/100)*0.0 + (1-(NVA_rate.value/100))*geq_fra.value;
            // Calculate the uncertainty
            Double maxValue = (NVA_rate.value/100)*25.0 + (1-(NVA_rate.value/100))*min(geq_fra.value*(1+geq_fra.uncertainty/100),100.0) ;
            Double minValue = (NVA_rate.value/100)*00.0 + (1-(NVA_rate.value/100))*max(geq_fra.value*(1-geq_fra.uncertainty/100),0.0) ;
            Double uncertainty = max(maxValue-value,value-minValue)/value *100.0;
            
            return new IndicateurResponse(
                    Indicateur.GEQ,
                    value,
                    Flag.ADJUSTED_DATA.getCode(),uncertainty,
                    geq_fra.time,geq_fra.source+", SIRENE","");
            
        } else {
            
            // Get the default data assigned to the economic division of the legal unit
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.GEQ, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2));
            
            return new IndicateurResponse(
                    Indicateur.GEQ,
                    rs.value,
                    rs.flag,rs.uncertainty,
                    rs.time,rs.source,"");
        }
    }
    
    // Calculate the default data for the indicator GHG
    private IndicateurResponse getDefaultGHG() throws SQLException {
        
        // Get the default data assigned to the economic division of the legal unit
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.GHG, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2));
        
        return new IndicateurResponse(
                Indicateur.GHG,
                rs.value,
                rs.flag,rs.uncertainty,
                rs.time,rs.source,"");
    }
    
    // Calculate the default data for the indicator MAT
    private IndicateurResponse getDefaultMAT() throws SQLException {
        
        // Get the default data assigned to the economic division of the legal unit
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.MAT, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2));
        
        return new IndicateurResponse(
                Indicateur.MAT,
                rs.value,
                rs.flag,rs.uncertainty,
                rs.time,rs.source,"");
    }
    
    // Calculate the default data for the indicator WAS
    private IndicateurResponse getDefaultWAS() throws SQLException {
        
        // Get the default data assigned to the economic division of the legal unit
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.WAS, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2));
        
        return new IndicateurResponse(
                Indicateur.WAS,
                rs.value,
                rs.flag,rs.uncertainty,
                rs.time,rs.source,"");
    }
    
    // Calculate the default data for the indicator NRG
    private IndicateurResponse getDefaultNRG() throws SQLException {
        
        // Get the default data assigned to the economic division of the legal unit
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.NRG, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2));
        
        return new IndicateurResponse(
                Indicateur.NRG,
                rs.value,
                rs.flag,rs.uncertainty,
                rs.time,rs.source,"");
    }
    
    // Calculate the default data for the indicator WAT
    private IndicateurResponse getDefaultWAT() throws SQLException {
        
        // Get the default data assigned to the economic division of the legal unit
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.WAT, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2));
        
        return new IndicateurResponse(
                Indicateur.WAT,
                rs.value,
                rs.flag,rs.uncertainty,
                rs.time,rs.source,"");
    }
    
    // Calculate the default data for the indicator HAZ
    private IndicateurResponse getDefaultHAZ() throws SQLException {
        
        // Get the default data at national level (no detailed values available)
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.HAZ, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2));
        
        return new IndicateurResponse(
                Indicateur.HAZ,
                rs.value,
                rs.flag,rs.uncertainty,
                rs.time,rs.source,"");
    }
        
}
