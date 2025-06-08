<%-- 
    Document   : welcome
    Created on : 28-may-2025, 15:05:35
    Author     : Parri
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bienvenido a Estudy+ - Gestor de Estudio Inteligente</title>
        <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/welcome.css">
        <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/welcome.css" disabled>
    </head>
    <body>
        <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
        <%@ include file="../common/navbar.jsp" %>

        <%--
        <div class="hero">
            <h1>Bienvenido a Estudy+</h1>
            <p>Tu Gestor Inteligente de Tiempos de Estudio y Productividad.</p>
            <p>Optimiza tus sesiones, gestiona tus entregas y mejora tu rendimiento académico.</p>
        </div>

        <div class="container">
            <h2>¿Qué es Estudy+?</h2>
            <p>
                Estudy+ es una aplicación web diseñada para estudiantes que buscan maximizar su eficiencia y productividad.
                Ofrecemos herramientas intuitivas para organizar tu tiempo de estudio, programar descansos efectivos,
                y llevar un control detallado de tus entregas y exámenes.
            </p>
            <p>
                Pero Estudy+ va más allá. Nuestra API inteligente analiza tu rendimiento histórico para ofrecerte
                recomendaciones personalizadas sobre técnicas de estudio óptimas, horarios ideales y detección
                temprana de fatiga, ayudándote a evitar el agotamiento y mantenerte motivado.
            </p>

            <div class="button-group">
                <s:a href="%{registerUrl}">¡Regístrate Ahora!</s:a>
                <s:a href="%{featuresUrl}">Descubre más características</s:a>
            </div>
        </div>

        <footer>
            &copy; 2025 Estudy+. Todos los derechos reservados.
        </footer>
        
        <div class="theme-switcher-controls">
            <button id="toggleThemeBtn" class="btn">Alternar Modo Oscuro/Claro</button>
            <label for="autoThemeToggle">
                <input type="checkbox" id="autoThemeToggle"> Activar Modo Día/Noche Automático
            </label>
        </div>--%>
        <div class="hero">
            <%-- Encabezado principal --%>
            <h1><s:text name="welcome.hero.heading"/></h1>
            <%-- Subtítulos del héroe --%>
            <p><s:text name="welcome.hero.tagline1"/></p>
            <p><s:text name="welcome.hero.tagline2"/></p>
        </div>

        <div class="container">
            <%-- Sección "¿Qué es Estudy+?" --%>
            <h2><s:text name="welcome.whatis.heading"/></h2>
            <p>
                <s:text name="welcome.whatis.paragraph1"/>
            </p>
            <p>
                <s:text name="welcome.whatis.paragraph2"/>
            </p>

            <div class="button-group">
                <%-- Botones de acción --%>
                <s:url action="register" var="registerUrl"/> <%-- Asume que tienes una acción 'register' --%>
                <s:a href="%{registerUrl}"><s:text name="welcome.registerButton"/></s:a>

                <s:url action="features" var="featuresUrl"/> <%-- Asume que tienes una acción 'features' --%>
                <s:a href="%{featuresUrl}"><s:text name="welcome.featuresButton"/></s:a>
                </div>
            </div>

            <footer>
            <%-- Texto del pie de página --%>
            <s:text name="footer.copyright"/>
        </footer>
    </body>
</html>
