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
@Table(name = "examen")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Examen.findAll", query = "SELECT e FROM Examen e")
    , @NamedQuery(name = "Examen.findById", query = "SELECT e FROM Examen e WHERE e.id = :id")
    , @NamedQuery(name = "Examen.findByNombre", query = "SELECT e FROM Examen e WHERE e.nombre = :nombre")
    , @NamedQuery(name = "Examen.findByDescripcion", query = "SELECT e FROM Examen e WHERE e.descripcion = :descripcion")
    , @NamedQuery(name = "Examen.findByFechaExamen", query = "SELECT e FROM Examen e WHERE e.fechaExamen = :fechaExamen")
    , @NamedQuery(name = "Examen.findByFechaCreacion", query = "SELECT e FROM Examen e WHERE e.fechaCreacion = :fechaCreacion")
    , @NamedQuery(name = "Examen.findByAsignatura", query = "SELECT e FROM Examen e WHERE e.asignatura = :asignatura")
    , @NamedQuery(name = "Examen.findByTipoExamen", query = "SELECT e FROM Examen e WHERE e.tipoExamen = :tipoExamen")
    , @NamedQuery(name = "Examen.findByPrioridad", query = "SELECT e FROM Examen e WHERE e.prioridad = :prioridad")
    , @NamedQuery(name = "Examen.findByRecordatorioActivo", query = "SELECT e FROM Examen e WHERE e.recordatorioActivo = :recordatorioActivo")
    , @NamedQuery(name = "Examen.findByFechaRecordatorio", query = "SELECT e FROM Examen e WHERE e.fechaRecordatorio = :fechaRecordatorio")
    , @NamedQuery(name = "Examen.findUpcomingByUserId", query = "SELECT e FROM Examen e WHERE e.usuarioId.id = :userId AND e.fechaExamen >= :currentDate ORDER BY e.fechaExamen ASC")})
public class Examen implements Serializable {

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
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1000)
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_examen")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaExamen;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;
    @Size(max = 255)
    @Column(name = "asignatura")
    private String asignatura;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "tipo_examen")
    private String tipoExamen;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "prioridad")
    private String prioridad;
    @Column(name = "recordatorio_activo")
    private Boolean recordatorioActivo;
    @Basic(optional = false)
    @Column(name = "fecha_recordatorio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRecordatorio;
    @OneToMany(mappedBy = "examenAsociadoId")
    private Collection<Sesionestudio> sesionestudioCollection;
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario usuarioId;

    public Examen() {
    }

    public Examen(Integer id) {
        this.id = id;
    }

    public Examen(Integer id, String nombre, String descripcion, Date fechaExamen, Date fechaCreacion, String tipoExamen, String prioridad, Date fechaRecordatorio) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaExamen = fechaExamen;
        this.fechaCreacion = fechaCreacion;
        this.tipoExamen = tipoExamen;
        this.prioridad = prioridad;
        this.fechaRecordatorio = fechaRecordatorio;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaExamen() {
        return fechaExamen;
    }

    public void setFechaExamen(Date fechaExamen) {
        this.fechaExamen = fechaExamen;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public String getTipoExamen() {
        return tipoExamen;
    }

    public void setTipoExamen(String tipoExamen) {
        this.tipoExamen = tipoExamen;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
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
        if (!(object instanceof Examen)) {
            return false;
        }
        Examen other = (Examen) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rest.Examen[ id=" + id + " ]";
    }
    
}
