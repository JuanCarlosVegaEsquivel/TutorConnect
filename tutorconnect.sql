/*
  Script de creación de base de datos para TutorConnect
  Universidad Fidélitas - Desarrollo de Aplicaciones Web y Patrones

  Las contraseñas de los 3 usuarios de ejemplo son "123456"
*/

DROP DATABASE IF EXISTS tutorconnect;
CREATE DATABASE tutorconnect
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
USE tutorconnect;

-- Tablas

CREATE TABLE usuario (
  id_usuario BIGINT NOT NULL AUTO_INCREMENT,
  nombre     VARCHAR(100) NOT NULL,
  correo     VARCHAR(100) NOT NULL UNIQUE,
  contrasena VARCHAR(100) NOT NULL,
  rol        ENUM('ADMIN','TUTOR','ESTUDIANTE') NOT NULL DEFAULT 'ESTUDIANTE',
  activo     BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id_usuario)
) ENGINE = InnoDB;

CREATE TABLE asignatura (
  id_asignatura BIGINT NOT NULL AUTO_INCREMENT,
  nombre        VARCHAR(80) NOT NULL,
  descripcion   VARCHAR(255),
  activo        BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id_asignatura)
) ENGINE = InnoDB;

CREATE TABLE tutor (
  id_usuario BIGINT NOT NULL,
  biografia  VARCHAR(500),
  activo     BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id_usuario),
  FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE estudiante (
  id_usuario BIGINT NOT NULL,
  carrera    VARCHAR(100),
  telefono   VARCHAR(20),
  activo     BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id_usuario),
  FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE tutor_asignatura (
  id_usuario    BIGINT NOT NULL,
  id_asignatura BIGINT NOT NULL,
  PRIMARY KEY (id_usuario, id_asignatura),
  FOREIGN KEY (id_usuario) REFERENCES tutor(id_usuario) ON DELETE CASCADE,
  FOREIGN KEY (id_asignatura) REFERENCES asignatura(id_asignatura) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE estudiante_asignatura (
  id_usuario    BIGINT NOT NULL,
  id_asignatura BIGINT NOT NULL,
  PRIMARY KEY (id_usuario, id_asignatura),
  FOREIGN KEY (id_usuario) REFERENCES estudiante(id_usuario) ON DELETE CASCADE,
  FOREIGN KEY (id_asignatura) REFERENCES asignatura(id_asignatura) ON DELETE CASCADE
) ENGINE = InnoDB;

-- Módulo de Horarios (Horario.java): dia/horaInicio/horaFin como texto,
CREATE TABLE horario (
  id_horario  BIGINT NOT NULL AUTO_INCREMENT,
  dia         VARCHAR(20) NOT NULL,
  hora_inicio VARCHAR(5)  NOT NULL,
  hora_fin    VARCHAR(5)  NOT NULL,
  activo      BOOLEAN NOT NULL DEFAULT TRUE,
  id_usuario  BIGINT NOT NULL,
  PRIMARY KEY (id_horario),
  FOREIGN KEY (id_usuario) REFERENCES tutor(id_usuario) ON DELETE CASCADE
) ENGINE = InnoDB;

-- Módulo de Solicitudes (Solicitud.java): registra la petición del
-- estudiante hacia un tutor, para una asignatura y un horario puntual.
CREATE TABLE solicitud (
  id_solicitud    BIGINT NOT NULL AUTO_INCREMENT,
  id_estudiante   BIGINT NOT NULL,
  id_tutor        BIGINT NOT NULL,
  id_asignatura   BIGINT NOT NULL,
  id_horario      BIGINT NOT NULL,
  fecha_solicitud DATETIME NOT NULL,
  estado          ENUM('PENDIENTE','APROBADA','RECHAZADA','CANCELADA') NOT NULL DEFAULT 'PENDIENTE',
  comentario      VARCHAR(255),
  PRIMARY KEY (id_solicitud),
  FOREIGN KEY (id_estudiante) REFERENCES estudiante(id_usuario) ON DELETE CASCADE,
  FOREIGN KEY (id_tutor) REFERENCES tutor(id_usuario) ON DELETE CASCADE,
  FOREIGN KEY (id_asignatura) REFERENCES asignatura(id_asignatura) ON DELETE CASCADE,
  FOREIGN KEY (id_horario) REFERENCES horario(id_horario) ON DELETE CASCADE
) ENGINE = InnoDB;

-- Módulo de Sesiones (Sesion.java): el encuentro real de tutoría,
-- una vez que la solicitud fue aprobada.
CREATE TABLE sesion (
  id_sesion         BIGINT NOT NULL AUTO_INCREMENT,
  id_solicitud      BIGINT NOT NULL,
  fecha_hora        DATETIME NOT NULL,
  duracion_minutos  INT,
  estado            ENUM('PROGRAMADA','REALIZADA','CANCELADA') NOT NULL DEFAULT 'PROGRAMADA',
  observaciones     VARCHAR(500),
  PRIMARY KEY (id_sesion),
  UNIQUE (id_solicitud),
  FOREIGN KEY (id_solicitud) REFERENCES solicitud(id_solicitud) ON DELETE CASCADE
) ENGINE = InnoDB;

-- Módulo de Calificaciones (Calificacion.java): evaluación del
-- estudiante sobre una sesión ya realizada.
CREATE TABLE calificacion (
  id_calificacion BIGINT NOT NULL AUTO_INCREMENT,
  id_sesion       BIGINT NOT NULL,
  puntuacion      INT NOT NULL,
  comentario      VARCHAR(500),
  fecha           DATETIME NOT NULL,
  PRIMARY KEY (id_calificacion),
  UNIQUE (id_sesion),
  FOREIGN KEY (id_sesion) REFERENCES sesion(id_sesion) ON DELETE CASCADE
) ENGINE = InnoDB;

-- La contraseña de los tres es "123456", hasheada con BCrypt.

INSERT INTO usuario (nombre, correo, contrasena, rol, activo) VALUES
('Wilberth Molina', 'admin@fidelitas.ac.cr', '$2a$10$b9nSM2YVvwo6zwuXmRFeEODSpOAvM7nuPSP18B/gRn5G7Sm.V/WoW', 'ADMIN', true),
('Jorge Víquez', 'jviquez@fidelitas.ac.cr', '$2a$10$/IVVo9d/A7CZ.RvEqvwaZeUz3pF4OpjOEXhVFtijZGZjkjCxPcwcy', 'TUTOR', true),
('Carlos Mora', 'cmora@fidelitas.ac.cr', '$2a$10$tdtKVd6YJEf4dRL/iFlpo.gaUhOFNLMFqUwwinWstjIdxq71lbYYS', 'ESTUDIANTE', true);

INSERT INTO asignatura (nombre, descripcion, activo) VALUES
('Cálculo I', 'Límites, derivadas e integrales de una variable.', true),
('Programación I', 'Fundamentos de programación estructurada.', true),
('Programación II', 'Programación orientada a objetos con Java.', true),
('Bases de Datos', 'Modelo relacional y consultas SQL.', true);

-- id_usuario = 2 -> Jorge Víquez, como tutor
INSERT INTO tutor (id_usuario, biografia, activo) VALUES
(2, 'Ingeniero en Sistemas con 5 años de experiencia impartiendo tutorías.', true);

-- id_usuario = 3 -> Carlos Mora, como estudiante
INSERT INTO estudiante (id_usuario, carrera, telefono, activo) VALUES
(3, 'Ingeniería en Sistemas', '6023-1145', true);

INSERT INTO tutor_asignatura (id_usuario, id_asignatura) VALUES
(2, 1), (2, 2), (2, 3);

INSERT INTO estudiante_asignatura (id_usuario, id_asignatura) VALUES
(3, 1), (3, 4);

-- Horarios de ejemplo para Jorge Víquez (id_usuario = 2)
INSERT INTO horario (dia, hora_inicio, hora_fin, activo, id_usuario) VALUES
('Lunes', '14:00', '17:00', true, 2),
('Miércoles', '09:00', '12:00', true, 2),
('Viernes', '15:00', '18:00', true, 2);

-- Solicitud de ejemplo: Carlos Mora (estudiante) pide tutoría de
-- Cálculo I con Jorge Víquez, en su horario del lunes.
INSERT INTO solicitud (id_estudiante, id_tutor, id_asignatura, id_horario, fecha_solicitud, estado, comentario) VALUES
(3, 2, 1, 1, NOW(), 'APROBADA', 'Necesito reforzar límites y derivadas.');

INSERT INTO sesion (id_solicitud, fecha_hora, duracion_minutos, estado, observaciones) VALUES
(1, '2026-08-24 14:00:00', 60, 'REALIZADA', 'Se cubrió el tema de límites.');

INSERT INTO calificacion (id_sesion, puntuacion, comentario, fecha) VALUES
(1, 5, 'Excelente explicación, muy claro.', NOW());

-- --- Verificación ---
SELECT 'USUARIOS' AS '';
SELECT id_usuario, nombre, correo, rol, activo FROM usuario;

SELECT 'TUTORES Y SUS MATERIAS' AS '';
SELECT u.nombre AS tutor, GROUP_CONCAT(a.nombre SEPARATOR ', ') AS materias
  FROM tutor t
  JOIN usuario u ON u.id_usuario = t.id_usuario
  LEFT JOIN tutor_asignatura ta ON ta.id_usuario = t.id_usuario
  LEFT JOIN asignatura a ON a.id_asignatura = ta.id_asignatura
 GROUP BY t.id_usuario;

SELECT 'ESTUDIANTES' AS '';
SELECT u.nombre AS estudiante, e.carrera, e.telefono
  FROM estudiante e
  JOIN usuario u ON u.id_usuario = e.id_usuario;

SELECT 'HORARIOS POR TUTOR' AS '';
SELECT u.nombre AS tutor, h.dia, h.hora_inicio, h.hora_fin, h.activo
  FROM horario h
  JOIN tutor t ON t.id_usuario = h.id_usuario
  JOIN usuario u ON u.id_usuario = t.id_usuario
 ORDER BY u.nombre, h.dia;

SELECT 'SOLICITUDES' AS '';
SELECT s.id_solicitud, ue.nombre AS estudiante, ut.nombre AS tutor,
       a.nombre AS asignatura, s.estado, h.dia, h.hora_inicio, h.hora_fin
  FROM solicitud s
  JOIN estudiante e ON e.id_usuario = s.id_estudiante
  JOIN usuario ue ON ue.id_usuario = e.id_usuario
  JOIN tutor t ON t.id_usuario = s.id_tutor
  JOIN usuario ut ON ut.id_usuario = t.id_usuario
  JOIN asignatura a ON a.id_asignatura = s.id_asignatura
  JOIN horario h ON h.id_horario = s.id_horario;

SELECT 'SESIONES Y CALIFICACIONES' AS '';
SELECT se.id_sesion, se.fecha_hora, se.estado, c.puntuacion, c.comentario
  FROM sesion se
  LEFT JOIN calificacion c ON c.id_sesion = se.id_sesion;
