/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.modelo.rest.facade;

import com.estudyplus.modelo.dto.ConversacionDTO;
import com.estudyplus.modelo.dto.ConversationRequestPayload;
import com.estudyplus.modelo.dto.FriendRequestPayload;
import com.estudyplus.modelo.entitys.Amistad;
import com.estudyplus.modelo.entitys.Conversacion;
import com.estudyplus.modelo.entitys.Entrega;
import com.estudyplus.modelo.entitys.Examen;
import com.estudyplus.modelo.entitys.GrupoEstudio;
import com.estudyplus.modelo.entitys.LogProductividad;
import com.estudyplus.modelo.entitys.Mensaje;
import com.estudyplus.modelo.entitys.ParticipanteGrupo;
import com.estudyplus.modelo.entitys.SesionEstudio;
import com.estudyplus.modelo.entitys.TecnicaEstudio;
import com.estudyplus.modelo.entitys.Usuario;
import com.estudyplus.modelo.rest.AmistadClient;
import com.estudyplus.modelo.rest.ConversacionClient;
import com.estudyplus.modelo.rest.EntregaClient;
import com.estudyplus.modelo.rest.ExamenClient;
import com.estudyplus.modelo.rest.GrupoEstudioClient;
import com.estudyplus.modelo.rest.LogProductividadClient;
import com.estudyplus.modelo.rest.MensajeClient;
import com.estudyplus.modelo.rest.ParticipanteGrupoClient;
import com.estudyplus.modelo.rest.SesionEstudioClient;
import com.estudyplus.modelo.rest.TecnicaEstudioClient;
import com.estudyplus.modelo.rest.UsuarioClient;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fachada para interactuar con la API REST externa de Estudy+.
 * Centraliza las llamadas a los diferentes clientes REST.
 * @author Parri
 */
public class EstudyRestFacade {
    // Instancias de los clientes REST específicos
    private final UsuarioClient usuarioClient;
    private final SesionEstudioClient sesionEstudioClient;
    private final EntregaClient entregaClient;
    private final ExamenClient examenClient;
    private final TecnicaEstudioClient tecnicaEstudioClient;
    private final LogProductividadClient logProductividadClient;
    private final GrupoEstudioClient grupoEstudioClient;
    private final ParticipanteGrupoClient participanteGrupoClient;
    private final AmistadClient amistadClient;
    private final ConversacionClient conversacionClient;
    private final MensajeClient mensajeClient;
    private static final Logger logger = LoggerFactory.getLogger(EstudyRestFacade.class);
    
    public EstudyRestFacade() {
        this.usuarioClient = new UsuarioClient();
        this.sesionEstudioClient = new SesionEstudioClient();
        this.entregaClient = new EntregaClient();
        this.examenClient = new ExamenClient();
        this.tecnicaEstudioClient = new TecnicaEstudioClient();
        this.logProductividadClient = new LogProductividadClient();
        this.grupoEstudioClient = new GrupoEstudioClient();
        this.participanteGrupoClient = new ParticipanteGrupoClient();
        this.amistadClient = new AmistadClient();
        this.conversacionClient = new ConversacionClient();
        this.mensajeClient = new MensajeClient();
    }
    
    // --- Métodos para Usuario ---
    public List<Usuario> getAllUsuarios() throws IOException {
        try {
            // findAll_JSON devuelve un Object, que en este caso será un array de Usuario o una List.
            // Necesitamos saber cómo el servicio JAX-RS lo serializa.
            // Si el servicio devuelve una lista de Usuarios como JSON array, entonces la deserialización directa a List<Usuario>
            // con Jersey puede ser más compleja o requerir un GenericType.
            // Para simplificar, asumo que el servicio devuelve un array JSON de objetos Usuario.
            Usuario[] usuariosArray = usuarioClient.findAll_JSON(Usuario[].class);
            return Arrays.asList(usuariosArray);
        } catch (ClientErrorException e) {
            throw new IOException("Error al obtener todos los usuarios: " + e.getMessage(), e);
        }
    }

    public Usuario getUsuarioById(int id) throws IOException {
        try {
            return usuarioClient.find_JSON(Usuario.class, String.valueOf(id));
        } catch (ClientErrorException e) {
            // Podrías verificar e.getResponse().getStatus() para manejar 404 vs 500
            if (e.getResponse().getStatus() == 404) {
                return null; // O lanzar una excepción específica de "no encontrado"
            }
            throw new IOException("Error al obtener usuario por ID: " + e.getMessage(), e);
        }
    }

    public Usuario createUsuario(Usuario usuario) throws IOException {
        try {
            // create_JSON no devuelve el objeto creado, solo hace la petición POST.
            // Si el backend devuelve el objeto creado (común en APIs REST), necesitarías un método post que lo devuelva.
            // Asumiendo que el cliente no lo hace, esta operación es un "fire and forget" o se recupera después.
            // Si tu backend devuelve el objeto creado, deberías ajustar tu UsuarioClient para capturarlo.
            // Por ahora, simplemente llamamos al método que envía el objeto.
            usuarioClient.create_JSON(usuario);
            // Si necesitas el objeto creado con su ID, tu servicio REST en el backend debería devolverlo,
            // y tu UsuarioClient debería tener un método like 'postAndReceive' que lo capture.
            // Por ahora, no podemos devolverlo si el cliente Jersey no lo capta.
            return usuario; // Devolvemos el mismo objeto enviado (sin el ID generado por la DB)
        } catch (ClientErrorException e) {
            throw new IOException("Error al crear usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza un usuario existente.
     * @param usuario El objeto Usuario con los datos a actualizar.
     * @return El Usuario actualizado.
     * @throws IOException Si hay un error de comunicación.
     * @throws ClientErrorException Si la API externa devuelve un error (ej. 404 Not Found).
     */
    public Usuario updateUsuario(Usuario usuario) throws IOException {
        try {
            // edit_JSON no devuelve el objeto actualizado.
            usuarioClient.edit_JSON(usuario, String.valueOf(usuario.getId()));
            return usuario; // Devolvemos el mismo objeto que fue enviado
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                 throw new IOException("Usuario no encontrado para actualizar.", e);
            }
            throw new IOException("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }
    
    public Usuario updatePuntos(Usuario usuario, Integer puntos) throws IOException {
        try {
            // edit_JSON no devuelve el objeto actualizado.
            usuarioClient.updatePuntos(new GenericType<List<GrupoEstudio>>() {},String.valueOf(usuario.getId()),puntos);
            return usuario; // Devolvemos el mismo objeto que fue enviado
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                 throw new IOException("Usuario no encontrado para actualizar.", e);
            }
            throw new IOException("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }
    
    public List<Usuario> getRanking() throws IOException {
        try {
            List<Usuario> us = usuarioClient.getRanking_JSON(new GenericType<List<Usuario>>() {});
            logger.debug("Lista: "+Arrays.toString(us.toArray()));
            return us; 
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al obtener ranking: {}", e.getMessage(), e);
            throw new IOException("Error de comunicación al obtener ranking.", e);
        } catch (Exception e) {
            logger.error("Excepción al obtener ranking: {}", e.getMessage(), e);
            throw new IOException("Fallo inesperado al obtener renking.", e);
        }
    }

    public void deleteUsuario(int id) throws IOException {
        try {
            usuarioClient.remove(String.valueOf(id));
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new IOException("Usuario no encontrado para eliminar.", e);
            }
            throw new IOException("Error al eliminar usuario: " + e.getMessage(), e);
        }
    }
    
    /**
     * Autentica a un usuario delegando la validación de credenciales a la API REST externa
     * a través del {@link UsuarioClient}.
     *
     * @param username El nombre de usuario.
     * @param password La contraseña en texto plano.
     * @return El objeto {@link Usuario} autenticado si las credenciales son válidas,
     * o {@code null} si la autenticación falla.
     * @throws IOException Si ocurre un error de comunicación general o un error inesperado.
     */
    public Usuario authenticateUser(String username, String password) throws IOException {
        UsuarioClient usuarioClient = null; // Declara el cliente fuera del try para asegurar el finally
        try {
            logger.info("Intentando autenticar usuario: {}", username);
            usuarioClient = new UsuarioClient(); // Crea una nueva instancia del cliente
            
            // Llama al nuevo método authenticate del UsuarioClient
            Usuario authenticatedUser = usuarioClient.authenticate(username, password);

            if (authenticatedUser != null) {
                logger.info("Usuario '{}' autenticado exitosamente.", username);
                return authenticatedUser;
            } else {
                logger.warn("Fallo de autenticación para usuario: {}. Credenciales inválidas.", username);
                return null; // Credenciales inválidas o usuario no encontrado
            }
        } catch (ClientErrorException e) {
            // Captura errores específicos del cliente JAX-RS (ej. la API externa no responde, errores HTTP no manejados)
            logger.error("Error del cliente durante la autenticación para el usuario '{}': {}. Estado HTTP: {}",
                         username, e.getMessage(), e.getResponse().getStatus(), e);
            throw new IOException("Error de comunicación con el servicio de autenticación: " + e.getMessage(), e);
        } catch (Exception e) {
            // Captura cualquier otra excepción inesperada
            logger.error("Error inesperado durante la autenticación para el usuario '{}': {}", username, e.getMessage(), e);
            throw new IOException("Ocurrió un error inesperado durante la autenticación: " + e.getMessage(), e);
        } finally {
            // Asegúrate de cerrar el cliente para liberar los recursos de conexión HTTP
            if (usuarioClient != null) {
                usuarioClient.close();
            }
        }
    }
    
    /**
     * Registra un usuario llamando al endpoint /register de la API externa.
     * Asume que la API externa en UsuarioFacadeREST tiene un @POST @Path("register")
     * que toma un objeto Usuario con la contraseña en texto plano, la hashea y la guarda.
     * @param user Usuario a registrar (con la contraseña en texto plano).
     * @param plainPassword La contraseña en texto plano, si el endpoint la espera por separado o la entidad la lleva.
     * Dado que el register en UsuarioFacadeREST ya toma un Usuario y asume la contraseña,
     * lo más limpio es que el `user` ya venga con la `plainPassword` asignada al campo `contrasena`.
     * Sin embargo, para mantener el método de `UserService` claro, pasamos `plainPassword`
     * y lo asignamos aquí antes de enviar.
     * @return El Usuario registrado.
     * @throws IOException Si hay un error de comunicación.
     * @throws ClientErrorException Si la API externa devuelve un error (ej. 409 Conflict).
     */
    public Usuario registerUser(Usuario user, String plainPassword) throws IOException, ClientErrorException {
        UsuarioClient usuarioClient = null;
        try {
            usuarioClient = new UsuarioClient();
            // Asigna la contraseña en texto plano a la entidad antes de enviarla
            // Esto es crucial porque el endpoint /register en UsuarioFacadeREST la espera en el objeto Usuario.
            user.setContrasena(plainPassword);
            
            // La API externa /register devuelve 201 Created y el Usuario.
            // El `create_JSON` de UsuarioClient ya mapea a Object.
            // Asumimos que `UsuarioClient` podría tener un método `register` específico que devuelva `Usuario`.
            // Si no, tendremos que usar `create_JSON` y luego una llamada `find` si necesitamos el ID, o asumir que la API devuelve el objeto creado.
            // Para la simplicidad y la alineación con UsuarioFacadeREST.register, vamos a crear un método conceptual `register` en `UsuarioClient`.
            
            // CONCEPTO: UsuarioClient.register(Usuario user)
            // que llamaría a webTarget.path("register").request().post(Entity.json(user));
            // y leería la respuesta como Usuario.class
            
            // Por ahora, si UsuarioClient no tiene 'register', y 'create_JSON' es un void, es un problema.
            // Si create_JSON devuelve el objeto creado (como es común en APIs REST), lo usamos.
            // Suponiendo que `create_JSON` DEBERÍA devolver la entidad creada para este caso (como en muchos frameworks).
            // Si no, tendrías que llamar a `create_JSON` (que es void) y luego un `findByUsername_JSON` si tu API lo permite.
            
            // Para el ejemplo de UsuarioService, asumamos que `create_JSON` de UsuarioClient
            // se puede modificar para aceptar un Usuario y devolver un Usuario si la API lo devuelve.
            // O, mejor, un método `register` en UsuarioClient.
            
            // Si create_JSON es void, esto es un problema. Asumo que el endpoint /register de la API externa
            // devolverá el Usuario creado, y que deberíamos tener un método en UsuarioClient para ello.
            
            // **Revisión del `UsuarioClient`:** Necesitamos que el `create_JSON` (o un nuevo `register`)
            // devuelva la entidad creada si el endpoint de la API externa /register la devuelve.
            // La definición actual de `create_JSON` es `void`. Esto implicaría:
            // 1. La API externa en `/register` no devuelve el objeto creado (lo cual es raro para 201).
            // 2. O la generación de `UsuarioClient` no capturó la respuesta.
            
            // Alternativa: Si `create_JSON` es `void`, `registerUser` en `EstudyRestFacade`
            // tendría que hacer un `findByUsername` después de `create_JSON` si el username es único y no se devuelve el ID.
            
            // Para la consistencia con el `register` en `UsuarioFacadeREST` que devuelve `Response.status(CREATED).entity(nuevoUsuario).build()`,
            // nuestro `UsuarioClient` debería tener un método que pueda leer esa entidad.
            // Lo más directo sería modificar `UsuarioClient` con un método `register` que devuelva `Usuario`.
            
            // **Vamos a suponer que hay un método `usuarioClient.register(user)` que devuelve `Usuario`.**
            Usuario registeredUser = usuarioClient.register(user);
            
            return registeredUser;
        } catch (ClientErrorException e) {
            // Propagar el error para que UserService lo maneje (ej. 409 Conflict)
            throw e;
        } finally {
            if (usuarioClient != null) {
                usuarioClient.close();
            }
        }
    }
    
    /**
     * Actualiza solo la contraseña de un usuario.
     * Este método ASUME que hay un endpoint específico en la API REST externa para cambiar la contraseña
     * (ej. /rest.usuario/{id}/password o un DTO específico con la nueva contraseña).
     * O que el método PUT /edit de la API REST sepa hashear una nueva contraseña si se le envía.
     *
     * @param userId El ID del usuario.
     * @param newPlainPassword La nueva contraseña en texto plano.
     * @return true si la actualización fue exitosa.
     * @throws IOException Si hay un error de comunicación.
     * @throws ClientErrorException Si la API externa devuelve un error.
     */
    public boolean updateUserPassword(int userId, String newPlainPassword) throws IOException, ClientErrorException {
        UsuarioClient usuarioClient = null;
        try {
            usuarioClient = new UsuarioClient();
            // **Aquí necesitamos un nuevo método en UsuarioClient que se conecte a un endpoint de cambio de contraseña en la API externa.**
            // Por ejemplo: `usuarioClient.changePassword(userId, newPlainPassword);`
            // Y ese endpoint en la API externa (UsuarioFacadeREST) recibiría el ID y la nueva contraseña,
            // la hashearía y la actualizaría.

            // Dado que no hemos creado un método `changePassword` en `UsuarioClient` o `UsuarioFacadeREST` explícitamente,
            // podemos simularlo asumiendo que `UsuarioClient` tendrá un método PUT específico para esto.
            // La forma más directa es que la API externa tenga un endpoint tipo:
            // PUT /rest.usuario/{id}/password que reciba un String para la nueva contraseña.
            
            // Esto es una POC: Realmente necesitarías un endpoint dedicado en UsuarioFacadeREST para esto,
            // y un método específico en UsuarioClient para llamarlo.
            // Por ahora, la implementación más sencilla (menos segura si se expone la contraseña en el PUT principal)
            // sería actualizar la entidad completa con la nueva contraseña ya hasheada si la API externa no lo hace.
            // Pero como nuestra API ya tiene BCrypt, lo ideal es que el endpoint `/rest.usuario/{id}/password` la reciba en texto plano.
            
            // Para el propósito de esta implementación de EstudyRestFacade, asumimos que
            // UsuarioClient puede llamar a un endpoint de cambio de contraseña.
            // Si no se crea un endpoint específico, esta llamada es conceptual.

            // **Si NO hay un endpoint específico en UsuarioFacadeREST para cambiar la contraseña,**
            // y la API externa no hashea las contraseñas al recibir un PUT para Usuario,
            // entonces tendrías que hashear la contraseña aquí y enviarla. PERO ESTO NO ES LO SEGURO
            // ya que la API externa debería ser la única que sabe cómo hashear y almacenar contraseñas.
            
            // **Propuesta de implementación de `updateUserPassword` en `EstudyRestFacade`:**
            // Requiere un nuevo método en UsuarioClient:
            // `public boolean changePassword(String id, String newPlainPassword) throws ClientErrorException`
            // Este método llamaría a `PUT /rest.usuario/{id}/password` en la API externa.
            
            // Por ahora, dejemos esta implementación como un placeholder que requiere un método en UsuarioClient:
            boolean success = usuarioClient.changePassword(String.valueOf(userId), newPlainPassword);
            return success;
            
        } catch (ClientErrorException e) {
            throw e;
        } finally {
            if (usuarioClient != null) {
                usuarioClient.close();
            }
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // --- Métodos para SesionEstudio ---
    // (Ajusta SesionEstudioClient de manera similar a UsuarioClient)
    public List<SesionEstudio> getAllSesionesEstudio() throws IOException {
        try {
            SesionEstudio[] sesionesArray = sesionEstudioClient.findAll_JSON(SesionEstudio[].class);
            return Arrays.asList(sesionesArray);
        } catch (ClientErrorException e) {
            throw new IOException("Error al obtener todas las sesiones de estudio: " + e.getMessage(), e);
        }
    }

    public SesionEstudio getSesionEstudioById(int id) throws IOException {
        try {
            return sesionEstudioClient.find_JSON(SesionEstudio.class, String.valueOf(id));
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                return null;
            }
            throw new IOException("Error al obtener sesión de estudio por ID: " + e.getMessage(), e);
        }
    }

    public SesionEstudio createSesionEstudio(SesionEstudio sesionEstudio,  boolean startImmediately) throws IOException {
        try {
            if(startImmediately){
                return sesionEstudioClient.create_instant_JSON(SesionEstudio.class, sesionEstudio);
            }
            sesionEstudioClient.create_JSON(sesionEstudio);
            return sesionEstudio;
        } catch (ClientErrorException e) {
            throw new IOException("Error al crear sesión de estudio: " + e.getMessage(), e);
        }
    }

    public void deleteSesionEstudio(int id) throws IOException {
        try {
            sesionEstudioClient.remove(String.valueOf(id));
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new IOException("Sesión de estudio no encontrada para eliminar.", e);
            }
            throw new IOException("Error al eliminar sesión de estudio: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene una lista de SesionesEstudio para un usuario en un rango de fechas.
     * Asume que la API REST externa tiene un endpoint para esto.
     * @param userId El ID del usuario.
     * @param startDate La fecha de inicio del rango.
     * @param endDate La fecha de fin del rango.
     * @return Lista de SesionEstudio.
     * @throws IOException Si hay un error de comunicación.
     * @throws ClientErrorException Si la API externa devuelve un error.
     */
    public List<SesionEstudio> getSesionesEstudioByUserIdAndDateRange(int userId, Date startDate, Date endDate) throws IOException, ClientErrorException {
        try {
            // Convertir Date a Long de milisegundos para pasar por URL (más fácil que formatos de fecha en PATH)
            Long fromMillis = startDate.getTime();
            Long toMillis = endDate.getTime();
            return sesionEstudioClient.findByUserIdAndDateRange_JSON(userId, fromMillis, toMillis);
        } catch (ClientErrorException e){
            throw new IOException("Error al obtener todas las sesiones de estudio: " + e.getMessage(), e);
        }
    }

    public SesionEstudio getSesionEstudioById(Integer id) throws IOException {
        try {
            GenericType<SesionEstudio> responseType = new GenericType<SesionEstudio>() {};
            return sesionEstudioClient.find_JSON(SesionEstudio.class, id.toString());
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al obtener sesión de estudio por ID {}: {}", id, e.getMessage(), e);
            throw new IOException("Error del cliente REST al obtener sesión de estudio: " + e.getMessage(), e);
        }
    }

    public SesionEstudio updateSesionEstudio(SesionEstudio sesion) throws IOException {
        try {
            sesionEstudioClient.edit_JSON(sesion, sesion.getId().toString());
            return sesion; // La API REST suele devolver el objeto actualizado, si no, se devuelve el mismo.
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al actualizar sesión de estudio {}: {}", sesion.getId(), e.getMessage(), e);
            throw new IOException("Error del cliente REST al actualizar sesión de estudio: " + e.getMessage(), e);
        }
    }

    public List<SesionEstudio> getSesionesEstudioByUserId(Integer userId) throws IOException {
        try {
            GenericType<List<SesionEstudio>> responseType = new GenericType<List<SesionEstudio>>() {};
            return sesionEstudioClient.getByUserId_JSON(responseType, userId.toString());
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al obtener sesiones de estudio para usuario {}: {}", userId, e.getMessage(), e);
            throw new IOException("Error del cliente REST al obtener sesiones de estudio: " + e.getMessage(), e);
        }
    }

    public List<SesionEstudio> getUpcomingSesionesEstudioByUserId(Integer userId) throws IOException {
        try {
            GenericType<List<SesionEstudio>> responseType = new GenericType<List<SesionEstudio>>() {};
            return sesionEstudioClient.getUpcomingByUserId_JSON(responseType, userId.toString());
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al obtener próximas sesiones de estudio para usuario {}: {}", userId, e.getMessage(), e);
            throw new IOException("Error del cliente REST al obtener próximas sesiones de estudio: " + e.getMessage(), e);
        }
    }

    public List<SesionEstudio> getHistorySesionesEstudioByUserId(Integer userId) throws IOException {
        try {
            GenericType<List<SesionEstudio>> responseType = new GenericType<List<SesionEstudio>>() {};
            return sesionEstudioClient.getHistoryByUserId_JSON(responseType, userId.toString());
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al obtener historial de sesiones de estudio para usuario {}: {}", userId, e.getMessage(), e);
            throw new IOException("Error del cliente REST al obtener historial de sesiones de estudio: " + e.getMessage(), e);
        }
    }
    
    public List<SesionEstudio> getSessionsByGroupId(int groupId) throws IOException {
        try {
            // Asumiendo un endpoint GET /rest.sesionestudio/group/{groupId}
            return sesionEstudioClient.findSessionsByGroupId_JSON(new GenericType<List<SesionEstudio>>() {}, String.valueOf(groupId));
        } catch (ClientErrorException e) {
            throw new IOException("Error al obtener sesiones de estudio por grupo: " + e.getMessage(), e);
        }
    }
    
    public SesionEstudio pauseSession(SesionEstudio sessionId) throws IOException {
        try {
            // Asumiendo un endpoint GET /rest.sesionestudio/group/{groupId}
            return sesionEstudioClient.pauseSession_JSON(new GenericType<SesionEstudio>() {}, sessionId);
        } catch (ClientErrorException e) {
            throw new IOException("Error al pausar sesion de estudio: " + e.getMessage(), e);
        }
    }
    
    public SesionEstudio startSession(SesionEstudio sessionId) throws IOException {
        try {
            // Asumiendo un endpoint GET /rest.sesionestudio/group/{groupId}
            return sesionEstudioClient.startSession_JSON(new GenericType<SesionEstudio>() {}, sessionId);
        } catch (ClientErrorException e) {
            throw new IOException("Error al pausar sesion de estudio: " + e.getMessage(), e);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // --- Métodos para Entrega ---
    public List<Entrega> getAllEntregas() throws IOException{
        try {
            Entrega[] entregasArray = entregaClient.findAll_JSON(Entrega[].class);
            return Arrays.asList(entregasArray);
        } catch (ClientErrorException e) {
            throw new IOException("Error al obtener todas las sesiones de estudio: " + e.getMessage(), e);
        }
    }
    public Entrega getEntregaById(int entregaId) throws IOException {
        try {
            return entregaClient.find_JSON(Entrega.class, String.valueOf(entregaId));
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                return null;
            }
            throw new IOException("Error al obtener sesión de estudio por ID: " + e.getMessage(), e);
        }
    }
    public Entrega createEntrega(Entrega nuevaEntrega) throws IOException {
        try {
            return entregaClient.create_instant_JSON(Entrega.class,nuevaEntrega);
        } catch (ClientErrorException e) {
            throw new IOException("Error al crear sesión de estudio: " + e.getMessage(), e);
        }
    }
    public Entrega updateEntrega(Entrega entregaActualizar) throws IOException {
        try {
            entregaClient.edit_JSON(entregaActualizar, String.valueOf(entregaActualizar.getId()));
            return entregaActualizar;
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new IOException("Sesión de estudio no encontrada para actualizar.", e);
            }
            throw new IOException("Error al actualizar sesión de estudio: " + e.getMessage(), e);
        }
    }
    public void deleteEntrega(int entregaId) throws IOException {
        try {
            entregaClient.remove(String.valueOf(entregaId));
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new IOException("Sesión de estudio no encontrada para eliminar.", e);
            }
            throw new IOException("Error al eliminar sesión de estudio: " + e.getMessage(), e);
        }
    }
    
    public List<Entrega> getEntregasByUserId(int userId) throws IOException {
        try {
            // Asumiendo que EntregaClient.findByUserId_JSON existe y devuelve una lista.
            // Necesitarás implementar este método en EntregaClient y el endpoint en EntregaFacadeREST.
            return entregaClient.findByUserId_JSON(new GenericType<List<Entrega>>() {}, String.valueOf(userId));
        } catch (Exception e) {
            throw new IOException("Error al obtener entregas por usuario a través de la API REST: " + e.getMessage(), e);
        }
    }
    /**
     * Obtiene una lista de Entregas próximas para un usuario específico.
     * @param userId El ID del usuario.
     * @return Lista de Entregas próximas.
     * @throws IOException Si ocurre un error de comunicación con el servicio REST.
     */
    public List<Entrega> getUpcomingEntregasByUserId(Integer userId) throws IOException {
        try {
            GenericType<List<Entrega>> responseType = new GenericType<List<Entrega>>() {};
            // Asume que EntregaClient tiene un método getUpcomingByUserId_JSON
            return entregaClient.getUpcomingByUserId_JSON(responseType, userId.toString());
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al obtener próximas entregas para usuario {}: {}", userId, e.getMessage(), e);
            throw new IOException("Error del cliente REST al obtener próximas entregas: " + e.getMessage(), e);
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // --- Métodos para Examen ---
    public List<Examen> getAllExamenes() throws IOException{
        try {
            Examen[] examenesArray = examenClient.findAll_JSON(Examen[].class);
            return Arrays.asList(examenesArray);
        } catch (ClientErrorException e) {
            throw new IOException("Error al obtener todas las sesiones de estudio: " + e.getMessage(), e);
        }
    }
    public Examen getExamenById(int id) throws IOException{
        try {
            return examenClient.find_JSON(Examen.class, String.valueOf(id));
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                return null;
            }
            throw new IOException("Error al obtener sesión de estudio por ID: " + e.getMessage(), e);
        }
    }
    public Examen createExamen(Examen examen) throws IOException{
        try {
            return examenClient.create_instant_JSON(Examen.class,examen);
        } catch (ClientErrorException e) {
            throw new IOException("Error al crear sesión de estudio: " + e.getMessage(), e);
        }
    }
    public Examen updateExamen(Examen examen) throws IOException{
        try {
            examenClient.edit_JSON(examen, String.valueOf(examen.getId()));
            return examen;
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new IOException("Sesión de estudio no encontrada para actualizar.", e);
            }
            throw new IOException("Error al actualizar sesión de estudio: " + e.getMessage(), e);
        }
    }
    public void deleteExamen(int id) throws IOException{
        try {
            examenClient.remove(String.valueOf(id));
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new IOException("Sesión de estudio no encontrada para eliminar.", e);
            }
            throw new IOException("Error al eliminar sesión de estudio: " + e.getMessage(), e);
        }
    }
    public List<Examen> getExamenesByUserId(int userId) throws IOException {
        try {
            // Asumiendo que ExamenClient.findByUserId_JSON existe y devuelve una lista.
            // Necesitarás implementar este método en ExamenClient y el endpoint en ExamenFacadeREST.
            return examenClient.findByUserId_JSON(new GenericType<List<Examen>>() {}, String.valueOf(userId));
        } catch (Exception e) {
            throw new IOException("Error al obtener exámenes por usuario a través de la API REST: " + e.getMessage(), e);
        }
    }
    /**
     * Obtiene una lista de Exámenes próximos para un usuario específico.
     * @param userId El ID del usuario.
     * @return Lista de Exámenes próximos.
     * @throws IOException Si ocurre un error de comunicación con el servicio REST.
     */
    public List<Examen> getUpcomingExamenesByUserId(Integer userId) throws IOException {
        try {
            GenericType<List<Examen>> responseType = new GenericType<List<Examen>>() {};
            // Asume que ExamenClient tiene un método getUpcomingByUserId_JSON
            return examenClient.getUpcomingByUserId_JSON(responseType, userId.toString());
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al obtener próximos exámenes para usuario {}: {}", userId, e.getMessage(), e);
            throw new IOException("Error del cliente REST al obtener próximos exámenes: " + e.getMessage(), e);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // --- Métodos para TecnicaEstudio ---
    public List<TecnicaEstudio> getAllTecnicasEstudio() throws IOException{
        try {
            TecnicaEstudio[] examenesArray = tecnicaEstudioClient.findAll_JSON(TecnicaEstudio[].class);
            return Arrays.asList(examenesArray);
        } catch (ClientErrorException e) {
            throw new IOException("Error al obtener todas las sesiones de estudio: " + e.getMessage(), e);
        }
    }
    public TecnicaEstudio getTecnicaEstudioById(int id) throws IOException{
        try {
            return tecnicaEstudioClient.find_JSON(TecnicaEstudio.class, String.valueOf(id));
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                return null;
            }
            throw new IOException("Error al obtener sesión de estudio por ID: " + e.getMessage(), e);
        }
    }
    public TecnicaEstudio createTecnicaEstudio(TecnicaEstudio examen) throws IOException{
        try {
            tecnicaEstudioClient.create_JSON(examen);
            return examen;
        } catch (ClientErrorException e) {
            throw new IOException("Error al crear sesión de estudio: " + e.getMessage(), e);
        }
    }
    public TecnicaEstudio updateTecnicaEstudio(TecnicaEstudio examen) throws IOException{
        try {
            tecnicaEstudioClient.edit_JSON(examen, String.valueOf(examen.getId()));
            return examen;
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new IOException("Sesión de estudio no encontrada para actualizar.", e);
            }
            throw new IOException("Error al actualizar sesión de estudio: " + e.getMessage(), e);
        }
    }
    public void deleteTecnicaEstudio(int id) throws IOException{
        try {
            tecnicaEstudioClient.remove(String.valueOf(id));
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new IOException("Sesión de estudio no encontrada para eliminar.", e);
            }
            throw new IOException("Error al eliminar sesión de estudio: " + e.getMessage(), e);
        }
    }
    /**
     * Obtiene una lista de Técnicas de Estudio de tipo INDIVIDUAL.
     * @return Lista de Técnicas de Estudio individuales.
     * @throws IOException Si ocurre un error de comunicación con el servicio REST.
     */
    public List<TecnicaEstudio> getIndividualTecnicasEstudio() throws IOException {
        try {
            GenericType<List<TecnicaEstudio>> responseType = new GenericType<List<TecnicaEstudio>>() {};
            // Asume que TecnicaEstudioClient tiene un método findIndividual_JSON
            return tecnicaEstudioClient.findIndividual_JSON(responseType);
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al obtener técnicas de estudio individuales: {}", e.getMessage(), e);
            throw new IOException("Error del cliente REST al obtener técnicas de estudio individuales: " + e.getMessage(), e);
        }
    }
    
    public List<TecnicaEstudio> getGrupoTecnicaEstudio() throws IOException {
        try {
            GenericType<List<TecnicaEstudio>> responseType = new GenericType<List<TecnicaEstudio>>() {};
            // Asume que TecnicaEstudioClient tiene un método findIndividual_JSON
            return tecnicaEstudioClient.findGrupal_JSON(responseType);
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al obtener técnicas de estudio individuales: {}", e.getMessage(), e);
            throw new IOException("Error del cliente REST al obtener técnicas de estudio individuales: " + e.getMessage(), e);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // --- Métodos para LogProductividad ---
    public List<LogProductividad> getAllLogsProductividad() throws IOException{
        try {
            LogProductividad[] examenesArray = logProductividadClient.findAll_JSON(LogProductividad[].class);
            return Arrays.asList(examenesArray);
        } catch (ClientErrorException e) {
            throw new IOException("Error al obtener todas las sesiones de estudio: " + e.getMessage(), e);
        }
    }
    public LogProductividad getLogProductividadById(int id) throws IOException{
        try {
            return logProductividadClient.find_JSON(LogProductividad.class, String.valueOf(id));
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                return null;
            }
            throw new IOException("Error al obtener sesión de estudio por ID: " + e.getMessage(), e);
        }
    }
    public LogProductividad createLogProductividad(LogProductividad examen) throws IOException{
        try {
            logProductividadClient.create_JSON(examen);
            return examen;
        } catch (ClientErrorException e) {
            throw new IOException("Error al crear sesión de estudio: " + e.getMessage(), e);
        }
    }
    public LogProductividad updateLogProductividad(LogProductividad examen) throws IOException{
        try {
            logProductividadClient.edit_JSON(examen, String.valueOf(examen.getId()));
            return examen;
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new IOException("Sesión de estudio no encontrada para actualizar.", e);
            }
            throw new IOException("Error al actualizar sesión de estudio: " + e.getMessage(), e);
        }
    }
    public void deleteLogProductividad(int id) throws IOException{
        try {
            logProductividadClient.remove(String.valueOf(id));
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new IOException("Sesión de estudio no encontrada para eliminar.", e);
            }
            throw new IOException("Error al eliminar sesión de estudio: " + e.getMessage(), e);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // --- Métodos para GrupoEstudio ---
    public List<GrupoEstudio> getAllGruposEstudio() throws IOException{
        try {
            GrupoEstudio[] examenesArray = grupoEstudioClient.findAll_JSON(GrupoEstudio[].class);
            return Arrays.asList(examenesArray);
        } catch (ClientErrorException e) {
            throw new IOException("Error al obtener todas los grupos de estudio: " + e.getMessage(), e);
        }
    }
    public GrupoEstudio getGrupoEstudioById(int id) throws IOException{
        try {
            return grupoEstudioClient.find_JSON(GrupoEstudio.class, String.valueOf(id));
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                return null;
            }
            throw new IOException("Error al obtener grupo de estudio por ID: " + e.getMessage(), e);
        }
    }
    public GrupoEstudio createGrupoEstudio(GrupoEstudio examen) throws IOException{
        try {
            return grupoEstudioClient.create_instant_JSON(GrupoEstudio.class,examen);
        } catch (ClientErrorException e) {
            throw new IOException("Error al crear grupo de estudio: " + e.getMessage(), e);
        }
    }
    public GrupoEstudio updateGrupoEstudio(GrupoEstudio examen) throws IOException{
        try {
            grupoEstudioClient.edit_JSON(examen, String.valueOf(examen.getId()));
            return examen;
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new IOException("Grupo de estudio no encontrado para actualizar.", e);
            }
            throw new IOException("Error al actualizar grupo de estudio: " + e.getMessage(), e);
        }
    }
    public void deleteGrupoEstudio(int id) throws IOException{
        try {
            grupoEstudioClient.remove(String.valueOf(id));
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new IOException("Grupo de estudio no encontrado para eliminar.", e);
            }
            throw new IOException("Error al eliminar grupo de estudio: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene la lista de grupos de estudio a los que pertenece un usuario.
     * Este método llama al endpoint específico en UsuarioClient.
     * @param userId ID del usuario.
     * @return Una lista de objetos GrupoEstudio.
     * @throws IOException Si ocurre un error en la comunicación con la API.
     */
    public List<GrupoEstudio> getGruposDeUsuario(int userId) throws IOException {
        try {
            // Usar el método específico de UsuarioClient para obtener los grupos del usuario
            return usuarioClient.getGruposDeUsuario(new GenericType<List<GrupoEstudio>>() {}, String.valueOf(userId));
        } catch (Exception e) {
            throw new IOException("Error al obtener grupos del usuario: " + e.getMessage(), e);
        }
    }
    
        /**
        * Obtiene los participantes de un grupo.
        * @param groupId ID del grupo.
        * @return Lista de Usuarios que son participantes del grupo.
        * @throws IOException Si ocurre un error en la comunicación con la API.
        */
        public List<Usuario> getParticipantesByGroupId(int groupId) throws IOException {
            try {
                // Asumiendo un endpoint como GET /rest.grupoestudio/{id}/participantes
                return grupoEstudioClient.getParticipantes(new GenericType<List<Usuario>>() {}, String.valueOf(groupId));
            } catch (ClientErrorException e) {
                throw new IOException("Error al obtener participantes del grupo: " + e.getMessage(), e);
            }
        }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // --- Métodos para ParticipanteGrupo ---
    public List<ParticipanteGrupo> getAllParticipantesGrupo() throws IOException{
        try {
            ParticipanteGrupo[] examenesArray = participanteGrupoClient.findAll_JSON(ParticipanteGrupo[].class);
            return Arrays.asList(examenesArray);
        } catch (ClientErrorException e) {
            throw new IOException("Error al obtener todas las sesiones de estudio: " + e.getMessage(), e);
        }
    }
    public ParticipanteGrupo getParticipanteGrupoById(int id) throws IOException{
        try {
            return participanteGrupoClient.find_JSON(ParticipanteGrupo.class, String.valueOf(id));
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                return null;
            }
            throw new IOException("Error al obtener sesión de estudio por ID: " + e.getMessage(), e);
        }
    }
    public ParticipanteGrupo createParticipanteGrupo(ParticipanteGrupo examen) throws IOException{
        try {
            participanteGrupoClient.create_instant_JSON(ParticipanteGrupo.class,examen);
            return examen;
        } catch (ClientErrorException e) {
            throw new IOException("Error al crear sesión de estudio: " + e.getMessage(), e);
        }
    }
    public ParticipanteGrupo updateParticipanteGrupo(ParticipanteGrupo examen) throws IOException{
        try {
            participanteGrupoClient.edit_JSON(examen, String.valueOf(examen.getId()));
            return examen;
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new IOException("Sesión de estudio no encontrada para actualizar.", e);
            }
            throw new IOException("Error al actualizar sesión de estudio: " + e.getMessage(), e);
        }
    }
    public void deleteParticipanteGrupo(int id) throws IOException{
        try {
            participanteGrupoClient.remove(String.valueOf(id));
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new IOException("Sesión de estudio no encontrada para eliminar.", e);
            }
            throw new IOException("Error al eliminar sesión de estudio: " + e.getMessage(), e);
        }
    }
    
    /**
     * Añade un participante a un grupo de estudio.
     * Este método llama al endpoint específico en ParticipanteGrupoClient.
     * @param participante El objeto ParticipanteGrupo a crear.
     * @return El ParticipanteGrupo creado.
     * @throws IOException Si ocurre un error en la comunicación con la API.
     */
    public ParticipanteGrupo addParticipanteGrupo(ParticipanteGrupo participante) throws IOException {
        try {
            return participanteGrupoClient.create_JSON(participante, ParticipanteGrupo.class);
        } catch (ClientErrorException e) {
            throw new IOException("Error al añadir participante al grupo: " + e.getMessage(), e);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // --- Métodos para Amistades y Mensajes ---
    public List<Usuario> searchUsers(String query) throws IOException {
        try {
            // Asume que UsuarioClient tiene un método GET a /search?q={query} que devuelve List<Usuario>
            // Tendrás que añadir este método en tu UsuarioClient.
            return usuarioClient.search_JSON(new GenericType<List<Usuario>>() {}, query); // NECESITAS ESTE MÉTODO EN USUARIOCLIENT
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al buscar usuarios: {}", e.getMessage(), e);
            throw new IOException("Error de comunicación al buscar usuarios.", e);
        } catch (Exception e) {
            logger.error("Excepción al buscar usuarios: {}", e.getMessage(), e);
            throw new IOException("Fallo inesperado al buscar usuarios.", e);
        }
    }

    public Amistad sendFriendRequest(Integer remitenteId, Integer receptorId) throws IOException {
        try {
            FriendRequestPayload payload = new FriendRequestPayload(remitenteId, receptorId);
            // Asume que AmistadClient tiene un método POST a /request que devuelve Amistad o Response
            Response response = amistadClient.sendRequest_JSON_Response(payload); // NECESITAS ESTE MÉTODO EN AMISTADCLIENT

            int statusCode = response.getStatus();
            if (statusCode == Response.Status.CREATED.getStatusCode() || statusCode == Response.Status.OK.getStatusCode()) {
                logger.info("Solicitud de amistad enviada con éxito de {} a {}.", remitenteId, receptorId);
                return response.readEntity(Amistad.class);
            } else {
                String errorMsg = response.readEntity(String.class);
                logger.error("Error al enviar solicitud de amistad: HTTP {} - {}", statusCode, errorMsg);
                throw new IOException("Error al enviar solicitud de amistad: " + errorMsg);
            }
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al enviar solicitud de amistad: {}", e.getMessage(), e);
            throw new IOException("Fallo de comunicación al enviar solicitud de amistad.", e);
        } catch (Exception e) {
            logger.error("Excepción al enviar solicitud de amistad: {}", e.getMessage(), e);
            throw new IOException("Fallo inesperado al enviar solicitud de amistad.", e);
        }
    }
    
    public Amistad acceptFriendRequest(Integer amistadId) throws IOException {
        try {
            // Asume que AmistadClient tiene un método PUT a /{id}/accept que devuelve Amistad o Response
            Response response = amistadClient.acceptRequest_JSON_Response(amistadId.toString()); // NECESITAS ESTE MÉTODO EN AMISTADCLIENT

            int statusCode = response.getStatus();
            if (statusCode == Response.Status.OK.getStatusCode()) {
                logger.info("Solicitud de amistad ID {} aceptada con éxito.", amistadId);
                return response.readEntity(Amistad.class);
            } else {
                String errorMsg = response.readEntity(String.class);
                logger.error("Error al aceptar solicitud de amistad ID {}: HTTP {} - {}", amistadId, statusCode, errorMsg);
                throw new IOException("Error al aceptar solicitud de amistad: " + errorMsg);
            }
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al aceptar solicitud de amistad ID {}: {}", amistadId, e.getMessage(), e);
            throw new IOException("Fallo de comunicación al aceptar solicitud de amistad.", e);
        } catch (Exception e) {
            logger.error("Excepción al aceptar solicitud de amistad ID {}: {}", amistadId, e.getMessage(), e);
            throw new IOException("Fallo inesperado al aceptar solicitud de amistad.", e);
        }
    }

    public Amistad rejectFriendRequest(Integer amistadId) throws IOException {
        try {
            // Asume que AmistadClient tiene un método PUT a /{id}/reject que devuelve Amistad o Response
            Response response = amistadClient.rejectRequest_JSON_Response(amistadId.toString()); // NECESITAS ESTE MÉTODO EN AMISTADCLIENT

            int statusCode = response.getStatus();
            if (statusCode == Response.Status.OK.getStatusCode()) {
                logger.info("Solicitud de amistad ID {} rechazada con éxito.", amistadId);
                return response.readEntity(Amistad.class);
            } else {
                String errorMsg = response.readEntity(String.class);
                logger.error("Error al rechazar solicitud de amistad ID {}: HTTP {} - {}", amistadId, statusCode, errorMsg);
                throw new IOException("Error al rechazar solicitud de amistad: " + errorMsg);
            }
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al rechazar solicitud de amistad ID {}: {}", amistadId, e.getMessage(), e);
            throw new IOException("Fallo de comunicación al rechazar solicitud de amistad.", e);
        } catch (Exception e) {
            logger.error("Excepción al rechazar solicitud de amistad ID {}: {}", amistadId, e.getMessage(), e);
            throw new IOException("Fallo inesperado al rechazar solicitud de amistad.", e);
        }
    }

    public List<Amistad> getPendingFriendRequests(Integer userId) throws IOException {
        try {
            // Asume que AmistadClient tiene un método GET a /pending/{userId} que devuelve List<Amistad>
            return amistadClient.getPendingRequests_JSON(new GenericType<List<Amistad>>() {}, userId.toString()); // NECESITAS ESTE MÉTODO EN AMISTADCLIENT
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al obtener solicitudes pendientes: {}", e.getMessage(), e);
            throw new IOException("Error de comunicación al obtener solicitudes pendientes.", e);
        } catch (Exception e) {
            logger.error("Excepción al obtener solicitudes pendientes: {}", e.getMessage(), e);
            throw new IOException("Fallo inesperado al obtener solicitudes pendientes.", e);
        }
    }

    public List<Usuario> getFriends(Integer userId) throws IOException {
        try {
            // Asume que AmistadClient tiene un método GET a /friends/{userId} que devuelve List<Usuario>
            return amistadClient.getFriends_JSON(new GenericType<List<Usuario>>() {}, userId.toString()); // NECESITAS ESTE MÉTODO EN AMISTADCLIENT
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al obtener amigos: {}", e.getMessage(), e);
            throw new IOException("Error de comunicación al obtener amigos.", e);
        } catch (Exception e) {
            logger.error("Excepción al obtener amigos: {}", e.getMessage(), e);
            throw new IOException("Fallo inesperado al obtener amigos.", e);
        }
    }

    // --- MÉTODOS PARA CONVERSACIONES ---

    public Conversacion getOrCreateConversation(Integer user1Id, Integer user2Id) throws IOException {
        try {
            ConversationRequestPayload payload = new ConversationRequestPayload(user1Id, user2Id);
            // Asume que ConversacionClient tiene un método POST a /findOrCreate que devuelve Conversacion o Response
            Response response = conversacionClient.findOrCreate_JSON_Response(payload); // NECESITAS ESTE MÉTODO EN CONVERSACIONCLIENT

            int statusCode = response.getStatus();
            if (statusCode == Response.Status.OK.getStatusCode() || statusCode == Response.Status.CREATED.getStatusCode()) {
                logger.info("Conversación obtenida/creada con éxito entre {} y {}.", user1Id, user2Id);
                return response.readEntity(Conversacion.class);
            } else {
                String errorMsg = response.readEntity(String.class);
                logger.error("Error al obtener/crear conversación: HTTP {} - {}", statusCode, errorMsg);
                throw new IOException("Error al obtener/crear conversación: " + errorMsg);
            }
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al obtener/crear conversación: {}", e.getMessage(), e);
            throw new IOException("Fallo de comunicación al obtener/crear conversación.", e);
        } catch (Exception e) {
            logger.error("Excepción al obtener/crear conversación: {}", e.getMessage(), e);
            throw new IOException("Fallo inesperado al obtener/crear conversación.", e);
        }
    }

    public List<ConversacionDTO> getConversationsForUser(Integer userId) throws IOException {
        try {
            // Asume que ConversacionClient tiene un método GET a /user/{userId} que devuelve List<Conversacion>
            return conversacionClient.getConversationsForUser_JSON(new GenericType<List<ConversacionDTO>>() {}, userId.toString()); // NECESITAS ESTE MÉTODO EN CONVERSACIONCLIENT
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al obtener conversaciones para el usuario: {}", e.getMessage(), e);
            throw new IOException("Error de comunicación al obtener conversaciones.", e);
        } catch (Exception e) {
            logger.error("Excepción al obtener conversaciones para el usuario: {}", e.getMessage(), e);
            throw new IOException("Fallo inesperado al obtener conversaciones.", e);
        }
    }
    
    public Conversacion getConversacionById(Integer conversationId) throws IOException {
        try {
            // Asume que ConversacionClient tiene un método GET a /user/{userId} que devuelve List<Conversacion>
            return conversacionClient.find_JSON(new GenericType<Conversacion>() {}, conversationId.toString()); // NECESITAS ESTE MÉTODO EN CONVERSACIONCLIENT
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al obtener conversaciones con id: {}", e.getMessage(), e);
            throw new IOException("Error de comunicación al obtener conversaciones.", e);
        } catch (Exception e) {
            logger.error("Excepción al obtener conversaciones con id: {}", e.getMessage(), e);
            throw new IOException("Fallo inesperado al obtener conversaciones.", e);
        }
    }
    
    public Conversacion getconversationByGroupId(Integer groupId) throws IOException {
        try {
            // Asume que ConversacionClient tiene un método GET a /user/{userId} que devuelve List<Conversacion>
            return conversacionClient.find_group_JSON(new GenericType<Conversacion>() {}, groupId.toString()); // NECESITAS ESTE MÉTODO EN CONVERSACIONCLIENT
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al obtener conversaciones con id: {}", e.getMessage(), e);
            throw new IOException("Error de comunicación al obtener conversaciones.", e);
        } catch (Exception e) {
            logger.error("Excepción al obtener conversaciones con id: {}", e.getMessage(), e);
            throw new IOException("Fallo inesperado al obtener conversaciones.", e);
        }
    }
    
    // --- MÉTODOS PARA MENSAJES ---
    /**
     * Obtiene o crea la conversación asociada a un grupo.
     * @param groupId ID del grupo.
     * @return La conversación del grupo.
     * @throws IOException Si ocurre un error.
     */
    public Conversacion getOrCreateGroupConversation(int groupId) throws IOException {
        try {
            // Asumiendo un endpoint GET /rest.conversacion/group/{groupId} que crea si no existe
            return conversacionClient.getOrCreateGroupConversation(Conversacion.class, String.valueOf(groupId));
        } catch (ClientErrorException e) {
            throw new IOException("Error al obtener o crear conversación de grupo: " + e.getMessage(), e);
        }
    }

    public List<Mensaje> getMessagesInConversation(Integer conversationId, int offset, int limit) throws IOException {
        try {
            // Asume que MensajeClient tiene un método GET a /conversacion/{convId}?offset={offset}&limit={limit}
            return mensajeClient.getMessagesInConversation_JSON(new GenericType<List<Mensaje>>() {}, conversationId.toString(), Integer.toString(offset), Integer.toString(limit)); // NECESITAS ESTE MÉTODO EN MENSAJECLIENT
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al obtener mensajes de la conversación: {}", e.getMessage(), e);
            throw new IOException("Error de comunicación al obtener mensajes.", e);
        } catch (Exception e) {
            logger.error("Excepción al obtener mensajes de la conversación: {}", e.getMessage(), e);
            throw new IOException("Fallo inesperado al obtener mensajes.", e);
        }
    }

    public Mensaje sendMessage(Mensaje mensaje) throws IOException {
        try {
            // Asume que MensajeClient tiene un método POST a /rest.mensaje que devuelve Mensaje o Response
            Response response = mensajeClient.sendMessage_JSON_Response(mensaje); // NECESITAS ESTE MÉTODO EN MENSAJECLIENT

            int statusCode = response.getStatus();
            if (statusCode == Response.Status.CREATED.getStatusCode() || statusCode == Response.Status.OK.getStatusCode() || statusCode == Response.Status.NO_CONTENT.getStatusCode()) {
                logger.info("Mensaje enviado con éxito en conversación ID {} desde emisor ID {}.", mensaje.getConversacionId(), mensaje.getEmisorId());
                return response.readEntity(Mensaje.class);
            } else {
                String errorMsg = response.readEntity(String.class);
                logger.error("Error al enviar mensaje: HTTP {} - {}", statusCode, errorMsg);
                throw new IOException("Error al enviar mensaje: " + errorMsg);
            }
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al enviar mensaje: {}", e.getMessage(), e);
            throw new IOException("Fallo de comunicación al enviar mensaje.", e);
        } catch (Exception e) {
            logger.error("Excepción al enviar mensaje: {}", e.getMessage(), e);
            throw new IOException("Fallo inesperado al enviar mensaje.", e);
        }
    }
    
    public void markMessagesAsRead(Integer conversationId, Integer userId) throws IOException {
        try {
            // Asume que MensajeClient tiene un método PUT a /read/{convId}/{userId}
            mensajeClient.markMessagesAsRead_JSON(conversationId.toString(), userId.toString()); // NECESITAS ESTE MÉTODO EN MENSAJECLIENT
            logger.info("Mensajes marcados como leídos en conversación ID {} para usuario ID {}.", conversationId, userId);
        } catch (ClientErrorException e) {
            logger.error("ClientErrorException al marcar mensajes como leídos: {}", e.getMessage(), e);
            throw new IOException("Fallo de comunicación al marcar mensajes como leídos.", e);
        } catch (Exception e) {
            logger.error("Excepción al marcar mensajes como leídos: {}", e.getMessage(), e);
            throw new IOException("Fallo inesperado al marcar mensajes como leídos.", e);
        }
    }
    
    
    // Cierre del cliente: es importante cerrar los clientes de Jersey cuando ya no se necesiten
    // Esto es un punto delicado en Fachadas que son de larga vida. Podrías pasarlos por inyección
    // o gestionar el ciclo de vida del cliente fuera de la Fachada si hay muchos.
    public void closeClients() {
        if (usuarioClient != null) {
            usuarioClient.close();
        }
        if (sesionEstudioClient != null) {
            sesionEstudioClient.close();
        }
        if (entregaClient != null) {
            entregaClient.close();
        }
        if( examenClient != null){
            examenClient.close();
        }
        if( grupoEstudioClient != null){
            grupoEstudioClient.close();
        }
        if( logProductividadClient != null){
            logProductividadClient.close();
        }
        if( tecnicaEstudioClient != null){
            tecnicaEstudioClient.close();
        }
        if( participanteGrupoClient != null){
            participanteGrupoClient.close();
        }
    }
    
}
