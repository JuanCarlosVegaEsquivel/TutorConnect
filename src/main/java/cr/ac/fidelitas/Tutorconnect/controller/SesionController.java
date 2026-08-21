package cr.ac.fidelitas.Tutorconnect.controller;

import cr.ac.fidelitas.Tutorconnect.domain.EstadoSolicitud;
import cr.ac.fidelitas.Tutorconnect.service.SesionService;
import cr.ac.fidelitas.Tutorconnect.service.SolicitudService;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author (tu nombre aquí)
 */
@Controller
@RequestMapping("/sesiones")
public class SesionController {

    private final SesionService sesionService;
    private final SolicitudService solicitudService;

    @Autowired
    public SesionController(SesionService sesionService, SolicitudService solicitudService) {
        this.sesionService = sesionService;
        this.solicitudService = solicitudService;
    }

    // Listado
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("sesiones", sesionService.listar());
        return "sesion/listado";
    }

    // Formulario para nueva sesión (solo a partir de solicitudes aprobadas)
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("solicitudesAprobadas", solicitudService.listarPorEstado(EstadoSolicitud.APROBADA));
        return "sesion/formulario";
    }

    // Guardar
    @PostMapping("/guardar")
    public String guardar(@RequestParam Long idSolicitud,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora,
                           @RequestParam(required = false) Integer duracionMinutos,
                           @RequestParam(required = false) String observaciones) {
        sesionService.guardar(idSolicitud, fechaHora, duracionMinutos, observaciones);
        return "redirect:/sesiones";
    }

    // Marcar como realizada
    @GetMapping("/realizada/{id}")
    public String marcarRealizada(@PathVariable Long id) {
        sesionService.marcarRealizada(id);
        return "redirect:/sesiones";
    }

    // Cancelar
    @GetMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Long id) {
        sesionService.cancelar(id);
        return "redirect:/sesiones";
    }

    // Eliminar
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        sesionService.eliminar(id);
        return "redirect:/sesiones";
    }

}
