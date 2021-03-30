/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meta;

/**
 *
 * @author SylvainPro
 */
public enum Flag {
    
    HIGH ("h","Valeur déclarée",true,true),
    MEDIUM ("m","Valeur déclarée",true,true),
    LOW ("l","Valeur déclarée",true,true),
    PUBLISHED("p","Valeur déclarée",true,true),
    REPORTED ("r","Valeur obtenue à partir de données publiées",false,true),
    ESTIMATED ("e","Valeur estimée à partir de données publiées",false,true),
    ADJUSTED ("a","Valeur statistique ajustée",false,true),
    STATISTIC ("s","Valeur statistique",false,false),
    DEFAULT ("d","Valeur par défaut",false,false),
    NOT_APPLICABLE ("n","Non applicable",false,false);
        
    private Flag (String code, String libelle, Boolean isDeclared, Boolean isAdjusted) {
        this.code = code;
        this.libelle = libelle;
        this.isDeclared = isDeclared;
        this.isAdjusted = isAdjusted;
    }
        
    private final String code;
    private final String libelle;
    private final Boolean isDeclared;
    private final Boolean isAdjusted;
    
    public String getCode () {
        return this.code;
    }

    public String getLibelle() {
        return libelle;
    }
    
    public Boolean isDeclared() {
        return isDeclared;
    }

    public Boolean isAdjusted() {
        return isAdjusted;
    }
    
    /* ----- AUTRES ----- */
    
    public static Flag getFlag (String flag) {
        
        switch (flag) {
            case "h" :
                return HIGH;
            case "m" :
                return MEDIUM;
            case "l" :
                return LOW;
            case "p" :
                return PUBLISHED;
            case "r" :
                return REPORTED;
            case "e" :
                return ESTIMATED;
            case "a" :
                return ADJUSTED;
            case "s" :
                return STATISTIC;
            case "d" :
                return DEFAULT;
            case "n" :
                return NOT_APPLICABLE;
            default:
                return DEFAULT;
        }
        
    }
    
}
