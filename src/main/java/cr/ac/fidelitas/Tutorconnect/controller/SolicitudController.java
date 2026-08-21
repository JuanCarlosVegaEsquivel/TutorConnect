package cr.ac.fidelitas.Tutorconnect.controller;

import cr.ac.fidelitas.Tutorconnect.service.AsignaturaService;
import cr.ac.fidelitas.Tutorconnect.service.EstudianteService;
import cr.ac.fidelitas.Tutorconnect.service.HorarioService;
import cr.ac.fidelitas.Tutorconnect.service.SolicitudService;
import cr.ac.fidelitas.Tutorconnect.service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author (tu nombre aquí)
 */
@Controller
@RequestMapping("/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final EstudianteService estudianteService;
    private final TutorService tutorService;
    private final AsignaturaService asignaturaService;
    private final HorarioService horarioService;

    @Autowired
    public SolicitudController(SolicitudService solicitudService,
                                EstudianteService estudianteService,
                                TutorService tutorService,
                                AsignaturaService asignaturaService,
                                HorarioService horarioService) {
        this.solicitudService = solicitudService;
        this.estudianteService = estudianteService;
        this.tutorService = tutorService;
        this.asignaturaService = asignaturaService;
        this.horarioService = horarioService;
    }

    // Listado
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("solicitudes", solicitudService.listar());
        return "solicitud/listado";
    }

    // Formulario para nueva solicitud
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        cargarCombos(model);
        return "solicitud/formulario";
    }

    // Guardar
    @PostMapping("/guardar")
    public String guardar(@RequestParam Long idEstudiante,
                           @RequestParam Long idTutor,
                           @RequestParam Long idAsignatura,
                           @RequestParam Long idHorario,
                           @RequestParam(required = false) String comentario,
                           Model model) {
        try {
            solicitudService.guardar(idEstudiante, idTutor, idAsignatura, idHorario, comentario);
            return "redirect:/solicitudes";
        } catch (RuntimeException e) {
            cargarCombos(model);
            model.addAttribute("error", e.getMessage());
            return "solicitud/formulario";
        }
    }

    // Aprobar
    @GetMapping("/aprobar/{id}")
    public String aprobar(@PathVariable Long id) {
        solicitudService.aprobar(id);
        return "redirect:/solicitudes";
    }

    // Rechazar
    @GetMapping("/rechazar/{id}")
    public String rechazar(@PathVariable Long id) {
        solicitudService.rechazar(id);
        return "redirect:/solicitudes";
    }

    // Cancelar
    @GetMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Long id) {
        solicitudService.cancelar(id);
        return "redirect:/solicitudes";
    }

    // Eliminar
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
