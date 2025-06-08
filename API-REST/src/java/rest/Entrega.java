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
@Table(name = "entrega")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Entrega.findAll", query = "SELECT e FROM Entrega e")
    , @NamedQuery(name = "Entrega.findById", query = "SELECT e FROM Entrega e WHERE e.id = :id")
    , @NamedQuery(name = "Entrega.findByTitulo", query = "SELECT e FROM Entrega e WHERE e.titulo = :titulo")
    , @NamedQuery(name = "Entrega.findByFechaLimite", query = "SELECT e FROM Entrega e WHERE e.fechaLimite = :fechaLimite")
    , @NamedQuery(name = "Entrega.findByFechaCreacion", query = "SELECT e FROM Entrega e WHERE e.fechaCreacion = :fechaCreacion")
    , @NamedQuery(name = "Entrega.findByFechaEntregaReal", query = "SELECT e FROM Entrega e WHERE e.fechaEntregaReal = :fechaEntregaReal")
    , @NamedQuery(name = "Entrega.findByEstado", query = "SELECT e FROM Entrega e WHERE e.estado = :estado")
    , @NamedQuery(name = "Entrega.findByPrioridad", query = "SELECT e FROM Entrega e WHERE e.prioridad = :prioridad")
    , @NamedQuery(name = "Entrega.findByAsignatura", query = "SELECT e FROM Entrega e WHERE e.asignatura = :asignatura")
    , @NamedQuery(name = "Entrega.findByRecordatorioActivo", query = "SELECT e FROM Entrega e WHERE e.recordatorioActivo = :recordatorioActivo")
    , @NamedQuery(name = "Entrega.findByFechaRecordatorio", query = "SELECT e FROM Entrega e WHERE e.fechaRecordatorio = :fechaRecordatorio")
    , @NamedQuery(name = "Entrega.findUpcomingByUserId", query = "SELECT e FROM Entrega e WHERE e.usuarioId.id = :userId AND e.fechaLimite >= :currentDate AND e.estado != 'Completada' ORDER BY e.fechaLimite ASC")})
public class Entrega implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "titulo")
    private String titulo;
    @Lob
    @Size(max = 65535)
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_limite")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaLimite;
    @Column(name = "fecha_creacion")
    private Integer fechaCreacion;
    @Column(name = "fecha_entrega_real")
    private Integer fechaEntregaReal;
    @Size(max = 50)
    @Column(name = "estado")
    private String estado;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "prioridad")
    private String prioridad;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "asignatura")
    private String asignatura;
    @Column(name = "recordatorio_activo")
    private Boolean recordatorioActivo;
    @Column(name = "fecha_recordatorio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRecordatorio;
    @OneToMany(mappedBy = "entregaAsociadaId")
    private Collection<Sesionestudio> sesionestudioCollection;
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario usuarioId;

    public Entrega() {
    }

    public Entrega(Integer id) {
        this.id = id;
    }

    public Entrega(Integer id, String titulo, Date fechaLimite, String prioridad, String asignatura) {
        this.id = id;
        this.titulo = titulo;
        this.fechaLimite = fechaLimite;
        this.prioridad = prioridad;
        this.asignatura = asignatura;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Date fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public Integer getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Integer fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getFechaEntregaReal() {
        return fechaEntregaReal;
    }

    public void setFechaEntregaReal(Integer fechaEntregaReal) {
        this.fechaEntregaReal = fechaEntregaReal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public Boolean getRecordatorioActivo() {
        return recordatorioActivo;
    }

    public void setRecordatorioActivo(Boolean recordatorioActivo) {
        this.recordatorioActivo = recordatorioActivo;
    }

    public Date getFechaRecordatorio() {
        return fechaRecordatorio;
    }

    public void setFechaRecordatorio(Date fechaRecordatorio) {
        this.fechaRecordatorio = fechaRecordatorio;
    }

    @XmlTransient
    public Collection<Sesionestudio> getSesionestudioCollection() {
        return sesionestudioCollection;
    }

    public void setSesionestudioCollection(Collection<Sesionestudio> sesionestudioCollection) {
        this.sesionestudioCollection = sesionestudioCollection;
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
        if (!(object instanceof Entrega)) {
            return false;
        }
        Entrega other = (Entrega) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rest.Entrega[ id=" + id + " ]";
    }
    
}
