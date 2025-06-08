/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action.messages;

import com.estudyplus.controlador.services.FriendshipService;
import com.estudyplus.controlador.services.MessageService;
import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.modelo.dto.ConversacionDTO;
import com.estudyplus.modelo.dto.JsonResponse;
import com.estudyplus.modelo.entitys.Amistad;
import com.estudyplus.modelo.entitys.Conversacion;
import com.estudyplus.modelo.entitys.Mensaje;
import com.estudyplus.modelo.entitys.Usuario;
import com.opensymphony.xwork2.ActionSupport;
import java.util.List;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;
import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Action para gestionar conversaciones y mensajes.
 * Todos los métodos devuelven JsonResponse para peticiones AJAX.
 * @author Parri
 */
public class MessageManagementAction extends ActionSupport implements SessionAware {

    private static final Logger logger = LoggerFactory.getLogger(MessageManagementAction.class);

    private JsonResponse jsonResponse;
    private Map<String, Object> session;
    private Integer friendId; // Para iniciar/obtener conversación con un amigo
    private Integer conversationId; // Para obtener mensajes o enviar en una conversación existente
    private String messageContent; // Contenido del nuevo mensaje

    private MessageService messageService;

    public MessageManagementAction() {
        this.messageService = new MessageService();
    }

    /**
     * Obtiene todas las conversaciones del usuario logeado.
     * Devuelve JsonResponse con la lista de objetos Conversacion en 'data'.
     */
    public String getConversationsAjax() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, "Necesitas iniciar sesión para ver tus conversaciones.");
            logger.warn("Intento de obtener conversaciones AJAX sin usuario logeado.");
            return SUCCESS;
        }

        try {
            List<ConversacionDTO> conversations = messageService.getConversationsForUser(loggedInUser.getId());
            jsonResponse = new JsonResponse(true, "Conversaciones obtenidas con éxito.", conversations);
            logger.info("Conversaciones obtenidas para usuario ID {}: {} conversaciones.", loggedInUser.getId(), conversations != null ? conversations.size() : 0);
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, "Error al obtener las conversaciones: " + e.getMessage());
            logger.error("ServiceException al obtener conversaciones para usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, "Ocurrió un error inesperado al obtener conversaciones.");
            logger.error("Excepción inesperada en getConversationsAjax para usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
            return SUCCESS;
        }
    }

    /**
     * Obtiene o crea una conversación con un amigo específico.
     * Devuelve JsonResponse con el objeto Conversacion en 'data'.
     */
    public String getOrCreateConversationAjax() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, "Necesitas iniciar sesión para iniciar una conversación.");
            logger.warn("Intento de obtener/crear conversación AJAX sin usuario logeado.");
            return SUCCESS;
        }

        if (friendId == null) {
            jsonResponse = new JsonResponse(false, "ID del amigo no especificado para iniciar conversación.");
            logger.warn("Intento de obtener/crear conversación AJAX con friendId nulo.");
            return SUCCESS;
        }

        try {
            Conversacion conversation = messageService.getOrCreateConversation(loggedInUser.getId(), friendId);
            jsonResponse = new JsonResponse(true, "Conversación obtenida/creada con éxito.", conversation);
            logger.info("Conversación obtenida/creada entre usuario ID {} y amigo ID {}.", loggedInUser.getId(), friendId);
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, "Error al obtener/crear la conversación: " + e.getMessage());
            logger.error("ServiceException al obtener/crear conversación entre {} y {}: {}", loggedInUser.getId(), friendId, e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, "Ocurrió un error inesperado al obtener/crear conversación.");
            logger.error("Excepción inesperada en getOrCreateConversationAjax entre {} y {}: {}", loggedInUser.getId(), friendId, e.getMessage(), e);
            return SUCCESS;
        }
    }

    /**
     * Obtiene los mensajes de una conversación específica.
     * Devuelve JsonResponse con la lista de objetos Mensaje en 'data'.
     *
     * @param offset El desplazamiento para la paginación (ej. 0 para los primeros mensajes).
     * @param limit El número máximo de mensajes a devolver.
     */
    public String getMessagesInConversationAjax() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, "Necesitas iniciar sesión para ver los mensajes.");
            logger.warn("Intento de obtener mensajes AJAX sin usuario logeado.");
            return SUCCESS;
        }

        if (conversationId == null) {
            jsonResponse = new JsonResponse(false, "ID de conversación no especificado para obtener mensajes.");
            logger.warn("Intento de obtener mensajes AJAX con conversationId nulo.");
            return SUCCESS;
        }

        try {
            // Puedes añadir lógica de paginación aquí si es necesario (offset, limit)
            // Por simplicidad, obtenemos los últimos 50 mensajes.
            List<Mensaje> messages = messageService.getMessagesInConversation(conversationId, 0, 50);
            jsonResponse = new JsonResponse(true, "Mensajes obtenidos con éxito.", messages);
            logger.info("Mensajes obtenidos para conversación ID {}: {} mensajes.", conversationId, messages != null ? messages.size() : 0);
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, "Error al obtener los mensajes: " + e.getMessage());
            logger.error("ServiceException al obtener mensajes para conversación ID {}: {}", conversationId, e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, "Ocurrió un error inesperado al obtener mensajes.");
            logger.error("Excepción inesperada en getMessagesInConversationAjax para conversación ID {}: {}", conversationId, e.getMessage(), e);
            return SUCCESS;
        }
    }

    /**
     * Envía un nuevo mensaje en una conversación.
     * Devuelve JsonResponse con el objeto Mensaje creado en 'data'.
     */
    public String sendMessageAjax() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, "Necesitas iniciar sesión para enviar mensajes.");
            logger.warn("Intento de enviar mensaje AJAX sin usuario logeado.");
            return SUCCESS;
        }

        if (conversationId == null || messageContent == null || messageContent.trim().isEmpty()) {
            jsonResponse = new JsonResponse(false, "ID de conversación o contenido del mensaje no especificado.");
            logger.warn("Intento de enviar mensaje AJAX con datos incompletos.");
            return SUCCESS;
        }

        try {
            Mensaje newMessage = new Mensaje();
//            Conversacion conversacion = new Conversacion();
//            conversacion.setId(conversationId);
            newMessage.setEmisorId(loggedInUser);
            newMessage.setContenido(ESAPI.encoder().encodeForHTML(messageContent.trim()));
            newMessage.setFechaEnvio(new java.util.Date()); // Establecer fecha de envío
            newMessage.setLeido(false); // Por defecto, no leído por el receptor
//            newMessage.setConversacionId(conversacion);

            Mensaje sentMessage = messageService.sendMessage(newMessage, conversationId);
            jsonResponse = new JsonResponse(true, "Mensaje enviado con éxito.", sentMessage);
            logger.info("Mensaje enviado por usuario ID {} en conversación ID {}.", loggedInUser.getId(), conversationId);
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, "Error al enviar el mensaje: " + e.getMessage());
            logger.error("ServiceException al enviar mensaje en conversación ID {}: {}", conversationId, e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, "Ocurrió un error inesperado al enviar el mensaje.");
            logger.error("Excepción inesperada en sendMessageAjax en conversación ID {}: {}", conversationId, e.getMessage(), e);
            return SUCCESS;
        }
    }

    /**
     * Marca los mensajes de una conversación como leídos.
     * Devuelve JsonResponse con el resultado de la operación.
     */
    public String markMessagesAsReadAjax() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, "Necesitas iniciar sesión para marcar mensajes como leídos.");
            logger.warn("Intento de marcar mensajes como leídos AJAX sin usuario logeado.");
            return SUCCESS;
        }

        if (conversationId == null) {
            jsonResponse = new JsonResponse(false, "ID de conversación no especificado para marcar mensajes como leídos.");
            logger.warn("Intento de marcar mensajes como leídos AJAX con conversationId nulo.");
            return SUCCESS;
        }

        try {
            messageService.markMessagesAsRead(conversationId, loggedInUser.getId());
            jsonResponse = new JsonResponse(true, "Mensajes marcados como leídos con éxito.");
            logger.info("Mensajes marcados como leídos en conversación ID {} por usuario ID {}.", conversationId, loggedInUser.getId());
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, "Error al marcar mensajes como leídos: " + e.getMessage());
            logger.error("ServiceException al marcar mensajes como leídos en conversación ID {}: {}", conversationId, e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, "Ocurrió un error inesperado al marcar mensajes como leídos.");
            logger.error("Excepción inesperada en markMessagesAsReadAjax en conversación ID {}: {}", conversationId, e.getMessage(), e);
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

    public Integer getFriendId() {
        return friendId;
    }

    public void setFriendId(Integer friendId) {
        this.friendId = friendId;
    }

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
