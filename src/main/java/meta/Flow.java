/* API - La Société Nouvelle
 * Licence : OpenSource - Réutilisation libre
 */

package meta;

/** FLOW ENUM
 * ----------------------------------------------------------------------------------------------------
 * Decription : economic flow
 * ----------------------------------------------------------------------------------------------------
 * @author Sylvain HUMILIERE | La Société Nouvelle
 */

public enum Flow {
    
    //      CODE            LABEL
    // ------------------------------------------------------------------------------------------
    
    // Flux - Espaces économiques
    GAP(    "GAP",          "Gross Available Production"),
    GDP(    "GDP",          "Gross Domestic Production"),
    IMP(    "IMP",          "Importations"),
    // Flux - Branches d'activités
    PRD(    "PRD",          "Production"),
    GVA(    "GVA",          "Gross Value Added"),
    IC(     "IC",           "Intermediate Consumptions"),
    // Autres flux
    NAP(    "NAP",          "Net Available Production"),
    NDP(    "NDP",          "Net Domestic Production"),
    NVA(    "NVA",          "Net Value Added"),
    TC(     "TC",           "Total Consumptions");
    
    // ------------------------------------------------------------------------------------------
        
    /* ---------- Attributs ---------- */
    
    private final String code;
    private final String libelle;
    
    /* ---------- Constructor ---------- */
    
    private Flow (String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }

    /* ---------- Check ---------- */
    
    public static Boolean isCodeCorrect(String code) {
        for (Flow flow : Flow.values()) {
            if (flow.code.equals(code)) return true;
        }
        return false;
    }
    
    public static Boolean isCodeCorrect(String code,String activite) 
    {
        // Espaces économiques
        if (activite.equals("00"))
        {
            return (code.equals(GAP.code) 
                 || code.equals(GDP.code) 
                 || code.equals(IMP.code));
        }
        // Branches d'activités
        else 
        {
            return (code.equals(PRD.code) 
                 || code.equals(GVA.code) 
                 || code.equals(IC.code));
        }
    }
    
    /* ---------- Getters ---------- */
    
    public String getCode () {
        return this.code;
    }

    public String getLibelle() {
        return libelle;
    }    
    
}
