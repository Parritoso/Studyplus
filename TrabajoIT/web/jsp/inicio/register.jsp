<%-- 
    Document   : register
    Created on : 28-may-2025, 16:00:38
    Author     : Parri
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Registro - Estudy+</title>
        <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/register.css">
        <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/register.css" disabled>
    </head>
    <body>
        <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
        <%@ include file="../common/navbar.jsp" %>
        <%--<div class="container">
        <h2>Crear Nueva Cuenta</h2>

        <s:if test="feedbackMessage != null && !feedbackMessage.isEmpty()">
            <p class="feedback-message"><s:property value="feedbackMessage"/></p>
        </s:if>

        <s:form action="register" method="post">
            <div class="form-group">
                <label for="username">Nombre de Usuario:</label>
                <s:textfield name="username" cssClass="form-control" placeholder="Elige un nombre de usuario"/>
                <s:fielderror fieldName="username" cssClass="error-message"/>
            </div>
            <div class="form-group">
                <label for="email">Email:</label>
                <s:textfield name="email" cssClass="form-control" type="email" placeholder="tu.email@example.com"/>
                <s:fielderror fieldName="email" cssClass="error-message"/>
            </div>
            <div class="form-group">
                <label for="password">Contraseña:</label>
                <s:password name="password" cssClass="form-control" placeholder="Crea tu contraseña"/>
                <s:fielderror fieldName="password" cssClass="error-message"/>
            </div>
            <div class="form-group">
                <label for="confirmPassword">Confirmar Contraseña:</label>
                <s:password name="confirmPassword" cssClass="form-control" placeholder="Repite tu contraseña"/>
                <s:fielderror fieldName="confirmPassword" cssClass="error-message"/>
            </div>
            <div class="form-group">
                <s:submit value="Registrarme" cssClass="btn"/>
            </div>
        </s:form>
        <p class="links-auth">¿Ya tienes cuenta? <s:a href="%{loginUrl}">Inicia Sesión aquí</s:a></p>
    </div>

    <footer>
        &copy; 2025 Estudy+. Todos los derechos reservados.
    </footer>--%>
        <div class="container">
        <%-- Encabezado principal --%>
        <h2><s:text name="register.heading"/></h2>

        <%-- Mostrar mensajes de feedback --%>
        <s:if test="feedbackMessage != null && !feedbackMessage.isEmpty()">
            <p class="feedback-message"><s:property value="feedbackMessage"/></p>
        </s:if>

        <s:form action="register" method="post">
            <div class="form-group">
                <label for="username"><s:text name="register.usernameLabel"/></label>
                <s:textfield name="username" cssClass="form-control" placeholder="%{getText('register.usernamePlaceholder')}"/>
                <s:fielderror fieldName="username" cssClass="error-message"/>
            </div>
            <div class="form-grou">
                <label for="email"><s:text name="register.emailLabel"/></label>
                <s:textfield name="email" cssClass="form-control" type="email" placeholder="%{getText('register.emailPlaceholder')}"/>
                <s:fielderror fieldName="email" cssClass="error-message"/>
            </div>
            <div class="form-group">
                <label for="password"><s:text name="register.passwordLabel"/></label>
                <s:password name="password" cssClass="form-control" placeholder="%{getText('register.passwordPlaceholder')}"/>
                <s:fielderror fieldName="password" cssClass="error-message"/>
            </div>
            <div class="form-group">
                <label for="confirmPassword"><s:text name="register.confirmPasswordLabel"/></label>
                <s:password name="confirmPassword" cssClass="form-control" placeholder="%{getText('register.confirmPasswordPlaceholder')}"/>
                <s:fielderror fieldName="confirmPassword" cssClass="error-message"/>
            </div>
            <div class="form-group">
                <s:submit value="%{getText('register.submitButton')}" cssClass="btn"/>
            </div>
        </s:form>
        <p class="links-auth"><s:text name="register.alreadyHaveAccount"/> <s:url action="login" var="loginUrl"/><s:a href="%{loginUrl}"><s:text name="register.loginLink"/></s:a></p>
    </div>

    <footer>
        <%-- Texto del pie de página --%>
        <s:text name="footer.copyright"/>
    </footer>
    </body>
</html>
