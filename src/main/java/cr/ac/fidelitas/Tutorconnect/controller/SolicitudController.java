package cr.ac.fidelitas.Tutorconnect.controller;

import cr.ac.fidelitas.Tutorconnect.domain.Rol;
import cr.ac.fidelitas.Tutorconnect.domain.Usuario;
import cr.ac.fidelitas.Tutorconnect.repository.UsuarioRepository;
import cr.ac.fidelitas.Tutorconnect.service.AsignaturaService;
import cr.ac.fidelitas.Tutorconnect.service.EstudianteService;
import cr.ac.fidelitas.Tutorconnect.service.HorarioService;
import cr.ac.fidelitas.Tutorconnect.service.SolicitudService;
import cr.ac.fidelitas.Tutorconnect.service.TutorService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final EstudianteService estudianteService;
    private final TutorService tutorService;
    private final AsignaturaService asignaturaService;
    private final HorarioService horarioService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public SolicitudController(SolicitudService solicitudService,
                                EstudianteService estudianteService,
                                TutorService tutorService,
                                AsignaturaService asignaturaService,
                                HorarioService horarioService,
                                UsuarioRepository usuarioRepository) {
        this.solicitudService = solicitudService;
        this.estudianteService = estudianteService;
        this.tutorService = tutorService;
        this.asignaturaService = asignaturaService;
        this.horarioService = horarioService;
        this.usuarioRepository = usuarioRepository;
    }

    // Listado
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("solicitudes", solicitudService.listar());
        return "solicitud/listado";
    }

    // Formulario para nueva solicitud
    @GetMapping("/nuevo")
    public String nuevo(Model model, Principal principal) {
        cargarCombos(model);

        Usuario usuarioActual = usuarioRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean esAdmin = usuarioActual.getRol() == Rol.ADMIN;
        model.addAttribute("esAdmin", esAdmin);

        //Si NO es admin, el estudiante queda fijo: es el propio usuario logueado.
        //No se le muestra el desplegable con todos los estudiantes.
        if (!esAdmin) {
            model.addAttribute("idEstudianteActual", usuarioActual.getIdUsuario());
            model.addAttribute("nombreEstudianteActual", usuarioActual.getNombre());
        }

        return "solicitud/formulario";
    }

    //Guardar
    @PostMapping("/guardar")
    public String guardar(@RequestParam(required = false) Long idEstudiante,
                           @RequestParam Long idTutor,
                           @RequestParam Long idAsignatura,
                           @RequestParam Long idHorario,
                           @RequestParam(required = false) String comentario,
                           Model model,
                           Principal principal) {

        Usuario usuarioActual = usuarioRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        //Si el usuario logueado NO es admin, se ignora cualquier idEstudiante
        // que venga del formulario (por seguridad, no confiamos en el cliente)
        // y se fuerza a que sea su propio id.
        Long idEstudianteFinal = (usuarioActual.getRol() == Rol.ADMIN)
                ? idEstudiante
                : usuarioActual.getIdUsuario();

        try {
            solicitudService.guardar(idEstudianteFinal, idTutor, idAsignatura, idHorario, comentario);
            return "redirect:/solicitudes";
        } catch (RuntimeException e) {
            cargarCombos(model);
            model.addAttribute("error", e.getMessage());
            return "solicitud/formulario";
        }
    }

    //Aprobar
    @GetMapping("/aprobar/{id}")
    public String aprobar(@PathVariable Long id) {
        solicitudService.aprobar(id);
        return "redirect:/solicitudes";
    }

    //Rechazar
    @GetMapping("/rechazar/{id}")
    public String rechazar(@PathVariable Long id) {
        solicitudService.rechazar(id);
        return "redirect:/solicitudes";
    }

    //Cancelar
    @GetMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Long id) {
        solicitudService.cancelar(id);
        return "redirect:/solicitudes";
    }

    //Eliminar
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        solicitudService.eliminar(id);
        return "redirect:/solicitudes";
    }

    private void cargarCombos(Model model) {
        model.addAttribute("estudiantes", estudianteService.listarActivos());
        model.addAttribute("tutores", tutorService.listarActivos());
        model.addAttribute("asignaturas", asignaturaService.listarActivas());
        model.addAttribute("horarios", horarioService.listar());
    }

}