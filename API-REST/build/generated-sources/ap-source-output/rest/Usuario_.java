package rest;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rest.Amistad;
import rest.Entrega;
import rest.Examen;
import rest.Grupoestudio;
import rest.Logproductividad;
import rest.Mensaje;
import rest.Participanteconversacion;
import rest.Participantegrupo;
import rest.Sesionestudio;

@Generated(value="EclipseLink-2.6.1.v20150605-rNA", date="2025-06-05T21:16:22")
@StaticMetamodel(Usuario.class)
public class Usuario_ { 

    public static volatile CollectionAttribute<Usuario, Amistad> amistadCollection1;
    public static volatile SingularAttribute<Usuario, String> configuracionPerfil;
    public static volatile CollectionAttribute<Usuario, Mensaje> mensajeCollection;
    public static volatile CollectionAttribute<Usuario, Logproductividad> logproductividadCollection;
    public static volatile SingularAttribute<Usuario, Date> fechaRegistro;
    public static volatile CollectionAttribute<Usuario, Entrega> entregaCollection;
    public static volatile SingularAttribute<Usuario, Integer> puntos;
    public static volatile SingularAttribute<Usuario, String> nombre;
    public static volatile CollectionAttribute<Usuario, Participantegrupo> participantegrupoCollection;
    public static volatile CollectionAttribute<Usuario, Grupoestudio> grupoestudioCollection;
    public static volatile CollectionAttribute<Usuario, Sesionestudio> sesionestudioCollection;
    public static volatile CollectionAttribute<Usuario, Participanteconversacion> participanteconversacionCollection;
    public static volatile SingularAttribute<Usuario, String> contrasena;
    public static volatile CollectionAttribute<Usuario, Examen> examenCollection;
    public static volatile SingularAttribute<Usuario, Integer> id;
    public static volatile CollectionAttribute<Usuario, Amistad> amistadCollection;
    public static volatile SingularAttribute<Usuario, String> email;

}