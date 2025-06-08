/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.Amistad;
import rest.Usuario;
import rest.dto.FriendRequestPayload;
import rest.dto.FriendshipResponse;

/**
 *
 * @author Parri
 */
@Stateless
@Path("rest.amistad")
public class AmistadFacadeREST extends AbstractFacade<Amistad> {
    
    private static final Logger logger = LoggerFactory.getLogger(AmistadFacadeREST.class);

    @PersistenceContext(unitName = "API-RESTPU")
    private EntityManager em;

    public AmistadFacadeREST() {
        super(Amistad.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Amistad entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Amistad entity) {
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
    public Amistad find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Amistad> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Amistad> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @GET
    @Path("friends/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usuario> getFriends(@PathParam("id") Integer id) {
        try {
            // Buscar los amigos desde la tabla de amistad
            List<Usuario> amigos = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.id IN ("
                + "SELECT CASE WHEN a.usuario1Id.id = :id THEN a.usuario2Id.id ELSE a.usuario1Id.id END " // Corrección aquí
                + "FROM Amistad a WHERE (a.usuario1Id.id = :id OR a.usuario2Id.id = :id) AND a.estado = 'aceptado')", // Corrección aquí
                Usuario.class)
                .setParameter("id", id)
                .getResultList();

            logger.debug("[getFriends] Lista: "+Arrays.toString(amigos.toArray()));
            return amigos;

        } catch (NoResultException e) {
            logger.debug("[getFriends] No se encontraron amigos para el usuario {}: {}", id, e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("[getFriends] Error general al obtener amigos para el usuario {}: {}", id, e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    @GET
    @Path("pending/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Amistad> getPendingRequests(@PathParam("id") Integer id) {
        try {
            return em.createQuery(
                "SELECT a FROM Amistad a WHERE (a.usuario1Id.id = :id OR a.usuario2Id.id = :id) AND a.estado = 'pendiente'",
                Amistad.class)
                .setParameter("id", id)
                .getResultList();

        } catch (NoResultException e) {
            logger.debug("[getPendingRequests] No se encontraron solicitudes pendientes para el usuario {}: {}", id, e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("[getPendingRequests] Error general al obtener solicitudes pendientes para el usuario {}: {}", id, e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    @PUT
    @Path("{id}/accept")
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptFriend(@PathParam("id") Integer id) {
        try {
            Amistad amistad = em.find(Amistad.class, id);

            if (amistad == null) {
                return Response.status(Response.Status.NOT_FOUND) // 404
                           .entity(new FriendshipResponse("error", "Amistad no encontrada con ID: " + id))
                           .build();
            }

            if (!"pendiente".equalsIgnoreCase(amistad.getEstado())) {
                return Response.status(Response.Status.CONFLICT) // 409
                           .entity(new FriendshipResponse("error", "La solicitud ya fue procesada (estado actual: " + amistad.getEstado() + ")"))
                           .build();
            }

            amistad.setEstado("aceptado");
            amistad.setFechaRespuesta(new Date());

            em.merge(amistad);

            return Response.ok(new FriendshipResponse("aceptado", "Solicitud de amistad aceptada.")).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR) // 500
                       .entity(new FriendshipResponse("error", "Error al aceptar la solicitud de amistad: " + e.getMessage()))
                       .build();
        }
    }

    
    @PUT
    @Path("{id}/reject")
    @Produces(MediaType.TEXT_PLAIN)
    public String rejectFriend(@PathParam("id") Integer id) {
        try {
            Amistad amistad = em.find(Amistad.class, id);

            if (amistad == null) {
                return "Amistad no encontrada con ID: " + id;
            }

            if (!"pendiente".equalsIgnoreCase(amistad.getEstado())) {
                return "La solicitud ya fue procesada (estado actual: " + amistad.getEstado() + ")";
            }

            amistad.setEstado("rechazado");
            amistad.setFechaRespuesta(new Date());

            em.merge(amistad);

            return "Solicitud de amistad rechazada.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al rechazar la solicitud de amistad: " + e.getMessage();
        }
    }

    @POST
    @Path("request")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendFriendRequest(FriendRequestPayload payload) {
        try {
            if (payload == null || payload.getRemitenteId() == null || payload.getReceptorId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity("Parámetros de solicitud inválidos.").build();
            }

            // Verificar si ya existe una amistad entre estos usuarios (en cualquier orden)
            TypedQuery<Amistad> query = em.createQuery(
                "SELECT a FROM Amistad a WHERE " +
                "(a.usuario1Id.id = :user1 AND a.usuario2Id.id = :user2) OR " + // <-- Corrección aquí
                "(a.usuario1Id.id = :user2 AND a.usuario2Id.id = :user1)",    // <-- Corrección aquí
                Amistad.class
            );
            query.setParameter("user1", payload.getRemitenteId());
            query.setParameter("user2", payload.getReceptorId());

            List<Amistad> existentes = query.getResultList();
            if (!existentes.isEmpty()) {
                return Response.status(Response.Status.CONFLICT)
                               .entity("Ya existe una solicitud o amistad entre estos usuarios.").build();
            }

            // Crear nueva solicitud de amistad
            Amistad amistad = new Amistad();
            amistad.setUsuario1Id(em.find(Usuario.class, payload.getRemitenteId()));
            amistad.setUsuario2Id(em.find(Usuario.class, payload.getReceptorId()));
            amistad.setEstado("pendiente");
            amistad.setFechaPeticion(new Date());

            em.persist(amistad);

            return Response.status(Response.Status.CREATED).entity(amistad).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error interno al procesar la solicitud de amistad.").build();
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
