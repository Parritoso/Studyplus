# Métodos de Seguridad Informática en un Modelo MVC+S

Este documento detalla varios métodos de seguridad informática relevantes para una aplicación construida bajo el patrón Modelo-Vista-Controlador más Servicios (MVC+S), con especial atención a su implementación en Java.

## Introducción

La seguridad en aplicaciones web es una preocupación fundamental. Un modelo MVC+S, al separar las responsabilidades de la presentación, la lógica de negocio y el almacenamiento de datos, ofrece puntos estratégicos donde se pueden implementar diversas medidas de seguridad para proteger la aplicación contra amenazas comunes.

## Métodos de Seguridad

A continuación, se describen algunos métodos de seguridad importantes, con ejemplos de cómo podrían implementarse en un contexto Java.

### 1. Protección contra Ataques de Falsificación de Peticiones en Sitios Cruzados (CSRF)

**¿Qué es CSRF?**

CSRF es un ataque donde un sitio web malicioso, un correo electrónico, un mensaje instantáneo, o un programa hace que el navegador web de un usuario víctima realice una acción no deseada en un sitio de confianza cuando el usuario está autenticado.

**¿Cómo funciona?**

El atacante engaña al navegador del usuario para que envíe una petición HTTP a la aplicación web de destino. Debido a que el usuario está autenticado, la aplicación no puede distinguir entre una petición legítima del usuario y una petición maliciosa originada por el atacante.

**Implementación en Java: Tokens CSRF**

La técnica más común para prevenir ataques CSRF es el uso de tokens sincronizados.

**Mecanismo:**

1.  **Generación del Token:** Cuando se presenta un formulario al usuario, el servidor genera un token único y secreto asociado a la sesión del usuario.
2.  **Inclusión en el Formulario:** Este token se incluye como un campo oculto en el formulario HTML.
3.  **Validación en el Servidor:** Cuando el formulario es enviado, el servidor verifica que el token recibido coincida con el token almacenado en la sesión del usuario. Si no coinciden, la petición se considera inválida y se rechaza.

**Ejemplo de Implementación en Java (Servlet/JSP):**

**Generación del Token (en un Servlet o filtro):**

```java
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Base64;
import java.util.Random;

public class CSRFTokenGenerator {

    public static String generateToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Random random = new Random();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getEncoder().encodeToString(bytes);
        session.setAttribute("csrfToken", token);
        return token;
    }

    public static boolean isTokenValid(HttpServletRequest request, String submittedToken) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String sessionToken = (String) session.getAttribute("csrfToken");
            return sessionToken != null && sessionToken.equals(submittedToken);
        }
        return false;
    }
}
```

**Inclusión del Token en un JSP:**

```java
<%@ page import="com.example.security.CSRFTokenGenerator" %>
<% String csrfToken = CSRFTokenGenerator.generateToken(request); %>
<form action="/mi-accion" method="post">
    <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
    <button type="submit">Enviar</button>
</form>
```

**Validación del Token en un Servlet:**

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebServlet("/mi-accion")
public class MiAccionServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String submittedToken = request.getParameter("csrfToken");

        if (submittedToken != null && CSRFTokenGenerator.isTokenValid(request, submittedToken)) {
            // Procesar la petición legítima
            response.getWriter().println("¡Acción procesada exitosamente!");
        } else {
            // La petición es inválida, posiblemente un ataque CSRF
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Petición inválida (CSRF)");
        }
    }
}
```

**Consideraciones:**
- El token debe ser único por sesión.
- El token debe ser impredecible.
- Es importante invalidar el token después de un cierto tiempo o después de su uso para acciones críticas.
- Frameworks Java como Spring Security proporcionan soporte integrado para la protección CSRF.

### 2. Autenticación y Autorización Robustas ###

**Autenticación**: Verificar la identidad del usuario.

**Autorización**: Determinar qué acciones puede realizar un usuario autenticado.

**Implementación en Java:**

 - **Java EE Security API**: Proporciona mecanismos estándar para la autenticación (por ejemplo, Basic Auth, Form-Based Auth) y la autorización basada en roles.
 - **Implementación Personalizada**: Se pueden implementar esquemas de autenticación y autorización personalizados utilizando filtros de Servlet, interceptores o aspectos. Esto implica gestionar la creación de sesiones, el almacenamiento de información del usuario y la verificación de permisos.

### 3. Validación y Desinfección de Entradas ###

**¿Por qué es importante?**

La validación y desinfección de las entradas del usuario son cruciales para prevenir ataques como la inyección de SQL, la inyección de comandos y el Cross-Site Scripting (XSS).

**Validación**: Asegurarse de que los datos ingresados por el usuario cumplen con los requisitos esperados (formato, longitud, tipo, etc.).

**Desinfección**: Limpiar o escapar los datos ingresados por el usuario para eliminar o neutralizar cualquier código malicioso que puedan contener.

**Implementación en Java**:
 - Validación con Bean Validation API (JSR-380): Permite definir restricciones de validación mediante anotaciones en los JavaBeans. Frameworks como Hibernate Validator proporcionan una implementación de esta API.
 - Validación Manual: Implementar lógica de validación personalizada utilizando expresiones regulares, comprobaciones de tipo, etc.
 - Librerías de Desinfección: Utilizar librerías como OWASP Java HTML Sanitizer para limpiar entradas HTML y prevenir ataques XSS.
 - Consultas Parametrizadas (Prepared Statements) en JDBC: Fundamental para prevenir la inyección de SQL al separar la estructura de la consulta de los datos proporcionados por el usuario.

**Ejemplo de Validación con Bean Validation:**
```java
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class Usuario {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    private String email;

    // Getters y setters
}
```

**Ejemplo de Desinfección con OWASP Java HTML Sanitizer:**
```java
import org.owasp.html.Sanitizers;
import org.owasp.html.PolicyFactory;

public class SanitizerUtil {

    private static final PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    public static String sanitizeHTML(String input) {
        return policy.sanitize(input);
    }
}
```

### 4. Gestión Segura de Sesiones ###
Importancia: Una gestión de sesiones insegura puede permitir a los atacantes robar o manipular las sesiones de los usuarios autenticados.
Medidas de Seguridad:
 - Uso de Cookies Seguras (HttpOnly y Secure):
   - `HttpOnly`: Impide que JavaScript del lado del cliente acceda a la cookie de sesión, mitigando ataques XSS que intentan robar la ID de sesión.
   - `Secure`: Asegura que la cookie de sesión solo se transmita a través de conexiones HTTPS encriptadas, protegiéndola de la interceptación en tránsito.
 - Generación de IDs de Sesión Fuertes y Aleatorias: Utilizar algoritmos robustos para generar IDs de sesión impredecibles.
 - Rotación Periódica de IDs de Sesión: Cambiar la ID de sesión después de eventos críticos como el inicio de sesión para limitar el impacto de una posible fuga.
 - Tiempo de Expiración de Sesión (Timeout): Establecer un tiempo de inactividad después del cual la sesión se invalida automáticamente.
 - Almacenamiento Seguro de Datos de Sesión: Si se almacenan datos sensibles en la sesión, considerar su encriptación.

Implementación en Java (configuración en `web.xml` o mediante API específica del servidor de aplicaciones):
```xml
<session-config>
    <cookie-config>
        <http-only>true</http-only>
        <secure>true</secure>
    </cookie-config>
    <session-timeout>30</session-timeout> </session-config>
```

### 5. Protección de Datos Sensibles ###
 - Cifrado en Tránsito (HTTPS): Implementar HTTPS para asegurar que todas las comunicaciones entre el navegador del usuario y el servidor estén encriptadas, protegiendo la confidencialidad e integridad de los datos.
 - Cifrado en Reposo: Cifrar los datos sensibles almacenados en la base de datos o en otros medios de almacenamiento.
 - Hashing Seguro de Contraseñas: Nunca almacenar contraseñas en texto plano. Utilizar funciones hash seguras (como bcrypt, Argon2) con "salt" únicos por usuario para dificultar los ataques de diccionario y de tabla arcoíris.
 - Minimización de la Exposición de Datos: Solo almacenar y exponer los datos estrictamente necesarios.
 - Control de Acceso Estricto: Implementar políticas de control de acceso para limitar quién puede acceder a los datos sensibles.

**Implementación en Java:**

 - HTTPS: Configurar el servidor de aplicaciones (Tomcat, Jetty, etc.) para habilitar HTTPS y obtener un certificado SSL/TLS.
 - Cifrado: Utilizar las API de cifrado de Java (JCE - Java Cryptography Extension) o librerías de terceros como Bouncy Castle. 

** Hashing de Contraseñas (Ejemplo con BCrypt)**:
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }
}
```

### 6. Gestión de Errores y Excepciones Segura ###

¿Por qué es importante?

La información detallada sobre los errores y excepciones puede revelar información sensible sobre la aplicación a posibles atacantes.

Prácticas Seguras:

 - Manejo de Excepciones Genérico en la Capa de Presentación: Mostrar mensajes de error amigables y genéricos al usuario.
 - Registro Detallado de Errores en el Servidor: Registrar la información detallada de los errores y excepciones en logs del servidor para su posterior análisis y depuración. Asegurarse de que estos logs estén protegidos contra acceso no autorizado.
 - Evitar la Exposición de Información Sensible en los Mensajes de Error: No incluir detalles internos de la aplicación, rutas de archivos, o información de configuración en los mensajes de error mostrados al usuario.
**Implementación en Java:**

Utilizar `bloques try-catch` para manejar las excepciones y proporcionar respuestas controladas. Configurar un sistema de logging robusto (por ejemplo, Log4j, SLF4j) para registrar los detalles de los errores.

### 7. Seguridad en la Capa de Servicios (API) ###
Al exponer una API (REST, SOAP, etc.), es crucial asegurar esta capa también:

 - Autenticación y Autorización: Utilizar mecanismos de autenticación robustos como OAuth 2.0, JWT (JSON Web Tokens) o API Keys. Implementar una autorización granular para controlar el acceso a los recursos de la API.
 - Validación de Entradas: Validar rigurosamente todos los datos que se reciben a través de la API.
 - Limitación de Tasas (Rate Limiting): Proteger la API contra ataques de denegación de servicio (DoS) limitando el número de peticiones que un cliente puede realizar en un período de tiempo determinado.
 - Control de Versiones de la API: Permitir la evolución de la API sin romper la compatibilidad con los clientes existentes.
 - Seguridad en la Transferencia (HTTPS): Asegurar todas las comunicaciones con la API mediante HTTPS.

La capa de servicios es un punto de entrada crucial para la funcionalidad y los datos de nuestra aplicación. Asegurar esta capa es fundamental para protegerla de accesos no autorizados, manipulación de datos y otros ataques. Las estrategias de seguridad pueden variar significativamente entre APIs REST y SOAP debido a sus diferentes arquitecturas y estándares.

#### Seguridad en APIs REST

Las APIs REST (Representational State Transfer) se caracterizan por ser ligeras, sin estado y operar típicamente sobre HTTP. Esto influye en las estrategias de seguridad que se aplican.

**1. Autenticación:** Verificar la identidad del cliente que realiza la petición.

   * **Autenticación Basada en Tokens (JWT - JSON Web Tokens):**
      * El cliente se autentica inicialmente (por ejemplo, con usuario y contraseña).
      * El servidor genera un JWT que contiene información sobre la identidad del cliente y, opcionalmente, sus permisos.
      * El JWT se devuelve al cliente, que lo incluye en las cabeceras de las peticiones subsiguientes (normalmente en la cabecera `Authorization: Bearer <token>`).
      * El servidor verifica la firma del JWT para autenticar al cliente en cada petición, sin necesidad de mantener una sesión en el servidor.
      * **Ejemplo Conceptual en Java (Generación de JWT con una librería como JJWT):**

```java
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JWTUtil {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generar una clave segura en producción
    private static final long EXPIRATION_TIME = 864_000_000; // 10 días en milisegundos

    public static String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ... (Métodos para validar el token)
}
```

      **Ejemplo Conceptual en Java (Filtro para verificar el JWT):**

```java
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String userId = JWTUtil.getUserIdFromToken(token);
                // Aquí podrías cargar la información del usuario y establecerla en un contexto de seguridad
                request.setAttribute("userId", userId);
                chain.doFilter(request, response);
            } catch (Exception e) {
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token inválido.");
            }
        } else {
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token de autenticación requerido.");
        }
    }
}
```

   * **OAuth 2.0:** Un estándar de autorización que permite a aplicaciones de terceros obtener acceso limitado a recursos HTTP de un servicio, en nombre de un usuario, mediante la obtención de tokens de acceso. Es especialmente útil para escenarios donde diferentes aplicaciones necesitan interactuar con la API.

   * **API Keys:** Claves secretas que se envían con cada petición para identificar a la aplicación cliente. Son más sencillas de implementar pero menos seguras que los tokens de usuario, ya que no identifican a un usuario específico.

   * **Autenticación Básica (Basic Auth):** El cliente envía el nombre de usuario y la contraseña codificados en Base64 en la cabecera `Authorization`. No se recomienda para la mayoría de los casos debido a su falta de seguridad (las credenciales se transmiten en cada petición).

**2. Autorización:** Determinar qué recursos y acciones puede realizar un cliente autenticado.

   * **Control de Acceso Basado en Roles (RBAC):** Asignar roles a los usuarios/clientes y definir permisos para cada rol. El servidor verifica el rol del cliente autenticado antes de permitir el acceso a un recurso.
   * **Control de Acceso Basado en Atributos (ABAC):** Evaluar un conjunto de atributos (del usuario, del recurso, del contexto) para tomar decisiones de acceso. Es más flexible pero también más complejo de implementar.
   * **Políticas de Acceso:** Definir reglas explícitas que determinan quién puede acceder a qué recursos y bajo qué condiciones.

**3. Validación de Entradas:** Asegurarse de que los datos enviados a la API cumplen con los esquemas y formatos esperados para prevenir inyecciones y otros ataques. Utilizar mecanismos de validación robustos en el servidor.

**4. Limitación de Tasas (Rate Limiting):** Proteger la API contra ataques de denegación de servicio (DoS) o abuso limitando el número de peticiones que un cliente puede realizar dentro de un período de tiempo determinado.

**5. Protección contra Inyecciones:**
   * **SQL Injection:** Si la API interactúa con una base de datos SQL, utilizar consultas parametrizadas o ORMs que las generen automáticamente.
   * **NoSQL Injection:** Si se utiliza una base de datos NoSQL, tener en cuenta las posibles vulnerabilidades de inyección específicas de esa base de datos y utilizar las protecciones adecuadas que ofrezca el driver.
   * **Command Injection:** Evitar la ejecución directa de comandos del sistema basados en la entrada del usuario.

**6. Seguridad en la Transferencia (HTTPS):** Como se mencionó anteriormente, es fundamental utilizar HTTPS para cifrar toda la comunicación entre el cliente y el servidor, protegiendo la confidencialidad e integridad de los datos y previniendo ataques MitM.

**7. Control de Versiones de la API:** Implementar un sistema de versionado de la API (en la URL, en las cabeceras) para permitir la evolución de la API sin romper la compatibilidad con los clientes existentes y para poder abordar problemas de seguridad en versiones específicas.

**8. Gestión de Errores:** Evitar la exposición de información sensible en los mensajes de error. Proporcionar mensajes genéricos al cliente y registrar los detalles de los errores en el servidor.

**9. Auditoría y Logging:** Registrar todas las peticiones a la API, incluyendo la identidad del cliente, la acción realizada, los datos accedidos y cualquier error o evento de seguridad relevante.

### Seguridad en APIs SOAP

Las APIs SOAP (Simple Object Access Protocol) son más complejas y se basan en el protocolo XML para el intercambio de mensajes. Tienen sus propios estándares de seguridad bien definidos.

**1. Autenticación y Autorización:**

   * **WS-Security:** Un conjunto de estándares que proporcionan mecanismos para asegurar mensajes SOAP, incluyendo autenticación, integridad y confidencialidad.
      * **UsernameToken:** Permite la autenticación mediante el envío de un nombre de usuario y una contraseña dentro del encabezado SOAP.
      * **SAML Tokens (Security Assertion Markup Language):** Permiten el intercambio de información de autenticación y autorización entre diferentes dominios de seguridad.
      * **X.509 Certificates:** Se pueden utilizar para la autenticación basada en certificados digitales.

   * **Implementación en Java (con Apache CXF, un framework popular para servicios web SOAP):** La configuración de WS-Security se realiza típicamente a través de archivos de configuración XML o mediante anotaciones en el código Java.

      * **Ejemplo Conceptual (fragmento de configuración de interceptores de entrada para UsernameToken con Apache CXF):**

```xml
<jaxws:endpoint id="myServiceEndpoint" implementor="#myServiceImpl" address="/MyService">
    <jaxws:inInterceptors>
        <bean class="org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor">
            <constructor-arg>
                <map>
                    <entry key="action" value="UsernameToken"/>
                    <entry key="passwordType" value="PasswordDigest"/>
                    <entry key="passwordCallbackClass" value="com.example.security.MyPasswordCallback"/>
                </map>
            </constructor-arg>
        </bean>
    </jaxws:inInterceptors>
    </jaxws:endpoint>
```

      Se necesitaría una implementación de `MyPasswordCallback` para verificar las credenciales del usuario.

**2. Integridad del Mensaje:**

   * **Firmas Digitales (WS-Security):** Utilizar firmas digitales basadas en certificados para asegurar que el mensaje SOAP no ha sido alterado en tránsito.

**3. Confidencialidad del Mensaje:**

   * **Cifrado (WS-Security):** Cifrar partes o la totalidad del mensaje SOAP para proteger la confidencialidad de la información sensible.

**4. Validación de Esquema XML:** Asegurarse de que los mensajes SOAP entrantes cumplen con el esquema WSDL (Web Services Description Language) definido para prevenir la manipulación de la estructura del mensaje.

**5. Protección contra Ataques XML:**

   * **XML External Entity (XXE) Attacks:** Deshabilitar el procesamiento de entidades externas en el parser XML.
   * **XML Bomb/DoS Attacks:** Establecer límites en el tamaño y la complejidad de los documentos XML para prevenir ataques de denegación de servicio basados en el procesamiento de XML malicioso.

**6. Seguridad en la Transferencia (HTTPS):** Al igual que con REST, utilizar HTTPS es altamente recomendado para proteger la comunicación SOAP a nivel de transporte.

**7. Auditoría y Logging:** Registrar las peticiones y respuestas SOAP, así como los eventos de seguridad relevantes.

**Consideraciones Adicionales para Ambos (REST y SOAP):**

* **Seguridad en el Servidor:** Asegurar el propio servidor donde se ejecutan los servicios (sistema operativo, servidor de aplicaciones, etc.) mediante hardening, parches de seguridad y controles de acceso.
* **Seguridad en las Comunicaciones Internas:** Si la capa de servicios se comunica con otras partes de la aplicación (por ejemplo, la capa de datos), asegurar también estas comunicaciones internas.
* **Pruebas de Seguridad:** Realizar pruebas de seguridad periódicas (pruebas de penetración, análisis de vulnerabilidades) para identificar y corregir posibles fallos de seguridad en la capa de servicios.
* **Principios de Mínimo Privilegio:** Otorgar solo los permisos necesarios a los clientes para realizar sus tareas.
* **Concienciación en Seguridad:** Educar a los desarrolladores sobre las mejores prácticas de seguridad para el desarrollo de APIs.

## Arquitectura de Seguridad Propuesta ##
 Vamos a esbozar una arquitectura de seguridad para una aplicación Struts 2 con un punto de entrada API REST gestionado por Servlets, donde ambos acceden a un servicio RESTful interno para el modelo de datos.

La idea principal es tener varias capas de seguridad, aplicando diferentes mecanismos en cada punto de interacción:

1. **Cliente Web Struts 2**: Seguridad centrada en la interacción del usuario a través del navegador.
2. **API REST (Servlets)**: Seguridad para las peticiones directas a la API.
3. **Servicio RESTful Interno**: Seguridad para el acceso al modelo de datos desde Struts 2 y los Servlets.

**Implementación de Seguridad por Capa:**

1. Cliente Web Struts 2:
   - Protección CSRF: Implementar tokens CSRF en todos los formularios gestionados por Struts 2. Esto generalmente se puede hacer a través de la configuración de Struts 2 y la inclusión de la etiqueta `<s:token>` en los formularios JSP.
     - Ejemplo en `struts.xml`: Asegurarse de que el interceptor token esté habilitado para las acciones que procesan formularios.
     - Ejemplo en JSP:
```java
<%@ taglib prefix="s" uri="/struts-tags" %>
<s:form action="miAccion">
    <s:token/>
    <s:textfield name="campo1" label="Campo 1"/>
    <s:submit value="Enviar"/>
</s:form>
```
   - Autenticación Basada en Sesión: Utilizar el mecanismo de gestión de sesiones de Struts 2 o la API de Servlets para autenticar a los usuarios que acceden a la interfaz web. Después de una autenticación exitosa (por ejemplo, mediante un formulario de login), almacenar la información del usuario en la sesión.
     - Ejemplo de un Interceptor de Struts 2:
```java
public class AuthorizationInterceptor extends AbstractInterceptor {
    private List<String> allowedRoles;

    public void setAllowedRoles(String roles) {
        this.allowedRoles = Arrays.asList(roles.split(","));
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpSession session = ServletActionContext.getRequest().getSession(false);
        if (session != null && session.getAttribute("usuarioAutenticado") != null) {
            String usuario = (String) session.getAttribute("usuarioAutenticado");
            // Obtener roles del usuario (podría estar en la sesión o consultarse al servicio interno)
            List<String> rolesUsuario = obtenerRolesUsuario(usuario);

            if (rolesUsuario.stream().anyMatch(allowedRoles::contains)) {
                return invocation.invoke();
            } else {
                HttpServletResponse response = ServletActionContext.getResponse();
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso no autorizado.");
                return Action.NONE;
            }
        } else {
            HttpServletResponse response = ServletActionContext.getResponse();
            response.sendRedirect(ServletActionContext.getRequest().getContextPath() + "/login.jsp");
            return Action.NONE;
        }
    }

    private List<String> obtenerRolesUsuario(String usuario) {
        // Llamada al servicio RESTful interno para obtener roles del usuario
        // ...
        return Arrays.asList("USER"); // Simulación
    }
}
```
**Configuración del Interceptor en `struts.xml`:**

```xml
<interceptors>
    <interceptor name="authInterceptor" class="com.example.security.AuthorizationInterceptor">
        <param name="allowedRoles">ADMIN</param>
    </interceptor>
    <interceptor-stack name="secureStack">
        <interceptor-ref name="defaultStack"/>
        <interceptor-ref name="authInterceptor"/>
    </interceptor-stack>
</interceptors>
<default-interceptor-ref name="secureStack"/>

    <action name="adminAction" class="com.example.AdminAction">
        <interceptor-ref name="secureStack">
            <param name="authInterceptor.allowedRoles">ADMIN</param>
        </interceptor-ref>
        <result>admin.jsp</result>
    </action>
```
   - **Autorización**: Implementar interceptores en Struts 2 para verificar si el usuario autenticado tiene los roles o permisos necesarios para acceder a ciertas acciones o páginas. La información de roles/permisos podría obtenerse del servicio RESTful interno al autenticar al usuario y almacenarse en la sesión.
   - **Validación de Entradas**: Utilizar el framework de validación de Struts 2 para asegurar que los datos enviados por el usuario cumplen con las reglas definidas antes de ser procesados o enviados al servicio interno.
   - **HTTPS**: Forzar el uso de HTTPS para toda la comunicación entre el navegador del usuario y el servidor Struts 2. Esto se puede configurar en el servidor de aplicaciones (Tomcat, etc.).
   - **Cookies Seguras**: Configurar las cookies de sesión como `HttpOnly` y `Secure` en el servidor de aplicaciones.
3. API REST (Servlets):
  - **Autenticación Basada en Tokens (JWT)**: Implementar un filtro de Servlet que intercepta las peticiones a la API REST y verifica la presencia y validez de un JWT en la cabecera `Authorization: Bearer <token>`.
  - **Autorización**: Después de autenticar al usuario a través del JWT, extraer la información de roles/permisos del token o consultar al servicio RESTful interno para determinar si el usuario tiene permiso para acceder al recurso solicitado. Implementar esta lógica en el filtro o en los propios Servlets de la API.
  - **Validación de Entradas**: Validar todos los datos recibidos en las peticiones a la API REST (por ejemplo, utilizando Bean Validation o validación manual) antes de procesarlos o enviarlos al servicio interno.
  - **Limitación de Tasas (Rate Limiting)**: Implementar un mecanismo para limitar el número de peticiones que un cliente de la API puede realizar en un período de tiempo. Esto se puede hacer con un filtro o una librería específica.
  - **Protección contra Inyecciones**: Asegurarse de que cualquier interacción con el servicio RESTful interno desde los Servlets de la API esté protegida contra inyecciones (si el servicio interno utiliza alguna forma de consulta basada en datos).
  - **HTTPS**: Forzar el uso de HTTPS para toda la comunicación con la API REST.
  - **Gestión de Errores**: Devolver códigos de estado HTTP apropiados y mensajes de error seguros y consistentes.
  - **Auditoría y Logging**: Registrar todas las peticiones a la API REST, incluyendo la identidad del cliente (si está disponible), la acción realizada y cualquier error.
4. Servicio RESTful Interno:
  - **Autenticación**: Este servicio debe autenticar las peticiones que recibe tanto desde Struts 2 como desde los Servlets de la API. Se pueden utilizar diferentes mecanismos dependiendo de cómo se realice la comunicación interna:
    - **Tokens Internos**: Struts 2 y los Servlets de la API podrían obtener un token interno después de autenticar al usuario externo y utilizar este token para comunicarse con el servicio interno.
    - **Autenticación Basada en Claves API Internas**: Si la comunicación es entre componentes de la misma aplicación, se podrían utilizar claves API internas para autenticar las peticiones.
    - **Confianza Basada en la Red**: Si el servicio interno solo es accesible desde la misma red del servidor, se podrían implementar reglas de firewall para restringir el acceso.
  - **Autorización**: El servicio interno debe verificar si el cliente (Struts 2 o el Servlet de la API, identificado por el mecanismo de autenticación interna) tiene permiso para acceder a los datos o realizar la acción solicitada. Esto podría basarse en roles o permisos asociados al usuario original (propagados a través del token interno) o en los permisos del propio componente que realiza la petición.
  - **Validación de Entradas**: Validar todos los datos recibidos antes de interactuar con el modelo de datos.
  - **Protección contra Inyecciones**: Si el servicio interno interactúa directamente con una base de datos u otro sistema de almacenamiento, debe implementar medidas robustas contra inyecciones (consultas parametrizadas, ORMs seguros, etc.).
  - **Seguridad en la Transferencia (Opcional)**: Si la comunicación interna viaja a través de una red no segura, se podría considerar el uso de TLS/SSL para cifrarla. Sin embargo, en un entorno de servidor controlado, esto podría no ser estrictamente necesario (aunque siempre es una buena práctica).
  - **Auditoría y Logging**: Registrar todas las peticiones al servicio interno, incluyendo el cliente que realizó la petición, la acción realizada y los datos accedidos.

**Flujo de Seguridad Típico (Cliente Web):**
1. El usuario intenta acceder a una página protegida en la aplicación Struts 2.
2. Un interceptor de Struts 2 verifica si el usuario está autenticado (sesión activa). Si no, se le redirige a la página de login.
3. El usuario introduce sus credenciales en el formulario de login (con token CSRF).
4. La Action de Struts 2 recibe las credenciales y las envía al servicio RESTful interno para la autenticación.
5. El servicio interno verifica las credenciales y, si son correctas, devuelve información del usuario (incluyendo roles/permisos).
6. La Action de Struts 2 almacena la información del usuario en la sesión y genera un token interno (opcional).
7. Para las siguientes peticiones que modifican datos, el formulario incluirá un token CSRF que será validado por Struts 2.
8. Cuando Struts 2 necesita acceder al modelo de datos, realiza una petición al servicio RESTful interno, posiblemente incluyendo el token interno para la autenticación y para que el servicio interno pueda realizar la autorización basada en los roles del usuario.

**Flujo de Seguridad Típico (Cliente API):**
1. Una aplicación cliente realiza una petición a un endpoint protegido de la API REST (Servlet).
2. Un filtro de Servlet intercepta la petición y busca un JWT en la cabecera `Authorization`.
3. El filtro verifica la firma y validez del JWT.
4. Si el token es válido, el filtro puede extraer la identidad del usuario y sus roles/permisos (o consultar al servicio interno).
5. El Servlet de la API recibe la petición autenticada y autorizada.
6. Si el Servlet necesita acceder al modelo de datos, realiza una petición al servicio RESTful interno, posiblemente incluyendo el JWT o un token interno derivado de él para la autenticación y autorización en el servicio interno.
7. El servicio interno verifica el token y los permisos antes de acceder a los datos.
