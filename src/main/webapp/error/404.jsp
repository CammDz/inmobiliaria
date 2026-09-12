<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="false" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Página no encontrada - InmoVaIn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5 text-center">
            <div class="card shadow border-0">
                <div class="card-body p-5">
                    <i class="bi bi-search text-warning display-1"></i>
                    <h1 class="fw-bold mt-3">404</h1>
                    <p class="text-muted">La página que buscas no existe o fue movida.</p>
                    <a href="<%= request.getContextPath() %>/index.jsp" class="btn btn-primary">
                        <i class="bi bi-house me-1"></i>Volver al inicio</a>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>