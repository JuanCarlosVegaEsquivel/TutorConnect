package cr.ac.fidelitas.Tutorconnect.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                //Gestión administrativa: solo ADMIN.
                .requestMatchers("/usuario/**", "/asignaturas/**",
                                 "/tutores/**", "/estudiantes/**").hasRole("ADMIN")
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
