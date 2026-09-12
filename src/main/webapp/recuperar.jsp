<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recuperar contraseña - InmoVaIn</title>
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
                        <i class="bi bi-key text-primary fs-1"></i>
                        <h3 class="fw-bold mb-0">Recuperar contraseña</h3>
                        <p class="text-muted">Te enviaremos un enlace para restablecerla</p>
                    </div>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <c:out value="${error}" />
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <c:if test="${not empty mensaje}">
                        <div class="alert alert-success" role="alert">
                            <c:out value="${mensaje}" />
                        </div>

                        <%-- Modo demostración: sin SMTP configurado, se muestra el enlace
                             para que la sustentación pueda completarse. En producción se
                             envía por correo y esta casilla no debe aparecer. --%>
                        <c:if test="${not empty enlaceDemo && not empty correoDestino}">
                            <div class="alert alert-info small">
                                <strong><i class="bi bi-info-circle me-1"></i>Modo demostración (correo no configurado).</strong>
                                En este entorno académico el enlace de restablecimiento para
                                <strong><c:out value="${correoDestino}" /></strong> es:
                                <div class="border rounded bg-white p-2 mt-2 overflow-auto">
                                    <a href="${enlaceDemo}"><c:out value="${enlaceDemo}" /></a>
                                </div>
                            </div>
                        </c:if>
                    </c:if>

                    <c:if test="${empty mensaje}">
                        <form action="recuperar" method="post" novalidate>
                            <div class="mb-3">
                                <label for="correo" class="form-label">Correo electrónico</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                                    <input type="email" class="form-control" id="correo" name="correo"
                                           placeholder="usuario@correo.com" required autofocus>
                                </div>
                            </div>
                            <button type="submit" class="btn btn-primary w-100"><i class="bi bi-send me-1"></i>Enviar enlace</button>
                        </form>
                    </c:if>

                    <hr class="my-4">
                    <p class="text-center text-muted small mb-0">
                        <a href="login.jsp"><i class="bi bi-arrow-left me-1"></i>Volver a iniciar sesión</a>
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