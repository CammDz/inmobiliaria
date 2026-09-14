package com.inmobiliaria.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Clase centralizada para la conexión a la base de datos.
 *
 * <p>Utiliza un pool de conexiones HikariCP: las consultas reutilizan la misma
 * conexión en lugar de abrir una nueva cada vez (clave cuando la base de datos
 * es remota y cada nueva conexión paga el handshake TCP/SSL por internet).</p>
 *
 * <p>Lee la configuración desde {@link DbConfig} (variables de entorno,
 * archivo {@code database.properties} o valores por defecto).
 * Para cambiar entre base de datos local y online solo se modifica
 * {@code database.properties} o se asignan las variables de entorno
 * correspondientes.</p>
 */
public class DatabaseConnection {

    private static final HikariDataSource DATA_SOURCE;

    static {
        try {
            Class.forName(DbConfig.DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontró el driver JDBC: " + DbConfig.DRIVER, e);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DbConfig.construirUrl());
        config.setUsername(DbConfig.USER);
        config.setPassword(DbConfig.PASSWORD);
        config.setDriverClassName(DbConfig.DRIVER);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(10_000);
        config.setIdleTimeout(300_000);
        config.setMaxLifetime(1_800_000);
        config.setValidationTimeout(5_000);
        // No detener la app si la BD está temporalmente caída; el error sale por consulta.
        config.setInitializationFailTimeout(-1);

        DATA_SOURCE = new HikariDataSource(config);
        Runtime.getRuntime().addShutdownHook(new Thread(DATA_SOURCE::close));
        System.out.println("[DatabaseConnection] Pool HikariCP listo: " + DbConfig.resumen());
    }

    private DatabaseConnection() {}

    /**
     * Obtiene una conexión del pool a la base de datos.
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DATA_SOURCE.getConnection();
        } catch (SQLException e) {
            throw new SQLException("Error al obtener conexión del pool (" + DbConfig.HOST + ":" + DbConfig.PORT
                    + "/" + DbConfig.NAME + "). "
                    + "Verifique que MySQL/MariaDB esté activo y que los datos de conexión sean correctos.", e);
        }
    }

    /**
     * Cierra una conexión de forma segura.
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
