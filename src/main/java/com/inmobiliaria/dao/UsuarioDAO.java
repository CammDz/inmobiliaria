package com.inmobiliaria.dao;

import com.inmobiliaria.model.Usuario;
import com.inmobiliaria.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    /**
     * Busca un usuario por su correo.
     */
    public Usuario buscarPorCorreo(String correo) {
        String sql = "SELECT * FROM usuario WHERE correo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorCorreo: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca un usuario por su id.
     */
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
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
     * Verifica si un correo ya existe.
     */
    public boolean existeCorreo(String correo) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE correo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en existeCorreo: " + e.getMessage());
        }
        return false;
    }

    /**
     * Inserta un nuevo usuario y devuelve el id generado.
     * Devuelve -1 si el correo ya existe.
     */
    public int insertar(Usuario usuario) throws SQLException {
        if (existeCorreo(usuario.getCorreo())) {
            return -1;
        }
        String sql = "INSERT INTO usuario (correo, contrasena, nombre, apellido, telefono, activo) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getCorreo());
            ps.setString(2, usuario.getContrasena());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getApellido());
            ps.setString(5, usuario.getTelefono());
            ps.setInt(6, usuario.isActivo() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    /**
     * Actualiza los datos de un usuario (sin contraseña).
     */
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET correo = ?, nombre = ?, apellido = ?, telefono = ?, activo = ? "
                   + "WHERE id_usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getCorreo());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellido());
            ps.setString(4, usuario.getTelefono());
            ps.setInt(5, usuario.isActivo() ? 1 : 0);
            ps.setInt(6, usuario.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza la contraseña de un usuario.
     */
    public boolean actualizarContrasena(int idUsuario, String contrasenaHash) {
        String sql = "UPDATE usuario SET contrasena = ? WHERE id_usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contrasenaHash);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en actualizarContrasena: " + e.getMessage());
            return false;
        }
    }

    /**
     * Eliminación lógica: desactiva un usuario.
     */
    public boolean desactivar(int idUsuario) {
        String sql = "UPDATE usuario SET activo = 0 WHERE id_usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en desactivar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reactiva un usuario.
     */
    public boolean activar(int idUsuario) {
        String sql = "UPDATE usuario SET activo = 1 WHERE id_usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en activar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lista todos los usuarios.
     */
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY id_usuario";
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

    /**
     * Lista usuarios que tienen un rol específico.
     */
    public List<Usuario> listarPorRol(int idRol) {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT u.* FROM usuario u "
                   + "INNER JOIN usuario_rol ur ON u.id_usuario = ur.id_usuario "
                   + "WHERE ur.id_rol = ? AND u.activo = 1 ORDER BY u.nombre";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRol);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en listarPorRol: " + e.getMessage());
        }
        return lista;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setCorreo(rs.getString("correo"));
        u.setContrasena(rs.getString("contrasena"));
        u.setNombre(rs.getString("nombre"));
        u.setApellido(rs.getString("apellido"));
        u.setTelefono(rs.getString("telefono"));
        u.setActivo(rs.getInt("activo") == 1);
        u.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        return u;
    }
}