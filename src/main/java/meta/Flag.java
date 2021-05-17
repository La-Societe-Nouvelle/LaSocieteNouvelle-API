/* API - La Société Nouvelle
 * Licence : OpenSource - Réutilisation libre
 */

package meta;

/** FLAG ENUM
 * ----------------------------------------------------------------------------------------------------
 * Decription : flag to inform about the origin of the values
 * ----------------------------------------------------------------------------------------------------
 * @author Sylvain HUMILIERE | La Société Nouvelle
 */

public enum Flag {
    
    // VARIABLE             CODE    LABEL                                   DECLARED    ADJUSTED
    // ------------------------------------------------------------------------------------------
    
    // Valeurs déclarées/publiées
    PUBLICATION(            "p",    "Valeur déclarée",                      true,       true),
    PUBLIC_REPORTING(       "r",    "Valeur publique",                      false,      true),
    
    // Valeurs ajustées
    SIMPLIFIED_PUBLICATION( "h",    "Valeur déclarée sur valeur ajoutée",   true,       true),
    ESTIMATION(             "e",    "Valeur estimée",                       false,      true),
    ADJUSTED_DATA(          "a",    "Valeur ajustée",                       false,      true),
    
    // Valeurs par defaut
    SECTOR_SPECIFIC_DATA(   "s",    "Valeur sectorielle",                   false,      false),
    DEFAULT_DATA(           "d",    "Valeur par défaut",                    false,      false),
    
    // Valeur non applicable
    NOT_APPLICABLE(         "n",    "Non applicable",                       false,      false);
    
    // ------------------------------------------------------------------------------------------
        
    /* ---------- Attributs ---------- */
    
    private final String code;
    private final String libelle;
    private final Boolean isDeclared;   // specified if the value is declared by the company
    private final Boolean isAdjusted;   // specified if the value is adjusted or if a default one
    
    /* ---------- Constructor ---------- */
    
    private Flag (String code, String libelle, Boolean isDeclared, Boolean isAdjusted) {
        this.code = code;
        this.libelle = libelle;
        this.isDeclared = isDeclared;
        this.isAdjusted = isAdjusted;
    }

    /* ---------- Getters ---------- */
    
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
    
    /* ---------- retrieve Flag object from char ---------- */
    
    public static Flag getFlag (String flag) {
        
        switch (flag) 
        {
            case "p" :
                return PUBLICATION;
            case "h" :
                return SIMPLIFIED_PUBLICATION;
            case "r" :
                return PUBLIC_REPORTING;
            case "e" :
                return ESTIMATION;
            case "a" :
                return ADJUSTED_DATA;
            case "s" :
                return SECTOR_SPECIFIC_DATA;
            case "d" :
                return DEFAULT_DATA;
            case "n" :
                return NOT_APPLICABLE;
            default:
                return DEFAULT_DATA;
        }
    }
    
}
