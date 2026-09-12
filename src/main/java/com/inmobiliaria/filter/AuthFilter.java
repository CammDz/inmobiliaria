package com.inmobiliaria.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

/**
 * Filtro de autenticación y control de acceso por rol.
 *
 * Protege las rutas:
 *   /admin/*  -> solo usuarios con rol Administrador
 *   /agente/* -> solo usuarios con rol Agente Inmobiliario
 *   /cliente/*-> solo usuarios con rol Cliente
 *
 * Si un usuario no autenticado intenta acceder, se redirige al login.
 * Si un usuario autenticado intenta una ruta sin permiso, se redirige a acceso-denegado.jsp
 */
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Sin inicialización adicional.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String camino = req.getRequestURI().substring(req.getContextPath().length());
        String ruta = req.getServletPath();

        HttpSession session = req.getSession(false);
        boolean autenticado = session != null && session.getAttribute("usuario_id") != null;

        // Si no está autenticado, redirigir al login
        if (!autenticado) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        @SuppressWarnings("unchecked")
        Set<String> roles = (Set<String>) session.getAttribute("usuario_roles");

        // Verificar permiso según la ruta
        if (ruta.startsWith("/admin/")) {
            if (roles == null || !roles.contains("Administrador")) {
                resp.sendRedirect(req.getContextPath() + "/acceso-denegado.jsp");
                return;
            }
        } else if (ruta.startsWith("/agente/")) {
            // El Administrador tiene acceso total, incluidas las rutas de agente.
            if (roles == null
                    || (!roles.contains("Administrador") && !roles.contains("Agente Inmobiliario"))) {
                resp.sendRedirect(req.getContextPath() + "/acceso-denegado.jsp");
                return;
            }
        } else if (ruta.startsWith("/cliente/")) {
            // El Administrador tiene acceso total, incluidas las rutas de cliente.
            if (roles == null
                    || (!roles.contains("Administrador") && !roles.contains("Cliente"))) {
                resp.sendRedirect(req.getContextPath() + "/acceso-denegado.jsp");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Sin acciones de cierre.
    }
}