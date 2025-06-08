/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.api;

import com.estudyplus.modelo.entitys.SesionEstudio;
import com.estudyplus.modelo.rest.facade.EstudyRestFacade;
import com.estudyplus.utils.AuditLogger;
import com.estudyplus.utils.InputValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
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
public class SesionEstudioApiServlet extends HttpServlet {

    private final EstudyRestFacade restFacade = new EstudyRestFacade();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(SesionEstudioApiServlet.class); // Logger para el servlet
    
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
                logger.info("Usuario '{}' intentando obtener todas las sesiones de estudio.", authenticatedUser);
                List<SesionEstudio> sesiones = restFacade.getAllSesionesEstudio();
                out.print(objectMapper.writeValueAsString(sesiones));
                AuditLogger.log("READ_ALL", "SesionEstudio", "N/A", authenticatedUser, "Obtención de todas las sesiones de estudio.");
            } else {
                String idStr = pathInfo.substring(1);
                if (!InputValidator.isValidIntegerId(idStr)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"message\": \"ID de sesión de estudio inválido.\"}");
                    logger.warn("Usuario '{}' intentó obtener sesión con ID inválido: '{}'", authenticatedUser, idStr);
                    return;
                }
                int sesionId = Integer.parseInt(idStr);
                logger.info("Usuario '{}' intentando obtener sesión de estudio con ID: {}", authenticatedUser, sesionId);

                SesionEstudio sesion = restFacade.getSesionEstudioById(sesionId);
                if (sesion != null) {
                    out.print(objectMapper.writeValueAsString(sesion));
                    AuditLogger.log("READ", "SesionEstudio", String.valueOf(sesionId), authenticatedUser, "Obtención de sesión de estudio por ID.");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"message\": \"Sesión de estudio con ID " + sesionId + " no encontrada.\"}");
                    logger.warn("Usuario '{}' intentó obtener sesión no encontrada con ID: {}", authenticatedUser, sesionId);
                    AuditLogger.log("READ_FAILED", "SesionEstudio", String.valueOf(sesionId), authenticatedUser, "Intento de obtener sesión no encontrada.");
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de sesión de estudio inválido (formato incorrecto).\"}");
            logger.error("Error de formato de ID en GET de SesionEstudio para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al procesar la solicitud: " + e.getMessage() + "\"}");
            logger.error("Error interno en GET de SesionEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("ERROR", "SesionEstudio", "N/A", authenticatedUser, "Error interno en GET: " + e.getMessage());
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
            SesionEstudio nuevaSesion = objectMapper.readValue(request.getReader(), SesionEstudio.class);

            // Validación de entradas para creación
            if (nuevaSesion.getDuracionPlanificadaMinutos() <= 0 || nuevaSesion.getDuracionPlanificadaMinutos() > 1440) { // Max 24 horas
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Duración de la sesión inválida. Debe ser un número positivo (en minutos) y no exceder 1440.\"}");
                logger.warn("Usuario '{}' intentó crear sesión con duración inválida: {}", authenticatedUser, nuevaSesion.getDuracionPlanificadaMinutos());
                return;
            }
            // TODO: Validar fecha/hora de inicio, ID de usuario, etc.

            logger.info("Usuario '{}' intentando crear una nueva sesión de estudio con duración: {} minutos", authenticatedUser, nuevaSesion.getDuracionPlanificadaMinutos());
            SesionEstudio creada = restFacade.createSesionEstudio(nuevaSesion,false);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(objectMapper.writeValueAsString(creada));
            AuditLogger.log("CREATE", "SesionEstudio", String.valueOf(creada.getId()), authenticatedUser, "Nueva sesión de estudio creada.");

        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Error al leer el JSON de la solicitud o JSON inválido: " + e.getMessage() + "\"}");
            logger.error("Error de JSON en POST de SesionEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al crear la sesión de estudio: " + e.getMessage() + "\"}");
            logger.error("Error interno en POST de SesionEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("CREATE_FAILED", "SesionEstudio", "N/A", authenticatedUser, "Error al crear sesión de estudio: " + e.getMessage());
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
            out.print("{\"message\": \"Se requiere ID de sesión de estudio para la actualización.\"}");
            logger.warn("Usuario '{}' intentó PUT sin ID de sesión de estudio.", authenticatedUser);
            return;
        }

        try {
            String idStr = pathInfo.substring(1);
            if (!InputValidator.isValidIntegerId(idStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de sesión de estudio inválido.\"}");
                logger.warn("Usuario '{}' intentó PUT con ID inválido: '{}'", authenticatedUser, idStr);
                return;
            }
            int sesionId = Integer.parseInt(idStr);
            SesionEstudio sesionActualizar = objectMapper.readValue(request.getReader(), SesionEstudio.class);
            sesionActualizar.setId(sesionId);

            // Validación de entradas para actualización
            if (!Objects.isNull(sesionActualizar.getDuracionPlanificadaMinutos()) && (sesionActualizar.getDuracionPlanificadaMinutos() <= 0 || sesionActualizar.getDuracionPlanificadaMinutos() > 1440)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Duración de la sesión inválida en la actualización.\"}");
                logger.warn("Usuario '{}' intentó actualizar sesión con duración inválida: {}", authenticatedUser, sesionActualizar.getDuracionPlanificadaMinutos());
                return;
            }

            logger.info("Usuario '{}' intentando actualizar sesión de estudio con ID: {}", authenticatedUser, sesionId);
            SesionEstudio actualizado = restFacade.updateSesionEstudio(sesionActualizar);
            if (actualizado != null) {
                out.print(objectMapper.writeValueAsString(actualizado));
                AuditLogger.log("UPDATE", "SesionEstudio", String.valueOf(sesionId), authenticatedUser, "Sesión de estudio actualizada.");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"message\": \"Sesión de estudio con ID " + sesionId + " no encontrada para actualizar.\"}");
                logger.warn("Usuario '{}' intentó actualizar sesión no encontrada con ID: {}", authenticatedUser, sesionId);
                AuditLogger.log("UPDATE_FAILED", "SesionEstudio", String.valueOf(sesionId), authenticatedUser, "Intento de actualizar sesión no encontrada.");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de sesión de estudio inválido (formato incorrecto).\"}");
            logger.error("Error de formato de ID en PUT de SesionEstudio para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Error al leer el JSON o al procesar la solicitud: " + e.getMessage() + "\"}");
            logger.error("Error de JSON en PUT de SesionEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al actualizar la sesión de estudio: " + e.getMessage() + "\"}");
            logger.error("Error interno en PUT de SesionEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("ERROR", "SesionEstudio", "N/A", authenticatedUser, "Error interno en PUT: " + e.getMessage());
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
            out.print("{\"message\": \"Se requiere ID de sesión de estudio para la eliminación.\"}");
            logger.warn("Usuario '{}' intentó DELETE sin ID de sesión de estudio.", authenticatedUser);
            return;
        }

        try {
            String idStr = pathInfo.substring(1);
            if (!InputValidator.isValidIntegerId(idStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de sesión de estudio inválido.\"}");
                logger.warn("Usuario '{}' intentó DELETE con ID inválido: '{}'", authenticatedUser, idStr);
                return;
            }
            int sesionId = Integer.parseInt(idStr);

            logger.info("Usuario '{}' intentando eliminar sesión de estudio con ID: {}", authenticatedUser, sesionId);
            restFacade.deleteSesionEstudio(sesionId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            AuditLogger.log("DELETE", "SesionEstudio", String.valueOf(sesionId), authenticatedUser, "Sesión de estudio eliminada.");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de sesión de estudio inválido (formato incorrecto).\"}");
            logger.error("Error de formato de ID en DELETE de SesionEstudio para usuario '{}': {}", authenticatedUser, e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al eliminar la sesión de estudio: " + e.getMessage() + "\"}");
            logger.error("Error interno en DELETE de SesionEstudio para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("DELETE_FAILED", "SesionEstudio", "N/A", authenticatedUser, "Error al eliminar sesión de estudio: " + e.getMessage());
        } finally {
            out.close();
        }
    }

}
