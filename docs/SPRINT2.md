# Sprint 2 - Módulos de negocio y control de acceso

**Proyecto:** InmoVaIn Soluciones (inmobiliaria web)
**Duración:** Semana 2

## Objetivo del sprint
Implementar los módulos por rol (administrador, agente, cliente), el filtro de seguridad por roles y las vistas web con Bootstrap.

## Product Backlog seleccionado
1. `AuthFilter` que protege `/admin/*`, `/agente/*`, `/cliente/*` por rol.
2. Servlets y vistas del **administrador**: usuarios y roles, ciudades, tipos, características, inmobiliarias.
3. Servlets y vistas del **agente**: CRUD de propiedades, imágenes, características, citas y solicitudes.
4. Servlets y vistas del **cliente**: favoritos, citas, solicitudes con subida de documentos, perfil.
5. Sitio público: landing, catálogo con filtros y detalle de propiedad con galería.
6. Mensajes flash (éxito/error) y páginas de acceso denegado / errores.

## Definición de Terminado (DoD)
- [x] Rutas protegidas por rol y verificación de pertenencia de datos (agente sobre sus propiedades).
- [x] CRUD funcionales de todas las entidades administrativas.
- [x] Catálogo público con búsqueda y filtros (ciudad, tipo, precio, características, término).
- [x] Subida de documentos con `@MultipartConfig` a `uploads/`.
- [x] Vistas responsive con Bootstrap 5.3 y CSS propio.

## Sprint Review
- **Logros:** 20 servlets y ~24 JSP; dashboard por rol con KPIs; prevención de citas/solicitudes duplicadas; eliminación lógica en usuarios/propiedades; galería de imágenes por propiedad con imagen principal.
- **Hallazgo:** los agentes también deben consultar reportes -> se agregó el módulo `agente/reportes` (duplica el acceso de admin, sin pisar los permisos del filtro).
- **Bug corregido:** `FavoritoServlet` no procesaba la acción `quitar`; se corrigió y la vista usa el modelo `Favorito` (y no `Propiedad`).

## Retrospective
- **Hacer más:** verificación de propiedad de datos en cada acción (ownership) antes de ejecutar mutaciones.
- **Hacer menos:** enlaces entre módulos que el filtro bloquearía al rol (se revisaron todos los accesos cruzados).
- **Probar más:** JSPs que dependen de listas con joins en los modelos (agente, cliente, favoritos).

### Acciones
| Acción | Estado |
|--------|--------|
| Crear `css/style.css`, `js/script.js` y `images/placeholder.jpg` | Hecho en Sprint 2 |
| Sembrar datos de negocio (propiedades, citas, solicitudes) | `DataInitializer` en Sprint 3 |
| Reportes y auditoría | Sprint 3 |