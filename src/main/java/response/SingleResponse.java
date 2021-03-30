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
    
    private HeaderResponse header;
    private ProfilSocialResponse profil;

    /* ---------- CONSTRUCTORS ---------- */
    
    public SingleResponse (DatabaseConnection connection, String siren) {
        header = new HeaderResponse();
        if (siren.matches("[0-9]{9}")) {
            try {
                profil = new ProfilSocialResponse(connection,header,siren);
            } catch (SQLException ex) {
                header.setStatut(Statut.SERVER_ERROR);
                Logger.getLogger(SingleResponse.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            header.setStatut(Statut.BAD_REQUEST);
        }
    }
    
    /* ---------- GETTERS ---------- */

    public HeaderResponse getHeader() {
        return header;
    }
    public ProfilSocialResponse getProfil() {
        return profil;
    }
    
    
}
