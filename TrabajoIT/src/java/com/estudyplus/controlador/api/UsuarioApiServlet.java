/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.api;

import com.estudyplus.modelo.entitys.Usuario;
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
public class UsuarioApiServlet extends HttpServlet {

    private final EstudyRestFacade restFacade = new EstudyRestFacade();
    private final ObjectMapper objectMapper = new ObjectMapper(); // Para serializar/deserializar JSON
    private static final Logger logger = LoggerFactory.getLogger(UsuarioApiServlet.class); // Logger para el servlet
    
    // Método auxiliar para obtener el nombre de usuario del atributo de la petición
    private String getAuthenticatedUsername(HttpServletRequest request) {
        return (String) request.getAttribute("username"); // Asumimos que JwtAuthFilter lo setea
    }

    // GET /api/usuarios           -> Obtener todos los usuarios
    // GET /api/usuarios/{id}      -> Obtener un usuario por ID
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo(); // Obtiene la parte de la URL después de "/api/usuarios"
        String authenticatedUser = getAuthenticatedUsername(request); // Obtener usuario autenticado


        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                logger.info("Usuario '{}' intentando obtener todos los usuarios.", authenticatedUser);
                // Petición: GET /api/usuarios
                List<Usuario> usuarios = restFacade.getAllUsuarios();
                out.print(objectMapper.writeValueAsString(usuarios));
                AuditLogger.log("READ_ALL", "Usuario", "N/A", authenticatedUser, "Obtención de todos los usuarios.");
            } else {
                // Petición: GET /api/usuarios/{id}
                // Extraer el ID de la URL
                String userIdStr = pathInfo.substring(1); // Quita el '/' inicial
                if (!InputValidator.isValidIntegerId(userIdStr)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"message\": \"ID de usuario inválido.\"}");
                    logger.warn("Usuario '{}' intentó obtener usuario con ID inválido: '{}'", authenticatedUser, userIdStr);
                    return;
                }
                int userId = Integer.parseInt(userIdStr);
                logger.info("Usuario '{}' intentando obtener usuario con ID: {}", authenticatedUser, userId);
                Usuario usuario = restFacade.getUsuarioById(userId);

                if (usuario != null) {
                    out.print(objectMapper.writeValueAsString(usuario));
                    AuditLogger.log("READ", "Usuario", String.valueOf(userId), authenticatedUser, "Obtención de usuario por ID.");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found
                    out.print("{\"message\": \"Usuario con ID " + userId + " no encontrado.\"}");
                    logger.warn("Usuario '{}' intentó obtener usuario no encontrado con ID: {}", authenticatedUser, userId);
                    AuditLogger.log("READ_FAILED", "Usuario", String.valueOf(userId), authenticatedUser, "Intento de obtener usuario no encontrado.");
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            out.print("{\"message\": \"ID de usuario inválido.\"}");
            logger.error("Error de formato de ID en GET de Usuario para usuario '{}': {}", authenticatedUser, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            out.print("{\"message\": \"Error al procesar la solicitud: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } catch (Exception e) {
            // Capturar otras excepciones de la Fachada/DB
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            out.print("{\"message\": \"Error interno del servidor: " + e.getMessage() + "\"}");
            logger.error("Error interno en GET de Usuario para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("ERROR", "Usuario", "N/A", authenticatedUser, "Error interno en GET: " + e.getMessage());
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    // POST /api/usuarios          -> Crear un nuevo usuario
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String authenticatedUser = getAuthenticatedUsername(request);

        try {
            // Leer el cuerpo de la petición (JSON) y convertirlo a un objeto Usuario
            Usuario nuevoUsuario = objectMapper.readValue(request.getReader(), Usuario.class);

            // Validaciones básicas (pueden ser más robustas)
            if (nuevoUsuario.getNombre() == null || nuevoUsuario.getNombre().isEmpty() ||
                nuevoUsuario.getEmail() == null || nuevoUsuario.getEmail().isEmpty() ||
                nuevoUsuario.getContrasena() == null || nuevoUsuario.getContrasena().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Los campos nombre, email y contraseña son obligatorios.\"}");
                logger.warn("Usuario '{}' intentó crear usuario con email inválido: '{}'", authenticatedUser, nuevoUsuario.getEmail());
                return;
            }
            if (!InputValidator.isValidPassword(nuevoUsuario.getContrasena())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Contraseña no cumple con los requisitos de seguridad.\"}");
                logger.warn("Usuario '{}' intentó crear usuario con contraseña débil.", authenticatedUser);
                return;
            }

            logger.info("Usuario '{}' intentando crear un nuevo usuario con email: {}", authenticatedUser, nuevoUsuario.getEmail());
            Usuario usuarioCreado = restFacade.createUsuario(nuevoUsuario); // Usar la Fachada

            response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
            out.print(objectMapper.writeValueAsString(usuarioCreado));
            AuditLogger.log("CREATE", "Usuario", String.valueOf(usuarioCreado.getId()), authenticatedUser, "Nuevo usuario creado: " + usuarioCreado.getEmail());
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request (si el JSON es inválido)
            out.print("{\"message\": \"Error al leer el JSON de la solicitud: " + e.getMessage() + "\"}");
            logger.error("Error de JSON en POST de Usuario para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            out.print("{\"message\": \"Error al crear el usuario: " + e.getMessage() + "\"}");
            logger.error("Error interno en POST de Usuario para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("CREATE_FAILED", "Usuario", "N/A", authenticatedUser, "Error al crear usuario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    // PUT /api/usuarios/{id}      -> Actualizar un usuario existente
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request);

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Se requiere el ID del usuario para la actualización (e.g., /api/usuarios/1).\"}");
            logger.warn("Usuario '{}' intentó PUT sin ID de usuario.", authenticatedUser);
            return;
        }

        try {
            String idStr = pathInfo.substring(1);
            if (!InputValidator.isValidIntegerId(idStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de usuario inválido.\"}");
                logger.warn("Usuario '{}' intentó PUT con ID inválido: '{}'", authenticatedUser, idStr);
                return;
            }
            int userId = Integer.parseInt(idStr); // Extraer ID de la URL
            Usuario usuarioActualizar = objectMapper.readValue(request.getReader(), Usuario.class);

            // Asegurarse de que el ID del path y el ID del objeto coincidan (o al menos que el del path sea el que se usa)
            usuarioActualizar.setId(userId);

            // Validación de entradas para actualización
            if (usuarioActualizar.getEmail() != null && !InputValidator.isValidEmail(usuarioActualizar.getEmail())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Email inválido en la actualización.\"}");
                logger.warn("Usuario '{}' intentó actualizar usuario con email inválido: '{}'", authenticatedUser, usuarioActualizar.getEmail());
                return;
            }
            if (usuarioActualizar.getContrasena() != null && !InputValidator.isValidPassword(usuarioActualizar.getContrasena())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"Nueva contraseña no cumple con los requisitos de seguridad.\"}");
                logger.warn("Usuario '{}' intentó actualizar usuario con contraseña débil.", authenticatedUser);
                return;
            }
            logger.info("Usuario '{}' intentando actualizar usuario con ID: {}", authenticatedUser, userId);
            
            Usuario usuarioActualizado = restFacade.updateUsuario(usuarioActualizar); // Usar la Fachada
            if (usuarioActualizado != null) {
                out.print(objectMapper.writeValueAsString(usuarioActualizado));
                AuditLogger.log("UPDATE", "Usuario", String.valueOf(userId), authenticatedUser, "Usuario actualizado: " + usuarioActualizar.getEmail());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"message\": \"Usuario con ID " + userId + " no encontrado para actualizar.\"}");
                logger.warn("Usuario '{}' intentó actualizar usuario no encontrado con ID: {}", authenticatedUser, userId);
                AuditLogger.log("UPDATE_FAILED", "Usuario", String.valueOf(userId), authenticatedUser, "Intento de actualizar usuario no encontrado.");
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de usuario inválido.\"}");
            logger.error("Error de formato de ID en PUT de Usuario para usuario '{}': {}", authenticatedUser, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Error al leer el JSON o al procesar la solicitud: " + e.getMessage() + "\"}");
            logger.error("Error de JSON en PUT de Usuario para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al actualizar el usuario: " + e.getMessage() + "\"}");
            logger.error("Error interno en PUT de Usuario para usuario '{}': {}", authenticatedUser, e.getMessage(), e);
            AuditLogger.log("ERROR", "Usuario", "N/A", authenticatedUser, "Error interno en PUT: " + e.getMessage());
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    // DELETE /api/usuarios/{id}   -> Eliminar un usuario
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        String authenticatedUser = getAuthenticatedUsername(request);

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"Se requiere el ID del usuario para la eliminación (e.g., /api/usuarios/1).\"}");
            logger.warn("Usuario '{}' intentó DELETE sin ID de usuario.", authenticatedUser);
            return;
        }

        try {
            String idStr = pathInfo.substring(1);
            if (!InputValidator.isValidIntegerId(idStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"message\": \"ID de usuario inválido.\"}");
                logger.warn("Usuario '{}' intentó DELETE con ID inválido: '{}'", authenticatedUser, idStr);
                return;
            }
            int userId = Integer.parseInt(idStr); // Extraer ID de la URL
            logger.info("Usuario '{}' intentando eliminar usuario con ID: {}", authenticatedUser, userId);
            restFacade.deleteUsuario(userId); // Usar la Fachada
            response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content (Éxito sin contenido de respuesta)
            AuditLogger.log("DELETE", "Usuario", String.valueOf(userId), authenticatedUser, "Usuario eliminado.");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"message\": \"ID de usuario inválido.\"}");
            logger.error("Error de formato de ID en DELETE de Usuario para usuario '{}': {}", authenticatedUser, e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\": \"Error al eliminar el usuario: " + e.getMessage() + "\"}");
            AuditLogger.log("DELETE_FAILED", "Usuario", "N/A", authenticatedUser, "Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

}
