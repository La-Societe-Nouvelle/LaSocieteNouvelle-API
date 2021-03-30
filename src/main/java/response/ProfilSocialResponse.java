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
import java.util.ArrayList;
import java.util.HashMap;

/** PROFIL RESPONSE
 * ----------------------------------------------------------------------------------------------------
 * Components :
 *      UNITE LEGALE
 *      EMPREINTE SOCIETALE
 * ----------------------------------------------------------------------------------------------------
 * @author Sylvain HUMILIERE | La Société Nouvelle
 */

public class ProfilSocialResponse implements Serializable {
           
    private final UniteLegaleResponse uniteLegale;
    private EmpreinteSocietale empreinteSocietale;

    /* ---------- CONSTRUCTORS ---------- */
    
    public ProfilSocialResponse (DatabaseConnection connection, HeaderResponse header, String siren) throws SQLException {
        this.uniteLegale = new UniteLegaleResponse(connection,siren);
        if(uniteLegale.getSiren()!=null) {
            empreinteSocietale = new EmpreinteSocietale(connection,uniteLegale);
            header.setStatut(Statut.OK);
        } else {
            header.setStatut(Statut.NOT_FOUND);
        }
    }
    
    public ProfilSocialResponse (DatabaseConnection connection, UniteLegaleResponse uniteLegale) throws SQLException {
        this.uniteLegale = uniteLegale;
        empreinteSocietale = new EmpreinteSocietale(connection, uniteLegale);
    }
   
    /* ----- STATIC METHOD ----- */
    
    public static ArrayList<ProfilSocialResponse> getListProfilsSociaux (DatabaseConnection connection,ArrayList<UniteLegaleResponse> listUnitesLegales) throws SQLException {
        ArrayList<ProfilSocialResponse> listProfilsSociaux = new ArrayList<>();
        for (UniteLegaleResponse uniteLegaleResponse : listUnitesLegales) {
            ProfilSocialResponse profilSocialResponse = new ProfilSocialResponse(connection, uniteLegaleResponse);
            listProfilsSociaux.add(profilSocialResponse);
        }
        return listProfilsSociaux;
    }  
    
    /* ----- GETTERS ----- */

    public UniteLegaleResponse getDescriptionUniteLegale() {
        return uniteLegale;
    }
    public HashMap<String,IndicateurResponse> getEmpreinteSocietale() {
        return empreinteSocietale.getEmpreinteSocietaleResponse();
    }
        
}
