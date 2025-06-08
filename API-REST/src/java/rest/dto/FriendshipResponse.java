/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.dto;

/**
 *
 * @author Parri
 */
public class FriendshipResponse {
    private String status; // "aceptado", "rechazado", "error", etc.
    private String message; // Mensaje descriptivo

    public FriendshipResponse() {}

    public FriendshipResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getters y Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
