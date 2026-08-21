package cr.ac.fidelitas.Tutorconnect.controller;

import cr.ac.fidelitas.Tutorconnect.domain.Rol;
import cr.ac.fidelitas.Tutorconnect.domain.Usuario;
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
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
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
        usuario.setRol(rol == Rol.ADMIN ? Rol.ESTUDIANTE : rol);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        redirect.addFlashAttribute("exito", "Cuenta creada. Ya puede iniciar sesión.");
        return "redirect:/login";
    }
}
