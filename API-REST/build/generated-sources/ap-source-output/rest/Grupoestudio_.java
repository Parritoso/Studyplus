package rest;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import rest.Participantegrupo;
import rest.Sesionestudio;
import rest.Usuario;

@Generated(value="EclipseLink-2.6.1.v20150605-rNA", date="2025-06-05T21:16:22")
@StaticMetamodel(Grupoestudio.class)
public class Grupoestudio_ { 

    public static volatile SingularAttribute<Grupoestudio, String> descripcion;
    public static volatile SingularAttribute<Grupoestudio, Usuario> creadorUsuarioId;
    public static volatile CollectionAttribute<Grupoestudio, Sesionestudio> sesionestudioCollection;
    public static volatile SingularAttribute<Grupoestudio, String> notasRapidas;
    public static volatile SingularAttribute<Grupoestudio, Date> fechaCreacion;
    public static volatile SingularAttribute<Grupoestudio, String> checklist;
    public static volatile SingularAttribute<Grupoestudio, Integer> id;
    public static volatile SingularAttribute<Grupoestudio, String> nombre;
    public static volatile CollectionAttribute<Grupoestudio, Participantegrupo> participantegrupoCollection;

}