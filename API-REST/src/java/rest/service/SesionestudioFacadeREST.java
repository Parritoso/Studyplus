/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.Sesionestudio;
import rest.dto.SessionCompletionData;
import rest.dto.SessionState;

/**
 *
 * @author Parri
 */
@Stateless
@Path("rest.sesionestudio")
public class SesionestudioFacadeREST extends AbstractFacade<Sesionestudio> {
    private static final Logger logger = LoggerFactory.getLogger(SesionestudioFacadeREST.class);

    @PersistenceContext(unitName = "API-RESTPU")
    private EntityManager em;

    public SesionestudioFacadeREST() {
        super(Sesionestudio.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Sesionestudio entity) {
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
    public Sesionestudio create_instant(Sesionestudio entity) {
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
    public void edit(@PathParam("id") Integer id, Sesionestudio entity) {
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
    public Sesionestudio find(@PathParam("id") Integer id) {
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
    public List<Sesionestudio> findAll() {
        try{
           return super.findAll();
        } catch(Exception e){
            logger.debug("[findRange] Error en create: "+e.getMessage());
        }
        return Collections.EMPTY_LIST;
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Sesionestudio> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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

    /**
     * Endpoint para obtener sesiones de estudio por ID de usuario y rango de
     * fechas. Los parámetros de fecha se esperan en milisegundos desde la época
     * (timestamp).
     *
     * @param userId El ID del usuario.
     * @param fromMillis Timestamp de inicio del rango (inclusive).
     * @param toMillis Timestamp de fin del rango (inclusive).
     * @return Lista de SesionEstudio que cumplen con los criterios.
     */
    @GET
    @Path("byUser/{userId}/{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Sesionestudio> findByUserIdAndDateRange(
            @PathParam("userId") Integer userId,
            @PathParam("from") Long fromMillis,
            @PathParam("to") Long toMillis) {

        Date startDate = new Date(fromMillis);
        Date endDate = new Date(toMillis);

        logger.debug("[findByUserIdAndDateRange] Buscando sesiones de estudio para el usuario con ID: {} en el rango de fechas: {} - {}", userId, startDate, endDate);
        // Se asume que en tu entidad SesionEstudio, el usuario está mapeado como una entidad Usuario
        // y se puede acceder a su ID con .usuarioId.id, o directamente si es una FK primitiva.
        // Si UsuarioId en SesionEstudio es un objeto Usuario, la consulta sería s.usuarioId.id
        // Si UsuarioId es un Integer directamente, la consulta sería s.usuarioId
        // Basado en tu entidad SesionEstudio, es un objeto Usuario, así que usamos s.usuarioId.id
        try {
            List<Sesionestudio> sesiones = em.createQuery(
                    "SELECT s FROM SesionEstudio s WHERE s.usuarioId.id = :userId AND s.fechaInicioReal BETWEEN :startDate AND :endDate", Sesionestudio.class)
                    .setParameter("userId", userId)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();

            logger.debug("[findByUserIdAndDateRange] Se encontraron {} sesiones de estudio en el rango de fechas.", sesiones.size());
            return sesiones;
        } catch (Exception e) {
            logger.debug("[findByUserIdAndDateRange] Error al obtener sesiones de estudio: {}", e.getMessage(), e);
            throw new WebApplicationException("Error al obtener sesiones de estudio.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para obtener sesiones de estudio por ID de usuario y tipo
     * (upcoming/history).
     *
     * @param userId El ID del usuario.
     * @param type "upcoming" o "history".
     * @return Lista de SesionEstudio.
     */
    @GET
    @Path("user/{userId}/{type}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Sesionestudio> getSessionsByUserIdAndType(
            @PathParam("userId") Integer userId,
            @PathParam("type") String type) {

        String jpql;
        if ("upcoming".equalsIgnoreCase(type)) {
            jpql = "SELECT s FROM Sesionestudio s WHERE s.usuarioId.id = :userId AND s.estado = 'PLANNED' AND s.fechaSesionPlanificada >= :now ORDER BY s.fechaSesionPlanificada ASC";
        } else if ("history".equalsIgnoreCase(type)) {
            jpql = "SELECT s FROM Sesionestudio s WHERE s.usuarioId.id = :userId AND (s.estado = 'COMPLETED' OR s.estado = 'ABORTED') ORDER BY s.fechaFinReal DESC";
        } else {
            // Tipo desconocido, podrías lanzar una excepción o devolver una lista vacía
            logger.debug("[getSessionsByUserIdAndType] Tipo desconocido: {}", type);
            return new java.util.ArrayList<>();
        }

        logger.debug("[getSessionsByUserIdAndType] Buscando sesiones de estudio para el usuario con ID: {} de tipo: {}", userId, type);
        
        try {
            javax.persistence.TypedQuery<Sesionestudio> query = em.createQuery(jpql, Sesionestudio.class)
                    .setParameter("userId", userId);

            if ("upcoming".equalsIgnoreCase(type)) {
                query.setParameter("now", new Date());
            }

            List<Sesionestudio> sesiones = query.getResultList();
            logger.debug("[getSessionsByUserIdAndType] Se encontraron {} sesiones de estudio de tipo '{}'.", sesiones.size(), type);
            return sesiones;
        } catch (NoResultException e) {
            logger.debug("[getSessionsByUserIdAndType] No se encontraron sesiones de estudio para el usuario con ID: {} y tipo: {}", userId, type);
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            logger.debug("[getSessionsByUserIdAndType] Error al obtener sesiones de estudio: {}", e.getMessage(), e);
            throw new WebApplicationException("Error al obtener sesiones de estudio.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para iniciar una sesión de estudio. Cambia el estado a ACTIVE y
     * establece fechaInicioReal.
     *
     * @param id ID de la sesión.
     * @return La sesión actualizada.
     */
    @PUT
    @Path("{id}/start")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response startSession(@PathParam("id") Integer id) {
        logger.debug("[startSession] Iniciando sesión con ID: {}", id);
        Sesionestudio sesion = super.find(id);
        if (sesion == null) {
            logger.warn("[startSession] Sesión no encontrada para ID: {}", id);
            return Response.status(Response.Status.NOT_FOUND).entity("Sesión no encontrada.").build();
        }
        if (!sesion.getEstado().equals(SessionState.PLANNED.name()) && !sesion.getEstado().equals(SessionState.PAUSED.name()) && !sesion.getEstado().equals(SessionState.ACTIVE.name())) {
            logger.warn("[startSession] Estado de sesión inválido para iniciar/reanudar. Estado actual: {}", sesion.getEstado());
            return Response.status(Response.Status.BAD_REQUEST).entity("Estado de sesión inválido para iniciar/reanudar.").build();
        }

        sesion.setEstado(SessionState.ACTIVE.name());
        if (sesion.getFechaInicioReal() == null) { // Solo establece la primera vez que se inicia
            sesion.setFechaInicioReal(new Date());
            logger.info("[startSession] Se estableció la fecha de inicio real para la sesión con ID: {}", id);
        }
        super.edit(sesion);
        logger.info("[startSession] Sesión con ID: {} ha sido iniciada. Estado: {}", id, sesion.getEstado());
        return Response.ok(sesion).build();
    }

    /**
     * Endpoint para pausar una sesión de estudio. Cambia el estado a PAUSED y
     * actualiza el tiempo efectivo.
     *
     * @param id ID de la sesión.
     * @return La sesión actualizada.
     */
    @PUT
    @Path("{id}/pause")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response pauseSession(@PathParam("id") Integer id) {
        logger.debug("[pauseSession] Pausando sesión con ID: {}", id);
        Sesionestudio sesion = super.find(id);
        if (sesion == null) {
            logger.warn("[pauseSession] Sesión no encontrada para ID: {}", id);
            return Response.status(Response.Status.NOT_FOUND).entity("Sesión no encontrada.").build();
        }
        if (!sesion.getEstado().equals(SessionState.ACTIVE.name())) {
            logger.warn("[pauseSession] Estado de sesión inválido para pausar. Estado actual: {}", sesion.getEstado());
            return Response.status(Response.Status.BAD_REQUEST).entity("Estado de sesión inválido para pausar.").build();
        }

        // Calcular tiempo efectivo desde el último inicio/reanudación
        if (sesion.getFechaInicioReal() != null) {
            long elapsedMillis = new Date().getTime() - sesion.getFechaInicioReal().getTime();
            Integer currentEffective = sesion.getTiempoEstudioEfectivoMinutos() != null ? sesion.getTiempoEstudioEfectivoMinutos() : 0;
            sesion.setTiempoEstudioEfectivoMinutos(currentEffective + (int) (elapsedMillis / (1000 * 60)));
            logger.debug("[pauseSession] Tiempo efectivo calculado y actualizado a: {} minutos.", sesion.getTiempoEstudioEfectivoMinutos());
        }

        sesion.setEstado(SessionState.PAUSED.name());
        super.edit(sesion);
        logger.info("[pauseSession] Sesión con ID: {} ha sido pausada. Tiempo efectivo actualizado a: {} minutos", id, sesion.getTiempoEstudioEfectivoMinutos());
        return Response.ok(sesion).build();
    }

    /**
     * Endpoint para finalizar una sesión de estudio. Cambia el estado a
     * COMPLETED/ABORTED y actualiza todos los campos de finalización.
     *
     * @param id ID de la sesión.
     * @param completionData Datos de finalización de la sesión.
     * @return La sesión actualizada.
     */
    @PUT
    @Path("{id}/end")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response endSession(@PathParam("id") Integer id, SessionCompletionData completionData) {
        logger.debug("[endSession] Finalizando sesión con ID: {}", id);
        Sesionestudio sesion = super.find(id);
        if (sesion == null) {
            logger.warn("[endSession] Sesión no encontrada para ID: {}", id);
            return Response.status(Response.Status.NOT_FOUND).entity("Sesión no encontrada.").build();
        }
        if (!sesion.getEstado().equals(SessionState.ACTIVE.name()) && !sesion.getEstado().equals(SessionState.PAUSED.name())) {
            logger.warn("[endSession] Estado de sesión inválido para finalizar. Estado actual: {}", sesion.getEstado());
            return Response.status(Response.Status.BAD_REQUEST).entity("Estado de sesión inválido para finalizar.").build();
        }

        Date now = new Date();
        sesion.setFechaFinReal(now);
        sesion.setNotas(completionData.getNotes());
        sesion.setCalificacionEnfoque(completionData.getFocusRating());
        sesion.setDetallesInterrupcion(completionData.getInterruptionDetails());
        sesion.setEstado(completionData.getOutcome().toUpperCase()); // "COMPLETED" o "ABORTED"
        
        logger.debug("[endSession] Estado de la sesión actualizado a: {}", sesion.getEstado());

        // Calcular duración real total (desde fechaInicioReal hasta fechaFinReal)
        if (sesion.getFechaInicioReal() != null) {
            long totalDurationMillis = now.getTime() - sesion.getFechaInicioReal().getTime();
            sesion.setDuracionRealMinutos((int) (totalDurationMillis / (1000 * 60)));
            logger.debug("[endSession] Duración real calculada: {} minutos.", sesion.getDuracionRealMinutos());
        }

        // Si estaba activa, actualizar tiempo efectivo por última vez
        if (sesion.getEstado().equals(SessionState.ACTIVE.name()) && sesion.getFechaInicioReal() != null) {
            long elapsedMillis = now.getTime() - sesion.getFechaInicioReal().getTime();
            Integer currentEffective = sesion.getTiempoEstudioEfectivoMinutos() != null ? sesion.getTiempoEstudioEfectivoMinutos() : 0;
            sesion.setTiempoEstudioEfectivoMinutos(currentEffective + (int) (elapsedMillis / (1000 * 60)));
            logger.debug("[endSession] Tiempo efectivo finalizado y actualizado a: {} minutos.", sesion.getTiempoEstudioEfectivoMinutos());
        }
        // Si estaba pausada, el tiempo efectivo ya se calculó al pausar.

        // El tiempo de descanso no se calcula directamente aquí, se acumularía en el frontend
        // o se calcularía como DuracionReal - TiempoEfectivo al finalizar.
        super.edit(sesion);
        logger.info("[endSession] Sesión con ID: {} finalizada. Estado: {}, Duración real: {} minutos", id, sesion.getEstado(), sesion.getDuracionRealMinutos());
        
        return Response.ok(sesion).build();
    }

    /**
     * Obtiene las sesiones de estudio asociadas a un grupo.
     *
     * @param groupId ID del grupo.
     * @return Lista de SesionEstudio.
     */
    @GET
    @Path("group/{groupId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Sesionestudio> findSessionsByGroupId(@PathParam("groupId") Integer groupId) {
        logger.debug("[findSessionsByGroupId] Buscando sesiones de estudio para el grupo con ID: {}", groupId);
        try {
            List<Sesionestudio> sesiones = em.createQuery(
                "SELECT s FROM Sesionestudio s WHERE s.grupoAsociadoId.id = :groupId ORDER BY s.fechaSesionPlanificada DESC", 
                Sesionestudio.class)
                .setParameter("groupId", groupId)
                .getResultList();
        
            logger.debug("[findSessionsByGroupId] Se encontraron {} sesiones de estudio para el grupo con ID: {}", sesiones.size(), groupId);
            return sesiones;
        } catch (NoResultException e) {
            logger.debug("[findSessionsByGroupId] No se encontraron sesiones de estudio para el grupo con ID: {}", groupId);
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            logger.error("[findSessionsByGroupId] Error al obtener las sesiones del grupo con ID: {}. Error: {}", groupId, e.getMessage(), e);
            throw new WebApplicationException("Error al obtener las sesiones del grupo.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene TODAS las sesiones de estudio de un usuario específico.
     * Corresponde a GET /rest.sesionestudio/usuario/{userId}
     *
     * @param userId ID del usuario.
     * @return Lista de Sesionestudio.
     */
    @GET
    @Path("usuario/{userId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Sesionestudio> getByUserId(@PathParam("userId") Integer userId) {
        logger.debug("[getByUserId] Buscando sesiones de estudio para el usuario con ID: {}", userId);
        try {
            List<Sesionestudio> sesiones = em.createQuery(
                "SELECT s FROM Sesionestudio s WHERE s.usuarioId.id = :userId ORDER BY s.fechaSesionPlanificada DESC", 
                Sesionestudio.class)
                .setParameter("userId", userId)
                .getResultList();
        
            logger.debug("[getByUserId] Se encontraron {} sesiones de estudio para el usuario con ID: {}", sesiones.size(), userId);
            return sesiones;
        } catch (NoResultException e) {
            logger.debug("[getByUserId] No se encontraron sesiones de estudio para el usuario con ID: {}", userId);
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            logger.error("[getByUserId] Error al obtener las sesiones del usuario con ID: {}. Error: {}", userId, e.getMessage(), e);
            throw new WebApplicationException("Error al obtener las sesiones del usuario.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene las sesiones de estudio PRÓXIMAS de un usuario específico. Se
     * consideran "próximas" las sesiones con fecha planificada en el futuro o
     * las que están en estado ACTIVA/PAUSADA y no tienen fecha de fin real.
     * Corresponde a GET /rest.sesionestudio/usuario/{userId}/proximas
     *
     * @param userId ID del usuario.
     * @return Lista de Sesionestudio.
     */
    @GET
    @Path("usuario/{userId}/proximas")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Sesionestudio> getUpcomingByUserId(@PathParam("userId") Integer userId) {
        logger.debug("[getUpcomingByUserId] Buscando sesiones próximas para el usuario con ID: {}", userId);
        try {
            // Se usa LocalDateTime para obtener la fecha y hora actual sin problemas de zona horaria
            LocalDateTime now = LocalDateTime.now();
            Date currentDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

            // Las sesiones próximas son aquellas que:
            // 1. Están PLANIFICADAS y su fecha_sesion_planificada es en el futuro.
            // 2. Están ACTIVA o PAUSADA y aún no tienen fecha_fin_real.
            List<Sesionestudio> sesiones = em.createQuery(
                "SELECT s FROM Sesionestudio s WHERE s.usuarioId.id = :userId AND "
                + "("
                + "   (s.estado = 'PLANNED' AND s.fechaSesionPlanificada > :currentDate) OR "
                + "   (s.estado IN ('ACTIVE', 'PAUSED') AND s.fechaFinReal IS NULL) "
                + ") "
                + "ORDER BY s.fechaSesionPlanificada ASC", 
                Sesionestudio.class)
                .setParameter("userId", userId)
                .setParameter("currentDate", currentDate)
                .getResultList();
        
            logger.debug("[getUpcomingByUserId] Se encontraron {} sesiones próximas para el usuario con ID: {}", sesiones.size(), userId);
            return sesiones;
        } catch (NoResultException e) {
            logger.debug("[getUpcomingByUserId] No se encontraron sesiones próximas para el usuario con ID: {}", userId);
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            logger.error("[getUpcomingByUserId] Error al obtener las sesiones próximas para el usuario con ID: {}. Error: {}", userId, e.getMessage(), e);
            throw new WebApplicationException("Error al obtener las sesiones próximas del usuario.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene las sesiones de estudio del HISTORIAL de un usuario específico.
     * Se consideran del "historial" las sesiones que tienen fecha de fin real
     * registrada o que están en estado "FINALIZED". Corresponde a GET
     * /rest.sesionestudio/usuario/{userId}/historial
     *
     * @param userId ID del usuario.
     * @return Lista de Sesionestudio.
     */
    @GET
    @Path("usuario/{userId}/historial")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Sesionestudio> getHistoryByUserId(@PathParam("userId") Integer userId) {
        logger.debug("[getHistoryByUserId] Buscando sesiones del historial para el usuario con ID: {}", userId);
        try {
            // Las sesiones del historial son aquellas que:
            // 1. Tienen una fecha_fin_real establecida (ya terminaron).
            // 2. O están en estado 'FINALIZED' (para mayor seguridad, aunque fecha_fin_real debería estar).
            List<Sesionestudio> sesiones = em.createQuery(
                "SELECT s FROM Sesionestudio s WHERE s.usuarioId.id = :userId AND "
                + "("
                + "   s.fechaFinReal IS NOT NULL OR "
                + "   s.estado = 'FINALIZED'"
                + ") "
                + "ORDER BY s.fechaSesionPlanificada DESC", 
                Sesionestudio.class)
                .setParameter("userId", userId)
                .getResultList();
        
            logger.debug("[getHistoryByUserId] Se encontraron {} sesiones del historial para el usuario con ID: {}", sesiones.size(), userId);
            return sesiones;
        } catch (NoResultException e) {
            logger.debug("[getHistoryByUserId] No se encontraron sesiones del historial para el usuario con ID: {}", userId);
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            logger.error("[getHistoryByUserId] Error al obtener las sesiones del historial para el usuario con ID: {}. Error: {}", userId, e.getMessage(), e);
            throw new WebApplicationException("Error al obtener las sesiones del historial del usuario.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

}
