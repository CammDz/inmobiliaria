package com.inmobiliaria.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase centralizada para la conexión a la base de datos.
 *
 * <p>Lee la configuración desde {@link DbConfig} (variables de entorno,
 * archivo {@code database.properties} o valores por defecto).
 * Para cambiar entre base de datos local y online solo se modifica
 * {@code database.properties} o se asignan las variables de entorno
 * correspondientes.</p>
 */
public class DatabaseConnection {

    private static final String URL = DbConfig.construirUrl();

    static {
        try {
            Class.forName(DbConfig.DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontró el driver JDBC: " + DbConfig.DRIVER, e);
        }
        System.out.println("[DatabaseConnection] Conectando con: " + DbConfig.resumen());
    }

    private DatabaseConnection() {}

    /**
     * Obtiene una conexión a la base de datos.
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, DbConfig.USER, DbConfig.PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("Error al conectar con la base de datos (" + DbConfig.HOST + ":" + DbConfig.PORT
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
