package cr.ac.fidelitas.Tutorconnect.service;

import cr.ac.fidelitas.Tutorconnect.domain.Asignatura;
import cr.ac.fidelitas.Tutorconnect.domain.Estudiante;
import cr.ac.fidelitas.Tutorconnect.domain.Usuario;
import cr.ac.fidelitas.Tutorconnect.repository.AsignaturaRepository;
import cr.ac.fidelitas.Tutorconnect.repository.EstudianteRepository;
import cr.ac.fidelitas.Tutorconnect.repository.UsuarioRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author (tu nombre aquí)
 */

@Service
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsignaturaRepository asignaturaRepository;

    @Autowired
    public EstudianteService(EstudianteRepository estudianteRepository,
                              UsuarioRepository usuarioRepository,
                              AsignaturaRepository asignaturaRepository) {
        this.estudianteRepository = estudianteRepository;
        this.usuarioRepository = usuarioRepository;
        this.asignaturaRepository = asignaturaRepository;
    }

    @Transactional(readOnly = true)
    public List<Estudiante> listar() {
        return estudianteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Estudiante> listarActivos() {
        return estudianteRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Estudiante> buscarPorId(Long idUsuario) {
        return estudianteRepository.findById(idUsuario);
    }

    @Transactional
    public Estudiante guardarConAsignaturas(Long idUsuario, String carrera, String telefono,
                                             boolean activo, List<Long> asignaturaIds) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUsuario));

        Estudiante estudiante = estudianteRepository.findById(idUsuario).orElseGet(Estudiante::new);
        estudiante.setUsuario(usuario);
        estudiante.setCarrera(carrera);
        estudiante.setTelefono(telefono);
        estudiante.setActivo(activo);

        Set<Asignatura> asignaturas = new HashSet<>();
        if (asignaturaIds != null && !asignaturaIds.isEmpty()) {
            asignaturas.addAll(asignaturaRepository.findAllById(asignaturaIds));
        }
        estudiante.setAsignaturasInteres(asignaturas);

        return estudianteRepository.save(estudiante);
    }

    @Transactional
    public void eliminar(Long idUsuario) {
        estudianteRepository.deleteById(idUsuario);
    }

}
