/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action;

import com.estudyplus.controlador.services.StudySessionService;
import com.estudyplus.controlador.services.StudySessionService.SessionCompletionData;
import com.estudyplus.controlador.services.UserService;
import com.estudyplus.controlador.services.enums.SessionState;
import com.estudyplus.controlador.services.enums.TipoTecnica;
import com.estudyplus.controlador.services.exception.InvalidSessionStateException;
import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.controlador.services.exception.SessionNotFoundException;
import com.estudyplus.controlador.services.exception.UserNotFoundException;
import com.estudyplus.modelo.dto.JsonResponse;
import com.estudyplus.modelo.entitys.Entrega;
import com.estudyplus.modelo.entitys.Examen;
import com.estudyplus.modelo.entitys.SesionEstudio;
import com.estudyplus.modelo.entitys.TecnicaEstudio;
import com.estudyplus.modelo.entitys.Usuario;
import com.estudyplus.modelo.rest.facade.EstudyRestFacade;
import com.estudyplus.modelo.rest.facade.EstudyRestFacadeFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.opensymphony.xwork2.ActionSupport;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apache.struts2.interceptor.SessionAware;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Parri
 */
public class SesionEstudioAction extends ActionSupport implements SessionAware {

    private static final Logger logger = LoggerFactory.getLogger(SesionEstudioAction.class);
    private static final ObjectMapper objectMapper = new ObjectMapper(); // Para JSON

    private StudySessionService sesionEstudioService;
    private UserService userService; // Para obtener usuario completo en el createSession
    private EstudyRestFacade estudyRestFacade;
    private Map<String, Object> sessionMap;

    // --- Propiedades para la vista de listado y filtrado ---
    private List<SesionEstudio> sesiones;
    private List<TecnicaEstudio> tecnicasDisponibles; // Para el filtro y el formulario
    private List<Entrega> entregasDisponibles;     // Para el formulario de asociación
    private List<Examen> examenesDisponibles;       // Para el formulario de asociación

    private String filterType = "all"; // "upcoming" o "history"
    private Integer filterTecnicaId; // Filtro por técnica específica
    private String filterTipoTecnica; // Filtro por tipo de técnica (INDIVIDUAL/GRUPO)

    // JSON para pasar datos de sesiones al calendario o JS
    private String sesionesJson;

    // --- Propiedades para el formulario de crear/editar ---
    private Integer sesionId; // Para editar una sesión existente
    private SesionEstudio sesion; // El objeto de sesión para el formulario
    private Boolean startImmediately; // Para la creación: si se inicia ahora o se programa
    private String plannedDateStr; // Fecha y hora planificada como String
    private String plannedTimeStr; // Hora planificada como String (para combinar con plannedDateStr)
    private Map<String, String> filterTypeOptions;
    
    // --- NUEVAS PROPIEDADES PARA NOTAS Y CHECKLIST ---
    private String notasRapidas; // Contenido de las notas rápidas
    private String checklist;    // Contenido del checklist (JSON string)
    
    // --- Propiedades para la finalización de sesión ---
    private String notes;
    private Integer focusRating;
    private String interruptionDetails;
    private String outcome; // "completed" o "aborted"
    
    // --- Propiedad para respuestas AJAX ---
    private JsonResponse jsonResponse;


    public SesionEstudioAction() {
        this.estudyRestFacade = EstudyRestFacadeFactory.getInstance();
        this.sesionEstudioService = new StudySessionService();
        this.userService = new UserService(EstudyRestFacadeFactory.getInstance()); // Asegúrate de que UserService tenga un constructor por defecto
        this.filterTypeOptions = getFilterTypeOptions();
    }
    
    public Map<String, String> getFilterTypeOptions() {
        if (filterTypeOptions == null) {
            filterTypeOptions = new LinkedHashMap<>();
            filterTypeOptions.put("upcoming", "Próximas Sesiones");
            filterTypeOptions.put("history", "Historial de Sesiones");
        }
        return filterTypeOptions;
    }

    /**
     * Muestra la página principal de gestión de sesiones con listas filtradas.
     * @return SUCCESS o LOGIN/ERROR
     */
    public String execute() {
        sesiones = new ArrayList();
        tecnicasDisponibles = new ArrayList();
        entregasDisponibles = new ArrayList();
        examenesDisponibles = new ArrayList();
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            addActionError("Debes iniciar sesión para ver tus sesiones de estudio.");
            return LOGIN;
        }

        try {
            // Cargar todas las técnicas de estudio disponibles para el filtro y el formulario
            tecnicasDisponibles = estudyRestFacade.getAllTecnicasEstudio(); // Asume método en la fachada
            if (tecnicasDisponibles == null) tecnicasDisponibles = Collections.emptyList();

            // Cargar entregas y exámenes disponibles para el formulario (solo próximas)
            entregasDisponibles = estudyRestFacade.getUpcomingEntregasByUserId(loggedInUser.getId());
            if (entregasDisponibles == null) entregasDisponibles = Collections.emptyList();
            examenesDisponibles = estudyRestFacade.getUpcomingExamenesByUserId(loggedInUser.getId());
            if (examenesDisponibles == null) examenesDisponibles = Collections.emptyList();


            // Obtener sesiones según el filtro
            if ("history".equalsIgnoreCase(filterType)) {
                sesiones = estudyRestFacade.getHistorySesionesEstudioByUserId(loggedInUser.getId()); // Asume método en la fachada
            } else if ("upcoming".equalsIgnoreCase(filterType)) { // Por defecto o "upcoming"
                sesiones = estudyRestFacade.getUpcomingSesionesEstudioByUserId(loggedInUser.getId()); // Asume método en la fachada
            }  else { 
                sesiones = estudyRestFacade.getSesionesEstudioByUserId(loggedInUser.getId()); // Obtener TODAS las sesiones
            }
            if (sesiones == null) sesiones = Collections.emptyList();

            // Aplicar filtros adicionales (por técnica o tipo de técnica)
            sesiones = filterSessions(sesiones, filterTecnicaId, filterTipoTecnica);

            // Preparar JSON de sesiones para el calendario en JSP (si es necesario)
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            // Formato ISO 8601
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")); 
            sesionesJson = mapper.writeValueAsString(sesiones);


        } catch (UserNotFoundException e) {
            addActionError("Error: Usuario no encontrado. " + e.getMessage());
            logger.error("Usuario no encontrado: {}", e.getMessage(), e);
            return ERROR;
        } catch (ServiceException e) {
            addActionError("Error del servicio al cargar sesiones: " + e.getMessage());
            logger.error("Service Exception: {}", e.getMessage(), e);
            return ERROR;
        } catch (Exception e) {
            addActionError("Ocurrió un error inesperado al cargar las sesiones: " + e.getMessage());
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return ERROR;
        }

        return SUCCESS;
    }

    /**
     * Muestra el formulario para crear una nueva sesión de estudio.
     * También carga las técnicas, entregas y exámenes disponibles.
     * @return SUCCESS o LOGIN/ERROR
     */
    public String showCreateForm() {
        sesiones = new ArrayList();
        tecnicasDisponibles = new ArrayList();
        entregasDisponibles = new ArrayList();
        examenesDisponibles = new ArrayList();
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            addActionError("Debes iniciar sesión para crear una sesión de estudio.");
            return LOGIN;
        }
        sesion = new SesionEstudio(); // Nueva instancia para el formulario

        try {
            // Cargar listas para dropdowns
            tecnicasDisponibles = estudyRestFacade.getAllTecnicasEstudio();
            if (tecnicasDisponibles == null) tecnicasDisponibles = Collections.emptyList();

            entregasDisponibles = estudyRestFacade.getUpcomingEntregasByUserId(loggedInUser.getId()); // Asume este método
            if (entregasDisponibles == null) entregasDisponibles = Collections.emptyList();

            examenesDisponibles = estudyRestFacade.getUpcomingExamenesByUserId(loggedInUser.getId()); // Asume este método
            if (examenesDisponibles == null) examenesDisponibles = Collections.emptyList();

        } catch (ServiceException | UserNotFoundException e) {
            addActionError("Error al cargar datos para el formulario: " + e.getMessage());
            logger.error("Error cargando datos para create form: {}", e.getMessage(), e);
            return ERROR;
        } catch (Exception e) {
            addActionError("Ocurrió un error inesperado al preparar el formulario: " + e.getMessage());
            logger.error("Unexpected error preparing create form: {}", e.getMessage(), e);
            return ERROR;
        }
        return SUCCESS;
    }

    /**
     * Procesa el envío del formulario para crear una nueva sesión.
     * @return SUCCESS (redirecciona a la lista) o INPUT/ERROR
     */
    public String create() {
        sesiones = new ArrayList();
        tecnicasDisponibles = new ArrayList();
        entregasDisponibles = new ArrayList();
        examenesDisponibles = new ArrayList();
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            logger.debug("Debes iniciar sesión para crear una sesión de estudio.");
            addActionError("Debes iniciar sesión para crear una sesión de estudio.");
            return LOGIN;
        }

        if (sesion == null) {
            logger.debug("Datos de sesión no proporcionados.");
            addActionError("Datos de sesión no proporcionados.");
            return INPUT;
        }

        try {
            sesion.setUsuarioId(loggedInUser); // Asignar el usuario logueado

            // Procesar la fecha y hora planificada si no se inicia inmediatamente
            if (startImmediately == null || !startImmediately) {
                if (plannedDateStr != null && !plannedDateStr.isEmpty() && plannedTimeStr != null && !plannedTimeStr.isEmpty()) {
                    SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String fullDateTimeStr = plannedDateStr + " " + plannedTimeStr;
                    sesion.setFechaSesionPlanificada(dateTimeFormatter.parse(fullDateTimeStr));
                } else {
                    logger.debug("La fecha y hora planificada son obligatorias si la sesión no se inicia inmediatamente.");
                    addActionError("La fecha y hora planificada son obligatorias si la sesión no se inicia inmediatamente.");
                    return INPUT;
                }
            } else {
                sesion.setFechaSesionPlanificada(null); // Si se inicia inmediatamente, no hay fecha planificada
            }

            // Validar si la técnica de estudio viene con ID
            if (sesion.getTecnicaAplicadaId() != null && sesion.getTecnicaAplicadaId().getId() != null && sesion.getTecnicaAplicadaId().getId() <= 0) {
                 sesion.setTecnicaAplicadaId(null); // No técnica si ID es 0 o inválido
            }
            // Validar si la entrega asociada viene con ID
//            logger.debug("sesion.getEntregaAsociada() != null: "+(sesion.getEntregaAsociada() != null)+"\nsesion.getEntregaAsociada().getId() != null: "+(sesion.getEntregaAsociada().getId() != null)+"\nsesion.getEntregaAsociada().getId(): "+(sesion.getEntregaAsociada().getId()));
            if (sesion.getEntregaAsociada() != null && sesion.getEntregaAsociada().getId() != null && sesion.getEntregaAsociada().getId() <= 0) {
                 sesion.setEntregaAsociada(null); // No entrega si ID es 0 o inválido
            }
            // Validar si el examen asociado viene con ID
            if (sesion.getExamenAsociado() != null && sesion.getExamenAsociado().getId() != null && sesion.getExamenAsociado().getId() <= 0) {
                 sesion.setExamenAsociado(null); // No examen si ID es 0 o inválido
            }
            
            // --- Establecer las nuevas propiedades ---
            sesion.setNotasRapidas(ESAPI.encoder().encodeForHTML(sesion.getNotasRapidas() != null ? sesion.getNotasRapidas() : ""));
            sesion.setChecklist(ESAPI.encoder().encodeForHTML(sesion.getChecklist() != null ? sesion.getChecklist() : "[]"));
            sesion.setTitulo(ESAPI.encoder().encodeForHTML(sesion.getTitulo()));
            sesion.setDescripcion(ESAPI.encoder().encodeForHTML(sesion.getDescripcion()));


            sesionId = sesionEstudioService.createSession(sesion, startImmediately != null && startImmediately).getId();
            addActionMessage("Sesión de estudio creada con éxito.");
            logger.debug("Sesión de estudio creada con éxito.");
            if(startImmediately){
                return "startActiveSession";
            }
            return SUCCESS; // Redireccionar a la lista
        } catch (IllegalArgumentException | UserNotFoundException | ParseException e) {
            addActionError("Error de validación: " + e.getMessage());
            logger.warn("Validation/Parse error creating session: {}", e.getMessage(), e);
            // Recargar datos para el formulario en caso de error de INPUT
            try {
                // *** CAMBIO AQUÍ: Recargar solo técnicas individuales en caso de error ***
                tecnicasDisponibles = sesionEstudioService.getIndividualTecnicasEstudio();
                //tecnicasDisponibles = estudyRestFacade.getAllTecnicasEstudio();
                entregasDisponibles = estudyRestFacade.getUpcomingEntregasByUserId(loggedInUser.getId());
                examenesDisponibles = estudyRestFacade.getUpcomingExamenesByUserId(loggedInUser.getId());
            } catch (ServiceException | IOException | UserNotFoundException innerE) {
                logger.error("Error recargando datos para el formulario: {}", innerE.getMessage());
            }
            return INPUT;
        } catch (ServiceException e) {
            try {
                addActionError("Error del servicio al crear sesión: " + e.getMessage());
                logger.error("Service error creating session: {}", e.getMessage(), e);
                tecnicasDisponibles = estudyRestFacade.getAllTecnicasEstudio();
                if (tecnicasDisponibles == null) tecnicasDisponibles = Collections.emptyList();
                
                entregasDisponibles = estudyRestFacade.getUpcomingEntregasByUserId(loggedInUser.getId()); // Asume este método
                if (entregasDisponibles == null) entregasDisponibles = Collections.emptyList();
                
                examenesDisponibles = estudyRestFacade.getUpcomingExamenesByUserId(loggedInUser.getId()); // Asume este método
                if (examenesDisponibles == null) examenesDisponibles = Collections.emptyList();
                return ERROR;
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(SesionEstudioAction.class.getName()).log(Level.SEVERE, null, ex);
                return ERROR;
            }
        } catch (Exception e) {
            addActionError("Ocurrió un error inesperado al crear la sesión: " + e.getMessage());
            logger.error("Unexpected error creating session: {}", e.getMessage(), e);
            return ERROR;
        }
    }

    /**
     * Muestra el formulario para editar una sesión existente.
     * @return SUCCESS o LOGIN/ERROR/NOTFOUND
     */
    public String showEditForm() {
        sesiones = new ArrayList();
        tecnicasDisponibles = new ArrayList();
        entregasDisponibles = new ArrayList();
        examenesDisponibles = new ArrayList();
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            addActionError("Debes iniciar sesión para editar una sesión de estudio.");
            return LOGIN;
        }
        if (sesionId == null) {
            addActionError("ID de sesión no proporcionado para edición.");
            return INPUT;
        }

        try {
            sesion = sesionEstudioService.getSessionById(sesionId);
            if (sesion == null || !sesion.getUsuarioId().getId().equals(loggedInUser.getId())) {
                addActionError("Sesión no encontrada o no tienes permiso para editarla.");
                return "notfound"; // Resultado personalizado para "no encontrado"
            }
            
            // *** CAMBIO AQUÍ: Obtener solo técnicas individuales ***
            tecnicasDisponibles = sesionEstudioService.getIndividualTecnicasEstudio();
            if (tecnicasDisponibles == null) tecnicasDisponibles = Collections.emptyList();
            
            /*// Cargar listas para dropdowns
            tecnicasDisponibles = estudyRestFacade.getAllTecnicasEstudio();
            if (tecnicasDisponibles == null) tecnicasDisponibles = Collections.emptyList();*/

            entregasDisponibles = estudyRestFacade.getUpcomingEntregasByUserId(loggedInUser.getId()); // Asume este método
            if (entregasDisponibles == null) entregasDisponibles = Collections.emptyList();

            examenesDisponibles = estudyRestFacade.getUpcomingExamenesByUserId(loggedInUser.getId()); // Asume este método
            if (examenesDisponibles == null) examenesDisponibles = Collections.emptyList();

            // Preparar las strings de fecha y hora para el formulario
            if (sesion.getFechaSesionPlanificada() != null) {
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
                plannedDateStr = dateFormatter.format(sesion.getFechaSesionPlanificada());
                plannedTimeStr = timeFormatter.format(sesion.getFechaSesionPlanificada());
            }
            
            // --- Cargar las nuevas propiedades para edición ---
            // SANEAR NOTAS RÁPIDAS Y CHECKLIST PARA INYECCIÓN EN JAVASCRIPT EN JSP
            if (sesion.getNotasRapidas() != null) {
                sesion.setNotasRapidas(ESAPI.encoder().encodeForJavaScript(sesion.getNotasRapidas()));
            }
            if (sesion.getChecklist() != null) {
                sesion.setChecklist(ESAPI.encoder().encodeForJavaScript(sesion.getChecklist()));
            }

        } catch (ServiceException | UserNotFoundException e) {
            addActionError("Error al cargar la sesión para edición: " + e.getMessage());
            logger.error("Error loading session for edit: {}", e.getMessage(), e);
            return ERROR;
        } catch (Exception e) {
            addActionError("Ocurrió un error inesperado al cargar la sesión para edición: " + e.getMessage());
            logger.error("Unexpected error loading session for edit: {}", e.getMessage(), e);
            return ERROR;
        }
        return SUCCESS;
    }

    /**
     * Procesa el envío del formulario para actualizar una sesión existente.
     * @return SUCCESS (redirecciona a la lista) o INPUT/ERROR
     */
    public String update() {
        sesiones = new ArrayList();
        tecnicasDisponibles = new ArrayList();
        entregasDisponibles = new ArrayList();
        examenesDisponibles = new ArrayList();
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            addActionError("Debes iniciar sesión para actualizar una sesión de estudio.");
            return LOGIN;
        }
        if (sesion == null || sesion.getId() == null) {
            addActionError("Datos de sesión o ID no proporcionados para actualizar.");
            return INPUT;
        }

        try {
            SesionEstudio existingSession = sesionEstudioService.getSessionById(sesion.getId());
            if (existingSession == null || !existingSession.getUsuarioId().getId().equals(loggedInUser.getId())) {
                addActionError("Sesión no encontrada o no tienes permiso para editarla.");
                return "notfound";
            }

            // Actualizar solo los campos modificables desde el formulario
            existingSession.setTitulo(ESAPI.encoder().encodeForHTML(sesion.getTitulo()));
            existingSession.setDescripcion(ESAPI.encoder().encodeForHTML(sesion.getDescripcion()));
            existingSession.setDuracionPlanificadaMinutos(sesion.getDuracionPlanificadaMinutos());
            existingSession.setTecnicaAplicadaId(sesion.getTecnicaAplicadaId() != null && sesion.getTecnicaAplicadaId().getId() != null && sesion.getTecnicaAplicadaId().getId() > 0 ? sesion.getTecnicaAplicadaId() : null);
            existingSession.setEntregaAsociada(sesion.getEntregaAsociada() != null && sesion.getEntregaAsociada().getId() != null && sesion.getEntregaAsociada().getId() > 0 ? sesion.getEntregaAsociada() : null);
            existingSession.setExamenAsociado(sesion.getExamenAsociado() != null && sesion.getExamenAsociado().getId() != null && sesion.getExamenAsociado().getId() > 0 ? sesion.getExamenAsociado() : null);
            
            // La fecha planificada solo se actualiza si la sesión no está activa/pausada/completada/abortada
            // y si se ha proporcionado una nueva fecha.
            // Si la sesión está PLANNED, se puede cambiar la fecha
            if (SessionState.PLANNED.name().equals(existingSession.getEstadoString())) {
                if (plannedDateStr != null && !plannedDateStr.isEmpty() && plannedTimeStr != null && !plannedTimeStr.isEmpty()) {
                    SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String fullDateTimeStr = plannedDateStr + " " + plannedTimeStr;
                    existingSession.setFechaSesionPlanificada(dateTimeFormatter.parse(fullDateTimeStr));
                } else {
                    // Si estaba planificada y borran fecha, puede ser un error o se quiere desprogramar
                    // Para este caso, lanzamos error o se define otro comportamiento
                    addActionError("La fecha y hora planificada no pueden estar vacías si la sesión está programada.");
                    return INPUT;
                }
            }
            /*// Si la sesión ya está en un estado de ejecución o finalizado, no se debería permitir cambiar fecha planificada.
            // Para la edición no se permite cambiar a "iniciar inmediatamente".
            existingSession.setNotasRapidas(notasRapidas);
            existingSession.setChecklist(checklist);*/
            // SANEAR NOTAS RÁPIDAS Y CHECKLIST AL ACTUALIZAR DESDE EL FORMULARIO (si se usan)
            // Si el formulario de edición no tiene campos para notas rápidas/checklist,
            // estos valores no se enviarán y se mantendrán los existentes en la DB.
            // Si los tuviera, se actualizarían con sesion.getNotasRapidas() y sesion.getChecklist()
            // y se sanearían aquí.
            // Por ahora, asumimos que no se actualizan desde este formulario, sino vía AJAX.

            if (SessionState.PLANNED.name().equals(existingSession.getEstadoString())) {
                if (plannedDateStr != null && !plannedDateStr.isEmpty() && plannedTimeStr != null && !plannedTimeStr.isEmpty()) {
                    SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String fullDateTimeStr = plannedDateStr + " " + plannedTimeStr;
                    existingSession.setFechaSesionPlanificada(dateTimeFormatter.parse(fullDateTimeStr));
                } else {
                    addActionError("La fecha y hora planificada no pueden estar vacías si la sesión está programada.");
                    return INPUT;
                }
            }
            
            sesionEstudioService.updateSesionEstudio(existingSession); // Actualizar en la fachada
            addActionMessage("Sesión de estudio actualizada con éxito.");
            return SUCCESS; // Redireccionar a la lista
        } catch (IllegalArgumentException | UserNotFoundException | ParseException e) {
            addActionError("Error de validación al actualizar sesión: " + e.getMessage());
            logger.warn("Validation/Parse error updating session: {}", e.getMessage(), e);
            // Recargar datos para el formulario en caso de error de INPUT
            try {
                tecnicasDisponibles = estudyRestFacade.getAllTecnicasEstudio();
                entregasDisponibles = estudyRestFacade.getUpcomingEntregasByUserId(loggedInUser.getId());
                examenesDisponibles = estudyRestFacade.getUpcomingExamenesByUserId(loggedInUser.getId());
            } catch (ServiceException | IOException | UserNotFoundException innerE) {
                logger.error("Error recargando datos para el formulario: {}", innerE.getMessage());
            }
            return INPUT;
        } catch (ServiceException e) {
            addActionError("Error del servicio al actualizar sesión: " + e.getMessage());
            logger.error("Service error updating session: {}", e.getMessage(), e);
            return ERROR;
        } catch (Exception e) {
            addActionError("Ocurrió un error inesperado al actualizar la sesión: " + e.getMessage());
            logger.error("Unexpected error updating session: {}", e.getMessage(), e);
            return ERROR;
        }
    }
    
    /**
     * Elimina una sesión de estudio.
     * @return SUCCESS o LOGIN/ERROR/NOTFOUND
     */
    public String delete() {
        sesiones = new ArrayList();
        tecnicasDisponibles = new ArrayList();
        entregasDisponibles = new ArrayList();
        examenesDisponibles = new ArrayList();
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            addActionError("Debes iniciar sesión para eliminar una sesión de estudio.");
            return LOGIN;
        }
        if (sesionId == null) {
            addActionError("ID de sesión no proporcionado para eliminación.");
            return INPUT;
        }

        try {
            SesionEstudio sessionToDelete = sesionEstudioService.getSessionById(sesionId);
            if (sessionToDelete == null || !sessionToDelete.getUsuarioId().getId().equals(loggedInUser.getId())) {
                addActionError("Sesión no encontrada o no tienes permiso para eliminarla.");
                return "notfound";
            }
            
            sesionEstudioService.deleteSession(sesionId);
            addActionMessage("Sesión de estudio eliminada con éxito.");
            return SUCCESS; // Redireccionar a la lista
        } catch (ServiceException e) {
            addActionError("Error del servicio al eliminar sesión: " + e.getMessage());
            logger.error("Service error deleting session: {}", e.getMessage(), e);
            return ERROR;
        } catch (Exception e) {
            addActionError("Ocurrió un error inesperado al eliminar la sesión: " + e.getMessage());
            logger.error("Unexpected error deleting session: {}", e.getMessage(), e);
            return ERROR;
        }
    }

    /**
     * Inicia o reanuda una sesión de estudio y redirige a la página de sesión activa.
     * @return "activeSessionPage" si es exitoso, o LOGIN/ERROR/NOTFOUND/INVALID_STATE
     */
    public String startSession() {
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            addActionError("Debes iniciar sesión para controlar una sesión de estudio.");
            return LOGIN;
        }
        if (sesionId == null) {
            addActionError("ID de sesión no proporcionado para iniciar/reanudar.");
            return INPUT;
        }

        try {
            sesion = sesionEstudioService.startSession(sesionId); // Llama al servicio para iniciar/reanudar
            // Verificar que la sesión pertenece al usuario logueado
            if (!sesion.getUsuarioId().getId().equals(loggedInUser.getId())) {
                logger.warn("Usuario con ID {} no tiene permiso para iniciar/reanudar la session {}",sesion.getUsuarioId().getId(),sesionId);
                addActionError("No tienes permiso para iniciar/reanudar esta sesión.");
                return "notfound";
            }
            addActionMessage("Sesión iniciada/reanudada con éxito.");
            
            // *** APLICAR ESCAPE JAVASCRIPT A LAS NOTAS RÁPIDAS Y CHECKLIST ANTES DE ENVIAR AL JSP ***
            if (sesion.getNotasRapidas() != null) {
                sesion.setNotasRapidas(ESAPI.encoder().encodeForJavaScript(sesion.getNotasRapidas()));
            } else {
                sesion.setNotasRapidas(""); // Asegurarse de que no sea null para JS
            }
            if (sesion.getChecklist() != null) {
                sesion.setChecklist(ESAPI.encoder().encodeForJavaScript(sesion.getChecklist()));
            } else {
                sesion.setChecklist("[]"); // Asegurarse de que sea un JSON vacío para JS
            }
            
            return "activeSessionPage"; // Redirige a la nueva página de sesión activa
        } catch (SessionNotFoundException e) {
            addActionError("Sesión no encontrada: " + e.getMessage());
            logger.error("Session not found for start: {}", e.getMessage(), e);
            return "notfound";
        } catch (InvalidSessionStateException e) {
            addActionError("Estado de sesión inválido para iniciar/reanudar: " + e.getMessage());
            logger.warn("Invalid state for start: {}", e.getMessage(), e);
            return "invalidState"; // Podrías definir un resultado específico para esto
        } catch (ServiceException e) {
            addActionError("Error del servicio al iniciar/reanudar sesión: " + e.getMessage());
            logger.error("Service error starting/resuming session: {}", e.getMessage(), e);
            return ERROR;
        } catch (Exception e) {
            addActionError("Ocurrió un error inesperado al iniciar/reanudar la sesión: " + e.getMessage());
            logger.error("Unexpected error starting/resuming session: {}", e.getMessage(), e);
            return ERROR;
        }
    }

    /**
     * Pausa una sesión de estudio.
     * @return SUCCESS (redirige a la lista o a la misma página activa) o LOGIN/ERROR/NOTFOUND/INVALID_STATE
     */
    public String pauseSession() {
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            addActionError("Debes iniciar sesión para controlar una sesión de estudio.");
            return LOGIN;
        }
        if (sesionId == null) {
            addActionError("ID de sesión no proporcionado para pausar.");
            return INPUT;
        }

        try {
            sesion = sesionEstudioService.pauseSession(sesionId);
            if (!sesion.getUsuarioId().getId().equals(loggedInUser.getId())) {
                addActionError("No tienes permiso para pausar esta sesión.");
                return "notfound";
            }
            addActionMessage("Sesión pausada con éxito.");
            return SUCCESS; // Podrías redirigir de vuelta a la página activa para que muestre el estado pausado
        } catch (SessionNotFoundException e) {
            addActionError("Sesión no encontrada: " + e.getMessage());
            logger.error("Session not found for pause: {}", e.getMessage(), e);
            return "notfound";
        } catch (InvalidSessionStateException e) {
            addActionError("Estado de sesión inválido para pausar: " + e.getMessage());
            logger.warn("Invalid state for pause: {}", e.getMessage(), e);
            return "invalidState";
        } catch (ServiceException e) {
            addActionError("Error del servicio al pausar sesión: " + e.getMessage());
            logger.error("Service error pausing session: {}", e.getMessage(), e);
            return ERROR;
        } catch (Exception e) {
            addActionError("Ocurrió un error inesperado al pausar la sesión: " + e.getMessage());
            logger.error("Unexpected error pausing session: {}", e.getMessage(), e);
            return ERROR;
        }
    }

    /**
     * Reanuda una sesión de estudio. (Este método es redundante si startSession maneja PAUSED, pero lo mantenemos por claridad si hay lógica específica).
     * @return SUCCESS (redirige a la lista o a la misma página activa) o LOGIN/ERROR/NOTFOUND/INVALID_STATE
     */
    public String resumeSession() {
        // La lógica de resumeSession es idéntica a startSession si startSession maneja el estado PAUSED
        // Sin embargo, para mayor claridad y si en el futuro hay diferencias, lo mantenemos separado.
        return startSession(); // Reutilizamos el método startSession
    }


    /**
     * Finaliza una sesión de estudio.
     * @return SUCCESS (redirige a la lista) o LOGIN/ERROR/NOTFOUND/INVALID_STATE
     */
    public String endSession() {
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            addActionError("Debes iniciar sesión para finalizar una sesión de estudio.");
            return LOGIN;
        }
        if (sesionId == null) {
            addActionError("ID de sesión no proporcionado para finalizar.");
            return INPUT;
        }

        try {
            // Crear el DTO con los datos de finalización
            SessionCompletionData completionData;
            completionData = new SessionCompletionData();
            completionData.setNotes(notes);
            completionData.setFocusRating(focusRating);
            completionData.setInterruptionDetails(interruptionDetails);
            completionData.setOutcome(outcome); // "completed" o "aborted"

            sesion = sesionEstudioService.endSession(sesionId, completionData);
            if (!sesion.getUsuarioId().getId().equals(loggedInUser.getId())) {
                addActionError("No tienes permiso para finalizar esta sesión.");
                return "notfound";
            }
            addActionMessage("Sesión finalizada con éxito. Estado: " + sesion.getEstadoString());
            return SUCCESS; // Redirige a la lista de sesiones
        } catch (SessionNotFoundException e) {
            addActionError("Sesión no encontrada: " + e.getMessage());
            logger.error("Session not found for end: {}", e.getMessage(), e);
            return "notfound";
        } catch (InvalidSessionStateException e) {
            addActionError("Estado de sesión inválido para finalizar: " + e.getMessage());
            logger.warn("Invalid state for end: {}", e.getMessage(), e);
            return "invalidState";
        } catch (IllegalArgumentException e) {
            addActionError("Datos de finalización inválidos: " + e.getMessage());
            logger.warn("Invalid completion data: {}", e.getMessage(), e);
            return INPUT; // Podrías redirigir a un formulario de finalización si es complejo
        } catch (ServiceException e) {
            addActionError("Error del servicio al finalizar sesión: " + e.getMessage());
            logger.error("Service error ending session: {}", e.getMessage(), e);
            return ERROR;
        } catch (Exception e) {
            addActionError("Ocurrió un error inesperado al finalizar la sesión: " + e.getMessage());
            logger.error("Unexpected error ending session: {}", e.getMessage(), e);
            return ERROR;
        }
    }
    
    /**
     * Método AJAX para actualizar las notas rápidas de una sesión.
     * Recibe el ID de la sesión y el nuevo contenido de las notas.
     * @return SUCCESS (con JsonResponse)
     */
    public String updateNotesAjax() {
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, getText("session.error.notLoggedIn"));
            return SUCCESS;
        }
        if (sesionId == null) {
            jsonResponse = new JsonResponse(false, getText("session.error.noSessionId"));
            return SUCCESS;
        }

        try {
            SesionEstudio existingSession = sesionEstudioService.getSessionById(sesionId);
            if (existingSession == null || !existingSession.getUsuarioId().getId().equals(loggedInUser.getId())) {
                jsonResponse = new JsonResponse(false, getText("session.error.unauthorized"));
                return SUCCESS;
            }

            // SANEAR LA ENTRADA ANTES DE GUARDAR EN LA BASE DE DATOS
            existingSession.setNotasRapidas(ESAPI.encoder().encodeForHTML(notasRapidas)); // HTML escape para guardar en DB
            sesionEstudioService.updateSesionEstudio(existingSession);
            jsonResponse = new JsonResponse(true, getText("session.notes.updateSuccess"), existingSession.getNotasRapidas());
            logger.info("Notas rápidas actualizadas para sesión ID {} por usuario ID {}.", sesionId, loggedInUser.getId());
            return SUCCESS;
        } catch (SessionNotFoundException e) {
            jsonResponse = new JsonResponse(false, getText("session.error.sessionNotFound") + e.getMessage());
            logger.error("SessionNotFoundException al actualizar notas para sesión ID {}: {}", sesionId, e.getMessage(), e);
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, getText("session.notes.updateError") + e.getMessage());
            logger.error("ServiceException al actualizar notas para sesión ID {}: {}", sesionId, e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, getText("session.notes.updateUnexpectedError"));
            logger.error("Excepción inesperada al actualizar notas para sesión ID {}: {}", sesionId, e.getMessage(), e);
            return SUCCESS;
        }
    }

    /**
     * Método AJAX para actualizar el checklist de una sesión.
     * Recibe el ID de la sesión y el nuevo contenido del checklist como JSON string.
     * @return SUCCESS (con JsonResponse)
     */
    public String updateChecklistAjax() {
        Usuario loggedInUser = (Usuario) sessionMap.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, getText("session.error.notLoggedIn"));
            return SUCCESS;
        }
        if (sesionId == null) {
            jsonResponse = new JsonResponse(false, getText("session.error.noSessionId"));
            return SUCCESS;
        }

        try {
            SesionEstudio existingSession = sesionEstudioService.getSessionById(sesionId);
            if (existingSession == null || !existingSession.getUsuarioId().getId().equals(loggedInUser.getId())) {
                jsonResponse = new JsonResponse(false, getText("session.error.unauthorized"));
                return SUCCESS;
            }
            
            List<Map<String, Object>> checklistItems;
            String sanitizedChecklistJsonString; // Para almacenar la cadena JSON con los campos 'text' saneados

            try {
                // 1. Parsear la cadena JSON entrante a una lista de mapas
                checklistItems = objectMapper.readValue(checklist, new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>(){});

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

            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                // Captura errores de parseo o serialización JSON de Jackson
                jsonResponse = new JsonResponse(false, getText("session.checklist.invalidJson") + e.getMessage());
                logger.warn("JSON inválido o error de procesamiento para checklist en sesión ID {}: {}", sesionId, e.getMessage());
                return SUCCESS;
            }

             // 4. Guardar la cadena JSON SANEADA en la base de datos
            existingSession.setChecklist(sanitizedChecklistJsonString);
            logger.debug("CHECKLIST después de sanear y re-serializar: " + sanitizedChecklistJsonString);
            sesionEstudioService.updateSesionEstudio(existingSession);
            jsonResponse = new JsonResponse(true, getText("session.checklist.updateSuccess"), existingSession.getChecklist());
            logger.info("Checklist actualizado para sesión ID {} por usuario ID {}.", sesionId, loggedInUser.getId());
            return SUCCESS;
        } catch (SessionNotFoundException e) {
            jsonResponse = new JsonResponse(false, getText("session.error.sessionNotFound") + e.getMessage());
            logger.error("SessionNotFoundException al actualizar checklist para sesión ID {}: {}", sesionId, e.getMessage(), e);
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, getText("session.checklist.updateError") + e.getMessage());
            logger.error("ServiceException al actualizar checklist para sesión ID {}: {}", sesionId, e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, getText("session.checklist.updateUnexpectedError"));
            logger.error("Excepción inesperada al actualizar checklist para sesión ID {}: {}", sesionId, e.getMessage(), e);
            return SUCCESS;
        }
    }
    
    public String getSessionDataAjax() {
        /*Map<String, Object> responseData = new HashMap<>();
        boolean success;
        String message;
        
        try {
        SesionEstudio sesion = sesionEstudioService.getSessionById(sesionId);
        
        if (sesion != null) {
        responseData.put("success", true);
        // Proporcionar un valor por defecto si las notas o checklist son null en la DB
        responseData.put("notasRapidas", sesion.getNotasRapidas() != null ? sesion.getNotasRapidas() : "");
        // Para el checklist, asegúrate de que sea una cadena JSON válida, incluso si está vacía.
        // Si getChecklist() ya devuelve un JSON String, no es necesario stringify aquí.
        // Si devuelve un objeto que Jackson puede serializar, simplemente pásalo.
        // Suponemos que getChecklist() devuelve un String JSON guardado previamente.
        responseData.put("checklist", sesion.getChecklist() != null ? sesion.getChecklist() : "[]");
        
        success = true; // Para la propiedad 'success' en la respuesta de Struts2
        } else {
        responseData.put("success", false);
        responseData.put("message", "Sesión no encontrada para el ID: " + sesionId);
        success = false;
        message = "Sesión no encontrada para el ID: " + sesionId;
        }
        this.jsonResponse = new JsonResponse(success,objectMapper.writeValueAsString(responseData));
        
        } catch (Exception e) {
        success = false;
        message = "Error interno del servidor al obtener datos de sesión: " + e.getMessage();
        responseData.put("success", false);
        responseData.put("message", message);
        try {
        this.jsonResponse = new JsonResponse(success,objectMapper.writeValueAsString(responseData));
        } catch (Exception jsonEx) {
        // Si falla incluso la serialización del error
        this.jsonResponse = new JsonResponse(false,"{\"message\":\"Error de serialización de error: " + jsonEx.getMessage() + "\"}");
        }
        addActionError(message); // Para el log de errores de Struts2
        e.printStackTrace(); // Para depuración en el servidor
        }
        return SUCCESS; // Struts2 result type, mapeado a JSON en struts.xml*/
         Map<String, Object> dataPayload = new HashMap<>(); // Este mapa será el 'data' dentro de JsonResponse

        try {
            SesionEstudio sesion = sesionEstudioService.getSessionById(sesionId);

            if (sesion != null) {
                // Preparar los datos que irán dentro del campo 'data' de JsonResponse
                dataPayload.put("notasRapidas", sesion.getNotasRapidas() != null ? sesion.getNotasRapidas() : "");
                dataPayload.put("checklist", sesion.getChecklist() != null ? sesion.getChecklist() : "[]");
                
                // Crear la respuesta exitosa
                this.jsonResponse = new JsonResponse(true, "Datos de sesión cargados exitosamente.", dataPayload);
            } else {
                // Crear la respuesta de error si la sesión no se encuentra
                this.jsonResponse = new JsonResponse(false, "Sesión no encontrada para el ID: " + sesionId);
            }

        } catch (Exception e) {
            // Crear una respuesta de error en caso de excepción
            String errorMessage = "Error interno del servidor al obtener datos de sesión: " + e.getMessage();
            this.jsonResponse = new JsonResponse(false, errorMessage, null); // data es null en caso de error
            addActionError(errorMessage); // Para el log de errores de Struts2
            e.printStackTrace(); // Para depuración en el servidor
        }
        return SUCCESS; // Struts2 serializará el objeto 'jsonResponse' como JSON
    }
    
    // --- Métodos de filtrado auxiliar ---
    private List<SesionEstudio> filterSessions(List<SesionEstudio> originalSessions, Integer tecnicaId, String tipoTecnica) {
        if (originalSessions == null || originalSessions.isEmpty()) {
            return Collections.emptyList();
        }

        List<SesionEstudio> filtered = originalSessions;

        // Filtrar por técnica de estudio específica
        if (tecnicaId != null && tecnicaId > 0) {
            filtered = filtered.stream()
                .filter(s -> s.getTecnicaAplicadaId() != null && s.getTecnicaAplicadaId().getId() != null && s.getTecnicaAplicadaId().getId().equals(tecnicaId))
                .collect(Collectors.toList());
        }

        // Filtrar por tipo de técnica (Individual/Grupo)
        if (filterTipoTecnica != null && !filterTipoTecnica.isEmpty()) {
            try {
                TipoTecnica tipo = TipoTecnica.valueOf(filterTipoTecnica.toUpperCase());
                filtered = filtered.stream()
                    .filter(s -> s.getTecnicaAplicadaId() != null && s.getTecnicaAplicadaId().getTipoTecnica() != null && s.getTecnicaAplicadaId().getTipoTecnica().equals(tipo))
                    .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                logger.warn("Tipo de técnica inválido para filtro: {}", filterTipoTecnica);
                // No se aplica el filtro si el tipo es inválido
            }
        }
        return filtered;
    }


    // --- Getters y Setters de Propiedades de la Acción ---
    @Override
    public void setSession(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }

    public List<SesionEstudio> getSesiones() {
        return sesiones;
    }

    public List<TecnicaEstudio> getTecnicasDisponibles() {
        return tecnicasDisponibles;
    }

    public List<Entrega> getEntregasDisponibles() {
        return entregasDisponibles;
    }

    public List<Examen> getExamenesDisponibles() {
        return examenesDisponibles;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public Integer getFilterTecnicaId() {
        return filterTecnicaId;
    }

    public void setFilterTecnicaId(Integer filterTecnicaId) {
        this.filterTecnicaId = filterTecnicaId;
    }

    public String getFilterTipoTecnica() {
        return filterTipoTecnica;
    }

    public void setFilterTipoTecnica(String filterTipoTecnica) {
        this.filterTipoTecnica = filterTipoTecnica;
    }

    public String getSesionesJson() {
        return sesionesJson;
    }

    public void setSesionesJson(String sesionesJson) {
        this.sesionesJson = sesionesJson;
    }

    public Integer getSesionId() {
        return sesionId;
    }

    public void setSesionId(Integer sesionId) {
        this.sesionId = sesionId;
    }

    public SesionEstudio getSesion() {
        return sesion;
    }

    public void setSesion(SesionEstudio sesion) {
        this.sesion = sesion;
    }

    public Boolean getStartImmediately() {
        return startImmediately;
    }

    public void setStartImmediately(Boolean startImmediately) {
        this.startImmediately = startImmediately;
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

    public void setFilterTypeOptions(Map<String, String> filterTypeOptions){
        this.filterTypeOptions = filterTypeOptions;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getFocusRating() {
        return focusRating;
    }

    public void setFocusRating(Integer focusRating) {
        this.focusRating = focusRating;
    }

    public String getInterruptionDetails() {
        return interruptionDetails;
    }

    public void setInterruptionDetails(String interruptionDetails) {
        this.interruptionDetails = interruptionDetails;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
    
    public JsonResponse getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(JsonResponse jsonResponse) {
        this.jsonResponse = jsonResponse;
    }
    
    public String getNotasRapidas() {
        return notasRapidas;
    }

    public void setNotasRapidas(String notasRapidas) {
        this.notasRapidas = notasRapidas;
    }

    public String getChecklist() {
        return checklist;
    }

    public void setChecklist(String checklist) {
        this.checklist = checklist;
    }
    
    
    // Método auxiliar para obtener los tipos de técnica (para el dropdown en JSP)
    public TipoTecnica[] getTipoTecnicaOptions() {
        return TipoTecnica.values();
    }
}
