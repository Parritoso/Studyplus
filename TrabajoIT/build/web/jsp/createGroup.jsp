<%-- 
    Document   : createGroupç
    Created on : 03-jun-2025, 13:09:17
    Author     : Parri
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><s:text name="group.create.title"/></title>
    <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/createGroup.css">
    <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/createGroup.css" disabled>
</head>
<body>
    <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
    <%@ include file="/jsp/common/navbar.jsp" %>

    <div class="container">
        <h2><s:text name="group.create.heading"/></h2>

        <%-- Mostrar errores de acción si existen --%>
        <s:if test="hasActionErrors()">
            <div class="alert  alert-danger">
                <strong><s:text name="alert.danger"/></strong> <s:actionerror escape="false"/>
            </div>
        </s:if>

        <s:form action="createGroup" method="post">
            <div class="form-group">
                <label for="grupo_nombre"><s:text name="group.create.nameLabel"/></label>
                <s:textfield name="grupo.nombre" id="grupo_nombre" cssClass="form-control" placeholder="%{getText('group.create.namePlaceholder')}"/>
                <s:fielderror fieldName="grupo.nombre" cssClass="error-message"/>
            </div>
            <div class="form-group">
                <label for="grupo_descripcion"><s:text name="group.create.descriptionLabel"/></label>
                <s:textarea name="grupo.descripcion" id="grupo_descripcion" cssClass="form-control" rows="5" placeholder="%{getText('group.create.descriptionPlaceholder')}"/>
                <s:fielderror fieldName="grupo.descripcion" cssClass="error-message"/>
            </div>
            <div class="form-group">
                <s:submit value="%{getText('group.create.submitButton')}" cssClass="btn"/>
            </div>
        </s:form>
        <p class="links-auth">
            <s:url action="dashboard" var="dashboardUrl"/>
            <s:a href="%{dashboardUrl}"><s:text name="group.create.backToDashboard"/></s:a>
        </p>
    </div>

    <footer>
        <s:text name="footer.copyright"/>
    </footer>
</body>
</html>
