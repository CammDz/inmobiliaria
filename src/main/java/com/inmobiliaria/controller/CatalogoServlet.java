package com.inmobiliaria.controller;

import com.inmobiliaria.dao.CaracteristicaDAO;
import com.inmobiliaria.dao.CiudadDAO;
import com.inmobiliaria.dao.ImagenPropiedadDAO;
import com.inmobiliaria.dao.PropiedadDAO;
import com.inmobiliaria.dao.TipoPropiedadDAO;
import com.inmobiliaria.model.Propiedad;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/catalogo")
public class CatalogoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final PropiedadDAO propiedadDAO = new PropiedadDAO();
    private final CiudadDAO ciudadDAO = new CiudadDAO();
    private final TipoPropiedadDAO tipoDAO = new TipoPropiedadDAO();
    private final CaracteristicaDAO caracteristicaDAO = new CaracteristicaDAO();
    private final ImagenPropiedadDAO imagenDAO = new ImagenPropiedadDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Leer parámetros de filtro
        String termino = request.getParameter("busqueda");
        String ciudadParam = request.getParameter("ciudad");
        String tipoParam = request.getParameter("tipo");
        String precioMinParam = request.getParameter("precioMin");
        String precioMaxParam = request.getParameter("precioMax");
        String[] caracteristicasParam = request.getParameterValues("caracteristicas");

        Integer idCiudad = parsearEntero(ciudadParam);
        Integer idTipo = parsearEntero(tipoParam);
        Double precioMin = parsearDoble(precioMinParam);
        Double precioMax = parsearDoble(precioMaxParam);

        List<Integer> idCaracteristicas = new ArrayList<>();
        if (caracteristicasParam != null) {
            for (String c : caracteristicasParam) {
                Integer val = parsearEntero(c);
                if (val != null && val > 0) {
                    idCaracteristicas.add(val);
                }
            }
        }

        List<Propiedad> propiedades = propiedadDAO.buscar(termino, idCiudad, idTipo,
                precioMin, precioMax, idCaracteristicas);

        // Obtener imagen principal de cada propiedad
        for (Propiedad p : propiedades) {
            p.setImagenes(imagenDAO.listarPorPropiedad(p.getIdPropiedad()));
            p.setCaracteristicas(caracteristicaDAO.listarPorPropiedad(p.getIdPropiedad()));
        }

        request.setAttribute("propiedades", propiedades);
        request.setAttribute("ciudades", ciudadDAO.listarTodas());
        request.setAttribute("tipos", tipoDAO.listarTodos());
        request.setAttribute("caracteristicas", caracteristicaDAO.listarTodas());

        // Mantener los filtros seleccionados para repoblar el formulario
        request.setAttribute("busqueda", termino);
        request.setAttribute("ciudadSeleccionada", idCiudad);
        request.setAttribute("tipoSeleccionado", idTipo);
        request.setAttribute("precioMin", precioMinParam);
        request.setAttribute("precioMax", precioMaxParam);
        request.setAttribute("caracteristicasSeleccionadas", idCaracteristicas);

        request.getRequestDispatcher("/propiedades/catalogo.jsp").forward(request, response);
    }

    private Integer parsearEntero(String valor) {
        try {
            if (valor != null && !valor.trim().isEmpty()) {
                int num = Integer.parseInt(valor.trim());
                return num > 0 ? num : null;
            }
        } catch (NumberFormatException ignored) {}
        return null;
    }

    private Double parsearDoble(String valor) {
        try {
            if (valor != null && !valor.trim().isEmpty()) {
                double num = Double.parseDouble(valor.trim());
                return num >= 0 ? num : null;
            }
        } catch (NumberFormatException ignored) {}
        return null;
    }
}