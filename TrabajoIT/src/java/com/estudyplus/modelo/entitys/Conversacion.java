/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.modelo.entitys;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author Parri
 */
public class Conversacion implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Date fechaCreacion;
    private Date ultimoMensajeFecha;
    private Collection<Participanteconversacion> participanteconversacionCollection;
    private Collection<Mensaje> mensajeCollection;
    private transient int unreadMessagesCount;

    public Conversacion() {
    }

    public Conversacion(Integer id) {
        this.id = id;
    }

    public Conversacion(Integer id, Date fechaCreacion) {
        this.id = id;
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getUltimoMensajeFecha() {
        return ultimoMensajeFecha;
    }

    public void setUltimoMensajeFecha(Date ultimoMensajeFecha) {
        this.ultimoMensajeFecha = ultimoMensajeFecha;
    }

    public Collection<Participanteconversacion> getParticipanteconversacionCollection() {
        return participanteconversacionCollection;
    }

    public void setParticipanteconversacionCollection(Collection<Participanteconversacion> participanteconversacionCollection) {
        this.participanteconversacionCollection = participanteconversacionCollection;
    }

    public Collection<Mensaje> getMensajeCollection() {
        return mensajeCollection;
    }

    public void setMensajeCollection(Collection<Mensaje> mensajeCollection) {
        this.mensajeCollection = mensajeCollection;
    }
    
    public int getUnreadMessagesCount() {
        return unreadMessagesCount;
    }

    public void setUnreadMessagesCount(int unreadMessagesCount) {
        this.unreadMessagesCount = unreadMessagesCount;
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
        if (!(object instanceof Conversacion)) {
            return false;
        }
        Conversacion other = (Conversacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rest.Conversacion[ id=" + id + " ]";
    }
    
}
