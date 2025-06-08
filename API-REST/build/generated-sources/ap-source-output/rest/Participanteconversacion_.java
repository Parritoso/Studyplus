package rest;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rest.Conversacion;
import rest.ParticipanteconversacionPK;
import rest.Usuario;

@Generated(value="EclipseLink-2.6.1.v20150605-rNA", date="2025-06-05T21:16:22")
@StaticMetamodel(Participanteconversacion.class)
public class Participanteconversacion_ { 

    public static volatile SingularAttribute<Participanteconversacion, Conversacion> conversacion;
    public static volatile SingularAttribute<Participanteconversacion, Date> fechaUnion;
    public static volatile SingularAttribute<Participanteconversacion, Usuario> usuario;
    public static volatile SingularAttribute<Participanteconversacion, ParticipanteconversacionPK> participanteconversacionPK;

}