/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.estudyplus.controlador.action;

import com.estudyplus.controlador.services.StudySessionService;
import com.estudyplus.controlador.services.UserService;
import com.estudyplus.controlador.services.exception.ServiceException;
import com.estudyplus.controlador.services.exception.UserNotFoundException;
import com.estudyplus.modelo.dto.JsonResponse;
import com.estudyplus.modelo.entitys.TecnicaEstudio;
import com.estudyplus.modelo.entitys.Usuario;
import com.estudyplus.modelo.rest.facade.EstudyRestFacade;
import com.estudyplus.modelo.rest.facade.EstudyRestFacadeFactory;
import com.estudyplus.utils.parseConfigJson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.I18nInterceptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;
import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action para gestionar el perfil del usuario:
 * - Mostrar información del perfil.
 * - Actualizar datos del perfil (nombre, email, configuración).
 * - Cambiar contraseña.
 * - Eliminar cuenta.
 * Todos los métodos AJAX devuelven JsonResponse.
 * @author Parri
 */
public class ProfileAction extends ActionSupport implements SessionAware {

    private static final Logger logger = LoggerFactory.getLogger(ProfileAction.class);
    private static final ObjectMapper objectMapper = new ObjectMapper(); // Para JSON

    private UserService userService;
    private StudySessionService sesionEstudioService; // Para obtener técnicas de estudio
    private EstudyRestFacade facade = EstudyRestFacadeFactory.getInstance();
    private Map<String, Object> session;

    // Propiedades para el perfil
    private Usuario usuario; // El usuario logeado, para mostrar y editar
    private String feedbackMessage;
    private JsonResponse jsonResponse; // Para respuestas AJAX

    // Propiedades para el cambio de contraseña
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;

    // Propiedades para la eliminación de cuenta
    private String deletePasswordConfirmation;

    // Propiedades para opciones de configuración (ej. idiomas, técnicas)
    private List<TecnicaEstudio> tecnicasDisponibles = new ArrayList();
    private Map<String, String> availableLocales = getAvailableLocalesMap(); // Para el selector de idioma
    private String locale_e; // Propiedad para el idioma seleccionado por el usuario
    private List<Integer> favoriteTechniqueIds; // Propiedad para las técnicas favoritas

    public ProfileAction() {
        this.userService = new UserService(EstudyRestFacadeFactory.getInstance());
        this.sesionEstudioService = new StudySessionService(); // Inicializar el servicio de sesiones
    }

    /**
     * Carga y muestra el perfil del usuario logeado.
     * Es el método principal para acceder a la página de perfil.
     * @return SUCCESS, LOGIN o ERROR
     */
    @Override
    public String execute() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            addActionError(getText("profile.error.notLoggedIn"));
            return LOGIN;
        }

        try {
            // Obtener la información más reciente del usuario
            usuario = facade.getUsuarioById(loggedInUser.getId());
            if (usuario == null) {
                addActionError(getText("profile.error.userNotFound"));
                logger.error("Usuario logeado ID {} no encontrado en la base de datos.", loggedInUser.getId());
                return ERROR;
            }
            // Actualizar el objeto de sesión con la información más reciente
            session.put("loggedInUser", usuario);

            // Cargar datos para los selectores (ej. técnicas de estudio)
            tecnicasDisponibles = facade.getAllTecnicasEstudio();
            if (tecnicasDisponibles == null) tecnicasDisponibles = Collections.emptyList();

            // Cargar locales disponibles (ejemplo, podrías tenerlos en un enum o configuración)
            availableLocales = getAvailableLocalesMap();
            
            /*// Establecer el locale actual para que el <s:select> lo muestre seleccionado
            if (session.containsKey(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)) {
            Locale currentSessionLocale = (Locale) session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE);
            this.locale_e = currentSessionLocale.toString();
            } else {
            this.locale_e = "es"; // Por defecto, si no hay locale en sesión
            }*/
            // --- Cargar configuraciones del perfil (idioma y técnicas favoritas) ---
            Map<String, Object> configMap = parseConfigJson(usuario.getConfiguracionPerfil());

            // Establecer el locale actual para que el <s:select> lo muestre seleccionado
            // Prioridad: 1. Configuración guardada, 2. Sesión, 3. Por defecto 'es'
            if (configMap.containsKey("locale")) {
                this.locale_e = (String) configMap.get("locale");
                // Asegurarse de que el locale de la sesión coincida con el de la configuración
                session.put(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE, new Locale(this.locale_e));
            } else if (session.containsKey(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)) {
                Locale currentSessionLocale = (Locale) session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE);
                this.locale_e = currentSessionLocale.toString();
            } else {
                this.locale_e = "es"; // Por defecto, si no hay locale en sesión ni en configuración
            }

            // Establecer las técnicas favoritas para que el <s:select> las muestre seleccionadas
            if (configMap.containsKey("favoriteTechniques")) {
                // Jackson deserializa List<Integer> como List<Object> por defecto, casteamos
                List<?> rawList = (List<?>) configMap.get("favoriteTechniques");
                this.favoriteTechniqueIds = new ArrayList<>();
                for (Object item : rawList) {
                    if (item instanceof Integer) {
                        this.favoriteTechniqueIds.add((Integer) item);
                    } else if (item instanceof String) { // En caso de que se deserialice como String (ej. "1")
                        try {
                            this.favoriteTechniqueIds.add(Integer.parseInt((String) item));
                        } catch (NumberFormatException nfe) {
                            logger.warn("Formato de ID de técnica favorita inválido: {}", item);
                        }
                    }
                }
            } else {
                this.favoriteTechniqueIds = Collections.emptyList();
            }

            logger.info("Perfil cargado para usuario ID {}.", loggedInUser.getId());
            return SUCCESS;
        } catch (ServiceException e) {
            addActionError(getText("profile.error.loadFailed") + e.getMessage());
            logger.error("ServiceException al cargar perfil para usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
            return ERROR;
        } catch (Exception e) {
            addActionError(getText("profile.error.unexpectedLoad"));
            logger.error("Excepción inesperada al cargar perfil para usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
            return ERROR;
        }
    }

    /**
     * Procesa la actualización de los datos del perfil (nombre, email, configuración).
     * Devuelve JsonResponse para ser consumido por AJAX.
     * @return SUCCESS (para JSON)
     */
    public String updateProfileAjax() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, getText("profile.error.notLoggedIn"));
            return SUCCESS;
        }

        if (usuario == null) {
            jsonResponse = new JsonResponse(false, getText("profile.error.noDataProvided"));
            return SUCCESS;
        }

        try {
            // Obtener el usuario actual de la base de datos para actualizar
            Usuario existingUser = facade.getUsuarioById(loggedInUser.getId());
            if (existingUser == null) {
                jsonResponse = new JsonResponse(false, getText("profile.error.userNotFound"));
                logger.error("Usuario logeado ID {} no encontrado para actualizar perfil.", loggedInUser.getId());
                return SUCCESS;
            }

            // Actualizar solo los campos permitidos
            existingUser.setNombre(ESAPI.encoder().encodeForHTML(usuario.getNombre()));
            existingUser.setEmail(ESAPI.encoder().encodeForHTML(usuario.getEmail()));
            //existingUser.setConfiguracionPerfil(usuario.getConfiguracionPerfil()); // Asume que el front-end envía el String completo
            Map<String, Object> configMap = parseConfigJson.parseConfigJson(existingUser.getConfiguracionPerfil(), objectMapper);
            
            logger.debug("locale_e: "+locale_e+" favoriteTechniqueIds: "+favoriteTechniqueIds);
            // Actualizar locale
            if (this.locale_e != null && !this.locale_e.isEmpty()) {
                configMap.put("locale", this.locale_e);
                // Actualizar la sesión de Struts2 para que el I18nInterceptor lo recoja
                session.put(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE, new Locale(this.locale_e));
            } else {
                configMap.remove("locale"); // Si se deselecciona o se envía vacío
                session.remove(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE); // O establecer un valor por defecto
            }

            // Actualizar técnicas favoritas
            if (this.favoriteTechniqueIds != null && !this.favoriteTechniqueIds.isEmpty()) {
                configMap.put("favoriteTechniques", this.favoriteTechniqueIds);
            } else {
                configMap.remove("favoriteTechniques"); // Si no se selecciona ninguna
            }

            // Convertir el mapa de configuración a JSON y guardarlo en el usuario
            existingUser.setConfiguracionPerfil(objectMapper.writeValueAsString(configMap));
            
            /*// --- Lógica para el idioma y configuración del perfil ---
            String currentConfig = existingUser.getConfiguracionPerfil();
            String updatedConfig = updateLocaleInConfig(currentConfig, this.locale_e);
            existingUser.setConfiguracionPerfil(updatedConfig);*/

            facade.updateUsuario(existingUser);
            session.put("loggedInUser", existingUser); // Actualizar la sesión
            
            /*// Si el idioma ha cambiado, actualizar la sesión de Struts2 para que el I18nInterceptor lo recoja
            if (this.locale_e != null && !this.locale_e.isEmpty()) {
                Locale selectedLocale = new Locale(this.locale_e);
                session.put(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE, selectedLocale);
            }*/

            jsonResponse = new JsonResponse(true, getText("profile.update.success"), existingUser);
            logger.info("Perfil actualizado para usuario ID {}.", loggedInUser.getId());
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, getText("profile.update.error") + e.getMessage());
            logger.error("ServiceException al actualizar perfil para usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, getText("profile.update.unexpectedError"));
            logger.error("Excepción inesperada al actualizar perfil para usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
            return SUCCESS;
        }
    }

    /**
     * Procesa el cambio de contraseña.
     * Devuelve JsonResponse para ser consumido por AJAX.
     * @return SUCCESS (para JSON)
     */
    public String changePasswordAjax() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, getText("profile.error.notLoggedIn"));
            return SUCCESS;
        }

        if (currentPassword == null || newPassword == null || confirmNewPassword == null ||
            currentPassword.trim().isEmpty() || newPassword.trim().isEmpty() || confirmNewPassword.trim().isEmpty()) {
            jsonResponse = new JsonResponse(false, getText("profile.changePassword.error.emptyFields"));
            return SUCCESS;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            jsonResponse = new JsonResponse(false, getText("profile.changePassword.error.passwordMismatch"));
            return SUCCESS;
        }
        
        if (newPassword.length() < 6) { // Ejemplo de validación de longitud mínima
            jsonResponse = new JsonResponse(false, getText("profile.changePassword.error.passwordTooShort"));
            return SUCCESS;
        }

        try {
            // Verificar la contraseña actual (asume que el servicio tiene un método para esto)
            // Esto es CRÍTICO para la seguridad. NO compares contraseñas en texto plano aquí.
            // El servicio debe verificar el hash de la contraseña actual.
            boolean currentPasswordMatches = facade.authenticateUser(loggedInUser.getNombre(), currentPassword)!=null;
            if (!currentPasswordMatches) {
                jsonResponse = new JsonResponse(false, getText("profile.changePassword.error.currentPasswordIncorrect"));
                return SUCCESS;
            }

            // Cambiar la contraseña. El servicio debe hashear la nueva contraseña antes de almacenarla.
            facade.updateUserPassword(loggedInUser.getId(), newPassword);

            jsonResponse = new JsonResponse(true, getText("profile.changePassword.success"));
            logger.info("Contraseña cambiada para usuario ID {}.", loggedInUser.getId());
            return SUCCESS;
        } catch (UserNotFoundException e) {
            jsonResponse = new JsonResponse(false, getText("profile.error.userNotFound"));
            logger.error("Usuario ID {} no encontrado al cambiar contraseña.", loggedInUser.getId());
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, getText("profile.changePassword.error") + e.getMessage());
            logger.error("ServiceException al cambiar contraseña para usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, getText("profile.changePassword.unexpectedError"));
            logger.error("Excepción inesperada al cambiar contraseña para usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
            return SUCCESS;
        }
    }

    /**
     * Procesa la eliminación de la cuenta del usuario.
     * Devuelve JsonResponse para ser consumido por AJAX.
     * @return SUCCESS (para JSON)
     */
    public String deleteAccountAjax() {
        Usuario loggedInUser = (Usuario) session.get("loggedInUser");
        if (loggedInUser == null) {
            jsonResponse = new JsonResponse(false, getText("profile.error.notLoggedIn"));
            return SUCCESS;
        }

        if (deletePasswordConfirmation == null || deletePasswordConfirmation.trim().isEmpty()) {
            jsonResponse = new JsonResponse(false, getText("profile.deleteAccount.error.emptyPassword"));
            return SUCCESS;
        }

        try {
            // Verificar la contraseña antes de eliminar
            boolean passwordMatches = facade.authenticateUser(loggedInUser.getNombre(), deletePasswordConfirmation)!=null;
            if (!passwordMatches) {
                jsonResponse = new JsonResponse(false, getText("profile.deleteAccount.error.passwordIncorrect"));
                return SUCCESS;
            }

            facade.deleteUsuario(loggedInUser.getId());
            session.remove("loggedInUser"); // Eliminar usuario de la sesión

            jsonResponse = new JsonResponse(true, getText("profile.deleteAccount.success"));
            logger.info("Cuenta eliminada para usuario ID {}.", loggedInUser.getId());
            return SUCCESS;
        } catch (UserNotFoundException e) {
            jsonResponse = new JsonResponse(false, getText("profile.error.userNotFound"));
            logger.error("Usuario ID {} no encontrado al eliminar cuenta.", loggedInUser.getId());
            return SUCCESS;
        } catch (ServiceException e) {
            jsonResponse = new JsonResponse(false, getText("profile.deleteAccount.error") + e.getMessage());
            logger.error("ServiceException al eliminar cuenta para usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
            return SUCCESS;
        } catch (Exception e) {
            jsonResponse = new JsonResponse(false, getText("profile.deleteAccount.unexpectedError"));
            logger.error("Excepción inesperada al eliminar cuenta para usuario ID {}: {}", loggedInUser.getId(), e.getMessage(), e);
            return SUCCESS;
        }
    }

    // Método auxiliar para locales (ejemplo, podrías expandirlo)
    private Map<String, String> getAvailableLocalesMap() {
        // En un entorno real, esto podría venir de una configuración o de un servicio de i18n
        return new HashMap<String, String>() {{
            put("es", "Español");
            put("en", "English");
        }};
    }

    /**
     * Método auxiliar para actualizar el locale en la configuración del perfil (asumiendo formato JSON).
     * Si configuracionPerfil no es JSON, se inicializa como tal.
     * @param currentConfig La cadena JSON actual de configuracionPerfil.
     * @param newLocale El nuevo código de idioma (ej. "es", "en").
     * @return La cadena JSON de configuracionPerfil actualizada.
     */
    private String updateLocaleInConfig(String currentConfig, String newLocale) {
        Map<String, Object> configMap = new HashMap<>();
        if (currentConfig != null && !currentConfig.isEmpty()) {
            try {
                configMap = objectMapper.readValue(currentConfig, new TypeReference<Map<String, Object>>() {});
            } catch (IOException e) {
                logger.warn("Error al parsear configuracionPerfil como JSON: {}. Se inicializará una nueva configuración.", e.getMessage());
                // Si hay un error al parsear, se asume que no es JSON o está corrupto, y se empieza de cero.
                configMap = new HashMap<>();
            }
        }
        configMap.put("locale", newLocale); // Guarda el locale
        try {
            return objectMapper.writeValueAsString(configMap);
        } catch (IOException e) {
            logger.error("Error al serializar configuracionPerfil a JSON: {}", e.getMessage(), e);
            return currentConfig; // Devuelve la configuración original si falla la serialización
        }
    }
    
    /**
     * Parsea la cadena JSON de configuracionPerfil a un Map.
     * Si la cadena es nula, vacía o inválida, devuelve un HashMap vacío.
     */
    private Map<String, Object> parseConfigJson(String configJson) {
        if (configJson == null || configJson.trim().isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(configJson, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            logger.warn("Error al parsear configuracionPerfil como JSON: {}. Se devolverá un mapa vacío.", e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * MÉTODO ESTÁTICO ACCESIBLE DESDE EL JSP PARA PARSEAR TÉCNICAS FAVORITAS.
     * Este método se registrará en struts.xml para ser llamado desde el JSP.
     * @param configJson La cadena JSON de configuracionPerfil.
     * @return Una lista de IDs de técnicas favoritas, o una lista vacía si no se encuentran.
     */
    public static List<Integer> parseFavoriteTechniques(String configJson) {
        if (configJson == null || configJson.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            Map<String, Object> configMap = objectMapper.readValue(configJson, new TypeReference<Map<String, Object>>() {});
            if (configMap.containsKey("favoriteTechniques")) {
                List<?> rawList = (List<?>) configMap.get("favoriteTechniques");
                List<Integer> favTechs = new ArrayList<>();
                for (Object item : rawList) {
                    if (item instanceof Integer) {
                        favTechs.add((Integer) item);
                    } else if (item instanceof String) {
                        try {
                            favTechs.add(Integer.parseInt((String) item));
                        } catch (NumberFormatException nfe) {
                            logger.warn("Formato de ID de técnica favorita inválido en JSON: {}", item);
                        }
                    }
                }
                return favTechs;
            }
        } catch (IOException e) {
            logger.warn("Error al parsear configuracionPerfil (para técnicas favoritas) como JSON: {}. Se devolverá una lista vacía.", e.getMessage());
        }
        return Collections.emptyList();
    }
    
    // --- Getters y Setters ---
    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }

    public JsonResponse getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(JsonResponse jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }

    public String getDeletePasswordConfirmation() {
        return deletePasswordConfirmation;
    }

    public void setDeletePasswordConfirmation(String deletePasswordConfirmation) {
        this.deletePasswordConfirmation = deletePasswordConfirmation;
    }

    public List<TecnicaEstudio> getTecnicasDisponibles() {
        return tecnicasDisponibles;
    }

    public void setTecnicasDisponibles(List<TecnicaEstudio> tecnicasDisponibles) {
        this.tecnicasDisponibles = tecnicasDisponibles;
    }

    public Map<String, String> getAvailableLocales() {
        return availableLocales;
    }

    public void setAvailableLocales(Map<String, String> availableLocales) {
        this.availableLocales = availableLocales;
    }
    
    public String getLocale_e(){
        return locale_e;
    }

    public void setLocale_e(String locale_e) {
        this.locale_e = locale_e;
    }
}
