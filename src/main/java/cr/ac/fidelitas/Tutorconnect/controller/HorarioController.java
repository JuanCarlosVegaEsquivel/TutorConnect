package cr.ac.fidelitas.Tutorconnect.controller;

import cr.ac.fidelitas.Tutorconnect.domain.Horario;
import cr.ac.fidelitas.Tutorconnect.domain.Rol;
import cr.ac.fidelitas.Tutorconnect.domain.Tutor;
import cr.ac.fidelitas.Tutorconnect.domain.Usuario;
import cr.ac.fidelitas.Tutorconnect.repository.UsuarioRepository;
import cr.ac.fidelitas.Tutorconnect.service.HorarioService;
import cr.ac.fidelitas.Tutorconnect.service.TutorService;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Vector
 */
@Controller
@RequestMapping("/horarios")
public class HorarioController {

    private final HorarioService horarioService;
    private final TutorService tutorService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public HorarioController(HorarioService horarioService,
                             TutorService tutorService,
                             UsuarioRepository usuarioRepository) {
        this.horarioService = horarioService;
        this.tutorService = tutorService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String listar(Model model, Principal principal) {
        Usuario usuarioActual = usuarioActual(principal);

        // ADMIN ve todos los horarios; TUTOR solo ve los suyos.
        List<Horario> horarios = (usuarioActual.getRol() == Rol.ADMIN)
                ? horarioService.listar()
                : horarioService.listarPorTutor(usuarioActual.getIdUsuario());

        model.addAttribute("horarios", horarios);
        return "horario/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, Principal principal) {
        Usuario usuarioActual = usuarioActual(principal);
        model.addAttribute("horario", new Horario());
        cargarFormulario(model, usuarioActual);
        return "horario/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, Principal principal) {
        Usuario usuarioActual = usuarioActual(principal);
        Horario horario = horarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con id: " + id));

        verificarPropietario(horario, usuarioActual);

        model.addAttribute("horario", horario);
        cargarFormulario(model, usuarioActual);
        return "horario/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Horario horario, Model model, Principal principal) {

        Usuario usuarioActual = usuarioActual(principal);

        try {
            //Si NO es admin, el tutor del horario siempre es el propio usuario
            if (usuarioActual.getRol() != Rol.ADMIN) {
                Tutor tutorPropio = tutorService.buscarPorId(usuarioActual.getIdUsuario())
                        .orElseThrow(() -> new RuntimeException("Perfil de tutor no encontrado."));
                horario.setTutor(tutorPropio);

                if (horario.getIdHorario() != null) {
                    Horario existente = horarioService.buscarPorId(horario.getIdHorario())
                            .orElseThrow(() -> new RuntimeException("Horario no encontrado."));
                    verificarPropietario(existente, usuarioActual);
                }
            }

            if (horario.getIdHorario() != null) {
                horarioService.actualizar(horario.getIdHorario(), horario);
            } else {
                horarioService.guardar(horario);
            }

            return "redirect:/horarios";

        } catch (RuntimeException e) {
            model.addAttribute("horario", horario);
            cargarFormulario(model, usuarioActual);
            model.addAttribute("error", e.getMessage());
            return "horario/formulario";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Principal principal) {
        Usuario usuarioActual = usuarioActual(principal);
        Horario horario = horarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con id: " + id));

        verificarPropietario(horario, usuarioActual);

        horarioService.eliminar(id);
        return "redirect:/horarios";
    }

    private Usuario usuarioActual(Principal principal) {
        return usuarioRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private void verificarPropietario(Horario horario, Usuario usuarioActual) {
        if (usuarioActual.getRol() != Rol.ADMIN
                && !horario.getTutor().getIdUsuario().equals(usuarioActual.getIdUsuario())) {
            throw new RuntimeException("No tiene permiso para modificar el horario de otro tutor.");
        }
    }

    private void cargarFormulario(Model model, Usuario usuarioActual) {
        boolean esAdmin = usuarioActual.getRol() == Rol.ADMIN;
        model.addAttribute("esAdmin", esAdmin);
        if (esAdmin) {
            model.addAttribute("tutores", tutorService.listarActivos());
        } else {
            model.addAttribute("idTutorActual", usuarioActual.getIdUsuario());
            model.addAttribute("nombreTutorActual", usuarioActual.getNombre());
        }
    }
}