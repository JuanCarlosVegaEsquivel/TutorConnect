package cr.ac.fidelitas.Tutorconnect.controller;

import cr.ac.fidelitas.Tutorconnect.domain.Estudiante;
import cr.ac.fidelitas.Tutorconnect.domain.Rol;
import cr.ac.fidelitas.Tutorconnect.domain.Tutor;
import cr.ac.fidelitas.Tutorconnect.domain.Usuario;
import cr.ac.fidelitas.Tutorconnect.repository.EstudianteRepository;
import cr.ac.fidelitas.Tutorconnect.repository.TutorRepository;
import cr.ac.fidelitas.Tutorconnect.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Vector
 */
@Controller
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final TutorRepository tutorRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository,
                          EstudianteRepository estudianteRepository,
                          TutorRepository tutorRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.estudianteRepository = estudianteRepository;
        this.tutorRepository = tutorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/registro")
    public String formularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@RequestParam String nombre,
                            @RequestParam String correo,
                            @RequestParam String contrasena,
                            @RequestParam Rol rol,
                            Model model,
                            RedirectAttributes redirect) {

        if (usuarioRepository.existsByCorreo(correo)) {
            model.addAttribute("error", "Ya existe una cuenta con ese correo.");
            model.addAttribute("usuario", new Usuario());
            return "auth/registro";
        }
        if (contrasena == null || contrasena.length() < 6) {
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
            model.addAttribute("usuario", new Usuario());
            return "auth/registro";
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setContrasena(passwordEncoder.encode(contrasena));
        //Nunca se permite registrarse como ADMIN desde el formulario público.
        Rol rolFinal = (rol == Rol.ADMIN) ? Rol.ESTUDIANTE : rol;
        usuario.setRol(rolFinal);
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        if (rolFinal == Rol.ESTUDIANTE) {
            Estudiante estudiante = new Estudiante();
            estudiante.setUsuario(usuario);
            estudiante.setActivo(true);
            estudianteRepository.save(estudiante);
        } else if (rolFinal == Rol.TUTOR) {
            Tutor tutor = new Tutor();
            tutor.setUsuario(usuario);
            tutor.setActivo(true);
            tutorRepository.save(tutor);
        }

        redirect.addFlashAttribute("exito", "Cuenta creada. Ya puede iniciar sesión.");
        return "redirect:/login";
    }
}