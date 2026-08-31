-- =====================================================================
-- SICA - data.sql - Datos iniciales / semilla
-- =====================================================================
USE sica_db;

-- ---------- ROLES ----------
INSERT INTO roles (nombre_rol) VALUES
('Superusuario'),
('Supervisor de Seguridad'),
('Guarda de Seguridad'),
('Funcionario de Empresa');

-- ---------- PERMISOS ----------
INSERT INTO permisos (nombre_permiso, descripcion) VALUES
('crear_usuario', 'Crear cuentas de usuario del sistema'),
('editar_usuario', 'Editar cuentas de usuario existentes'),
('eliminar_usuario', 'Eliminar/desactivar cuentas de usuario'),
('registrar_visita', 'Registrar el ingreso de una persona (check-in)'),
('aprobar_visita', 'Aprobar o rechazar una visita pendiente'),
('registrar_salida', 'Registrar la salida de una persona (check-out)'),
('crear_persona', 'Registrar una nueva persona (trabajador o invitado)'),
('editar_persona', 'Editar los datos de una persona existente'),
('bloquear_persona', 'Cambiar el estado de acceso de una persona a bloqueado'),
('registrar_incidente', 'Reportar un incidente de seguridad'),
('generar_reporte_auditoria', 'Consultar la bitácora de auditoría'),
('gestionar_empresas', 'Crear/editar empresas registradas en el complejo');

-- ---------- ROL_PERMISOS ----------
-- Superusuario (id 1): todos los permisos
INSERT INTO rol_permisos (rol_id, permiso_id) SELECT 1, id FROM permisos;

-- Supervisor de Seguridad (id 2): todo lo operativo + incidentes + auditoría, no gestión de usuarios/empresas
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT 2, id FROM permisos WHERE nombre_permiso IN
('registrar_visita','aprobar_visita','registrar_salida','crear_persona','editar_persona',
 'bloquear_persona','registrar_incidente','generar_reporte_auditoria');

-- Guarda de Seguridad (id 3): solo operación de puerta
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT 3, id FROM permisos WHERE nombre_permiso IN
('registrar_visita','registrar_salida','crear_persona','registrar_incidente');

-- Funcionario de Empresa (id 4): aprobar visitas de su empresa + registrar invitados propios
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT 4, id FROM permisos WHERE nombre_permiso IN
('aprobar_visita','crear_persona','editar_persona');

-- ---------- USUARIOS DE EJEMPLO (una credencial por rol) ----------
-- NOTA: en este proyecto académico la contraseña se guarda con SHA-256 simple
-- (ver PasswordUtil.java). Contraseña en texto plano para todos: "Acme#2026"
-- hash SHA-256 real de "Acme#2026" (verificado con PasswordUtil.hash):
INSERT INTO usuarios (nombre, email, password, rol_id, esta_activo) VALUES
('Ana Restrepo (Superusuario)', 'admin@acme.com', '3e7ff9f69d0598165959220f5cf4a6b3ed498d2031ebe5b3f59924bbf34aa5ad', 1, TRUE),
('Carlos Duarte (Supervisor Seguridad)', 'supervisor@acme.com', '3e7ff9f69d0598165959220f5cf4a6b3ed498d2031ebe5b3f59924bbf34aa5ad', 2, TRUE),
('Jorge Ramírez (Guarda)', 'guarda@acme.com', '3e7ff9f69d0598165959220f5cf4a6b3ed498d2031ebe5b3f59924bbf34aa5ad', 3, TRUE),
('Laura Gómez (Funcionaria TechCorp)', 'funcionario@acme.com', '3e7ff9f69d0598165959220f5cf4a6b3ed498d2031ebe5b3f59924bbf34aa5ad', 4, TRUE);

-- ---------- ESTADOS DE ACCESO DE PERSONA ----------
INSERT INTO persona_estados_acceso (nombre_estado) VALUES
('Activo'),
('Con Prohibicion de Ingreso');

-- ---------- ESTADOS DE VISITA ----------
INSERT INTO visita_estados (nombre_estado) VALUES
('Dentro'),
('Fuera'),
('Pendiente de Aprobacion'),
('Aprobado'),
('Rechazado'),
('Expirado'),
('Cerrada por Sistema (Salida Olvidada)');

-- ---------- EMPRESAS ----------
INSERT INTO empresas (nombre, contacto_principal) VALUES
('TechCorp SAS', 'Laura Gómez'),
('Innova Logistics', 'Pedro Salcedo'),
('Acme Financial Group', 'Marcela Torres');

-- ---------- PERSONAS DE EJEMPLO ----------
INSERT INTO personas (nombre, documento_identidad, empresa_id, tipo_persona, estado_acceso_id, url_foto) VALUES
('Diego Fernández', '1098765432', 1, 'Trabajador', 1, NULL),
('Mariana López', '1122334455', NULL, 'Invitado', 1, NULL);
