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
import javax.persistence.Lob;
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
@Table(name = "logproductividad")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Logproductividad.findAll", query = "SELECT l FROM Logproductividad l")
    , @NamedQuery(name = "Logproductividad.findById", query = "SELECT l FROM Logproductividad l WHERE l.id = :id")
    , @NamedQuery(name = "Logproductividad.findByFechaRegistro", query = "SELECT l FROM Logproductividad l WHERE l.fechaRegistro = :fechaRegistro")
    , @NamedQuery(name = "Logproductividad.findByDuracionEstudioTotalMinutos", query = "SELECT l FROM Logproductividad l WHERE l.duracionEstudioTotalMinutos = :duracionEstudioTotalMinutos")
    , @NamedQuery(name = "Logproductividad.findByDuracionDescansoTotalMinutos", query = "SELECT l FROM Logproductividad l WHERE l.duracionDescansoTotalMinutos = :duracionDescansoTotalMinutos")
    , @NamedQuery(name = "Logproductividad.findByNivelFatiga", query = "SELECT l FROM Logproductividad l WHERE l.nivelFatiga = :nivelFatiga")
    , @NamedQuery(name = "Logproductividad.findByCalificacionEnfoque", query = "SELECT l FROM Logproductividad l WHERE l.calificacionEnfoque = :calificacionEnfoque")
    , @NamedQuery(name = "Logproductividad.findByTipoEvento", query = "SELECT l FROM Logproductividad l WHERE l.tipoEvento = :tipoEvento")
    , @NamedQuery(name = "Logproductividad.findBySesionEstudioId", query = "SELECT l FROM Logproductividad l WHERE l.sesionEstudioId = :sesionEstudioId")})
public class Logproductividad implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;
    @Column(name = "duracion_estudio_total_minutos")
    private Integer duracionEstudioTotalMinutos;
    @Column(name = "duracion_descanso_total_minutos")
    private Integer duracionDescansoTotalMinutos;
    @Size(max = 50)
    @Column(name = "nivel_fatiga")
    private String nivelFatiga;
    @Basic(optional = false)
    @NotNull
    @Column(name = "calificacion_enfoque")
    private int calificacionEnfoque;
    @Lob
    @Size(max = 65535)
    @Column(name = "notas")
    private String notas;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "tipo_evento")
    private String tipoEvento;
    @Column(name = "sesion_estudio_id")
    private Integer sesionEstudioId;
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario usuarioId;

    public Logproductividad() {
    }

    public Logproductividad(Integer id) {
        this.id = id;
    }

    public Logproductividad(Integer id, int calificacionEnfoque, String tipoEvento) {
        this.id = id;
        this.calificacionEnfoque = calificacionEnfoque;
        this.tipoEvento = tipoEvento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getDuracionEstudioTotalMinutos() {
        return duracionEstudioTotalMinutos;
    }

    public void setDuracionEstudioTotalMinutos(Integer duracionEstudioTotalMinutos) {
        this.duracionEstudioTotalMinutos = duracionEstudioTotalMinutos;
    }

    public Integer getDuracionDescansoTotalMinutos() {
        return duracionDescansoTotalMinutos;
    }

    public void setDuracionDescansoTotalMinutos(Integer duracionDescansoTotalMinutos) {
        this.duracionDescansoTotalMinutos = duracionDescansoTotalMinutos;
    }

    public String getNivelFatiga() {
        return nivelFatiga;
    }

    public void setNivelFatiga(String nivelFatiga) {
        this.nivelFatiga = nivelFatiga;
    }

    public int getCalificacionEnfoque() {
        return calificacionEnfoque;
    }

    public void setCalificacionEnfoque(int calificacionEnfoque) {
        this.calificacionEnfoque = calificacionEnfoque;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Integer getSesionEstudioId() {
        return sesionEstudioId;
    }

    public void setSesionEstudioId(Integer sesionEstudioId) {
        this.sesionEstudioId = sesionEstudioId;
    }

    public Usuario getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Usuario usuarioId) {
        this.usuarioId = usuarioId;
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
        if (!(object instanceof Logproductividad)) {
            return false;
        }
        Logproductividad other = (Logproductividad) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rest.Logproductividad[ id=" + id + " ]";
    }
    
}
