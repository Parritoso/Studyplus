/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.modelo.entitys;

import java.io.Serializable;

/**
 *
 * @author Parri
 */
public class ParticipanteconversacionPK implements Serializable {

    private int conversacionId;
    private int usuarioId;

    public ParticipanteconversacionPK() {
    }

    public ParticipanteconversacionPK(int conversacionId, int usuarioId) {
        this.conversacionId = conversacionId;
        this.usuarioId = usuarioId;
    }

    public int getConversacionId() {
        return conversacionId;
    }

    public void setConversacionId(int conversacionId) {
        this.conversacionId = conversacionId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) conversacionId;
        hash += (int) usuarioId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ParticipanteconversacionPK)) {
            return false;
        }
        ParticipanteconversacionPK other = (ParticipanteconversacionPK) object;
        if (this.conversacionId != other.conversacionId) {
            return false;
        }
        if (this.usuarioId != other.usuarioId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rest.ParticipanteconversacionPK[ conversacionId=" + conversacionId + ", usuarioId=" + usuarioId + " ]";
    }
    
}
