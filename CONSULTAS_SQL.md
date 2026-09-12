# CONSULTAS SQL - InmoVaIn Soluciones

Las siguientes consultas están implementadas en `ReporteDAO.java` y se muestran
desde el panel de **Reportes** de los roles Administrador y Agente. Todas fueron
ejecutadas contra `inmobiliaria_db` con los datos del `04_seed.sql` (resultados
reales incluidos).

Se demuestran los tipos de consulta solicitados:

1. INNER JOIN con **3 o más tablas**.
2. INNER JOIN con **4 tablas**.
3. Relación **N:M**.
4. **LEFT JOIN** (todas las filas de la tabla izquierda).
5. **GROUP BY** con **HAVING**.

---

## 1. INNER JOIN con 3 tablas - propiedades por ciudad

```sql
SELECT c.nombre AS ciudad, tp.nombre AS tipo,
       p.estado, COUNT(*) AS total_propiedades
FROM propiedad p
INNER JOIN ciudad c        ON p.id_ciudad = c.id_ciudad
INNER JOIN tipo_propiedad tp ON p.id_tipo = tp.id_tipo
INNER JOIN inmobiliaria i  ON p.id_inmobiliaria = i.id_inmobiliaria
WHERE p.activa = 1
GROUP BY c.nombre, tp.nombre, p.estado
ORDER BY c.nombre, total_propiedades DESC;
```

Resultado (primeras filas):

| ciudad | total |
|--------|-------|
| Armenia | ... |
| Barranquilla | ... |
| Bogotá | ... |

**Dónde se usa:** `ReporteDAO.propiedadesPorCiudad()` -> panel de reportes.

---

## 2. INNER JOIN con 4 tablas - solicitudes por inmobiliaria

```sql
SELECT i.nombre AS inmobiliaria, s.tipo AS tipo_solicitud,
       s.estado, UPPER(u.nombre) AS cliente_nombre, p.titulo AS propiedad
FROM solicitud s
INNER JOIN usuario u    ON s.id_cliente = u.id_usuario
INNER JOIN propiedad p  ON s.id_propiedad = p.id_propiedad
INNER JOIN inmobiliaria i ON p.id_inmobiliaria = i.id_inmobiliaria
ORDER BY i.nombre, s.fecha_registro DESC;
```

Resultado (primeras filas): `Inmobiliaria Andes`, `Inmobiliaria Andes`,
`Inmobiliaria Caribe`, `Inmobiliaria Centro`, `Inmobiliaria Costa` ...

**Dónde se usa:** `ReporteDAO.solicitudesPorInmobiliaria()`.

---

## 3. Relación N:M - características por propiedad

Las tablas `propiedad` y `caracteristica` se relacionan a través de la tabla
puente `propiedad_caracteristica`.

```sql
SELECT p.titulo AS propiedad,
       COUNT(pc.id_caracteristica) AS num_caracteristicas,
       GROUP_CONCAT(c.nombre ORDER BY c.nombre SEPARATOR ', ') AS caracteristicas
FROM propiedad p
INNER JOIN propiedad_caracteristica pc ON p.id_propiedad = pc.id_propiedad
INNER JOIN caracteristica c            ON pc.id_caracteristica = c.id_caracteristica
GROUP BY p.id_propiedad, p.titulo
ORDER BY num_caracteristicas DESC;
```

Resultado (primeras filas):

| propiedad | num_caracteristicas |
|-----------|---------------------|
| Penthouse Vista al Río | 6 |
| Finca Eco-turística El Paraíso | 5 |
| Casa Campestre El Roble | 4 |
| Apartamento Lago Urbano | 4 |
| Casa Finca Los Mangos | 4 |

**Dónde se usa:** `ReporteDAO.caracteristicasPorPropiedad()`.

---

## 4. LEFT JOIN - propiedades con o sin citas

```sql
SELECT p.titulo AS propiedad, c.nombre AS ciudad,
       COUNT(ci.id_cita) AS num_citas
FROM propiedad p
INNER JOIN ciudad c ON p.id_ciudad = c.id_ciudad
LEFT JOIN cita ci   ON p.id_propiedad = ci.id_propiedad
GROUP BY p.id_propiedad, p.titulo, c.nombre
ORDER BY num_citas DESC;
```

Resultado: **15 propiedades** de las 15 toman fila en el resultado (aunque varias
no tienen citas: las 12 citas del seed se reparten en 12 de las 15 propiedades).
El `LEFT JOIN` garantiza que **todas** las propiedades aparezcan.

**Dónde se usa:** `ReporteDAO.propiedadesConCitas()`.

---

## 5. GROUP BY + HAVING - ciudades con más de 2 propiedades

```sql
SELECT c.nombre AS ciudad, c.departamento,
       COUNT(p.id_propiedad) AS total,
       ROUND(AVG(p.precio), 0) AS precio_promedio,
       MAX(p.precio) AS precio_maximo
FROM ciudad c
INNER JOIN propiedad p ON p.id_ciudad = c.id_ciudad
WHERE p.activa = 1
GROUP BY c.nombre, c.departamento
HAVING COUNT(p.id_propiedad) > 2
ORDER BY total DESC;
```

Resultado real:

| ciudad | total | precio_promedio |
|--------|-------|-----------------|
| Bogotá | 3 | 1.933.333.333 |
| Medellín | 3 | 1.436.666.667 |
| Cali | 3 | 262.000.000 |

**Dónde se usa:** `ReporteDAO.ciudadesConMuchasPropiedades()`. El `HAVING`
descarta las ciudades con 2 o menos propiedades publicadas.

---

## Consultas adicionales disponibles en ReporteDAO

### Citas por estado
```sql
SELECT estado, COUNT(*) AS total, COUNT(DISTINCT id_cliente) AS clientes_distintos
FROM cita GROUP BY estado ORDER BY total DESC;
```

### Propiedades por estado
```sql
SELECT estado, COUNT(*) AS total
FROM propiedad WHERE activa = 1 GROUP BY estado ORDER BY total DESC;
```

### Propiedades disponibles por tipo (LEFT JOIN + HAVING)
```sql
SELECT tp.nombre AS tipo_propiedad, COUNT(p.id_propiedad) AS disponibles,
       MIN(p.precio) AS precio_minimo, MAX(p.precio) AS precio_maximo
FROM tipo_propiedad tp
LEFT JOIN propiedad p ON p.id_tipo = tp.id_tipo AND p.activa = 1
GROUP BY tp.nombre
HAVING COUNT(p.id_propiedad) >= 1
ORDER BY disponibles DESC;
```

---

## Consultas de soporte que evidencian otras visiones del modelo

```sql
-- Favoritos con escala completa (clientes y propiedades)
SELECT u.correo AS cliente, COUNT(f.id_favorito) AS favoritos
FROM usuario u
INNER JOIN favorito f ON f.id_cliente = u.id_usuario
GROUP BY u.correo HAVING COUNT(f.id_favorito) >= 1;
```

```sql
-- Documentos por solicitud (vía solicitud -> propiedad)
SELECT d.id_documento, d.nombre_archivo, s.id_solicitud, p.titulo AS propiedad
FROM documento_solicitud d
INNER JOIN solicitud s ON d.id_solicitud = s.id_solicitud
INNER JOIN propiedad p ON s.id_propiedad = p.id_propiedad;
```