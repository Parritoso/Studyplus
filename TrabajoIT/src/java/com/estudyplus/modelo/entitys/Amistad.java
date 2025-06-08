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
public class Amistad implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String estado;
    private Date fechaPeticion;
    private Date fechaRespuesta;
    private Usuario usuario1Id;
    private Usuario usuario2Id;

    public Amistad() {
    }

    public Amistad(Integer id) {
        this.id = id;
    }

    public Amistad(Integer id, String estado, Date fechaPeticion) {
        this.id = id;
        this.estado = estado;
        this.fechaPeticion = fechaPeticion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaPeticion() {
        return fechaPeticion;
    }

    public void setFechaPeticion(Date fechaPeticion) {
        this.fechaPeticion = fechaPeticion;
    }

    public Date getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(Date fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public Usuario getUsuario1Id() {
        return usuario1Id;
    }

    public void setUsuario1Id(Usuario usuario1Id) {
        this.usuario1Id = usuario1Id;
    }

    public Usuario getUsuario2Id() {
        return usuario2Id;
    }

    public void setUsuario2Id(Usuario usuario2Id) {
        this.usuario2Id = usuario2Id;
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
        if (!(object instanceof Amistad)) {
            return false;
        }
        Amistad other = (Amistad) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rest.Amistad[ id=" + id + " ]";
    }
    
}
