package rest;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rest.Usuario;

@Generated(value="EclipseLink-2.6.1.v20150605-rNA", date="2025-06-05T21:16:22")
@StaticMetamodel(Logproductividad.class)
public class Logproductividad_ { 

    public static volatile SingularAttribute<Logproductividad, String> tipoEvento;
    public static volatile SingularAttribute<Logproductividad, Date> fechaRegistro;
    public static volatile SingularAttribute<Logproductividad, Integer> duracionDescansoTotalMinutos;
    public static volatile SingularAttribute<Logproductividad, Integer> calificacionEnfoque;
    public static volatile SingularAttribute<Logproductividad, String> notas;
    public static volatile SingularAttribute<Logproductividad, Integer> sesionEstudioId;
    public static volatile SingularAttribute<Logproductividad, Integer> id;
    public static volatile SingularAttribute<Logproductividad, String> nivelFatiga;
    public static volatile SingularAttribute<Logproductividad, Integer> duracionEstudioTotalMinutos;
    public static volatile SingularAttribute<Logproductividad, Usuario> usuarioId;

}