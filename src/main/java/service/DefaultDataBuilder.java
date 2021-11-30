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
import java.util.regex.Pattern;
import meta.Flag;
import meta.Indicateur;
import response.IndicateurResponse;

/**
 *
 * @author SylvainPro
 */
public class DefaultDataBuilder {
    
    private final static Pattern DIVISION_PATTERN = Pattern.compile("[0-9]{2}");
    private final static Pattern BRANCHE_PATTERN = Pattern.compile("([A-Z]{2}|TOTAL)");
    
    /* ---------- Default Data Builder ---------- */
    
    public static IndicateurResponse getDefaultDataIndicateur(DatabaseConnection connection, 
                                                              Indicateur indicateur,
                                                              String area, 
                                                              String activity, 
                                                              String flow) throws SQLException
    {
        // Data available in database
        if ("FRA".equals(area))
        {
            DataResult rs = fetchDefaultData(connection, indicateur, area, activity, flow);
            
            // Return the data
            return new IndicateurResponse(indicateur,
                rs.value,
                Flag.DEFAULT_DATA.getCode(),
                rs.uncertainty,
                rs.time, 
                rs.source,
                "");
        }
        // Build data
        else
        {
            // Build the default value with ratio
            return buildDefaultDataIndicateur(connection, indicateur, area, activity, flow);
        }
    }
    
    /* ---------- Fetch data ---------- */
    
    private static DataResult fetchDefaultData(DatabaseConnection connection, 
                                               Indicateur indicateur,
                                               String area, 
                                               String activity, 
                                               String flow) throws SQLException 
    {        
        if (DIVISION_PATTERN.matcher(activity).find())
        {
            return DataAccess.getDivisionData(connection, indicateur, area, activity, flow);
        }
        else if (BRANCHE_PATTERN.matcher(activity).find())
        {
            return DataAccess.getBrancheData(connection, indicateur, area, activity, flow);
        }
        else return null;
    }
    
    /* ---------- Indicators Builders ---------- */
    
    // Redirect to the correct function for each indicator
    private static IndicateurResponse buildDefaultDataIndicateur(DatabaseConnection connection, 
                                                                 Indicateur indicateur,
                                                                 String area, 
                                                                 String activity, 
                                                                 String flow) throws SQLException 
    {
        System.out.println(indicateur);
        switch (indicateur) 
        {
            case ART: return buildDefaultDataART(connection,area,activity,flow);
            case DIS: return buildDefaultDataDIS(connection,area,activity,flow);
            case ECO: return buildDefaultDataECO(connection,area,activity,flow);
            case GEQ: return buildDefaultDataGEQ(connection,area,activity,flow);
            case GHG: return buildDefaultDataGHG(connection,area,activity,flow);
            case HAZ: return buildDefaultDataHAZ(connection,area,activity,flow);
            case NRG: return buildDefaultDataNRG(connection,area,activity,flow);
            case KNW: return buildDefaultDataKNW(connection,area,activity,flow);
            case MAT: return buildDefaultDataMAT(connection,area,activity,flow);
            case SOC: return buildDefaultDataSOC(connection,area,activity,flow);
            case WAS: return buildDefaultDataWAS(connection,area,activity,flow);
            case WAT: return buildDefaultDataWAT(connection,area,activity,flow);
            default:  return null;
        }
    }
    
    // default data for indicator ART
    private static IndicateurResponse buildDefaultDataART(DatabaseConnection connection, String pays, String nace, String flow) throws SQLException
    {
        if (pays.equals("FRA"))     // should not be reached
        {
            // Value at economic area level
            DataResult rs = DataAccess.getAreaData(connection, Indicateur.ART, pays, flow.equals("GVA") ? "GDP" : "GAP");
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
        DataResult rs = DataAccess.getAreaData(connection, Indicateur.DIS, pays, flow.equals("GVA") ? "GDP" : "GAP");
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
            DataResult rs = DataAccess.getAreaData(connection, Indicateur.ECO, pays, flow.equals("GVA") ? "GDP" : "GAP");
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
        DataResult rs = DataAccess.getAreaData(connection, Indicateur.GEQ, pays, flow.equals("GVA") ? "GDP" : "GAP");
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
    private static IndicateurResponse buildDefaultDataGHG(DatabaseConnection connection, String pays, String activity, String flow) throws SQLException
    {
        // Value for requested economic area economic (or default if undefined)
        DataResult dv_area_gdp = DataAccess.getAreaData(connection, Indicateur.GHG, pays, "GDP");
        if (dv_area_gdp == null) dv_area_gdp = DataAccess.getDefaultData(connection, Indicateur.GHG);
        
        // Value in France for requested activity (or domestic production if undefined)
        DataResult dv_fra_nace = fetchDefaultData(connection, Indicateur.GHG, "FRA", activity, flow);
        if (dv_fra_nace == null) dv_fra_nace = DataAccess.getAreaData(connection, Indicateur.GHG, "FRA", "GDP");     // should not be reached
        
        // Value for domestic production in France
        DataResult dv_fra_gdp = DataAccess.getAreaData(connection, Indicateur.GHG, "FRA", "GDP");
        
        // Values
        System.out.println(dv_fra_nace.value);
        System.out.println(dv_fra_gdp.value);
        System.out.println(dv_area_gdp.value);
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
        // Value for domestic production in France
        DataResult dv_euu_gdp = DataAccess.getAreaData(connection, Indicateur.HAZ, "EUU", "GDP");
        
        // Values
        Double value = dv_euu_gdp.value;
        Double uncertainty = dv_euu_gdp.uncertainty;
            
        return new IndicateurResponse(Indicateur.HAZ,
            value,
            Flag.DEFAULT_DATA.getCode(),
            uncertainty,
            dv_euu_gdp.time,
            dv_euu_gdp.source,
            "Valeur ajustée à partir de l'"+dv_euu_gdp.info.toLowerCase());
    }
    
    // default data for indicator KNW
    private static IndicateurResponse buildDefaultDataKNW(DatabaseConnection connection, String pays, String nace, String flow) throws SQLException
    {
        DataResult rs = DataAccess.getAreaData(connection, Indicateur.KNW, pays, flow.equals("GVA") ? "GDP" : "GAP");
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
        DataResult dv_area_gdp = DataAccess.getAreaData(connection, Indicateur.MAT, pays, "GDP");
        if (dv_area_gdp == null) dv_area_gdp = DataAccess.getDefaultData(connection, Indicateur.MAT);
        
        // Value in France for requested activity (or domestic production if undefined)
        DataResult dv_fra_nace = fetchDefaultData(connection, Indicateur.MAT, "FRA", nace, flow);
        if (dv_fra_nace == null) dv_fra_nace = DataAccess.getAreaData(connection, Indicateur.MAT, "FRA", "GDP");     // should not be reached
        
        // Value for domestic production in France
        DataResult dv_fra_gdp = DataAccess.getAreaData(connection, Indicateur.MAT, "FRA", "GDP");
        
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
        DataResult dv_area_gdp = DataAccess.getAreaData(connection, Indicateur.NRG, pays, "GDP");
        if (dv_area_gdp == null) dv_area_gdp = DataAccess.getDefaultData(connection, Indicateur.NRG);
        
        // Value in France for requested activity (or domestic production if undefined)
        DataResult dv_fra_nace = fetchDefaultData(connection, Indicateur.NRG, "FRA", nace, flow);
        if (dv_fra_nace == null) dv_fra_nace = DataAccess.getAreaData(connection, Indicateur.NRG, "FRA", "GDP");     // should not be reached
        
        // Value for domestic production in France
        DataResult dv_fra_gdp = DataAccess.getAreaData(connection, Indicateur.NRG, "FRA", "GDP");
        
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
            DataResult rs = DataAccess.getAreaData(connection, Indicateur.SOC, pays, flow.equals("GVA") ? "GDP" : "GAP");
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
        DataResult dv_area_gdp = DataAccess.getAreaData(connection, Indicateur.WAS, pays, "GDP");
        if (dv_area_gdp == null) dv_area_gdp = DataAccess.getDefaultData(connection, Indicateur.WAS);
        
        // Value in France for requested activity (or domestic production if undefined)
        DataResult dv_fra_nace = fetchDefaultData(connection, Indicateur.WAS, "FRA", nace, flow);
        if (dv_fra_nace == null) dv_fra_nace = DataAccess.getAreaData(connection, Indicateur.WAS, "FRA", "GDP");     // should not be reached
        
        // Value for domestic production in France
        DataResult dv_fra_gdp = DataAccess.getAreaData(connection, Indicateur.WAS, "FRA", "GDP");
        
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
        DataResult dv_area_gdp = DataAccess.getAreaData(connection, Indicateur.WAT, pays, "GDP");
        if (dv_area_gdp == null) dv_area_gdp = DataAccess.getDefaultData(connection, Indicateur.WAT);
        
        // Value in France for requested activity (or domestic production if undefined)
        DataResult dv_fra_nace = fetchDefaultData(connection, Indicateur.WAT, "FRA", nace, flow);
        if (dv_fra_nace == null) dv_fra_nace = DataAccess.getAreaData(connection, Indicateur.WAT, "FRA", "GDP");     // should not be reached
        
        // Value for domestic production in France
        DataResult dv_fra_gdp = DataAccess.getAreaData(connection, Indicateur.WAT, "FRA", "GDP");
        
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
