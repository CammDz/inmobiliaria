package com.inmobiliaria.controller.cliente;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.CitaDAO;
import com.inmobiliaria.dao.PropiedadDAO;
import com.inmobiliaria.model.Cita;
import com.inmobiliaria.model.Propiedad;
import com.inmobiliaria.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;

@WebServlet("/cliente/citas")
public class CitasClienteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CitaDAO citaDAO = new CitaDAO();
    private final PropiedadDAO propiedadDAO = new PropiedadDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idCliente = AuthUtil.getUsuarioId(request.getSession(false));

        String accion = request.getParameter("accion");
        if ("cancelar".equals(accion)) {
            int idCita = parsearId(request.getParameter("id"));
            Cita cita = citaDAO.buscarPorId(idCita);
            if (cita != null && cita.getIdCliente() == idCliente) {
                if (citaDAO.cancelar(idCita)) {
                    auditoriaDAO.registrar(idCliente, "CANCELACIÓN DE CITA", "cita", idCita,
                            "Cliente canceló la cita", AuthUtil.getIp(request));
                    AuthUtil.setMensajeExito(request.getSession(), "Cita cancelada correctamente.");
                } else {
                    AuthUtil.setMensajeError(request.getSession(), "No se pudo cancelar la cita.");
                }
            }
            response.sendRedirect(request.getContextPath() + "/cliente/citas");
            return;
        }

        request.setAttribute("citas", citaDAO.listarPorCliente(idCliente));
        request.getRequestDispatcher("/cliente/citas.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idCliente = AuthUtil.getUsuarioId(request.getSession(false));
        String accion = request.getParameter("accion");

        if ("crear".equals(accion)) {
            int idPropiedad = parsearId(request.getParameter("idPropiedad"));
            String fechaStr = request.getParameter("fecha");
            String horaStr = request.getParameter("hora");
            String observaciones = request.getParameter("observaciones");

            Propiedad propiedad = propiedadDAO.buscarPorId(idPropiedad);
            if (propiedad == null || !propiedad.isActiva()) {
                AuthUtil.setMensajeError(request.getSession(), "La propiedad no existe o no está disponible.");
                response.sendRedirect(request.getContextPath() + "/catalogo");
                return;
            }

            // Validar fecha y hora
            Date fecha = null;
            Time hora = null;
            try {
                fecha = Date.valueOf(fechaStr);
            } catch (IllegalArgumentException e) {
                AuthUtil.setMensajeError(request.getSession(), "La fecha ingresada no es válida.");
                response.sendRedirect(request.getContextPath() + "/propiedad?id=" + idPropiedad);
                return;
            }
            try {
                hora = Time.valueOf(horaStr + ":00");
            } catch (IllegalArgumentException e) {
                AuthUtil.setMensajeError(request.getSession(), "La hora ingresada no es válida.");
                response.sendRedirect(request.getContextPath() + "/propiedad?id=" + idPropiedad);
                return;
            }

            // Evitar citas duplicadas
            if (citaDAO.existeCitaDuplicada(idCliente, idPropiedad, fecha, hora)) {
                AuthUtil.setMensajeError(request.getSession(),
                        "Ya tiene una cita pendiente o aprobada para esta propiedad en esa fecha y hora.");
                response.sendRedirect(request.getContextPath() + "/propiedad?id=" + idPropiedad);
                return;
            }

            Cita cita = new Cita();
            cita.setIdCliente(idCliente);
            cita.setIdPropiedad(idPropiedad);
            cita.setFecha(fecha);
            cita.setHora(hora);
            cita.setEstado("PENDIENTE");
            cita.setObservaciones(observaciones);

            if (citaDAO.insertar(cita)) {
                auditoriaDAO.registrar(idCliente, "CREACIÓN DE CITA", "cita", cita.getIdCita(),
                        "Solicitud de cita para la propiedad: " + propiedad.getTitulo(), AuthUtil.getIp(request));
                AuthUtil.setMensajeExito(request.getSession(),
                        "Cita solicitada. El agente la revisará y le dará respuesta.");
            } else {
                AuthUtil.setMensajeError(request.getSession(), "No se pudo crear la cita. Intente nuevamente.");
            }
            response.sendRedirect(request.getContextPath() + "/cliente/citas");
        } else {
            response.sendRedirect(request.getContextPath() + "/cliente/citas");
        }
    }

    private int parsearId(String valor) {
        try {
            return valor != null ? Integer.parseInt(valor) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}