/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Parri
 */
@Entity
@Table(name = "amistad")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Amistad.findAll", query = "SELECT a FROM Amistad a")
    , @NamedQuery(name = "Amistad.findById", query = "SELECT a FROM Amistad a WHERE a.id = :id")
    , @NamedQuery(name = "Amistad.findByEstado", query = "SELECT a FROM Amistad a WHERE a.estado = :estado")
    , @NamedQuery(name = "Amistad.findByFechaPeticion", query = "SELECT a FROM Amistad a WHERE a.fechaPeticion = :fechaPeticion")
    , @NamedQuery(name = "Amistad.findByFechaRespuesta", query = "SELECT a FROM Amistad a WHERE a.fechaRespuesta = :fechaRespuesta")})
public class Amistad implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "estado")
    private String estado;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fechaPeticion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPeticion;
    @Column(name = "fechaRespuesta")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRespuesta;
    @JoinColumn(name = "usuario1_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario usuario1Id;
    @JoinColumn(name = "usuario2_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario usuario2Id;

    public Amistad() {
    }

    public Amistad(Integer id) {
        this.id = id;
    }

    public Amistad(Integer id, String estado, Date fechaPeticion) {
        this.id = id;
        this.estado = estado;
        this.fechaPeticion = fechaPeticion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaPeticion() {
        return fechaPeticion;
    }

    public void setFechaPeticion(Date fechaPeticion) {
        this.fechaPeticion = fechaPeticion;
    }

    public Date getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(Date fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public Usuario getUsuario1Id() {
        return usuario1Id;
    }

    public void setUsuario1Id(Usuario usuario1Id) {
        this.usuario1Id = usuario1Id;
    }

    public Usuario getUsuario2Id() {
        return usuario2Id;
    }

    public void setUsuario2Id(Usuario usuario2Id) {
        this.usuario2Id = usuario2Id;
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
        if (!(object instanceof Amistad)) {
            return false;
        }
        Amistad other = (Amistad) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rest.Amistad[ id=" + id + " ]";
    }
    
}
