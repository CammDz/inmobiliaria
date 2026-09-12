package com.inmobiliaria.dao;

import com.inmobiliaria.model.ImagenPropiedad;
import com.inmobiliaria.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ImagenPropiedadDAO {

    public List<ImagenPropiedad> listarPorPropiedad(int idPropiedad) {
        List<ImagenPropiedad> lista = new ArrayList<>();
        String sql = "SELECT * FROM imagen_propiedad WHERE id_propiedad = ? AND activa = 1 "
                   + "ORDER BY es_principal DESC, id_imagen";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPropiedad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en listarPorPropiedad: " + e.getMessage());
        }
        return lista;
    }

    /**
     * URL de la imagen principal de una propiedad (o la primera disponible).
     */
    public String obtenerImagenPrincipal(int idPropiedad) {
        String sql = "SELECT url_imagen FROM imagen_propiedad "
                   + "WHERE id_propiedad = ? AND activa = 1 ORDER BY es_principal DESC, id_imagen LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPropiedad);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("url_imagen");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerImagenPrincipal: " + e.getMessage());
        }
        return "images/placeholder.jpg";
    }

    public boolean insertar(ImagenPropiedad img) {
        String sql = "INSERT INTO imagen_propiedad (id_propiedad, url_imagen, es_principal, activa) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, img.getIdPropiedad());
            ps.setString(2, img.getUrlImagen());
            ps.setInt(3, img.isEsPrincipal() ? 1 : 0);
            ps.setInt(4, img.isActiva() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    img.setIdImagen(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error en insertar imagen: " + e.getMessage());
            return false;
        }
    }

    /**
     * Desactiva una imagen (eliminación lógica).
     */
    public boolean desactivar(int idImagen) {
        String sql = "UPDATE imagen_propiedad SET activa = 0 WHERE id_imagen = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idImagen);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en desactivar imagen: " + e.getMessage());
            return false;
        }
    }

    /**
     * Establece una imagen como principal.
     */
    public boolean marcarComoPrincipal(int idImagen) {
        String sql = "UPDATE imagen_propiedad SET es_principal = 1 WHERE id_imagen = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idImagen);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en marcarComoPrincipal: " + e.getMessage());
            return false;
        }
    }

    public ImagenPropiedad buscarPorId(int id) {
        String sql = "SELECT * FROM imagen_propiedad WHERE id_imagen = ?";
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

    private ImagenPropiedad mapear(ResultSet rs) throws SQLException {
        ImagenPropiedad img = new ImagenPropiedad();
        img.setIdImagen(rs.getInt("id_imagen"));
        img.setIdPropiedad(rs.getInt("id_propiedad"));
        img.setUrlImagen(rs.getString("url_imagen"));
        img.setEsPrincipal(rs.getInt("es_principal") == 1);
        img.setActiva(rs.getInt("activa") == 1);
        img.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        return img;
    }
}