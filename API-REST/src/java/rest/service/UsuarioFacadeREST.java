/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.Grupoestudio;
import rest.Participantegrupo;
import rest.Usuario;
import rest.dto.LoginRequest;
import rest.dto.PasswordChangeRequest;
import rest.dto.UsuarioResponseDTO;

/**
 *
 * @author Parri
 */
@Stateless
@Path("rest.usuario")
public class UsuarioFacadeREST extends AbstractFacade<Usuario> {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuarioFacadeREST.class);

    @PersistenceContext(unitName = "API-RESTPU")
    private EntityManager em;

    public UsuarioFacadeREST() {
        super(Usuario.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Usuario entity) {
        try{
            super.create(entity);
        } catch(Exception e){
            logger.debug("[create] Error en create: "+e.getMessage());
        }
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Usuario entity) {
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
    public Usuario find(@PathParam("id") Integer id) {
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
    public List<Usuario> findAll() {
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
    public List<Usuario> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    /**
     * Endpoint para el registro de nuevos usuarios.
     * Hashea la contraseña antes de guardar el usuario en la base de datos.
     *
     * @param usuario El objeto Usuario a registrar.
     * @return Response con el Usuario creado (201 Created) o error (400 Bad Request, 500 Internal Server Error).
     */
    @POST
    @Path("register") // /rest.usuario/register
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) // Devuelve el usuario creado
    public Response register(Usuario usuario) {
        if (usuario == null || usuario.getNombre() == null || usuario.getNombre().isEmpty() ||
            usuario.getContrasena() == null || usuario.getContrasena().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Nombre de usuario y contraseña son obligatorios.").build();
        }
        
        logger.debug("[register] Nombre y contraseñas validas");

        // 1. Verificar si el nombre de usuario ya existe
        try {
            TypedQuery<Usuario> query = em.createNamedQuery("Usuario.findByNombre", Usuario.class)
                                         .setParameter("nombre", usuario.getNombre());
            query.getSingleResult(); // Esto lanzará NoResultException si no lo encuentra
            logger.debug("[register] El nombre de usuario ya está en uso.");
            return Response.status(Response.Status.CONFLICT).entity("El nombre de usuario ya está en uso.").build(); // 409 Conflict
        } catch (NoResultException e) {
            // OK, el usuario no existe, podemos continuar con el registro
        } catch (Exception e) {
            logger.debug("[register] Error interno al registrar usuario: "+e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error interno al registrar usuario.").build();
        }

        // 2. Hashear la contraseña
        try {
            String hashedPassword = BCrypt.hashpw(usuario.getContrasena(), BCrypt.gensalt());
            usuario.setContrasena(hashedPassword); // Asigna la contraseña hasheada
            // Establece la fecha de registro/creación
            usuario.setFechaRegistro(new Date()); // Asumo que tienes un campo fechaRegistro en Usuario
            usuario.setPuntos(0);

            // 3. Persistir el nuevo usuario
            super.create(usuario); // Llama al método create del AbstractFacade
            // Devuelve el usuario creado, con la contraseña hasheada (no enviar el hash al cliente si no es necesario)
            // Considera devolver un DTO de Usuario sin el hash de contraseña
            return Response.status(Response.Status.CREATED).entity(usuario).build(); // 201 Created
        } catch (Exception e) {
            logger.debug("[register] Error interno al registrar usuario: "+ e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error interno al registrar usuario.").build();
        }
    }

    /**
     * Endpoint para la autenticación de usuarios.
     * Verifica la contraseña proporcionada contra el hash almacenado.
     *
     * @param loginRequest Objeto LoginRequest con username y password.
     * @return Response con el Usuario autenticado (200 OK) o error (401 Unauthorized, 400 Bad Request, 500 Internal Server Error).
     */
    @POST
    @Path("authenticate") // /rest.usuario/authenticate
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty() ||
            loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Nombre de usuario y contraseña son obligatorios.").build();
        }

        logger.debug("[authenticate] Nombre y contraseñas validas");
        
        Usuario usuario = null;
        try {
            TypedQuery<Usuario> query = em.createNamedQuery("Usuario.findByNombre", Usuario.class)
                                         .setParameter("nombre", loginRequest.getUsername());
            usuario = query.getSingleResult();
        } catch (NoResultException e) {
            logger.debug("[authenticate] Credenciales invalidas");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Credenciales inválidas.").build(); // 401 Unauthorized
        } catch (Exception e) {
            logger.debug("[authenticate] Error interno: "+e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error interno al autenticar.").build();
        }

        // Verificar la contraseña hasheada
        if (BCrypt.checkpw(loginRequest.getPassword(), usuario.getContrasena())) {
            // No devolver la contraseña hasheada al cliente por seguridad.
            // Considera crear un DTO de Usuario para la respuesta de autenticación.
            UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getEmail(),
                    usuario.getPuntos(),
                    usuario.getConfiguracionPerfil(),
                    usuario.getFechaRegistro()
                );
            return Response.ok(responseDTO).build(); // 200 OK
        } else {
            logger.debug("[authenticate] Credenciales invalidas, hash incorrecto");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Credenciales inválidas.").build(); // 401 Unauthorized
        }
    }

    /**
     * Endpoint para cambiar la contraseña de un usuario.
     * Se requiere el ID del usuario en la URL y la nueva contraseña en el cuerpo.
     *
     * @param id El ID del usuario.
     * @param passwordChangeRequest Objeto PasswordChangeRequest con la nueva contraseña.
     * @return Response (200 OK, 404 Not Found, 400 Bad Request, 500 Internal Server Error).
     */
    @PUT
    @Path("{id}/password") // /rest.usuario/{id}/password
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(@PathParam("id") Integer id, PasswordChangeRequest passwordChangeRequest) {
        if (passwordChangeRequest == null || passwordChangeRequest.getNewPassword() == null || passwordChangeRequest.getNewPassword().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("La nueva contraseña no puede estar vacía.").build();
        }

        logger.debug("[changePassword] La nueva contraseña no puede estar vacía.");
        
        Usuario usuario = super.find(id);
        if (usuario == null) {
            logger.debug("[changePassword] Usuario no encontrado.");
            return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado.").build(); // 404 Not Found
        }

        try {
            String newHashedPassword = BCrypt.hashpw(passwordChangeRequest.getNewPassword(), BCrypt.gensalt());
            usuario.setContrasena(newHashedPassword); // Actualiza la contraseña hasheada
            super.edit(usuario); // Persiste el cambio
            return Response.ok().entity("Contraseña cambiada con éxito.").build(); // 200 OK
        } catch (Exception e) {
            logger.debug("[changePassword] Error interno al cambiar la contraseña: "+e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error interno al cambiar la contraseña.").build();
        }
    }
    /**
     * Endpoint para buscar usuarios por nombre (o parte del nombre).
     * @param query El término de búsqueda.
     * @return Lista de usuarios que coinciden con el término de búsqueda.
     */
    @GET
    @Path("search") // /rest.usuario/search?q={query}
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usuario> searchUsers(@javax.ws.rs.QueryParam("q") String query) {
        logger.debug("[searchUsers] Inicio de búsqueda con query: '{}'", query);
        if (query == null || query.trim().isEmpty()) {
            logger.debug("[searchUsers] La consulta está vacía o es nula.");
            return new ArrayList<>(); // Devolver lista vacía si la consulta está vacía
        }
        try {
            // Asumo que quieres buscar por el campo 'nombre' del usuario
            TypedQuery<Usuario> q = em.createQuery("SELECT u FROM Usuario u WHERE LOWER(u.nombre) LIKE LOWER(:query)", Usuario.class);
            q.setParameter("query", "%" + query + "%");
            List<Usuario> resultados = q.getResultList();
            logger.debug("[searchUsers] Se encontraron {} usuarios que coinciden con el término de búsqueda.", resultados.size());
            return resultados;
        } catch (Exception e) {
            logger.debug("[searchUsers] Error al buscar usuarios: {}", e.getMessage(), e);
            e.printStackTrace();
            throw new WebApplicationException("Error al buscar usuarios.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para obtener la lista de grupos a los que pertenece un usuario.
     * @param userId El ID del usuario.
     * @return Lista de Grupoestudio a los que pertenece el usuario.
     */
    @GET
    @Path("{id}/grupos") // /rest.usuario/{id}/grupos
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Grupoestudio> getGruposDeUsuario(@PathParam("id") Integer userId) {
        logger.debug("[getGruposDeUsuario] Obteniendo grupos para el usuario con ID: {}", userId);
        try {
            // Se asume que Participantegrupo tiene un mapeo a Usuario (usuarioId) y Grupoestudio (grupoId)
            // Y que Participantegrupo es una entidad JPA accesible.
            List<Participantegrupo> participantes = em.createQuery(
                "SELECT p FROM Participantegrupo p WHERE p.usuarioId.id = :userId", Participantegrupo.class)
                .setParameter("userId", userId)
                .getResultList();

            logger.debug("[getGruposDeUsuario] Se encontraron {} participaciones para el usuario.", participantes.size());
            // Mapear Participantegrupo a Grupoestudio y eliminar duplicados
            
            List<Grupoestudio> grupos = participantes.stream()
                .map(Participantegrupo::getGrupoId) // Asumiendo que getGrupoId devuelve Grupoestudio
                .filter(Objects::nonNull)
                .distinct() // Asegura que cada grupo aparece solo una vez
                .collect(Collectors.toList());
            logger.debug("[getGruposDeUsuario] Total de grupos únicos encontrados: {}", grupos.size());
            return grupos;
        } catch (NoResultException e) {
            logger.debug("[getGruposDeUsuario] No se encontraron resultados para el usuario con ID: {}", userId);
            // Si no hay resultados, simplemente devuelve una lista vacía
            return new ArrayList<>();
        } catch (Exception e) {
            logger.debug("[getGruposDeUsuario] Error al obtener grupos del usuario: {}", e.getMessage(), e);
            e.printStackTrace(); // Log the exception for debugging
            throw new WebApplicationException("Error al obtener grupos del usuario", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GET
    @Path("addPuntos{puntos}/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void updatePuntos(@PathParam("puntos") Integer puntos, @PathParam("id") Integer userId){
        try{
           em.createQuery("UPDATE Usuario u SET u.puntos = u.puntos + :puntos WHERE u.id = :id")
            .setParameter("puntos", puntos)
            .setParameter("id", userId)
            .executeUpdate();
        } catch(Exception e){
            logger.debug("[updatePuntos] Error al añadir puntos "+ e.getMessage());
        }
    }
    
    @GET
    @Path("ranking")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usuario> getRanking(){
        try{
            List<Usuario> us = em.createQuery("SELECT u FROM Usuario u ORDER BY u.puntos DESC").setMaxResults(10).getResultList();
            logger.debug("[getRanking] Lista: "+Arrays.toString(us.toArray()));
            return us;
        }catch (NoResultException e){
            logger.debug("[getRanking] Error al obtener ranking global: {}", e.getMessage(), e);
            e.printStackTrace();
            return new ArrayList<>();
        }catch (Exception e){
            logger.debug("[getRanking] Error al obtener ranking global: {}", e.getMessage(), e);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
}
