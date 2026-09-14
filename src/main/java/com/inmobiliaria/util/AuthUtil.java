package com.inmobiliaria.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Set;

/**
 * Utilidades para manejar sesiones y permisos.
 */
public class AuthUtil {

    private static final String ATTR_USUARIO_ID = "usuario_id";
    private static final String ATTR_USUARIO_NOMBRE = "usuario_nombre";
    private static final String ATTR_USUARIO_CORREO = "usuario_correo";
    private static final String ATTR_USUARIO_ROLES = "usuario_roles";

    private AuthUtil() {}

    public static void guardarSesion(HttpSession session, int idUsuario, String nombre,
                                     String correo, Set<String> roles) {
        session.setAttribute(ATTR_USUARIO_ID, idUsuario);
        session.setAttribute(ATTR_USUARIO_NOMBRE, nombre);
        session.setAttribute(ATTR_USUARIO_CORREO, correo);
        session.setAttribute(ATTR_USUARIO_ROLES, roles);
    }

    public static boolean estaAutenticado(HttpSession session) {
        return session != null && session.getAttribute(ATTR_USUARIO_ID) != null;
    }

    public static Integer getUsuarioId(HttpSession session) {
        if (session == null) return null;
        Object val = session.getAttribute(ATTR_USUARIO_ID);
        return val instanceof Integer ? (Integer) val : null;
    }

    public static String getUsuarioNombre(HttpSession session) {
        if (session == null) return null;
        return (String) session.getAttribute(ATTR_USUARIO_NOMBRE);
    }

    @SuppressWarnings("unchecked")
    public static Set<String> getRoles(HttpSession session) {
        if (session == null) return null;
        return (Set<String>) session.getAttribute(ATTR_USUARIO_ROLES);
    }

    public static boolean tieneRol(HttpSession session, String rol) {
        Set<String> roles = getRoles(session);
        return roles != null && roles.contains(rol);
    }

    public static void cerrarSesion(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    /**
     * Establece un mensaje de éxito para mostrar en la siguiente página.
     */
    public static void setMensajeExito(HttpSession session, String mensaje) {
        session.setAttribute("mensaje_exito", mensaje);
    }

    /**
     * Establece un mensaje de error para mostrar en la siguiente página.
     */
    public static void setMensajeError(HttpSession session, String mensaje) {
        session.setAttribute("mensaje_error", mensaje);
    }

    /**
     * Obtiene y elimina el mensaje de éxito pendiente.
     */
    public static String consumeMensajeExito(HttpSession session) {
        String msg = (String) session.getAttribute("mensaje_exito");
        session.removeAttribute("mensaje_exito");
        return msg;
    }

    /**
     * Obtiene y elimina el mensaje de error pendiente.
     */
    public static String consumeMensajeError(HttpSession session) {
        String msg = (String) session.getAttribute("mensaje_error");
        session.removeAttribute("mensaje_error");
        return msg;
    }

    /**
     * Obtiene la IP del cliente.
     */
    public static String getIp(HttpServletRequest request) {
        String ip = null;
        String fwd = request.getHeader("X-Forwarded-For");
        if (fwd != null && !fwd.trim().isEmpty()) {
            ip = fwd.split(",")[0].trim();
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
}