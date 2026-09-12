<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Solicitudes - Agente</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <h2 class="fw-bold mb-4"><i class="bi bi-file-earmark-text me-2 text-primary"></i>Solicitudes</h2>

    <c:choose>
        <c:when test="${empty solicitudes}">
            <div class="text-center py-5 bg-white rounded border">
                <i class="bi bi-inbox text-muted" style="font-size: 3rem;"></i>
                <p class="text-muted mt-3">No hay solicitudes para tus propiedades.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card border-0 shadow-sm">
                <div class="card-body table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>#</th><th>Cliente</th><th>Propiedad</th><th>Tipo</th>
                                <th>Monto ofertado</th><th>Estado</th><th>Fecha</th><th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="s" items="${solicitudes}">
                                <tr>
                                    <td>${s.idSolicitud}</td>
                                    <td><strong><c:out value="${s.nombreCliente}" /></strong><br>
                                        <span class="text-muted small"><c:out value="${s.correoCliente}" /></span></td>
                                    <td><c:out value="${s.tituloPropiedad}" /></td>
                                    <td><span class="badge bg-secondary">${s.tipo}</span></td>
                                    <td>$<c:out value="${s.montoOfrecido}" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${s.estado == 'EN_REVISION'}"><span class="badge bg-info text-dark">EN REVISIÓN</span></c:when>
                                            <c:when test="${s.estado == 'APROBADA'}"><span class="badge bg-success">APROBADA</span></c:when>
                                            <c:when test="${s.estado == 'RECHAZADA'}"><span class="badge bg-danger">RECHAZADA</span></c:when>
                                        </c:choose>
                                    </td>
                                    <td class="small text-muted"><c:out value="${s.fechaRegistro}" /></td>
                                    <td>
                                        <c:if test="${s.estado == 'EN_REVISION'}">
                                            <div class="d-flex gap-1 flex-wrap">
                                                <a href="solicitudes?accion=aprobar&id=${s.idSolicitud}" class="btn btn-sm btn-outline-success"
                                                   onclick="return confirm('¿Aprobar esta solicitud?');"><i class="bi bi-check-lg"></i> Aprobar</a>
                                                <a href="solicitudes?accion=rechazar&id=${s.idSolicitud}" class="btn btn-sm btn-outline-danger"
                                                   onclick="return confirm('¿Rechazar esta solicitud?');"><i class="bi bi-x-lg"></i> Rechazar</a>
                                            </div>
                                        </c:if>
                                        <a href="documentos?idSolicitud=${s.idSolicitud}" class="btn btn-sm btn-outline-info"
                                           title="Ver documentos asociados"><i class="bi bi-paperclip"></i></a>
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