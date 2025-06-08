/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action;

import com.opensymphony.xwork2.ActionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Parri
 */
public class ErrorAction extends ActionSupport {

    private static final Logger logger = LoggerFactory.getLogger(ErrorAction.class);

    /**
     * Método que se ejecuta cuando una acción no es encontrada (error 404).
     * @return String "success" para la vista.
     */
    public String notFound() {
        logger.warn("Se ha accedido a una URL no encontrada. Manejando con ErrorAction.notFound.");
        // Aquí podrías añadir lógica si necesitas, por ejemplo, loguear la URL específica
        // O preparar algún mensaje para la vista.
        return SUCCESS; // Devuelve "success" para que Struts2 vaya a la vista 404.jsp
    }

    /**
     * Puedes añadir otros métodos para manejar distintos tipos de errores si es necesario.
     * Por ejemplo, un método para errores internos del servidor (500).
     */
    public String internalError() {
        logger.error("Se ha producido un error interno en el servidor. Manejando con ErrorAction.internalError.");
        return ERROR; // Asumiendo que tendrías un resultado "error" global o específico para 500.jsp
    }
    
}
