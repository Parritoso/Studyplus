<%-- 
    Document   : contact
    Created on : 28-may-2025, 15:07:00
    Author     : Parri
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Contacto - Estudy+</title>
        <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/contact.css">
        <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/contact.css" disabled>
    </head>
    <body>
        <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
        <%@ include file="../common/navbar.jsp" %>
        <%--<div class="container">
        <h2>Contacto</h2>
        <p>Estamos aquí para ayudarte. Si tienes alguna pregunta, sugerencia o necesitas soporte, no dudes en contactarnos.</p>

        <div class="contact-info">
            <p><strong>Email:</strong> support@estudyplus.com</p>
            <p><strong>Teléfono:</strong> +34 123 456 789</p>
            <p><strong>Dirección:</strong> Calle Ficticia 123, Sevilla, España</p>
            <p><strong>Horario:</strong> Lunes a Viernes, 9:00 - 18:00 (CET)</p>
        </div>

        <h3>Síguenos en Redes Sociales:</h3>
        <p>
            <a href="#">Facebook</a> |
            <a href="#">Twitter</a> |
            <a href="#">LinkedIn</a> |
            <a href="#">Instagram</a>
        </p>

        <%-- Opcional: un formulario de contacto más complejo --%>
        <%--
        <h3>Envíanos un Mensaje</h3>
        <s:form action="submitContactForm" method="post">
            <div class="form-group">
                <label for="name">Tu Nombre:</label>
                <s:textfield name="name" cssClass="form-control"/>
            </div>
            <div class="form-group">
                <label for="email">Tu Email:</label>
                <s:textfield name="email" cssClass="form-control" type="email"/>
            </div>
            <div class="form-group">
                <label for="subject">Asunto:</label>
                <s:textfield name="subject" cssClass="form-control"/>
            </div>
            <div class="form-group">
                <label for="message">Mensaje:</label>
                <s:textarea name="message" cssClass="form-control"/>
            </div>
            <s:submit value="Enviar Mensaje" cssClass="btn"/>
        </s:form>
       
    </div>

    <footer>
        &copy; 2025 Estudy+. Todos los derechos reservados.
    </footer>--%>
        <div class="container">
            <%-- Encabezado principal --%>
            <h2><s:text name="contact.heading"/></h2>
            <%-- Mensaje introductorio --%>
            <p><s:text name="contact.introMessage"/></p>

            <div class="contact-info">
                <%-- Información de contacto --%>
                <p><strong><s:text name="contact.emailLabel"/></strong> <s:text name="contact.emailValue"/></p>
                <p><strong><s:text name="contact.phoneLabel"/></strong> <s:text name="contact.phoneValue"/></p>
                <p><strong><s:text name="contact.addressLabel"/></strong> <s:text name="contact.addressValue"/></p>
                <p><strong><s:text name="contact.hoursLabel"/></strong> <s:text name="contact.hoursValue"/></p>
            </div>

            <%-- Redes Sociales --%>
            <h3><s:text name="contact.socialHeading"/></h3>
            <p>
                <a href="#">Facebook</a> |
                <a href="#">Twitter</a> |
                <a href="#">LinkedIn</a> |
                <a href="#">Instagram</a>
            </p>

            <%-- Opcional: un formulario de contacto más complejo --%>
            <%--
            <h3>Envíanos un Mensaje</h3>
            <s:form action="submitContactForm" method="post">
                <div class="form-group">
                    <label for="name">Tu Nombre:</label>
                    <s:textfield name="name" cssClass="form-control"/>
                </div>
                <div class="form-group">
                    <label for="email">Tu Email:</label>
                    <s:textfield name="email" cssClass="form-control" type="email"/>
                </div>
                <div class="form-group">
                    <label for="subject">Asunto:</label>
                    <s:textfield name="subject" cssClass="form-control"/>
                </div>
                <div class="form-group">
                    <label for="message">Mensaje:</label>
                    <s:textarea name="message" cssClass="form-control"/>
                </div>
                <s:submit value="Enviar Mensaje" cssClass="btn"/>
            </s:form>
            --%>
        </div>

        <footer>
            <%-- Texto del pie de página --%>
            <s:text name="footer.copyright"/>
        </footer>
    </body>
</html>
