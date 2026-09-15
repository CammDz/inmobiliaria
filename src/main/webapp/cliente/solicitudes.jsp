<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cop" uri="http://inmovain.com/tld/precio" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis solicitudes - InmoVaIn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fw-bold mb-0"><i class="bi bi-file-earmark-text me-2 text-primary"></i>Mis solicitudes</h2>
        <a href="../catalogo" class="btn btn-primary"><i class="bi bi-plus-lg me-1"></i>Nueva solicitud</a>
    </div>

    <c:choose>
        <c:when test="${empty solicitudes}">
            <div class="text-center py-5 bg-white rounded border">
                <i class="bi bi-inbox text-muted" style="font-size: 3rem;"></i>
                <p class="text-muted mt-3">No has enviado solicitudes todavía.</p>
                <a href="../catalogo" class="btn btn-primary">Hacer una solicitud</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>#</th><th>Propiedad</th><th>Tipo</th><th>Monto ofrecido</th>
                                <th>Estado</th><th>Fecha</th><th>Observaciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="s" items="${solicitudes}">
                                <tr>
                                    <td>${s.idSolicitud}</td>
                                    <td><c:out value="${s.tituloPropiedad}" /></td>
                                    <td><span class="badge bg-secondary">${s.tipo}</span></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${s.montoOfrecido > 0}">${cop:colombiano(s.montoOfrecido)}</c:when>
                                            <c:otherwise>—</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${s.estado == 'PENDIENTE'}"><span class="badge bg-warning text-dark">PENDIENTE</span></c:when>
                                            <c:when test="${s.estado == 'EN_REVISION'}"><span class="badge bg-info text-dark">EN REVISIÓN</span></c:when>
                                            <c:when test="${s.estado == 'APROBADA'}"><span class="badge bg-success">APROBADA</span></c:when>
                                            <c:when test="${s.estado == 'RECHAZADA'}"><span class="badge bg-danger">RECHAZADA</span></c:when>
                                        </c:choose>
                                    </td>
                                    <td class="small text-muted"><fmt:formatDate value="${s.fechaRegistro}" pattern="dd/MM/yyyy hh:mm a" /></td>
                                    <td class="small text-muted"><c:out value="${s.observaciones}" /></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

            <h4 class="fw-bold mb-3"><i class="bi bi-paperclip me-2 text-primary"></i>Subir documentos</h4>
            <c:forEach var="s" items="${solicitudes}">
                <c:set var="idSol" value="${s.idSolicitud}" />
                <%
                    int idSol = ((com.inmobiliaria.model.Solicitud) pageContext.findAttribute("s")).getIdSolicitud();
                    java.util.List<com.inmobiliaria.model.DocumentoSolicitud> docsTmp =
                            new com.inmobiliaria.dao.DocumentoSolicitudDAO().listarPorSolicitud(idSol);
                    request.setAttribute("docsTmp", docsTmp);
                %>
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-header d-flex justify-content-between align-items-center bg-white">
                        <strong>Documentos de la solicitud #${s.idSolicitud} — <c:out value="${s.tituloPropiedad}" /></strong>
                        <c:choose>
                            <c:when test="${s.estado == 'PENDIENTE' || s.estado == 'EN_REVISION'}">
                                <button class="btn btn-sm btn-outline-primary" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#docForm${s.idSolicitud}" aria-expanded="false">
                                    <i class="bi bi-upload me-1"></i>Adjuntar documento
                                </button>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-secondary">Solicitud cerrada</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty docsTmp}">
                            <ul class="list-group mb-3">
                                <c:forEach var="d" items="${docsTmp}">
                                    <li class="list-group-item d-flex justify-content-between align-items-center">
                                        <span><i class="bi bi-file-earmark me-2 text-primary"></i>
                                            <c:out value="${d.tipoDocumento}" /> — <c:out value="${d.nombreArchivo}" />
                                        </span>
                                        <a href="${pageContext.request.contextPath}/documentos/archivo?id=${d.idDocumento}" target="_blank"
                                           class="btn btn-sm btn-outline-primary"><i class="bi bi-eye"></i> Ver</a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:if>
                        <div class="collapse" id="docForm${s.idSolicitud}">
                            <form action="solicitudes" method="post" enctype="multipart/form-data">
                                <input type="hidden" name="accion" value="subirDocumento">
                                <input type="hidden" name="idSolicitud" value="${s.idSolicitud}">
                                <div class="row g-2 align-items-center">
                                    <div class="col-md-4">
                                        <select class="form-select" name="tipoDocumento" required>
                                            <option value="">Tipo de documento...</option>
                                            <option value="Cédula">Cédula</option>
                                            <option value="Certificado laboral">Certificado laboral</option>
                                            <option value="Desprendibles">Desprendibles de nómina</option>
                                            <option value="Extracto bancario">Extracto bancario</option>
                                            <option value="Otro">Otro</option>
                                        </select>
                                    </div>
                                    <div class="col-md-5">
                                        <input type="file" class="form-control" name="archivo" accept=".pdf,.jpg,.jpeg,.png" required>
                                    </div>
                                    <div class="col-md-3">
                                        <button type="submit" class="btn btn-primary w-100"><i class="bi bi-upload me-1"></i>Subir</button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</main>

<%@ include file="../WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>