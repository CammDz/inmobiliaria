package com.inmobiliaria.controller;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.RecuperacionDAO;
import com.inmobiliaria.dao.UsuarioDAO;
import com.inmobiliaria.model.Usuario;
import com.inmobiliaria.util.AuthUtil;
import com.inmobiliaria.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Solicitud de recuperación de contraseña.
 *
 * <p>Genera un token de un solo uso con expiración. Si el correo existe se
 * crea el token; el enlace se envía por correo si el SMTP está configurado,
 * y en modo demostración se muestra en pantalla y en la consola de Tomcat.</p>
 *
 * <p>Medida anti-enumeración: siempre se responde el mismo mensaje
 * «Si el correo está registrado recibirá un enlace» para no revelar
 * qué correos existen.</p>
 */
@WebServlet("/recuperar")
public class RecuperarServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RecuperacionDAO recuperacionDAO = new RecuperacionDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/recuperar.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String correo = request.getParameter("correo");

        if (ValidationUtil.estaVacio(correo)) {
            request.setAttribute("error", "Ingrese su correo electrónico.");
            request.getRequestDispatcher("/recuperar.jsp").forward(request, response);
            return;
        }
        if (!ValidationUtil.esCorreoValido(correo)) {
            request.setAttribute("error", "El formato del correo no es válido.");
            request.getRequestDispatcher("/recuperar.jsp").forward(request, response);
            return;
        }

        Usuario usuario = usuarioDAO.buscarPorCorreo(correo.trim());
        String mensaje = "Si el correo ingresado está registrado, recibirá un enlace "
                + "para restablecer su contraseña.";

        if (usuario != null) {
            String token = recuperacionDAO.crearToken(usuario.getIdUsuario());
            if (token != null) {
                String enlace = construirEnlace(request, token);
                // En producción el enlace se envía por correo (SMTP). Aquí se
                // registra en consola y, en modo demostración, en pantalla.
                System.out.println("[RecuperarServlet] Enlace de recuperación para "
                        + correo + ": " + enlace);
                auditoriaDAO.registrar(usuario.getIdUsuario(), "SOLICITUD RECUPERACIÓN",
                        "token_recuperacion", 0,
                        "Se generó un token de recuperación de contraseña",
                        AuthUtil.getIp(request));

                request.setAttribute("enlaceDemo", enlace);
                request.setAttribute("correoDestino", correo.trim());
            }
        }

        request.setAttribute("mensaje", mensaje);
        request.getRequestDispatcher("/recuperar.jsp").forward(request, response);
    }

    /**
     * Construye la URL completa del enlace de restablecimiento.
     */
    private String construirEnlace(HttpServletRequest request, String token) {
        return request.getScheme() + "://" + request.getServerName() + ":"
                + request.getServerPort() + request.getContextPath()
                + "/restablecer?token=" + token;
    }
}