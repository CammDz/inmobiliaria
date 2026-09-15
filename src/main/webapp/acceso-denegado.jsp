<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Acceso denegado - InmoVaIn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="WEB-INF/jspf/navbar.jspf" %>

<main class="container py-5 text-center">
    <div class="card border-0 shadow mx-auto" style="max-width: 500px;">
        <div class="card-body p-5">
            <i class="bi bi-shield-exclamation text-danger display-1"></i>
            <h1 class="fw-bold mt-3">Acceso denegado</h1>
            <p class="text-muted">
                No tienes permisos para acceder a esta sección.
                Si crees que esto es un error, inicia sesión con una cuenta con los permisos adecuados.
            </p>
            <%
                boolean sesionActiva = session != null && session.getAttribute("usuario_id") != null;
            %>
            <% if (sesionActiva) { %>
                <a href="<%= request.getContextPath() %>/index.jsp" class="btn btn-primary"><i class="bi bi-house me-1"></i>Volver al inicio</a>
            <% } else { %>
                <a href="<%= request.getContextPath() %>/login.jsp" class="btn btn-primary"><i class="bi bi-box-arrow-in-right me-1"></i>Iniciar sesión</a>
                <a href="<%= request.getContextPath() %>/catalogo" class="btn btn-outline-primary ms-2">Ver catálogo</a>
            <% } %>
        </div>
    </div>
</main>

<%@ include file="WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>