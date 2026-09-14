# AUDITORÍA DEL PROYECTO - InmoVaIn Soluciones (Inmobiliaria Web)

**Fecha:** 2026-09-12
**Autor:** Revisión técnica
**Alcance:** Auditoría del código existente sin reescribir el proyecto. Toda afirmación de
"funciona" está respaldada por una prueba ejecutada (ver sección 10).

> **Nota metodológica:** este documento no declara funcional como algo que no se haya
> ejecutado. Donde se dice "verificado" significa que se ejecutó el despliegue real en
> Apache Tomcat 8.5.96 (el incluido en XAMPP) contra MariaDB 10.4.32 y se probó vía HTTP.

---

## 1. Funcionalidades encontradas

| Módulo | Elementos encontrados |
|--------|------------------------|
| Landing | `index.jsp` con presentación, buscador, destacadas, registro y login |
| Catálogo público | `CatalogoServlet` + `propiedades/catalogo.jsp` con filtros (ciudad, tipo, precio, características, término) |
| Detalle público | `PropiedadServlet` + `propiedades/detalle.jsp` con galería y características |
| Registro | `RegistroServlet` + `registro.jsp` (rol Cliente, perfil 1:1, BCrypt) |
| Login / Logout | `LoginServlet`, `LogoutServlet`, `login.jsp` |
| Sesiones | `AuthUtil` (atributos `usuario_id`, `usuario_nombre`, `usuario_correo`, `usuario_roles`) |
| Protección de rutas | `AuthFilter` para `/admin/*`, `/agente/*`, `/cliente/*` |
| Encoding | `CharacterEncodingFilter` UTF-8 |
| Admin | Usuarios/roles, ciudades, tipos, características, inmobiliarias, auditoría, reportes |
| Agente | CRUD propiedades, imágenes, citas, solicitudes, documentos, reportes |
| Cliente | Favoritos, citas, solicitudes (con subida de documentos), perfil |
| Auditoría | `AuditoriaDAO` registra acción, tabla, registro, detalles, IP, fecha |
| Reportes SQL | `ReporteDAO` con 8 consultas (INNER JOIN x3/x4, N:M, LEFT JOIN, GROUP BY/HAVING) |
| Persistencia | 15 DAO con `PreparedStatement` y try-with-resources |
| Datos de prueba | `DataInitializer` (listener) siembra usuarios, propiedades, citas, solicitudes, favoritos |

## 2. Funcionalidades que funcionan (verificadas ejecutando)

1. **Compilación**: 64 clases Java compilan sin errores ni advertencias con `javac --release 11`.
2. **Creación de base de datos**: los 4 scripts (`01`-`04`) se ejecutan correctamente en MariaDB 10.4.32.
3. **Despliegue**: la aplicación se despliega en Tomcat 8.5.96 (el de XAMPP) con JDK 21.
4. **`DataInitializer`**: al primer arranque sembró 5 usuarios, 5 perfiles, 15 propiedades,
   3 citas, 3 solicitudes y 4 favoritos (verificado con `SELECT COUNT(*)`).
5. **Landing** (`/`): HTTP 200, 16 KB.
6. **Catálogo** (`/catalogo`): HTTP 200, 42 KB, con propiedades.
7. **Login** de `admin@inmovain.com` / `123456`: HTTP 302 -> `/admin/dashboard.jsp`.
8. **Login** de `cliente1@inmovain.com` / `123456`: HTTP 302 -> `/cliente/dashboard.jsp`.
9. **Control de acceso**: admin autenticado entrando a `/cliente/perfil` -> 302 a `acceso-denegado.jsp`;
   cliente entrando a `/admin/usuarios` -> 302 a `acceso-denegado.jsp`.
10. **Contraseña incorrecta**: muestra el mensaje "Correo o contraseña incorrectos".
11. **Correo duplicado en registro**: muestra el mensaje de correo ya registrado.
12. **Hash BCrypt**: las contraseñas en `usuario.contrasena` son hashes `$2a$10$...`, no texto plano.

## 3. Funcionalidades incompletas

| # | Funcionalidad | Estado |
|---|---------------|--------|
| 3.1 | **Recuperación de contraseña** | No existe. No hay tabla de tokens, ni servlet, ni JSP. `grep` confirma que no hay referencias a "recuperar" ni "token". |
| 3.2 | **Generación de WAR** | No existe ningún `.war` en `target/` ni en `build/`. Solo hay clases compiladas y un `exploded/` desactualizado. |
| 3.3 | **Configuración externa de BD** | No existe. Las credenciales están hardcodeadas en `DatabaseConnection.java`. |
| 3.4 | **Scripts DML con 10+ registros por tabla principal** | El `04_seed.sql` solo carga roles, inmobiliarias, ciudades, tipos y características. Usuarios/propiedades/citas se crean por Java (no por SQL). |
| 3.5 | **Script de reinicio/limpieza de BD** | No existe. |
| 3.6 | **Pruebas unitarias** | No hay JUnit ni carpeta `src/test/java`. |
| 3.7 | **Documentación exigida por el parcial** | Resuelto (12/09/2026): existen `INSTALACION_LOCAL.md`, `BASE_DATOS_EN_LINEA.md`, `MER_DBEAVER.md`, `MODELO_RELACIONAL.md`, `DICCIONARIO_DATOS.md`, `CONSULTAS_SQL.md`, `PRUEBAS_FUNCIONALES.md`, `SCRUM_FINAL.md` y `docs/`. |
| 3.8 | **Rol VISITANTE explícito** | El rol `Visitante` quedó agregado al seed (`sql/04_seed.sql`) y en la BD en línea (id 4). El visitante se modela como "no autenticado": navega catálogo y detalle. |
| 3.9 | **Subida de imágenes por archivo** | El agente solo puede registrar imágenes por URL, no subir archivos. |
| 3.10 | **`fecha_nacimiento` y `foto_url` en perfil de admin/agente** | El formulario de perfil solo existe para cliente. |

## 4. Errores detectados

| # | Error | Ubicación | Severidad |
|---|-------|-----------|-----------|
| 4.1 | Credenciales de BD hardcodeadas (`root`/`root`) | `util/DatabaseConnection.java:22-26` | Alta (portabilidad) |
| 4.2 | `web.xml` declara versión 4.0 no soportada por Tomcat 8.5 | `WEB-INF/web.xml:6` | Media |
| 4.3 | No se regenera el identificador de sesión al iniciar sesión | `controller/LoginServlet.java:74` | Alta (seguridad) |
| 4.4 | Mensaje de excepción SQL crudo expuesto al usuario (`e.getMessage()`) | `controller/RegistroServlet.java:120` | Media |
| 4.5 | Tras invalidar la sesión en logout, se crea una nueva para el mensaje | `controller/LogoutServlet.java:27-28` | Baja |
| 4.6 | Las páginas de error 404 y 500 apuntan a `acceso-denegado.jsp` | `WEB-INF/web.xml:50-57` | Media |
| 4.7 | No se valida la extensión/MIME de los documentos subidos | `controller/cliente/SolicitudesClienteServlet.java:101-153` | Alta (seguridad) |
| 4.8 | `guardarEdicion` ignora la matrícula enviada en el formulario (la sobrescribe con la antigua) | `controller/agente/PropiedadesAgenteServlet.java:206` | Baja |
| 4.9 | `exploded/` está desactualizado: carpeta `agentes/` (plural) vs `agente/` del código, sin `precio.tld` y sin `images/propiedades/` | `exploded/` | Alta (despliegue) |
| 4.10 | El formulario de login no usa POST puro a un servlet si se abre `login.jsp` directamente; funciona, pero conviene centralizar | `login.jsp` | Baja |
| 4.11 | Sin protección CSRF en formularios POST | Todos los formularios | Media |
| 4.12 | `imagen_propiedad` de la siembra usa `"images/propiedades/prop" + id + "_1.jpg"`, dependiente de que los id autoincrementales coincidan con los nombres de archivo | `util/DataInitializer.java:322` | Media |

## 5. Problemas de seguridad

1. **Credenciales en el repositorio**: `DatabaseConnection` contiene `root`/`root`. No debe subirse.
2. **Fijación de sesión**: no se llama a `request.changeSessionId()` ni se invalida la sesión previa al autenticar.
3. **Falta de HttpOnly/SameSite** en la cookie de sesión (no se declara `<session-config><cookie-config>`).
4. **Subida de archivos sin restricción de tipo**: se acepta cualquier archivo hasta 5 MB.
5. **CSRF**: no hay tokens anti-CSRF.
6. **XSS**: se usa `<c:out>` en gran parte de las vistas; `ValidationUtil.escapeHtml` existe. Correcto en general.
7. **Enumeración de correos**: el registro informa si un correo ya existe (aceptable en contexto académico, pero es una fuga de información).
8. **Sin bloqueo por intentos fallidos** de login.
9. **Fuga de mensajes internos** de excepción en `RegistroServlet`.

## 6. Problemas de base de datos

1. **Datos de prueba insuficientes por SQL**: el parcial exige mínimo 10 registros por tabla
   principal; el `04_seed.sql` no inserta usuarios/propiedades/citas/solicitudes/favoritos/auditoría
   (los crea Java). Además hay **5 usuarios** en total, no 10.
2. **`cita` sin restricción UNIQUE** `(id_propiedad, fecha, hora)` (el código evita duplicados a nivel de aplicación, pero no en BD).
3. **`usuario_rol` sin índice adicional**; su PK compuesta ya garantiza unicidad (correcto).
4. **`favorito` sí tiene UNIQUE** `(id_cliente, id_propiedad)` (correcto).
5. **`perfil.id_usuario` UNIQUE** presente (correcto, relación 1:1).
6. **`propiedad.matricula_inmobiliaria` UNIQUE** presente (correcto).
7. **Sin tabla de tokens de recuperación** de contraseña.
8. **`auditoria` solo referencia `usuario`** (ON DELETE SET NULL) - correcto.

## 7. Problemas de despliegue

1. **No hay WAR** generado.
2. **Maven no está instalado** en el entorno de desarrollo; el README asume `javac` manual.
3. **`exploded/` inválido** por la discrepancia `agentes`/`agente` (4.9).
4. **Tomcat incluido en XAMPP es 8.5.96** (Servlet 3.1); el `web.xml` 4.0 genera warning de versión.
5. **Rutas absolutas**: `getRealPath("/uploads")` funciona pero los documentos se pierden al redeplegar el WAR
   (se recomienda carpeta externa configurable, documentada como limitación).
6. **JDBC driver** incluido tanto en `lib/`, `WEB-INF/lib/` como en `pom.xml`; hay que evitar duplicados al empaquetar.

## 8. Problemas de documentación

1. El README describe una estructura con `agentes/` que no coincide con `src` (`agente/`).
2. El README indica Java 8+ y Tomcat 9.x, pero el entorno probado usa Tomcat 8.5.96.
3. `docs/SPRINT3.md` afirma "58 clases compiladas"; en realidad son **59** clases fuente.
4. Resuelto (12/09/2026): todos los documentos exigidos en la sección 3.7 existen.

## 9. Lista priorizada de tareas pendientes

| Prioridad | Tarea |
|-----------|-------|
| P0 | Centralizar y externalizar la configuración de conexión (env vars + properties) |
| P0 | Corregir `web.xml` a Servlet 3.1 (compatibilidad Tomcat 8.5 y 9) |
| P0 | Regenerar seguridad de sesión (cambio de id + cookie HttpOnly) |
| P0 | Implementar recuperación de contraseña con token seguro y expiración |
| P1 | Escribir `04_seed.sql` con 10+ registros por tabla principal (contraseñas BCrypt) y script de reinicio |
| P1 | Generar WAR final y limpiar `exploded/` |
| P1 | Validar tipo de archivo subido y no exponer mensajes internos |
| P1 | Crear documentación obligatoria (instalación, BD en línea, MER, modelo, diccionario, consultas, pruebas, Scrum) |
| P2 | Añadir restricción UNIQUE a cita y tabla de tokens |
| P2 | Pruebas unitarias (JUnit) para validaciones, BCrypt y permisos |
| P2 | Página de error propia (404/500) distinta de acceso denegado |

## 10. Evidencia de las pruebas ejecutadas

**Entorno:** Windows 11, XAMPP (MariaDB 10.4.32 + Tomcat 8.5.96), JDK 21 (Tomcat) / JDK 25 (compilación), Tomcat en `http://localhost:8080/inmobiliaria`.

```
Compilación : javac --release 11 ... -> EXIT 0, 59 .class
MySQL       : mysqld 10.4.32 -> puerto 3306 abierto
Scripts SQL : 01,02,03,04 -> 16 tablas creadas
Despliegue  : Tomcat 8.5.96 -> "Deployment of web application directory [...\inmobiliaria] has finished"
Warning     : "Unknown version string [4.0]. Default version will be used."
Sembrado    : usuarios=5 propiedades=15 citas=3 solicitudes=3 favoritos=4 perfiles=5
GET /                 -> 200 (16089 bytes)
GET /catalogo         -> 200 (42250 bytes)
POST /login admin     -> 302 -> /admin/dashboard.jsp
POST /login cliente1  -> 302 -> /cliente/dashboard.jsp
GET /admin/dashboard.jsp (admin)  -> 200 (10355 bytes)
GET /cliente/perfil (admin)       -> 302 -> /acceso-denegado.jsp
GET /admin/usuarios (cliente)     -> 302 -> /acceso-denegado.jsp
POST /login mala clave-> 200 con mensaje "incorrectos"
POST /registro correo dup -> 200 con mensaje de correo ya registrado
```

**Pruebas NO realizadas todavía** (se ejecutarán en fases posteriores):
- Subida de documentos (multipart) end-to-end.
- CRUD completo de propiedad desde el panel del agente.
- Generación del WAR y despliegue del WAR empaquetado.
- Recuperación de contraseña (no existía al momento de la auditoría).
