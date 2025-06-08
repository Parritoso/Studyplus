package rest;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rest.Conversacion;
import rest.Usuario;

@Generated(value="EclipseLink-2.6.1.v20150605-rNA", date="2025-06-05T21:16:22")
@StaticMetamodel(Mensaje.class)
public class Mensaje_ { 

    public static volatile SingularAttribute<Mensaje, String> contenido;
    public static volatile SingularAttribute<Mensaje, Date> fechaEnvio;
    public static volatile SingularAttribute<Mensaje, Usuario> emisorId;
    public static volatile SingularAttribute<Mensaje, Conversacion> conversacionId;
    public static volatile SingularAttribute<Mensaje, Boolean> leido;
    public static volatile SingularAttribute<Mensaje, Integer> id;

}