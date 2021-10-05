/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import data.DataAccess;
import data.DataResult;
import data.DatabaseConnection;
import java.sql.SQLException;
import meta.Flag;
import meta.Indicateur;
import response.IndicateurResponse;

/**
 *
 * @author SylvainPro
 */
public class DefaultDataBuilder {
    
    /* ---------- Default Data Builder ---------- */
    
    public static IndicateurResponse getDefaultDataIndicateur(DatabaseConnection connection, 
                                                              Indicateur indicateur,
                                                              String pays, 
                                                              String nace, 
                                                              String flow) throws SQLException
    {
        // fetch default data
        DataResult rs = DataAccess.getDefaultData(connection, indicateur, pays, nace, flow);
        
        if (rs!=null) // -> default data available in database
        {
            // Return the data
            return new IndicateurResponse(indicateur,
                rs.value,
                rs.flag,
                rs.uncertainty,
                rs.time, 
                rs.source,
                "");
        }
        else if (nace.equals("00")) // -> data for economic area not available (header -> data not available)
        {
            // Fetch default data
            rs = DataAccess.getDefaultData(connection, indicateur);
            return new IndicateurResponse(indicateur,
                rs.value,
                rs.flag,
                rs.uncertainty,
                rs.time, 
                rs.source,
                "");
        }
        else
        {
            // Build the default value with ratio
            return buildDefaultDataIndicateur(connection, indicateur, pays, nace, flow);
        }
    }
    
    /* ---------- Indicators Builders ---------- */
    
    // Redirect to the correct function for each indicator
    private static IndicateurResponse buildDefaultDataIndicateur(DatabaseConnection connection, 
                                                                 Indicateur indicateur,
                                                                 String pays, 
                                                                 String nace, 
                                                                 String flow) throws SQLException 
    {
        switch (indicateur) 
        {
            case ART: return buildDefaultDataART(connection,pays,nace,flow);
            case DIS: return buildDefaultDataDIS(connection,pays,nace,flow);
            case ECO: return buildDefaultDataECO(connection,pays,nace,flow);
            case GEQ: return buildDefaultDataGEQ(connection,pays,nace,flow);
            case GHG: return buildDefaultDataGHG(connection,pays,nace,flow);
            case HAZ: return buildDefaultDataHAZ(connection,pays,nace,flow);
            case NRG: return buildDefaultDataNRG(connection,pays,nace,flow);
            case KNW: return buildDefaultDataKNW(connection,pays,nace,flow);
            case MAT: return buildDefaultDataMAT(connection,pays,nace,flow);
            case SOC: return buildDefaultDataSOC(connection,pays,nace,flow);
            case WAS: return buildDefaultDataWAS(connection,pays,nace,flow);
            case WAT: return buildDefaultDataWAT(connection,pays,nace,flow);
            default:  return null;
        }
    }
    
    // default data for indicator ART
    private static IndicateurResponse buildDefaultDataART(DatabaseConnection connection, String pays, String nace, String flow) throws SQLException
    {
        if (pays.equals("FRA"))     // should not be reached
        {
            // Value at economic area level
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.ART, pays, flow.equals("GVA") ? "GDP" : "GAP");
            return new IndicateurResponse(Indicateur.ART,
                rs.value,
                Flag.DEFAULT_DATA.getCode(),
                rs.uncertainty,
                rs.time,
                rs.source,
                rs.info);
        }
        else
        {
            // Default value
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.ART);
            return new IndicateurResponse(Indicateur.ART,
                rs.value,
                Flag.DEFAULT_DATA.getCode(),
                rs.uncertainty,
                rs.time,
                rs.source,
                rs.info);
        }
    }
    
    // default data for indicator DIS
    private static IndicateurResponse buildDefaultDataDIS(DatabaseConnection connection, String pays, String nace, String flow) throws SQLException
    {
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.DIS, pays, flow.equals("GVA") ? "GDP" : "GAP");
        if (rs == null) rs = DataAccess.getDefaultData(connection, Indicateur.DIS);
        
        return new IndicateurResponse(Indicateur.DIS,
            rs.value,
            Flag.DEFAULT_DATA.getCode(),
            rs.uncertainty,
            rs.time,
            rs.source,
            rs.info);
    }
    
    // default data for indicator ECO
    private static IndicateurResponse buildDefaultDataECO(DatabaseConnection connection, String pays, String nace, String flow) throws SQLException
    {
        if (pays.equals("FRA"))     // should not be reached
        {
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.ECO, pays, flow.equals("GVA") ? "GDP" : "GAP");
            return new IndicateurResponse(Indicateur.ECO,
                rs.value,
                Flag.DEFAULT_DATA.getCode(),
                rs.uncertainty,
                rs.time,
                rs.source,
                rs.info);
        }
        else
        {
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.ECO);
            return new IndicateurResponse(Indicateur.ECO,
                rs.value,
                Flag.DEFAULT_DATA.getCode(),
                rs.uncertainty,
                rs.time,
                rs.source,
                rs.info);
        }
    }
    
    // default data for indicator GEQ
    private static IndicateurResponse buildDefaultDataGEQ(DatabaseConnection connection, String pays, String nace, String flow) throws SQLException
    {
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.GEQ, pays, flow.equals("GVA") ? "GDP" : "GAP");
        if (rs == null) rs = DataAccess.getDefaultData(connection, Indicateur.GEQ);
        
        return new IndicateurResponse(Indicateur.GEQ,
            rs.value,
            Flag.DEFAULT_DATA.getCode(),
            rs.uncertainty,
            rs.time,
            rs.source,
            rs.info);
    }
    
    // default data for indicator GHG
    private static IndicateurResponse buildDefaultDataGHG(DatabaseConnection connection, String pays, String nace, String flow) throws SQLException
    {
        // Value for requested economic area economic (or default if undefined)
        DataResult dv_area_gdp = DataAccess.getDefaultData(connection, Indicateur.GHG, pays, "GDP");
        if (dv_area_gdp == null) dv_area_gdp = DataAccess.getDefaultData(connection, Indicateur.GHG);
        
        // Value in France for requested activity (or domestic production if undefined)
        DataResult dv_fra_nace = DataAccess.getDefaultData(connection, Indicateur.GHG, "FRA", nace, flow);
        if (dv_fra_nace == null) dv_fra_nace = DataAccess.getDefaultData(connection, Indicateur.GHG, "FRA", "GDP");     // should not be reached
        
        // Value for domestic production in France
        DataResult dv_fra_gdp = DataAccess.getDefaultData(connection, Indicateur.GHG, "FRA", "GDP");
        
        // Values
        Double value = dv_fra_nace.value * (dv_area_gdp.value/dv_fra_gdp.value);
        Double uncertainty = dv_fra_nace.uncertainty*1.5;
            
        return new IndicateurResponse(Indicateur.GHG,
            value,
            Flag.DEFAULT_DATA.getCode(),
            uncertainty,
            dv_fra_nace.time,
            dv_fra_nace.source,
            "Valeur ajustée à partir de l'"+dv_area_gdp.info.toLowerCase());
    }
    
    // default data for indicator HAZ
    private static IndicateurResponse buildDefaultDataHAZ(DatabaseConnection connection, String pays, String nace, String flow) throws SQLException
    {
        // Value for requested economic area economic (or default if undefined)
        DataResult dv_area_gdp = DataAccess.getDefaultData(connection, Indicateur.HAZ, pays, "GDP");
        if (dv_area_gdp == null) dv_area_gdp = DataAccess.getDefaultData(connection, Indicateur.HAZ);
        
        // Value in France for requested activity (or domestic production if undefined)
        DataResult dv_fra_nace = DataAccess.getDefaultData(connection, Indicateur.HAZ, "FRA", nace, flow);
        if (dv_fra_nace == null) dv_fra_nace = DataAccess.getDefaultData(connection, Indicateur.HAZ, "FRA", "GDP");     // should not be reached
        
        // Value for domestic production in France
        DataResult dv_fra_gdp = DataAccess.getDefaultData(connection, Indicateur.HAZ, "FRA", "GDP");
        
        // Values
        Double value = dv_fra_nace.value * (dv_area_gdp.value/dv_fra_gdp.value);
        Double uncertainty = dv_fra_nace.uncertainty*1.5;
            
        return new IndicateurResponse(Indicateur.HAZ,
            value,
            Flag.DEFAULT_DATA.getCode(),
            uncertainty,
            dv_fra_nace.time,
            dv_fra_nace.source,
            "Valeur ajustée à partir de l'"+dv_area_gdp.info.toLowerCase());
    }
    
    // default data for indicator KNW
    private static IndicateurResponse buildDefaultDataKNW(DatabaseConnection connection, String pays, String nace, String flow) throws SQLException
    {
        DataResult rs = DataAccess.getDefaultData(connection, Indicateur.KNW, pays, flow.equals("GVA") ? "GDP" : "GAP");
        if (rs == null) rs = DataAccess.getDefaultData(connection, Indicateur.KNW);
            
        return new IndicateurResponse(Indicateur.KNW,
            rs.value,
            Flag.DEFAULT_DATA.getCode(),
            rs.uncertainty,
            rs.time,
            rs.source,
            rs.info);
    }
    
    // default data for indicator MAT
    private static IndicateurResponse buildDefaultDataMAT(DatabaseConnection connection, String pays, String nace, String flow) throws SQLException
    {
        // Value for requested economic area economic (or default if undefined)
        DataResult dv_area_gdp = DataAccess.getDefaultData(connection, Indicateur.MAT, pays, "GDP");
        if (dv_area_gdp == null) dv_area_gdp = DataAccess.getDefaultData(connection, Indicateur.MAT);
        
        // Value in France for requested activity (or domestic production if undefined)
        DataResult dv_fra_nace = DataAccess.getDefaultData(connection, Indicateur.MAT, "FRA", nace, flow);
        if (dv_fra_nace == null) dv_fra_nace = DataAccess.getDefaultData(connection, Indicateur.MAT, "FRA", "GDP");     // should not be reached
        
        // Value for domestic production in France
        DataResult dv_fra_gdp = DataAccess.getDefaultData(connection, Indicateur.MAT, "FRA", "GDP");
        
        // Values
        Double value = dv_fra_nace.value * (dv_area_gdp.value/dv_fra_gdp.value);
        Double uncertainty = dv_fra_nace.uncertainty*1.5;
            
        return new IndicateurResponse(Indicateur.MAT,
            value,
            Flag.DEFAULT_DATA.getCode(),
            uncertainty,
            dv_fra_nace.time,
            dv_fra_nace.source,
            "Valeur ajustée à partir de l'"+dv_area_gdp.info.toLowerCase());
    }
    
    // default data for indicator NRG
    private static IndicateurResponse buildDefaultDataNRG(DatabaseConnection connection, String pays, String nace, String flow) throws SQLException
    {
        // Value for requested economic area economic (or default if undefined)
        DataResult dv_area_gdp = DataAccess.getDefaultData(connection, Indicateur.NRG, pays, "GDP");
        if (dv_area_gdp == null) dv_area_gdp = DataAccess.getDefaultData(connection, Indicateur.NRG);
        
        // Value in France for requested activity (or domestic production if undefined)
        DataResult dv_fra_nace = DataAccess.getDefaultData(connection, Indicateur.NRG, "FRA", nace, flow);
        if (dv_fra_nace == null) dv_fra_nace = DataAccess.getDefaultData(connection, Indicateur.NRG, "FRA", "GDP");     // should not be reached
        
        // Value for domestic production in France
        DataResult dv_fra_gdp = DataAccess.getDefaultData(connection, Indicateur.NRG, "FRA", "GDP");
        
        // Values
        Double value = dv_fra_nace.value * (dv_area_gdp.value/dv_fra_gdp.value);
        Double uncertainty = dv_fra_nace.uncertainty*1.5;
            
        return new IndicateurResponse(Indicateur.NRG,
            value,
            Flag.DEFAULT_DATA.getCode(),
            uncertainty,
            dv_fra_nace.time,
            dv_fra_nace.source,
            "Valeur ajustée à partir de l'"+dv_area_gdp.info.toLowerCase());
    }
    
    // default data for indicator SOC
    private static IndicateurResponse buildDefaultDataSOC(DatabaseConnection connection, String pays, String nace, String flow) throws SQLException
    {
        if (pays.equals("FRA"))     // should not be reached
        {
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.SOC, pays, flow.equals("GVA") ? "GDP" : "GAP");
            return new IndicateurResponse(Indicateur.SOC,
                rs.value,
                Flag.DEFAULT_DATA.getCode(),
                rs.uncertainty,
                rs.time,
                rs.source,
                rs.info);
        }
        else
        {
            DataResult rs = DataAccess.getDefaultData(connection, Indicateur.SOC);
            return new IndicateurResponse(Indicateur.SOC,
                rs.value,
                Flag.DEFAULT_DATA.getCode(),
                rs.uncertainty,
                rs.time,
                rs.source,
                rs.info);
        }
    }
    
    // default data for indicator WAS
    private static IndicateurResponse buildDefaultDataWAS(DatabaseConnection connection, String pays, String nace, String flow) throws SQLException
    {
        // Value for requested economic area economic (or default if undefined)
        DataResult dv_area_gdp = DataAccess.getDefaultData(connection, Indicateur.WAS, pays, "GDP");
        if (dv_area_gdp == null) dv_area_gdp = DataAccess.getDefaultData(connection, Indicateur.WAS);
        
        // Value in France for requested activity (or domestic production if undefined)
        DataResult dv_fra_nace = DataAccess.getDefaultData(connection, Indicateur.WAS, "FRA", nace, flow);
        if (dv_fra_nace == null) dv_fra_nace = DataAccess.getDefaultData(connection, Indicateur.WAS, "FRA", "GDP");     // should not be reached
        
        // Value for domestic production in France
        DataResult dv_fra_gdp = DataAccess.getDefaultData(connection, Indicateur.WAS, "FRA", "GDP");
        
        // Values
        Double value = dv_fra_nace.value * (dv_area_gdp.value/dv_fra_gdp.value);
        Double uncertainty = dv_fra_nace.uncertainty*1.5;
            
        return new IndicateurResponse(Indicateur.WAS,
            value,
            Flag.DEFAULT_DATA.getCode(),
            uncertainty,
            dv_fra_nace.time,
            dv_fra_nace.source,
            "Valeur ajustée à partir de l'"+dv_area_gdp.info.toLowerCase());
    }
    
    // default data for indicator WAT
    private static IndicateurResponse buildDefaultDataWAT(DatabaseConnection connection, String pays, String nace, String flow) throws SQLException
    {
        // Value for requested economic area economic (or default if undefined)
        DataResult dv_area_gdp = DataAccess.getDefaultData(connection, Indicateur.WAT, pays, "GDP");
        if (dv_area_gdp == null) dv_area_gdp = DataAccess.getDefaultData(connection, Indicateur.WAT);
        
        // Value in France for requested activity (or domestic production if undefined)
        DataResult dv_fra_nace = DataAccess.getDefaultData(connection, Indicateur.WAT, "FRA", nace, flow);
        if (dv_fra_nace == null) dv_fra_nace = DataAccess.getDefaultData(connection, Indicateur.WAT, "FRA", "GDP");     // should not be reached
        
        // Value for domestic production in France
        DataResult dv_fra_gdp = DataAccess.getDefaultData(connection, Indicateur.WAT, "FRA", "GDP");
        
        // Values
        Double value = dv_fra_nace.value * (dv_area_gdp.value/dv_fra_gdp.value);
        Double uncertainty = dv_fra_nace.uncertainty*1.5;
            
        return new IndicateurResponse(Indicateur.WAT,
            value,
            Flag.DEFAULT_DATA.getCode(),
            uncertainty,
            dv_fra_nace.time,
            dv_fra_nace.source,
            "Valeur ajustée à partir de l'"+dv_area_gdp.info.toLowerCase());
    }
    
}
