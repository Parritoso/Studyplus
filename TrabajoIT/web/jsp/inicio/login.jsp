<%-- 
    Document   : login
    Created on : 28-may-2025, 15:09:26
    Author     : Parri
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Iniciar Sesión - Estudy+</title>
        <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/login.css">
        <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/login.css" disabled>
    </head>
    <body>
        <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
        <%@ include file="../common/navbar.jsp" %>

        <%--<div class="container">
            <h2>Iniciar Sesión</h2>

            <%-- Mostrar mensajes de feedback (ej. "Nombre de usuario o contraseña incorrectos.") 
            <s:if test="feedbackMessage != null && !feedbackMessage.isEmpty()">
                <p class="feedback-message"><s:property value="feedbackMessage"/></p>
            </s:if>

            <%-- Mostrar errores de acción generales (ej. errores de servicio) 
            <s:actionerror cssClass="action-error-list"/>

            <s:form action="login" method="post">
                <div class="form-group">
                    <label for="username">Nombre de Usuario / Email:</label>
                    <s:textfield name="username" cssClass="form-control" placeholder="Tu nombre de usuario o email"/>
                    <s:fielderror fieldName="username" cssClass="error-message"/>
                </div>
                <div class="form-group">
                    <label for="password">Contraseña:</label>
                    <s:password name="password" id="password" cssClass="form-control" placeholder="Tu contraseña"/>
                    <span id="togglePassword" class="toggle-password">Mostrar</span>
                    <s:fielderror fieldName="password" cssClass="error-message"/>
                </div>
                <div class="form-group">
                    <s:submit value="Entrar" cssClass="btn"/>
                </div>
            </s:form>
            <p class="links-auth">¿No tienes cuenta? <s:a href="%{registerUrl}">Regístrate aquí</s:a></p>
        </div>

        <footer>
            &copy; 2025 Estudy+. Todos los derechos reservados.
        </footer>
        <script>
            // Asegúrate de que el DOM esté cargado antes de intentar acceder a los elementos
            document.addEventListener('DOMContentLoaded', function() {
                const passwordInput = document.getElementById('password');
                const togglePassword = document.getElementById('togglePassword');

                if (passwordInput && togglePassword) {
                    togglePassword.addEventListener('click', function() {
                        // Cambia el tipo de input entre 'password' y 'text'
                        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
                        passwordInput.setAttribute('type', type);

                        // Cambia el texto del botón
                        this.textContent = type === 'password' ? 'Mostrar' : 'Ocultar';
                    });
                }
            });
        </script>--%>
            <div class="container">
            <%-- Main heading --%>
            <h2><s:text name="login.heading"/></h2>

            <%-- Display feedback messages (e.g., "Invalid username or password.") --%>
            <s:if test="feedbackMessage != null && !feedbackMessage.isEmpty()">
                <p class="feedback-message"><s:property value="feedbackMessage"/></p>
            </s:if>

            <%-- Display general action errors (e.g., service errors) --%>
            <s:actionerror cssClass="action-error-list"/>

            <s:form action="login" method="post">
                <div class="form-group">
                    <label for="username"><s:text name="login.usernameLabel"/></label>
                    <s:textfield name="username" cssClass="form-control" placeholder="%{getText('login.usernamePlaceholder')}"/>
                    <s:fielderror fieldName="username" cssClass="error-message"/>
                </div>
                <div class="form-group password-input-group"> <%-- Added password-input-group class for consistent styling --%>
                    <label for="password"><s:text name="login.passwordLabel"/></label>
                    <s:password name="password" id="password" cssClass="form-control" placeholder="%{getText('login.passwordPlaceholder')}"/>
                    <span id="togglePassword" class="toggle-password"><s:text name="login.showPassword"/></span>
                    <s:fielderror fieldName="password" cssClass="error-message"/>
                </div>
                <div class="form-group">
                    <s:submit value="%{getText('login.submitButton')}" cssClass="btn"/>
                </div>
            </s:form>
            <p class="links-auth"><s:text name="login.noAccount"/> <s:url action="register" var="registerUrl"/><s:a href="%{registerUrl}"><s:text name="login.registerLink"/></s:a></p>
        </div>

        <footer>
            <%-- Footer text --%>
            <s:text name="footer.copyright"/>
        </footer>
        <script>
            // Asegúrate de que el DOM esté cargado antes de intentar acceder a los elementos
            document.addEventListener('DOMContentLoaded', function() {
                const passwordInput = document.getElementById('password');
                const togglePassword = document.getElementById('togglePassword');

                if (passwordInput && togglePassword) {
                    togglePassword.addEventListener('click', function() {
                        // Cambia el tipo de input entre 'password' y 'text'
                        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
                        passwordInput.setAttribute('type', type);

                        // Cambia el texto del botón usando las claves de internacionalización
                        // This relies on the Struts2 text tags being processed before the JS runs,
                        // or you could dynamically set the text using a data attribute on the span.
                        // For simplicity, we'll assume the text tags render correctly.
                        this.textContent = type === 'password' ? '<s:text name="login.showPassword"/>' : '<s:text name="login.hidePassword"/>';
                    });
                }
            });
        </script>
    </body>
</html>
