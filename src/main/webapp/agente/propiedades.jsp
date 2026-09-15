<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cop" uri="http://inmovain.com/tld/precio" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis propiedades - Agente</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fw-bold mb-0"><i class="bi bi-buildings me-2 text-primary"></i>Mis propiedades</h2>
        <a href="propiedades?accion=crear" class="btn btn-primary"><i class="bi bi-plus-lg me-1"></i>Nueva propiedad</a>
    </div>

    <c:choose>
        <c:when test="${empty propiedades}">
            <div class="text-center py-5 bg-white rounded border">
                <i class="bi bi-house-x text-muted" style="font-size: 3rem;"></i>
                <p class="text-muted mt-3">Aún no tiene propiedades registradas.</p>
                <a href="propiedades?accion=crear" class="btn btn-primary">Crear la primera propiedad</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row g-4">
                <c:forEach var="p" items="${propiedades}">
                    <div class="col-md-6 col-lg-4">
                        <div class="card h-100 shadow-sm">
                            <c:set var="imgUrl" value="../images/placeholder.jpg" />
                            <c:if test="${not empty p.imagenes && p.imagenes.size() > 0}">
                                <c:set var="imgUrl" value="${p.imagenes[0].urlImagen}" />
                                <c:if test="${!imgUrl.startsWith('http') && !imgUrl.startsWith('/')}">
                                    <c:set var="imgUrl" value="${pageContext.request.contextPath}/${imgUrl}" />
                                </c:if>
                            </c:if>
                            <img src="${imgUrl}" class="card-img-top" style="height: 180px; object-fit: cover;"
                                 alt="${p.titulo}" onerror="this.src='${pageContext.request.contextPath}/images/placeholder.jpg'">
                            <div class="card-body">
                                <div class="d-flex justify-content-between mb-1">
                                    <span class="badge bg-primary">${p.nombreTipo}</span>
                                    <span class="text-success fw-bold">${cop:colombiano(p.precio)}</span>
                                </div>
                                <span class="badge bg-info text-dark mb-1">${p.estado}</span>
                                <c:if test="${!p.activa}"><span class="badge bg-danger mb-1">Desactivada</span></c:if>
                                <h6 class="card-title mb-1"><c:out value="${p.titulo}" /></h6>
                                <p class="text-muted small mb-2">
                                    <i class="bi bi-geo-alt me-1"></i><c:out value="${p.nombreCiudad}" />
                                </p>
                                <div class="d-flex gap-1 flex-wrap">
                                    <a href="propiedades?accion=editar&id=${p.idPropiedad}" class="btn btn-sm btn-outline-primary"><i class="bi bi-pencil"></i> Editar</a>
                                    <a href="propiedades?accion=imagenes&id=${p.idPropiedad}" class="btn btn-sm btn-outline-secondary"><i class="bi bi-images"></i> Imágenes</a>
                                    <c:choose>
                                        <c:when test="${p.activa}">
                                            <a href="propiedades?accion=desactivar&id=${p.idPropiedad}" class="btn btn-sm btn-outline-danger"
                                               onclick="return confirm('¿Desactivar esta propiedad? Dejará de mostrarse en el catálogo.');"><i class="bi bi-eye-slash"></i></a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="propiedades?accion=activar&id=${p.idPropiedad}" class="btn btn-sm btn-outline-success"><i class="bi bi-eye"></i></a>
                                        </c:otherwise>
                                    </c:choose>
                                    <a href="../propiedad?id=${p.idPropiedad}" target="_blank" class="btn btn-sm btn-outline-info" title="Ver como cliente"><i class="bi bi-box-arrow-up-right"></i></a>
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