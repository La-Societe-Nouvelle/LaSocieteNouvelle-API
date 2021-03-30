/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package response;

/**
 *
 * @author SylvainPro
 */
public class InfoResultsResponse {
    
    private Integer nbResults;
    private String libelleRecherche;
    private Integer nbPage;
    private Integer currentPage;

    public InfoResultsResponse() {
    }
    
    /* ----- SETTERS ----- */
    
    public void setNbResults (Integer statut) {
        this.nbResults = statut;
    }
    public void setLibelleRecherche (String message) {
        this.libelleRecherche = message;
    }
    public void setNbPage(Integer nbPage) {
        this.nbPage = nbPage;
    }
    public void setCurrentPage(Integer currentPage) {
        if (currentPage==null) {
            this.currentPage = 1;
        } else if (currentPage < 1) {
            this.currentPage = 1;
        } else {
            this.currentPage = currentPage;
        }
    }
    
    /* ----- GETTERS ----- */
    
    public Integer getNbResults() {
        return nbResults;
    }
    public String getLibelleRecherche() {
        return libelleRecherche;
    }
    public Integer getNbPage() {
        return nbPage;
    }
    public Integer getCurrentPage() {
        return currentPage;
    }
    
    
       
}
