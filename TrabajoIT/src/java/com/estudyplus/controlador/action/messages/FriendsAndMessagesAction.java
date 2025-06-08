/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action.messages;

import com.opensymphony.xwork2.ActionSupport;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action principal para la página consolidada de amigos y mensajes.
 * Simplemente sirve el JSP inicial. Las interacciones dinámicas se manejan
 * con JavaScript y otras acciones que devuelven JSON.
 * @author Parri
 */
public class FriendsAndMessagesAction extends ActionSupport implements SessionAware {

    private static final Logger logger = LoggerFactory.getLogger(FriendsAndMessagesAction.class);

    private Map<String, Object> session; // Para acceder a la sesión

    @Override
    public String execute() {
        // Verificar si el usuario está logeado antes de mostrar la página
        if (session == null || !session.containsKey("loggedInUser")) {
            logger.warn("Intento de acceso a FriendsAndMessagesAction sin usuario logeado.");
            return ERROR; // Redirige a la página de error o login
        }
        logger.info("Acceso a la página de amigos y mensajes para el usuario logeado.");
        return SUCCESS; // Carga el JSP friends_and_messages.jsp
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
