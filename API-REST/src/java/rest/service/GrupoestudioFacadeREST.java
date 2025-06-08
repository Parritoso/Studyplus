/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.Entrega;
import rest.Grupoestudio;
import rest.Participantegrupo;
import rest.Usuario;

/**
 *
 * @author Parri
 */
@Stateless
@Path("rest.grupoestudio")
public class GrupoestudioFacadeREST extends AbstractFacade<Grupoestudio> {
    private static final Logger logger = LoggerFactory.getLogger(GrupoestudioFacadeREST.class);

    @PersistenceContext(unitName = "API-RESTPU")
    private EntityManager em;

    public GrupoestudioFacadeREST() {
        super(Grupoestudio.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Grupoestudio entity) {
        try{
           super.create(entity);
        } catch(Exception e){
            logger.debug("[create] Error en create: "+e.getMessage());
        }
    }

    /**
     * Crea una nueva Sesión de Estudio. Ahora devuelve la entidad SesionEstudio
     * creada con el ID asignado por la base de datos.
     *
     * @param entity El objeto SesionEstudio a crear.
     * @return La SesionEstudio creada con su ID.
     */
    @POST
    @Path("instant")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) // Añadir Produces para que devuelva la entidad
    public Grupoestudio create_instant(Grupoestudio entity) {
        try{
           super.create(entity); // Esto persiste la entidad en la base de datos
           em.flush(); // Asegura que el ID generado por la DB sea asignado al objeto 'entity'
           return entity; // Devuelve la entidad con el ID
        } catch(Exception e){
            logger.debug("[create_instant] Error en create: "+e.getMessage());
        }
        return null;
    }
    
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Grupoestudio entity) {
        try{
           super.edit(entity);
        } catch(Exception e){
            logger.debug("[edit] Error en create: "+e.getMessage());
        }
        
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        try{
           super.remove(super.find(id));
        } catch(Exception e){
            logger.debug("[remove] Error en create: "+e.getMessage());
        }
        
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Grupoestudio find(@PathParam("id") Integer id) {
        try{
           return super.find(id);
        } catch(Exception e){
            logger.debug("[find] Error en create: "+e.getMessage());
        }
        return null;
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Grupoestudio> findAll() {
        try{
            return super.findAll();
        } catch(Exception e){
            logger.debug("[findAll] Error en create: "+e.getMessage());
        }
        return Collections.EMPTY_LIST;
        
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Grupoestudio> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        try{
           return super.findRange(new int[]{from, to}); 
        } catch(Exception e){
            logger.debug("[findRange] Error en create: "+e.getMessage());
        }
        return Collections.EMPTY_LIST;
    }
    
    @GET
    @Path("{id}/participantes")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usuario> getParticipantesByGroupId(@PathParam("id") Integer groupId) {
        logger.debug("[getParticipantesByGroupId] Buscando participantes del grupo con ID: {}", groupId);
        try {
            Grupoestudio grupo = super.find(groupId);
            if (grupo == null) {
                logger.debug("[getParticipantesByGroupId] Grupo no encontrado con ID: {}", groupId);
                return new ArrayList<>();
            }

            if (grupo.getParticipantegrupoCollection() == null) {
                logger.debug("[getParticipantesByGroupId] El grupo con ID {} no tiene participantes.", groupId);
                return new ArrayList<>();
            }

            List<Usuario> participantes = em.createQuery("SELECT p.usuarioId FROM Participantegrupo p WHERE p.grupoId.id = :id").setParameter("id", groupId).getResultList();
//            List<Usuario> participantes = grupo.getParticipantegrupoCollection().stream()
//                .map(Participantegrupo::getUsuarioId)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());

            logger.debug("[getParticipantesByGroupId] Se encontraron {} participantes.", participantes.size());
            return participantes;
        } catch (Exception e) {
            logger.debug("[getParticipantesByGroupId] Error al obtener participantes del grupo: {}", e.getMessage(), e);
            throw new WebApplicationException("Error al obtener participantes del grupo.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
