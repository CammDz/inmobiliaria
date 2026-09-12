<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    int idAgenteD = (Integer) session.getAttribute("usuario_id");
    com.inmobiliaria.dao.PropiedadDAO pDAOx = new com.inmobiliaria.dao.PropiedadDAO();
    com.inmobiliaria.dao.CitaDAO cDAOx = new com.inmobiliaria.dao.CitaDAO();
    com.inmobiliaria.dao.SolicitudDAO sDAOx = new com.inmobiliaria.dao.SolicitudDAO();
    com.inmobiliaria.dao.DocumentoSolicitudDAO dDAOx = new com.inmobiliaria.dao.DocumentoSolicitudDAO();
    pageContext.setAttribute("misPropiedades", pDAOx.listarPorAgente(idAgenteD).size());
    pageContext.setAttribute("misCitas", cDAOx.listarPorAgente(idAgenteD).size());
    pageContext.setAttribute("misSolicitudes", sDAOx.listarPorAgente(idAgenteD).size());
    pageContext.setAttribute("misDocumentos", dDAOx.listarPorAgente(idAgenteD).size());
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel del agente - InmoVaIn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <h2 class="fw-bold mb-4"><i class="bi bi-briefcase me-2 text-primary"></i>Panel del agente inmobiliario</h2>

    <div class="row g-3 mb-4">
        <div class="col-sm-6 col-lg-3">
            <div class="card text-bg-primary border-0 shadow-sm">
                <div class="card-body">
                    <i class="bi bi-buildings fs-1"></i>
                    <h3 class="fw-bold mb-0">${misPropiedades}</h3>
                    <span>Mis propiedades</span>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-lg-3">
            <div class="card text-bg-success border-0 shadow-sm">
                <div class="card-body">
                    <i class="bi bi-calendar-check fs-1"></i>
                    <h3 class="fw-bold mb-0">${misCitas}</h3>
                    <span>Citas</span>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-lg-3">
            <div class="card text-bg-warning border-0 shadow-sm">
                <div class="card-body">
                    <i class="bi bi-file-earmark-text fs-1"></i>
                    <h3 class="fw-bold mb-0">${misSolicitudes}</h3>
                    <span>Solicitudes</span>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-lg-3">
            <div class="card text-bg-info border-0 shadow-sm">
                <div class="card-body">
                    <i class="bi bi-paperclip fs-1"></i>
                    <h3 class="fw-bold mb-0">${misDocumentos}</h3>
                    <span>Documentos</span>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-3">
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <h5 class="fw-bold"><i class="bi bi-buildings me-2 text-primary"></i>Mis propiedades</h5>
                    <p class="text-muted small">Crea, edita y administra tus propiedades, imágenes y características.</p>
                    <a href="propiedades" class="btn btn-sm btn-primary">Gestionar</a>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <h5 class="fw-bold"><i class="bi bi-calendar-check me-2 text-primary"></i>Citas</h5>
                    <p class="text-muted small">Aproba, rechaza o cancela las citas de visita de tus propiedades.</p>
                    <a href="citas" class="btn btn-sm btn-primary">Gestionar</a>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <h5 class="fw-bold"><i class="bi bi-file-earmark-text me-2 text-primary"></i>Solicitudes</h5>
                    <p class="text-muted small">Revisa, aprueba o rechaza las solicitudes de compra y alquiler.</p>
                    <a href="solicitudes" class="btn btn-sm btn-primary">Gestionar</a>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <h5 class="fw-bold"><i class="bi bi-paperclip me-2 text-primary"></i>Documentos</h5>
                    <p class="text-muted small">Revisa los documentos subidos por los clientes en sus solicitudes.</p>
                    <a href="documentos" class="btn btn-sm btn-primary">Ver documentos</a>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <h5 class="fw-bold"><i class="bi bi-bar-chart me-2 text-primary"></i>Reportes</h5>
                    <p class="text-muted small">Consulta los reportes de ventas, alquileres y citas generados por SQL.</p>
                    <a href="reportes" class="btn btn-sm btn-outline-primary">Ver reportes</a>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <h5 class="fw-bold"><i class="bi bi-camera me-2 text-primary"></i>Galería e imágenes</h5>
                    <p class="text-muted small">Administra la galería de imágenes de cada una de tus propiedades.</p>
                    <a href="propiedades" class="btn btn-sm btn-outline-primary">Ir a propiedades</a>
                </div>
            </div>
        </div>
    </div>
</main>

<%@ include file="../WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>