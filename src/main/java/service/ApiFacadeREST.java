/*
 * API - La Société Nouvelle
 * Licence : OpenSource - Réutilisation avec modification à des fins commercciales autorisée
 * 
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
import response.DefaultDataResponse;

/**  API - LA SOCIETE NOUVELLE | FACADE REST
--------------------------------------------------------------------------------------------------------
Request to check if the server is on
--------------------------------------------------------------------------------------------------------
@author Sylvain HUMILIERE
 */

@Stateless
@Path("/v2")
public class ApiFacadeREST {
        
    @Context
    private HttpServletRequest request;
    public ApiFacadeREST() {}

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    
    /* REQUEST - CKECK SERVER
    ----------------------------------------------------------------------------------------------------
    Request to check if the server is on
    ----------------------------------------------------------------------------------------------------
    */
    
    @GET
    @Path("/serverON")
    @Produces({MediaType.APPLICATION_JSON})
    public Boolean serverON () {
        return true;
    }
    
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    
    /* REQUEST - SIREN
    ----------------------------------------------------------------------------------------------------
    Request for the Societal Footprint of a legal unit (company)
    ----------------------------------------------------------------------------------------------------
    */
    @GET
    @Path("/siren/{siren}")
    @Produces({MediaType.APPLICATION_JSON})
    public SingleResponse findProfilSocialBySiren (
            @PathParam("siren") String siren) {
        DatabaseConnection connection = new DatabaseConnection();
        SingleResponse response = new SingleResponse(connection,siren);
        connection.close();
        return response;
    }
    
    /* REQUEST - DENOMINATION 
    ----------------------------------------------------------------------------------------------------
    Request to find a legal unit in the database by its denomination
    ----------------------------------------------------------------------------------------------------
    Les résultats correspondent aux unités légales dont la raison sociale (denomination ou prénom et 
    nom) contient la recherche
    Note : Si le temps de réponse dépasse 5000 ms, seuls les résultats exacts sont revoyés
    
    Critères de classement des résultats (dans l'ordre d'importance) :
        - Catégorie juridique (société puis entrepreneur individuel)
        - Tranche d'effectifs (de la taille la plus grande à la plus petite)
        - Ordre alphabétique
    Les critères ont été choisis pour renvoyer les unités légales ayant davantage de probabilité d'être 
    recherchées.
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
    
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    
    /* REQUEST - DEFAULT DATA
    ----------------------------------------------------------------------------------------------------
    Request for defaut economic value quality by country (required) and activity
    ----------------------------------------------------------------------------------------------------
    */
    @GET
    @Path("/default")
    @Produces({MediaType.APPLICATION_JSON})
    public DefaultDataResponse findProfilSocialDefault (
            @QueryParam("pays") String pays,
            @QueryParam("activite") String activite) {
        DatabaseConnection connection = new DatabaseConnection();
        DefaultDataResponse response = new DefaultDataResponse(connection,pays,activite);
        connection.close();
        return response;
    }
    
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    
    /* REQUEST - RANKING OF COMPANIES
    ----------------------------------------------------------------------------------------------------
    Request for the "best" companies in a specific country and economic sector
    ----------------------------------------------------------------------------------------------------
    Seules les unités légales dont au moins une valeur est ajustée (déclaration ou utilisation de 
    données publiques propres à l'entreprise)
    Le classement repose sur un tri rapide avec une comparaison suivant les valeurs des indicateurs.
    Pour chaque indicateur, un score est obtenu correspondant à un rapport coeficienté des valeurs, avec 
    la règle suivante :
        - si les deux valeurs sont déclarées : 
            coef 5 pour GHG; coef 4 pour ECO, NRG, coef 3 pour ART, SOC, ,coef 2 
        - si une des deux valeurs est déclarée :
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
    
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    
    
}
