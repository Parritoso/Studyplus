/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
import rest.Tecnicaestudio;
import rest.dto.TipoTecnica;

/**
 *
 * @author Parri
 */
@Stateless
@Path("rest.tecnicaestudio")
public class TecnicaestudioFacadeREST extends AbstractFacade<Tecnicaestudio> {
    private static final Logger logger = LoggerFactory.getLogger(TecnicaestudioFacadeREST.class);

    @PersistenceContext(unitName = "API-RESTPU")
    private EntityManager em;

    public TecnicaestudioFacadeREST() {
        super(Tecnicaestudio.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Tecnicaestudio entity) {
        try{
           super.create(entity);
        } catch(Exception e){
            logger.debug("[create] Error en create: "+e.getMessage());
        }
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Tecnicaestudio entity) {
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
    public Tecnicaestudio find(@PathParam("id") Integer id) {
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
    public List<Tecnicaestudio> findAll() {
        try{
           logger.debug("[findAll] Total de tecnicas encontradas: {}",super.findAll().size());
           return super.findAll();
        } catch(Exception e){
            logger.debug("[findAll] Error en create: "+e.getMessage());
        }
        return Collections.EMPTY_LIST;
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Tecnicaestudio> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        try{
           return super.findRange(new int[]{from, to}); 
        } catch(Exception e){
            logger.debug("[findRange] Error en create: "+e.getMessage());
        }
        return Collections.EMPTY_LIST;
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }
    
    @GET
    @Path("individual")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Tecnicaestudio> findIndividual() {
        // Implementa la lógica para filtrar y devolver solo las técnicas de tipo INDIVIDUAL
        logger.debug("[findIndividual] Buscando técnicas de estudio de tipo INDIVIDUAL");

        try {
            List<Tecnicaestudio> tecnicas = em.createNamedQuery("Tecnicaestudio.findByTipoTecnica", Tecnicaestudio.class)
                    .setParameter("tipoTecnica", TipoTecnica.INDIVIDUAL.name())
                    .getResultList();

            logger.debug("[findIndividual] Se encontraron {} técnicas de estudio de tipo INDIVIDUAL.", tecnicas.size());
            return tecnicas;
        } catch (NoResultException e) {
            logger.debug("[findIndividual] No se encontraron técnicas de estudio de tipo INDIVIDUAL.");
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            logger.error("[findIndividual] Error al obtener las técnicas de estudio de tipo INDIVIDUAL. Error: {}", e.getMessage(), e);
            throw new WebApplicationException("Error al obtener las técnicas de estudio de tipo INDIVIDUAL.", Response.Status.INTERNAL_SERVER_ERROR);
        }
        // Ejemplo: return em.createNamedQuery("TecnicaEstudio.findByTipo", TecnicaEstudio.class)
        //             .setParameter("tipo", TipoTecnica.INDIVIDUAL)
        //             .getResultList();
        // Necesitarías un NamedQuery en tu entidad TecnicaEstudio:
        // @NamedQuery(name = "TecnicaEstudio.findByTipo", query = "SELECT t FROM TecnicaEstudio t WHERE t.tipoTecnica = :tipo")
    }
    
    @GET
    @Path("grupal")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Tecnicaestudio> findGrupal() {
        // Implementa la lógica para filtrar y devolver solo las técnicas de tipo INDIVIDUAL
        logger.debug("[findIndividual] Buscando técnicas de estudio de tipo GRUPAL");

        try {
            List<Tecnicaestudio> tecnicas = em.createNamedQuery("Tecnicaestudio.findByTipoTecnica", Tecnicaestudio.class)
                    .setParameter("tipoTecnica", TipoTecnica.GRUPO.name())
                    .getResultList();

            logger.debug("[findIndividual] Se encontraron {} técnicas de estudio de tipo GRUPO.", tecnicas.size());
            return tecnicas;
        } catch (NoResultException e) {
            logger.debug("[findIndividual] No se encontraron técnicas de estudio de tipo GRUPO.");
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            logger.error("[findIndividual] Error al obtener las técnicas de estudio de tipo GRUPO. Error: {}", e.getMessage(), e);
            throw new WebApplicationException("Error al obtener las técnicas de estudio de tipo GRUPO.", Response.Status.INTERNAL_SERVER_ERROR);
        }
        // Ejemplo: return em.createNamedQuery("TecnicaEstudio.findByTipo", TecnicaEstudio.class)
        //             .setParameter("tipo", TipoTecnica.INDIVIDUAL)
        //             .getResultList();
        // Necesitarías un NamedQuery en tu entidad TecnicaEstudio:
        // @NamedQuery(name = "TecnicaEstudio.findByTipo", query = "SELECT t FROM TecnicaEstudio t WHERE t.tipoTecnica = :tipo")
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
