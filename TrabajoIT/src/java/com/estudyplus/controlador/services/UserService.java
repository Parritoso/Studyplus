/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.services;

import com.estudyplus.controlador.services.exception.AuthenticationFailedException;
import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.controlador.services.exception.UserAlreadyExistsException;
import com.estudyplus.controlador.services.exception.UserNotFoundException;
import com.estudyplus.modelo.entitys.GrupoEstudio;
import com.estudyplus.modelo.entitys.Usuario;
import com.estudyplus.modelo.rest.facade.EstudyRestFacade;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import javax.ws.rs.ClientErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Propósito: Gestionar todas las operaciones relacionadas con los usuarios, incluyendo registro, autenticación y gestión de perfil.
 * @see com.estudyplus.modelo.rest.facade.EstudyRestFacade
 * @author Parri
 */
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final EstudyRestFacade facade;
    // Patrón de regex para validación de email (básico)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    /**
     * Constructor para UserService.
     * Inyecta la fachada de REST para la comunicación con la API externa.
     * @param facade La instancia de EstudyRestFacade.
     */
    public UserService(EstudyRestFacade facade) {
        if (facade == null) {
            throw new IllegalArgumentException("EstudyRestFacade no puede ser nulo.");
        }
        this.facade = facade;
    }
    
    /**
     * Valida la unicidad del nombre de usuario y email.
     * Delega el registro (que incluirá el hasheo de la contraseña) a la API externa vía EstudyRestFacade.
     * Retorna el Usuario creado o lanza una excepción si falla
     * @param user El objeto Usuario (con nombre, email, pero la contraseña se tomará de plainPassword).
     * @param plainPassword La contraseña en texto plano para el nuevo usuario.
     * @return El Usuario registrado (con la contraseña hasheada en el backend externo, y no devuelta aquí).
     * @throws UserAlreadyExistsException Si el nombre de usuario o el email ya están en uso.
     * @throws IllegalArgumentException Si los datos del usuario o la contraseña son inválidos.
     * @throws ServiceException Si ocurre un error inesperado al comunicarse con la API.
     */
    public Usuario registerUser(Usuario user, String plainPassword){
        if (user == null || user.getNombre() == null || user.getNombre().trim().isEmpty() ||
            user.getEmail() == null || user.getEmail().trim().isEmpty() ||
            plainPassword == null || plainPassword.trim().isEmpty()) {
            logger.warn("Intento de registro con datos incompletos.");
            throw new IllegalArgumentException("Nombre de usuario, email y contraseña no pueden estar vacíos.");
        }
        if (user.getNombre().length() < 3 || user.getNombre().length() > 50) {
            throw new IllegalArgumentException("El nombre de usuario debe tener entre 3 y 50 caracteres.");
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("El formato del email no es válido.");
        }
        if (plainPassword.length() < 6) { // Ejemplo de política de contraseña
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
         try {
            // La lógica de la API REST externa (en UsuarioFacadeREST) ya maneja la verificación
            // de unicidad y el hasheo de la contraseña al registrar.
            // Aquí, pasamos el plainPassword para que el facade lo envíe al endpoint /register
            // que espera el Usuario con el plainPassword.
            // Nota: En un diseño más estricto, el DTO LoginRequest se usaría también aquí.
            // Pero siguiendo el patrón de register en UsuarioFacadeREST, el Usuario tiene la contraseña
            // en texto plano para ser hasheada en la API externa.
            Usuario registeredUser = facade.registerUser(user, plainPassword);

            if (registeredUser == null) {
                // Si la fachada devuelve null o un error específico (que aún no está implementado
                // para registerUser en EstudyRestFacade, pero debería propagar errores como 409)
                // Se asume que EstudyRestFacade.registerUser lanzaría una excepción o indicaría conflicto.
                // Por ahora, asumimos que si no es nulo, fue exitoso o se lanzó excepción.
                // En un escenario real, EstudyRestFacade.registerUser debería lanzar UserAlreadyExistsException
                // si recibe un 409 de la API externa.
                // Para este ejemplo, si la fachada no lanza la excepción por 409, tendríamos que buscar el usuario primero.
                throw new ServiceException("Error desconocido durante el registro de usuario.");
            }
            logger.info("Usuario '{}' registrado con éxito.", user.getNombre());
            return registeredUser;
        } catch (ClientErrorException e) {
            // Manejar errores específicos de la API externa como conflicto (409)
            if (e.getResponse().getStatus() == 409) {
                String responseMessage = "";
                try {
                    responseMessage = e.getResponse().readEntity(String.class);
                } catch (Exception ex) {
                    logger.error("Error al leer el mensaje de respuesta de conflicto: {}", ex.getMessage());
                }
                logger.warn("Intento de registro fallido debido a conflicto: {}", responseMessage);
                if (responseMessage.contains("nombre de usuario ya está en uso")) {
                    throw new UserAlreadyExistsException("El nombre de usuario ya está en uso.");
                } else if (responseMessage.contains("email ya está registrado")) {
                    throw new UserAlreadyExistsException("El email ya está registrado.");
                } else {
                    throw new UserAlreadyExistsException("Conflicto al registrar usuario: " + responseMessage);
                }
            }
            logger.error("Error del cliente REST durante el registro de usuario '{}': {}", user.getNombre(), e.getMessage(), e);
            throw new ServiceException("Error de comunicación con el servicio de registro.", e);
        } catch (Exception e) {
            logger.error("Error inesperado durante el registro de usuario '{}': {}", user.getNombre(), e.getMessage(), e);
            throw new ServiceException("Error inesperado al registrar usuario.", e);
        }
    }
    
    /**
     * Delega la autenticación a la API externa vía EstudyRestFacade.
     * Retorna el Usuario autenticado o null (o lanza una excepción AuthenticationFailedException).
     * @param username El nombre de usuario.
     * @param plainPassword La contraseña en texto plano.
     * @return El objeto Usuario autenticado.
     * @throws AuthenticationFailedException Si las credenciales son inválidas.
     * @throws IllegalArgumentException Si el nombre de usuario o la contraseña son nulos/vacíos.
     * @throws ServiceException Si ocurre un error inesperado al comunicarse con la API.
     */
    public Usuario authenticateUser(String username, String plainPassword){
        if (username == null || username.trim().isEmpty() || plainPassword == null || plainPassword.isEmpty()) {
            logger.warn("Intento de autenticación con credenciales nulas o vacías.");
            throw new IllegalArgumentException("Nombre de usuario y contraseña son obligatorios.");
        }

        try {
            //SOLO TESTING! ELIMINAR
            if(username.equals("test") && plainPassword.equals("test")){
                return new Usuario(0,"test","test@test","test");
            }
            
            Usuario authenticatedUser = facade.authenticateUser(username, plainPassword);
            if (authenticatedUser == null) {
                logger.warn("Autenticación fallida para el usuario: {}", username);
                throw new AuthenticationFailedException("Usuario o contraseña incorrectos.");
            }
            logger.info("Usuario '{}' autenticado con éxito.", username);
            return authenticatedUser;
        } catch (ClientErrorException e) {
            // Si la fachada ya maneja 401/404 devolviendo null, este bloque capturaría otros ClientErrorExceptions.
            // Si la fachada propaga 401/404 como ClientErrorException, podríamos capturarlos aquí.
            if (e.getResponse().getStatus() == 401 || e.getResponse().getStatus() == 404) {
                 logger.warn("Autenticación fallida por respuesta HTTP 401/404 para usuario '{}'.", username);
                 throw new AuthenticationFailedException("Usuario o contraseña incorrectos.");
            }
            logger.error("Error del cliente REST durante la autenticación para usuario '{}': {}", username, e.getMessage(), e);
            throw new ServiceException("Error de comunicación con el servicio de autenticación.", e);
        } catch (IOException e) {
            logger.error("Error de E/S durante la autenticación para usuario '{}': {}", username, e.getMessage(), e);
            throw new ServiceException("Error de comunicación con el servicio de autenticación.", e);
        } catch (Exception e) {
            logger.error("Error inesperado durante la autenticación para usuario '{}': {}", username, e.getMessage(), e);
            throw new ServiceException("Error inesperado al autenticar usuario.", e);
        }
    }
    
    /**
     * Valida los datos de perfil.
     * Delega la actualización a EstudyRestFacade.
     * @param user El objeto Usuario con la información actualizada. El ID del usuario debe estar presente.
     * @return El Usuario actualizado.
     * @throws UserNotFoundException Si el usuario a actualizar no existe.
     * @throws IllegalArgumentException Si los datos del usuario son inválidos.
     * @throws ServiceException Si ocurre un error inesperado al comunicarse con la API.
     */
    public Usuario updateUserProfile(Usuario user){
        if (user == null || user.getId() == null || user.getId() <= 0) {
            logger.warn("Intento de actualización de perfil con usuario nulo o ID inválido.");
            throw new IllegalArgumentException("El usuario y su ID deben ser válidos para actualizar el perfil.");
        }
        if (user.getNombre() == null || user.getNombre().trim().isEmpty() ||
            user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre y email no pueden estar vacíos.");
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("El formato del email no es válido.");
        }
        // Nota: Este método NO cambia la contraseña. Para eso, usar un método específico.
        // Asegurarse de que la contraseña no se envía accidentalmente o se ignora en la API externa.
        user.setContrasena(null); // Asegurar que no se envía la contraseña en texto plano aquí.

        try {
            // Verificar si el usuario existe antes de intentar actualizar
            Usuario existingUser = facade.getUsuarioById(user.getId());
            if (existingUser == null) {
                logger.warn("Intento de actualización de perfil para usuario ID {} fallido: usuario no encontrado.", user.getId());
                throw new UserNotFoundException("El usuario con ID " + user.getId() + " no fue encontrado.");
            }

            // Podría haber lógica adicional aquí para evitar que el nombre de usuario o email se dupliquen con otros usuarios
            // si se cambiaron y no son el mismo que el del usuario actual. Esto sería una llamada adicional a la API.

            // La fachada maneja la llamada PUT a la API externa
            Usuario updatedUser = facade.updateUsuario(user);
            if (updatedUser == null) {
                 logger.error("Fallo la actualización de usuario ID {} en la fachada (retornó null).", user.getId());
                 throw new ServiceException("No se pudo actualizar el perfil del usuario.");
            }
            logger.info("Perfil del usuario ID {} actualizado con éxito.", user.getId());
            return updatedUser;
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                 throw new UserNotFoundException("El usuario con ID " + user.getId() + " no fue encontrado para actualizar.");
            }
            logger.error("Error del cliente REST durante la actualización de perfil para usuario ID {}: {}", user.getId(), e.getMessage(), e);
            throw new ServiceException("Error de comunicación con el servicio de actualización de perfil.", e);
        } catch (IOException e) {
            logger.error("Error de E/S durante la actualización de perfil para usuario ID {}: {}", user.getId(), e.getMessage(), e);
            throw new ServiceException("Error de comunicación con el servicio de actualización de perfil.", e);
        } catch (Exception e) {
            logger.error("Error inesperado durante la actualización de perfil para usuario ID {}: {}", user.getId(), e.getMessage(), e);
            throw new ServiceException("Error inesperado al actualizar el perfil del usuario.", e);
        }
    }
    
    /**
     * Delega la búsqueda a EstudyRestFacade
     * @param userId El ID del usuario.
     * @return El objeto Usuario encontrado.
     * @throws UserNotFoundException Si el usuario con el ID especificado no existe.
     * @throws IllegalArgumentException Si el ID del usuario es inválido.
     * @throws ServiceException Si ocurre un error inesperado al comunicarse con la API.
     */
    public Usuario getUserById(int userId){
        if (userId <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo.");
        }

        try {
            Usuario user = facade.getUsuarioById(userId);
            if (user == null) {
                logger.warn("Usuario con ID {} no encontrado.", userId);
                throw new UserNotFoundException("Usuario con ID " + userId + " no encontrado.");
            }
            logger.info("Usuario con ID {} recuperado con éxito.", userId);
            return user;
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new UserNotFoundException("Usuario con ID " + userId + " no encontrado.");
            }
            logger.error("Error del cliente REST al obtener usuario por ID {}: {}", userId, e.getMessage(), e);
            throw new ServiceException("Error de comunicación al obtener usuario.", e);
        } catch (IOException e) {
            logger.error("Error de E/S al obtener usuario por ID {}: {}", userId, e.getMessage(), e);
            throw new ServiceException("Error de comunicación al obtener usuario.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al obtener usuario por ID {}: {}", userId, e.getMessage(), e);
            throw new ServiceException("Error inesperado al obtener usuario.", e);
        }
    }
    
    /**
     * Cambia la contraseña de un usuario.
     * Requiere la verificación de la contraseña actual.
     *
     * @param userId El ID del usuario.
     * @param currentPlainPassword La contraseña actual en texto plano.
     * @param newPlainPassword La nueva contraseña en texto plano.
     * @return true si la contraseña se cambió con éxito, false si la contraseña actual es incorrecta.
     * @throws UserNotFoundException Si el usuario no existe.
     * @throws IllegalArgumentException Si las contraseñas son inválidas o el ID es incorrecto.
     * @throws ServiceException Si ocurre un error inesperado al comunicarse con la API.
     */
    public boolean changePassword(int userId, String currentPlainPassword, String newPlainPassword) {
        if (userId <= 0 || currentPlainPassword == null || currentPlainPassword.isEmpty() ||
            newPlainPassword == null || newPlainPassword.isEmpty()) {
            throw new IllegalArgumentException("Datos de entrada inválidos para cambiar contraseña.");
        }
        if (newPlainPassword.length() < 6) { // Ejemplo de política de contraseña
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres.");
        }
        if (currentPlainPassword.equals(newPlainPassword)) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la actual.");
        }

        try {
            // 1. Autenticar al usuario con sus credenciales actuales.
            // Esto asegura que el usuario que intenta cambiar la contraseña es quien dice ser.
            Usuario authenticatedUser = facade.authenticateUser(getUserById(userId).getNombre(), currentPlainPassword); // Necesita el nombre de usuario
            if (authenticatedUser == null || authenticatedUser.getId() != userId) {
                throw new AuthenticationFailedException("Contraseña actual incorrecta.");
            }

            // 2. Obtener el usuario de la DB (el authenticateUser solo devuelve un subconjunto de datos)
            Usuario userToUpdate = getUserById(userId); // Reutilizamos getUserById para obtener todos los datos del usuario.

            // 3. Hashear la nueva contraseña y actualizar el usuario
            // La API REST externa debe manejar el hasheo de la nueva contraseña.
            // Para esto, el endpoint PUT en UsuarioFacadeREST necesitaría ser modificado
            // para aceptar la contraseña en texto plano o un objeto que contenga la nueva contraseña.
            // Actualmente, el edit en UsuarioFacadeREST asume que la entidad Usuario ya tiene la contraseña hasheada.
            // Si tu API REST solo tiene un PUT para la entidad completa, deberíamos hacer lo siguiente:
            // - Enviar la nueva contraseña en texto plano, y la API se encarga de hashearla y actualizarla.
            // Para ello, la API REST en UsuarioFacadeREST debería tener un método específico para cambiar la contraseña
            // (ej. @PUT @Path("{id}/password")), o el método `edit` de la API REST debería ser lo suficientemente inteligente
            // para hashear una nueva contraseña si se envía.
            // **ASUMIMOS que EstudyRestFacade.updateUserPassword (que crearemos) llamaría a un endpoint específico en la API externa.**

            // Si no tienes un endpoint específico de cambio de contraseña en la API externa,
            // esta parte sería un problema de seguridad si intentaras hashear aquí.

            // Por ahora, asumamos que la fachada puede manejar esto, posiblemente llamando a un nuevo endpoint en la API externa.
            boolean success = facade.updateUserPassword(userId, newPlainPassword); // Este método debe existir en EstudyRestFacade
            if (!success) {
                logger.error("Fallo la actualización de contraseña para usuario ID {}.", userId);
                throw new ServiceException("No se pudo actualizar la contraseña.");
            }
            logger.info("Contraseña del usuario ID {} actualizada con éxito.", userId);
            return true;
        } catch (AuthenticationFailedException e) {
            throw e; // Propaga la excepción de autenticación fallida
        } catch (UserNotFoundException e) {
            throw e; // Propaga si el usuario no existe
        } catch (ClientErrorException | IOException e) {
            logger.error("Error de comunicación al cambiar la contraseña para usuario ID {}: {}", userId, e.getMessage(), e);
            throw new ServiceException("Error de comunicación al cambiar contraseña.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al cambiar la contraseña para usuario ID {}: {}", userId, e.getMessage(), e);
            throw new ServiceException("Error inesperado al cambiar contraseña.", e);
        }
    }
    
    /**
     * Actualiza las preferencias de estudio del usuario.
     *
     * @param userId El ID del usuario.
     * @param preferences Un objeto o mapa con las preferencias a actualizar.
     * Para simplificar, este método podría simplemente actualizar campos específicos del Usuario
     * o requerir una entidad `UserPreferences` separada.
     * Por ahora, simularemos que `Usuario` tiene campos de preferencias o que se usan un `Map<String, String>`.
     * Aquí, lo hacemos flexible y simplemente actualizamos el usuario si la entidad `Usuario` soporta
     * directamente los campos de preferencia (ej. `setTechniquePreference`).
     * @return El Usuario con las preferencias actualizadas.
     * @throws UserNotFoundException Si el usuario no existe.
     * @throws ServiceException Si ocurre un error inesperado.
     */
    public Usuario updateStudyPreferences(int userId, String techniquePreference, String notificationPreference) {
        if (userId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido.");
        }
        try {
            Usuario user = getUserById(userId); // Obtener el usuario existente
            // Aquí asumiríamos que la entidad Usuario tiene campos para estas preferencias
            // O que se usa un DTO/entidad de preferencias separada.
            // Por simplicidad, si los campos no existen en Usuario, esto es conceptual.
            if (techniquePreference != null && !techniquePreference.trim().isEmpty()) {
                // user.setTechniquePreference(techniquePreference); // Ejemplo: si Usuario tiene este campo
            }
            if (notificationPreference != null && !notificationPreference.trim().isEmpty()) {
                // user.setNotificationPreference(notificationPreference); // Ejemplo: si Usuario tiene este campo
            }

            // Para que esto funcione, `updateUsuario` en `EstudyRestFacade` debe aceptar un `Usuario` actualizado
            // y la API externa debe manejar la actualización de estos campos.
            // Como las preferencias pueden ser más complejas, idealmente tendrías una entidad `PreferenciasUsuario`
            // y su propio `PreferenceService`.
            // Por ahora, si estos campos están en Usuario, actualizamos todo el objeto.
            Usuario updatedUser = facade.updateUsuario(user); // Reutilizamos el update general
            if (updatedUser == null) {
                throw new ServiceException("No se pudieron actualizar las preferencias del usuario.");
            }
            logger.info("Preferencias de estudio para usuario ID {} actualizadas.", userId);
            return updatedUser;
        } catch (UserNotFoundException e) {
            throw e;
        } catch (ClientErrorException | IOException e) {
            logger.error("Error de comunicación al actualizar preferencias para usuario ID {}: {}", userId, e.getMessage(), e);
            throw new ServiceException("Error de comunicación al actualizar preferencias.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar preferencias para usuario ID {}: {}", userId, e.getMessage(), e);
            throw new ServiceException("Error inesperado al actualizar preferencias.", e);
        }
    }
    
    /**
     * Gestiona las preferencias de notificaciones y recordatorios.
     * Similar a updateStudyPreferences, podría requerir un DTO específico o campos en Usuario.
     *
     * @param userId El ID del usuario.
     * @param enableNotifications Si las notificaciones generales están habilitadas.
     * @param notificationFrequency Frecuencia (ej., "diario", "semanal").
     * @return El Usuario con las preferencias de notificación actualizadas.
     * @throws UserNotFoundException Si el usuario no existe.
     * @throws ServiceException Si ocurre un error inesperado.
     */
    public Usuario manageNotificationPreferences(int userId, boolean enableNotifications, String notificationFrequency) {
        if (userId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido.");
        }
        try {
            Usuario user = getUserById(userId); // Obtener el usuario existente

            // user.setNotificationsEnabled(enableNotifications); // Ejemplo: si Usuario tiene este campo
            // user.setNotificationFrequency(notificationFrequency); // Ejemplo: si Usuario tiene este campo

            Usuario updatedUser = facade.updateUsuario(user);
            if (updatedUser == null) {
                throw new ServiceException("No se pudieron actualizar las preferencias de notificación.");
            }
            logger.info("Preferencias de notificación para usuario ID {} actualizadas.", userId);
            return updatedUser;
        } catch (UserNotFoundException e) {
            throw e;
        } catch (ClientErrorException | IOException e) {
            logger.error("Error de comunicación al actualizar preferencias de notificación para usuario ID {}: {}", userId, e.getMessage(), e);
            throw new ServiceException("Error de comunicación al actualizar preferencias de notificación.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar preferencias de notificación para usuario ID {}: {}", userId, e.getMessage(), e);
            throw new ServiceException("Error inesperado al actualizar preferencias de notificación.", e);
        }
    }
    
    /**
     * Obtiene los grupos de estudio a los que pertenece un usuario.
     * Llama a EstudyRestFacade para obtener los grupos directamente de la API.
     * @param userId ID del usuario.
     * @return Lista de GrupoEstudio.
     * @throws ServiceException Si ocurre un error en el servicio.
     * @throws UserNotFoundException Si el usuario no es encontrado (aunque la fachada ya lo maneja, se mantiene por consistencia).
     */
    public List<GrupoEstudio> getGruposByUsuario(int userId) throws ServiceException, UserNotFoundException {
        try {
            // Llama a la fachada para obtener los grupos.
            // La fachada ya maneja la comunicación con el UsuarioClient.
            return facade.getGruposDeUsuario(userId);
        } catch (IOException e) {
            throw new ServiceException("Error al obtener grupos del usuario: " + e.getMessage(), e);
        }
    }
}
