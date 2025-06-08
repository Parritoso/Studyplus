/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.services;

import com.estudyplus.controlador.services.enums.SessionState;
import com.estudyplus.controlador.services.exception.InvalidSessionStateException;
import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.controlador.services.exception.SessionNotFoundException;
import com.estudyplus.controlador.services.exception.UserNotFoundException;
import com.estudyplus.modelo.entitys.LogProductividad;
import com.estudyplus.modelo.entitys.SesionEstudio;
import com.estudyplus.modelo.entitys.TecnicaEstudio;
import com.estudyplus.modelo.entitys.Usuario;
import com.estudyplus.modelo.rest.facade.EstudyRestFacade;
import com.estudyplus.modelo.rest.facade.EstudyRestFacadeFactory;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.ws.rs.ClientErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Propósito: Gestionar la creación, seguimiento y finalización de las sesiones de estudio.
 * @see com.estudyplus.modelo.rest.facade.EstudyRestFacade
 * @author Parri
 */
public class StudySessionService {
    
    private static final Logger logger = LoggerFactory.getLogger(StudySessionService.class);
    private final EstudyRestFacade facade;
    private final UserService userService; // Necesario para verificar si el usuario existe

    // Mapa para mantener el estado en memoria de las sesiones activas/pausadas
    // Esto es una simplificación. En un entorno de producción, esto requeriría persistencia
    // o un sistema de mensajería para mantener el estado entre instancias del servidor.
    //private final ConcurrentMap<Integer, Long> activeSessionStartTimes = new ConcurrentHashMap<>();
    //private final ConcurrentMap<Integer, Long> pausedSessionStartTimes = new ConcurrentHashMap<>();
    //private final ConcurrentMap<Integer, Long> sessionEffectiveStudyTime = new ConcurrentHashMap<>(); // Tiempo acumulado de estudio
    //private final ConcurrentMap<Integer, Long> sessionBreakTime = new ConcurrentHashMap<>(); // Tiempo acumulado de descanso
    
    public StudySessionService(EstudyRestFacade facade, UserService userService) {
        if (facade == null || userService == null) {
            throw new IllegalArgumentException("EstudyRestFacade y UserService no pueden ser nulos.");
        }
        this.facade = facade;
        this.userService = userService;
    }
    
    
    // Constructor que usa la factoría (para uso sin inyección explícita)
    public StudySessionService() {
        this(EstudyRestFacadeFactory.getInstance(), new UserService(EstudyRestFacadeFactory.getInstance()));
    }
    /**
     * Valida los datos de la sesión y delega la creación a EstudyRestFacade.
     * Gestiona el estado inicial y los tiempos basándose en `startImmediately`.
     *
     * @param session El objeto SesionEstudio con los datos iniciales. Debe incluir el ID del usuario propietario.
     * @param startImmediately Indica si la sesión debe iniciarse inmediatamente (true) o programarse (false).
     * @return La SesionEstudio creada (con el ID asignado por la DB).
     * @throws IllegalArgumentException Si los datos de la sesión son inválidos.
     * @throws UserNotFoundException Si el usuario propietario de la sesión no existe.
     * @throws ServiceException Si ocurre un error de comunicación o inesperado.
     */
    public SesionEstudio createSession(SesionEstudio session, boolean startImmediately) throws ServiceException, UserNotFoundException, IllegalArgumentException {
        if (session == null || session.getUsuarioId() == null || session.getUsuarioId().getId() == null || session.getUsuarioId().getId() <= 0) {
            logger.warn("Intento de creación de sesión con datos incompletos o ID de usuario inválido.");
            throw new IllegalArgumentException("La sesión debe tener un usuario propietario válido.");
        }
        if (session.getTitulo() == null || session.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la sesión de estudio no puede estar vacío.");
        }
        if (session.getDuracionPlanificadaMinutos() == null || session.getDuracionPlanificadaMinutos() <= 0) {
            throw new IllegalArgumentException("La duración planificada debe ser positiva.");
        }

        try {
            Usuario owner = userService.getUserById(session.getUsuarioId().getId());
            if (owner == null) {
                throw new UserNotFoundException("El usuario con ID " + session.getUsuarioId().getId() + " no existe.");
            }
            session.setUsuarioId(owner);

            if (session.getTecnicaAplicadaId() != null && session.getTecnicaAplicadaId().getId() == null) {
                session.setTecnicaAplicadaId(null); // Asegurarse de que sea null si no se selecciona
            }
            
            if(session.getEntregaAsociada()!=null && session.getEntregaAsociada().getId() != null && session.getEntregaAsociada().getId() > 0){
                session.setEntregaAsociada(facade.getEntregaById(session.getEntregaAsociada().getId()));
            }
            
            if(session.getExamenAsociado()!=null && session.getExamenAsociado().getId() != null && session.getExamenAsociado().getId() > 0){
                session.setExamenAsociado(facade.getExamenById(session.getExamenAsociado().getId()));
            }

            // Establecer estado inicial y fechas
            session.setFechaCreacion(new Date()); // Fecha de creación de la entrada
            session.setDuracionRealMinutos(0); // Inicializar a 0 (no null)
            session.setTiempoEstudioEfectivoMinutos(0); // Inicializar a 0 (no null)
            session.setTiempoDescansoMinutos(0); // Inicializar a 0 (no null)
            session.setCalificacionEnfoque(0); // Inicializar a 0 (no null)

            if (startImmediately) {
                session.setEstado(SessionState.ACTIVE.name());
                session.setFechaInicioReal(new Date());
                // Si se inicia inmediatamente, la fecha planificada podría ser la actual o nula
                // Depende de la lógica de negocio, si una sesión "iniciada inmediatamente" no tuvo planificación previa.
                // Aquí la establecemos como la fecha de inicio real para evitar nulls innecesarios en ciertas consultas.
                session.setFechaSesionPlanificada(session.getFechaInicioReal());
                session.setFechaUltimaActualizacionEstado(new Date()); // Primera actualización de estado
            } else {
                if (session.getFechaSesionPlanificada() == null) {
                    throw new IllegalArgumentException("La fecha de sesión planificada no puede ser nula si no se inicia inmediatamente.");
                }
                session.setEstado(SessionState.PLANNED.name());
                session.setFechaInicioReal(null); // No se ha iniciado aún
                session.setFechaFinReal(null); // No se ha finalizado aún
                session.setFechaUltimaActualizacionEstado(new Date()); // Se establece al planificarse
            }

            SesionEstudio createdSession = facade.createSesionEstudio(session, startImmediately); // Asumimos este método en EstudyRestFacade
            if (createdSession == null) {
                throw new ServiceException("No se pudo crear la sesión de estudio.");
            }

            logger.info("Sesión de estudio creada con éxito: ID {}, Título: '{}' para usuario ID {}.",
                    createdSession.getId(), createdSession.getTitulo(), createdSession.getUsuarioId().getId());
            return createdSession;
        } catch (UserNotFoundException e) {
            throw e;
        } catch (ClientErrorException | IOException e) {
            logger.error("Error de comunicación al crear sesión: {}", e.getMessage(), e);
            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al crear sesión: {}", e.getMessage(), e);
            throw new ServiceException("Error inesperado al crear sesión.", e);
        }
    }

    /**
     * Marca una sesión como iniciada o la reanuda desde el estado PAUSED.
     * Actualiza `fechaInicioReal` si es la primera vez que se inicia,
     * y acumula el `tiempoDescansoMinutos` si viene de PAUSED.
     *
     * @param sessionId El ID de la sesión a iniciar.
     * @return La SesionEstudio actualizada a estado ACTIVO.
     * @throws SessionNotFoundException Si la sesión no existe.
     * @throws InvalidSessionStateException Si la sesión no está en estado PLANNED o PAUSED.
     * @throws ServiceException Si ocurre un error inesperado.
     */
    public SesionEstudio startSession(int sessionId) throws ServiceException, SessionNotFoundException, InvalidSessionStateException {
        if (sessionId <= 0) {
            throw new IllegalArgumentException("ID de sesión inválido.");
        }

        try {
            SesionEstudio session = facade.getSesionEstudioById(sessionId);
            if (session == null) {
                logger.warn("Intento de inicio de sesión fallido: ID {} no encontrado.", sessionId);
                throw new SessionNotFoundException("Sesión con ID " + sessionId + " no encontrada.");
            }

            if (!SessionState.PLANNED.name().equals(session.getEstado()) &&
                !SessionState.PAUSED.name().equals(session.getEstado()) && !SessionState.ACTIVE.name().equals(session.getEstado())) {
                logger.warn("Intento de inicio de sesión ID {} en estado inválido: {}. Debe ser PLANNED o PAUSED.", sessionId, session.getEstado());
                throw new InvalidSessionStateException("La sesión debe estar en estado 'PLANNED' o 'PAUSED' para iniciarla. Estado actual: " + session.getEstado());
            }

            Date now = new Date();

            // Si estaba en PAUSED, calcular el tiempo de descanso acumulado
            if (SessionState.PAUSED.name().equals(session.getEstado())) {
                if (session.getFechaUltimaActualizacionEstado() != null) {
                    long pauseMillis = now.getTime() - session.getFechaUltimaActualizacionEstado().getTime();
                    session.setTiempoDescansoMinutos(
                        session.getTiempoDescansoMinutos() + (int) (pauseMillis / (1000 * 60))
                    );
                } else {
                    logger.warn("Sesión en PAUSED sin fechaUltimaActualizacionEstado para ID {}", sessionId);
                }
            } else if (SessionState.PLANNED.name().equals(session.getEstado())) {
                // Si es la primera vez que se inicia, establecer fechaInicioReal
                if (session.getFechaInicioReal() == null) {
                    session.setFechaInicioReal(now);
                }
            }
            
            // Actualizar el estado y la fecha de última actualización
            session.setEstado(SessionState.ACTIVE.name());
            session.setFechaUltimaActualizacionEstado(now);

            facade.startSession(session);

            SesionEstudio updatedSession = facade.getSesionEstudioById(sessionId);
            logger.info("Sesión ID {} iniciada/reanudada con éxito. Usuario ID: {}. Estado: {}.", sessionId, session.getUsuarioId().getId(), updatedSession.getEstado());
            return updatedSession;
        } catch (SessionNotFoundException | InvalidSessionStateException | IllegalArgumentException e) {
            throw e;
        } catch (ClientErrorException | IOException e) {
            logger.error("Error de comunicación al iniciar sesión {}: {}", sessionId, e.getMessage(), e);
            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al iniciar sesión {}: {}", sessionId, e.getMessage(), e);
            throw new ServiceException("Error inesperado al iniciar sesión.", e);
        }
    }

    /**
     * Marca una sesión como pausada, calcula el tiempo de estudio efectivo transcurrido
     * desde la última actualización de estado (inicio/reanudación).
     *
     * @param sessionId El ID de la sesión a pausar.
     * @return La SesionEstudio actualizada a estado PAUSED.
     * @throws SessionNotFoundException Si la sesión no existe.
     * @throws InvalidSessionStateException Si la sesión no está en estado ACTIVO.
     * @throws ServiceException Si ocurre un error inesperado.
     */
    public SesionEstudio pauseSession(int sessionId) throws ServiceException, SessionNotFoundException, InvalidSessionStateException {
        if (sessionId <= 0) {
            throw new IllegalArgumentException("ID de sesión inválido.");
        }

        try {
            SesionEstudio session = facade.getSesionEstudioById(sessionId);
            if (session == null) {
                logger.warn("Intento de pausa de sesión fallido: ID {} no encontrado.", sessionId);
                throw new SessionNotFoundException("Sesión con ID " + sessionId + " no encontrada.");
            }

            if (!SessionState.ACTIVE.name().equals(session.getEstado())) {
                logger.warn("Intento de pausa de sesión ID {} en estado inválido: {}. Debe ser ACTIVE.", sessionId, session.getEstado());
                throw new InvalidSessionStateException("La sesión debe estar en estado 'ACTIVE' para pausarla. Estado actual: " + session.getEstado());
            }

            Date now = new Date();

            // Calcular el tiempo de estudio efectivo transcurrido desde la última actualización (ACTIVE)
            if (session.getFechaUltimaActualizacionEstado() != null) {
                long studyMillis = now.getTime() - session.getFechaUltimaActualizacionEstado().getTime();
                session.setTiempoEstudioEfectivoMinutos(
                    session.getTiempoEstudioEfectivoMinutos() + (int) (studyMillis / (1000 * 60))
                );
            } else {
                logger.warn("Sesión en ACTIVE sin fechaUltimaActualizacionEstado para ID {}", sessionId);
            }

            // Actualizar el estado y la fecha de última actualización
            session.setEstado(SessionState.PAUSED.name());
            session.setFechaUltimaActualizacionEstado(now);

            SesionEstudio updatedSession = session;
            
            facade.pauseSession(session);

            logger.info("Sesión ID {} pausada con éxito. Tiempo efectivo acumulado: {} minutos.", sessionId, updatedSession.getTiempoEstudioEfectivoMinutos());
            return updatedSession;
        } catch (SessionNotFoundException | InvalidSessionStateException | IllegalArgumentException e) {
            throw e;
        } catch (ClientErrorException | IOException e) {
            logger.error("Error de comunicación al pausar sesión {}: {}", sessionId, e.getMessage(), e);
            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al pausar sesión {}: {}", sessionId, e.getMessage(), e);
            throw new ServiceException("Error inesperado al pausar sesión.", e);
        }
    }
    
    /**
     * Finaliza una sesión, calculando los tiempos finales y actualizando el estado.
     * Puede finalizar una sesión en estado PLANNED, ACTIVE o PAUSED.
     *
     * @param sessionId El ID de la sesión a finalizar.
     * @param completionData DTO con notas, calificación de enfoque, etc.
     * @return La SesionEstudio actualizada a estado COMPLETED o ABORTED.
     * @throws SessionNotFoundException Si la sesión no existe.
     * @throws InvalidSessionStateException Si la sesión no está en un estado válido para finalizar.
     * @throws IllegalArgumentException Si los datos de finalización son inválidos.
     * @throws ServiceException Si ocurre un error inesperado.
     */
    public SesionEstudio endSession(int sessionId, SessionCompletionData completionData) throws ServiceException, SessionNotFoundException, InvalidSessionStateException, IllegalArgumentException {
        if (sessionId <= 0) {
            throw new IllegalArgumentException("ID de sesión inválido.");
        }
        if (completionData == null) {
            throw new IllegalArgumentException("Los datos de finalización de la sesión no pueden ser nulos.");
        }

        try {
            SesionEstudio session = facade.getSesionEstudioById(sessionId);
            if (session == null) {
                logger.warn("Intento de finalización de sesión fallido: ID {} no encontrado.", sessionId);
                throw new SessionNotFoundException("Sesión con ID " + sessionId + " no encontrada.");
            }

            // Permitir finalizar desde PLANNED, ACTIVE, PAUSED
            if (!SessionState.PLANNED.name().equals(session.getEstado()) &&
                !SessionState.ACTIVE.name().equals(session.getEstado()) &&
                !SessionState.PAUSED.name().equals(session.getEstado())) {
                logger.warn("Intento de finalización de sesión ID {} en estado inválido: {}.", sessionId, session.getEstado());
                throw new InvalidSessionStateException("La sesión debe estar en estado 'PLANNED', 'ACTIVE' o 'PAUSED' para finalizarla. Estado actual: " + session.getEstado());
            }

            Date now = new Date();

            // Calcular tiempos acumulados antes de finalizar
            if (SessionState.ACTIVE.name().equals(session.getEstado())) {
                if (session.getFechaUltimaActualizacionEstado() != null) {
                    long studyMillis = now.getTime() - session.getFechaUltimaActualizacionEstado().getTime();
                    session.setTiempoEstudioEfectivoMinutos(
                        session.getTiempoEstudioEfectivoMinutos() + (int) (studyMillis / (1000 * 60))
                    );
                }
            } else if (SessionState.PAUSED.name().equals(session.getEstado())) {
                if (session.getFechaUltimaActualizacionEstado() != null) {
                    long pauseMillis = now.getTime() - session.getFechaUltimaActualizacionEstado().getTime();
                    session.setTiempoDescansoMinutos(
                        session.getTiempoDescansoMinutos() + (int) (pauseMillis / (1000 * 60))
                    );
                }
            }
            // Si estaba en PLANNED y se aborta, los tiempos efectivos y de descanso se mantienen en 0.

            // Establecer fecha de fin real (solo si la sesión no estaba PLANNED y se aborta directamente)
            // Si la sesión fue al menos ACTIVE o PAUSED, la fechaFinReal se establece a 'now'.
            // Si se aborta una sesión PLANNED sin inicio real, fechaFinReal podría ser null o 'now'
            // Decidimos establecerla a 'now' para cualquier finalización (completed/aborted).
            session.setFechaFinReal(now);

            // Calcular duración total real si la sesión llegó a tener una fecha de inicio real
            if (session.getFechaInicioReal() != null) {
                long totalDurationMillis = session.getFechaFinReal().getTime() - session.getFechaInicioReal().getTime();
                session.setDuracionRealMinutos((int) (totalDurationMillis / (1000 * 60)));
            } else {
                session.setDuracionRealMinutos(0); // Si nunca se inició realmente
            }

            // Actualizar la entidad SesionEstudio con los datos de finalización
            session.setNotas(completionData.getNotes());
            session.setCalificacionEnfoque(completionData.getFocusRating());
            session.setDetallesInterrupcion(completionData.getInterruptionDetails());

            // Determinar el estado final de la sesión
            if (completionData.getOutcome() != null && completionData.getOutcome().equalsIgnoreCase("aborted")) {
                session.setEstado(SessionState.ABORTED.name());
            } else {
                session.setEstado(SessionState.COMPLETED.name());
            }
            session.setFechaUltimaActualizacionEstado(now); // Última actualización de estado

            SesionEstudio updatedSession = facade.updateSesionEstudio(session); // Actualizar en la DB

            // Registrar log de productividad
            if (session.getUsuarioId() != null && (session.getEstado().equals(SessionState.COMPLETED.name()) || session.getEstado().equals(SessionState.ABORTED.name()))) {
                LogProductividad log = new LogProductividad();
                log.setUsuarioId(session.getUsuarioId());
                log.setSesionEstudioId(session);
                log.setFechaRegistro(new Date());
                log.setDuracionEstudioTotalMinutos(session.getTiempoEstudioEfectivoMinutos());
                log.setDuracionDescansoTotalMinutos(session.getTiempoDescansoMinutos());
                log.setCalificacionEnfoque(session.getCalificacionEnfoque());
                log.setNotas(session.getNotas());
                log.setTipoEvento("SESION_FINALIZADA_" + session.getEstado());

                facade.createLogProductividad(log); // Este método debe ser implementado en EstudyRestFacade
                logger.info("Log de productividad registrado para sesión ID {} con estado {}.", sessionId, session.getEstado());
            }

            logger.info("Sesión ID {} finalizada con éxito. Estado: {}. Estudio efectivo: {} min, Descanso: {} min, Duración total: {} min.",
                    sessionId, session.getEstado(), session.getTiempoEstudioEfectivoMinutos(),
                    session.getTiempoDescansoMinutos(), session.getDuracionRealMinutos());
            return updatedSession;
        } catch (SessionNotFoundException | InvalidSessionStateException | IllegalArgumentException e) {
            throw e;
        } catch (ClientErrorException | IOException e) {
            logger.error("Error de comunicación al finalizar sesión {}: {}", sessionId, e.getMessage(), e);
            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al finalizar sesión {}: {}", sessionId, e.getMessage(), e);
            throw new ServiceException("Error inesperado al finalizar sesión.", e);
        }
    }

    /**
     * Obtiene el tiempo efectivo de estudio acumulado para una sesión.
     * Esta función debe buscar la sesión en la base de datos para obtener los valores más recientes.
     * Calcula el tiempo efectivo actual si la sesión está ACTIVA.
     *
     * @param sessionId ID de la sesión.
     * @return Tiempo efectivo de estudio en milisegundos.
     * @throws SessionNotFoundException Si la sesión no existe.
     * @throws ServiceException Si ocurre un error inesperado.
     */
    public long getCurrentEffectiveStudyTime(int sessionId) throws ServiceException, SessionNotFoundException {
        try {
            SesionEstudio session = facade.getSesionEstudioById(sessionId);
            if (session == null) {
                throw new SessionNotFoundException("Sesión con ID " + sessionId + " no encontrada.");
            }

            long effectiveMillis = (long) session.getTiempoEstudioEfectivoMinutos() * 60 * 1000;

            // Si la sesión está actualmente activa, añadir el tiempo transcurrido desde la última actualización
            if (SessionState.ACTIVE.name().equals(session.getEstado()) && session.getFechaUltimaActualizacionEstado() != null) {
                effectiveMillis += System.currentTimeMillis() - session.getFechaUltimaActualizacionEstado().getTime();
            }
            return effectiveMillis;
        } catch (ClientErrorException | IOException e) {
            logger.error("Error de comunicación al obtener tiempo de estudio efectivo {}: {}", sessionId, e.getMessage(), e);
            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al obtener tiempo de estudio efectivo {}: {}", sessionId, e.getMessage(), e);
            throw new ServiceException("Error inesperado al obtener tiempo de estudio efectivo.", e);
        }
    }

    /**
     * Obtiene el tiempo de descanso acumulado para una sesión.
     * Esta función debe buscar la sesión en la base de datos para obtener los valores más recientes.
     * Calcula el tiempo de descanso actual si la sesión está PAUSADA.
     *
     * @param sessionId ID de la sesión.
     * @return Tiempo de descanso en milisegundos.
     * @throws SessionNotFoundException Si la sesión no existe.
     * @throws ServiceException Si ocurre un error inesperado.
     */
    public long getCurrentBreakTime(int sessionId) throws ServiceException, SessionNotFoundException {
        try {
            SesionEstudio session = facade.getSesionEstudioById(sessionId);
            if (session == null) {
                throw new SessionNotFoundException("Sesión con ID " + sessionId + " no encontrada.");
            }

            long breakMillis = (long) session.getTiempoDescansoMinutos() * 60 * 1000;

            // Si la sesión está actualmente pausada, añadir el tiempo transcurrido desde la última actualización
            if (SessionState.PAUSED.name().equals(session.getEstado()) && session.getFechaUltimaActualizacionEstado() != null) {
                breakMillis += System.currentTimeMillis() - session.getFechaUltimaActualizacionEstado().getTime();
            }
            return breakMillis;
        } catch (ClientErrorException | IOException e) {
            logger.error("Error de comunicación al obtener tiempo de descanso {}: {}", sessionId, e.getMessage(), e);
            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al obtener tiempo de descanso {}: {}", sessionId, e.getMessage(), e);
            throw new ServiceException("Error inesperado al obtener tiempo de descanso.", e);
        }
    }

    /**
     * Delega la búsqueda a EstudyRestFacade.
     *
     * @param userId El ID del usuario.
     * @param startDate La fecha de inicio del rango (inclusive).
     * @param endDate La fecha de fin del rango (inclusive).
     * @return Una lista de SesionEstudio.
     * @throws IllegalArgumentException Si el ID de usuario o las fechas son inválidas.
     * @throws UserNotFoundException Si el usuario no existe.
     * @throws ServiceException Si ocurre un error inesperado.
     */
    public List<SesionEstudio> getSessionsForUser(int userId, Date startDate, Date endDate) throws ServiceException, UserNotFoundException, IllegalArgumentException {
        if (userId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido.");
        }
        if (startDate == null || endDate == null || startDate.after(endDate)) {
            throw new IllegalArgumentException("Rango de fechas inválido.");
        }

        try {
            Usuario owner = userService.getUserById(userId);
            if (owner == null) {
                throw new UserNotFoundException("El usuario con ID " + userId + " no existe.");
            }

            List<SesionEstudio> sessions = facade.getSesionesEstudioByUserIdAndDateRange(userId, startDate, endDate);
            logger.info("Recuperadas {} sesiones para el usuario ID {} entre {} y {}.",
                    sessions != null ? sessions.size() : 0, userId, startDate, endDate);
            return sessions;
        } catch (UserNotFoundException e) {
            throw e;
        } catch (ClientErrorException | IOException e) {
            logger.error("Error de comunicación al obtener sesiones para usuario {}: {}", userId, e.getMessage(), e);
            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al obtener sesiones para usuario {}: {}", userId, e.getMessage(), e);
            throw new ServiceException("Error inesperado al obtener sesiones.", e);
        }
    }

    /**
     * Obtiene una sesión por su ID. Método auxiliar.
     *
     * @param sessionId
     * @return
     * @throws SessionNotFoundException
     * @throws ServiceException
     */
    public SesionEstudio getSessionById(int sessionId) throws ServiceException, SessionNotFoundException {
        try {
            SesionEstudio session = facade.getSesionEstudioById(sessionId);
            if (session == null) {
                throw new SessionNotFoundException("Sesión con ID " + sessionId + " no encontrada.");
            }
            return session;
        } catch (ClientErrorException | IOException e) {
            logger.error("Error de comunicación al obtener sesión {}: {}", sessionId, e.getMessage(), e);
            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al obtener sesión {}: {}", sessionId, e.getMessage(), e);
            throw new ServiceException("Error inesperado al obtener sesión.", e);
        }
    }

    /**
     * Actualiza una sesión de estudio existente.
     * Delega la actualización a EstudyRestFacade.
     *
     * @param session El objeto SesionEstudio con los datos actualizados.
     * @return La SesionEstudio actualizada.
     * @throws IllegalArgumentException Si los datos de la sesión son inválidos o el ID es nulo.
     * @throws SessionNotFoundException Si la sesión no existe.
     * @throws ServiceException Si ocurre un error de comunicación o inesperado.
     */
    public SesionEstudio updateSesionEstudio(SesionEstudio session) throws ServiceException, SessionNotFoundException, IllegalArgumentException {
        if (session == null || session.getId() == null || session.getId() <= 0) {
            throw new IllegalArgumentException("La sesión a actualizar debe tener un ID válido.");
        }
        if (session.getTitulo() == null || session.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la sesión de estudio no puede estar vacío.");
        }
        if (session.getDuracionPlanificadaMinutos() == null || session.getDuracionPlanificadaMinutos() <= 0) {
            throw new IllegalArgumentException("La duración planificada debe ser positiva.");
        }

        try {
            // Opcional: Obtener la sesión existente para verificar el estado o permisos antes de actualizar
            SesionEstudio existingSession = facade.getSesionEstudioById(session.getId());
            if (existingSession == null) {
                throw new SessionNotFoundException("Sesión con ID " + session.getId() + " no encontrada para actualizar.");
            }

            SesionEstudio updatedSession = facade.updateSesionEstudio(session);
            if (updatedSession == null) {
                throw new ServiceException("No se pudo actualizar la sesión de estudio.");
            }
            logger.info("Sesión de estudio ID {} actualizada con éxito.", updatedSession.getId());
            return updatedSession;
        } catch (SessionNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (ClientErrorException | IOException e) {
            logger.error("Error de comunicación al actualizar sesión {}: {}", session.getId(), e.getMessage(), e);
            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar sesión {}: {}", session.getId(), e.getMessage(), e);
            throw new ServiceException("Error inesperado al actualizar sesión.", e);
        }
    }

    /**
     * Método para eliminar una sesión.
     *
     * @param sessionId
     * @throws SessionNotFoundException
     * @throws ServiceException
     */
    public void deleteSession(int sessionId) throws ServiceException, SessionNotFoundException, IllegalArgumentException {
        if (sessionId <= 0) {
            throw new IllegalArgumentException("ID de sesión inválido.");
        }
        try {
            SesionEstudio session = facade.getSesionEstudioById(sessionId);
            if (session == null) {
                throw new SessionNotFoundException("Sesión con ID " + sessionId + " no encontrada para eliminar.");
            }
            facade.deleteSesionEstudio(sessionId);
            logger.info("Sesión ID {} eliminada con éxito.", sessionId);
        } catch (ClientErrorException | IOException e) {
            logger.error("Error de comunicación al eliminar sesión {}: {}", sessionId, e.getMessage(), e);
            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar sesión {}: {}", sessionId, e.getMessage(), e);
            throw new ServiceException("Error inesperado al eliminar sesión.", e);
        }
    }

//    /**
//     * Valida los datos de la sesión
//     * Delega la creación a EstudyRestFacade.
//     * @param session El objeto SesionEstudio con los datos iniciales. Debe incluir el ID del usuario propietario.
//     * @param startImmediately Indica si la sesión debe iniciarse inmediatamente (true) o programarse (false).
//     * @return La SesionEstudio creada (con el ID asignado por la DB).
//     * @throws IllegalArgumentException Si los datos de la sesión son inválidos.
//     * @throws UserNotFoundException Si el usuario propietario de la sesión no existe.
//     * @throws ServiceException Si ocurre un error de comunicación o inesperado.
//     */
//    public SesionEstudio createSession(SesionEstudio session, boolean startImmediately){
//        if (session == null || session.getUsuarioId() == null || session.getUsuarioId().getId() <= 0) {
//            logger.warn("Intento de creación de sesión con datos incompletos o ID de usuario inválido.");
//            throw new IllegalArgumentException("La sesión debe tener un usuario propietario válido.");
//        }
//        if (session.getTitulo() == null || session.getTitulo().trim().isEmpty()) {
//            throw new IllegalArgumentException("El título de la sesión de estudio no puede estar vacío.");
//        }
//        if (session.getDuracionPlanificadaMinutos() == null || session.getDuracionPlanificadaMinutos() <= 0) {
//            throw new IllegalArgumentException("La duración planificada debe ser positiva.");
//        }
//
//        try {
//            // Verificar que el usuario propietario existe
//            Usuario owner = userService.getUserById(session.getUsuarioId().getId());
//            if (owner == null) {
//                throw new UserNotFoundException("El usuario con ID " + session.getUsuarioId() + " no existe.");
//            }
//            
//            session.setUsuarioId(owner);
//
//            if (session.getTecnicaAplicadaId() != null && session.getTecnicaAplicadaId().getId() != null) {
//                // Opcional: Verificar que la técnica de estudio exista realmente en la DB.
//                // Esto requeriría un método en TecnicaEstudioService/Facade.
//                // Por ahora, asumimos que el ID es válido si se proporciona.
//                logger.debug("Sesión asociada a técnica de estudio ID: {}", session.getTecnicaAplicadaId().getId());
//            } else {
//                session.setTecnicaAplicadaId(null); // Asegurarse de que sea null si no se selecciona
//            }
//            
//            // 3. Establecer estado inicial y fechas según si se inicia inmediatamente o se programa
//            session.setFechaCreacion(new Date()); // Fecha de creación de la entrada
//            session.setDuracionRealMinutos(0); // Inicializar
//            session.setTiempoEstudioEfectivoMinutos(0); // Inicializar
//            session.setTiempoDescansoMinutos(0); // Inicializar
//            session.setCalificacionEnfoque(0); // Inicializar
//            
//            if (startImmediately) {
//                session.setEstado(SessionState.ACTIVE.name());
//                session.setFechaInicioReal(new Date());
//                session.setFechaSesionPlanificada(new Date()); // Si se inicia inmediatamente, no hay fecha planificada futura
//            } else {
//                // Si no se inicia inmediatamente, debe tener una fecha planificada
//                if (session.getFechaSesionPlanificada() == null) {
//                     throw new IllegalArgumentException("La fecha de sesión planificada no puede ser nula si no se inicia inmediatamente.");
//                }
//                session.setEstado(SessionState.PLANNED.name());
//                session.setFechaInicioReal(null); // No se ha iniciado aún
//            }
//
//            // Si hay asociación con entrega/examen, verificar su existencia si es necesario.
//            // Para esta implementación básica, solo se verifica que los IDs estén presentes si se asignan.
//            // La validación de existencia de Entrega/Examen se podría hacer llamando a TaskService.
//
//            SesionEstudio createdSession = facade.createSesionEstudio(session, startImmediately); // Asumimos este método en EstudyRestFacade
//            if (createdSession == null) {
//                throw new ServiceException("No se pudo crear la sesión de estudio.");
//            }
//            
//            // Si se inició inmediatamente, registrar el tiempo de inicio en memoria
//            if (startImmediately) {
//                activeSessionStartTimes.put(createdSession.getId(), System.currentTimeMillis());
//                sessionEffectiveStudyTime.put(createdSession.getId(), 0L);
//                sessionBreakTime.put(createdSession.getId(), 0L);
//            }
//            
//            logger.info("Sesión de estudio creada con éxito: ID {}, Título: '{}' para usuario ID {}.",
//                    createdSession.getId(), createdSession.getTitulo(), createdSession.getUsuarioId());
//            return createdSession;
//        } catch (UserNotFoundException e) {
//            throw e; // Propagar si el usuario no existe
//        } catch (ClientErrorException | IOException e) {
//            logger.error("Error de comunicación al crear sesión: {}", e.getMessage(), e);
//            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
//        } catch (Exception e) {
//            logger.error("Error inesperado al crear sesión: {}", e.getMessage(), e);
//            throw new ServiceException("Error inesperado al crear sesión.", e);
//        }
//    }
//    
//    /**
//     * Marca una sesión como iniciada
//     * Podría iniciar un temporizador lógico interno o registrar el evento.
//     * @param sessionId El ID de la sesión a iniciar.
//     * @return La SesionEstudio actualizada a estado ACTIVO.
//     * @throws SessionNotFoundException Si la sesión no existe.
//     * @throws InvalidSessionStateException Si la sesión no está en estado PLANNED o PAUSED.
//     * @throws ServiceException Si ocurre un error inesperado.
//     */
//    public SesionEstudio startSession(int sessionId){
//        if (sessionId <= 0) {
//            throw new IllegalArgumentException("ID de sesión inválido.");
//        }
//
//        try {
//            SesionEstudio session = facade.getSesionEstudioById(sessionId); // Asumimos este método en EstudyRestFacade
//            if (session == null) {
//                logger.warn("Intento de inicio de sesión fallido: ID {} no encontrado.", sessionId);
//                throw new SessionNotFoundException("Sesión con ID " + sessionId + " no encontrada.");
//            }
//
//            if (!SessionState.PLANNED.name().equals(session.getEstado()) &&
//                !SessionState.PAUSED.name().equals(session.getEstado()) &&
//                !SessionState.ACTIVE.name().equals(session.getEstado())) {
//                logger.warn("Intento de inicio de sesión ID {} en estado inválido: {}.", sessionId, session.getEstado());
//                throw new InvalidSessionStateException("La sesión debe estar en estado 'PLANNED' o 'PAUSED' para iniciarla. Estado actual: " + session.getEstado());
//            }
//
//            if (SessionState.PLANNED.name().equals(session.getEstado())) {
//                session.setFechaInicioReal(new Date());
//                // Inicializar tiempos si es la primera vez que se inicia
//                sessionEffectiveStudyTime.put(sessionId, 0L);
//                sessionBreakTime.put(sessionId, 0L);
//            }
//            // Si estaba en PAUSED, el effectiveStudyTime y breakTime ya deberían tener los acumulados.
//
//            session.setEstado(SessionState.ACTIVE.name());
//            SesionEstudio updatedSession = facade.updateSesionEstudio(session); // Asumimos este método en EstudyRestFacade
//
//            activeSessionStartTimes.put(sessionId, System.currentTimeMillis());
//            pausedSessionStartTimes.remove(sessionId); // Asegurarse de que no esté en pausadas
//
//            logger.info("Sesión ID {} iniciada/reanudada con éxito. Usuario ID: {}.", sessionId, session.getUsuarioId());
//            return updatedSession;
//        } catch (SessionNotFoundException | InvalidSessionStateException e) {
//            throw e;
//        } catch (ClientErrorException | IOException e) {
//            logger.error("Error de comunicación al iniciar sesión {}: {}", sessionId, e.getMessage(), e);
//            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
//        } catch (Exception e) {
//            logger.error("Error inesperado al iniciar sesión {}: {}", sessionId, e.getMessage(), e);
//            throw new ServiceException("Error inesperado al iniciar sesión.", e);
//        }
//    }
//    
//    /**
//     * Marca una sesión como pausada, calcula el tiempo transcurrido.
//     * @param sessionId El ID de la sesión a pausar.
//     * @return La SesionEstudio actualizada a estado PAUSED.
//     * @throws SessionNotFoundException Si la sesión no existe.
//     * @throws InvalidSessionStateException Si la sesión no está en estado ACTIVO.
//     * @throws ServiceException Si ocurre un error inesperado.
//     */
//    public SesionEstudio pauseSession(int sessionId){
//        if (sessionId <= 0) {
//            throw new IllegalArgumentException("ID de sesión inválido.");
//        }
//
//        try {
//            SesionEstudio session = facade.getSesionEstudioById(sessionId);
//            if (session == null) {
//                logger.warn("Intento de pausa de sesión fallido: ID {} no encontrado.", sessionId);
//                throw new SessionNotFoundException("Sesión con ID " + sessionId + " no encontrada.");
//            }
//
//            if (!SessionState.ACTIVE.name().equals(session.getEstado())) {
//                logger.warn("Intento de pausa de sesión ID {} en estado inválido: {}.", sessionId, session.getEstado());
//                throw new InvalidSessionStateException("La sesión debe estar en estado 'ACTIVE' para pausarla. Estado actual: " + session.getEstado());
//            }
//
//            Long startTime = activeSessionStartTimes.remove(sessionId);
//            if (startTime != null) {
//                long elapsed = System.currentTimeMillis() - startTime;
//                sessionEffectiveStudyTime.merge(sessionId, elapsed, Long::sum); // Acumular tiempo de estudio
//            }
//
//            session.setEstado(SessionState.PAUSED.name());
//            SesionEstudio updatedSession = facade.updateSesionEstudio(session);
//
//            pausedSessionStartTimes.put(sessionId, System.currentTimeMillis());
//
//            logger.info("Sesión ID {} pausada con éxito. Tiempo efectivo acumulado: {}ms.", sessionId, sessionEffectiveStudyTime.get(sessionId));
//            return updatedSession;
//        } catch (SessionNotFoundException | InvalidSessionStateException e) {
//            throw e;
//        } catch (ClientErrorException | IOException e) {
//            logger.error("Error de comunicación al pausar sesión {}: {}", sessionId, e.getMessage(), e);
//            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
//        } catch (Exception e) {
//            logger.error("Error inesperado al pausar sesión {}: {}", sessionId, e.getMessage(), e);
//            throw new ServiceException("Error inesperado al pausar sesión.", e);
//        }
//    }
//    
//    /**
//     * Reanuda una sesión pausada.
//     * @param sessionId El ID de la sesión a reanudar.
//     * @return La SesionEstudio actualizada a estado ACTIVO.
//     * @throws SessionNotFoundException Si la sesión no existe.
//     * @throws InvalidSessionStateException Si la sesión no está en estado PAUSED.
//     * @throws ServiceException Si ocurre un error inesperado.
//     */
//    public SesionEstudio resumeSession(int sessionId){
//        if (sessionId <= 0) {
//            throw new IllegalArgumentException("ID de sesión inválido.");
//        }
//
//        try {
//            SesionEstudio session = facade.getSesionEstudioById(sessionId);
//            if (session == null) {
//                logger.warn("Intento de reanudación de sesión fallido: ID {} no encontrado.", sessionId);
//                throw new SessionNotFoundException("Sesión con ID " + sessionId + " no encontrada.");
//            }
//
//            if (!SessionState.PAUSED.name().equals(session.getEstado())) {
//                logger.warn("Intento de reanudación de sesión ID {} en estado inválido: {}.", sessionId, session.getEstado());
//                throw new InvalidSessionStateException("La sesión debe estar en estado 'PAUSED' para reanudarla. Estado actual: " + session.getEstado());
//            }
//
//            Long pauseStartTime = pausedSessionStartTimes.remove(sessionId);
//            if (pauseStartTime != null) {
//                long elapsedPause = System.currentTimeMillis() - pauseStartTime;
//                sessionBreakTime.merge(sessionId, elapsedPause, Long::sum); // Acumular tiempo de descanso
//            }
//
//            session.setEstado(SessionState.ACTIVE.name());
//            SesionEstudio updatedSession = facade.updateSesionEstudio(session);
//
//            activeSessionStartTimes.put(sessionId, System.currentTimeMillis());
//
//            logger.info("Sesión ID {} reanudada con éxito. Tiempo de descanso acumulado: {}ms.", sessionId, sessionBreakTime.get(sessionId));
//            return updatedSession;
//        } catch (SessionNotFoundException | InvalidSessionStateException e) {
//            throw e;
//        } catch (ClientErrorException | IOException e) {
//            logger.error("Error de comunicación al reanudar sesión {}: {}", sessionId, e.getMessage(), e);
//            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
//        } catch (Exception e) {
//            logger.error("Error inesperado al reanudar sesión {}: {}", sessionId, e.getMessage(), e);
//            throw new ServiceException("Error inesperado al reanudar sesión.", e);
//        }
//    }
//    
//    /**
//     * Calcula la duración total de la sesión.
//     * Registra datos de productividad (enfoque, distracciones) si se proporcionan.
//     * Actualiza el estado de la sesión en la base de datos a través de EstudyRestFacade.
//     * @param sessionId El ID de la sesión a finalizar.
//     * @param completionData DTO con notas, calificación de enfoque, etc.
//     * @return La SesionEstudio actualizada a estado COMPLETED o ABORTED.
//     * @throws SessionNotFoundException Si la sesión no existe.
//     * @throws InvalidSessionStateException Si la sesión no está en estado ACTIVO o PAUSED.
//     * @throws IllegalArgumentException Si los datos de finalización son inválidos.
//     * @throws ServiceException Si ocurre un error inesperado.
//     */
//    public SesionEstudio endSession(int sessionId, SessionCompletionData completionData){
//        if (sessionId <= 0) {
//            throw new IllegalArgumentException("ID de sesión inválido.");
//        }
//        if (completionData == null) {
//            throw new IllegalArgumentException("Los datos de finalización de la sesión no pueden ser nulos.");
//        }
//
//        try {
//            SesionEstudio session = facade.getSesionEstudioById(sessionId);
//            if (session == null) {
//                logger.warn("Intento de finalización de sesión fallido: ID {} no encontrado.", sessionId);
//                throw new SessionNotFoundException("Sesión con ID " + sessionId + " no encontrada.");
//            }
//
//            if (!SessionState.ACTIVE.name().equals(session.getEstado()) &&
//                !SessionState.PAUSED.name().equals(session.getEstado()) &&
//                !SessionState.PLANNED.name().equals(session.getEstado())) { // Permite abortar una sesión planificada
//                logger.warn("Intento de finalización de sesión ID {} en estado inválido: {}.", sessionId, session.getEstado());
//                throw new InvalidSessionStateException("La sesión debe estar en estado 'ACTIVE', 'PAUSED' o 'PLANNED' para finalizarla. Estado actual: " + session.getEstado());
//            }
//
//            // Calcular tiempos finales
//            long currentMillis = System.currentTimeMillis();
//            Long startTime = activeSessionStartTimes.remove(sessionId);
//            if (startTime != null) { // Si estaba activa
//                long elapsed = currentMillis - startTime;
//                sessionEffectiveStudyTime.merge(sessionId, elapsed, Long::sum);
//            }
//            Long pauseStartTime = pausedSessionStartTimes.remove(sessionId);
//            if (pauseStartTime != null) { // Si estaba pausada
//                long elapsedPause = currentMillis - pauseStartTime;
//                sessionBreakTime.merge(sessionId, elapsedPause, Long::sum);
//            }
//
//            long finalEffectiveStudyTime = sessionEffectiveStudyTime.getOrDefault(sessionId, 0L);
//            long finalBreakTime = sessionBreakTime.getOrDefault(sessionId, 0L);
//            long actualTotalDuration = (session.getFechaInicioReal() != null ? currentMillis - session.getFechaInicioReal().getTime() : 0);
//
//            // Actualizar la entidad SesionEstudio
//            session.setFechaFinReal(new Date());
//            session.setDuracionRealMinutos((int) (actualTotalDuration / (1000 * 60))); // Convertir a minutos
//            session.setTiempoEstudioEfectivoMinutos((int) (finalEffectiveStudyTime / (1000 * 60)));
//            session.setTiempoDescansoMinutos((int) (finalBreakTime / (1000 * 60)));
//            session.setNotas(completionData.getNotes());
//            session.setCalificacionEnfoque(completionData.getFocusRating());
//            session.setDetallesInterrupcion(completionData.getInterruptionDetails());
//
//            // Determinar el estado final de la sesión
//            if (SessionState.PLANNED.name().equals(session.getEstado()) && completionData.getOutcome() != null && completionData.getOutcome().equalsIgnoreCase("aborted")) {
//                session.setEstado(SessionState.ABORTED.name());
//            } else if (completionData.getOutcome() != null && completionData.getOutcome().equalsIgnoreCase("aborted")) {
//                 session.setEstado(SessionState.ABORTED.name());
//            } else {
//                 session.setEstado(SessionState.COMPLETED.name());
//            }
//
//            SesionEstudio updatedSession = facade.updateSesionEstudio(session); // Actualizar en la DB
//
//            // Registrar log de productividad (si aplica)
//            if (session.getUsuarioId() != null && (session.getEstado().equals(SessionState.COMPLETED.name()) || session.getEstado().equals(SessionState.ABORTED.name()))) {
//                LogProductividad log = new LogProductividad();
//                log.setUsuarioId(session.getUsuarioId());
//                log.setSesionEstudioId(session);
//                log.setFechaRegistro(new Date());
//                log.setDuracionEstudioTotalMinutos(session.getTiempoEstudioEfectivoMinutos());
//                log.setDuracionDescansoTotalMinutos(session.getTiempoDescansoMinutos());
//                log.setCalificacionEnfoque(session.getCalificacionEnfoque());
//                log.setNotas(session.getNotas());
//                log.setTipoEvento("SESION_FINALIZADA_" + session.getEstado()); // EJEMPLO: "SESION_FINALIZADA_COMPLETED"
//                
//                // Asumiendo que `EstudyRestFacade` tiene un método para crear LogProductividad
//                facade.createLogProductividad(log); // Este método debe ser implementado en EstudyRestFacade
//                logger.info("Log de productividad registrado para sesión ID {}.", sessionId);
//            }
//
//            // Limpiar datos de estado en memoria
//            sessionEffectiveStudyTime.remove(sessionId);
//            sessionBreakTime.remove(sessionId);
//
//            logger.info("Sesión ID {} finalizada con éxito. Estado: {}.", sessionId, session.getEstado());
//            return updatedSession;
//        } catch (SessionNotFoundException | InvalidSessionStateException | IllegalArgumentException e) {
//            throw e;
//        } catch (ClientErrorException | IOException e) {
//            logger.error("Error de comunicación al finalizar sesión {}: {}", sessionId, e.getMessage(), e);
//            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
//        } catch (Exception e) {
//            logger.error("Error inesperado al finalizar sesión {}: {}", sessionId, e.getMessage(), e);
//            throw new ServiceException("Error inesperado al finalizar sesión.", e);
//        }
//    }
//    
//    /**
//     * Delega la búsqueda a EstudyRestFacade.
//     * @param userId El ID del usuario.
//     * @param startDate La fecha de inicio del rango (inclusive).
//     * @param endDate La fecha de fin del rango (inclusive).
//     * @return Una lista de SesionEstudio.
//     * @throws IllegalArgumentException Si el ID de usuario o las fechas son inválidas.
//     * @throws UserNotFoundException Si el usuario no existe.
//     * @throws ServiceException Si ocurre un error inesperado.
//     */
//    public List<SesionEstudio> getSessionsForUser(int userId, Date startDate, Date endDate){
//        if (userId <= 0) {
//            throw new IllegalArgumentException("ID de usuario inválido.");
//        }
//        if (startDate == null || endDate == null || startDate.after(endDate)) {
//            throw new IllegalArgumentException("Rango de fechas inválido.");
//        }
//
//        try {
//            // Verificar que el usuario existe antes de buscar sus sesiones
//            Usuario owner = userService.getUserById(userId);
//            if (owner == null) {
//                throw new UserNotFoundException("El usuario con ID " + userId + " no existe.");
//            }
//
//            // Asumiendo que `EstudyRestFacade` tiene un método para buscar sesiones por usuario y rango de fechas
//            List<SesionEstudio> sessions = facade.getSesionesEstudioByUserIdAndDateRange(userId, startDate, endDate);
//            logger.info("Recuperadas {} sesiones para el usuario ID {} entre {} y {}.",
//                    sessions != null ? sessions.size() : 0, userId, startDate, endDate);
//            return sessions;
//        } catch (UserNotFoundException e) {
//            throw e; // Propagar si el usuario no existe
//        } catch (ClientErrorException | IOException e) {
//            logger.error("Error de comunicación al obtener sesiones para usuario {}: {}", userId, e.getMessage(), e);
//            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
//        } catch (Exception e) {
//            logger.error("Error inesperado al obtener sesiones para usuario {}: {}", userId, e.getMessage(), e);
//            throw new ServiceException("Error inesperado al obtener sesiones.", e);
//        }
//    }
//    
//    /**
//     * Obtiene una sesión por su ID. Método auxiliar.
//     * @param sessionId
//     * @return
//     * @throws SessionNotFoundException
//     * @throws ServiceException
//     */
//    public SesionEstudio getSessionById(int sessionId) {
//        try {
//            SesionEstudio session = facade.getSesionEstudioById(sessionId);
//            if (session == null) {
//                throw new SessionNotFoundException("Sesión con ID " + sessionId + " no encontrada.");
//            }
//            return session;
//        } catch (ClientErrorException | IOException e) {
//            logger.error("Error de comunicación al obtener sesión {}: {}", sessionId, e.getMessage(), e);
//            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
//        } catch (Exception e) {
//            logger.error("Error inesperado al obtener sesión {}: {}", sessionId, e.getMessage(), e);
//            throw new ServiceException("Error inesperado al obtener sesión.", e);
//        }
//    }
//    
//    /**
//     * Actualiza una sesión de estudio existente.
//     * Delega la actualización a EstudyRestFacade.
//     * @param session El objeto SesionEstudio con los datos actualizados.
//     * @return La SesionEstudio actualizada.
//     * @throws IllegalArgumentException Si los datos de la sesión son inválidos o el ID es nulo.
//     * @throws SessionNotFoundException Si la sesión no existe.
//     * @throws ServiceException Si ocurre un error de comunicación o inesperado.
//     */
//    public SesionEstudio updateSesionEstudio(SesionEstudio session) throws ServiceException, SessionNotFoundException, IllegalArgumentException {
//        if (session == null || session.getId() == null || session.getId() <= 0) {
//            throw new IllegalArgumentException("La sesión a actualizar debe tener un ID válido.");
//        }
//        if (session.getTitulo() == null || session.getTitulo().trim().isEmpty()) {
//            throw new IllegalArgumentException("El título de la sesión de estudio no puede estar vacío.");
//        }
//        if (session.getDuracionPlanificadaMinutos() == null || session.getDuracionPlanificadaMinutos() <= 0) {
//            throw new IllegalArgumentException("La duración planificada debe ser positiva.");
//        }
//
//        try {
//            // Opcional: Obtener la sesión existente para verificar el estado o permisos antes de actualizar
//            SesionEstudio existingSession = facade.getSesionEstudioById(session.getId());
//            if (existingSession == null) {
//                throw new SessionNotFoundException("Sesión con ID " + session.getId() + " no encontrada para actualizar.");
//            }
//
//            // Aquí podrías añadir lógica para evitar actualizar ciertos campos si la sesión está en un estado específico
//            // Por ejemplo, no permitir cambiar la fecha planificada si ya está ACTIVE o COMPLETED.
//            // Esto dependerá de tus reglas de negocio.
//
//            SesionEstudio updatedSession = facade.updateSesionEstudio(session);
//            if (updatedSession == null) {
//                throw new ServiceException("No se pudo actualizar la sesión de estudio.");
//            }
//            logger.info("Sesión de estudio ID {} actualizada con éxito.", updatedSession.getId());
//            return updatedSession;
//        } catch (SessionNotFoundException | IllegalArgumentException e) {
//            throw e;
//        } catch (ClientErrorException | IOException e) {
//            logger.error("Error de comunicación al actualizar sesión {}: {}", session.getId(), e.getMessage(), e);
//            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
//        } catch (Exception e) {
//            logger.error("Error inesperado al actualizar sesión {}: {}", session.getId(), e.getMessage(), e);
//            throw new ServiceException("Error inesperado al actualizar sesión.", e);
//        }
//    }
//
//    /**
//     * Obtiene el tiempo efectivo de estudio acumulado para una sesión activa/pausada.
//     * @param sessionId
//     * @return Tiempo en milisegundos.
//     */
//    public long getCurrentEffectiveStudyTime(int sessionId) {
//        long effective = sessionEffectiveStudyTime.getOrDefault(sessionId, 0L);
//        if (activeSessionStartTimes.containsKey(sessionId)) {
//            effective += System.currentTimeMillis() - activeSessionStartTimes.get(sessionId);
//        }
//        return effective;
//    }
//
//    /**
//     * Obtiene el tiempo de descanso acumulado para una sesión.
//     * @param sessionId
//     * @return Tiempo en milisegundos.
//     */
//    public long getCurrentBreakTime(int sessionId) {
//        long breaks = sessionBreakTime.getOrDefault(sessionId, 0L);
//        if (pausedSessionStartTimes.containsKey(sessionId)) {
//            breaks += System.currentTimeMillis() - pausedSessionStartTimes.get(sessionId);
//        }
//        return breaks;
//    }
//    
    /**
     * Obtiene una lista de Técnicas de Estudio de tipo INDIVIDUAL desde la fachada.
     * @return Lista de Técnicas de Estudio individuales.
     * @throws ServiceException Si ocurre un error de comunicación o inesperado.
     */
    public List<TecnicaEstudio> getIndividualTecnicasEstudio() throws ServiceException {
        try {
            return facade.getIndividualTecnicasEstudio();
        } catch (IOException e) {
            logger.error("Error de comunicación al obtener técnicas individuales: {}", e.getMessage(), e);
            throw new ServiceException("Error al obtener técnicas de estudio individuales.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al obtener técnicas individuales: {}", e.getMessage(), e);
            throw new ServiceException("Error inesperado al obtener técnicas de estudio individuales.", e);
        }
    }
//
//    /**
//     * Método para eliminar una sesión.
//     * @param sessionId
//     * @throws SessionNotFoundException
//     * @throws ServiceException
//     */
//    public void deleteSession(int sessionId) {
//        if (sessionId <= 0) {
//            throw new IllegalArgumentException("ID de sesión inválido.");
//        }
//        try {
//            SesionEstudio session = facade.getSesionEstudioById(sessionId);
//            if (session == null) {
//                throw new SessionNotFoundException("Sesión con ID " + sessionId + " no encontrada para eliminar.");
//            }
//            facade.deleteSesionEstudio(sessionId); // Asumiendo este método en EstudyRestFacade
//            activeSessionStartTimes.remove(sessionId);
//            pausedSessionStartTimes.put(sessionId, 0L);
//            sessionEffectiveStudyTime.remove(sessionId);
//            sessionBreakTime.remove(sessionId);
//            logger.info("Sesión ID {} eliminada con éxito.", sessionId);
//        } catch (ClientErrorException | IOException e) {
//            logger.error("Error de comunicación al eliminar sesión {}: {}", sessionId, e.getMessage(), e);
//            throw new ServiceException("Error de comunicación con el servicio de sesiones.", e);
//        } catch (Exception e) {
//            logger.error("Error inesperado al eliminar sesión {}: {}", sessionId, e.getMessage(), e);
//            throw new ServiceException("Error inesperado al eliminar sesión.", e);
//        }
//    }
    
    public static class SessionCompletionData {
        private String notes; // Notas o logros
        private Integer focusRating; // Calificación de enfoque (ej. 1-5)
        private Long actualDurationMillis; // Duración real total de la sesión en milisegundos
        private Long effectiveStudyMillis; // Tiempo efectivo de estudio en milisegundos
        private Long breakMillis; // Tiempo de descanso en milisegundos
        private String interruptionDetails; // Detalles de interrupciones
        private String outcome; // Resultado de la sesión (ej. "Success", "Interrupted")

        // Constructor sin argumentos
        public SessionCompletionData() {}

        public SessionCompletionData(String notes, Integer focusRating, Long actualDurationMillis, Long effectiveStudyMillis, Long breakMillis, String interruptionDetails, String outcome) {
            this.notes = notes;
            this.focusRating = focusRating;
            this.actualDurationMillis = actualDurationMillis;
            this.effectiveStudyMillis = effectiveStudyMillis;
            this.breakMillis = breakMillis;
            this.interruptionDetails = interruptionDetails;
            this.outcome = outcome;
        }

        // Getters y Setters
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

        public Long getActualDurationMillis() {
            return actualDurationMillis;
        }

        public void setActualDurationMillis(Long actualDurationMillis) {
            this.actualDurationMillis = actualDurationMillis;
        }

        public Long getEffectiveStudyMillis() {
            return effectiveStudyMillis;
        }

        public void setEffectiveStudyMillis(Long effectiveStudyMillis) {
            this.effectiveStudyMillis = effectiveStudyMillis;
        }

        public Long getBreakMillis() {
            return breakMillis;
        }

        public void setBreakMillis(Long breakMillis) {
            this.breakMillis = breakMillis;
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
    }
}
