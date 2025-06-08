/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.modelo.entitys;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Parri
 */

public class LogProductividad implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Date fechaRegistro;
    private Integer duracionEstudioTotalMinutos;

    private Integer duracionDescansoTotalMinutos;

    private String nivelFatiga;
    
    private Integer calificacionEnfoque; // Calificación de enfoque de la sesión
    
    private String notas; // Notas o logros de la sesión

    private String tipoEvento; // Ejemplo: "SESION_FINALIZADA_COMPLETED", "SESION_FINALIZADA_ABORTED"

    private Usuario usuarioId;
    
    private SesionEstudio sesionEstudioId; // Referencia a la SesionEstudio asociada (nuevo)

    public LogProductividad() {
    }

    public LogProductividad(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getDuracionEstudioTotalMinutos() {
        return duracionEstudioTotalMinutos;
    }

    public void setDuracionEstudioTotalMinutos(Integer duracionEstudioTotalMinutos) {
        this.duracionEstudioTotalMinutos = duracionEstudioTotalMinutos;
    }

    public Integer getDuracionDescansoTotalMinutos() {
        return duracionDescansoTotalMinutos;
    }

    public void setDuracionDescansoTotalMinutos(Integer duracionDescansoTotalMinutos) {
        this.duracionDescansoTotalMinutos = duracionDescansoTotalMinutos;
    }

    public String getNivelFatiga() {
        return nivelFatiga;
    }

    public Integer getCalificacionEnfoque() {
        return calificacionEnfoque;
    }

    public void setCalificacionEnfoque(Integer calificacionEnfoque) {
        this.calificacionEnfoque = calificacionEnfoque;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public SesionEstudio getSesionEstudioId() {
        return sesionEstudioId;
    }

    public void setSesionEstudioId(SesionEstudio sesionEstudioId) {
        this.sesionEstudioId = sesionEstudioId;
    }

    public void setNivelFatiga(String nivelFatiga) {
        this.nivelFatiga = nivelFatiga;
    }

    public Usuario getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Usuario usuarioId) {
        this.usuarioId = usuarioId;
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
        if (!(object instanceof LogProductividad)) {
            return false;
        }
        LogProductividad other = (LogProductividad) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.estudyplus.modelo.entitys.Logproductividad[ id=" + id + " ]";
    }
    
}
