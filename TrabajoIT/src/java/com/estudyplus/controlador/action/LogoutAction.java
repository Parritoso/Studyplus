package com.estudyplus.controlador.action;

import com.opensymphony.xwork2.ActionSupport;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Parri
 */
public class LogoutAction extends ActionSupport implements SessionAware {

    private Map<String, Object> session;

    @Override
    public String execute() {
        // Invalida la sesión (elimina todos los atributos)
        if (session != null) {
            session.clear(); // O session.remove("loggedInUser"); session.remove("isLoggedIn");
        }
        return SUCCESS; // Redirige a la página de bienvenida
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
