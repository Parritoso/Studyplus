/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.services;

import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.controlador.services.exception.TaskNotFoundException;
import com.estudyplus.modelo.entitys.Entrega;
import com.estudyplus.modelo.entitys.Examen;
import com.estudyplus.modelo.rest.facade.EstudyRestFacade;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Propósito: Gestionar entregas, exámenes y recordatorios asociados.
 * @see com.estudyplus.modelo.rest.facade.EstudyRestFacade
 * @author Parri
 */
public class TaskService {
    private EstudyRestFacade restFacade;
    
    public TaskService() {
        this.restFacade = new EstudyRestFacade(); // O inyectarlo si usas CDI/Spring
    }

    public TaskService(EstudyRestFacade instance) {
        restFacade = instance;
    }
    /**
     * Valida fechas, delega a EstudyRestFacade.
     * @param delivery La entidad Entrega a crear.
     * @return La Entrega creada con su ID asignado.
     * @throws ServiceException Si ocurre un error durante la creación.
     */
    public Entrega createDelivery(Entrega delivery){
        if (delivery == null || delivery.getTitulo() == null || delivery.getTitulo().isEmpty() ||
            delivery.getFechaLimite() == null || delivery.getUsuarioId() == null) {
            throw new IllegalArgumentException("Entrega, título, fecha límite y usuario son obligatorios.");
        }
        try {
            // Se asume que restFacade.createEntrega devolverá la entidad persistida con su ID
            return restFacade.createEntrega(delivery);
        } catch (Exception e) {
            throw new ServiceException("Error al crear la entrega: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valida fechas, delega a EstudyRestFacade.
     * @param exam La entidad Examen a crear.
     * @return El Examen creado con su ID asignado.
     * @throws ServiceException Si ocurre un error durante la creación.
     */
    public Examen createExam(Examen exam){
        if (exam == null || exam.getNombre() == null || exam.getNombre().isEmpty() ||
            exam.getFechaExamen() == null || exam.getUsuarioId() == null || exam.getAsignatura() == null || exam.getAsignatura().isEmpty()) {
            throw new IllegalArgumentException("Examen, nombre, fecha, asignatura y usuario son obligatorios.");
        }
        try {
            // Se asume que restFacade.createExamen devolverá la entidad persistida con su ID
            return restFacade.createExamen(exam);
        } catch (Exception e) {
            throw new ServiceException("Error al crear el examen: " + e.getMessage(), e);
        }
    }
    
    /**
     * Actualiza el estado de una entrega
     * @param deliveryId El ID de la entrega a actualizar.
     * @param status El nuevo estado de la entrega (e.g., "Entregado", "Pendiente").
     * @return La Entrega actualizada.
     * @throws TaskNotFoundException Si la entrega no se encuentra.
     * @throws ServiceException Si ocurre un error durante la actualización.
     */
    public Entrega updateDeliveryStatus(int deliveryId, String status){
        if (status == null || status.isEmpty()) {
            throw new IllegalArgumentException("El estado no puede ser nulo o vacío.");
        }
        try {
            Entrega entrega = restFacade.getEntregaById(deliveryId);
            if (entrega == null) {
                throw new TaskNotFoundException("Entrega con ID " + deliveryId + " no encontrada.");
            }
            entrega.setEstado(status);
            // Si el estado es "Entregado", registrar la fecha real de entrega
            if ("ENTREGADO".equalsIgnoreCase(status)) { // Usar mayúsculas para consistencia
                entrega.setFechaEntregaReal(new Date());
            } else if ("PENDIENTE".equalsIgnoreCase(status) && entrega.getFechaEntregaReal() != null) {
                 // Si vuelve a pendiente, se resetea la fecha de entrega real
                entrega.setFechaEntregaReal(null);
            }

            return restFacade.updateEntrega(entrega);
        } catch (TaskNotFoundException e) {
            throw e; // Relanza la excepción específica
        } catch (Exception e) {
            throw new ServiceException("Error al actualizar el estado de la entrega con ID " + deliveryId + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Recupera entregas y exámenes próximos.
     * @param userId El ID del usuario.
     * @return Una lista de objetos (Entrega o Examen) ordenados por fecha.
     * @throws ServiceException Si ocurre un error durante la recuperación de datos.
     */
    public List<Object> getUpcomingTasks(int userId){
        try {
            // Obtener todas las entregas pendientes para el usuario
            List<Entrega> entregas = restFacade.getEntregasByUserId(userId);
            List<Entrega> entregasPendientes = entregas.stream()
                .filter(e -> !"ENTREGADO".equalsIgnoreCase(e.getEstado()) && !"CANCELADO".equalsIgnoreCase(e.getEstado()))
                .collect(Collectors.toList());

            // Obtener todos los exámenes futuros para el usuario
            List<Examen> examenes = restFacade.getExamenesByUserId(userId);
            // Filtra exámenes cuya fecha aún no ha pasado completamente
            List<Examen> examenesFuturos = examenes.stream()
                .filter(ex -> ex.getFechaExamen() != null && ex.getFechaExamen().after(new Date()))
                .collect(Collectors.toList());

            // Combinar ambas listas
            List<Object> combinedTasks = new ArrayList<>();
            combinedTasks.addAll(entregasPendientes);
            combinedTasks.addAll(examenesFuturos);

            // Ordenar la lista combinada por fecha (fechaLimite para Entrega, fechaExamen para Examen)
            // Se puede usar un Comparator que maneje ambos tipos
            Collections.sort(combinedTasks, (task1, task2) -> {
                Date date1 = null;
                Date date2 = null;

                if (task1 instanceof Entrega) {
                    date1 = ((Entrega) task1).getFechaLimite();
                } else if (task1 instanceof Examen) {
                    date1 = ((Examen) task1).getFechaExamen();
                }

                if (task2 instanceof Entrega) {
                    date2 = ((Entrega) task2).getFechaLimite();
                } else if (task2 instanceof Examen) {
                    date2 = ((Examen) task2).getFechaExamen();
                }

                if (date1 == null && date2 == null) return 0;
                if (date1 == null) return 1; // Un nulo va al final
                if (date2 == null) return -1; // Un nulo va al final

                return date1.compareTo(date2);
            });

            return combinedTasks;

        } catch (Exception e) {
            throw new ServiceException("Error al obtener las tareas pendientes para el usuario " + userId + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Configura o actualiza un recordatorio para una tarea (Entrega o Examen).
     * Se asume que el taskId se refiere al ID de la Entrega o Examen.
     * Se usa un flag boolean para indicar si es una entrega (true) o un examen (false).
     * 
     * @param taskId El ID de la tarea (Entrega o Examen).
     * @param reminderTime La fecha y hora para el recordatorio. Si es null, se desactiva el recordatorio.
     * @param isDelivery Indica si la tarea es una Entrega (true) o un Examen (false).
     * @return La tarea actualizada (Entrega o Examen).
     * @throws TaskNotFoundException Si la tarea no se encuentra.
     * @throws ServiceException Si ocurre un error durante la actualización.
     */
    public Object setReminder(int taskId, Date reminderTime, boolean isDelivery){
        try {
            if (isDelivery) {
                Entrega entrega = restFacade.getEntregaById(taskId);
                if (entrega == null) {
                    throw new TaskNotFoundException("Entrega con ID " + taskId + " no encontrada para recordatorio.");
                }
                entrega.setFechaRecordatorio(reminderTime);
                entrega.setRecordatorioActivo(reminderTime != null);
                return restFacade.updateEntrega(entrega);
            } else { // Es un Examen
                Examen examen = restFacade.getExamenById(taskId);
                if (examen == null) {
                    throw new TaskNotFoundException("Examen con ID " + taskId + " no encontrado para recordatorio.");
                }
                examen.setFechaRecordatorio(reminderTime);
                examen.setRecordatorioActivo(reminderTime != null);
                return restFacade.updateExamen(examen);
            }
        } catch (TaskNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al configurar recordatorio para la tarea con ID " + taskId + ": " + e.getMessage(), e);
        }
    }
    
    public Entrega getDeliveryById(int deliveryId) throws TaskNotFoundException, ServiceException {
        try {
            Entrega entrega = restFacade.getEntregaById(deliveryId);
            if (entrega == null) {
                throw new TaskNotFoundException("Entrega con ID " + deliveryId + " no encontrada.");
            }
            return entrega;
        } catch (Exception e) {
            throw new ServiceException("Error al obtener la entrega con ID " + deliveryId + ": " + e.getMessage(), e);
        }
    }

    public Examen getExamById(int examId) throws TaskNotFoundException, ServiceException {
        try {
            Examen examen = restFacade.getExamenById(examId);
            if (examen == null) {
                throw new TaskNotFoundException("Examen con ID " + examId + " no encontrado.");
            }
            return examen;
        } catch (Exception e) {
            throw new ServiceException("Error al obtener el examen con ID " + examId + ": " + e.getMessage(), e);
        }
    }

    public void deleteDelivery(int deliveryId) throws TaskNotFoundException, ServiceException {
        try {
            Entrega entrega = restFacade.getEntregaById(deliveryId);
            if (entrega == null) {
                throw new TaskNotFoundException("Entrega con ID " + deliveryId + " no encontrada para eliminar.");
            }
            restFacade.deleteEntrega(deliveryId);
        } catch (TaskNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al eliminar la entrega con ID " + deliveryId + ": " + e.getMessage(), e);
        }
    }

    public void deleteExam(int examId) throws TaskNotFoundException, ServiceException {
        try {
            Examen examen = restFacade.getExamenById(examId);
            if (examen == null) {
                throw new TaskNotFoundException("Examen con ID " + examId + " no encontrado para eliminar.");
            }
            restFacade.deleteExamen(examId);
        } catch (TaskNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Error al eliminar el examen con ID " + examId + ": " + e.getMessage(), e);
        }
    }
}
