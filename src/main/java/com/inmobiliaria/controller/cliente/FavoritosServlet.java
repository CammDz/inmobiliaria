package com.inmobiliaria.controller.cliente;

import com.inmobiliaria.dao.FavoritoDAO;
import com.inmobiliaria.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/cliente/favoritos")
public class FavoritosServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final FavoritoDAO favoritoDAO = new FavoritoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idCliente = AuthUtil.getUsuarioId(request.getSession(false));

        String accion = request.getParameter("accion");
        if ("quitar".equals(accion)) {
            int idPropiedad = parsearId(request.getParameter("id"));
            favoritoDAO.quitar(idCliente, idPropiedad);
            AuthUtil.setMensajeExito(request.getSession(), "La propiedad fue retirada de sus favoritos.");
            response.sendRedirect(request.getContextPath() + "/cliente/favoritos");
            return;
        }

        request.setAttribute("favoritos", favoritoDAO.listarPorCliente(idCliente));
        request.getRequestDispatcher("/cliente/favoritos.jsp").forward(request, response);
    }

    private int parsearId(String valor) {
        try {
            return valor != null ? Integer.parseInt(valor) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}