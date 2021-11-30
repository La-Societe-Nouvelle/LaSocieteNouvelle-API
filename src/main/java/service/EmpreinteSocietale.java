/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import data.DatabaseConnection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import response.IndicateurResponse;
import response.UniteLegaleResponse;

/**
 *
 * @author SylvainPro
 */

public class EmpreinteSocietale {
    
    private HashMap<String,IndicateurResponse> indicateurs;
    
    /* ---------- Constructors ---------- */
    
    /*  NOTES
    ----------------------------------------------------------------------------------------------------
    Two constructors are used :
     - one for the corporates footprints
     - one for the default data
    The difference is in the called function of the builder
    ----------------------------------------------------------------------------------------------------
     */
    
    // Basic constructor
    public EmpreinteSocietale(DatabaseConnection connection, UniteLegaleResponse uniteLegale) throws SQLException 
    {
        // Initialize the builder
        EmpreinteSocietaleBuilder builder = new EmpreinteSocietaleBuilder(connection);
        
        // Build the indicators (from legal unit data)
        try  
        {
            indicateurs = builder.buildEmpreinteSocietaleUniteLegale(uniteLegale);
        } 
        catch (NullPointerException ex) {
            indicateurs = null;
            Logger.getLogger(EmpreinteSocietale.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Constructor used to get default data (macroeconomic scale)
    public EmpreinteSocietale(DatabaseConnection connection, 
                              String area, 
                              String activity, 
                              String flow) throws SQLException 
    {
        // Initialize the builder
        EmpreinteSocietaleBuilder builder = new EmpreinteSocietaleBuilder(connection);
        
        // Build the indicators (from country, activity, flow)
        try  
        {
            indicateurs = builder.buildEmpreinteSocietaleDefaultData(area, activity, flow);
        } 
        catch (NullPointerException ex) {
            indicateurs = null;
            Logger.getLogger(EmpreinteSocietale.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /* ---------- Getters ---------- */
    
    public HashMap<String,IndicateurResponse> getEmpreinteSocietaleResponse() 
    {
            return indicateurs;
    }
    
}
