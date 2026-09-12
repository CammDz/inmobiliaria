<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Usuario y roles - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<%@ include file="../WEB-INF/jspf/navbar.jspf" %>
<%@ include file="../WEB-INF/jspf/mensajes.jspf" %>

<main class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fw-bold mb-0"><i class="bi bi-people me-2 text-primary"></i>Usuarios y roles</h2>
        <a href="usuarios?accion=crear" class="btn btn-primary"><i class="bi bi-person-plus me-1"></i>Nuevo usuario</a>
    </div>

    <div class="card border-0 shadow-sm">
        <div class="card-body table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Correo</th>
                        <th>Teléfono</th>
                        <th>Roles</th>
                        <th>Estado</th>
                        <th>Registro</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="u" items="${usuarios}">
                        <tr>
                            <td>${u.idUsuario}</td>
                            <td><strong><c:out value="${u.nombre}" /> <c:out value="${u.apellido}" /></strong></td>
                            <td><c:out value="${u.correo}" /></td>
                            <td><c:out value="${u.telefono}" /></td>
                            <td>
                                <c:forEach var="r" items="${roless}">
                                    <c:forEach var="rid" items="${rolesPorUsuario[u.idUsuario]}">
                                        <c:if test="${rid == r.idRol}">
                                            <span class="badge bg-secondary">${r.nombre}</span>
                                        </c:if>
                                    </c:forEach>
                                </c:forEach>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${u.activo}">
                                        <span class="badge bg-success">Activo</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-danger">Inactivo</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="small text-muted"><c:out value="${u.fechaRegistro}" /></td>
                            <td>
                                <a href="usuarios?accion=editar&id=${u.idUsuario}" class="btn btn-sm btn-outline-primary" title="Editar">
                                    <i class="bi bi-pencil"></i>
                                </a>
                                <c:choose>
                                    <c:when test="${u.activo}">
                                        <a href="usuarios?accion=desactivar&id=${u.idUsuario}" class="btn btn-sm btn-outline-danger"
                                           onclick="return confirm('¿Desactivar este usuario? No podrá iniciar sesión.');" title="Desactivar">
                                            <i class="bi bi-person-x"></i>
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="usuarios?accion=activar&id=${u.idUsuario}" class="btn btn-sm btn-outline-success" title="Activar">
                                            <i class="bi bi-person-check"></i>
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</main>

<%@ include file="../WEB-INF/jspf/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>