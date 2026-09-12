<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cop" uri="http://inmovain.com/tld/precio" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:choose><c:when test="${empty propiedad}">Nueva</c:when><c:otherwise>Editar</c:otherwise></c:choose> propiedad - Agente</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <div class="row justify-content-center">
        <div class="col-lg-9">
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body p-4">
                    <h4 class="fw-bold mb-4">
                        <i class="bi bi-building-add me-2 text-primary"></i>
                        <c:choose>
                            <c:when test="${empty propiedad}">Registrar nueva propiedad</c:when>
                            <c:otherwise>Editar propiedad #${propiedad.idPropiedad}</c:otherwise>
                        </c:choose>
                    </h4>

                    <form action="propiedades" method="post" onsubmit="return validarPropiedad()">
                        <input type="hidden" name="accion" value="${empty propiedad ? 'guardarNuevo' : 'guardarEdicion'}">
                        <input type="hidden" name="id" value="${propiedad.idPropiedad}">

                        <div class="row">
                            <div class="col-md-8 mb-3">
                                <label class="form-label">Título <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" name="titulo" required
                                       value="<c:out value='${propiedad.titulo}' />">
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Matrícula inmobiliaria <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" name="matricula" required
                                       value="<c:out value='${propiedad.matriculaInmobiliaria}' />"
                                       ${empty propiedad ? '' : 'readonly'}>
                                <div class="form-text">Identificador único.</div>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Descripción</label>
                            <textarea class="form-control" name="descripcion" rows="3"><c:out value="${propiedad.descripcion}" /></textarea>
                        </div>
                        <div class="row">
                            <div class="col-md-3 mb-3">
                                <label class="form-label">Precio <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" name="precio" min="1" step="0.01" required
                                       value="${cop:entero(propiedad.precio)}">
                            </div>
                            <div class="col-md-3 mb-3">
                                <label class="form-label">Ciudad <span class="text-danger">*</span></label>
                                <select class="form-select" name="idCiudad" required>
                                    <option value="">Seleccione...</option>
                                    <c:forEach var="c" items="${ciudades}">
                                        <option value="${c.idCiudad}" ${propiedad.idCiudad == c.idCiudad ? 'selected' : ''}>${c.nombre}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-3 mb-3">
                                <label class="form-label">Tipo <span class="text-danger">*</span></label>
                                <select class="form-select" name="idTipo" required>
                                    <option value="">Seleccione...</option>
                                    <c:forEach var="t" items="${tipos}">
                                        <option value="${t.idTipo}" ${propiedad.idTipo == t.idTipo ? 'selected' : ''}>${t.nombre}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-3 mb-3">
                                <label class="form-label">Inmobiliaria <span class="text-danger">*</span></label>
                                <select class="form-select" name="idInmobiliaria" required>
                                    <option value="">Seleccione...</option>
                                    <c:forEach var="i" items="${inmobiliarias}">
                                        <option value="${i.idInmobiliaria}" ${propiedad.idInmobiliaria == i.idInmobiliaria ? 'selected' : ''}>${i.nombre}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Estado</label>
                                <select class="form-select" name="estado">
                                    <option value="DISPONIBLE" ${propiedad.estado == 'DISPONIBLE' ? 'selected' : ''}>Disponible</option>
                                    <option value="RESERVADA" ${propiedad.estado == 'RESERVADA' ? 'selected' : ''}>Reservada</option>
                                    <option value="VENDIDA" ${propiedad.estado == 'VENDIDA' ? 'selected' : ''}>Vendida</option>
                                    <option value="ALQUILADA" ${propiedad.estado == 'ALQUILADA' ? 'selected' : ''}>Alquilada</option>
                                </select>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Tipo de operación</label>
                                <select class="form-select" name="tipoOperacion">
                                    <option value="VENTA" ${propiedad.tipoOperacion == 'VENTA' ? 'selected' : ''}>Venta</option>
                                    <option value="ALQUILER" ${propiedad.tipoOperacion == 'ALQUILER' ? 'selected' : ''}>Alquiler</option>
                                    <option value="AMBOS" ${propiedad.tipoOperacion == 'AMBOS' ? 'selected' : ''}>Ambos</option>
                                </select>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Dirección</label>
                                <input type="text" class="form-control" name="direccion"
                                       value="<c:out value='${propiedad.direccion}' />">
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-3 mb-3">
                                <label class="form-label">Área (m²)</label>
                                <input type="number" class="form-control" name="areaM2" min="0" step="0.01"
                                       value="${propiedad.areaM2}">
                            </div>
                            <div class="col-md-3 mb-3">
                                <label class="form-label">Habitaciones</label>
                                <input type="number" class="form-control" name="habitaciones" min="0"
                                       value="${propiedad.habitaciones}">
                            </div>
                            <div class="col-md-3 mb-3">
                                <label class="form-label">Baños</label>
                                <input type="number" class="form-control" name="banos" min="0"
                                       value="${propiedad.banos}">
                            </div>
                            <div class="col-md-3 mb-3">
                                <label class="form-label">Parqueaderos</label>
                                <input type="number" class="form-control" name="parqueaderos" min="0"
                                       value="${propiedad.parqueaderos}">
                            </div>
                        </div>
                        <div class="mb-4">
                            <label class="form-label">Características (relación N:M)</label>
                            <div class="d-flex flex-wrap gap-3 border rounded p-3">
                                <c:forEach var="car" items="${todasCaracteristicas}">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" name="caracteristicas"
                                               value="${car.idCaracteristica}" id="car${car.idCaracteristica}"
                                               ${propiedad.caracteristicas.contains(car) ? 'checked' : ''}>
                                        <label class="form-check-label" for="car${car.idCaracteristica}">${car.nombre}</label>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                        <c:if test="${not empty propiedad}">
                            <div class="form-check form-switch mb-3">
                                <input class="form-check-input" type="checkbox" name="activa" value="1" id="activa"
                                       ${propiedad.activa ? 'checked' : ''}>
                                <label class="form-check-label" for="activa">Propiedad activa (visible en catálogo)</label>
                            </div>
                        </c:if>
                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i>Guardar</button>
                            <a href="propiedades" class="btn btn-outline-secondary">Cancelar</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</main>

<%@ include file="../WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
function validarPropiedad() {
    var titulo = document.querySelector('input[name="titulo"]').value;
    var precio = document.querySelector('input[name="precio"]').value;
    if (titulo.trim() === '') {
        alert('El título es obligatorio.');
        return false;
    }
    if (!precio || parseFloat(precio) <= 0) {
        alert('El precio debe ser mayor que cero.');
        return false;
    }
    return true;
}
</script>
</body>
</html>