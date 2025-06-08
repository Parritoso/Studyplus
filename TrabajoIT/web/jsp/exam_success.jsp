<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Examen Creado con Éxito - Estudy+</title>
        <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light_mode/exam_success.css">
        <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/exam_success.css" disabled>
    </head>
    <body>
        <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
        <%@ include file="/jsp/common/navbar.jsp" %>

        <div class="container">
            <h2><s:property value="feedbackMessage"/></h2>
            <p>Tu examen ha sido registrado exitosamente. Aquí tienes los detalles:</p>

            <div class="details-box">
                <p><strong>Nombre:</strong> <s:property value="nombre"/></p>
                <s:if test="descripcion != null && !descripcion.isEmpty()">
                    <p><strong>Descripción:</strong> <s:property value="descripcion"/></p>
                </s:if>
                <p><strong>Asignatura:</strong> <s:property value="asignatura"/></p>
                <p><strong>Fecha del Examen:</strong> <s:property value="fechaExamenStr"/></p>
                <p><strong>Tipo de Examen:</strong> <s:property value="tipoExamen"/></p>
                <s:if test="prioridad != null && !prioridad.isEmpty()">
                    <p><strong>Prioridad:</strong> <s:property value="prioridad"/></p>
                </s:if>
                <p><strong>Recordatorio Activo:</strong> <s:property value="recordatorioActivo ? 'Sí' : 'No'"/></p>
                <s:if test="recordatorioActivo && fechaRecordatorioStr != null && !fechaRecordatorioStr.isEmpty()">
                    <p><strong>Fecha Recordatorio:</strong> <s:property value="fechaRecordatorioStr"/></p>
                </s:if>
                <p><strong>ID de Examen:</strong> <s:property value="createdExamId"/></p>
            </div>

            <p class="countdown">Serás redirigido al Dashboard en <span id="countdown">5</span> segundos...</p>

            <s:url action="dashboard" var="dashboardUrl"/>
            <s:a href="%{dashboardUrl}" cssClass="btn-dashboard">Ir al Dashboard Ahora</s:a>
            </div>

            <script type="text/javascript">
                document.addEventListener("DOMContentLoaded", () => {
                    let seconds = 5;

                    const countdownElement = document.getElementById("countdown");
                    const dashboardUrl = '<s:property value="dashboardUrl"/>'; // Asegúrate de que esta variable sea segura

                    if (!countdownElement) {
                        console.error("Elemento con ID 'countdown' no encontrado.");
                        return;
                    }

                    const countdown = () => {
                        if (seconds <= 0) {
                            window.location.href = dashboardUrl;
                        } else {
                            countdownElement.textContent = seconds;
                            seconds--;
                            setTimeout(countdown, 1000);
                        }
                    };

                    countdown();
                });
        </script>

        <footer>
            &copy; 2025 Estudy+. Todos los derechos reservados.
        </footer>
    </body>
</html>
