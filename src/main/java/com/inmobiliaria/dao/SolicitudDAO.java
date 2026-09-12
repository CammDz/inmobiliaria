package com.inmobiliaria.dao;

import com.inmobiliaria.model.Solicitud;
import com.inmobiliaria.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SolicitudDAO {

    private static final String SELECT_BASE =
            "SELECT s.*, u.nombre AS nombre_cliente, u.apellido AS apellido_cliente, "
          + "u.correo AS correo_cliente, p.titulo AS titulo_propiedad, "
          + "i.nombre AS nombre_inmobiliaria "
          + "FROM solicitud s "
          + "INNER JOIN usuario u ON s.id_cliente = u.id_usuario "
          + "INNER JOIN propiedad p ON s.id_propiedad = p.id_propiedad "
          + "INNER JOIN inmobiliaria i ON p.id_inmobiliaria = i.id_inmobiliaria";

    public List<Solicitud> listarPorCliente(int idCliente) {
        List<Solicitud> lista = new ArrayList<>();
        String sql = SELECT_BASE + " WHERE s.id_cliente = ? ORDER BY s.fecha_registro DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en listarPorCliente: " + e.getMessage());
        }
        return lista;
    }

    public List<Solicitud> listarPorAgente(int idAgente) {
        List<Solicitud> lista = new ArrayList<>();
        String sql = SELECT_BASE + " WHERE p.id_agente = ? ORDER BY s.fecha_registro DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAgente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en listarPorAgente: " + e.getMessage());
        }
        return lista;
    }

    public List<Solicitud> listarTodas() {
        List<Solicitud> lista = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY s.fecha_registro DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en listarTodas: " + e.getMessage());
        }
        return lista;
    }

    public Solicitud buscarPorId(int id) {
        String sql = SELECT_BASE + " WHERE s.id_solicitud = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Verifica que el cliente no tenga una solicitud pendiente para la misma propiedad.
     */
    public boolean existeSolicitudPendiente(int idCliente, int idPropiedad) {
        String sql = "SELECT COUNT(*) FROM solicitud "
                   + "WHERE id_cliente = ? AND id_propiedad = ? "
                   + "AND estado IN ('PENDIENTE','EN_REVISION')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, idPropiedad);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en existeSolicitudPendiente: " + e.getMessage());
        }
        return false;
    }

    /**
     * Verifica que la solicitud pertenezca a una propiedad del agente dado.
     */
    public boolean perteneceAPropiedadDelAgente(int idSolicitud, int idAgente) {
        String sql = "SELECT COUNT(*) FROM solicitud s "
                   + "INNER JOIN propiedad p ON s.id_propiedad = p.id_propiedad "
                   + "WHERE s.id_solicitud = ? AND p.id_agente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            ps.setInt(2, idAgente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en perteneceAPropiedadDelAgente: " + e.getMessage());
        }
        return false;
    }

    public boolean insertar(Solicitud solicitud) {
        String sql = "INSERT INTO solicitud (id_cliente, id_propiedad, tipo, estado, observaciones, monto_ofrecido) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, solicitud.getIdCliente());
            ps.setInt(2, solicitud.getIdPropiedad());
            ps.setString(3, solicitud.getTipo());
            ps.setString(4, solicitud.getEstado());
            ps.setString(5, solicitud.getObservaciones());
            ps.setDouble(6, solicitud.getMontoOfrecido());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    solicitud.setIdSolicitud(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error en insertar solicitud: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza el estado de la solicitud (APROBADA, RECHAZADA, EN_REVISION).
     */
    public boolean actualizarEstado(int idSolicitud, String estado) {
        String sql = "UPDATE solicitud SET estado = ? WHERE id_solicitud = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idSolicitud);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en actualizarEstado: " + e.getMessage());
            return false;
        }
    }

    private Solicitud mapear(ResultSet rs) throws SQLException {
        Solicitud s = new Solicitud();
        s.setIdSolicitud(rs.getInt("id_solicitud"));
        s.setIdCliente(rs.getInt("id_cliente"));
        s.setIdPropiedad(rs.getInt("id_propiedad"));
        s.setTipo(rs.getString("tipo"));
        s.setEstado(rs.getString("estado"));
        s.setObservaciones(rs.getString("observaciones"));
        s.setMontoOfrecido(rs.getDouble("monto_ofrecido"));
        s.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        s.setNombreCliente(rs.getString("nombre_cliente") + " " + rs.getString("apellido_cliente"));
        s.setCorreoCliente(rs.getString("correo_cliente"));
        s.setTituloPropiedad(rs.getString("titulo_propiedad"));
        s.setNombreInmobiliaria(rs.getString("nombre_inmobiliaria"));
        return s;
    }
}