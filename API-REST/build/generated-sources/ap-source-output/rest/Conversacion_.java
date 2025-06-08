package rest;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rest.Mensaje;
import rest.Participanteconversacion;

@Generated(value="EclipseLink-2.6.1.v20150605-rNA", date="2025-06-05T21:16:22")
@StaticMetamodel(Conversacion.class)
public class Conversacion_ { 

    public static volatile CollectionAttribute<Conversacion, Participanteconversacion> participanteconversacionCollection;
    public static volatile CollectionAttribute<Conversacion, Mensaje> mensajeCollection;
    public static volatile SingularAttribute<Conversacion, Date> ultimoMensajeFecha;
    public static volatile SingularAttribute<Conversacion, Date> fechaCreacion;
    public static volatile SingularAttribute<Conversacion, Integer> id;

}