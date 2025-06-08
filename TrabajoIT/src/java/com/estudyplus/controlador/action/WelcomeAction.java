/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action;

import com.opensymphony.xwork2.ActionSupport;

/**
 *
 * @author Parri
 */
public class WelcomeAction extends ActionSupport {

    @Override
    public String execute() {
        // No necesita lógica compleja, simplemente devuelve SUCCESS para mostrar la vista
        return SUCCESS;
    }
}
