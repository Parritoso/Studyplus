<%-- 
    Document   : features
    Created on : 28-may-2025, 15:06:40
    Author     : Parri
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Características - Estudy+</title>
        <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/features.css">
        <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/features.css" disabled>
    </head>
    <body>
        <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
        <%@ include file="../common/navbar.jsp" %>

        <%--<div class="container">
            <h2>Características Principales de Estudy+</h2>
            <ul>
                <li>Gestión de Sesiones de Estudio (Pomodoro, 90/20, etc.)</li>
                <li>Registro y Recordatorios de Entregas y Exámenes</li>
                <li>Gráficas y Dashboard de Productividad Personal</li>
                <li>Gamificación (Puntos, Medallas, Logros)</li>
                <li>Recomendaciones de Estudio Personalizadas (Basadas en IA)</li>
                <li>Detección de Fatiga y Prevención de Burnout</li>
                <li>Estudio Grupal y Eventos Colaborativos</li>
                <li>Notas Rápidas y Checklists por Sesión</li>
                <li>Modo Focus con Música de Ambiente</li>
                <li>Soporte de Accesibilidad y Modo Oscuro</li>
            </ul>
            <p>¡Y muchas más por venir!</p>
        </div>

        <footer>
            &copy; 2025 Estudy+. Todos los derechos reservados.
        </footer>--%>
        <div class="container">
            <%-- Encabezado principal --%>
            <h2><s:text name="features.heading"/></h2>
            <ul>
                <%-- Lista de características --%>
                <li><s:text name="features.item1"/></li>
                <li><s:text name="features.item2"/></li>
                <li><s:text name="features.item3"/></li>
                <li><s:text name="features.item4"/></li>
                <li><s:text name="features.item5"/></li>
                <li><s:text name="features.item6"/></li>
                <li><s:text name="features.item7"/></li>
                <li><s:text name="features.item8"/></li>
                <li><s:text name="features.item9"/></li>
                <li><s:text name="features.item10"/></li>
            </ul>
            <%-- Mensaje adicional --%>
            <p><s:text name="features.moreToCome"/></p>
        </div>

        <footer>
            <%-- Texto del pie de página --%>
            <s:text name="footer.copyright"/>
        </footer>
    </body>
</html>
