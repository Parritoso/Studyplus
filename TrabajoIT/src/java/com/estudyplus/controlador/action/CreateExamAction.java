/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action;

import com.estudyplus.controlador.services.TaskService;
import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.modelo.entitys.Examen;
import com.estudyplus.modelo.entitys.Usuario;
import com.estudyplus.modelo.rest.facade.EstudyRestFacadeFactory;
import com.opensymphony.xwork2.ActionSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Parri
 */
public class CreateExamAction extends ActionSupport implements SessionAware {

    private static final Logger logger = LoggerFactory.getLogger(CreateExamAction.class);

    // --- Propiedades para capturar los datos del formulario JSP ---
    private String nombre;
    private String descripcion;
    private String fechaExamenStr; // Campo de texto para la fecha del examen
    private String asignatura;
    private String tipoExamen;
    private String prioridad;
    private Boolean recordatorioActivo;
    private String fechaRecordatorioStr; // Campo de texto para la fecha del recordatorio
    
    // Propiedad para el ID del examen creado (para la página de éxito)
    private Integer createdExamId; // Asumo que el ID es Integer

    // Mensaje para la vista después de la operación
    private String feedbackMessage;
    
    // Sesión para obtener el usuario autenticado
    private Map<String, Object> session;

    // Inyección del servicio
    private TaskService taskService;

    // Constructor para inicializar el TaskService usando la factoría
    public CreateExamAction() {
        this.taskService = new TaskService(EstudyRestFacadeFactory.getInstance());
    }
    
    // Método que se ejecuta para mostrar el formulario (GET) o procesarlo (POST)
    @Override
    public String execute() {
        // Si la petición es GET, simplemente mostramos el formulario vacío
        if (ServletActionContext.getRequest().getMethod().equalsIgnoreCase("GET")) {
            logger.info("Acceso inicial al formulario de creación de examen (GET).");
            return INPUT; 
        }

        // Si es una petición POST, procesamos el formulario
        logger.info("Intentando crear examen (POST) para usuario.");

        // 1. Obtener el usuario autenticado de la sesión
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            feedbackMessage = "No se ha encontrado un usuario autenticado. Por favor, inicie sesión.";
            logger.warn("Intento de crear examen sin usuario autenticado en sesión.");
            return ERROR; 
        }
        
        // 2. Validación de entradas básicas
        if (nombre == null || nombre.trim().isEmpty()) {
            addFieldError("nombre", "El nombre del examen es obligatorio.");
        }
        if (fechaExamenStr == null || fechaExamenStr.trim().isEmpty()) {
            addFieldError("fechaExamenStr", "La fecha del examen es obligatoria.");
        }
        if (asignatura == null || asignatura.trim().isEmpty()) {
            addFieldError("asignatura", "La asignatura es obligatoria.");
        }
        if (tipoExamen == null || tipoExamen.trim().isEmpty()) {
            addFieldError("tipoExamen", "El tipo de examen es obligatorio.");
        }

        // Si hay errores en los campos obligatorios, devolver INPUT
        if (hasFieldErrors()) {
            logger.warn("Errores de validación en el formulario de creación de examen.");
            feedbackMessage = "Por favor, corrija los errores en el formulario.";
            return INPUT;
        }

        // 3. Convertir Strings de fecha a Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaExamen = null;
        Date fechaRecordatorio = null;
        try {
            fechaExamen = dateFormat.parse(fechaExamenStr);
        } catch (ParseException e) {
            addFieldError("fechaExamenStr", "Formato de fecha de examen inválido. Usa YYYY-MM-DD.");
            feedbackMessage = "Por favor, corrija el formato de la fecha del examen.";
            logger.warn("Formato de fecha de examen inválido para '{}'.", fechaExamenStr);
            return INPUT;
        }

        // Validar y convertir fechaRecordatorio si el recordatorio está activo
        if (recordatorioActivo != null && recordatorioActivo) {
            if (fechaRecordatorioStr == null || fechaRecordatorioStr.trim().isEmpty()) {
                addFieldError("fechaRecordatorioStr", "La fecha de recordatorio es obligatoria si el recordatorio está activo.");
                feedbackMessage = "Por favor, especifique la fecha de recordatorio.";
                return INPUT;
            }
            try {
                fechaRecordatorio = dateFormat.parse(fechaRecordatorioStr);
            } catch (ParseException e) {
                addFieldError("fechaRecordatorioStr", "Formato de fecha de recordatorio inválido. Usa YYYY-MM-DD.");
                feedbackMessage = "Por favor, corrija el formato de la fecha de recordatorio.";
                logger.warn("Formato de fecha de recordatorio inválido para '{}'.", fechaRecordatorioStr);
                return INPUT;
            }
            // Opcional: Validar que fechaRecordatorio sea anterior a fechaExamen
            if (fechaRecordatorio != null && fechaExamen != null && fechaRecordatorio.after(fechaExamen)) {
                addFieldError("fechaRecordatorioStr", "La fecha de recordatorio debe ser anterior a la fecha del examen.");
                feedbackMessage = "La fecha de recordatorio no puede ser posterior a la del examen.";
                return INPUT;
            }
        } else {
            // Si recordatorio no está activo, asegurarse de que la fecha de recordatorio sea nula.
            fechaRecordatorio = null;
        }
        
        // 4. Crear la entidad Examen y asociar el usuario
        try {
            Examen examen = new Examen();
            examen.setNombre(ESAPI.encoder().encodeForHTML(nombre));
            examen.setDescripcion(ESAPI.encoder().encodeForHTML(descripcion));
            examen.setFechaExamen(fechaExamen);
            examen.setAsignatura(ESAPI.encoder().encodeForHTML(asignatura));
            examen.setTipoExamen(ESAPI.encoder().encodeForHTML(tipoExamen));
            examen.setPrioridad(ESAPI.encoder().encodeForHTML(prioridad));
            examen.setRecordatorioActivo(recordatorioActivo != null ? recordatorioActivo : false); // Valor predeterminado si es nulo
            examen.setFechaRecordatorio(fechaRecordatorio);
            // La fecha de creación se suele establecer en el servicio o la base de datos
            examen.setFechaCreacion(new Date()); // Establecer aquí o en el servicio/fachada
            
            // Asocia el usuario autenticado
            examen.setUsuarioId(loggedInUser);

            // 5. Llamar al servicio para crear el examen
            Examen created = taskService.createExam(examen); // Asumo un método createExam en TaskService
            
            if (created != null && created.getId() != null) {
                this.createdExamId = created.getId(); // Guarda el ID del examen creado
                feedbackMessage = "Examen '" + nombre + "' creado con éxito.";
                logger.info("Examen '{}' (ID: {}) creado con éxito para usuario '{}'.", nombre, created.getId(), loggedInUser.getNombre());
                return SUCCESS; // Redirige a la página de éxito/confirmación
            } else {
                feedbackMessage = "Error inesperado al crear el examen. El servicio no devolvió el examen creado.";
                logger.error("TaskService.createExam devolvió null o ID nulo para examen '{}'.", nombre);
                return ERROR;
            }

        } catch (ServiceException e) {
            feedbackMessage = "Error del servicio al crear el examen: " + e.getMessage();
            addActionError(feedbackMessage);
            logger.error("ServiceException al crear examen para usuario '{}': {}", loggedInUser.getNombre(), e.getMessage(), e);
            return ERROR; 
        } catch (Exception e) {
            feedbackMessage = "Ocurrió un error inesperado al procesar el examen: " + e.getMessage();
            addActionError("Ocurrió un error inesperado.");
            logger.error("Excepción inesperada en CreateExamAction para usuario '{}': {}", loggedInUser.getNombre(), e.getMessage(), e);
            return ERROR;
        }
    }

    // Nuevo método para manejar la visualización de la página de éxito
    public String showSuccessPage() {
        // Las propiedades se poblarán por los parámetros de la redirección.
        if (createdExamId == null) {
            addActionError("No se encontró información del examen creado.");
            return ERROR;
        }
        return SUCCESS;
    }

    // --- Getters y Setters ---
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFechaExamenStr() { return fechaExamenStr; }
    public void setFechaExamenStr(String fechaExamenStr) { this.fechaExamenStr = fechaExamenStr; }

    public String getAsignatura() { return asignatura; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }

    public String getTipoExamen() { return tipoExamen; }
    public void setTipoExamen(String tipoExamen) { this.tipoExamen = tipoExamen; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public Boolean getRecordatorioActivo() { return recordatorioActivo; }
    public void setRecordatorioActivo(Boolean recordatorioActivo) { this.recordatorioActivo = recordatorioActivo; }

    public String getFechaRecordatorioStr() { return fechaRecordatorioStr; }
    public void setFechaRecordatorioStr(String fechaRecordatorioStr) { this.fechaRecordatorioStr = fechaRecordatorioStr; }

    public Integer getCreatedExamId() { return createdExamId; }
    public void setCreatedExamId(Integer createdExamId) { this.createdExamId = createdExamId; }

    public String getFeedbackMessage() { return feedbackMessage; }
    public void setFeedbackMessage(String feedbackMessage) { this.feedbackMessage = feedbackMessage; }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
