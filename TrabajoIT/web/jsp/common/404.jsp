<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="javax.servlet.http.HttpServletResponse"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
%>
<!DOCTYPE html>
<html>
    <%--<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Error 404 - Página no encontrada</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
        <style>
            body {
                font-family: Arial, sans-serif;
                text-align: center;
                padding: 50px;
                background-color: #f8f8f8;
                display: flex;
                flex-direction: column;
                min-height: 100vh;
                justify-content: center;
                align-items: center;
            }
            .error-container {
                background-color: #fff;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                padding: 30px;
                margin: 20px;
                max-width: 600px;
                width: 90%;
            }
            h1 {
                color: #dc3545;
                font-size: 3em;
                margin-bottom: 10px;
            }
            h2 {
                color: #007bff;
                margin-top: 30px;
                margin-bottom: 20px;
                font-size: 2em;
            }
            .btn {
                padding: 12px 25px;
                border: none;
                border-radius: 8px;
                cursor: pointer;
                font-size: 1.1em;
                font-weight: bold;
                transition: background-color 0.3s ease, transform 0.2s ease;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                background-color: #007bff;
                color: white;
                text-decoration: none;
                display: inline-block;
                margin-top: 20px;
            }
            .btn:hover {
                background-color: #0056b3;
                transform: translateY(-2px);
            }
            p {
                color: #666;
                font-size: 1.2em;
                margin-bottom: 20px;
            }
            #randomMessage {
                font-style: italic;
                font-weight: bold;
                color: #333;
            }
            a {
                color: #007bff;
                text-decoration: none;
            }
            a:hover {
                text-decoration: underline;
            }
            .quote-section {
                margin-top: 30px;
                padding: 20px;
                background-color: #e9ecef;
                border-left: 5px solid #007bff;
                font-size: 1.1em;
                color: #555;
                max-width: 600px; /* Igual que el error-container */
                width: 90%;
            }
            .quote-section p {
                margin-bottom: 5px;
            }
            .quote-section .author {
                font-weight: bold;
                text-align: right;
                display: block;
                margin-top: 10px;
            }

            /* --- Estilos para el juego --- */
            .game-container {
                margin-top: 30px;
                text-align: center;
                background-color: #e0f2f7; /* Un fondo azul claro para el juego */
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                padding: 15px;
                max-width: 600px;
                width: 90%;
            }
            #gameCanvas {
                border: 2px solid #007bff;
                background-color: #add8e6; /* Azul cielo */
                display: block;
                margin: 10px auto;
            }
            .game-instructions {
                font-size: 0.9em;
                color: #555;
                margin-top: 10px;
            }
            .game-start-btn {
                background-color: #28a745;
                color: white;
                padding: 10px 20px;
                border: none;
                border-radius: 5px;
                cursor: pointer;
                font-size: 1.1em;
                margin-top: 10px;
                transition: background-color 0.3s ease;
            }
            .game-start-btn:hover {
                background-color: #218838;
            }
            /* Estilos para el juego de preguntas */
            #quizGame {
                display: none; /* Oculto por defecto */
            }
            #quizGame h3 {
                color: #28a745;
                font-size: 1.8em;
                margin-bottom: 20px;
            }
            #questionDisplay {
                font-size: 1.3em;
                margin-bottom: 25px;
                font-weight: bold;
            }
            #answersContainer {
                display: flex;
                flex-direction: column;
                gap: 10px;
                margin-bottom: 20px;
            }
            .answer-btn {
                background-color: #17a2b8;
                color: white;
                padding: 12px 20px;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                font-size: 1.1em;
                transition: background-color 0.3s ease;
            }
            .answer-btn:hover {
                background-color: #138496;
            }
            .answer-btn.correct {
                background-color: #28a745; /* Verde para respuesta correcta */
            }
            .answer-btn.incorrect {
                background-color: #dc3545; /* Rojo para respuesta incorrecta */
            }
            #scoreDisplay {
                font-size: 1.2em;
                font-weight: bold;
                margin-top: 15px;
                color: #555;
            }
            #nextQuestionBtn, #restartQuizBtn {
                background-color: #007bff;
                color: white;
                padding: 10px 20px;
                border: none;
                border-radius: 5px;
                cursor: pointer;
                font-size: 1em;
                margin-top: 20px;
            }
            #nextQuestionBtn:hover, #restartQuizBtn:hover {
                background-color: #0056b3;
            }
            #quizLoading {
                display: none;
                margin-top: 20px;
                font-style: italic;
                color: #6c757d;
            }
        </style>
    </head>
    <body>
        <div class="error-container">
            <h1>Oops! Error 404</h1>
            <p id="randomMessage"></p>
            <p>Puedes volver a la <a href="${pageContext.request.contextPath}/">página principal</a> o intentar navegar desde allí.</p>
        </div>

        <div class="quote-section">
            <p id="quoteText"></p>
            <span id="quoteAuthor" class="author"></span>
        </div>

        <div id="mainGameContainer"></div>
        
        <div class="game-container">
            <h2>¡Mientras tanto, evita los distractores!</h2>
            <p class="game-instructions">Presiona **ESPACIO** o haz **clic** para que tu Estudiante vuele y evite los distractores.</p>
            <canvas id="gameCanvas" width="400" height="300"></canvas>
            <button id="startGameBtn" class="game-start-btn">Comenzar Juego</button>
            <p class="game-instructions">Puntuación: <span id="scoreDisplay">0</span></p>
        </div>

        <script>
            // --- Lógica para mensajes aleatorios y citas ---
            const messages = [
                "Parece que te has perdido. ¡No te preocupes, esto le pasa hasta a los mejores estudiantes!",
                "Parece que te has perdido. ¡No te preocupes, esto le pasa hasta a los mejores estudiantes!",
                "Parece que te has perdido. ¡No te preocupes, esto le pasa hasta a los mejores estudiantes!",
                "¡Vaya! Parece que algo se ha perdido.",
                "Esta página se ha escapado de nuestro radar.",
                "Lo sentimos, esta página no existe en nuestro universo.",
                "Oops, ¡esto no estaba en el mapa!",
                "¿En qué página estás? ¡No la encontramos!",
                "Inesperado error 404. Nuestra página se tomó un descanso.",
                "¡Ups! Parece que esta página ha estudiado demasiado y se ha fugado.",
                "Este es un lugar sin descubrir. ¿Quizás una nueva técnica de estudio?",
                "Ni nuestros algoritmos inteligentes encuentran esta página. ¡Mil disculpas!"
            ];

            function getRandomMessage() {
                const randomIndex = Math.floor(Math.random() * messages.length);
                return messages[randomIndex];
            }

            document.getElementById('randomMessage').textContent = getRandomMessage();

            // ***************************************************************
            //  ¡Este es el lugar clave para las declaraciones de las citas!
            //  Asegúrate de que estas líneas estén aquí, después de los elementos HTML
            //  <p id="quoteText"></p> y <span id="quoteAuthor"></span>
            // ***************************************************************
            const quoteTextElement = document.getElementById('quoteText');
            const quoteAuthorElement = document.getElementById('quoteAuthor');

            async function fetchRandomQuote() {
                try {
                    const response = await fetch('https://api.quotable.io/random');
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    const data = await response.json();
                    console.log("Datos de la cita recibidos:", data); // Verificación extra
                    // Verifica que data.content y data.author no sean undefined o null
                    if (data.content && data.author) {
                        quoteTextElement.textContent = `"`+data.content+`"`;
                        quoteAuthorElement.textContent = `- `+data.author;
                    } else {
                        throw new Error("Datos de cita incompletos.");
                    }
                } catch (error) {
                    console.error("Error al obtener la cita:", error);
                    quoteTextElement.textContent = "\"El único error verdadero es aquel del que no aprendemos nada.\"";
                    quoteAuthorElement.textContent = "- John Powell";
                }
            }
            
            // --- Contenedores para los juegos ---
                const mainGameContainer = document.getElementById('mainGameContainer');

                // HTML para el juego "Evita los Distractores"
                const flappyGameHtml = `
                    <div id="flappyGame" class="game-container">
                        <h2>¡Mientras tanto, evita los distractores!</h2>
                        <p class="game-instructions">Presiona **ESPACIO** o haz **clic** para que tu Estudiante vuele y evite los distractores.</p>
                        <canvas id="gameCanvas" width="400" height="300"></canvas>
                        <button id="startGameBtn" class="game-start-btn">Comenzar Juego</button>
                        <p class="game-instructions">Puntuación: <span id="flappyScoreDisplay">0</span></p>
                    </div>
                `;

                // HTML para el juego de preguntas (Quiz)
                const quizGameHtml = `
                    <div id="quizGame" class="game-container">
                        <h3>Juego de Preguntas de Cultura General</h3>
                        <div id="quizLoading">Cargando preguntas...</div>
                        <div id="quizContent" style="display:none;">
                            <p id="questionDisplay"></p>
                            <div id="answersContainer"></div>
                            <p id="quizScoreDisplay">Puntuación: 0</p>
                            <button id="nextQuestionBtn" style="display:none;">Siguiente Pregunta</button>
                            <button id="restartQuizBtn" style="display:none;">Reiniciar Quiz</button>
                        </div>
                    </div>
                `;

            // --- Lógica del Juego "Evita los Distractores" ---
            const canvas = document.getElementById('gameCanvas');
            const ctx = canvas.getContext('2d');
            const startGameBtn = document.getElementById('startGameBtn');
            const scoreDisplay = document.getElementById('scoreDisplay');
            
             // --- Lógica de selección aleatoria del juego ---
                const gameToDisplay = Math.floor(Math.random() * 2); // 0 para Evita los Distractores, 1 para Quiz

                if (gameToDisplay === 0) {
                    mainGameContainer.innerHTML = flappyGameHtml;
                    initializeFlappyGame();
                    console.log("Mostrando el juego 'Evita los Distractores'.");
                } else {
                    mainGameContainer.innerHTML = quizGameHtml;
                    initializeQuizGame();
                    console.log("Mostrando el juego de preguntas.");
                }
                
                // --- Lógica del Juego "Evita los Distractores" ---
                function initializeFlappyGame() {
                    const canvas = document.getElementById('gameCanvas');
                    const ctx = canvas.getContext('2d');
                    const startGameBtn = document.getElementById('startGameBtn');
                    const flappyScoreDisplay = document.getElementById('flappyScoreDisplay'); // ID único

                    let student = { x: 50, y: canvas.height / 2, width: 20, height: 20, dy: 0, gravity: 0.2, jumpPower: -4 };
                    let distractors = [];
                    let gameSpeed = 2;
                    let score = 0;
                    let isPlaying = false;
                    let gameLoopInterval;
                    let distractorSpawnInterval;

                    function drawStudent() {
                        ctx.fillStyle = 'green'; // Representa al estudiante o libro
                        ctx.fillRect(student.x, student.y, student.width, student.height);
                    }

                    function drawDistractors() {
                        ctx.fillStyle = 'red'; // Representa los distractores
                        distractors.forEach(d => {
                            ctx.fillRect(d.x, d.y, d.width, d.height);
                        });
                    }

                    function updateGameArea() {
                        if (!isPlaying) return;

                        ctx.clearRect(0, 0, canvas.width, canvas.height); // Limpiar canvas

                        // Actualizar estudiante
                        student.dy += student.gravity;
                        student.y += student.dy;

                        // Limitar estudiante a los bordes del canvas
                        if (student.y + student.height > canvas.height) {
                            student.y = canvas.height - student.height;
                            student.dy = 0;
                            gameOver();
                        }
                        if (student.y < 0) {
                            student.y = 0;
                            student.dy = 0;
                        }

                        // Actualizar distractores
                        distractors.forEach((d, index) => {
                            d.x -= gameSpeed;

                            // Eliminar distractores fuera de pantalla
                            if (d.x + d.width < 0) {
                                distractors.splice(index, 1);
                                score++; // Incrementar puntuación al pasar un distractor
                                flappyScoreDisplay.textContent = score;
                            }

                            // Detección de colisiones (simplificada)
                            if (student.x < d.x + d.width &&
                                student.x + student.width > d.x &&
                                student.y < d.y + d.height &&
                                student.y + student.height > d.y) {
                                gameOver();
                            }
                        });

                        drawStudent();
                        drawDistractors();
                    }

                    function spawnDistractor() {
                        if (!isPlaying) return;
                        const distractorHeight = 30 + Math.random() * 40; // Altura aleatoria
                        const distractorGap = 80 + Math.random() * 50; // Espacio entre distractores
                        const topDistractorY = Math.random() * (canvas.height - distractorGap - distractorHeight * 2) + distractorHeight;

                        distractors.push({ x: canvas.width, y: 0, width: 20, height: topDistractorY }); // Parte superior del distractor
                        distractors.push({ x: canvas.width, y: topDistractorY + distractorGap, width: 20, height: canvas.height - (topDistractorY + distractorGap) }); // Parte inferior
                    }

                    function studentJump() {
                        if (isPlaying) {
                            student.dy = student.jumpPower;
                        }
                    }

                    function startGame() {
                        if (isPlaying) return; // Evita iniciar múltiples veces

                        isPlaying = true;
                        score = 0;
                        flappyScoreDisplay.textContent = score;
                        student.y = canvas.height / 2;
                        student.dy = 0;
                        distractors = []; // Limpiar distractores anteriores

                        startGameBtn.textContent = 'Reiniciar';
                        startGameBtn.onclick = resetGame; // Cambiar acción del botón a reset

                        gameLoopInterval = setInterval(updateGameArea, 1000 / 60); // 60 FPS
                        distractorSpawnInterval = setInterval(spawnDistractor, 2000); // Aparece un distractor cada 2 segundos
                    }

                    function gameOver() {
                        clearInterval(gameLoopInterval);
                        clearInterval(distractorSpawnInterval);
                        isPlaying = false;
                        // Usar un modal o mensaje en el DOM en lugar de alert()
                        // alert(`¡Juego terminado! Tu puntuación final: ${score}.`);
                        flappyScoreDisplay.textContent = `¡Finalizado! ${score}`; // Actualiza el display de puntuación
                        startGameBtn.textContent = 'Volver a Jugar';
                        startGameBtn.onclick = startGame; // Vuelve a la acción de iniciar
                    }

                    function resetGame() {
                        gameOver(); // Detiene el juego actual
                        startGame(); // Inicia un nuevo juego
                    }

                    // Event Listeners para el juego "Evita los Distractores"
                    canvas.addEventListener('click', studentJump);
                    document.addEventListener('keydown', (e) => {
                        if (e.code === 'Space') { // Barra espaciadora
                            studentJump();
                            e.preventDefault(); // Prevenir el desplazamiento de la página
                        }
                    });
                    startGameBtn.addEventListener('click', startGame);

                    drawStudent(); // Dibujar el estudiante inicial
                }

            // --- Lógica del Juego de Preguntas (Quiz) ---
                let questions = [];
                let currentQuestionIndex = 0;
                let quizScore = 0; // Usar quizScore para el quiz
                let answeredThisQuestion = false;

                const questionDisplay = document.getElementById('questionDisplay');
                const answersContainer = document.getElementById('answersContainer');
                const quizScoreDisplay = document.getElementById('quizScoreDisplay'); // ID único
                const nextQuestionBtn = document.getElementById('nextQuestionBtn');
                const restartQuizBtn = document.getElementById('restartQuizBtn');
                const quizLoading = document.getElementById('quizLoading');
                const quizContent = document.getElementById('quizContent');

                async function fetchQuestions() {
                    quizLoading.style.display = 'block';
                    quizContent.style.display = 'none';
                    try {
                        // Usamos encode=url3986 para manejar caracteres especiales
                        const response = await fetch('https://opentdb.com/api.php?amount=10&type=multiple&encode=url3986');
                        const data = await response.json();

                        if (data.response_code === 0) {
                            questions = data.results.map(q => ({
                                question: decodeURIComponent(q.question),
                                correct_answer: decodeURIComponent(q.correct_answer),
                                incorrect_answers: q.incorrect_answers.map(ans => decodeURIComponent(ans))
                            }));
                            console.log("Preguntas cargadas:", questions);
                            quizLoading.style.display = 'none';
                            quizContent.style.display = 'block';
                            startQuiz();
                        } else {
                            console.error("Error al cargar preguntas:", data.response_code);
                            questionDisplay.textContent = "Error al cargar preguntas. Inténtalo de nuevo más tarde.";
                            quizLoading.style.display = 'none';
                            restartQuizBtn.style.display = 'block';
                        }
                    } catch (error) {
                        console.error("Error de red al cargar preguntas:", error);
                        questionDisplay.textContent = "Error de red al cargar preguntas. Revisa tu conexión.";
                        quizLoading.style.display = 'none';
                        restartQuizBtn.style.display = 'block';
                    }
                }

                function initializeQuizGame() {
                    fetchQuestions();
                }

                function startQuiz() {
                    currentQuestionIndex = 0;
                    quizScore = 0;
                    quizScoreDisplay.textContent = `Puntuación: ${quizScore}`;
                    nextQuestionBtn.style.display = 'none';
                    restartQuizBtn.style.display = 'none';
                    displayQuestion();
                }

                function displayQuestion() {
                    answeredThisQuestion = false;
                    answersContainer.innerHTML = ''; // Limpiar respuestas anteriores
                    nextQuestionBtn.style.display = 'none';

                    if (currentQuestionIndex < questions.length) {
                        const q = questions[currentQuestionIndex];
                        questionDisplay.textContent = q.question;

                        let answers = [...q.incorrect_answers, q.correct_answer];
                        // Mezclar las respuestas para que no siempre esté la correcta en el mismo lugar
                        answers = answers.sort(() => Math.random() - 0.5);

                        answers.forEach(answer => {
                            const button = document.createElement('button');
                            button.textContent = answer;
                            button.classList.add('answer-btn');
                            button.addEventListener('click', () => selectAnswer(button, answer, q.correct_answer));
                            answersContainer.appendChild(button);
                        });
                    } else {
                        // Fin del quiz
                        questionDisplay.textContent = `¡Quiz Terminado! Tu puntuación final es: ${quizScore} de ${questions.length}`;
                        answersContainer.innerHTML = '';
                        restartQuizBtn.style.display = 'block';
                    }
                }

                function selectAnswer(selectedButton, selectedAnswer, correctAnswer) {
                    if (answeredThisQuestion) return; // Evitar múltiples clics
                    answeredThisQuestion = true;

                    // Deshabilitar todos los botones de respuesta
                    Array.from(answersContainer.children).forEach(btn => btn.disabled = true);

                    if (selectedAnswer === correctAnswer) {
                        quizScore++;
                        selectedButton.classList.add('correct');
                    } else {
                        selectedButton.classList.add('incorrect');
                        // Mostrar la respuesta correcta
                        Array.from(answersContainer.children).forEach(btn => {
                            if (btn.textContent === correctAnswer) {
                                btn.classList.add('correct');
                            }
                        });
                    }
                    quizScoreDisplay.textContent = `Puntuación: ${quizScore}`;
                    nextQuestionBtn.style.display = 'block';
                }

                nextQuestionBtn.addEventListener('click', () => {
                    currentQuestionIndex++;
                    displayQuestion();
                });

                restartQuizBtn.addEventListener('click', () => {
                    fetchQuestions(); // Vuelve a cargar preguntas para un nuevo quiz
                });
            });
            
            window.onload = () => {
                fetchRandomQuote();
            };
        </script>
    </body>--%>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%-- Título de la página --%>
        <title><s:text name="error404.title"/></title>
        <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/404.css">
        <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/404.css" disabled>
    </head>
    <body>
        <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
        <div class="error-container">
            <h1><s:text name="error404.heading"/></h1>
            <p id="randomMessage"></p>
            <p><s:text name="error404.returnHome"/> <a href="${pageContext.request.contextPath}/"><s:text name="error404.homePageLink"/></a> <s:text name="error404.orTryNavigating"/></p>
        </div>

        <div class="quote-section">
            <p id="quoteText"></p>
            <span id="quoteAuthor" class="author"></span>
        </div>

        <div id="mainGameContainer"></div>
        
        <script>
            // --- Lógica para mensajes aleatorios y citas ---
            // Definir los mensajes aleatorios usando las claves de i18n
            const messages = [
                "<s:text name="error404.message.lost"/>",
                "<s:text name="error404.message.somethingLost"/>",
                "<s:text name="error404.message.escaped"/>",
                "<s:text name="error404.message.notExist"/>",
                "<s:text name="error404.message.notOnMap"/>",
                "<s:text name="error404.message.notFound"/>",
                "<s:text name="error404.message.break"/>",
                "<s:text name="error404.message.studiedTooMuch"/>",
                "<s:text name="error404.message.undiscovered"/>",
                "<s:text name="error404.message.algorithmsFail"/>"
            ];

            function getRandomMessage() {
                const randomIndex = Math.floor(Math.random() * messages.length);
                return messages[randomIndex];
            }

            document.getElementById('randomMessage').textContent = getRandomMessage();

            const quoteTextElement = document.getElementById('quoteText');
            const quoteAuthorElement = document.getElementById('quoteAuthor');

            async function fetchRandomQuote() {
                try {
                    const response = await fetch('https://api.quotable.io/random');
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    const data = await response.json();
                    if (data.content && data.author) {
                        quoteTextElement.textContent = `"` + data.content + `"`;
                        quoteAuthorElement.textContent = `- ` + data.author;
                    } else {
                        throw new Error("Datos de cita incompletos.");
                    }
                } catch (error) {
                    console.error("Error al obtener la cita:", error);
                    // Fallback a cita internacionalizada si la API falla
                    quoteTextElement.textContent = `<s:text name="error404.quoteFallbackText"/>`;
                    quoteAuthorElement.textContent = `<s:text name="error404.quoteFallbackAuthor"/>`;
                }
            }
            
            // --- Contenedores para los juegos ---
            const mainGameContainer = document.getElementById('mainGameContainer');

            // HTML para el juego "Evita los Distractores"
            const flappyGameHtml = `
                <div id="flappyGame" class="game-container">
                    <h2><s:text name="game.distractors.title"/></h2>
                    <p class="game-instructions"><s:text name="game.distractors.instructions"/></p>
                    <canvas id="gameCanvas" width="400" height="300"></canvas>
                    <button id="startGameBtn" class="game-start-btn"><s:text name="game.distractors.startButton"/></button>
                    <p class="game-instructions"><s:text name="game.distractors.score"/> <span id="flappyScoreDisplay">0</span></p>
                </div>
            `;

            // HTML para el juego de preguntas (Quiz)
            const quizGameHtml = `
                <div id="quizGame" class="game-container">
                    <h3><s:text name="game.quiz.title"/></h3>
                    <div id="quizLoading"><s:text name="game.quiz.loading"/></div>
                    <div id="quizContent" style="display:none;">
                        <p id="questionDisplay"></p>
                        <div id="answersContainer"></div>
                        <p id="quizScoreDisplay"><s:text name="game.quiz.score"/> 0</p>
                        <button id="nextQuestionBtn" style="display:none;"><s:text name="game.quiz.nextQuestion"/></button>
                        <button id="restartQuizBtn" style="display:none;"><s:text name="game.quiz.restartQuiz"/></button>
                    </div>
                </div>
            `;
            
            // --- Lógica de selección aleatoria del juego ---
            document.addEventListener('DOMContentLoaded', function() {
                const gameToDisplay = Math.floor(Math.random() * 2); // 0 para Evita los Distractores, 1 para Quiz

                if (gameToDisplay === 0) {
                    mainGameContainer.innerHTML = flappyGameHtml;
                    initializeFlappyGame();
                    console.log("Mostrando el juego 'Evita los Distractores'.");
                } else {
                    mainGameContainer.innerHTML = quizGameHtml;
                    initializeQuizGame();
                    console.log("Mostrando el juego de preguntas.");
                }
            });

            // --- Lógica del Juego "Evita los Distractores" ---
            function initializeFlappyGame() {
                const canvas = document.getElementById('gameCanvas');
                const ctx = canvas.getContext('2d');
                const startGameBtn = document.getElementById('startGameBtn');
                const flappyScoreDisplay = document.getElementById('flappyScoreDisplay'); // ID único
                
                let student = { x: 50, y: canvas.height / 2, width: 20, height: 20, dy: 0, gravity: 0.2, jumpPower: -4 };
                let distractors = [];
                let gameSpeed = 2;
                let score = 0;
                let isPlaying = false;
                let gameLoopInterval;
                let distractorSpawnInterval;

                function drawStudent() {
                    ctx.fillStyle = 'green'; // Representa al estudiante o libro
                    ctx.fillRect(student.x, student.y, student.width, student.height);
                }

                function drawDistractors() {
                    ctx.fillStyle = 'red'; // Representa los distractores
                    distractors.forEach(d => {
                        ctx.fillRect(d.x, d.y, d.width, d.height);
                    });
                }

                function updateGameArea() {
                    if (!isPlaying) return;

                    ctx.clearRect(0, 0, canvas.width, canvas.height); // Limpiar canvas

                    // Actualizar estudiante
                    student.dy += student.gravity;
                    student.y += student.dy;

                    // Limitar estudiante a los bordes del canvas
                    if (student.y + student.height > canvas.height) {
                        student.y = canvas.height - student.height;
                        student.dy = 0;
                        gameOver();
                    }
                    if (student.y < 0) {
                        student.y = 0;
                        student.dy = 0;
                    }

                    // Actualizar distractores
                    distractors.forEach((d, index) => {
                        d.x -= gameSpeed;

                        // Eliminar distractores fuera de pantalla
                        if (d.x + d.width < 0) {
                            distractors.splice(index, 1);
                            score++; // Incrementar puntuación al pasar un distractor
                            flappyScoreDisplay.textContent = `<s:text name="game.distractors.score"/> ${score}`;
                        }

                        // Detección de colisiones (simplificada)
                        if (student.x < d.x + d.width &&
                            student.x + student.width > d.x &&
                            student.y < d.y + d.height &&
                            student.y + student.height > d.y) {
                            gameOver();
                        }
                    });

                    drawStudent();
                    drawDistractors();
                }

                function spawnDistractor() {
                    if (!isPlaying) return;
                    const distractorHeight = 30 + Math.random() * 40; // Altura aleatoria
                    const distractorGap = 80 + Math.random() * 50; // Espacio entre distractores
                    const topDistractorY = Math.random() * (canvas.height - distractorGap - distractorHeight * 2) + distractorHeight;

                    distractors.push({ x: canvas.width, y: 0, width: 20, height: topDistractorY }); // Parte superior del distractor
                    distractors.push({ x: canvas.width, y: topDistractorY + distractorGap, width: 20, height: canvas.height - (topDistractorY + distractorGap) }); // Parte inferior
                }

                function studentJump() {
                    if (isPlaying) {
                        student.dy = student.jumpPower;
                    }
                }

                function startGame() {
                    if (isPlaying) return; // Evita iniciar múltiples veces

                    isPlaying = true;
                    score = 0;
                    flappyScoreDisplay.textContent = `<s:text name="game.distractors.score"/> ${score}`;
                    student.y = canvas.height / 2;
                    student.dy = 0;
                    distractors = []; // Limpiar distractores anteriores

                    startGameBtn.textContent = `<s:text name="game.distractors.restartButton"/>`;
                    startGameBtn.onclick = resetGame; // Cambiar acción del botón a reset

                    gameLoopInterval = setInterval(updateGameArea, 1000 / 60); // 60 FPS
                    distractorSpawnInterval = setInterval(spawnDistractor, 2000); // Aparece un distractor cada 2 segundos
                }

                function gameOver() {
                    clearInterval(gameLoopInterval);
                    clearInterval(distractorSpawnInterval);
                    isPlaying = false;
                    flappyScoreDisplay.textContent = `<s:text name="game.distractors.gameOver"/> ${score}.`; // Actualiza el display de puntuación
                    startGameBtn.textContent = `<s:text name="game.distractors.playAgainButton"/>`;
                    startGameBtn.onclick = startGame; // Vuelve a la acción de iniciar
                }

                function resetGame() {
                    gameOver(); // Detiene el juego actual
                    startGame(); // Inicia un nuevo juego
                }

                // Event Listeners para el juego "Evita los Distractores"
                // Estos listeners deben ser asignados DESPUÉS de que el canvas y el botón se hayan creado en el DOM
                // por mainGameContainer.innerHTML = flappyGameHtml;
                // Por eso, se inicializan dentro de initializeFlappyGame
                // canvas.addEventListener('click', studentJump);
                // document.addEventListener('keydown', (e) => { ... });
                // startGameBtn.addEventListener('click', startGame);

                // drawStudent(); // Dibujar el estudiante inicial
            }

            // --- Lógica del Juego de Preguntas (Quiz) ---
            let questions = [];
            let currentQuestionIndex = 0;
            let quizScore = 0; // Usar quizScore para el quiz
            let answeredThisQuestion = false;

            // Estas referencias deben ser obtenidas DESPUÉS de que el HTML del quiz se haya cargado en el DOM
            let questionDisplay;
            let answersContainer;
            let quizScoreDisplay;
            let nextQuestionBtn;
            let restartQuizBtn;
            let quizLoading;
            let quizContent;

            function getQuizElements() {
                questionDisplay = document.getElementById('questionDisplay');
                answersContainer = document.getElementById('answersContainer');
                quizScoreDisplay = document.getElementById('quizScoreDisplay');
                nextQuestionBtn = document.getElementById('nextQuestionBtn');
                restartQuizBtn = document.getElementById('restartQuizBtn');
                quizLoading = document.getElementById('quizLoading');
                quizContent = document.getElementById('quizContent');
            }

            async function fetchQuestions() {
                getQuizElements(); // Asegurarse de que los elementos estén referenciados
                quizLoading.style.display = 'block';
                quizContent.style.display = 'none';
                try {
                    const response = await fetch('https://opentdb.com/api.php?amount=10&type=multiple&encode=url3986');
                    const data = await response.json();

                    if (data.response_code === 0) {
                        questions = data.results.map(q => ({
                            question: decodeURIComponent(q.question),
                            correct_answer: decodeURIComponent(q.correct_answer),
                            incorrect_answers: q.incorrect_answers.map(ans => decodeURIComponent(ans))
                        }));
                        console.log("Preguntas cargadas:", questions);
                        quizLoading.style.display = 'none';
                        quizContent.style.display = 'block';
                        startQuiz();
                    } else {
                        console.error("Error al cargar preguntas:", data.response_code);
                        questionDisplay.textContent = `<s:text name="game.quiz.questionLoadError"/>`;
                        quizLoading.style.display = 'none';
                        restartQuizBtn.style.display = 'block';
                    }
                } catch (error) {
                    console.error("Error de red al cargar preguntas:", error);
                    questionDisplay.textContent = `<s:text name="game.quiz.networkError"/>`;
                    quizLoading.style.display = 'none';
                    restartQuizBtn.style.display = 'block';
                }
            }

            function initializeQuizGame() {
                fetchQuestions();
                // Asignar listeners después de que los botones existan
                nextQuestionBtn.addEventListener('click', () => {
                    currentQuestionIndex++;
                    displayQuestion();
                });
                restartQuizBtn.addEventListener('click', () => {
                    fetchQuestions(); // Vuelve a cargar preguntas para un nuevo quiz
                });
            }

            function startQuiz() {
                currentQuestionIndex = 0;
                quizScore = 0;
                quizScoreDisplay.textContent = `<s:text name="game.quiz.score"/> ${quizScore}`;
                nextQuestionBtn.style.display = 'none';
                restartQuizBtn.style.display = 'none';
                displayQuestion();
            }

            function displayQuestion() {
                answeredThisQuestion = false;
                answersContainer.innerHTML = ''; // Limpiar respuestas anteriores
                nextQuestionBtn.style.display = 'none';

                if (currentQuestionIndex < questions.length) {
                    const q = questions[currentQuestionIndex];
                    questionDisplay.textContent = q.question;

                    let answers = [...q.incorrect_answers, q.correct_answer];
                    // Mezclar las respuestas para que no siempre esté la correcta en el mismo lugar
                    answers = answers.sort(() => Math.random() - 0.5);

                    answers.forEach(answer => {
                        const button = document.createElement('button');
                        button.textContent = answer;
                        button.classList.add('answer-btn');
                        button.addEventListener('click', () => selectAnswer(button, answer, q.correct_answer));
                        answersContainer.appendChild(button);
                    });
                } else {
                    // Fin del quiz
                    // Usar MessageFormat para el texto con placeholders
                    const quizFinishedText = `<s:text name="game.quiz.quizFinished" />`;
                    const formattedText = quizFinishedText.replace('{0}', quizScore).replace('{1}', questions.length);
                    questionDisplay.textContent = formattedText;

                    answersContainer.innerHTML = '';
                    restartQuizBtn.style.display = 'block';
                }
            }

            function selectAnswer(selectedButton, selectedAnswer, correctAnswer) {
                if (answeredThisQuestion) return; // Evitar múltiples clics
                answeredThisQuestion = true;

                // Deshabilitar todos los botones de respuesta
                Array.from(answersContainer.children).forEach(btn => btn.disabled = true);

                if (selectedAnswer === correctAnswer) {
                    quizScore++;
                    selectedButton.classList.add('correct');
                } else {
                    selectedButton.classList.add('incorrect');
                    // Mostrar la respuesta correcta
                    Array.from(answersContainer.children).forEach(btn => {
                        if (btn.textContent === correctAnswer) {
                            btn.classList.add('correct');
                        }
                    });
                }
                quizScoreDisplay.textContent = `<s:text name="game.quiz.score"/> ${quizScore}`;
                nextQuestionBtn.style.display = 'block';
            }
            
            window.onload = () => {
                fetchRandomQuote();
            };
        </script>
        <footer>
            <%-- Texto del pie de página --%>
            <s:text name="footer.copyright"/>
        </footer>
    </body>
</html>
