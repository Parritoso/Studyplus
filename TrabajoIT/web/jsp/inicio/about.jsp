<%-- 
    Document   : about
    Created on : 28-may-2025, 15:06:50
    Author     : Parri
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Acerca de Nosotros - Estudy+</title>
        <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/about.css">
        <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/about.css" disabled>
    </head>
    <body>
        <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
        <%@ include file="../common/navbar.jsp" %>
        
    <%--<div class="container">
        <h2>Acerca de Estudy+</h2>
        <p>
            Estudy+ nace de la visión de transformar la experiencia de estudio en la era digital. Creemos que la educación
            es el pilar del futuro y que cada estudiante merece herramientas que optimicen su proceso de aprendizaje.
        </p>
        <p>
            Nuestro equipo está compuesto por desarrolladores, educadores y entusiastas de la tecnología,
            todos comprometidos con la creación de una plataforma intuitiva, potente y, sobre todo, inteligente.
            Combinamos metodologías probadas de estudio con inteligencia artificial para ofrecer una experiencia
            única y personalizada.
        </p>
        <p>
            Nuestra misión es empoderar a estudiantes de todo el mundo, ayudándoles a alcanzar sus metas académicas
            y profesionales de una manera más eficiente, saludable y gratificante.
        </p>
        <p>¡Gracias por ser parte de la comunidad Estudy+!</p>
    </div>

    <footer>
        &copy; 2025 Estudy+. Todos los derechos reservados.
    </footer>--%>
     <div class="container">
        <%-- Main heading --%>
        <h2><s:text name="about.heading"/></h2>
        <%-- Paragraphs --%>
        <p>
            <s:text name="about.paragraph1"/>
        </p>
        <p>
            <s:text name="about.paragraph2"/>
        </p>
        <p>
            <s:text name="about.paragraph3"/>
        </p>
        <p><s:text name="about.paragraph4"/></p>
    </div>

    <footer>
        <%-- Footer text --%>
        <s:text name="footer.copyright"/>
    </footer>
    </body>
</html>
