/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author SylvainPro
 */
public class DataResult {
    
    public final Double value;
    //public final String unit;
    public final String flag;
    public final Double uncertainty;
    public final String time;
    public final String source;
    public final String info;
    
    /* ----- CONSTRUCTOR ----- */
    
    public DataResult (ResultSet resultSet) throws SQLException 
    {
        this.value = resultSet.getDouble("value");
        //this.unit = resultSet.getString("unit");
        this.flag = resultSet.getString("flag");
        this.uncertainty = resultSet.getDouble("uncertainty");
        this.time = resultSet.getString("year");
        this.source = resultSet.getString("source");
        this.info = ""; //resultSet.getString("info");
    }
    
}
