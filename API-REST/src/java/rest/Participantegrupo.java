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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Parri
 */
@Entity
@Table(name = "participantegrupo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Participantegrupo.findAll", query = "SELECT p FROM Participantegrupo p")
    , @NamedQuery(name = "Participantegrupo.findById", query = "SELECT p FROM Participantegrupo p WHERE p.id = :id")
    , @NamedQuery(name = "Participantegrupo.findByRol", query = "SELECT p FROM Participantegrupo p WHERE p.rol = :rol")
    , @NamedQuery(name = "Participantegrupo.findByFechaUnion", query = "SELECT p FROM Participantegrupo p WHERE p.fechaUnion = :fechaUnion")})
public class Participantegrupo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 50)
    @Column(name = "rol")
    private String rol;
    @Column(name = "fecha_union")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaUnion;
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario usuarioId;
    @JoinColumn(name = "grupo_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Grupoestudio grupoId;

    public Participantegrupo() {
    }

    public Participantegrupo(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Date getFechaUnion() {
        return fechaUnion;
    }

    public void setFechaUnion(Date fechaUnion) {
        this.fechaUnion = fechaUnion;
    }

    public Usuario getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Usuario usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Grupoestudio getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Grupoestudio grupoId) {
        this.grupoId = grupoId;
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
        if (!(object instanceof Participantegrupo)) {
            return false;
        }
        Participantegrupo other = (Participantegrupo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rest.Participantegrupo[ id=" + id + " ]";
    }
    
}
