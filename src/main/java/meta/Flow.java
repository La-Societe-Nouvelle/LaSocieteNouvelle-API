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
    
    // Valeurs déclarées/publiées
    GAP(    "GAP",          "Gross Available Production"),
    GDP(    "GDP",          "Gross Domestic Production"),
    GVA(    "GVA",          "Gross Value Added"),
    IC(     "IC",           "Intermediate Consumptions"),
    IMP(    "IMP",          "Importations"),
    NAP(    "NAP",          "Net Available Production"),
    NDP(    "NDP",          "Net Domestic Production"),
    NVA(    "NVA",          "Net Value Added"),
    PRD(    "PRD",          "Production"),
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
    
    /* ---------- Getters ---------- */
    
    public String getCode () {
        return this.code;
    }

    public String getLibelle() {
        return libelle;
    }    
    
}
