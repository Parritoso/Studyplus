<%-- 
    Document   : activeStudySession
    Created on : 02-jun-2025, 11:50:00
    Author     : Parri
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%-- Content Security Policy (CSP) para permitir recursos y estilos inline --%>
        <meta http-equiv="Content-Security-Policy" content="
              default-src 'self';
              script-src 'self' 'unsafe-inline' https://www.youtube.com https://s.ytimg.com https://www.youtube-nocookie.com https://googleusercontent.com;
              style-src 'self' 'unsafe-inline'; <%-- AÑADIDO: Permite estilos inline --%>
              frame-src https://www.youtube.com https://www.youtube-nocookie.com https://googleusercontent.com;
              img-src 'self' data: https://i.ytimg.com https://googleusercontent.com;
              connect-src 'self' https://www.youtube.com https://s.ytimg.com https://googleusercontent.com;
              media-src 'self' https://www.youtube.com https://googleusercontent.com;
              ">
        <title>Sesión de Estudio Activa - Estudy+</title>
        <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/activeStudySession.css">
        <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/activeStudySession.css" disabled>
    </head>
    <body>
        <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
        <%@ include file="/jsp/common/navbar.jsp" %>

        <div class="container">
            <h2>Sesión de Estudio Activa</h2>

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

            <div class="session-details">
                <p><strong>Título:</strong> <s:property value="sesion.titulo"/></p>
                <s:if test="sesion.descripcion != null && !sesion.descripcion.isEmpty()">
                    <p><strong>Descripción:</strong> <s:property value="sesion.descripcion"/></p>
                </s:if>
                <p><strong>Duración Planificada:</strong> <s:property value="sesion.duracionPlanificadaMinutos"/> minutos</p>
                <s:if test="sesion.tecnicaAplicadaId != null">
                    <p><strong>Técnica:</strong> <s:property value="sesion.tecnicaAplicadaId.nombre"/></p>
                    <p><strong>Foco Planificado:</strong> <s:property value="sesion.tecnicaAplicadaId.duracionFocoMinutos"/> min</p>
                    <p><strong>Descanso Planificado:</strong> <s:property value="sesion.tecnicaAplicadaId.duracionDescansoMinutos"/> min</p>
                </s:if>
                <s:if test="sesion.entregaAsociada != null">
                    <p><strong>Asociada a Entrega:</strong> <s:property value="sesion.entregaAsociada.titulo"/></p>
                </s:if>
                <s:if test="sesion.examenAsociado != null">
                    <p><strong>Asociado a Examen:</strong> <s:property value="sesion.examenAsociado.nombre"/></p>
                </s:if>
                <p><strong>Estado Actual:</strong> <span id="sessionStateDisplay"><s:property value="sesion.estadoString"/></span></p>
            </div>

            <div class="timer-label" id="timerLabel">Tiempo Transcurrido</div>
            <div class="timer-display" id="timerDisplay">00:00:00</div>

            <div class="controls">
                <s:url action="pauseStudySession" var="pauseUrl">
                    <s:param name="sesionId" value="sesion.id"/>
                </s:url>
                <s:a href="%{pauseUrl}" cssClass="btn btn-pause" id="pauseBtn">Pausar</s:a>

                <s:url action="resumeStudySession" var="resumeUrl">
                    <s:param name="sesionId" value="sesion.id"/>
                </s:url>
                <s:a href="%{resumeUrl}" cssClass="btn btn-resume" id="resumeBtn" style="display:none;">Reanudar</s:a>

                    <button type="button" class="btn btn-end" id="endSessionBtn">Finalizar Sesión</button>
                </div>
            </div>

        <%-- NUEVO: Sección de Notas Rápidas --%>
        <div class="notes-section">
            <h3><s:text name="session.active.quickNotesSectionTitle"/></h3>
            <textarea id="quickNotesTextarea" class="form-control" rows="5" placeholder="<s:text name='session.active.quickNotesPlaceholder'/>"><s:property value="sesion.notasRapidas"/></textarea>
            <button type="button" class="btn btn-save-notes" id="saveNotesBtn"><s:text name="session.active.saveNotesButton"/></button>
            <div id="notesFeedback" class="feedback-message"></div>
        </div>

        <%-- NUEVO: Sección de Checklist --%>
        <div class="checklist-section">
            <h3><s:text name="session.active.checklistSectionTitle"/></h3>
            <ul id="checklistItems" class="checklist-list">
                <%-- Los ítems del checklist se renderizarán con JavaScript --%>
            </ul>
            <div class="add-checklist-item-form">
                <input type="text" id="newChecklistItemText" placeholder="<s:text name='session.active.addChecklistItemPlaceholder'/>" class="form-control"/>
                <button type="button" id="addChecklistItemBtn" class="btn"><s:text name="session.active.addButton"/></button>
            </div>
            <div id="checklistFeedback" class="feedback-message"></div>
        </div>

        <div class="music-player-section">
            <h3>Música de Fondo para el Focus</h3>
            <div class="music-controls">
                <label for="musicSelect">Seleccionar Música:</label>
                <select id="musicSelect" class="form-control">
                    <option value="">-- No Music --</option>
                    <option value="dQw4w9WgXcQ">Lofi Hip Hop Radio (24/7)</option>
                    <option value="q76bJ_90_1Q">Rain Sounds for Sleep (8 Hours)</option>
                    <option value="aQZ7Q-e-c0E">Deep Focus Music (Alpha Waves)</option>
                    <option value="P1g2b56a9gE">Relaxing Ocean Waves</option> </select>
                <button id="playMusicBtn" class="btn">Reproducir</button>
                <button id="pauseMusicBtn" class="btn" style="display:none;">Pausar</button>
            </div>
            <div id="player"></div>
            <%-- Contenedor para el aviso de privacidad --%>
            <div id="privacyWarning" class="privacy-warning">
                <p><strong>Problema con el reproductor de música:</strong></p>
                <p>Tu navegador podría estar bloqueando el contenido de terceros (como YouTube) debido a la "Prevención de Rastreo" (Tracking Prevention).</p>
                <p>Para solucionar esto, por favor, intenta:</p>
                <ul>
                    <li>Desactivar la "Protección de Rastreo Mejorada" (o similar) para este sitio en la configuración de tu navegador.</li>
                    <li>Añadir este sitio a las excepciones de tu bloqueador de anuncios o extensiones de privacidad.</li>
                    <li>Usar un navegador diferente.</li>
                </ul>
                <p>Esto permitirá que el reproductor de YouTube funcione correctamente.</p>
            </div>
        </div>

        <div id="endSessionModal" class="modal">
            <div class="modal-content">
                <h3>Finalizar Sesión de Estudio</h3>
                <s:form action="endStudySession" method="post" id="endSessionForm">
                    <s:hidden name="sesionId" value="%{sesion.id}"/>
                    <s:hidden name="outcome" id="outcomeInput"/>

                    <div class="form-group">
                        <label for="notes">Notas de la Sesión:</label>
                        <s:textarea name="notes" id="notes" cssClass="form-control" rows="4" placeholder="¿Qué aprendiste? ¿Qué hiciste?"></s:textarea>
                        </div>
                        <div class="form-group">
                            <label for="focusRating">Calificación de Enfoque (1-5):</label>
                        <s:textfield name="focusRating" id="focusRating" type="number" cssClass="form-control" min="1" max="5" placeholder="Ej: 4"/>
                    </div>
                    <div class="form-group">
                        <label for="interruptionDetails">Detalles de Interrupciones:</label>
                        <s:textarea name="interruptionDetails" id="interruptionDetails" cssClass="form-control" rows="3" placeholder="¿Qué te interrumpió? ¿Cuánto tiempo?"></s:textarea>
                        </div>
                        <div class="modal-buttons">
                            <button type="button" class="btn btn-abort-modal" id="abortSessionBtn">Abortar Sesión</button>
                            <button type="submit" class="btn btn-submit">Marcar como Completada</button>
                        </div>
                </s:form>
            </div>
        </div>

        <footer>
            &copy; 2025 Estudy+. Todos los derechos reservados.
        </footer>

        <audio id="alarmSound" loop preload="auto">
            <source id="alarmSource" src="<s:url value='/public/alarm.mp3'/>" type="audio/mpeg">
            <source src="<s:url value='/public/alarm-1.mp3'/>" type="audio/mpeg">
            <source src="<s:url value='/public/alarm-2.mp3'/>" type="audio/mpeg">
            <source src="<s:url value='/public/alarm-3.mp3'/>" type="audio/mpeg">
            Your browser does not support the audio element.
        </audio>

        <script src="https://www.youtube.com/iframe_api"></script>

        <script type="text/javascript">
            document.addEventListener("DOMContentLoaded", function () {
                // --- Datos de la Sesión (pasados desde la acción Struts2) ---
                const sesionId = <s:property value="sesion.id" escape="false"/>;
                const sesionTitulo = "<s:property value="sesion.titulo" escape="false"/>";
                const duracionPlanificadaMinutos = <s:property value="sesion.duracionPlanificadaMinutos" escape="false"/>;
                const estadoInicial = "<s:property value="sesion.estadoString" escape="false"/>";
                const tecnicaAplicada = "<s:property value="sesion.tecnicaAplicadaId != null ? sesion.tecnicaAplicadaId.nombre : null" escape="false"/>";
                const focoPlanificadoMinutos = "<s:property value="sesion.tecnicaAplicadaId != null ? sesion.tecnicaAplicadaId.duracionFocoMinutos : null" escape="false"/>";
                const descansoPlanificadoMinutos = "<s:property value="sesion.tecnicaAplicadaId != null ? sesion.tecnicaAplicadaId.duracionDescansoMinutos : null" escape="false"/>";

                // --- NUEVOS: Datos iniciales de Notas Rápidas y Checklist ---
                let currentNotasRapidas = "<s:property value="sesion.notasRapidas" escape="false"/>";
                let currentChecklist = [];
                //try {
                //    const checklistJson = "<s:property value="sesion.checklist" escape="false"/>";
                //    if (checklistJson && checklistJson.trim() !== '') {
                //        currentChecklist = JSON.parse(checklistJson);
                //    }
                //} catch (e) {
                //    console.error("Error parsing initial checklist JSON:", e);
                //    // currentChecklist remains empty array
                //}

                // --- Referencias a elementos del DOM ---
                const timerDisplay = document.getElementById('timerDisplay');
                const timerLabel = document.getElementById('timerLabel');
                const sessionStateDisplay = document.getElementById('sessionStateDisplay');
                const pauseBtn = document.getElementById('pauseBtn');
                const resumeBtn = document.getElementById('resumeBtn');
                const endSessionBtn = document.getElementById('endSessionBtn');
                const alarmSound = document.getElementById('alarmSound');

                // --- Variables de Estado del Temporizador ---
                let timerInterval;
                let alarmInterval;
                let isPausedByFocusLoss = false;
                let totalElapsedSeconds = 0; // Para el cronómetro continuo

                // Para el temporizador de foco/descanso
                const tecnicasConTemporizador = ["Recuperación Activa", "Pomodoro", "90/20", "Técnica Feynman", "Estudio Intercalado"];
                let isTechniqueTimer = tecnicasConTemporizador.includes(tecnicaAplicada);
                let currentPhase = 'foco'; // 'foco' o 'descanso'
                let remainingPhaseSeconds = focoPlanificadoMinutos * 60;
                let cycleCount = 0; // Para llevar la cuenta de los ciclos de foco/descanso

                // --- Modal de Finalización ---
                const endSessionModal = document.getElementById('endSessionModal');
                const endSessionForm = document.getElementById('endSessionForm');
                const outcomeInput = document.getElementById('outcomeInput');
                const abortSessionBtn = document.getElementById('abortSessionBtn');

                // --- Elementos para Música de Fondo ---
                const musicSelect = document.getElementById('musicSelect');
                const playMusicBtn = document.getElementById('playMusicBtn');
                const pauseMusicBtn = document.getElementById('pauseMusicBtn');
                let youtubePlayer; // Variable para el objeto reproductor de YouTube

                // --- Configuración de Sonidos de Alarma ---
                // Duplicamos 'alarm.mp3' para que sea más probable
                const alarmSoundFiles = [
                    'alarm.mp3',
                    'alarm.mp3', // Duplicado para mayor probabilidad
                    'alarm-1.mp3',
                    'alarm-2.mp3',
                    'alarm-3.mp3'
                ];
                let isFirstAlarm = true; // Para asegurar que la primera alarma sea siempre 'alarm.mp3'
                // --- NUEVOS: Elementos de Notas Rápidas y Checklist ---
                const quickNotesTextarea = document.getElementById('quickNotesTextarea');
                const saveNotesBtn = document.getElementById('saveNotesBtn');
                const notesFeedback = document.getElementById('notesFeedback');
                const checklistItemsUl = document.getElementById('checklistItems');
                const newChecklistItemText = document.getElementById('newChecklistItemText');
                const addChecklistItemBtn = document.getElementById('addChecklistItemBtn');
                const checklistFeedback = document.getElementById('checklistFeedback');

                // --- Funciones de Utilidad de Tiempo ---
                function formatTime(seconds) {
                    const h = Math.floor(seconds / 3600);
                    const m = Math.floor((seconds % 3600) / 60);
                    const s = seconds % 60;
                    return [h, m, s]
                            .map(v => v < 10 ? '0' + v : v)
                            .join(':');
                }

                function updateTimerDisplay() {
                    if (isTechniqueTimer) {
                        timerDisplay.textContent = formatTime(remainingPhaseSeconds);
                        if (currentPhase === 'foco') {
                            timerLabel.textContent = `Tiempo de Foco Restante (` + focoPlanificadoMinutos + ` min)`;
                            timerDisplay.classList.remove('break-mode', 'paused-mode');
                        } else { // descanso
                            timerLabel.textContent = `Tiempo de Descanso Restante (` + descansoPlanificadoMinutos + ` min)`;
                            timerDisplay.classList.add('break-mode');
                            timerDisplay.classList.remove('paused-mode');
                        }
                    } else {
                        timerDisplay.textContent = formatTime(totalElapsedSeconds);
                        timerLabel.textContent = 'Tiempo Transcurrido';
                        timerDisplay.classList.remove('break-mode', 'paused-mode');
                    }
                }

                function startTimer() {
                    if (timerInterval)
                        clearInterval(timerInterval); // Limpiar cualquier intervalo anterior

                    timerInterval = setInterval(() => {
                        if (isTechniqueTimer) {
                            if (remainingPhaseSeconds > 0) {
                                remainingPhaseSeconds--;
                            } else {
                                // Fase actual terminada, cambiar a la siguiente
                                if (currentPhase === 'foco') {
                                    currentPhase = 'descanso';
                                    remainingPhaseSeconds = descansoPlanificadoMinutos * 60;
                                    alarmSound.pause(); // Asegurarse de que no suene si se reanuda desde un descanso
                                    alarmSound.currentTime = 0;
                                    sumarPuntosEnServidor();
                                } else { // descanso
                                    currentPhase = 'foco';
                                    remainingPhaseSeconds = focoPlanificadoMinutos * 60;
                                    cycleCount++; // Un ciclo completo de foco-descanso
                                    alarmSound.pause(); // Detener la alarma al volver al foco
                                    alarmSound.currentTime = 0;
                                }
                            }
                        } else { // Cronómetro continuo
                            totalElapsedSeconds++;
                        }
                        updateTimerDisplay();
                    }, 1000); // Actualizar cada segundo
                }

                function pauseTimer() {
                    clearInterval(timerInterval);
                    timerDisplay.classList.add('paused-mode');
                    alarmSound.pause(); // Asegurarse de que la alarma se detenga si se pausa
                    alarmSound.currentTime = 0;
                }

                // --- Lógica de Detección de Foco de Página ---
                function handleVisibilityChange() {
                    if (document.hidden) {
                        // Página no visible
                        if (isTechniqueTimer && currentPhase === 'foco') {
                            pauseTimer();
                            isPausedByFocusLoss = true;
                            logger.info("Foco de página perdido. Temporizador de foco pausado.");
                        } else if (!isTechniqueTimer) { // Cronómetro continuo
                            pauseTimer();
                            isPausedByFocusLoss = true;
                            logger.info("Foco de página perdido. Cronómetro pausado.");
                        } else if (isTechniqueTimer && currentPhase === 'descanso') {
                            // Si es descanso, el temporizador sigue corriendo
                            // Pero si el descanso termina mientras no hay foco, debe sonar la alarma
                            if (remainingPhaseSeconds <= 0 && !alarmInterval) {
                                startAlarm();
                                logger.warn("Descanso terminado sin foco. Alarma activada.");
                            }
                        }
                    } else {
                        // Página visible de nuevo
                        if (isPausedByFocusLoss) {
                            startTimer();
                            isPausedByFocusLoss = false;
                            logger.info("Foco de página recuperado. Temporizador/Cronómetro reanudado.");
                        }
                        stopAlarm(); // Siempre detener la alarma al recuperar el foco
                    }
                }

                document.addEventListener('visibilitychange', handleVisibilityChange);

                // --- Lógica de Alarma ---
                function selectRandomAlarmSound() {
                    if (isFirstAlarm) {
                        isFirstAlarm = false;
                        return 'alarm.mp3'; // Siempre la primera vez
                    } else {
                        const randomIndex = Math.floor(Math.random() * alarmSoundFiles.length);
                        return alarmSoundFiles[randomIndex];
                    }
                }

                // --- Lógica de Alarma ---
                // Variable para almacenar la URL base de los audios
                let audioBaseUrl = ''; // Se inicializará en DOMContentLoaded
                function startAlarm() {
                    // Seleccionar un sonido de alarma aleatorio (o el primero si es la primera vez)
                    const selectedSound = selectRandomAlarmSound();
                    alarmSource.src = audioBaseUrl + selectedSound;
                    alarmSound.load(); // Cargar el nuevo sonido
                    if (alarmSound && alarmSound.paused) {
                        alarmSound.play().catch(e => console.error("Error al reproducir alarma:", e));
                    }
                    if (!alarmInterval) {
                        alarmInterval = setInterval(() => {
                            if (alarmSound.paused) { // Asegurarse de que siga sonando
                                alarmSound.play().catch(e => console.error("Error al re-reproducir alarma:", e));
                            }
                        }, 5000); // Intentar reproducir cada 5 segundos si se detiene por alguna razón
                    }
                }

                function stopAlarm() {
                    if (alarmInterval) {
                        clearInterval(alarmInterval);
                        alarmInterval = null;
                    }
                    if (alarmSound) {
                        alarmSound.pause();
                        alarmSound.currentTime = 0; // Reiniciar para la próxima vez
                    }
                }

                // --- Control de Botones ---
                pauseBtn.addEventListener('click', function (event) {
                    event.preventDefault(); // Prevenir la navegación por defecto
                    pauseTimer();
                    sessionStateDisplay.textContent = 'PAUSED';
                    pauseBtn.style.display = 'none';
                    resumeBtn.style.display = 'inline-block';
                    // Pausar la música también
                    if (youtubePlayer && youtubePlayer.getPlayerState() === YT.PlayerState.PLAYING) {
                        youtubePlayer.pauseVideo();
                        playMusicBtn.style.display = 'inline-block';
                        pauseMusicBtn.style.display = 'none';
                    }
                    // La acción de Struts2 se llamará a través del href
                    window.location.href = pauseBtn.href;
                });

                resumeBtn.addEventListener('click', function (event) {
                    event.preventDefault(); // Prevenir la navegación por defecto
                    startTimer(); // Reanuda el temporizador JS
                    sessionStateDisplay.textContent = 'ACTIVE';
                    pauseBtn.style.display = 'inline-block';
                    resumeBtn.style.display = 'none';
                    // Reanudar la música si estaba reproduciéndose antes de la pausa manual
                    if (youtubePlayer && musicSelect.value !== "") { // Solo si hay una canción seleccionada
                        youtubePlayer.playVideo();
                        playMusicBtn.style.display = 'none';
                        pauseMusicBtn.style.display = 'inline-block';
                    }
                    // La acción de Struts2 se llamará a través del href
                    window.location.href = resumeBtn.href;
                });

                endSessionBtn.addEventListener('click', function () {
                    pauseTimer(); // Pausar el temporizador mientras se muestra el modal
                    // Pausar la música al finalizar
                    if (youtubePlayer && youtubePlayer.getPlayerState() === YT.PlayerState.PLAYING) {
                        youtubePlayer.pauseVideo();
                        playMusicBtn.style.display = 'inline-block';
                        pauseMusicBtn.style.display = 'none';
                    }
                    endSessionModal.style.display = 'flex'; // Mostrar el modal
                });

                abortSessionBtn.addEventListener('click', function () {
                    if (confirm('¿Estás seguro de que quieres ABORTAR esta sesión? Los datos de progreso no se guardarán.')) {
                        outcomeInput.value = 'aborted';
                        endSessionForm.submit();
                    }
                });

                // Clic fuera del modal para cerrar (opcional, pero buena UX)
                window.addEventListener('click', function (event) {
                    if (event.target == endSessionModal) {
                        endSessionModal.style.display = 'none';
                        // Si se cierra el modal sin finalizar, reanudar el temporizador si estaba activo
                        if (sessionStateDisplay.textContent === 'ACTIVE') {
                            startTimer();
                            // Reanudar la música si estaba reproduciéndose y la sesión está activa
                            if (youtubePlayer && musicSelect.value !== "") {
                                youtubePlayer.playVideo();
                                playMusicBtn.style.display = 'none';
                                pauseMusicBtn.style.display = 'inline-block';
                            }
                        }
                    }
                });

                // --- Lógica para Música de Fondo (YouTube IFrame Player API) ---
                function onYouTubeIframeAPIReady() {
                    youtubePlayer = new YT.Player('player', {
                        height: '315',
                        width: '100%',
                        videoId: '', // Se cargará dinámicamente
                        playerVars: {
                            'playsinline': 1,
                            'autoplay': 0, // No autoplay inicialmente
                            'loop': 1, // Bucle infinito
                            'playlist': '' // Necesario para loop
                        },
                        events: {
                            'onReady': onPlayerReady,
                            'onStateChange': onPlayerStateChange
                        }
                    });
                }

                function onPlayerReady(event) {
                    // El reproductor está listo, ahora podemos controlar la música
                    console.log('YouTube Player is ready.');
                    // Opcional: cargar un video por defecto si hay uno seleccionado
                    if (musicSelect.value !== "") {
                        youtubePlayer.loadVideoById({
                            videoId: musicSelect.value,
                            playlist: musicSelect.value // Asegurar loop
                        });
                    }
                }

                function onPlayerStateChange(event) {
                    // Actualizar botones de play/pause según el estado del reproductor
                    if (event.data === YT.PlayerState.PLAYING) {
                        playMusicBtn.style.display = 'none';
                        pauseMusicBtn.style.display = 'inline-block';
                    } else {
                        playMusicBtn.style.display = 'inline-block';
                        pauseMusicBtn.style.display = 'none';
                    }
                }

                // Nuevo: Manejador de errores del reproductor de YouTube
                function onPlayerError(event) {
                    console.error("Error en el reproductor de YouTube:", event.data);
                    privacyWarning.style.display = 'block'; // Mostrar el aviso de privacidad
                    // Puedes añadir lógica para detener el temporizador o deshabilitar controles de música si el error es crítico
                }

                musicSelect.addEventListener('change', function () {
                    const videoId = this.value;
                    if (videoId) {
                        youtubePlayer.loadVideoById({
                            videoId: videoId,
                            playlist: videoId
                        });
                        youtubePlayer.playVideo(); // Reproducir automáticamente al cambiar
                    } else {
                        youtubePlayer.stopVideo();
                    }
                });

                playMusicBtn.addEventListener('click', function () {
                    if (musicSelect.value === "") {
                        alert('Por favor, selecciona una opción de música primero.');
                        return;
                    }
                    if (youtubePlayer) {
                        youtubePlayer.playVideo();
                    }
                });

                pauseMusicBtn.addEventListener('click', function () {
                    if (youtubePlayer) {
                        youtubePlayer.pauseVideo();
                    }
                });

                // --- NUEVA LÓGICA: Notas Rápidas ---
                saveNotesBtn.addEventListener('click', async function () {
                    const newNotes = quickNotesTextarea.value;
                    const url = '<s:url action="updateNotesAjax"/>';
                    const formData = new URLSearchParams();
                    formData.append('sesionId', sesionId);
                    formData.append('notasRapidas', newNotes);

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
                            notesFeedback.textContent = '<s:text name="session.notes.updateSuccess"/>';
                            notesFeedback.style.color = 'green';
                            currentNotasRapidas = newNotes; // Actualizar el estado local
                        } else {
                            notesFeedback.textContent = '<s:text name="session.notes.updateError"/>: ' + data.message;
                            notesFeedback.style.color = 'red';
                        }
                    } catch (error) {
                        console.error('Error al guardar notas:', error);
                        notesFeedback.textContent = '<s:text name="session.notes.updateUnexpectedError"/>';
                        notesFeedback.style.color = 'red';
                    } finally {
                        setTimeout(() => notesFeedback.textContent = '', 3000);
                    }
                });

                // --- NUEVA LÓGICA: Checklist ---
                function renderChecklist() {
                    checklistItemsUl.innerHTML = ''; // Limpiar lista existente
                    currentChecklist.forEach((item, index) => {
                        // --- AÑADE ESTA LÍNEA PARA DEPURACIÓN ---
                        //console.log("DEBUG CHECKLIST ITEM:", item.id, "Text:", item.text, "Completed:", item.completed);
                        // ----------------------------------------
                        const li = document.createElement('li');
                        li.className = 'checklist-item' + (item.completed ? ' completed' : '');
                        li.innerHTML = `
                                <input type="checkbox" id="checklist-item-` + index + `" ` + (item.completed ? 'checked' : '') + `>
                                <label for="checklist-item-` + index + `">` + item.text + `</label>
                                <button type="button" class="delete-checklist-item" data-index="` + index + `">&times;</button>
                            `;
                        checklistItemsUl.appendChild(li);

                        // Añadir listeners para toggle y delete
                        li.querySelector('input[type="checkbox"]').addEventListener('change', function () {
                            toggleChecklistItem(index);
                        });
                        li.querySelector('.delete-checklist-item').addEventListener('click', function () {
                            deleteChecklistItem(index);
                        });
                    });
                }

                async function updateChecklistInDb() {
                    const url = '<s:url action="updateChecklistAjax"/>';
                    const formData = new URLSearchParams();
                    formData.append('sesionId', sesionId);
                    formData.append('checklist', JSON.stringify(currentChecklist));

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
                            checklistFeedback.textContent = '<s:text name="session.checklist.updateSuccess"/>';
                            checklistFeedback.style.color = 'green';
                        } else {
                            checklistFeedback.textContent = '<s:text name="session.checklist.updateError"/>: ' + data.message;
                            checklistFeedback.style.color = 'red';
                        }
                    } catch (error) {
                        console.error('Error al guardar checklist:', error);
                        checklistFeedback.textContent = '<s:text name="session.checklist.updateUnexpectedError"/>';
                        checklistFeedback.style.color = 'red';
                    } finally {
                        setTimeout(() => checklistFeedback.textContent = '', 3000);
                    }
                }

                function addChecklistItem() {
                    const text = newChecklistItemText.value.trim();
                    if (text) {
                        currentChecklist.push({text: text, completed: false});
                        newChecklistItemText.value = '';
                        renderChecklist();
                        updateChecklistInDb();
                    } else {
                        checklistFeedback.textContent = '<s:text name="session.checklist.emptyItemAlert"/>';
                        checklistFeedback.style.color = 'orange';
                        setTimeout(() => checklistFeedback.textContent = '', 3000);
                    }
                }

                function toggleChecklistItem(index) {
                    if (index >= 0 && index < currentChecklist.length) {
                        currentChecklist[index].completed = !currentChecklist[index].completed;
                        renderChecklist();
                        updateChecklistInDb();
                    }
                }

                function deleteChecklistItem(index) {
                    if (index >= 0 && index < currentChecklist.length) {
                        if (confirm('<s:text name="session.checklist.confirmDeleteItem"/>')) {
                            currentChecklist.splice(index, 1);
                            renderChecklist();
                            updateChecklistInDb();
                        }
                    }
                }

                addChecklistItemBtn.addEventListener('click', addChecklistItem);
                newChecklistItemText.addEventListener('keypress', function (event) {
                    if (event.key === 'Enter') {
                        event.preventDefault(); // Evitar envío del formulario
                        addChecklistItem();
                    }
                });

                // --- NUEVA FUNCIÓN: Obtener datos iniciales de la sesión (notas y checklist) ---
                async function fetchInitialSessionData() {
                    const url = '<s:url action="getSessionDataAjax"/>';
                    const formData = new URLSearchParams();
                    formData.append('sesionId', sesionId);

                    try {
                        console.log("DEBUG: Fetching initial session data for sesionId:", sesionId);
                        const response = await fetch(url, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            body: formData
                        });
                        const data = await response.json();

                        //if (data.success) {
                        //    console.log("DEBUG: Initial session data received:", data);
                        //    // Actualizar notas rápidas
                        //    currentNotasRapidas = data.notasRapidas || ""; // Usar || "" para asegurar string
                        //    quickNotesTextarea.value = currentNotasRapidas;

                        // Actualizar checklist
                        //    try {
                        // Si el checklist ya viene como un array JSON válido, usarlo directamente
                        // Si no, parsearlo. Si Struts ya lo devuelve parseado, esto podría simplificarse.
                        //        currentChecklist = JSON.parse(data.message.checklist); // Parsear o usar array vacío
                        //        console.log("DEBUG: Initial checklist parsed:", currentChecklist);
                        //    } catch (e) {
                        //        console.error("Error parsing checklist from fetched data:", e);
                        //        currentChecklist = []; // Fallback a array vacío si hay error
                        //    }
                        //    renderChecklist(); // Renderizar el checklist después de cargarlo
                        //} else {
                        //    console.error("Error al obtener datos iniciales de sesión:", data.message);
                        //    notesFeedback.textContent = 'Error al cargar notas y checklist: ' + data.message;
                        //    notesFeedback.style.color = 'red';
                        //}
                        if (data.success) { // Accede a la propiedad 'success' del JsonResponse
                            console.log("DEBUG: Initial session data received:", data);
                            // Accede a los datos reales a través de la propiedad 'data' del JsonResponse
                            currentNotasRapidas = data.data.notasRapidas || "";
                            try {
                                currentChecklist = JSON.parse(data.data.checklist || "[]");
                            } catch (e) {
                                console.error("Error parsing checklist from fetched data:", e);
                                currentChecklist = [];
                            }
                            renderChecklist();
                        } else {
                            // Accede al mensaje de error a través de la propiedad 'message' del JsonResponse
                            console.error("Error al obtener datos iniciales de sesión:", data.message);
                            notesFeedback.textContent = 'Error al cargar notas y checklist: ' + data.message;
                            notesFeedback.style.color = 'red';
                        }
                    } catch (error) {
                        console.error('Error de red al obtener datos iniciales de sesión:', error);
                        notesFeedback.textContent = 'Error de conexión al cargar datos iniciales.';
                        notesFeedback.style.color = 'red';
                    }
                }

                // --- Inicialización del Temporizador ---
                // Se usa DOMContentLoaded para asegurar que el DOM esté completamente cargado
                // antes de intentar acceder a los elementos y ejecutar la lógica.
                document.addEventListener('DOMContentLoaded', function () {
                    // Inicializar la URL base para los audios
                    audioBaseUrl = '<s:url value="/audio/" includeContext="true"/>';

                    // *** LLAMADA CLAVE: Obtener y cargar datos de la sesión ***
                    fetchInitialSessionData(); // Esta función ahora se encarga de llenar quickNotesTextarea y currentChecklist, y luego llama a renderChecklist()

                    // Establecer las notas rápidas iniciales
                    quickNotesTextarea.value = currentNotasRapidas;

                    if (estadoInicial === 'ACTIVE') {
                        if (isTechniqueTimer) {
                            remainingPhaseSeconds = focoPlanificadoMinutos * 60;
                            currentPhase = 'foco';
                        } else {
                            totalElapsedSeconds = 0;
                        }
                        startTimer();
                        pauseBtn.style.display = 'inline-block';
                        resumeBtn.style.display = 'none';
                    } else if (estadoInicial === 'PAUSED') {
                        pauseTimer();
                        pauseBtn.style.display = 'none';
                        resumeBtn.style.display = 'inline-block';
                    } else {
                        console.error("Estado de sesión inesperado en la página activa:", estadoInicial);
                        timerDisplay.textContent = "Error";
                        timerLabel.textContent = "Sesión no iniciada correctamente.";
                        pauseBtn.style.display = 'none';
                        resumeBtn.style.display = 'none';
                    }

                    updateTimerDisplay();
                    renderChecklist(); // Renderizar el checklist inicial
                });
                
                
                async function sumarPuntosEnServidor() {
                        const url = '<s:url action="SumarPuntos"/>';
                        const formData = new URLSearchParams();

                        let puntos = 0;

                        switch (tecnicaAplicada) {
                            case "Pomodoro":
                                puntos = 10;
                                break;
                            case "90/20":
                                puntos = 18;
                                break;
                            case "Feynman":
                                puntos = 12;
                                break;
                            case "Estudio Intercalado":
                                puntos = 14;
                                break;
                            case "Recuperación Activa":
                                puntos = 8;
                                break;
                            case "Repetición Espaciada":
                                puntos = 6;
                                break;
                            default:
                                puntos = 5; // técnica no reconocida o nula
                        }

                        formData.append('puntos', puntos);
                        formData.append('sesionId', sesionId); // asumimos que también ya está definido arriba

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
                                console.log(`${puntos} puntos sumados con éxito usando la técnica: ${tecnicaAplicada}`);
                            } else {
                                console.warn('Error al sumar puntos:', data.message);
                            }
                        } catch (error) {
                            console.error('Error en la llamada a sumar puntos:', error);
                        }
                }
                
                fetchInitialSessionData();
                updateTimerDisplay();
                renderChecklist(); // Renderizar el checklist inicial
            });
        </script>
    </body>
</html>
