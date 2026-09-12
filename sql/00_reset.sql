-- ============================================
-- SCRIPT 00: REINICIO TOTAL DE LA BASE DE DATOS
-- Elmina la base de datos inmobiliaria_db y la recrea vacía.
-- Después ejecutar en orden: 01, 02, 03, 04.
-- ============================================

DROP DATABASE IF EXISTS inmobiliaria_db;
CREATE DATABASE inmobiliaria_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;