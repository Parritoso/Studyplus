/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action.messages;

import com.estudyplus.controlador.services.FriendshipService;
import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.modelo.dto.JsonResponse;
import com.estudyplus.modelo.entitys.Amistad;
import com.estudyplus.modelo.entitys.Usuario;
import com.opensymphony.xwork2.ActionSupport;
import java.util.List;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Action para gestionar amigos y solicitudes de amistad (aceptar/rechazar).
 * Todos los métodos devuelven JsonResponse para peticiones AJAX.
 * @author Parri
 */
public class FriendshipManagementAction extends ActionSupport implements SessionAware {

    private static final Logger logger = LoggerFactory.getLogger(FriendshipManagementAction.class);

    private JsonResponse jsonResponse;
    private Map<String, Object> session;
    private Integer amistadId; // Para aceptar/rechazar solicitudes

    private FriendshipService friendshipService;

    public FriendshipManagementAction() {
        this.friendshipService = new FriendshipService();
    }

    /**
     * Obtiene la lista de amigos del usuario logeado.
     * Devuelve JsonResponse con la lista de usuarios amigos en 'data'.
     */
    public String getFriendsAjax() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, "Necesitas iniciar sesión para ver tus amigos.");
            logger.warn("Intento de obtener amigos AJAX sin usuario logeado.");
            return SUCCESS;
        }

        try {
            List<Usuario> friends = friendshipService.getFriends(loggedInUser.getId());
            jsonResponse = new JsonResponse(true, "Lista de amigos obtenida con éxito.", friends);
            logger.info("Amigos obtenidos para usuario ID {}: {} amigos.", loggedInUser.getId(), friends != null ? friends.size() : 0);
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, "Error al obtener la lista de amigos: " + e.getMessage());
            logger.error("ServiceException al obtener amigos para usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, "Ocurrió un error inesperado al obtener amigos.");
            logger.error("Excepción inesperada en getFriendsAjax para usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
            return SUCCESS;
        }
    }

    /**
     * Obtiene las solicitudes de amistad pendientes para el usuario logeado.
     * Devuelve JsonResponse con la lista de objetos Amistad en 'data'.
     */
    public String getPendingRequestsAjax() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, "Necesitas iniciar sesión para ver las solicitudes pendientes.");
            logger.warn("Intento de obtener solicitudes pendientes AJAX sin usuario logeado.");
            return SUCCESS;
        }

        try {
            List<Amistad> pendingRequests = friendshipService.getPendingFriendRequests(loggedInUser.getId());
            jsonResponse = new JsonResponse(true, "Solicitudes pendientes obtenidas con éxito.", pendingRequests);
            logger.info("Solicitudes pendientes obtenidas para usuario ID {}: {} solicitudes.", loggedInUser.getId(), pendingRequests != null ? pendingRequests.size() : 0);
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, "Error al obtener las solicitudes pendientes: " + e.getMessage());
            logger.error("ServiceException al obtener solicitudes pendientes para usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, "Ocurrió un error inesperado al obtener solicitudes pendientes.");
            logger.error("Excepción inesperada en getPendingRequestsAjax para usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
            return SUCCESS;
        }
    }

    /**
     * Acepta una solicitud de amistad.
     * Devuelve JsonResponse con el resultado de la operación.
     */
    public String acceptRequestAjax() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, "Necesitas iniciar sesión para aceptar solicitudes.");
            logger.warn("Intento de aceptar solicitud AJAX sin usuario logeado.");
            return SUCCESS;
        }

        if (amistadId == null) {
            jsonResponse = new JsonResponse(false, "ID de solicitud de amistad no especificado.");
            logger.warn("Intento de aceptar solicitud AJAX con ID nulo.");
            return SUCCESS;
        }

        try {
            Amistad acceptedAmistad = friendshipService.acceptFriendRequest(amistadId);
            jsonResponse = new JsonResponse(true, "Solicitud de amistad aceptada con éxito.", acceptedAmistad);
            logger.info("Solicitud de amistad ID {} aceptada por usuario ID {}.", amistadId, loggedInUser.getId());
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, "Error al aceptar la solicitud de amistad: " + e.getMessage());
            logger.error("ServiceException al aceptar solicitud de amistad ID {}: {}", amistadId, e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, "Ocurrió un error inesperado al aceptar la solicitud de amistad.");
            logger.error("Excepción inesperada en acceptRequestAjax para solicitud ID {}: {}", amistadId, e.getMessage(), e);
            return SUCCESS;
        }
    }

    /**
     * Rechaza una solicitud de amistad.
     * Devuelve JsonResponse con el resultado de la operación.
     */
    public String rejectRequestAjax() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, "Necesitas iniciar sesión para rechazar solicitudes.");
            logger.warn("Intento de rechazar solicitud AJAX sin usuario logeado.");
            return SUCCESS;
        }

        if (amistadId == null) {
            jsonResponse = new JsonResponse(false, "ID de solicitud de amistad no especificado.");
            logger.warn("Intento de rechazar solicitud AJAX con ID nulo.");
            return SUCCESS;
        }

        try {
            Amistad rejectedAmistad = friendshipService.rejectFriendRequest(amistadId);
            jsonResponse = new JsonResponse(true, "Solicitud de amistad rechazada con éxito.", rejectedAmistad);
            logger.info("Solicitud de amistad ID {} rechazada por usuario ID {}.", amistadId, loggedInUser.getId());
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, "Error al rechazar la solicitud de amistad: " + e.getMessage());
            logger.error("ServiceException al rechazar solicitud de amistad ID {}: {}", amistadId, e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, "Ocurrió un error inesperado al rechazar la solicitud de amistad.");
            logger.error("Excepción inesperada en rejectRequestAjax para solicitud ID {}: {}", amistadId, e.getMessage(), e);
            return SUCCESS;
        }
    }

    // --- Getters y Setters ---
    public JsonResponse getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(JsonResponse jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    public Integer getAmistadId() {
        return amistadId;
    }

    public void setAmistadId(Integer amistadId) {
        this.amistadId = amistadId;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
