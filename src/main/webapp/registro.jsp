<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrarse - InmoVaIn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="WEB-INF/jspf/navbar.jspf" %>
<%@ include file="WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
            <div class="card shadow border-0">
                <div class="card-body p-4">
                    <div class="text-center mb-4">
                        <i class="bi bi-person-plus text-primary fs-1"></i>
                        <h3 class="fw-bold mb-0">Crear cuenta</h3>
                        <p class="text-muted">Únete a InmoVaIn y encuentra la propiedad ideal</p>
                    </div>

                    <form action="registro" method="post" novalidate onsubmit="return validarRegistro()">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="nombre" class="form-label">Nombre <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="nombre" name="nombre" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="apellido" class="form-label">Apellido <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="apellido" name="apellido" required>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="correo" class="form-label">Correo electrónico <span class="text-danger">*</span></label>
                                <input type="email" class="form-control" id="correo" name="correo" required>
                                <div class="invalid-feedback" id="errorCorreo"></div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="telefono" class="form-label">Teléfono</label>
                                <input type="tel" class="form-control" id="telefono" name="telefono" placeholder="300 123 4567">
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="contrasena" class="form-label">Contraseña <span class="text-danger">*</span></label>
                                <input type="password" class="form-control" id="contrasena" name="contrasena" required>
                                <div class="form-text">Mínimo 4 caracteres.</div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="confirmarContrasena" class="form-label">Confirmar contraseña <span class="text-danger">*</span></label>
                                <input type="password" class="form-control" id="confirmarContrasena" name="confirmarContrasena" required>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary w-100 mt-2"><i class="bi bi-person-check me-1"></i>Registrarme</button>
                    </form>

                    <hr class="my-4">
                    <p class="text-center text-muted small mb-0">
                        ¿Ya tienes cuenta? <a href="login.jsp">Inicia sesión</a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</main>

<%@ include file="WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
function validarRegistro() {
    var correo = document.getElementById('correo').value;
    var contrasena = document.getElementById('contrasena').value;
    var confirmar = document.getElementById('confirmarContrasena').value;

    if (document.getElementById('nombre').value.trim() === '' ||
        document.getElementById('apellido').value.trim() === '') {
        alert('Debe diligenciar el nombre y el apellido.');
        return false;
    }
    if (!/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/.test(correo)) {
        alert('El formato del correo no es válido.');
        return false;
    }
    if (contrasena.length < 4) {
        alert('La contraseña debe tener al menos 4 caracteres.');
        return false;
    }
    if (contrasena !== confirmar) {
        alert('Las contraseñas no coinciden.');
        return false;
    }
    return true;
}
</script>
</body>
</html>