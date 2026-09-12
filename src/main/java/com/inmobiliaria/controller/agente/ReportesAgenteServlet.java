package com.inmobiliaria.controller.agente;

import com.inmobiliaria.dao.ReporteDAO;
import com.inmobiliaria.model.Reporte;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/agente/reportes")
public class ReportesAgenteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ReporteDAO reporteDAO = new ReporteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Reporte> reportes = new ArrayList<>();
        reportes.add(reporteDAO.propiedadesPorCiudad());
        reportes.add(reporteDAO.citasPorEstado());
        reportes.add(reporteDAO.propiedadesPorEstado());
        reportes.add(reporteDAO.solicitudesPorInmobiliaria());
        reportes.add(reporteDAO.propiedadesConCitas());
        reportes.add(reporteDAO.propiedadesPorTipo());
        request.setAttribute("reportes", reportes);
        request.getRequestDispatcher("/agente/reportes.jsp").forward(request, response);
    }
}