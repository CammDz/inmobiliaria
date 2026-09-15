package com.inmobiliaria.util;

import com.inmobiliaria.dao.CaracteristicaDAO;
import com.inmobiliaria.dao.CitaDAO;
import com.inmobiliaria.dao.FavoritoDAO;
import com.inmobiliaria.dao.ImagenPropiedadDAO;
import com.inmobiliaria.dao.PerfilDAO;
import com.inmobiliaria.dao.PropiedadDAO;
import com.inmobiliaria.dao.RolDAO;
import com.inmobiliaria.dao.SolicitudDAO;
import com.inmobiliaria.dao.UsuarioDAO;
import com.inmobiliaria.model.Cita;
import com.inmobiliaria.model.Caracteristica;
import com.inmobiliaria.model.ImagenPropiedad;
import com.inmobiliaria.model.Perfil;
import com.inmobiliaria.model.Propiedad;
import com.inmobiliaria.model.Rol;
import com.inmobiliaria.model.Solicitud;
import com.inmobiliaria.model.Usuario;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Date;
import java.util.List;

/**
 * Inicializa los datos de prueba al arrancar la aplicación:
 * - Si no existen usuarios, crea los usuarios por rol (Administrador, Agente, Cliente)
 *   con contraseña BCrypt y su perfil 1:1.
 * - Si no existen propiedades, crea un conjunto de propiedades, imágenes,
 *   características (N:M), citas, solicitudes y favoritos.
 * Es idempotente: no duplica datos si ya fueron cargados.
 */
public class DataInitializer implements ServletContextListener {

    // Contraseña de prueba para todos los usuarios de ejemplo.
    private static final String PASSWORD_DEMO = "123456";

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RolDAO rolDAO = new RolDAO();
    private final PerfilDAO perfilDAO = new PerfilDAO();
    private final PropiedadDAO propiedadDAO = new PropiedadDAO();
    private final ImagenPropiedadDAO imagenDAO = new ImagenPropiedadDAO();
    private final CaracteristicaDAO caracteristicaDAO = new CaracteristicaDAO();
    private final CitaDAO citaDAO = new CitaDAO();
    private final SolicitudDAO solicitudDAO = new SolicitudDAO();
    private final FavoritoDAO favoritoDAO = new FavoritoDAO();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (!baseDeDatosDisponible()) {
            System.out.println("[DataInitializer] No se pudo conectar a la base de datos. "
                    + "Verifique que inmobiliaria_db exista y los scripts SQL se hayan ejecutado.");
            return;
        }
        try {
            verificarColumnaDocumentos();
            semillaUsuarios();
            semillaPropiedades();
        } catch (Exception e) {
            System.err.println("[DataInitializer] Error durante la inicialización de datos: " + e.getMessage());
        }
    }

    /**
     * Migración automática: agrega la columna MEDIUMBLOB que guarda el contenido
     * de los documentos (el disco de Render es efímero). Es idempotente.
     */
    private void verificarColumnaDocumentos() {
        String sqlBuscar = "SELECT COUNT(*) FROM information_schema.COLUMNS "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'documento_solicitud' AND COLUMN_NAME = 'contenido'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlBuscar);
             ResultSet rs = ps.executeQuery()) {
            boolean existe = rs.next() && rs.getInt(1) > 0;
            if (!existe) {
                try (Statement st = conn.createStatement()) {
                    st.execute("ALTER TABLE documento_solicitud ADD COLUMN contenido MEDIUMBLOB NULL AFTER tipo_documento");
                    System.out.println("[DataInitializer] Columna contenido agregada a documento_solicitud.");
                }
            }
        } catch (SQLException e) {
            System.err.println("[DataInitializer] No se pudo verificar/crear la columna contenido: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Sin acciones al apagar.
    }

    private boolean baseDeDatosDisponible() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM rol");
             ResultSet rs = ps.executeQuery()) {
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Crea los usuarios de ejemplo y sus perfiles si no existen.
     */
    private void semillaUsuarios() {
        List<Usuario> existentes = usuarioDAO.listarTodos();
        if (!existentes.isEmpty()) {
            System.out.println("[DataInitializer] Usuarios ya existentes (" + existentes.size()
                    + "). Se omite la creación.");
            return;
        }

        Rol admin = rolDAO.buscarPorNombre("Administrador");
        Rol agente = rolDAO.buscarPorNombre("Agente Inmobiliario");
        Rol cliente = rolDAO.buscarPorNombre("Cliente");
        if (admin == null || agente == null || cliente == null) {
            System.out.println("[DataInitializer] No se encontraron los roles base. "
                    + "Ejecute el script 04_seed.sql antes de iniciar.");
            return;
        }

        String hash = PasswordUtil.hashPassword(PASSWORD_DEMO);

        // Administrador
        int idAdmin = nuevoUsuario("Sandra", "Álvarez", "admin@inmovain.com",
                "300-1112233", hash);
        if (idAdmin > 0) {
            rolDAO.asignarRol(idAdmin, admin.getIdRol());
            crearPerfil(idAdmin, "Administradora principal del sistema.", "CC-1012345", null);
        }

        // Agentes (dos para alimentar los reportes)
        int idAgente1 = nuevoUsuario("Felipe", "Gómez", "agente1@inmovain.com",
                "300-2223344", hash);
        if (idAgente1 > 0) {
            rolDAO.asignarRol(idAgente1, agente.getIdRol());
            crearPerfil(idAgente1, "Asesor sénior de ventas.", "CC-2023456", null);
        }

        int idAgente2 = nuevoUsuario("Mariana", "Londoño", "agente2@inmovain.com",
                "300-3334455", hash);
        if (idAgente2 > 0) {
            rolDAO.asignarRol(idAgente2, agente.getIdRol());
            crearPerfil(idAgente2, "Asesora de arrendamientos.", "CC-3034567", null);
        }

        // Clientes (dos para alimentar citas y solicitudes)
        int idCliente1 = nuevoUsuario("José", "Ramírez", "cliente1@inmovain.com",
                "300-4445566", hash);
        if (idCliente1 > 0) {
            rolDAO.asignarRol(idCliente1, cliente.getIdRol());
            crearPerfil(idCliente1, "Cliente particular en busca de vivienda.", "CC-4045678",
                    Date.valueOf("1985-04-12"));
        }

        int idCliente2 = nuevoUsuario("Luisa", "Fernández", "cliente2@inmovain.com",
                "300-5556677", hash);
        if (idCliente2 > 0) {
            rolDAO.asignarRol(idCliente2, cliente.getIdRol());
            crearPerfil(idCliente2, "Cliente en busca de local comercial.", "CC-5056789",
                    Date.valueOf("1990-09-25"));
        }

        System.out.println("[DataInitializer] Usuarios de ejemplo creados. "
                + "Contraseña para todos: " + PASSWORD_DEMO);
    }

    private int nuevoUsuario(String nombre, String apellido, String correo,
                             String telefono, String hash) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setCorreo(correo);
        u.setTelefono(telefono);
        u.setContrasena(hash);
        u.setActivo(true);
        try {
            return usuarioDAO.insertar(u);
        } catch (SQLException e) {
            System.err.println("[DataInitializer] Error creando usuario " + correo + ": " + e.getMessage());
            return -1;
        }
    }

    private void crearPerfil(int idUsuario, String direccion, String documento, Date fechaNacimiento) {
        Perfil p = new Perfil();
        p.setIdUsuario(idUsuario);
        p.setDireccion(direccion);
        p.setDocumentoIdentidad(documento);
        p.setFechaNacimiento(fechaNacimiento);
        if (!perfilDAO.crear(p)) {
            System.err.println("[DataInitializer] No se pudo crear el perfil del usuario " + idUsuario);
        }
    }

    /**
     * Crea propiedades de ejemplo con imágenes, características y datos de negocio.
     */
    private void semillaPropiedades() {
        List<Propiedad> existentes = propiedadDAO.listarTodas();
        if (!existentes.isEmpty()) {
            System.out.println("[DataInitializer] Propiedades ya existentes (" + existentes.size()
                    + "). Se omite la creación.");
            return;
        }

        List<Usuario> agentes = usuarioDAO.listarPorRol(2);
        List<Usuario> clientes = usuarioDAO.listarPorRol(3);
        if (agentes.isEmpty() || clientes.isEmpty()) {
            System.out.println("[DataInitializer] No hay agentes o clientes para asignar. Se omite la siembra de propiedades.");
            return;
        }

        int idAgente1 = agentes.get(0).getIdUsuario();
        int idAgente2 = agentes.size() > 1 ? agentes.get(1).getIdUsuario() : idAgente1;
        int idCliente1 = clientes.get(0).getIdUsuario();
        int idCliente2 = clientes.size() > 1 ? clientes.get(1).getIdUsuario() : idCliente1;

        List<Caracteristica> caracteristicas = caracteristicaDAO.listarTodas();

        // [titulo, precio, idCiudad, idTipo, idInmobiliaria, matricula, estado,
        //  tipoOperacion, direccion, areaM2, habitaciones, banos, parqueaderos, idAgente, descripcion]
        Object[][] datos = {
                {"Apto 101 Edificio Torres del Prado", 520000000.0, 1, 1, 1, "ATP-0001", "DISPONIBLE", "VENTA",
                        "Cra 45 #67-10, Medellín", 76.5, 3, 2, 1, idAgente1,
                        "Apartamento en estrato 5 con excelente vista, cerca al metro y zonas comerciales."},
                {"Casa dos pisos Bello Horizonte", 690000000.0, 5, 2, 2, "CBH-0002", "DISPONIBLE", "VENTA",
                        "Calle 48 #20-15, Bucaramanga", 180.0, 4, 3, 2, idAgente1,
                        "Casa amplia con jardín, salón social y garaje para dos vehículos."},
                {"Local comercial centro financiero", 480000000.0, 3, 3, 3, "LNC-0003", "DISPONIBLE", "ALQUILER",
                        "Cra 7 #32-50, Cali", 45.0, 0, 1, 0, idAgente2,
                        "Local en pleno centro financiero, ideal para oficinas y puntos de venta."},
                {"Oficina piso 12 Torre Empresarial", 650000000.0, 2, 4, 4, "OTE-0004", "DISPONIBLE", "ALQUILER",
                        "Cra 15 #88-22, Bogotá", 60.0, 0, 1, 1, idAgente2,
                        "Oficina modernamente amoblada con acceso a salas de juntas compartidas."},
                {"Lote urbanizable 1200 m²", 850000000.0, 4, 5, 5, "LTU-0005", "DISPONIBLE", "VENTA",
                        "Km 8 vía Barranquilla", 1200.0, 0, 0, 0, idAgente1,
                        "Lote plano con servicios públicos, apto para proyecto de vivienda."},
                {"Finca campestre con piscina", 2800000000.0, 8, 6, 6, "FCP-0006", "DISPONIBLE", "VENTA",
                        "Vereda La Esperanza, Manizales", 5000.0, 5, 4, 6, idAgente1,
                        "Finca con casa principal, cabaña de huéspedes, piscina y zona BBQ."},
                {"Penthouse vista panorámica", 3400000000.0, 1, 7, 7, "PPV-0007", "DISPONIBLE", "VENTA",
                        "Cra 80 #55-20, Medellín", 220.0, 4, 4, 3, idAgente2,
                        "Penthouse de lujo en la mejor zona de Medellín con terraza privada."},
                {"Duplex El Poblado", 1850000000.0, 1, 8, 8, "DEP-0008", "RESERVADA", "VENTA",
                        "Transversal 25 #9-30, Medellín", 140.0, 3, 3, 2, idAgente2,
                        "Dúplex elegante recientemente remodelado, cerca a Parques del Río."},
                {"Estudio microcentro", 190000000.0, 3, 9, 9, "ESM-0009", "DISPONIBLE", "ALQUILER",
                        "Cra 20 #45-15, Pereira", 32.0, 1, 1, 0, idAgente1,
                        "Apartaestudio amoblado perfecto para estudiantes y profesionales."},
                {"Casa campestre Lagos del Oriente", 2150000000.0, 10, 10, 10, "CCL-0010", "DISPONIBLE", "VENTA",
                        "Vía Bonda, Santa Marta", 3500.0, 4, 3, 4, idAgente1,
                        "Casa campestre rodeada de naturaleza con lago privado y zonas verdes."},
                {"Apto 502 Conjunto Residencial Parque", 480000000.0, 2, 1, 1, "ACP-0011", "DISPONIBLE", "ALQUILER",
                        "Cra 5 #73-18, Bogotá", 68.0, 3, 2, 1, idAgente2,
                        "Amplio apartamento con salón comunal, vigilancia y parque infantil."},
                {"Casa tradicional Bocagrande", 980000000.0, 7, 2, 2, "CTB-0012", "VENDIDA", "VENTA",
                        "Avenida San Martín #12-34, Cartagena", 210.0, 4, 3, 2, idAgente2,
                        "Hermosa casa colonial restaurada a pocas cuadras de la playa."},
                {"Local esquina C.C. Portal", 560000000.0, 3, 3, 3, "LCE-0013", "DISPONIBLE", "ALQUILER",
                        "Cra 10 #20-15, Barranquilla", 38.0, 0, 1, 1, idAgente1,
                        "Local en esquina dentro del centro comercial, alta afluencia."},
                {"Apartaestudio zona universitaria", 185000000.0, 5, 9, 4, "AUT-0014", "DISPONIBLE", "ALQUILER",
                        "Calle 45 #27-50, Bucaramanga", 30.0, 1, 1, 0, idAgente2,
                        "Ideal inversión para renta estudiantil, a dos cuadras de la UIS."},
                {"Casa quinta con jardín", 1200000000.0, 8, 2, 5, "CQJ-0015", "ALQUILADA", "ALQUILER",
                        "Avenida Santander #15-40, Manizales", 260.0, 5, 4, 3, idAgente1,
                        "Casa quinta con hermosa zona de jardín, BBQ y piscina climatizada."}
        };

        int idPropiec = 0;
        for (Object[] d : datos) {
            Propiedad p = new Propiedad();
            p.setTitulo((String) d[0]);
            p.setPrecio((Double) d[1]);
            p.setIdCiudad((Integer) d[2]);
            p.setIdTipo((Integer) d[3]);
            p.setIdInmobiliaria((Integer) d[4]);
            p.setMatriculaInmobiliaria((String) d[5]);
            p.setEstado((String) d[6]);
            p.setTipoOperacion((String) d[7]);
            p.setDireccion((String) d[8]);
            p.setAreaM2((Double) d[9]);
            p.setHabitaciones((Integer) d[10]);
            p.setBanos((Integer) d[11]);
            p.setParqueaderos((Integer) d[12]);
            p.setIdAgente((Integer) d[13]);
            p.setDescripcion((String) d[14]);
            p.setActiva(true);

            int idPropiedad = propiedadDAO.insertar(p);
            if (idPropiedad > 0) {
                idPropiec++;
                agregarImagenes(idPropiedad);
                asignarCaracteristicas(idPropiedad, caracteristicas, idPropiec);
            }
        }

        int creadas = propiedadDAO.listarTodas().size();
        if (creadas > 0) {
            // Datos de negocio: citas, solicitudes y favoritos
            List<Propiedad> todas = propiedadDAO.listarTodas();
            Propiedad p1 = todas.get(0);
            Propiedad p2 = todas.size() > 1 ? todas.get(1) : p1;
            Propiedad p3 = todas.size() > 2 ? todas.get(2) : p1;
            Propiedad p4 = todas.size() > 3 ? todas.get(3) : p1;

            crearCita(idCliente1, p1.getIdPropiedad(), "2026-09-20", "10:00",
                    "PENDIENTE", "Me interesa conocer el apartamento el fin de semana.");
            crearCita(idCliente1, p7O(todas), "2026-09-25", "16:30",
                    "APROBADA", "Cita confirmada para visitar el penthouse.");
            crearCita(idCliente2, p2.getIdPropiedad(), "2026-09-22", "09:30",
                    "PENDIENTE", "Solicitud de visita para la casa de dos pisos.");

            crearSolicitud(idCliente1, p2.getIdPropiedad(), "COMPRA", 920000000.0,
                    "PENDIENTE", "Oferta inicial, puedo negociar.");
            crearSolicitud(idCliente1, p7O(todas), "COMPRA", 2300000000.0,
                    "EN_REVISION", "Oferta seria por el penthouse.");
            crearSolicitud(idCliente2, p3.getIdPropiedad(), "ALQUILER", 3200000.0,
                    "APROBADA", "Solicitud de arrendamiento aprobada.");

            favoritoDAO.agregar(idCliente1, p1.getIdPropiedad());
            favoritoDAO.agregar(idCliente1, p2.getIdPropiedad());
            favoritoDAO.agregar(idCliente2, p7O(todas));
            favoritoDAO.agregar(idCliente2, p4.getIdPropiedad());

            System.out.println("[DataInitializer] Se sembraron " + creadas
                    + " propiedades con imágenes, características, citas, solicitudes y favoritos.");
        }
    }

    // Selecciona la propiedad penthouse (índice 6 en la lista, si existe)
    private int p7O(List<Propiedad> todas) {
        return todas.size() > 6 ? todas.get(6).getIdPropiedad() : todas.get(0).getIdPropiedad();
    }

    private void agregarImagenes(int idPropiedad) {
        ImagenPropiedad img = new ImagenPropiedad();
        img.setIdPropiedad(idPropiedad);
        img.setUrlImagen("images/propiedades/prop" + idPropiedad + "_1.jpg");
        img.setEsPrincipal(true);
        img.setActiva(true);
        imagenDAO.insertar(img);

        ImagenPropiedad img2 = new ImagenPropiedad();
        img2.setIdPropiedad(idPropiedad);
        img2.setUrlImagen("images/propiedades/prop" + idPropiedad + "_2.jpg");
        img2.setEsPrincipal(false);
        img2.setActiva(true);
        imagenDAO.insertar(img2);
    }

    private void asignarCaracteristicas(int idPropiedad, List<Caracteristica> caracteristicas, int semilla) {
        if (caracteristicas == null || caracteristicas.isEmpty()) {
            return;
        }
        // Asigna de 3 a 5 características según la semilla (rotación).
        int base = semilla % caracteristicas.size();
        int cantidad = 3 + (semilla % 3); // 3, 4 o 5
        for (int i = 0; i < cantidad; i++) {
            Caracteristica c = caracteristicas.get((base + i) % caracteristicas.size());
            caracteristicaDAO.asignarAPropiedad(idPropiedad, c.getIdCaracteristica());
        }
    }

    private void crearCita(int idCliente, int idPropiedad, String fecha, String hora,
                           String estado, String observaciones) {
        Cita cita = new Cita();
        cita.setIdCliente(idCliente);
        cita.setIdPropiedad(idPropiedad);
        cita.setFecha(Date.valueOf(fecha));
        cita.setHora(Time.valueOf(hora + ":00"));
        cita.setEstado(estado);
        cita.setObservaciones(observaciones);
        if (!citaDAO.insertar(cita)) {
            System.err.println("[DataInitializer] Error creando cita para propiedad " + idPropiedad);
        }
    }

    private void crearSolicitud(int idCliente, int idPropiedad, String tipo, double monto,
                                String estado, String observaciones) {
        Solicitud s = new Solicitud();
        s.setIdCliente(idCliente);
        s.setIdPropiedad(idPropiedad);
        s.setTipo(tipo);
        s.setEstado(estado);
        s.setMontoOfrecido(monto);
        s.setObservaciones(observaciones);
        if (!solicitudDAO.insertar(s)) {
            System.err.println("[DataInitializer] Error creando solicitud para propiedad " + idPropiedad);
        }
    }
}