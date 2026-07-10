package cr.ac.fidelitas.Tutorconnect.controller;

import cr.ac.fidelitas.Tutorconnect.domain.Asignatura;
import cr.ac.fidelitas.Tutorconnect.service.AsignaturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/asignaturas")
public class AsignaturaController {

    private final AsignaturaService asignaturaService;

    @Autowired
    public AsignaturaController(AsignaturaService asignaturaService) {
        this.asignaturaService = asignaturaService;
    }

    // Listado
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("asignaturas", asignaturaService.listar());
        return "asignatura/listar";
    }

    // Formulario para nueva asignatura
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("asignatura", new Asignatura());
        return "asignatura/formulario";
    }

    // Formulario para editar
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Asignatura asignatura = asignaturaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Asignatura no encontrada con id: " + id));
        model.addAttribute("asignatura", asignatura);
        return "asignatura/formulario";
    }

    // Guardar (nueva o edición)
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Asignatura asignatura) {
        if (asignatura.getIdAsignatura() != null) {
            asignaturaService.actualizar(asignatura.getIdAsignatura(), asignatura);
        } else {
            asignaturaService.guardar(asignatura);
        }
        return "redirect:/asignaturas";
    }

    // Eliminar
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        asignaturaService.eliminar(id);
        return "redirect:/asignaturas";
    }
}