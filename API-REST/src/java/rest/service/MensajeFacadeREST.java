/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.Mensaje;

/**
 *
 * @author Parri
 */
@Stateless
@Path("rest.mensaje")
public class MensajeFacadeREST extends AbstractFacade<Mensaje> {
    private static final Logger logger = LoggerFactory.getLogger(MensajeFacadeREST.class);

    @PersistenceContext(unitName = "API-RESTPU")
    private EntityManager em;

    public MensajeFacadeREST() {
        super(Mensaje.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Mensaje entity) {
        try{
           super.create(entity);
        } catch(Exception e){
            logger.debug("[create] Error en create: "+e.getMessage());
        }
        
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Mensaje entity) {
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
    public Mensaje find(@PathParam("id") Integer id) {
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
    public List<Mensaje> findAll() {
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
    public List<Mensaje> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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

    /**
     * Cuenta el número de mensajes no leídos en una conversación específica
     * para un usuario dado (que no es el emisor del mensaje).
     * Endpoint: GET /rest.mensaje/unreadCount/{conversationId}/{userId}
     *
     * @param conversationId El ID de la conversación.
     * @param userId El ID del usuario para el que se cuentan los mensajes no leídos (es decir, el receptor).
     * @return El número de mensajes no leídos como texto plano.
     */
    @GET
    @Path("unreadCount/{conversationId}/{userId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String countUnreadMessagesInConversationForUser(
            @PathParam("conversationId") Integer conversationId,
            @PathParam("userId") Integer userId) {
        logger.debug("[countUnreadMessagesInConversationForUser] Contando mensajes no leídos en la conversación con ID: {} para el usuario con ID: {}", conversationId, userId);
        try {
            // Contamos mensajes que no han sido leídos (leido = false)
            // Y donde el emisor NO es el userId (el usuario no lee sus propios mensajes al enviarlos)
            String jpql = "SELECT COUNT(m) FROM Mensaje m WHERE m.conversacionId.id = :conversationId AND m.leido = false AND m.emisorId.id != :userId";
            Query query = em.createQuery(jpql);
            query.setParameter("conversationId", conversationId);
            query.setParameter("userId", userId);
            Long count = (Long) query.getSingleResult();
            logger.debug("[countUnreadMessagesInConversationForUser] Se encontraron {} mensajes no leídos.", count);
            return String.valueOf(count);
        } catch (Exception e) {
            logger.debug("[countUnreadMessagesInConversationForUser] Error al contar mensajes no leídos: {}", e.getMessage(), e);
            e.printStackTrace();
            throw new WebApplicationException("Error interno al contar mensajes no leídos.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Marca como leídos todos los mensajes no leídos de una conversación para un usuario específico.
     * Esto significa que los mensajes fueron enviados por otro usuario y el usuario actual los acaba de ver.
     * Endpoint: PUT /rest.mensaje/read/{conversationId}/{userId}
     *
     * @param conversationId El ID de la conversación.
     * @param userId El ID del usuario que está leyendo los mensajes (es decir, el receptor).
     */
    @PUT
    @Path("read/{conversationId}/{userId}")
    public void markMessagesAsReadForUser(
            @PathParam("conversationId") Integer conversationId,
            @PathParam("userId") Integer userId) {
        logger.debug("[markMessagesAsReadForUser] Marcando mensajes como leídos en la conversación con ID: {} para el usuario con ID: {}", conversationId, userId);
        try {
            // Marca como leídos los mensajes que no han sido leídos (leido = false)
            // Y donde el emisor NO es el userId (solo marca como leídos los que recibió de otros)
            String jpql = "UPDATE Mensaje m SET m.leido = true WHERE m.conversacionId.id = :conversationId AND m.leido = false AND m.emisorId.id != :userId";
            Query query = em.createQuery(jpql);
            query.setParameter("conversationId", conversationId);
            query.setParameter("userId", userId);
            int updatedCount = query.executeUpdate();
            logger.debug("[markMessagesAsReadForUser] Se marcaron {} mensajes como leídos.", updatedCount);
        } catch (Exception e) {
            logger.debug("[markMessagesAsReadForUser] Error al marcar mensajes como leídos: {}", e.getMessage(), e);
            e.printStackTrace();
            throw new WebApplicationException("Error interno al marcar mensajes como leídos.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Este endpoint es necesario para que el MensajeClient pueda obtener la lista de mensajes de un chat.
    @GET
    @Path("conversacion/{conversationId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Mensaje> getMessagesInConversation(
            @PathParam("conversationId") Integer conversationId,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) {
        logger.debug("[getMessagesInConversation] Obteniendo mensajes de la conversación con ID: {}. Offset: {}, Limit: {}", conversationId, offset, limit);
        try {
            // Asegúrate de que los mensajes se ordenen por fecha de envío para una visualización correcta.
            // Los trae más nuevos primero por DESC.
            String jpql = "SELECT m FROM Mensaje m WHERE m.conversacionId.id = :conversationId ORDER BY m.fechaEnvio DESC";
            Query query = em.createQuery(jpql);
            query.setParameter("conversationId", conversationId);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            List<Mensaje> mensajes = query.getResultList();
            logger.debug("[getMessagesInConversation] Se encontraron {} mensajes.", mensajes.size());
            return mensajes;
        } catch (Exception e) {
            logger.debug("[getMessagesInConversation] Error al obtener mensajes en la conversación: {}", e.getMessage(), e);
            e.printStackTrace();
            throw new WebApplicationException("Error al obtener mensajes de la conversación.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
