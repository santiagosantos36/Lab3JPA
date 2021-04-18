/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Competitor;
import com.example.models.CompetitorDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Mauricio
 */

@Path("/competitors")
@Produces(MediaType.APPLICATION_JSON)
public class CompetitorService {

@PersistenceContext(unitName = "CompetitorsPU")
 EntityManager entityManager;
    
    @PostConstruct
 public void init() {
    try {
    entityManager =
   PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
    } catch (Exception e) {
    e.printStackTrace();
    }
    }
 

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {

        Query q = entityManager.createQuery("select u from Competitor u order by u.surname ASC");
        List<Competitor> competitors = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin","*").entity(competitors).build();

    }


    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompetitor(CompetitorDTO competitor) {
 JSONObject rta = new JSONObject();
 Competitor competitorTmp = new Competitor();
 competitorTmp.setAddress(competitor.getAddress());
 competitorTmp.setAge(competitor.getAge());
 competitorTmp.setCellphone(competitor.getCellphone());
 competitorTmp.setCity(competitor.getCity());
 competitorTmp.setCountry(competitor.getCountry());
 competitorTmp.setName(competitor.getName());
 competitorTmp.setSurname(competitor.getSurname());
 competitorTmp.setTelephone(competitor.getTelephone());
 competitorTmp.setContraseña(competitor.getContraseña());
 try {
 entityManager.getTransaction().begin();
 entityManager.persist(competitorTmp);
 entityManager.getTransaction().commit();
 entityManager.refresh(competitorTmp);
 rta.put("competitor_id", competitorTmp.getId());
 } catch (Throwable t) {
 t.printStackTrace();
 if (entityManager.getTransaction().isActive()) {
 entityManager.getTransaction().rollback();
 }
 competitorTmp = null;
 } finally {
 entityManager.clear();
 entityManager.close();
 }
 return Response.status(200).header("Access-Control-Allow-Origin","*").entity(rta).build();
    
    
}
    
   @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(CompetitorDTO competitor) throws JSONException {
        JSONObject rta = new JSONObject();
        Competitor competitorTmp = new Competitor();
        competitorTmp.setAddress(competitor.getAddress());
        competitorTmp.setContraseña(competitor.getContraseña());
        Query q = entityManager.createQuery("select u from Competitor u order by u.surname ASC");
        List<Competitor> competitors = q.getResultList();
        for (int i = 0; i < competitors.size(); i++) {

            if(competitors.get(i).getAddress().equals(competitorTmp.getAddress())&&competitors.get(i).getContraseña().equals(competitorTmp.getContraseña())){
                System.out.println("----> entra");
                rta.put("Ingresó correctamente", competitors.get(i).getId());
            }
            else {
            throw new NotAuthorizedException("No existe el usuario, porfavor verifique la informacion");
            
            }
        }

        return Response.status(200).header("Access-Control-Allow-Origin","*").entity(rta).build();
        
        
    }
    public class NotAuthorizedException extends WebApplicationException {
        public NotAuthorizedException(String message) {
            super(Response.status(Response.Status.UNAUTHORIZED).entity(message).type(MediaType.TEXT_PLAIN).build());
        }
    }
}
