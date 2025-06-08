<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Crear Nuevo Examen - Estudy+</title>
        <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/createExam.css">
        <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/createExam.css" disabled>
        <script>
            function toggleReminderDate() {
                var checkbox = document.getElementById('recordatorioActivoCheckbox');
                var dateField = document.getElementById('fechaRecordatorioField');
                if (checkbox.checked) {
                    dateField.style.display = 'block';
                } else {
                    dateField.style.display = 'none';
                }
            }
            // Llamar en la carga inicial para establecer el estado correcto si hay valores previos
            window.onload = toggleReminderDate;
        </script>
    </head>
    <body>
        <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
        <%@ include file="/jsp/common/navbar.jsp" %>

        <div class="container">
            <h2>Crear Nuevo Examen</h2>

            <s:if test="feedbackMessage != null && !feedbackMessage.isEmpty()">
                <p class="feedback-message"><s:property value="feedbackMessage"/></p>
            </s:if>
            <s:actionerror cssClass="action-error-list_var1"/>

            <s:form action="createExam" method="post">
                <div class="form-group">
                    <label for="nombre">Nombre del Examen:</label>
                    <s:textfield name="nombre" cssClass="form-control" placeholder="Ej: Examen de Matemáticas"/>
                    <s:fielderror fieldName="nombre" cssClass="error-message"/>
                </div>
                <div class="form-group">
                    <label for="descripcion">Descripción (Opcional):</label>
                    <s:textarea name="descripcion" cssClass="form-control" rows="3" placeholder="Detalles del examen..."/>
                </div>
                <div class="form-group">
                    <label for="fechaExamenStr">Fecha del Examen:</label>
                    <s:textfield name="fechaExamenStr" type="date" cssClass="form-control"/>
                    <s:fielderror fieldName="fechaExamenStr" cssClass="error-message"/>
                </div>
                <div class="form-group">
                    <label for="asignatura">Asignatura:</label>
                    <s:textfield name="asignatura" cssClass="form-control" placeholder="Ej: Matemáticas, Historia"/>
                    <s:fielderror fieldName="asignatura" cssClass="error-message"/>
                </div>
                <div class="form-group">
                    <label for="tipoExamen">Tipo de Examen:</label>
                    <s:select name="tipoExamen" cssClass="form-control"
                              list="{'Parcial', 'Final', 'Recuperación', 'Prueba Corta', 'Oral', 'Otro'}"
                              headerKey="" headerValue="-- Selecciona Tipo --"/>
                    <s:fielderror fieldName="tipoExamen" cssClass="error-message"/>
                </div>
                <div class="form-group">
                    <label for="prioridad">Prioridad:</label>
                    <s:select name="prioridad" cssClass="form-control"
                              list="{'Baja', 'Media', 'Alta', 'Urgente'}"
                              headerKey="" headerValue="-- Selecciona Prioridad --"/>
                </div>
                <div class="form-group">
                    <s:checkbox name="recordatorioActivo" id="recordatorioActivoCheckbox" 
                                label="Activar Recordatorio" fieldValue="true"
                                onclick="toggleReminderDate()"/>
                </div>
                <div class="form-group" id="fechaRecordatorioField" style="display:none;">
                    <label for="fechaRecordatorioStr">Fecha del Recordatorio:</label>
                    <s:textfield name="fechaRecordatorioStr" type="date" cssClass="form-control"/>
                    <s:fielderror fieldName="fechaRecordatorioStr" cssClass="error-message"/>
                </div>
                <div class="form-group">
                    <s:submit value="Crear Examen" cssClass="btn"/>
                </div>
            </s:form>
        </div>

        <footer>
            &copy; 2025 Estudy+. Todos los derechos reservados.
        </footer>
    </body>
</html>
