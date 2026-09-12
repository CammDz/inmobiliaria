<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis citas - InmoVaIn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fw-bold mb-0"><i class="bi bi-calendar-check me-2 text-primary"></i>Mis citas</h2>
        <a href="../catalogo" class="btn btn-primary"><i class="bi bi-calendar-plus me-1"></i>Solicitar cita</a>
    </div>

    <c:choose>
        <c:when test="${empty citas}">
            <div class="text-center py-5 bg-white rounded border">
                <i class="bi bi-calendar-x text-muted" style="font-size: 3rem;"></i>
                <p class="text-muted mt-3">No tienes citas registradas.</p>
                <a href="../catalogo" class="btn btn-primary">Agendar una visita</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row g-4">
                <c:forEach var="c" items="${citas}">
                    <div class="col-md-6 col-lg-4">
                        <div class="card h-100 shadow-sm border-0">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-start mb-2">
                                    <h6 class="fw-bold mb-0"><i class="bi bi-house me-1 text-primary"></i><c:out value="${c.tituloPropiedad}" /></h6>
                                    <c:choose>
                                        <c:when test="${c.estado == 'PENDIENTE'}"><span class="badge bg-warning text-dark">PENDIENTE</span></c:when>
                                        <c:when test="${c.estado == 'APROBADA'}"><span class="badge bg-success">APROBADA</span></c:when>
                                        <c:when test="${c.estado == 'RECHAZADA'}"><span class="badge bg-danger">RECHAZADA</span></c:when>
                                        <c:when test="${c.estado == 'CANCELADA'}"><span class="badge bg-secondary">CANCELADA</span></c:when>
                                    </c:choose>
                                </div>
                                <p class="text-muted small mb-1"><i class="bi bi-geo-alt me-1"></i><c:out value="${c.direccionPropiedad}" /></p>
                                <p class="small mb-1"><i class="bi bi-calendar me-1"></i><c:out value="${c.fecha}" /> &nbsp; <i class="bi bi-clock me-1"></i><c:out value="${c.hora}" /></p>
                                <p class="small text-muted mb-1"><i class="bi bi-person me-1"></i>Agente: <c:out value="${c.nombreAgente}" /></p>
                                <c:if test="${not empty c.observaciones}">
                                    <p class="small text-muted mb-2"><i class="bi bi-chat-left-text me-1"></i><c:out value="${c.observaciones}" /></p>
                                </c:if>
                                <c:if test="${c.estado == 'PENDIENTE' || c.estado == 'APROBADA'}">
                                    <a href="citas?accion=cancelar&id=${c.idCita}" class="btn btn-sm btn-outline-danger mt-2"
                                       onclick="return confirm('¿Cancelar esta cita?');"><i class="bi bi-x-circle me-1"></i>Cancelar</a>
                                    <a href="../propiedad?id=${c.idPropiedad}" class="btn btn-sm btn-outline-primary mt-2">Ver propiedad</a>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<%@ include file="../WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>