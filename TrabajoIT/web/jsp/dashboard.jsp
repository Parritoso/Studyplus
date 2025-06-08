<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
    <%--<head>
        <meta charset="UTF-8">
        <title>Dashboard - Estudy+</title>
        <style>
            body { font-family: Arial, sans-serif; line-height: 1.6; margin: 0; padding: 0; background-color: #f4f4f4; color: #333; }
            .navbar { background-color: #333; color: white; padding: 10px 0; text-align: center; }
            .navbar a { color: white; text-decoration: none; padding: 0 15px; }
            .navbar a:hover { text-decoration: underline; }
            .dashboard-header { text-align: center; padding: 40px 20px; background-color: #007bff; color: white; margin-bottom: 20px; }
            .dashboard-header h1 { margin: 0; }
            .container { max-width: 1200px; margin: auto; padding: 20px; }
            .dashboard-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
                gap: 20px;
            }
            .card {
                background-color: #fff;
                padding: 25px;
                border-radius: 8px;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                text-align: center;
                transition: transform 0.2s ease-in-out;
                display: flex;
                flex-direction: column;
                justify-content: space-between;
            }
            .card:hover {
                transform: translateY(-5px);
            }
            .card h3 {
                color: #007bff;
                margin-top: 0;
                margin-bottom: 15px;
            }
            .card p {
                color: #555;
                font-size: 0.9em;
                margin-bottom: 20px;
                flex-grow: 1; /* Para que el párrafo ocupe el espacio y empuje el botón hacia abajo */
            }
            .card .btn-card {
                background-color: #28a745;
                color: white;
                padding: 10px 15px;
                border-radius: 5px;
                text-decoration: none;
                font-weight: bold;
                display: inline-block; /* Para que el padding y margen funcionen bien */
                width: fit-content;
                margin: 0 auto; /* Centrar el botón */
            }
            .btn-card:hover {
                background-color: #218838;
            }
            /* Estilo específico para el botón de sesión rápida */
            .btn-quick-session {
                background-color: #007bff; /* Color azul para destacar */
            }
            .btn-quick-session:hover {
                background-color: #0056b3;
            }
            footer { text-align: center; padding: 20px; margin-top: 30px; background-color: #333; color: white; }
            /* Modal styles */
            .modal {
                display: none; /* Hidden by default */
                position: fixed; /* Stay in place */
                z-index: 1000; /* Sit on top */
                left: 0;
                top: 0;
                width: 100%; /* Full width */
                height: 100%; /* Full height */
                overflow: auto; /* Enable scroll if needed */
                background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
                justify-content: center;
                align-items: center;
            }
            .modal-content {
                background-color: #fefefe;
                margin: auto;
                padding: 30px;
                border: 1px solid #888;
                border-radius: 10px;
                width: 90%;
                max-width: 600px;
                box-shadow: 0 5px 15px rgba(0,0,0,0.3);
                text-align: left;
                position: relative;
            }
            .modal-content h3 {
                text-align: center;
                color: #007bff;
                margin-bottom: 20px;
            }
            .form-group {
                margin-bottom: 15px;
            }
            .form-group label {
                font-weight: bold;
                margin-bottom: 5px;
                display: block;
            }
            .form-control {
                width: calc(100% - 22px); /* Adjust for padding and border */
                padding: 10px;
                border: 1px solid #ddd;
                border-radius: 5px;
                box-sizing: border-box;
            }
            .form-control[readonly] {
                background-color: #e9ecef;
                cursor: not-allowed;
            }
            .modal-buttons {
                display: flex;
                justify-content: space-between;
                margin-top: 25px;
            }
            .modal-buttons .btn {
                width: 48%;
                padding: 10px 15px;
                border-radius: 5px;
                cursor: pointer;
                font-weight: bold;
                transition: background-color 0.3s ease;
            }
            .modal-buttons .btn-cancel {
                background-color: #6c757d;
                color: white;
            }
            .modal-buttons .btn-cancel:hover {
                background-color: #5a6268;
            }
            .modal-buttons .btn-submit {
                background-color: #28a745;
                color: white;
            }
            .modal-buttons .btn-submit:hover {
                background-color: #218838;
            }
            /* Alert messages within modal */
            .modal .alert {
                margin-bottom: 15px;
                padding: 10px;
                border-radius: 5px;
                font-size: 0.9em;
            }
            .modal .alert-danger {
                background-color: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
            }
        </style>
    </head>
    <body>
    <%-- INCLUIR LA BARRA DE NAVEGACIÓN COMÚN (adaptada para usuarios logeados) 
    <%@ include file="/jsp/common/navbar.jsp" %>

        <div class="dashboard-header">
            <h1>Bienvenido a tu Dashboard, <s:property value="username"/>!</h1>
            <p>Aquí puedes gestionar todas tus actividades en Estudy+.</p>
        </div>

        <div class="container">
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
            <div class="dashboard-grid">

    <%-- Nueva tarjeta para Iniciar Sesión Rápida 
    <div class="card">
        <h3>Iniciar Sesión Rápida</h3>
        <p>Comienza una sesión de estudio inmediatamente sin necesidad de configuración previa.</p>
        <s:url action="showQuickSessionForm" var="quickSessionUrl"/>
        <button type="button" id="openQuickSessionModalBtn" class="btn-card btn-quick-session">¡Empezar Ahora!</button>
    </div>

                <div class="card">
                    <h3>Gestionar Entregas y Exámenes</h3>
                    <p>Crea, visualiza y marca como completadas tus entregas y exámenes.</p>
                    <s:url action="manageDeliverables" var="viewTasksUrl"/>
                    <s:a href="%{viewTasksUrl}" cssClass="btn-card">Ir a Tareas</s:a>
                    </div>

                    <div class="card">
                        <h3>Sesiones de Estudio</h3>
                        <p>Planifica, inicia y sigue tus sesiones de estudio para optimizar tu tiempo.</p>
                    <s:url action="manageStudySessions" var="studySessionsUrl"/>
                    <s:a href="%{studySessionsUrl}" cssClass="btn-card">Gestionar Sesiones</s:a>
                    </div>

                    <div class="card">
                        <h3>Grupos Colaborativos</h3>
                        <p>Únete o crea grupos de estudio, comparte recursos y organiza eventos.</p>
                    <s:url action="manageStudyGroups" var="studyGroupsUrl"/>
                    <s:a href="%{studyGroupsUrl}" cssClass="btn-card">Explorar Grupos</s:a>
                    </div>

                    <div class="card">
                        <h3>Sistema de Puntos y Rankings</h3>
                        <p>Gana puntos y medallas, y sube en el ranking de productividad.</p>
                    <s:url action="viewGamification" var="gamificationUrl"/>
                    <s:a href="%{gamificationUrl}" cssClass="btn-card">Ver Mis Logros</s:a>
                    </div>

                    <div class="card">
                        <h3>Chats y Amistades</h3>
                        <p>Conéctate con tus amigos y compañeros de estudio en tiempo real.</p>
                    <s:url action="friendsAndMessages" var="friendsAndMessagesUrl"/>
                    <s:a href="%{friendsAndMessagesUrl}" cssClass="btn-card">Acceder</s:a>
                    </div>

                    <div class="card">
                        <h3>Configuración y Perfil</h3>
                        <p>Actualiza tu perfil, preferencias y ajustes de la aplicación.</p>
                    <s:url action="editProfile" var="profileUrl"/>
                    <s:a href="%{profileUrl}" cssClass="btn-card">Gestionar Perfil</s:a>
                    </div>

                </div>
            </div>

            <footer>
                &copy; 2025 Estudy+. Todos los derechos reservados.
            </footer>

            <div id="quickSessionModal" class="modal">
                <div class="modal-content">
                    <h3>Iniciar Sesión de Estudio Rápida</h3>
    <%-- Mostrar errores de acción si existen 
    <s:if test="hasActionErrors()">
        <div class="alert alert-danger action-error-list">
            <s:actionerror escape="false"/>
        </div>
    </s:if>
    <s:form action="startQuickSession" method="post" id="quickSessionForm">
        <div class="form-group">
            <label for="sesion_titulo">Título:</label>
            <s:textfield name="sesion.titulo" id="sesion_titulo" cssClass="form-control" readonly="true" value="%{sesion.titulo}"/>
        </div>
        <div class="form-group">
            <label for="currentDateTimeFormatted">Fecha y Hora Actual:</label>
            <s:textfield name="currentDateTimeFormatted" id="currentDateTimeFormatted" cssClass="form-control" readonly="true" value="%{currentDateTimeFormatted}"/>
        </div>
        <div class="form-group">
            <label for="sesion_descripcion">Descripción:</label>
            <s:textarea name="sesion.descripcion" id="sesion_descripcion" cssClass="form-control" rows="3" readonly="true" value="%{sesion.descripcion}"/>
        </div>
        <div class="form-group">
            <label for="sesion_duracionPlanificadaMinutos">Duración Planificada (minutos):</label>
            <s:textfield name="sesion.duracionPlanificadaMinutos" id="sesion_duracionPlanificadaMinutos" type="number" cssClass="form-control" min="10" max="480" placeholder="Ej: 60" value="%{sesion.duracionPlanificadaMinutos}"/>
        </div>
        <div class="form-group">
            <label for="sesion_tecnicaAplicadaId">Técnica de Estudio:</label>
            <s:select name="sesion.tecnicaAplicadaId.id" id="sesion_tecnicaAplicadaId" list="tecnicasDisponibles" listKey="id" listValue="nombre"
                      headerKey="" headerValue="-- Seleccionar Técnica --" cssClass="form-control" value="%{sesion.tecnicaAplicadaId != null ? sesion.tecnicaAplicadaId.id : ''}"/>
        </div>
        <div class="form-group">
            <label for="sesion_entregaAsociada">Entrega Asociada:</label>
            <s:select name="sesion.entregaAsociada.id" id="sesion_entregaAsociada" list="entregasDisponibles" listKey="id" listValue="titulo"
                      headerKey="" headerValue="-- No Asociar --" cssClass="form-control" value="%{sesion.entregaAsociada != null ? sesion.entregaAsociada.id : ''}"/>
        </div>
        <div class="form-group">
            <label for="sesion_examenAsociado">Examen Asociado:</label>
            <s:select name="sesion.examenAsociado.id" id="sesion_examenAsociado" list="examenesDisponibles" listKey="id" listValue="nombre"
                      headerKey="" headerValue="-- No Asociar --" cssClass="form-control" value="%{sesion.examenAsociado != null ? sesion.examenAsociado.id : ''}"/>
        </div>
        <div class="modal-buttons">
            <button type="button" class="btn btn-cancel" id="cancelQuickSessionBtn">Cancelar</button>
            <button type="submit" class="btn btn-submit">Iniciar Sesión</button>
        </div>
    </s:form>
</div>
</div>
<script type="text/javascript">
document.addEventListener('DOMContentLoaded', function () {
    const quickSessionModal = document.getElementById('quickSessionModal');
    const openQuickSessionModalBtn = document.getElementById('openQuickSessionModalBtn');
    const cancelQuickSessionBtn = document.getElementById('cancelQuickSessionBtn');
    const quickSessionForm = document.getElementById('quickSessionForm');
    const actionErrorListDiv = quickSessionModal.querySelector('.action-error-list'); // Nuevo: referencia al div de errores

                // Helper function to populate select elements
                function populateSelect(selectElement, items, idKey, valueKey, headerText, selectedId) {
                    selectElement.innerHTML = ''; // Clear existing options
                    const headerOption = document.createElement('option');
                    headerOption.value = '';
                    headerOption.textContent = headerText;
                    selectElement.appendChild(headerOption);

                    items.forEach(item => {
                        const option = document.createElement('option');
                        option.value = item[idKey];
                        option.textContent = item[valueKey];
                        if (selectedId !== null && selectedId !== undefined && String(item[idKey]) === String(selectedId)) {
                            option.selected = true;
                        }
                        selectElement.appendChild(option);
                    });
                }

                // Function to open the modal and fetch data
                async function openQuickSessionModal() {
                    try {
                        // Ocultar errores anteriores
                        if (actionErrorListDiv) {
                            actionErrorListDiv.innerHTML = '';
                            actionErrorListDiv.style.display = 'none';
                        }

                        const response = await fetch('<s:url action="showQuickSessionForm"/>');
                        if (!response.ok) {
                            // Si la respuesta no es OK (ej. 404, 500), intentar leer el JSON de error
                            const errorData = await response.json().catch(() => ({})); // Capturar si no es JSON
                            let errorMessage = 'Error desconocido al cargar el formulario.';
                            if (errorData && errorData.actionErrors && errorData.actionErrors.length > 0) {
                                errorMessage = errorData.actionErrors.join('; ');
                            } else if (errorData && errorData.message) {
                                errorMessage = errorData.message;
                            }
                            throw new Error(errorMessage);
                        }

                        // *** AQUÍ ESTÁ EL CAMBIO CLAVE ***
                        // El JSON es un array, tomamos el primer elemento.
                        const rawData = await response.json();
                        const data = rawData[0]; // Acceder al primer objeto del array

                        // Manejar y mostrar actionErrors si los hay en el JSON recibido
                        if (data.actionErrors && data.actionErrors.length > 0) {
                            if (actionErrorListDiv) {
                                actionErrorListDiv.innerHTML = '<ul>' + data.actionErrors.map(err => `<li>${err}</li>`).join('') + '</ul>';
                                actionErrorListDiv.style.display = 'block';
                            }
                        }

                        // Populate non-editable fields
                        // Ahora data.sesion sí existirá
                        document.getElementById('sesion_titulo').value = data.sesion.titulo;
                        document.getElementById('currentDateTimeFormatted').value = data.currentDateTimeFormatted;
                        document.getElementById('sesion_descripcion').value = data.sesion.descripcion;
                        document.getElementById('sesion_duracionPlanificadaMinutos').value = data.sesion.duracionPlanificadaMinutos;

                        // Populate and select dropdowns
                        populateSelect(document.getElementById('sesion_tecnicaAplicadaId'), data.tecnicasDisponibles, 'id', 'nombre', '-- Seleccionar Técnica --', data.sesion.tecnicaAplicadaId ? data.sesion.tecnicaAplicadaId.id : '');
                        populateSelect(document.getElementById('sesion_entregaAsociada'), data.entregasDisponibles, 'id', 'titulo', '-- No Asociar --', data.sesion.entregaAsociada ? data.sesion.entregaAsociada.id : '');
                        populateSelect(document.getElementById('sesion_examenAsociado'), data.examenesDisponibles, 'id', 'nombre', '-- No Asociar --', data.sesion.examenAsociado ? data.sesion.examenAsociado.id : '');

                        quickSessionModal.style.display = 'flex';

                    } catch (error) {
                        console.error("Error al cargar el formulario de sesión rápida:", error);
                        // Si ya hay errores de acción en el JSON, la alerta podría duplicarlos o no ser necesaria.
                        // Podrías decidir mostrar la alerta solo si no hay actionErrors explícitos en el JSON.
                        if (actionErrorListDiv && actionErrorListDiv.style.display !== 'block') { // Solo si no se mostraron ya errores
                            alert("No se pudo cargar el formulario de sesión rápida. Por favor, inténtalo de nuevo. " + error.message);
                        }
                    }
                }

                // Function to close the modal
                function closeQuickSessionModal() {
                    quickSessionModal.style.display = 'none';
                    // Clear any previous errors when closing
                    if (actionErrorListDiv) {
                        actionErrorListDiv.innerHTML = '';
                        actionErrorListDiv.style.display = 'none';
                    }
                }

                // Event listener to open modal
                openQuickSessionModalBtn.addEventListener('click', function (event) {
                    event.preventDefault();
                    openQuickSessionModal();
                });

                // Event listener to close modal
                cancelQuickSessionBtn.addEventListener('click', closeQuickSessionModal);
                window.addEventListener('click', function (event) {
                    if (event.target == quickSessionModal) {
                        closeQuickSessionModal();
                    }
                });

                // Check if there are Struts2 action errors after form submission
                // If there are action errors, it means the form submission failed.
                // Struts2 has already populated the value stack with the submitted 'sesion' object
                // and the dropdown lists. We just need to open the modal and display errors.
            <s:if test="hasActionErrors()">
                // Populate the form fields with the values that caused the error
                // These values are already on the value stack due to Struts2's default behavior
                // for 'input' results. We just need to ensure the modal is displayed.
                quickSessionModal.style.display = 'flex'; // Open the modal
                const errorListContainer = quickSessionModal.querySelector('.action-error-list');
                if (errorListContainer) {
                    errorListContainer.style.display = 'block'; // Ensure error div is visible
                }
                // The <s:actionerror/> tag inside the modal will automatically render the errors
            </s:if>
            });
        </script>
    </body>--%>
    <head>
        <meta charset="UTF-8">
        <%-- Título de la página --%>
        <title><s:text name="dashboard.title"/></title>
        <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/dashboard.css">
        <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/dashboard.css" disabled>
        <%--<style>
            body { font-family: Arial, sans-serif; line-height: 1.6; margin: 0; padding: 0; background-color: #f4f4f4; color: #333; }
            .navbar { background-color: #333; color: white; padding: 10px 0; text-align: center; }
            .navbar a { color: white; text-decoration: none; padding: 0 15px; }
            .navbar a:hover { text-decoration: underline; }
            .dashboard-header { text-align: center; padding: 40px 20px; background-color: #007bff; color: white; margin-bottom: 20px; }
            .dashboard-header h1 { margin: 0; }
            .container { max-width: 1200px; margin: auto; padding: 20px; }
            .dashboard-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
                gap: 20px;
            }
            .card {
                background-color: #fff;
                padding: 25px;
                border-radius: 8px;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                text-align: center;
                transition: transform 0.2s ease-in-out;
                display: flex;
                flex-direction: column;
                justify-content: space-between;
            }
            .card:hover {
                transform: translateY(-5px);
            }
            .card h3 {
                color: #007bff;
                margin-top: 0;
                margin-bottom: 15px;
            }
            .card p {
                color: #555;
                font-size: 0.9em;
                margin-bottom: 20px;
                flex-grow: 1; /* Para que el párrafo ocupe el espacio y empuje el botón hacia abajo */
            }
            .card .btn-card {
                background-color: #28a745;
                color: white;
                padding: 10px 15px;
                border-radius: 5px;
                text-decoration: none;
                font-weight: bold;
                display: inline-block; /* Para que el padding y margen funcionen bien */
                width: fit-content;
                margin: 0 auto; /* Centrar el botón */
            }
            .btn-card:hover {
                background-color: #218838;
            }
            /* Estilo específico para el botón de sesión rápida */
            .btn-quick-session {
                background-color: #007bff; /* Color azul para destacar */
            }
            .btn-quick-session:hover {
                background-color: #0056b3;
            }
            footer { text-align: center; padding: 20px; margin-top: 30px; background-color: #333; color: white; }
            /* Modal styles */
            .modal {
                display: none; /* Hidden by default */
                position: fixed; /* Stay in place */
                z-index: 1000; /* Sit on top */
                left: 0;
                top: 0;
                width: 100%; /* Full width */
                height: 100%; /* Full height */
                overflow: auto; /* Enable scroll if needed */
                background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
                justify-content: center;
                align-items: center;
            }
            .modal-content {
                background-color: #fefefe;
                margin: auto;
                padding: 30px;
                border: 1px solid #888;
                border-radius: 10px;
                width: 90%;
                max-width: 600px;
                box-shadow: 0 5px 15px rgba(0,0,0,0.3);
                text-align: left;
                position: relative;
            }
            .modal-content h3 {
                text-align: center;
                color: #007bff;
                margin-bottom: 20px;
            }
            .form-group {
                margin-bottom: 15px;
            }
            .form-group label {
                font-weight: bold;
                margin-bottom: 5px;
                display: block;
            }
            .form-control {
                width: calc(100% - 22px); /* Adjust for padding and border */
                padding: 10px;
                border: 1px solid #ddd;
                border-radius: 5px;
                box-sizing: border-box;
            }
            .form-control[readonly] {
                background-color: #e9ecef;
                cursor: not-allowed;
            }
            .modal-buttons {
                display: flex;
                justify-content: space-between;
                margin-top: 25px;
            }
            .modal-buttons .btn {
                width: 48%;
                padding: 10px 15px;
                border-radius: 5px;
                cursor: pointer;
                font-weight: bold;
                transition: background-color 0.3s ease;
            }
            .modal-buttons .btn-cancel {
                background-color: #6c757d;
                color: white;
            }
            .modal-buttons .btn-cancel:hover {
                background-color: #5a6268;
            }
            .modal-buttons .btn-submit {
                background-color: #28a745;
                color: white;
            }
            .modal-buttons .btn-submit:hover {
                background-color: #218838;
            }
            /* Alert messages within modal */
            .modal .alert {
                margin-bottom: 15px;
                padding: 10px;
                border-radius: 5px;
                font-size: 0.9em;
            }
            .modal .alert-danger {
                background-color: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
            }
            /* Media Queries para responsividad */
            @media (max-width: 768px) {
                .main-content-wrapper {
                    flex-direction: column; /* Apila las columnas en pantallas pequeñas */
                    padding: 10px;
                }
                .groups-sidebar {
                    margin-right: 0;
                    margin-bottom: 20px; /* Espacio entre la sidebar y las tarjetas */
                    width: auto; /* Permite que la sidebar ocupe todo el ancho */
                    position: static; /* Desactiva sticky en móvil */
                }
                .dashboard-grid {
                    grid-template-columns: 1fr; /* Una columna en móvil */
                }
            </style>--%>
    </head>
    <body>
        <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
        <%-- INCLUIR LA BARRA DE NAVEGACIÓN COMÚN (adaptada para usuarios logeados) --%>
        <%@ include file="/jsp/common/navbar.jsp" %>

        <div class="dashboard-header">
            <h1><s:text name="dashboard.welcome"><s:param value="username"/></s:text></h1>
            <p><s:text name="dashboard.intro"/></p>
        </div>

        <div class="main-content-wrapper"> <%-- Nuevo contenedor para el layout de 2 columnas --%>
            <%-- Columna de Grupos de Estudio (Sidebar) --%>
            <div class="groups-sidebar">
                <h3><s:text name="dashboard.sidebar.groups.title"/></h3>
                <s:if test="userGroups != null && !userGroups.isEmpty()">
                    <ul>
                        <s:iterator value="userGroups" status="groupStatus">
                            <li>
                                <s:url action="viewGroup" var="viewGroupUrl">
                                    <s:param name="groupId" value="id"/>
                                </s:url>
                                <s:a href="%{viewGroupUrl}">
                                    <s:property value="nombre"/>
                                </s:a>
                            </li>
                        </s:iterator>
                    </ul>
                </s:if>
                <s:else>
                    <p class="no-groups-message"><s:text name="dashboard.sidebar.groups.noGroups"/></p>
                </s:else>
                <s:url action="showCreateGroupForm" var="createGroupUrl"/>
                <s:a href="%{createGroupUrl}" cssClass="btn-create-group"><s:text name="dashboard.sidebar.groups.createButton"/></s:a>
                </div>
            <%-- Contenido principal del Dashboard (Tarjetas) --%>
            <div class="main-dashboard-content">
                <s:if test="hasActionMessages()">
                    <div class="alert alert-success">
                        <strong><s:text name="alert.success"/></strong> <s:actionmessage/>
                    </div>
                </s:if>
                <s:if test="hasActionErrors()">
                    <div class="alert alert-danger action-error-list">
                        <strong><s:text name="alert.danger"/></strong> <s:actionerror escape="false"/>
                    </div>
                </s:if>
                <div class="dashboard-grid">

                    <%-- Nueva tarjeta para Iniciar Sesión Rápida --%>
                    <div class="card">
                        <h3><s:text name="card.quickSession.title"/></h3>
                        <p><s:text name="card.quickSession.description"/></p>
                        <s:url action="showQuickSessionForm" var="quickSessionUrl"/>
                        <button type="button" id="openQuickSessionModalBtn" class="btn-card btn-quick-session"><s:text name="card.quickSession.button"/></button>
                    </div>

                    <div class="card">
                        <h3><s:text name="card.deliverables.title"/></h3>
                        <p><s:text name="card.deliverables.description"/></p>
                        <s:url action="manageDeliverables" var="viewTasksUrl"/>
                        <s:a href="%{viewTasksUrl}" cssClass="btn-card"><s:text name="card.deliverables.button"/></s:a>
                        </div>

                        <div class="card">
                            <h3><s:text name="card.studySessions.title"/></h3>
                        <p><s:text name="card.studySessions.description"/></p>
                        <s:url action="manageStudySessions" var="studySessionsUrl"/>
                        <s:a href="%{studySessionsUrl}" cssClass="btn-card"><s:text name="card.studySessions.button"/></s:a>
                        </div>

                        <div class="card">
                            <h3><s:text name="card.gamification.title"/></h3>
                        <p><s:text name="card.gamification.description"/></p>
                        <s:url action="viewGamification" var="gamificationUrl"/>
                        <s:a href="%{gamificationUrl}" cssClass="btn-card"><s:text name="card.gamification.button"/></s:a>
                        </div>

                        <div class="card">
                            <h3><s:text name="card.chats.title"/></h3>
                        <p><s:text name="card.chats.description"/></p>
                        <s:url action="friendsAndMessages" var="friendsAndMessagesUrl"/>
                        <s:a href="%{friendsAndMessagesUrl}" cssClass="btn-card"><s:text name="card.chats.button"/></s:a>
                        </div>

                        <div class="card">
                            <h3><s:text name="card.profile.title"/></h3>
                        <p><s:text name="card.profile.description"/></p>
                        <s:url action="editProfile" var="profileUrl"/>
                        <s:a href="%{profileUrl}" cssClass="btn-card"><s:text name="card.profile.button"/></s:a>
                        </div>

                    </div>
                </div>
            <%--<div class="container">
                <s:if test="hasActionMessages()">
                    <div class="alert alert-success">
                        <strong><s:text name="alert.success"/></strong> <s:actionmessage/>
                    </div>
                </s:if>
                <s:if test="hasActionErrors()">
                    <div class="alert alert-danger action-error-list">
                        <strong><s:text name="alert.danger"/></strong> <s:actionerror/>
                    </div>
                </s:if>
                <div class="dashboard-grid">

            <%-- Nueva tarjeta para Iniciar Sesión Rápida 
            <div class="card">
                <h3><s:text name="card.quickSession.title"/></h3>
                <p><s:text name="card.quickSession.description"/></p>
                <s:url action="showQuickSessionForm" var="quickSessionUrl"/>
                <button type="button" id="openQuickSessionModalBtn" class="btn-card btn-quick-session"><s:text name="card.quickSession.button"/></button>
            </div>

                    <div class="card">
                        <h3><s:text name="card.deliverables.title"/></h3>
                        <p><s:text name="card.deliverables.description"/></p>
                        <s:url action="manageDeliverables" var="viewTasksUrl"/>
                        <s:a href="%{viewTasksUrl}" cssClass="btn-card"><s:text name="card.deliverables.button"/></s:a>
                        </div>

                        <div class="card">
                            <h3><s:text name="card.studySessions.title"/></h3>
                        <p><s:text name="card.studySessions.description"/></p>
                        <s:url action="manageStudySessions" var="studySessionsUrl"/>
                        <s:a href="%{studySessionsUrl}" cssClass="btn-card"><s:text name="card.studySessions.button"/></s:a>
                        </div>

                        <div class="card">
                            <h3><s:text name="card.groups.title"/></h3>
                        <p><s:text name="card.groups.description"/></p>
                        <s:url action="manageStudyGroups" var="studyGroupsUrl"/>
                        <s:a href="%{studyGroupsUrl}" cssClass="btn-card"><s:text name="card.groups.button"/></s:a>
                        </div>

                        <div class="card">
                            <h3><s:text name="card.gamification.title"/></h3>
                        <p><s:text name="card.gamification.description"/></p>
                        <s:url action="viewGamification" var="gamificationUrl"/>
                        <s:a href="%{gamificationUrl}" cssClass="btn-card"><s:text name="card.gamification.button"/></s:a>
                        </div>

                        <div class="card">
                            <h3><s:text name="card.chats.title"/></h3>
                        <p><s:text name="card.chats.description"/></p>
                        <s:url action="friendsAndMessages" var="friendsAndMessagesUrl"/>
                        <s:a href="%{friendsAndMessagesUrl}" cssClass="btn-card"><s:text name="card.chats.button"/></s:a>
                        </div>

                        <div class="card">
                            <h3><s:text name="card.profile.title"/></h3>
                        <p><s:text name="card.profile.description"/></p>
                        <s:url action="editProfile" var="profileUrl"/>
                        <s:a href="%{profileUrl}" cssClass="btn-card"><s:text name="card.profile.button"/></s:a>
                        </div>

                    </div>
                </div>--%>

            <div id="quickSessionModal" class="modal">
                <div class="modal-content">
                    <h3><s:text name="modal.quickSession.title"/></h3>
                    <%-- Mostrar errores de acción si existen --%>
                    <s:if test="hasActionErrors()">
                        <div class="alert alert-danger action-error-list">
                            <strong><s:text name="alert.danger"/></strong> <s:actionerror escape="false"/>
                        </div>
                    </s:if>
                    <s:form action="startQuickSession" method="post" id="quickSessionForm">
                        <div class="form-group">
                            <label for="sesion_titulo"><s:text name="modal.quickSession.form.title"/></label>
                            <s:textfield name="sesion_titulo" id="sesion_titulo" cssClass="form-control" readonly="true" value="%{sesion.titulo}"/>
                        </div>
                        <div class="form-group">
                            <label for="currentDateTimeFormatted"><s:text name="modal.quickSession.form.currentDateTime"/></label>
                            <s:textfield name="currentDateTimeFormatted" id="currentDateTimeFormatted" cssClass="form-control" readonly="true" value="%{currentDateTimeFormatted}"/>
                        </div>
                        <div class="form-group">
                            <label for="sesion_descripcion"><s:text name="modal.quickSession.form.description"/></label>
                            <s:textarea name="sesion.descripcion" id="sesion_descripcion" cssClass="form-control" rows="3" readonly="true" value="%{sesion.descripcion}"/>
                        </div>
                        <div class="form-group">
                            <label for="sesion_duracionPlanificadaMinutos"><s:text name="modal.quickSession.form.duration"/></label>
                            <s:textfield name="duracionPlanificadaMinutosForm" id="sesion_duracionPlanificadaMinutos" type="number" cssClass="form-control" min="10" max="480" placeholder="Ej: 60" value="%{duracionPlanificadaMinutosForm}"/>
                        </div>
                        <div class="form-group">
                            <label for="sesion_tecnicaAplicadaId"><s:text name="modal.quickSession.form.studyTechnique"/></label>
                            <s:select name="sesion.tecnicaAplicadaId.id" id="sesion_tecnicaAplicadaId" list="tecnicasDisponibles" listKey="id" listValue="nombre"
                                      headerKey="" headerValue="%{getText('modal.quickSession.form.noTechnique')}" cssClass="form-control" value="%{sesion.tecnicaAplicadaId != null ? sesion.tecnicaAplicadaId.id : ''}"/>
                        </div>
                        <div class="form-group">
                            <label for="sesion_entregaAsociada"><s:text name="modal.quickSession.form.associatedDeliverable"/></label>
                            <s:select name="sesion.entregaAsociada.id" id="sesion_entregaAsociada" list="entregasDisponibles" listKey="id" listValue="titulo"
                                      headerKey="" headerValue="%{getText('modal.quickSession.form.noDeliverable')}" cssClass="form-control" value="%{sesion.entregaAsociada != null ? sesion.entregaAsociada.id : ''}"/>
                        </div>
                        <div class="form-group">
                            <label for="sesion_examenAsociado"><s:text name="modal.quickSession.form.associatedExam"/></label>
                            <s:select name="sesion.examenAsociado.id" id="sesion_examenAsociado" list="examenesDisponibles" listKey="id" listValue="nombre"
                                      headerKey="" headerValue="%{getText('modal.quickSession.form.noExam')}" cssClass="form-control" value="%{sesion.examenAsociado != null ? sesion.examenAsociado.id : ''}"/>
                        </div>
                        <%-- NUEVO: Campo para Notas Rápidas --%>
            <div class="form-group">
                <label for="notasRapidas"><s:text name="session.form.quickNotes"/></label>
                <s:textarea name="notasRapidas" id="notasRapidas" cssClass="form-control" rows="5" placeholder="%{getText('session.form.quickNotesPlaceholder')}" value="%{notasRapidas}"/>
            </div>

            <%-- NUEVO: Campo para Checklist (como JSON string) --%>
            <div class="form-group">
                <label for="checklist"><s:text name="session.form.checklist"/></label>
                <s:textarea name="checklist" id="checklist" cssClass="form-control" rows="5" placeholder="%{getText('session.form.checklistPlaceholder')}" value="%{checklist}"/>
                <small><s:text name="session.form.checklistHelp"/></small>
            </div>
                        <div class="modal-buttons">
                            <button type="button" class="btn btn-cancel" id="cancelQuickSessionBtn"><s:text name="modal.quickSession.button.cancel"/></button>
                            <button type="submit" class="btn btn-submit"><s:text name="modal.quickSession.button.start"/></button>
                        </div>
                    </s:form>
                </div>
            </div>
                    
            <%--<script type="text/javascript">
                document.addEventListener('DOMContentLoaded', function () {
                    const quickSessionModal = document.getElementById('quickSessionModal');
                    const openQuickSessionModalBtn = document.getElementById('openQuickSessionModalBtn');
                    const cancelQuickSessionBtn = document.getElementById('cancelQuickSessionBtn');
                    const quickSessionForm = document.getElementById('quickSessionForm');
                    const actionErrorListDiv = quickSessionModal.querySelector('.action-error-list');

                    // Helper function to populate select elements
                    function populateSelect(selectElement, items, idKey, valueKey, headerText, selectedId) {
                        selectElement.innerHTML = ''; // Clear existing options
                        const headerOption = document.createElement('option');
                        headerOption.value = '';
                        headerOption.textContent = headerText;
                        selectElement.appendChild(headerOption);

                        items.forEach(item => {
                            const option = document.createElement('option');
                            option.value = item[idKey];
                            option.textContent = item[valueKey];
                            if (selectedId !== null && selectedId !== undefined && String(item[idKey]) === String(selectedId)) {
                                option.selected = true;
                            }
                            selectElement.appendChild(option);
                        });
                    }

                    // Function to open the modal and fetch data
                    async function openQuickSessionModal() {
                        try {
                            // Ocultar errores anteriores
                            if (actionErrorListDiv) {
                                actionErrorListDiv.innerHTML = '';
                                actionErrorListDiv.style.display = 'none';
                            }

                            const response = await fetch('<s:url action="showQuickSessionForm"/>');
                            if (!response.ok) {
                                const errorData = await response.json().catch(() => ({}));
                                let errorMessage = '<s:text name="modal.quickSession.error.unknownLoad"/>';
                                if (errorData && errorData.actionErrors && errorData.actionErrors.length > 0) {
                                    errorMessage = errorData.actionErrors.join('; ');
                                } else if (errorData && errorData.message) {
                                    errorMessage = errorData.message;
                                }
                                throw new Error(errorMessage);
                            }

                            const rawData = await response.json();
                            const data = rawData[0]; // Acceder al primer objeto del array

                            // Manejar y mostrar actionErrors si los hay en el JSON recibido
                            if (data.actionErrors && data.actionErrors.length > 0) {
                                if (actionErrorListDiv) {
                                    actionErrorListDiv.innerHTML = '<ul>' + data.actionErrors.map(err => `<li>${err}</li>`).join('') + '</ul>';
                                    actionErrorListDiv.style.display = 'block';
                                }
                            }

                            // Populate non-editable fields
                            document.getElementById('sesion_titulo').value = data.sesion.titulo;
                            document.getElementById('currentDateTimeFormatted').value = data.currentDateTimeFormatted;
                            document.getElementById('sesion_descripcion').value = data.sesion.descripcion;
                            document.getElementById('sesion_duracionPlanificadaMinutos').value = data.sesion.duracionPlanificadaMinutos;

                            // Populate and select dropdowns
                            populateSelect(document.getElementById('sesion_tecnicaAplicadaId'), data.tecnicasDisponibles, 'id', 'nombre', '<s:text name="modal.quickSession.form.noTechnique"/>', data.sesion.tecnicaAplicadaId ? data.sesion.tecnicaAplicadaId.id : '');
                            populateSelect(document.getElementById('sesion_entregaAsociada'), data.entregasDisponibles, 'id', 'titulo', '<s:text name="modal.quickSession.form.noDeliverable"/>', data.sesion.entregaAsociada ? data.sesion.entregaAsociada.id : '');
                            populateSelect(document.getElementById('sesion_examenAsociado'), data.examenesDisponibles, 'id', 'nombre', '<s:text name="modal.quickSession.form.noExam"/>', data.sesion.examenAsociado ? data.sesion.examenAsociado.id : '');

                            quickSessionModal.style.display = 'flex';

                        } catch (error) {
                            console.error("Error al cargar el formulario de sesión rápida:", error);
                            if (actionErrorListDiv && actionErrorListDiv.style.display !== 'block') {
                                alert(`<s:text name="modal.quickSession.error.failedLoad"/> ${error.message}`);
                                                }
                                            }
                                        }

                                        // Function to close the modal
                                        function closeQuickSessionModal() {
                                            quickSessionModal.style.display = 'none';
                                            // Clear any previous errors when closing
                                            if (actionErrorListDiv) {
                                                actionErrorListDiv.innerHTML = '';
                                                actionErrorListDiv.style.display = 'none';
                                            }
                                        }

                                        // Event listener to open modal
                                        openQuickSessionModalBtn.addEventListener('click', function (event) {
                                            event.preventDefault();
                                            openQuickSessionModal();
                                        });

                                        // Event listener to close modal
                                        cancelQuickSessionBtn.addEventListener('click', closeQuickSessionModal);
                                        window.addEventListener('click', function (event) {
                                            if (event.target == quickSessionModal) {
                                                closeQuickSessionModal();
                                            }
                                        });

                                        // Check if there are Struts2 action errors after form submission
                <s:if test="hasActionErrors()">
                                        quickSessionModal.style.display = 'flex'; // Open the modal
                                        const errorListContainer = quickSessionModal.querySelector('.action-error-list');
                                        if (errorListContainer) {
                                            errorListContainer.style.display = 'block'; // Ensure error div is visible
                                        }
                </s:if>
                                    });
            </script>--%>
        </div>
        <footer>
            <s:text name="footer.copyright"/>
        </footer>
            <script type="text/javascript">
                document.addEventListener('DOMContentLoaded', function () {
                    const quickSessionModal = document.getElementById('quickSessionModal');
                    const openQuickSessionModalBtn = document.getElementById('openQuickSessionModalBtn');
                    const cancelQuickSessionBtn = document.getElementById('cancelQuickSessionBtn');
                    const quickSessionForm = document.getElementById('quickSessionForm');
                    const actionErrorListDiv = quickSessionModal.querySelector('.alert-danger'); // Referencia al div de errores

                    // Helper function to populate select elements
                    function populateSelect(selectElement, items, idKey, valueKey, headerText, selectedId) {
                        selectElement.innerHTML = ''; // Clear existing options
                        const headerOption = document.createElement('option');
                        headerOption.value = '';
                        headerOption.textContent = headerText;
                        selectElement.appendChild(headerOption);

                        items.forEach(item => {
                            const option = document.createElement('option');
                            option.value = item[idKey];
                            option.textContent = item[valueKey];
                            if (selectedId !== null && selectedId !== undefined && String(item[idKey]) === String(selectedId)) {
                                option.selected = true;
                            }
                            selectElement.appendChild(option);
                        });
                    }

                    // Function to open the modal and fetch data
                    async function openQuickSessionModal() {
                        try {
                            // Ocultar errores anteriores
                            if (actionErrorListDiv) {
                                actionErrorListDiv.innerHTML = '';
                                actionErrorListDiv.style.display = 'none';
                            }

                            const response = await fetch('<s:url action="showQuickSessionForm"/>');
                            if (!response.ok) {
                                const errorData = await response.json().catch(() => ({}));
                                let errorMessage = '<s:text name="modal.quickSession.error.unknownLoad"/>';
                                if (errorData && errorData.actionErrors && errorData.actionErrors.length > 0) {
                                    errorMessage = errorData.actionErrors.join('; ');
                                } else if (errorData && errorData.message) {
                                    errorMessage = errorData.message;
                                }
                                // Usar un div de mensaje en lugar de alert()
                                const dashboardErrorDiv = document.querySelector('.main-dashboard-content .alert-danger');
                                if (dashboardErrorDiv) {
                                    dashboardErrorDiv.innerHTML = `<strong><s:text name="alert.danger"/></strong> ${errorMessage}`;
                                    dashboardErrorDiv.style.display = 'block';
                                } else {
                                    console.error("No se encontró el div de errores del dashboard para mostrar el mensaje.");
                                }
                                throw new Error(errorMessage); // Lanzar el error para detener el proceso
                            }

                            const rawData = await response.json();
                            const data = rawData[0]; // Acceder al primer objeto del array

                            // Manejar y mostrar actionErrors si los hay en el JSON recibido
                            if (data.actionErrors && data.actionErrors.length > 0) {
                                if (actionErrorListDiv) {
                                    actionErrorListDiv.innerHTML = '<strong><s:text name="alert.danger"/></strong> <ul>' + data.actionErrors.map(err => `<li>${err}</li>`).join('') + '</ul>';
                                    actionErrorListDiv.style.display = 'block';
                                }
                            }

                            // Populate non-editable fields
                            document.getElementById('sesion_titulo').value = data.sesion.titulo;
                            document.getElementById('currentDateTimeFormatted').value = data.currentDateTimeFormatted;
                            document.getElementById('sesion_descripcion').value = data.sesion.descripcion;
                            document.getElementById('sesion_duracionPlanificadaMinutos').value = data.sesion.duracionPlanificadaMinutos;

                            // Populate and select dropdowns
                            populateSelect(document.getElementById('sesion_tecnicaAplicadaId'), data.tecnicasDisponibles, 'id', 'nombre', '<s:text name="modal.quickSession.form.noTechnique"/>', data.sesion.tecnicaAplicadaId ? data.sesion.tecnicaAplicadaId.id : '');
                            populateSelect(document.getElementById('sesion_entregaAsociada'), data.entregasDisponibles, 'id', 'titulo', '<s:text name="modal.quickSession.form.noDeliverable"/>', data.sesion.entregaAsociada ? data.sesion.entregaAsociada.id : '');
                            populateSelect(document.getElementById('sesion_examenAsociado'), data.examenesDisponibles, 'id', 'nombre', '<s:text name="modal.quickSession.form.noExam"/>', data.sesion.examenAsociado ? data.sesion.examenAsociado.id : '');

                            quickSessionModal.style.display = 'flex';

                        } catch (error) {
                            console.error("Error al cargar el formulario de sesión rápida:", error);
                            // El mensaje de error ya se mostró en el div del dashboard o en el modal si hubo actionErrors del JSON.
                            // Evitamos el alert() aquí.
                        }
                    }

                    // Function to close the modal
                    function closeQuickSessionModal() {
                        quickSessionModal.style.display = 'none';
                        // Clear any previous errors when closing
                        if (actionErrorListDiv) {
                            actionErrorListDiv.innerHTML = '';
                            actionErrorListDiv.style.display = 'none';
                        }
                    }

                    // Event listener to open modal
                    openQuickSessionModalBtn.addEventListener('click', function (event) {
                        event.preventDefault();
                        openQuickSessionModal();
                    });

                    // Event listener to close modal
                    cancelQuickSessionBtn.addEventListener('click', closeQuickSessionModal);
                    window.addEventListener('click', function (event) {
                        if (event.target == quickSessionModal) {
                            closeQuickSessionModal();
                        }
                    });

                    // Check if there are Struts2 action errors after form submission
                <s:if test="hasActionErrors()">
                    quickSessionModal.style.display = 'flex'; // Open the modal
                    const errorListContainer = quickSessionModal.querySelector('.alert-danger');
                    if (errorListContainer) {
                        errorListContainer.style.display = 'block'; // Ensure error div is visible
                    }
                </s:if>
                });
            </script>
    </body>
</html>
