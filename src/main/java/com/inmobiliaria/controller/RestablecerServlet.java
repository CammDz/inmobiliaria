package com.inmobiliaria.controller;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.RecuperacionDAO;
import com.inmobiliaria.dao.UsuarioDAO;
import com.inmobiliaria.util.AuthUtil;
import com.inmobiliaria.util.PasswordUtil;
import com.inmobiliaria.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Restablecimiento de contraseña con token.
 *
 * <p>GET: valida el token y muestra el formulario de nueva contraseña.
 * POST: valida la contraseña, la actualiza con BCrypt y consume el token
 * (de un solo uso).</p>
 */
@WebServlet("/restablecer")
public class RestablecerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final RecuperacionDAO recuperacionDAO = new RecuperacionDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        int idUsuario = recuperacionDAO.validarToken(token);

        if (idUsuario <= 0) {
            request.setAttribute("error",
                    "El enlace no es válido, ya fue usado o está vencido. Solicite uno nuevo.");
            request.getRequestDispatcher("/recuperar.jsp").forward(request, response);
            return;
        }

        request.setAttribute("token", token);
        request.getRequestDispatcher("/restablecer.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        String nuevaContrasena = request.getParameter("contrasena");
        String confirmarContrasena = request.getParameter("confirmarContrasena");

        int idUsuario = recuperacionDAO.validarToken(token);
        if (idUsuario <= 0) {
            request.setAttribute("error",
                    "El enlace no es válido, ya fue usado o está vencido. Solicite uno nuevo.");
            request.getRequestDispatcher("/recuperar.jsp").forward(request, response);
            return;
        }

        if (ValidationUtil.estaVacio(nuevaContrasena)) {
            request.setAttribute("error", "Ingrese la nueva contraseña.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/restablecer.jsp").forward(request, response);
            return;
        }
        if (!ValidationUtil.esContrasenaValida(nuevaContrasena)) {
            request.setAttribute("error", "La contraseña debe tener al menos 4 caracteres.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/restablecer.jsp").forward(request, response);
            return;
        }
        if (!nuevaContrasena.equals(confirmarContrasena)) {
            request.setAttribute("error", "Las contraseñas no coinciden.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/restablecer.jsp").forward(request, response);
            return;
        }

        if (usuarioDAO.actualizarContrasena(idUsuario, PasswordUtil.hashPassword(nuevaContrasena))) {
            // Consumir el token (un solo uso).
            recuperacionDAO.marcarUsado(token);
            auditoriaDAO.registrar(idUsuario, "RESTABLECIMIENTO DE CONTRASEÑA",
                    "usuario", idUsuario,
                    "El usuario restableció su contraseña con token", AuthUtil.getIp(request));

            System.out.println("[RestablecerServlet] Contraseña restablecida para el usuario id=" + idUsuario);
            AuthUtil.setMensajeExito(request.getSession(),
                    "Su contraseña fue restablecida correctamente. Ya puede iniciar sesión.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        } else {
            request.setAttribute("error", "No se pudo restablecer la contraseña. Intente nuevamente.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/restablecer.jsp").forward(request, response);
        }
    }
}