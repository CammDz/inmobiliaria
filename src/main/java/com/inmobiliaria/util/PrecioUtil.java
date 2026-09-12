package com.inmobiliaria.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utilidad para formatear precios en pesos colombianos (COP)
 * con separador de miles estilo fincaraiz, por ejemplo "$ 520.000.000".
 */
public final class PrecioUtil {

    private static final NumberFormat FORMATO_MILES = NumberFormat.getNumberInstance(new Locale("es", "CO"));

    static {
        // Solo pesos, sin decimales.
        FORMATO_MILES.setMinimumFractionDigits(0);
        FORMATO_MILES.setMaximumFractionDigits(0);
    }

    private PrecioUtil() {
    }

    /**
     * Formatea un precio en COP, por ejemplo 850000000 -> "$ 850.000.000".
     *
     * @param precio valor numérico del precio
     * @return precio formateado con símbolo de pesos y separador de miles
     */
    public static String colombiano(double precio) {
        return "$ " + FORMATO_MILES.format(precio);
    }

    /**
     * Devuelve el precio como entero sin formato, útil para inputs numéricos
     * (evita notación científica tipo "8.5E8"). Por ejemplo 850000000.0 -> 850000000L.
     *
     * @param precio valor numérico del precio
     * @return precio como long
     */
    public static long entero(double precio) {
        return (long) precio;
    }
}