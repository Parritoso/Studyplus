package rest;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rest.Grupoestudio;
import rest.Usuario;

@Generated(value="EclipseLink-2.6.1.v20150605-rNA", date="2025-06-05T21:16:22")
@StaticMetamodel(Participantegrupo.class)
public class Participantegrupo_ { 

    public static volatile SingularAttribute<Participantegrupo, Date> fechaUnion;
    public static volatile SingularAttribute<Participantegrupo, Integer> id;
    public static volatile SingularAttribute<Participantegrupo, Grupoestudio> grupoId;
    public static volatile SingularAttribute<Participantegrupo, Usuario> usuarioId;
    public static volatile SingularAttribute<Participantegrupo, String> rol;

}