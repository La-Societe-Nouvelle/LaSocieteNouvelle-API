/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package response;

import data.Statut;

/**
 *
 * @author SylvainPro
 */
public class HeaderResponse {
    
    private Statut statut;

    public HeaderResponse() {
        this.statut = Statut.SERVER_ERROR;
    }

    public Integer getStatut() {
        return statut.getCode();
    }

    public String getMessage() {
        return statut.getMessage();
    }
    
    public void setStatut (Statut statut) {
        this.statut = statut;
    }
    
}
