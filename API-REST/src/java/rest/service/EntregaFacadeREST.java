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
import rest.Entrega;

/**
 *
 * @author Parri
 */
@Stateless
@Path("rest.entrega")
public class EntregaFacadeREST extends AbstractFacade<Entrega> {
    
    private static final Logger logger = LoggerFactory.getLogger(EntregaFacadeREST.class);

    @PersistenceContext(unitName = "API-RESTPU")
    private EntityManager em;

    public EntregaFacadeREST() {
        super(Entrega.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Entrega entity) {
        try{
            super.create(entity);
        } catch(Exception e){
            logger.debug("[create] Error en create: "+e.getMessage());
        }
    }
    
    /**
     * Crea una nueva Sesión de Estudio. Ahora devuelve la entidad SesionEstudio
     * creada con el ID asignado por la base de datos.
     *
     * @param entity El objeto SesionEstudio a crear.
     * @return La SesionEstudio creada con su ID.
     */
    @POST
    @Path("instant")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) // Añadir Produces para que devuelva la entidad
    public Entrega create_instant(Entrega entity) {
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
    public void edit(@PathParam("id") Integer id, Entrega entity) {
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
    public Entrega find(@PathParam("id") Integer id) {
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
    public List<Entrega> findAll() {
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
    public List<Entrega> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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
    public List<Entrega> findByUser(@PathParam("userId") Integer userId) {
        logger.debug("[findByUser - Entrega] Buscando entregas para el usuario con ID: {}", userId);
        try {
            List<Entrega> entregas = em.createQuery(
                "SELECT e FROM Entrega e WHERE e.usuarioId.id = :userId", Entrega.class)
                .setParameter("userId", userId)
                .getResultList();

            logger.debug("[findByUser - Entrega] Se encontraron {} entregas.", entregas.size());
            return entregas;
        } catch (Exception e) {
            logger.debug("[findByUser - Entrega] Error al obtener entregas: {}", e.getMessage(), e);
            throw new WebApplicationException("Error al obtener entregas del usuario.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Obtiene una lista de entregas próximas para un usuario específico.
     * Las entregas se consideran "próximas" si su fecha límite es igual o posterior a la fecha actual
     * y su estado no es 'Completada'.
     * Endpoint: GET /rest.entrega/usuario/{userId}/proximas
     *
     * @param userId El ID del usuario.
     * @return Una lista de objetos Entrega próximas, ordenadas por fecha límite ascendente.
     */
    @GET
    @Path("usuario/{userId}/proximas") // Coincide con la ruta esperada por tu cliente
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Entrega> findUpcomingByUserId(@PathParam("userId") Integer userId) {
        logger.debug("[findUpcomingByUserId - Entrega] Buscando entregas próximas para el usuario con ID: {}", userId);
        try {
            List<Entrega> entregas = em.createNamedQuery("Entrega.findUpcomingByUserId", Entrega.class)
                .setParameter("userId", userId)
                .setParameter("currentDate", new Date())
                .getResultList();

            logger.debug("[findUpcomingByUserId - Entrega] Se encontraron {} entregas próximas.", entregas.size());
            return entregas;
        } catch (Exception e) {
            logger.debug("[findUpcomingByUserId - Entrega] Error al obtener entregas próximas: {}", e.getMessage(), e);
            throw new WebApplicationException("Error al obtener entregas próximas.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
