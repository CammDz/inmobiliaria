package com.inmobiliaria.dao;

import com.inmobiliaria.model.DocumentoSolicitud;
import com.inmobiliaria.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DocumentoSolicitudDAO {

    public List<DocumentoSolicitud> listarPorSolicitud(int idSolicitud) {
        List<DocumentoSolicitud> lista = new ArrayList<>();
        String sql = "SELECT * FROM documento_solicitud WHERE id_solicitud = ? ORDER BY fecha_subida DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en listarPorSolicitud: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Documentos de todas las solicitudes de un agente.
     */
    public List<DocumentoSolicitud> listarPorAgente(int idAgente) {
        List<DocumentoSolicitud> lista = new ArrayList<>();
        String sql = "SELECT d.* FROM documento_solicitud d "
                   + "INNER JOIN solicitud s ON d.id_solicitud = s.id_solicitud "
                   + "INNER JOIN propiedad p ON s.id_propiedad = p.id_propiedad "
                   + "WHERE p.id_agente = ? ORDER BY d.fecha_subida DESC";
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

    public DocumentoSolicitud buscarPorId(int id) {
        String sql = "SELECT * FROM documento_solicitud WHERE id_documento = ?";
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

    public boolean insertar(DocumentoSolicitud doc) {
        String sql = "INSERT INTO documento_solicitud (id_solicitud, nombre_archivo, ruta_archivo, tipo_documento) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, doc.getIdSolicitud());
            ps.setString(2, doc.getNombreArchivo());
            ps.setString(3, doc.getRutaArchivo());
            ps.setString(4, doc.getTipoDocumento());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    doc.setIdDocumento(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error en insertar documento: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM documento_solicitud WHERE id_documento = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en eliminar documento: " + e.getMessage());
            return false;
        }
    }

    private DocumentoSolicitud mapear(ResultSet rs) throws SQLException {
        DocumentoSolicitud d = new DocumentoSolicitud();
        d.setIdDocumento(rs.getInt("id_documento"));
        d.setIdSolicitud(rs.getInt("id_solicitud"));
        d.setNombreArchivo(rs.getString("nombre_archivo"));
        d.setRutaArchivo(rs.getString("ruta_archivo"));
        d.setTipoDocumento(rs.getString("tipo_documento"));
        d.setFechaSubida(rs.getTimestamp("fecha_subida"));
        return d;
    }
}