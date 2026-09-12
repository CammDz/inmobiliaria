<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:choose><c:when test="${empty usuario}">Nuevo</c:when><c:otherwise>Editar</c:otherwise></c:choose> usuario - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <div class="row justify-content-center">
        <div class="col-lg-7">
            <div class="card border-0 shadow-sm">
                <div class="card-body p-4">
                    <h4 class="fw-bold mb-4">
                        <i class="bi bi-person-lines-fill me-2 text-primary"></i>
                        <c:choose>
                            <c:when test="${empty usuario}">Nuevo usuario</c:when>
                            <c:otherwise>Editar usuario #${usuario.idUsuario}</c:otherwise>
                        </c:choose>
                    </h4>

                    <form action="usuarios" method="post" novalidate onsubmit="return validarForm()">
                        <input type="hidden" name="id"
                               value="<c:out value='${usuario.idUsuario}' />">
                        <input type="hidden" name="accion"
                               value="${empty usuario ? 'guardarNuevo' : 'guardarEdicion'}">

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Nombre <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" name="nombre" required
                                       value="<c:out value='${usuario.nombre}' />">
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Apellido <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" name="apellido" required
                                       value="<c:out value='${usuario.apellido}' />">
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Correo electrónico <span class="text-danger">*</span></label>
                                <input type="email" class="form-control" name="correo" required
                                       value="<c:out value='${usuario.correo}' />">
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Teléfono</label>
                                <input type="text" class="form-control" name="telefono"
                                       value="<c:out value='${usuario.telefono}' />">
                            </div>
                        </div>
                        <c:if test="${not empty usuario}">
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Documento de identidad</label>
                                    <input type="text" class="form-control" name="documentoIdentidad"
                                           value="<c:out value='${perfil.documentoIdentidad}' />">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Dirección</label>
                                    <input type="text" class="form-control" name="direccion"
                                           value="<c:out value='${perfil.direccion}' />">
                                </div>
                            </div>
                        </c:if>
                        <div class="mb-3">
                            <label class="form-label">
                                Contraseña ${empty usuario ? '<span class="text-danger">*</span>' : ''}
                                <c:if test="${not empty usuario}"><span class="text-muted small">(dejar vacía para no cambiar)</span></c:if>
                            </label>
                            <input type="password" class="form-control" name="contrasena"
                                   ${empty usuario ? 'required' : ''} placeholder="Mínimo 4 caracteres">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Roles <span class="text-danger">*</span></label>
                            <div class="d-flex flex-wrap gap-3">
                                <c:forEach var="r" items="${roles}">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" name="roles"
                                               value="${r.idRol}" id="rol${r.idRol}"
                                               ${rolesUsuario.contains(r.idRol) ? 'checked' : ''}>
                                        <label class="form-check-label" for="rol${r.idRol}">${r.nombre}</label>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                        <c:if test="${not empty usuario}">
                            <div class="form-check form-switch mb-3">
                                <input class="form-check-input" type="checkbox" name="activo" value="1" id="activo"
                                       ${usuario.activo ? 'checked' : ''}>
                                <label class="form-check-label" for="activo">Usuario activo</label>
                            </div>
                        </c:if>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i>Guardar</button>
                            <a href="usuarios" class="btn btn-outline-secondary">Cancelar</a>
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
function validarForm() {
    var contrasena = document.querySelector('input[name="contrasena"]').value;
    var hayRol = document.querySelectorAll('input[name="roles"]:checked').length > 0;
    if (!hayRol) {
        alert('Debe asignar al menos un rol.');
        return false;
    }
    if (contrasena.length > 0 && contrasena.length < 4) {
        alert('La contraseña debe tener al menos 4 caracteres.');
        return false;
    }
    return true;
}
</script>
</body>
</html>