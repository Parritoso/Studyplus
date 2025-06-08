/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.Conversacion;
import rest.Grupoestudio;
import rest.Participanteconversacion;
import rest.ParticipanteconversacionPK;
import rest.Participantegrupo;
import rest.Usuario;
import rest.dto.ConversacionDTO;
import rest.dto.ConversationRequestPayload;

/**
 *
 * @author Parri
 */
@Stateless
@Path("rest.conversacion")
public class ConversacionFacadeREST extends AbstractFacade<Conversacion> {
    private static final Logger logger = LoggerFactory.getLogger(ConversacionFacadeREST.class);

    @PersistenceContext(unitName = "API-RESTPU")
    private EntityManager em;

    public ConversacionFacadeREST() {
        super(Conversacion.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Conversacion entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Conversacion entity) {
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
    public Conversacion find(@PathParam("id") Integer id) {
        return super.find(id);
    }
    
    @GET
    @Path("{id}-group")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Conversacion findByGroup(@PathParam("id") Integer id) {
        try{
            Conversacion conversacion = em.createQuery("SELECT c FROM Conversacion c WHERE c.grupoEstudio.id = :id", Conversacion.class).setParameter("id", id).getSingleResult();
            logger.info("Se encontro una conversación para el grupo {} con id: {}",id,conversacion.getId());
            return conversacion;
        } catch (NumberFormatException e) {
            logger.error("Error: id no es un número válido: " + id, e);
            // Retorna una lista vacía o lanza una excepción HTTP 400 Bad Request
            return null;
        } catch (Exception e) {
            logger.error("Error inesperado al obtener la conversacion para el grupo " + id + ": " + e.getMessage(), e);
            // Retorna una lista vacía o lanza una excepción HTTP 500 Internal Server Error
            return null;
        }
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Conversacion> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Conversacion> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    /**
     * Endpoint para obtener todas las conversaciones de un usuario específico.
     * URL: GET /rest.conversacion/user/{userId}
     *
     * @param userIdStr El ID del usuario en formato String.
     * @return Una lista de objetos ConversacionDTO en formato JSON o XML.
     */
    @GET
    @Path("user/{userId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<ConversacionDTO> getConversationsForUser(@PathParam("userId") String userIdStr) {
        try {
            Integer userId = Integer.parseInt(userIdStr);
            logger.info("Solicitud para obtener conversaciones para el usuario ID: {}.", userId);

            // JPQL para obtener conversaciones, haciendo JOIN FETCH con participantes y mensajes
            // Esto asegura que estas colecciones se carguen EAGERLY dentro de la misma transacción.
            // DISTINCT es crucial para evitar duplicados en el resultado debido al JOIN FETCH.
            // PASO 1: Obtener los IDs de las conversaciones en las que participa el usuario.
            // Esto actúa como un filtro inicial.
            List<Integer> conversationIds = em.createQuery(
                "SELECT c.id FROM Conversacion c JOIN c.participanteconversacionCollection pc WHERE pc.usuario.id = :userId", Integer.class)
                .setParameter("userId", userId)
                .getResultList();

            if (conversationIds.isEmpty()) {
                logger.info("No se encontraron conversaciones para el usuario ID: {}.", userId);
                return new ArrayList<>(); // Si no hay IDs, no hay conversaciones
            }
            
            // PASO 2: Cargar las conversaciones completas con *todos* sus participantes y mensajes
            // usando los IDs obtenidos. El JOIN FETCH ahora traerá todas las colecciones.
            List<Conversacion> rawConversations = em.createQuery(
                "SELECT DISTINCT c FROM Conversacion c " +
                "JOIN FETCH c.participanteconversacionCollection pc_all " + // pc_all traerá *todos* los participantes de 'c'
                "LEFT JOIN FETCH c.mensajeCollection m " +                 // m traerá todos los mensajes de 'c'
                "WHERE c.id IN :conversationIds", Conversacion.class) // Filtrar por los IDs encontrados
                .setParameter("conversationIds", conversationIds)
                .getResultList();
            
            Set<Conversacion> uniqueConversations = new HashSet<>(rawConversations);
            List<Conversacion> finalConversations = new ArrayList<>(uniqueConversations);
            // Transformar las entidades Conversacion a DTOs y calcular los campos
            List<ConversacionDTO> dtos = new ArrayList<>();
//            for (Conversacion conv : finalConversations) {
//                // Las colecciones 'participanteconversacionCollection' y 'mensajeCollection'
//                // ahora deberían estar completamente inicializadas gracias al JOIN FETCH.
//                dtos.add(new ConversacionDTO(conv, userId)); // Pasa el userId actual para el cálculo de mensajes no leídos
//            }
//            List<Conversacion> conversations = em.createQuery(
//                "SELECT DISTINCT c FROM Conversacion c " +
//                "JOIN FETCH c.participanteconversacionCollection pc " + // Carga los participantes
//                "LEFT JOIN FETCH c.mensajeCollection m " +             // Carga los mensajes (LEFT JOIN para conversaciones sin mensajes)
//                "WHERE pc.usuario.id = :userId", Conversacion.class)
//                .setParameter("userId", userId)
//                .getResultList();
//
//            // Transformar las entidades Conversacion a DTOs
//            List<ConversacionDTO> dtos = new ArrayList<>();
            for (Conversacion conv : finalConversations) {
                // Aquí, las colecciones 'participanteconversacionCollection' y 'mensajeCollection'
                // ya deberían estar inicializadas gracias al JOIN FETCH en la consulta.
                dtos.add(new ConversacionDTO(conv, userId)); // Pasa el userId actual para el cálculo de mensajes no leídos
            }

            // Opcional: Ordenar las conversaciones en el backend (no leídas primero, luego por fecha)
            dtos.sort((c1, c2) -> {
                boolean c1Unread = c1.getUnreadMessagesCount() > 0;
                boolean c2Unread = c2.getUnreadMessagesCount() > 0;

                if (c1Unread && !c2Unread) {
                    return -1; // c1 (no leída) va antes que c2 (leída)
                }
                if (!c1Unread && c2Unread) {
                    return 1; // c2 (no leída) va antes que c1 (leída)
                }

                // Si ambos tienen mensajes no leídos o ambos no tienen, ordenar por fecha del último mensaje (descendente)
                if (c1.getUltimoMensajeFecha() == null && c2.getUltimoMensajeFecha() == null) {
                    return 0; // Sin mensajes, el orden no importa
                }
                if (c1.getUltimoMensajeFecha() == null) {
                    return 1; // c2 tiene mensajes, c1 no, c2 va antes
                }
                if (c2.getUltimoMensajeFecha() == null) {
                    return -1; // c1 tiene mensajes, c2 no, c1 va antes
                }
                return c2.getUltimoMensajeFecha().compareTo(c1.getUltimoMensajeFecha()); // Más reciente primero
            });

            logger.info("Conversaciones obtenidas y transformadas a DTOs para usuario ID {}: {} DTOs.", userId, dtos.size());
            return dtos;

        } catch (NumberFormatException e) {
            logger.error("Error: userId no es un número válido: " + userIdStr, e);
            // Retorna una lista vacía o lanza una excepción HTTP 400 Bad Request
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error inesperado al obtener conversaciones para el usuario " + userIdStr + ": " + e.getMessage(), e);
            // Retorna una lista vacía o lanza una excepción HTTP 500 Internal Server Error
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca una conversación existente entre dos usuarios. Si no existe, crea una nueva.
     * La búsqueda es agnóstica al orden de los IDs de usuario (usuario1-usuario2 o usuario2-usuario1).
     *
     * @param requestPayload Contiene los IDs de los dos usuarios.
     * @return Una respuesta con la Conversacion (encontrada o creada) y el estado HTTP (OK o CREATED).
     * Retorna BAD_REQUEST si los IDs son nulos o iguales, NOT_FOUND si los usuarios no existen,
     * o INTERNAL_SERVER_ERROR si ocurre un error inesperado.
     */
    @POST
    @Path("findOrCreate")
    @Consumes(MediaType.APPLICATION_JSON) // Specifies that this endpoint consumes JSON
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response findOrCreateConversation(ConversationRequestPayload requestPayload){
        Integer user1Id = requestPayload.getUser1Id();
        Integer user2Id = requestPayload.getUser2Id();

        logger.debug("Recibida solicitud findOrCreate para usuarios: user1Id={}, user2Id={}", user1Id, user2Id);

        // Validaciones básicas de los IDs de usuario
        if (user1Id == null || user2Id == null) {
            logger.warn("Solicitud findOrCreate con IDs de usuario nulos. user1Id={}, user2={}", user1Id, user2Id);
            return Response.status(Response.Status.BAD_REQUEST).entity("Ambos IDs de usuario son requeridos.").build();
        }
        
        // Evitar conversaciones consigo mismo
        if (user1Id.equals(user2Id)) {
            logger.warn("Intento de crear/buscar conversación con el mismo usuario: user1Id={}", user1Id);
            return Response.status(Response.Status.BAD_REQUEST).entity("No se puede crear una conversación con el mismo usuario.").build();
        }

        try {
            // 1. Obtener las entidades de Usuario correspondientes a los IDs
            Usuario usuario1 = em.find(Usuario.class, user1Id);
            Usuario usuario2 = em.find(Usuario.class, user2Id);

            if (usuario1 == null || usuario2 == null) {
                logger.warn("Uno o ambos usuarios no encontrados. user1Id={}, user2Id={}", user1Id, user2Id);
                return Response.status(Response.Status.NOT_FOUND).entity("Uno o ambos usuarios no existen.").build();
            }

            // --- 2. Buscar si ya existe una conversación 1-a-1 entre estos dos usuarios específicos ---
            // La estrategia es buscar Conversaciones que:
            // a) Tengan a ambos usuarios (user1 y user2) como participantes.
            // b) Tengan un total de EXACTAMENTE dos participantes (para asegurar que es 1-a-1).

            // JPQL para encontrar conversaciones con ambos usuarios como participantes.
            // Esto nos da las IDs de conversación que involucran a ambos, pero podrían ser conversaciones grupales.
            List<Integer> potentialConversationIds = em.createQuery(
                "SELECT pc.conversacion.id FROM Participanteconversacion pc " +
                "WHERE pc.usuario.id IN (:user1Id, :user2Id) " +
                "GROUP BY pc.conversacion.id " +
                "HAVING COUNT(DISTINCT pc.usuario.id) = 2", Integer.class) // Asegura que ambos usuarios están en la conversación
                .setParameter("user1Id", user1Id)
                .setParameter("user2Id", user2Id)
                .getResultList();

            if (!potentialConversationIds.isEmpty()) {
                // Ahora, para cada una de estas IDs, verificamos si tiene exactamente 2 participantes en total.
                for (Integer convId : potentialConversationIds) {
                    Long totalParticipantsInConv = em.createQuery(
                        "SELECT COUNT(pc) FROM Participanteconversacion pc WHERE pc.conversacion.id = :convId", Long.class)
                        .setParameter("convId", convId)
                        .getSingleResult();

                    if (totalParticipantsInConv == 2) {
                        // ¡Encontrada la conversación 1-a-1!
                        Conversacion foundConversation = em.find(Conversacion.class, convId);
                        logger.info("Conversación 1-a-1 existente encontrada entre ID {} y ID {}. ID Conversación: {}", user1Id, user2Id, foundConversation.getId());
                        return Response.ok(foundConversation).build(); // HTTP 200 OK
                    }
                }
            }

            // --- 3. Si no existe una conversación 1-a-1, crear una nueva ---
            logger.info("No se encontró conversación 1-a-1 existente. Creando una nueva entre ID {} y ID {}.", user1Id, user2Id);
            
            Conversacion newConversation = new Conversacion();
            newConversation.setFechaCreacion(new Date()); // Establecer la fecha de creación
            // ultimoMensajeFecha puede ser null inicialmente, se actualizará con el primer mensaje.

            em.persist(newConversation); // Persistir la nueva conversación
            em.flush(); // Importante: fuerza la sincronización con la DB para obtener el ID autogenerado.

            // Crear y persistir el Participanteconversacion para el primer usuario
            ParticipanteconversacionPK pk1 = new ParticipanteconversacionPK(newConversation.getId(), user1Id);
            Participanteconversacion participante1 = new Participanteconversacion();
            participante1.setParticipanteconversacionPK(pk1);
            participante1.setConversacion(newConversation); // Enlazar la entidad Conversacion
            participante1.setUsuario(usuario1);             // Enlazar la entidad Usuario
            participante1.setFechaUnion(new Date());        // Establecer la fecha de unión

            em.persist(participante1);

            // Crear y persistir el Participanteconversacion para el segundo usuario
            ParticipanteconversacionPK pk2 = new ParticipanteconversacionPK(newConversation.getId(), user2Id);
            Participanteconversacion participante2 = new Participanteconversacion();
            participante2.setParticipanteconversacionPK(pk2);
            participante2.setConversacion(newConversation); // Enlazar la entidad Conversacion
            participante2.setUsuario(usuario2);             // Enlazar la entidad Usuario
            participante2.setFechaUnion(new Date());        // Establecer la fecha de unión

            em.persist(participante2);

            logger.info("Nueva conversación creada (ID: {}) con participantes {} y {}.", newConversation.getId(), user1Id, user2Id);
            return Response.status(Response.Status.CREATED).entity(newConversation).build(); // HTTP 201 Created

        } catch (Exception e) {
            logger.error("Error inesperado al buscar o crear conversación entre ID {} y ID {}: {}", user1Id, user2Id, e.getMessage(), e);
            // Dependiendo del error, podrías dar un mensaje más específico.
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al procesar la solicitud de conversación.").build();
        }
    }
    
    /**
     * Obtiene o crea la conversación asociada a un grupo.
     * Corresponde a GET /rest.conversacion/group/{groupId}
     *
     * @param groupId ID del grupo.
     * @return El objeto Conversacion del grupo.
     */
    @GET
    @Path("group/{groupId}")
    public Response getOrCreateGroupConversation(@PathParam("groupId") String groupIdStr) {
        Integer groupId;
        try {
            groupId = Integer.parseInt(groupIdStr);
        } catch (NumberFormatException e) {
            logger.error("ID de grupo inválido: {}", groupIdStr, e);
            return Response.status(Response.Status.BAD_REQUEST).entity("ID de grupo inválido.").build();
        }

        logger.info("Solicitud para obtener o crear conversación para el Grupo ID: {}.", groupId);

        Grupoestudio grupoestudio;
        try {
            grupoestudio = em.find(Grupoestudio.class, groupId);
            if (grupoestudio == null) {
                logger.warn("Grupo de estudio con ID {} no encontrado.", groupId);
                return Response.status(Response.Status.NOT_FOUND).entity("Grupo de estudio no encontrado.").build();
            }
        } catch (Exception e) {
            logger.error("Error al buscar grupo de estudio con ID {}: {}", groupId, e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al buscar grupo de estudio.").build();
        }

        // ***** IMPORTANTE: Asunción de relación *****
        // Asumimos que la entidad 'Conversacion' tiene un campo que referencia directamente a 'Grupoestudio'
        // o a su ID, para poder vincular la conversación al grupo de forma única.
        // Si no existe este campo en tu entidad 'Conversacion', esta query fallará o será ineficiente.
        // Por ejemplo, si 'Conversacion' tuviera un campo 'grupoAsociado' (OneToOne/ManyToOne) o 'grupoAsociadoId' (Integer).
        Conversacion groupConversation;
        try {
            // Ejemplo de cómo buscarías la conversación si hubiera un campo 'grupoAsociado' en Conversacion
            // Si tu entidad Conversacion tiene un campo 'grupoAsociado' que es un objeto Grupoestudio
            groupConversation = em.createQuery(
                "SELECT c FROM Conversacion c WHERE c.grupoEstudio.id = :groupId", Conversacion.class)
                .setParameter("groupId", groupId)
                .getSingleResult();
            
            logger.info("Conversación existente encontrada para el Grupo ID {}.", groupId);
            return Response.ok(groupConversation).build();

        } catch (NoResultException e) {
            logger.info("No se encontró conversación para el Grupo ID {}. Creando nueva conversación.", groupId);
            // La conversación no existe, la creamos
            groupConversation = new Conversacion();
            groupConversation.setFechaCreacion(new Date());
            // Si Conversacion tuviera un campo 'grupoAsociado', lo asignarías aquí:
            groupConversation.setGrupoEstudio(grupoestudio);

            em.persist(groupConversation);
            em.flush(); // Fuerza la asignación del ID a la conversación recién creada

            logger.info("Nueva conversación creada con ID: {}.", groupConversation.getId());

            // Añadir participantes del grupo a la conversación
            if (grupoestudio.getParticipantegrupoCollection() != null) {
                for (Participantegrupo pg : grupoestudio.getParticipantegrupoCollection()) {
                    Usuario usuario = pg.getUsuarioId(); // Obtener el Usuario del Participantegrupo
                    if (usuario != null) {
                        ParticipanteconversacionPK pk = new ParticipanteconversacionPK(groupConversation.getId(), usuario.getId());
                        Participanteconversacion pc = new Participanteconversacion();
                        pc.setParticipanteconversacionPK(pk);
                        pc.setConversacion(groupConversation);
                        pc.setUsuario(usuario);
                        pc.setFechaUnion(new Date()); // Fecha actual de unión a la conversación

                        em.persist(pc);
                        logger.debug("Usuario ID {} añadido a la Conversación ID {}.", usuario.getId(), groupConversation.getId());
                    }
                }
            }
            em.flush(); // Guarda los participantes de la conversación

            logger.info("Conversación creada y participantes del grupo añadidos para el Grupo ID {}.", groupId);
            return Response.status(Response.Status.CREATED).entity(groupConversation).build();

        } catch (Exception e) {
            logger.error("Error inesperado al obtener o crear conversación para el Grupo ID {}: {}", groupId, e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error interno al procesar la conversación del grupo.").build();
        }
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
