/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.modelo.entitys;

import com.estudyplus.controlador.services.enums.TipoTecnica;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Parri
 */
public class TecnicaEstudio implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Integer id;

    private String nombre;

    private String descripcion;

    private Integer duracionFocoMinutos;

    private Integer duracionDescansoMinutos;
    
    private TipoTecnica tipoTecnica;

    private Collection<SesionEstudio> sesionestudioCollection;

    public TecnicaEstudio() {
    }

    public TecnicaEstudio(Integer id) {
        this.id = id;
    }

    public TecnicaEstudio(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getDuracionFocoMinutos() {
        return duracionFocoMinutos;
    }

    public void setDuracionFocoMinutos(Integer duracionFocoMinutos) {
        this.duracionFocoMinutos = duracionFocoMinutos;
    }

    public Integer getDuracionDescansoMinutos() {
        return duracionDescansoMinutos;
    }

    public void setDuracionDescansoMinutos(Integer duracionDescansoMinutos) {
        this.duracionDescansoMinutos = duracionDescansoMinutos;
    }

    public TipoTecnica getTipoTecnica() {
        return tipoTecnica;
    }

    public void setTipoTecnica(TipoTecnica tipoTecnica) {
        this.tipoTecnica = tipoTecnica;
    }

    @XmlTransient
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
        if (!(object instanceof TecnicaEstudio)) {
            return false;
        }
        TecnicaEstudio other = (TecnicaEstudio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.estudyplus.modelo.entitys.Tecnicaestudio[ id=" + id + " ]";
    }
    
}
