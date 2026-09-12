package com.inmobiliaria.controller.admin;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.CaracteristicaDAO;
import com.inmobiliaria.model.Caracteristica;
import com.inmobiliaria.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/caracteristicas")
public class CaracteristicasServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CaracteristicaDAO caracteristicaDAO = new CaracteristicaDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if ("eliminar".equals(accion)) {
            int id = parsearId(request.getParameter("id"));
            boolean ok = caracteristicaDAO.eliminar(id);
            if (ok) {
                auditoriaDAO.registrar(AuthUtil.getUsuarioId(request.getSession(false)),
                        "ELIMINACIÓN DE CARACTERÍSTICA", "caracteristica", id,
                        "Característica eliminada", AuthUtil.getIp(request));
                AuthUtil.setMensajeExito(request.getSession(), "Característica eliminada correctamente.");
            } else {
                AuthUtil.setMensajeError(request.getSession(),
                        "No se pudo eliminar la característica porque está asociada a propiedades.");
            }
            response.sendRedirect(request.getContextPath() + "/admin/caracteristicas");
            return;
        }
        request.setAttribute("caracteristicas", caracteristicaDAO.listarTodas());
        request.getRequestDispatcher("/admin/caracteristicas.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");

        Integer adminId = AuthUtil.getUsuarioId(request.getSession(false));

        if (nombre == null || nombre.trim().isEmpty()) {
            AuthUtil.setMensajeError(request.getSession(), "El nombre de la característica es obligatorio.");
            response.sendRedirect(request.getContextPath() + "/admin/caracteristicas");
            return;
        }

        if ("guardarNuevo".equals(accion)) {
            Caracteristica c = new Caracteristica();
            c.setNombre(nombre.trim());
            c.setDescripcion(descripcion);
            if (caracteristicaDAO.insertar(c)) {
                auditoriaDAO.registrar(adminId, "CREACIÓN DE CARACTERÍSTICA", "caracteristica",
                        c.getIdCaracteristica(), "Característica creada: " + nombre, AuthUtil.getIp(request));
                AuthUtil.setMensajeExito(request.getSession(), "Característica creada correctamente.");
            } else {
                AuthUtil.setMensajeError(request.getSession(), "No se pudo crear la característica.");
            }
        } else if ("guardarEdicion".equals(accion)) {
            int id = parsearId(request.getParameter("id"));
            Caracteristica c = caracteristicaDAO.buscarPorId(id);
            if (c != null) {
                c.setNombre(nombre.trim());
                c.setDescripcion(descripcion);
                if (caracteristicaDAO.actualizar(c)) {
                    auditoriaDAO.registrar(adminId, "MODIFICACIÓN DE CARACTERÍSTICA", "caracteristica",
                            id, "Característica modificada: " + nombre, AuthUtil.getIp(request));
                    AuthUtil.setMensajeExito(request.getSession(), "Característica actualizada correctamente.");
                } else {
                    AuthUtil.setMensajeError(request.getSession(), "No se pudo actualizar la característica.");
                }
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/caracteristicas");
    }

    private int parsearId(String valor) {
        try {
            return valor != null ? Integer.parseInt(valor) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}