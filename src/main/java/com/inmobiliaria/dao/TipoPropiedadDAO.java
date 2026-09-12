package com.inmobiliaria.dao;

import com.inmobiliaria.model.TipoPropiedad;
import com.inmobiliaria.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TipoPropiedadDAO {

    public List<TipoPropiedad> listarTodos() {
        List<TipoPropiedad> lista = new ArrayList<>();
        String sql = "SELECT * FROM tipo_propiedad ORDER BY nombre";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en listarTodos: " + e.getMessage());
        }
        return lista;
    }

    public TipoPropiedad buscarPorId(int id) {
        String sql = "SELECT * FROM tipo_propiedad WHERE id_tipo = ?";
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

    public boolean insertar(TipoPropiedad tipo) {
        String sql = "INSERT INTO tipo_propiedad (nombre, descripcion) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo.getNombre());
            ps.setString(2, tipo.getDescripcion());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en insertar tipo: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(TipoPropiedad tipo) {
        String sql = "UPDATE tipo_propiedad SET nombre = ?, descripcion = ? WHERE id_tipo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo.getNombre());
            ps.setString(2, tipo.getDescripcion());
            ps.setInt(3, tipo.getIdTipo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en actualizar tipo: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM tipo_propiedad WHERE id_tipo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("No se puede eliminar el tipo (puede tener propiedades asociadas): " + e.getMessage());
            return false;
        }
    }

    private TipoPropiedad mapear(ResultSet rs) throws SQLException {
        TipoPropiedad t = new TipoPropiedad();
        t.setIdTipo(rs.getInt("id_tipo"));
        t.setNombre(rs.getString("nombre"));
        t.setDescripcion(rs.getString("descripcion"));
        return t;
    }
}