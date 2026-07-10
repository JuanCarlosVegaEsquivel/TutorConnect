package cr.ac.fidelitas.Tutorconnect.controller;

import cr.ac.fidelitas.Tutorconnect.domain.Tutor;
import cr.ac.fidelitas.Tutorconnect.service.AsignaturaService;
import cr.ac.fidelitas.Tutorconnect.service.TutorService;
import cr.ac.fidelitas.Tutorconnect.service.UsuarioService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * @author Vector
 */

@Controller
@RequestMapping("/tutores")
public class TutorController {

    private final TutorService tutorService;
    private final UsuarioService usuarioService;
    private final AsignaturaService asignaturaService;

    @Autowired
    public TutorController(TutorService tutorService,
                            UsuarioService usuarioService,
                            AsignaturaService asignaturaService) {
        this.tutorService = tutorService;
        this.usuarioService = usuarioService;
        this.asignaturaService = asignaturaService;
    }

    @GetMapping
    public String listar(Model model) {
        var tutores = tutorService.listar();
        model.addAttribute("tutores", tutores);
        model.addAttribute("totalTutores", tutores.size());
        return "tutor/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("tutor", new Tutor());
        model.addAttribute("usuarios", usuarioService.getUsuarios(false));
        model.addAttribute("asignaturas", asignaturaService.listarActivas());
        model.addAttribute("asignaturasSeleccionadas", new ArrayList<Long>());
        return "tutor/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Tutor tutor = tutorService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado con id: " + id));

        List<Long> seleccionadas = tutor.getAsignaturas().stream()
                .map(a -> a.getIdAsignatura())
                .collect(Collectors.toList());

        model.addAttribute("tutor", tutor);
        model.addAttribute("usuarios", usuarioService.getUsuarios(false));
        model.addAttribute("asignaturas", asignaturaService.listarActivas());
        model.addAttribute("asignaturasSeleccionadas", seleccionadas);
        return "tutor/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Long idUsuario,
                           @RequestParam(required = false) String biografia,
                           @RequestParam(required = false) Boolean activo,
                           @RequestParam(required = false) List<Long> asignaturaIds) {
        tutorService.guardarConAsignaturas(idUsuario, biografia, activo != null, asignaturaIds);
        return "redirect:/tutores";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        tutorService.eliminar(id);
        return "redirect:/tutores";
    }

}
