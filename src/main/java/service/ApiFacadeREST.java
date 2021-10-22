/* API - La Société Nouvelle
 * Licence : OpenSource - Réutilisation libre
 */

package service;

import data.DatabaseConnection;
import response.SingleResponse;
import response.MultipleResponse;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import response.DataSerieResponse;
import response.DefaultDataResponse;

/**  API - LA SOCIETE NOUVELLE | FACADE REST
--------------------------------------------------------------------------------------------------------
Version : 2.0
Services :
   /siren/{siren}
--------------------------------------------------------------------------------------------------------
@author Sylvain HUMILIERE
 */

@Stateless
@Path("/v2")
public class ApiFacadeREST {
        
    @Context
    private HttpServletRequest request;
    public ApiFacadeREST() {}
    
    /* SERVICE - VERIFICATION SERVERR
    ----------------------------------------------------------------------------------------------------
    Requête : serveur disponible
    ----------------------------------------------------------------------------------------------------
    */
    
    @GET
    @Path("/serverON")
    @Produces({MediaType.APPLICATION_JSON})
    public Boolean serverON () {
        return true;
    }
        
    /* SERVICE - BASIC REQUEST
    ----------------------------------------------------------------------------------------------------
    Description : get Social Footprint of a company
    ----------------------------------------------------------------------------------------------------
    */
    
    @GET
    @Path("/siren/{siren}")
    @Produces({MediaType.APPLICATION_JSON})
    public SingleResponse findProfilSocialBySiren (
            @PathParam("siren") String siren) {
        
        // Connection à la base de données
        DatabaseConnection connection = new DatabaseConnection();
        
        // Construction de la réponse
        SingleResponse response = new SingleResponse(connection,siren);
        
        //Fermeture de la connection
        connection.close();
        
        return response;
    }
    
    /* SERVICE - SERIES
    ----------------------------------------------------------------------------------------------------
    Description : get evolution of indicator at macro scale
    ----------------------------------------------------------------------------------------------------
    */
    
    @GET
    @Path("/serie")
    @Produces({MediaType.APPLICATION_JSON})
    public DataSerieResponse getDataSerie (
            @QueryParam("indic") String indic,
            @QueryParam("area") String area,
            @QueryParam("flow") String flow) {
        
        // Connection à la base de données
        DatabaseConnection connection = new DatabaseConnection();
        
        // Construction de la réponse
        DataSerieResponse response = new DataSerieResponse(connection,indic,area,flow);
        
        //Fermeture de la connection
        connection.close();
        
        return response;
    }
    
    /* SERVICE - RECHERCHE UNITE LEGALE 
    ----------------------------------------------------------------------------------------------------
    Requête : recherche d'une unité légale à partir de sa dénomination
    ----------------------------------------------------------------------------------------------------
     */

    @GET
    @Path("/search")
    @Produces({MediaType.APPLICATION_JSON})
    public MultipleResponse searchUniteLegale (
            @QueryParam("denomination") String denomination,
            @QueryParam("page") Integer numPage) {
        DatabaseConnection connection = new DatabaseConnection();
        MultipleResponse response = new MultipleResponse(connection,denomination,numPage);
        connection.close();
        return response;
    }
        
    /* SERVICE - DONNEES PAR DEFAUT
    ----------------------------------------------------------------------------------------------------
    Requête : Obtention de données statistiques
    ----------------------------------------------------------------------------------------------------
    */
    @GET
    @Path("/default")
    @Produces({MediaType.APPLICATION_JSON})
    public DefaultDataResponse findProfilSocialDefault (
            @QueryParam("pays") String pays,
            @QueryParam("activite") String activite,
            @QueryParam("flow") String flow) {
        DatabaseConnection connection = new DatabaseConnection();
        DefaultDataResponse response = new DefaultDataResponse(connection, pays, activite, flow);
        connection.close();
        return response;
    }
        
    /* SERVICE - CLASSEMENT (En cours de développement)
    ----------------------------------------------------------------------------------------------------
    Requête : Obtention des unités légales les plus performantes (ESE)
    ----------------------------------------------------------------------------------------------------
     */
    
    @GET
    @Path("/rank")
    @Produces({MediaType.APPLICATION_JSON})
    public MultipleResponse rankUnitesLegales (
            @QueryParam("activite") String activite,
            @QueryParam("reg") String region,
            @QueryParam("dep") String departement) {
        DatabaseConnection connection = new DatabaseConnection();
        MultipleResponse response = new MultipleResponse(connection,activite,region,departement);
        connection.close();
        return response;
    }    
    
}
