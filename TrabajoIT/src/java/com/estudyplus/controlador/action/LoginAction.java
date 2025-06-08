/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action;

import com.estudyplus.controlador.services.UserService;
import com.estudyplus.controlador.services.exception.AuthenticationFailedException;
import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.modelo.entitys.Usuario;
import com.estudyplus.modelo.rest.facade.EstudyRestFacadeFactory;
import com.opensymphony.xwork2.ActionSupport;
import java.util.Map;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Parri
 */
public class LoginAction extends ActionSupport implements SessionAware {
    private static final Logger logger = LoggerFactory.getLogger(LoginAction.class);

    private String username;
    private String password;
    private String feedbackMessage;
    private Map<String, Object> session; // Objeto de sesión

    // Inyectar el UserService (más adelante podríamos usar un framework de inyección como Spring)
    private UserService userService;

    public LoginAction() {
        // Inicialización simple del servicio por ahora.
        // En una aplicación real, se usaría inyección de dependencias.
        this.userService = new UserService(EstudyRestFacadeFactory.getInstance());
    }
    
    @Override
    public String execute() {
        // Corrección: Usar ServletActionContext para obtener HttpServletRequest
        if (ServletActionContext.getRequest().getMethod().equalsIgnoreCase("GET")) {
            logger.info("Acceso inicial a la página de login (GET).");
            return INPUT; // Muestra el formulario vacío
        }

        // Si es una petición POST (envío del formulario), intentar autenticar
        try {
            logger.info("Intento de autenticación para usuario: {}", username);
            Usuario authenticatedUser = userService.authenticateUser(ESAPI.encoder().encodeForHTML(username), ESAPI.encoder().encodeForHTML(password));

            // Si authenticateUser retorna un Usuario, significa éxito
            if (authenticatedUser != null) {
                feedbackMessage = "¡Bienvenido, " + authenticatedUser.getNombre() + "!";
                session.put("loggedInUser", authenticatedUser); // Almacena el objeto Usuario
                session.put("isLoggedIn", true);
                logger.info("Usuario '{}' autenticado con éxito, redirigiendo a dashboard.", username);
                return SUCCESS; // Redirigirá a dashboard.action (configurado en public.xml)
            } else {
                // Aunque authenticateUser debería lanzar excepción si falla,
                // este else-if es una salvaguarda.
                feedbackMessage = "Usuario o contraseña incorrectos.";
                logger.warn("Autenticación fallida: usuario o contraseña incorrectos para '{}'.", username);
                return ERROR; // Vuelve al formulario con mensaje de error
            }

        } catch (IllegalArgumentException e) {
            // Campos nulos/vacíos (manejo de validación básica)
            addActionError(e.getMessage()); // Añade el mensaje como un error de acción
            logger.warn("Error de validación en login para usuario '{}': {}", username, e.getMessage());
            return INPUT; // Vuelve al formulario, mostrando los errores de validación
        } catch (AuthenticationFailedException e) {
            // Credenciales inválidas
            addActionError(e.getMessage()); // Añade el mensaje como un error de acción
            feedbackMessage = e.getMessage(); // También puedes usar feedbackMessage si prefieres
            logger.warn("Autenticación fallida para usuario '{}': {}", username, e.getMessage());
            return ERROR; // Vuelve al formulario con un mensaje de error
        } catch (ServiceException e) {
            // Errores de comunicación con la API u otros errores de servicio
            addActionError("Error al intentar iniciar sesión. Por favor, inténtelo de nuevo más tarde.");
            logger.error("Error de servicio durante la autenticación para usuario '{}': {}", username, e.getMessage(), e);
            return ERROR; // Vuelve al formulario con un error genérico de sistema
        } catch (Exception e) {
            // Captura cualquier otra excepción inesperada
            addActionError("Ha ocurrido un error inesperado. Por favor, inténtelo de nuevo.");
            logger.error("Excepción inesperada en LoginAction para usuario '{}': {}", username, e.getMessage(), e);
            return ERROR; // Vuelve al formulario con un error genérico
        }
    }

     public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFeedbackMessage() { return feedbackMessage; }
    public void setFeedbackMessage(String feedbackMessage) { this.feedbackMessage = feedbackMessage; }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
    
}
