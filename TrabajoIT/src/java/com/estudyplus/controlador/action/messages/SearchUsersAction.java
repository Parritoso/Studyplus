/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action.messages;

import com.estudyplus.controlador.services.FriendshipService;
import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.modelo.dto.JsonResponse;
import com.estudyplus.modelo.entitys.Usuario;
import com.opensymphony.xwork2.ActionSupport;
import java.util.List;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Action para buscar usuarios. El método `searchAjax` devuelve JSON
 * para ser consumido por peticiones AJAX.
 * @author Parri
 */
public class SearchUsersAction extends ActionSupport implements SessionAware {

    private static final Logger logger = LoggerFactory.getLogger(SearchUsersAction.class);

    private String searchQuery; // Para el término de búsqueda
    private Integer groupId;
    private JsonResponse jsonResponse; // Objeto que se serializará a JSON como respuesta
    private Map<String, Object> session;

    private FriendshipService friendshipService;

    public SearchUsersAction() {
        this.friendshipService = new FriendshipService();
    }

    // Método principal para la carga inicial de la página (si se usa de forma no-AJAX)
    // En el contexto de una página única con AJAX, este método podría no ser llamado directamente
    // o simplemente devolver INPUT para mostrar el formulario vacío.
    @Override
    public String execute() {
        // En un flujo AJAX, esta acción podría no ser el punto de entrada principal
        // para la búsqueda, sino que el formulario se carga con FriendsAndMessagesAction.
        // Si se accede directamente a searchUsers.action via GET, simplemente muestra el formulario.
        logger.info("Acceso a SearchUsersAction (execute).");
        return INPUT; // Devuelve el formulario de búsqueda (si existiera como JSP separado)
    }

    /**
     * Método para manejar peticiones AJAX de búsqueda de usuarios.
     * Devuelve un objeto JsonResponse que será serializado a JSON.
     * Los resultados de la búsqueda se incluyen en el campo 'data'.
     */
    public String searchAjax() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, "Necesitas iniciar sesión para buscar usuarios.");
            logger.warn("Intento de búsqueda AJAX sin usuario logeado.");
            return SUCCESS; // Struts2 serializará jsonResponse
        }

        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            jsonResponse = new JsonResponse(false, "El término de búsqueda no puede estar vacío.");
            logger.warn("Búsqueda AJAX con término vacío.");
            return SUCCESS;
        }

        try {
            List<Usuario> usersFound = friendshipService.searchUsers(searchQuery);

            // Filtrar al propio usuario de los resultados
            if (usersFound != null) {
                usersFound.removeIf(u -> u.getId().equals(loggedInUser.getId()));
            }

            if (usersFound == null || usersFound.isEmpty()) {
                jsonResponse = new JsonResponse(true, "No se encontraron usuarios que coincidan con su búsqueda.", usersFound);
                logger.info("Búsqueda AJAX sin resultados para '{}'.", searchQuery);
            } else {
                jsonResponse = new JsonResponse(true, "Resultados de la búsqueda:", usersFound);
                logger.info("Búsqueda AJAX exitosa para '{}', {} resultados.", searchQuery, usersFound.size());
            }
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, "Error al buscar usuarios: " + e.getMessage());
            logger.error("ServiceException en searchAjax para '{}': {}", searchQuery, e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, "Ocurrió un error inesperado al buscar usuarios.");
            logger.error("Excepción inesperada en searchAjax para '{}': {}", searchQuery, e.getMessage(), e);
            return SUCCESS;
        }
    }

    public String searchGroupAjax() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, "Necesitas iniciar sesión para buscar usuarios.");
            logger.warn("Intento de búsqueda AJAX sin usuario logeado.");
            return SUCCESS; // Struts2 serializará jsonResponse
        }

        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            jsonResponse = new JsonResponse(false, "El término de búsqueda no puede estar vacío.");
            logger.warn("Búsqueda AJAX con término vacío.");
            return SUCCESS;
        }

        try {
            List<Usuario> usersFound = friendshipService.searchUsers(searchQuery);

            // Filtrar al propio usuario de los resultados
            if (usersFound != null) {
                usersFound.removeIf(u -> u.getId().equals(loggedInUser.getId()));
            }

            if (usersFound == null || usersFound.isEmpty()) {
                jsonResponse = new JsonResponse(true, "No se encontraron usuarios que coincidan con su búsqueda.", usersFound);
                logger.info("Búsqueda AJAX sin resultados para '{}'.", searchQuery);
            } else {
                jsonResponse = new JsonResponse(true, "Resultados de la búsqueda:", usersFound);
                logger.info("Búsqueda AJAX exitosa para '{}', {} resultados.", searchQuery, usersFound.size());
            }
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, "Error al buscar usuarios: " + e.getMessage());
            logger.error("ServiceException en searchAjax para '{}': {}", searchQuery, e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, "Ocurrió un error inesperado al buscar usuarios.");
            logger.error("Excepción inesperada en searchAjax para '{}': {}", searchQuery, e.getMessage(), e);
            return SUCCESS;
        }
    }

    // --- Getters y Setters para Struts2 ---
    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public JsonResponse getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(JsonResponse jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
