/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.api;

import com.estudyplus.modelo.entitys.ParticipanteGrupo;
import com.estudyplus.modelo.rest.facade.EstudyRestFacade;
import com.estudyplus.utils.AuditLogger;
import com.estudyplus.utils.InputValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Parri
 */
public class ParticipanteGrupoApiServlet extends HttpServlet {

    private final EstudyRestFacade restFacade = new EstudyRestFacade();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ParticipanteGrupoApiServlet.class); // Logger para el servlet
    
    // Método auxiliar para obtener el nombre de usuario del atributo de la petición
    private String getAuthenticatedUsername(HttpServletRequest request) {
        return (String) request.getAttribute("username"); // Asumimos que JwtAuthFilter lo setea
    }

    // GET /api/participantes-grupo           -> Obtener todos los participantes de grupo
    // GET /api/participantes-grupo/{id}      -> Obtener un participante de grupo por ID
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                logger.info("Usuario '{}' intentando obtener todos los participantes de grupo.", authenticatedUser);
                List<ParticipanteGrupo> participantes = restFacade.getAllParticipantesGrupo();
                out.print(objectMapper.writeValueAsString(participantes));
                AuditLogger.log("READ_ALL", "ParticipanteGrupo", "N/A", authenticatedUser, "Obtención de todos los participantes de grupo.");
            } else {
                String idStr = pathInfo.substring(1);
                // Asumiendo que el ID aquí es el ID del ParticipanteGrupo (la clave primaria).
                // Si la PK es compuesta (usuarioId, grupoId), necesitarías un enfoque diferente para GET by ID.
                if (!InputValidator.isValidIntegerId(idStr)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"message\": \"ID de participante de grupo inválido.\"}");
                    logger.warn("Usuario '{}' intentó obtener participante con ID inválido: '{}'", authenticatedUser, idStr);
                    return;
                }
                int participanteId = Integer.parseInt(idStr);
                logger.info("Usuario '{}' intentando obtener participante de grupo con ID: {}", authenticatedUser, participanteId);

                ParticipanteGrupo participante = restFacade.getParticipanteGrupoById(participanteId);
                if (participante != null) {
                    out.print(objectMapper.writeValueAsString(participante));
                    AuditLogger.log("READ", "ParticipanteGrupo", String.valueOf(participanteId), authenticatedUser, "Obtención de participante de grupo por ID.");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"message\": \"Participante de grupo con ID " + participanteId + " no encontrado.\"}");
                    logger.warn("Usuario '{}' intentó obtener participante no encontrado con ID: {}", authenticatedUser, participanteId);
                    AuditLogger.log("READ_FAILED", "ParticipanteGrupo", String.valueOf(participanteId), authenticatedUser, "Intento de obtener participante no encontrado.");
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de participante de grupo inválido (formato incorrecto).\"}");
            logger.error("Error de formato de ID en GET de ParticipanteGrupo para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al procesar la solicitud: " + e.getMessage() + "\"}");
            logger.error("Error interno en GET de ParticipanteGrupo para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("ERROR", "ParticipanteGrupo", "N/A", authenticatedUser, "Error interno en GET: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // POST /api/participantes-grupo          -> Crear un nuevo participante de grupo
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        try {
            ParticipanteGrupo nuevoParticipante = objectMapper.readValue(request.getReader(), ParticipanteGrupo.class);

            // Validación de entradas para creación
            if (nuevoParticipante.getUsuarioId().getId() == null || nuevoParticipante.getUsuarioId().getId() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de usuario de participante inválido.\"}");
                logger.warn("Usuario '{}' intentó crear participante con usuario ID inválido: {}", authenticatedUser, nuevoParticipante.getUsuarioId());
                return;
            }
            if (nuevoParticipante.getGrupoId().getId() == null || nuevoParticipante.getGrupoId().getId() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de grupo de participante inválido.\"}");
                logger.warn("Usuario '{}' intentó crear participante con grupo ID inválido: {}", authenticatedUser, nuevoParticipante.getGrupoId());
                return;
            }
            // Puedes añadir validación para el rol, si tienes un campo de rol.

            logger.info("Usuario '{}' intentando crear un nuevo participante de grupo para usuario ID: {} y grupo ID: {}", authenticatedUser, nuevoParticipante.getUsuarioId(), nuevoParticipante.getGrupoId());
            ParticipanteGrupo creado = restFacade.createParticipanteGrupo(nuevoParticipante);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(objectMapper.writeValueAsString(creado));
            AuditLogger.log("CREATE", "ParticipanteGrupo", String.valueOf(creado.getId()), authenticatedUser, "Nuevo participante de grupo creado.");

        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Error al leer el JSON de la solicitud o JSON inválido: " + e.getMessage() + "\"}");
            logger.error("Error de JSON en POST de ParticipanteGrupo para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al crear el participante de grupo: " + e.getMessage() + "\"}");
            logger.error("Error interno en POST de ParticipanteGrupo para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("CREATE_FAILED", "ParticipanteGrupo", "N/A", authenticatedUser, "Error al crear participante de grupo: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // PUT /api/participantes-grupo/{id}      -> Actualizar un participante de grupo existente
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Se requiere el ID del participante de grupo para la actualización.\"}");
            logger.warn("Usuario '{}' intentó PUT sin ID de participante de grupo.", authenticatedUser);
            return;

        }

        try {
            String idStr = pathInfo.substring(1);
            if (!InputValidator.isValidIntegerId(idStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de participante de grupo inválido.\"}");
                logger.warn("Usuario '{}' intentó PUT con ID inválido: '{}'", authenticatedUser, idStr);
                return;
            }
            int participanteId = Integer.parseInt(idStr);
            ParticipanteGrupo participanteActualizar = objectMapper.readValue(request.getReader(), ParticipanteGrupo.class);
            participanteActualizar.setId(participanteId);

            // Validación de entradas para actualización
            if (participanteActualizar.getUsuarioId().getId() != null && participanteActualizar.getUsuarioId().getId() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de usuario de participante inválido en la actualización.\"}");
                logger.warn("Usuario '{}' intentó actualizar participante con usuario ID inválido: {}", authenticatedUser, participanteActualizar.getUsuarioId());
                return;
            }
            if (participanteActualizar.getGrupoId().getId() != null && participanteActualizar.getGrupoId().getId() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de grupo de participante inválido en la actualización.\"}");
                logger.warn("Usuario '{}' intentó actualizar participante con grupo ID inválido: {}", authenticatedUser, participanteActualizar.getGrupoId());
                return;
            }

            logger.info("Usuario '{}' intentando actualizar participante de grupo con ID: {}", authenticatedUser, participanteId);
            ParticipanteGrupo actualizado = restFacade.updateParticipanteGrupo(participanteActualizar);
            if (actualizado != null) {
                out.print(objectMapper.writeValueAsString(actualizado));
                AuditLogger.log("UPDATE", "ParticipanteGrupo", String.valueOf(participanteId), authenticatedUser, "Participante de grupo actualizado.");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"message\": \"Participante de grupo con ID " + participanteId + " no encontrado para actualizar.\"}");
                logger.warn("Usuario '{}' intentó actualizar participante no encontrado con ID: {}", authenticatedUser, participanteId);
                AuditLogger.log("UPDATE_FAILED", "ParticipanteGrupo", String.valueOf(participanteId), authenticatedUser, "Intento de actualizar participante no encontrado.");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de participante de grupo inválido (formato incorrecto).\"}");
            logger.error("Error de formato de ID en PUT de ParticipanteGrupo para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Error al leer el JSON o al procesar la solicitud: " + e.getMessage() + "\"}");
            logger.error("Error de JSON en PUT de ParticipanteGrupo para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al actualizar el participante de grupo: " + e.getMessage() + "\"}");
            logger.error("Error interno en PUT de ParticipanteGrupo para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("ERROR", "ParticipanteGrupo", "N/A", authenticatedUser, "Error interno en PUT: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // DELETE /api/participantes-grupo/{id}   -> Eliminar un participante de grupo
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Se requiere el ID del participante de grupo para la eliminación.\"}");
            logger.warn("Usuario '{}' intentó DELETE sin ID de participante de grupo.", authenticatedUser);
            return;
        }

        try {
            String idStr = pathInfo.substring(1);
            if (!InputValidator.isValidIntegerId(idStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de participante de grupo inválido.\"}");
                logger.warn("Usuario '{}' intentó DELETE con ID inválido: '{}'", authenticatedUser, idStr);
                return;
            }
            int participanteId = Integer.parseInt(idStr);

            logger.info("Usuario '{}' intentando eliminar participante de grupo con ID: {}", authenticatedUser, participanteId);
            restFacade.deleteParticipanteGrupo(participanteId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            AuditLogger.log("DELETE", "ParticipanteGrupo", String.valueOf(participanteId), authenticatedUser, "Participante de grupo eliminado.");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de participante de grupo inválido (formato incorrecto).\"}");
            logger.error("Error de formato de ID en DELETE de ParticipanteGrupo para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al eliminar el participante de grupo: " + e.getMessage() + "\"}");
            logger.error("Error interno en DELETE de ParticipanteGrupo para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("DELETE_FAILED", "ParticipanteGrupo", "N/A", authenticatedUser, "Error al eliminar participante de grupo: " + e.getMessage());
        } finally {
            out.close();
        }
    }

}
