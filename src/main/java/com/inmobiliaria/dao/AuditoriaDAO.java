package com.inmobiliaria.dao;

import com.inmobiliaria.model.Auditoria;
import com.inmobiliaria.model.Reporte;
import com.inmobiliaria.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaDAO {

    /**
     * Registra una acción de auditoría.
     */
    public boolean registrar(Integer idUsuario, String accion, String tablaAfectada,
                             Integer idRegistro, String detalles, String ipAddress) {
        String sql = "INSERT INTO auditoria (id_usuario, accion, tabla_afectada, id_registro, detalles, ip_address) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (idUsuario != null) {
                ps.setInt(1, idUsuario);
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setString(2, accion);
            ps.setString(3, tablaAfectada);
            if (idRegistro != null) {
                ps.setInt(4, idRegistro);
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setString(5, detalles);
            ps.setString(6, ipAddress);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en registrar auditoria: " + e.getMessage());
            return false;
        }
    }

    public List<Auditoria> listarTodas() {
        List<Auditoria> lista = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(u.nombre, ' ', u.apellido) AS nombre_usuario "
                   + "FROM auditoria a LEFT JOIN usuario u ON a.id_usuario = u.id_usuario "
                   + "ORDER BY a.fecha_hora DESC LIMIT 200";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en listarTodas: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista la auditoría de un usuario específico.
     */
    public List<Auditoria> listarPorUsuario(int idUsuario) {
        List<Auditoria> lista = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(u.nombre, ' ', u.apellido) AS nombre_usuario "
                   + "FROM auditoria a LEFT JOIN usuario u ON a.id_usuario = u.id_usuario "
                   + "WHERE a.id_usuario = ? ORDER BY a.fecha_hora DESC LIMIT 100";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en listarPorUsuario: " + e.getMessage());
        }
        return lista;
    }

    private Auditoria mapear(ResultSet rs) throws SQLException {
        Auditoria a = new Auditoria();
        a.setIdAuditoria(rs.getInt("id_auditoria"));
        int idUsuario = rs.getInt("id_usuario");
        a.setIdUsuario(rs.wasNull() ? null : idUsuario);
        a.setAccion(rs.getString("accion"));
        a.setTablaAfectada(rs.getString("tabla_afectada"));
        int idRegistro = rs.getInt("id_registro");
        a.setIdRegistro(rs.wasNull() ? null : idRegistro);
        a.setDetalles(rs.getString("detalles"));
        a.setIpAddress(rs.getString("ip_address"));
        a.setFechaHora(rs.getTimestamp("fecha_hora"));
        a.setNombreUsuario(rs.getString("nombre_usuario"));
        return a;
    }
}