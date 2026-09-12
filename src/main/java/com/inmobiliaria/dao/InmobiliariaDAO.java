package com.inmobiliaria.dao;

import com.inmobiliaria.model.Inmobiliaria;
import com.inmobiliaria.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InmobiliariaDAO {

    public List<Inmobiliaria> listarTodas() {
        List<Inmobiliaria> lista = new ArrayList<>();
        String sql = "SELECT * FROM inmobiliaria WHERE activa = 1 ORDER BY nombre";
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

    public List<Inmobiliaria> listarTodasIncluidasInactivas() {
        List<Inmobiliaria> lista = new ArrayList<>();
        String sql = "SELECT * FROM inmobiliaria ORDER BY nombre";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en listarTodasIncluidasInactivas: " + e.getMessage());
        }
        return lista;
    }

    public Inmobiliaria buscarPorId(int id) {
        String sql = "SELECT * FROM inmobiliaria WHERE id_inmobiliaria = ?";
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

    public boolean insertar(Inmobiliaria inm) {
        String sql = "INSERT INTO inmobiliaria (nombre, direccion, telefono, correo, activa) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, inm.getNombre());
            ps.setString(2, inm.getDireccion());
            ps.setString(3, inm.getTelefono());
            ps.setString(4, inm.getCorreo());
            ps.setInt(5, inm.isActiva() ? 1 : 0);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en insertar: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Inmobiliaria inm) {
        String sql = "UPDATE inmobiliaria SET nombre = ?, direccion = ?, telefono = ?, correo = ?, activa = ? "
                   + "WHERE id_inmobiliaria = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, inm.getNombre());
            ps.setString(2, inm.getDireccion());
            ps.setString(3, inm.getTelefono());
            ps.setString(4, inm.getCorreo());
            ps.setInt(5, inm.isActiva() ? 1 : 0);
            ps.setInt(6, inm.getIdInmobiliaria());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en actualizar: " + e.getMessage());
            return false;
        }
    }

    private Inmobiliaria mapear(ResultSet rs) throws SQLException {
        Inmobiliaria i = new Inmobiliaria();
        i.setIdInmobiliaria(rs.getInt("id_inmobiliaria"));
        i.setNombre(rs.getString("nombre"));
        i.setDireccion(rs.getString("direccion"));
        i.setTelefono(rs.getString("telefono"));
        i.setCorreo(rs.getString("correo"));
        i.setActiva(rs.getInt("activa") == 1);
        return i;
    }
}