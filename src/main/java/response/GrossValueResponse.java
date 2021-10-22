/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package response;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author SylvainPro
 */
public class GrossValueResponse {
    
    private Double value;
    private String flag;
    
    public GrossValueResponse(ResultSet resultSet) throws SQLException
    {
        this.value = resultSet.getDouble("value");
        this.flag = resultSet.getString("flag");
    }

    public Double getValue() {
        return value;
    }

    public String getFlag() {
        return flag;
    }
    
}
