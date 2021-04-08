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

    /* ---------- Contructors ---------- */
    
    // Constructor used for simple request
    public ProfilSocialResponse (DatabaseConnection connection, HeaderResponse header, String siren) throws SQLException {
        
        // Try to build the legal unit description
        this.uniteLegale = new UniteLegaleResponse(connection,siren);
        
        // If the legal unit is in the database
        if(uniteLegale.getSiren()!=null) {
            
            // Get the corporate social footprint
            empreinteSocietale = new EmpreinteSocietale(connection,uniteLegale);
            header.setStatut(Statut.OK);
            
        // If not
        } else {
            header.setStatut(Statut.NOT_FOUND);
        }
    }
    
    // Construtor used for ranking request (UniteLegaleResponse already build)
    public ProfilSocialResponse (DatabaseConnection connection, UniteLegaleResponse uniteLegale) throws SQLException {
        this.uniteLegale = uniteLegale;
        empreinteSocietale = new EmpreinteSocietale(connection, uniteLegale);
    }
   
    /* ---------- Static builder ---------- */
    
    // Builder for list of legal units
    public static ArrayList<ProfilSocialResponse> getListProfilsSociaux (DatabaseConnection connection,ArrayList<UniteLegaleResponse> listUnitesLegales) throws SQLException {
        ArrayList<ProfilSocialResponse> listProfilsSociaux = new ArrayList<>();
        
        // For each legal unit - building the company profile and adding it to the results list
        for (UniteLegaleResponse uniteLegaleResponse : listUnitesLegales) {
            ProfilSocialResponse profilSocialResponse = new ProfilSocialResponse(connection, uniteLegaleResponse);
            listProfilsSociaux.add(profilSocialResponse);
        }
        
        return listProfilsSociaux;
    }  
    
    /* ---------- Getters ---------- */

    public UniteLegaleResponse getDescriptionUniteLegale() {
        if (uniteLegale.getSiren()!=null) {
            return uniteLegale;
        } else {
            return null;
        }
    }
    public HashMap<String,IndicateurResponse> getEmpreinteSocietale() {
        if (empreinteSocietale!=null) {
            return empreinteSocietale.getEmpreinteSocietaleResponse();
        } else {
            return null;
        }
    }
        
}
