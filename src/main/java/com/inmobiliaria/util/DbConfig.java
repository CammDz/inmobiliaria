package com.inmobiliaria.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuración centralizada y portable de la base de datos.
 *
 * <p>El orden de prioridad para cada valor es:</p>
 * <ol>
 *   <li>Variables de entorno (por ejemplo {@code DB_HOST}, {@code DB_PASSWORD}).</li>
 *   <li>Archivo externo indicado por el system property {@code inmobiliaria.config}
 *       o la variable de entorno {@code INMOBILIARIA_CONFIG}.</li>
 *   <li>Archivo {@code database.properties} dentro del classpath (por ejemplo
 *       {@code WEB-INF/classes/database.properties}).</li>
 *   <li>Valores por defecto (localhost, 3306, inmobiliaria_db, root, contraseña vacía).</li>
 * </ol>
 *
 * <p>De esta forma la aplicación puede conectarse tanto a un MySQL/MariaDB local
 * (XAMPP) como a una base de datos en línea sin recompilar ni tocar el código Java.</p>
 */
public final class DbConfig {

    public static final String HOST;
    public static final String PORT;
    public static final String NAME;
    public static final String USER;
    public static final String PASSWORD;
    public static final String DRIVER;
    public static final String SSL;
    public static final String TIMEZONE;
    public static final String CHARACTER_ENCODING;

    /** Origen de la configuración cargada (útil para diagnóstico). */
    public static final String ORIGEN;

    private static final String ARCHIVO_PROPIEDADES = "database.properties";

    static {
        Properties props = new Properties();

        // 1) Archivo externo (system property o variable de entorno).
        String rutaExterna = System.getProperty("inmobiliaria.config");
        if (rutaExterna == null || rutaExterna.trim().isEmpty()) {
            rutaExterna = System.getenv("INMOBILIARIA_CONFIG");
        }
        String origen = "valores por defecto";
        boolean cargado = false;

        if (rutaExterna != null && !rutaExterna.trim().isEmpty()) {
            File archivo = new File(rutaExterna.trim());
            if (archivo.isFile()) {
                try (FileInputStream in = new FileInputStream(archivo)) {
                    props.load(in);
                    origen = "archivo externo " + archivo.getAbsolutePath();
                    cargado = true;
                } catch (IOException e) {
                    System.err.println("[DbConfig] No se pudo leer el archivo externo " + archivo.getAbsolutePath()
                            + ": " + e.getMessage());
                }
            } else {
                System.err.println("[DbConfig] El archivo indicado no existe: " + archivo.getAbsolutePath());
            }
        }

        // 2) Archivo dentro del classpath.
        if (!cargado) {
            try (InputStream in = DbConfig.class.getClassLoader().getResourceAsStream(ARCHIVO_PROPIEDADES)) {
                if (in != null) {
                    props.load(in);
                    origen = "classpath:" + ARCHIVO_PROPIEDADES;
                    cargado = true;
                }
            } catch (IOException e) {
                System.err.println("[DbConfig] No se pudo leer " + ARCHIVO_PROPIEDADES + " del classpath: " + e.getMessage());
            }
        }

        // 3) Variables de entorno tienen la mayor prioridad.
        HOST = valor(props, "DB_HOST", "db.host", "localhost");
        PORT = valor(props, "DB_PORT", "db.port", "3306");
        NAME = valor(props, "DB_NAME", "db.name", "inmobiliaria_db");
        USER = valor(props, "DB_USER", "db.user", "root");
        PASSWORD = valor(props, "DB_PASSWORD", "db.password", "");
        DRIVER = valor(props, "DB_DRIVER", "db.driver", "com.mysql.cj.jdbc.Driver");
        SSL = valor(props, "DB_SSL", "db.ssl", "false");
        TIMEZONE = valor(props, "DB_TIMEZONE", "db.timezone", "UTC");
        CHARACTER_ENCODING = valor(props, "DB_CHARACTER_ENCODING", "db.characterEncoding", "UTF-8");

        ORIGEN = origen + (cargado ? "" : " (sin archivo de propiedades)");
    }

    private DbConfig() {
    }

    /**
     * Devuelve el valor de una variable de entorno (o el prefijo {@code DB_}) o,
     * si no existe, el de la propiedad, o el valor por defecto.
     */
    private static String valor(Properties props, String claveEnv, String claveProp, String porDefecto) {
        String env = System.getenv(claveEnv);
        if (env != null && !env.trim().isEmpty()) {
            return env.trim();
        }
        String prop = props.getProperty(claveProp);
        if (prop != null && !prop.trim().isEmpty()) {
            return prop.trim();
        }
        return porDefecto;
    }

    /**
     * Construye la URL JDBC de MySQL/MariaDB con los parámetros configurados.
     */
    public static String construirUrl() {
        StringBuilder url = new StringBuilder("jdbc:mysql://")
                .append(HOST).append(":").append(PORT).append("/").append(NAME)
                .append("?useSSL=").append(SSL)
                .append("&serverTimezone=").append(TIMEZONE)
                .append("&allowPublicKeyRetrieval=true")
                .append("&useUnicode=true")
                .append("&characterEncoding=").append(CHARACTER_ENCODING);
        if (esConexionRemota()) {
            // Parámetros útiles para proveedores en línea; no afectan a local.
            url.append("&connectTimeout=10000&socketTimeout=30000");
        }
        return url.toString();
    }

    private static boolean esConexionRemota() {
        return HOST != null && !HOST.equals("localhost") && !HOST.equals("127.0.0.1");
    }

    /**
     * Resumen de configuración SIN exponer la contraseña (para logs de diagnóstico).
     */
    public static String resumen() {
        return "host=" + HOST + ":" + PORT + " bd=" + NAME + " usuario=" + USER
                + " ssl=" + SSL + " origen=" + ORIGEN;
    }
}
