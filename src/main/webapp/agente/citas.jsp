<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Citas - Agente</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <h2 class="fw-bold mb-4"><i class="bi bi-calendar-check me-2 text-primary"></i>Citas de mis propiedades</h2>

    <c:choose>
        <c:when test="${empty citas}">
            <div class="text-center py-5 bg-white rounded border">
                <i class="bi bi-calendar-x text-muted" style="font-size: 3rem;"></i>
                <p class="text-muted mt-3">No hay citas registradas para tus propiedades.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card border-0 shadow-sm">
                <div class="card-body table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>#</th><th>Cliente</th><th>Propiedad</th><th>Fecha</th><th>Hora</th>
                                <th>Estado</th><th>Observaciones</th><th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${citas}">
                                <tr>
                                    <td>${c.idCita}</td>
                                    <td><strong><c:out value="${c.nombreCliente}" /></strong><br>
                                        <span class="text-muted small"><c:out value="${c.correoCliente}" /></span></td>
                                    <td><c:out value="${c.tituloPropiedad}" /><br>
                                        <span class="text-muted small"><c:out value="${c.direccionPropiedad}" /></span></td>
                                    <td><c:out value="${c.fecha}" /></td>
                                    <td><c:out value="${c.hora}" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${c.estado == 'PENDIENTE'}"><span class="badge bg-warning text-dark">PENDIENTE</span></c:when>
                                            <c:when test="${c.estado == 'APROBADA'}"><span class="badge bg-success">APROBADA</span></c:when>
                                            <c:when test="${c.estado == 'RECHAZADA'}"><span class="badge bg-danger">RECHAZADA</span></c:when>
                                            <c:when test="${c.estado == 'CANCELADA'}"><span class="badge bg-secondary">CANCELADA</span></c:when>
                                        </c:choose>
                                    </td>
                                    <td class="small text-muted"><c:out value="${c.observaciones}" /></td>
                                    <td>
                                        <c:if test="${c.estado == 'PENDIENTE'}">
                                            <div class="d-flex gap-1 flex-wrap">
                                                <a href="citas?accion=aprobar&id=${c.idCita}" class="btn btn-sm btn-outline-success"
                                                   onclick="return confirm('¿Aprobar esta cita?');"><i class="bi bi-check-lg"></i> Aprobar</a>
                                                <a href="citas?accion=rechazar&id=${c.idCita}" class="btn btn-sm btn-outline-danger"
                                                   onclick="return confirm('¿Rechazar esta cita?');"><i class="bi bi-x-lg"></i> Rechazar</a>
                                            </div>
                                        </c:if>
                                        <c:if test="${c.estado == 'APROBADA'}">
                                            <a href="citas?accion=cancelar&id=${c.idCita}" class="btn btn-sm btn-outline-secondary"
                                               onclick="return confirm('¿Cancelar esta cita?');"><i class="bi bi-x-circle"></i> Cancelar</a>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<%@ include file="../WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>