-- ============================================
-- SCRIPT 04: DATOS DE PRUEBA (SEED) - MÍNIMO 10 REGISTROS POR TABLA PRINCIPAL
-- Proyecto: Inmobiliaria - Aplicación Web
-- ============================================
-- Ejecutar sobre una base vacía (después de 01, 02 y 03).
-- En MySQL/MariaDB:   mysql -u root -p < sql/04_seed.sql
-- ============================================

USE b4wfxuuy1vfroipr47zb;

-- ============================================
-- ROLES (4)
-- ============================================
INSERT INTO rol (nombre, descripcion) VALUES
('Administrador', 'Acceso total al sistema'),
('Agente Inmobiliario', 'Gestiona propiedades, citas y solicitudes'),
('Cliente', 'Busca propiedades, solicita citas y gestiona favoritos'),
('Visitante', 'Usuario no autenticado que navega el catálogo público y el detalle de propiedades');

-- ============================================
-- INMOBILIARIAS (10)
-- ============================================
INSERT INTO inmobiliaria (nombre, direccion, telefono, correo, activa) VALUES
('Inmobiliaria del Norte', 'Cra 45 #67-10, Medellín', '604-2345678', 'norte@inmobiliaria.com', 1),
('Inmobiliaria Valle', 'Cra 7 #32-50, Cali', '602-3456789', 'valle@inmobiliaria.com', 1),
('Inmobiliaria Caribe', 'Cra 54 #12-30, Barranquilla', '605-4567890', 'caribe@inmobiliaria.com', 1),
('Inmobiliaria Andes', 'Cra 15 #88-22, Bogotá', '601-5678901', 'andes@inmobiliaria.com', 1),
('Inmobiliaria Centro', 'Cra 20 #45-15, Pereira', '606-6789012', 'centro@inmobiliaria.com', 1),
('Inmobiliaria Costa', 'Cra 3 #10-45, Santa Marta', '604-7890123', 'costa@inmobiliaria.com', 1),
('Inmobiliaria Metro', 'Cra 80 #55-20, Medellín', '604-8901234', 'metro@inmobiliaria.com', 1),
('Inmobiliaria Progresar', 'Cra 27 #15-60, Bucaramanga', '607-9012345', 'progresar@inmobiliaria.com', 1),
('Inmobiliaria Hogar', 'Cra 5 #73-18, Bogotá', '601-0123456', 'hogar@inmobiliaria.com', 1),
('Inmobiliaria Futuro', 'Cra 12 #34-70, Cartagena', '605-1234567', 'futuro@inmobiliaria.com', 1);

-- ============================================
-- CIUDADES (10)
-- ============================================
INSERT INTO ciudad (nombre, departamento) VALUES
('Medellín', 'Antioquia'),
('Bogotá', 'Cundinamarca'),
('Cali', 'Valle del Cauca'),
('Barranquilla', 'Atlántico'),
('Bucaramanga', 'Santander'),
('Cartagena', 'Bolívar'),
('Pereira', 'Risaralda'),
('Manizales', 'Caldas'),
('Armenia', 'Quindío'),
('Santa Marta', 'Magdalena');

-- ============================================
-- TIPOS DE PROPIEDAD (10)
-- ============================================
INSERT INTO tipo_propiedad (nombre, descripcion) VALUES
('Apartamento', 'Unidad residencial en edificio'),
('Casa', 'Vivienda unifamiliar independiente'),
('Local Comercial', 'Espacio para actividad comercial'),
('Oficina', 'Espacio para trabajo profesional'),
('Lote', 'Terreno para construcción'),
('Finca', 'Propiedad rural'),
('Penthouse', 'Ático de lujo en edificio'),
('Duplex', 'Vivienda de dos niveles'),
('Estudio', 'Apartaestudio pequeño'),
('Casa Campestre', 'Casa en zona rural cercana');

-- ============================================
-- CARACTERÍSTICAS (15)
-- ============================================
INSERT INTO caracteristica (nombre, descripcion) VALUES
('Piscina', 'Área de natación'),
('Gym', 'Gimnasio equipado'),
('Seguridad 24h', 'Vigilancia permanente'),
('Parqueadero', 'Espacio para vehículo'),
('Amoblado', 'Incluye mobiliario'),
('Terraza', 'Espacio al aire libre'),
('Jardín', 'Área verde privada'),
('BBQ', 'Zona de parrilla'),
('Salón Comunal', 'Espacio para eventos'),
('Vigilancia', 'Sistema de seguridad'),
('Cerca al Metro', 'Acceso a transporte masivo'),
('Vista al Río', 'Vista panorámica'),
('Ascensor', 'Acceso vertical'),
('Bodega', 'Espacio de almacenamiento'),
('Aire Acondicionado', 'Sistema de climatización');

-- ============================================
-- USUARIOS (12) - contraseña de todos: 123456
-- Hashes BCrypt (jBCrypt, $2a$10$) para "123456"
-- ============================================
INSERT INTO usuario (correo, contrasena, nombre, apellido, telefono, activo) VALUES
('admin@inmovain.com',   '$2a$10$2wXnEw5AzsJNtZ/SLV6mfeDfrjywri27ul6QGF8NEaT9KvNktDWNW', 'Carlos',  'Ramírez',  '311-5550001', 1),
('admin2@inmovain.com',  '$2a$10$DWKs6k9QTQgK7tY9wM5LZ.94zpTAbXL0ZP7TZe6SnBvyb6Mp67Mj.', 'Laura',   'Gómez',    '311-5550002', 1),
('agente1@inmovain.com', '$2a$10$UfcEZdOoGTQUGldj/gYGxehmkuq5QRR3wx11Oqwb7NXrVr6EIHfLO', 'Andrés',  'Martínez', '311-5550011', 1),
('agente2@inmovain.com', '$2a$10$2wXnEw5AzsJNtZ/SLV6mfeDfrjywri27ul6QGF8NEaT9KvNktDWNW', 'Diana',   'Pérez',    '311-5550012', 1),
('agente3@inmovain.com', '$2a$10$DWKs6k9QTQgK7tY9wM5LZ.94zpTAbXL0ZP7TZe6SnBvyb6Mp67Mj.', 'Felipe',  'Torres',   '311-5550013', 1),
('cliente1@inmovain.com','$2a$10$UfcEZdOoGTQUGldj/gYGxehmkuq5QRR3wx11Oqwb7NXrVr6EIHfLO', 'Carolina','Ortiz',    '311-5550101', 1),
('cliente2@inmovain.com','$2a$10$2wXnEw5AzsJNtZ/SLV6mfeDfrjywri27ul6QGF8NEaT9KvNktDWNW', 'Juan',    'Ríos',     '311-5550102', 1),
('cliente3@inmovain.com','$2a$10$DWKs6k9QTQgK7tY9wM5LZ.94zpTAbXL0ZP7TZe6SnBvyb6Mp67Mj.', 'María',   'Cabrera',  '311-5550103', 1),
('cliente4@inmovain.com','$2a$10$UfcEZdOoGTQUGldj/gYGxehmkuq5QRR3wx11Oqwb7NXrVr6EIHfLO', 'Diego',   'Suárez',   '311-5550104', 1),
('cliente5@inmovain.com','$2a$10$2wXnEw5AzsJNtZ/SLV6mfeDfrjywri27ul6QGF8NEaT9KvNktDWNW', 'Valentina','Mora',    '311-5550105', 1),
('cliente6@inmovain.com','$2a$10$DWKs6k9QTQgK7tY9wM5LZ.94zpTAbXL0ZP7TZe6SnBvyb6Mp67Mj.', 'Camilo',  'Herrera',  '311-5550106', 1),
('cliente7@inmovain.com','$2a$10$UfcEZdOoGTQUGldj/gYGxehmkuq5QRR3wx11Oqwb7NXrVr6EIHfLO', 'Paula',   'Sánchez',  '311-5550107', 1);

-- ============================================
-- PERFILES (12) - relación 1:1 con usuario
-- ============================================
INSERT INTO perfil (id_usuario, direccion, documento_identidad, fecha_nacimiento) VALUES
(1,  'Cra 33 #45-10 Medellín',            '1036-550001', '1985-03-12'),
(2,  'Cll 80 #20-30 Bogotá',              '1023-550002', '1990-07-25'),
(3,  'Cra 45 #67-10 Medellín',            '1040-550011', '1988-11-02'),
(4,  'Cll 5 #20-60 Cali',                 '1143-550012', '1992-01-18'),
(5,  'Av 15 #100-22 Bogotá',              '1023-550013', '1986-09-30'),
(6,  'Cll 44 #70-32 Medellín',            '1037-550101', '1995-04-14'),
(7,  'Av 68 #95-12 Bogotá',               '1020-550102', '1993-12-08'),
(8,  'Cra 9 #45-22 Cali',                 '1143-550103', '1998-06-21'),
(9,  'Cll 55 #80-45 Bogotá',              '1022-550104', '1991-02-17'),
(10, 'Av La Playa #42-10 Medellín',       '1037-550105', '1996-10-05'),
(11, 'Cra 82 #55-20 Medellín',            '1038-550106', '1994-08-29'),
(12, 'Cll 100 #15-40 Bogotá',             '1021-550107', '1997-05-11');

-- ============================================
-- USUARIO_ROL (N:M)
-- ============================================
INSERT INTO usuario_rol (id_usuario, id_rol) VALUES
(1, 1), (2, 1),
(3, 2), (4, 2), (5, 2),
(6, 3), (7, 3), (8, 3), (9, 3), (10, 3), (11, 3), (12, 3);

-- ============================================
-- PROPIEDADES (15)
-- ============================================
INSERT INTO propiedad (titulo, descripcion, precio, id_ciudad, id_tipo, id_inmobiliaria, id_agente,
                       matricula_inmobiliaria, estado, tipo_operacion, direccion, area_m2,
                       habitaciones, banos, parqueaderos, activa) VALUES
('Penthouse Vista al Río', 'Apartamento de lujo con vista panorámica al río, acabados premium.', 1850000000, 1, 7, 7, 3, '001-000001', 'DISPONIBLE', 'VENTA', 'Cra 80 #55-20 Torre 3', 220, 4, 3, 2, 1),
('Casa Finca Los Mangos', 'Casa campestre con amplio terreno, ideal para descanso familiar.', 3500000, 10, 10, 6, 3, '002-000002', 'DISPONIBLE', 'ALQUILER', 'Vía a Minca, Km 5', 480, 5, 3, 4, 1),
('Apartamento Centro Histórico', 'Excelente ubicación en el centro de la ciudad.', 1250000000, 2, 1, 4, 4, '003-000003', 'RESERVADA', 'VENTA', 'Cra 6 #19-30', 95, 3, 2, 1, 1),
('Local Comercial El Diamante', 'Local comercial con gran afluencia de público.', 780000000, 3, 3, 2, 4, '004-000004', 'DISPONIBLE', 'AMBOS', 'Cra 7 #8-50', 60, 0, 1, 2, 1),
('Oficina Torre Empresarial', 'Oficina en torre empresarial con todos los servicios.', 950000000, 2, 4, 9, 5, '005-000005', 'DISPONIBLE', 'VENTA', 'Av 100 #15-40', 85, 0, 1, 2, 1),
('Lote Industrial Autopista', 'Lote con acceso directo a la autopista, uso industrial.', 420000000, 7, 5, 5, 5, '006-000006', 'DISPONIBLE', 'VENTA', 'Autopista Sur Km 4', 5000, 0, 0, 0, 1),
('Finca Eco-turística El Paraíso', 'Finca con riqueza natural, apta para ecoturismo.', 2800000000, 5, 6, 8, 3, '007-000007', 'DISPONIBLE', 'VENTA', 'Vereda El Picacho', 8000, 6, 4, 3, 1),
('Duplex Familiar Altavista', 'Duplex en conjunto cerrado con área social amplia.', 980000000, 1, 8, 1, 4, '008-000008', 'RESERVADA', 'VENTA', 'Cll 44 #70-15', 170, 4, 3, 2, 1),
('Apartaestudio Moderno Sur', 'Apartaestudio amoblado cerca a universidades.', 1800000, 3, 9, 2, 3, '009-000009', 'DISPONIBLE', 'ALQUILER', 'Cll 5 #35-20', 45, 1, 1, 0, 1),
('Casa Campestre El Roble', 'Casa campestre con jardines y zona de parrilla.', 1150000000, 9, 10, 4, 5, '010-000010', 'DISPONIBLE', 'VENTA', 'Vía al Quindío, sector Roble', 350, 4, 3, 2, 1),
('Apartamento Lago Urbano', 'Vista al lago, conjunto cerrado con piscina y gym.', 1480000000, 1, 1, 7, 3, '011-000011', 'DISPONIBLE', 'VENTA', 'Cll 30 #80-10', 110, 3, 2, 1, 1),
('Casa Colonial El Carmen', 'Casa colonial restaurada en zona histórica.', 890000000, 4, 2, 3, 4, '012-000012', 'DISPONIBLE', 'AMBOS', 'Cll 35 #15-20', 220, 4, 3, 2, 1),
('Penthouse Skyline 300', 'Penthouse de lujo en el piso 30 con vista 360 grados.', 3600000000, 2, 7, 9, 5, '013-000013', 'VENDIDA', 'VENTA', 'Av El Dorado #80-50', 300, 5, 4, 3, 1),
('Oficina Ejecutiva Centro Sur', 'Oficina amoblada lista para usar.', 4200000, 3, 4, 2, 3, '014-000014', 'DISPONIBLE', 'ALQUILER', 'Cll 22 #50-30', 120, 0, 2, 1, 1),
('Lote Urbanizable Villa Verde', 'Lote con topografía plana, fácil de urbanizar.', 260000000, 8, 5, 5, 4, '015-000015', 'DISPONIBLE', 'VENTA', 'Sector La Enea', 3000, 0, 0, 0, 1);

-- ============================================
-- IMÁGENES DE PROPIEDAD (30) - archivos en images/propiedades/
-- ============================================
INSERT INTO imagen_propiedad (id_propiedad, url_imagen, es_principal, activa) VALUES
(1,  'images/propiedades/prop1_1.jpg',  1, 1), (1,  'images/propiedades/prop1_2.jpg',  0, 1),
(2,  'images/propiedades/prop2_1.jpg',  1, 1), (2,  'images/propiedades/prop2_2.jpg',  0, 1),
(3,  'images/propiedades/prop3_1.jpg',  1, 1), (3,  'images/propiedades/prop3_2.jpg',  0, 1),
(4,  'images/propiedades/prop4_1.jpg',  1, 1), (4,  'images/propiedades/prop4_2.jpg',  0, 1),
(5,  'images/propiedades/prop5_1.jpg',  1, 1), (5,  'images/propiedades/prop5_2.jpg',  0, 1),
(6,  'images/propiedades/prop6_1.jpg',  1, 1), (6,  'images/propiedades/prop6_2.jpg',  0, 1),
(7,  'images/propiedades/prop7_1.jpg',  1, 1), (7,  'images/propiedades/prop7_2.jpg',  0, 1),
(8,  'images/propiedades/prop8_1.jpg',  1, 1), (8,  'images/propiedades/prop8_2.jpg',  0, 1),
(9,  'images/propiedades/prop9_1.jpg',  1, 1), (9,  'images/propiedades/prop9_2.jpg',  0, 1),
(10, 'images/propiedades/prop10_1.jpg', 1, 1), (10, 'images/propiedades/prop10_2.jpg', 0, 1),
(11, 'images/propiedades/prop11_1.jpg', 1, 1), (11, 'images/propiedades/prop11_2.jpg', 0, 1),
(12, 'images/propiedades/prop12_1.jpg', 1, 1), (12, 'images/propiedades/prop12_2.jpg', 0, 1),
(13, 'images/propiedades/prop13_1.jpg', 1, 1), (13, 'images/propiedades/prop13_2.jpg', 0, 1),
(14, 'images/propiedades/prop14_1.jpg', 1, 1), (14, 'images/propiedades/prop14_2.jpg', 0, 1),
(15, 'images/propiedades/prop15_1.jpg', 1, 1), (15, 'images/propiedades/prop15_2.jpg', 0, 1);

-- ============================================
-- PROPIEDAD_CARACTERISTICA (N:M) - 50 relaciones
-- ============================================
INSERT INTO propiedad_caracteristica (id_propiedad, id_caracteristica) VALUES
(1, 1), (1, 3), (1, 7), (1, 9), (1, 12), (1, 13),
(2, 5), (2, 6), (2, 7), (2, 8),
(3, 3), (3, 9), (3, 13),
(4, 3), (4, 9), (4, 10),
(5, 9), (5, 10), (5, 13),
(6, 9), (6, 10),
(7, 1), (7, 6), (7, 7), (7, 8), (7, 11),
(8, 5), (8, 6), (8, 13),
(9, 5), (9, 13),
(10, 5), (10, 6), (10, 7), (10, 10),
(11, 3), (11, 9), (11, 12), (11, 13),
(12, 6), (12, 7), (12, 9),
(13, 1), (13, 3), (13, 9), (13, 13),
(14, 10), (14, 13),
(15, 9), (15, 10);

-- ============================================
-- CITAS (12) - UNIQUE (id_cliente, id_propiedad, fecha, hora)
-- ============================================
INSERT INTO cita (id_cliente, id_propiedad, fecha, hora, estado, observaciones) VALUES
(6,  1,  '2026-09-25', '16:30', 'APROBADA',   'Visita coordinada por el agente Andrés Martínez'),
(6,  3,  '2026-09-20', '10:00', 'PENDIENTE',  'Cliente interesado en negociación'),
(7,  2,  '2026-09-22', '09:30', 'PENDIENTE',  'Verificar disponibilidad de servicios'),
(7,  5,  '2026-09-28', '11:00', 'APROBADA',   'Visita a oficina ejecutiva'),
(8,  4,  '2026-10-01', '15:00', 'PENDIENTE',  'Interés en local comercial'),
(8,  6,  '2026-10-05', '10:30', 'RECHAZADA',  'No cumple requisitos de financiación'),
(9,  7,  '2026-10-08', '09:00', 'PENDIENTE',  'Interés en finca ecoturística'),
(9,  9,  '2026-10-12', '14:00', 'APROBADA',   'Apartaestudio para estudiante'),
(10, 10, '2026-10-15', '16:00', 'PENDIENTE',  'Evaluar zona campestre'),
(10, 12, '2026-10-18', '11:30', 'CANCELADA',  'Cliente desistió de la visita'),
(11, 11, '2026-10-20', '10:00', 'PENDIENTE',  'Interés en conjunto con piscina'),
(12, 14, '2026-10-22', '09:30', 'PENDIENTE',  'Oficina para nueva sede');

-- ============================================
-- SOLICITUDES (12)
-- ============================================
INSERT INTO solicitud (id_cliente, id_propiedad, tipo, estado, observaciones, monto_ofrecido) VALUES
(6,  1,  'COMPRA',  'APROBADA',    'Oferta inicial aceptada por el vendedor', 1800000000),
(6,  3,  'COMPRA',  'EN_REVISION', 'En estudio de crédito',                   1200000000),
(7,  2,  'ALQUILER', 'APROBADA',   'Contrato de arrendamiento firmado',       3400000),
(7,  5,  'COMPRA',  'PENDIENTE',   'A la espera de documentos',               900000000),
(8,  4,  'COMPRA',  'EN_REVISION', 'Negociación de forma de pago',            760000000),
(8,  6,  'COMPRA',  'RECHAZADA',   'Oferta muy baja',                         400000000),
(9,  7,  'COMPRA',  'PENDIENTE',   'Solicitando visita adicional',            2700000000),
(9,  9,  'ALQUILER', 'APROBADA',   'Arrendamiento de un año',                 1800000),
(10, 10, 'COMPRA',  'PENDIENTE',   'En estudio de avalúo',                    1100000000),
(10, 12, 'COMPRA',  'EN_REVISION', 'Revisión de título',                      870000000),
(11, 11, 'COMPRA',  'PENDIENTE',   'Nueva oferta presentada',                 1450000000),
(12, 14, 'ALQUILER', 'PENDIENTE',  'Solicitud en revisión',                   4000000);

-- ============================================
-- DOCUMENTO_SOLICITUD (4) - ilustrativos
-- ============================================
INSERT INTO documento_solicitud (id_solicitud, nombre_archivo, ruta_archivo, tipo_documento) VALUES
(1, 'cedula_carolina.pdf',      'uploads/cedula_carolina.pdf',      'CEDULA'),
(1, 'certificado_tradicion.pdf','uploads/certificado_tradicion.pdf','CERTIFICADO TRADICIÓN'),
(2, 'certificado_ingresos.pdf', 'uploads/certificado_ingresos.pdf', 'CERTIFICADO INGRESOS'),
(3, 'comprobante_pago.pdf',     'uploads/comprobante_pago.pdf',     'COMPROBANTE PAGO');

-- ============================================
-- FAVORITOS (12) - UNIQUE (id_cliente, id_propiedad)
-- ============================================
INSERT INTO favorito (id_cliente, id_propiedad) VALUES
(6, 1), (6, 7), (6, 13),
(7, 2), (7, 4),
(8, 3), (8, 5), (8, 8),
(9, 9), (9, 15),
(10, 11), (10, 12);

-- ============================================
-- AUDITORÍA (6) - registro inicial de ejemplo
-- ============================================
INSERT INTO auditoria (id_usuario, accion, tabla_afectada, id_registro, detalles, ip_address) VALUES
(1, 'INICIO DE SESIÓN',    'usuario',   1, 'Acceso inicial administrador', '127.0.0.1'),
(3, 'CREACIÓN DE PROPIEDAD','propiedad', 1, 'Se cargó propiedad con datos semilla', '127.0.0.1'),
(4, 'CREACIÓN DE PROPIEDAD','propiedad', 4, 'Se cargó propiedad con datos semilla', '127.0.0.1'),
(6, 'SOLICITUD DE CITA',   'cita',      1, 'Se registró cita de demostración',       '127.0.0.1'),
(6, 'CREACIÓN DE SOLICITUD','solicitud', 1, 'Solicitud de compra de demostración',    '127.0.0.1'),
(6, 'AGREGAR A FAVORITOS', 'favorito',  1, 'Propiedad agregada a favoritos',         '127.0.0.1');