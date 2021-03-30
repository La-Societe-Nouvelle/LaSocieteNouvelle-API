/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import meta.Indicateur;
import response.IndicateurResponse;
import response.ProfilSocialResponse;
import response.UniteLegaleResponse;
import static java.lang.Integer.signum;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author SylvainPro
 */

public class DataSort {
    
    // Tri pour recherche unité légale à partir de la dénomination
    // Ordre des critères : société / effectifs ?
    public static UniteLegaleResponse[] getDefaultMatchingUnitesLegales(ArrayList<UniteLegaleResponse> listUniteLegaleResponses,String recherche) {
        UniteLegaleResponse[] unitesLegales = new UniteLegaleResponse[listUniteLegaleResponses.size()];
        int i = 0;
        for (UniteLegaleResponse uniteLegale : listUniteLegaleResponses) {
            unitesLegales[i] = uniteLegale;
            i++;
        }
        fastRanking(unitesLegales,0,unitesLegales.length,recherche);
        return unitesLegales;
    }
    // Algortithme de tri
    private static void fastRanking(UniteLegaleResponse[] unitesLegales,int deb,int fin,String recherche) {
        if (deb<fin) {
            int positionPivot = partition(unitesLegales,deb,fin,recherche);
            fastRanking(unitesLegales,deb,positionPivot,recherche);
            fastRanking(unitesLegales,positionPivot+1,fin,recherche);
        }
    }
    private static int partition(UniteLegaleResponse[] unitesLegales,int deb,int fin,String recherche) {
        UniteLegaleResponse uniteLegalePivot = unitesLegales[deb];
        int compt = deb;
        for (int i=deb+1;i<fin;i++) {
            if (isBetterThan(unitesLegales[i],uniteLegalePivot,recherche.toUpperCase())) {
                compt++;
                echange(unitesLegales,compt,i);
            }
        }
        echange(unitesLegales,deb,compt);
        return compt;
    }
    private static void echange(UniteLegaleResponse[] unitesLegales, int i, int j) {
        UniteLegaleResponse uniteLegale_temp = unitesLegales[i];
        unitesLegales[i] = unitesLegales[j];
        unitesLegales[j] = uniteLegale_temp;
    }
    private static Boolean isBetterThan(UniteLegaleResponse uniteLegale1,UniteLegaleResponse uniteLegale2,String recherche) {
        // Comparaison - catégorie juridique
        if (!uniteLegale1.getCategorieJuridique().substring(0,1).equals(uniteLegale2.getCategorieJuridique().substring(0,1))) {
            if (uniteLegale1.getCategorieJuridique().substring(0,1).equals("5")) {
                return true;
            } else if (uniteLegale2.getCategorieJuridique().substring(0,1).equals("5")) {
                return false;
            } else {
                return uniteLegale1.getCategorieJuridique().substring(0,1).equals("3");
            }
        // Comparaison - tranche effectifs
        } else if (!uniteLegale1.getTrancheEffectifs().equals(uniteLegale2.getTrancheEffectifs())) {
            Integer trancheEffectifs1 = uniteLegale1.isEmployeur() ? Integer.parseInt(uniteLegale1.getTrancheEffectifs()) : 0;
            Integer trancheEffectifs2 = uniteLegale2.isEmployeur() ? Integer.parseInt(uniteLegale2.getTrancheEffectifs()) : 0;
            return trancheEffectifs1>trancheEffectifs2;
        // Comparaison - alphabétique
        } else {
            Integer comparaison = uniteLegale1.getDenomination().compareToIgnoreCase(uniteLegale2.getDenomination());
            if (comparaison!=0) {
                return comparaison < 0;
            } else {
                return true;
            }
        }
    }
    
    // Tri ranking performance
    public static HashMap<Integer,UniteLegaleResponse> getDefaultRankingUnitesLegales(List<ProfilSocialResponse> listProfilsSociaux) {
        HashMap<Integer,UniteLegaleResponse> results = new HashMap<>();
        ProfilSocialResponse[] profilsSociaux = new ProfilSocialResponse[listProfilsSociaux.size()];
        listProfilsSociaux.toArray(profilsSociaux);
        fastRanking(profilsSociaux,0,profilsSociaux.length);
        int rank = 1;
        for (ProfilSocialResponse profilSocial : profilsSociaux) {
            results.put(rank,profilSocial.getDescriptionUniteLegale());
            rank++;
        }
        return results;
    }
    // Algortithme de tri
    private static void fastRanking(ProfilSocialResponse[] profilsSociaux,int deb,int fin) {
        if (deb<fin) {
            int positionPivot = partition(profilsSociaux,deb,fin);
            fastRanking(profilsSociaux,deb,positionPivot);
            fastRanking(profilsSociaux,positionPivot+1,fin);
        }
    }
    private static int partition(ProfilSocialResponse[] profilsSociaux,int deb,int fin) {
        ProfilSocialResponse profilPivot = profilsSociaux[deb];
        int compt = deb;
        for (int i=deb+1;i<fin;i++) {
            if (isBetterThan(profilsSociaux[i],profilPivot)) {
                compt++;
                echange(profilsSociaux,compt,i);
            }
        }
        echange(profilsSociaux,deb,compt);
        return compt;
    }
    private static void echange(ProfilSocialResponse[] profilsSociaux, int i, int j) {
        ProfilSocialResponse profil_temp = profilsSociaux[i];
        profilsSociaux[i] = profilsSociaux[j];
        profilsSociaux[j] = profil_temp;
    }
    // Comparaison
    private static Boolean isBetterThan(ProfilSocialResponse profilSocietal1,ProfilSocialResponse profilSocietal2) {
        Double score = 0.0;
        // Comparaison sur les indicateurs
        for (Indicateur indicateur : Indicateur.values()) {
            score+=DataSort.getComparisonScoreIndicateur(profilSocietal1, profilSocietal2,indicateur);
        }
        // Comparaison autre score (B-Corp / Impact score)
        // Comparaison labels & chartes
        return score>0.0;
    }
    private static Double getComparisonScoreIndicateur(ProfilSocialResponse profilSocietal1,ProfilSocialResponse profilSocietal2,Indicateur indicateur) {
        Double score;
        IndicateurResponse indicateurUniteLegale1 = profilSocietal1.getEmpreinteSocietale().get(indicateur.getCode());
        IndicateurResponse indicateurUniteLegale2 = profilSocietal2.getEmpreinteSocietale().get(indicateur.getCode());
        if (indicateurUniteLegale1!=null & indicateurUniteLegale2!=null) {
            if (indicateurUniteLegale1.isValueDeclared() & indicateurUniteLegale2.isValueDeclared()) {
                score = Math.pow(indicateurUniteLegale1.getValue()/indicateurUniteLegale2.getValue(),indicateur.getCoef())-1;
            } else if (indicateurUniteLegale1.isValueDeclared()) {
                score = Math.pow(indicateurUniteLegale1.getValue()/indicateurUniteLegale2.getValue(),signum(indicateur.getCoef()))-1;
                //score+= 1.0;
                score+= Math.abs(indicateur.getCoef());
            } else if (indicateurUniteLegale2.isValueDeclared()) {
                score = Math.pow(indicateurUniteLegale1.getValue()/indicateurUniteLegale2.getValue(),signum(indicateur.getCoef()))-1;
                //score+= -1.0;
                score-= Math.abs(indicateur.getCoef());
            } else {
                score = Math.pow(indicateurUniteLegale1.getValue()/indicateurUniteLegale2.getValue(),signum(indicateur.getCoef()))-1;
            }
        } else if (indicateurUniteLegale1!=null) {
            score = signum(indicateur.getCoef())*0.5;
        } else if (indicateurUniteLegale2!=null) {
            score =signum(indicateur.getCoef())*(-0.5);
        } else {
            score = 0.0;
        }
        //System.out.println(indicateur.getCode() + " : " + score);
        return score;
    }
    
}
