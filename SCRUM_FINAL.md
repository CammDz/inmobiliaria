# SCRUM FINAL - InmoVaIn Soluciones (inmobiliaria web)

**Proyecto:** InmoVaIn Soluciones - Sistema de Gestión Inmobiliaria (Java EE)
**Metodología:** Scrum (3 sprints de una semana cada uno)
**Equipo:** Grupo de desarrollo (documentado en los actas de `docs/SPRINT1.md`,
`docs/SPRINT2.md`, `docs/SPRINT3.md`)

---

## Repositorio Git y tablero de seguimiento

- **Repositorio Git (público):** `https://github.com/CammDz/inmobiliaria` - el historial refleja los tres sprints
  (ver `git log --oneline`: Sprint 1, Sprint 2, Sprint 3 + fixes de seguridad).
- **Tablero Scrum:** https://padlet.com/dayroncamilo4/inmovain-soluciones-tablero-scrum-s023incmxbtuuuhomv53 - detalle en `docs/TABLERO_PADLET.md` y PDF en `docs/tablero_padlet.pdf`.

---

## 1. Resumen del ciclo

| Sprint | Foco | Principales entregables |
|--------|------|-------------------------|
| **Sprint 1** | Base y arquitectura | Estructura del proyecto, scripts SQL (BD, tablas, restricciones, seed), modelos Java, capa DAO con `PreparedStatement`, autenticación con BCrypt y sesiones |
| **Sprint 2** | Módulos funcionales | Catálogo público con filtros, módulo Cliente (favoritos, citas, solicitudes, documentos, perfil), módulo Agente (CRUD propiedades, citas, solicitudes, reportes), módulo Administrador (usuarios, catálogos, auditoría) |
| **Sprint 3** | Auditoría, seguridad, datos y entrega | Auditoría de acciones, 8 reportes SQL, seed 10+ registros por tabla, configuración portable de BD, recuperación de contraseña, WAR final y documentación completa |

---

## 2. Definición de Terminado (DoD) - verificación final

| Criterio | Estado |
|----------|--------|
| Compilación sin errores (64 clases, `javac --release 11`) | [OK] PASS |
| 17 tablas creadas desde SQL (02-04) con FKs, UNIQUE e índices | [OK] PASS |
| Datos de prueba: 10+ registros por tabla principal | [OK] PASS (rol 3, usuario 12, propiedad 15, cita 12, solicitud 12, favorito 12...) |
| Login por roles con redirección correcta | [OK] PASS |
| Permisos por filtro (`/admin/*`, `/agente/*`, `/cliente/*`) | [OK] PASS |
| Protección anti fijación de sesión | [OK] PASS |
| Recuperación de contraseña con token seguro, expiración y un solo uso | [OK] PASS |
| Páginas de error 404/500 amigables (sin stack trace) | [OK] PASS |
| Validación del tipo de archivo al subir documentos | [OK] PASS |
| WAR generado y probado (despliegue limpio) | [OK] PASS |
| 34 casos de prueba funcionales PASS | [OK] PASS |
| Documentación completa (ver sección 5) | [OK] PASS |

---

## 3. Métricas finales

| Métrica | Valor |
|---------|-------|
| Clases Java | **59** |
| Tablas de base de datos | **17** |
| Consultas SQL documentadas en `ReporteDAO` | **8** |
| DAO | 15 + recuperación |
| Scripts SQL | 5 (`00_reset`...`04_seed`) |
| Usuarios de demostración (seed) | 12 (todos con BCrypt) |
| Propiedades de demostración | 15 (+30 imágenes) |
| Casos de prueba funcionales | 34 - todos PASS |
| Artefacto de entrega | `build/inmobiliaria.war` (~6,5 MB) |

---

## 4. Retrospectiva

### Lo que funcionó bien
- Arquitectura **MVC + DAO** simple y consistente, sin dependencias innecesarias.
- `PreparedStatement` en toda la capa DAO (protección SQL injection).
- Separación de responsabilidades por paquetes (`controller`, `dao`, `model`,
  `filter`, `util`) que facilitó la auditoría y las pruebas.
- Configuración centralizada de BD (`DbConfig`) que permite alternar entre BD
  local y en línea sin tocar el código.

### Mejoras aplicadas durante el proyecto
- **Fijación de sesión**: se descarta la sesión previa en `LoginServlet` y
  `RegistroServlet` antes de autenticar.
- **Recuperación de contraseña**: token `SecureRandom` 256 bits, almacenado
  como SHA-256, con expiración (1 hora) y consumo de un solo uso.
- **`web.xml` 3.1** (compatible con Tomcat 8.5/9) y cookies `HttpOnly` +
  `tracking-mode=COOKIE`.
- **Sin fuga de detalles SQL**: `RegistroServlet` dejó de exponer `e.getMessage()`.
- **Subida controlada de documentos**: solo PDF, JPG, PNG, GIF, DOC/DOCX.
- **Matrícula inmutable** en la edición de propiedades.
- **Seed SQL completo** con contraseñas BCrypt, sustituyendo el uso exclusivo
  del `DataInitializer`.

### Riesgos y limitaciones conocidas
- El envío real de correo para la recuperación requiere SMTP: en modo
  demostración el enlace se muestra en pantalla y se registra en consola
  (documentado en `recuperar.jsp`).
- No hay pruebas automáticas JUnit; se usaron pruebas manuales puras y de
  integración HTTP (ver `PRUEBAS_UNITARIAS.md`).
- Sin sistema de pagos ni transacciones de compra/venta en línea (fuera del
  alcance académico).

---

## 5. Documentación entregada

| Documento | Contenido |
|-----------|-----------|
| `AUDITORIA_PROYECTO.md` | Auditoría completa del sistema con evidencia |
| `INSTALACION_LOCAL.md` | Guía de instalación paso a paso (probada) |
| `BASE_DATOS_EN_LINEA.md` | Configuración de BD remota (variables de entorno) |
| `MER_DBEAVER.md` | Cómo generar el diagrama Entidad-Relación en DBeaver |
| `MODELO_RELACIONAL.md` | Modelo relacional (17 tablas, claves, cardinalidades) |
| `DICCIONARIO_DATOS.md` | Diccionario de datos columna a columna |
| `CONSULTAS_SQL.md` | 5 consultas exigidas + 3 adicionales, con resultados reales |
| `PRUEBAS_FUNCIONALES.md` | 34 casos de prueba ejecutados (PASS) |
| `PRUEBAS_UNITARIAS.md` | Pruebas unitarias manuales y guía JUnit |
| `SCRUM_FINAL.md` | Este documento |
| `database.properties.example` | Plantilla de configuración (sin credenciales) |
| `docs/SPRINT1.md` ... `SPRINT3.md` | Actas de los sprints |

---

## 6. Lanzamiento (Release)

**v1.0.0** - entregable final:

- Base de datos: `sql/00_reset.sql`...`sql/04_seed.sql`.
- Código fuente: `src/main/java` + `src/main/webapp`.
- Artefacto desplegable: `build/inmobiliaria.war`.
- Despliegue: Apache Tomcat 8.5+/9.x con MySQL/MariaDB.

**Usuarios demo (contraseña `123456`):** `admin@inmovain.com`
(Administrador), `agente1@inmovain.com` (Agente Inmobiliario),
`cliente1@inmovain.com` (Cliente), entre otros (ver `INSTALACION_LOCAL.md`).