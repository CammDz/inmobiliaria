package com.inmobiliaria.dao;

import com.inmobiliaria.model.TokenRecuperacion;
import com.inmobiliaria.util.DatabaseConnection;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

/**
 * Acceso a datos de los tokens de recuperación de contraseña.
 *
 * <p>El token se genera con {@link SecureRandom} (64 caracteres hex,
 * equivalente a 256 bits) y se almacena HASHEADO con SHA-256 para que,
 * aunque se filtre la base de datos, el token nunca viaje «en claro»
 * por el almacenamiento.</p>
 */
public class RecuperacionDAO {

    private static final int VALIDEZ_HORAS = 1;
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String HEX = "0123456789abcdef";

    /**
     * Genera un token nuevo para el usuario y lo guarda en base de datos.
     * Antes de insertar invalida los tokens previos del usuario (de un solo uso).
     *
     * @return el token generado (texto claro para el enlace) o {@code null}
     *         si el usuario no existe.
     */
    public String crearToken(int idUsuario) {
        String tokenClaro = generarToken();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, VALIDEZ_HORAS);
        Timestamp expira = new Timestamp(cal.getTimeInMillis());

        String sha = sha256(tokenClaro);

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Invalida tokens anteriores del mismo usuario.
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM token_recuperacion WHERE id_usuario = ?")) {
                del.setInt(1, idUsuario);
                del.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO token_recuperacion (id_usuario, token, expira_en, usado) VALUES (?, ?, ?, 0)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, idUsuario);
                ps.setString(2, sha);
                ps.setTimestamp(3, expira);
                ps.executeUpdate();
            }
            return tokenClaro;
        } catch (SQLException e) {
            System.err.println("Error en crearToken: " + e.getMessage());
            return null;
        }
    }

    /**
     * Busca un token por su valor en claro y valida que no esté usado ni vencido.
     *
     * @return el id del usuario propietario o -1 si el token no es válido.
     */
    public int validarToken(String tokenClaro) {
        if (tokenClaro == null || tokenClaro.trim().isEmpty()) {
            return -1;
        }
        String sha = sha256(tokenClaro.trim());
        String sql = "SELECT id_usuario FROM token_recuperacion "
                   + "WHERE token = ? AND usado = 0 AND expira_en > NOW()";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_usuario");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en validarToken: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Marca el token como usado (de un solo uso). Operación idempotente.
     */
    public boolean marcarUsado(String tokenClaro) {
        if (tokenClaro == null) {
            return false;
        }
        String sha = sha256(tokenClaro);
        String sql = "UPDATE token_recuperacion SET usado = 1 WHERE token = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sha);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en marcarUsado: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // Utilidades internas
    // ============================================================

    private static String generarToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(HEX.charAt((b >> 4) & 0xF)).append(HEX.charAt(b & 0xF));
        }
        return sb.toString();
    }

    private static String sha256(String texto) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(texto.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(HEX.charAt((b >> 4) & 0xF)).append(HEX.charAt(b & 0xF));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    /** Útil para limpiar tokens vencidos (opcional). */
    public boolean eliminarExpirados() {
        String sql = "DELETE FROM token_recuperacion WHERE expira_en <= NOW() OR usado = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en eliminarExpirados: " + e.getMessage());
            return false;
        }
    }
}