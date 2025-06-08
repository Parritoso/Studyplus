package rest;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rest.Sesionestudio;
import rest.Usuario;

@Generated(value="EclipseLink-2.6.1.v20150605-rNA", date="2025-06-05T21:16:22")
@StaticMetamodel(Entrega.class)
public class Entrega_ { 

    public static volatile SingularAttribute<Entrega, String> descripcion;
    public static volatile SingularAttribute<Entrega, String> estado;
    public static volatile SingularAttribute<Entrega, String> asignatura;
    public static volatile SingularAttribute<Entrega, Boolean> recordatorioActivo;
    public static volatile SingularAttribute<Entrega, String> titulo;
    public static volatile SingularAttribute<Entrega, Usuario> usuarioId;
    public static volatile SingularAttribute<Entrega, String> prioridad;
    public static volatile SingularAttribute<Entrega, Date> fechaRecordatorio;
    public static volatile CollectionAttribute<Entrega, Sesionestudio> sesionestudioCollection;
    public static volatile SingularAttribute<Entrega, Date> fechaLimite;
    public static volatile SingularAttribute<Entrega, Integer> fechaEntregaReal;
    public static volatile SingularAttribute<Entrega, Integer> fechaCreacion;
    public static volatile SingularAttribute<Entrega, Integer> id;

}