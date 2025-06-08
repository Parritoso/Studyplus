/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action;

import com.estudyplus.controlador.services.TaskService;
import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.modelo.entitys.Entrega;
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
public class CreateDeliveryAction extends ActionSupport implements SessionAware {
    private static final Logger logger = LoggerFactory.getLogger(CreateDeliveryAction.class);
    // --- Propiedades para capturar los datos del formulario JSP ---
    private String titulo;
    private String descripcion;
    private String fechaLimiteStr; // Campo de texto para la fecha límite
    private String asignatura;
    private String prioridad;
    
    // Propiedad para el ID de la entrega creada (para la página de éxito)
    private Long createdDeliveryId;
    
    // Mensaje para la vista después de la operación
    private String feedbackMessage;
    
    // Sesión para obtener el usuario autenticado
    private Map<String, Object> session;

    // Inyección del servicio (inicializado a través de la factoría)
    private TaskService taskService;

    // Constructor para inicializar el TaskService usando la factoría
    public CreateDeliveryAction() {
        this.taskService = new TaskService(EstudyRestFacadeFactory.getInstance());
    }
    
    @Override
    public String execute() {
        // Si la petición es GET, simplemente mostramos el formulario vacío
        if (ServletActionContext.getRequest().getMethod().equalsIgnoreCase("GET")) {
            logger.info("Acceso inicial al formulario de creación de entrega (GET).");
            // No hay errores, ni feedback, solo mostrar el formulario limpio
            return INPUT; 
        }

        // Si es una petición POST, procesamos el formulario
        logger.info("Intentando crear entrega (POST) para usuario.");

        // 1. Obtener el usuario autenticado de la sesión
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            feedbackMessage = "No se ha encontrado un usuario autenticado. Por favor, inicie sesión.";
            logger.warn("Intento de crear entrega sin usuario autenticado en sesión.");
            // Redirige al login o a un error de autorización
            return ERROR; 
        }
        
        // 2. Validación de entradas
        if (titulo == null || titulo.trim().isEmpty()) {
            addFieldError("titulo", "El título es obligatorio.");
        }
        if (fechaLimiteStr == null || fechaLimiteStr.trim().isEmpty()) {
            addFieldError("fechaLimiteStr", "La fecha límite es obligatoria.");
        }
        if (asignatura == null || asignatura.trim().isEmpty()) {
            addFieldError("asignatura", "La asignatura es obligatoria.");
        }

        // Si hay errores en los campos, devolver INPUT para que el formulario se vuelva a mostrar
        // con los mensajes de error y los datos válidos que se ingresaron.
        if (hasFieldErrors()) {
            logger.warn("Errores de validación en el formulario de creación de entrega.");
            feedbackMessage = "Por favor, corrija los errores en el formulario.";
            return INPUT;
        }

        // 3. Convertir String de fecha a Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaLimite = null;
        try {
            fechaLimite = dateFormat.parse(fechaLimiteStr);
        } catch (ParseException e) {
            addFieldError("fechaLimiteStr", "Formato de fecha inválido. Usa YYYY-MM-DD.");
            feedbackMessage = "Por favor, corrija el formato de la fecha.";
            logger.warn("Formato de fecha inválido para '{}'.", fechaLimiteStr);
            return INPUT; // Volver al formulario con el error de formato de fecha
        }
        
        // 4. Crear la entidad Entrega y asociar el usuario
        try {
            Entrega entrega = new Entrega();
            entrega.setTitulo(ESAPI.encoder().encodeForHTML(titulo));
            entrega.setDescripcion(ESAPI.encoder().encodeForHTML(descripcion));
            entrega.setFechaLimite(fechaLimite);
            entrega.setAsignatura(ESAPI.encoder().encodeForHTML(asignatura));
            entrega.setPrioridad(ESAPI.encoder().encodeForHTML(prioridad)); // Asegúrate de que el tipo coincida con tu entidad

            // Asocia el usuario autenticado
            entrega.setUsuarioId(loggedInUser); // Usamos el objeto Usuario completo de la sesión

            // 5. Llamar al servicio para crear la entrega
            Entrega created = taskService.createDelivery(entrega);
            
            if (created != null && created.getId() != null) {
                this.createdDeliveryId = created.getId().longValue(); // Guarda el ID de la entrega creada
                feedbackMessage = "Entrega '" + titulo + "' creada con éxito.";
                logger.info("Entrega '{}' (ID: {}) creada con éxito para usuario '{}'.", titulo, created.getId(), loggedInUser.getNombre());
                return SUCCESS; // Redirige a la página de éxito/confirmación
            } else {
                feedbackMessage = "Error inesperado al crear la entrega. El servicio no devolvió la entrega creada.";
                logger.error("TaskService.createDelivery devolvió null o ID nulo para entrega '{}'.", titulo);
                return ERROR;
            }

        } catch (ServiceException e) {
            feedbackMessage = "Error del servicio al crear la entrega: " + e.getMessage();
            addActionError(feedbackMessage); // Añade error a la lista de errores de acción
            logger.error("ServiceException al crear entrega para usuario '{}': {}", loggedInUser.getNombre(), e.getMessage(), e);
            return ERROR; 
        } catch (Exception e) {
            feedbackMessage = "Ocurrió un error inesperado al procesar la entrega: " + e.getMessage();
            addActionError("Ocurrió un error inesperado."); // Mensaje genérico para el usuario
            logger.error("Excepción inesperada en CreateDeliveryAction para usuario '{}': {}", loggedInUser.getNombre(), e.getMessage(), e);
            return ERROR;
        }
    }
    
    // Nuevo método para manejar la visualización de la página de éxito
    public String showSuccessPage() {
        // Cuando se redirige a esta acción, las propiedades (titulo, etc.) ya están pobladas por Struts2
        // debido a los parámetros en la redirección en tasks.xml.
        if (createdDeliveryId == null) {
            // Esto podría ocurrir si alguien intenta acceder directamente a /deliverySuccess.action
            // sin un ID de entrega creado.
            addActionError("No se encontró información de la entrega creada.");
            return ERROR; // Redirige a la página de error o al dashboard
        }
        return SUCCESS;
    }

    // --- Getters y Setters para las propiedades del formulario ---
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFechaLimiteStr() { return fechaLimiteStr; }
    public void setFechaLimiteStr(String fechaLimiteStr) { this.fechaLimiteStr = fechaLimiteStr; }

    public String getAsignatura() { return asignatura; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public String getFeedbackMessage() { return feedbackMessage; }
    public void setFeedbackMessage(String feedbackMessage) { this.feedbackMessage = feedbackMessage; }

    public Long getCreatedDeliveryId() { return createdDeliveryId; }
    public void setCreatedDeliveryId(Long createdDeliveryId) { this.createdDeliveryId = createdDeliveryId; }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
    
}
