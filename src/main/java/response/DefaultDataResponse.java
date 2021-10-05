/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package response;

import service.EmpreinteSocietale;
import data.DatabaseConnection;
import data.Statut;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import meta.Flow;

/**
 *
 * @author SylvainPro
 */
public class DefaultDataResponse implements Serializable {
    
    private HeaderResponse header;
    private EmpreinteSocietale empreinteSocietale;
    
    public DefaultDataResponse (DatabaseConnection connection, String pays, String activite, String flow) {
        
        header = new HeaderResponse();
        
        if (pays==null) pays = "_DV";
        if (activite==null) activite = "00";
        if (flow==null) flow = activite.equals("00") ? "GAP" : "PRD";
        
        if (activite.equals("00") && flow.equals("PRD")) flow = "GAP";
        if (activite.equals("00") && flow.equals("GVA")) flow = "GDP";
        if (activite.equals("00") && flow.equals("IC")) flow = "GAP";
        
        if ((pays.matches("[A-Z]{3}") || pays.matches("_DV")) && activite.matches("[0-9]{2}") && Flow.isCodeCorrect(flow,activite)) 
        {
            try 
            {
                empreinteSocietale = new EmpreinteSocietale(connection, pays, activite, flow);
                header.setStatut(Statut.OK);
            } 
            catch (SQLException ex) 
            {
                header.setStatut(Statut.SERVER_ERROR);
                Logger.getLogger(DefaultDataResponse.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        else {header.setStatut(Statut.BAD_REQUEST);}
        
    }
    
    /* ----- GETTERS ----- */

    public HeaderResponse getHeader() {
        return header;
    }
    public HashMap<String,IndicateurResponse> getEmpreinteSocietale() {
        if (empreinteSocietale!=null) {
            return empreinteSocietale.getEmpreinteSocietaleResponse();
        } else {
            return null;
        }
    }
    
}
