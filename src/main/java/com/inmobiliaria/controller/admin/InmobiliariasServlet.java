package com.inmobiliaria.controller.admin;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.InmobiliariaDAO;
import com.inmobiliaria.model.Inmobiliaria;
import com.inmobiliaria.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/inmobiliarias")
public class InmobiliariasServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final InmobiliariaDAO inmobiliariaDAO = new InmobiliariaDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if ("desactivar".equals(accion)) {
            Inmobiliaria inm = inmobiliariaDAO.buscarPorId(parsearId(request.getParameter("id")));
            if (inm != null) {
                inm.setActiva(false);
                inmobiliariaDAO.actualizar(inm);
                auditoriaDAO.registrar(AuthUtil.getUsuarioId(request.getSession(false)),
                        "DESACTIVACIÓN DE INMOBILIARIA", "inmobiliaria", inm.getIdInmobiliaria(),
                        "Inmobiliaria desactivada: " + inm.getNombre(), AuthUtil.getIp(request));
                AuthUtil.setMensajeExito(request.getSession(), "Inmobiliaria desactivada correctamente.");
            }
            response.sendRedirect(request.getContextPath() + "/admin/inmobiliarias");
            return;
        }
        if ("activar".equals(accion)) {
            Inmobiliaria inm = inmobiliariaDAO.buscarPorId(parsearId(request.getParameter("id")));
            if (inm != null) {
                inm.setActiva(true);
                inmobiliariaDAO.actualizar(inm);
                auditoriaDAO.registrar(AuthUtil.getUsuarioId(request.getSession(false)),
                        "ACTIVACIÓN DE INMOBILIARIA", "inmobiliaria", inm.getIdInmobiliaria(),
                        "Inmobiliaria activada: " + inm.getNombre(), AuthUtil.getIp(request));
                AuthUtil.setMensajeExito(request.getSession(), "Inmobiliaria activada correctamente.");
            }
            response.sendRedirect(request.getContextPath() + "/admin/inmobiliarias");
            return;
        }
        request.setAttribute("inmobiliarias", inmobiliariaDAO.listarTodasIncluidasInactivas());
        request.getRequestDispatcher("/admin/inmobiliarias.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        String nombre = request.getParameter("nombre");
        Integer adminId = AuthUtil.getUsuarioId(request.getSession(false));

        if (nombre == null || nombre.trim().isEmpty()) {
            AuthUtil.setMensajeError(request.getSession(), "El nombre de la inmobiliaria es obligatorio.");
            response.sendRedirect(request.getContextPath() + "/admin/inmobiliarias");
            return;
        }

        if ("guardarNuevo".equals(accion)) {
            Inmobiliaria inm = new Inmobiliaria();
            inm.setNombre(nombre.trim());
            inm.setDireccion(request.getParameter("direccion"));
            inm.setTelefono(request.getParameter("telefono"));
            inm.setCorreo(request.getParameter("correo"));
            inm.setActiva(true);
            if (inmobiliariaDAO.insertar(inm)) {
                auditoriaDAO.registrar(adminId, "CREACIÓN DE INMOBILIARIA", "inmobiliaria",
                        inm.getIdInmobiliaria(), "Inmobiliaria creada: " + nombre, AuthUtil.getIp(request));
                AuthUtil.setMensajeExito(request.getSession(), "Inmobiliaria creada correctamente.");
            } else {
                AuthUtil.setMensajeError(request.getSession(), "No se pudo crear la inmobiliaria.");
            }
        } else if ("guardarEdicion".equals(accion)) {
            int id = parsearId(request.getParameter("id"));
            Inmobiliaria inm = inmobiliariaDAO.buscarPorId(id);
            if (inm != null) {
                inm.setNombre(nombre.trim());
                inm.setDireccion(request.getParameter("direccion"));
                inm.setTelefono(request.getParameter("telefono"));
                inm.setCorreo(request.getParameter("correo"));
                if (inmobiliariaDAO.actualizar(inm)) {
                    auditoriaDAO.registrar(adminId, "MODIFICACIÓN DE INMOBILIARIA", "inmobiliaria",
                            id, "Inmobiliaria modificada: " + nombre, AuthUtil.getIp(request));
                    AuthUtil.setMensajeExito(request.getSession(), "Inmobiliaria actualizada correctamente.");
                } else {
                    AuthUtil.setMensajeError(request.getSession(), "No se pudo actualizar la inmobiliaria.");
                }
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/inmobiliarias");
    }

    private int parsearId(String valor) {
        try {
            return valor != null ? Integer.parseInt(valor) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}