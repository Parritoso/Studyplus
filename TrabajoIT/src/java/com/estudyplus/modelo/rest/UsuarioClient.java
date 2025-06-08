/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.modelo.rest;

import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.modelo.entitys.GrupoEstudio;
import com.estudyplus.modelo.entitys.ParticipanteGrupo;
import com.estudyplus.modelo.entitys.Usuario;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Jersey REST client generated for REST resource:UsuarioFacadeREST
 * [rest.usuario]<br>
 * USAGE:
 * <pre>
 *        UsuarioClient_test client = new UsuarioClient_test();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author Parri
 */
public class UsuarioClient {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8080/API-REST/webresources";

    public UsuarioClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("rest.usuario");
    }

    public String countREST() throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("count");
        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    public void edit_XML(Object requestEntity, String id) throws ClientErrorException {
        webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_XML).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML));
    }

    public Usuario edit_JSON(Object requestEntity, String id) throws ClientErrorException {
        Response response = webTarget.path(MessageFormat.format("{0}", new Object[]{id}))
                                     .request(MediaType.APPLICATION_JSON)
                                     .put(Entity.entity(requestEntity, MediaType.APPLICATION_JSON));
        
        // ACEPTAR TANTO 200 OK COMO 204 NO CONTENT
        if (response.getStatus() == Response.Status.OK.getStatusCode() ||
            response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {

            // Si la respuesta es 204, no habrá entidad para leer.
            // Si la respuesta es 200 (si cambiaras el server para devolver una entidad), sí la habría.
            // Aquí se asume que si es 204, no hay necesidad de leer una entidad Usuario.
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(Usuario.class);
            } else { // Es 204 NO_CONTENT
                return null; // O devolver un Usuario vacío si tu lógica lo requiere, pero típicamente para 204 no se espera un cuerpo.
            }
        } else {
            // Para otros códigos de estado (4xx, 5xx) sigue lanzando el error
            String errorMsg = response.readEntity(String.class);
            throw new ClientErrorException("Error al editar usuario: " + response.getStatus() + " - " + errorMsg, response.getStatus());
        }
    }

    public <T> T find_XML(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T find_JSON(Class<T> responseType, String id) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T findRange_XML(Class<T> responseType, String from, String to) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[]{from, to}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T findRange_JSON(Class<T> responseType, String from, String to) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[]{from, to}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public void create_XML(Object requestEntity) throws ClientErrorException {
        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML));
    }

    public void create_JSON(Object requestEntity) throws ClientErrorException {
        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON));
    }

    public <T> T findAll_XML(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T findAll_JSON(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public void remove(String id) throws ClientErrorException {
        webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request().delete();
    }

    public Response register(Object requestEntity) throws ClientErrorException {
        return webTarget.path("register").request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
    }

    /**
     * Intenta autenticar un usuario con el nombre de usuario y la contraseña proporcionados.
     *
     * Asume que la API REST externa tiene un endpoint de autenticación como
     * `http://localhost:8080/API-REST/webresources/rest.usuario/authenticate`
     * que espera un JSON con `{"username": "...", "password": "..."}`
     * y devuelve el objeto Usuario autenticado si es exitoso (200 OK),
     * o un estado de error (401 Unauthorized, 404 Not Found) si las credenciales son inválidas.
     *
     * @param username El nombre de usuario.
     * @param password La contraseña en texto plano.
     * @return El objeto {@link Usuario} autenticado si las credenciales son válidas,
     * o {@code null} si la autenticación falla (credenciales inválidas/usuario no encontrado).
     * @throws ClientErrorException Si ocurre un error de comunicación con la API REST externa
     * (distinto de 401/404 que se manejan devolviendo null).
     */
    public Usuario authenticate(String username, String password) throws ClientErrorException {
        // Apunta al endpoint específico de autenticación (ej. /rest.usuario/authenticate)
        WebTarget authResource = webTarget.path("authenticate");

        // Crea el payload JSON con las credenciales
        // Una buena práctica sería usar un DTO (Data Transfer Object) como LoginRequest {username, password}
        // y serializarlo a JSON con Jackson, pero para simplicidad se usa String.format.
        String authPayload = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);

        try {
            // Realiza la petición POST con el payload JSON
            Response response = authResource.request(MediaType.APPLICATION_JSON)
                                            .post(Entity.entity(authPayload, MediaType.APPLICATION_JSON));

            int statusCode = response.getStatus();

            if (statusCode == Response.Status.OK.getStatusCode()) { // HTTP 200 OK
                // La autenticación fue exitosa, lee el Usuario del cuerpo de la respuesta
                return response.readEntity(Usuario.class);
            } else if (statusCode == Response.Status.UNAUTHORIZED.getStatusCode() ||
                       statusCode == Response.Status.NOT_FOUND.getStatusCode()) { // HTTP 401 Unauthorized o 404 Not Found
                // Credenciales inválidas o usuario no encontrado
                return null;
            } else {
                // Otro tipo de error (ej. 500 Internal Server Error de la API externa)
                String errorMsg = response.readEntity(String.class);
                throw new ClientErrorException("Error en la API de autenticación: HTTP " + statusCode + " - " + errorMsg, statusCode);
            }
        } finally {
            // Es importante cerrar la respuesta para liberar recursos de conexión
            // En JAX-RS, si no estás usando try-with-resources en el Response, debes cerrarlo manualmente.
            // Para este caso simplificado de Client.post, no es estrictamente necesario response.close() aquí
            // ya que el cliente gestiona la conexión, pero es buena práctica en un flujo más complejo.
        }
    }
    
    public Usuario register(Usuario requestEntity) throws ClientErrorException {
        // Asumiendo que el endpoint /rest.usuario/register espera un Usuario y devuelve el Usuario creado (201 Created)
        WebTarget registerResource = webTarget.path("register"); // Apunta al endpoint /register
        Response response = registerResource.request(MediaType.APPLICATION_JSON)
                                            .post(Entity.entity(requestEntity, MediaType.APPLICATION_JSON));
        
        if (response.getStatus() == Response.Status.CREATED.getStatusCode() || response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.readEntity(Usuario.class);
        } else {
            String errorMsg = response.readEntity(String.class);
            throw new ClientErrorException("Error al registrar usuario: " + response.getStatus() + " - " + errorMsg, response.getStatus());
        }
    }
    
    public boolean changePassword(String id, String newPlainPassword) throws ClientErrorException {
        WebTarget resource = webTarget.path(MessageFormat.format("{0}/password", new Object[]{id})); // Ejemplo de endpoint
        // Crea un payload simple para la nueva contraseña. Un DTO sería mejor.
        String passwordPayload = String.format("{\"newPassword\": \"%s\"}", newPlainPassword);
        Response response = resource.request(MediaType.APPLICATION_JSON)
                                    .put(Entity.entity(passwordPayload, MediaType.APPLICATION_JSON));
        
        if (response.getStatus() == Response.Status.OK.getStatusCode() || response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
            return true; // Éxito
        } else {
            String errorMsg = response.readEntity(String.class);
            throw new ClientErrorException("Error al cambiar la contraseña: " + response.getStatus() + " - " + errorMsg, response.getStatus());
        }
    }
    
    // Método para buscar usuarios
    public <T> T search_JSON(GenericType<T> responseType, String query) throws ClientErrorException {
        // Corresponde a GET /rest.usuario/search?q={query}
        WebTarget resource = webTarget;
        resource = resource.path("search").queryParam("q", query);
        return resource.request(MediaType.APPLICATION_JSON).get(responseType);
    }
    
    
     /**
     * Obtiene la lista de grupos de estudio a los que pertenece un usuario.
     * Corresponde a GET /rest.usuario/{userId}/grupos
     * @param responseType El tipo de la respuesta esperada (ej. new GenericType<List<GrupoEstudio>>() {}).
     * @param userId El ID del usuario.
     * @return Una lista de objetos GrupoEstudio.
     * @throws ClientErrorException Si la petición HTTP falla.
     */
    public <T> T getGruposDeUsuario(GenericType<T> responseType, String userId) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/grupos", new Object[]{userId}));
        return resource.request(MediaType.APPLICATION_JSON).get(responseType);
    }
    
    public <T> T updatePuntos(GenericType<T> responseType, String userId,Integer puntos) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("addPuntos{0}/{1}", new Object[]{puntos,userId}));
        return resource.request(MediaType.APPLICATION_JSON).get(responseType);
    }
    
    public <T> T getRanking_JSON(GenericType<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("ranking");
        return resource.request(MediaType.APPLICATION_JSON).get(responseType);
    }
    
    public void close() {
        client.close();
    }
    
}
