/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.services;

import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.modelo.entitys.Amistad;
import com.estudyplus.modelo.entitys.Usuario;
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
public class FriendshipService {
    private static final Logger logger = LoggerFactory.getLogger(FriendshipService.class);
    private final EstudyRestFacade facade;

    public FriendshipService(EstudyRestFacade facade) {
        if (facade == null) {
            throw new IllegalArgumentException("EstudyRestFacade no puede ser nula.");
        }
        this.facade = facade;
    }

    // Constructor que usa la factoría (para casos donde no se inyecta directamente)
    public FriendshipService() {
        this(EstudyRestFacadeFactory.getInstance());
    }

    /**
     * Busca usuarios por una cadena de consulta.
     * @param query La cadena de búsqueda.
     * @return Una lista de Usuarios que coinciden.
     * @throws ServiceException Si ocurre un error durante la búsqueda.
     */
    public List<Usuario> searchUsers(String query) throws ServiceException {
        try {
            logger.info("Buscando usuarios con la query: '{}'", query);
            return facade.searchUsers(query);
        } catch (IOException e) {
            logger.error("Error de comunicación al buscar usuarios: {}", e.getMessage(), e);
            throw new ServiceException("No se pudieron buscar usuarios. " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al buscar usuarios: {}", e.getMessage(), e);
            throw new ServiceException("Ocurrió un error inesperado al buscar usuarios.", e);
        }
    }

    /**
     * Envía una solicitud de amistad.
     * @param remitenteId ID del usuario que envía.
     * @param receptorId ID del usuario que recibe.
     * @return El objeto Amistad creado/actualizado.
     * @throws ServiceException Si la solicitud no pudo ser enviada.
     */
    public Amistad sendFriendRequest(Integer remitenteId, Integer receptorId) throws ServiceException {
        try {
            logger.info("Enviando solicitud de amistad de usuario {} a usuario {}.", remitenteId, receptorId);
            // Lógica de validación a nivel de negocio (ej. ya son amigos, solicitud pendiente)
            // Esto es ideal que se valide en la API REST, pero también se puede hacer aquí.
            return facade.sendFriendRequest(remitenteId, receptorId);
        } catch (IOException e) {
            logger.error("Error de comunicación al enviar solicitud de amistad: {}", e.getMessage(), e);
            throw new ServiceException("No se pudo enviar la solicitud de amistad. " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al enviar solicitud de amistad: {}", e.getMessage(), e);
            throw new ServiceException("Ocurrió un error inesperado al enviar la solicitud de amistad.", e);
        }
    }

    /**
     * Acepta una solicitud de amistad.
     * @param amistadId ID de la solicitud de amistad.
     * @return El objeto Amistad actualizado.
     * @throws ServiceException Si la solicitud no pudo ser aceptada.
     */
    public Amistad acceptFriendRequest(Integer amistadId) throws ServiceException {
        try {
            logger.info("Aceptando solicitud de amistad con ID: {}.", amistadId);
            return facade.acceptFriendRequest(amistadId);
        } catch (IOException e) {
            logger.error("Error de comunicación al aceptar solicitud de amistad ID {}: {}", amistadId, e.getMessage(), e);
            throw new ServiceException("No se pudo aceptar la solicitud de amistad. " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al aceptar solicitud de amistad ID {}: {}", amistadId, e.getMessage(), e);
            throw new ServiceException("Ocurrió un error inesperado al aceptar la solicitud de amistad.", e);
        }
    }

    /**
     * Rechaza una solicitud de amistad.
     * @param amistadId ID de la solicitud de amistad.
     * @return El objeto Amistad actualizado.
     * @throws ServiceException Si la solicitud no pudo ser rechazada.
     */
    public Amistad rejectFriendRequest(Integer amistadId) throws ServiceException {
        try {
            logger.info("Rechazando solicitud de amistad con ID: {}.", amistadId);
            return facade.rejectFriendRequest(amistadId);
        } catch (IOException e) {
            logger.error("Error de comunicación al rechazar solicitud de amistad ID {}: {}", amistadId, e.getMessage(), e);
            throw new ServiceException("No se pudo rechazar la solicitud de amistad. " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al rechazar solicitud de amistad ID {}: {}", amistadId, e.getMessage(), e);
            throw new ServiceException("Ocurrió un error inesperado al rechazar la solicitud de amistad.", e);
        }
    }

    /**
     * Obtiene las solicitudes de amistad pendientes para un usuario.
     * @param userId ID del usuario.
     * @return Lista de objetos Amistad pendientes.
     * @throws ServiceException Si ocurre un error al obtener las solicitudes.
     */
    public List<Amistad> getPendingFriendRequests(Integer userId) throws ServiceException {
        try {
            logger.info("Obteniendo solicitudes de amistad pendientes para usuario ID: {}.", userId);
            return facade.getPendingFriendRequests(userId);
        } catch (IOException e) {
            logger.error("Error de comunicación al obtener solicitudes pendientes para usuario ID {}: {}", userId, e.getMessage(), e);
            throw new ServiceException("No se pudieron obtener las solicitudes de amistad pendientes. " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al obtener solicitudes pendientes para usuario ID {}: {}", userId, e.getMessage(), e);
            throw new ServiceException("Ocurrió un error inesperado al obtener las solicitudes de amistad pendientes.", e);
        }
    }

    /**
     * Obtiene la lista de amigos de un usuario.
     * @param userId ID del usuario.
     * @return Lista de objetos Usuario que son amigos.
     * @throws ServiceException Si ocurre un error al obtener los amigos.
     */
    public List<Usuario> getFriends(Integer userId) throws ServiceException {
        try {
            logger.info("Obteniendo amigos para usuario ID: {}.", userId);
            return facade.getFriends(userId);
        } catch (IOException e) {
            logger.error("Error de comunicación al obtener amigos para usuario ID {}: {}", userId, e.getMessage(), e);
            throw new ServiceException("No se pudieron obtener los amigos. " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al obtener amigos para usuario ID {}: {}", userId, e.getMessage(), e);
            throw new ServiceException("Ocurrió un error inesperado al obtener los amigos.", e);
        }
    }
}
