/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.modelo.entitys;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Parri
 */
public class Mensaje implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String contenido;
    private Date fechaEnvio;
    private boolean leido;
    private Conversacion conversacionId;
    private Usuario emisorId;

    public Mensaje() {
    }

    public Mensaje(Integer id) {
        this.id = id;
    }

    public Mensaje(Integer id, String contenido, Date fechaEnvio, boolean leido) {
        this.id = id;
        this.contenido = contenido;
        this.fechaEnvio = fechaEnvio;
        this.leido = leido;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public boolean getLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }

    public Conversacion getConversacionId() {
        return conversacionId;
    }

    public void setConversacionId(Conversacion conversacionId) {
        this.conversacionId = conversacionId;
    }

    public Usuario getEmisorId() {
        return emisorId;
    }

    public void setEmisorId(Usuario emisorId) {
        this.emisorId = emisorId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Mensaje)) {
            return false;
        }
        Mensaje other = (Mensaje) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rest.Mensaje[ id=" + id + " ]";
    }
    
}
