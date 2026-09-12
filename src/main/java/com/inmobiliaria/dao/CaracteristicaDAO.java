package com.inmobiliaria.dao;

import com.inmobiliaria.model.Caracteristica;
import com.inmobiliaria.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CaracteristicaDAO {

    public List<Caracteristica> listarTodas() {
        List<Caracteristica> lista = new ArrayList<>();
        String sql = "SELECT * FROM caracteristica ORDER BY nombre";
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

    public Caracteristica buscarPorId(int id) {
        String sql = "SELECT * FROM caracteristica WHERE id_caracteristica = ?";
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
     * Características de una propiedad (relación N:M).
     */
    public List<Caracteristica> listarPorPropiedad(int idPropiedad) {
        List<Caracteristica> lista = new ArrayList<>();
        String sql = "SELECT c.* FROM caracteristica c "
                   + "INNER JOIN propiedad_caracteristica pc ON c.id_caracteristica = pc.id_caracteristica "
                   + "WHERE pc.id_propiedad = ? ORDER BY c.nombre";
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
     * Asocia una característica a una propiedad.
     */
    public boolean asignarAPropiedad(int idPropiedad, int idCaracteristica) {
        String sql = "INSERT INTO propiedad_caracteristica (id_propiedad, id_caracteristica) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPropiedad);
            ps.setInt(2, idCaracteristica);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en asignarAPropiedad: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina todas las características de una propiedad.
     */
    public boolean eliminarDePropiedad(int idPropiedad) {
        String sql = "DELETE FROM propiedad_caracteristica WHERE id_propiedad = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPropiedad);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en eliminarDePropiedad: " + e.getMessage());
            return false;
        }
    }

    public boolean insertar(Caracteristica car) {
        String sql = "INSERT INTO caracteristica (nombre, descripcion) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, car.getNombre());
            ps.setString(2, car.getDescripcion());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en insertar caracteristica: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Caracteristica car) {
        String sql = "UPDATE caracteristica SET nombre = ?, descripcion = ? WHERE id_caracteristica = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, car.getNombre());
            ps.setString(2, car.getDescripcion());
            ps.setInt(3, car.getIdCaracteristica());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en actualizar caracteristica: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM caracteristica WHERE id_caracteristica = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("No se puede eliminar la caracteristica (puede estar asociada): " + e.getMessage());
            return false;
        }
    }

    private Caracteristica mapear(ResultSet rs) throws SQLException {
        Caracteristica c = new Caracteristica();
        c.setIdCaracteristica(rs.getInt("id_caracteristica"));
        c.setNombre(rs.getString("nombre"));
        c.setDescripcion(rs.getString("descripcion"));
        return c;
    }
}