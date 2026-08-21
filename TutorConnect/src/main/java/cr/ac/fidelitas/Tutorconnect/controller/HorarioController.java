package cr.ac.fidelitas.Tutorconnect.controller;

import cr.ac.fidelitas.Tutorconnect.domain.Horario;
import cr.ac.fidelitas.Tutorconnect.service.HorarioService;
import cr.ac.fidelitas.Tutorconnect.service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/horarios")
public class HorarioController {

    private final HorarioService horarioService;
    private final TutorService tutorService;

    @Autowired
    public HorarioController(HorarioService horarioService,
                             TutorService tutorService) {
        this.horarioService = horarioService;
        this.tutorService = tutorService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("horarios", horarioService.listar());
        return "horario/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("horario", new Horario());
        model.addAttribute("tutores", tutorService.listarActivos());
        return "horario/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Horario horario = horarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException(
                        "Horario no encontrado con id: " + id));

        model.addAttribute("horario", horario);
        model.addAttribute("tutores", tutorService.listarActivos());

        return "horario/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Horario horario, Model model) {

        try {

            if (horario.getIdHorario() != null) {
                horarioService.actualizar(horario.getIdHorario(), horario);
            } else {
                horarioService.guardar(horario);
            }

            return "redirect:/horarios";

        } catch (RuntimeException e) {

            model.addAttribute("horario", horario);
            model.addAttribute("tutores", tutorService.listarActivos());
            model.addAttribute("error", e.getMessage());

            return "horario/formulario";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        horarioService.eliminar(id);
        return "redirect:/horarios";
    }
}