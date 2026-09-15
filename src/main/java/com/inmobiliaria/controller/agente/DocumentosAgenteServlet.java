package com.inmobiliaria.controller.agente;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.DocumentoSolicitudDAO;
import com.inmobiliaria.dao.SolicitudDAO;
import com.inmobiliaria.model.DocumentoSolicitud;
import com.inmobiliaria.model.Solicitud;
import com.inmobiliaria.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/agente/documentos")
public class DocumentosAgenteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final DocumentoSolicitudDAO documentoDAO = new DocumentoSolicitudDAO();
    private final SolicitudDAO solicitudDAO = new SolicitudDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idAgente = AuthUtil.getUsuarioId(request.getSession(false));

        String idSolicitudParam = request.getParameter("idSolicitud");
        List<DocumentoSolicitud> documentos = new ArrayList<>();

        if (idSolicitudParam != null && !idSolicitudParam.isEmpty()) {
            int idSolicitud = parsearId(idSolicitudParam);
            if (solicitudDAO.perteneceAPropiedadDelAgente(idSolicitud, idAgente)) {
                documentos = documentoDAO.listarPorSolicitud(idSolicitud);
                request.setAttribute("idSolicitudFiltro", idSolicitud);
            }
        } else {
            documentos = documentoDAO.listarPorAgente(idAgente);
        }

        List<Solicitud> solicitudesDelAgente = solicitudDAO.listarPorAgente(idAgente);
        request.setAttribute("documentos", documentos);
        request.setAttribute("solicitudes", solicitudesDelAgente);
        request.getRequestDispatcher("/agente/documentos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        int idAgente = AuthUtil.getUsuarioId(request.getSession(false));

        if ("eliminar".equals(accion)) {
            int idDocumento = parsearId(request.getParameter("idDocumento"));
            DocumentoSolicitud doc = documentoDAO.buscarPorId(idDocumento);
            if (doc != null && solicitudDAO.perteneceAPropiedadDelAgente(doc.getIdSolicitud(), idAgente)) {
                documentoDAO.eliminar(idDocumento);
                auditoriaDAO.registrar(idAgente, "ELIMINACIÓN DE DOCUMENTO", "documento_solicitud",
                        idDocumento, "Documento eliminado: " + doc.getNombreArchivo(), AuthUtil.getIp(request));
                AuthUtil.setMensajeExito(request.getSession(), "Documento eliminado correctamente.");
            } else {
                AuthUtil.setMensajeError(request.getSession(), "No se pudo eliminar el documento.");
            }
        }
        String filter = request.getParameter("idSolicitud");
        response.sendRedirect(request.getContextPath() + "/agente/documentos"
                + (filter != null && !filter.trim().isEmpty() ? "?idSolicitud=" + filter : ""));
    }

    private int parsearId(String valor) {
        try {
            return valor != null ? Integer.parseInt(valor) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}