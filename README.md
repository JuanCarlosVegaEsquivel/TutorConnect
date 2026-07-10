# TutorConnect - Sistema de Gestión de Tutorías Universitarias

Proyecto del curso Desarrollo de Aplicaciones Web  
Universidad Fidélitas - 2026

**Integrantes:**
- Henry Gerardo Izaba Díaz
- Juan Carlos Vega Esquivel
- Jorge Viquez Espinoza
- Gabriel Martinez Quesada
---
## ¿De qué trata el proyecto?
- TutorConnect es una plataforma web para gestionar tutorías académicas entre estudiantes y tutores de una universidad. La idea es reemplazar la coordinación informal (WhatsApp, correos, etc.) por un sistema centralizado donde se puedan solicitar, aprobar y dar seguimiento a las sesiones de tutoría.
---
## Usuarios del sistema
- **Estudiante:** solicita tutorías y da seguimiento a sus sesiones
- **Tutor:** gestiona su disponibilidad y atiende solicitudes
- **Administrador:** supervisa el sistema y gestiona usuarios
  
---

## Funcionalidades principales
- Registro e inicio de sesión por rol
- Búsqueda de tutores por asignatura
- Solicitud y aprobación de tutorías
- Gestión de horarios disponibles
- Historial de sesiones
- Calificación de tutorías (opcional)
- Reportes para administración (opcional)

---

## Tecnologías
- **Frontend:** HTML5, CSS3, Bootstrap, Thymeleaf
- **Backend:** Java, Spring Boot (arquitectura MVC)
- **Base de Datos:** MySQL + Hibernate/JPA
- **Autenticacion:** Spring Security (Login y roles)
- **Control de versiones:** Git +GitHub

---

### Pasos para levantar el proyecto
 
1. **Configurar la conexión** en `src/main/resources/application.properties` según tu instalación local de MySQL (usuario y contraseña):
```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/tutorconnect
   spring.datasource.username=root
   spring.datasource.password=1234
```
   Las tablas se crean y actualizan automáticamente al iniciar la aplicación (`spring.jpa.hibernate.ddl-auto=update`), no es necesario correr scripts SQL manuales.
 
2. **Ejecutar la aplicación** desde el IDE (clase `TutorconnectApplication`) o por línea de comandos:
```bash
   ./mvnw spring-boot:run
```
 
3. **Acceder desde el navegador.** La app corre en el puerto `80`, así que la URL base es:
```
   http://localhost/
```
   Rutas disponibles actualmente:
   - `http://localhost/usuarios` — listado de usuarios
   - `http://localhost/tutores` — listado, alta y edición de tutores
   - `http://localhost/estudiantes` — módulo de estudiantes
   - `http://localhost/asignaturas` — módulo de asignaturas (ver nota en Pendientes)
---
 
## Estado del avance
 
**Entrega 2 en progreso.** Módulos implementados hasta el momento, cada uno con arquitectura en capas (`domain` → `repository` → `service` → `controller` → vistas Thymeleaf con Bootstrap):
 
| Módulo | Responsable | Estado |
|---|---|---|
| Usuario | Jorge Viquez Espinoza | Creó la base de datos en MySQL Workbench, el módulo de Usuario con su listado, la conexión con MySQL y la integración de Bootstrap. |
| Asignatura | Henry Gerardo Izaba Díaz | CRUD en backend completo. Vistas con desajuste de nombres/rutas pendiente de corregir (ver Pendientes). |
| Tutor | Juan Carlos Vega Esquivel | CRUD completo: alta, edición, listado y eliminación, con selección de asignaturas que imparte (relación muchos a muchos) y actualizacion del READ.ME |
| Estudiante | Gabriel Martinez Quesada | CRUD implementado. |
 
---
 
## Flujo de trabajo colaborativo
 
El equipo trabaja con ramas por funcionalidad (`feature/nombre-del-modulo`) y Pull Requests hacia `main`, revisados antes de aceptarse.

