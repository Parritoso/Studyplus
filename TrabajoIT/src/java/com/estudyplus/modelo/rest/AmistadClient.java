/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.modelo.rest;

import com.estudyplus.modelo.dto.FriendRequestPayload;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 * Jersey REST client generated for REST resource:AmistadFacadeREST
 * [rest.amistad]<br>
 * USAGE:
 * <pre>
 *        AmistadClient client = new AmistadClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author Parri
 */
public class AmistadClient {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8080/API-REST/webresources";

    public AmistadClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("rest.amistad");
    }

    public String countREST() throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("count");
        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    public void edit_XML(Object requestEntity, String id) throws ClientErrorException {
        webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_XML).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML));
    }

    public void edit_JSON(Object requestEntity, String id) throws ClientErrorException {
        webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON));
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
    
    // Método para enviar solicitud de amistad
    public Response sendRequest_JSON_Response(FriendRequestPayload requestEntity) throws ClientErrorException {
        // Asumiendo que el endpoint es POST /rest.amistad/request
        return webTarget.path("request").request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                        .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON));
    }

    // Método para aceptar solicitud de amistad
    public Response acceptRequest_JSON_Response(String id) throws ClientErrorException {
        // Asumiendo que el endpoint es PUT /rest.amistad/{id}/accept
        return webTarget.path(java.text.MessageFormat.format("{0}/accept", new Object[]{id}))
                        .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                        .put(javax.ws.rs.client.Entity.json("")); // Cuerpo vacío o un DTO simple de confirmación
    }

    // Método para rechazar solicitud de amistad
    public Response rejectRequest_JSON_Response(String id) throws ClientErrorException {
        // Asumiendo que el endpoint es PUT /rest.amistad/{id}/reject
        return webTarget.path(java.text.MessageFormat.format("{0}/reject", new Object[]{id}))
                        .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                        .put(javax.ws.rs.client.Entity.json(""));
    }

    // Método para obtener solicitudes de amistad pendientes
    public <T> T getPendingRequests_JSON(GenericType<T> responseType, String userId) throws ClientErrorException {
        // Asumiendo que el endpoint es GET /rest.amistad/pending/{userId}
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("pending/{0}", new Object[]{userId}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    // Método para obtener la lista de amigos
    public <T> T getFriends_JSON(GenericType<T> responseType, String userId) throws ClientErrorException {
        // Asumiendo que el endpoint es GET /rest.amistad/friends/{userId}
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("friends/{0}", new Object[]{userId}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public void close() {
        client.close();
    }
    
}
