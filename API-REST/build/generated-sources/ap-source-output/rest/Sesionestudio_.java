package rest;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rest.Entrega;
import rest.Examen;
import rest.Grupoestudio;
import rest.Tecnicaestudio;
import rest.Usuario;

@Generated(value="EclipseLink-2.6.1.v20150605-rNA", date="2025-06-05T21:16:22")
@StaticMetamodel(Sesionestudio.class)
public class Sesionestudio_ { 

    public static volatile SingularAttribute<Sesionestudio, String> descripcion;
    public static volatile SingularAttribute<Sesionestudio, String> estado;
    public static volatile SingularAttribute<Sesionestudio, Integer> duracionRealMinutos;
    public static volatile SingularAttribute<Sesionestudio, String> notas;
    public static volatile SingularAttribute<Sesionestudio, String> titulo;
    public static volatile SingularAttribute<Sesionestudio, Integer> tiempoDescansoMinutos;
    public static volatile SingularAttribute<Sesionestudio, String> checklist;
    public static volatile SingularAttribute<Sesionestudio, Date> fechaSesionPlanificada;
    public static volatile SingularAttribute<Sesionestudio, String> detallesInterrupcion;
    public static volatile SingularAttribute<Sesionestudio, Usuario> usuarioId;
    public static volatile SingularAttribute<Sesionestudio, Tecnicaestudio> tecnicaAplicadaId;
    public static volatile SingularAttribute<Sesionestudio, Integer> tiempoEstudioEfectivoMinutos;
    public static volatile SingularAttribute<Sesionestudio, Date> fechaFinReal;
    public static volatile SingularAttribute<Sesionestudio, String> notasRapidas;
    public static volatile SingularAttribute<Sesionestudio, Integer> calificacionEnfoque;
    public static volatile SingularAttribute<Sesionestudio, Integer> duracionPlanificadaMinutos;
    public static volatile SingularAttribute<Sesionestudio, Date> fechaInicioReal;
    public static volatile SingularAttribute<Sesionestudio, Entrega> entregaAsociadaId;
    public static volatile SingularAttribute<Sesionestudio, Date> fechaCreacion;
    public static volatile SingularAttribute<Sesionestudio, Date> fechaUltimaActualizacionEstado;
    public static volatile SingularAttribute<Sesionestudio, Examen> examenAsociadoId;
    public static volatile SingularAttribute<Sesionestudio, Integer> id;
    public static volatile SingularAttribute<Sesionestudio, Grupoestudio> grupoAsociadoId;

}