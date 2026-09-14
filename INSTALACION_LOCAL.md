# Instalación Local - InmoVaIn Soluciones

Guía probada para levantar la aplicación en el computador (al final se listan las
evidencias de que cada paso quedó verificado).

## 1. Requisitos

| Herramienta | Versión usada | Nota |
|-------------|---------------|------|
| JDK | 21 (Se compila con `--release 11`) | `C:\Program Files\Java\jdk-21.0.10` |
| Apache Tomcat | 8.5.96 | `C:\xampp\tomcat` |
| MariaDB/MySQL | 10.4.32 (XAMPP) | `C:\xampp\mysql\bin` |
| Git (opcional) | 2.55 | Para el historial de sprints |

## 2. Paso 1 - Crear la base de datos local

Desde `C:\xampp\mysql\bin`, ejecutar en orden (importante
`--default-character-set=utf8mb4` para los acentos):

```cmd
mysql -uroot -proot --default-character-set=utf8mb4 < sql\01_database.sql
mysql -uroot -proot --default-character-set=utf8mb4 inmobiliaria_db < sql\02_tables.sql
mysql -uroot -proot --default-character-set=utf8mb4 inmobiliaria_db < sql\03_constraints.sql
mysql -uroot -proot --default-character-set=utf8mb4 inmobiliaria_db < sql\04_seed.sql
```

- `01_database.sql` crea `inmobiliaria_db`.
- `02_tables.sql` crea las 17 tablas.
- `03_constraints.sql` agrega FKs, índices y las restricciones UNIQUE
  (correo, matrícula, perfil.id_usuario, usuario_rol, cita, favorito).
- `04_seed.sql` inserta 10+ registros por tabla principal (contraseñas BCrypt).

Si se quiere reiniciar todo desde cero: `sql/00_reset.sql`.

## 3. Paso 2 - Configurar la conexión

Copiar `database.properties.example` como:
`src/main/webapp/WEB-INF/classes/database.properties` y ajustar si es necesario:

```properties
db.host=localhost
db.port=3306
db.name=inmobiliaria_db
db.user=root
db.password=
db.driver=com.mysql.cj.jdbc.Driver
db.ssl=false
db.timezone=UTC
db.characterEncoding=UTF-8
```

La conexión está **centralizada** en `DbConfig`/`DatabaseConnection` y admite además
variables de entorno `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`,
`DB_DRIVER`, `DB_SSL`, `DB_TIMEZONE`, `DB_CHARACTER_ENCODING`, o una ruta externa
vía `INMOBILIARIA_CONFIG`.

## 4. Paso 3 - Compilar

Con las librerías de `lib/` en el classpath, desde la raíz del proyecto:

```cmd
"C:\Program Files\Java\jdk-21.0.10\bin\javac" --release 11 -encoding UTF-8 -parameters ^
  -cp "lib\*" -d build\classes src\main\java\**\*.java
```

Resultado verificable: **64 clases** compiladas sin errores.

## 5. Paso 4 - Desplegar en Tomcat

- Copiar `build/classes/**` y todo `src/main/webapp` a
  `C:\xampp\tomcat\webapps\inmobiliaria\` (la raíz de la webapp se llama
  `inmobiliaria`).
- Tomcat requiere `CATALINA_HOME=C:\xampp\tomcat` y `JAVA_HOME` apuntando al JDK.
- Iniciar con `startup.bat` y esperar ~15 segundos.
- La app queda en: **http://localhost:8080/inmobiliaria/**

## 6. Paso 5 - Probar con datos de prueba

| Rol | Usuario | Contraseña |
|-----|---------|------------|
| Administrador | admin2@inmovain.com | 123456 |
| Agente Inmobiliario | agente2@inmovain.com | 123456 |
| Cliente | cliente3@inmovain.com | 123456 |

Verificar:

1. Landing page con buscador y destacadas.
2. Login/registro (el registro rechaza correos duplicados con mensaje claro).
3. Al ingresar con cada rol, la redirección llega a su panel (`/admin/`, `/agente/`,
   `/cliente/`). Escribir `/agente/dashboard.jsp` como cliente redirige a
   `acceso-denegado.jsp` (control por filtro, no solo ocultando menús).
4. CRUD de propiedades, galería de imágenes y características; citas; solicitudes
   con subida de documentos; reportes; auditoría.
5. Acentos correctos (ej. "Ramírez", "Atlántico") gracias al encoding UTF-8 en
   archivos, BD y `CharacterEncodingFilter`.

## 7. Evidencia de la instalación probada

- Login HTTP probado: `agente2` → panel agente ("Hola, Diana Pérez" con acentos OK).
- Control de acceso probado: visita anónima a `/agente/*` → 302 a login;
  cliente → `acceso-denegado.jsp`.
- Reportes con las 5 consultas obligatorias documentadas en `CONSULTAS_SQL.md`.
- BD local con 10+ registros por tabla principal y BD en línea (ver
  `BASE_DATOS_EN_LINEA.md`).
- 34 casos de prueba funcionales documentados en `PRUEBAS_FUNCIONALES.md`.