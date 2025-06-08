/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.modelo.entitys;

import java.io.Serializable;
import java.util.Collection;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Parri
 */
public class Examen implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Integer id;

    private String nombre;
    
    private String descripcion;

    private Date fechaExamen;
    
    private Date fechaCreacion;

    private String asignatura;
    
    private String tipoExamen;
    
    private String prioridad;

    private Boolean recordatorioActivo;
    
    private Date fechaRecordatorio;
    
    private Collection<SesionEstudio> sesionestudioCollection;

    private Usuario usuarioId;

    public Examen() {
        this.fechaCreacion = new Date(); // Establecer la fecha de creación por defecto
        this.recordatorioActivo = false;
    }

    public Examen(Integer id) {
        this();
        this.id = id;
    }

    public Examen(Integer id, String nombre, Date fechaExamen, String asignatura, Usuario usuarioId) {
        this();
        this.id = id;
        this.nombre = nombre;
        this.fechaExamen = fechaExamen;
        this.asignatura = asignatura;
        this.usuarioId = usuarioId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaExamen() {
        return fechaExamen;
    }

    public void setFechaExamen(Date fechaExamen) {
        this.fechaExamen = fechaExamen;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public Boolean getRecordatorioActivo() {
        return recordatorioActivo;
    }

    public void setRecordatorioActivo(Boolean recordatorioActivo) {
        this.recordatorioActivo = recordatorioActivo;
    }

    public Usuario getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Usuario usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getTipoExamen() {
        return tipoExamen;
    }

    public void setTipoExamen(String tipoExamen) {
        this.tipoExamen = tipoExamen;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public Date getFechaRecordatorio() {
        return fechaRecordatorio;
    }

    public void setFechaRecordatorio(Date fechaRecordatorio) {
        this.fechaRecordatorio = fechaRecordatorio;
    }

    public Collection<SesionEstudio> getSesionestudioCollection() {
        return sesionestudioCollection;
    }

    public void setSesionestudioCollection(Collection<SesionEstudio> sesionestudioCollection) {
        this.sesionestudioCollection = sesionestudioCollection;
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
        if (!(object instanceof Examen)) {
            return false;
        }
        Examen other = (Examen) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.estudyplus.modelo.entitys.Examen[ id=" + id + " ]";
    }
    
}
