package cr.ac.fidelitas.Tutorconnect.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


/**
 *
 * @author Vector
 */
@Configuration
public class SecurityConfig {

    //BCrypt: nunca se guardan contraseñas en texto plano.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                //Rutas públicas: no requieren sesión iniciada.
                .requestMatchers("/", "/login", "/registro/**",
                                 "/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()
                //Búsqueda/consulta de tutores: cualquier usuario logueado
                //(estudiante, tutor o admin) puede buscar y ver el listado.
                //OJO: coincide solo con "/tutores" exacto (no "/tutores/nuevo",
                //"/tutores/editar/**", etc.), esas siguen protegidas más abajo.
                .requestMatchers(HttpMethod.GET, "/tutores").authenticated()
                //Gestión administrativa (crear/editar/eliminar/listar usuarios,
                //asignaturas y estudiantes): solo ADMIN.
                .requestMatchers("/usuario/**", "/asignaturas/**",
                                 "/tutores/**", "/estudiantes/**").hasRole("ADMIN")
                //Horarios y sesiones: solo el tutor (dueño de su agenda) o el admin
                //pueden crear, editar, eliminar o marcar sesiones como realizadas/canceladas.
                .requestMatchers("/horarios/**", "/sesiones/**").hasAnyRole("TUTOR", "ADMIN")
                //Calificar una sesión es acción del estudiante (evalúa al tutor);
                //eliminar calificaciones queda como acción administrativa.
                .requestMatchers("/calificaciones/nuevo", "/calificaciones/guardar")
                    .hasAnyRole("ESTUDIANTE", "ADMIN")
                .requestMatchers("/calificaciones/eliminar/**").hasRole("ADMIN")
                //Solicitar una tutoría es acción del estudiante; aprobar/rechazar
                //es acción del tutor; cancelar la propia solicitud, del estudiante;
                //eliminar el registro, acción administrativa.
                .requestMatchers("/solicitudes/nuevo", "/solicitudes/guardar",
                                 "/solicitudes/cancelar/**").hasAnyRole("ESTUDIANTE", "ADMIN")
                .requestMatchers("/solicitudes/aprobar/**", "/solicitudes/rechazar/**")
                    .hasAnyRole("TUTOR", "ADMIN")
                .requestMatchers("/solicitudes/eliminar/**").hasRole("ADMIN")
                //Cualquier otra ruta exige estar logueado.
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("correo")
                .passwordParameter("contrasena")
                .defaultSuccessUrl("/inicio", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}