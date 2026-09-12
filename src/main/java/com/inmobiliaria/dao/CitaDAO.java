package com.inmobiliaria.dao;

import com.inmobiliaria.model.Cita;
import com.inmobiliaria.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {

    private static final String SELECT_BASE =
            "SELECT c.*, u.nombre AS nombre_cliente, u.apellido AS apellido_cliente, "
          + "u.correo AS correo_cliente, p.titulo AS titulo_propiedad, "
          + "p.direccion AS direccion_propiedad, "
          + "CONCAT(a.nombre, ' ', a.apellido) AS nombre_agente "
          + "FROM cita c "
          + "INNER JOIN usuario u ON c.id_cliente = u.id_usuario "
          + "INNER JOIN propiedad p ON c.id_propiedad = p.id_propiedad "
          + "INNER JOIN usuario a ON p.id_agente = a.id_usuario";

    public List<Cita> listarPorCliente(int idCliente) {
        List<Cita> lista = new ArrayList<>();
        String sql = SELECT_BASE + " WHERE c.id_cliente = ? ORDER BY c.fecha DESC, c.hora DESC";
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

    public List<Cita> listarPorAgente(int idAgente) {
        List<Cita> lista = new ArrayList<>();
        String sql = SELECT_BASE + " WHERE p.id_agente = ? ORDER BY c.fecha DESC, c.hora DESC";
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

    public List<Cita> listarTodas() {
        List<Cita> lista = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY c.fecha DESC, c.hora DESC";
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

    public Cita buscarPorId(int id) {
        String sql = SELECT_BASE + " WHERE c.id_cita = ?";
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
     * Verifica si existe una cita duplicada (mismo cliente, propiedad, fecha y hora).
     */
    public boolean existeCitaDuplicada(int idCliente, int idPropiedad, java.sql.Date fecha, java.sql.Time hora) {
        String sql = "SELECT COUNT(*) FROM cita "
                   + "WHERE id_cliente = ? AND id_propiedad = ? AND fecha = ? AND hora = ? AND estado IN ('PENDIENTE','APROBADA')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, idPropiedad);
            ps.setDate(3, fecha);
            ps.setTime(4, hora);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en existeCitaDuplicada: " + e.getMessage());
        }
        return false;
    }

    public boolean insertar(Cita cita) {
        String sql = "INSERT INTO cita (id_cliente, id_propiedad, fecha, hora, estado, observaciones) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cita.getIdCliente());
            ps.setInt(2, cita.getIdPropiedad());
            ps.setDate(3, cita.getFecha());
            ps.setTime(4, cita.getHora());
            ps.setString(5, cita.getEstado());
            ps.setString(6, cita.getObservaciones());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cita.setIdCita(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error en insertar cita: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza el estado de una cita (APROBADA, RECHAZADA, CANCELADA, PENDIENTE).
     */
    public boolean actualizarEstado(int idCita, String estado) {
        String sql = "UPDATE cita SET estado = ? WHERE id_cita = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idCita);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en actualizarEstado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si la cita pertenece a una propiedad del agente dado.
     */
    public boolean perteneceAPropiedadDelAgente(int idCita, int idAgente) {
        String sql = "SELECT COUNT(*) FROM cita c "
                   + "INNER JOIN propiedad p ON c.id_propiedad = p.id_propiedad "
                   + "WHERE c.id_cita = ? AND p.id_agente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCita);
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

    public boolean cancelar(int idCita) {
        return actualizarEstado(idCita, "CANCELADA");
    }

    private Cita mapear(ResultSet rs) throws SQLException {
        Cita c = new Cita();
        c.setIdCita(rs.getInt("id_cita"));
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setIdPropiedad(rs.getInt("id_propiedad"));
        c.setFecha(rs.getDate("fecha"));
        c.setHora(rs.getTime("hora"));
        c.setEstado(rs.getString("estado"));
        c.setObservaciones(rs.getString("observaciones"));
        c.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        c.setNombreCliente(rs.getString("nombre_cliente") + " " + rs.getString("apellido_cliente"));
        c.setCorreoCliente(rs.getString("correo_cliente"));
        c.setTituloPropiedad(rs.getString("titulo_propiedad"));
        c.setDireccionPropiedad(rs.getString("direccion_propiedad"));
        c.setNombreAgente(rs.getString("nombre_agente"));
        return c;
    }
}