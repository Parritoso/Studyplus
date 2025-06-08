/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.services;

import com.estudyplus.controlador.services.exception.GroupNotFoundException;
import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.modelo.entitys.Conversacion;
import com.estudyplus.modelo.entitys.GrupoEstudio;
import com.estudyplus.modelo.entitys.Mensaje;
import com.estudyplus.modelo.entitys.ParticipanteGrupo;
import com.estudyplus.modelo.entitys.SesionEstudio;
import com.estudyplus.modelo.entitys.Usuario;
import com.estudyplus.modelo.rest.facade.EstudyRestFacade;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Propósito: Gestionar grupos de estudio, miembros y eventos colaborativos.
 * @see com.estudyplus.modelo.rest.facade.EstudyRestFacade
 * @see com.estudyplus.controlador.services.UserService
 * @author Parri
 */
public class GroupStudyService {
    private static final Logger logger = LoggerFactory.getLogger(GroupStudyService.class);
    
    private EstudyRestFacade facade;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public GroupStudyService() {
        this.facade = new EstudyRestFacade();
    }

    /**
     * Crea un nuevo grupo de estudio.
     * @param grupo El objeto GrupoEstudio a crear.
     * @return El grupo creado con su ID asignado.
     * @throws ServiceException Si ocurre un error en el servicio.
     */
    public GrupoEstudio createGroup(GrupoEstudio grupo) throws ServiceException {
        try {
            return facade.createGrupoEstudio(grupo);
        } catch (IOException e) {
            throw new ServiceException("Error al crear grupo de estudio: " + e.getMessage(), e);
        }
    }

    /**
     * Añade un participante a un grupo de estudio.
     * @param participante El objeto ParticipanteGrupo a añadir.
     * @return El participante creado.
     * @throws ServiceException Si ocurre un error en el servicio.
     */
    public ParticipanteGrupo addParticipantToGroup(ParticipanteGrupo participante) throws ServiceException {
        try {
            return facade.addParticipanteGrupo(facade.createParticipanteGrupo(participante));
        } catch (IOException e) {
            throw new ServiceException("Error al añadir participante al grupo: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene un grupo de estudio por su ID.
     * @param groupId ID del grupo.
     * @return El GrupoEstudio encontrado.
     * @throws ServiceException Si ocurre un error en el servicio.
     * @throws GroupNotFoundException Si el grupo no es encontrado.
     */
    public GrupoEstudio getGroupById(int groupId) throws ServiceException, GroupNotFoundException {
        try {
            GrupoEstudio grupo = facade.getGrupoEstudioById(groupId);
            if (grupo == null) {
                throw new GroupNotFoundException("Grupo con ID " + groupId + " no encontrado.");
            }
            return grupo;
        } catch (IOException e) {
            throw new ServiceException("Error de comunicación al obtener grupo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Verifica si un usuario es participante de un grupo.
     * @param groupId ID del grupo.
     * @param userId ID del usuario.
     * @return true si el usuario es participante, false en caso contrario.
     * @throws ServiceException Si ocurre un error en el servicio.
     */
    public boolean isUserParticipant(int groupId, int userId) throws ServiceException {
        try {
            List<Usuario> participantes = getParticipantesByGroupId(groupId);
            return participantes.stream().anyMatch(p -> p.getId().equals(userId));
        } catch (Exception e) {
            throw new ServiceException("Error al verificar la participación del usuario en el grupo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene la lista de participantes de un grupo.
     * @param groupId ID del grupo.
     * @return Lista de objetos Usuario que son participantes del grupo.
     * @throws ServiceException Si ocurre un error en el servicio.
     */
    public List<Usuario> getParticipantesByGroupId(int groupId) throws ServiceException {
        try {
            // Asume que la fachada tiene un método para obtener participantes de un grupo
            // Este método en la fachada llamaría a un nuevo endpoint en GrupoEstudioClient
            return facade.getParticipantesByGroupId(groupId);
        } catch (IOException e) {
            throw new ServiceException("Error al obtener participantes del grupo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene los mensajes de chat de un grupo.
     * @param groupId ID del grupo.
     * @param offset Desplazamiento para la paginación.
     * @param limit Límite de mensajes a obtener.
     * @return Lista de objetos Mensaje.
     * @throws ServiceException Si ocurre un error en el servicio.
     */
    public List<Mensaje> getGroupMessages(int groupId, int offset, int limit) throws ServiceException {
        try {
            // Primero, obtener o crear la conversación del grupo
            Conversacion groupConversation = facade.getOrCreateGroupConversation(groupId);
            if (groupConversation == null || groupConversation.getId() == null) {
                return Collections.emptyList();
            }
            // Luego, obtener los mensajes de esa conversación
            List<Mensaje> messages = facade.getMessagesInConversation(groupConversation.getId(), offset, limit);
            // Ordenar por fecha de envío para asegurar el orden cronológico
            if (messages != null) {
                messages.sort(Comparator.comparing(Mensaje::getFechaEnvio));
            }
            return messages;
        } catch (IOException e) {
            throw new ServiceException("Error al obtener mensajes del grupo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene nuevos mensajes de chat de un grupo desde un ID de mensaje.
     * @param groupId ID del grupo.
     * @param lastMessageId Último ID de mensaje conocido por el cliente.
     * @return Lista de nuevos objetos Mensaje.
     * @throws ServiceException Si ocurre un error en el servicio.
     */
    public List<Mensaje> getNewGroupMessages(int groupId, int lastMessageId) throws ServiceException {
        try {
            Conversacion groupConversation = facade.getOrCreateGroupConversation(groupId);
            if (groupConversation == null || groupConversation.getId() == null) {
                return Collections.emptyList();
            }
            // Asume que getMessagesInConversation puede filtrar por ID > lastMessageId o que la paginación lo permite
            // Para una implementación real, necesitarías un endpoint específico que devuelva mensajes > lastMessageId
            List<Mensaje> allMessages = facade.getMessagesInConversation(groupConversation.getId(), 0, Integer.MAX_VALUE); // Obtener todos y filtrar en el servicio
            if (allMessages == null) return Collections.emptyList();

            return allMessages.stream()
                              .filter(m -> m.getId() > lastMessageId)
                              .sorted(Comparator.comparing(Mensaje::getFechaEnvio))
                              .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ServiceException("Error al obtener nuevos mensajes del grupo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Envía un mensaje al chat de un grupo.
     * @param groupId ID del grupo.
     * @param message El objeto Mensaje a enviar (con contenido, emisor, etc.).
     * @return El mensaje enviado con su ID asignado.
     * @throws ServiceException Si ocurre un error en el servicio.
     */
    public Mensaje sendGroupMessage(int groupId, Mensaje message) throws ServiceException {
        try {
            Conversacion groupConversation = facade.getOrCreateGroupConversation(groupId);
            if (groupConversation == null || groupConversation.getId() == null) {
                throw new ServiceException("No se pudo obtener o crear la conversación para el grupo.");
            }
            message.setConversacionId(groupConversation); // Asociar el mensaje a la conversación del grupo
            return facade.sendMessage(message);
        } catch (IOException e) {
            throw new ServiceException("Error al enviar mensaje al grupo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene todas las notas rápidas de las sesiones de estudio de un grupo.
     * Agrega el contenido de `notasRapidas` de todas las sesiones de grupo.
     * @param groupId ID del grupo.
     * @return Un String con todas las notas rápidas concatenadas.
     * @throws ServiceException Si ocurre un error en el servicio.
     */
    public String getGlobalNotesForGroup(int groupId) throws ServiceException {
        try {
            List<SesionEstudio> groupSessions = facade.getSessionsByGroupId(groupId);
            if (groupSessions == null || groupSessions.isEmpty()) {
                return "";
            }
            return groupSessions.stream()
                                .map(SesionEstudio::getNotasRapidas)
                                .filter(notes -> notes != null && !notes.trim().isEmpty())
                                .collect(Collectors.joining("\n\n--- Notas de otra sesión ---\n\n"));
        } catch (IOException e) {
            throw new ServiceException("Error al obtener notas globales del grupo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene el checklist global de las sesiones de estudio de un grupo.
     * Agrega los ítems de `checklist` de todas las sesiones de grupo, eliminando duplicados.
     * @param groupId ID del grupo.
     * @return Un String JSON que representa el checklist global.
     * @throws ServiceException Si ocurre un error en el servicio.
     */
    public String getGlobalChecklistForGroup(int groupId) throws ServiceException {
        ObjectMapper objectMapper = new ObjectMapper(); // O úsalo como un campo de la clase
        Set<ChecklistItem> uniqueChecklistItems = new LinkedHashSet<>(); // LinkedHashSet para mantener el orden de inserción y asegurar unicidad

        try {
            // 1. Obtener la checklist directamente del GrupoEstudio
            // Asumo que 'facade' tiene un método para obtener el GrupoEstudio por su ID
            GrupoEstudio grupoEstudio = facade.getGrupoEstudioById(groupId); 

            if (grupoEstudio != null) {
                String groupGlobalChecklistJson = grupoEstudio.getChecklist(); // Asumo que el campo se llama 'checklist' en GrupoEstudio
                logger.debug("groupGlobalChecklistJson: "+groupGlobalChecklistJson);
                if (groupGlobalChecklistJson != null && !groupGlobalChecklistJson.trim().isEmpty()) {
                    try {
                        List<ChecklistItem> groupInitialChecklist = objectMapper.readValue(groupGlobalChecklistJson, new TypeReference<List<ChecklistItem>>() {});
                        uniqueChecklistItems.addAll(groupInitialChecklist);
                    } catch (IOException e) {
                        logger.error("[getGlobalChecklistForGroup] Error parsing global checklist JSON for group " + groupId + ": " + e.getMessage());
                    }
                }
            } else {
                // Si el grupo no existe, podrías querer lanzar una excepción o simplemente devolver una lista vacía
                // Para este caso, continuaremos para que si al menos hay sesiones se procesen
                logger.error("[getGlobalChecklistForGroup] No se encontró el GrupoEstudio con ID: " + groupId);
            }

            // 2. Procesar las checklists de las sesiones de estudio (lógica existente)
            List<SesionEstudio> groupSessions = facade.getSessionsByGroupId(groupId);
            if (groupSessions != null && !groupSessions.isEmpty()) {
                for (SesionEstudio session : groupSessions) {
                    String checklistJson = session.getChecklist();
                    if (checklistJson != null && !checklistJson.trim().isEmpty()) {
                        try {
                            List<ChecklistItem> sessionChecklist = objectMapper.readValue(checklistJson, new TypeReference<List<ChecklistItem>>() {});
                            uniqueChecklistItems.addAll(sessionChecklist);
                        } catch (IOException e) {
                            logger.error("[getGlobalChecklistForGroup] Error parsing checklist JSON for session " + session.getId() + ": " + e.getMessage());
                        }
                    }
                }
            }

            logger.info("[getGlobalChecklistForGroup] uniqueChecklistItems: {}",uniqueChecklistItems);
            // 3. Devolver la combinación de ambas listas como JSON
            return objectMapper.writeValueAsString(uniqueChecklistItems);

        } catch (IOException e) {
            throw new ServiceException("Error al obtener checklist global del grupo: " + e.getMessage(), e);
        } catch (Exception e) { // Captura cualquier otra excepción del facade.getGrupoEstudioById()
            throw new ServiceException("Error inesperado al obtener datos del grupo o sesiones: " + e.getMessage(), e);
        }
    }
    
    /**
     * Actualiza las notas rápidas globales de un grupo.
     * NOTA: Esta implementación simplificada *no* actualiza las notas en las sesiones individuales,
     * sino que asume que el grupo tiene un campo `globalNotes` en su entidad o un mecanismo de almacenamiento.
     * Para una gestión más robusta, se podría considerar una entidad `GrupoNota` o `GrupoChecklist`.
     * Por ahora, actualizamos el campo `notasRapidas` del propio `GrupoEstudio`.
     * @param groupId ID del grupo.
     * @param newNotes El nuevo contenido de las notas.
     * @throws ServiceException Si ocurre un error.
     */
    public void updateGlobalNotesForGroup(int groupId, String newNotes) throws ServiceException {
        try {
            GrupoEstudio grupo = getGroupById(groupId); // Obtener el grupo
            grupo.setNotasRapidas(newNotes); // Asumiendo que GrupoEstudio tiene este campo
            facade.updateGrupoEstudio(grupo); // Actualizar el grupo en la fachada/API
        } catch (GroupNotFoundException e) {
            throw new ServiceException("Grupo no encontrado para actualizar notas: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new ServiceException("Error al actualizar notas globales del grupo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Actualiza el checklist global de un grupo.
     * NOTA: Similar a `updateGlobalNotesForGroup`, esta implementación asume
     * que el grupo tiene un campo `globalChecklist` en su entidad.
     * @param groupId ID del grupo.
     * @param newChecklistJson El nuevo contenido del checklist como JSON string.
     * @throws ServiceException Si ocurre un error.
     */
    public void updateGlobalChecklistForGroup(int groupId, String newChecklistJson) throws ServiceException {
        try {
            GrupoEstudio grupo = getGroupById(groupId); // Obtener el grupo
            grupo.setChecklist(newChecklistJson); // Asumiendo que GrupoEstudio tiene este campo
            facade.updateGrupoEstudio(grupo); // Actualizar el grupo en la fachada/API
        } catch (GroupNotFoundException e) {
            throw new ServiceException("Grupo no encontrado para actualizar checklist: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new ServiceException("Error al actualizar checklist global del grupo: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene las sesiones de estudio asociadas a un grupo.
     * @param groupId ID del grupo.
     * @return Lista de SesionEstudio.
     * @throws ServiceException Si ocurre un error en el servicio.
     */
    public List<SesionEstudio> getSessionsByGroupId(int groupId) throws ServiceException {
        try {
            return facade.getSessionsByGroupId(groupId);
        } catch (IOException e) {
            throw new ServiceException("Error al obtener sesiones del grupo: " + e.getMessage(), e);
        }
    }

    public Conversacion getConversacionByID(Integer groupId) {
        try {
            return facade.getconversationByGroupId(groupId);
        } catch (IOException e) {
            throw new ServiceException("Error al obtener conversacion del grupo: " + e.getMessage(), e);
        }
    }
    
    // DTO auxiliar para el checklist, para usar con LinkedHashSet
    // Necesita equals y hashCode para que LinkedHashSet funcione correctamente
    private static class ChecklistItem {
        public String text;
        public boolean completed;

        // Constructor por defecto para Jackson
        public ChecklistItem() {}

        public ChecklistItem(String text, boolean completed) {
            this.text = text;
            this.completed = completed;
        }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChecklistItem that = (ChecklistItem) o;
            return text.equalsIgnoreCase(that.text); // Comparar solo por texto para unicidad
        }

        @Override
        public int hashCode() {
            return text.toLowerCase().hashCode();
        }
    }
    
    /**
     * Añade un participante a un grupo.
     * @param userId
     * @param groupId 
     */
    public static void joinGroup(int userId, int groupId){
        
    }
    
    /**
     * Remueve un participante.
     * @param userId
     * @param groupId 
     */
    public static void leaveGroup(int userId, int groupId){
        
    }
    
    /**
     * Programa una sesión para el grupo.
     * @param groupId
     * @param session 
     */
    public static void scheduleGroupSession(int groupId, SesionEstudio session){
        
    }
    
    /**
     * Lista los miembros del grupo.
     * @param groupId 
     */
    public static void getGroupMembers(int groupId){
        
    }
    
    /**
     * (Si se implementa chat directo).
     * @param groupId 
     */
    public static void getGroupChatHistory(int groupId){
        
    }
}
