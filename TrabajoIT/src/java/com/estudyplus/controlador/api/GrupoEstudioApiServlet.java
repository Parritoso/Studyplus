/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.api;

import com.estudyplus.modelo.entitys.GrupoEstudio;
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
public class GrupoEstudioApiServlet extends HttpServlet {

    private final EstudyRestFacade restFacade = new EstudyRestFacade();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(GrupoEstudioApiServlet.class); // Logger para el servlet
    
    // Método auxiliar para obtener el nombre de usuario del atributo de la petición
    private String getAuthenticatedUsername(HttpServletRequest request) {
        return (String) request.getAttribute("username"); // Asumimos que JwtAuthFilter lo setea
    }

    // GET /api/grupos-estudio           -> Obtener todos los grupos de estudio
    // GET /api/grupos-estudio/{id}      -> Obtener un grupo de estudio por ID
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                logger.info("Usuario '{}' intentando obtener todos los grupos de estudio.", authenticatedUser);
                List<GrupoEstudio> grupos = restFacade.getAllGruposEstudio();
                out.print(objectMapper.writeValueAsString(grupos));
                AuditLogger.log("READ_ALL", "GrupoEstudio", "N/A", authenticatedUser, "Obtención de todos los grupos de estudio.");
            } else {
                String idStr = pathInfo.substring(1);
                if (!InputValidator.isValidIntegerId(idStr)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"message\": \"ID de grupo de estudio inválido.\"}");
                    logger.warn("Usuario '{}' intentó obtener grupo con ID inválido: '{}'", authenticatedUser, idStr);
                    return;
                }
                int grupoId = Integer.parseInt(idStr);
                logger.info("Usuario '{}' intentando obtener grupo de estudio con ID: {}", authenticatedUser, grupoId);

                GrupoEstudio grupo = restFacade.getGrupoEstudioById(grupoId);
                if (grupo != null) {
                    out.print(objectMapper.writeValueAsString(grupo));
                    AuditLogger.log("READ", "GrupoEstudio", String.valueOf(grupoId), authenticatedUser, "Obtención de grupo de estudio por ID.");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"message\": \"Grupo de estudio con ID " + grupoId + " no encontrado.\"}");
                    logger.warn("Usuario '{}' intentó obtener grupo no encontrado con ID: {}", authenticatedUser, grupoId);
                    AuditLogger.log("READ_FAILED", "GrupoEstudio", String.valueOf(grupoId), authenticatedUser, "Intento de obtener grupo no encontrado.");
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de grupo de estudio inválido (formato incorrecto).\"}");
            logger.error("Error de formato de ID en GET de GrupoEstudio para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al procesar la solicitud: " + e.getMessage() + "\"}");
            logger.error("Error interno en GET de GrupoEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("ERROR", "GrupoEstudio", "N/A", authenticatedUser, "Error interno en GET: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // POST /api/grupos-estudio          -> Crear un nuevo grupo de estudio
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        try {
            GrupoEstudio nuevoGrupo = objectMapper.readValue(request.getReader(), GrupoEstudio.class);

            // Validación de entradas para creación
            if (!InputValidator.isValidAlphanumeric(nuevoGrupo.getNombre(), 3, 100)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Nombre de grupo inválido. Debe ser alfanumérico y entre 3 y 100 caracteres.\"}");
                logger.warn("Usuario '{}' intentó crear grupo con nombre inválido: '{}'", authenticatedUser, nuevoGrupo.getNombre());
                return;
            }

            logger.info("Usuario '{}' intentando crear un nuevo grupo de estudio con nombre: {}", authenticatedUser, nuevoGrupo.getNombre());
            GrupoEstudio creado = restFacade.createGrupoEstudio(nuevoGrupo);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(objectMapper.writeValueAsString(creado));
            AuditLogger.log("CREATE", "GrupoEstudio", String.valueOf(creado.getId()), authenticatedUser, "Nuevo grupo de estudio creado: " + creado.getNombre());

        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Error al leer el JSON de la solicitud o JSON inválido: " + e.getMessage() + "\"}");
            logger.error("Error de JSON en POST de GrupoEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al crear el grupo de estudio: " + e.getMessage() + "\"}");
            logger.error("Error interno en POST de GrupoEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("CREATE_FAILED", "GrupoEstudio", "N/A", authenticatedUser, "Error al crear grupo de estudio: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // PUT /api/grupos-estudio/{id}      -> Actualizar un grupo de estudio existente
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Se requiere el ID del grupo de estudio para la actualización.\"}");
            logger.warn("Usuario '{}' intentó PUT sin ID de grupo de estudio.", authenticatedUser);
            return;
        }

        try {
            String idStr = pathInfo.substring(1);
            if (!InputValidator.isValidIntegerId(idStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de grupo de estudio inválido.\"}");
                logger.warn("Usuario '{}' intentó PUT con ID inválido: '{}'", authenticatedUser, idStr);
                return;
            }
            int grupoId = Integer.parseInt(idStr);
            GrupoEstudio grupoActualizar = objectMapper.readValue(request.getReader(), GrupoEstudio.class);
            grupoActualizar.setId(grupoId);

            // Validación de entradas para actualización
            if (grupoActualizar.getNombre() != null && !InputValidator.isValidAlphanumeric(grupoActualizar.getNombre(), 3, 100)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Nombre de grupo inválido en la actualización.\"}");
                logger.warn("Usuario '{}' intentó actualizar grupo con nombre inválido: '{}'", authenticatedUser, grupoActualizar.getNombre());
                return;
            }

            logger.info("Usuario '{}' intentando actualizar grupo de estudio con ID: {}", authenticatedUser, grupoId);
            GrupoEstudio actualizado = restFacade.updateGrupoEstudio(grupoActualizar);
            if (actualizado != null) {
                out.print(objectMapper.writeValueAsString(actualizado));
                AuditLogger.log("UPDATE", "GrupoEstudio", String.valueOf(grupoId), authenticatedUser, "Grupo de estudio actualizado: " + grupoActualizar.getNombre());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"message\": \"Grupo de estudio con ID " + grupoId + " no encontrado para actualizar.\"}");
                logger.warn("Usuario '{}' intentó actualizar grupo no encontrado con ID: {}", authenticatedUser, grupoId);
                AuditLogger.log("UPDATE_FAILED", "GrupoEstudio", String.valueOf(grupoId), authenticatedUser, "Intento de actualizar grupo no encontrado.");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de grupo de estudio inválido (formato incorrecto).\"}");
            logger.error("Error de formato de ID en PUT de GrupoEstudio para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Error al leer el JSON o al procesar la solicitud: " + e.getMessage() + "\"}");
            logger.error("Error de JSON en PUT de GrupoEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al actualizar el grupo de estudio: " + e.getMessage() + "\"}");
            logger.error("Error interno en PUT de GrupoEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("ERROR", "GrupoEstudio", "N/A", authenticatedUser, "Error interno en PUT: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // DELETE /api/grupos-estudio/{id}   -> Eliminar un grupo de estudio
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Se requiere el ID del grupo de estudio para la eliminación.\"}");
            logger.warn("Usuario '{}' intentó DELETE sin ID de grupo de estudio.", authenticatedUser);
            return;
        }

        try {
            String idStr = pathInfo.substring(1);
            if (!InputValidator.isValidIntegerId(idStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de grupo de estudio inválido.\"}");
                logger.warn("Usuario '{}' intentó DELETE con ID inválido: '{}'", authenticatedUser, idStr);
                return;
            }
            int grupoId = Integer.parseInt(idStr);

            logger.info("Usuario '{}' intentando eliminar grupo de estudio con ID: {}", authenticatedUser, grupoId);
            restFacade.deleteGrupoEstudio(grupoId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            AuditLogger.log("DELETE", "GrupoEstudio", String.valueOf(grupoId), authenticatedUser, "Grupo de estudio eliminado.");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de grupo de estudio inválido (formato incorrecto).\"}");
            logger.error("Error de formato de ID en DELETE de GrupoEstudio para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al eliminar el grupo de estudio: " + e.getMessage() + "\"}");
            logger.error("Error interno en DELETE de GrupoEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("DELETE_FAILED", "GrupoEstudio", "N/A", authenticatedUser, "Error al eliminar grupo de estudio: " + e.getMessage());
        } finally {
            out.close();
        }
    }

}
