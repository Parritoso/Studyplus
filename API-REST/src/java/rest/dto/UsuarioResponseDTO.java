/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.dto;

import java.io.Serializable;
import java.util.Date;
import rest.Usuario;
/**
 * DTO para la respuesta de usuario, excluyendo información sensible como la contraseña.
 * @author Parri
 */
public class UsuarioResponseDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer id;
        private String nombre;
        private String email;
        private Integer puntos;
        private String configuracionPerfil;
        private Date fechaRegistro;

        public UsuarioResponseDTO() {
        }

        public UsuarioResponseDTO(Integer id, String nombre, String email, Integer puntos, String configuracionPerfil, Date fechaRegistro) {
            this.id = id;
            this.nombre = nombre;
            this.email = email;
            this.puntos = puntos;
            this.configuracionPerfil = configuracionPerfil;
            this.fechaRegistro = fechaRegistro;
        }
        
        public UsuarioResponseDTO(Usuario usuario) {
            this.id = usuario.getId();
            this.nombre = usuario.getNombre();
            this.email = usuario.getEmail();
            this.puntos = usuario.getPuntos();
            this.configuracionPerfil = usuario.getConfiguracionPerfil();
            this.fechaRegistro = usuario.getFechaRegistro();
        }

        // --- Getters y Setters ---
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getPuntos() {
            return puntos;
        }

        public void setPuntos(Integer puntos) {
            this.puntos = puntos;
        }

        public String getConfiguracionPerfil() {
            return configuracionPerfil;
        }

        public void setConfiguracionPerfil(String configuracionPerfil) {
            this.configuracionPerfil = configuracionPerfil;
        }

        public Date getFechaRegistro() {
            return fechaRegistro;
        }

        public void setFechaRegistro(Date fechaRegistro) {
            this.fechaRegistro = fechaRegistro;
        }
}
