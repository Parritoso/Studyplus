<%-- 
    Document   : manageDeliverables
    Created on : 31-may-2025, 20:42:23
    Author     : Parri
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Gestionar Entregas y Exámenes - Estudy+</title>
        <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/manageDeliverables.css">
        <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/manageDeliverables.css" disabled>
    </head>
    <body>
        <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
        <%@ include file="/jsp/common/navbar.jsp" %>

        <div class="container">
            <h2>Gestión de Entregas, Exámenes y Recordatorios</h2>

            <div class="btn-group">
                <s:url action="showCreateDelivery" var="createEntregaUrl"/>
                <s:a href="%{createEntregaUrl}" cssClass="btn">Crear Nueva Entrega</s:a>
                <s:url action="showCreateExam" var="createExamenUrl"/>
                <s:a href="%{createExamenUrl}" cssClass="btn" style="background-color: #17a2b8;">Crear Nuevo Examen</s:a>
            </div>

            <div class="upcoming-events">
                <h3 class="section-title">Próximas Entregas y Exámenes</h3>
                <s:if test="(proximasEntregas != null && !proximasEntregas.isEmpty()) || (proximosExamenes != null && !proximosExamenes.isEmpty())">
                    <ul class="event-list">
                        <%-- Mostrar Próximas Entregas --%>
                        <s:iterator value="proximasEntregas">
                            <li>
                                <div>
                                    <strong><s:property value="titulo"/></strong> (<s:property value="asignatura"/> - Entrega)<br>
                                    <span class="date">
                                        <fmt:formatDate value="%{fechaLimite}" pattern="dd/MM/yyyy HH:mm" default="Fecha no disponible"/>
                                    </span>
                                </div>
                                <div class="actions">
                                    <s:url action="editEntrega" var="editEntregaUrl">
                                        <s:param name="id" value="id"/>
                                    </s:url>
                                    <s:a href="%{editEntregaUrl}" class="btn" style="background-color: #ffc107; padding: 5px 10px;">Editar</s:a>
                                    <s:url action="deleteEntrega" var="deleteEntregaUrl">
                                        <s:param name="id" value="id"/>
                                    </s:url>
                                    <s:a href="%{deleteEntregaUrl}" class="btn" style="background-color: #dc3545; padding: 5px 10px;"
                                         onclick="return confirm('¿Estás seguro de que quieres eliminar esta entrega?');">Eliminar</s:a>
                                </div>
                            </li>
                        </s:iterator>

                        <%-- Mostrar Próximos Exámenes --%>
                        <s:iterator value="proximosExamenes">
                            <li>
                                <div>
                                    <strong><s:property value="nombre"/></strong> (<s:property value="asignatura"/> - Examen)<br>
                                    <span class="date">
                                        <fmt:formatDate value="%{fechaExamen}" pattern="dd/MM/yyyy HH:mm" default="Fecha no disponible"/>
                                    </span>
                                </div>
                                <div class="actions">
                                    <s:url action="editExamen" var="editExamenUrl">
                                        <s:param name="id" value="id"/>
                                    </s:url>
                                    <s:a href="%{editExamenUrl}" class="btn" style="background-color: #ffc107; padding: 5px 10px;">Editar</s:a>
                                    <s:url action="deleteExamen" var="deleteExamenUrl">
                                        <s:param name="id" value="id"/>
                                    </s:url>
                                    <s:a href="%{deleteExamenUrl}" class="btn" style="background-color: #dc3545; padding: 5px 10px;"
                                         onclick="return confirm('¿Estás seguro de que quieres eliminar este examen?');">Eliminar</s:a>
                                </div>
                            </li>
                        </s:iterator>
                    </ul>
                </s:if>
                <s:else>
                    <p class="event-empty">No hay entregas, exámenes o recordatorios próximos.</p>
                </s:else>
            </div>

            <div class="calendar-section">
                <h3 class="section-title">Calendario de Eventos</h3>
                <div class="calendar" id="academicCalendar">
                    <div class="calendar-header">
                        <span id="currentMonthYear"></span>
                    </div>
                    <div class="day-name">Lun</div>
                    <div class="day-name">Mar</div>
                    <div class="day-name">Mié</div>
                    <div class="day-name">Jue</div>
                    <div class="day-name">Vie</div>
                    <div class="day-name">Sáb</div>
                    <div class="day-name">Dom</div>
                </div>
            </div>

        </div>

        <footer>
            &copy; 2025 Estudy+. Todos los derechos reservados.
        </footer>

        <script type="text/javascript">
            document.addEventListener('DOMContentLoaded', function() {
                const calendarContainer = document.getElementById('academicCalendar');
                const currentMonthYearSpan = document.getElementById('currentMonthYear');
                const today = new Date();
                const currentMonth = today.getMonth();
                const currentYear = today.getFullYear();

                // Recuperar los JSON de la acción Struts2
                // Si las propiedades son null, se inicializarán como cadenas vacías 'null'
                const rawEntregasJson = '<s:property value="proximasEntregasJson" escape="false"/>';
                const rawExamenesJson = '<s:property value="proximosExamenesJson" escape="false"/>';
                
                let allEvents = [];

                try {
                    // Solo parsear si la cadena JSON no es nula o vacía
                    const entregas = rawEntregasJson && rawEntregasJson !== 'null' ? JSON.parse(rawEntregasJson) : [];
                    const examenes = rawExamenesJson && rawExamenesJson !== 'null' ? JSON.parse(rawExamenesJson) : [];
                    
                    // Unir entregas y exámenes en una sola lista de eventos para el calendario
                    // Asegúrate de que las fechas se conviertan a objetos Date si no lo están
                    entregas.forEach(e => {
                        e.type = 'entrega';
                        e.date = new Date(e.fechaLimite); // Usar fechaLimite para entregas
                        allEvents.push(e);
                    });
                    examenes.forEach(ex => {
                        ex.type = 'examen';
                        ex.date = new Date(ex.fechaExamen); // Usar fechaExamen para exámenes
                        allEvents.push(ex);
                    });
                    
                    // Ordenar todos los eventos por fecha
                    allEvents.sort((a, b) => a.date - b.date);

                } catch (e) {
                    console.error("Error al parsear JSON de eventos:", e);
                    allEvents = []; // Asegurarse de que no haya errores si el JSON es inválido
                }


                function renderCalendar(month, year) {
                    // Limpiar el calendario y re-añadir los elementos base
                    let monthYearText = 'Fecha inválida';
                    const date = new Date(year, month);

                    if (!isNaN(date)) {
                        monthYearText = date.toLocaleString('es-ES', { month: 'long', year: 'numeric' });
                    }
                    calendarContainer.innerHTML = `
                        <div class="calendar-header">
                            <span id="currentMonthYear">${monthYearText}</span>
                        </div>
                        <div class="day-name">Lun</div>
                        <div class="day-name">Mar</div>
                        <div class="day-name">Mié</div>
                        <div class="day-name">Jue</div>
                        <div class="day-name">Vie</div>
                        <div class="day-name">Sáb</div>
                        <div class="day-name">Dom</div>
                    `;

                    const firstDay = new Date(year, month, 1);
                    let startDay = (firstDay.getDay() === 0) ? 6 : firstDay.getDay() - 1; // Ajusta para que la semana empiece en Lunes

                    const daysInMonth = new Date(year, month + 1, 0).getDate();

                    // Rellenar días vacíos al principio
                    for (let i = 0; i < startDay; i++) {
                        const emptyCell = document.createElement('div');
                        emptyCell.classList.add('day-cell');
                        calendarContainer.appendChild(emptyCell);
                    }

                    // Rellenar días del mes
                    for (let day = 1; day <= daysInMonth; day++) {
                        const dayCell = document.createElement('div');
                        dayCell.classList.add('day-cell', 'current-month');
                        
                        const date = new Date(year, month, day);
                        if (date.toDateString() === today.toDateString()) {
                            dayCell.classList.add('today');
                        }
                        
                        dayCell.innerHTML = `<div class="day-number">${day}</div>`;
                        
                        // Añadir marcadores de evento (entregas y exámenes)
                        allEvents.forEach(event => {
                            if (event.date.getDate() === day &&
                                event.date.getMonth() === month &&
                                event.date.getFullYear() === year) {
                                const eventMarker = document.createElement('span');
                                if (event.type === 'entrega') {
                                    eventMarker.classList.add('event-marker');
                                    eventMarker.textContent = event.titulo;
                                } else if (event.type === 'examen') {
                                    eventMarker.classList.add('exam-marker');
                                    eventMarker.textContent = event.nombre; // Usar 'nombre' para exámenes
                                }
                                dayCell.appendChild(eventMarker);
                            }
                        });
                        
                        calendarContainer.appendChild(dayCell);
                    }
                }

                renderCalendar(currentMonth, currentYear);
            });
        </script>
    </body>
</html>
