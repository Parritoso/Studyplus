/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.services;

import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.modelo.dto.ConversacionDTO;
import com.estudyplus.modelo.entitys.Conversacion;
import com.estudyplus.modelo.entitys.Mensaje;
import com.estudyplus.modelo.rest.facade.EstudyRestFacade;
import com.estudyplus.modelo.rest.facade.EstudyRestFacadeFactory;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Parri
 */
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private final EstudyRestFacade facade;

    public MessageService(EstudyRestFacade facade) {
        if (facade == null) {
            throw new IllegalArgumentException("EstudyRestFacade no puede ser nula.");
        }
        this.facade = facade;
    }

    // Constructor que usa la factoría
    public MessageService() {
        this(EstudyRestFacadeFactory.getInstance());
    }

    /**
     * Obtiene o crea una conversación entre dos usuarios.
     * @param user1Id ID del primer usuario.
     * @param user2Id ID del segundo usuario.
     * @return El objeto Conversacion.
     * @throws ServiceException Si ocurre un error.
     */
    public Conversacion getOrCreateConversation(Integer user1Id, Integer user2Id) throws ServiceException {
        try {
            logger.info("Intentando obtener/crear conversación entre {} y {}.", user1Id, user2Id);
            return facade.getOrCreateConversation(user1Id, user2Id);
        } catch (IOException e) {
            logger.error("Error de comunicación al obtener/crear conversación: {}", e.getMessage(), e);
            throw new ServiceException("No se pudo obtener/crear la conversación. " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al obtener/crear conversación: {}", e.getMessage(), e);
            throw new ServiceException("Ocurrió un error inesperado al obtener/crear la conversación.", e);
        }
    }

    /**
     * Obtiene todas las conversaciones de un usuario.
     * @param userId ID del usuario.
     * @return Lista de objetos Conversacion.
     * @throws ServiceException Si ocurre un error.
     */
    public List<ConversacionDTO> getConversationsForUser(Integer userId) throws ServiceException {
        try {
            logger.info("Obteniendo conversaciones para el usuario ID: {}.", userId);
            return facade.getConversationsForUser(userId);
        } catch (IOException e) {
            logger.error("Error de comunicación al obtener conversaciones para el usuario ID {}: {}", userId, e.getMessage(), e);
            throw new ServiceException("No se pudieron obtener las conversaciones. " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al obtener conversaciones para el usuario ID {}: {}", userId, e.getMessage(), e);
            throw new ServiceException("Ocurrió un error inesperado al obtener las conversaciones.", e);
        }
    }

    /**
     * Obtiene los mensajes de una conversación específica.
     * @param conversationId ID de la conversación.
     * @param offset Número de mensajes a saltar (para paginación).
     * @param limit Número máximo de mensajes a devolver.
     * @return Lista de objetos Mensaje.
     * @throws ServiceException Si ocurre un error.
     */
    public List<Mensaje> getMessagesInConversation(Integer conversationId, int offset, int limit) throws ServiceException {
        try {
            logger.info("Obteniendo {} mensajes (offset {}) de la conversación ID: {}.", limit, offset, conversationId);
            return facade.getMessagesInConversation(conversationId, offset, limit);
        } catch (IOException e) {
            logger.error("Error de comunicación al obtener mensajes de la conversación ID {}: {}", conversationId, e.getMessage(), e);
            throw new ServiceException("No se pudieron obtener los mensajes de la conversación. " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al obtener mensajes de la conversación ID {}: {}", conversationId, e.getMessage(), e);
            throw new ServiceException("Ocurrió un error inesperado al obtener los mensajes de la conversación.", e);
        }
    }

    /**
     * Envía un mensaje en una conversación.
     * @param mensaje El objeto Mensaje a enviar.
     * @return El objeto Mensaje creado con su ID.
     * @throws ServiceException Si ocurre un error.
     */
    public Mensaje sendMessage(Mensaje mensaje, Integer conversationId) throws ServiceException {
        try {
            Conversacion conversation = facade.getConversacionById(conversationId);
            mensaje.setConversacionId(conversation);
            logger.info("Enviando mensaje en conversación ID {} desde emisor ID {}.", mensaje.getConversacionId(), mensaje.getEmisorId());
            return facade.sendMessage(mensaje);
        } catch (IOException e) {
            logger.error("Error de comunicación al enviar mensaje: {}", e.getMessage(), e);
            throw new ServiceException("No se pudo enviar el mensaje. " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al enviar mensaje: {}", e.getMessage(), e);
            throw new ServiceException("Ocurrió un error inesperado al enviar el mensaje.", e);
        }
    }

    /**
     * Marca los mensajes de una conversación como leídos para un usuario específico.
     * @param conversationId ID de la conversación.
     * @param userId ID del usuario que marcó los mensajes como leídos.
     * @throws ServiceException Si ocurre un error.
     */
    public void markMessagesAsRead(Integer conversationId, Integer userId) throws ServiceException {
        try {
            logger.info("Marcando mensajes como leídos en conversación ID {} para usuario ID {}.", conversationId, userId);
            facade.markMessagesAsRead(conversationId, userId);
        } catch (IOException e) {
            logger.error("Error de comunicación al marcar mensajes como leídos: {}", e.getMessage(), e);
            throw new ServiceException("No se pudieron marcar los mensajes como leídos. " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al marcar mensajes como leídos: {}", e.getMessage(), e);
            throw new ServiceException("Ocurrió un error inesperado al marcar los mensajes como leídos.", e);
        }
    }
}
