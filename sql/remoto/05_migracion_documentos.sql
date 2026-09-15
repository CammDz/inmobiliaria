-- ============================================
-- SCRIPT 05 (REMOTO): ALMACENAMIENTO DE DOCUMENTOS EN BASE DE DATOS
-- Agrega la columna MEDIUMBLOB para guardar el contenido del archivo
-- en lugar de escribir en disco (el disco de Render es efímero).
--
-- Ejecutar contra la base en línea:
--   mysql -h HOST -u USUARIO -p NOMBRE_BD < sql/remoto/05_migracion_documentos.sql
-- ============================================

ALTER TABLE documento_solicitud ADD COLUMN contenido MEDIUMBLOB NULL AFTER tipo_documento;