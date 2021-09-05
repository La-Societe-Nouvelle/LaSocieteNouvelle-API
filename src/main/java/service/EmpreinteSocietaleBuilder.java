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
    
    public EmpreinteSocietaleBuilder(DatabaseConnection connection) 
    {
        // Set the databse connection
        this.connection = connection;
    }
    
    /* ---------- CSF Builders ---------- */
    
    // Build the CSF of a legal unit
    public HashMap<String,IndicateurResponse> buildEmpreinteSocietaleUniteLegale(UniteLegaleResponse uniteLegale) throws SQLException 
    {
        // Set the legal unit for further references
        this.uniteLegale = uniteLegale;
        
        // Initialize the set of indicators
        HashMap<String,IndicateurResponse> empreinteSocietale = new HashMap<>();
        
        // Build the comparatives values
        HashMap<String,IndicateurResponse> empreinteSocietaleReference = buildEmpreinteSocietaleUniteLegale("FRA",uniteLegale.getActivitePrincipale().substring(0,2), "PRD");
        
        for (Indicateur indicateur : Indicateur.values()) 
        {
            // Try to get the value from the database (specific value for the legal unit)
            IndicateurResponse indicateurResponse = new IndicateurResponse(connection, indicateur, uniteLegale);
                        
            // Calculate the default value if there is no value published & the indicator is include in the CSF
            if (indicateurResponse.getValue()==null & indicateur.isIqve()) 
            {
                indicateurResponse = getDefaultIndicateurResponse(indicateur);
            }
            
            // Set the comparative value for the CSF indicators (for which a comparative value is available)
            if (indicateur.isIqve()) 
            {
                indicateurResponse.setReference(empreinteSocietaleReference.get(indicateur.getCode()));
                empreinteSocietale.put(indicateur.getCode(),indicateurResponse);
            }
            
            // Put the indicator in the corporate social footprint
            if (indicateurResponse.getValue()!=null) empreinteSocietale.put(indicateur.getCode(),indicateurResponse);
        }
        
        return empreinteSocietale;
    }
    
    // Build a default social footprint based on the country and the main activity
    public HashMap<String,IndicateurResponse> buildEmpreinteSocietaleUniteLegale(String pays, String nace, String flow) throws SQLException 
    {
        // Initialize the set of indicators
        HashMap<String,IndicateurResponse> empreinteSocietale = new HashMap<>();
        
        for (Indicateur indicateur : Indicateur.values()) 
        {
            // only get the value for CSF indicators
            if (indicateur.isIqve()) 
            {
                // Get the data from the database
                DataResult rs = DataAccess.getDefaultData(connection, indicateur, pays, nace, flow);
                
                // Build the indicator response
                IndicateurResponse indicateurResponse = new IndicateurResponse(indicateur,
                    rs.value,
                    rs.flag,
                    rs.uncertainty,
                    rs.time, 
                    rs.source,
                    "");
                
                empreinteSocietale.put(indicateur.getCode(),indicateurResponse);
            }
        }
        return empreinteSocietale;
    }
    
    /* ---------- Indicators Builders ---------- */
    
    // Redirect to the correct function for each indicator
    private IndicateurResponse getDefaultIndicateurResponse(Indicateur indicateur) throws SQLException 
    {
        switch (indicateur) 
        {
            case ART:
                return getDefaultART();
            case DIS:
                return getDefaultDIS();
            case ECO:
                return getDefaultECO();
            case GEQ:
                return getDefaultGEQ();
            case GHG:
                return getDefaultGHG();
            case HAZ:
                return getDefaultHAZ();
            case NRG:
                return getDefaultNRG();
            case KNW:
                return getDefaultKNW();
            case MAT:
                return getDefaultMAT();
            case SOC:
                return getDefaultSOC();
            case WAS:
                return getDefaultWAS();
            case WAT:
                return getDefaultWAT();
            default:
                return null;
        }
    }
    
    // Calculate the default data for the indicator ART
    private IndicateurResponse getDefaultART() throws SQLException 
    {
        // Retrieve the net value added rate (according to the economic division)
        DataResult nva_rate = DataAccess.getDefaultData(connection, Indicateur.NVA, "FRA", uniteLegale.getActivitePrincipale().substring(0,2), "GDP");
        
        // Retrieve the default value for intermediate consumption (according to the economic division)
        DataResult art_ic = DataAccess.getDefaultData(connection, Indicateur.ART, "FRA", uniteLegale.getActivitePrincipale().substring(0,2), "IC");
        
        // Estimate the value for the net value added based on the activities of the establishments
        Double art_nva = 0.0;
        Double nbEtablissements = getValue("SELECT COUNT(*) AS value "
            + "FROM sirene.etablissements "
            + "WHERE siren = '"+uniteLegale.getSiren()+"' "
                + "AND etatAdministratifEtablissement = 'A' "
                + "AND statutDiffusionEtablissement = 'O';");
        if (nbEtablissements > 0) 
        {
            Double nbEtablissementsRegistreMetiers = getValue("SELECT COUNT(*) AS value "
                    + "FROM sirene.etablissements "
                    + "WHERE siren = '"+uniteLegale.getSiren()+"' "
                        + "AND activitePrincipaleRegistreMetiersEtablissement != '' "
                        + "AND etatAdministratifEtablissement = 'A' "
                        + "AND statutDiffusionEtablissement = 'O';");
            art_nva = nbEtablissementsRegistreMetiers/nbEtablissements *100;
        }
        
        // Calculate the value
        Double value = (nva_rate.value/100)*art_nva + (1-(nva_rate.value/100))*art_ic.value;
        // Calculate the uncertainty
        Double maxValue = (nva_rate.value/100)*min(art_nva*1.5,100.0) + (1-(nva_rate.value/100))*min(art_ic.value*(1+art_ic.uncertainty/100),100.0) ;
        Double minValue = (nva_rate.value/100)*max(art_nva*0.5,0.0)   + (1-(nva_rate.value/100))*max(art_ic.value*(1-art_ic.uncertainty/100),0.0) ;
        Double uncertainty = max(maxValue-value,value-minValue)/value *100.0;
        
        return new IndicateurResponse(Indicateur.ART,
            value,
            Flag.ADJUSTED_DATA.getCode(),
            uncertainty,
            art_ic.time,
            art_ic.source+", SIRENE",
            "");
    }
    
    // Calculate the default data for the indicator DIS
    private IndicateurResponse getDefaultDIS() throws SQLException 
    {
        // If the legal unit don't have more than one employee
        if (uniteLegale.getTrancheEffectifs()==null 
         || uniteLegale.getTrancheEffectifs().equals("0") 
         || uniteLegale.getTrancheEffectifs().equals("1")) 
        {
            // Retrieve the net value added rate (according to the economic division)
            DataResult nva_rate = DataAccess.getDefaultData(connection, Indicateur.NVA, "FRA", uniteLegale.getActivitePrincipale().substring(0,2), "GDP");
            // Retrieve the default value for intermediate consumption (according to the economic division)
            DataResult dis_ic = DataAccess.getDefaultData(connection, Indicateur.DIS, "FRA", uniteLegale.getActivitePrincipale().substring(0,2), "IC");
            
            // Calculate the value
            Double value = (nva_rate.value/100)*0.0 + (1-(nva_rate.value/100))*dis_ic.value;
            // Calculate the uncertainty
            Double maxValue = (nva_rate.value/100)*25.0 + (1-(nva_rate.value/100))*min(dis_ic.value*(1+dis_ic.uncertainty/100),100.0) ;
            Double minValue = (nva_rate.value/100)*00.0 + (1-(nva_rate.value/100))*max(dis_ic.value*(1-dis_ic.uncertainty/100),0.0) ;
            Double uncertainty = max(maxValue-value,value-minValue)/value *100.0;
                        
            return new IndicateurResponse(Indicateur.DIS,
                value,
                Flag.ADJUSTED_DATA.getCode(),
                uncertainty,
                dis_ic.time,
                dis_ic.source+", SIRENE",
                "");
        } 
        // If the legal unit have more than one employee
        else 
        {
            // Retrieve the default value for production (according to the economic division)
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.DIS, "FRA", uniteLegale.getActivitePrincipale().substring(0,2), "PRD");
            
            return new IndicateurResponse(Indicateur.DIS,
                rs.value,
                rs.flag,
                rs.uncertainty,
                rs.time,
                rs.source,
                "");
        }
    }
    
    // Calculate the default data for the indicator ECO
    private IndicateurResponse getDefaultECO() throws SQLException 
    {
        // Retrieve the net value added rate (according to the economic division)
        DataResult nva_rate = DataAccess.getDefaultData(connection, Indicateur.NVA, "FRA", uniteLegale.getActivitePrincipale().substring(0,2), "GDP");
        // Retrieve the default value for intermediate consumption (according to the economic division)
        DataResult eco_ci = DataAccess.getDefaultData(connection, Indicateur.ECO, "FRA", uniteLegale.getActivitePrincipale().substring(0,2), "IC");
        
        // Estimate the net value added produced in France
        Double eco_nva = 100.0;
        Double nbEtablissements = getValue("SELECT COUNT(*) AS value "
                + "FROM sirene.etablissements "
                + "WHERE siren = '"+uniteLegale.getSiren()+"' "
                    + "AND etatAdministratifEtablissement = 'A';");
        if (nbEtablissements>0) {
            Double nbEtablissementsEtranger = getValue("SELECT COUNT(*) AS value "
                    + "FROM sirene.etablissements "
                    + "WHERE siren = '"+uniteLegale.getSiren()+"' "
                        + "AND codePaysEtrangerEtablissement != '' "
                        + "AND etatAdministratifEtablissement = 'A';");
            eco_nva = (nbEtablissements-nbEtablissementsEtranger)/nbEtablissements *100;
        }
        
        // Calculate the value
        Double value = (nva_rate.value/100)*eco_nva + (1-(nva_rate.value/100))*eco_ci.value;
        // Calculate the uncertainty
        Double maxValue = (nva_rate.value/100)*min(eco_nva*1.5,100.0) + (1-(nva_rate.value/100))*min(eco_ci.value*(1+eco_ci.uncertainty/100),100.0) ;
        Double minValue = (nva_rate.value/100)*max(eco_nva*0.5,0.0)   + (1-(nva_rate.value/100))*max(eco_ci.value*(1-eco_ci.uncertainty/100),0.0) ;
        Double uncertainty = max(maxValue-value,value-minValue)/value *100.0;
        
        return new IndicateurResponse(Indicateur.ECO,
            value,
            Flag.ADJUSTED_DATA.getCode(),
            uncertainty,
            eco_ci.time,
            eco_ci.source+", SIRENE",
            eco_ci.info);
    }
    
    // Calculate the default data for the indicator GEQ
    private IndicateurResponse getDefaultGEQ() throws SQLException 
    {
        // If the legal unit don't have more than one employee
        if (uniteLegale.getTrancheEffectifs()==null 
         || uniteLegale.getTrancheEffectifs().equals("0") 
         || uniteLegale.getTrancheEffectifs().equals("1")) 
        {
            // Retrieve the net value added rate (according to the economic division)
            DataResult nva_rate = DataAccess.getDefaultData(connection, Indicateur.NVA, "FRA", uniteLegale.getActivitePrincipale().substring(0,2), "GDP");
            // Retrieve the default value for intermediate consumption (according to the economic division)
            DataResult geq_ic = DataAccess.getDefaultData(connection, Indicateur.GEQ, "FRA", uniteLegale.getActivitePrincipale().substring(0,2), "IC");
            
            // Calculate the value
            Double value = (nva_rate.value/100)*0.0 + (1-(nva_rate.value/100))*geq_ic.value;
            // Calculate the uncertainty
            Double maxValue = (nva_rate.value/100)*25.0 + (1-(nva_rate.value/100))*min(geq_ic.value*(1+geq_ic.uncertainty/100),100.0) ;
            Double minValue = (nva_rate.value/100)*00.0 + (1-(nva_rate.value/100))*max(geq_ic.value*(1-geq_ic.uncertainty/100),0.0) ;
            Double uncertainty = max(maxValue-value,value-minValue)/value *100.0;
            
            return new IndicateurResponse(Indicateur.GEQ,
                value,
                Flag.ADJUSTED_DATA.getCode(),
                uncertainty,
                geq_ic.time,
                geq_ic.source+", SIRENE",
                "");
        }
        // If the legal unit have more than one employee
        else 
        {
            // Retrieve the default value for production (according to the economic division)
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.GEQ, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2), "PRD");
            
            return new IndicateurResponse(Indicateur.GEQ,
                rs.value,
                rs.flag,
                rs.uncertainty,
                rs.time,
                rs.source+", SIRENE",
                "");
        }
    }
    
    // Calculate the default data for the indicator GHG
    private IndicateurResponse getDefaultGHG() throws SQLException 
    {
        // Retrieve the default value for production (according to the economic division)
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.GHG, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2), "PRD");
        
        return new IndicateurResponse(Indicateur.GHG,
            rs.value,
            rs.flag,
            rs.uncertainty,
            rs.time,
            rs.source,
            "");
    }
    
    // Calculate the default data for the indicator HAZ
    private IndicateurResponse getDefaultHAZ() throws SQLException 
    {
        // Retrieve the default value for production (according to the economic division)
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.HAZ, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2), "PRD");
        
        return new IndicateurResponse(Indicateur.HAZ,
            rs.value,
            rs.flag,
            rs.uncertainty,
            rs.time,
            rs.source,
            "");
    }
    
    // Calculate the default data for the indicator KNW
    private IndicateurResponse getDefaultKNW() throws SQLException 
    {
        // If the legal unit main activity is research or education
        if (uniteLegale.getActivitePrincipale().substring(0,2).equals("72") 
         || uniteLegale.getActivitePrincipale().substring(0,2).equals("85"))
        {
            // Retrieve the net value added rate (according to the economic division)
            DataResult nva_rate = DataAccess.getDefaultData(connection, Indicateur.NVA, "FRA", uniteLegale.getActivitePrincipale().substring(0,2), "GDP");
            // Retrieve the default value for intermediate consumption (according to the economic division)
            DataResult knw_ic = DataAccess.getDefaultData(connection, Indicateur.KNW, "FRA", uniteLegale.getActivitePrincipale().substring(0,2), "IC");
            
            // Calculate the value
            Double value = (nva_rate.value/100)*100.0 + (1-(nva_rate.value/100))*knw_ic.value;
            // Calculate the uncertainty
            Double maxValue = (nva_rate.value/100)*100.0*1.00 + (nva_rate.value/100)*min(knw_ic.value*(1+knw_ic.uncertainty/100),100.0) ;
            Double minValue = (nva_rate.value/100)*100.0*0.75 + (nva_rate.value/100)*max(knw_ic.value*(1-knw_ic.uncertainty/100),0.0) ;
            Double uncertainty = max(maxValue-value,value-minValue)/value *100.0;
            
            return new IndicateurResponse(Indicateur.KNW,
                value,
                Flag.SECTOR_SPECIFIC_DATA.getCode(),
                uncertainty,
                knw_ic.time,
                knw_ic.source+", SIRENE",
                "");
        }
        // for others activities
        else 
        {
            // Retrieve the default value for production (according to the economic division)
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.KNW, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2), "PRD");
            
            return new IndicateurResponse(Indicateur.KNW,
                rs.value,
                Flag.DEFAULT_DATA.getCode(),
                rs.uncertainty,
                rs.time,
                rs.source+",SIRENE",
                "");
        }
    }
    
    // Calculate the default data for the indicator MAT
    private IndicateurResponse getDefaultMAT() throws SQLException 
    {
        // Retrieve the default value for production (according to the economic division)
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.MAT, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2), "PRD");
        
        return new IndicateurResponse(Indicateur.MAT,
            rs.value,
            rs.flag,
            rs.uncertainty,
            rs.time,
            rs.source,
            "");
    }
    
    // Calculate the default data for the indicator NRG
    private IndicateurResponse getDefaultNRG() throws SQLException 
    {
        // Retrieve the default value for production (according to the economic division)
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.NRG, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2), "PRD");
        
        return new IndicateurResponse(Indicateur.NRG,
            rs.value,
            rs.flag,
            rs.uncertainty,
            rs.time,
            rs.source,
            "");
    }
    
    // Calculate the default data for the indicator SOC
    private IndicateurResponse getDefaultSOC() throws SQLException 
    {
        // Retrieve the net value added rate (according to the economic division)
        DataResult nva_rate = DataAccess.getDefaultData(connection, Indicateur.NVA, "FRA", uniteLegale.getActivitePrincipale().substring(0,2), "GDP");
        // Retrieve the default value for intermediate consumption (according to the economic division)
        DataResult soc_ic = DataAccess.getDefaultData(connection, Indicateur.SOC, "FRA", uniteLegale.getActivitePrincipale().substring(0,2), "IC");
        
        // If the legal unit belong to the social economy (based on SIRENE)
        if (uniteLegale.getIsEconomieSocialeSolidaire()) 
        {
            // Calculate the value
            Double value = (nva_rate.value/100)*100.0 + (1-(nva_rate.value/100))*soc_ic.value;
            // Calculate the uncertainty
            Double maxValue = (nva_rate.value/100)*100.0*1.00 + (1-(nva_rate.value/100))*min(soc_ic.value*(1+soc_ic.uncertainty/100),100.0) ;
            Double minValue = (nva_rate.value/100)*100.0*0.75 + (1-(nva_rate.value/100))*max(soc_ic.value*(1-soc_ic.uncertainty/100),0.0) ;
            Double uncertainty = max(maxValue-value,value-minValue)/value *100.0;
            
            return new IndicateurResponse(Indicateur.SOC,
                value,
                Flag.ADJUSTED_DATA.getCode(),
                uncertainty,
                soc_ic.time,
                soc_ic.source+", SIRENE",
                "");
        } 
        else 
        {
            // Calculate the value
            Double value = (nva_rate.value/100)*0.0 + (1-(nva_rate.value/100))*soc_ic.value;
            // Calculate the uncertainty
            Double maxValue = (nva_rate.value/100)*25.0 + (1-(nva_rate.value/100))*min(soc_ic.value*(1+soc_ic.uncertainty/100),100.0) ;
            Double minValue = (nva_rate.value/100)*00.0 + (1-(nva_rate.value/100))*max(soc_ic.value*(1-soc_ic.uncertainty/100),0.0) ;
            Double uncertainty = max(maxValue-value,value-minValue)/value *100.0;
            
            return new IndicateurResponse(Indicateur.SOC,
                value,
                Flag.ADJUSTED_DATA.getCode(),
                uncertainty,
                soc_ic.time,
                soc_ic.source+", SIRENE",
                "");
        }
    }
    
    // Calculate the default data for the indicator WAS
    private IndicateurResponse getDefaultWAS() throws SQLException 
    {
        // Retrieve the default value for production (according to the economic division)
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.WAS, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2), "PRD");
        
        return new IndicateurResponse(Indicateur.WAS,
            rs.value,
            rs.flag,
            rs.uncertainty,
            rs.time,
            rs.source,
            "");
    }
    
    // Calculate the default data for the indicator WAT
    private IndicateurResponse getDefaultWAT() throws SQLException 
    {
        // Retrieve the default value for production (according to the economic division)
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.WAT, "FRA", uniteLegale.getActivitePrincipale().substring(0, 2), "GDP");
        
        return new IndicateurResponse(Indicateur.WAT,
            rs.value,
            rs.flag,
            rs.uncertainty,
            rs.time,
            rs.source,
            "");
    }
    
}
