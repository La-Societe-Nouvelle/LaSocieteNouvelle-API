/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package response;

import data.DatabaseConnection;
import data.Statut;
import java.sql.SQLException;
import java.util.HashMap;
import meta.Indicateur;
import service.DataSerieBuilder;

/**
 *
 * @author SylvainPro
 */
public class DataSerieResponse {
    
    private HeaderResponse header;
    private HashMap<String,GrossValueResponse> serie;
    private HashMap<String,String> metaData;
    
    public DataSerieResponse (DatabaseConnection connection, String indic, String area, String flow) 
    {
        Indicateur indicateur = Indicateur.getIndicateur(indic);
        
        header = new HeaderResponse();  
        
        if (indicateur!=null && area!=null && flow!=null)
        {
            DataSerieBuilder builder = new DataSerieBuilder(connection);
            try 
            {
                builder.buildDataSerie(indicateur, area, flow);
                serie = builder.getSerie();
                metaData = builder.getMetaData();
                header.setStatut(serie.size() > 0 ? Statut.OK : Statut.NOT_FOUND);
            } 
            catch (SQLException ex) {header.setStatut(Statut.SERVER_ERROR);}
        }
        else {header.setStatut(Statut.BAD_REQUEST);}        
    }

    public HeaderResponse getHeader() {
        return header;
    }

    public HashMap<String, GrossValueResponse> getSerie() {
        return serie;
    }

    public HashMap<String, String> getMetaData() {
        return metaData;
    }
    
}
