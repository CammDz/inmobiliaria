<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Galería de imágenes - Agente</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fw-bold mb-0"><i class="bi bi-images me-2 text-primary"></i>Galería de <c:out value="${propiedad.titulo}" /></h2>
        <a href="propiedades" class="btn btn-outline-secondary"><i class="bi bi-arrow-left me-1"></i>Volver</a>
    </div>

    <div class="row g-4">
        <div class="col-lg-4">
            <div class="card border-0 shadow-sm">
                <div class="card-body">
                    <h5 class="fw-bold mb-3"><i class="bi bi-plus-circle me-1"></i>Agregar imagen</h5>
                    <form action="propiedades" method="post">
                        <input type="hidden" name="accion" value="guardarImagenes">
                        <input type="hidden" name="idPropiedad" value="${propiedad.idPropiedad}">
                        <div class="mb-3">
                            <label class="form-label">URL de la imagen <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="urlImagen" required
                                   placeholder="https://ejemplo.com/imagen.jpg">
                            <div class="form-text">Puede usar una URL pública o una ruta de la carpeta images/.</div>
                        </div>
                        <div class="form-check mb-3">
                            <input class="form-check-input" type="checkbox" name="principal" value="1" id="principal">
                            <label class="form-check-label" for="principal">Establecer como imagen principal</label>
                        </div>
                        <button type="submit" class="btn btn-primary w-100"><i class="bi bi-plus-lg me-1"></i>Agregar</button>
                    </form>
                </div>
            </div>
        </div>
        <div class="col-lg-8">
            <div class="row g-3">
                <c:choose>
                    <c:when test="${empty imagenes}">
                        <div class="col-12">
                            <div class="text-center py-5 bg-white rounded border">
                                <i class="bi bi-image text-muted" style="font-size: 3rem;"></i>
                                <p class="text-muted mt-3">Esta propiedad aún no tiene imágenes.</p>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="img" items="${imagenes}">
                            <c:set var="imgUrl" value="${img.urlImagen}" />
                            <c:if test="${!imgUrl.startsWith('http') && !imgUrl.startsWith('/')}">
                                <c:set var="imgUrl" value="${pageContext.request.contextPath}/${imgUrl}" />
                            </c:if>
                            <div class="col-md-6">
                                <div class="card border-0 shadow-sm">
                                    <img src="${imgUrl}" class="card-img-top" style="height: 160px; object-fit: cover;"
                                         alt="Imagen" onerror="this.src='${pageContext.request.contextPath}/images/placeholder.jpg'">
                                    <div class="card-body py-2">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div>
                                                <c:if test="${img.esPrincipal}">
                                                    <span class="badge bg-success">Principal</span>
                                                </c:if>
                                                <span class="small text-muted">#${img.idImagen}</span>
                                            </div>
                                            <a href="propiedades?accion=eliminarImagen&idImagen=${img.idImagen}&idPropiedad=${propiedad.idPropiedad}"
                                               class="btn btn-sm btn-outline-danger"
                                               onclick="return confirm('¿Eliminar esta imagen?');">
                                                <i class="bi bi-trash"></i>
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</main>

<%@ include file="../WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>