package com.inmobiliaria.controller.admin;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.TipoPropiedadDAO;
import com.inmobiliaria.model.TipoPropiedad;
import com.inmobiliaria.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/tipos")
public class TiposServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final TipoPropiedadDAO tipoDAO = new TipoPropiedadDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if ("eliminar".equals(accion)) {
            int id = parsearId(request.getParameter("id"));
            boolean ok = tipoDAO.eliminar(id);
            if (ok) {
                auditoriaDAO.registrar(AuthUtil.getUsuarioId(request.getSession(false)),
                        "ELIMINACIÓN DE TIPO DE PROPIEDAD", "tipo_propiedad", id,
                        "Tipo eliminado", AuthUtil.getIp(request));
                AuthUtil.setMensajeExito(request.getSession(), "Tipo de propiedad eliminado correctamente.");
            } else {
                AuthUtil.setMensajeError(request.getSession(),
                        "No se pudo eliminar el tipo porque tiene propiedades asociadas.");
            }
            response.sendRedirect(request.getContextPath() + "/admin/tipos");
            return;
        }
        request.setAttribute("tipos", tipoDAO.listarTodos());
        request.getRequestDispatcher("/admin/tipos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");

        Integer adminId = AuthUtil.getUsuarioId(request.getSession(false));

        if (nombre == null || nombre.trim().isEmpty()) {
            AuthUtil.setMensajeError(request.getSession(), "El nombre del tipo es obligatorio.");
            response.sendRedirect(request.getContextPath() + "/admin/tipos");
            return;
        }

        if ("guardarNuevo".equals(accion)) {
            TipoPropiedad t = new TipoPropiedad();
            t.setNombre(nombre.trim());
            t.setDescripcion(descripcion);
            if (tipoDAO.insertar(t)) {
                auditoriaDAO.registrar(adminId, "CREACIÓN DE TIPO DE PROPIEDAD", "tipo_propiedad",
                        t.getIdTipo(), "Tipo creado: " + nombre, AuthUtil.getIp(request));
                AuthUtil.setMensajeExito(request.getSession(), "Tipo de propiedad creado correctamente.");
            } else {
                AuthUtil.setMensajeError(request.getSession(), "No se pudo crear el tipo de propiedad.");
            }
        } else if ("guardarEdicion".equals(accion)) {
            int id = parsearId(request.getParameter("id"));
            TipoPropiedad t = tipoDAO.buscarPorId(id);
            if (t != null) {
                t.setNombre(nombre.trim());
                t.setDescripcion(descripcion);
                if (tipoDAO.actualizar(t)) {
                    auditoriaDAO.registrar(adminId, "MODIFICACIÓN DE TIPO DE PROPIEDAD",
                            "tipo_propiedad", id, "Tipo modificado: " + nombre, AuthUtil.getIp(request));
                    AuthUtil.setMensajeExito(request.getSession(), "Tipo de propiedad actualizado correctamente.");
                } else {
                    AuthUtil.setMensajeError(request.getSession(), "No se pudo actualizar el tipo.");
                }
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/tipos");
    }

    private int parsearId(String valor) {
        try {
            return valor != null ? Integer.parseInt(valor) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}