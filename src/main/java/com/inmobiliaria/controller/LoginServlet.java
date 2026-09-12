package com.inmobiliaria.controller;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.RolDAO;
import com.inmobiliaria.dao.UsuarioDAO;
import com.inmobiliaria.model.Usuario;
import com.inmobiliaria.util.AuthUtil;
import com.inmobiliaria.util.PasswordUtil;
import com.inmobiliaria.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RolDAO rolDAO = new RolDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si ya está autenticado, redirigir al dashboard correspondiente
        HttpSession session = request.getSession(false);
        if (AuthUtil.estaAutenticado(session)) {
            redirigirSegunRol(request, response, session);
            return;
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");

        // Validar campos
        if (ValidationUtil.estaVacio(correo) || ValidationUtil.estaVacio(contrasena)) {
            request.setAttribute("error", "Ingrese su correo y contraseña.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        if (!ValidationUtil.esCorreoValido(correo)) {
            request.setAttribute("error", "El formato del correo no es válido.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        Usuario usuario = usuarioDAO.buscarPorCorreo(correo);

        if (usuario == null || !PasswordUtil.verificarPassword(contrasena, usuario.getContrasena())) {
            request.setAttribute("error", "Correo o contraseña incorrectos.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        if (!usuario.isActivo()) {
            request.setAttribute("error", "Su cuenta está desactivada. Contacte al administrador.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // Protección contra fijación de sesión (session fixation):
        // se descarta cualquier sesión previa (posiblemente fijada por un
        // atacante) y se crea una nueva sesión con un ID fresco.
        HttpSession sesionPrevia = request.getSession(false);
        if (sesionPrevia != null) {
            sesionPrevia.invalidate();
        }

        // Autenticación exitosa
        Set<String> roles = rolDAO.obtenerRolesDeUsuario(usuario.getIdUsuario());
        HttpSession session = request.getSession(true);
        AuthUtil.guardarSesion(session, usuario.getIdUsuario(),
                usuario.getNombreCompleto(), usuario.getCorreo(), roles);

        auditoriaDAO.registrar(usuario.getIdUsuario(), "INICIO DE SESIÓN",
                "usuario", usuario.getIdUsuario(), "El usuario inició sesión", AuthUtil.getIp(request));

        redirigirSegunRol(request, response, session);
    }

    /**
     * Redirige al usuario según su rol principal hacia el dashboard adecuado.
     */
    private void redirigirSegunRol(HttpServletRequest request, HttpServletResponse response,
                                   HttpSession session) throws IOException {
        Set<String> roles = AuthUtil.getRoles(session);
        if (roles == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        if (roles.contains("Administrador")) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
        } else if (roles.contains("Agente Inmobiliario")) {
            response.sendRedirect(request.getContextPath() + "/agente/dashboard.jsp");
        } else if (roles.contains("Cliente")) {
            response.sendRedirect(request.getContextPath() + "/cliente/dashboard.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }
}