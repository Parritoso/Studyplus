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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Parri
 */
@Entity
@Table(name = "conversacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Conversacion.findAll", query = "SELECT c FROM Conversacion c")
    , @NamedQuery(name = "Conversacion.findById", query = "SELECT c FROM Conversacion c WHERE c.id = :id")
    , @NamedQuery(name = "Conversacion.findByFechaCreacion", query = "SELECT c FROM Conversacion c WHERE c.fechaCreacion = :fechaCreacion")
    , @NamedQuery(name = "Conversacion.findByUltimoMensajeFecha", query = "SELECT c FROM Conversacion c WHERE c.ultimoMensajeFecha = :ultimoMensajeFecha")})
public class Conversacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fechaCreacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;
    @Column(name = "ultimoMensajeFecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ultimoMensajeFecha;
    @JoinColumn(name = "grupo_estudio_id", referencedColumnName = "id")
    @ManyToOne(optional = true) // 'optional = true' indica que la relación es nullable
    private Grupoestudio grupoEstudio; // Nombre de la propiedad en Java
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "conversacion")
    private Collection<Participanteconversacion> participanteconversacionCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "conversacionId")
    private Collection<Mensaje> mensajeCollection;

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
    
    public Grupoestudio getGrupoEstudio() {
        return grupoEstudio;
    }

    public void setGrupoEstudio(Grupoestudio grupoEstudio) {
        this.grupoEstudio = grupoEstudio;
    }

    @XmlTransient
    public Collection<Participanteconversacion> getParticipanteconversacionCollection() {
        return participanteconversacionCollection;
    }

    public void setParticipanteconversacionCollection(Collection<Participanteconversacion> participanteconversacionCollection) {
        this.participanteconversacionCollection = participanteconversacionCollection;
    }

    @XmlTransient
    public Collection<Mensaje> getMensajeCollection() {
        return mensajeCollection;
    }

    public void setMensajeCollection(Collection<Mensaje> mensajeCollection) {
        this.mensajeCollection = mensajeCollection;
    }

    @Override
    public int hashCode() {
            // El hash code debe basarse únicamente en el ID para la unicidad de la entidad.
            return (id != null ? id.hashCode() : 0);
        }

    @Override
    public boolean equals(Object object) {
            // Compara objetos en base a su ID.
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
