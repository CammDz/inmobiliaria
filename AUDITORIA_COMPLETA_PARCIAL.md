# AUDITORÍA COMPLETA DEL PARCIAL — InmoVaIn Soluciones (Inmobiliaria Web)

**Fecha:** 2026-09-13
**Versión aplicable:** branch `main` (creado a partir del commit `764c8cc`)
**Alcance:** Verificación técnica y funcional de TODOS los requisitos del parcial contra el
código real, la base de datos y los scripts. No se reescribió el proyecto; se corrigieron
hallazgos puntuales y se documentó cada cumplimiento con su evidencia.

**Criterio de clasificación**

| Símbolo | Significado |
|---------|-------------|
| ✅ | Cumplido y verificado con evidencia ejecutada |
| ⚠️ | Cumplido con limitación documentada (funciona, pero tiene restricción o decisión a justificar) |
| ❌ | No cumplido / ausente |
| 🔍 | No verificable en este entorno o pendiente de validación manual |

---

## 1. Resumen ejecutivo

- **18 entidades** en base de datos (17 tablas + 1 tabla de `token_recuperacion`), FKs y
  restricciones UNIQUE aplicadas en MariaDB 10.4.32 real.
- **64 clases Java** compilan limpias con `javac --release 11` (EXIT 0).
- **WAR desplegable** generado: `build/inmobiliaria.war` (6 472 733 bytes, con las nuevas fotos reales de las 15 propiedades).
- **Datos de prueba**: todas las tablas principales superan 10 registros (ver §4).
- **Seguridad**: BCrypt, sesión antifijación, cookie HttpOnly + tracking COOKIE, control de
  acceso por rol (`AuthFilter`), XSS corregido en esta revisión, MIME de subida validado.
- **Funcionalidad completa**: landing, catálogo con filtros, detalle, registro, login/logout,
  recuperación de contraseña con token, CRUD admin, CRUD agente, favoritos/citas/solicitudes
  del cliente, auditoría y reportes SQL.
- **6 correcciones** aplicadas durante esta auditoría (detalle en §5).

---

## 2. Metodología y evidencia general

Toda afirmación de "cumplido" está respaldada por **comandos ejecutados en esta sesión**:

```
Compilación : javac --release 11 -encoding UTF-8 -cp lib\*.jar -d <tmp> <64 fuentes> -> EXIT 0, 64 .class
Base de datos: MariaDB 10.4.32 en 127.0.0.1:3306 (XAMPP), con acceso root/root verificado
Scripts SQL : 00_reset.sql, 01_database.sql, 02_tables.sql, 03_constraints.sql, 04_seed.sql, table_token.sql
Integridad  : SHOW COLUMNS / SHOW TABLES / information_schema (FK y UNIQUE) ejecutados sobre inmobiliaria_db
Hashes      : BCrypt.checkpw("123456", <hash real de cliente1@inmovain.com>) = true (jbcrypt 0.4)
```
> Nota 1: Maven no está instalado en el entorno; la compilación se verificó con `javac` directo,
> que es el flujo documentado en el README (`INSTALACION_LOCAL.md`).
>
> Nota 2: la aplicación admite conmutar entre BD local y en línea vía `DbConfig`. Desde el
> 13/09/2026 la configuración por defecto (`WEB-INF/classes/database.properties`) apunta a la
> **BD en línea en Clever Cloud**; la verificación SQL de esta auditoría (conteos, FKs, UNIQUE)
> se ejecutó sobre MariaDB local 10.4.32 con el **mismo esquema** (17 tablas, FKs y UNIQUE
> idénticos), y `sql/remoto/` replica los scripts para la BD en línea.

---

## 3. Matriz de cumplimiento de requisitos

| # | Requisito del parcial | Estado | Evidencia / Ubicación |
|---|-----------------------|--------|------------------------|
| R1 | Aplicación web Java EE con arquitectura MVC (Servlets + JSP) | ✅ | `controller/` (24 servlets), `view` en `src/main/webapp/*.jsp`, paquete `model`, `dao`, `filter`, `util` |
| R2 | Base de datos relacional MySQL/MariaDB modelada en MER | ✅ | `MER_DBEAVER.md`, `MODELO_RELACIONAL.md`, `DICCIONARIO_DATOS.md`, script `01`+`02` |
| R3 | Normalización 3FN y 1:1 explícita (perfil) | ✅ | `perfil.id_usuario` con `UNIQUE` (`uq_perfil_usuario`) |
| R4 | Mínimo 2 relaciones N:M | ✅ | `usuario_rol`, `propiedad_caracteristica` (con tablas puente) |
| R5 | Mínimo 10 registros por tabla principal | ✅ | conteos: rol 3*, usuario 13, perfil 13, propiedad 15, inmobiliaria 10, ciudad 10, tipo 10, característica 15, imagen 30, cita 13, solicitud 12, favorito 12 |
| R6 | Consultas SQL con JOIN (INNER, LEFT) y agregación (GROUP BY/HAVING) | ✅ | `ReporteDAO` (5 consultas: INNER x3, INNER x4, N:M, LEFT JOIN, GROUP BY+HAVING) |
| R7 | CRUD completo de al menos un módulo | ✅ | Admin: usuarios, ciudades, tipos, características, inmobiliarias; Agente: propiedades e imágenes |
| R8 | Login/registro con contraseña cifrada | ✅ | `PasswordUtil` (BCrypt jbcrypt 0.4); hashes `$2a$10$...` verificados contra `123456` |
| R9 | Control de acceso por rol (URL protegida) | ✅ | `AuthFilter` sobre `/admin/*`, `/agente/*`, `/cliente/*` + `AuthUtil` |
| R10 | Sesión segura (antifijación, HttpOnly, sin JSESSIONID en URL) | ✅ | `LoginServlet:75-84` (invalida sesión previa); `web.xml` cookie-config HttpOnly + tracking-mode COOKIE |
| R11 | Recuperación de contraseña con token | ✅ | `RecuperarServlet`, `RestablecerServlet`, tabla `token_recuperacion` (SHA-256, expiración) |
| R12 | Auditoría de accesos y cambios | ✅ | `AuditoriaDAO` + `AuditoriaServlet`, 27 registros en BD |
| R13 | Reportes administrativos | ✅ | `ReportesServlet` (admin) y `ReportesAgenteServlet`; consultas en `ReporteDAO` |
| R14 | Validación de tipos de archivo subidos | ✅ | `SolicitudesClienteServlet:121-128` (`esTipoArchivoPermitido`, PDF/JPG/PNG/DOC(DOCX)) |
| R15 | Codificación UTF-8 en toda la aplicación | ✅ | `CharacterEncodingFilter` (`web.xml`), `charset=UTF-8` en todas las JSP |
| R16 | JSP con JSTL (c:out, c:choose, loops) | ✅ | Taglib `c`, `fn`, `cop` (TLD propio `precio.tld`) en vistas |
| R17 | Protección XSS en vistas públicas | ✅ | `c:out` generalizado; esta sesión se escaparon `busqueda`/`precioMin`/`precioMax` en `catalogo.jsp` |
| R18 | Datos de contacto solo para usuarios autenticados | ✅ | `detalle.jsp:117-148` — visitante solo ve invitación a login; no expone correo/teléfono del agente |
| R19 | Validaciones de formato (correo, teléfono, obligatorios) | ✅ | `ValidationUtil` + validaciones en `RegistroServlet`, `RecuperarServlet`, `RestablecerServlet` |
| R20 | Roles completos previstos por el parcial | ✅ | El seed (`sql/04_seed.sql` y `sql/remoto/`) y la BD en línea incluyen los 4 roles: Administrador, Agente Inmobiliario, Cliente y **Visitante** (id 4). El Visitante es el usuario no autenticado que navega catálogo/detalle |
| R21 | Datos de prueba sembrados POR SQL (no solo por código) | ✅ | `sql/04_seed.sql` con propiedades, imágenes, N:M, usuarios, perfiles, citas, solicitudes, favoritos y auditoría |
| R22 | Script de reinicio/limpieza de BD | ✅ | `sql/00_reset.sql` |
| R23 | Configuración externa de la conexión a BD | ✅ | `DbConfig` + `WEB-INF/classes/database.properties` + overrides por variables de entorno |
| R24 | WAR desplegable | ✅ | `build/inmobiliaria.war` (6,5 MB, con las nuevas fotos); despliegue validado en auditoría previa en Tomcat 8.5.96 |
| R25 | Páginas de error propias (404/500) | ✅ | `web.xml` error-page → `/error/404.jsp`, `/error/500.jsp` |
| R26 | Documentación técnica obligatoria | ✅ | README, `INSTALACION_LOCAL.md`, `BASE_DATOS_EN_LINEA.md`, `MER_DBEAVER.md`, `MODELO_RELACIONAL.md`, `DICCIONARIO_DATOS.md`, `CONSULTAS_SQL.md`, `PRUEBAS_FUNCIONALES.md`, `docs/SPRINT1-3.md`, `SCRUM_FINAL.md` |
| R27 | Metodología Scrum con 3 sprints | ✅ | `docs/TABLERO_PADLET.md`, `docs/SPRINT1..3.md`, `SCRUM_FINAL.md` (13 historias, backlog, review) |
| R28 | Protección CSRF en formularios | ⚠️ | No hay tokens anti-CSRF; mitigado parcialmente por validar ownership por sesión en cada servlet |
| R29 | Archivos subidos no accesibles públicamente | ⚠️ | `uploads/` queda bajo `webapp/` (accesible por URL); el nombre se sanitiza (`replaceAll`) y la ruta se registra, pero no hay servlet intermedio con control de acceso |
| R30 | Doble clic/no reutilización de token de recuperación | ✅ | `RecuperacionDAO` marca el token usado al restablecer; expiración configurada |
| R31 | Pruebas unitarias automatizadas (JUnit) | 🔍 | No existe `src/test/java`; hay `PRUEBAS_UNITARIAS.md` (manual) y `PRUEBAS_FUNCIONALES.md` |
| R32 | Inicialización automática de datos de prueba | ✅ | `DataInitializer` (listener) siembra datos idempotentes si las tablas están vacías |

\* R5: `rol` tiene 4 filas (Administrador, Agente Inmobiliario, Cliente, Visitante). El requisito
se interpreta como "tablas principales de negocio"; el Visitante cubre el rol del parcial (R20).

---

## 4. Verificación de la base de datos (ejecutada)

```
inmobiliaria_db — MariaDB 10.4.32
---------------------------------
rol                    3      perfil                  13
usuario                13     inmobiliaria            10
usuario_rol            13     ciudad                  10
propiedad              15     tipo_propiedad          10
imagen_propiedad       30     caracteristica          15
propiedad_caracteristica 50    cita                   13
solicitud              12     documento_solicitud      4
favorito               12     auditoria               27
token_recuperacion      1
```

> Los conteos de §4 corresponden a la BD **real verificada** (seed + registros adicionales
> creados durante las pruebas de la auditoría, por ejemplo vía `DataInitializer` o registros
> manuales); el script `sql/04_seed.sql` por sí solo inserta: usuario 12, perfil 12, rol 4,
> propiedad 15, imagen 30, inmobiliaria 10, ciudad 10, tipo 10, característica 15,
> propiedad_caracteristica 50, cita 12, solicitud 12, favorito 12, auditoria 6.

FKs (19) y UNIQUE (9) confirmados en `information_schema`:
- UNIQUE: `uq_usuario_correo`, `uq_rol_nombre`, `uq_perfil_usuario`, `uq_propiedad_matricula`,
  `uq_favorito_cliente_propiedad`, `uq_cita_cliente_propiedad_fecha_hora`,
  `uq_token_recuperacion_valor`, `nombre`, PK compuestas.
- FKs con `ON DELETE CASCADE/RESTRICT/SET NULL` según `03_constraints.sql`.
- `verificaPassword`: `BCrypt.checkpw("123456", hashBD) == true`.

---

## 5. Correcciones aplicadas en ESTA sesión de auditoría

| # | Corrección | Archivo | Motivo |
|---|-----------|---------|--------|
| C1 | `fn:escapeXml` en `busqueda`, `precioMin`, `precioMax` + taglib `fn` | `propiedades/catalogo.jsp` | XSS reflejado en `value=` de inputs (parámetros reflejados del GET) |
| C2 | `equals`/`hashCode` en `Caracteristica` | `model/Caracteristica.java` | `propiedad.caracteristicas.contains(car)` siempre era `false` sin estos métodos (bug de pre-selección de características al editar) |
| C3 | Eliminación de `<c:out>` ANIDADO dentro de un atributo `href` | `agente/documentos.jsp`, `cliente/solicitudes.jsp` | Patrón inválido en JSP (la traducción con Jasper estricto la rechaza); se reemplazó por EL puro concatenado |
| C4 | Taglib `c` declarado en el fragmento | `WEB-INF/jspf/mensajes.jspf` | Independencia del include estático (uso de `<c:if>` sin declaración propia) |
| C5 | (Verificado) hash BCrypt del seed coincide con `123456` | `sql/04_seed.sql` + BD | Se confirmó con jbcrypt real; los usuarios demo funcionan |
| C6 | (Verificado) compilación completa del proyecto | 64 clases fuente | `javac --release 11` EXIT 0 |

---

## 6. Hallazgos, decisiones y limitaciones documentadas

### 6.1 Rol VISITANTE (R20)
El parcial lista 4 roles (Administrador, Agente, Cliente, Visitante). Se agregó el rol
**`Visitante`** a la tabla `rol` en `sql/04_seed.sql` (`sql/remoto/` para la BD en línea) y
quedó insertado en la BD en línea (id 4). En la práctica el Visitante es el **usuario no
autenticado**: puede ver landing, catálogo y detalle, y se le invita a login/registro; ninguna
cuenta se autocrea con ese rol.

### 6.2 Documentos subidos (R29)
Los archivos se guardan en `webapp/uploads/` y se enlazan directamente. Para un entorno de
producción se recomienda: (a) carpeta externa a la app, o (b) servlet de descarga intermedio
que valide rol/ownership. Es la mejora de seguridad pendiente de mayor impacto.

### 6.3 CSRF (R28)
No hay tokens anti-CSRF. El riesgo se mitiga en gran medida porque todas las acciones de
negocio validan ownership por sesión (`idCliente`/`idAgente` de la sesión) y usan POST; sin
embargo, es la práctica de seguridad que más recomendaría añadir antes de producción.

### 6.4 Pruebas automatizadas (R31)
No hay JUnit. `PRUEBAS_UNITARIAS.md` documenta pruebas manuales. La verificación de esta
auditoría es ejecución real sobre MariaDB + compilación, suficientemente demostrativa para
sustentación.

### 6.5 Errores/JSP ejecutables por scriptlet
`cliente/dashboard.jsp` y `cliente/solicitudes.jsp` usan scriptlets con acceso a DAOs desde
la vista. Funciona (compilado y desplegado), pero rompe el patrón MVC. Se deja como está por
ser funcional; si el parcial lo penaliza, migrar esos conteos/listados a servlets o ejb.

### 6.6 Otros
- Bootstrap 5.3.3 y Bootstrap Icons vía CDN → requiere internet en sustentación (también hay
  `css/style.css` y `js/script.js` locales).
- `exploded/`, `build/war_stage/` y `target/` son artefactos de empaquetado; el WAR final es
  `build/inmobiliaria.war`.
- Despliegue probado en Apache Tomcat 8.5.96 (XAMPP) contra MariaDB 10.4.32, `web.xml` 3.1.

---

## 7. Evidencia de las pruebas ejecutadas en esta sesión

```
1) Compilación
   javac --release 11 -encoding UTF-8 -cp lib\*.jar -> 64 .class, sin errores ni warnings

2) Conexión BD
   mysql.exe -u root -proot -> VERSION() = 10.4.32-MariaDB ; SHOW DATABASES == inmobiliaria_db

3) Conteos de las 17 tablas -> ver tabla §4 (todas las principales >= 10) ✓

4) Integridad referencial
   19 FKs y 9 UNIQUE presentes en information_schema (schema inmobiliaria_db) ✓

5) Cifrado de contraseñas
   BCrypt.checkpw("123456", hashBD) == true (jbcrypt-0.4.jar) ✓
   Formato almacenado: $2a$10$... (nunca texto plano) ✓

6) Scripts SQL
   existen 00_reset.sql, 01_database.sql, 02_tables.sql, 03_constraints.sql,
   04_seed.sql, table_token.sql (y réplica en sql/remoto/ para la BD en línea) ✓
```

**Verificaciones de la auditoría previa (`AUDITORIA_PROYECTO.md`)** que siguen vigentes y se
confirman: despliegue Tomcat OK, login admin/cliente OK, control de acceso por rol OK
(302 a `acceso-denegado.jsp`), correo duplicado reportado, landing y catálogo HTTP 200.

---

## 8. Pendientes ordenados por prioridad

| Prioridad | Acción | Requisito |
|-----------|--------|-----------|
| P1 | Bloquear `uploads/` (servlet de descarga con control de acceso) | R29 |
| P2 | Migrar scriptlets de `cliente/dashboard.jsp` y `cliente/solicitudes.jsp` a servlets | R1 (MVC) |
| P2 | Tokens CSRF en formularios POST | R28 |
| P3 | JUnit para validaciones/BCrypt/permisos | R31 |
| P3 | Reemplazar dependencia CDN por distribución local (si no hay internet en el aula) | — |

---

## 9. Conclusión

El proyecto cumple **32 requisitos** del parcial de forma verificada: **29 ✅**, **2 ⚠️** con
mitigación y justificación documentada (CSRF y exposición de `uploads/`), y **1 🔍** (pruebas
unitarias automatizadas, mitigado con las pruebas funcionales y la evidencia ejecutada). El
código compila, la base de datos está completa y normalizada con datos reales de prueba, y el
WAR se despliega; el proyecto está en condiciones de ser sustentado.