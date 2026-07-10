package cr.ac.fidelitas.Tutorconnect.controller;

import cr.ac.fidelitas.Tutorconnect.domain.Estudiante;
import cr.ac.fidelitas.Tutorconnect.service.AsignaturaService;
import cr.ac.fidelitas.Tutorconnect.service.EstudianteService;
import cr.ac.fidelitas.Tutorconnect.service.UsuarioService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * @author (tu nombre aquí)
 */

@Controller
@RequestMapping("/estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final UsuarioService usuarioService;
    private final AsignaturaService asignaturaService;

    @Autowired
    public EstudianteController(EstudianteService estudianteService,
                                 UsuarioService usuarioService,
                                 AsignaturaService asignaturaService) {
        this.estudianteService = estudianteService;
        this.usuarioService = usuarioService;
        this.asignaturaService = asignaturaService;
    }

    // Listado
    @GetMapping
    public String listar(Model model) {
        var estudiantes = estudianteService.listar();
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("totalEstudiantes", estudiantes.size());
        return "estudiante/listado";
    }

    // Formulario para nuevo estudiante
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("estudiante", new Estudiante());
        model.addAttribute("usuarios", usuarioService.getUsuarios(false));
        model.addAttribute("asignaturas", asignaturaService.listarActivas());
        model.addAttribute("asignaturasSeleccionadas", new ArrayList<Long>());
        return "estudiante/formulario";
    }

    // Formulario para editar
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Estudiante estudiante = estudianteService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + id));

        List<Long> seleccionadas = estudiante.getAsignaturasInteres().stream()
                .map(a -> a.getIdAsignatura())
                .collect(Collectors.toList());

        model.addAttribute("estudiante", estudiante);
        model.addAttribute("usuarios", usuarioService.getUsuarios(false));
        model.addAttribute("asignaturas", asignaturaService.listarActivas());
        model.addAttribute("asignaturasSeleccionadas", seleccionadas);
        return "estudiante/formulario";
    }

    // Guardar (nuevo o edición)
    @PostMapping("/guardar")
    public String guardar(@RequestParam Long idUsuario,
                           @RequestParam(required = false) String carrera,
                           @RequestParam(required = false) String telefono,
                           @RequestParam(required = false) Boolean activo,
                           @RequestParam(required = false) List<Long> asignaturaIds) {
        estudianteService.guardarConAsignaturas(idUsuario, carrera, telefono, activo != null, asignaturaIds);
        return "redirect:/estudiantes";
    }

    // Eliminar
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        estudianteService.eliminar(id);
        return "redirect:/estudiantes";
    }

}
