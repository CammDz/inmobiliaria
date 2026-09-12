# DICCIONARIO DE DATOS - InmoVaIn Soluciones

Detalle columna a columna de las **17 tablas** de `inmobiliaria_db`.
Convenciones: **PK** = clave primaria, **FK** = clave foránea, **UQ** = valor único.

---

## 1. `rol`
| Columna | Tipo | Restricción | Descripción |
|---------|------|-------------|-------------|
| id_rol | INT | PK, AUTO_INCREMENT | Identificador del rol |
| nombre | VARCHAR(50) | UQ, NOT NULL | Nombre: Administrador / Agente Inmobiliario / Cliente |
| descripcion | VARCHAR(255) | | Descripción libre del rol |

## 2. `usuario`
| Columna | Tipo | Restricción | Descripción |
|---------|------|-------------|-------------|
| id_usuario | INT | PK, AUTO_INCREMENT | Identificador del usuario |
| correo | VARCHAR(150) | UQ (negocio), NOT NULL | Correo de acceso |
| contrasena | VARCHAR(255) | NOT NULL | Hash BCrypt de la contraseña |
| nombre | VARCHAR(100) | NOT NULL | Primer nombre |
| apellido | VARCHAR(100) | NOT NULL | Apellido |
| telefono | VARCHAR(20) | | Teléfono de contacto |
| activo | TINYINT(1) | DEFAULT 1 | 1 = activo, 0 = desactivado |
| fecha_registro | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Fecha de alta |

## 3. `usuario_rol` (N:M usuario<->rol)
| Columna | Tipo | Restricción | Descripción |
|---------|------|-------------|-------------|
| id_usuario | INT | PK, FK->usuario | Usuario |
| id_rol | INT | PK, FK->rol | Rol asignado |

## 4. `perfil` (1:1 con usuario)
| Columna | Tipo | Restricción | Descripción |
|---------|------|-------------|-------------|
| id_perfil | INT | PK, AUTO_INCREMENT | Identificador del perfil |
| id_usuario | INT | FK->usuario, UQ | Usuario asociado (único) |
| direccion | VARCHAR(255) | | Dirección de residencia |
| fecha_nacimiento | DATE | | Fecha de nacimiento |
| documento_identidad | VARCHAR(30) | | Número de identificación |
| foto_url | VARCHAR(500) | | URL de la foto de perfil |

## 5. `inmobiliaria`
| Columna | Tipo | Restricción | Descripción |
|---------|------|-------------|-------------|
| id_inmobiliaria | INT | PK, AUTO_INCREMENT | Identificador |
| nombre | VARCHAR(150) | NOT NULL | Razón social / nombre |
| direccion | VARCHAR(255) | | Dirección de la oficina |
| telefono | VARCHAR(20) | | Teléfono |
| correo | VARCHAR(150) | | Correo corporativo |
| activa | TINYINT(1) | DEFAULT 1 | 1 = activa |

## 6. `ciudad`
| Columna | Tipo | Restricción | Descripción |
|---------|------|-------------|-------------|
| id_ciudad | INT | PK, AUTO_INCREMENT | Identificador |
| nombre | VARCHAR(100) | NOT NULL | Nombre de la ciudad |
| departamento | VARCHAR(100) | | Departamento / estado |

## 7. `tipo_propiedad`
| Columna | Tipo | Restricción | Descripción |
|---------|------|-------------|-------------|
| id_tipo | INT | PK, AUTO_INCREMENT | Identificador |
| nombre | VARCHAR(100) | NOT NULL | Tipo: Apartamento, Casa, Local, Oficina... |
| descripcion | VARCHAR(255) | | Descripción del tipo |

## 8. `propiedad`
| Columna | Tipo | Restricción | Descripción |
|---------|------|-------------|-------------|
| id_propiedad | INT | PK, AUTO_INCREMENT | Identificador |
| titulo | VARCHAR(200) | NOT NULL | Título comercial |
| descripcion | TEXT | | Descripción detallada |
| precio | DECIMAL(15,2) | NOT NULL | Precio (COP) o canon mensual |
| id_ciudad | INT | FK->ciudad | Ubicación |
| id_tipo | INT | FK->tipo_propiedad | Tipo de inmueble |
| id_inmobiliaria | INT | FK->inmobiliaria | Inmobiliaria gestora |
| id_agente | INT | FK->usuario | Agente responsable |
| matricula_inmobiliaria | VARCHAR(50) | UQ, NOT NULL | Matrícula inmobiliaria (única e inmutable) |
| estado | ENUM(DISPONIBLE, RESERVADA, VENDIDA, ALQUILADA) | DEFAULT DISPONIBLE | Estado comercial |
| tipo_operacion | ENUM(VENTA, ALQUILER, AMBOS) | DEFAULT VENTA | Operación ofrecida |
| direccion | VARCHAR(255) | | Dirección de la propiedad |
| area_m2 | DECIMAL(10,2) | | Área construida/terreno |
| habitaciones | INT | | Nº de habitaciones |
| banos | INT | | Nº de baños |
| parqueaderos | INT | | Nº de parqueaderos |
| activa | TINYINT(1) | DEFAULT 1 | Visible en catálogo |
| fecha_registro | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Fecha de publicación |

## 9. `imagen_propiedad`
| Columna | Tipo | Restricción | Descripción |
|---------|------|-------------|-------------|
| id_imagen | INT | PK, AUTO_INCREMENT | Identificador |
| id_propiedad | INT | FK->propiedad | Propiedad |
| url_imagen | VARCHAR(500) | NOT NULL | Ruta del archivo |
| es_principal | TINYINT(1) | DEFAULT 0 | Imagen destacada |
| activa | TINYINT(1) | DEFAULT 1 | Visible |
| fecha_registro | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Fecha de carga |

## 10. `caracteristica`
| Columna | Tipo | Restricción | Descripción |
|---------|------|-------------|-------------|
| id_caracteristica | INT | PK, AUTO_INCREMENT | Identificador |
| nombre | VARCHAR(100) | NOT NULL | Piscina, Gym, Seguridad... |
| descripcion | VARCHAR(255) | | Descripción |

## 11. `propiedad_caracteristica` (N:M)
| Columna | Tipo | Restricción | Descripción |
|---------|------|-------------|-------------|
| id_propiedad | INT | PK, FK->propiedad | Propiedad |
| id_caracteristica | INT | PK, FK->caracteristica | Característica asignada |

## 12. `cita`
| Columna | Tipo | Restricción | Descripción |
|---------|------|-------------|-------------|
| id_cita | INT | PK, AUTO_INCREMENT | Identificador |
| id_cliente | INT | FK->usuario | Cliente solicitante |
| id_propiedad | INT | FK->propiedad | Propiedad visitada |
| fecha | DATE | NOT NULL | Fecha de la visita |
| hora | TIME | NOT NULL | Hora de la visita |
| estado | ENUM(PENDIENTE, APROBADA, RECHAZADA, CANCELADA) | DEFAULT PENDIENTE | Estado |
| observaciones | TEXT | | Notas de la visita |
| fecha_registro | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Fecha de solicitud |
| UQ | (id_cliente, id_propiedad, fecha, hora) | | No duplicar la misma cita |

## 13. `solicitud`
| Columna | Tipo | Restricción | Descripción |
|---------|------|-------------|-------------|
| id_solicitud | INT | PK, AUTO_INCREMENT | Identificador |
| id_cliente | INT | FK->usuario | Cliente |
| id_propiedad | INT | FK->propiedad | Propiedad |
| tipo | ENUM(COMPRA, ALQUILER) | NOT NULL | Tipo de operación |
| estado | ENUM(PENDIENTE, EN_REVISION, APROBADA, RECHAZADA) | DEFAULT PENDIENTE | Estado |
| observaciones | TEXT | | Observaciones |
| monto_ofrecido | DECIMAL(15,2) | | Oferta del cliente |
| fecha_registro | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Fecha de creación |

## 14. `documento_solicitud`
| Columna | Tipo | Restricción | Descripción |
|---------|------|-------------|-------------|
| id_documento | INT | PK, AUTO_INCREMENT | Identificador |
| id_solicitud | INT | FK->solicitud | Solicitud a la que pertenece |
| nombre_archivo | VARCHAR(255) | NOT NULL | Nombre original |
| ruta_archivo | VARCHAR(500) | NOT NULL | Ruta física del archivo |
| tipo_documento | VARCHAR(50) | | Tipo (cédula, certificado...) |
| fecha_subida | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Fecha de subida |

## 15. `favorito`
| Columna | Tipo | Restricción | Descripción |
|---------|------|-------------|-------------|
| id_favorito | INT | PK, AUTO_INCREMENT | Identificador |
| id_cliente | INT | FK->usuario | Cliente |
| id_propiedad | INT | FK->propiedad | Propiedad guardada |
| fecha | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Fecha |
| UQ | (id_cliente, id_propiedad) | | Evita duplicados |

## 16. `auditoria`
| Columna | Tipo | Restricción | Descripción |
|---------|------|-------------|-------------|
| id_auditoria | INT | PK, AUTO_INCREMENT | Identificador |
| id_usuario | INT | FK->usuario (SET NULL) | Usuario que ejecutó la acción |
| accion | VARCHAR(100) | NOT NULL | Acción: INICIO DE SESIÓN, CREACIÓN... |
| tabla_afectada | VARCHAR(50) | | Tabla modificada |
| id_registro | INT | | Id del registro afectado |
| detalles | TEXT | | Detalle de la acción |
| ip_address | VARCHAR(50) | | IP del cliente |
| fecha_hora | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Momento |

## 17. `token_recuperacion`
| Columna | Tipo | Restricción | Descripción |
|---------|------|-------------|-------------|
| id_token | BIGINT | PK, AUTO_INCREMENT | Identificador |
| id_usuario | INT | FK->usuario (CASCADE) | Usuario |
| token | VARCHAR(64) | UQ, NOT NULL | Hash SHA-256 del token aleatorio |
| expira_en | DATETIME | NOT NULL | Fecha de expiración (> NOW()) |
| usado | TINYINT(1) | DEFAULT 0 | 1 = ya consumido |
| fecha_creacion | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Fecha de generación |

## Resumen de conteos del seed (04_seed.sql)
rol=3, usuario=12, perfil=12, inmobiliaria=10, ciudad=10, tipo_propiedad=10,
propiedad=15, imagen_propiedad=30, caracteristica=15, propiedad_caracteristica=50,
cita=12, solicitud=12, documento_solicitud=4, favorito=12, auditoria=6.