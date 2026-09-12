# Sprint 3 - Auditoría, reportes, datos de prueba, documentación y entrega

**Proyecto:** InmoVaIn Soluciones (inmobiliaria web)
**Duración:** Semana 3

## Objetivo del sprint
Completar la auditoría y los reportes SQL, sembrar datos de demostración 10+ por tabla principal, documentar y empaquetar la entrega.

## Product Backlog seleccionado
1. Tabla y registro de **auditoría** (creación/modificación de usuarios, citas, solicitudes, documentos, perfil, contraseña).
2. **Reportes SQL** (8 consultas: INNER JOIN x3/4, N:M, LEFT JOIN, GROUP BY + HAVING) para admin y agente.
3. **DataInitializer** (listener): usuarios con BCrypt, perfiles, 15 propiedades, imágenes, características N:M, citas, solicitudes y favoritos.
4. Vista de auditoría y de reportes (ambas roles).
5. Estilos globales, JS y placeholder de imágenes.
6. Documentación: README, documento técnico, Scrum; empaquetado desplegable.
7. Revisión integral (rutas, sesiones, permisos, enlaces) y Git.

## Definición de Terminado (DoD)
- [x] Auditoría registra acción, tabla, registro, detalles, IP y usuario.
- [x] `DataInitializer` es idempotente (no duplica si ya hay datos).
- [x] Reportes visibles para Administrador y Agente.
- [x] Compilación `javac` exitosa del proyecto completo.
- [x] `exploded/` ensamblado (webapp + clases + jars).
- [x] README + documentación técnica + 3 sprints Scrum.

## Sprint Review
- **Logros:** 8 reportes SQL documentados; auditoría completa con IP; siembra idempotente que arranca al desplegar; WAR/estructura lista para Tomcat; compilación de **58 clases Java** sin errores.
- **Métricas:** DoD cumplido; entrega lista para despliegue en `http://localhost:8080/`.

## Retrospective
- **Hacer más:** ejecutar la revisión de JSP frente a los modelos (accidentes de nomenclatura como `listarTodos` vs `listarTodas` se detectaron en compilación).
- **Continuar:** mantener las reglas de negocio en DAO (duplicados, ownership) y las salidas escapadas contra XSS.

## Backlog futuro (no incluido en la entrega)
- Imágenes reales en `images/` (hoy placeholder).
- Pruebas automatizadas (JUnit) y CI.
- Soporte multi-idioma y notificaciones por correo.

### Acciones de cierre
| Acción | Estado |
|--------|--------|
| Revisar enlaces rotos y accesos cruzados | Hecho |
| Organizar repositorio con Git | Pase final del equipo |