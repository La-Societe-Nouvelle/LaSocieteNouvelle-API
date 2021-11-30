/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package response;

import data.DatabaseConnection;
import data.Statut;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** SINGLE RESPONSE
 * ----------------------------------------------------------------------------------------------------
 * Components :
 *      HEADER
 *      PROFIL
 * ----------------------------------------------------------------------------------------------------
 * @author Sylvain HUMILIERE | La Société Nouvelle
 */

public class SingleResponse implements Serializable {
    
    // header
    private HeaderResponse header;
    // body
    private ProfilSocialResponse profil;

    /* ---------- Contructor ---------- */
    
    public SingleResponse (DatabaseConnection connection, String siren) {
        
        // Initiate the header
        header = new HeaderResponse();
        
        // check if the pattern of the siren number is correct
        if (siren.matches("[0-9]{9}")) {
            
            try 
            {
                // Buil the response
                profil = new ProfilSocialResponse(connection,header,siren);
             
            // Exception    
            } catch (SQLException ex) {
                header.setStatut(Statut.SERVER_ERROR);
                Logger.getLogger(SingleResponse.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        // Case - pattern doesn't match a siren number
        } else {
            header.setStatut(Statut.BAD_REQUEST);
        }
    }
    
    /* ---------- Getters ---------- */

    public HeaderResponse getHeader() {
        return header;
    }
    public ProfilSocialResponse getProfil() {
        return profil;
    }
    
    
}
