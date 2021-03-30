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
    
    ECO ("ECO","Contribution à l'Economie nationale","%",true,5),
    ART ("ART","Contribution aux Métiers d'Art et aux Savoir-Faire","%",true,2),
    SOC ("SOC","Contribution aux Acteurs d'Intérêt social","%",true,3),
    KNW ("KNW","Contribution à l'Evolution des Connaissances et des Compétences","%",true,2),
    DIS ("DIS","Indice de répartition des revenus","",true,-2),
    GEQ ("GEQ","Indice d'Egalité des revenus entre les Femmes et les Hommes","",true,-3),
    GHG ("GHG","Intensité d'Emission de Gaz à Effet de Serre","gCO2e/€",true,-5),
    MAT ("MAT","Intensité d'Extraction de Matières premières","g/€",true,-3),
    WAS ("WAS","Intensité de Production de Déchets","g/€",true,-4),
    NRG ("NRG","Intensité de Consommation d'Energie","kJ/€",true,-4),
    WAT ("WAT","Intensité de Consommation d'Eau","L/€",true,-3),
    HAZ ("HAZ","Intensité d'Utilisation de produits dangereux pour la Santé et l'Environnement","g/€",true,-2),
    IEP ("IEP","Index de l'Egalité professionnelle Femmes-Hommes","/100",false,1);
        
    private Indicateur (String code, String libelle, String unit, Boolean iqve,Integer coef) {
        this.code = code;
        this.libelle = libelle;
        this.unit = unit;
        this.iqve = iqve;
        this.coef = coef;
    }
        
    private final String code;
    private final String libelle;
    private final String unit;
    private final Boolean iqve;
    private final Integer coef;
    
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
