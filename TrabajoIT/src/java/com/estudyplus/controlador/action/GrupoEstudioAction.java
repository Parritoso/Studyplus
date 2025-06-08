/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action;

import com.estudyplus.controlador.services.GroupStudyService;
import com.estudyplus.controlador.services.MessageService;
import com.estudyplus.controlador.services.StudySessionService;
import com.estudyplus.controlador.services.UserService;
import com.estudyplus.controlador.services.enums.TipoTecnica;
import com.estudyplus.controlador.services.exception.GroupNotFoundException;
import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.controlador.services.exception.UserNotFoundException;
import com.estudyplus.modelo.dto.JsonResponse;
import com.estudyplus.modelo.entitys.Conversacion;
import com.estudyplus.modelo.entitys.Entrega;
import com.estudyplus.modelo.entitys.Examen;
import com.estudyplus.modelo.entitys.GrupoEstudio;
import com.estudyplus.modelo.entitys.Mensaje;
import com.estudyplus.modelo.entitys.ParticipanteGrupo;
import com.estudyplus.modelo.entitys.SesionEstudio;
import com.estudyplus.modelo.entitys.TecnicaEstudio;
import com.estudyplus.modelo.entitys.Usuario;
import com.estudyplus.modelo.rest.facade.EstudyRestFacade;
import com.estudyplus.modelo.rest.facade.EstudyRestFacadeFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Parri
 */
public class GrupoEstudioAction extends ActionSupport implements SessionAware {

    private static final Logger logger = LoggerFactory.getLogger(GrupoEstudioAction.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private GroupStudyService grupoEstudioService;
    private UserService userService;
    private MessageService messageService;
    private StudySessionService sesionEstudioService; // Para el modal de planificación
    private EstudyRestFacade estudyRestFacade;
    private Map<String, Object> sessionMap;

    // --- Propiedades para la vista del grupo ---
    private Integer groupId;
    private GrupoEstudio grupo;
    private Integer invitedUserId;
    private List<Usuario> participantes;
    private List<Mensaje> chatMessages;
    private String newMessageContent; // Para enviar mensajes
    private Integer lastMessageId; // Para polling de chat
    private String globalNotes; // Notas rápidas agregadas del grupo
    private String globalChecklist; // Checklist agregado del grupo (JSON string)
    private List<SesionEstudio> groupSessions; // Historial de sesiones del grupo

    // --- Propiedades para el modal de planificación de sesión ---
    private SesionEstudio sesion; // Objeto de sesión para el formulario del modal
    private String plannedDateStr; // Fecha planificada como String
    private String plannedTimeStr; // Hora planificada como String
    private List<TecnicaEstudio> tecnicasDisponibles;
    private List<Entrega> entregasDisponibles;
    private List<Examen> examenesDisponibles;

    // --- Propiedad para respuestas AJAX ---
    private JsonResponse jsonResponse;

    public GrupoEstudioAction() {
        this.estudyRestFacade = new EstudyRestFacade();
        this.grupoEstudioService = new GroupStudyService();
        this.userService = new UserService(estudyRestFacade);
        this.sesionEstudioService = new StudySessionService(); // Inicializar
        this.messageService = new MessageService();
        this.tecnicasDisponibles = new ArrayList();
        this.entregasDisponibles = new ArrayList();
        this.examenesDisponibles = new ArrayList();
        this.participantes = new ArrayList();
    }

    /**
     * Muestra la página de detalles del grupo.
     * Carga todos los datos iniciales: grupo, participantes, chat, notas/checklist globales, sesiones.
     * @return SUCCESS, LOGIN, NOTFOUND, ERROR
     */
    public String viewGroup() {
        this.tecnicasDisponibles = new ArrayList();
        this.entregasDisponibles = new ArrayList();
        this.examenesDisponibles = new ArrayList();
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            addActionError(getText("group.error.notLoggedIn"));
            return LOGIN;
        }
        if (groupId == null) {
            addActionError(getText("group.error.noGroupId"));
            return INPUT;
        }

        try {
            grupo = grupoEstudioService.getGroupById(groupId);
            if (grupo == null) {
                addActionError(getText("group.error.groupNotFound"));
                return "notfound";
            }

            // Verificar si el usuario es participante del grupo
            boolean isParticipant = grupoEstudioService.isUserParticipant(groupId, loggedInUser.getId());
            if (!isParticipant) {
                addActionError(getText("group.error.notParticipant"));
                return "unauthorized"; // O redirigir a una página de error
            }

            participantes = grupoEstudioService.getParticipantesByGroupId(groupId);
            if (participantes == null) participantes = Collections.emptyList();

            

            // Cargar notas y checklist globales
            globalNotes = grupoEstudioService.getGlobalNotesForGroup(groupId);
            globalChecklist = grupoEstudioService.getGlobalChecklistForGroup(groupId);

            // Cargar sesiones del grupo
            groupSessions = grupoEstudioService.getSessionsByGroupId(groupId);
            if (groupSessions == null) groupSessions = Collections.emptyList();

            // Preparar datos para el modal de planificación de sesión
            tecnicasDisponibles = estudyRestFacade.getGrupoTecnicaEstudio();
            if (tecnicasDisponibles == null) tecnicasDisponibles = Collections.emptyList();
            entregasDisponibles = estudyRestFacade.getUpcomingEntregasByUserId(loggedInUser.getId());
            if (entregasDisponibles == null) entregasDisponibles = Collections.emptyList();
            examenesDisponibles = estudyRestFacade.getUpcomingExamenesByUserId(loggedInUser.getId());
            if (examenesDisponibles == null) examenesDisponibles = Collections.emptyList();

            sesion = new SesionEstudio(); // Inicializar para el modal

        } catch (GroupNotFoundException e) {
            addActionError(getText("group.error.groupNotFound") + e.getMessage());
            logger.error("GroupNotFoundException al cargar grupo {}: {}", groupId, e.getMessage(), e);
            return "notfound";
        } catch (ServiceException | UserNotFoundException e) {
            addActionError(getText("group.error.loadGroup") + e.getMessage());
            logger.error("ServiceException al cargar grupo {}: {}", groupId, e.getMessage(), e);
            return ERROR;
        } catch (Exception e) {
            addActionError(getText("group.error.unexpectedLoad") + e.getMessage());
            logger.error("Unexpected error al cargar grupo {}: {}", groupId, e.getMessage(), e);
            return ERROR;
        }
        return SUCCESS;
    }

    /**
     * Método AJAX para enviar un mensaje al chat del grupo.
     * @return SUCCESS (con JsonResponse)
     */
    public String sendGroupMessageAjax() {
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, getText("group.error.notLoggedIn"));
            return SUCCESS;
        }
        if (groupId == null || newMessageContent == null || newMessageContent.trim().isEmpty()) {
            jsonResponse = new JsonResponse(false, getText("group.chat.emptyMessage"));
            return SUCCESS;
        }

        try {
            // Verificar que el usuario es participante del grupo
            boolean isParticipant = grupoEstudioService.isUserParticipant(groupId, loggedInUser.getId());
            if (!isParticipant) {
                jsonResponse = new JsonResponse(false, getText("group.error.notParticipant"));
                return SUCCESS;
            }

            Mensaje newMessage = new Mensaje();
            newMessage.setContenido(ESAPI.encoder().encodeForHTML(newMessageContent.trim()));
            newMessage.setFechaEnvio(new Date());
            newMessage.setEmisorId(loggedInUser);

            // El servicio se encargará de asociarlo a la conversación del grupo
            Mensaje sentMessage = grupoEstudioService.sendGroupMessage(groupId, newMessage);
            jsonResponse = new JsonResponse(true, getText("group.chat.messageSent"), sentMessage);
            logger.info("Mensaje enviado en grupo ID {} por usuario ID {}.", groupId, loggedInUser.getId());
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, getText("group.chat.sendMessageError") + e.getMessage());
            logger.error("ServiceException al enviar mensaje en grupo {}: {}", groupId, e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, getText("group.chat.sendMessageUnexpectedError"));
            logger.error("Excepción inesperada al enviar mensaje en grupo {}: {}", groupId, e.getMessage(), e);
            return SUCCESS;
        }
    }

    /**
     * Método AJAX para obtener nuevos mensajes del chat del grupo.
     * @return SUCCESS (con JsonResponse)
     */
    public String getNewGroupMessagesAjax() {
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, getText("group.error.notLoggedIn"));
            return SUCCESS;
        }
        if (groupId == null) {
            jsonResponse = new JsonResponse(false, getText("group.error.noGroupId"));
            return SUCCESS;
        }

        try {
            // Verificar que el usuario es participante del grupo
            boolean isParticipant = grupoEstudioService.isUserParticipant(groupId, loggedInUser.getId());
            if (!isParticipant) {
                jsonResponse = new JsonResponse(false, getText("group.error.notParticipant"));
                return SUCCESS;
            }

            Conversacion conversacion = grupoEstudioService.getConversacionByID(groupId);
            List<Mensaje> newMessages = messageService.getMessagesInConversation(conversacion.getId(), 0, 50);
            if (newMessages == null) newMessages = Collections.emptyList();

            if (!newMessages.isEmpty()) {
                lastMessageId = newMessages.get(newMessages.size() - 1).getId();
            }
            jsonResponse = new JsonResponse(true, getText("group.chat.messagesFetched"), newMessages);
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, getText("group.chat.fetchMessagesError") + e.getMessage());
            logger.error("ServiceException al obtener nuevos mensajes en grupo {}: {}", groupId, e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, getText("group.chat.fetchMessagesUnexpectedError"));
            logger.error("Excepción inesperada al obtener nuevos mensajes en grupo {}: {}", groupId, e.getMessage(), e);
            return SUCCESS;
        }
    }

    /**
     * Método AJAX para actualizar las notas rápidas globales del grupo.
     * @return SUCCESS (con JsonResponse)
     */
    public String updateGlobalNotesAjax() {
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, getText("group.error.notLoggedIn"));
            return SUCCESS;
        }
        if (groupId == null) {
            jsonResponse = new JsonResponse(false, getText("group.error.noGroupId"));
            return SUCCESS;
        }

        try {
            // Verificar que el usuario es participante del grupo
            boolean isParticipant = grupoEstudioService.isUserParticipant(groupId, loggedInUser.getId());
            if (!isParticipant) {
                jsonResponse = new JsonResponse(false, getText("group.error.notParticipant"));
                return SUCCESS;
            }

            grupoEstudioService.updateGlobalNotesForGroup(groupId, globalNotes);
            jsonResponse = new JsonResponse(true, getText("group.notes.updateSuccess"), globalNotes);
            logger.info("Notas globales actualizadas para grupo ID {} por usuario ID {}.", groupId, loggedInUser.getId());
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, getText("group.notes.updateError") + e.getMessage());
            logger.error("ServiceException al actualizar notas globales para grupo {}: {}", groupId, e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, getText("group.notes.updateUnexpectedError"));
            logger.error("Excepción inesperada al actualizar notas globales para grupo {}: {}", groupId, e.getMessage(), e);
            return SUCCESS;
        }
    }

    /**
     * Método AJAX para actualizar el checklist global del grupo.
     * @return SUCCESS (con JsonResponse)
     */
    public String updateGlobalChecklistAjax() {
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, getText("group.error.notLoggedIn"));
            return SUCCESS;
        }
        if (groupId == null) {
            jsonResponse = new JsonResponse(false, getText("group.error.noGroupId"));
            return SUCCESS;
        }

        try {
            // Verificar que el usuario es participante del grupo
            boolean isParticipant = grupoEstudioService.isUserParticipant(groupId, loggedInUser.getId());
            if (!isParticipant) {
                jsonResponse = new JsonResponse(false, getText("group.error.notParticipant"));
                return SUCCESS;
            }

            List<Map<String, Object>> checklistItems;
            String sanitizedChecklistJsonString; // Para almacenar la cadena JSON con los campos 'text' saneados
            // Validar que el JSON sea un array válido (opcional pero recomendado)
            try {
                // 1. Parsear la cadena JSON entrante a una lista de mapas
                checklistItems = objectMapper.readValue(globalChecklist, new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>(){});

                // 2. Iterar y sanear el campo 'text' de cada ítem
                Encoder encoder = ESAPI.encoder(); // Obtener la instancia del codificador de ESAPI
                for (Map<String, Object> item : checklistItems) {
                    if (item.containsKey("text") && item.get("text") instanceof String) {
                        String originalText = (String) item.get("text");
                        // Aplica el escape HTML solo al contenido del texto
                        String sanitizedText = encoder.encodeForHTML(originalText);
                        item.put("text", sanitizedText);
                    }
                }

                // 3. Serializar la lista modificada de nuevo a una cadena JSON
                sanitizedChecklistJsonString = objectMapper.writeValueAsString(checklistItems);
            } catch (com.fasterxml.jackson.core.JsonParseException e) {
                jsonResponse = new JsonResponse(false, getText("group.checklist.invalidJson") + e.getMessage());
                logger.warn("JSON inválido para checklist global en grupo ID {}: {}", groupId, e.getMessage());
                return SUCCESS;
            }

            grupoEstudioService.updateGlobalChecklistForGroup(groupId, globalChecklist);
            jsonResponse = new JsonResponse(true, getText("group.checklist.updateSuccess"), globalChecklist);
            logger.info("Checklist global actualizado para grupo ID {} por usuario ID {}.", groupId, loggedInUser.getId());
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, getText("group.checklist.updateError") + e.getMessage());
            logger.error("ServiceException al actualizar checklist global para grupo {}: {}", groupId, e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, getText("group.checklist.updateUnexpectedError"));
            logger.error("Excepción inesperada al actualizar checklist global para grupo {}: {}", groupId, e.getMessage(), e);
            return SUCCESS;
        }
    }

    /**
     * Prepara los datos para el modal de planificación de sesión de grupo.
     * @return SUCCESS (con JsonResponse)
     */
    public String preparePlanGroupSessionModalAjax() {
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, getText("group.error.notLoggedIn"));
            return SUCCESS;
        }
        if (groupId == null) {
            jsonResponse = new JsonResponse(false, getText("group.error.noGroupId"));
            return SUCCESS;
        }

        try {
            // Verificar que el usuario es participante del grupo
            boolean isParticipant = grupoEstudioService.isUserParticipant(groupId, loggedInUser.getId());
            if (!isParticipant) {
                jsonResponse = new JsonResponse(false, getText("group.error.notParticipant"));
                return SUCCESS;
            }

            // Cargar listas para dropdowns del modal
            tecnicasDisponibles = estudyRestFacade.getGrupoTecnicaEstudio();
            if (tecnicasDisponibles == null) tecnicasDisponibles = Collections.emptyList();
            entregasDisponibles = estudyRestFacade.getUpcomingEntregasByUserId(loggedInUser.getId());
            if (entregasDisponibles == null) entregasDisponibles = Collections.emptyList();
            examenesDisponibles = estudyRestFacade.getUpcomingExamenesByUserId(loggedInUser.getId());
            if (examenesDisponibles == null) examenesDisponibles = Collections.emptyList();

            // Devolver los datos necesarios para el modal
            Map<String, Object> modalData = new LinkedHashMap<>();
            modalData.put("tecnicasDisponibles", tecnicasDisponibles);
            modalData.put("entregasDisponibles", entregasDisponibles);
            modalData.put("examenesDisponibles", examenesDisponibles);
            modalData.put("loggedInUserId", loggedInUser.getId());
            modalData.put("currentDateTimeFormatted", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())); // Para mostrar en el modal

            jsonResponse = new JsonResponse(true, getText("group.planSession.modalReady"), modalData);
            return SUCCESS;
        } catch (ServiceException | UserNotFoundException e) {
            jsonResponse = new JsonResponse(false, getText("group.planSession.prepareError") + e.getMessage());
            logger.error("ServiceException al preparar modal de planificación de sesión para grupo {}: {}", groupId, e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, getText("group.planSession.prepareUnexpectedError"));
            logger.error("Excepción inesperada al preparar modal de planificación de sesión para grupo {}: {}", groupId, e.getMessage(), e);
            return SUCCESS;
        }
    }

    /**
     * Procesa la planificación de una nueva sesión de estudio para el grupo.
     * @return SUCCESS (con JsonResponse)
     */
    public String planGroupSession() {
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, getText("group.error.notLoggedIn"));
            return SUCCESS;
        }
        if (groupId == null || sesion == null || sesion.getTitulo() == null || sesion.getTitulo().isEmpty()) {
            jsonResponse = new JsonResponse(false, getText("group.planSession.validationError"));
            return SUCCESS;
        }

        try {
            // Verificar que el usuario es participante del grupo
            boolean isParticipant = grupoEstudioService.isUserParticipant(groupId, loggedInUser.getId());
            if (!isParticipant) {
                jsonResponse = new JsonResponse(false, getText("group.error.notParticipant"));
                return SUCCESS;
            }

            // Asignar el usuario logueado como creador y el grupo
            sesion.setUsuarioId(loggedInUser);
            GrupoEstudio currentGroup = new GrupoEstudio();
            currentGroup.setId(groupId);
            sesion.setGrupoAsociadoId(currentGroup); // Asociar al grupo

            // Procesar la fecha y hora planificada
            if (plannedDateStr != null && !plannedDateStr.isEmpty() && plannedTimeStr != null && !plannedTimeStr.isEmpty()) {
                SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String fullDateTimeStr = plannedDateStr + " " + plannedTimeStr;
                sesion.setFechaSesionPlanificada(dateTimeFormatter.parse(fullDateTimeStr));
            } else {
                jsonResponse = new JsonResponse(false, getText("group.planSession.dateRequired"));
                return SUCCESS;
            }

            // Validar IDs de entidades asociadas
            if (sesion.getTecnicaAplicadaId() != null && (sesion.getTecnicaAplicadaId().getId() == null || sesion.getTecnicaAplicadaId().getId() <= 0)) {
                sesion.setTecnicaAplicadaId(null);
            }
            if (sesion.getEntregaAsociada() != null && (sesion.getEntregaAsociada().getId() == null || sesion.getEntregaAsociada().getId() <= 0)) {
                sesion.setEntregaAsociada(null);
            }
            if (sesion.getExamenAsociado() != null && (sesion.getExamenAsociado().getId() == null || sesion.getExamenAsociado().getId() <= 0)) {
                sesion.setExamenAsociado(null);
            }

            // Las notas rápidas y checklist de una sesión planificada para un grupo pueden ser vacías inicialmente
            sesion.setNotasRapidas("");
            sesion.setChecklist("[]");

            // Crear la sesión (no se inicia inmediatamente, solo se planifica)
            SesionEstudio createdSession = sesionEstudioService.createSession(sesion, false); // Siempre false para planificación
            jsonResponse = new JsonResponse(true, getText("group.planSession.success"), createdSession);
            logger.info("Sesión de estudio planificada para grupo ID {} por usuario ID {}.", groupId, loggedInUser.getId());
            return SUCCESS;
        } catch (ParseException e) {
            jsonResponse = new JsonResponse(false, getText("group.planSession.invalidDateFormat") + e.getMessage());
            logger.warn("ParseException al planificar sesión para grupo {}: {}", groupId, e.getMessage(), e);
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, getText("group.planSession.createError") + e.getMessage());
            logger.error("ServiceException al planificar sesión para grupo {}: {}", groupId, e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, getText("group.planSession.createUnexpectedError"));
            logger.error("Excepción inesperada al planificar sesión para grupo {}: {}", groupId, e.getMessage(), e);
            return SUCCESS;
        }
    }
    
    public String getGlobalChecklistAjax() {
         Map<String, Object> dataPayload = new HashMap<>(); // Este mapa será el 'data' dentro de JsonResponse

        try {
            GrupoEstudio grupo = grupoEstudioService.getGroupById(groupId);

            if (grupo != null) {
                // Preparar los datos que irán dentro del campo 'data' de JsonResponse
                dataPayload.put("notasRapidas", grupo.getNotasRapidas() != null ? grupo.getNotasRapidas() : "");
                dataPayload.put("checklist", grupoEstudioService.getGlobalChecklistForGroup(groupId) != null ? grupoEstudioService.getGlobalChecklistForGroup(groupId) : "[]");
                
                // Crear la respuesta exitosa
                this.jsonResponse = new JsonResponse(true, "Datos de sesión cargados exitosamente.", dataPayload);
            } else {
                // Crear la respuesta de error si la sesión no se encuentra
                this.jsonResponse = new JsonResponse(false, "Sesión no encontrada para el ID: " + grupo);
            }

        } catch (Exception e) {
            // Crear una respuesta de error en caso de excepción
            String errorMessage = "Error interno del servidor al obtener datos de grupo: " + e.getMessage();
            this.jsonResponse = new JsonResponse(false, errorMessage, null); // data es null en caso de error
            addActionError(errorMessage); // Para el log de errores de Struts2
            e.printStackTrace(); // Para depuración en el servidor
        }
        return SUCCESS; // Struts2 serializará el objeto 'jsonResponse' como JSON
    }

    public String sendGroupInvitationAjax(){
        try{
            ParticipanteGrupo participante = new ParticipanteGrupo();
            participante.setUsuarioId(userService.getUserById(invitedUserId));
            participante.setFechaUnion(new Date());
            participante.setRol("participante");
            participante.setGrupoId(grupoEstudioService.getGroupById(groupId));
            grupoEstudioService.addParticipantToGroup(participante);
            jsonResponse = new JsonResponse(true, getText("group.planSession.success"));
        } catch (Exception e){
            jsonResponse = new JsonResponse(false, getText("group.planSession.success"));
        }
        return SUCCESS;
    }

    // --- Getters y Setters ---
    @Override
    public void setSession(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public GrupoEstudio getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoEstudio grupo) {
        this.grupo = grupo;
    }

    public List<Usuario> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<Usuario> participantes) {
        this.participantes = participantes;
    }

    public List<Mensaje> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(List<Mensaje> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public String getNewMessageContent() {
        return newMessageContent;
    }

    public void setNewMessageContent(String newMessageContent) {
        this.newMessageContent = newMessageContent;
    }

    public Integer getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(Integer lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public String getGlobalNotes() {
        return globalNotes;
    }

    public void setGlobalNotes(String globalNotes) {
        this.globalNotes = globalNotes;
    }

    public String getGlobalChecklist() {
        return globalChecklist;
    }

    public void setGlobalChecklist(String globalChecklist) {
        this.globalChecklist = globalChecklist;
    }

    public List<SesionEstudio> getGroupSessions() {
        return groupSessions;
    }

    public void setGroupSessions(List<SesionEstudio> groupSessions) {
        this.groupSessions = groupSessions;
    }

    public SesionEstudio getSesion() {
        return sesion;
    }

    public void setSesion(SesionEstudio sesion) {
        this.sesion = sesion;
    }

    public String getPlannedDateStr() {
        return plannedDateStr;
    }

    public void setPlannedDateStr(String plannedDateStr) {
        this.plannedDateStr = plannedDateStr;
    }

    public String getPlannedTimeStr() {
        return plannedTimeStr;
    }

    public void setPlannedTimeStr(String plannedTimeStr) {
        this.plannedTimeStr = plannedTimeStr;
    }

    public List<TecnicaEstudio> getTecnicasDisponibles() {
        return tecnicasDisponibles;
    }

    public void setTecnicasDisponibles(List<TecnicaEstudio> tecnicasDisponibles) {
        this.tecnicasDisponibles = tecnicasDisponibles;
    }

    public List<Entrega> getEntregasDisponibles() {
        return entregasDisponibles;
    }

    public void setEntregasDisponibles(List<Entrega> entregasDisponibles) {
        this.entregasDisponibles = entregasDisponibles;
    }

    public List<Examen> getExamenesDisponibles() {
        return examenesDisponibles;
    }

    public void setExamenesDisponibles(List<Examen> examenesDisponibles) {
        this.examenesDisponibles = examenesDisponibles;
    }

    public JsonResponse getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(JsonResponse jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    public Integer getInvitedUserId() {
        return invitedUserId;
    }

    public void setInvitedUserId(Integer invitedUserId) {
        this.invitedUserId = invitedUserId;
    }

    // Para el filtro de sesiones (si se implementan)
    public TipoTecnica[] getTipoTecnicaOptions() {
        return TipoTecnica.values();
    }
}
