<%-- 
    Document   : profile
    Created on : 03-jun-2025, 11:22:40
    Author     : Parri
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><s:text name="profile.title"/></title>
    <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/profile.css">
    <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/profile.css" disabled>
</head>
<body>
    <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
    <%@ include file="/jsp/common/navbar.jsp" %>

    <div class="container">
        <h2><s:text name="profile.heading"/></h2>

        <%-- Mensajes de feedback general --%>
        <div id="generalFeedback" class="feedback-message" style="display:none;"></div>

        <%-- Información del Perfil --%>
        <div class="profile-info">
            <p><strong><s:text name="profile.info.username"/></strong> <s:property value="usuario.nombre"/></p>
            <p><strong><s:text name="profile.info.email"/></strong> <s:property value="usuario.email"/></p>
            <p><strong><s:text name="profile.info.points"/></strong> <s:property value="usuario.puntos"/></p>
            <p><strong><s:text name="profile.info.registrationDate"/></strong> <s:date name="usuario.fechaRegistro" format="dd/MM/yyyy HH:mm"/></p>
        </div>

        <div class="section-separator"></div>

        <%-- Formulario de Edición de Perfil --%>
        <div class="form-section">
            <h3><s:text name="profile.edit.heading"/></h3>
            <s:form id="editProfileForm" action="updateProfileAjax" method="post" cssClass="ajax-form">
                <div class="form-group">
                    <label for="usuario_nombre"><s:text name="profile.edit.nameLabel"/></label>
                    <s:textfield name="usuario.nombre" id="usuario_nombre" cssClass="form-control" value="%{usuario.nombre}"/>
                    <s:fielderror fieldName="usuario.nombre" cssClass="error-message"/>
                </div>
                <div class="form-group">
                    <label for="usuario_email"><s:text name="profile.edit.emailLabel"/></label>
                    <s:textfield name="usuario.email" id="usuario_email" cssClass="form-control" type="email" value="%{usuario.email}"/>
                    <s:fielderror fieldName="usuario.email" cssClass="error-message"/>
                </div>
                <%--<div class="form-group">
                    <label for="usuario_configuracionPerfil"><s:text name="profile.edit.configLabel"/></label>
                    <s:textarea name="usuario.configuracionPerfil" id="usuario_configuracionPerfil" cssClass="form-control" rows="5" value="%{usuario.configuracionPerfil}"/>
                    <p class="error-message" id="configuracionPerfilError" style="display:none;"></p>
                </div>--%>
                <div class="form-group">
                    <label for="localeSelector"><s:text name="profile.edit.languageLabel"/></label>
                    <s:select name="locale_e" id="localeSelector" list="availableLocales.entrySet()" listKey="key" listValue="value"
                              headerKey="" headerValue="%{getText('profile.edit.selectLanguage')}" cssClass="form-control"
                              value="%{locale}"/> <%-- Usamos 'locale' de la acción --%>
                </div>
                <div class="form-group">
                    <label for="favoriteTechniques"><s:text name="profile.edit.favTechniquesLabel"/></label>
                    <%-- CAMBIO CLAVE: name="favoriteTechniqueIds" y value="%{parseFavoriteTechniques(usuario.configuracionPerfil)}" --%>
                    <s:select name="favoriteTechniqueIds" id="favoriteTechniques" list="tecnicasDisponibles" listKey="id" listValue="nombre"
                              multiple="true" size="5" cssClass="form-control"
                              value="%{@com.estudyplus.controlador.action.ProfileAction@parseFavoriteTechniques(usuario.configuracionPerfil)}"/>
                    <p class="error-message" id="favoriteTechniquesError" style="display:none;"></p>
                </div>

                <button type="submit" class="btn"><s:text name="profile.edit.saveButton"/></button>
            </s:form>
        </div>

        <div class="section-separator"></div>

        <%-- Sección de Cambio de Contraseña --%>
        <div class="form-section">
            <h3><s:text name="profile.changePassword.heading"/></h3>
            <button type="button" class="btn btn-warning" id="openChangePasswordModalBtn"><s:text name="profile.changePassword.button"/></button>
        </div>

        <div class="section-separator"></div>

        <%-- Sección de Eliminar Cuenta --%>
        <div class="form-section">
            <h3><s:text name="profile.deleteAccount.heading"/></h3>
            <button type="button" class="btn btn-danger" id="openDeleteAccountModalBtn"><s:text name="profile.deleteAccount.button"/></button>
        </div>
    </div>

    <footer>
        <s:text name="footer.copyright"/>
    </footer>

    <%-- Modal para Cambio de Contraseña --%>
    <div id="changePasswordModal" class="modal">
        <div class="modal-content">
            <h3><s:text name="profile.changePassword.modalTitle"/></h3>
            <div id="changePasswordFeedback" class="feedback-message" style="display:none;"></div>
            <s:form id="changePasswordForm" action="changePasswordAjax" method="post" cssClass="ajax-form">
                <div class="form-group">
                    <label for="currentPassword"><s:text name="profile.changePassword.currentPasswordLabel"/></label>
                    <s:password name="currentPassword" id="currentPassword" cssClass="form-control" placeholder="%{getText('profile.changePassword.currentPasswordPlaceholder')}"/>
                    <p class="error-message" id="currentPasswordError" style="display:none;"></p>
                </div>
                <div class="form-group">
                    <label for="newPassword"><s:text name="profile.changePassword.newPasswordLabel"/></label>
                    <s:password name="newPassword" id="newPassword" cssClass="form-control" placeholder="%{getText('profile.changePassword.newPasswordPlaceholder')}"/>
                    <p class="error-message" id="newPasswordError" style="display:none;"></p>
                </div>
                <div class="form-group">
                    <label for="confirmNewPassword"><s:text name="profile.changePassword.confirmNewPasswordLabel"/></label>
                    <s:password name="confirmNewPassword" id="confirmNewPassword" cssClass="form-control" placeholder="%{getText('profile.changePassword.confirmNewPasswordPlaceholder')}"/>
                    <p class="error-message" id="confirmNewPasswordError" style="display:none;"></p>
                </div>
                <div class="modal-buttons">
                    <button type="button" class="btn btn-cancel" id="cancelChangePasswordBtn"><s:text name="profile.modal.cancelButton"/></button>
                    <button type="submit" class="btn btn-submit"><s:text name="profile.changePassword.submitButton"/></button>
                </div>
            </s:form>
        </div>
    </div>

    <%-- Modal para Eliminar Cuenta --%>
    <div id="deleteAccountModal" class="modal">
        <div class="modal-content">
            <h3><s:text name="profile.deleteAccount.modalTitle"/></h3>
            <div id="deleteAccountFeedback" class="feedback-message" style="display:none;"></div>
            <p><s:text name="profile.deleteAccount.confirmMessage"/></p>
            <s:form id="deleteAccountForm" action="deleteAccountAjax" method="post" cssClass="ajax-form">
                <div class="form-group">
                    <label for="deletePasswordConfirmation"><s:text name="profile.deleteAccount.passwordLabel"/></label>
                    <s:password name="deletePasswordConfirmation" id="deletePasswordConfirmation" cssClass="form-control" placeholder="%{getText('profile.deleteAccount.passwordPlaceholder')}"/>
                    <p class="error-message" id="deletePasswordConfirmationError" style="display:none;"></p>
                </div>
                <div class="modal-buttons">
                    <button type="button" class="btn btn-cancel" id="cancelDeleteAccountBtn"><s:text name="profile.modal.cancelButton"/></button>
                    <button type="submit" class="btn btn-danger"><s:text name="profile.deleteAccount.submitButton"/></button>
                </div>
            </s:form>
        </div>
    </div>

    <script type="text/javascript">
        document.addEventListener('DOMContentLoaded', function() {
            const generalFeedback = document.getElementById('generalFeedback');

            // --- Funciones de Feedback ---
            function showFeedback(container, message, isSuccess) {
                container.textContent = message;
                container.className = 'feedback-message ' + (isSuccess ? 'message-success' : 'message-error');
                container.style.display = 'block';
                setTimeout(() => {
                    container.style.display = 'none';
                }, 5000); // Ocultar después de 5 segundos
            }

            function clearFormErrors(formId) {
                const form = document.getElementById(formId);
                if (form) {
                    form.querySelectorAll('.error-message').forEach(el => el.style.display = 'none');
                }
            }

            // --- Lógica de Modales ---
            const changePasswordModal = document.getElementById('changePasswordModal');
            const openChangePasswordModalBtn = document.getElementById('openChangePasswordModalBtn');
            const cancelChangePasswordBtn = document.getElementById('cancelChangePasswordBtn');
            const changePasswordFeedback = document.getElementById('changePasswordFeedback');

            const deleteAccountModal = document.getElementById('deleteAccountModal');
            const openDeleteAccountModalBtn = document.getElementById('openDeleteAccountModalBtn');
            const cancelDeleteAccountBtn = document.getElementById('cancelDeleteAccountBtn');
            const deleteAccountFeedback = document.getElementById('deleteAccountFeedback');

            // Abrir modal de cambio de contraseña
            if (openChangePasswordModalBtn) {
                openChangePasswordModalBtn.addEventListener('click', function() {
                    changePasswordModal.style.display = 'flex';
                    changePasswordFeedback.style.display = 'none'; // Ocultar feedback anterior
                    clearFormErrors('changePasswordForm');
                });
            }

            // Cerrar modal de cambio de contraseña
            if (cancelChangePasswordBtn) {
                cancelChangePasswordBtn.addEventListener('click', function() {
                    changePasswordModal.style.display = 'none';
                });
            }
            window.addEventListener('click', function(event) {
                if (event.target == changePasswordModal) {
                    changePasswordModal.style.display = 'none';
                }
            });

            // Abrir modal de eliminar cuenta
            if (openDeleteAccountModalBtn) {
                openDeleteAccountModalBtn.addEventListener('click', function() {
                    deleteAccountModal.style.display = 'flex';
                    deleteAccountFeedback.style.display = 'none'; // Ocultar feedback anterior
                    clearFormErrors('deleteAccountForm');
                });
            }

            // Cerrar modal de eliminar cuenta
            if (cancelDeleteAccountBtn) {
                cancelDeleteAccountBtn.addEventListener('click', function() {
                    deleteAccountModal.style.display = 'none';
                });
            }
            window.addEventListener('click', function(event) {
                if (event.target == deleteAccountModal) {
                    deleteAccountModal.style.display = 'none';
                }
            });

            // --- Manejo de Formularios AJAX ---
            async function handleAjaxFormSubmit(event) {
                event.preventDefault(); // Prevenir el envío normal del formulario

                const form = event.target;
                const formData = new URLSearchParams(new FormData(form));
                const actionUrl = form.action;
                const feedbackContainer = form.querySelector('.feedback-message') || generalFeedback;
                
                clearFormErrors(form.id); // Limpiar errores de campo anteriores

                try {
                    const response = await fetch(actionUrl, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        },
                        body: formData
                    });
                    const jsonResponse = await response.json();

                    if (jsonResponse.success) {
                        showFeedback(feedbackContainer, jsonResponse.message, true);
                        // Recargar la página o actualizar el DOM si es necesario después del éxito
                        if (form.id === 'deleteAccountForm') {
                            // Redirigir a la página de bienvenida después de eliminar la cuenta
                            window.location.href = '<s:url action="welcome"/>';
                        } else {
                            // Para otros formularios, simplemente recargar la página para ver los cambios
                            // O actualizar elementos específicos del DOM si la respuesta incluye los datos actualizados
                            location.reload(); // Recarga simple para ver los cambios en el perfil
                        }
                    } else {
                        showFeedback(feedbackContainer, jsonResponse.message, false);
                        // Mostrar errores de campo si existen (Struts2 Json plugin no los devuelve por defecto en JsonResponse)
                        // Si Struts2 enviara fieldErrors en el JSON, los procesaríamos aquí.
                        // Por ahora, solo el mensaje general.
                    }
                } catch (error) {
                    console.error('Error en la petición AJAX:', error);
                    showFeedback(feedbackContainer, '<s:text name="profile.error.ajaxFailed"/>', false);
                }
            }

            // Asignar el manejador a los formularios AJAX
            document.getElementById('editProfileForm').addEventListener('submit', handleAjaxFormSubmit);
            document.getElementById('changePasswordForm').addEventListener('submit', handleAjaxFormSubmit);
            document.getElementById('deleteAccountForm').addEventListener('submit', handleAjaxFormSubmit);
        });
    </script>
</body>
</html>
