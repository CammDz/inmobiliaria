# MODELO RELACIONAL - InmoVaIn Soluciones

El modelo relacional de la base `inmobiliaria_db` se compone de **17 tablas** que
implementan las entidades y relaciones del sistema. A continuación se describen
tablas, claves y cardinalidades.

---

## 1. Listado de tablas

| # | Tabla | Tipo | Descripción |
|---|-------|------|-------------|
| 1 | `rol` | Catálogo | Roles del sistema (`Administrador`, `Agente Inmobiliario`, `Cliente`) |
| 2 | `usuario` | Principal | Personas que acceden al sistema |
| 3 | `usuario_rol` | N:M | Asignación de roles a usuarios |
| 4 | `perfil` | Principal | Datos ampliados del usuario (1:1) |
| 5 | `inmobiliaria` | Catálogo | Empresas de bienes raíces del sistema |
| 6 | `ciudad` | Catálogo | Ciudades (con departamento) |
| 7 | `tipo_propiedad` | Catálogo | Tipos de propiedad |
| 8 | `propiedad` | Principal | Inmuebles publicados |
| 9 | `imagen_propiedad` | Transaccional | Imágenes de cada propiedad |
| 10 | `caracteristica` | Catálogo | Características de las propiedades |
| 11 | `propiedad_caracteristica` | N:M | Características asignadas a una propiedad |
| 12 | `cita` | Transaccional | Visitas/agendamiento entre cliente y propiedad |
| 13 | `solicitud` | Transaccional | Solicitudes de compra o alquiler |
| 14 | `documento_solicitud` | Transaccional | Documentos adjuntos a una solicitud |
| 15 | `favorito` | Transaccional | Propiedades guardadas por clientes |
| 16 | `auditoria` | Transaccional | Trazabilidad de acciones sensibles |
| 17 | `token_recuperacion` | Transaccional | Tokens de recuperación de contraseña |

---

## 2. Claves primarias y únicas relevantes

| Tabla | PK | Únicas / Notas |
|-------|----|----------------|
| `rol` | `id_rol` | `nombre` UNIQUE |
| `usuario` | `id_usuario` | `correo` UNIQUE (aplicada en el DAO y por regla de negocio) |
| `usuario_rol` | `id_usuario + id_rol` | compuesta |
| `perfil` | `id_perfil` | `id_usuario` UNIQUE -> garantiza 1:1 |
| `propiedad` | `id_propiedad` | `matricula_inmobiliaria` UNIQUE |
| `cita` | `id_cita` | `(id_cliente, id_propiedad, fecha, hora)` UNIQUE |
| `favorito` | `id_favorito` | `(id_cliente, id_propiedad)` UNIQUE |
| `token_recuperacion` | `id_token` | `token` UNIQUE |

---

## 3. Relaciones por cardinalidad

### 1 : 1

- **usuario <-> perfil**: `perfil.id_usuario -> usuario.id_usuario` (cada usuario
  tiene un único perfil; `UNIQUE (id_usuario)` lo garantiza).

### 1 : N

- **inmobiliaria -> propiedad**: `propiedad.id_inmobiliaria -> inmobiliaria.id_inmobiliaria`.
- **usuario (agente) -> propiedad**: `propiedad.id_agente -> usuario.id_usuario`.
- **ciudad -> propiedad**: `propiedad.id_ciudad -> ciudad.id_ciudad`.
- **tipo_propiedad -> propiedad**: `propiedad.id_tipo -> tipo_propiedad.id_tipo`.
- **propiedad -> imagen_propiedad**: `imagen_propiedad.id_propiedad -> propiedad.id_propiedad`.
- **usuario (cliente) -> cita**: `cita.id_cliente -> usuario.id_usuario`.
- **propiedad -> cita**: `cita.id_propiedad -> propiedad.id_propiedad`.
- **usuario (cliente) -> solicitud**: `solicitud.id_cliente -> usuario.id_usuario`.
- **propiedad -> solicitud**: `solicitud.id_propiedad -> propiedad.id_propiedad`.
- **solicitud -> documento_solicitud**: `documento_solicitud.id_solicitud -> solicitud.id_solicitud`.
- **usuario (cliente) -> favorito**: `favorito.id_cliente -> usuario.id_usuario`.
- **propiedad -> favorito**: `favorito.id_propiedad -> propiedad.id_propiedad`.
- **usuario -> auditoria**: `auditoria.id_usuario -> usuario.id_usuario` (ON DELETE SET NULL).
- **usuario -> token_recuperacion**: `token_recuperacion.id_usuario -> usuario.id_usuario`
  (ON DELETE CASCADE).

### N : M

- **usuario <-> rol**: vía `usuario_rol(id_usuario, id_rol)`.
- **propiedad <-> caracteristica**: vía `propiedad_caracteristica(id_propiedad, id_caracteristica)`.

---

## 4. Reglas de integridad

- Todas las tablas usan `ENGINE=InnoDB` (FKs de verdad) y `utf8mb4_unicode_ci`.
- `cita` impide que un cliente repita la misma cita sobre la misma propiedad,
  misma fecha y misma hora (`UNIQUE`).
- `favorito` impide duplicados de la misma propiedad por cliente.
- `token_recuperacion` **elimina en cascada** si se borra el usuario; los tokens
  vencidos o usados pueden purgarse con `RecuperacionDAO.eliminarExpirados()`.
- `auditoria` registra la IP y no bloquea el borrado de usuarios
  (ON DELETE SET NULL).

---

## 5. Diagrama textual de alto nivel

```text
rol 1 ---------------- * usuario_rol * ----------------------- 1 usuario
usuario 1 ------------ 1 perfil

inmobiliaria 1 ------- * propiedad * ----------------------- 1 ciudad
                            |                          |
                            |                          +---------- 1 tipo_propiedad
                            |
agente = usuario 1 -------- *
                            |
                          * ----- 1 imagen_propiedad
                          * ----- * caracteristica (vía propiedad_caracteristica)
                          * ----- * cita (cliente = usuario)
                          * ----- * solicitud (cliente = usuario) -- * documento_solicitud
                          * ----- * favorito (cliente = usuario)

usuario 1 ------------ * auditoria
usuario 1 ------------ * token_recuperacion
```