<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cop" uri="http://inmovain.com/tld/precio" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>InmoVaIn Soluciones - Inmobiliaria</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
</head>
<body class="landing-page">

<%@ include file="WEB-INF/jspf/navbar.jspf" %>
<%@ include file="WEB-INF/jspf/mensajes.jspf" %>

<!-- HERO -->
<section class="hero py-5">
    <div class="container py-5">
        <div class="row align-items-center">
            <div class="col-lg-6">
                <h1 class="display-4 fw-bold text-white">Tu próximo hogar empieza aquí</h1>
                <p class="lead text-white-50">En InmoVaIn Soluciones te ayudamos a comprar, vender o alquilar propiedades con seguridad y transparencia.</p>
                <form class="d-flex gap-2 mt-4" action="catalogo" method="get">
                    <input type="text" class="form-control form-control-lg" name="busqueda"
                           placeholder="Buscar por ciudad, barrio o tipo...">
                    <button type="submit" class="btn btn-warning btn-lg text-dark"><i class="bi bi-search me-1"></i>Buscar</button>
                </form>
            </div>
        </div>
    </div>
</section>

<!-- CARACTERÍSTICAS DE SERVICIO -->
<section class="py-4 bg-light">
    <div class="container">
        <div class="row text-center g-4">
            <div class="col-md-4">
                <i class="bi bi-building text-primary fs-1"></i>
                <h5>Amplio catálogo</h5>
                <p class="text-muted">Propiedades certificadas en toda la ciudad con precios justos.</p>
            </div>
            <div class="col-md-4">
                <i class="bi bi-shield-check text-primary fs-1"></i>
                <h5>Proceso seguro</h5>
                <p class="text-muted">Documentación revisada y soporte en cada paso de la negociación.</p>
            </div>
            <div class="col-md-4">
                <i class="bi bi-person-check text-primary fs-1"></i>
                <h5>Asesoría personalizada</h5>
                <p class="text-muted">Agentes expertos que te acompañan hasta cerrar tu negocio.</p>
            </div>
        </div>
    </div>
</section>

<!-- PROPIEDADES DESTACADAS -->
<section class="py-5">
    <div class="container">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="fw-bold mb-0">Propiedades destacadas</h2>
            <a href="catalogo" class="btn btn-outline-primary">Ver todas <i class="bi bi-arrow-right ms-1"></i></a>
        </div>
        <div class="row g-4">
            <%
                com.inmobiliaria.dao.PropiedadDAO propiedadDAO = new com.inmobiliaria.dao.PropiedadDAO();
                com.inmobiliaria.dao.ImagenPropiedadDAO imagenDAO = new com.inmobiliaria.dao.ImagenPropiedadDAO();
                java.util.List<com.inmobiliaria.model.Propiedad> destacadas = propiedadDAO.listarDestacadas();
                for (com.inmobiliaria.model.Propiedad p : destacadas) {
                    p.setImagenes(imagenDAO.listarPorPropiedad(p.getIdPropiedad()));
                }
                pageContext.setAttribute("destacadas", destacadas);
            %>
            <c:forEach var="p" items="${destacadas}">
                <div class="col-md-6 col-lg-4">
                    <div class="card h-100 shadow-sm">
                        <c:set var="imgUrl" value="images/placeholder.jpg" />
                        <c:forEach var="img" items="${p.imagenes}">
                            <c:if test="${img.esPrincipal}">
                                <c:set var="imgUrl" value="${img.urlImagen}" />
                            </c:if>
                        </c:forEach>
                        <c:if test="${empty p.imagenes}">
                            <c:set var="imgUrl" value="images/placeholder.jpg" />
                        </c:if>
                        <c:if test="${not empty p.imagenes && empty imgUrl}">
                            <c:set var="imgUrl" value="${p.imagenes[0].urlImagen}" />
                        </c:if>
                        <img src="${imgUrl}" class="card-img-top" style="height: 200px; object-fit: cover;"
                             alt="${p.titulo}" onerror="this.src='${pageContext.request.contextPath}/images/placeholder.jpg'">
                        <div class="card-body">
                            <div class="d-flex justify-content-between mb-1">
                                <span class="badge bg-primary">${p.nombreTipo}</span>
                                <span class="text-success fw-bold">${cop:colombiano(p.precio)}</span>
                            </div>
                            <h5 class="card-title mb-1"><c:out value="${p.titulo}" /></h5>
                            <p class="text-muted small mb-2">
                                <i class="bi bi-geo-alt me-1"></i><c:out value="${p.nombreCiudad}" />
                                <c:if test="${p.habitaciones > 0}"> <i class="bi bi-door-closed ms-2"></i> ${p.habitaciones} hab</c:if>
                                <c:if test="${p.banos > 0}"> <i class="bi bi-droplet-fill ms-2"></i> ${p.banos} baños</c:if>
                            </p>
                            <a href="propiedad?id=${p.idPropiedad}" class="btn btn-sm btn-outline-primary w-100">Ver detalle</a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</section>

<!-- CTA -->
<section class="py-5 bg-primary text-white">
    <div class="container text-center">
        <h2 class="fw-bold">¿Listo para tu próxima propiedad?</h2>
        <p class="mb-4">Crea tu cuenta y comienza a gestionar favoritos, citas y solicitudes.</p>
        <a href="registro.jsp" class="btn btn-warning btn-lg text-dark me-2"><i class="bi bi-person-plus me-1"></i>Regístrate gratis</a>
        <a href="login.jsp" class="btn btn-outline-light btn-lg">Ya tengo cuenta</a>
    </div>
</section>

<%@ include file="WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>