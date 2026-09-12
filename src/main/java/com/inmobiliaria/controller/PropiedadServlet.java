package com.inmobiliaria.controller;

import com.inmobiliaria.dao.CaracteristicaDAO;
import com.inmobiliaria.dao.FavoritoDAO;
import com.inmobiliaria.dao.ImagenPropiedadDAO;
import com.inmobiliaria.dao.PropiedadDAO;
import com.inmobiliaria.model.Propiedad;
import com.inmobiliaria.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/propiedad")
public class PropiedadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final PropiedadDAO propiedadDAO = new PropiedadDAO();
    private final ImagenPropiedadDAO imagenDAO = new ImagenPropiedadDAO();
    private final CaracteristicaDAO caracteristicaDAO = new CaracteristicaDAO();
    private final FavoritoDAO favoritoDAO = new FavoritoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Acción para agregar/quitar favorito (solo clientes autenticados)
        String accion = request.getParameter("accion");
        if ("favorito".equals(accion)) {
            manejarFavorito(request, response);
            return;
        }

        int idPropiedad = parsearId(request.getParameter("id"));
        if (idPropiedad <= 0) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        Propiedad propiedad = propiedadDAO.buscarPorId(idPropiedad);
        if (propiedad == null || !propiedad.isActiva()) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        propiedad.setImagenes(imagenDAO.listarPorPropiedad(idPropiedad));
        propiedad.setCaracteristicas(caracteristicaDAO.listarPorPropiedad(idPropiedad));

        request.setAttribute("propiedad", propiedad);

        // Información para clientes autenticados
        HttpSession session = request.getSession(false);
        boolean autenticado = AuthUtil.estaAutenticado(session);
        request.setAttribute("autenticado", autenticado);
        if (autenticado) {
            Integer idUsuario = AuthUtil.getUsuarioId(session);
            request.setAttribute("esFavorito", favoritoDAO.esFavorito(idUsuario, idPropiedad));
            request.setAttribute("esCliente", AuthUtil.tieneRol(session, "Cliente"));
        }

        request.getRequestDispatcher("/propiedades/detalle.jsp").forward(request, response);
    }

    private void manejarFavorito(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (!AuthUtil.estaAutenticado(session) || !AuthUtil.tieneRol(session, "Cliente")) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        int idPropiedad = parsearId(request.getParameter("idPropiedad"));
        int idCliente = AuthUtil.getUsuarioId(session);
        String modo = request.getParameter("modo");

        if ("agregar".equals(modo)) {
            boolean ok = favoritoDAO.agregar(idCliente, idPropiedad);
            if (ok) {
                AuthUtil.setMensajeExito(session, "Propiedad agregada a favoritos.");
            } else {
                AuthUtil.setMensajeError(session, "La propiedad ya está en sus favoritos.");
            }
        } else if ("quitar".equals(modo)) {
            favoritoDAO.quitar(idCliente, idPropiedad);
            AuthUtil.setMensajeExito(session, "Propiedad eliminada de favoritos.");
        }
        response.sendRedirect(request.getContextPath() + "/propiedad?id=" + idPropiedad);
    }

    private int parsearId(String valor) {
        try {
            return valor != null ? Integer.parseInt(valor) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}