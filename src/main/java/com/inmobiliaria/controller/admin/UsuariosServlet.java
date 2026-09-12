package com.inmobiliaria.controller.admin;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.PerfilDAO;
import com.inmobiliaria.dao.RolDAO;
import com.inmobiliaria.dao.UsuarioDAO;
import com.inmobiliaria.model.Perfil;
import com.inmobiliaria.model.Rol;
import com.inmobiliaria.model.Usuario;
import com.inmobiliaria.util.AuthUtil;
import com.inmobiliaria.util.PasswordUtil;
import com.inmobiliaria.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/usuarios")
public class UsuariosServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RolDAO rolDAO = new RolDAO();
    private final PerfilDAO perfilDAO = new PerfilDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        switch (accion) {
            case "crear":
                mostrarFormulario(request, response, null);
                break;
            case "editar":
                mostrarFormulario(request, response, usuarioDAO.buscarPorId(parsearId(request.getParameter("id"))));
                break;
            case "desactivar":
                desactivar(request, response);
                break;
            case "activar":
                activar(request, response);
                break;
            default:
                listar(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if ("guardarNuevo".equals(accion)) {
            crearNuevo(request, response);
        } else if ("guardarEdicion".equals(accion)) {
            actualizar(request, response);
        } else {
            listar(request, response);
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        java.util.Map<Integer, java.util.List<Integer>> rolesPorUsuario = new java.util.HashMap<>();
        for (Usuario u : usuarios) {
            rolesPorUsuario.put(u.getIdUsuario(), rolDAO.obtenerIdsRolesDeUsuario(u.getIdUsuario()));
        }
        request.setAttribute("usuarios", usuarios);
        request.setAttribute("roless", rolDAO.listarTodos());
        request.setAttribute("rolesPorUsuario", rolesPorUsuario);
        request.getRequestDispatcher("/admin/usuarios.jsp").forward(request, response);
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {
        request.setAttribute("usuario", usuario);
        request.setAttribute("roles", rolDAO.listarTodos());
        request.setAttribute("perfil", usuario != null ? perfilDAO.buscarPorIdUsuario(usuario.getIdUsuario()) : null);
        request.setAttribute("rolesUsuario", usuario != null
                ? rolDAO.obtenerIdsRolesDeUsuario(usuario.getIdUsuario()) : new java.util.ArrayList<Integer>());
        request.getRequestDispatcher("/admin/usuario-form.jsp").forward(request, response);
    }

    private void crearNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        String contrasena = request.getParameter("contrasena");
        String[] rolesIds = request.getParameterValues("roles");

        if (ValidationUtil.estaVacio(nombre) || ValidationUtil.estaVacio(apellido)
                || ValidationUtil.estaVacio(correo) || ValidationUtil.estaVacio(contrasena)) {
            AuthUtil.setMensajeError(request.getSession(), "Todos los campos obligatorios deben ser diligenciados.");
            response.sendRedirect(request.getContextPath() + "/admin/usuarios?accion=crear");
            return;
        }
        if (!ValidationUtil.esCorreoValido(correo)) {
            AuthUtil.setMensajeError(request.getSession(), "El formato del correo no es válido.");
            response.sendRedirect(request.getContextPath() + "/admin/usuarios?accion=crear");
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre.trim());
        usuario.setApellido(apellido.trim());
        usuario.setCorreo(correo.trim());
        usuario.setTelefono(telefono);
        usuario.setContrasena(PasswordUtil.hashPassword(contrasena));
        usuario.setActivo(true);

        int nuevoId;
        try {
            nuevoId = usuarioDAO.insertar(usuario);
        } catch (java.sql.SQLException e) {
            AuthUtil.setMensajeError(request.getSession(),
                    "No fue posible crear el usuario. Verifique los datos e intente nuevamente.");
            response.sendRedirect(request.getContextPath() + "/admin/usuarios?accion=crear");
            return;
        }
        if (nuevoId == -1) {
            AuthUtil.setMensajeError(request.getSession(),
                    "No fue posible crear el usuario porque el correo ya está registrado.");
            response.sendRedirect(request.getContextPath() + "/admin/usuarios?accion=crear");
            return;
        }

        // Asignar roles
        if (rolesIds != null) {
            for (String rolId : rolesIds) {
                rolDAO.asignarRol(nuevoId, parsearId(rolId));
            }
        }

        // Crear perfil 1:1
        Perfil perfil = new Perfil();
        perfil.setIdUsuario(nuevoId);
        perfilDAO.crear(perfil);

        // Auditoría
        Integer adminId = AuthUtil.getUsuarioId(request.getSession(false));
        auditoriaDAO.registrar(adminId, "CREACIÓN DE USUARIO", "usuario", nuevoId,
                "Administrador creó el usuario " + correo, AuthUtil.getIp(request));

        AuthUtil.setMensajeExito(request.getSession(), "Usuario creado correctamente.");
        response.sendRedirect(request.getContextPath() + "/admin/usuarios");
    }

    private void actualizar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = parsearId(request.getParameter("id"));
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        String contrasena = request.getParameter("contrasena");
        String[] rolesIds = request.getParameterValues("roles");
        String activoParam = request.getParameter("activo");

        if (ValidationUtil.estaVacio(nombre) || ValidationUtil.estaVacio(apellido)
                || ValidationUtil.estaVacio(correo)) {
            AuthUtil.setMensajeError(request.getSession(), "Todos los campos obligatorios deben ser diligenciados.");
            response.sendRedirect(request.getContextPath() + "/admin/usuarios?accion=editar&id=" + id);
            return;
        }
        if (!ValidationUtil.esCorreoValido(correo)) {
            AuthUtil.setMensajeError(request.getSession(), "El formato del correo no es válido.");
            response.sendRedirect(request.getContextPath() + "/admin/usuarios?accion=editar&id=" + id);
            return;
        }

        Usuario usuario = usuarioDAO.buscarPorId(id);
        if (usuario == null) {
            AuthUtil.setMensajeError(request.getSession(), "El usuario no existe.");
            response.sendRedirect(request.getContextPath() + "/admin/usuarios");
            return;
        }

        usuario.setNombre(nombre.trim());
        usuario.setApellido(apellido.trim());
        usuario.setCorreo(correo.trim());
        usuario.setTelefono(telefono);
        usuario.setActivo("1".equals(activoParam));

        usuarioDAO.actualizar(usuario);

        // Actualizar contraseña si fue escrita
        if (!ValidationUtil.estaVacio(contrasena)) {
            usuarioDAO.actualizarContrasena(id, PasswordUtil.hashPassword(contrasena));
        }

        // Actualizar roles
        rolDAO.obtenerIdsRolesDeUsuario(id).forEach(rolId -> rolDAO.retirarRol(id, rolId));
        if (rolesIds != null) {
            for (String rolId : rolesIds) {
                rolDAO.asignarRol(id, parsearId(rolId));
            }
        }

        Integer adminId = AuthUtil.getUsuarioId(request.getSession(false));
        auditoriaDAO.registrar(adminId, "MODIFICACIÓN DE USUARIO", "usuario", id,
                "Administrador modificó el usuario " + correo, AuthUtil.getIp(request));

        AuthUtil.setMensajeExito(request.getSession(), "Usuario actualizado correctamente.");
        response.sendRedirect(request.getContextPath() + "/admin/usuarios");
    }

    private void desactivar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = parsearId(request.getParameter("id"));
        Usuario u = usuarioDAO.buscarPorId(id);
        boolean ok = usuarioDAO.desactivar(id);
        if (u != null) {
            Integer adminId = AuthUtil.getUsuarioId(request.getSession(false));
            auditoriaDAO.registrar(adminId, "DESACTIVACIÓN DE USUARIO", "usuario", id,
                    "Desactivó el usuario " + u.getCorreo(), AuthUtil.getIp(request));
        }
        String mensaje = ok ? "Usuario desactivado correctamente."
                : "No se pudo desactivar el usuario.";
        setMensajeSegunEstado(request, ok, mensaje);
        response.sendRedirect(request.getContextPath() + "/admin/usuarios");
    }

    private void activar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = parsearId(request.getParameter("id"));
        Usuario u = usuarioDAO.buscarPorId(id);
        boolean ok = usuarioDAO.activar(id);
        if (u != null) {
            Integer adminId = AuthUtil.getUsuarioId(request.getSession(false));
            auditoriaDAO.registrar(adminId, "ACTIVACIÓN DE USUARIO", "usuario", id,
                    "Activó el usuario " + u.getCorreo(), AuthUtil.getIp(request));
        }
        String mensaje = ok ? "Usuario activado correctamente."
                : "No se pudo activar el usuario.";
        setMensajeSegunEstado(request, ok, mensaje);
        response.sendRedirect(request.getContextPath() + "/admin/usuarios");
    }

    private void setMensajeSegunEstado(HttpServletRequest request, boolean ok, String okMsg) {
        if (ok) {
            AuthUtil.setMensajeExito(request.getSession(), okMsg);
        } else {
            AuthUtil.setMensajeError(request.getSession(), okMsg.replace("correctamente.", "."));
        }
    }

    private int parsearId(String valor) {
        try {
            return valor != null ? Integer.parseInt(valor) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}