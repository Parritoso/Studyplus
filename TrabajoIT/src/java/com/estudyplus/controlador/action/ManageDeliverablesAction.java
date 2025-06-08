/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action;

import com.estudyplus.modelo.entitys.Entrega;
import com.estudyplus.modelo.entitys.Examen;
import com.estudyplus.modelo.entitys.Usuario;
import com.estudyplus.modelo.rest.facade.EstudyRestFacade;
import com.estudyplus.modelo.rest.facade.EstudyRestFacadeFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.opensymphony.xwork2.ActionSupport;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Parri
 */
public class ManageDeliverablesAction extends ActionSupport implements SessionAware {
    private static final Logger logger = LoggerFactory.getLogger(ManageDeliverablesAction.class);

    private List<Entrega> proximasEntregas;
    private List<Examen> proximosExamenes;
    private String feedbackMessage; // Para posibles mensajes de éxito/error
    private Map<String, Object> session;

        // JSON String para pasar al JSP (usando Jackson)
    private String proximasEntregasJson;
    private String proximosExamenesJson;

    private EstudyRestFacade restFacade = EstudyRestFacadeFactory.getInstance(); // Inyección de dependencia a ajustar

    @Override
    public String execute() throws Exception {
        // Inicializar las listas como vacías para evitar NullPointerException
        proximasEntregas = new ArrayList<>();
        proximosExamenes = new ArrayList<>();

        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            setFeedbackMessage("No has iniciado sesión. Por favor, accede a tu cuenta.");
            return LOGIN;
        }

        try {
            // --- Obtener Entregas del Usuario (Reemplazar con llamada a la API REST) ---
            List<Entrega> allEntregas = new ArrayList<>();
            try {
                // Asumiendo que getEntregasByUsuarioId podría devolver null si no encuentra nada
                List<Entrega> fetchedEntregas = restFacade.getEntregasByUserId(loggedInUser.getId());
                if (fetchedEntregas != null) {
                    allEntregas.addAll(fetchedEntregas);
                }
                logger.debug("API REST devolvió {} entregas para el usuario ID {}.", allEntregas.size(), loggedInUser.getId());
                allEntregas.forEach(e -> logger.debug("  Entrega obtenida: ID={}, Título='{}', Fecha Límite={}, Estado='{}'",
                                             e.getId(), e.getTitulo(), e.getFechaLimite(), e.getEstado()));
            } catch (Exception e) {
                
                // Loguear el error pero no detener la ejecución, para que la página se cargue
                logger.error("Error al obtener entregas del usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
                // Puedes añadir un mensaje de error específico si quieres
                // addActionError("No se pudieron cargar las entregas: " + e.getMessage());
            }
            
            // --- Obtener Examenes del Usuario (Reemplazar con llamada a la API REST) ---
            List<Examen> allExamenes = new ArrayList<>();
            try {
                List<Examen> fetchedExamenes = restFacade.getExamenesByUserId(loggedInUser.getId());
                if (fetchedExamenes != null) {
                    allExamenes.addAll(fetchedExamenes);
                }
                logger.debug("API REST devolvió {} exámenes para el usuario ID {}.", allExamenes.size(), loggedInUser.getId());
                allExamenes.forEach(ex -> logger.debug("  Examen obtenido: ID={}, Nombre='{}', Fecha Examen={}",
                                              ex.getId(), ex.getNombre(), ex.getFechaExamen()));
            } catch (Exception e) {
                logger.error("Error al obtener exámenes del usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
                // addActionError("No se pudieron cargar los exámenes: " + e.getMessage());
            }
            
//            allEntregas = simulateUserDeliverables(loggedInUser.getId());
//            allExamenes = simulateUserExams(loggedInUser.getId());
            Date now = new Date();

            // --- Filtrar y ordenar entregas próximas ---
            proximasEntregas = allEntregas.stream()
                .filter(e -> {
                    boolean isFuture = e.getFechaLimite() != null && e.getFechaLimite().after(now);
                    boolean isNotCompleted = !"Completada".equalsIgnoreCase(e.getEstado());
                    // Log detallado de cada ítem del filtro
                    logger.debug("Filtrando Entrega ID={}: Fecha Límite={}, Después de Ahora={}, Estado='{}', No Completada={}",
                                 e.getId(), e.getFechaLimite(), isFuture, e.getEstado(), isNotCompleted);
                    return isFuture && isNotCompleted;
                })
                .sorted(Comparator.comparing(Entrega::getFechaLimite))
                .limit(5)
                .collect(Collectors.toList());

            logger.info("Entregas próximas después de filtrar y limitar: {} (Máx 5).", proximasEntregas.size());
            proximasEntregas.forEach(e -> logger.info("  Próxima Entrega: ID={}, Título='{}', Fecha Límite={}",
                                             e.getId(), e.getTitulo(), e.getFechaLimite()));
            
            // --- Filtrar y ordenar exámenes próximos ---
            proximosExamenes = allExamenes.stream()
                .filter(ex -> {
                    boolean isFuture = ex.getFechaExamen() != null && ex.getFechaExamen().after(now);
                    logger.debug("Filtrando Examen ID={}: Fecha Examen={}, Después de Ahora={}",
                                 ex.getId(), ex.getFechaExamen(), isFuture);
                    return isFuture;
                })
                .sorted(Comparator.comparing(Examen::getFechaExamen))
                .limit(5)
                .collect(Collectors.toList());
            
            logger.info("Exámenes próximos después de filtrar y limitar: {} (Máx 5).", proximosExamenes.size());
            proximosExamenes.forEach(ex -> logger.info("  Próximo Examen: ID={}, Nombre='{}', Fecha Examen={}",
                                              ex.getId(), ex.getNombre(), ex.getFechaExamen()));

            // Convertir las listas a JSON usando Jackson
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            mapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));

            proximasEntregasJson = mapper.writeValueAsString(proximasEntregas);
            proximosExamenesJson = mapper.writeValueAsString(proximosExamenes);


        } catch (Exception e) {
            // Este catch es para errores inesperados no relacionados con la obtención de datos
            logger.error("Ocurrió un error inesperado al cargar las tareas: {}", e.getMessage(), e);
            addActionError("Ocurrió un error inesperado al cargar la información: " + e.getMessage());
            e.printStackTrace();
            return ERROR;
        }

        return SUCCESS;
    }

    // --- Implementación de SessionAware ---
    @Override
    public void setSession(Map<String, Object> sessionMap) {
        this.session = sessionMap;
    }

    // --- Getters para Struts2 ---
    public List<Entrega> getProximasEntregas() {
        return proximasEntregas;
    }

    public List<Examen> getProximosExamenes() {
        return proximosExamenes;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }
    
    public String getProximasEntregasJson() {
        return proximasEntregasJson;
    }

    public String getProximosExamenesJson() {
        return proximosExamenesJson;
    }


    // --- SIMULACIÓN DE DATOS (PARA TESTING MIENTRAS NO HAYA API REST REAL) ---
    private List<Entrega> simulateUserDeliverables(Integer userId) {
        List<Entrega> simulated = new ArrayList<>();
        if (userId != null) {
            Usuario u = new Usuario(); u.setId(userId);
            simulated.add(createSimulatedEntrega(1, "Proyecto Final", "Desarrollo de IA", new Date(System.currentTimeMillis() + 86400000 * 7), new Date(), new Date(), "Pendiente", "Alta", "Programacion", true, new Date(System.currentTimeMillis() + 86400000 * 6), u));
            simulated.add(createSimulatedEntrega(2, "Tarea Matematicas", "Ejercicios de Algebra", new Date(System.currentTimeMillis() + 86400000 * 3), new Date(), null, "Pendiente", "Media", "Matematicas", true, new Date(System.currentTimeMillis() + 86400000 * 2), u));
            simulated.add(createSimulatedEntrega(3, "Ensayo Historia", "Sobre la Edad Media", new Date(System.currentTimeMillis() + 86400000 * 1), new Date(), null, "Pendiente", "Baja", "Historia", false, null, u));
            simulated.add(createSimulatedEntrega(4, "Entrega pasada", "Ya hecha", new Date(System.currentTimeMillis() - 86400000 * 2), new Date(), new Date(), "Completada", "Baja", "General", false, null, u));
        }
        return simulated;
    }
    
    private Entrega createSimulatedEntrega(Integer id, String titulo, String descripcion, Date fechaLimite, Date fechaCreacion, Date fechaEntregaReal, String estado, String prioridad, String asignatura, Boolean recordatorioActivo, Date fechaRecordatorio, Usuario usuarioId) {
        Entrega e = new Entrega();
        e.setId(id); e.setTitulo(titulo); e.setDescripcion(descripcion); e.setFechaLimite(fechaLimite);
        e.setFechaCreacion(fechaCreacion); e.setFechaEntregaReal(fechaEntregaReal); e.setEstado(estado);
        e.setPrioridad(prioridad); e.setAsignatura(asignatura); e.setRecordatorioActivo(recordatorioActivo);
        e.setFechaRecordatorio(fechaRecordatorio); e.setUsuarioId(usuarioId);
        return e;
    }

    private List<Examen> simulateUserExams(Integer userId) {
        List<Examen> simulated = new ArrayList<>();
        if (userId != null) {
            Usuario u = new Usuario(); u.setId(userId);
            simulated.add(createSimulatedExamen(101, "Examen de Fisica", "Temas 1-3", new Date(System.currentTimeMillis() + 86400000 * 4), new Date(), "Fisica", "Parcial", "Alta", true, new Date(System.currentTimeMillis() + 86400000 * 3), u));
            simulated.add(createSimulatedExamen(102, "Examen de Quimica", "Organica I", new Date(System.currentTimeMillis() + 86400000 * 9), new Date(), "Quimica", "Final", "Media", false, null, u));
            simulated.add(createSimulatedExamen(103, "Quiz de Ingles", "Vocabulario Unidad 5", new Date(System.currentTimeMillis() + 86400000 * 2), new Date(), "Ingles", "Quiz", "Baja", true, new Date(System.currentTimeMillis() + 86400000 * 1), u));
            simulated.add(createSimulatedExamen(104, "Examen pasado", "Ya evaluado", new Date(System.currentTimeMillis() - 86400000 * 5), new Date(), "General", "Final", "Alta", false, null, u));
        }
        return simulated;
    }

    private Examen createSimulatedExamen(Integer id, String nombre, String descripcion, Date fechaExamen, Date fechaCreacion, String asignatura, String tipoExamen, String prioridad, Boolean recordatorioActivo, Date fechaRecordatorio, Usuario usuarioId) {
        Examen ex = new Examen();
        ex.setId(id); ex.setNombre(nombre); ex.setDescripcion(descripcion); ex.setFechaExamen(fechaExamen);
        ex.setFechaCreacion(fechaCreacion); ex.setAsignatura(asignatura); ex.setTipoExamen(tipoExamen);
        ex.setPrioridad(prioridad); ex.setRecordatorioActivo(recordatorioActivo);
        ex.setFechaRecordatorio(fechaRecordatorio); ex.setUsuarioId(usuarioId);
        return ex;
    }
    // --- FIN SIMULACIÓN DE DATOS ---
}
