/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.modelo.dto;

import com.estudyplus.modelo.entitys.Usuario;
import java.io.Serializable;


/**
 *
 * @author Parri
 */
public class UsuarioDTO implements Serializable {
    private Integer id;
    private String nombreUsuario; // O el campo que uses para mostrar el nombre del usuario

    public UsuarioDTO() {
    }

    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nombreUsuario = usuario.getNombre(); // Asegúrate de que este campo exista en tu entidad Usuario
    }

    // Getters
    public Integer getId() { return id; }
    public String getNombreUsuario() { return nombreUsuario; }

    // Setters (opcional)
    public void setId(Integer id) { this.id = id; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    
}
