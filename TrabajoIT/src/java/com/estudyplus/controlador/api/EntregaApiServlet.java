/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.api;

import com.estudyplus.modelo.entitys.Entrega;
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
public class EntregaApiServlet extends HttpServlet {

    private final EstudyRestFacade restFacade = new EstudyRestFacade();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(EntregaApiServlet.class); // Logger para el servlet
    
    // Método auxiliar para obtener el nombre de usuario del atributo de la petición
    private String getAuthenticatedUsername(HttpServletRequest request) {
        return (String) request.getAttribute("username"); // Asumimos que JwtAuthFilter lo setea
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                logger.info("Usuario '{}' intentando obtener todas las entregas.", authenticatedUser);
                List<Entrega> entregas = restFacade.getAllEntregas();
                out.print(objectMapper.writeValueAsString(entregas));
                AuditLogger.log("READ_ALL", "Entrega", "N/A", authenticatedUser, "Obtención de todas las entrgas.");
            } else {
                String userIdStr = pathInfo.substring(1); // Quita el '/' inicial
                if (!InputValidator.isValidIntegerId(userIdStr)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"message\": \"ID de usuario inválido.\"}");
                    logger.warn("Usuario '{}' intentó obtener usuario con ID inválido: '{}'", authenticatedUser, userIdStr);
                    return;
                }
                int entregaId = Integer.parseInt(userIdStr);
                logger.info("Usuario '{}' intentando obtener entrega con ID: {}", authenticatedUser, entregaId);
                Entrega entrega = restFacade.getEntregaById(entregaId);
                if (entrega != null) {
                    out.print(objectMapper.writeValueAsString(entrega));
                    AuditLogger.log("READ", "Entrega", String.valueOf(entregaId), authenticatedUser, "Obtención de entrega por ID.");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"message\": \"Entrega no encontrada.\"}");
                    logger.warn("Usuario '{}' intentó obtener entrega no encontrado con ID: {}", authenticatedUser, entregaId);
                    AuditLogger.log("READ_FAILED", "Entrega", String.valueOf(entregaId), authenticatedUser, "Intento de obtener entrega no encontrado.");
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de entrega inválido.\"}");
            logger.error("Error de formato de ID en GET de Entrega para entrega '{}': {}", authenticatedUser, e.getMessage());
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al procesar la solicitud: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al procesar la solicitud: " + e.getMessage() + "\"}");
            logger.error("Error interno en GET de Entrega para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("ERROR", "Entrega", "N/A", authenticatedUser, "Error interno en GET: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        try {
            Entrega nuevaEntrega = objectMapper.readValue(request.getReader(), Entrega.class);
            // Validación de entradas para creación
            if (!InputValidator.isValidAlphanumeric(nuevaEntrega.getTitulo(), 3, 255)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Título de entrega inválido. Debe ser alfanumérico y entre 3 y 255 caracteres.\"}");
                logger.warn("Usuario '{}' intentó crear entrega con título inválido: '{}'", authenticatedUser, nuevaEntrega.getTitulo());
                return;
            }
            
            logger.info("Usuario '{}' intentando crear una nueva entrega con título: {}", authenticatedUser, nuevaEntrega.getTitulo());
            Entrega creada = restFacade.createEntrega(nuevaEntrega);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(objectMapper.writeValueAsString(creada));
            AuditLogger.log("CREATE", "Entrega", String.valueOf(creada.getId()), authenticatedUser, "Nueva entrega creada: " + creada.getTitulo());
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Error al leer el JSON de la solicitud: " + e.getMessage() + "\"}");
            logger.error("Error de JSON en POST de Entrega para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al crear la entrega: " + e.getMessage() + "\"}");
            logger.error("Error interno en POST de Entrega para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("CREATE_FAILED", "Entrega", "N/A", authenticatedUser, "Error al crear entrega: " + e.getMessage());
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Se requiere ID de entrega para la actualización.\"}");
            logger.warn("Usuario '{}' intentó PUT sin ID de entrega.", authenticatedUser);
            return;
        }

        try {
            String idStr = pathInfo.substring(1);
            if (!InputValidator.isValidIntegerId(idStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de entrega inválido.\"}");
                logger.warn("Usuario '{}' intentó PUT con ID inválido: '{}'", authenticatedUser, idStr);
                return;
            }
            int entregaId = Integer.parseInt(idStr);
            Entrega entregaActualizar = objectMapper.readValue(request.getReader(), Entrega.class);
            entregaActualizar.setId(entregaId);

            // Validación de entradas para actualización
            if (entregaActualizar.getTitulo() != null && !InputValidator.isValidAlphanumeric(entregaActualizar.getTitulo(), 3, 255)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Título de entrega inválido en la actualización.\"}");
                logger.warn("Usuario '{}' intentó actualizar entrega con título inválido: '{}'", authenticatedUser, entregaActualizar.getTitulo());
                return;
            }
            
            logger.info("Usuario '{}' intentando actualizar entrega con ID: {}", authenticatedUser, entregaId);
            Entrega actualizada = restFacade.updateEntrega(entregaActualizar);
            if (actualizada != null) {
                out.print(objectMapper.writeValueAsString(actualizada));
                AuditLogger.log("UPDATE", "Entrega", String.valueOf(entregaId), authenticatedUser, "Entrega actualizada: " + entregaActualizar.getTitulo());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"message\": \"Entrega no encontrada para actualizar.\"}");
                logger.warn("Usuario '{}' intentó actualizar entrega no encontrada con ID: {}", authenticatedUser, entregaId);
                AuditLogger.log("UPDATE_FAILED", "Entrega", String.valueOf(entregaId), authenticatedUser, "Intento de actualizar entrega no encontrada.");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de entrega inválido.\"}");
            logger.error("Error de formato de ID en PUT de Entrega para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Error al leer el JSON o al procesar la solicitud: " + e.getMessage() + "\"}");
            logger.error("Error de JSON en PUT de Entrega para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al actualizar la entrega: " + e.getMessage() + "\"}");
            logger.error("Error interno en PUT de Entrega para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("ERROR", "Entrega", "N/A", authenticatedUser, "Error interno en PUT: " + e.getMessage());
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Se requiere ID de entrega para la eliminación.\"}");
            logger.warn("Usuario '{}' intentó DELETE sin ID de entrega.", authenticatedUser);
            return;
        }

        try {
            String idStr = pathInfo.substring(1);
            if (!InputValidator.isValidIntegerId(idStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de entrega inválido.\"}");
                logger.warn("Usuario '{}' intentó DELETE con ID inválido: '{}'", authenticatedUser, idStr);
                return;
            }
            int entregaId = Integer.parseInt(idStr);
            logger.info("Usuario '{}' intentando eliminar entrega con ID: {}", authenticatedUser, entregaId);
            restFacade.deleteEntrega(entregaId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content
            AuditLogger.log("DELETE", "Entrega", String.valueOf(entregaId), authenticatedUser, "Entrega eliminada.");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de entrega inválido.\"}");
            logger.error("Error de formato de ID en DELETE de Entrega para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al eliminar la entrega: " + e.getMessage() + "\"}");
            logger.error("Error interno en DELETE de Entrega para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("DELETE_FAILED", "Entrega", "N/A", authenticatedUser, "Error al eliminar entrega: " + e.getMessage());
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

}
