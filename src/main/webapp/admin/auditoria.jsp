<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Auditoría - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <h2 class="fw-bold mb-4"><i class="bi bi-clock-history me-2 text-primary"></i>Registro de auditoría</h2>

    <div class="card border-0 shadow-sm">
        <div class="card-body table-responsive">
            <table class="table table-hover align-middle table-sm">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Fecha y hora</th>
                        <th>Usuario</th>
                        <th>Acción</th>
                        <th>Tabla</th>
                        <th>Registro</th>
                        <th>Detalles</th>
                        <th>IP</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="a" items="${registros}">
                        <tr>
                            <td>${a.idAuditoria}</td>
                            <td class="small text-muted"><fmt:formatDate value="${a.fechaHora}" pattern="yyyy-MM-dd HH:mm:ss" timeZone="America/Bogota" /></td>
                            <td><c:out value="${a.nombreUsuario}" /> <span class="text-muted small">(#${a.idUsuario})</span></td>
                            <td><span class="badge bg-info text-dark"><c:out value="${a.accion}" /></span></td>
                            <td class="small"><c:out value="${a.tablaAfectada}" /></td>
                            <td class="small">${a.idRegistro}</td>
                            <td class="small text-muted"><c:out value="${a.detalles}" /></td>
                            <td class="small text-muted"><c:out value="${a.ipAddress}" /></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</main>

<%@ include file="../WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>