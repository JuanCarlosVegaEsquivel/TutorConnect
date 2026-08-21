package cr.ac.fidelitas.Tutorconnect.controller;

import cr.ac.fidelitas.Tutorconnect.domain.Rol;
import cr.ac.fidelitas.Tutorconnect.domain.Sesion;
import cr.ac.fidelitas.Tutorconnect.domain.Usuario;
import cr.ac.fidelitas.Tutorconnect.repository.UsuarioRepository;
import cr.ac.fidelitas.Tutorconnect.service.SesionService;
import cr.ac.fidelitas.Tutorconnect.service.SolicitudService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sesiones")
public class SesionController {

    private final SesionService sesionService;
    private final SolicitudService solicitudService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public SesionController(SesionService sesionService,
                            SolicitudService solicitudService,
                            UsuarioRepository usuarioRepository) {
        this.sesionService = sesionService;
        this.solicitudService = solicitudService;
        this.usuarioRepository = usuarioRepository;
    }

    //Listado
    @GetMapping
    public String listar(Model model, Principal principal) {
        Usuario usuarioActual = usuarioActual(principal);

        List<Sesion> sesiones = (usuarioActual.getRol() == Rol.ADMIN)
                ? sesionService.listar()
                : sesionService.listarPorTutor(usuarioActual.getIdUsuario());

        model.addAttribute("sesiones", sesiones);
        return "sesion/listado";
    }

    //Formulario para nueva sesión (solo a partir de solicitudes aprobadas SIN sesión aún)
    @GetMapping("/nuevo")
    public String nuevo(Model model, Principal principal) {
        Usuario usuarioActual = usuarioActual(principal);

        Long idTutorFiltro = (usuarioActual.getRol() == Rol.TUTOR) ? usuarioActual.getIdUsuario() : null;
        model.addAttribute("solicitudesAprobadas",
                sesionService.solicitudesDisponiblesParaSesion(idTutorFiltro));

        return "sesion/formulario";
    }

    //Guardar
    @PostMapping("/guardar")
    public String guardar(@RequestParam Long idSolicitud,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora,
                           @RequestParam(required = false) Integer duracionMinutos,
                           @RequestParam(required = false) String observaciones,
                           Model model,
                           Principal principal) {
        try {
            sesionService.guardar(idSolicitud, fechaHora, duracionMinutos, observaciones);
            return "redirect:/sesiones";
        } catch (RuntimeException e) {
            Usuario usuarioActual = usuarioActual(principal);
            Long idTutorFiltro = (usuarioActual.getRol() == Rol.TUTOR) ? usuarioActual.getIdUsuario() : null;
            model.addAttribute("solicitudesAprobadas",
                    sesionService.solicitudesDisponiblesParaSesion(idTutorFiltro));
            model.addAttribute("error", e.getMessage());
            return "sesion/formulario";
        }
    }

    //Marcar como realizada
    @GetMapping("/realizada/{id}")
    public String marcarRealizada(@PathVariable Long id) {
        sesionService.marcarRealizada(id);
        return "redirect:/sesiones";
    }

    //Cancelar
    @GetMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Long id) {
        sesionService.cancelar(id);
        return "redirect:/sesiones";
    }

    //Eliminar
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        sesionService.eliminar(id);
        return "redirect:/sesiones";
    }

    private Usuario usuarioActual(Principal principal) {
        return usuarioRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}