<%-- WEB-INF/jsp/common/navbar.jsp --%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<style>
    /* Restablecer y transiciones (aplicar solo al contenedor del switch si es necesario, o mantener un reseteo más específico) */
/* En lugar de *, usa selectores más específicos */
.day-night-switch-container * {
    margin: 0; /* Asegúrate de que no haya `xp` en la unidad */
    padding: 0; /* Asegúrate de que no haya `xp` en la unidad */
    box-sizing: border-box; /* Recomendado para mejor control del layout */
    transition: all 0.8s ease;
}

.day-night-switch-container body{
    width: 100vw;
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(180deg,#313880 0%, #080b23 100%);
    position: relative;
}

.day-night-switch-container h4 { 
    color: #17198f;
    margin-bottom: 20px;
    text-align: center;
    font-weight: 600;
}

.day-night-switch-container label{
    width: 60px; 
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #17193f;
    border-radius: 30px;
    cursor: pointer;
    overflow: hidden;
}

/* Opcional: Si necesitas un reset global más específico para este componente */
.day-night-switch-container input[type="checkbox"] {
    display: none;
}

.day-night-switch-container .switch-btn {
    width: 180px;
    height: 60px;
    background: #212659;
    border: 4px solid #ffffff17;
    border-radius: 50px;
    padding: 6px;
    z-index: 999; /* Asegura que esté por encima de otros elementos si es necesario */
    position: relative; /* Añadir position relative para que el bg-fill absoluto funcione dentro de él */
}

.day-night-switch-container .icons {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
    user-select: none;
    color: #17193f;
    transform: translate(0px, 41px);
}

.day-night-switch-container .icons img {
    width: 62px;
    height: 62px;
    object-fit: cover;
}

.day-night-switch-container label[for="toggle--daynight"] { /* Especifica la etiqueta si hay más etiquetas */
    width: 60px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #17193f;
    border-radius: 30px;
    cursor: pointer;
    overflow: hidden;
}

/* Estilos cuando el checkbox está marcado */
.day-night-switch-container input[type="checkbox"]:checked ~ .switch-btn {
    background: #ffffff;
    border: 4px solid #c3dfffba;
}

.day-night-switch-container input[type="checkbox"]:checked ~ .bg-fill {
    background-color: #ddecfe;
    border-radius: 50%;
    transform: scale(200); /* Ajusta este valor si el efecto es demasiado grande o pequeño */
    color: #fff; /* Esto no afectará el color de fondo, pero lo mantengo por si es una copia de algo más */
}

.day-night-switch-container input[type="checkbox"]:checked ~ .switch-btn label {
    background: #ffffff;
    transform: translateX(calc(180px - 60px - 2 * 6px)); /* Ancho del switch - ancho del label - 2 * padding */
    box-shadow: 0px 3px 5px 0px rgba(0,0,0,0.2);
}

.day-night-switch-container input[type="checkbox"]:checked ~ .switch-btn label .icons {
    transform: translate(0px, -41px);
}

.day-night-switch-container .bg-fill {
    width: 20px;
    height: 20px;
    background: transparent;
    border-radius: 50%;
    position: absolute;
    /* Ajusta top y left para que el círculo aparezca desde el centro del switch-btn */
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%); /* Centra el elemento */
    z-index: -1; /* Detrás del switch-btn */
}
</style>

<div class="navbar">
    <%-- Generar URLs para las acciones --%>
    <s:url action="welcome" var="welcomeUrl"/>
    <s:url action="features" var="featuresUrl"/>
    <s:url action="about" var="aboutUrl"/>
    <s:url action="contact" var="contactUrl"/>
    <s:url action="dashboard" var="dashboardUrl"/>
    <s:url action="login" var="loginUrl"/>
    <s:url action="register" var="registerUrl"/>
    <s:url action="logout" var="logoutUrl"/>
    <s:url action="showCreateDelivery" var="createDeliveryUrl"/>
    <s:url action="showCreateExam" var="createExamUrl"/>
    <s:url action="manageDeliverables" var="upcomingTasksUrl"/>
    <s:url action="friendsAndMessages" var="friendsAndMessagesUrl"/>
    <s:url action="editProfile" var="profileUrl"/>

    <s:a href="%{welcomeUrl}">Inicio</s:a> |
    <s:a href="%{featuresUrl}">Características</s:a> |
    <s:a href="%{aboutUrl}">Acerca de</s:a> |
    <s:a href="%{contactUrl}">Contacto</s:a> |

    <%-- Lógica condicional básica para mostrar enlaces según si el usuario está logeado --%>
    <s:if test="#session.isLoggedIn == true">
        <s:a href="%{dashboardUrl}">Dashboard</s:a> |
        <s:a href="%{createDeliveryUrl}">Nueva Entrega</s:a> |
        <s:a href="%{createExamUrl}">Nuevo Examen</s:a> |
        <s:a href="%{upcomingTasksUrl}">Mis Tareas</s:a> |
        <s:a href="%{friendsAndMessagesUrl}">Chats y Amistades</s:a> |
        <s:a href="%{profileUrl}"><s:text name="navbar.profile"/></s:a> |
        <s:a href="%{logoutUrl}">Cerrar Sesión</s:a>
    </s:if>
    <s:else>
        <s:a href="%{loginUrl}">Iniciar Sesión</s:a> |
        <s:a href="%{registerUrl}">Registrarse</s:a>
    </s:else>

    <div class="day-night-switch-container">
        <input type="checkbox" id="toggle--daynight" />
        <div class="bg-fill"></div>
        <div class="switch-btn">
            <label for="toggle--daynight">
                <div class="icons">
                    <img src="${pageContext.request.contextPath}/public/moon-icon.png" alt="moon">
                    <img src="${pageContext.request.contextPath}/public/sun-icon.png" alt="moon">
                </div>
            </label>
        </div>
    </div>
</div>
