/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.api;

import com.estudyplus.modelo.entitys.LogProductividad;
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
public class LogProductividadApiServlet extends HttpServlet {

    private final EstudyRestFacade restFacade = new EstudyRestFacade();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(LogProductividadApiServlet.class); // Logger para el servlet
    
    // Método auxiliar para obtener el nombre de usuario del atributo de la petición
    private String getAuthenticatedUsername(HttpServletRequest request) {
        return (String) request.getAttribute("username"); // Asumimos que JwtAuthFilter lo setea
    }

    // GET /api/logs-productividad           -> Obtener todos los logs de productividad
    // GET /api/logs-productividad/{id}      -> Obtener un log de productividad por ID
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                logger.info("Usuario '{}' intentando obtener todos los logs de productividad.", authenticatedUser);
                List<LogProductividad> logs = restFacade.getAllLogsProductividad();
                out.print(objectMapper.writeValueAsString(logs));
                AuditLogger.log("READ_ALL", "LogProductividad", "N/A", authenticatedUser, "Obtención de todos los logs de productividad.");
            } else {
                String idStr = pathInfo.substring(1);
                if (!InputValidator.isValidIntegerId(idStr)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"message\": \"ID de log de productividad inválido.\"}");
                    logger.warn("Usuario '{}' intentó obtener log con ID inválido: '{}'", authenticatedUser, idStr);
                    return;
                }
                int logId = Integer.parseInt(idStr);
                logger.info("Usuario '{}' intentando obtener log de productividad con ID: {}", authenticatedUser, logId);

                LogProductividad log = restFacade.getLogProductividadById(logId);
                if (log != null) {
                    out.print(objectMapper.writeValueAsString(log));
                    AuditLogger.log("READ", "LogProductividad", String.valueOf(logId), authenticatedUser, "Obtención de log de productividad por ID.");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"message\": \"Log de productividad con ID " + logId + " no encontrado.\"}");
                    logger.warn("Usuario '{}' intentó obtener log no encontrado con ID: {}", authenticatedUser, logId);
                    AuditLogger.log("READ_FAILED", "LogProductividad", String.valueOf(logId), authenticatedUser, "Intento de obtener log no encontrado.");
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de log de productividad inválido (formato incorrecto).\"}");
            logger.error("Error de formato de ID en GET de LogProductividad para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al procesar la solicitud: " + e.getMessage() + "\"}");
            logger.error("Error interno en GET de LogProductividad para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("ERROR", "LogProductividad", "N/A", authenticatedUser, "Error interno en GET: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // POST /api/logs-productividad          -> Crear un nuevo log de productividad
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        try {
            LogProductividad nuevoLog = objectMapper.readValue(request.getReader(), LogProductividad.class);

            // Validación de entradas para creación
            if (nuevoLog.getDuracionEstudioTotalMinutos() == null || nuevoLog.getDuracionDescansoTotalMinutos() < 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Minutos enfocados inválidos. Debe ser un número no negativo.\"}");
                logger.warn("Usuario '{}' intentó crear log con minutos enfocados inválidos: {}", authenticatedUser, nuevoLog.getDuracionDescansoTotalMinutos());
                return;
            }
            if (nuevoLog.getDuracionEstudioTotalMinutos() == null || nuevoLog.getDuracionEstudioTotalMinutos() < 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Minutos de descanso inválidos. Debe ser un número no negativo.\"}");
                logger.warn("Usuario '{}' intentó crear log con minutos de descanso inválidos: {}", authenticatedUser, nuevoLog.getDuracionEstudioTotalMinutos());
                return;
            }
            // TODO: Validar fecha, ID de usuario, etc.

            logger.info("Usuario '{}' intentando crear un nuevo log de productividad.", authenticatedUser);
            LogProductividad creado = restFacade.createLogProductividad(nuevoLog);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(objectMapper.writeValueAsString(creado));
            AuditLogger.log("CREATE", "LogProductividad", String.valueOf(creado.getId()), authenticatedUser, "Nuevo log de productividad creado.");

        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Error al leer el JSON de la solicitud o JSON inválido: " + e.getMessage() + "\"}");
            logger.error("Error de JSON en POST de LogProductividad para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al crear el log de productividad: " + e.getMessage() + "\"}");
            logger.error("Error interno en POST de LogProductividad para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("CREATE_FAILED", "LogProductividad", "N/A", authenticatedUser, "Error al crear log de productividad: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // PUT /api/logs-productividad/{id}      -> Actualizar un log de productividad existente
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Se requiere el ID del log de productividad para la actualización.\"}");
            logger.warn("Usuario '{}' intentó PUT sin ID de log de productividad.", authenticatedUser);
            return;
        }

        try {
            String idStr = pathInfo.substring(1);
            if (!InputValidator.isValidIntegerId(idStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de log de productividad inválido.\"}");
                logger.warn("Usuario '{}' intentó PUT con ID inválido: '{}'", authenticatedUser, idStr);
                return;
            }
            int logId = Integer.parseInt(idStr);
            LogProductividad logActualizar = objectMapper.readValue(request.getReader(), LogProductividad.class);
            logActualizar.setId(logId);

            // Validación de entradas para actualización
            if (logActualizar.getDuracionEstudioTotalMinutos() != null && logActualizar.getDuracionEstudioTotalMinutos() < 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Minutos enfocados inválidos en la actualización.\"}");
                logger.warn("Usuario '{}' intentó actualizar log con minutos enfocados inválidos: {}", authenticatedUser, logActualizar.getDuracionEstudioTotalMinutos());
                return;
            }
            if (logActualizar.getDuracionEstudioTotalMinutos() != null && logActualizar.getDuracionEstudioTotalMinutos() < 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Minutos de descanso inválidos en la actualización.\"}");
                logger.warn("Usuario '{}' intentó actualizar log con minutos de descanso inválidos: {}", authenticatedUser, logActualizar.getDuracionEstudioTotalMinutos());
                return;
            }

            logger.info("Usuario '{}' intentando actualizar log de productividad con ID: {}", authenticatedUser, logId);
            LogProductividad actualizado = restFacade.updateLogProductividad(logActualizar);
            if (actualizado != null) {
                out.print(objectMapper.writeValueAsString(actualizado));
                AuditLogger.log("UPDATE", "LogProductividad", String.valueOf(logId), authenticatedUser, "Log de productividad actualizado.");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"message\": \"Log de productividad con ID " + logId + " no encontrado para actualizar.\"}");
                logger.warn("Usuario '{}' intentó actualizar log no encontrado con ID: {}", authenticatedUser, logId);
                AuditLogger.log("UPDATE_FAILED", "LogProductividad", String.valueOf(logId), authenticatedUser, "Intento de actualizar log no encontrado.");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de log de productividad inválido (formato incorrecto).\"}");
            logger.error("Error de formato de ID en PUT de LogProductividad para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Error al leer el JSON o al procesar la solicitud: " + e.getMessage() + "\"}");
            logger.error("Error de JSON en PUT de LogProductividad para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al actualizar el log de productividad: " + e.getMessage() + "\"}");
            logger.error("Error interno en PUT de LogProductividad para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("ERROR", "LogProductividad", "N/A", authenticatedUser, "Error interno en PUT: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // DELETE /api/logs-productividad/{id}   -> Eliminar un log de productividad
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Se requiere el ID del log de productividad para la eliminación.\"}");
            logger.warn("Usuario '{}' intentó DELETE sin ID de log de productividad.", authenticatedUser);
            return;
        }

        try {
            String idStr = pathInfo.substring(1);
            if (!InputValidator.isValidIntegerId(idStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de log de productividad inválido.\"}");
                logger.warn("Usuario '{}' intentó DELETE con ID inválido: '{}'", authenticatedUser, idStr);
                return;
            }
            int logId = Integer.parseInt(idStr);

            logger.info("Usuario '{}' intentando eliminar log de productividad con ID: {}", authenticatedUser, logId);
            restFacade.deleteLogProductividad(logId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            AuditLogger.log("DELETE", "LogProductividad", String.valueOf(logId), authenticatedUser, "Log de productividad eliminado.");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de log de productividad inválido (formato incorrecto).\"}");
            logger.error("Error de formato de ID en DELETE de LogProductividad para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al eliminar el log de productividad: " + e.getMessage() + "\"}");
            logger.error("Error interno en DELETE de LogProductividad para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("DELETE_FAILED", "LogProductividad", "N/A", authenticatedUser, "Error al eliminar log de productividad: " + e.getMessage());
        } finally {
            out.close();
        }
    }

}
