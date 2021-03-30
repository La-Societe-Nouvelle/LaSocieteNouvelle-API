/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import data.DatabaseConnection;
import java.sql.SQLException;
import java.util.HashMap;
import response.IndicateurResponse;
import response.UniteLegaleResponse;

/**
 *
 * @author SylvainPro
 */

public class EmpreinteSocietale {
    
    private final HashMap<String,IndicateurResponse> indicateurs;
    
    public EmpreinteSocietale(DatabaseConnection connection,UniteLegaleResponse uniteLegale) throws SQLException {
        EmpreinteSocietaleBuilder builder = new EmpreinteSocietaleBuilder(connection);
        indicateurs = builder.buildEmpreinteSocietaleUniteLegale(uniteLegale);
    }
    
    public EmpreinteSocietale(DatabaseConnection connection,String pays,String nace) throws SQLException {
        EmpreinteSocietaleBuilder builder = new EmpreinteSocietaleBuilder(connection);
        indicateurs = builder.buildEmpreinteSocietaleUniteLegale(pays, nace);
    }
    
    public HashMap<String,IndicateurResponse> getEmpreinteSocietaleResponse() {
            return indicateurs;
    }
    
}
