package cr.ac.fidelitas.Tutorconnect.controller;

import cr.ac.fidelitas.Tutorconnect.domain.EstadoSesion;
import cr.ac.fidelitas.Tutorconnect.domain.Sesion;
import cr.ac.fidelitas.Tutorconnect.service.CalificacionService;
import cr.ac.fidelitas.Tutorconnect.service.SesionService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author (tu nombre aquí)
 */
@Controller
@RequestMapping("/calificaciones")
public class CalificacionController {

    private final CalificacionService calificacionService;
    private final SesionService sesionService;

    @Autowired
    public CalificacionController(CalificacionService calificacionService, SesionService sesionService) {
        this.calificacionService = calificacionService;
        this.sesionService = sesionService;
    }

    // Listado
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("calificaciones", calificacionService.listar());
        return "calificacion/listado";
    }

    // Formulario para nueva calificación (solo a partir de sesiones realizadas)
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        List<Sesion> sesionesRealizadas = sesionService.listar().stream()
                .filter(s -> s.getEstado() == EstadoSesion.REALIZADA)
                .collect(Collectors.toList());
        model.addAttribute("sesionesRealizadas", sesionesRealizadas);
        return "calificacion/formulario";
    }

    // Guardar
    @PostMapping("/guardar")
    public String guardar(@RequestParam Long idSesion,
                           @RequestParam Integer puntuacion,
                           @RequestParam(required = false) String comentario) {
        calificacionService.guardar(idSesion, puntuacion, comentario);
        return "redirect:/calificaciones";
    }

    // Eliminar
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        calificacionService.eliminar(id);
        return "redirect:/calificaciones";
    }

}
