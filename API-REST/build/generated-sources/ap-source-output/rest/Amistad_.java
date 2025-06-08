package rest;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rest.Usuario;

@Generated(value="EclipseLink-2.6.1.v20150605-rNA", date="2025-06-05T21:16:22")
@StaticMetamodel(Amistad.class)
public class Amistad_ { 

    public static volatile SingularAttribute<Amistad, String> estado;
    public static volatile SingularAttribute<Amistad, Date> fechaPeticion;
    public static volatile SingularAttribute<Amistad, Usuario> usuario2Id;
    public static volatile SingularAttribute<Amistad, Usuario> usuario1Id;
    public static volatile SingularAttribute<Amistad, Integer> id;
    public static volatile SingularAttribute<Amistad, Date> fechaRespuesta;

}