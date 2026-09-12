# InmoVaIn Soluciones - Inmobiliaria Web (Java EE)

Aplicación web para la gestión integral de una inmobiliaria construida con **Java EE (Servlets + JSP)**, **JDBC** y **MySQL/MariaDB**, desplegable en **Apache Tomcat**.

> Sin frameworks de alto nivel (nada de Spring Boot, Node, PHP, Laravel o React): todo sobre el estándar Java EE con arquitectura **MVC**.

## Características

- **Autenticación por roles** (`Administrador`, `Agente Inmobiliario`, `Cliente`) con contraseñas **BCrypt**.
- **Catálogo público** de propiedades con filtros (ciudad, tipo, precio, características, término) y fichas de detalle con galería.
- **Cliente**: favoritos, solicitudes de cita, solicitudes de compra/alquiler con **subida de documentos** y perfil editable.
- **Agente**: CRUD de propiedades con imágenes y características (relación N:M), gestión de citas y solicitudes, consulta de reportes.
- **Administrador**: usuarios y roles, inmobiliarias, ciudades, tipos de propiedad, características, **auditoría** y **reportes SQL**.
- **Reportes SQL** (múltiples JOIN, N:M, LEFT JOIN, GROUP BY/HAVING).
- **Registro de auditoría** de acciones sensibles (creación/modificación de usuarios, citas, solicitudes, documento, contraseña, perfil).
- Validaciones de formulario con mensajes de error amigables y protección contra inyección SQL (`PreparedStatement`), XSS (escape de salida) y control de acceso por filtro.

## Tecnologías

| Capa | Tecnología |
|------|------------|
| Cliente | HTML5, CSS3, JavaScript, Bootstrap 5.3, Bootstrap Icons |
| Servidor | Java 8+, Servlets 3.1 (compatible 4.0), JSP + JSTL |
| Persistencia | JDBC + MySQL/MariaDB |
| Seguridad | jBCrypt (BCrypt), sesiones HttpOnly, protección anti fijación de sesión |
| Servidor de aplicación | Apache Tomcat 8.5+ / 9.x (probado en 8.5.96) |

## Repositorio, tablero y evidencia

- **Git:** repositorio público en `https://github.com/CammDz/inmobiliaria` (historial de commits por sprint).
- **Tablero Scrum (Padlet):** https://padlet.com/dayroncamilo4/inmovain-soluciones-tablero-scrum-s023incmxbtuuuhomv53 (PDF en `docs/tablero_padlet.pdf` y detalle en `docs/TABLERO_PADLET.md`).
- **Documentación:** MER y modelo relacional en `MODELO_RELACIONAL.md`, diccionario de datos en `DICCIONARIO_DATOS.md`,
  sprints en `docs/SPRINT1.md`, `docs/SPRINT2.md`, `docs/SPRINT3.md` y cierre en `SCRUM_FINAL.md`.

## Estructura del proyecto

```
inmobiliaria/
+-- sql/                        # Scripts de base de datos
|   +-- 00_reset.sql            # Reinicio total (opcional)
|   +-- 01_database.sql         # Crear BD
|   +-- 02_tables.sql           # 17 tablas
|   +-- 03_constraints.sql      # FKs, índices, UNIQUEs
|   +-- 04_seed.sql             # Datos de prueba (10+ por tabla, BCrypt)
+-- src/main/java/
|   +-- com/inmobiliaria/
|       +-- model/              # Clases de modelo
|       +-- dao/                # Acceso a datos (JDBC, PreparedStatement)
|       +-- controller/         # Servlets (admin, agente, cliente)
|       +-- filter/             # AuthFilter y EncodingFilter
|       +-- util/               # DbConfig, BCrypt, validación, inicializador
+-- src/main/webapp/            # JSP, CSS, JS, imágenes, partials
|   +-- WEB-INF/web.xml
|   +-- WEB-INF/jspf/           # Partial: navbar, footer, mensajes
|   +-- admin/                  # Vistas del administrador
|   +-- agente/                 # Vistas del agente
|   +-- cliente/                # Vistas del cliente
|   +-- propiedades/            # Catálogo y detalle (público)
|   +-- error/                  # Páginas 404 y 500
|   +-- recuperar.jsp           # Recuperación de contraseña
|   +-- restablecer.jsp         # Cambio de contraseña con token
+-- lib/                        # Jars de dependencias
+-- build/                      # Clases compiladas, WAR y staging
+-- database.properties.example # Plantilla de configuración de BD
```

## Requisitos

- JDK 8 o superior (probado con JDK 21 y 25)
- Apache Tomcat 8.5+ / 9.x (probado con 8.5.96 de XAMPP)
- MySQL 8 / MariaDB 10.x
- Conector JDBC de MySQL incluido en `lib/`

## Instalación (paso a paso)

### 1. Base de datos

Ejecuta en orden los scripts de `sql/` sobre tu MySQL:

```bash
mysql -u root -p < sql/01_database.sql
mysql -u root -p < sql/02_tables.sql
mysql -u root -p < sql/03_constraints.sql
mysql -u root -p < sql/04_seed.sql
```

> O reinicia la BD: `mysql -u root -p < sql/00_reset.sql` antes de los anteriores.

### 2. Configuración de la conexión

Copia la plantilla y ajusta tus credenciales:

```bash
copy database.properties.example src\main\webapp\WEB-INF\classes\database.properties
```

El archivo `database.properties` está en `.gitignore` (no se sube al repositorio).
También puede configurar variables de entorno: `DB_HOST`, `DB_PORT`, `DB_NAME`,
`DB_USER`, `DB_PASSWORD`, `DB_SSL`, `DB_TIMEZONE` (ver `INSTALACION_LOCAL.md`).

### 3. Compilación (sin Maven)

```bash
javac --release 11 -encoding UTF-8 -parameters -d build\classes -cp "lib\*" (dir /s /b src\main\java\*.java)
```

### 4. Despliegue (WAR recomendado)

```bash
jar cf build\inmobiliaria.war -C build\war_stage .
copy build\inmobiliaria.war C:\xampp\tomcat\webapps\
```

O despliegue "exploded": copie el contenido de `src/main/webapp/`, `build/classes`
y `lib/` a `webapps/inmobiliaria/`.

> Ver `INSTALACION_LOCAL.md` para la guía completa con evidencia de cada paso.

### 5. Inicia Tomcat

```bash
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.10
C:\xampp\tomcat\bin\catalina.bat start
```

Al arrancar, `DataInitializer` crea usuarios y propiedades de demostración **solo
si la tabla `usuario` está vacía**. Con el script `04_seed.sql` ejecutado, los
datos ya existen y el listener se salta.

Accede a **http://localhost:8080/inmobiliaria/**

## Usuarios de prueba (contraseña: `123456`)

| Rol | Correo |
|-----|--------|
| Administrador | `admin@inmovain.com`, `admin2@inmovain.com` |
| Agente Inmobiliario | `agente1@inmovain.com`, `agente2@inmovain.com`, `agente3@inmovain.com` |
| Cliente | `cliente1@inmovain.com`, ..., `cliente7@inmovain.com` |

## Documentación

- `INSTALACION_LOCAL.md` - instalación completa paso a paso (probada).
- `BASE_DATOS_EN_LINEA.md` - cómo apuntar a una base remota.
- `MER_DBEAVER.md` - diagrama Entidad-Relación en DBeaver.
- `MODELO_RELACIONAL.md`, `DICCIONARIO_DATOS.md` - modelo y estructura de datos.
- `CONSULTAS_SQL.md` - las consultas SQL exigidas con resultados reales.
- `PRUEBAS_FUNCIONALES.md`, `PRUEBAS_UNITARIAS.md` - evidencia de pruebas.
- `SCRUM_FINAL.md` - cierre del ciclo Scrum.
- `AUDITORIA_PROYECTO.md` - auditoría del proyecto con evidencia.
- `docs/DOCUMENTACION.md` - documentación técnica extendida.
- `docs/SPRINT1.md` ... `docs/SPRINT3.md` - actas de los sprints.

## Reportes generados por SQL (`ReporteDAO`)

1. Propiedades activas por ciudad/tipo/estado (**INNER JOIN x 4 tablas** + GROUP BY).
2. Solicitudes por inmobiliaria (**INNER JOIN x 4 tablas**).
3. Características por propiedad (**relación N:M** con GROUP BY + GROUP_CONCAT).
4. Propiedades y número de citas (**LEFT JOIN**).
5. Ciudades con 2+ propiedades (**GROUP BY + HAVING**).
6. Citas por estado (GROUP BY).
7. Propiedades por estado (GROUP BY).
8. Propiedades disponibles por tipo (LEFT JOIN + GROUP BY + HAVING).

Todas con resultados reales documentados en `CONSULTAS_SQL.md`.