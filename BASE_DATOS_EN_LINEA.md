# Base de Datos en Línea (despliegue remoto) - InmoVaIn Soluciones

La aplicación **no repite la cadena de conexión** en cada clase: todo se centraliza
en `com.inmobiliaria.util.DbConfig` y `DatabaseConnection`, y se configura por
**variables de entorno** (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`,
`DB_SSL`, `DB_TIMEZONE`, `DB_CHARACTER_ENCODING`) o por el archivo
`database.properties` (ver `database.properties.example` y `INSTALACION_LOCAL.md`).

## 1. Instancia en línea (Clever Cloud / MySQL)

La instancia remota se creó en **Clever Cloud** (plan gratuito, motor MySQL).
Datos de conexión (sin credenciales sensibles, se pasan por variable de entorno):

- **Host:** `b4wfxuuy1vfroipr47zb-mysql.services.clever-cloud.com`
- **Puerto:** `3306`
- **Base de datos:** `b4wfxuuy1vfroipr47zb`
- **Usuario:** `uyb8zrc7rzzkqv6m`
- **Contraseña:** variable segura `DB_PASSWORD` (no se incluye en el repositorio)

> La contraseña no vive en archivos del repo por seguridad; se define como variable
> de entorno o en un `database.properties` local fuera de Git.

## 2. Despliegue del esquema

Los scripts de despliegue remoto están en `sql/remoto/` (no usan `CREATE DATABASE`
para no romper la BD del proveedor):

```
00_reset.sql    (borra las 17 tablas)
02_tables.sql   (CREATE TABLE, incluye USE b4wfxuuy1vfroipr47zb;)
03_constraints.sql (FKs, índices y restricciones UNIQUE)
04_seed.sql     (datos de prueba con BCrypt, mínimo 10 por tabla principal)
```

Ejemplo con el cliente MySQL de XAMPP (importante `--default-character-set=utf8mb4`
para no corromper acentos):

```bash
"C:\xampp\mysql\bin\mysql.exe" -h b4wfxuuy1vfroipr47zb-mysql.services.clever-cloud.com \
  -u uyb8zrc7rzzkqv6m -p \
  --default-character-set=utf8mb4 < sql/remoto/00_reset.sql
... (repetir con 02_tables.sql, 03_constraints.sql, 04_seed.sql)
```

## 3. Funciones de tráfico JDBC

Para generar tráfico y persistir tablas con datos: la misma aplicación inserta
auditoría en cada acceso (`AuditoriaDAO`), y el `DataInitializer` garantiza catálogos
básicos al arrancar.

## 4. Verificación (evidencia)

Una vez importado, se verificó contra la instancia en línea:

| Verificación | Resultado |
|--------------|-----------|
| 12 usuarios sembrados | OK |
| 15 propiedades | OK |
| 30 imágenes (2 por propiedad) | OK |
| 10 ciudades | OK |
| 12 citas / solicitudes / favoritos | OK |
| Acentos (ej. "Atlántico") | OK (UTF-8 correcto, HEX C3 A1) |

## 5. Apuntar la app a la BD en línea

En Tomcat, definir las variables de entorno del contexto o un
`database.properties` con los valores remotos:

```
db.host=b4wfxuuy1vfroipr47zb-mysql.services.clever-cloud.com
db.port=3306
db.name=b4wfxuuy1vfroipr47zb
db.user=uyb8zrc7rzzkqv6m
db.password=AQUI_LA_CLAVE
db.driver=com.mysql.cj.jdbc.Driver
db.ssl=true
db.timezone=UTC
db.characterEncoding=UTF-8
```

`DbConfig.construirUrl()` agrega automáticamente `useSSL`, `serverTimezone`,
`characterEncoding=UTF-8` y, solo para hosts remotos, timeouts de conexión
(`src/main/java/com/inmobiliaria/util/DbConfig.java`).

## 6. Vuelta a local

Basta con apuntar `db.host=localhost`, `db.port=3306`, `db.name=inmobiliaria_db`,
`db.user=root` y reiniciar la app: ningún código cambia porque la conexión está
centralizada y configurable.