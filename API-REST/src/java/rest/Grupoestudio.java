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
@Table(name = "grupoestudio")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Grupoestudio.findAll", query = "SELECT g FROM Grupoestudio g")
    , @NamedQuery(name = "Grupoestudio.findById", query = "SELECT g FROM Grupoestudio g WHERE g.id = :id")
    , @NamedQuery(name = "Grupoestudio.findByNombre", query = "SELECT g FROM Grupoestudio g WHERE g.nombre = :nombre")
    , @NamedQuery(name = "Grupoestudio.findByFechaCreacion", query = "SELECT g FROM Grupoestudio g WHERE g.fechaCreacion = :fechaCreacion")})
public class Grupoestudio implements Serializable {

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
    @Lob
    @Size(max = 65535)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;
    @Lob
    @Size(max = 65535)
    @Column(name = "notas_rapidas")
    private String notasRapidas;
    @Lob
    @Size(max = 65535)
    @Column(name = "checklist")
    private String checklist;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "grupoId")
    private Collection<Participantegrupo> participantegrupoCollection;
    @JoinColumn(name = "creador_usuario_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario creadorUsuarioId;
    @OneToMany(mappedBy = "grupoAsociadoId")
    private Collection<Sesionestudio> sesionestudioCollection;

    public Grupoestudio() {
    }

    public Grupoestudio(Integer id) {
        this.id = id;
    }

    public Grupoestudio(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getNotasRapidas() {
        return notasRapidas;
    }

    public void setNotasRapidas(String notasRapidas) {
        this.notasRapidas = notasRapidas;
    }

    public String getChecklist() {
        return checklist;
    }

    public void setChecklist(String checklist) {
        this.checklist = checklist;
    }

    @XmlTransient
    public Collection<Participantegrupo> getParticipantegrupoCollection() {
        return participantegrupoCollection;
    }

    public void setParticipantegrupoCollection(Collection<Participantegrupo> participantegrupoCollection) {
        this.participantegrupoCollection = participantegrupoCollection;
    }

    public Usuario getCreadorUsuarioId() {
        return creadorUsuarioId;
    }

    public void setCreadorUsuarioId(Usuario creadorUsuarioId) {
        this.creadorUsuarioId = creadorUsuarioId;
    }

    @XmlTransient
    public Collection<Sesionestudio> getSesionestudioCollection() {
        return sesionestudioCollection;
    }

    public void setSesionestudioCollection(Collection<Sesionestudio> sesionestudioCollection) {
        this.sesionestudioCollection = sesionestudioCollection;
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
        if (!(object instanceof Grupoestudio)) {
            return false;
        }
        Grupoestudio other = (Grupoestudio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rest.Grupoestudio[ id=" + id + " ]";
    }
    
}
