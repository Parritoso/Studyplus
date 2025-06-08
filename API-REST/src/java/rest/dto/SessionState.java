/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.dto;

/**
 * Enumeración que representa los posibles estados de una sesión de estudio.
 */
public enum SessionState {
    PLANNED,    // Sesión planificada, aún no iniciada
    ACTIVE,     // Sesión en curso
    PAUSED,     // Sesión pausada temporalmente
    COMPLETED,  // Sesión finalizada con éxito
    ABORTED;    // Sesión finalizada prematuramente o cancelada
}
