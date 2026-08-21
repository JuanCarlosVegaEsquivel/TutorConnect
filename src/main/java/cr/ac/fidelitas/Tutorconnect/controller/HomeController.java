package cr.ac.fidelitas.Tutorconnect.controller;

import cr.ac.fidelitas.Tutorconnect.domain.Rol;
import cr.ac.fidelitas.Tutorconnect.domain.Usuario;
import cr.ac.fidelitas.Tutorconnect.repository.UsuarioRepository;
import cr.ac.fidelitas.Tutorconnect.service.AsignaturaService;
import cr.ac.fidelitas.Tutorconnect.service.EstudianteService;
import cr.ac.fidelitas.Tutorconnect.service.HorarioService;
import cr.ac.fidelitas.Tutorconnect.service.TutorService;
import java.security.Principal;
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
    private final TutorService tutorService;
    private final EstudianteService estudianteService;
    private final AsignaturaService asignaturaService;
    private final HorarioService horarioService;

    public HomeController(UsuarioRepository usuarioRepository,
                          TutorService tutorService,
                          EstudianteService estudianteService,
                          AsignaturaService asignaturaService,
                          HorarioService horarioService) {
        this.usuarioRepository = usuarioRepository;
        this.tutorService = tutorService;
        this.estudianteService = estudianteService;
        this.asignaturaService = asignaturaService;
        this.horarioService = horarioService;
    }

    @GetMapping("/")
    public String index() {
        return "home/index";
    }

    @GetMapping("/inicio")
    public String inicio(Model model, Principal principal) {
        Usuario usuario = usuarioRepository.findByCorreo(principal.getName()).orElse(null);
        model.addAttribute("usuario", usuario);

        // Estadísticas rápidas, solo se calculan y se muestran si es ADMIN.
        if (usuario != null && usuario.getRol() == Rol.ADMIN) {
            model.addAttribute("totalUsuarios", usuarioRepository.count());
            model.addAttribute("totalTutores", tutorService.listarActivos().size());
            model.addAttribute("totalEstudiantes", estudianteService.listarActivos().size());
            model.addAttribute("totalAsignaturas", asignaturaService.listarActivas().size());
            model.addAttribute("totalHorarios", horarioService.listar().size());
        }

        return "home/inicio";
    }
}