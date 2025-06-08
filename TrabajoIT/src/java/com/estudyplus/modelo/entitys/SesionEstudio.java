/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.modelo.entitys;

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

public class SesionEstudio implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String titulo;

    private String descripcion;

    private Integer duracionPlanificadaMinutos;
    
    private Date fechaSesionPlanificada;
    
    private Date fechaCreacion; // Cuando se creó la entrada de la sesión
    
    private Date fechaInicioReal; // Cuando el usuario realmente inició la sesión
    
    private Date fechaFinReal; // Cuando el usuario finalizó la sesión
    
    private Integer duracionRealMinutos; // Duración total desde inicio a fin real, incluyendo pausas
    
    private Integer tiempoEstudioEfectivoMinutos; // Tiempo real de estudio sin pausas
    
    private Integer tiempoDescansoMinutos; // Tiempo de descanso acumulado
    private Date fechaUltimaActualizacionEstado;
    
    private String estado; // PLANNED, ACTIVE, PAUSED, COMPLETED, ABORTED
    
    private String notas;
    
    private Integer calificacionEnfoque; // Calificación de 1 a 5, por ejemplo
    
    private String detallesInterrupcion; // Texto para anotar interrupciones
    
    private Usuario usuarioId; // Referencia al Usuario propietario
    
    private TecnicaEstudio tecnicaAplicadaId; // Referencia a la Técnica de Estudio utilizada
    
    private Entrega entregaAsociada; // Si la sesión está ligada a una entrega
    
    private Examen examenAsociado; // Si la sesión está ligada a un examen
    private String notasRapidas;
    private String checklist;
    private GrupoEstudio GrupoAsociadoId;

    public SesionEstudio() {
    }

    public SesionEstudio(Integer id) {
        this.id = id;
    }

    public SesionEstudio(Integer id, String titulo, int duracionPlanificadaMinutos, Date fechaSesionPlanificada, Usuario usuarioId) {
        this.id = id;
        this.titulo = titulo;
        this.duracionPlanificadaMinutos = duracionPlanificadaMinutos;
        this.fechaSesionPlanificada = fechaSesionPlanificada;
        this.usuarioId = usuarioId;
        this.fechaCreacion = new Date(); // Establecer la fecha de creación por defecto
        this.estado = "PLANNED"; // Estado inicial por defecto
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

    public Integer getDuracionPlanificadaMinutos() {
        return duracionPlanificadaMinutos;
    }

    public void setDuracionPlanificadaMinutos(Integer duracionPlanificadaMinutos) {
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

    public Entrega getEntregaAsociada() {
        return entregaAsociada;
    }

    public void setEntregaAsociada(Entrega entregaAsociada) {
        this.entregaAsociada = entregaAsociada;
    }

    public Examen getExamenAsociado() {
        return examenAsociado;
    }

    public void setExamenAsociado(Examen examenAsociado) {
        this.examenAsociado = examenAsociado;
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

    public Usuario getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Usuario usuarioId) {
        this.usuarioId = usuarioId;
    }

    public TecnicaEstudio getTecnicaAplicadaId() {
        return tecnicaAplicadaId;
    }

    public void setTecnicaAplicadaId(TecnicaEstudio tecnicaAplicadaId) {
        this.tecnicaAplicadaId = tecnicaAplicadaId;
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

    public GrupoEstudio getGrupoAsociadoId() {
        return GrupoAsociadoId;
    }

    public void setGrupoAsociadoId(GrupoEstudio GrupoAsociadoId) {
        this.GrupoAsociadoId = GrupoAsociadoId;
    }

    public Date getFechaUltimaActualizacionEstado() {
        return fechaUltimaActualizacionEstado;
    }

    public void setFechaUltimaActualizacionEstado(Date fechaUltimaActualizacionEstado) {
        this.fechaUltimaActualizacionEstado = fechaUltimaActualizacionEstado;
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
        if (!(object instanceof SesionEstudio)) {
            return false;
        }
        SesionEstudio other = (SesionEstudio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.estudyplus.modelo.entitys.Sesionestudio[ id=" + id + " ]";
    }

    public String getEstadoString() {
        return estado;
    }
    
}
