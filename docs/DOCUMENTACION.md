# Documentación técnica - InmoVaIn Soluciones

Aplicación web para la gestión de una inmobiliaria. Tecnología: Java EE (Servlets/JSP), JDBC + MySQL, Bootstrap.

---

## 1. Contexto y alcance

El sistema permite a una inmobiliaria administrar su portafolio de propiedades, gestionar
clientes, agentes, citas de visita, solicitudes de compra/alquiler y documentos adjuntos,
con control de acceso por roles, registros de auditoría y reportes generados con SQL.

### Actores / roles

| Rol | Funcionalidades |
|-----|-----------------|
| **Administrador** | Gestión completa de usuarios y roles, inmobiliarias, ciudades, tipos de propiedad, características; consulta de auditoría y reportes SQL. |
| **Agente Inmobiliario** | CRUD de propiedades (con imágenes y características), aprobación/rechazo de citas y solicitudes, revisión de documentos, consulta de reportes. |
| **Cliente** | Búsqueda en catálogo, favoritos, citas de visita, solicitudes de compra/alquiler con subida de documentos, edición de perfil. |
| **Visitante** | Navegar catálogo público y ver detalle de propiedades. |

---

## 2. Modelo entidad-relación (MER)

### Entidades y cardinalidades

```
rol 1 -- N usuario_rol  N -- 1 usuario
usuario 1 -- 1 perfil
usuario (agente) 1 -- N propiedad
usuario (cliente) 1 -- N cita, N solicitud, N favorito
inmobiliaria 1 -- N propiedad
ciudad 1 -- N propiedad
tipo_propiedad 1 -- N propiedad
propiedad 1 -- N imagen_propiedad
propiedad N -- M caracteristica  (N:M vía propiedad_caracteristica)
propiedad 1 -- N cita, N solicitud, N favorito
solicitud 1 -- N documento_solicitud
usuario 0 -- N auditoria (FK anulable, ON DELETE SET NULL)
```

### Diagrama de relaciones resumido (SQL)

```
rol --< usuario_rol >-- usuario --< perfil
                                 |
usuario (agente) --< propiedad >-- usuario (cliente)
inmobiliaria --< propiedad >-- ciudad
tipo_propiedad --< propiedad >-- imagen_propiedad
propiedad >-- propiedad_caracteristica <-- caracteristica
propiedad --< cita | solicitud | favorito
solicitud --< documento_solicitud
usuario --< auditoria
```

---

## 3. Diccionario de datos (tablas)

### rol
| Columna | Tipo | Descripción |
|---------|------|-------------|
| id_rol | INT PK | Identificador |
| nombre | VARCHAR(50) UNIQUE | Nombre del rol |
| descripcion | VARCHAR(255) | Detalle |

### usuario
| Columna | Tipo | Descripción |
|---------|------|-------------|
| id_usuario | INT PK | Identificador |
| correo | VARCHAR(150) UNIQUE | Correo (login) |
| contrasena | VARCHAR(255) | Hash BCrypt |
| nombre, apellido | VARCHAR(100) | Datos personales |
| telefono | VARCHAR(20) | Contacto |
| activo | TINYINT(1) | 1=activo, 0=desactivado |
| fecha_registro | TIMESTAMP | Alta |

### perfil (1:1 con usuario)
id_perfil PK, id_usuario UNIQUE FK, direccion, fecha_nacimiento DATE, documento_identidad VARCHAR(30), foto_url.

### inmobiliaria
id_inmobiliaria PK, nombre, direccion, telefono, correo, activa.

### ciudad
id_ciudad PK, nombre, departamento.

### tipo_propiedad
id_tipo PK, nombre, descripcion.

### propiedad
id_propiedad PK, titulo, descripcion TEXT, precio DECIMAL(15,2), id_ciudad FK, id_tipo FK, id_inmobiliaria FK, id_agente FK (usuario), matricula_inmobiliaria VARCHAR(50) UNIQUE, estado ENUM('DISPONIBLE','RESERVADA','VENDIDA','ALQUILADA'), tipo_operacion ENUM('VENTA','ALQUILER','AMBOS'), direccion, area_m2 DECIMAL(10,2), habitaciones, banos, parqueaderos, activa, fecha_registro.

### imagen_propiedad
id_imagen PK, id_propiedad FK, url_imagen VARCHAR(500), es_principal TINYINT(1), activa, fecha_registro.

### caracteristica
id_caracteristica PK, nombre, descripcion.

### propiedad_caracteristica (N:M)
id_propiedad FK + id_caracteristica FK (PK compuesta).

### cita
id_cita PK, id_cliente FK, id_propiedad FK, fecha DATE, hora TIME, estado ENUM('PENDIENTE','APROBADA','RECHAZADA','CANCELADA'), observaciones TEXT, fecha_registro.

### solicitud
id_solicitud PK, id_cliente FK, id_propiedad FK, tipo ENUM('COMPRA','ALQUILER'), estado ENUM('PENDIENTE','EN_REVISION','APROBADA','RECHAZADA'), observaciones, monto_ofrecido DECIMAL(15,2), fecha_registro.

### documento_solicitud
id_documento PK, id_solicitud FK, nombre_archivo, ruta_archivo, tipo_documento, fecha_subida.

### favorito
id_favorito PK, id_cliente FK, id_propiedad FK, fecha. **UNIQUE (id_cliente, id_propiedad)**.

### auditoria
id_auditoria PK, id_usuario FK (SET NULL), accion, tabla_afectada, id_registro, detalles, ip_address, fecha_hora.

### Índices creados en 03_constraints.sql
- `idx_propiedad_ciudad`, `idx_propiedad_tipo`, `idx_propiedad_estado`, `idx_propiedad_precio`
- `idx_cita_fecha`, `idx_solicitud_estado`, `idx_auditoria_fecha`

---

## 4. Consultas SQL de reportes (ReporteDAO)

| # | Técnica SQL | Título |
|---|-------------|--------|
| 1 | INNER JOIN x 3 tablas + GROUP BY | Cantidad de propiedades por ciudad |
| 2 | INNER JOIN x 4 tablas | Solicitudes por inmobiliaria |
| 3 | Relación N:M + GROUP_CONCAT | Características por propiedad |
| 4 | LEFT JOIN | Propiedades y número de citas |
| 5 | GROUP BY + HAVING | Ciudades con más de 2 propiedades |
| 6 | GROUP BY | Citas por estado |
| 7 | GROUP BY | Propiedades por estado |
| 8 | LEFT JOIN + GROUP BY + HAVING | Propiedades disponibles por tipo |

Ejemplo (consulta 5 - GROUP BY + HAVING):

```sql
SELECT c.nombre AS ciudad, c.departamento, COUNT(p.id_propiedad) AS total,
       ROUND(AVG(p.precio),0) AS precio_promedio, MAX(p.precio) AS precio_maximo
FROM ciudad c
INNER JOIN propiedad p ON p.id_ciudad = c.id_ciudad
WHERE p.activa = 1
GROUP BY c.nombre, c.departamento
HAVING COUNT(p.id_propiedad) > 2
ORDER BY total DESC;
```

---

## 5. Casos de uso principales

### Usuario administrador
1. Iniciar sesión con correo y contraseña (BCrypt).
2. Gestionar usuarios: crear, editar, activar/desactivar y asignar/retirar roles.
3. Gestionar ciudades, tipos de propiedad, características e inmobiliarias (CRUD).
4. Consultar la bitácora de auditoría con IP y fecha.
5. Consultar los reportes SQL.

### Agente inmobiliario
1. Crear/editar propiedades (título, precio, ciudad, tipo, inmobiliaria, características N:M).
2. Subir imágenes y marcar la principal; eliminar imágenes.
3. Aprobar o rechazar citas; cancelar citas aprobadas (siempre de sus propiedades).
4. Aprobar o rechazar solicitudes; revisar y eliminar documentos.
5. Consultar reportes de ventas/alquileres.

### Cliente
1. Registrar cuenta (se le asigna automáticamente el rol Cliente + perfil 1:1).
2. Buscar propiedades en el catálogo y guardar en favoritos.
3. Solicitar citas (sin duplicados para la misma propiedad/fecha/hora).
4. Crear solicitudes de COMPRA o ALQUILER (una pendiente por propiedad).
5. Adjuntar documentos a sus solicitudes (PDF/imagen, máx. 5 MB).
6. Editar perfil y cambiar contraseña (verificando la actual).

### Guardado de operaciones negocio (reglas)
- El agente solo gestiona citas/solicitudes/documentos de **sus** propiedades (`perteneceAPropiedadDelAgente`).
- El cliente solo cancela **sus** citas y sube documentos a **sus** solicitudes.
- Correo y matrícula son únicos; no se permite duplicar favoritos.

---

## 6. Seguridad

- **Contraseñas**: hash BCrypt (`org.mindrot.jbcrypt`) en `PasswordUtil`; nunca se almacenan en texto plano.
- **Inyección SQL**: todas las consultas usan `PreparedStatement`.
- **XSS**: salida con `<c:out>` y `ValidationUtil.escapeHtml()`.
- **Acceso por ruta**: `AuthFilter` protege `/admin/*`, `/agente/*` y `/cliente/*` y valida el rol según prefijo; redirige a `/acceso-denegado.jsp` si no corresponde.
- **Encoding**: `CharacterEncodingFilter` fuerza UTF-8 en peticiones y respuestas.
- **Validación de entrada**: `ValidationUtil` (correo, teléfono, contraseña >=4, precios > 0).
- **Sesión**: atributos de usuario cerrados (`usuario_id`, `usuario_nombre`, `usuario_correo`, `usuario_roles`); los DAO re-cargan el rol activo en cada petición protegida.
- **Eliminación lógica**: usuarios y propiedades se desactivan, no se borran físicamente en las vistas de gestión.

---

## 7. Instalación en detalle

1. **Prerrequisitos:** JDK 8+, Apache Tomcat 9.x, MySQL/MariaDB, Maven **no requerido** (compilación manual con javac).
2. **Base de datos:** ejecutar `sql/01_database.sql` -> `02_tables.sql` -> `03_constraints.sql` -> `04_seed.sql`.
3. **Conexión:** editar constantes en `com.inmobiliaria.util.DatabaseConnection` si tu usuario/contraseña difieren.
4. **Compilar:**
   ```
   javac -encoding UTF-8 -parameters -d build/classes ^
     -cp "lib/jbcrypt-0.4.jar;lib/javax.servlet-api-4.0.1.jar;lib/mysql-connector-j-8.0.33.jar" ^
     (dir /s /b src\main\java\*.java)
   ```
5. **Desplegar:** copiar `src/main/webapp/*` -> `webapps/ROOT/`, luego `build/classes/*` -> `webapps/ROOT/WEB-INF/classes/`, los jars de `lib/` -> `webapps/ROOT/WEB-INF/lib/`. (El directorio `exploded/` ya incluye el resultado ensamblado.)
6. **Arranque:** Tomcat iniciará `DataInitializer`, que siembra usuarios/propiedades/citas/solicitudes si la BD está vacía.
7. **URL:** `http://localhost:8080/`.

### Usuarios de prueba (contraseña: `123456`)
- Administrador: `admin@inmovain.com`
- Agentes: `agente1@inmovain.com`, `agente2@inmovain.com`
- Clientes: `cliente1@inmovain.com`, `cliente2@inmovain.com`

---

## 8. Pruebas manuales sugeridas

| # | Prueba | Resultado esperado |
|---|--------|--------------------|
| 1 | Login correcto como cada rol | Redirige al dashboard correspondiente |
| 2 | Login con contraseña incorrecta | Mensaje de error amigable |
| 3 | Registro de nuevo cliente con correo duplicado | Mensaje de error, no crea usuario |
| 4 | Cliente no puede entrar a `/admin/*` ni `/agente/*` | Redirige a acceso denegado |
| 5 | Agente intenta editar propiedad de otro agente | Redirige sin cambios |
| 6 | Crear propiedad con matrícula duplicada | Mensaje de error |
| 7 | Solicitar cita duplicada misma propiedad/fecha/hora | Mensaje de duplicado |
| 8 | Crear solicitud con una pendiente en la misma propiedad | Mensaje de duplicado |
| 9 | Subir documento de más de 5 MB | Rechazado por @MultipartConfig |
| 10 | Favorito duplicado | No se duplica (UNIQUE + esFavorito) |
| 11 | Cambio de contraseña con clave actual incorrecta | Mensaje de error |
| 12 | Auditoría tras crear usuario / cita / solicitud | Se registra con IP y fecha |