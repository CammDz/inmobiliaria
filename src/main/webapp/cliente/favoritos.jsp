<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cop" uri="http://inmovain.com/tld/precio" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis favoritos - InmoVaIn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <h2 class="fw-bold mb-4"><i class="bi bi-heart me-2 text-primary"></i>Propiedades favoritas</h2>

    <c:choose>
        <c:when test="${empty favoritos}">
            <div class="text-center py-5 bg-white rounded border">
                <i class="bi bi-heart text-muted" style="font-size: 3rem;"></i>
                <p class="text-muted mt-3">Aún no tienes propiedades favoritas.</p>
                <a href="../catalogo" class="btn btn-primary">Explorar catálogo</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row g-4">
                <c:forEach var="f" items="${favoritos}">
                    <div class="col-md-6 col-lg-4">
                        <div class="card h-100 shadow-sm">
                            <img src="${empty f.urlImagen ? '../images/placeholder.jpg' : f.urlImagen}"
                                 class="card-img-top" style="height: 180px; object-fit: cover;"
                                 alt="${f.tituloPropiedad}" onerror="this.src='${pageContext.request.contextPath}/images/placeholder.jpg'">
                            <div class="card-body">
                                <div class="d-flex justify-content-between mb-1">
                                    <span class="badge bg-primary">${f.nombreTipo}</span>
                                    <span class="text-success fw-bold">${cop:colombiano(f.precioPropiedad)}</span>
                                </div>
                                <h6 class="card-title mb-1"><c:out value="${f.tituloPropiedad}" /></h6>
                                <p class="text-muted small mb-2">
                                    <i class="bi bi-geo-alt me-1"></i><c:out value="${f.nombreCiudad}" />
                                </p>
                                <div class="d-flex gap-2">
                                    <a href="../propiedad?id=${f.idPropiedad}" class="btn btn-sm btn-primary">Ver detalle</a>
                                    <a href="favoritos?accion=quitar&id=${f.idPropiedad}" class="btn btn-sm btn-outline-danger"
                                       onclick="return confirm('¿Quitar de favoritos?');"><i class="bi bi-heartbreak"></i></a>
                                </div>
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