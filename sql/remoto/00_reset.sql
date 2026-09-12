-- ============================================
-- SCRIPT 00 (REMOTO): REINICIO DE LA BASE REMOTA
-- Base remota: b4wfxuuy1vfroipr47zb
-- NOTA: En los proveedores en la nube la base de datos YA EXISTE y el usuario
--       NO puede ejecutar DROP DATABASE / CREATE DATABASE. Este script solo
--       elimina las 17 tablas para poder reimportar desde cero.
-- Después ejecutar en orden: 02, 03, 04.
-- Ejemplo:
--   mysql -h HOST_REMOTO -u USUARIO -p --default-character-set=utf8mb4 b4wfxuuy1vfroipr47zb < sql/remoto/00_reset.sql
-- ============================================

USE b4wfxuuy1vfroipr47zb;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS token_recuperacion;
DROP TABLE IF EXISTS auditoria;
DROP TABLE IF EXISTS favorito;
DROP TABLE IF EXISTS documento_solicitud;
DROP TABLE IF EXISTS solicitud;
DROP TABLE IF EXISTS cita;
DROP TABLE IF EXISTS propiedad_caracteristica;
DROP TABLE IF EXISTS caracteristica;
DROP TABLE IF EXISTS imagen_propiedad;
DROP TABLE IF EXISTS propiedad;
DROP TABLE IF EXISTS tipo_propiedad;
DROP TABLE IF EXISTS ciudad;
DROP TABLE IF EXISTS inmobiliaria;
DROP TABLE IF EXISTS perfil;
DROP TABLE IF EXISTS usuario_rol;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS rol;

SET FOREIGN_KEY_CHECKS = 1;