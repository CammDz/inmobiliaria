<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cop" uri="http://inmovain.com/tld/precio" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${propiedad.titulo}" /> - InmoVaIn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">

    <nav aria-label="breadcrumb" class="mb-3">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="../index.jsp">Inicio</a></li>
            <li class="breadcrumb-item"><a href="../catalogo">Catálogo</a></li>
            <li class="breadcrumb-item active"><c:out value="${propiedad.titulo}" /></li>
        </ol>
    </nav>

    <div class="row g-4">
        <!-- GALERÍA -->
        <div class="col-lg-8">
            <div class="card shadow-sm border-0">
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty propiedad.imagenes}">
                            <div id="galeriaPropiedad" class="carousel slide" data-bs-ride="carousel">
                                <div class="carousel-inner">
                                    <c:forEach var="img" items="${propiedad.imagenes}" varStatus="st">
                                        <div class="carousel-item ${st.first ? 'active' : ''}">
                                            <img src="${img.urlImagen}" class="d-block w-100" style="height: 420px; object-fit: cover;"
                                                 alt="Imagen ${st.index + 1} de ${propiedad.titulo}"
                                                 onerror="this.src='${pageContext.request.contextPath}/images/placeholder.jpg'">
                                        </div>
                                    </c:forEach>
                                </div>
                                <c:if test="${propiedad.imagenes.size() > 1}">
                                    <button class="carousel-control-prev" type="button" data-bs-target="#galeriaPropiedad" data-bs-slide="prev">
                                        <span class="carousel-control-prev-icon"></span>
                                    </button>
                                    <button class="carousel-control-next" type="button" data-bs-target="#galeriaPropiedad" data-bs-slide="next">
                                        <span class="carousel-control-next-icon"></span>
                                    </button>
                                </c:if>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <img src="../images/placeholder.jpg" class="d-block w-100" style="height: 420px; object-fit: cover;" alt="Sin imagen">
                        </c:otherwise>
                    </c:choose>

                    <h2 class="fw-bold mt-4 mb-2"><c:out value="${propiedad.titulo}" /></h2>
                    <p class="text-muted">
                        <i class="bi bi-geo-alt me-1"></i><c:out value="${propiedad.direccion}" />
                        <c:out value="${propiedad.nombreCiudad}" />, <c:out value="${propiedad.nombreDepartamento}" />
                    </p>

                    <h4 class="text-success fw-bold">${cop:colombiano(propiedad.precio)}</h4>
                    <span class="badge bg-info text-dark">${propiedad.estado}</span>
                    <span class="badge bg-secondary">${propiedad.tipoOperacion}</span>

                    <hr>
                    <h5><i class="bi bi-info-circle me-1 text-primary"></i>Descripción</h5>
                    <p class="text-muted"><c:out value="${propiedad.descripcion}" /></p>

                    <h5 class="mt-4"><i class="bi bi-list-check me-1 text-primary"></i>Características</h5>
                    <c:choose>
                        <c:when test="${not empty propiedad.caracteristicas}">
                            <div class="d-flex flex-wrap gap-2">
                                <c:forEach var="car" items="${propiedad.caracteristicas}">
                                    <span class="badge bg-light text-dark border"><i class="bi bi-check2-circle text-success me-1"></i>${car.nombre}</span>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted">No registra características.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- INFORMACIÓN LATERAL -->
        <div class="col-lg-4">
            <div class="card shadow-sm border-0 mb-3">
                <div class="card-body">
                    <h5 class="fw-bold mb-3"><i class="bi bi-info-square me-1 text-primary"></i>Información general</h5>
                    <ul class="list-unstyled small">
                        <li class="py-1"><strong>Tipo:</strong> <c:out value="${propiedad.nombreTipo}" /></li>
                        <li class="py-1"><strong>Estado:</strong> ${propiedad.estado}</li>
                        <li class="py-1"><strong>Operación:</strong> ${propiedad.tipoOperacion}</li>
                        <li class="py-1"><strong>Ciudad:</strong> <c:out value="${propiedad.nombreCiudad}" /></li>
                        <li class="py-1"><strong>Área:</strong> <c:out value="${propiedad.areaM2}" /> m²</li>
                        <li class="py-1"><strong>Habitaciones:</strong> <c:out value="${propiedad.habitaciones}" /></li>
                        <li class="py-1"><strong>Baños:</strong> <c:out value="${propiedad.banos}" /></li>
                        <li class="py-1"><strong>Parqueaderos:</strong> <c:out value="${propiedad.parqueaderos}" /></li>
                        <li class="py-1"><strong>Matrícula:</strong> <c:out value="${propiedad.matriculaInmobiliaria}" /></li>
                        <li class="py-1"><strong>Inmobiliaria:</strong> <c:out value="${propiedad.nombreInmobiliaria}" /></li>
                        <li class="py-1"><strong>Agente:</strong> <c:out value="${propiedad.nombreAgente}" /></li>
                    </ul>
                </div>
            </div>

            <!-- ACCIONES -->
            <div class="card shadow-sm border-0">
                <div class="card-body">
                    <c:choose>
                        <c:when test="${autenticado && esCliente}">
                            <h6 class="fw-bold mb-3">¿Te interesa esta propiedad?</h6>
                            <button class="btn btn-primary w-100 mb-2" data-bs-toggle="collapse" data-bs-target="#formCita">
                                <i class="bi bi-calendar-check me-1"></i>Solicitar cita de visita
                            </button>
                            <button class="btn btn-outline-success w-100 mb-2" data-bs-toggle="collapse" data-bs-target="#formSolicitud">
                                <i class="bi bi-file-earmark-text me-1"></i>Realizar solicitud
                            </button>
                            <c:choose>
                                <c:when test="${esFavorito}">
                                    <a href="../propiedad?id=${propiedad.idPropiedad}&accion=favorito&idPropiedad=${propiedad.idPropiedad}&modo=quitar"
                                       class="btn btn-warning w-100">
                                        <i class="bi bi-star-fill me-1"></i>Quitar de favoritos
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <a href="../propiedad?id=${propiedad.idPropiedad}&accion=favorito&idPropiedad=${propiedad.idPropiedad}&modo=agregar"
                                       class="btn btn-outline-warning w-100">
                                        <i class="bi bi-star me-1"></i>Agregar a favoritos
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:when test="${autenticado}">
                            <p class="text-muted small">Regístrate como cliente para solicitar citas, favoritos y solicitudes.</p>
                            <a href="../catalogo" class="btn btn-primary w-100">Ver más propiedades</a>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted small">Inicia sesión o regístrate para solicitar citas, guardar favoritos y enviar solicitudes.</p>
                            <a href="../login.jsp" class="btn btn-primary w-100 mb-2"><i class="bi bi-box-arrow-in-right me-1"></i>Iniciar sesión</a>
                            <a href="../registro.jsp" class="btn btn-outline-primary w-100">Registrarse</a>
                        </c:otherwise>
                    </c:choose>

                    <!-- Formulario CITA -->
                    <div class="collapse mt-3" id="formCita">
                        <form action="../cliente/citas" method="post">
                            <input type="hidden" name="accion" value="crear">
                            <input type="hidden" name="idPropiedad" value="${propiedad.idPropiedad}">
                            <div class="mb-2">
                                <label class="form-label small">Fecha</label>
                                <input type="date" class="form-control" name="fecha" required>
                            </div>
                            <div class="mb-2">
                                <label class="form-label small">Hora</label>
                                <input type="time" class="form-control" name="hora" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label small">Observaciones</label>
                                <textarea class="form-control" name="observaciones" rows="2"></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Enviar solicitud de cita</button>
                        </form>
                    </div>

                    <!-- Formulario SOLICITUD -->
                    <div class="collapse mt-3" id="formSolicitud">
                        <form action="../cliente/solicitudes" method="post">
                            <input type="hidden" name="accion" value="crear">
                            <input type="hidden" name="idPropiedad" value="${propiedad.idPropiedad}">
                            <div class="mb-2">
                                <label class="form-label small">Tipo de operación</label>
                                <select class="form-select" name="tipo" required>
                                    <option value="COMPRA">Compra</option>
                                    <option value="ALQUILER">Alquiler</option>
                                </select>
                            </div>
                            <div class="mb-2">
                                <label class="form-label small">Monto ofrecido</label>
                                <input type="number" class="form-control" name="montoOfrecido" min="0" step="0.01">
                            </div>
                            <div class="mb-3">
                                <label class="form-label small">Observaciones</label>
                                <textarea class="form-control" name="observaciones" rows="2"></textarea>
                            </div>
                            <button type="submit" class="btn btn-success w-100">Enviar solicitud</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<%@ include file="../WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>