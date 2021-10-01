/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package response;

//import com.lasocietenouvelle.apisystema.service.ProfilSocial;
import data.DataAccess;
import data.DatabaseConnection;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/** UNITE LEGALE RESPONSE
 * ----------------------------------------------------------------------------------------------------
 * Informations utiles concernant l'unité légale (dénomination, adresse, effectifs, etc.)
 * ----------------------------------------------------------------------------------------------------
 * @author Sylvain HUMILIERE | La Société Nouvelle
 */

public class UniteLegaleResponse implements Serializable {
    
    /* ---------- ATTRIBUTS ---------- */
    
    private String siren;
    private String categorieJuridique;
    private String denomination;
    private String denominationUsuelle;
    private String sigle;
    private String libelleCommuneEtablissement;
    private String codePostalSiege;
    private String activitePrincipale;
    private String activitePrincipaleLibelle;
    private String trancheEffectifs;
    private Boolean isEconomieSocialeSolidaire;
    
    private Boolean isActivitesArtisanales;
    private Boolean isLocalisationEtranger;

    /* ---------- DEFAULT CONSTRUCTEUR ---------- */
    
    public UniteLegaleResponse(DatabaseConnection connection,String siren) throws SQLException 
    {
        // Données - Unité Légale
        ResultSet rs = DataAccess.getUniteLegaleData(connection,siren);
        if (rs.next()) {
            this.siren = rs.getString("siren");
            this.categorieJuridique = rs.getString("categorieJuridiqueUniteLegale");
            this.denomination = getDenomination(rs);
            this.denominationUsuelle = rs.getString("denominationUsuelle1UniteLegale");
            this.sigle = rs.getString("sigleUniteLegale");
            this.libelleCommuneEtablissement = rs.getString("libelleCommuneEtablissement");
            this.codePostalSiege = rs.getString("codePostalEtablissement");
            this.activitePrincipale = rs.getString("activitePrincipaleUniteLegale");
            this.activitePrincipaleLibelle = rs.getString("activitePrincipaleLibelle");
            this.trancheEffectifs = rs.getString("trancheEffectifsUniteLegale");
            this.isEconomieSocialeSolidaire = rs.getString("economieSocialeSolidaireUniteLegale").equals("O");
        }
        // Données - Etablissements
        rs = DataAccess.getEtablissementsData(connection,siren);
        if (rs.next()) {
            this.isActivitesArtisanales = rs.getBoolean("isActivitesArtisanales");
            this.isLocalisationEtranger = rs.getBoolean("isLocalisationEtranger");
        }
    }
        
    private static String getDenomination(ResultSet resultSet) throws SQLException {
        if (!resultSet.getString("denominationUniteLegale").equals("")) {
            return resultSet.getString("denominationUniteLegale");
        } else {
           return resultSet.getString("prenom1UniteLegale") + " " + resultSet.getString("nomUniteLegale");
        }
    }
    
    /* ----- LIST UNITES LEGALES ----- */
    
    public static ArrayList<UniteLegaleResponse> getListUnitesLegalesByExactName(DatabaseConnection connection,String denomination) throws SQLException {
        ArrayList<UniteLegaleResponse> listUnitesLegales = new ArrayList<>();
        ResultSet rs = DataAccess.getUnitesLegalesDenominationLike(connection, denomination);
        while (rs.next()) {
            UniteLegaleResponse uniteLegale = new UniteLegaleResponse(rs);
            listUnitesLegales.add(uniteLegale);
        }
        return listUnitesLegales;
    }
    
    public static ArrayList<UniteLegaleResponse> getListUnitesLegalesBySimilarName(DatabaseConnection connection,String denomination) throws SQLException {
        ArrayList<UniteLegaleResponse> listUnitesLegales = new ArrayList<>();
        ResultSet rs = DataAccess.getUnitesLegalesDenominationSimilarTo(connection, denomination);
        while (rs.next()) {
            UniteLegaleResponse uniteLegale = new UniteLegaleResponse(rs);
            listUnitesLegales.add(uniteLegale);
        }
        return listUnitesLegales;
    }
    
    public static ArrayList<UniteLegaleResponse> getListUnitesLegalesByCharacteristics(DatabaseConnection connection,String activite,String region,String departement) throws SQLException {
        ArrayList<UniteLegaleResponse> listUnitesLegales = new ArrayList<>();
        ResultSet rs = DataAccess.getListUnitesLegales(connection, activite, region, departement);
        while (rs.next()) {
            UniteLegaleResponse uniteLegale = new UniteLegaleResponse(rs);
            listUnitesLegales.add(uniteLegale);
        }
        return listUnitesLegales;
    }
    
    private UniteLegaleResponse (ResultSet resultSet) throws SQLException {
        this.siren = resultSet.getString("siren");
        this.categorieJuridique = resultSet.getString("categorieJuridiqueUniteLegale");
        this.denomination = getDenomination(resultSet);
        this.denominationUsuelle = resultSet.getString("denominationUsuelle1UniteLegale");
        this.sigle = resultSet.getString("sigleUniteLegale");
        this.libelleCommuneEtablissement = resultSet.getString("libelleCommuneEtablissement");
        this.codePostalSiege = resultSet.getString("codePostalEtablissement");
        this.activitePrincipale = resultSet.getString("activitePrincipaleUniteLegale");
        this.activitePrincipaleLibelle = resultSet.getString("activitePrincipaleLibelle");
        this.trancheEffectifs = resultSet.getString("trancheEffectifsUniteLegale");
        this.isEconomieSocialeSolidaire = resultSet.getString("economieSocialeSolidaireUniteLegale").equals("O");
    }
    
    /* ----- GETTERS ----- */

    public String getSiren() {
        return siren;
    }

    public String getCategorieJuridique() {
        return categorieJuridique;
    }
    
    public String getDenomination() {
        return denomination;
    }

    public String getDenominationUsuelle() {
        return denominationUsuelle;
    }

    public String getSigle() {
        return sigle;
    }
    
    public String getCommuneSiege() {
        return libelleCommuneEtablissement;
    }
    
    public String getCodePostalSiege() {
        return codePostalSiege;
    }

    public String getActivitePrincipale() {
        return activitePrincipale;
    }

    public String getActivitePrincipaleLibelle() {
        return activitePrincipaleLibelle;
    }
    
    public String getTrancheEffectifs() {
        return trancheEffectifs;
    }
    
    public Boolean getIsEconomieSocialeSolidaire() {
        return isEconomieSocialeSolidaire;
    }

    public Boolean getIsEmployeur () {
        return !this.trancheEffectifs.equals("NN") & !this.trancheEffectifs.equals("");
    }

    public Boolean getIsActivitesArtisanales() {
        return isActivitesArtisanales;
    }

    public Boolean getIsLocalisationEtranger() {
        return isLocalisationEtranger;
    }
    
}
