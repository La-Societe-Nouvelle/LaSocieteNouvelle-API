/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import data.DataAccess;
import data.DatabaseConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import meta.Indicateur;
import response.GrossValueResponse;
import response.IndicateurResponse;

/**
 *
 * @author SylvainPro
 */
public class DataSerieBuilder {
    
    private final DatabaseConnection connection;
    
    private HashMap<String,GrossValueResponse> serie;
    private HashMap<String,String> metaData;
    
    public DataSerieBuilder(DatabaseConnection connection)
    {
        this.connection = connection;
    }
    
    /* ---------- Default Data Builder ---------- */
    
    public void buildDataSerie(Indicateur indicateur,
                               String area, 
                               String flow) throws SQLException
    {
        serie = new HashMap<>();
        metaData = new HashMap<>();

        // fetch data
        ResultSet rs = DataAccess.getDataSerie(connection, indicateur, area, flow);
            
        while (rs.next())
        {
            String year = rs.getString("time");
            GrossValueResponse value = new GrossValueResponse(rs);
            serie.put(year, value);
            
            if (metaData.isEmpty()) buildMetaData(rs);
        }
    }
    
    private void buildMetaData(ResultSet rs) throws SQLException
    {        
        metaData.put("indic", rs.getString("indic"));
        metaData.put("unit", rs.getString("unit"));
        metaData.put("area", rs.getString("geo"));
        metaData.put("source", rs.getString("source"));
        metaData.put("info", rs.getString("info"));
        metaData.put("lastUpdate", rs.getString("lastUpdate"));
    }

    public HashMap<String, GrossValueResponse> getSerie() {
        return serie;
    }

    public HashMap<String, String> getMetaData() {
        return metaData;
    }
    
}
