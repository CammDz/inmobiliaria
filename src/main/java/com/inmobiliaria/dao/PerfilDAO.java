package com.inmobiliaria.dao;

import com.inmobiliaria.model.Perfil;
import com.inmobiliaria.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PerfilDAO {

    /**
     * Busca el perfil por id de usuario (relación 1:1).
     */
    public Perfil buscarPorIdUsuario(int idUsuario) {
        String sql = "SELECT * FROM perfil WHERE id_usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorIdUsuario: " + e.getMessage());
        }
        return null;
    }

    /**
     * Crea un perfil para un usuario (relación 1:1).
     */
    public boolean crear(Perfil perfil) {
        String sql = "INSERT INTO perfil (id_usuario, direccion, fecha_nacimiento, documento_identidad, foto_url) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, perfil.getIdUsuario());
            ps.setString(2, perfil.getDireccion());
            if (perfil.getFechaNacimiento() != null) {
                ps.setDate(3, perfil.getFechaNacimiento());
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            ps.setString(4, perfil.getDocumentoIdentidad());
            ps.setString(5, perfil.getFotoUrl());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    perfil.setIdPerfil(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error en crear perfil: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza un perfil existente.
     */
    public boolean actualizar(Perfil perfil) {
        String sql = "UPDATE perfil SET direccion = ?, fecha_nacimiento = ?, "
                   + "documento_identidad = ?, foto_url = ? WHERE id_usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, perfil.getDireccion());
            if (perfil.getFechaNacimiento() != null) {
                ps.setDate(2, perfil.getFechaNacimiento());
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }
            ps.setString(3, perfil.getDocumentoIdentidad());
            ps.setString(4, perfil.getFotoUrl());
            ps.setInt(5, perfil.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en actualizar perfil: " + e.getMessage());
            return false;
        }
    }

    private Perfil mapear(ResultSet rs) throws SQLException {
        Perfil p = new Perfil();
        p.setIdPerfil(rs.getInt("id_perfil"));
        p.setIdUsuario(rs.getInt("id_usuario"));
        p.setDireccion(rs.getString("direccion"));
        p.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
        p.setDocumentoIdentidad(rs.getString("documento_identidad"));
        p.setFotoUrl(rs.getString("foto_url"));
        return p;
    }
}