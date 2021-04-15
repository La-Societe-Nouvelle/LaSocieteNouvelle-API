/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meta;

import java.util.ArrayList;

/**
 *
 * @author SylvainPro
 */
public enum Indicateur {
    
    /* --- INDICATEURS ESE --- */
    ECO ("ECO","Contribution à l'Economie nationale",                                               "%",        true,   +5,     1),
    ART ("ART","Contribution aux Métiers d'Art et aux Savoir-Faire",                                "%",        true,   +2,     1),
    SOC ("SOC","Contribution aux Acteurs d'Intérêt social",                                         "%",        true,   +3,     1),
    KNW ("KNW","Contribution à l'Evolution des Connaissances et des Compétences",                   "%",        true,   +2,     1),
    DIS ("DIS","Indice de répartition des revenus",                                                 "/100",     true,   -2,     1),
    GEQ ("GEQ","Indice d'Ecart des rémunérations entre les Femmes et les Hommes",                   "%",        true,   -3,     1),
    GHG ("GHG","Intensité d'Emission de Gaz à Effet de Serre",                                      "gCO2e/€",  true,   -5,     0),
    MAT ("MAT","Intensité d'Extraction de Matières premières",                                      "g/€",      true,   -3,     0),
    WAS ("WAS","Intensité de Production de Déchets",                                                "g/€",      true,   -4,     0),
    NRG ("NRG","Intensité de Consommation d'Energie",                                               "kJ/€",     true,   -4,     0),
    WAT ("WAT","Intensité de Consommation d'Eau",                                                   "L/€",      true,   -3,     1),
    HAZ ("HAZ","Intensité d'Utilisation de produits dangereux pour la Santé et l'Environnement",    "g/€",      true,   -2,     1),
    
    /* --- AUTRES INDICATEURS --- */
    IEP ("IEP","Index de l'Egalité professionnelle Femmes-Hommes",                                  "/100",     false,  +1,     0),
    
    /* --- DONNEES COMPLEMENTAIRES DISPONIBLES --- */
    NVA ("NVA","Taux de Valeur Ajoutée Nette",                                                      "%",        false,  null,   1),
    IMP ("IMP","Taux d'Importations",                                                               "%",        false,  null,   1);
        
    private final String code;
    private final String libelle;
    private final String unit;
    private final Boolean iqve;
    private final Integer coef;
    private final Integer precision;
    
    private Indicateur (String code, String libelle, String unit, Boolean iqve,Integer coef,Integer precision) {
        this.code = code;
        this.libelle = libelle;
        this.unit = unit;
        this.iqve = iqve;
        this.coef = coef;
        this.precision = precision;
    }
        
    public String getCode () {
        return this.code;
    }

    public String getLibelle() {
        return libelle;
    }
    
    public String getUnit() {
        return unit;
    }

    public Boolean isIqve() {
        return iqve;
    }

    public Integer getCoef() {
        return coef;
    }
    
    public Integer getPrecision() {
        return precision;
    }

    /* ----- AUTRES ----- */
    
    public static Indicateur getIndicateur(String indic) {
        
        switch (indic) {
            case "ECO" :
                return ECO;
            case "ART" :
                return ART;
            case "SOC" :
                return SOC;
            case "KNW" :
                return KNW;
            case "DIS" :
                return DIS;
            case "GEQ" :
                return GEQ;
            case "GHG" :
                return GHG;
            case "MAT" :
                return MAT;
            case "WAS" :
                return WAS;
            case "NRG" :
                return NRG;
            case "WAT" :
                return WAT;
            case "HAZ" :
                return HAZ;
            case "IEP" :
                return IEP;
            default:
                return ECO;
        }
        
    }
    public static ArrayList<Indicateur> getListIQVE() {
        ArrayList<Indicateur> listIQVE = new ArrayList<>();
        for (Indicateur indicateur : Indicateur.values()) {
            if (indicateur.isIqve()) {
                listIQVE.add(indicateur);
            }
        }
        return listIQVE;
    }
    
}
