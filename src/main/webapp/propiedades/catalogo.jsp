<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cop" uri="http://inmovain.com/tld/precio" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Catálogo de propiedades - InmoVaIn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <h2 class="fw-bold mb-4"><i class="bi bi-search me-2 text-primary"></i>Catálogo de propiedades</h2>

    <!-- FILTROS -->
    <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/catalogo" method="get" class="row g-3">
                <div class="col-md-4">
                    <label class="form-label small text-muted">Buscar</label>
                    <input type="text" class="form-control" name="busqueda" value="${fn:escapeXml(busqueda)}"
                           placeholder="Título o descripción">
                </div>
                <div class="col-md-2">
                    <label class="form-label small text-muted">Ciudad</label>
                    <select class="form-select" name="ciudad">
                        <option value="">Todas</option>
                        <c:forEach var="c" items="${ciudades}">
                            <option value="${c.idCiudad}" ${ciudadSeleccionada == c.idCiudad ? 'selected' : ''}>${c.nombre}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label small text-muted">Tipo</label>
                    <select class="form-select" name="tipo">
                        <option value="">Todos</option>
                        <c:forEach var="t" items="${tipos}">
                            <option value="${t.idTipo}" ${tipoSeleccionado == t.idTipo ? 'selected' : ''}>${t.nombre}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label small text-muted">Precio mínimo</label>
                    <input type="number" class="form-control" name="precioMin" value="${fn:escapeXml(precioMin)}" min="0">
                </div>
                <div class="col-md-2">
                    <label class="form-label small text-muted">Precio máximo</label>
                    <input type="number" class="form-control" name="precioMax" value="${fn:escapeXml(precioMax)}" min="0">
                </div>

                <div class="col-12">
                    <label class="form-label small text-muted">Características</label>
                    <div class="d-flex flex-wrap gap-3">
                        <c:forEach var="car" items="${caracteristicas}">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="caracteristicas"
                                       value="${car.idCaracteristica}" id="car${car.idCaracteristica}"
                                       ${caracteristicasSeleccionadas.contains(car.idCaracteristica) ? 'checked' : ''}>
                                <label class="form-check-label small" for="car${car.idCaracteristica}">${car.nombre}</label>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <div class="col-12">
                    <button type="submit" class="btn btn-primary"><i class="bi bi-funnel me-1"></i>Aplicar filtros</button>
                    <a href="${pageContext.request.contextPath}/catalogo" class="btn btn-outline-secondary">Limpiar</a>
                </div>
            </form>
        </div>
    </div>

    <!-- RESULTADOS -->
    <c:choose>
        <c:when test="${empty propiedades}">
            <div class="text-center py-5">
                <i class="bi bi-search text-muted" style="font-size: 3rem;"></i>
                <p class="text-muted mt-3">No se encontraron propiedades con los criterios seleccionados.</p>
                <a href="${pageContext.request.contextPath}/catalogo" class="btn btn-outline-primary">Ver todo el catálogo</a>
            </div>
        </c:when>
        <c:otherwise>
            <p class="text-muted">Se encontraron <strong>${propiedades.size()}</strong> propiedad(es).</p>
            <div class="row g-4">
                <c:forEach var="p" items="${propiedades}">
                    <div class="col-md-6 col-lg-4">
                        <div class="card h-100 shadow-sm">
                            <c:set var="imgUrl" value="images/placeholder.jpg" />
                            <c:if test="${not empty p.imagenes && p.imagenes.size() > 0}">
                                <c:set var="imgUrl" value="${p.imagenes[0].urlImagen}" />
                            </c:if>
                            <img src="${imgUrl}" class="card-img-top" style="height: 200px; object-fit: cover;"
                                 alt="${p.titulo}" onerror="this.src='${pageContext.request.contextPath}/images/placeholder.jpg'">
                            <div class="card-body">
                                <div class="d-flex justify-content-between mb-1">
                                    <span class="badge bg-primary">${p.nombreTipo}</span>
                                    <span class="text-success fw-bold">${cop:colombiano(p.precio)}</span>
                                </div>
                                <span class="badge bg-info text-dark mb-2">${p.estado}</span>
                                <h5 class="card-title mb-1"><c:out value="${p.titulo}" /></h5>
                                <p class="text-muted small mb-2">
                                    <i class="bi bi-geo-alt me-1"></i><c:out value="${p.nombreCiudad}" />,
                                    <c:out value="${p.nombreDepartamento}" />
                                </p>
                                <p class="small mb-2">
                                    <c:if test="${p.habitaciones > 0}"><span class="me-2"><i class="bi bi-door-closed"></i> ${p.habitaciones}</span></c:if>
                                    <c:if test="${p.banos > 0}"><span class="me-2"><i class="bi bi-droplet-fill"></i> ${p.banos}</span></c:if>
                                    <c:if test="${p.areaM2 > 0}"><span><i class="bi bi-arrows-angle-expand"></i> ${p.areaM2} m²</span></c:if>
                                </p>
                                <a href="${pageContext.request.contextPath}/propiedad?id=${p.idPropiedad}" class="btn btn-sm btn-outline-primary w-100">Ver detalle</a>
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