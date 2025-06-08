package rest;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rest.Sesionestudio;

@Generated(value="EclipseLink-2.6.1.v20150605-rNA", date="2025-06-05T21:16:22")
@StaticMetamodel(Tecnicaestudio.class)
public class Tecnicaestudio_ { 

    public static volatile SingularAttribute<Tecnicaestudio, String> descripcion;
    public static volatile CollectionAttribute<Tecnicaestudio, Sesionestudio> sesionestudioCollection;
    public static volatile SingularAttribute<Tecnicaestudio, String> tipoTecnica;
    public static volatile SingularAttribute<Tecnicaestudio, Integer> id;
    public static volatile SingularAttribute<Tecnicaestudio, Integer> duracionFocoMinutos;
    public static volatile SingularAttribute<Tecnicaestudio, String> nombre;
    public static volatile SingularAttribute<Tecnicaestudio, Integer> duracionDescansoMinutos;

}