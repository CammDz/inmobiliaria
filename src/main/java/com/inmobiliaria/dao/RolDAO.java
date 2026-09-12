package com.inmobiliaria.dao;

import com.inmobiliaria.model.Rol;
import com.inmobiliaria.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RolDAO {

    public List<Rol> listarTodos() {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT * FROM rol ORDER BY id_rol";
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

    public Rol buscarPorId(int id) {
        String sql = "SELECT * FROM rol WHERE id_rol = ?";
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

    public Rol buscarPorNombre(String nombre) {
        String sql = "SELECT * FROM rol WHERE nombre = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorNombre: " + e.getMessage());
        }
        return null;
    }

    /**
     * Devuelve los roles de un usuario como conjunto de nombres.
     */
    public Set<String> obtenerRolesDeUsuario(int idUsuario) {
        Set<String> roles = new LinkedHashSet<>();
        String sql = "SELECT r.nombre FROM rol r "
                   + "INNER JOIN usuario_rol ur ON r.id_rol = ur.id_rol "
                   + "WHERE ur.id_usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerRolesDeUsuario: " + e.getMessage());
        }
        return roles;
    }

    /**
     * Asigna un rol a un usuario.
     */
    public boolean asignarRol(int idUsuario, int idRol) {
        String sql = "INSERT INTO usuario_rol (id_usuario, id_rol) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idRol);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en asignarRol: " + e.getMessage());
            return false;
        }
    }

    /**
     * Quita un rol a un usuario.
     */
    public boolean retirarRol(int idUsuario, int idRol) {
        String sql = "DELETE FROM usuario_rol WHERE id_usuario = ? AND id_rol = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idRol);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en retirarRol: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lista los ids de roles de un usuario.
     */
    public List<Integer> obtenerIdsRolesDeUsuario(int idUsuario) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_rol FROM usuario_rol WHERE id_usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id_rol"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerIdsRolesDeUsuario: " + e.getMessage());
        }
        return ids;
    }

    /**
     * Inserta un nuevo rol.
     */
    public boolean insertar(Rol rol) {
        String sql = "INSERT INTO rol (nombre, descripcion) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rol.getNombre());
            ps.setString(2, rol.getDescripcion());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en insertar rol: " + e.getMessage());
            return false;
        }
    }

    private Rol mapear(ResultSet rs) throws SQLException {
        Rol r = new Rol();
        r.setIdRol(rs.getInt("id_rol"));
        r.setNombre(rs.getString("nombre"));
        r.setDescripcion(rs.getString("descripcion"));
        return r;
    }
}