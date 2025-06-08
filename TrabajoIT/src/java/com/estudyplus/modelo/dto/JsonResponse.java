/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.modelo.dto;

import java.io.Serializable;
import java.util.Map; // Para datos adicionales


/**
 * DTO genérico para respuestas JSON de acciones de Struts2.
 * Permite comunicar el estado de una operación (éxito/error),
 * un mensaje de feedback y cualquier dato adicional.
 */ 
public class JsonResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private Object data; // Puede ser cualquier objeto (List, Map, Entity)

    public JsonResponse() {
    }

    public JsonResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public JsonResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // --- Getters y Setters ---
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
