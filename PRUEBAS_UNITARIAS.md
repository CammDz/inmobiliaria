# PRUEBAS UNITARIAS - InmoVaIn Soluciones

En el proyecto **no se usa JUnit** (sin Maven/Gradle; compilado directo con
`javac`). Para garantizar calidad se ejecutaron **pruebas unitarias manuales**
sobre clases puras (sin entorno web) mediante programas Java independientes y
se documenta cómo añadir JUnit si se desea automatizarlo.

---

## 1. Pruebas ejecutadas (manuales, sin JUnit)

### 1.1 `PasswordUtil` - hash y verificación BCrypt

Se compiló y ejecutó un programa independiente sobre `jbcrypt-0.4.jar` que
verifica los 3 hashes usados en `sql/04_seed.sql`:

```java
import org.mindrot.jbcrypt.BCrypt;
public class CheckHash {
    public static void main(String[] a) {
        String[] hs = { /* 3 hashes del seed */ };
        for (String h : hs) System.out.println(BCrypt.checkpw("123456", h));
    }
}
```

Resultado real: `true` `true` `true` -> **PASS** (los 12 usuarios del seed
pueden iniciar sesión con `123456`).

También se comprobó `PasswordUtil.main`, que genera hashes de `123456` y
`admin123` con sal distinta en cada invocación (BCrypt con sal aleatoria).

### 1.2 `RecuperacionDAO` - token seguro y un solo uso (integración mínima)

Flujo comprobado por HTTP (ver `PRUEBAS_FUNCIONALES.md` sección 5):

1. `crearToken` genera 64 caracteres hex (256 bits, `SecureRandom`) y **elimina
   los tokens previos** del usuario.
2. El token se almacena **hasheado (SHA-256)**: aunque se filtre la BD, el valor
   en claro no puede reutilizarse si se consulta la tabla.
3. `validarToken` solo acepta tokens `usado=0` y `expira_en > NOW()`.
4. Tras el cambio de contraseña, `marcarUsado` consume el token (falló el reuso).

### 1.3 `DbConfig` - prioridad de configuración

Casos revisados por inspección y por ejecución real:

| Prioridad | Escenario | Resultado |
|-----------|-----------|-----------|
| 3 | `database.properties` en `WEB-INF/classes` (root/root) | La app conectó y los logins funcionaron |
| 1 | Variables de entorno | Se leen por `System.getenv` (documentado en `BASE_DATOS_EN_LINEA.md`) |
| 4 | Sin configuración | Defaults `localhost:3306/inmobiliaria_db/root` |

Log verificado al arranque: `[DatabaseConnection] Conectando con: host=..., port=..., name=...`
(sin contraseña en el log).

### 1.4 `ValidationUtil`

Casos de la matriz manual (funciones puras con JRE):

| Entrada | `esCorreoValido` | `esContrasenaValida` | `estaVacio` |
|---------|:---:|:---:|:---:|
| `null` / `""` / espacios | false | false | true |
| `user@example.com` | true | - | false |
| `abc` (correo) | false | - | - |
| `"1234"` | - | true (>=4) | false |
| `"abc"` | - | false | false |

### 1.5 Parseo y helpers de los controladores

`parsearId`, `parsearDoble`, `ObtenerNombreArchivo(colunio de content-disposition)`
y `esTipoArchivoPermitido` se comprobaron con los casos límite típicos
(-1 para no numérico, 0 para vacío, rechazo de `text/plain`, aceptación de
`application/pdf`, `image/jpeg`, `application/msword`).

---

## 2. Cómo añadir JUnit (opcional)

La separación en `model/`, `util/` y `dao/` hace sencillo escribir pruebas
unitarias automáticas. Si se desea:

1. Descargue `junit-4.13.2.jar` + `hamcrest-core-1.3.jar` a `lib/`.
2. Compile y ejecute las pruebas:

```bash
javac -cp "lib/junit-4.13.2.jar;lib/jbcrypt-0.4.jar;build/classes" -d build/test src/test/java/**/*.java
java -cp "build/test;build/classes;lib/*" org.junit.runner.JUnitCore com.inmobiliaria.util.PruebasValidationUtil
```

3. Sugerencia de casos a automatizar:
   - `PasswordUtil.hashPassword/verificarPassword` (hash nuevo verifica, hash
     ajeno no).
   - `ValidationUtil` (correo, teléfono, contraseña, carpeta vacía).
   - `ValidationUtil.escapeHtml` (`<script>` -> `&lt;script&gt;`).
   - `RecuperacionDAO.generarToken` (longitud 64, hex, distintos entre llamadas).
   - `DbConfig.construirUrl` (parámetros por defecto y con overrides).

---

## 3. Cobertura declarada

| Clase bajo prueba | Técnica | Resultado |
|-------------------|---------|-----------|
| `PasswordUtil` | Programa independiente | PASS |
| `RecuperacionDAO` | Integración HTTP | PASS |
| `DbConfig` | Ejecución real + inspección | PASS |
| `ValidationUtil` | Matriz manual | PASS |
| Helpers de servlets | Matriz manual | PASS |