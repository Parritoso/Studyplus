/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Parri
 */
@Entity
@Table(name = "usuario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u")
    , @NamedQuery(name = "Usuario.findById", query = "SELECT u FROM Usuario u WHERE u.id = :id")
    , @NamedQuery(name = "Usuario.findByNombre", query = "SELECT u FROM Usuario u WHERE u.nombre = :nombre")
    , @NamedQuery(name = "Usuario.findByEmail", query = "SELECT u FROM Usuario u WHERE u.email = :email")
    , @NamedQuery(name = "Usuario.findByContrasena", query = "SELECT u FROM Usuario u WHERE u.contrasena = :contrasena")
    , @NamedQuery(name = "Usuario.findByPuntos", query = "SELECT u FROM Usuario u WHERE u.puntos = :puntos")
    , @NamedQuery(name = "Usuario.findByFechaRegistro", query = "SELECT u FROM Usuario u WHERE u.fechaRegistro = :fechaRegistro")})
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "nombre")
    private String nombre;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Correo electrónico no válido")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "email")
    private String email;   
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "contrasena")
    private String contrasena;
    @Column(name = "puntos")
    private Integer puntos;
    @Lob
    @Size(max = 65535)
    @Column(name = "configuracion_perfil")
    private String configuracionPerfil;
    @Column(name = "fecha_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuarioId")
    private Collection<Participantegrupo> participantegrupoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "creadorUsuarioId")
    private Collection<Grupoestudio> grupoestudioCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario")
    private Collection<Participanteconversacion> participanteconversacionCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuarioId")
    private Collection<Sesionestudio> sesionestudioCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuarioId")
    private Collection<Entrega> entregaCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuarioId")
    private Collection<Examen> examenCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario1Id")
    private Collection<Amistad> amistadCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario2Id")
    private Collection<Amistad> amistadCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "emisorId")
    private Collection<Mensaje> mensajeCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuarioId")
    private Collection<Logproductividad> logproductividadCollection;

    public Usuario() {
    }

    public Usuario(Integer id) {
        this.id = id;
    }

    public Usuario(Integer id, String nombre, String email, String contrasena, Integer puntos) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.puntos = puntos == null ? 0 : puntos;
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

    @XmlTransient
    public Collection<Participantegrupo> getParticipantegrupoCollection() {
        return participantegrupoCollection;
    }

    public void setParticipantegrupoCollection(Collection<Participantegrupo> participantegrupoCollection) {
        this.participantegrupoCollection = participantegrupoCollection;
    }

    @XmlTransient
    public Collection<Grupoestudio> getGrupoestudioCollection() {
        return grupoestudioCollection;
    }

    public void setGrupoestudioCollection(Collection<Grupoestudio> grupoestudioCollection) {
        this.grupoestudioCollection = grupoestudioCollection;
    }

    @XmlTransient
    public Collection<Participanteconversacion> getParticipanteconversacionCollection() {
        return participanteconversacionCollection;
    }

    public void setParticipanteconversacionCollection(Collection<Participanteconversacion> participanteconversacionCollection) {
        this.participanteconversacionCollection = participanteconversacionCollection;
    }

    @XmlTransient
    public Collection<Sesionestudio> getSesionestudioCollection() {
        return sesionestudioCollection;
    }

    public void setSesionestudioCollection(Collection<Sesionestudio> sesionestudioCollection) {
        this.sesionestudioCollection = sesionestudioCollection;
    }

    @XmlTransient
    public Collection<Entrega> getEntregaCollection() {
        return entregaCollection;
    }

    public void setEntregaCollection(Collection<Entrega> entregaCollection) {
        this.entregaCollection = entregaCollection;
    }

    @XmlTransient
    public Collection<Examen> getExamenCollection() {
        return examenCollection;
    }

    public void setExamenCollection(Collection<Examen> examenCollection) {
        this.examenCollection = examenCollection;
    }

    @XmlTransient
    public Collection<Amistad> getAmistadCollection() {
        return amistadCollection;
    }

    public void setAmistadCollection(Collection<Amistad> amistadCollection) {
        this.amistadCollection = amistadCollection;
    }

    @XmlTransient
    public Collection<Amistad> getAmistadCollection1() {
        return amistadCollection1;
    }

    public void setAmistadCollection1(Collection<Amistad> amistadCollection1) {
        this.amistadCollection1 = amistadCollection1;
    }

    @XmlTransient
    public Collection<Mensaje> getMensajeCollection() {
        return mensajeCollection;
    }

    public void setMensajeCollection(Collection<Mensaje> mensajeCollection) {
        this.mensajeCollection = mensajeCollection;
    }

    @XmlTransient
    public Collection<Logproductividad> getLogproductividadCollection() {
        return logproductividadCollection;
    }

    public void setLogproductividadCollection(Collection<Logproductividad> logproductividadCollection) {
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
        return "rest.Usuario[ id=" + id + " ]";
    }*/

    @Override
    public String toString() {
        return "rest.Usuario[" + "id=" + id + ", nombre=" + nombre + ", email=" + email + ", contrasena=" + contrasena + ", puntos=" + puntos + ", configuracionPerfil=" + configuracionPerfil + ", fechaRegistro=" + fechaRegistro + ", participantegrupoCollection=" + participantegrupoCollection + ", grupoestudioCollection=" + grupoestudioCollection + ", participanteconversacionCollection=" + participanteconversacionCollection + ", sesionestudioCollection=" + sesionestudioCollection + ", entregaCollection=" + entregaCollection + ", examenCollection=" + examenCollection + ", amistadCollection=" + amistadCollection + ", amistadCollection1=" + amistadCollection1 + ", mensajeCollection=" + mensajeCollection + ", logproductividadCollection=" + logproductividadCollection + ']';
    }
    
}
