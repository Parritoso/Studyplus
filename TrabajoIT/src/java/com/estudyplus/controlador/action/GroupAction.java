/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action;

import com.estudyplus.controlador.services.GroupStudyService;
import com.estudyplus.controlador.services.UserService;
import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.modelo.dto.JsonResponse;
import com.estudyplus.modelo.entitys.GrupoEstudio;
import com.estudyplus.modelo.entitys.ParticipanteGrupo;
import com.estudyplus.modelo.entitys.Usuario;
import com.estudyplus.modelo.rest.facade.EstudyRestFacadeFactory;
import com.opensymphony.xwork2.ActionSupport;
import java.util.Date;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action para gestionar grupos de estudio:
 * - Mostrar formulario de creación de grupo.
 * - Crear un nuevo grupo.
 * - (Futuro) Ver detalles de un grupo.
 * @author Parri
 */
public class GroupAction extends ActionSupport implements SessionAware {

    private static final Logger logger = LoggerFactory.getLogger(GroupAction.class);

    private GroupStudyService groupService; // Nuevo servicio para operaciones de grupo
    private UserService userService; // Para obtener el usuario y actualizar la sesión si es necesario
    private Map<String, Object> session;

    // Propiedades para la creación de grupo
    private GrupoEstudio grupo;
    private String feedbackMessage;
    private JsonResponse jsonResponse; // Para respuestas AJAX

    // Propiedad para ver un grupo específico
    private Integer groupId; // ID del grupo a ver

    public GroupAction() {
        this.groupService = new GroupStudyService();
        this.userService = new UserService(EstudyRestFacadeFactory.getInstance());
    }

    /**
     * Muestra el formulario para crear un nuevo grupo.
     * @return SUCCESS
     */
    public String showCreateGroupForm() {
        // Inicializar un objeto GrupoEstudio si es necesario para el formulario
        if (grupo == null) {
            grupo = new GrupoEstudio();
        }
        return SUCCESS;
    }

    /**
     * Procesa la creación de un nuevo grupo.
     * @return SUCCESS (redirige al dashboard si es exitoso, o INPUT si hay errores)
     */
    public String createGroup() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            addActionError(getText("group.create.error.notLoggedIn"));
            return LOGIN;
        }

        if (grupo == null || grupo.getNombre() == null || grupo.getNombre().trim().isEmpty()) {
            addFieldError("grupo.nombre", getText("group.create.error.nameRequired"));
            return INPUT;
        }
        
        if (grupo.getDescripcion() == null || grupo.getDescripcion().trim().isEmpty()) {
            addFieldError("grupo.descripcion", getText("group.create.error.descriptionRequired"));
            return INPUT;
        }

        try {
            // Establecer la fecha de creación
            grupo.setFechaCreacion(new Date());
            grupo.setCreadorUsuarioId(loggedInUser);
            
            // Crear el grupo
            GrupoEstudio createdGroup = groupService.createGroup(grupo);

            // Añadir al usuario logeado como participante y administrador del grupo
            ParticipanteGrupo participante = new ParticipanteGrupo();
            participante.setUsuarioId(loggedInUser);
            participante.setGrupoId(createdGroup);
            participante.setRol("admin"); // O un rol predefinido para el creador
            participante.setFechaUnion(new Date());

            groupService.addParticipantToGroup(participante);

            // Opcional: Recargar el usuario en sesión para que el dashboard muestre el nuevo grupo
            Usuario updatedUser = userService.getUserById(loggedInUser.getId());
            session.put("loggedInUser", updatedUser);

            addActionMessage(getText("group.create.success", new String[]{createdGroup.getNombre()}));
            logger.info("Grupo '{}' creado por usuario ID {}.", createdGroup.getNombre(), loggedInUser.getId());
            return SUCCESS; // Redirige al dashboard
        } catch (ServiceException e) {
            addActionError(getText("group.create.error") + e.getMessage());
            logger.error("ServiceException al crear grupo para usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
            return INPUT;
        } catch (Exception e) {
            addActionError(getText("group.create.unexpectedError"));
            logger.error("Excepción inesperada al crear grupo para usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
            return INPUT;
        }
    }

    /**
     * (Placeholder) Muestra los detalles de un grupo específico.
     * @return SUCCESS o ERROR
     */
    public String viewGroup() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            addActionError(getText("group.view.error.notLoggedIn"));
            return LOGIN;
        }
        if (groupId == null) {
            addActionError(getText("group.view.error.noGroupId"));
            return ERROR;
        }

        try {
            GrupoEstudio selectedGroup = groupService.getGroupById(groupId);
            if (selectedGroup == null) {
                addActionError(getText("group.view.error.groupNotFound", new String[]{String.valueOf(groupId)}));
                return ERROR;
            }
            // Aquí podrías añadir lógica para verificar si el usuario es miembro del grupo
            // y si tiene permisos para verlo.

            this.grupo = selectedGroup; // Poner el grupo en el ValueStack para el JSP

            logger.info("Visualizando grupo ID {} por usuario ID {}.", groupId, loggedInUser.getId());
            return SUCCESS; // Redirige a un JSP de detalles de grupo
        } catch (ServiceException e) {
            addActionError(getText("group.view.error") + e.getMessage());
            logger.error("ServiceException al ver grupo ID {} para usuario ID {}: {}", groupId, loggedInUser.getId(), e.getMessage(), e);
            return ERROR;
        } catch (Exception e) {
            addActionError(getText("group.view.unexpectedError"));
            logger.error("Excepción inesperada al ver grupo ID {} para usuario ID {}: {}", groupId, loggedInUser.getId(), e.getMessage(), e);
            return ERROR;
        }
    }


    // --- Getters y Setters ---
    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public GrupoEstudio getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoEstudio grupo) {
        this.grupo = grupo;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }

    public JsonResponse getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(JsonResponse jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}
