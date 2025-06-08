<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Mis Sesiones de Estudio - Estudy+</title>
        <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/manageStudySessions.css">
        <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/manageStudySessions.css" disabled>
    </head>
    <body>
        <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
        <%@ include file="/jsp/common/navbar.jsp" %>

        <div class="container">
            <h2>Gestionar Sesiones de Estudio</h2>

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

            <div class="btn-group">
                <s:url action="showCreateStudySession" var="createSessionUrl"/>
                <s:a href="%{createSessionUrl}" cssClass="btn">Programar/Iniciar Nueva Sesión</s:a>
                </div>

            <s:form action="manageStudySessions" method="post" cssClass="filters-section">
                <label for="filterType">Mostrar:</label>
                <s:select name="filterType" list="filterTypeOptions"
                          value="%{filterType}" cssClass="form-control" onchange="this.form.submit()"/>

                <label for="filterTecnicaId">Técnica:</label>
                <s:select name="filterTecnicaId" list="tecnicasDisponibles" listKey="id" listValue="nombre"
                          headerKey="" headerValue="Todas las Técnicas" value="%{filterTecnicaId}"
                          cssClass="form-control" onchange="this.form.submit()"/>

                <label for="filterTipoTecnica">Tipo de Técnica:</label>
                <s:select name="filterTipoTecnica" list="tipoTecnicaOptions"
                          headerKey="" headerValue="Todos los Tipos" value="%{filterTipoTecnica}"
                          cssClass="form-control" onchange="this.form.submit()"/>

                <s:submit value="Aplicar Filtros" cssClass="btn" style="background-color: #007bff;"/>
            </s:form>

            <div class="session-list-section">
                <h3 class="section-title">
                    <s:if test="%{'history'.equalsIgnoreCase(filterType)}">Historial de Sesiones</s:if>
                    <s:else>Próximas y Sesiones Activas/Pausadas</s:else>
                    </h3>
                <s:if test="sesiones != null && !sesiones.isEmpty()">
                    <ul class="session-list">
                        <s:iterator value="sesiones">
                            <li>
                                <div class="session-info">
                                    <strong><s:property value="titulo"/></strong>
                                    <span>
                                        <s:if test="descripcion != null && !descripcion.isEmpty()">
                                            <s:property value="descripcion"/>
                                        </s:if>
                                    </span>
                                    <span>Duración Planificada: <s:property value="duracionPlanificadaMinutos"/> minutos</span>
                                    <s:if test="fechaSesionPlanificada != null">
                                        <span>Planificada para: <s:date name="fechaSesionPlanificada" format="dd/MM/yyyy HH:mm"/></span>
                                    </s:if>
                                    <s:if test="tecnicaAplicadaId != null">
                                        <span>Técnica: <s:property value="tecnicaAplicadaId.nombre"/></span>
                                    </s:if>
                                    <s:if test="entregaAsociada != null">
                                        <span>Asociada a Entrega: <s:property value="entregaAsociada.titulo"/></span>
                                    </s:if>
                                    <s:if test="examenAsociado != null">
                                        <span>Asociado a Examen: <s:property value="examenAsociado.nombre"/></span>
                                    </s:if>
                                    <span>Estado: <strong><s:property value="estadoString"/></strong></span>

                                    <%-- Mostrar detalles de tiempo real solo si la sesión se inició --%>
                                    <s:if test="fechaInicioReal != null">
                                        <span>Iniciada: <s:date name="fechaInicioReal" format="dd/MM/yyyy HH:mm"/></span>
                                        <s:if test="fechaFinReal != null">
                                            <span>Finalizada: <s:date name="fechaFinReal" format="dd/MM/yyyy HH:mm"/></span>
                                            <span>Duración Real: <s:property value="duracionRealMinutos"/> minutos</span>
                                            <span>Estudio Efectivo: <s:property value="tiempoEstudioEfectivoMinutos"/> minutos</span>
                                            <span>Descanso: <s:property value="tiempoDescansoMinutos"/> minutos</span>
                                            <s:if test="calificacionEnfoque != null && calificacionEnfoque > 0">
                                                <span>Calificación Enfoque: <s:property value="calificacionEnfoque"/>/5</span>
                                            </s:if>
                                        </s:if>
                                    </s:if>
                                    <%-- NUEVO: Mostrar Notas Rápidas --%>
                                    <s:if test="notasRapidas != null && !notasRapidas.isEmpty()">
                                        <div class="session-extra-info">
                                            <strong><s:text name="session.notes.label"/>:</strong>
                                            <p><s:property value="notasRapidas"/></p>
                                        </div>
                                    </s:if>
                                    <%-- NUEVO: Mostrar Checklist (renderizado simple como texto JSON) --%>
                                    <s:if test="checklist != null && !checklist.isEmpty()">
                                        <div class="session-extra-info">
                                            <strong><s:text name="session.checklist.label"/>:</strong>
                                            <ul class="checklist-display" id="checklistDisplay_%{id}">
                                                <%-- El contenido del checklist se renderizará con JavaScript --%>
                                            </ul>
                                        </div>
                                        <script type="text/javascript">
                                            document.addEventListener('DOMContentLoaded', function () {
                                                const checklistData = <s:property value="checklist" escape="false"/>;
                                                const checklistUl = document.getElementById('checklistDisplay_<s:property value="id"/>');
                                                if (checklistUl && checklistData && Array.isArray(checklistData)) {
                                                    checklistData.forEach(item => {
                                                        const li = document.createElement('li');
                                                        li.className = 'checklist-item' + (item.completed ? ' completed' : '');
                                                        li.innerHTML = `
                                                            <input type="checkbox" ${item.completed ? 'checked' : ''} disabled>
                                                            <label>${item.text}</label>
                                                        `;
                                                        checklistUl.appendChild(li);
                                                    });
                                                }
                                            });
                                        </script>
                                    </s:if>
                                </div>
                                <div class="session-actions">
                                    <s:url action="showEditStudySession" var="editUrl">
                                        <s:param name="sesionId" value="id"/>
                                    </s:url>
                                    <s:a href="%{editUrl}" cssClass="btn-small btn-edit">Editar</s:a>

                                    <s:url action="deleteStudySession" var="deleteUrl">
                                        <s:param name="sesionId" value="id"/>
                                    </s:url>
                                    <s:a href="%{deleteUrl}" cssClass="btn-small btn-delete" 
                                         onclick="return confirm('¿Estás seguro de que quieres eliminar esta sesión?');">Eliminar</s:a>

                                    <%-- Botones de control de estado de sesión (iniciar, pausar, reanudar, finalizar) --%>
                                    <s:if test="%{estadoString == 'PLANNED' || estadoString == 'PAUSED'}">
                                        <s:url action="startStudySession" var="startUrl">
                                            <s:param name="sesionId" value="id"/>
                                        </s:url>
                                        <s:a href="%{startUrl}" cssClass="btn-small btn-start">
                                            <s:if test="%{estadoString == 'PLANNED'}">Iniciar</s:if>
                                            <s:else>Reanudar</s:else>
                                        </s:a>
                                    </s:if>
                                    <s:if test="%{estadoString == 'ACTIVE'}">
                                        <s:url action="pauseStudySession" var="pauseUrl">
                                            <s:param name="sesionId" value="id"/>
                                        </s:url>
                                        <s:a href="%{pauseUrl}" cssClass="btn-small btn-pause">Pausar</s:a>

                                        <s:url action="endStudySession" var="endUrl">
                                            <s:param name="sesionId" value="id"/>
                                        </s:url>
                                        <s:a href="%{endUrl}" cssClass="btn-small btn-end">Finalizar</s:a>
                                    </s:if>
                                    <s:if test="%{estadoString == 'PAUSED'}">
                                        <s:url action="endStudySession" var="endUrl">
                                            <s:param name="sesionId" value="id"/>
                                        </s:url>
                                        <s:a href="%{endUrl}" cssClass="btn-small btn-end">Finalizar/Abortar</s:a>
                                    </s:if>
                                </div>
                            </li>
                        </s:iterator>
                    </ul>
                </s:if>
                <s:else>
                    <p class="session-empty">No hay sesiones de estudio para mostrar con los filtros actuales.</p>
                </s:else>
            </div>
        </div>

        <footer>
            &copy; 2025 Estudy+. Todos los derechos reservados.
        </footer>

        <script type="text/javascript">
            // Aquí puedes añadir JavaScript adicional si quieres, por ejemplo, para un calendario interactivo
            // que use las sesiones JSON, o para confirmar finalizaciones de sesión con un modal.
            // Ejemplo de acceso al JSON:
            // const sesionesData = JSON.parse('<s:property value="sesionesJson" escape="false"/>');
            // console.log(sesionesData); // Puedes usar esto para un calendario más avanzado
        </script>
    </body>
</html>