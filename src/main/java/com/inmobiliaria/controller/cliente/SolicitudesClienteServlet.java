package com.inmobiliaria.controller.cliente;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.DocumentoSolicitudDAO;
import com.inmobiliaria.dao.PropiedadDAO;
import com.inmobiliaria.dao.SolicitudDAO;
import com.inmobiliaria.model.DocumentoSolicitud;
import com.inmobiliaria.model.Propiedad;
import com.inmobiliaria.model.Solicitud;
import com.inmobiliaria.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.List;

@WebServlet("/cliente/solicitudes")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class SolicitudesClienteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final SolicitudDAO solicitudDAO = new SolicitudDAO();
    private final PropiedadDAO propiedadDAO = new PropiedadDAO();
    private final DocumentoSolicitudDAO documentoDAO = new DocumentoSolicitudDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idCliente = AuthUtil.getUsuarioId(request.getSession(false));

        request.setAttribute("solicitudes", solicitudDAO.listarPorCliente(idCliente));
        request.getRequestDispatcher("/cliente/solicitudes.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idCliente = AuthUtil.getUsuarioId(request.getSession(false));
        String accion = request.getParameter("accion");

        if ("crear".equals(accion)) {
            crearSolicitud(request, response, idCliente);
        } else if ("subirDocumento".equals(accion)) {
            subirDocumento(request, response, idCliente);
        } else {
            response.sendRedirect(request.getContextPath() + "/cliente/solicitudes");
        }
    }

    private void crearSolicitud(HttpServletRequest request, HttpServletResponse response, int idCliente)
            throws ServletException, IOException {
        int idPropiedad = parsearId(request.getParameter("idPropiedad"));
        String tipo = request.getParameter("tipo");
        String observaciones = request.getParameter("observaciones");
        double montoOfrecido = parsearDoble(request.getParameter("montoOfrecido"));

        Propiedad propiedad = propiedadDAO.buscarPorId(idPropiedad);
        if (propiedad == null || !propiedad.isActiva()) {
            AuthUtil.setMensajeError(request.getSession(), "La propiedad no existe o no está disponible.");
            respuestaCerrar(request, response);
            return;
        }
        if (tipo == null || !(tipo.equals("COMPRA") || tipo.equals("ALQUILER"))) {
            AuthUtil.setMensajeError(request.getSession(), "Seleccione un tipo de operación válido.");
            respuestaCerrar(request, response);
            return;
        }
        if (solicitudDAO.existeSolicitudPendiente(idCliente, idPropiedad)) {
            AuthUtil.setMensajeError(request.getSession(),
                    "Ya tiene una solicitud pendiente o en revisión para esta propiedad.");
            respuestaCerrar(request, response);
            return;
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setIdCliente(idCliente);
        solicitud.setIdPropiedad(idPropiedad);
        solicitud.setTipo(tipo);
        solicitud.setEstado("PENDIENTE");
        solicitud.setObservaciones(observaciones);
        solicitud.setMontoOfrecido(montoOfrecido);

        if (solicitudDAO.insertar(solicitud)) {
            auditoriaDAO.registrar(idCliente, "CREACIÓN DE SOLICITUD", "solicitud", solicitud.getIdSolicitud(),
                    "Solicitud de " + tipo + " para la propiedad: " + propiedad.getTitulo(), AuthUtil.getIp(request));
            AuthUtil.setMensajeExito(request.getSession(),
                    "Su solicitud fue creada. Los documentos pueden subirse desde el módulo de solicitudes.");
        } else {
            AuthUtil.setMensajeError(request.getSession(), "No se pudo crear la solicitud. Intente nuevamente.");
        }
        respuestaCerrar(request, response);
    }

    private void subirDocumento(HttpServletRequest request, HttpServletResponse response, int idCliente)
            throws ServletException, IOException {
        int idSolicitud = parsearId(request.getParameter("idSolicitud"));
        Solicitud solicitud = solicitudDAO.buscarPorId(idSolicitud);

        if (solicitud == null || solicitud.getIdCliente() != idCliente) {
            AuthUtil.setMensajeError(request.getSession(), "La solicitud no existe o no le pertenece.");
            response.sendRedirect(request.getContextPath() + "/cliente/solicitudes");
            return;
        }

        String tipoDocumento = request.getParameter("tipoDocumento");
        Part parteArchivo = request.getPart("archivo");

        if (parteArchivo == null || parteArchivo.getSize() == 0) {
            AuthUtil.setMensajeError(request.getSession(), "Debe seleccionar un archivo para subir.");
            response.sendRedirect(request.getContextPath() + "/cliente/solicitudes");
            return;
        }

        // Validar tipo MIME permitido (seguridad: no aceptar ejecutables ni scripts)
        String contentType = parteArchivo.getContentType();
        if (contentType == null || !esTipoArchivoPermitido(contentType)) {
            AuthUtil.setMensajeError(request.getSession(),
                    "Tipo de archivo no permitido. Use PDF, JPG, PNG o DOC/DOCX.");
            response.sendRedirect(request.getContextPath() + "/cliente/solicitudes");
            return;
        }

        // Obtener nombre original del archivo
        String nombreArchivo = obtenerNombreArchivo(parteArchivo);
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            nombreArchivo = "documento_" + System.currentTimeMillis() + "_" + idSolicitud;
        }

        // Nombre único para evitar colisiones
        String nombreUnico = System.currentTimeMillis() + "_" + idCliente + "_" + nombreArchivo.replaceAll("[^a-zA-Z0-9.\\-_]", "_");

        DocumentoSolicitud doc = new DocumentoSolicitud();
        doc.setIdSolicitud(idSolicitud);
        doc.setNombreArchivo(nombreArchivo);
        doc.setRutaArchivo("documentos/" + nombreUnico);
        doc.setTipoDocumento(tipoDocumento);
        doc.setContenido(parteArchivo.getInputStream().readAllBytes());

        if (documentoDAO.insertar(doc)) {
            auditoriaDAO.registrar(idCliente, "SUBDIDA DE DOCUMENTO", "documento_solicitud",
                    doc.getIdDocumento(), "Documento subido a la solicitud #" + idSolicitud, AuthUtil.getIp(request));
            AuthUtil.setMensajeExito(request.getSession(), "Documento subido correctamente.");
        } else {
            AuthUtil.setMensajeError(request.getSession(), "No se pudo guardar el documento.");
        }
        response.sendRedirect(request.getContextPath() + "/cliente/solicitudes");
    }

    /**
     * Redirige de vuelta a la página de detalle de la solicitud o al panel del cliente.
     */
    private void respuestaCerrar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/cliente/solicitudes");
    }

    private String obtenerNombreArchivo(Part parte) {
        for (String cadena : parte.getHeader("content-disposition").split(";")) {
            if (cadena.trim().startsWith("filename")) {
                return cadena.substring(cadena.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private int parsearId(String valor) {
        try {
            return valor != null ? Integer.parseInt(valor) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Tipos MIME permitidos para documentos adjuntos.
     */
    private boolean esTipoArchivoPermitido(String contentType) {
        return "application/pdf".equalsIgnoreCase(contentType)
                || "image/jpeg".equalsIgnoreCase(contentType)
                || "image/jpg".equalsIgnoreCase(contentType)
                || "image/png".equalsIgnoreCase(contentType)
                || "image/gif".equalsIgnoreCase(contentType)
                || "application/msword".equalsIgnoreCase(contentType)
                || "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equalsIgnoreCase(contentType);
    }

    private double parsearDoble(String valor) {
        try {
            if (valor != null && !valor.trim().isEmpty()) {
                return Double.parseDouble(valor.trim());
            }
        } catch (NumberFormatException ignored) {}
        return 0;
    }
}