package com.inmobiliaria.controller.agente;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.CitaDAO;
import com.inmobiliaria.model.Cita;
import com.inmobiliaria.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/agente/citas")
public class CitasAgenteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CitaDAO citaDAO = new CitaDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idAgente = AuthUtil.getUsuarioId(request.getSession(false));
        String accion = request.getParameter("accion");

        if ("aprobar".equals(accion)) {
            cambiarEstado(request, response, idAgente, "APROBADA");
            return;
        }
        if ("rechazar".equals(accion)) {
            cambiarEstado(request, response, idAgente, "RECHAZADA");
            return;
        }
        if ("cancelar".equals(accion)) {
            cambiarEstado(request, response, idAgente, "CANCELADA");
            return;
        }

        request.setAttribute("citas", citaDAO.listarPorAgente(idAgente));
        request.getRequestDispatcher("/agente/citas.jsp").forward(request, response);
    }

    private void cambiarEstado(HttpServletRequest request, HttpServletResponse response,
                               int idAgente, String estado) throws IOException {
        int id = parsearId(request.getParameter("id"));
        Cita cita = citaDAO.buscarPorId(id);
        if (cita != null && citaDAO.perteneceAPropiedadDelAgente(id, idAgente)) {
            if (citaDAO.actualizarEstado(id, estado)) {
                auditoriaDAO.registrar(idAgente, "CAMBIO DE ESTADO DE CITA", "cita", id,
                        "Cita #" + id + " -> " + estado, AuthUtil.getIp(request));
                AuthUtil.setMensajeExito(request.getSession(),
                        "Cita actualizada a: " + estado + ".");
            } else {
                AuthUtil.setMensajeError(request.getSession(), "No se pudo actualizar la cita.");
            }
        } else {
            AuthUtil.setMensajeError(request.getSession(),
                    "No tiene permisos para gestionar esta cita.");
        }
        response.sendRedirect(request.getContextPath() + "/agente/citas");
    }

    private int parsearId(String valor) {
        try {
            return valor != null ? Integer.parseInt(valor) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}