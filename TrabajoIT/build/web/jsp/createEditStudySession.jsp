<%-- 
    Document   : createEditStudySession
    Created on : 01-jun-2025, 13:19:34
    Author     : Parri
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>
            <s:if test="sesion != null && sesion.id != null">Editar Sesión de Estudio</s:if>
            <s:else>Crear Nueva Sesión de Estudio</s:else>
            - Estudy+
        </title>
        <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/createEditStudySession.css">
        <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/createEditStudySession.css" disabled>
    </head>
    <body>
        <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
        <%@ include file="/jsp/common/navbar.jsp" %>

        <div class="container">
            <h2>
                <s:if test="sesion != null && sesion.id != null">Editar Sesión de Estudio</s:if>
                <s:else>Programar/Iniciar Nueva Sesión de Estudio</s:else>
            </h2>

            <s:if test="hasActionMessages()">
                <div class="alert alert-success">
                    <s:actionmessage/>
                </div>
            </s:if>
            <s:if test="hasActionErrors()">
                <div class="alert alert-danger action-error-list">
                    <s:actionerror/>
                </div>
            </s:if>
            <s:if test="hasFieldErrors()">
                <div class="alert alert-danger">
                    <s:fielderror cssClass="error-message" />
                </div>
            </s:if>

            <s:form action="%{sesion != null && sesion.id != null ? 'updateStudySession' : 'createStudySession'}" method="post">
                <s:hidden name="sesion.id"/> <%-- Necesario para edición --%>

                <div class="form-group">
                    <label for="sesion_titulo">Título de la Sesión:<span style="color: red;">*</span></label>
                    <s:textfield name="sesion.titulo" cssClass="form-control" placeholder="Ej: Estudio para Examen de Matemáticas" required="true"/>
                </div>

                <div class="form-group">
                    <label for="sesion_descripcion">Descripción:</label>
                    <s:textarea name="sesion.descripcion" cssClass="form-control" placeholder="Notas sobre los temas a cubrir o objetivos."/>
                </div>

                <div class="form-group">
                    <label for="sesion_duracionPlanificadaMinutos">Duración Planificada (minutos):<span style="color: red;">*</span></label>
                    <s:textfield name="sesion.duracionPlanificadaMinutos" type="number" cssClass="form-control" placeholder="Ej: 60" required="true"/>
                </div>

                <div class="form-group">
                    <label for="sesion_tecnicaAplicadaId_id">Técnica de Estudio Aplicada:</label>
                    <s:select name="sesion.tecnicaAplicadaId.id" list="tecnicasDisponibles" listKey="id" listValue="nombre"
                              headerKey="0" headerValue="-- Seleccionar Técnica --" cssClass="form-control"/>
                    <small>Puedes asociar una técnica para seguir su progreso.</small>
                </div>

                <div class="form-group">
                    <label for="sesion_entregaAsociada_id">Asociar a Entrega:</label>
                    <s:select name="sesion.entregaAsociada.id" list="entregasDisponibles" listKey="id" listValue="titulo"
                              headerKey="0" headerValue="-- Sin Entrega --" cssClass="form-control"/>
                    <small>Relaciona esta sesión con una entrega específica.</small>
                </div>

                <div class="form-group">
                    <label for="sesion_examenAsociado_id">Asociar a Examen:</label>
                    <s:select name="sesion.examenAsociado.id" list="examenesDisponibles" listKey="id" listValue="nombre"
                              headerKey="0" headerValue="-- Sin Examen --" cssClass="form-control"/>
                    <small>Relaciona esta sesión con un examen.</small>
                </div>

                <%-- Solo mostrar estas opciones en el modo CREAR --%>
                <s:if test="sesion == null || sesion.id == null">
                    <div class="form-group inline-group">
                        <label for="startImmediately">Iniciar Inmediatamente:</label>
                        <s:checkbox name="startImmediately" id="startImmediately" value="true" onclick="togglePlannedDateTime()"/>
                        <small>Marca para empezar la sesión justo después de crearla.</small>
                    </div>

                    <div id="plannedDateTimeSection" class="form-group_var1">
                        <label for="plannedDateStr">Fecha y Hora Planificada:</label>
                        <div class="date-time-inputs">
                            <s:textfield name="plannedDateStr" type="date" cssClass="form-control" value="%{plannedDateStr}"/>
                            <s:textfield name="plannedTimeStr" type="time" cssClass="form-control" value="%{plannedTimeStr}"/>
                        </div>
                        <small>Si no se inicia inmediatamente, debe tener una fecha y hora programada.</small>
                    </div>
                </s:if>
                <s:else>
                    <%-- En modo edición, si la sesión está planificada, permitimos ver/editar la fecha planificada --%>
                    <s:if test="%{sesion.estadoString == 'PLANNED'}">
                        <div id="plannedDateTimeSection" class="form-group">
                            <label for="plannedDateStr">Fecha y Hora Planificada:</label>
                            <div class="date-time-inputs">
                                <s:textfield name="plannedDateStr" type="date" cssClass="form-control" value="%{plannedDateStr}"/>
                                <s:textfield name="plannedTimeStr" type="time" cssClass="form-control" value="%{plannedTimeStr}"/>
                            </div>
                            <small>Solo se puede editar la fecha de una sesión programada.</small>
                        </div>
                    </s:if>
                    <%-- Otros campos de estado y resultados de sesión se pueden mostrar pero no editar --%>
                    <div class="form-group">
                        <label>Estado Actual:</label>
                        <p class="form-control" style="background-color: #e9ecef;"><s:property value="sesion.estadoString"/></p>
                    </div>
                    <s:if test="sesion.fechaInicioReal != null">
                        <div class="form-group">
                            <label>Iniciada:</label>
                            <p class="form-control" style="background-color: #e9ecef;"><s:date name="sesion.fechaInicioReal" format="dd/MM/yyyy HH:mm"/></p>
                        </div>
                    </s:if>
                    <s:if test="sesion.fechaFinReal != null">
                         <div class="form-group">
                            <label>Finalizada:</label>
                            <p class="form-control" style="background-color: #e9ecef;"><s:date name="%{sesion.fechaFinReal}" format="dd/MM/yyyy HH:mm"/></p>
                        </div>
                        <div class="form-group">
                            <label>Duración Real (minutos):</label>
                            <p class="form-control" style="background-color: #e9ecef;"><s:property value="sesion.duracionRealMinutos"/></p>
                        </div>
                        <div class="form-group">
                            <label>Estudio Efectivo (minutos):</label>
                            <p class="form-control" style="background-color: #e9ecef;"><s:property value="sesion.tiempoEstudioEfectivoMinutos"/></p>
                        </div>
                        <div class="form-group">
                            <label>Tiempo de Descanso (minutos):</label>
                            <p class="form-control" style="background-color: #e9ecef;"><s:property value="sesion.tiempoDescansoMinutos"/></p>
                        </div>
                        <div class="form-group">
                            <label>Notas:</label>
                            <p class="form-control" style="background-color: #e9ecef;"><s:property value="sesion.notas"/></p>
                        </div>
                        <div class="form-group">
                            <label>Calificación de Enfoque:</label>
                            <p class="form-control" style="background-color: #e9ecef;"><s:property value="sesion.calificacionEnfoque"/>/5</p>
                        </div>
                        <div class="form-group">
                            <label>Detalles de Interrupción:</label>
                            <p class="form-control" style="background-color: #e9ecef;"><s:property value="sesion.detallesInterrupcion"/></p>
                        </div>
                    </s:if>
                </s:else>


                <div class="form-group">
                    <s:submit value="%{sesion != null && sesion.id != null ? 'Actualizar Sesión' : 'Guardar Sesión'}" cssClass="btn"/>
                </div>
            </s:form>
        </div>

        <footer>
            &copy; 2025 Estudy+. Todos los derechos reservados.
        </footer>

        <script>
            document.addEventListener('DOMContentLoaded', function() {
                const startImmediatelyCheckbox = document.getElementById('startImmediately');
                const plannedDateTimeSection = document.getElementById('plannedDateTimeSection');

                function togglePlannedDateTime() {
                    if (startImmediatelyCheckbox.checked) {
                        plannedDateTimeSection.style.display = 'none';
                    } else {
                        plannedDateTimeSection.style.display = 'block';
                    }
                }

                // Ejecutar al cargar la página para establecer el estado inicial
                if (startImmediatelyCheckbox) { // Solo si el checkbox existe (modo creación)
                    togglePlannedDateTime();
                    // Asegurar que la fecha actual sea el valor por defecto si no hay nada
                    if (startImmediatelyCheckbox.checked && !plannedDateTimeSection.querySelector('input[type="date"]').value) {
                         const today = new Date();
                         const yyyy = today.getFullYear();
                         const mm = String(today.getMonth() + 1).padStart(2, '0'); // Months start at 0!
                         const dd = String(today.getDate()).padStart(2, '0');
                         const hh = String(today.getHours()).padStart(2, '0');
                         const min = String(today.getMinutes()).padStart(2, '0');

                         plannedDateTimeSection.querySelector('input[type="date"]').value = `${yyyy}-${mm}-${dd}`;
                         plannedDateTimeSection.querySelector('input[type="time"]').value = `${hh}:${min}`;
                    }
                }
            });
        </script>
    </body>
</html>

