-- ============================================
-- SCRIPT 03: RESTRICCIONES E ÍNDICES
-- Proyecto: Inmobiliaria - Aplicación Web
-- ============================================

USE b4wfxuuy1vfroipr47zb;

-- ============================================
-- FOREIGN KEYS
-- ============================================

-- usuario_rol -> usuario
ALTER TABLE usuario_rol
    ADD CONSTRAINT fk_usuario_rol_usuario
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- usuario_rol -> rol
ALTER TABLE usuario_rol
    ADD CONSTRAINT fk_usuario_rol_rol
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- perfil -> usuario (relación 1:1)
ALTER TABLE perfil
    ADD CONSTRAINT fk_perfil_usuario
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- propiedad -> ciudad
ALTER TABLE propiedad
    ADD CONSTRAINT fk_propiedad_ciudad
    FOREIGN KEY (id_ciudad) REFERENCES ciudad(id_ciudad)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- propiedad -> tipo_propiedad
ALTER TABLE propiedad
    ADD CONSTRAINT fk_propiedad_tipo
    FOREIGN KEY (id_tipo) REFERENCES tipo_propiedad(id_tipo)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- propiedad -> inmobiliaria
ALTER TABLE propiedad
    ADD CONSTRAINT fk_propiedad_inmobiliaria
    FOREIGN KEY (id_inmobiliaria) REFERENCES inmobiliaria(id_inmobiliaria)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- propiedad -> usuario (agente)
ALTER TABLE propiedad
    ADD CONSTRAINT fk_propiedad_agente
    FOREIGN KEY (id_agente) REFERENCES usuario(id_usuario)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- imagen_propiedad -> propiedad
ALTER TABLE imagen_propiedad
    ADD CONSTRAINT fk_imagen_propiedad
    FOREIGN KEY (id_propiedad) REFERENCES propiedad(id_propiedad)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- propiedad_caracteristica -> propiedad
ALTER TABLE propiedad_caracteristica
    ADD CONSTRAINT fk_pc_propiedad
    FOREIGN KEY (id_propiedad) REFERENCES propiedad(id_propiedad)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- propiedad_caracteristica -> caracteristica
ALTER TABLE propiedad_caracteristica
    ADD CONSTRAINT fk_pc_caracteristica
    FOREIGN KEY (id_caracteristica) REFERENCES caracteristica(id_caracteristica)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- cita -> usuario (cliente)
ALTER TABLE cita
    ADD CONSTRAINT fk_cita_cliente
    FOREIGN KEY (id_cliente) REFERENCES usuario(id_usuario)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- cita -> propiedad
ALTER TABLE cita
    ADD CONSTRAINT fk_cita_propiedad
    FOREIGN KEY (id_propiedad) REFERENCES propiedad(id_propiedad)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- solicitud -> usuario (cliente)
ALTER TABLE solicitud
    ADD CONSTRAINT fk_solicitud_cliente
    FOREIGN KEY (id_cliente) REFERENCES usuario(id_usuario)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- solicitud -> propiedad
ALTER TABLE solicitud
    ADD CONSTRAINT fk_solicitud_propiedad
    FOREIGN KEY (id_propiedad) REFERENCES propiedad(id_propiedad)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- documento_solicitud -> solicitud
ALTER TABLE documento_solicitud
    ADD CONSTRAINT fk_documento_solicitud
    FOREIGN KEY (id_solicitud) REFERENCES solicitud(id_solicitud)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- favorito -> usuario (cliente)
ALTER TABLE favorito
    ADD CONSTRAINT fk_favorito_cliente
    FOREIGN KEY (id_cliente) REFERENCES usuario(id_usuario)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- favorito -> propiedad
ALTER TABLE favorito
    ADD CONSTRAINT fk_favorito_propiedad
    FOREIGN KEY (id_propiedad) REFERENCES propiedad(id_propiedad)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- auditoria -> usuario
ALTER TABLE auditoria
    ADD CONSTRAINT fk_auditoria_usuario
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
    ON DELETE SET NULL ON UPDATE CASCADE;

-- token_recuperacion -> usuario
ALTER TABLE token_recuperacion
    ADD CONSTRAINT fk_token_recuperacion_usuario
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- ============================================
-- UNIQUE CONSTRAINTS (mínimo 3 importantes)
-- ============================================

-- 1. Correo único por usuario
ALTER TABLE usuario
    ADD CONSTRAINT uq_usuario_correo
    UNIQUE (correo);

-- 2. Matrícula inmobiliaria única por propiedad
ALTER TABLE propiedad
    ADD CONSTRAINT uq_propiedad_matricula
    UNIQUE (matricula_inmobiliaria);

-- 3. Perfil único por usuario (relación 1:1)
ALTER TABLE perfil
    ADD CONSTRAINT uq_perfil_usuario
    UNIQUE (id_usuario);

-- 4. Nombre de rol único
ALTER TABLE rol
    ADD CONSTRAINT uq_rol_nombre
    UNIQUE (nombre);

-- 5. Favorito duplicado (un cliente no puede guardar la misma propiedad dos veces)
ALTER TABLE favorito
    ADD CONSTRAINT uq_favorito_cliente_propiedad
    UNIQUE (id_cliente, id_propiedad);

-- 6. Cita duplicada para el mismo cliente, propiedad, fecha y hora
ALTER TABLE cita
    ADD CONSTRAINT uq_cita_cliente_propiedad_fecha_hora
    UNIQUE (id_cliente, id_propiedad, fecha, hora);

-- ============================================
-- ÍNDICES PARA MEJOR RENDIMIENTO
-- ============================================

CREATE INDEX idx_propiedad_ciudad ON propiedad(id_ciudad);
CREATE INDEX idx_propiedad_tipo ON propiedad(id_tipo);
CREATE INDEX idx_propiedad_estado ON propiedad(estado);
CREATE INDEX idx_propiedad_precio ON propiedad(precio);
CREATE INDEX idx_cita_fecha ON cita(fecha);
CREATE INDEX idx_solicitud_estado ON solicitud(estado);
CREATE INDEX idx_auditoria_fecha ON auditoria(fecha_hora);
