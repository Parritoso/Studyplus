/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.modelo.dto;

import com.estudyplus.modelo.entitys.Participanteconversacion;
import java.io.Serializable;

/**
 *
 * @author Parri
 */
public class ParticipanteconversacionDTO implements Serializable {
    private UsuarioDTO usuario;

    public ParticipanteconversacionDTO() {
    }

    public ParticipanteconversacionDTO(Participanteconversacion pc) {
        if (pc.getUsuario() != null) {
            this.usuario = new UsuarioDTO(pc.getUsuario());
        }
    }

    // Getters
    public UsuarioDTO getUsuario() { return usuario; }

    // Setters (opcional)
    public void setUsuario(UsuarioDTO usuario) { this.usuario = usuario; }
}
