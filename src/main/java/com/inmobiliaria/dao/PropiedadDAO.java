package com.inmobiliaria.dao;

import com.inmobiliaria.model.Propiedad;
import com.inmobiliaria.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PropiedadDAO {

    private static final String SELECT_BASE =
            "SELECT p.*, c.nombre AS nombre_ciudad, c.departamento AS nombre_departamento, "
          + "tp.nombre AS nombre_tipo, i.nombre AS nombre_inmobiliaria, "
          + "CONCAT(u.nombre, ' ', u.apellido) AS nombre_agente "
          + "FROM propiedad p "
          + "INNER JOIN ciudad c ON p.id_ciudad = c.id_ciudad "
          + "INNER JOIN tipo_propiedad tp ON p.id_tipo = tp.id_tipo "
          + "INNER JOIN inmobiliaria i ON p.id_inmobiliaria = i.id_inmobiliaria "
          + "INNER JOIN usuario u ON p.id_agente = u.id_usuario";

    /**
     * Lista propiedades activas para el catálogo público.
     */
    public List<Propiedad> listarActivas() {
        List<Propiedad> lista = new ArrayList<>();
        String sql = SELECT_BASE + " WHERE p.activa = 1 ORDER BY p.fecha_registro DESC LIMIT 50";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en listarActivas: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista propiedades destacadas (las primeras 6 activas).
     */
    public List<Propiedad> listarDestacadas() {
        List<Propiedad> lista = new ArrayList<>();
        String sql = SELECT_BASE + " WHERE p.activa = 1 ORDER BY p.fecha_registro DESC LIMIT 6";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en listarDestacadas: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Búsqueda con filtros: ciudad, tipo, rango de precio, características, término.
     */
    public List<Propiedad> buscar(String termino, Integer idCiudad, Integer idTipo,
                                  Double precioMin, Double precioMax, List<Integer> idCaracteristicas) {
        List<Propiedad> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_BASE);
        List<Object> params = new ArrayList<>();

        sql.append(" WHERE p.activa = 1");

        if (termino != null && !termino.trim().isEmpty()) {
            sql.append(" AND (p.titulo LIKE ? OR p.descripcion LIKE ? OR c.nombre LIKE ? ")
               .append(" OR c.departamento LIKE ? OR tp.nombre LIKE ? OR p.direccion LIKE ?)");
            String patron = "%" + termino.trim() + "%";
            for (int i = 0; i < 6; i++) {
                params.add(patron);
            }
        }
        if (idCiudad != null && idCiudad > 0) {
            sql.append(" AND p.id_ciudad = ?");
            params.add(idCiudad);
        }
        if (idTipo != null && idTipo > 0) {
            sql.append(" AND p.id_tipo = ?");
            params.add(idTipo);
        }
        if (precioMin != null) {
            sql.append(" AND p.precio >= ?");
            params.add(precioMin);
        }
        if (precioMax != null) {
            sql.append(" AND p.precio <= ?");
            params.add(precioMax);
        }
        if (idCaracteristicas != null && !idCaracteristicas.isEmpty()) {
            sql.append(" AND p.id_propiedad IN (");
            sql.append(" SELECT pc.id_propiedad FROM propiedad_caracteristica pc");
            sql.append(" WHERE pc.id_caracteristica IN (");
            for (int i = 0; i < idCaracteristicas.size(); i++) {
                if (i > 0) sql.append(",");
                sql.append("?");
                params.add(idCaracteristicas.get(i));
            }
            sql.append(") GROUP BY pc.id_propiedad HAVING COUNT(DISTINCT pc.id_caracteristica) >= ?");
            params.add(idCaracteristicas.size());
            sql.append(")");
        }

        sql.append(" ORDER BY p.fecha_registro DESC LIMIT 100");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object obj = params.get(i);
                if (obj instanceof Integer) {
                    ps.setInt(i + 1, (Integer) obj);
                } else if (obj instanceof Double) {
                    ps.setDouble(i + 1, (Double) obj);
                } else {
                    ps.setString(i + 1, obj.toString());
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscar: " + e.getMessage());
        }
        return lista;
    }

    public Propiedad buscarPorId(int id) {
        String sql = SELECT_BASE + " WHERE p.id_propiedad = ?";
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
     * Lista propiedades de un agente específico (incluye inactivas).
     */
    public List<Propiedad> listarPorAgente(int idAgente) {
        List<Propiedad> lista = new ArrayList<>();
        String sql = SELECT_BASE + " WHERE p.id_agente = ? ORDER BY p.fecha_registro DESC";
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

    /**
     * Lista todas las propiedades (para reportes admin, incluye inactivas).
     */
    public List<Propiedad> listarTodas() {
        List<Propiedad> lista = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY p.fecha_registro DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en listarTodas: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Inserta una propiedad y devuelve el id generado.
     * Devuelve -1 si la matrícula ya existe.
     */
    public int insertar(Propiedad p) {
        if (existeMatricula(p.getMatriculaInmobiliaria())) {
            return -1;
        }
        String sql = "INSERT INTO propiedad (titulo, descripcion, precio, id_ciudad, id_tipo, "
                   + "id_inmobiliaria, id_agente, matricula_inmobiliaria, estado, tipo_operacion, "
                   + "direccion, area_m2, habitaciones, banos, parqueaderos, activa) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getTitulo());
            ps.setString(2, p.getDescripcion());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getIdCiudad());
            ps.setInt(5, p.getIdTipo());
            ps.setInt(6, p.getIdInmobiliaria());
            ps.setInt(7, p.getIdAgente());
            ps.setString(8, p.getMatriculaInmobiliaria());
            ps.setString(9, p.getEstado());
            ps.setString(10, p.getTipoOperacion());
            ps.setString(11, p.getDireccion());
            ps.setDouble(12, p.getAreaM2());
            ps.setInt(13, p.getHabitaciones());
            ps.setInt(14, p.getBanos());
            ps.setInt(15, p.getParqueaderos());
            ps.setInt(16, p.isActiva() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en insertar propiedad: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizar(Propiedad p) {
        String sql = "UPDATE propiedad SET titulo = ?, descripcion = ?, precio = ?, id_ciudad = ?, "
                   + "id_tipo = ?, id_inmobiliaria = ?, matricula_inmobiliaria = ?, estado = ?, "
                   + "tipo_operacion = ?, direccion = ?, area_m2 = ?, habitaciones = ?, "
                   + "banos = ?, parqueaderos = ?, activa = ? WHERE id_propiedad = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getTitulo());
            ps.setString(2, p.getDescripcion());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getIdCiudad());
            ps.setInt(5, p.getIdTipo());
            ps.setInt(6, p.getIdInmobiliaria());
            ps.setString(7, p.getMatriculaInmobiliaria());
            ps.setString(8, p.getEstado());
            ps.setString(9, p.getTipoOperacion());
            ps.setString(10, p.getDireccion());
            ps.setDouble(11, p.getAreaM2());
            ps.setInt(12, p.getHabitaciones());
            ps.setInt(13, p.getBanos());
            ps.setInt(14, p.getParqueaderos());
            ps.setInt(15, p.isActiva() ? 1 : 0);
            ps.setInt(16, p.getIdPropiedad());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en actualizar propiedad: " + e.getMessage());
            return false;
        }
    }

    /**
     * Desactivación lógica de una propiedad.
     */
    public boolean desactivar(int idPropiedad) {
        String sql = "UPDATE propiedad SET activa = 0 WHERE id_propiedad = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPropiedad);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en desactivar propiedad: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reactiva una propiedad.
     */
    public boolean activar(int idPropiedad) {
        String sql = "UPDATE propiedad SET activa = 1 WHERE id_propiedad = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPropiedad);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en activar propiedad: " + e.getMessage());
            return false;
        }
    }

    public boolean existeMatricula(String matricula) {
        String sql = "SELECT COUNT(*) FROM propiedad WHERE matricula_inmobiliaria = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en existeMatricula: " + e.getMessage());
        }
        return false;
    }

    private Propiedad mapear(ResultSet rs) throws SQLException {
        Propiedad p = new Propiedad();
        p.setIdPropiedad(rs.getInt("id_propiedad"));
        p.setTitulo(rs.getString("titulo"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecio(rs.getDouble("precio"));
        p.setIdCiudad(rs.getInt("id_ciudad"));
        p.setIdTipo(rs.getInt("id_tipo"));
        p.setIdInmobiliaria(rs.getInt("id_inmobiliaria"));
        p.setIdAgente(rs.getInt("id_agente"));
        p.setMatriculaInmobiliaria(rs.getString("matricula_inmobiliaria"));
        p.setEstado(rs.getString("estado"));
        p.setTipoOperacion(rs.getString("tipo_operacion"));
        p.setDireccion(rs.getString("direccion"));
        p.setAreaM2(rs.getDouble("area_m2"));
        p.setHabitaciones(rs.getInt("habitaciones"));
        p.setBanos(rs.getInt("banos"));
        p.setParqueaderos(rs.getInt("parqueaderos"));
        p.setActiva(rs.getInt("activa") == 1);
        p.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        p.setNombreCiudad(rs.getString("nombre_ciudad"));
        p.setNombreDepartamento(rs.getString("nombre_departamento"));
        p.setNombreTipo(rs.getString("nombre_tipo"));
        p.setNombreInmobiliaria(rs.getString("nombre_inmobiliaria"));
        p.setNombreAgente(rs.getString("nombre_agente"));
        return p;
    }
}