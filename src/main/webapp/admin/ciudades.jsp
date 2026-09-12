<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ciudades - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <h2 class="fw-bold mb-4"><i class="bi bi-geo-alt me-2 text-primary"></i>Administrar ciudades</h2>

    <div class="row g-4">
        <div class="col-lg-4">
            <div class="card border-0 shadow-sm">
                <div class="card-body">
                    <h5 class="fw-bold mb-3"><i class="bi bi-plus-circle me-1"></i>Nueva ciudad</h5>
                    <form action="ciudades" method="post">
                        <input type="hidden" name="accion" value="guardarNuevo">
                        <div class="mb-3">
                            <label class="form-label">Nombre <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="nombre" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Departamento</label>
                            <input type="text" class="form-control" name="departamento">
                        </div>
                        <button type="submit" class="btn btn-primary w-100"><i class="bi bi-save me-1"></i>Guardar</button>
                    </form>
                </div>
            </div>
        </div>
        <div class="col-lg-8">
            <div class="card border-0 shadow-sm">
                <div class="card-body table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr><th>ID</th><th>Nombre</th><th>Departamento</th><th>Acciones</th></tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${ciudades}">
                                <tr>
                                    <td>${c.idCiudad}</td>
                                    <td><strong><c:out value="${c.nombre}" /></strong></td>
                                    <td><c:out value="${c.departamento}" /></td>
                                    <td>
                                        <div class="d-flex gap-2 align-items-center">
                                            <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal"
                                                    data-bs-target="#modalEditar${c.idCiudad}"><i class="bi bi-pencil"></i></button>
                                            <a href="ciudades?accion=eliminar&id=${c.idCiudad}"
                                               class="btn btn-sm btn-outline-danger"
                                               onclick="return confirm('¿Eliminar la ciudad ${c.nombre}?');"><i class="bi bi-trash"></i></a>
                                        </div>
                                        <!-- Modal edición -->
                                        <div class="modal fade" id="modalEditar${c.idCiudad}" tabindex="-1">
                                            <div class="modal-dialog">
                                                <div class="modal-content">
                                                    <form action="ciudades" method="post">
                                                        <div class="modal-header">
                                                            <h5 class="modal-title">Editar ciudad</h5>
                                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                        </div>
                                                        <div class="modal-body">
                                                            <input type="hidden" name="accion" value="guardarEdicion">
                                                            <input type="hidden" name="id" value="${c.idCiudad}">
                                                            <div class="mb-3">
                                                                <label class="form-label">Nombre</label>
                                                                <input type="text" class="form-control" name="nombre" value="${c.nombre}" required>
                                                            </div>
                                                            <div class="mb-3">
                                                                <label class="form-label">Departamento</label>
                                                                <input type="text" class="form-control" name="departamento" value="${c.departamento}">
                                                            </div>
                                                        </div>
                                                        <div class="modal-footer">
                                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                                                            <button type="submit" class="btn btn-primary">Guardar</button>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</main>

<%@ include file="../WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>