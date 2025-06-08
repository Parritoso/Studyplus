<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <%--<title>Función No Implementada - Estudy+</title>
        <style>
            body { font-family: Arial, sans-serif; line-height: 1.6; margin: 0; padding: 0; background-color: #f4f4f4; color: #333; }
            .navbar { background-color: #333; color: white; padding: 10px 0; text-align: center; }
            .navbar a { color: white; text-decoration: none; padding: 0 15px; }
            .navbar a:hover { text-decoration: underline; }
            .container { max-width: 800px; margin: auto; padding: 50px 20px; background-color: #fff; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); text-align: center; margin-top: 50px; }
            h2 { color: #dc3545; margin-bottom: 20px; }
            p { margin-bottom: 25px; }
            .btn-back { background-color: #007bff; color: white; padding: 10px 20px; border-radius: 5px; text-decoration: none; font-weight: bold; }
            .btn-back:hover { background-color: #0056b3; }
            footer { text-align: center; padding: 20px; margin-top: 30px; background-color: #333; color: white; }
        </style>
    </head>
    <body>
        <%@ include file="/jsp/common/navbar.jsp" %>

    <div class="container">
        <h2>Función No Implementada Aún</h2>
        <p>¡Disculpa las molestias! Esta funcionalidad está en desarrollo y estará disponible muy pronto.</p>
        <p>Gracias por tu paciencia.</p>
        <s:url action="dashboard" var="dashboardUrl"/>
        <s:a href="%{dashboardUrl}" cssClass="btn-back">Volver al Dashboard</s:a>
    </div>

    <footer>
        &copy; 2025 Estudy+. Todos los derechos reservados.
    </footer>
</body>--%>
        <title><s:text name="notImplemented.title"/></title>
        <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/feature_not_implemented.css">
        <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/feature_not_implemented.css" disabled>
    </head>
    <body>
        <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
        <%@ include file="/jsp/common/navbar.jsp" %>

        <div class="container">
            <%-- Encabezado principal --%>
            <h2><s:text name="notImplemented.heading"/></h2>
            <%-- Párrafos de mensaje --%>
            <p><s:text name="notImplemented.message1"/></p>
            <p><s:text name="notImplemented.message2"/></p>
            <s:url action="dashboard" var="dashboardUrl"/>
            <%-- Texto del botón --%>
            <s:a href="%{dashboardUrl}" cssClass="btn-back"><s:text name="notImplemented.backToDashboard"/></s:a>
            </div>

            <footer>
            <%-- Texto del pie de página --%>
            <s:text name="footer.copyright"/>
        </footer>
    </body>
</html>