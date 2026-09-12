# Sprint 1 - Base del proyecto y arquitectura

**Proyecto:** InmoVaIn Soluciones (inmobiliaria web)
**Duración:** Semana 1

## Objetivo del sprint
Tener una base sólida: estructura del proyecto, base de datos completa, modelos y capas DAO, y los primeros módulos de autenticación.

## Product Backlog seleccionado
1. Crear estructura Maven-style del proyecto (sin Maven; compilación con javac).
2. Scripts SQL: base de datos, 16 tablas, restricciones (FK, UNIQUE, índices) y seed de datos de referencia.
3. Modelos Java (15 clases) y conexión JDBC centralizada.
4. Capa de acceso a datos (15 DAO) con `PreparedStatement`.
5. Utilidades: BCrypt, validación, autenticación/sesión.
6. Autenticación: login, registro (rol Cliente + perfil 1:1), logout, filtro de encoding UTF-8.

## Definición de Terminado (DoD)
- [x] Esquema SQL 01-04 exportable y ejecutable.
- [x] DAO compilando sin errores con los jars en `lib/`.
- [x] Login/registro funcionales con hash BCrypt.
- [x] Compilación `javac` exitosa de todas las clases.

## Sprint Review
- **Logros:** esquema relacional completo con N:M (usuario<->rol, propiedad<->característica), 1:1 (usuario<->perfil), FK con borrado lógico y UNIQUEs críticas. 15 modelos y 15 DAO. Autenticación por BCrypt lista.
- **Métricas:** 100% de criterios de aceptación del sprint cumplidos.

## Retrospective
- **Salir adelante:** decidimos centralizar la configuración de BD en `DatabaseConnection` y el seed dinámico de usuarios con hashes BCrypt en Java (listener), porque no precalculamos los salt en SQL.
- **Continuar:** validaciones en utilidades reutilizables y mensajes de usuario.

### Acciones
| Acción | Estado |
|--------|--------|
| Crear `DataInitializer` como listener para sembrar datos con BCrypt | En Sprint 2 |