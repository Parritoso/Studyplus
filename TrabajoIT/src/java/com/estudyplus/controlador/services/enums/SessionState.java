/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.services.enums;

/**
 *
 * @author Parri
 */
public enum SessionState {
    PLANNED,    // Sesión creada pero no iniciada
    ACTIVE,     // Sesión en curso
    PAUSED,     // Sesión pausada
    COMPLETED,  // Sesión finalizada con éxito
    ABORTED     // Sesión finalizada prematuramente (interrumpida)
}
