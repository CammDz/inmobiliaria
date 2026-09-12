package com.inmobiliaria.util;

import java.util.regex.Pattern;

/**
 * Clase de utilidades para validaciones comunes.
 */
public class ValidationUtil {

    private static final Pattern PATTERN_CORREO =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PATTERN_TELEFONO =
            Pattern.compile("^[0-9+ -]{7,20}$");

    private ValidationUtil() {}

    /**
     * Valida un correo electrónico.
     */
    public static boolean esCorreoValido(String correo) {
        return correo != null && PATTERN_CORREO.matcher(correo).matches();
    }

    /**
     * Valida un número de teléfono.
     */
    public static boolean esTelefonoValido(String telefono) {
        return telefono == null || telefono.isEmpty() || PATTERN_TELEFONO.matcher(telefono).matches();
    }

    /**
     * Valida longitud mínima de contraseña (8 caracteres).
     */
    public static boolean esContrasenaValida(String contrasena) {
        return contrasena != null && contrasena.length() >= 4 && contrasena.length() <= 100;
    }

    /**
     * Valida que un campo no esté vacío.
     */
    public static boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    /**
     * Valida un precio positivo.
     */
    public static boolean esPrecioValido(double precio) {
        return precio > 0;
    }

    /**
     * Valida que un número sea positivo.
     */
    public static boolean esNumeroPositivo(double numero) {
        return numero >= 0;
    }

    /**
     * Escapa texto para evitar problemas de XSS al mostrar en JSP.
     */
    public static String escapeHtml(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}