package com.inmobiliaria.controller;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer idUsuario = AuthUtil.getUsuarioId(request.getSession(false));
        if (idUsuario != null) {
            auditoriaDAO.registrar(idUsuario, "CIERRE DE SESIÓN",
                    "usuario", idUsuario, "El usuario cerró sesión", AuthUtil.getIp(request));
        }
        AuthUtil.cerrarSesion(request.getSession(false));
        AuthUtil.setMensajeExito(request.getSession(),
                "Sesión cerrada correctamente.");
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}