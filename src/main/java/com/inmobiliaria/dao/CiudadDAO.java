package com.inmobiliaria.dao;

import com.inmobiliaria.model.Ciudad;
import com.inmobiliaria.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CiudadDAO {

    public List<Ciudad> listarTodas() {
        List<Ciudad> lista = new ArrayList<>();
        String sql = "SELECT * FROM ciudad ORDER BY nombre";
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

    public Ciudad buscarPorId(int id) {
        String sql = "SELECT * FROM ciudad WHERE id_ciudad = ?";
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

    public boolean insertar(Ciudad ciudad) {
        String sql = "INSERT INTO ciudad (nombre, departamento) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ciudad.getNombre());
            ps.setString(2, ciudad.getDepartamento());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en insertar ciudad: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Ciudad ciudad) {
        String sql = "UPDATE ciudad SET nombre = ?, departamento = ? WHERE id_ciudad = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ciudad.getNombre());
            ps.setString(2, ciudad.getDepartamento());
            ps.setInt(3, ciudad.getIdCiudad());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en actualizar ciudad: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM ciudad WHERE id_ciudad = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("No se puede eliminar la ciudad (puede tener propiedades asociadas): " + e.getMessage());
            return false;
        }
    }

    private Ciudad mapear(ResultSet rs) throws SQLException {
        Ciudad c = new Ciudad();
        c.setIdCiudad(rs.getInt("id_ciudad"));
        c.setNombre(rs.getString("nombre"));
        c.setDepartamento(rs.getString("departamento"));
        return c;
    }
}