<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    int idClienteD = (Integer) session.getAttribute("usuario_id");
    com.inmobiliaria.dao.FavoritoDAO fDAOx = new com.inmobiliaria.dao.FavoritoDAO();
    com.inmobiliaria.dao.CitaDAO cDAOx = new com.inmobiliaria.dao.CitaDAO();
    com.inmobiliaria.dao.SolicitudDAO sDAOx = new com.inmobiliaria.dao.SolicitudDAO();
    pageContext.setAttribute("numFavoritos", fDAOx.listarPorCliente(idClienteD).size());
    pageContext.setAttribute("numCitas", cDAOx.listarPorCliente(idClienteD).size());
    pageContext.setAttribute("numSolicitudes", sDAOx.listarPorCliente(idClienteD).size());
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi panel - InmoVaIn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <h2 class="fw-bold mb-4"><i class="bi bi-person-circle me-2 text-primary"></i>Bienvenido, ${sessionScope.usuario_nombre}</h2>

    <div class="row g-3 mb-4">
        <div class="col-sm-6 col-lg-3">
            <div class="card text-bg-primary border-0 shadow-sm">
                <div class="card-body">
                    <i class="bi bi-heart fs-1"></i>
                    <h3 class="fw-bold mb-0">${numFavoritos}</h3>
                    <span>Favoritos</span>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-lg-3">
            <div class="card text-bg-success border-0 shadow-sm">
                <div class="card-body">
                    <i class="bi bi-calendar-check fs-1"></i>
                    <h3 class="fw-bold mb-0">${numCitas}</h3>
                    <span>Citas agendadas</span>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-lg-3">
            <div class="card text-bg-warning border-0 shadow-sm">
                <div class="card-body">
                    <i class="bi bi-file-earmark-text fs-1"></i>
                    <h3 class="fw-bold mb-0">${numSolicitudes}</h3>
                    <span>Solicitudes</span>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-lg-3">
            <div class="card text-bg-info border-0 shadow-sm">
                <div class="card-body">
                    <i class="bi bi-shop fs-1"></i>
                    <h3 class="fw-bold mb-0">1</h3>
                    <span>Perfil activo</span>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-3">
        <div class="col-md-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <h5 class="fw-bold"><i class="bi bi-search me-2 text-primary"></i>Explorar catálogo</h5>
                    <p class="text-muted small">Busca propiedades en venta o alquiler con filtros por ciudad, tipo y precio.</p>
                    <a href="../catalogo" class="btn btn-sm btn-primary"><i class="bi bi-house me-1"></i>Ir al catálogo</a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <h5 class="fw-bold"><i class="bi bi-heart me-2 text-primary"></i>Mis favoritos</h5>
                    <p class="text-muted small">Consulta y gestiona las propiedades que guardaste como favoritas.</p>
                    <a href="favoritos" class="btn btn-sm btn-primary"><i class="bi bi-bookmark me-1"></i>Ver favoritos</a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <h5 class="fw-bold"><i class="bi bi-calendar-check me-2 text-primary"></i>Mis citas</h5>
                    <p class="text-muted small">Agenda y consulta tus citas de visita a las propiedades.</p>
                    <a href="citas" class="btn btn-sm btn-primary"><i class="bi bi-calendar me-1"></i>Ver citas</a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <h5 class="fw-bold"><i class="bi bi-file-earmark-text me-2 text-primary"></i>Solicitudes</h5>
                    <p class="text-muted small">Envía solicitudes de compra/alquiler y adjunta documentos.</p>
                    <a href="solicitudes" class="btn btn-sm btn-primary"><i class="bi bi-send me-1"></i>Ver solicitudes</a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <h5 class="fw-bold"><i class="bi bi-person me-2 text-primary"></i>Mi perfil</h5>
                    <p class="text-muted small">Actualiza tus datos personales y cambia tu contraseña.</p>
                    <a href="perfil" class="btn btn-sm btn-primary"><i class="bi bi-gear me-1"></i>Editar perfil</a>
                </div>
            </div>
        </div>
    </div>
</main>

<%@ include file="../WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>