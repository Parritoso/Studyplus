<%-- 
    Document   : createDelivery
    Created on : 26-may-2025, 15:14:24
    Author     : Parri
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Crear Nueva Entrega - Estudy+</title>
        <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/createDelivery.css">
        <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/createDelivery.css" disabled>
    </head>
    <body>
        <div class="container">
            <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
            <h2>Crear Nueva Entrega</h2>

            <%-- Mostrar mensajes de feedback (éxito o error) --%>
            <s:if test="feedbackMessage != null && !feedbackMessage.isEmpty()">
                <p class="feedback-message"><s:property value="feedbackMessage"/></p>
            </s:if>

            <%-- Formulario de creación de entrega --%>
            <s:form action="createDelivery" method="post">
                <div class="form-group">
                    <label for="titulo">Título:</label>
                    <s:textfield name="titulo" cssClass="form-control" placeholder="Ej: Tarea de Programación Orientada a Objetos"/>
                    <%-- Muestra errores de validación de Struts2 para el campo 'titulo' --%>
                    <s:fielderror fieldName="titulo" cssClass="error-message"/>
                </div>

                <div class="form-group">
                    <label for="descripcion">Descripción (opcional):</label>
                    <s:textarea name="descripcion" cssClass="form-control" placeholder="Detalles de la entrega..."/>
                </div>

                <div class="form-group">
                    <label for="fechaLimiteStr">Fecha Límite:</label>
                    <s:textfield name="fechaLimiteStr" type="date" cssClass="form-control"/> <%-- type="date" para un selector de fecha HTML5 --%>
                    <s:fielderror fieldName="fechaLimiteStr" cssClass="error-message"/>
                </div>

                <div class="form-group">
                    <label for="asignatura">Asignatura:</label>
                    <s:textfield name="asignatura" cssClass="form-control" placeholder="Ej: Programación Avanzada"/>
                    <s:fielderror fieldName="asignatura" cssClass="error-message"/>
                </div>

                <div class="form-group">
                    <label for="prioridad">Prioridad:</label>
                    <s:select name="prioridad" cssClass="form-control"
                              list="{'Baja', 'Normal', 'Alta', 'Urgente'}"
                              headerKey="" headerValue="-- Seleccionar Prioridad --"/>
                </div>

                <%-- El usuarioId se manejará en la acción, no se expone en el formulario --%>
                <%-- <s:hidden name="usuarioId" value="1"/> --%>

                <div class="form-group">
                    <s:submit value="Guardar Entrega" cssClass="btn"/>
                </div>
            </s:form>

            <p style="text-align: center; margin-top: 20px;">
                <a href="<s:url action="viewUpcomingTasks"/>">Volver al listado de tareas</a>
            </p>
        </div>
    </body>
</html>
