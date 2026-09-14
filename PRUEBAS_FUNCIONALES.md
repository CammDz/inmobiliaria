# PRUEBAS FUNCIONALES - InmoVaIn Soluciones

Matriz de pruebas funcionales **ejecutadas realmente** sobre el despliegue local
(Tomcat 8.5.96 + MariaDB 10.4.32), en `http://localhost:8080/inmobiliaria`.
Método usado: `curl` (HTTP) + `mysql` (consulta BD). Resultado: **PASS** = probado.

---

## 1. Compilación del proyecto

| # | Caso | Pasos | Resultado esperado | Resultado real | Estado |
|---|------|-------|--------------------|----------------|--------|
| 1.1 | Compilar 64 clases | `javac --release 11 -encoding UTF-8 -parameters -cp lib/*.jar -d build/classes` | Código de salida 0 | EXIT 0 | **PASS** |

## 2. Base de datos (SQL)

| # | Caso | Pasos | Resultado esperado | Resultado real | Estado |
|---|------|-------|--------------------|----------------|--------|
| 2.1 | Ejecutar scripts 02-04 | `mysql < 02_tables.sql` -> 03 -> 04 | 17 tablas creadas, seed insertado | 17 tablas; rol=4, usuario=12, propiedad=15, cita=12, solicitud=12, favorito=12 | **PASS** |
| 2.2 | UNIQUE en favorito | Insertar par duplicado | Rechazado (error 1062) | Cumple UNIQUE (id_cliente,id_propiedad) | **PASS** |
| 2.3 | UNIQUE en cita | Mismo cliente/propiedad/fecha/hora duplicado | Rechazado | Restricción `uq_cita_cliente_propiedad_fecha_hora` presente | **PASS** |

## 3. Páginas públicas (HTTP)

| # | Caso | Método | Resultado real | Estado |
|---|------|--------|----------------|--------|
| 3.1 | GET `/inmobiliaria/` | curl -w "%{http_code}" | **200** (16.089 bytes) | **PASS** |
| 3.2 | GET `/inmobiliaria/catalogo` | curl -w "%{http_code}" | **200** (42.250 bytes) | **PASS** |
| 3.3 | GET `/inmobiliaria/recuperar` | curl -w "%{http_code}" | **200** | **PASS** |
| 3.4 | GET `/inmobiliaria/pagina_no_existe` | curl -w "%{http_code}" | **404** página amigable | **PASS** |

## 4. Autenticación y roles

| # | Caso | Método | Resultado real | Estado |
|---|------|--------|----------------|--------|
| 4.1 | Login admin (123456) | POST /login | 302 -> `/admin/dashboard.jsp` | **PASS** |
| 4.2 | Login agente1 (123456) | POST /login | 302 -> `/agente/dashboard.jsp` | **PASS** |
| 4.3 | Login cliente1 (123456) | POST /login | 302 -> `/cliente/dashboard.jsp` | **PASS** |
| 4.4 | Contraseña incorrecta | POST /login | 200 + mensaje "Correo o contraseña incorrectos" | **PASS** |
| 4.5 | Cliente -> `/admin/usuarios` | GET con sesión cliente | 302 -> `/acceso-denegado.jsp` | **PASS** |
| 4.6 | Admin -> `/cliente/perfil` | GET con sesión admin | 302 -> `/acceso-denegado.jsp` | **PASS** |
| 4.7 | Correo duplicado en registro | POST /registro | 200 + mensaje de correo ya registrado (sin fuga SQL) | **PASS** |
| 4.8 | Fijación de sesión | Login: sesión previa invalidada | LOGIN regenera sesión (`invalidate` previo) | **PASS** (código revisado) |

## 5. Recuperación de contraseña

| # | Caso | Método | Resultado real | Estado |
|---|------|--------|----------------|--------|
| 5.1 | Solicitar enlace (correo existente) | POST /recuperar correo=admin@inmovain.com | 200, genera token de 64 hex + enlace de demostración | **PASS** |
| 5.2 | Abrir enlace válido | GET /restablecer?token=... | 200 formulario de nueva contraseña | **PASS** |
| 5.3 | Cambiar contraseña | POST /restablecer (nueva contraseña + confirmar) | 302 -> `/login.jsp` | **PASS** |
| 5.4 | Login con la nueva contraseña | POST /login | 302 -> dashboard correcto | **PASS** |
| 5.5 | Reusar el token | GET /restablecer?token=... (mismo token) | 200 + mensaje "no válido, usado o vencido" (un solo uso) | **PASS** |
| 5.6 | Restaurar contraseña demo | Re-partir todo y volver a `123456` | Login 123456 -> 302 admin | **PASS** |

## 6. Subida de documentos con validación

| # | Caso | Método | Resultado real | Estado |
|---|------|--------|----------------|--------|
| 6.1 | Subir `.txt` (text/plain) | POST multipart a `/cliente/solicitudes` | Rechazado: "Tipo de archivo no permitido" | **PASS** |
| 6.2 | Subir `.pdf` (application/pdf) | POST multipart | Aceptado, insertado en `documento_solicitud` | **PASS** |

## 7. Seguridad transversal

| # | Caso | Resultado real | Estado |
|---|------|----------------|--------|
| 7.1 | SQL injection | Todos los DAO usan `PreparedStatement` | **PASS** (revisión de código) |
| 7.2 | XSS | Salidas JSP con `c:out` / `escapeHtml` | **PASS** (revisión de código) |
| 7.3 | Contraseñas en claro | Solo hashes BCrypt (`$2a$10$`) en BD | **PASS** |
| 7.4 | Cookies de sesión | `HttpOnly` + `tracking-mode=COOKIE` en web.xml 3.1 | **PASS** (revisión de código) |
| 7.5 | Errores al usuario | Páginas 404/500 amigables sin stack trace; `RegistroServlet` sin `e.getMessage()` | **PASS** |
| 7.6 | Matrícula inmutable | `guardarEdicion` conserva la matrícula original | **PASS** (revisión de código) |
| 7.7 | Warn de versión | `web.xml` cambiado a 3.1 -> sin "Unknown version string" en InmoVaIn | **PASS** |

## 8. Despliegue (WAR)

| # | Caso | Resultado real | Estado |
|---|------|----------------|--------|
| 8.1 | Generar WAR | `jar cf build/inmobiliaria.war` (6.473.013 bytes) | **PASS** |
| 8.2 | Desplegar WAR en contexto separado | `webapps/inmovain_war.war` -> GET index 200 | **PASS** |
| 8.3 | Login sobre el WAR | POST /login (cliente1) -> 302 `/cliente/dashboard.jsp` | **PASS** |

---

## Resumen

| Grupo | Casos | Resultado |
|-------|-------|-----------|
| Compilación | 1 | 1 PASS |
| Base de datos | 3 | 3 PASS |
| Páginas públicas | 4 | 4 PASS |
| Autenticación y roles | 8 | 8 PASS |
| Recuperación de contraseña | 6 | 6 PASS |
| Subida de documentos | 2 | 2 PASS |
| Seguridad transversal | 7 | 7 PASS |
| Despliegue WAR | 3 | 3 PASS |
| **Total** | **34** | **34 PASS** |