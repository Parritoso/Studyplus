<%-- 
    Document   : ranking
    Created on : 06-jun-2025, 19:43:34
    Author     : Daniel
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
    <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Prueba Visual - Ranking de Usuarios</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome (íconos) -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link id="light-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/light-mode/ranking.css">
    <link id="dark-theme" rel="stylesheet" href="${pageContext.request.contextPath}/public/css/dark-mode/ranking.css" disabled>
</head>
<body>
    <script src="${pageContext.request.contextPath}/public/js/darkmode.js"></script>
    <%@ include file="/jsp/common/navbar.jsp" %>
    <div class="ranking-container">
        <h2><i class="fas fa-trophy me-2"></i>Ranking de Usuarios</h2>
        
        <!-- Esta es la tabla que simulará tu JSP -->
       <s:form action="viewGamification">
            <table>
                <tr>
                    <td><s:select name="opcion" list="modos" value="opcion"/></td>
                    <td><s:submit value="Filtrar"/></td>
                </tr>
            </table>
        </s:form>
        
        <div class="card p-3 mb-4"> <%-- Añadido card para mejor visualización --%>
            <s:if test="puntosUsuarioLogeado == 0">
                <div class="alert alert-info text-center" role="alert">
                    ¡Parece que aún no tienes puntos! Para empezar a ganar puntos y aparecer en el ranking, ¡debes realizar **sesiones de estudio**!
                </div>
            </s:if>
            <s:else>
                <div class="text-center">
                    <p class="lead mb-0">¡Hola, <strong><s:property value="usuarioLogeado.nombre"/></strong>!</p>
                    <p class="mb-0">Tienes <strong><s:property value="puntosUsuarioLogeado"/> puntos</strong> y estás en la posición <strong><s:property value="posicionUsuarioLogeado"/></strong> en el ranking <s:property value="opcion"/>.</p>
                </div>
            </s:else>
        </div>
        
        <table class="table table-hover">
            <thead class="thead-dark">
                <tr>
                    <th>#</th>
                    <th>Usuario</th>
                    <th>Puntos</th>
                </tr>
            </thead>
            <tbody>
                <!-- Simulación del iterator con datos estáticos -->
                <s:iterator value="us" status="row">
                    <!-- Aplicamos clases según la posición -->
                    <tr class="<s:if test='#row.index == 0'>table-warning</s:if>
                           <s:elseif test='#row.index == 1'>table-light</s:elseif>
                           <s:elseif test='#row.index == 2'>table-bronze</s:elseif>">

                        <!-- Columna de posición -->
                        <td>
                            <s:if test="#row.index <= 2"> <!-- Primeros 3 puestos -->
                                <span class="badge 
                                    <s:if test='#row.index == 0'>bg-warning</s:if>
                                    <s:elseif test='#row.index == 1'>bg-secondary</s:elseif>
                                    <s:elseif test='#row.index == 2'>bg-bronze</s:elseif>">
                                    <s:property value="#row.index + 1"/>
                                </span>
                            </s:if>
                            <s:else> <!-- Resto de posiciones -->
                                <s:property value="#row.index + 1"/>
                            </s:else>
                        </td>

                        <!-- Columna de nombre -->
                        <td>
                            <s:property value="getNombre()"/>
                            <s:if test="#row.index == 0"> <!-- Corona para el 1ro -->
                                <i class="fas fa-crown text-warning"></i>
                            </s:if>
                        </td>

                        <!-- Columna de puntos -->
                        <td><s:property value="getPuntos()"/></td>
                    </tr>
                </s:iterator>
            </tbody>
        </table>
    </div>

    <!-- Bootstrap JS (opcional) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>