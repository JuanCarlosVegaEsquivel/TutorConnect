package cr.ac.fidelitas.Tutorconnect.controller;

import java.security.Principal;
import cr.ac.fidelitas.Tutorconnect.domain.Usuario;
import cr.ac.fidelitas.Tutorconnect.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author Vector
 */
@Controller
public class HomeController {

    private final UsuarioRepository usuarioRepository;

    public HomeController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String index() {
        return "home/index";
    }

    @GetMapping("/inicio")
    public String inicio(Model model, Principal principal) {
        Usuario usuario = usuarioRepository.findByCorreo(principal.getName()).orElse(null);
        model.addAttribute("usuario", usuario);
        return "home/inicio";
    }
}