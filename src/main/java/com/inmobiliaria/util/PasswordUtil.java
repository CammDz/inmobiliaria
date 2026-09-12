package com.inmobiliaria.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad para el hash y verificación de contraseñas usando BCrypt.
 * Las contraseñas nunca se almacenan en texto plano.
 */
public class PasswordUtil {

    private PasswordUtil() {}

    /**
     * Genera un hash BCrypt para una contraseña en texto plano.
     */
    public static String hashPassword(String contrasena) {
        return BCrypt.hashpw(contrasena, BCrypt.gensalt(10));
    }

    /**
     * Verifica si una contraseña en texto plano coincide con el hash.
     */
    public static boolean verificarPassword(String contrasena, String hashAlmacenado) {
        if (contrasena == null || hashAlmacenado == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(contrasena, hashAlmacenado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Método main para generar hashes de prueba.
     */
    public static void main(String[] args) {
        System.out.println("Hash para '123456': " + hashPassword("123456"));
        System.out.println("Hash para 'admin123': " + hashPassword("admin123"));
    }
}