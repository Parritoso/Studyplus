/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.Participantegrupo;

/**
 *
 * @author Parri
 */
@Stateless
@Path("rest.participantegrupo")
public class ParticipantegrupoFacadeREST extends AbstractFacade<Participantegrupo> {
    private static final Logger logger = LoggerFactory.getLogger(ParticipantegrupoFacadeREST.class);

    @PersistenceContext(unitName = "API-RESTPU")
    private EntityManager em;

    public ParticipantegrupoFacadeREST() {
        super(Participantegrupo.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Participantegrupo entity) {
        super.create(entity);
    }

    @POST
    @Path("instant")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) // Añadir Produces para que devuelva la entidad
    public Participantegrupo create_instant(Participantegrupo entity) {
        try{
           super.create(entity); // Esto persiste la entidad en la base de datos
           em.flush(); // Asegura que el ID generado por la DB sea asignado al objeto 'entity'
           return entity; // Devuelve la entidad con el ID
        } catch(Exception e){
            logger.debug("[create_instant] Error en create: "+e.getMessage());
        }
        return null;
    }
    
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Participantegrupo entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Participantegrupo find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Participantegrupo> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Participantegrupo> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
