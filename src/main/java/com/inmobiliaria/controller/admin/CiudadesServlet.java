package com.inmobiliaria.controller.admin;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.CiudadDAO;
import com.inmobiliaria.model.Ciudad;
import com.inmobiliaria.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/ciudades")
public class CiudadesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CiudadDAO ciudadDAO = new CiudadDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if ("eliminar".equals(accion)) {
            int id = parsearId(request.getParameter("id"));
            boolean ok = ciudadDAO.eliminar(id);
            if (ok) {
                auditoriaDAO.registrar(AuthUtil.getUsuarioId(request.getSession(false)),
                        "ELIMINACIÓN DE CIUDAD", "ciudad", id, "Ciudad eliminada", AuthUtil.getIp(request));
                AuthUtil.setMensajeExito(request.getSession(), "Ciudad eliminada correctamente.");
            } else {
                AuthUtil.setMensajeError(request.getSession(),
                        "No se pudo eliminar la ciudad porque tiene propiedades asociadas.");
            }
            response.sendRedirect(request.getContextPath() + "/admin/ciudades");
            return;
        }

        request.setAttribute("ciudades", ciudadDAO.listarTodas());
        request.getRequestDispatcher("/admin/ciudades.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        String nombre = request.getParameter("nombre");
        String departamento = request.getParameter("departamento");

        Integer adminId = AuthUtil.getUsuarioId(request.getSession(false));

        if (nombre == null || nombre.trim().isEmpty()) {
            AuthUtil.setMensajeError(request.getSession(), "El nombre de la ciudad es obligatorio.");
            response.sendRedirect(request.getContextPath() + "/admin/ciudades");
            return;
        }

        if ("guardarNuevo".equals(accion)) {
            Ciudad c = new Ciudad();
            c.setNombre(nombre.trim());
            c.setDepartamento(departamento);
            if (ciudadDAO.insertar(c)) {
                auditoriaDAO.registrar(adminId, "CREACIÓN DE CIUDAD", "ciudad", c.getIdCiudad(),
                        "Ciudad creada: " + nombre, AuthUtil.getIp(request));
                AuthUtil.setMensajeExito(request.getSession(), "Ciudad creada correctamente.");
            } else {
                AuthUtil.setMensajeError(request.getSession(), "No se pudo crear la ciudad.");
            }
        } else if ("guardarEdicion".equals(accion)) {
            int id = parsearId(request.getParameter("id"));
            Ciudad c = ciudadDAO.buscarPorId(id);
            if (c != null) {
                c.setNombre(nombre.trim());
                c.setDepartamento(departamento);
                if (ciudadDAO.actualizar(c)) {
                    auditoriaDAO.registrar(adminId, "MODIFICACIÓN DE CIUDAD", "ciudad", id,
                            "Ciudad modificada: " + nombre, AuthUtil.getIp(request));
                    AuthUtil.setMensajeExito(request.getSession(), "Ciudad actualizada correctamente.");
                } else {
                    AuthUtil.setMensajeError(request.getSession(), "No se pudo actualizar la ciudad.");
                }
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/ciudades");
    }

    private int parsearId(String valor) {
        try {
            return valor != null ? Integer.parseInt(valor) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}