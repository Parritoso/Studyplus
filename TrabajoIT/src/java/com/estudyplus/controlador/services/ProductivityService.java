/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.services;

import com.estudyplus.modelo.entitys.LogProductividad;

/**
 * Propósito: Calcular y proporcionar métricas de productividad, y gestionar el registro de logs.
 * @see com.estudyplus.modelo.rest.facade.EstudyRestFacade
 * @see com.estudyplus.controlador.services.StudySessionService
 * @author Parri
 */
public class ProductivityService {
    /**
     * Delega el registro de un evento de productividad.
     * @param log 
     */
    public static void logProductivity(LogProductividad log){
        
    }
    
    /**
     * Calcula el total de horas de estudio, eficiencia, etc., basándose en datos de StudySessionService y LogProductividad.
     * @param userId 
     */
    public static void getWeeklyProductivitySummary(int userId){
        
    }
    
    /**
     * Agrega datos clave para el dashboard (próximas tareas, productividad reciente).
     * @param userId 
     */
    public static void getDashboardData(int userId){
        
    }
}
