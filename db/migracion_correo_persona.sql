-- =====================================================================
-- SICA - migracion_correo_persona.sql
-- Ejecutar UNA sola vez sobre una base de datos que ya existía antes de
-- agregar el campo "correo" a personas (necesario para el botón
-- "Gestionar persona"). schema.sql ya incluye esta columna para
-- instalaciones nuevas; este script es solo para actualizar la tuya.
-- =====================================================================
USE sica_db;

ALTER TABLE personas
    ADD COLUMN correo VARCHAR(150) AFTER documento_identidad;
