<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar sesión - InmoVaIn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="WEB-INF/jspf/navbar.jspf" %>
<%@ include file="WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <div class="card shadow border-0">
                <div class="card-body p-4">
                    <div class="text-center mb-4">
                        <i class="bi bi-shield-lock text-primary fs-1"></i>
                        <h3 class="fw-bold mb-0">Iniciar sesión</h3>
                        <p class="text-muted">Accede a tu cuenta de cliente o agente</p>
                    </div>

                    <form action="login" method="post" novalidate>
                        <div class="mb-3">
                            <label for="correo" class="form-label">Correo electrónico</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                                <input type="email" class="form-control" id="correo" name="correo"
                                       placeholder="usuario@correo.com" required>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label for="contrasena" class="form-label">Contraseña</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-key"></i></span>
                                <input type="password" class="form-control" id="contrasena" name="contrasena"
                                       placeholder="Tu contraseña" required>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary w-100 mt-2"><i class="bi bi-box-arrow-in-right me-1"></i>Entrar</button>
                    </form>

                    <div class="text-center mt-3">
                        <a href="recuperar" class="small text-decoration-none"><i class="bi bi-key me-1"></i>¿Olvidaste tu contraseña?</a>
                    </div>

                    <hr class="my-4">
                    <p class="text-center text-muted small mb-0">
                        ¿No tienes cuenta? <a href="registro.jsp">Regístrate aquí</a>
                    </p>
                    <p class="text-center text-muted small mt-2">
                        <a href="index.jsp"><i class="bi bi-arrow-left me-1"></i>Volver al inicio</a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</main>

<%@ include file="WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>