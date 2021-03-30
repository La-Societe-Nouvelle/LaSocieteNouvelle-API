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

/**
 *
 * @author SylvainPro
 */
public class DefaultDataResponse implements Serializable {
    
    private HeaderResponse header;
    private EmpreinteSocietale empreinteSocietale;
    
    public DefaultDataResponse (DatabaseConnection connection,String pays,String activite) {
        
        header = new HeaderResponse();
        if (pays==null) pays = "WLD";
        if (activite==null) activite = "00";
        
        if (pays.matches("[A-Z]{3}") & activite.matches("[0-9]{2}")) {
            try {
                empreinteSocietale = new EmpreinteSocietale(connection, pays, activite);
                header.setStatut(Statut.OK);
            } catch (SQLException ex) {
                header.setStatut(Statut.SERVER_ERROR);
                Logger.getLogger(DefaultDataResponse.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            header.setStatut(Statut.BAD_REQUEST);
        }
        
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
