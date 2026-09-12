package com.inmobiliaria.controller.agente;

import com.inmobiliaria.dao.AuditoriaDAO;
import com.inmobiliaria.dao.CaracteristicaDAO;
import com.inmobiliaria.dao.CiudadDAO;
import com.inmobiliaria.dao.ImagenPropiedadDAO;
import com.inmobiliaria.dao.InmobiliariaDAO;
import com.inmobiliaria.dao.PropiedadDAO;
import com.inmobiliaria.dao.TipoPropiedadDAO;
import com.inmobiliaria.model.ImagenPropiedad;
import com.inmobiliaria.model.Propiedad;
import com.inmobiliaria.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/agente/propiedades")
public class PropiedadesAgenteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final PropiedadDAO propiedadDAO = new PropiedadDAO();
    private final CiudadDAO ciudadDAO = new CiudadDAO();
    private final TipoPropiedadDAO tipoDAO = new TipoPropiedadDAO();
    private final InmobiliariaDAO inmobiliariaDAO = new InmobiliariaDAO();
    private final CaracteristicaDAO caracteristicaDAO = new CaracteristicaDAO();
    private final ImagenPropiedadDAO imagenDAO = new ImagenPropiedadDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        int idAgente = AuthUtil.getUsuarioId(request.getSession(false));

        if ("crear".equals(accion)) {
            request.setAttribute("propiedad", null);
            cargarCatalogos(request);
            request.getRequestDispatcher("/agente/propiedad-form.jsp").forward(request, response);
            return;
        }
        if ("editar".equals(accion)) {
            int id = parsearId(request.getParameter("id"));
            Propiedad p = propiedadDAO.buscarPorId(id);
            if (p != null && p.getIdAgente() == idAgente) {
                p.setCaracteristicas(caracteristicaDAO.listarPorPropiedad(id));
                request.setAttribute("propiedad", p);
            } else {
                AuthUtil.setMensajeError(request.getSession(),
                        "No tiene permisos para editar esta propiedad.");
                response.sendRedirect(request.getContextPath() + "/agente/propiedades");
                return;
            }
            cargarCatalogos(request);
            request.getRequestDispatcher("/agente/propiedad-form.jsp").forward(request, response);
            return;
        }
        if ("desactivar".equals(accion)) {
            int id = parsearId(request.getParameter("id"));
            Propiedad p = propiedadDAO.buscarPorId(id);
            if (p != null && p.getIdAgente() == idAgente) {
                if (propiedadDAO.desactivar(id)) {
                    auditoriaDAO.registrar(idAgente, "DESACTIVACIÓN DE PROPIEDAD", "propiedad", id,
                            "Propiedad desactivada: " + p.getTitulo(), AuthUtil.getIp(request));
                    AuthUtil.setMensajeExito(request.getSession(), "Propiedad desactivada correctamente.");
                } else {
                    AuthUtil.setMensajeError(request.getSession(), "No se pudo desactivar la propiedad.");
                }
            }
            response.sendRedirect(request.getContextPath() + "/agente/propiedades");
            return;
        }
        if ("activar".equals(accion)) {
            int id = parsearId(request.getParameter("id"));
            Propiedad p = propiedadDAO.buscarPorId(id);
            if (p != null && p.getIdAgente() == idAgente) {
                if (propiedadDAO.activar(id)) {
                    auditoriaDAO.registrar(idAgente, "ACTIVACIÓN DE PROPIEDAD", "propiedad", id,
                            "Propiedad activada: " + p.getTitulo(), AuthUtil.getIp(request));
                    AuthUtil.setMensajeExito(request.getSession(), "Propiedad activada correctamente.");
                }
            }
            response.sendRedirect(request.getContextPath() + "/agente/propiedades");
            return;
        }
        if ("imagenes".equals(accion)) {
            int id = parsearId(request.getParameter("id"));
            Propiedad p = propiedadDAO.buscarPorId(id);
            if (p != null && p.getIdAgente() == idAgente) {
                request.setAttribute("propiedad", p);
                request.setAttribute("imagenes", imagenDAO.listarPorPropiedad(id));
                request.getRequestDispatcher("/agente/imagenes.jsp").forward(request, response);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/agente/propiedades");
            return;
        }
        if ("eliminarImagen".equals(accion)) {
            int idImagen = parsearId(request.getParameter("idImagen"));
            ImagenPropiedad img = imagenDAO.buscarPorId(idImagen);
            if (img != null) {
                Propiedad p = propiedadDAO.buscarPorId(img.getIdPropiedad());
                if (p != null && p.getIdAgente() == idAgente) {
                    imagenDAO.desactivar(idImagen);
                    AuthUtil.setMensajeExito(request.getSession(), "Imagen eliminada correctamente.");
                } else {
                    AuthUtil.setMensajeError(request.getSession(), "No tiene permisos para eliminar esta imagen.");
                }
            }
            String idProp = request.getParameter("idPropiedad");
            response.sendRedirect(request.getContextPath() + "/agente/propiedades?accion=imagenes&id=" + idProp);
            return;
        }

        // listar
        List<Propiedad> propiedades = propiedadDAO.listarPorAgente(idAgente);
        for (Propiedad p : propiedades) {
            p.setImagenes(imagenDAO.listarPorPropiedad(p.getIdPropiedad()));
        }
        request.setAttribute("propiedades", propiedades);
        request.getRequestDispatcher("/agente/propiedades.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        int idAgente = AuthUtil.getUsuarioId(request.getSession(false));

        if ("guardarNuevo".equals(accion)) {
            guardarNuevo(request, response, idAgente);
        } else if ("guardarEdicion".equals(accion)) {
            guardarEdicion(request, response, idAgente);
        } else if ("guardarImagenes".equals(accion)) {
            guardarImagenes(request, response, idAgente);
        } else if ("eliminarImagen".equals(accion)) {
            int idImagen = parsearId(request.getParameter("idImagen"));
            ImagenPropiedad img = imagenDAO.buscarPorId(idImagen);
            if (img != null) {
                Propiedad p = propiedadDAO.buscarPorId(img.getIdPropiedad());
                if (p != null && p.getIdAgente() == idAgente) {
                    imagenDAO.desactivar(idImagen);
                    AuthUtil.setMensajeExito(request.getSession(), "Imagen eliminada correctamente.");
                }
            }
            String ref = request.getParameter("idPropiedad");
            response.sendRedirect(request.getContextPath() + "/agente/propiedades?accion=imagenes&id=" + ref);
        }
    }

    private void guardarNuevo(HttpServletRequest request, HttpServletResponse response, int idAgente)
            throws IOException {
        Propiedad p = leerFormulario(request);
        p.setIdAgente(idAgente);
        p.setActiva(true);

        int nuevoId = propiedadDAO.insertar(p);
        if (nuevoId == -1) {
            AuthUtil.setMensajeError(request.getSession(),
                    "No se pudo crear la propiedad: la matrícula inmobiliaria ya está registrada.");
        } else {
            // Asociar características N:M
            String[] caracteristicas = request.getParameterValues("caracteristicas");
            if (caracteristicas != null) {
                for (String c : caracteristicas) {
                    caracteristicaDAO.asignarAPropiedad(nuevoId, parsearId(c));
                }
            }
            // Imagen principal por URL (opcional)
            String urlImagen = request.getParameter("urlImagen");
            if (urlImagen != null && !urlImagen.trim().isEmpty()) {
                ImagenPropiedad img = new ImagenPropiedad();
                img.setIdPropiedad(nuevoId);
                img.setUrlImagen(urlImagen.trim());
                img.setEsPrincipal(true);
                img.setActiva(true);
                imagenDAO.insertar(img);
            }
            auditoriaDAO.registrar(AuthUtil.getUsuarioId(request.getSession(false)),
                    "CREACIÓN DE PROPIEDAD", "propiedad", nuevoId,
                    "Propiedad creada: " + p.getTitulo(), AuthUtil.getIp(request));
            AuthUtil.setMensajeExito(request.getSession(), "Propiedad creada correctamente.");
        }
        response.sendRedirect(request.getContextPath() + "/agente/propiedades");
    }

    private void guardarEdicion(HttpServletRequest request, HttpServletResponse response, int idAgente)
            throws IOException {
        int id = parsearId(request.getParameter("id"));
        Propiedad existente = propiedadDAO.buscarPorId(id);
        if (existente == null || existente.getIdAgente() != idAgente) {
            AuthUtil.setMensajeError(request.getSession(), "No tiene permisos para modificar esta propiedad.");
            response.sendRedirect(request.getContextPath() + "/agente/propiedades");
            return;
        }

        Propiedad p = leerFormulario(request);
        p.setIdPropiedad(id);
        p.setIdAgente(idAgente);
        p.setActiva(request.getParameter("activa") != null);
        // La matrícula es un identificador único inmutable: se conserva la del
        // registro original aunque un cliente altere el campo del formulario.
        p.setMatriculaInmobiliaria(existente.getMatriculaInmobiliaria());
        p.setFechaRegistro(existente.getFechaRegistro());

        if (propiedadDAO.actualizar(p)) {
            // Reemplazar características N:M
            caracteristicaDAO.eliminarDePropiedad(id);
            String[] caracteristicas = request.getParameterValues("caracteristicas");
            if (caracteristicas != null) {
                for (String c : caracteristicas) {
                    caracteristicaDAO.asignarAPropiedad(id, parsearId(c));
                }
            }
            auditoriaDAO.registrar(AuthUtil.getUsuarioId(request.getSession(false)),
                    "MODIFICACIÓN DE PROPIEDAD", "propiedad", id,
                    "Propiedad modificada: " + p.getTitulo(), AuthUtil.getIp(request));
            AuthUtil.setMensajeExito(request.getSession(), "Propiedad actualizada correctamente.");
        } else {
            AuthUtil.setMensajeError(request.getSession(), "No se pudo actualizar la propiedad.");
        }
        response.sendRedirect(request.getContextPath() + "/agente/propiedades");
    }

    private void guardarImagenes(HttpServletRequest request, HttpServletResponse response, int idAgente)
            throws IOException {
        int id = parsearId(request.getParameter("idPropiedad"));
        Propiedad p = propiedadDAO.buscarPorId(id);
        if (p == null || p.getIdAgente() != idAgente) {
            response.sendRedirect(request.getContextPath() + "/agente/propiedades");
            return;
        }

        String urlImagen = request.getParameter("urlImagen");
        String principal = request.getParameter("principal");
        if (urlImagen != null && !urlImagen.trim().isEmpty()) {
            ImagenPropiedad img = new ImagenPropiedad();
            img.setIdPropiedad(id);
            img.setUrlImagen(urlImagen.trim());
            img.setEsPrincipal("1".equals(principal));
            img.setActiva(true);
            imagenDAO.insertar(img);
            AuthUtil.setMensajeExito(request.getSession(), "Imagen agregada correctamente.");
        } else {
            AuthUtil.setMensajeError(request.getSession(), "Debe indicar la URL de la imagen.");
        }
        response.sendRedirect(request.getContextPath() + "/agente/propiedades?accion=imagenes&id=" + id);
    }

    private Propiedad leerFormulario(HttpServletRequest request) {
        Propiedad p = new Propiedad();
        p.setTitulo(request.getParameter("titulo"));
        p.setDescripcion(request.getParameter("descripcion"));
        p.setPrecio(parsearDoble(request.getParameter("precio")));
        p.setIdCiudad(parsearId(request.getParameter("idCiudad")));
        p.setIdTipo(parsearId(request.getParameter("idTipo")));
        p.setIdInmobiliaria(parsearId(request.getParameter("idInmobiliaria")));
        p.setMatriculaInmobiliaria(request.getParameter("matricula") != null
                ? request.getParameter("matricula") : "");
        p.setEstado(request.getParameter("estado") != null ? request.getParameter("estado") : "DISPONIBLE");
        p.setTipoOperacion(request.getParameter("tipoOperacion") != null
                ? request.getParameter("tipoOperacion") : "VENTA");
        p.setDireccion(request.getParameter("direccion"));
        p.setAreaM2(parsearDoble(request.getParameter("areaM2")));
        p.setHabitaciones(parsearId(request.getParameter("habitaciones")));
        p.setBanos(parsearId(request.getParameter("banos")));
        p.setParqueaderos(parsearId(request.getParameter("parqueaderos")));
        return p;
    }

    private void cargarCatalogos(HttpServletRequest request) {
        request.setAttribute("ciudades", ciudadDAO.listarTodas());
        request.setAttribute("tipos", tipoDAO.listarTodos());
        request.setAttribute("inmobiliarias", inmobiliariaDAO.listarTodas());
        request.setAttribute("todasCaracteristicas", caracteristicaDAO.listarTodas());
    }

    private int parsearId(String valor) {
        try {
            return valor != null ? Integer.parseInt(valor) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
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