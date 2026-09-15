<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Documentos - Agente</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fw-bold mb-0"><i class="bi bi-paperclip me-2 text-primary"></i>Documentos adjuntos</h2>
        <c:if test="${not empty idSolicitudFiltro}">
            <span class="text-muted">Solicitud #<c:out value="${idSolicitudFiltro}" /></span>
        </c:if>
    </div>

    <c:choose>
        <c:when test="${empty documentos}">
            <div class="text-center py-5 bg-white rounded border">
                <i class="bi bi-file-earmark-x text-muted" style="font-size: 3rem;"></i>
                <p class="text-muted mt-3">No hay documentos registrados.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card border-0 shadow-sm">
                <div class="card-body table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>#</th><th>Solicitud</th><th>Cliente</th><th>Propiedad</th>
                                <th>Tipo de documento</th><th>Archivo</th><th>Acción</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="d" items="${documentos}">
                                <tr>
                                    <td>${d.idDocumento}</td>
                                    <td>#${d.idSolicitud}</td>
                                    <td><c:out value="${d.nombreCliente}" /></td>
                                    <td><c:out value="${d.tituloPropiedad}" /></td>
                                    <td><c:out value="${d.tipoDocumento}" /></td>
                                    <td class="text-muted small"><c:out value="${d.nombreArchivo}" /></td>
                                    <td>
                                        <div class="d-flex gap-1">
                                            <a href="${pageContext.request.contextPath}/documentos/archivo?id=${d.idDocumento}" target="_blank"
                                               class="btn btn-sm btn-outline-primary"><i class="bi bi-eye"></i> Ver</a>
                                            <a href="documentos?accion=eliminar&idDocumento=${d.idDocumento}&idSolicitud=${d.idSolicitud}"
                                               class="btn btn-sm btn-outline-danger"
                                               onclick="return confirm('¿Eliminar este documento?');"><i class="bi bi-trash"></i></a>
                                        </div>
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