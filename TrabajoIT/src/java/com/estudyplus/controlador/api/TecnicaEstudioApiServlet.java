/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.api;

import com.estudyplus.modelo.entitys.TecnicaEstudio;
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
public class TecnicaEstudioApiServlet extends HttpServlet {

    private final EstudyRestFacade restFacade = new EstudyRestFacade();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(TecnicaEstudioApiServlet.class); // Logger para el servlet
    
    // Método auxiliar para obtener el nombre de usuario del atributo de la petición
    private String getAuthenticatedUsername(HttpServletRequest request) {
        return (String) request.getAttribute("username"); // Asumimos que JwtAuthFilter lo setea
    }

    // GET /api/tecnicas           -> Obtener todas las técnicas de estudio
    // GET /api/tecnicas/{id}      -> Obtener una técnica de estudio por ID
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                logger.info("Usuario '{}' intentando obtener todas las técnicas de estudio.", authenticatedUser);
                List<TecnicaEstudio> tecnicas = restFacade.getAllTecnicasEstudio();
                out.print(objectMapper.writeValueAsString(tecnicas));
                AuditLogger.log("READ_ALL", "TecnicaEstudio", "N/A", authenticatedUser, "Obtención de todas las técnicas de estudio.");
            } else {
                String idStr = pathInfo.substring(1);
                if (!InputValidator.isValidIntegerId(idStr)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"message\": \"ID de técnica de estudio inválido.\"}");
                    logger.warn("Usuario '{}' intentó obtener técnica con ID inválido: '{}'", authenticatedUser, idStr);
                    return;
                }
                int tecnicaId = Integer.parseInt(idStr);
                logger.info("Usuario '{}' intentando obtener técnica de estudio con ID: {}", authenticatedUser, tecnicaId);

                TecnicaEstudio tecnica = restFacade.getTecnicaEstudioById(tecnicaId);
                if (tecnica != null) {
                    out.print(objectMapper.writeValueAsString(tecnica));
                    AuditLogger.log("READ", "TecnicaEstudio", String.valueOf(tecnicaId), authenticatedUser, "Obtención de técnica de estudio por ID.");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"message\": \"Técnica de estudio con ID " + tecnicaId + " no encontrada.\"}");
                    logger.warn("Usuario '{}' intentó obtener técnica no encontrada con ID: {}", authenticatedUser, tecnicaId);
                    AuditLogger.log("READ_FAILED", "TecnicaEstudio", String.valueOf(tecnicaId), authenticatedUser, "Intento de obtener técnica no encontrada.");
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de técnica de estudio inválido (formato incorrecto).\"}");
            logger.error("Error de formato de ID en GET de TecnicaEstudio para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al procesar la solicitud: " + e.getMessage() + "\"}");
            logger.error("Error interno en GET de TecnicaEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("ERROR", "TecnicaEstudio", "N/A", authenticatedUser, "Error interno en GET: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // POST /api/tecnicas          -> Crear una nueva técnica de estudio
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        try {
            TecnicaEstudio nuevaTecnica = objectMapper.readValue(request.getReader(), TecnicaEstudio.class);

            // Validación de entradas para creación
            if (!InputValidator.isValidAlphanumeric(nuevaTecnica.getNombre(), 3, 100)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Nombre de técnica de estudio inválido. Debe ser alfanumérico y entre 3 y 100 caracteres.\"}");
                logger.warn("Usuario '{}' intentó crear técnica con nombre inválido: '{}'", authenticatedUser, nuevaTecnica.getNombre());
                return;
            }
            if (nuevaTecnica.getDescripcion() != null && nuevaTecnica.getDescripcion().length() > 500) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Descripción de técnica de estudio demasiado larga (máximo 500 caracteres).\"}");
                logger.warn("Usuario '{}' intentó crear técnica con descripción demasiado larga.", authenticatedUser);
                return;
            }

            logger.info("Usuario '{}' intentando crear una nueva técnica de estudio con nombre: {}", authenticatedUser, nuevaTecnica.getNombre());
            TecnicaEstudio creada = restFacade.createTecnicaEstudio(nuevaTecnica);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(objectMapper.writeValueAsString(creada));
            AuditLogger.log("CREATE", "TecnicaEstudio", String.valueOf(creada.getId()), authenticatedUser, "Nueva técnica de estudio creada: " + creada.getNombre());

        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Error al leer el JSON de la solicitud o JSON inválido: " + e.getMessage() + "\"}");
            logger.error("Error de JSON en POST de TecnicaEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al crear la técnica de estudio: " + e.getMessage() + "\"}");
            logger.error("Error interno en POST de TecnicaEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("CREATE_FAILED", "TecnicaEstudio", "N/A", authenticatedUser, "Error al crear técnica de estudio: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // PUT /api/tecnicas/{id}      -> Actualizar una técnica de estudio existente
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Se requiere el ID de la técnica de estudio para la actualización.\"}");
            logger.warn("Usuario '{}' intentó PUT sin ID de técnica de estudio.", authenticatedUser);
            return;
        }

        try {
            String idStr = pathInfo.substring(1);
            if (!InputValidator.isValidIntegerId(idStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de técnica de estudio inválido.\"}");
                logger.warn("Usuario '{}' intentó PUT con ID inválido: '{}'", authenticatedUser, idStr);
                return;
            }
            int tecnicaId = Integer.parseInt(idStr);
            TecnicaEstudio tecnicaActualizar = objectMapper.readValue(request.getReader(), TecnicaEstudio.class);
            tecnicaActualizar.setId(tecnicaId);

            // Validación de entradas para actualización
            if (tecnicaActualizar.getNombre() != null && !InputValidator.isValidAlphanumeric(tecnicaActualizar.getNombre(), 3, 100)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Nombre de técnica de estudio inválido en la actualización.\"}");
                logger.warn("Usuario '{}' intentó actualizar técnica con nombre inválido: '{}'", authenticatedUser, tecnicaActualizar.getNombre());
                return;
            }
            if (tecnicaActualizar.getDescripcion() != null && tecnicaActualizar.getDescripcion().length() > 500) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Descripción de técnica de estudio demasiado larga en la actualización (máximo 500 caracteres).\"}");
                logger.warn("Usuario '{}' intentó actualizar técnica con descripción demasiado larga.", authenticatedUser);
                return;
            }

            logger.info("Usuario '{}' intentando actualizar técnica de estudio con ID: {}", authenticatedUser, tecnicaId);
            TecnicaEstudio actualizado = restFacade.updateTecnicaEstudio(tecnicaActualizar);
            if (actualizado != null) {
                out.print(objectMapper.writeValueAsString(actualizado));
                AuditLogger.log("UPDATE", "TecnicaEstudio", String.valueOf(tecnicaId), authenticatedUser, "Técnica de estudio actualizada: " + tecnicaActualizar.getNombre());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"message\": \"Técnica de estudio con ID " + tecnicaId + " no encontrada para actualizar.\"}");
                logger.warn("Usuario '{}' intentó actualizar técnica no encontrada con ID: {}", authenticatedUser, tecnicaId);
                AuditLogger.log("UPDATE_FAILED", "TecnicaEstudio", String.valueOf(tecnicaId), authenticatedUser, "Intento de actualizar técnica no encontrada.");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de técnica de estudio inválido (formato incorrecto).\"}");
            logger.error("Error de formato de ID en PUT de TecnicaEstudio para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Error al leer el JSON o al procesar la solicitud: " + e.getMessage() + "\"}");
            logger.error("Error de JSON en PUT de TecnicaEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al actualizar la técnica de estudio: " + e.getMessage() + "\"}");
            logger.error("Error interno en PUT de TecnicaEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("ERROR", "TecnicaEstudio", "N/A", authenticatedUser, "Error interno en PUT: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // DELETE /api/tecnicas/{id}   -> Eliminar una técnica de estudio
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Se requiere el ID de la técnica de estudio para la eliminación.\"}");
            logger.warn("Usuario '{}' intentó DELETE sin ID de técnica de estudio.", authenticatedUser);
            return;
        }

        try {
            String idStr = pathInfo.substring(1);
            if (!InputValidator.isValidIntegerId(idStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de técnica de estudio inválido.\"}");
                logger.warn("Usuario '{}' intentó DELETE con ID inválido: '{}'", authenticatedUser, idStr);
                return;
            }
            int tecnicaId = Integer.parseInt(idStr);

            logger.info("Usuario '{}' intentando eliminar técnica de estudio con ID: {}", authenticatedUser, tecnicaId);
            restFacade.deleteTecnicaEstudio(tecnicaId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            AuditLogger.log("DELETE", "TecnicaEstudio", String.valueOf(tecnicaId), authenticatedUser, "Técnica de estudio eliminada.");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de técnica de estudio inválido (formato incorrecto).\"}");
            logger.error("Error de formato de ID en DELETE de TecnicaEstudio para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al eliminar la técnica de estudio: " + e.getMessage() + "\"}");
            logger.error("Error interno en DELETE de TecnicaEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("DELETE_FAILED", "TecnicaEstudio", "N/A", authenticatedUser, "Error al eliminar técnica de estudio: " + e.getMessage());
        } finally {
            out.close();
        }
    }

}
