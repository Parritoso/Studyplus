<%-- 
    Document   : viewGroup
    Created on : 03-jun-2025, 13:09:51
    Author     : Parri
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><s:text name="group.page.title"/>: <s:property value="grupo.nombre"/></title>
        <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/viewGroup.css">
        <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/viewGroup.css" disabled>
    </head>
    <body>
        <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
        <%@ include file="/jsp/common/navbar.jsp" %>

        <div class="group-container">
            <%-- Columna de Botones (Izquierda del todo) --%>
            <div class="column buttons-column">
                <h3><s:text name="group.buttons.title"/></h3>
                <button type="button" id="openPlanSessionModalBtn" class="btn btn-plan-session"><s:text name="group.buttons.planSession"/></button>
                <%-- Otros botones irán aquí, controlados por JS según el estado del grupo --%>
                <button type="button" id="openInviteUserModalBtn" class="btn btn-primary"><s:text name="group.buttons.inviteUser"/></button>
            </div>

            <%-- Columna Central Izquierda (Placeholder, Notas/Checklist/Sesiones) --%>
            <div class="column main-left-column">
                <div class="placeholder-top">
                    <s:text name="group.placeholder.top"/>
                </div>
                <div class="notes-checklist-sessions-bottom">
                    <h3><s:text name="group.notesChecklistSessions.title"/></h3>

                    <%-- Sección de Notas Rápidas Globales --%>
                    <div class="notes-section">
                        <h4><s:text name="group.notes.sectionTitle"/></h4>
                        <textarea id="globalNotesTextarea" class="form-control" rows="8" placeholder="<s:text name='group.notes.placeholder'/>"><s:property value="globalNotes"/></textarea>
                        <button type="button" class="btn btn-save-notes" id="saveGlobalNotesBtn"><s:text name="group.notes.saveButton"/></button>
                        <div id="notesFeedback" class="feedback-message"></div>
                    </div>

                    <%-- Sección de Checklist Global --%>
                    <div class="checklist-section">
                        <h4><s:text name="group.checklist.sectionTitle"/></h4>
                        <ul id="globalChecklistItems" class="checklist-list">
                            <%-- Los ítems del checklist se renderizarán con JavaScript --%>
                        </ul>
                        <div class="add-checklist-item-form">
                            <input type="text" id="newGlobalChecklistItemText" placeholder="<s:text name='group.checklist.addPlaceholder'/>" class="form-control"/>
                            <button type="button" id="addGlobalChecklistItemBtn" class="btn"><s:text name="group.checklist.addButton"/></button>
                        </div>
                        <div id="checklistFeedback" class="feedback-message"></div>
                    </div>

                    <%-- Sección de Historial de Sesiones del Grupo --%>
                    <div class="session-history-section">
                        <h4><s:text name="group.sessionHistory.sectionTitle"/></h4>
                        <ul class="session-history-list">
                            <s:if test="groupSessions != null && !groupSessions.isEmpty()">
                                <s:iterator value="groupSessions" status="sessionStatus">
                                    <li>
                                        <strong><s:property value="titulo"/></strong>
                                        <span><s:text name="group.sessionHistory.plannedDuration"/>: <s:property value="duracionPlanificadaMinutos"/> <s:text name="session.active.minutes"/></span>
                                        <s:if test="fechaSesionPlanificada != null">
                                            <span><s:text name="group.sessionHistory.plannedFor"/>: <s:date name="fechaSesionPlanificada" format="dd/MM/yyyy HH:mm"/></span>
                                        </s:if>
                                        <s:if test="tecnicaAplicadaId != null">
                                            <span><s:text name="group.sessionHistory.technique"/>: <s:property value="tecnicaAplicadaId.nombre"/></span>
                                        </s:if>
                                        <span><s:text name="group.sessionHistory.status"/>: <s:property value="estadoString"/></span>
                                        <s:if test="fechaInicioReal != null">
                                            <span><s:text name="group.sessionHistory.started"/>: <s:date name="fechaInicioReal" format="dd/MM/yyyy HH:mm"/></span>
                                            <s:if test="fechaFinReal != null">
                                                <span><s:text name="group.sessionHistory.ended"/>: <s:date name="fechaFinReal" format="dd/MM/yyyy HH:mm"/></span>
                                                <span><s:text name="group.sessionHistory.realDuration"/>: <s:property value="duracionRealMinutos"/> <s:text name="session.active.minutes"/></span>
                                            </s:if>
                                        </s:if>
                                    </li>
                                </s:iterator>
                            </s:if>
                            <s:else>
                                <p><s:text name="group.sessionHistory.noSessions"/></p>
                            </s:else>
                        </ul>
                    </div>
                </div>
            </div>

            <%-- Columna de Chat --%>
            <div class="column chat-column">
                <h3><s:text name="group.chat.title"/></h3>
                <div id="chatMessages" class="chat-messages">
                    <s:if test="chatMessages != null && !chatMessages.isEmpty()">
                        <s:iterator value="chatMessages" status="msgStatus">
                            <div class="chat-message <s:if test="emisorUsuarioId.id == #session.loggedInUser.id">sent</s:if><s:else>received</s:else>">
                                <div class="sender"><s:property value="emisorUsuarioId.username"/></div>
                                <div class="content"><s:property value="contenido"/></div>
                                <div class="timestamp"><s:date name="fechaEnvio" format="HH:mm"/></div>
                            </div>
                        </s:iterator>
                    </s:if>
                    <s:else>
                        <p style="text-align: center; color: #888;"><s:text name="group.chat.noMessages"/></p>
                    </s:else>
                </div>
                <div class="chat-input-form">
                    <input type="text" id="newMessageContent" placeholder="<s:text name='group.chat.inputPlaceholder'/>" class="form-control"/>
                    <button type="button" id="sendMessageBtn" class="btn"><s:text name="group.chat.sendButton"/></button>
                </div>
                <div id="chatFeedback" class="feedback-message"></div>
            </div>

            <%-- Columna de Participantes --%>
            <div class="column participants-column">
                <h3><s:text name="group.participants.title"/></h3>
                <ul class="participants-list">
                    <s:if test="participantes != null && !participantes.isEmpty()">
                        <s:iterator value="participantes">
                            <li><strong><s:property value="nombre"/></strong></li>
                        </s:iterator>
                    </s:if>
                    <s:else>
                        <p><s:text name="group.participants.noParticipants"/></p>
                    </s:else>
                </ul>
            </div>
        </div>

        <%-- Modal de Planificación de Sesión --%>
        <div id="planSessionModal" class="modal">
            <div class="modal-content">
                <h3><s:text name="group.planSession.modalTitle"/></h3>
                <s:form action="planGroupStudySession" method="post" id="planSessionForm">
                    <s:hidden name="groupId" value="%{groupId}"/> <%-- Pasar el ID del grupo --%>

                    <div class="form-group">
                        <label for="sesion_titulo"><s:text name="session.form.title"/></label>
                        <s:textfield name="sesion.titulo" id="sesion_titulo_modal" cssClass="form-control" placeholder="%{getText('session.form.titlePlaceholder')}" value="%{sesion.titulo}"/>
                        <s:fielderror fieldName="sesion.titulo" cssClass="error-message"/>
                    </div>
                    <div class="form-group">
                        <label for="sesion_descripcion"><s:text name="session.form.description"/></label>
                        <s:textarea name="sesion.descripcion" id="sesion_descripcion_modal" cssClass="form-control" rows="3" placeholder="%{getText('session.form.descriptionPlaceholder')}" value="%{sesion.descripcion}"/>
                        <s:fielderror fieldName="sesion.descripcion" cssClass="error-message"/>
                    </div>
                    <div class="form-group">
                        <label for="sesion_duracionPlanificadaMinutos"><s:text name="session.form.duration"/></label>
                        <s:textfield name="sesion.duracionPlanificadaMinutos" id="sesion_duracionPlanificadaMinutos_modal" type="number" cssClass="form-control" min="10" max="480" placeholder="Ej: 60" value="%{sesion.duracionPlanificadaMinutos}"/>
                        <s:fielderror fieldName="sesion.duracionPlanificadaMinutos" cssClass="error-message"/>
                    </div>

                    <div class="form-group">
                        <label for="plannedDateStr_modal"><s:text name="session.form.plannedDate"/></label>
                        <s:textfield name="plannedDateStr" id="plannedDateStr_modal" type="date" cssClass="form-control" value="%{plannedDateStr}"/>
                    </div>
                    <div class="form-group">
                        <label for="plannedTimeStr_modal"><s:text name="session.form.plannedTime"/></label>
                        <s:textfield name="plannedTimeStr" id="plannedTimeStr_modal" type="time" cssClass="form-control" value="%{plannedTimeStr}"/>
                    </div>

                    <div class="form-group">
                        <label for="sesion_tecnicaAplicadaId_modal"><s:text name="session.form.studyTechnique"/></label>
                        <s:select name="sesion.tecnicaAplicadaId.id" id="sesion_tecnicaAplicadaId_modal" list="tecnicasDisponibles" listKey="id" listValue="nombre"
                                      headerKey="" headerValue="%{getText('modal.quickSession.form.noTechnique')}" cssClass="form-control" value="%{sesion.tecnicaAplicadaId != null ? sesion.tecnicaAplicadaId.id : ''}"/>
                    </div>
                    <div class="form-group">
                        <label for="sesion_entregaAsociada_modal"><s:text name="session.form.associatedDeliverable"/></label>
                        <s:select name="sesion.entregaAsociada.id" id="sesion_entregaAsociada_modal" list="entregasDisponibles" listKey="id" listValue="titulo"
                                      headerKey="" headerValue="%{getText('modal.quickSession.form.noDeliverable')}" cssClass="form-control" value="%{sesion.entregaAsociada != null ? sesion.entregaAsociada.id : ''}"/>
                    </div>
                    <div class="form-group">
                        <label for="sesion_examenAsociado_modal"><s:text name="session.form.associatedExam"/></label>
                        <s:select name="sesion.examenAsociado.id" id="sesion_examenAsociado_modal" list="examenesDisponibles" listKey="id" listValue="nombre"
                                      headerKey="" headerValue="%{getText('modal.quickSession.form.noExam')}" cssClass="form-control" value="%{sesion.examenAsociado != null ? sesion.examenAsociado.id : ''}"/>
                    </div>

                    <div id="planSessionFeedback" class="feedback-message"></div>

                    <div class="modal-buttons">
                        <button type="button" class="btn btn-cancel-modal" id="cancelPlanSessionBtn"><s:text name="session.form.cancelButton"/></button>
                        <button type="submit" class="btn btn-submit-modal"><s:text name="group.planSession.button"/></button>
                    </div>
                </s:form>
            </div>
        </div>

        <%-- NUEVO MODAL PARA INVITAR USUARIOS --%>
                <div id="inviteUserModal" class="modal">
                    <div class="modal-content">
                        <span class="close-button" id="cancelInviteUserBtn">&times;</span>
                        <h3><s:text name="group.inviteModal.title"/></h3>
                        <section id="searchUsersSection">
                            <h3 class="section-title"><s:text name="group.inviteModal.searchUsersTitle"/></h3>
                            <div class="form-group">
                                <input type="text" id="searchQuery" class="form-control" placeholder="<s:text name='group.inviteModal.searchPlaceholder'/>"/>
                            </div>
                            <button class="btn btn-primary" id="searchUsersBtn"><s:text name="group.inviteModal.searchButton"/></button>
                            <div id="searchResults" class="user-list">
                                </div>
                            <div id="inviteFeedback" class="feedback-message"></div>
                        </section>
                    </div>
                </div>

        <footer>
            © 2025 Estudy+. Todos los derechos reservados.
        </footer>

        <script type="text/javascript">
            document.addEventListener('DOMContentLoaded', function () {
                const groupId = <s:property value="groupId"/>;
                let currentLoggedInUserId = <s:property value="#session.loggedInUser.id"/>;
                const chatMessages = document.getElementById('chatMessages');
                const newMessageContentInput = document.getElementById('newMessageContent');
                const sendMessageBtn = document.getElementById('sendMessageBtn');
                const chatFeedback = document.getElementById('chatFeedback');
                let lastMessageId = <s:property value="lastMessageId != null ? lastMessageId : 0"/>;
                let chatPollingInterval;

                // --- Global Notes elements ---
                const globalNotesTextarea = document.getElementById('globalNotesTextarea');
                const saveGlobalNotesBtn = document.getElementById('saveGlobalNotesBtn');
                const notesFeedback = document.getElementById('notesFeedback');

                // --- Global Checklist elements ---
                const globalChecklistItemsUl = document.getElementById('globalChecklistItems');
                const newGlobalChecklistItemText = document.getElementById('newGlobalChecklistItemText');
                const addGlobalChecklistItemBtn = document.getElementById('addGlobalChecklistItemBtn');
                const checklistFeedback = document.getElementById('checklistFeedback');
                let currentGlobalChecklist = [];
                async function initChecklist() {
                    try {
                        const initialChecklistJson = '<s:property value="globalChecklist" escape="false"/>';
                        if (initialChecklistJson && initialChecklistJson.trim() !== '' && initialChecklistJson !== '[]') {
                            currentGlobalChecklist = JSON.parse(initialChecklistJson);
                        } else {
                            const url = '<s:url action="getGlobalChecklistAjax"/>'; // Nuevo endpoint para obtener la checklist
                            const formData = new URLSearchParams();
                            formData.append('groupId', groupId);

                            try {
                                const response = await fetch(url, {
                                    method: 'POST',
                                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                                    body: formData
                                });
                                const data = await response.json();
                                if(data.success){
                                    currentGlobalChecklist = JSON.parse(data.data.checklist);
                                    renderGlobalChecklist();
                                }
                            } catch (error) {
                                console.error('Error fetching global checklist:', error);
                                showFeedback(checklistFeedback, '<s:text name="group.checklist.loadUnexpectedError"/>', 'error');
                            }
                        }
                    } catch (e) {
                        console.error("Error parsing initial global checklist JSON:", e);
                        // currentGlobalChecklist remains empty array
                    }
                    // Start polling for new messages every 3 seconds
                    chatPollingInterval = setInterval(pollNewMessages, 5000);
                }
                initChecklist();

                // --- Plan Session Modal elements ---
                const planSessionModal = document.getElementById('planSessionModal');
                const openPlanSessionModalBtn = document.getElementById('openPlanSessionModalBtn');
                const cancelPlanSessionBtn = document.getElementById('cancelPlanSessionBtn');
                const planSessionForm = document.getElementById('planSessionForm');
                const planSessionFeedback = document.getElementById('planSessionFeedback');
                
                // --- Invite User Modal elements ---
                const inviteUserModal = document.getElementById('inviteUserModal');
                const openInviteUserModalBtn = document.getElementById('openInviteUserModalBtn');
                const cancelInviteUserBtn = document.getElementById('cancelInviteUserBtn');
                const searchQueryInput = document.getElementById('searchQuery');
                const searchUsersBtn = document.getElementById('searchUsersBtn');
                const searchResultsDiv = document.getElementById('searchResults');
                const inviteFeedback = document.getElementById('inviteFeedback');

                // Helper function to show feedback messages
                function showFeedback(element, message, type) {
                    element.textContent = message;
                    element.className = `feedback-message ${type}`;
                    setTimeout(() => element.textContent = '', 3000); // Clear after 3 seconds
                }

                // --- CHAT LÓGICA ---
                function renderChatMessage(messages) {
                    /*const isSent = message.emisorId.id === loggedInUserId;
                    const messageDiv = document.createElement('div');
                    messageDiv.className = (isSent ? 'chat-message sent' : 'chat-message received');
                    
                    const senderDiv = document.createElement('div');
                    senderDiv.className = 'sender';
                    senderDiv.textContent = message.emisorId.nombre;
                    messageDiv.appendChild(senderDiv);

                    const contentDiv = document.createElement('div');
                    contentDiv.className = 'content';
                    contentDiv.textContent = message.contenido;
                    messageDiv.appendChild(contentDiv);

                    const timestampDiv = document.createElement('div');
                    timestampDiv.className = 'timestamp';
                    const date = new Date(message.fechaEnvio);
                    timestampDiv.textContent = `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
                    messageDiv.appendChild(timestampDiv);

                    chatMessagesDiv.appendChild(messageDiv);
                    chatMessagesDiv.scrollTop = chatMessagesDiv.scrollHeight; // Scroll to bottom*/
                    chatMessages.innerHTML = ''; // Limpiar mensajes anteriores
                    if (!messages || messages.length === 0) {
                        chatMessages.innerHTML = '<p style="text-align: center; color: #888;">No hay mensajes en esta conversación.</p>';
                        return;
                    }

                    messages.forEach(msg => {
                        const div = document.createElement('div');
                        const isSentByUser = String(msg.emisorId.id) === String(currentLoggedInUserId);
                        div.className = isSentByUser ? 'chat-message sent' : 'chat-message received';
                        const senderName = msg.emisorId.id === currentLoggedInUserId ? 'Tú' : currentChatFriendName; // Asumiendo que el emisorId es el ID del usuario
                        let formattedTime = 'Hora desconocida';
                        if (msg.fechaEnvio) {
                            const date = new Date(msg.fechaEnvio);
                            if (!isNaN(date)) {
                                formattedTime = date.toLocaleTimeString();
                            }
                        }
                        div.innerHTML = `
                            <div class="message-sender">`+senderName+`</div>
                            <div class="message-content">`+msg.contenido+`</div>
                            <div class="message-time">`+formattedTime+`</div>
                        `;
                        chatMessages.appendChild(div);
                    });
                    chatMessages.scrollTop = chatMessages.scrollHeight; // Scroll al final
                }

                async function sendChatMessage() {
                    const content = newMessageContentInput.value.trim();
                    if (content === "") {
                        showFeedback(chatFeedback, '<s:text name="group.chat.emptyMessage"/>', 'warning');
                        return;
                    }

                    const url = '<s:url action="sendGroupMessageAjax"/>';
                    const formData = new URLSearchParams();
                    formData.append('groupId', groupId);
                    formData.append('newMessageContent', content);

                    try {
                        const response = await fetch(url, {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                            body: formData
                        });
                        const data = await response.json();

                        if (data.success) {
                            renderChatMessage(data.data); // Render the new message
                            newMessageContentInput.value = ''; // Clear input
                            lastMessageId = data.data.id; // Update last message ID
                            showFeedback(chatFeedback, '<s:text name="group.chat.messageSent"/>', 'success');
                        } else {
                            showFeedback(chatFeedback, `<s:text name="group.chat.sendMessageError"/>: ${data.message}`, 'error');
                        }
                    } catch (error) {
                        console.error('Error sending message:', error);
                        showFeedback(chatFeedback, '<s:text name="group.chat.sendMessageUnexpectedError"/>', 'error');
                    }
                }

                async function pollNewMessages() {
                    const url = '<s:url action="getNewGroupMessagesAjax"/>';
                    const formData = new URLSearchParams();
                    formData.append('groupId', groupId);
                    formData.append('lastMessageId', lastMessageId);

                    try {
                        const response = await fetch(url, {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                            body: formData
                        });
                        const data = await response.json();

                        if (data.success && data.data && data.data.length > 0) {
                            renderChatMessage(data.data);
                            //data.data.forEach(msg => renderChatMessage(msg));
                            lastMessageId = data.data[data.data.length - 1].id;
                        } else if (!data.success) {
                            console.error('Error polling messages:', data.message);
                            // Optionally show a discreet error, but don't spam the user
                        }
                    } catch (error) {
                        console.error('Error polling messages:', error);
                    }
                }

                sendMessageBtn.addEventListener('click', sendChatMessage);
                newMessageContentInput.addEventListener('keypress', function(event) {
                    if (event.key === 'Enter') {
                        event.preventDefault();
                        sendChatMessage();
                    }
                });

                // --- NOTAS RÁPIDAS GLOBALES LÓGICA ---
                saveGlobalNotesBtn.addEventListener('click', async function() {
                    const newNotes = globalNotesTextarea.value;
                    const url = '<s:url action="updateGlobalNotesAjax"/>';
                    const formData = new URLSearchParams();
                    formData.append('groupId', groupId);
                    formData.append('globalNotes', newNotes);

                    try {
                        const response = await fetch(url, {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                            body: formData
                        });
                        const data = await response.json();

                        if (data.success) {
                            showFeedback(notesFeedback, '<s:text name="group.notes.updateSuccess"/>', 'success');
                            // No need to update currentGlobalNotes as it's directly from textarea
                        } else {
                            showFeedback(notesFeedback, `<s:text name="group.notes.updateError"/>: ${data.message}`, 'error');
                        }
                    } catch (error) {
                        console.error('Error saving global notes:', error);
                        showFeedback(notesFeedback, '<s:text name="group.notes.updateUnexpectedError"/>', 'error');
                    }
                });

                // --- CHECKLIST GLOBAL LÓGICA ---
                function renderGlobalChecklist() {
                    globalChecklistItemsUl.innerHTML = ''; // Limpiar lista existente
                    currentGlobalChecklist.forEach((item, index) => {
                        const li = document.createElement('li');
                        li.className = 'checklist-item' + (item.completed ? ' completed' : '');
                        li.innerHTML = `
                            <input type="checkbox" id="global-checklist-item-`+index+`" `+(item.completed ? 'checked' : '')+`>
                            <label for="global-checklist-item-`+index+`">`+item.text+`</label>
                            <button type="button" class="delete-checklist-item" data-index="`+index+`">×</button>
                        `;
                        globalChecklistItemsUl.appendChild(li);

                        // Añadir listeners para toggle y delete
                        li.querySelector('input[type="checkbox"]').addEventListener('change', function() {
                            toggleGlobalChecklistItem(index);
                        });
                        li.querySelector('.delete-checklist-item').addEventListener('click', function() {
                            deleteGlobalChecklistItem(index);
                        });
                    });
                }

                async function updateGlobalChecklistInDb() {
                    const url = '<s:url action="updateGlobalChecklistAjax"/>';
                    const formData = new URLSearchParams();
                    formData.append('groupId', groupId);
                    formData.append('globalChecklist', JSON.stringify(currentGlobalChecklist));

                    try {
                        const response = await fetch(url, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            body: formData
                        });
                        const data = await response.json();

                        if (data.success) {
                            showFeedback(checklistFeedback, '<s:text name="group.checklist.updateSuccess"/>', 'success');
                        } else {
                            showFeedback(checklistFeedback, `<s:text name="group.checklist.updateError"/>: ${data.message}`, 'error');
                        }
                    } catch (error) {
                        console.error('Error saving global checklist:', error);
                        showFeedback(checklistFeedback, '<s:text name="group.checklist.updateUnexpectedError"/>', 'error');
                    }
                }

                function addGlobalChecklistItem() {
                    const text = newGlobalChecklistItemText.value.trim();
                    if (text) {
                        currentGlobalChecklist.push({ text: text, completed: false });
                        newGlobalChecklistItemText.value = '';
                        renderGlobalChecklist();
                        updateGlobalChecklistInDb();
                    } else {
                        showFeedback(checklistFeedback, '<s:text name="group.checklist.emptyItemAlert"/>', 'warning');
                    }
                }

                function toggleGlobalChecklistItem(index) {
                    if (index >= 0 && index < currentGlobalChecklist.length) {
                        currentGlobalChecklist[index].completed = !currentGlobalChecklist[index].completed;
                        renderGlobalChecklist();
                        updateGlobalChecklistInDb();
                    }
                }

                function deleteGlobalChecklistItem(index) {
                    if (index >= 0 && index < currentGlobalChecklist.length) {
                        if (confirm('<s:text name="group.checklist.confirmDeleteItem"/>')) {
                            currentGlobalChecklist.splice(index, 1);
                            renderGlobalChecklist();
                            updateGlobalChecklistInDb();
                        }
                    }
                }

                addGlobalChecklistItemBtn.addEventListener('click', addGlobalChecklistItem);
                newGlobalChecklistItemText.addEventListener('keypress', function(event) {
                    if (event.key === 'Enter') {
                        event.preventDefault();
                        addGlobalChecklistItem();
                    }
                });

                // --- MODAL DE PLANIFICACIÓN DE SESIÓN LÓGICA ---
                openPlanSessionModalBtn.addEventListener('click', async function() {
                    const url = '<s:url action="preparePlanGroupSessionModalAjax"/>';
                    const formData = new URLSearchParams();
                    formData.append('groupId', groupId);

                    try {
                        const response = await fetch(url, {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                            body: formData
                        });
                        const data = await response.json();

                        if (data.success) {
                            const modalData = data.data;
                            // Populate dropdowns in the modal
                            const tecnicasSelect = document.getElementById('sesion_tecnicaAplicadaId_modal');
                            const entregasSelect = document.getElementById('sesion_entregaAsociada_modal');
                            const examenesSelect = document.getElementById('sesion_examenAsociado_modal');

                            populateSelect(tecnicasSelect, modalData.tecnicasDisponibles, 'id', 'nombre', '<s:text name="session.form.noTechnique"/>');
                            populateSelect(entregasSelect, modalData.entregasDisponibles, 'id', 'titulo', '<s:text name="session.form.noDeliverable"/>');
                            populateSelect(examenesSelect, modalData.examenesDisponibles, 'id', 'nombre', '<s:text name="session.form.noExam"/>');

                            // Clear form fields and feedback
                            planSessionForm.reset();
                            planSessionFeedback.textContent = '';
                            planSessionFeedback.className = 'feedback-message';

                            planSessionModal.style.display = 'flex';
                        } else {
                            showFeedback(planSessionFeedback, `<s:text name="group.planSession.prepareError"/>: ${data.message}`, 'error');
                        }
                    } catch (error) {
                        console.error('Error preparing plan session modal:', error);
                        showFeedback(planSessionFeedback, '<s:text name="group.planSession.prepareUnexpectedError"/>', 'error');
                    }
                });

                function populateSelect(selectElement, items, idKey, valueKey, headerText) {
                    selectElement.innerHTML = '';
                    const headerOption = document.createElement('option');
                    headerOption.value = '0'; // Use 0 for "no selection"
                    headerOption.textContent = headerText;
                    selectElement.appendChild(headerOption);

                    items.forEach(item => {
                        const option = document.createElement('option');
                        option.value = item[idKey];
                        option.textContent = item[valueKey];
                        selectElement.appendChild(option);
                    });
                }

                cancelPlanSessionBtn.addEventListener('click', function() {
                    planSessionModal.style.display = 'none';
                });

                window.addEventListener('click', function(event) {
                    if (event.target == planSessionModal) {
                        planSessionModal.style.display = 'none';
                    }
                });
                
                /*async function fetchGlobalChecklist() {
                    showFeedback(checklistFeedback, '<s:text name="group.checklist.loading"/>', 'info'); // "Cargando checklist..."
                    const url = '<s:url action="getGlobalChecklistAjax"/>'; // Nuevo endpoint para obtener la checklist
                    const formData = new URLSearchParams();
                    formData.append('groupId', groupId);

                    try {
                        const response = await fetch(url, {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                            body: formData
                        });
                        const data = await response.json();

                        if (data.success && data.data) {
                            currentGlobalChecklist = data.data.checklist;
                            renderGlobalChecklist();
                            showFeedback(checklistFeedback, '', ''); // Clear loading message
                        } else {
                            showFeedback(checklistFeedback, `<s:text name="group.checklist.loadError"/>: ${data.message}`, 'error');
                            console.error('Error loading global checklist:', data.message);
                        }
                    } catch (error) {
                        console.error('Error fetching global checklist:', error);
                        showFeedback(checklistFeedback, '<s:text name="group.checklist.loadUnexpectedError"/>', 'error');
                    }
                }*/

                planSessionForm.addEventListener('submit', async function(event) {
                    event.preventDefault(); // Prevent default form submission

                    const url = '<s:url action="planGroupStudySession"/>';
                    const formData = new FormData(planSessionForm); // Use FormData for easier handling

                    try {
                        const response = await fetch(url, {
                            method: 'POST',
                            body: new URLSearchParams(formData) // Convert FormData to URLSearchParams for x-www-form-urlencoded
                        });
                        const data = await response.json();

                        if (data.success) {
                            showFeedback(planSessionFeedback, '<s:text name="group.planSession.success"/>', 'success');
                            planSessionForm.reset();
                            // Optionally, refresh the group sessions list or add the new session to the list
                            // For now, just close the modal and show success.
                            setTimeout(() => {
                                planSessionModal.style.display = 'none';
                                // Consider reloading the page or updating the sessions list dynamically
                                window.location.reload(); // Simple reload for now
                            }, 1500);
                        } else {
                            showFeedback(planSessionFeedback, `<s:text name="group.planSession.createError"/>: ${data.message}`, 'error');
                        }
                    } catch (error) {
                        console.error('Error planning group session:', error);
                        showFeedback(planSessionFeedback, '<s:text name="group.planSession.createUnexpectedError"/>', 'error');
                    }
                });
                
                // --- NUEVA LÓGICA DEL MODAL DE INVITACIÓN DE USUARIOS ---
                openInviteUserModalBtn.addEventListener('click', function() {
                    inviteUserModal.style.display = 'flex'; // Abre el modal
                    searchResultsDiv.innerHTML = ''; // Limpia resultados anteriores
                    searchQueryInput.value = ''; // Limpia el campo de búsqueda
                    inviteFeedback.textContent = ''; // Limpia el feedback
                });

                cancelInviteUserBtn.addEventListener('click', function() {
                    inviteUserModal.style.display = 'none'; // Cierra el modal
                });

                // Cierra el modal si se hace clic fuera del contenido
                window.addEventListener('click', function(event) {
                    if (event.target == planSessionModal) { // Ya existe para este modal
                        planSessionModal.style.display = 'none';
                    }
                    if (event.target == inviteUserModal) { // Añadido para el nuevo modal
                        inviteUserModal.style.display = 'none';
                    }
                });

                searchUsersBtn.addEventListener('click', searchUsers);
                searchQueryInput.addEventListener('keypress', function(event) {
                    if (event.key === 'Enter') {
                        event.preventDefault();
                        searchUsers();
                    }
                });

                async function searchUsers() {
                    const query = searchQueryInput.value.trim();
                    if (query.length < 3) { // Mínimo 3 caracteres para buscar
                        showFeedback(inviteFeedback, '<s:text name="group.inviteModal.searchQueryTooShort"/>', 'warning');
                        searchResultsDiv.innerHTML = '';
                        return;
                    }

                    showFeedback(inviteFeedback, '<s:text name="group.inviteModal.searching"/>', 'info');
                    searchResultsDiv.innerHTML = ''; // Limpiar resultados anteriores

                    const url = '<s:url action="searchUsersGroupAjax"/>'; // Endpoint para buscar usuarios
                    const formData = new URLSearchParams();
                    formData.append('searchQuery', query);
                    formData.append('groupId', groupId); // Para excluir usuarios ya en el grupo

                    try {
                        const response = await fetch(url, {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                            body: formData
                        });
                        const data = await response.json();

                        if (data.success && data.data && data.data.length > 0) {
                            showFeedback(inviteFeedback, '', ''); // Limpiar mensaje de carga
                            data.data.forEach(user => {
                                const userItem = document.createElement('div');
                                userItem.className = 'user-item';
                                userItem.innerHTML = `
                                    <span>`+user.nombre+` (`+user.email+`)</span>
                                    <button class="btn btn-invite" data-user-id="`+user.id+`"><s:text name="group.inviteModal.inviteButton"/></button>
                                `;
                                searchResultsDiv.appendChild(userItem);

                                userItem.querySelector('.btn-invite').addEventListener('click', function() {
                                    inviteUser(user.id, user.nombre);
                                });
                            });
                        } else if (data.success && data.data.length === 0) {
                            showFeedback(inviteFeedback, '<s:text name="group.inviteModal.noUsersFound"/>', 'info');
                        } else {
                            showFeedback(inviteFeedback, `<s:text name="group.inviteModal.searchError"/>: ${data.message}`, 'error');
                            console.error('Error searching users:', data.message);
                        }
                    } catch (error) {
                        console.error('Error fetching users:', error);
                        showFeedback(inviteFeedback, '<s:text name="group.inviteModal.searchUnexpectedError"/>', 'error');
                    }
                }

                async function inviteUser(userId, userName) {
                    if (!confirm('<s:text name="group.inviteModal.confirmInvitePrefix"/> ' + userName + '<s:text name="group.inviteModal.confirmInviteSuffix"/>')) {
                         return; // Si el usuario cancela la invitación
                    }

                    showFeedback(inviteFeedback, `<s:text name="group.inviteModal.sendingInvitePrefix"/> ${userName}...`, 'info');

                    const url = '<s:url action="sendGroupInvitationAjax"/>'; // Endpoint para enviar invitación
                    const formData = new URLSearchParams();
                    formData.append('groupId', groupId);
                    formData.append('invitedUserId', userId);

                    try {
                        const response = await fetch(url, {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                            body: formData
                        });
                        const data = await response.json();

                        if (data.success) {
                            showFeedback(inviteFeedback, `<s:text name="group.inviteModal.inviteSentSuccessPrefix"/> ${userName}<s:text name="group.inviteModal.inviteSentSuccessSuffix"/>`, 'success');
                            // Opcional: Eliminar al usuario de la lista de resultados para que no se le pueda invitar de nuevo
                            // renderizar nuevamente los resultados de búsqueda o eliminar el elemento específico
                            searchUsers(); // Recargar la lista para mostrar el estado actualizado
                        } else {
                            showFeedback(inviteFeedback, `<s:text name="group.inviteModal.inviteErrorPrefix"/> ${userName}: ${data.message}`, 'error');
                        }
                    } catch (error) {
                        console.error('Error inviting user:', error);
                        showFeedback(inviteFeedback, '<s:text name="group.inviteModal.inviteUnexpectedError"/>', 'error');
                    }
                }

                //fetchGlobalChecklist();
                // Initial render of checklist
                renderGlobalChecklist();
            });
        </script>
    </body>
</html>
