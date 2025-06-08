/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action;

import com.estudyplus.modelo.entitys.Usuario;
import com.estudyplus.modelo.rest.facade.EstudyRestFacade;
import com.estudyplus.modelo.rest.facade.EstudyRestFacadeFactory;
import com.opensymphony.xwork2.ActionSupport;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.struts2.ServletActionContext;
import org.owasp.esapi.ESAPI;

/**
 *
 * @author Parri
 */
public class RegisterAction extends ActionSupport {
    private EstudyRestFacade estudyRestFacade = EstudyRestFacadeFactory.getInstance();

    // Propiedades para el formulario de registro
    private String username;
    private String email;
    private String password;
    private String confirmPassword; // Para confirmar la contraseña

    // Mensaje de feedback para la vista
    private String feedbackMessage;

    @Override
    public String execute() {
        // Por ahora, esta acción solo muestra el formulario.
        // La lógica de registro real se implementará más adelante.
        // Si hay un POST a esta acción, aquí iría la validación y el procesamiento.

        // Simplemente para demostración, si se envía el formulario (POST),
        // podrías mostrar un mensaje de que se recibirá más adelante
        /*if ("POST".equalsIgnoreCase(com.opensymphony.xwork2.ActionContext.getContext().getServletRequest().getMethod())) {
        feedbackMessage = "¡Gracias por intentar registrarte! La funcionalidad de registro se implementará pronto.";
        // Aquí iría la validación real y la llamada al servicio de registro
        }*/
        if(ServletActionContext.getRequest().getMethod().equalsIgnoreCase("POST")){
            Usuario aux = new Usuario(ESAPI.encoder().encodeForHTML(username), ESAPI.encoder().encodeForHTML(email), ESAPI.encoder().encodeForHTML(password));
            try {
                estudyRestFacade.registerUser(aux,password);
            } catch (IOException ex) {
                Logger.getLogger(RegisterAction.class.getName()).log(Level.SEVERE, null, ex);
            }
            return LOGIN;
        }

        return SUCCESS;
    }

    // --- Getters y Setters para las propiedades ---
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }

    /*
    // Ejemplo de método validate() para registro (se ejecutaría automáticamente)
    @Override
    public void validate() {
        if (username == null || username.trim().isEmpty()) {
            addFieldError("username", "El nombre de usuario es obligatorio.");
        }
        if (email == null || email.trim().isEmpty()) {
            addFieldError("email", "El email es obligatorio.");
        } else if (!email.matches(".+@.+\\..+")) { // Validación simple de email
            addFieldError("email", "Formato de email inválido.");
        }
        if (password == null || password.trim().isEmpty()) {
            addFieldError("password", "La contraseña es obligatoria.");
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            addFieldError("confirmPassword", "Confirma tu contraseña.");
        }
        if (password != null && !password.equals(confirmPassword)) {
            addFieldError("confirmPassword", "Las contraseñas no coinciden.");
        }
    }
    */
}
