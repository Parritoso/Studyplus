package rest;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rest.Sesionestudio;
import rest.Usuario;

@Generated(value="EclipseLink-2.6.1.v20150605-rNA", date="2025-06-05T21:16:22")
@StaticMetamodel(Examen.class)
public class Examen_ { 

    public static volatile SingularAttribute<Examen, String> descripcion;
    public static volatile SingularAttribute<Examen, Date> fechaRecordatorio;
    public static volatile CollectionAttribute<Examen, Sesionestudio> sesionestudioCollection;
    public static volatile SingularAttribute<Examen, String> asignatura;
    public static volatile SingularAttribute<Examen, Boolean> recordatorioActivo;
    public static volatile SingularAttribute<Examen, Date> fechaExamen;
    public static volatile SingularAttribute<Examen, Date> fechaCreacion;
    public static volatile SingularAttribute<Examen, String> tipoExamen;
    public static volatile SingularAttribute<Examen, Integer> id;
    public static volatile SingularAttribute<Examen, String> nombre;
    public static volatile SingularAttribute<Examen, Usuario> usuarioId;
    public static volatile SingularAttribute<Examen, String> prioridad;

}