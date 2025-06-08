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
 * Action para enviar solicitudes de amistad.
 * Devuelve un objeto JsonResponse para ser consumido por peticiones AJAX.
 * @author Parri
 */
public class SendFriendRequestAction extends ActionSupport implements SessionAware {

    private static final Logger logger = LoggerFactory.getLogger(SendFriendRequestAction.class);

    private Integer receptorId; // ID del usuario al que se le envía la solicitud
    private JsonResponse jsonResponse; // Objeto que se serializará a JSON como respuesta
    private Map<String, Object> session;

    private FriendshipService friendshipService;

    public SendFriendRequestAction() {
        this.friendshipService = new FriendshipService();
    }

    @Override
    public String execute() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, "Necesitas iniciar sesión para enviar solicitudes de amistad.");
            logger.warn("Intento de enviar solicitud AJAX sin usuario logeado.");
            return SUCCESS;
        }

        if (receptorId == null) {
            jsonResponse = new JsonResponse(false, "ID del receptor no especificado.");
            logger.warn("Intento de enviar solicitud AJAX con ID de receptor nulo.");
            return SUCCESS;
        }
        
        if (loggedInUser.getId().equals(receptorId)) {
            jsonResponse = new JsonResponse(false, "No puedes enviarte una solicitud de amistad a ti mismo.");
            logger.warn("Intento de enviar solicitud AJAX a sí mismo.");
            return SUCCESS;
        }

        try {
            friendshipService.sendFriendRequest(loggedInUser.getId(), receptorId);
            jsonResponse = new JsonResponse(true, "Solicitud de amistad enviada con éxito.");
            logger.info("Solicitud de amistad enviada de usuario {} a {}.", loggedInUser.getId(), receptorId);
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, "Error al enviar solicitud de amistad: " + e.getMessage());
            logger.error("ServiceException al enviar solicitud de amistad de {} a {}: {}", loggedInUser.getId(), receptorId, e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, "Ocurrió un error inesperado al enviar la solicitud de amistad.");
            logger.error("Excepción inesperada en SendFriendRequestAction: {}", e.getMessage(), e);
            return SUCCESS;
        }
    }

    // --- Getters y Setters ---
    public Integer getReceptorId() {
        return receptorId;
    }

    public void setReceptorId(Integer receptorId) {
        this.receptorId = receptorId;
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
