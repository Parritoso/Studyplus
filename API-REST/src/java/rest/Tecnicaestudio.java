/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Parri
 */
@Entity
@Table(name = "tecnicaestudio")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tecnicaestudio.findAll", query = "SELECT t FROM Tecnicaestudio t")
    , @NamedQuery(name = "Tecnicaestudio.findById", query = "SELECT t FROM Tecnicaestudio t WHERE t.id = :id")
    , @NamedQuery(name = "Tecnicaestudio.findByNombre", query = "SELECT t FROM Tecnicaestudio t WHERE t.nombre = :nombre")
    , @NamedQuery(name = "Tecnicaestudio.findByDuracionFocoMinutos", query = "SELECT t FROM Tecnicaestudio t WHERE t.duracionFocoMinutos = :duracionFocoMinutos")
    , @NamedQuery(name = "Tecnicaestudio.findByDuracionDescansoMinutos", query = "SELECT t FROM Tecnicaestudio t WHERE t.duracionDescansoMinutos = :duracionDescansoMinutos")
    , @NamedQuery(name = "Tecnicaestudio.findByTipoTecnica", query = "SELECT t FROM Tecnicaestudio t WHERE t.tipoTecnica = :tipoTecnica")})
public class Tecnicaestudio implements Serializable {

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
    @Column(name = "duracion_foco_minutos")
    private Integer duracionFocoMinutos;
    @Column(name = "duracion_descanso_minutos")
    private Integer duracionDescansoMinutos;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "TIPO_TECNICA")
    private String tipoTecnica;
    @OneToMany(mappedBy = "tecnicaAplicadaId")
    private Collection<Sesionestudio> sesionestudioCollection;

    public Tecnicaestudio() {
    }

    public Tecnicaestudio(Integer id) {
        this.id = id;
    }

    public Tecnicaestudio(Integer id, String nombre, String tipoTecnica) {
        this.id = id;
        this.nombre = nombre;
        this.tipoTecnica = tipoTecnica;
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

    public Integer getDuracionFocoMinutos() {
        return duracionFocoMinutos;
    }

    public void setDuracionFocoMinutos(Integer duracionFocoMinutos) {
        this.duracionFocoMinutos = duracionFocoMinutos;
    }

    public Integer getDuracionDescansoMinutos() {
        return duracionDescansoMinutos;
    }

    public void setDuracionDescansoMinutos(Integer duracionDescansoMinutos) {
        this.duracionDescansoMinutos = duracionDescansoMinutos;
    }

    public String getTipoTecnica() {
        return tipoTecnica;
    }

    public void setTipoTecnica(String tipoTecnica) {
        this.tipoTecnica = tipoTecnica;
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
        if (!(object instanceof Tecnicaestudio)) {
            return false;
        }
        Tecnicaestudio other = (Tecnicaestudio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rest.Tecnicaestudio[ id=" + id + " ]";
    }
    
}
