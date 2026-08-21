/*
  Script de creación de base de datos para TutorConnect
  Universidad Fidélitas - Desarrollo de Aplicaciones Web y Patrones

  Alineado 1:1 con las entidades Java del repositorio, más el campo
  "rol" en usuario (ADMIN / TUTOR / ESTUDIANTE) para el login con
  Spring Security.

  Las contraseñas de los 3 usuarios de ejemplo son "123456", guardadas
  como hash BCrypt (obligatorio: Spring Security necesita un
  PasswordEncoder para comparar contraseñas, y BCrypt es el estándar).

  PENDIENTE seg\u00fan el enunciado del proyecto (secci\u00f3n 10.3):
    - Todav\u00eda no existe una tabla transaccional (ej. "tutoria") que
      registre las solicitudes entre estudiante y tutor. El enunciado
      pide "al menos una tabla destinada a registrar transacciones".
*/

DROP DATABASE IF EXISTS tutorconnect;
CREATE DATABASE tutorconnect
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
USE tutorconnect;

-- --- Tablas ---

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
-- porque la entidad los define como String, no como DayOfWeek/TIME.
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

-- --- Datos de ejemplo ---
-- La contraseña de los tres es "123456", hasheada con BCrypt.

INSERT INTO usuario (nombre, correo, contrasena, rol, activo) VALUES
('Andrea Solano', 'admin@fidelitas.ac.cr', '$2a$10$b9nSM2YVvwo6zwuXmRFeEODSpOAvM7nuPSP18B/gRn5G7Sm.V/WoW', 'ADMIN', true),
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
