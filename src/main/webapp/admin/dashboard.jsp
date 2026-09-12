<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    com.inmobiliaria.dao.UsuarioDAO uDAO = new com.inmobiliaria.dao.UsuarioDAO();
    com.inmobiliaria.dao.PropiedadDAO pDAO = new com.inmobiliaria.dao.PropiedadDAO();
    com.inmobiliaria.dao.CitaDAO cDAO = new com.inmobiliaria.dao.CitaDAO();
    com.inmobiliaria.dao.SolicitudDAO sDAO = new com.inmobiliaria.dao.SolicitudDAO();
    pageContext.setAttribute("totalUsuarios", uDAO.listarTodos().size());
    pageContext.setAttribute("totalPropiedades", pDAO.listarTodas().size());
    pageContext.setAttribute("totalCitas", cDAO.listarTodas().size());
    pageContext.setAttribute("totalSolicitudes", sDAO.listarTodas().size());
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel administrador - InmoVaIn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <h2 class="fw-bold mb-4"><i class="bi bi-speedometer2 me-2 text-primary"></i>Panel de administración</h2>

    <div class="row g-3 mb-4">
        <div class="col-sm-6 col-lg-3">
            <div class="card text-bg-primary border-0 shadow-sm">
                <div class="card-body">
                    <i class="bi bi-people fs-1"></i>
                    <h3 class="fw-bold mb-0">${totalUsuarios}</h3>
                    <span>Usuarios registrados</span>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-lg-3">
            <div class="card text-bg-success border-0 shadow-sm">
                <div class="card-body">
                    <i class="bi bi-buildings fs-1"></i>
                    <h3 class="fw-bold mb-0">${totalPropiedades}</h3>
                    <span>Propiedades</span>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-lg-3">
            <div class="card text-bg-warning border-0 shadow-sm">
                <div class="card-body">
                    <i class="bi bi-calendar-check fs-1"></i>
                    <h3 class="fw-bold mb-0">${totalCitas}</h3>
                    <span>Citas</span>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-lg-3">
            <div class="card text-bg-info border-0 shadow-sm">
                <div class="card-body">
                    <i class="bi bi-file-earmark-text fs-1"></i>
                    <h3 class="fw-bold mb-0">${totalSolicitudes}</h3>
                    <span>Solicitudes</span>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-3">
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <h5 class="fw-bold"><i class="bi bi-people me-2 text-primary"></i>Usuarios y roles</h5>
                    <p class="text-muted small">Crear, modificar, activar y desactivar usuarios. Asignar roles.</p>
                    <a href="usuarios" class="btn btn-sm btn-primary">Gestionar</a>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <h5 class="fw-bold"><i class="bi bi-geo-alt me-2 text-primary"></i>Catálogos</h5>
                    <p class="text-muted small">Administrar ciudades, tipos de propiedad, características e inmobiliarias.</p>
                    <a href="ciudades" class="btn btn-sm btn-outline-primary me-1">Ciudades</a>
                    <a href="tipos" class="btn btn-sm btn-outline-primary me-1">Tipos</a>
                    <a href="caracteristicas" class="btn btn-sm btn-outline-primary me-1">Características</a>
                    <a href="inmobiliarias" class="btn btn-sm btn-outline-primary mt-1">Inmobiliarias</a>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <h5 class="fw-bold"><i class="bi bi-clock-history me-2 text-primary"></i>Auditoría</h5>
                    <p class="text-muted small">Consulta el registro de todas las acciones importantes del sistema.</p>
                    <a href="auditoria" class="btn btn-sm btn-primary">Ver auditoría</a>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <h5 class="fw-bold"><i class="bi bi-bar-chart me-2 text-primary"></i>Reportes SQL</h5>
                    <p class="text-muted small">Reportes generados con consultas SQL (JOIN, LEFT JOIN, GROUP BY, HAVING).</p>
                    <a href="reportes" class="btn btn-sm btn-primary">Ver reportes</a>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <h5 class="fw-bold"><i class="bi bi-buildings me-2 text-primary"></i>Propiedades</h5>
                    <p class="text-muted small">Consulta el catálogo completo de propiedades registradas.</p>
                    <a href="../catalogo" class="btn btn-sm btn-outline-primary">Ver catálogo</a>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <h5 class="fw-bold"><i class="bi bi-calendar-check me-2 text-primary"></i>Citas y solicitudes</h5>
                    <p class="text-muted small">Consulta informes globales de citas y solicitudes del sistema.</p>
                    <a href="reportes" class="btn btn-sm btn-outline-primary">Ver informes</a>
                </div>
            </div>
        </div>
    </div>
</main>

<%@ include file="../WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>