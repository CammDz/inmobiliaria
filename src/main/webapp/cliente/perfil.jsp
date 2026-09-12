<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi perfil - InmoVaIn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <h2 class="fw-bold mb-4"><i class="bi bi-person-gear me-2 text-primary"></i>Mi perfil</h2>

    <div class="row g-4">
        <div class="col-lg-7">
            <div class="card border-0 shadow-sm">
                <div class="card-body p-4">
                    <h5 class="fw-bold mb-3"><i class="bi bi-pencil-square me-2 text-primary"></i>Datos personales</h5>
                    <form action="perfil" method="post">
                        <input type="hidden" name="accion" value="guardarDatos">
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
                                <input type="tel" class="form-control" name="telefono"
                                       value="<c:out value='${usuario.telefono}' />">
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Documento de identidad</label>
                                <input type="text" class="form-control" name="documentoIdentidad"
                                       value="<c:out value='${perfil.documentoIdentidad}' />">
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Fecha de nacimiento</label>
                                <input type="date" class="form-control" name="fechaNacimiento"
                                       value="<c:out value='${perfil.fechaNacimiento}' />">
                            </div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Dirección</label>
                            <input type="text" class="form-control" name="direccion"
                                   value="<c:out value='${perfil.direccion}' />">
                        </div>
                        <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i>Guardar cambios</button>
                    </form>
                </div>
            </div>
        </div>

        <div class="col-lg-5">
            <div class="card border-0 shadow-sm">
                <div class="card-body p-4">
                    <h5 class="fw-bold mb-3"><i class="bi bi-shield-lock me-2 text-primary"></i>Cambiar contraseña</h5>
                    <form action="perfil" method="post">
                        <input type="hidden" name="accion" value="cambiarContrasena">
                        <div class="mb-3">
                            <label class="form-label">Contraseña actual <span class="text-danger">*</span></label>
                            <input type="password" class="form-control" name="contrasenaActual" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Nueva contraseña <span class="text-danger">*</span></label>
                            <input type="password" class="form-control" name="nuevaContrasena" required minlength="4">
                            <div class="form-text">Mínimo 4 caracteres.</div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Confirmar nueva contraseña <span class="text-danger">*</span></label>
                            <input type="password" class="form-control" name="confirmarContrasena" required minlength="4">
                        </div>
                        <button type="submit" class="btn btn-outline-primary"><i class="bi bi-key me-1"></i>Actualizar contraseña</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</main>

<%@ include file="../WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>