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
public class Participanteconversacion implements Serializable {

    private static final long serialVersionUID = 1L;
    protected ParticipanteconversacionPK participanteconversacionPK;
    private Date fechaUnion;
    private Conversacion conversacion;
    private Usuario usuario;

    public Participanteconversacion() {
    }

    public Participanteconversacion(ParticipanteconversacionPK participanteconversacionPK) {
        this.participanteconversacionPK = participanteconversacionPK;
    }

    public Participanteconversacion(ParticipanteconversacionPK participanteconversacionPK, Date fechaUnion) {
        this.participanteconversacionPK = participanteconversacionPK;
        this.fechaUnion = fechaUnion;
    }

    public Participanteconversacion(int conversacionId, int usuarioId) {
        this.participanteconversacionPK = new ParticipanteconversacionPK(conversacionId, usuarioId);
    }

    public ParticipanteconversacionPK getParticipanteconversacionPK() {
        return participanteconversacionPK;
    }

    public void setParticipanteconversacionPK(ParticipanteconversacionPK participanteconversacionPK) {
        this.participanteconversacionPK = participanteconversacionPK;
    }

    public Date getFechaUnion() {
        return fechaUnion;
    }

    public void setFechaUnion(Date fechaUnion) {
        this.fechaUnion = fechaUnion;
    }

    public Conversacion getConversacion() {
        return conversacion;
    }

    public void setConversacion(Conversacion conversacion) {
        this.conversacion = conversacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (participanteconversacionPK != null ? participanteconversacionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Participanteconversacion)) {
            return false;
        }
        Participanteconversacion other = (Participanteconversacion) object;
        if ((this.participanteconversacionPK == null && other.participanteconversacionPK != null) || (this.participanteconversacionPK != null && !this.participanteconversacionPK.equals(other.participanteconversacionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rest.Participanteconversacion[ participanteconversacionPK=" + participanteconversacionPK + " ]";
    }
    
}
