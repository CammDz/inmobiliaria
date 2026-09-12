package com.inmobiliaria.controller.admin;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.model.Auditoria;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/auditoria")
public class AuditoriaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String filtroUsuarios = request.getParameter("idUsuario");
        List<Auditoria> registros;
        if (filtroUsuarios != null && !filtroUsuarios.isEmpty()) {
            registros = auditoriaDAO.listarPorUsuario(parsearId(filtroUsuarios));
        } else {
            registros = auditoriaDAO.listarTodas();
        }
        request.setAttribute("registros", registros);
        request.getRequestDispatcher("/admin/auditoria.jsp").forward(request, response);
    }

    private int parsearId(String valor) {
        try {
            return valor != null ? Integer.parseInt(valor) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}