package com.inmobiliaria.dao;

import com.inmobiliaria.model.Reporte;
import com.inmobiliaria.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Consultas de reportes SQL.
 *
 * Se incluyen:
 * 1. INNER JOIN con 3+ tablas (propiedades con ciudad, tipo e inmobiliaria)
 * 2. INNER JOIN con 4 tablas (solicitudes con usuario, propiedad e inmobiliaria)
 * 3. Relación N:M (propiedades con características)
 * 4. LEFT JOIN (propiedades que tienen o no citas)
 * 5. Agregación con GROUP BY y HAVING (citas por estado con recuento mínimo)
 */
public class ReporteDAO {

    /**
     * CONSULTA 1: INNER JOIN con 3 tablas.
     * Propiedades activas con ciudad, tipo de propiedad e inmobiliaria.
     */
    public Reporte propiedadesPorCiudad() {
        String titulo = "Cantidad de propiedades por ciudad";
        String sql = "SELECT c.nombre AS ciudad, tp.nombre AS tipo, "
                   + "p.estado, COUNT(*) AS total_propiedades "
                   + "FROM propiedad p "
                   + "INNER JOIN ciudad c ON p.id_ciudad = c.id_ciudad "
                   + "INNER JOIN tipo_propiedad tp ON p.id_tipo = tp.id_tipo "
                   + "INNER JOIN inmobiliaria i ON p.id_inmobiliaria = i.id_inmobiliaria "
                   + "WHERE p.activa = 1 "
                   + "GROUP BY c.nombre, tp.nombre, p.estado "
                   + "ORDER BY c.nombre, total_propiedades DESC";
        return ejecutar(titulo, sql);
    }

    /**
     * CONSULTA 2: INNER JOIN con 4 tablas.
     * Solicitudes con datos de cliente, propiedad e inmobiliaria.
     */
    public Reporte solicitudesPorInmobiliaria() {
        String titulo = "Solicitudes por inmobiliaria";
        String sql = "SELECT i.nombre AS inmobiliaria, s.tipo AS tipo_solicitud, "
                   + "s.estado, UPPER(u.nombre) AS cliente_nombre, p.titulo AS propiedad "
                   + "FROM solicitud s "
                   + "INNER JOIN usuario u ON s.id_cliente = u.id_usuario "
                   + "INNER JOIN propiedad p ON s.id_propiedad = p.id_propiedad "
                   + "INNER JOIN inmobiliaria i ON p.id_inmobiliaria = i.id_inmobiliaria "
                   + "ORDER BY i.nombre, s.fecha_registro DESC";
        return ejecutar(titulo, sql);
    }

    /**
     * CONSULTA 3: Relación N:M.
     * Características por propiedad (propiedad <-> caracteristica).
     */
    public Reporte caracteristicasPorPropiedad() {
        String titulo = "Características por propiedad (relación N:M)";
        String sql = "SELECT p.id_propiedad, p.titulo AS propiedad, "
                   + "COUNT(pc.id_caracteristica) AS num_caracteristicas, "
                   + "GROUP_CONCAT(c.nombre ORDER BY c.nombre SEPARATOR ', ') AS caracteristicas "
                   + "FROM propiedad p "
                   + "INNER JOIN propiedad_caracteristica pc ON p.id_propiedad = pc.id_propiedad "
                   + "INNER JOIN caracteristica c ON pc.id_caracteristica = c.id_caracteristica "
                   + "GROUP BY p.id_propiedad, p.titulo "
                   + "ORDER BY num_caracteristicas DESC";
        return ejecutar(titulo, sql);
    }

    /**
     * CONSULTA 4: LEFT JOIN.
     * Propiedades con o sin citas (todas las propiedades visibles).
     */
    public Reporte propiedadesConCitas() {
        String titulo = "Propiedades y número de citas (LEFT JOIN)";
        String sql = "SELECT p.titulo AS propiedad, c.nombre AS ciudad, "
                   + "COUNT(ci.id_cita) AS num_citas "
                   + "FROM propiedad p "
                   + "INNER JOIN ciudad c ON p.id_ciudad = c.id_ciudad "
                   + "LEFT JOIN cita ci ON p.id_propiedad = ci.id_propiedad "
                   + "GROUP BY p.id_propiedad, p.titulo, c.nombre "
                   + "ORDER BY num_citas DESC";
        return ejecutar(titulo, sql);
    }

    /**
     * CONSULTA 5: Agregación con GROUP BY y HAVING.
     * Ciudades con 2 o más propiedades disponibles.
     */
    public Reporte ciudadesConMuchasPropiedades() {
        String titulo = "Ciudades con más de 2 propiedades en el catálogo (GROUP BY + HAVING)";
        String sql = "SELECT c.nombre AS ciudad, c.departamento, COUNT(p.id_propiedad) AS total, "
                   + "ROUND(AVG(p.precio),0) AS precio_promedio, "
                   + "MAX(p.precio) AS precio_maximo "
                   + "FROM ciudad c "
                   + "INNER JOIN propiedad p ON p.id_ciudad = c.id_ciudad "
                   + "WHERE p.activa = 1 "
                   + "GROUP BY c.nombre, c.departamento "
                   + "HAVING COUNT(p.id_propiedad) > 2 "
                   + "ORDER BY total DESC";
        return ejecutar(titulo, sql);
    }

    /**
     * Reporte extra: citas por estado.
     */
    public Reporte citasPorEstado() {
        String titulo = "Citas por estado";
        String sql = "SELECT estado, COUNT(*) AS total, COUNT(DISTINCT id_cliente) AS clientes_distintos "
                   + "FROM cita "
                   + "GROUP BY estado "
                   + "ORDER BY total DESC";
        return ejecutar(titulo, sql);
    }

    /**
     * Reporte extra: propiedades por estado.
     */
    public Reporte propiedadesPorEstado() {
        String titulo = "Propiedades por estado";
        String sql = "SELECT estado, COUNT(*) AS total "
                   + "FROM propiedad "
                   + "WHERE activa = 1 "
                   + "GROUP BY estado "
                   + "ORDER BY total DESC";
        return ejecutar(titulo, sql);
    }

    /**
     * Reporte extra: propiedades disponibles por tipo.
     */
    public Reporte propiedadesPorTipo() {
        String titulo = "Propiedades disponibles por tipo";
        String sql = "SELECT tp.nombre AS tipo_propiedad, COUNT(p.id_propiedad) AS disponibles, "
                   + "MIN(p.precio) AS precio_minimo, MAX(p.precio) AS precio_maximo "
                   + "FROM tipo_propiedad tp "
                   + "LEFT JOIN propiedad p ON p.id_tipo = tp.id_tipo AND p.activa = 1 "
                   + "GROUP BY tp.nombre "
                   + "HAVING COUNT(p.id_propiedad) >= 1 "
                   + "ORDER BY disponibles DESC";
        return ejecutar(titulo, sql);
    }

    /**
     * Ejecuta una consulta y la convierte en Reporte con todas las columnas.
     */
    private Reporte ejecutar(String titulo, String sql) {
        List<String> encabezados = new ArrayList<>();
        List<List<String>> filas = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            ResultSetMetaData meta = rs.getMetaData();
            int numCols = meta.getColumnCount();
            for (int i = 1; i <= numCols; i++) {
                encabezados.add(meta.getColumnLabel(i));
            }
            while (rs.next()) {
                List<String> fila = new ArrayList<>();
                for (int i = 1; i <= numCols; i++) {
                    String valor = rs.getString(i);
                    fila.add(valor == null ? "" : valor);
                }
                filas.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al ejecutar reporte: " + e.getMessage());
            encabezados.add("Error");
            List<String> fila = new ArrayList<>();
            fila.add("No fue posible generar el reporte. Verifique la conexión a la base de datos.");
            filas.add(fila);
        }
        return new Reporte(titulo, encabezados, filas);
    }
}