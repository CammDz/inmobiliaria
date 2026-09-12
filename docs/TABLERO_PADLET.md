# Tablero de seguimiento (Padlet) - InmoVaIn Soluciones

Tablero Scrum del proyecto, correspondiente a las historias de usuario del Product
Backlog (priorizadas por el Product Owner - profesor) y su ejecución en los tres
sprints de 7 días.

**Enlace público del tablero (Padlet):** <https://padlet.com/dayroncamilo4/inmovain-soluciones-tablero-scrum-s023incmxbtuuuhomv53>

**Respaldo visual del tablero:** `docs/tablero_padlet.html` (versión HTML con las
mismas tarjetas, para captura de pantalla si el tablero en línea no cargara durante
la sustentación).

---

## Product Backlog (historias priorizadas)

| Id | Historia (Como usuario...) | Prioridad | Sprint |
|----|---------------------------|-----------|--------|
| H1 | Como visitante, quiero una página de aterrizaje para conocer la inmobiliaria y buscar propiedades rápido | Alta | Sprint 1 |
| H2 | Como usuario, quiero registrarme con un correo único y validado | Alta | Sprint 1 |
| H3 | Como usuario registrado, quiero iniciar y cerrar sesión seguro y que me lleve a mi panel por rol | Alta | Sprint 1 |
| H4 | Como administrador, quiero asignar y revocar roles a los usuarios | Alta | Sprint 2 |
| H5 | Como cliente, quiero completar mi perfil con documento, teléfono y dirección | Media | Sprint 2 |
| H6 | Como agente, quiero registrar y editar propiedades con fotos, características y precio | Alta | Sprint 2 |
| H7 | Como cliente, quiero buscar y filtrar por ciudad, tipo, precio y características | Alta | Sprint 2 |
| H8 | Como cliente, quiero marcar propiedades como favoritas | Media | Sprint 3 |
| H9 | Como cliente, quiero solicitar una cita en horario disponible | Media | Sprint 3 |
| H10 | Como cliente, quiero radicar documentos de compra/arriendo y ver el estado | Media | Sprint 3 |
| H11 | Como agente, quiero aprobar o rechazar solicitudes y documentos | Media | Sprint 3 |
| H12 | Como administrador, quiero reportes de propiedades/ciudades/citas con agregación | Media | Sprint 3 |
| H13 | Como administrador, quiero consultar la auditoría de accesos y cambios | Baja | Sprint 3 |

---

## Sprint 1 - Cimientos y acceso (7 días)

**Tarjetas:** H1, H2, H3 y tareas técnicas (MER, modelo relacional 3FN, script DDL/DML,
conexión JDBC, filtro de rutas).

| Por hacer | En curso | Terminado |
|-----------|----------|-----------|
| ~~H1~~ | ~~H1~~ | **H1** - Landing con buscador y destacadas |
| ~~H2~~ | ~~H2~~ | **H2** - Registro con correo UNIQUE y BCrypt |
| ~~H3~~ | ~~H3~~ | **H3** - Login/logout seguro, redirección por rol |
| ~~MER~~ | ~~Modelo relacional~~ | **MER y relacional 3FN** (modelo 1:1, 1:N, N:M) |
| ~~DDL~~ | ~~Seed~~ | **DDL/DML** + conexión JDBC centralizada |

## Sprint 2 - Núcleo del negocio (7 días)

**Tarjetas:** H4, H5, H6, H7 y tareas técnicas (galería 1:N, características N:M).

| Por hacer | En curso | Terminado |
|-----------|----------|-----------|
| ~~H4~~ | ~~H4~~ | **H4** - Admin asigna roles (UsuariosServlet) |
| ~~H5~~ | ~~H5~~ | **H5** - Perfil del cliente (1:1) |
| ~~H6~~ | ~~H6~~ | **H6** - CRUD propiedades + imágenes + características |
| ~~H7~~ | ~~H7~~ | **H7** - Catálogo con filtros (ciudad, tipo, precio) |

## Sprint 3 - Operación y cierre (7 días)

**Tarjetas:** H8, H9, H10, H11, H12, H13 y tareas técnicas (reportes, pruebas, despliegue).

| Por hacer | En curso | Terminado |
|-----------|----------|-----------|
| ~~H8~~ | ~~H8~~ | **H8** - Favoritos |
| ~~H9~~ | ~~H9~~ | **H9** - Citas sin cruce de agendas (UNIQUE + validación) |
| ~~H10~~ | ~~H10~~ | **H10** - Radicación de documentos y estado de solicitud |
| ~~H11~~ | ~~H11~~ | **H11** - Aprobación/rechazo por el agente |
| ~~H12~~ | ~~H12~~ | **H12** - Reportes (INNER JOIN, N:M, LEFT JOIN, GROUP BY+HAVING) |
| ~~H13~~ | ~~H13~~ | **H13** - Auditoría de accesos |
| ~~Pruebas~~ | ~~Despliegue~~ | **Pruebas + despliegue** (BD local y en línea) |

---

## Evidencia (sustentación)

1. Enlace del Padlet (pegar arriba).
2. Captura de pantalla del tablero completo → `docs/captura-tablero.png`.
3. El repositorio Git con el historial de commits queda evidenciado en
   `SCRUM_FINAL.md` y en el README del repo.