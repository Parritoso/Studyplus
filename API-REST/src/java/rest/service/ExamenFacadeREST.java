/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import java.util.Collections;
import java.util.Date;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.Examen;

/**
 *
 * @author Parri
 */
@Stateless
@Path("rest.examen")
public class ExamenFacadeREST extends AbstractFacade<Examen> {
    
    private static final Logger logger = LoggerFactory.getLogger(ExamenFacadeREST.class);

    @PersistenceContext(unitName = "API-RESTPU")
    private EntityManager em;

    public ExamenFacadeREST() {
        super(Examen.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Examen entity) {
        try{
           super.create(entity);
        } catch(Exception e){
            logger.debug("[create] Error en create: "+e.getMessage());
        }
        
    }
    
    @POST
    @Path("instant")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) // Añadir Produces para que devuelva la entidad
    public Examen create_instant(Examen entity) {
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
    public void edit(@PathParam("id") Integer id, Examen entity) {
        try{
           super.edit(entity);
        } catch(Exception e){
            logger.debug("[edit] Error en create: "+e.getMessage());
        }
        
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        try{
           super.remove(super.find(id));
        } catch(Exception e){
            logger.debug("[remove] Error en create: "+e.getMessage());
        }
        
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Examen find(@PathParam("id") Integer id) {
        try{
           return super.find(id);
        } catch(Exception e){
            logger.debug("[find] Error en create: "+e.getMessage());
        }
        return null;
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Examen> findAll() {
        try{
           return super.findAll(); 
        } catch(Exception e){
            logger.debug("[findAll] Error en create: "+e.getMessage());
        }
        return Collections.EMPTY_LIST;
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Examen> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        try{
           return super.findRange(new int[]{from, to}); 
        } catch(Exception e){
            logger.debug("[findRange] Error en create: "+e.getMessage());
        }
        return Collections.EMPTY_LIST;
        
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
    
    @GET
    @Path("byUser/{userId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Examen> findByUser(@PathParam("userId") Integer userId) {
        logger.debug("[findByUser - Examen] Buscando exámenes para el usuario con ID: {}", userId);
        try {
            List<Examen> examenes = em.createQuery(
                "SELECT e FROM Examen e WHERE e.usuarioId.id = :userId", Examen.class)
                .setParameter("userId", userId)
                .getResultList();

            logger.debug("[findByUser - Examen] Se encontraron {} exámenes.", examenes.size());
            return examenes;
        } catch (Exception e) {
            logger.debug("[findByUser - Examen] Error al obtener exámenes: {}", e.getMessage(), e);
            throw new WebApplicationException("Error al obtener exámenes del usuario.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Obtiene una lista de exámenes próximos para un usuario específico.
     * Los exámenes se consideran "próximos" si su fecha de examen es igual o posterior a la fecha actual.
     * Endpoint: GET /rest.examen/usuario/{userId}/proximos
     *
     * @param userId El ID del usuario.
     * @return Una lista de objetos Examen próximos, ordenados por fecha de examen ascendente.
     */
    @GET
    @Path("usuario/{userId}/proximos") // Coincide con la ruta esperada por tu cliente
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Examen> findUpcomingByUserId(@PathParam("userId") Integer userId) {
        logger.debug("[findUpcomingByUserId - Examen] Buscando exámenes próximos para el usuario con ID: {}", userId);
        try {
            List<Examen> examenes = em.createNamedQuery("Examen.findUpcomingByUserId", Examen.class)
                .setParameter("userId", userId)
                .setParameter("currentDate", new Date())
                .getResultList();

            logger.debug("[findUpcomingByUserId - Examen] Se encontraron {} exámenes próximos.", examenes.size());
            return examenes;
        } catch (Exception e) {
            logger.debug("[findUpcomingByUserId - Examen] Error al obtener exámenes próximos: {}", e.getMessage(), e);
            throw new WebApplicationException("Error al obtener exámenes próximos.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
