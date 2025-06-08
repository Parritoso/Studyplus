<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Amigos y Mensajes - Estudy+</title>
    <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/friends_and_messages.css">
    <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/friends_and_messages.css" disabled>
    <audio id="notificationSound" src="<s:url value='/public/notification.mp3'/>" preload="auto"></audio>
</head>
<body>
    <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
    <%@ include file="/jsp/common/navbar.jsp" %>

    <div class="main-container">
        <div class="left-panel">
            <div id="feedbackMessageContainer" class="message-box" style="display:none;"></div>

            <section id="searchUsersSection">
                <h3 class="section-title">Buscar Usuarios</h3>
                <div class="form-group">
                    <input type="text" id="searchQuery" class="form-control" placeholder="Nombre de usuario..."/>
                </div>
                <button class="btn" onclick="searchUsers()">Buscar</button>
                <div id="searchResults" class="user-list">
                    </div>
            </section>

            <section id="pendingRequestsSection" style="margin-top: 30px;">
                <h3 class="section-title">Solicitudes Pendientes (<span id="pendingRequestsCount">0</span>)</h3>
                <div id="pendingRequestsList" class="user-list">
                    </div>
            </section>

            <section id="friendsListSection" style="margin-top: 30px;">
                <h3 class="section-title">Mis Amigos (<span id="friendsCount">0</span>)</h3>
                <div id="friendsList" class="user-list">
                    </div>
            </section>

            <section id="conversationsListSection" style="margin-top: 30px;">
                <h3 class="section-title">Mis Conversaciones (<span id="conversationsCount">0</span>)</h3>
                <div id="conversationsList" class="user-list">
                    </div>
            </section>
        </div>

        <div class="right-panel" id="chatPanel">
            <div id="chatPlaceholder" style="text-align: center; padding-top: 100px; color: #888;">
                <h3>Selecciona un amigo o conversación para empezar a chatear.</h3>
                <p>Usa la sección de "Buscar Usuarios" para encontrar nuevos amigos.</p>
            </div>

            <div id="chatArea" style="display:none;">
                <div class="chat-header" id="chatHeader"></div>
                <div class="messages-display" id="messagesDisplay">
                    </div>
                <div class="message-input-area">
                    <input type="text" id="messageInput" class="form-control message-input" placeholder="Escribe tu mensaje..."/>
                    <button class="btn btn-small" onclick="sendMessage()">Enviar</button>
                </div>
            </div>
        </div>
    </div>

    <footer>
        &copy; 2025 Estudy+. Todos los derechos reservados.
    </footer>

    <script type="text/javascript">
        // Variables globales para el estado del chat
        let currentLoggedInUserId = <s:property value="#session.loggedInUser.id"/>; // ID del usuario logeado
        let currentConversationId = null;
        let currentChatFriendId = null; // ID del amigo con el que se está chateando
        let currentChatFriendName = '';
        let messagePollingInterval = null; // Para el temporizador de refresco de mensajes
        let conversationsPollingInterval = null; // <-- Añade esta línea para declarar la variable
        let lastConversationsState = {}; // Para guardar el estado de los no leídos y detectar cambios
        const notificationSound = document.getElementById('notificationSound');

        const feedbackContainer = document.getElementById('feedbackMessageContainer');
        const searchResultsDiv = document.getElementById('searchResults');
        const pendingRequestsList = document.getElementById('pendingRequestsList');
        const friendsList = document.getElementById('friendsList');
        const conversationsList = document.getElementById('conversationsList');
        const chatPlaceholder = document.getElementById('chatPlaceholder');
        const chatArea = document.getElementById('chatArea');
        const chatHeader = document.getElementById('chatHeader');
        const messagesDisplay = document.getElementById('messagesDisplay');
        const messageInput = document.getElementById('messageInput');

        function playNotificationSound() {
            if (notificationSound) {
                notificationSound.play().catch(e => console.warn("Error al reproducir sonido de notificación:", e));
            }
        }

        // Función para mostrar mensajes de feedback
        function showFeedback(message, isSuccess) {
            feedbackContainer.textContent = message;
            feedbackContainer.className = 'message-box ' + (isSuccess ? 'message-success' : 'message-error');
            feedbackContainer.style.display = 'block';
            setTimeout(() => {
                feedbackContainer.style.display = 'none';
            }, 5000); // Ocultar después de 5 segundos
        }

        // --- Funciones para Cargar Datos Iniciales ---

        async function loadInitialData() {
            await loadFriends();
            await loadPendingRequests();
            await loadConversations();
        }

        async function loadFriends() {
            try {
                const response = await fetch('<s:url action="getFriendsAjax"/>');
                const json = await response.json();
                if (json.success) {
                    renderFriendsList(json.data);
                } else {
                    showFeedback('Error al cargar amigos: ' + json.message, false);
                }
            } catch (error) {
                console.error('Error al cargar amigos:', error);
                showFeedback('Error de red al cargar amigos.', false);
            }
        }

        async function loadPendingRequests() {
            try {
                const response = await fetch('<s:url action="getPendingRequestsAjax"/>');
                const json = await response.json();
                if (json.success) {
                    renderPendingRequests(json.data);
                } else {
                    showFeedback('Error al cargar solicitudes pendientes: ' + json.message, false);
                }
            } catch (error) {
                console.error('Error al cargar solicitudes pendientes:', error);
                showFeedback('Error de red al cargar solicitudes pendientes.', false);
            }
        }

        async function loadConversations(isPolling = false  ) {
            try {
                const response = await fetch('<s:url action="getConversationsAjax"/>');
                const json = await response.json();
                if (json.success) {
                    // Comparar con el estado anterior solo si es un polling
                    if (isPolling) {
                        let unreadChanged = false;
                        let newConversationsState = {};

                        // Construir el nuevo estado y detectar cambios en el contador de no leídos
                        if (json.data) {
                            json.data.forEach(conv => {
                                newConversationsState[conv.id] = conv.unreadMessagesCount;
                                // Si el contador ha cambiado o es una conversación nueva con no leídos
                                if (lastConversationsState[conv.id] !== conv.unreadMessagesCount && conv.unreadMessagesCount > 0) {
                                    unreadChanged = true;
                                }
                            });
                        }

                        // Si hay un cambio en el número de mensajes no leídos y no es el chat actual abierto
                        if (unreadChanged && currentConversationId === null) {
                            playNotificationSound();
                        } else if (unreadChanged && currentConversationId !== null) {
                            // Si hay un cambio y estoy en un chat, pero los mensajes no leídos son de otro chat
                            let unreadMessagesForOtherChat = false;
                            for (const convId in newConversationsState) {
                                if (newConversationsState[convId] > 0 && parseInt(convId) !== currentConversationId) {
                                    unreadMessagesForOtherChat = true;
                                    break;
                                }
                            }
                            if (unreadMessagesForOtherChat) {
                                 playNotificationSound();
                            }
                        }

                        // Actualizar el último estado
                        lastConversationsState = newConversationsState;
                    } else {
                        // Si no es un polling (carga inicial), establecer el estado inicial
                        lastConversationsState = {};
                        if (json.data) {
                            json.data.forEach(conv => {
                                lastConversationsState[conv.id] = conv.unreadMessagesCount;
                            });
                        }
                    }

                    renderConversationsList(json.data);

                } else {
                    showFeedback('Error al cargar conversaciones: ' + json.message, false);
                }
            } catch (error) {
                console.error('Error al cargar conversaciones:', error);
                showFeedback('Error de red al cargar conversaciones.', false);
            }
        }

        // Función para iniciar el polling de conversaciones
        function startConversationsPolling() {
            if (conversationsPollingInterval) {
                clearInterval(conversationsPollingInterval); // Asegurar que no hay múltiples intervalos
            }
            // Refrescar conversaciones cada 5 segundos
            conversationsPollingInterval = setInterval(() => loadConversations(true), 5000);
        }

        // Detener polling de conversaciones (ej. al cerrar sesión o salir de la página)
        function stopConversationsPolling() {
            if (conversationsPollingInterval) {
                clearInterval(conversationsPollingInterval);
                conversationsPollingInterval = null;
            }
        }

        // --- Funciones de Renderizado ---

        function renderSearchResults(users) {
            searchResultsDiv.innerHTML = ''; // Limpiar resultados anteriores
            if (!users || users.length === 0) {
                searchResultsDiv.innerHTML = '<p>No se encontraron usuarios.</p>';
                return;
            }
            users.forEach(user => {
                const li = document.createElement('li');
                li.className = 'list-item';
                li.innerHTML = `
                    <span class="list-item-name">`+user.nombre+`</span>
                    <div class="list-item-actions">
                        <button class="btn btn-small btn-send-request" onclick="sendFriendRequest(`+user.id+`)">Enviar Solicitud</button>
                    </div>
                `;
                searchResultsDiv.appendChild(li);
            });
        }

        function renderPendingRequests(requests) {
            pendingRequestsList.innerHTML = '';
            document.getElementById('pendingRequestsCount').textContent = requests ? requests.length : 0;
            if (!requests || requests.length === 0) {
                pendingRequestsList.innerHTML = '<p>No tienes solicitudes pendientes.</p>';
                return;
            }
            requests.forEach(req => {
                // Determinar quién envió la solicitud para mostrar el nombre correcto
                const senderName = req.usuario1Id.id === currentLoggedInUserId ? req.usuario2Id.nombre : req.usuario1Id.nombre;
                const li = document.createElement('li');
                li.className = 'list-item';
                li.innerHTML = `
                    <span class="list-item-name">`+senderName+`</span>
                    <div class="list-item-actions">
                        <button class="btn btn-small" onclick="acceptFriendRequest(`+req.id+`)">Aceptar</button>
                        <button class="btn btn-small" style="background-color: #dc3545;" onclick="rejectFriendRequest(`+req.id+`)">Rechazar</button>
                    </div>
                `;
                pendingRequestsList.appendChild(li);
            });
        }

        function renderFriendsList(friends) {
            friendsList.innerHTML = '';
            document.getElementById('friendsCount').textContent = friends ? friends.length : 0;
            if (!friends || friends.length === 0) {
                friendsList.innerHTML = '<p>Aún no tienes amigos.</p>';
                return;
            }
            friends.forEach(friend => {
                const li = document.createElement('li');
                li.className = 'list-item';
                li.innerHTML = `
                    <span class="list-item-name">`+friend.nombre+`</span>
                    <div class="list-item-actions">
                        <button class="btn btn-small" onclick="openConversationWithFriend(`+friend.id+`, '`+friend.nombre+`')">Chatear</button>
                    </div>
                `;
                friendsList.appendChild(li);
            });
        }

        function renderConversationsList(conversations) {
            conversationsList.innerHTML = '';
            document.getElementById('conversationsCount').textContent = conversations ? conversations.length : 0;
            if (!conversations || conversations.length === 0) {
                conversationsList.innerHTML = '<p>No tienes conversaciones activas.</p>';
                return;
            }
            // El orden ya viene del backend (no leídos primero, luego por fecha)

            conversations.forEach(conv => {
                let friendName = 'Desconocido';
                // Asumiendo conversaciones 1-a-1 por ahora. Para grupos, necesitarías ajustar la lógica.
                if (conv.participantes && conv.participantes.length > 0) {
                    const otherParticipant = conv.participantes.find(p => p.usuario && p.usuario.id !== currentLoggedInUserId);
                    if (otherParticipant) {
                        friendName = otherParticipant.usuario.nombreUsuario;
                    } else if (conv.participantes.length === 1 && conv.participantes[0].usuario.id === currentLoggedInUserId) {
                        // Caso raro donde solo está el propio usuario (ej. un chat consigo mismo o grupo vacío)
                        friendName = 'Tú mismo';
                    }
                    // Lógica para grupos: si conv.participantes.length > 2, podrías mostrar un nombre de grupo o "Chat grupal"
                }

                const li = document.createElement('li');
                li.className = 'list-item';
                // Añadir clase si tiene mensajes no leídos
                if (conv.unreadMessagesCount > 0) {
                    li.classList.add('has-unread');
                    // Aplicar animación según la cantidad de mensajes no leídos
                    if (conv.unreadMessagesCount >= 5) { // 5 o más mensajes: rápido
                        li.classList.add('pulse-fast');
                    } else if (conv.unreadMessagesCount >= 2) { // 2 a 4 mensajes: medio
                        li.classList.add('pulse-medium');
                    } else { // 1 mensaje: lento
                        li.classList.add('pulse-slow');
                    }
                }

                // Manejar el clic para abrir la conversación
                li.onclick = () => openConversation(conv.id, friendName, conv.participantes.find(p => p.usuario.id !== currentLoggedInUserId).usuario.id);

                let unreadBadge = '';
                if (conv.unreadMessagesCount > 0) {
                    unreadBadge = `<span class="unread-count">`+conv.unreadMessagesCount+`</span>`;
                }

                let timeDisplay = 'Nueva';
                if (conv.ultimoMensajeFecha) {
                    const date = new Date(conv.ultimoMensajeFecha);
                    if (!isNaN(date)) {
                        timeDisplay = date.toLocaleTimeString();
                    }
                }
                li.innerHTML = `
                    <span class="list-item-name">`+friendName+`</span>
                    <div style="display: flex; align-items: center;">
                        <span>`+timeDisplay+`</span>
                        `+unreadBadge+`
                    </div>
                `;
                conversationsList.appendChild(li);
            });
        }

        function renderMessages(messages) {
            messagesDisplay.innerHTML = ''; // Limpiar mensajes anteriores
            if (!messages || messages.length === 0) {
                messagesDisplay.innerHTML = '<p style="text-align: center; color: #888;">No hay mensajes en esta conversación.</p>';
                return;
            }

            messages.forEach(msg => {
                const div = document.createElement('div');
                const isSentByUser = String(msg.emisorId.id) === String(currentLoggedInUserId);
                div.className = isSentByUser ? 'message sent' : 'message received';
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
                messagesDisplay.appendChild(div);
            });
            messagesDisplay.scrollTop = messagesDisplay.scrollHeight; // Scroll al final
        }

        // --- Funciones de Interacción AJAX ---

        async function searchUsers() {
            const query = document.getElementById('searchQuery').value;
            try {
                const response = await fetch('<s:url action="searchUsersAjax"/>', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `searchQuery=\${encodeURIComponent(query)}`
                });
                const json = await response.json();
                if (json.success) {
                    renderSearchResults(json.data);
                } else {
                    showFeedback('Error en la búsqueda: ' + json.message, false);
                    searchResultsDiv.innerHTML = ''; // Limpiar resultados si hay error
                }
            } catch (error) {
                console.error('Error de red al buscar usuarios:', error);
                showFeedback('Error de red al buscar usuarios.', false);
            }
        }

        async function sendFriendRequest(receptorId) {
            try {
                const response = await fetch('<s:url action="sendFriendRequestAjax"/>', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `receptorId=\${receptorId}`
                });
                const json = await response.json();
                if (json.success) {
                    showFeedback(json.message, true);
                    // Opcional: recargar la lista de amigos o solicitudes pendientes si la API lo requiere
                    loadPendingRequests(); // Podría ser relevante si la API devuelve la solicitud como pendiente
                    // Limpiar resultados de búsqueda o actualizar el estado del botón
                    searchUsers(); // Volver a buscar para actualizar el estado de los botones
                } else {
                    showFeedback('Error al enviar solicitud: ' + json.message, false);
                }
            } catch (error) {
                console.error('Error de red al enviar solicitud:', error);
                showFeedback('Error de red al enviar solicitud.', false);
            }
        }

        async function acceptFriendRequest(amistadId) {
            try {
                const response = await fetch('<s:url action="acceptRequestAjax"/>', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `amistadId=\${amistadId}`
                });
                const json = await response.json();
                if (json.success) {
                    showFeedback(json.message, true);
                    loadPendingRequests(); // Recargar solicitudes
                    loadFriends(); // Recargar amigos
                    loadConversations(); // Recargar conversaciones (podría crearse una nueva)
                } else {
                    showFeedback('Error al aceptar solicitud: ' + json.message, false);
                }
            } catch (error) {
                console.error('Error de red al aceptar solicitud:', error);
                showFeedback('Error de red al aceptar solicitud.', false);
            }
        }

        async function rejectFriendRequest(amistadId) {
            try {
                const response = await fetch('<s:url action="rejectRequestAjax"/>', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `amistadId=\${amistadId}`
                });
                const json = await response.json();
                if (json.success) {
                    showFeedback(json.message, true);
                    loadPendingRequests(); // Recargar solicitudes
                } else {
                    showFeedback('Error al rechazar solicitud: ' + json.message, false);
                }
            } catch (error) {
                console.error('Error de red al rechazar solicitud:', error);
                showFeedback('Error de red al rechazar solicitud.', false);
            }
        }

        async function openConversationWithFriend(friendId, friendName) {
            // Primero, obtener o crear la conversación
            try {
                const response = await fetch('<s:url action="getOrCreateConversationAjax"/>', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `friendId=\${friendId}`
                });
                const json = await response.json();
                if (json.success && json.data) {
                    currentConversationId = json.data.id;
                    currentChatFriendId = friendId;
                    currentChatFriendName = friendName;
                    openConversation(currentConversationId, friendName, friendId); // Llamar a la función principal de abrir chat
                    loadConversations(); // Recargar la lista de conversaciones para ver la nueva/actualizada
                } else {
                    showFeedback('Error al iniciar conversación: ' + json.message, false);
                }
            } catch (error) {
                console.error('Error de red al iniciar conversación:', error);
                showFeedback('Error de red al iniciar conversación.', false);
            }
        }

        async function openConversation(convId, friendName, friendId) {
            currentConversationId = convId;
            currentChatFriendId = friendId;
            currentChatFriendName = friendName;

            chatPlaceholder.style.display = 'none';
            chatArea.style = 'display: flex;flex-direction: column;'; // Mostrar el área de chat
            chatHeader.textContent = `Chat con `+friendName;

            // Limpiar y detener polling anterior de mensajes
            messagesDisplay.innerHTML = '';
            if (messagePollingInterval) {
                clearInterval(messagePollingInterval);
            }

            // Detener el polling de la lista de conversaciones mientras el chat está abierto
            stopConversationsPolling();

            await loadMessages(); // Cargar mensajes iniciales
            // Iniciar polling para nuevos mensajes en este chat cada 3 segundos
            messagePollingInterval = setInterval(loadMessages, 3000);

            // Marcar mensajes como leídos y luego recargar la lista de conversaciones
            await markMessagesAsRead(convId);
            await loadConversations(); // Recargar la lista de conversaciones para actualizar el contador
        }

        async function loadMessages() {
            if (!currentConversationId) return;

            try {
                const response = await fetch('<s:url action="getMessagesInConversationAjax"/>', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `conversationId=\${currentConversationId}`
                });
                const json = await response.json();
                if (json.success) {
                    renderMessages(json.data);
                } else {
                    showFeedback('Error al cargar mensajes: ' + json.message, false);
                }
            } catch (error) {
                console.error('Error de red al cargar mensajes:', error);
                showFeedback('Error de red al cargar mensajes.', false);
            }
        }

        async function sendMessage() {
            const content = messageInput.value.trim();
            if (!content || !currentConversationId) {
                showFeedback('El mensaje no puede estar vacío.', false);
                return;
            }

            try {
                const response = await fetch('<s:url action="sendMessageAjax"/>', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `conversationId=\${currentConversationId}&messageContent=\${encodeURIComponent(content)}`
                });
                const json = await response.json();
                if (json.success) {
                    messageInput.value = ''; // Limpiar input
                    await loadMessages(); // Recargar mensajes para ver el nuevo
                    await loadConversations(); // Recargar la lista de conversaciones para actualizar el orden y el "último mensaje"
                    // Opcional: showFeedback(json.message, true);
                } else {
                    showFeedback('Error al enviar mensaje: ' + json.message, false);
                }
            } catch (error) {
                console.error('Error de red al enviar mensaje:', error);
                showFeedback('Error de red al enviar mensaje.', false);
            }
        }

        async function markMessagesAsRead(convId) {
            try {
                const response = await fetch('<s:url action="markMessagesAsReadAjax"/>', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `conversationId=\${convId}`
                });
                const json = await response.json();
                if (!json.success) {
                    console.warn('No se pudieron marcar los mensajes como leídos:', json.message);
                }
            } catch (error) {
                console.error('Error de red al marcar mensajes como leídos:', error);
            }
        }

        // Cargar datos iniciales al cargar la página e iniciar el polling de conversaciones
        window.onload = () => {
            loadInitialData(); // Esto ya carga friends, pending requests y conversations
            startConversationsPolling(); // Iniciar el polling para la lista de conversaciones
        };

        // Permitir enviar mensaje con Enter
        messageInput.addEventListener('keypress', function(event) {
            if (event.key === 'Enter') {
                sendMessage();
            }
        });
    </script>
</body>
</html>
