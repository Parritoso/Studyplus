/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action;

import com.estudyplus.controlador.services.GamificationService;
import com.estudyplus.modelo.dto.JsonResponse;
import com.estudyplus.modelo.entitys.Usuario;
import com.opensymphony.xwork2.ActionSupport;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;

/**
 *
 * @author Parri
 */
public class SumarPuntosAction extends ActionSupport implements SessionAware{

    private int puntos;
    private String sesionId;
    private Map<String,Object>session;
    private JsonResponse jsonResponse;

    public String execute() {
        try {
            GamificationService servicio = new GamificationService();
            Usuario loggedInUser = (Usuario) session.get("loggedInUser");

            servicio.awardPoints(loggedInUser, puntos, SUCCESS);
            jsonResponse=new JsonResponse(true,"Puntos guardados");

        } catch (Exception e) {
            System.err.println("Mensaje Error"+e.getMessage());
        }

        return SUCCESS;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public void setSesionId(String sesionId) {
        this.sesionId = sesionId;
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session=map;
    }

    public JsonResponse getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(JsonResponse jsonResponse) {
        this.jsonResponse = jsonResponse;
    }


}
