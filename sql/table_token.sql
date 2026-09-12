-- Cambios incrementales de seguridad (ya incluidos en 02/03)
-- Tabla de tokens de recuperación de contraseña
CREATE TABLE IF NOT EXISTS token_recuperacion (
    id_token BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    token VARCHAR(64) NOT NULL,
    expira_en DATETIME NOT NULL,
    usado TINYINT(1) DEFAULT 0,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_token_recuperacion_valor UNIQUE (token)
) ENGINE=InnoDB;

-- FK token_recuperacion -> usuario
ALTER TABLE token_recuperacion
    ADD CONSTRAINT fk_token_recuperacion_usuario
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- UNIQUE cita: un cliente no repite la misma cita (propiedad, fecha y hora)
ALTER TABLE cita
    ADD CONSTRAINT uq_cita_cliente_propiedad_fecha_hora
    UNIQUE (id_cliente, id_propiedad, fecha, hora);