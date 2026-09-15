package com.inmobiliaria.controller;

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
import java.io.OutputStream;
import java.util.Set;

/**
 * Sirve el contenido binario de un documento de solicitud (almacenado en BD),
 * verificando que quien lo pide tenga permiso sobre la solicitud asociada.
 * Rutas: /documentos/archivo?id=ID
 */
@WebServlet("/documentos/archivo")
public class DocumentoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final DocumentoSolicitudDAO documentoDAO = new DocumentoSolicitudDAO();
    private final SolicitudDAO solicitudDAO = new SolicitudDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer idUsuario = AuthUtil.getUsuarioId(request.getSession(false));
        if (idUsuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        Set<String> roles = AuthUtil.getRoles(request.getSession(false));

        int idDocumento = parsearId(request.getParameter("id"));
        DocumentoSolicitud doc = documentoDAO.buscarPorId(idDocumento);
        if (doc == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Documento no encontrado.");
            return;
        }

        boolean permiso = false;
        if (roles != null && roles.contains("Administrador")) {
            permiso = true;
        } else if (roles != null && roles.contains("Agente Inmobiliario")) {
            permiso = solicitudDAO.perteneceAPropiedadDelAgente(doc.getIdSolicitud(), idUsuario);
        } else if (roles != null && roles.contains("Cliente")) {
            Solicitud solicitud = solicitudDAO.buscarPorId(doc.getIdSolicitud());
            permiso = solicitud != null && solicitud.getIdCliente() == idUsuario;
        }
        if (!permiso) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tiene permiso para ver este documento.");
            return;
        }

        byte[] contenido = documentoDAO.obtenerContenido(idDocumento);
        if (contenido == null || contenido.length == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "El documento no tiene contenido.");
            return;
        }

        response.setContentType(tipoMime(doc.getNombreArchivo()));
        response.setContentLength(contenido.length);
        response.setHeader("Content-Disposition",
                "inline; filename=\"" + doc.getNombreArchivo().replaceAll("[^a-zA-Z0-9.\\-_]", "_") + "\"");
        try (OutputStream out = response.getOutputStream()) {
            out.write(contenido);
        }
    }

    private String tipoMime(String nombreArchivo) {
        String nombre = nombreArchivo != null ? nombreArchivo.toLowerCase() : "";
        if (nombre.endsWith(".pdf")) return "application/pdf";
        if (nombre.endsWith(".jpg") || nombre.endsWith(".jpeg")) return "image/jpeg";
        if (nombre.endsWith(".png")) return "image/png";
        if (nombre.endsWith(".gif")) return "image/gif";
        if (nombre.endsWith(".doc")) return "application/msword";
        if (nombre.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        return "application/octet-stream";
    }

    private int parsearId(String valor) {
        try {
            return valor != null ? Integer.parseInt(valor) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}