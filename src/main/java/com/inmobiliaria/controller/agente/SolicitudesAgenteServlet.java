package com.inmobiliaria.controller.agente;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.SolicitudDAO;
import com.inmobiliaria.model.Solicitud;
import com.inmobiliaria.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/agente/solicitudes")
public class SolicitudesAgenteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final SolicitudDAO solicitudDAO = new SolicitudDAO();
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
        if ("revision".equals(accion)) {
            cambiarEstado(request, response, idAgente, "EN_REVISION");
            return;
        }

        request.setAttribute("solicitudes", solicitudDAO.listarPorAgente(idAgente));
        request.getRequestDispatcher("/agente/solicitudes.jsp").forward(request, response);
    }

    private void cambiarEstado(HttpServletRequest request, HttpServletResponse response,
                               int idAgente, String estado) throws IOException {
        int id = parsearId(request.getParameter("id"));
        Solicitud solicitud = solicitudDAO.buscarPorId(id);
        if (solicitud == null) {
            AuthUtil.setMensajeError(request.getSession(), "La solicitud no existe.");
            response.sendRedirect(request.getContextPath() + "/agente/solicitudes");
            return;
        }
        // Verificar que la solicitud pertenezca a una propiedad del agente
        if (!solicitudDAO.perteneceAPropiedadDelAgente(id, idAgente)) {
            AuthUtil.setMensajeError(request.getSession(),
                    "No tiene permisos para gestionar esta solicitud.");
            response.sendRedirect(request.getContextPath() + "/agente/solicitudes");
            return;
        }
        if (solicitudDAO.actualizarEstado(id, estado)) {
            auditoriaDAO.registrar(idAgente, "CAMBIO DE ESTADO DE SOLICITUD", "solicitud", id,
                    "Solicitud #" + id + " -> " + estado, AuthUtil.getIp(request));
            String mensaje;
            switch (estado) {
                case "APROBADA": mensaje = "Solicitud aprobada correctamente."; break;
                case "RECHAZADA": mensaje = "Solicitud rechazada."; break;
                default: mensaje = "Solicitud marcada como en revisión.";
            }
            AuthUtil.setMensajeExito(request.getSession(), mensaje);
        } else {
            AuthUtil.setMensajeError(request.getSession(), "No se pudo actualizar la solicitud.");
        }
        response.sendRedirect(request.getContextPath() + "/agente/solicitudes");
    }

    private int parsearId(String valor) {
        try {
            return valor != null ? Integer.parseInt(valor) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}