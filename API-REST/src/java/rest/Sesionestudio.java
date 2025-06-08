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
@Table(name = "sesionestudio")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sesionestudio.findAll", query = "SELECT s FROM Sesionestudio s")
    , @NamedQuery(name = "Sesionestudio.findById", query = "SELECT s FROM Sesionestudio s WHERE s.id = :id")
    , @NamedQuery(name = "Sesionestudio.findByTitulo", query = "SELECT s FROM Sesionestudio s WHERE s.titulo = :titulo")
    , @NamedQuery(name = "Sesionestudio.findByDuracionPlanificadaMinutos", query = "SELECT s FROM Sesionestudio s WHERE s.duracionPlanificadaMinutos = :duracionPlanificadaMinutos")
    , @NamedQuery(name = "Sesionestudio.findByFechaSesionPlanificada", query = "SELECT s FROM Sesionestudio s WHERE s.fechaSesionPlanificada = :fechaSesionPlanificada")
    , @NamedQuery(name = "Sesionestudio.findByFechaCreacion", query = "SELECT s FROM Sesionestudio s WHERE s.fechaCreacion = :fechaCreacion")
    , @NamedQuery(name = "Sesionestudio.findByFechaInicioReal", query = "SELECT s FROM Sesionestudio s WHERE s.fechaInicioReal = :fechaInicioReal")
    , @NamedQuery(name = "Sesionestudio.findByFechaFinReal", query = "SELECT s FROM Sesionestudio s WHERE s.fechaFinReal = :fechaFinReal")
    , @NamedQuery(name = "Sesionestudio.findByDuracionRealMinutos", query = "SELECT s FROM Sesionestudio s WHERE s.duracionRealMinutos = :duracionRealMinutos")
    , @NamedQuery(name = "Sesionestudio.findByTiempoEstudioEfectivoMinutos", query = "SELECT s FROM Sesionestudio s WHERE s.tiempoEstudioEfectivoMinutos = :tiempoEstudioEfectivoMinutos")
    , @NamedQuery(name = "Sesionestudio.findByTiempoDescansoMinutos", query = "SELECT s FROM Sesionestudio s WHERE s.tiempoDescansoMinutos = :tiempoDescansoMinutos")
    , @NamedQuery(name = "Sesionestudio.findByEstado", query = "SELECT s FROM Sesionestudio s WHERE s.estado = :estado")
    , @NamedQuery(name = "Sesionestudio.findByNotas", query = "SELECT s FROM Sesionestudio s WHERE s.notas = :notas")
    , @NamedQuery(name = "Sesionestudio.findByCalificacionEnfoque", query = "SELECT s FROM Sesionestudio s WHERE s.calificacionEnfoque = :calificacionEnfoque")
    , @NamedQuery(name = "Sesionestudio.findByFechaUltimaActualizacionEstado", query = "SELECT s FROM Sesionestudio s WHERE s.fechaUltimaActualizacionEstado = :fechaUltimaActualizacionEstado")})
public class Sesionestudio implements Serializable {

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
    @Column(name = "duracion_planificada_minutos")
    private int duracionPlanificadaMinutos;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_sesion_planificada")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaSesionPlanificada;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;
    @Column(name = "fecha_inicio_real")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicioReal;
    @Column(name = "fecha_fin_real")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFinReal;
    @Column(name = "duracion_real_minutos")
    private Integer duracionRealMinutos;
    @Column(name = "tiempo_estudio_efectivo_minutos")
    private Integer tiempoEstudioEfectivoMinutos;
    @Column(name = "tiempo_descanso_minutos")
    private Integer tiempoDescansoMinutos;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "estado")
    private String estado;
    @Size(max = 1000)
    @Column(name = "notas")
    private String notas;
    @Column(name = "calificacion_enfoque")
    private Integer calificacionEnfoque;
    @Lob
    @Size(max = 65535)
    @Column(name = "detalles_interrupcion")
    private String detallesInterrupcion;
    @Lob
    @Size(max = 65535)
    @Column(name = "notas_rapidas")
    private String notasRapidas;
    @Lob
    @Size(max = 65535)
    @Column(name = "checklist")
    private String checklist;
    @Column(name = "fechaUltimaActualizacionEstado")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaUltimaActualizacionEstado;
    @JoinColumn(name = "entrega_asociada_id", referencedColumnName = "id")
    @ManyToOne
    private Entrega entregaAsociadaId;
    @JoinColumn(name = "examen_asociado_id", referencedColumnName = "id")
    @ManyToOne
    private Examen examenAsociadoId;
    @JoinColumn(name = "grupo_asociado_id", referencedColumnName = "id")
    @ManyToOne
    private Grupoestudio grupoAsociadoId;
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario usuarioId;
    @JoinColumn(name = "tecnica_aplicada_id", referencedColumnName = "id")
    @ManyToOne
    private Tecnicaestudio tecnicaAplicadaId;

    public Sesionestudio() {
    }

    public Sesionestudio(Integer id) {
        this.id = id;
    }

    public Sesionestudio(Integer id, String titulo, int duracionPlanificadaMinutos, Date fechaSesionPlanificada, Date fechaCreacion, String estado) {
        this.id = id;
        this.titulo = titulo;
        this.duracionPlanificadaMinutos = duracionPlanificadaMinutos;
        this.fechaSesionPlanificada = fechaSesionPlanificada;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
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

    public int getDuracionPlanificadaMinutos() {
        return duracionPlanificadaMinutos;
    }

    public void setDuracionPlanificadaMinutos(int duracionPlanificadaMinutos) {
        this.duracionPlanificadaMinutos = duracionPlanificadaMinutos;
    }

    public Date getFechaSesionPlanificada() {
        return fechaSesionPlanificada;
    }

    public void setFechaSesionPlanificada(Date fechaSesionPlanificada) {
        this.fechaSesionPlanificada = fechaSesionPlanificada;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaInicioReal() {
        return fechaInicioReal;
    }

    public void setFechaInicioReal(Date fechaInicioReal) {
        this.fechaInicioReal = fechaInicioReal;
    }

    public Date getFechaFinReal() {
        return fechaFinReal;
    }

    public void setFechaFinReal(Date fechaFinReal) {
        this.fechaFinReal = fechaFinReal;
    }

    public Integer getDuracionRealMinutos() {
        return duracionRealMinutos;
    }

    public void setDuracionRealMinutos(Integer duracionRealMinutos) {
        this.duracionRealMinutos = duracionRealMinutos;
    }

    public Integer getTiempoEstudioEfectivoMinutos() {
        return tiempoEstudioEfectivoMinutos;
    }

    public void setTiempoEstudioEfectivoMinutos(Integer tiempoEstudioEfectivoMinutos) {
        this.tiempoEstudioEfectivoMinutos = tiempoEstudioEfectivoMinutos;
    }

    public Integer getTiempoDescansoMinutos() {
        return tiempoDescansoMinutos;
    }

    public void setTiempoDescansoMinutos(Integer tiempoDescansoMinutos) {
        this.tiempoDescansoMinutos = tiempoDescansoMinutos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public Integer getCalificacionEnfoque() {
        return calificacionEnfoque;
    }

    public void setCalificacionEnfoque(Integer calificacionEnfoque) {
        this.calificacionEnfoque = calificacionEnfoque;
    }

    public String getDetallesInterrupcion() {
        return detallesInterrupcion;
    }

    public void setDetallesInterrupcion(String detallesInterrupcion) {
        this.detallesInterrupcion = detallesInterrupcion;
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

    public Date getFechaUltimaActualizacionEstado() {
        return fechaUltimaActualizacionEstado;
    }

    public void setFechaUltimaActualizacionEstado(Date fechaUltimaActualizacionEstado) {
        this.fechaUltimaActualizacionEstado = fechaUltimaActualizacionEstado;
    }

    public Entrega getEntregaAsociadaId() {
        return entregaAsociadaId;
    }

    public void setEntregaAsociadaId(Entrega entregaAsociadaId) {
        this.entregaAsociadaId = entregaAsociadaId;
    }

    public Examen getExamenAsociadoId() {
        return examenAsociadoId;
    }

    public void setExamenAsociadoId(Examen examenAsociadoId) {
        this.examenAsociadoId = examenAsociadoId;
    }

    public Grupoestudio getGrupoAsociadoId() {
        return grupoAsociadoId;
    }

    public void setGrupoAsociadoId(Grupoestudio grupoAsociadoId) {
        this.grupoAsociadoId = grupoAsociadoId;
    }

    public Usuario getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Usuario usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Tecnicaestudio getTecnicaAplicadaId() {
        return tecnicaAplicadaId;
    }

    public void setTecnicaAplicadaId(Tecnicaestudio tecnicaAplicadaId) {
        this.tecnicaAplicadaId = tecnicaAplicadaId;
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
        if (!(object instanceof Sesionestudio)) {
            return false;
        }
        Sesionestudio other = (Sesionestudio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rest.Sesionestudio[ id=" + id + " ]";
    }
    
}
