# Base de Datos en Línea (despliegue remoto) - InmoVaIn Soluciones

La aplicación **no repite la cadena de conexión** en cada clase: todo se centraliza
en `com.inmobiliaria.util.DbConfig` y `DatabaseConnection`, y se configura por
**variables de entorno** (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`,
`DB_SSL`, `DB_TIMEZONE`, `DB_CHARACTER_ENCODING`) o por el archivo
`database.properties` (ver `database.properties.example` y `INSTALACION_LOCAL.md`).

## 1. Instancia en línea (Aiven / MySQL)

La instancia remota se creó en **Aiven** (plan Free, motor MySQL 8.4).
Datos de conexión (sin credenciales sensibles, se pasan por variable de entorno):

- **Host:** `inmobiliaria-db-java-inmobiliaria.f.aivencloud.com`
- **Puerto:** `24599`
- **Base de datos:** `defaultdb`
- **Usuario:** `avnadmin`
- **Contraseña:** variable segura `DB_PASSWORD` (no se incluye en el repositorio)
- **SSL:** obligatorio (la app ya agrega `useSSL=true`)

> La contraseña no vive en archivos del repo por seguridad; se define como variable
> de entorno o en un `database.properties` local fuera de Git.
>
> Nota: el cliente `mysql.exe` de XAMPP (MariaDB 10.4) no sirve para esta BD porque
> MySQL 8 usa `caching_sha2_password`; la importación se hizo con Python/pymysql.

## 2. Despliegue del esquema

Los scripts de despliegue remoto están en `sql/remoto/` (no usan `CREATE DATABASE`
y son agnósticos del proveedor: el nombre de la BD se pasa como argumento final del
cliente mysql, sin sentencia `USE`):

```
00_reset.sql    (borra las 17 tablas)
02_tables.sql   (CREATE TABLE)
03_constraints.sql (FKs, índices y restricciones UNIQUE)
04_seed.sql     (datos de prueba con BCrypt, mínimo 10 por tabla principal)
```

Ejemplo con el cliente MySQL (indicar el nombre de la BD al final;
`--default-character-set=utf8mb4` para no corromper acentos):

```bash
mysql -h inmobiliaria-db-java-inmobiliaria.f.aivencloud.com -P 24599 \
  -u avnadmin -p --ssl-mode=REQUIRED --default-character-set=utf8mb4 defaultdb \
  < sql/remoto/00_reset.sql
... (repetir con 02_tables.sql, 03_constraints.sql, 04_seed.sql)
```

## 3. Funciones de tráfico JDBC

Para generar tráfico y persistir tablas con datos: la misma aplicación inserta
auditoría en cada acceso (`AuditoriaDAO`), y el `DataInitializer` garantiza catálogos
básicos al arrancar.

## 4. Verificación (evidencia)

Una vez importado, se verificó contra la instancia en línea (Aiven):

| Verificación | Resultado |
|--------------|-----------|
| 12 usuarios sembrados | OK |
| 15 propiedades (todas activas) | OK |
| 30 imágenes (2 por propiedad) | OK |
| 10 ciudades | OK |
| 10 tipos / 10 inmobiliarias | OK |
| Acentos (ej. "Medellín") | OK (UTF-8 correcto, HEX C3 AD) |

## 5. Apuntar la app a la BD en línea

En Tomcat, definir las variables de entorno del contexto o un
`database.properties` con los valores remotos:

```
db.host=inmobiliaria-db-java-inmobiliaria.f.aivencloud.com
db.port=24599
db.name=defaultdb
db.user=avnadmin
db.password=AQUI_LA_CLAVE
db.driver=com.mysql.cj.jdbc.Driver
db.ssl=true
db.timezone=UTC
db.characterEncoding=UTF-8
```

En Render, lo correcto es definir estas mismas variables como variables de
entorno (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `DB_SSL=true`,
`DB_TIMEZONE=UTC`), porque `DbConfig` les da máxima prioridad y no hace falta
guardar credenciales en el repositorio.

`DbConfig.construirUrl()` agrega automáticamente `useSSL`, `serverTimezone`,
`characterEncoding=UTF-8` y, solo para hosts remotos, timeouts de conexión
(`src/main/java/com/inmobiliaria/util/DbConfig.java`).

## 6. Vuelta a local

Basta con apuntar `db.host=localhost`, `db.port=3306`, `db.name=inmobiliaria_db`,
`db.user=root` y reiniciar la app: ningún código cambia porque la conexión está
centralizada y configurable.