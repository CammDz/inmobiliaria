package com.inmobiliaria.dao;

import com.inmobiliaria.model.Favorito;
import com.inmobiliaria.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FavoritoDAO {

    public List<Favorito> listarPorCliente(int idCliente) {
        List<Favorito> lista = new ArrayList<>();
        String sql = "SELECT f.*, p.titulo AS titulo_propiedad, p.precio AS precio_propiedad, "
                   + "c.nombre AS nombre_ciudad, tp.nombre AS nombre_tipo, "
                   + "(SELECT ip.url_imagen FROM imagen_propiedad ip "
                   + " WHERE ip.id_propiedad = p.id_propiedad AND ip.activa = 1 "
                   + " ORDER BY ip.es_principal DESC LIMIT 1) AS url_imagen "
                   + "FROM favorito f "
                   + "INNER JOIN propiedad p ON f.id_propiedad = p.id_propiedad "
                   + "INNER JOIN ciudad c ON p.id_ciudad = c.id_ciudad "
                   + "INNER JOIN tipo_propiedad tp ON p.id_tipo = tp.id_tipo "
                   + "WHERE f.id_cliente = ? AND p.activa = 1 ORDER BY f.fecha DESC";
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

    /**
     * Verifica si el cliente ya tiene la propiedad en favoritos.
     */
    public boolean esFavorito(int idCliente, int idPropiedad) {
        String sql = "SELECT COUNT(*) FROM favorito WHERE id_cliente = ? AND id_propiedad = ?";
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
            System.err.println("Error en esFavorito: " + e.getMessage());
        }
        return false;
    }

    /**
     * Agrega a favoritos. Devuelve false si ya existe (restricción UNIQUE).
     */
    public boolean agregar(int idCliente, int idPropiedad) {
        if (esFavorito(idCliente, idPropiedad)) {
            return false;
        }
        String sql = "INSERT INTO favorito (id_cliente, id_propiedad) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, idPropiedad);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en agregar favorito: " + e.getMessage());
            return false;
        }
    }

    public boolean quitar(int idCliente, int idPropiedad) {
        String sql = "DELETE FROM favorito WHERE id_cliente = ? AND id_propiedad = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, idPropiedad);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en quitar favorito: " + e.getMessage());
            return false;
        }
    }

    private Favorito mapear(ResultSet rs) throws SQLException {
        Favorito f = new Favorito();
        f.setIdFavorito(rs.getInt("id_favorito"));
        f.setIdCliente(rs.getInt("id_cliente"));
        f.setIdPropiedad(rs.getInt("id_propiedad"));
        f.setFecha(rs.getTimestamp("fecha"));
        f.setTituloPropiedad(rs.getString("titulo_propiedad"));
        f.setPrecioPropiedad(rs.getDouble("precio_propiedad"));
        f.setNombreCiudad(rs.getString("nombre_ciudad"));
        f.setNombreTipo(rs.getString("nombre_tipo"));
        f.setUrlImagen(rs.getString("url_imagen"));
        return f;
    }
}