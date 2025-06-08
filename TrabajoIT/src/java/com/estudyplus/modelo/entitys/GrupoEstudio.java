/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.modelo.entitys;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Parri
 */
public class GrupoEstudio implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Integer id;

    private String nombre;

    private String descripcion;

    private Date fechaCreacion;

    private Collection<ParticipanteGrupo> participantegrupoCollection;

    private Usuario creadorUsuarioId;
    private String NotasRapidas;
    private String Checklist;

    public GrupoEstudio() {
    }

    public GrupoEstudio(Integer id) {
        this.id = id;
    }

    public GrupoEstudio(Integer id, String nombre) {
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

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @XmlTransient
    public Collection<ParticipanteGrupo> getParticipantegrupoCollection() {
        return participantegrupoCollection;
    }

    public void setParticipantegrupoCollection(Collection<ParticipanteGrupo> participantegrupoCollection) {
        this.participantegrupoCollection = participantegrupoCollection;
    }

    public Usuario getCreadorUsuarioId() {
        return creadorUsuarioId;
    }

    public void setCreadorUsuarioId(Usuario creadorUsuarioId) {
        this.creadorUsuarioId = creadorUsuarioId;
    }

    public String getNotasRapidas() {
        return NotasRapidas;
    }

    public void setNotasRapidas(String NotasRapidas) {
        this.NotasRapidas = NotasRapidas;
    }

    public String getChecklist() {
        return Checklist;
    }

    public void setChecklist(String Checklist) {
        this.Checklist = Checklist;
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
        if (!(object instanceof GrupoEstudio)) {
            return false;
        }
        GrupoEstudio other = (GrupoEstudio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.estudyplus.modelo.entitys.Grupoestudio[ id=" + id + " ]";
    }
    
}
