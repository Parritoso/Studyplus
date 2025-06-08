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
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Integer id;

    private String nombre;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Correo electrónico no válido")//if the field contains email address consider using this annotation to enforce field validation
    private String email;
    
    private String contrasena;

    private Integer puntos;
   
    private String configuracionPerfil;

    private Date fechaRegistro;

    private Collection<ParticipanteGrupo> participantegrupoCollection;

    private Collection<GrupoEstudio> grupoestudioCollection;

    private Collection<SesionEstudio> sesionestudioCollection;

    private Collection<Entrega> entregaCollection;

    private Collection<Examen> examenCollection;

    private Collection<LogProductividad> logproductividadCollection;

    public Usuario() {
    }

    public Usuario(Integer id) {
        this.id = id;
    }

    public Usuario(Integer id, String nombre, String email, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
    }
    
    public Usuario(String nombre, String email, String contrasena){
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
    }

    public Usuario(Integer id, String nombre, String email, String contrasena, Integer puntos) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.puntos = puntos;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }
    
    public void addPuntos(Integer puntos) {
        this.puntos += puntos;
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

    public Collection<ParticipanteGrupo> getParticipantegrupoCollection() {
        return participantegrupoCollection;
    }

    public void setParticipantegrupoCollection(Collection<ParticipanteGrupo> participantegrupoCollection) {
        this.participantegrupoCollection = participantegrupoCollection;
    }

    public Collection<GrupoEstudio> getGrupoestudioCollection() {
        return grupoestudioCollection;
    }

    public void setGrupoestudioCollection(Collection<GrupoEstudio> grupoestudioCollection) {
        this.grupoestudioCollection = grupoestudioCollection;
    }

    public Collection<SesionEstudio> getSesionestudioCollection() {
        return sesionestudioCollection;
    }

    public void setSesionestudioCollection(Collection<SesionEstudio> sesionestudioCollection) {
        this.sesionestudioCollection = sesionestudioCollection;
    }

    public Collection<Entrega> getEntregaCollection() {
        return entregaCollection;
    }

    public void setEntregaCollection(Collection<Entrega> entregaCollection) {
        this.entregaCollection = entregaCollection;
    }

    public Collection<Examen> getExamenCollection() {
        return examenCollection;
    }

    public void setExamenCollection(Collection<Examen> examenCollection) {
        this.examenCollection = examenCollection;
    }

    public Collection<LogProductividad> getLogproductividadCollection() {
        return logproductividadCollection;
    }

    public void setLogproductividadCollection(Collection<LogProductividad> logproductividadCollection) {
        this.logproductividadCollection = logproductividadCollection;
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
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    /*@Override
    public String toString() {
        return "com.estudyplus.modelo.entitys.Usuario[ id=" + id + " ]";
    }*/

    @Override
    public String toString() {
        return "com.estudyplus.modelo.entitys.Usuario[" + "id=" + id + ", nombre=" + nombre + ", email=" + email + ", contrasena=" + contrasena + ", puntos=" + puntos + ", configuracionPerfil=" + configuracionPerfil + ", fechaRegistro=" + fechaRegistro + ", participantegrupoCollection=" + participantegrupoCollection + ", grupoestudioCollection=" + grupoestudioCollection + ", sesionestudioCollection=" + sesionestudioCollection + ", entregaCollection=" + entregaCollection + ", examenCollection=" + examenCollection + ", logproductividadCollection=" + logproductividadCollection + ']';
    }
    
}
