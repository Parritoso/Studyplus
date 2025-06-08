/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action;

import com.estudyplus.controlador.services.StudySessionService;
import com.estudyplus.controlador.services.UserService;
import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.controlador.services.exception.UserNotFoundException;
import com.estudyplus.modelo.entitys.Entrega;
import com.estudyplus.modelo.entitys.Examen;
import com.estudyplus.modelo.entitys.GrupoEstudio;
import com.estudyplus.modelo.entitys.SesionEstudio;
import com.estudyplus.modelo.entitys.TecnicaEstudio;
import com.estudyplus.modelo.entitys.Usuario;
import com.estudyplus.modelo.rest.facade.EstudyRestFacade;
import com.estudyplus.modelo.rest.facade.EstudyRestFacadeFactory;
import com.opensymphony.xwork2.ActionSupport;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.json.annotations.JSON;
import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Parri
 */
public class DashboardAction extends ActionSupport implements SessionAware {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardAction.class);

    private String username; // Para mostrar en el dashboard
    private Map<String, Object> session;
    private Integer sesionId; // Para pasar el ID de la sesión recién creada
    private StudySessionService sesionEstudioService; // Servicio para crear sesiones
    private UserService userService;
    private EstudyRestFacade estudyRestFacade;
    
    // --- Propiedades para el formulario de sesión rápida y respuesta JSON ---
    private SesionEstudio sesion; // Objeto para bindear los datos del formulario y serializar
    private List<TecnicaEstudio> tecnicasDisponibles;
    private List<Entrega> entregasDisponibles;
    private List<Examen> examenesDisponibles;   
    private String currentDateTimeFormatted; // Para mostrar la fecha/hora actual en el campo no editable
    private Integer duracionPlanificadaMinutosForm;
    private List<GrupoEstudio> userGroups = new ArrayList(); // Lista de grupos del usuario

    public DashboardAction() {
        this.sesionEstudioService = new StudySessionService(); // Inicializar el servicio
        this.estudyRestFacade = EstudyRestFacadeFactory.getInstance(); // Inicializar la fachada
        this.userService = new UserService(estudyRestFacade);
    }
    
    @Override
    public String execute() {
        // Recuperar el nombre de usuario de la sesión para mostrarlo
        tecnicasDisponibles = new ArrayList();
        entregasDisponibles = new ArrayList();
        examenesDisponibles = new ArrayList();
        if (session.containsKey("loggedInUser")) {
            username = ((Usuario) session.get("loggedInUser")).getNombre();
            try{
                if (userService.getGruposByUsuario(((Usuario) session.get("loggedInUser")).getId()) != null) {
                    this.userGroups = (List<GrupoEstudio>) userService.getGruposByUsuario(((Usuario) session.get("loggedInUser")).getId());
                } else {
                    this.userGroups = Collections.emptyList();
                }
            } catch(Exception e){
                this.userGroups = Collections.emptyList();
            }
        } else {
            // Si por alguna razón no está en sesión (acceso directo o sesión expirada)
            return ERROR; // O redirigir a login
        }
        return SUCCESS;
    }
    
    /**
     * Prepara los datos para mostrar el formulario de sesión rápida como JSON.
     * @return SUCCESS para que el Struts2 JSON plugin lo serialice.
     */
    public String showQuickSessionForm() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            addActionError("Debes iniciar sesión para acceder a esta funcionalidad.");
            return LOGIN; // O un resultado JSON de error si se prefiere
        }
        
        sesion = new SesionEstudio(); // Inicializar el objeto sesion para el formulario
        
        // Pre-fill non-editable fields with default values
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        currentDateTimeFormatted = dateFormatter.format(new Date());
        sesion.setTitulo("Sesión Rápida - " + currentDateTimeFormatted);
        sesion.setDescripcion("Sesión de estudio iniciada rápidamente desde el dashboard.");
        sesion.setDuracionPlanificadaMinutos(30); // Default duration
        this.duracionPlanificadaMinutosForm = 30;

        try {
            populateQuickSessionFormDropdowns(loggedInUser); // Cargar las opciones de los selectores   
        } catch (ServiceException e){
            addActionError("Error al cargar algunas opciones para el formulario: " + e.getMessage());
            logger.error("Service Exception al preparar formulario de sesión rápida: {}", e.getMessage(), e);
            // Asegurarse de que las listas estén vacías para evitar NPE en el JSP
            tecnicasDisponibles = Collections.emptyList();
            entregasDisponibles = Collections.emptyList();
            examenesDisponibles = Collections.emptyList();
            return SUCCESS; // Permite que la página se muestre con el error y las listas vacías
        } catch (UserNotFoundException e) {
            addActionError("Error al cargar datos para el formulario de sesión rápida: " + e.getMessage());
            logger.error("Error loading data for quick session form: {}", e.getMessage(), e);
            tecnicasDisponibles = Collections.emptyList();
            entregasDisponibles = Collections.emptyList();
            examenesDisponibles = Collections.emptyList();
            return ERROR; // O un resultado JSON de error
        }
        return SUCCESS; // Struts2 JSON plugin serializará las propiedades con @JSON
    }
    
    /**
     * Crea y comienza una sesión de estudio de forma inmediata con valores por defecto
     * para título, descripción y fecha, pero usando las opciones seleccionadas por el usuario
     * para técnica, entrega, examen y duración.
     * @return "startActiveSession" para redirigir a la página de sesión activa.
     */
    public String startQuickSession() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            addActionError("Debes iniciar sesión para iniciar una sesión rápida.");
            return LOGIN;
        }

        if (sesion == null) {
            sesion = new SesionEstudio(); // Asegurarse de que no sea null para evitar NPEs
        }
        
        try {
            // Override title, description, and planned date as requested (non-editable fields)
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM HH:mm");
            String defaultTitle = "Sesión Rápida - " + dateFormatter.format(new Date());
            String defaultDescription = "Sesión de estudio iniciada rápidamente desde el dashboard.";
            
            sesion.setTitulo(ESAPI.encoder().encodeForHTML(defaultTitle));
            sesion.setDescripcion(ESAPI.encoder().encodeForHTML(defaultDescription));
            currentDateTimeFormatted = dateFormatter.format(new Date());
            sesion.setFechaSesionPlanificada(null); // Ensure it's not planned, it starts immediately
            
            // Validate planned duration, as it's user-editable
            if (duracionPlanificadaMinutosForm == null) {
                addActionError("La duración planificada es obligatoria.");
                populateQuickSessionFormDropdowns(loggedInUser); // Re-populate dropdowns for INPUT result
                return INPUT;
            }
            if (duracionPlanificadaMinutosForm <= 0) {
                addActionError("La duración planificada debe ser un número positivo.");
                populateQuickSessionFormDropdowns(loggedInUser); // Re-populate dropdowns for INPUT result
                return INPUT;
            }
            if (duracionPlanificadaMinutosForm < 10) {
                addActionError("La duración planificada debe ser de al menos 10 minutos.");
                populateQuickSessionFormDropdowns(loggedInUser); // Re-populate dropdowns for INPUT result
                return INPUT;
            }
            
            sesion.setDuracionPlanificadaMinutos(duracionPlanificadaMinutosForm);
            
            // Handle selectable fields from form (ensure IDs are correctly mapped or nullified if empty)
            if (sesion.getTecnicaAplicadaId() != null) {
                TecnicaEstudio tecnica = sesion.getTecnicaAplicadaId();
                if(tecnica != null && tecnica.getId() == null){
                    sesion.setTecnicaAplicadaId(null);
                }
            }
            if (sesion.getEntregaAsociada() != null) {
                Entrega entrega = sesion.getEntregaAsociada();
                if(entrega != null && entrega.getId() == null){
                   sesion.setEntregaAsociada(null); 
                }
            }
            if (sesion.getExamenAsociado() != null) {
                Examen examen = sesion.getExamenAsociado();
                if(examen != null && examen.getId()==null){
                    sesion.setExamenAsociado(null);
                }
                 
            }

            sesion.setUsuarioId(loggedInUser);
            
            SesionEstudio createdSession = sesionEstudioService.createSession(sesion, true); // true to start immediately

            if (createdSession != null && createdSession.getId() != null) {
                this.sesionId = createdSession.getId(); // Guardar el ID para la redirección
                addActionMessage("Sesión rápida iniciada con éxito.");
                return "startActiveSession"; // Redirige a la acción de iniciar sesión para mostrar la página activa
            } else {
                addActionError("No se pudo iniciar la sesión rápida.");
                return ERROR;
            }

        } catch (UserNotFoundException e) {
            addActionError("Error: Usuario no encontrado. " + e.getMessage());
            logger.error("Usuario no encontrado al iniciar sesión rápida: {}", e.getMessage(), e);
            return ERROR;
        } catch (ServiceException e) {
            addActionError("Error del servicio al iniciar sesión rápida: " + e.getMessage());
            logger.error("Service Exception al iniciar sesión rápida: {}", e.getMessage(), e);
            return ERROR;
        } catch (Exception e) {
            addActionError("Ocurrió un error inesperado al iniciar la sesión rápida: " + e.getMessage());
            logger.error("Unexpected error al iniciar sesión rápida: {}", e.getMessage(), e);
            return ERROR;
        }
    }
    
    // Helper method to populate dropdowns for form display/re-display
    private void populateQuickSessionFormDropdowns(Usuario loggedInUser) throws ServiceException, UserNotFoundException {
        // Asegúrate de usar el servicio correcto para obtener solo técnicas individuales
        tecnicasDisponibles = Collections.emptyList();
        entregasDisponibles = Collections.emptyList();
        examenesDisponibles = Collections.emptyList();
        try{
            tecnicasDisponibles = sesionEstudioService.getIndividualTecnicasEstudio();
            if (tecnicasDisponibles == null) tecnicasDisponibles = Collections.emptyList();
            
            entregasDisponibles = estudyRestFacade.getUpcomingEntregasByUserId(loggedInUser.getId());
            if (entregasDisponibles == null) entregasDisponibles = Collections.emptyList();

            examenesDisponibles = estudyRestFacade.getUpcomingExamenesByUserId(loggedInUser.getId());
            if (examenesDisponibles == null) examenesDisponibles = Collections.emptyList();
        } catch(ServiceException e) { // Capturar ServiceException para errores de comunicación con la API
            logger.error("Error al cargar datos de la API para el formulario rápido: {}", e.getMessage(), e);
            throw e; // Relanzar como ServiceException para que el método showQuickSessionForm lo capture
        } catch(Exception e){ // Capturar cualquier otra excepción inesperada
            logger.error("Error inesperado al cargar datos para el formulario rápido: {}", e.getMessage(), e);
            throw new ServiceException("Error inesperado al cargar datos para el formulario.", e); // Envolver en ServiceException
        }
        
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
    
    public Integer getSesionId() {
        return sesionId;
    }

    public void setSesionId(Integer sesionId) {
        this.sesionId = sesionId;
    }

    public List<GrupoEstudio> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<GrupoEstudio> userGroups) {
        this.userGroups = userGroups;
    }

    // Getters y Setters para las propiedades del formulario (serializadas a JSON)
    @JSON(serialize = true)
    public SesionEstudio getSesion() {
        return sesion;
    }

    public void setSesion(SesionEstudio sesion) {
        this.sesion = sesion;
    }

    @JSON(serialize = true)
    public List<TecnicaEstudio> getTecnicasDisponibles() {
        return tecnicasDisponibles;
    }

    // No se necesita setter para listas si solo se usan para poblar el formulario

    @JSON(serialize = true)
    public List<Entrega> getEntregasDisponibles() {
        return entregasDisponibles;
    }

    @JSON(serialize = true)
    public List<Examen> getExamenesDisponibles() {
        return examenesDisponibles;
    }

    @JSON(serialize = true)
    public String getCurrentDateTimeFormatted() {
        return currentDateTimeFormatted;
    }
    
    public Integer getDuracionPlanificadaMinutosForm() {
        return duracionPlanificadaMinutosForm;
    }

    public void setDuracionPlanificadaMinutosForm(Integer duracionPlanificadaMinutosForm) {
        this.duracionPlanificadaMinutosForm = duracionPlanificadaMinutosForm;
    }
}
