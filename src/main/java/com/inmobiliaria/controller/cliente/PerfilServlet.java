package com.inmobiliaria.controller.cliente;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.PerfilDAO;
import com.inmobiliaria.dao.UsuarioDAO;
import com.inmobiliaria.model.Perfil;
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
import java.sql.Date;

@WebServlet("/cliente/perfil")
public class PerfilServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final PerfilDAO perfilDAO = new PerfilDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idUsuario = AuthUtil.getUsuarioId(request.getSession(false));

        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        Perfil perfil = perfilDAO.buscarPorIdUsuario(idUsuario);

        request.setAttribute("usuario", usuario);
        request.setAttribute("perfil", perfil);
        request.getRequestDispatcher("/cliente/perfil.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idUsuario = AuthUtil.getUsuarioId(request.getSession(false));
        String accion = request.getParameter("accion");

        if ("guardarDatos".equals(accion)) {
            guardarDatos(request, response, idUsuario);
        } else if ("cambiarContrasena".equals(accion)) {
            cambiarContrasena(request, response, idUsuario);
        } else {
            response.sendRedirect(request.getContextPath() + "/cliente/perfil");
        }
    }

    private void guardarDatos(HttpServletRequest request, HttpServletResponse response, int idUsuario)
            throws IOException {
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");
        String documentoIdentidad = request.getParameter("documentoIdentidad");
        String fechaNacimiento = request.getParameter("fechaNacimiento");

        if (ValidationUtil.estaVacio(nombre) || ValidationUtil.estaVacio(apellido)
                || ValidationUtil.estaVacio(correo)) {
            AuthUtil.setMensajeError(request.getSession(), "Los campos obligatorios deben ser diligenciados.");
            response.sendRedirect(request.getContextPath() + "/cliente/perfil");
            return;
        }
        if (!ValidationUtil.esCorreoValido(correo)) {
            AuthUtil.setMensajeError(request.getSession(), "El formato del correo no es válido.");
            response.sendRedirect(request.getContextPath() + "/cliente/perfil");
            return;
        }
        if (!ValidationUtil.esTelefonoValido(telefono)) {
            AuthUtil.setMensajeError(request.getSession(), "El formato del teléfono no es válido.");
            response.sendRedirect(request.getContextPath() + "/cliente/perfil");
            return;
        }

        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        if (usuario == null) {
            AuthUtil.setMensajeError(request.getSession(), "El usuario no existe.");
            response.sendRedirect(request.getContextPath() + "/cliente/perfil");
            return;
        }

        // Verificar que el correo no esté en uso por otro usuario
        Usuario existente = usuarioDAO.buscarPorCorreo(correo);
        if (existente != null && existente.getIdUsuario() != idUsuario) {
            AuthUtil.setMensajeError(request.getSession(),
                    "No fue posible actualizar: el correo ya está registrado por otro usuario.");
            response.sendRedirect(request.getContextPath() + "/cliente/perfil");
            return;
        }

        usuario.setNombre(nombre.trim());
        usuario.setApellido(apellido.trim());
        usuario.setCorreo(correo.trim());
        usuario.setTelefono(telefono);
        if (usuarioDAO.actualizar(usuario)) {
            AuthUtil.guardarSesion(request.getSession(), idUsuario,
                    usuario.getNombreCompleto(), usuario.getCorreo(), AuthUtil.getRoles(request.getSession()));
        }

        // Perfil 1:1
        Perfil perfil = perfilDAO.buscarPorIdUsuario(idUsuario);
        if (perfil == null) {
            perfil = new Perfil();
            perfil.setIdUsuario(idUsuario);
            perfilDAO.crear(perfil);
        }
        perfil.setDireccion(direccion);
        perfil.setDocumentoIdentidad(documentoIdentidad);
        if (fechaNacimiento != null && !fechaNacimiento.trim().isEmpty()) {
            try {
                perfil.setFechaNacimiento(Date.valueOf(fechaNacimiento));
            } catch (IllegalArgumentException e) {
                perfil.setFechaNacimiento(null);
            }
        } else {
            perfil.setFechaNacimiento(null);
        }
        perfilDAO.actualizar(perfil);

        auditoriaDAO.registrar(idUsuario, "MODIFICACIÓN DE PERFIL", "perfil", idUsuario,
                "El usuario actualizó su perfil", AuthUtil.getIp(request));

        AuthUtil.setMensajeExito(request.getSession(), "Perfil actualizado correctamente.");
        response.sendRedirect(request.getContextPath() + "/cliente/perfil");
    }

    private void cambiarContrasena(HttpServletRequest request, HttpServletResponse response, int idUsuario)
            throws IOException {
        String contrasenaActual = request.getParameter("contrasenaActual");
        String nuevaContrasena = request.getParameter("nuevaContrasena");
        String confirmarContrasena = request.getParameter("confirmarContrasena");

        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        if (usuario == null || !PasswordUtil.verificarPassword(contrasenaActual, usuario.getContrasena())) {
            AuthUtil.setMensajeError(request.getSession(), "La contraseña actual no es correcta.");
            response.sendRedirect(request.getContextPath() + "/cliente/perfil");
            return;
        }
        if (!ValidationUtil.esContrasenaValida(nuevaContrasena)) {
            AuthUtil.setMensajeError(request.getSession(), "La nueva contraseña debe tener al menos 4 caracteres.");
            response.sendRedirect(request.getContextPath() + "/cliente/perfil");
            return;
        }
        if (!nuevaContrasena.equals(confirmarContrasena)) {
            AuthUtil.setMensajeError(request.getSession(), "Las contraseñas no coinciden.");
            response.sendRedirect(request.getContextPath() + "/cliente/perfil");
            return;
        }

        if (usuarioDAO.actualizarContrasena(idUsuario, PasswordUtil.hashPassword(nuevaContrasena))) {
            auditoriaDAO.registrar(idUsuario, "CAMBIO DE CONTRASEÑA", "usuario", idUsuario,
                    "El usuario cambió su contraseña", AuthUtil.getIp(request));
            AuthUtil.setMensajeExito(request.getSession(), "Contraseña actualizada correctamente.");
        } else {
            AuthUtil.setMensajeError(request.getSession(), "No se pudo actualizar la contraseña.");
        }
        response.sendRedirect(request.getContextPath() + "/cliente/perfil");
    }
}