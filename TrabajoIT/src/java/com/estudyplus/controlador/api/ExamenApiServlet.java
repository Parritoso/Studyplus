/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.api;

import com.estudyplus.modelo.entitys.Examen;
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
public class ExamenApiServlet extends HttpServlet {

     private final EstudyRestFacade restFacade = new EstudyRestFacade();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ExamenApiServlet.class); // Logger para el servlet
    
    // Método auxiliar para obtener el nombre de usuario del atributo de la petición
    private String getAuthenticatedUsername(HttpServletRequest request) {
        return (String) request.getAttribute("username"); // Asumimos que JwtAuthFilter lo setea
    }

    // GET /api/examenes           -> Obtener todos los exámenes
    // GET /api/examenes/{id}      -> Obtener un examen por ID
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                logger.info("Usuario '{}' intentando obtener todos los exámenes.", authenticatedUser);
                List<Examen> examenes = restFacade.getAllExamenes(); // Asume que este método existe en EstudyRestFacade
                out.print(objectMapper.writeValueAsString(examenes));
                AuditLogger.log("READ_ALL", "Examen", "N/A", authenticatedUser, "Obtención de todos los exámenes.");
            } else {
                String idStr = pathInfo.substring(1);
                if (!InputValidator.isValidIntegerId(idStr)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"message\": \"ID de examen inválido.\"}");
                    logger.warn("Usuario '{}' intentó obtener examen con ID inválido: '{}'", authenticatedUser, idStr);
                    return;
                }
                int examenId = Integer.parseInt(idStr);
                logger.info("Usuario '{}' intentando obtener examen con ID: {}", authenticatedUser, examenId);

                Examen examen = restFacade.getExamenById(examenId);
                if (examen != null) {
                    out.print(objectMapper.writeValueAsString(examen));
                    AuditLogger.log("READ", "Examen", String.valueOf(examenId), authenticatedUser, "Obtención de examen por ID.");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"message\": \"Examen con ID " + examenId + " no encontrado.\"}");
                    logger.warn("Usuario '{}' intentó obtener examen no encontrado con ID: {}", authenticatedUser, examenId);
                    AuditLogger.log("READ_FAILED", "Examen", String.valueOf(examenId), authenticatedUser, "Intento de obtener examen no encontrado.");
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de examen inválido.\"}");
            logger.error("Error de formato de ID en GET de Examen para usuario '{}': {}", authenticatedUser, e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al procesar la solicitud: " + e.getMessage() + "\"}");
            logger.error("Error interno en GET de Examen para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("ERROR", "Examen", "N/A", authenticatedUser, "Error interno en GET: " + e.getMessage());
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    // POST /api/examenes          -> Crear un nuevo examen
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        try {
            Examen nuevoExamen = objectMapper.readValue(request.getReader(), Examen.class);

            // Validación de entradas para creación
            if (!InputValidator.isValidAlphanumeric(nuevoExamen.getAsignatura(), 2, 100)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Asignatura de examen inválida. Debe ser alfanumérica y entre 2 y 100 caracteres.\"}");
                logger.warn("Usuario '{}' intentó crear examen con asignatura inválida: '{}'", authenticatedUser, nuevoExamen.getAsignatura());
                return;
            }
            // TODO: Añadir validación para fecha (Date)

            logger.info("Usuario '{}' intentando crear un nuevo examen para asignatura: {}", authenticatedUser, nuevoExamen.getAsignatura());
            Examen creado = restFacade.createExamen(nuevoExamen);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(objectMapper.writeValueAsString(creado));
            AuditLogger.log("CREATE", "Examen", String.valueOf(creado.getId()), authenticatedUser, "Nuevo examen creado para: " + creado.getAsignatura());

        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Error al leer el JSON de la solicitud o JSON inválido: " + e.getMessage() + "\"}");
            logger.error("Error de JSON en POST de Examen para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al crear el examen: " + e.getMessage() + "\"}");
            logger.error("Error interno en POST de Examen para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("CREATE_FAILED", "Examen", "N/A", authenticatedUser, "Error al crear examen: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // PUT /api/examenes/{id}      -> Actualizar un examen existente
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Se requiere el ID del examen para la actualización.\"}");
            logger.warn("Usuario '{}' intentó PUT sin ID de examen.", authenticatedUser);
            return;
        }

        try {
            String idStr = pathInfo.substring(1);
            if (!InputValidator.isValidIntegerId(idStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de examen inválido.\"}");
                logger.warn("Usuario '{}' intentó PUT con ID inválido: '{}'", authenticatedUser, idStr);
                return;
            }
            int examenId = Integer.parseInt(idStr);
            Examen examenActualizar = objectMapper.readValue(request.getReader(), Examen.class);
            examenActualizar.setId(examenId);

            // Validación de entradas para actualización
            if (examenActualizar.getAsignatura() != null && !InputValidator.isValidAlphanumeric(examenActualizar.getAsignatura(), 2, 100)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Asignatura de examen inválida en la actualización.\"}");
                logger.warn("Usuario '{}' intentó actualizar examen con asignatura inválida: '{}'", authenticatedUser, examenActualizar.getAsignatura());
                return;
            }

            logger.info("Usuario '{}' intentando actualizar examen con ID: {}", authenticatedUser, examenId);
            Examen actualizado = restFacade.updateExamen(examenActualizar);
            if (actualizado != null) {
                out.print(objectMapper.writeValueAsString(actualizado));
                AuditLogger.log("UPDATE", "Examen", String.valueOf(examenId), authenticatedUser, "Examen actualizado: " + examenActualizar.getAsignatura());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"message\": \"Examen con ID " + examenId + " no encontrado para actualizar.\"}");
                logger.warn("Usuario '{}' intentó actualizar examen no encontrado con ID: {}", authenticatedUser, examenId);
                AuditLogger.log("UPDATE_FAILED", "Examen", String.valueOf(examenId), authenticatedUser, "Intento de actualizar examen no encontrado.");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de examen inválido (formato incorrecto).\"}");
            logger.error("Error de formato de ID en PUT de Examen para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Error al leer el JSON o al procesar la solicitud: " + e.getMessage() + "\"}");
            logger.error("Error de JSON en PUT de Examen para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al actualizar el examen: " + e.getMessage() + "\"}");
            logger.error("Error interno en PUT de Examen para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("ERROR", "Examen", "N/A", authenticatedUser, "Error interno en PUT: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // DELETE /api/examenes/{id}   -> Eliminar un examen
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Se requiere el ID del examen para la eliminación.\"}");
            logger.warn("Usuario '{}' intentó DELETE sin ID de examen.", authenticatedUser);
            return;
        }

        try {
            String idStr = pathInfo.substring(1);
            if (!InputValidator.isValidIntegerId(idStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de examen inválido.\"}");
                logger.warn("Usuario '{}' intentó DELETE con ID inválido: '{}'", authenticatedUser, idStr);
                return;
            }
            int examenId = Integer.parseInt(idStr);

            logger.info("Usuario '{}' intentando eliminar examen con ID: {}", authenticatedUser, examenId);
            restFacade.deleteExamen(examenId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            AuditLogger.log("DELETE", "Examen", String.valueOf(examenId), authenticatedUser, "Examen eliminado.");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de examen inválido (formato incorrecto).\"}");
            logger.error("Error de formato de ID en DELETE de Examen para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al eliminar el examen: " + e.getMessage() + "\"}");
            logger.error("Error interno en DELETE de Examen para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("DELETE_FAILED", "Examen", "N/A", authenticatedUser, "Error al eliminar examen: " + e.getMessage());
        } finally {
            out.close();
        }
    }

}
