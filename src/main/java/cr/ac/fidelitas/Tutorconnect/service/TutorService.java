package cr.ac.fidelitas.Tutorconnect.service;

import cr.ac.fidelitas.Tutorconnect.domain.Asignatura;
import cr.ac.fidelitas.Tutorconnect.domain.Tutor;
import cr.ac.fidelitas.Tutorconnect.domain.Usuario;
import cr.ac.fidelitas.Tutorconnect.repository.AsignaturaRepository;
import cr.ac.fidelitas.Tutorconnect.repository.TutorRepository;
import cr.ac.fidelitas.Tutorconnect.repository.UsuarioRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Vector
 */

@Service
public class TutorService {

    private final TutorRepository tutorRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsignaturaRepository asignaturaRepository;

    @Autowired
    public TutorService(TutorRepository tutorRepository,
                         UsuarioRepository usuarioRepository,
                         AsignaturaRepository asignaturaRepository) {
        this.tutorRepository = tutorRepository;
        this.usuarioRepository = usuarioRepository;
        this.asignaturaRepository = asignaturaRepository;
    }

    @Transactional(readOnly = true)
    public List<Tutor> listar() {
        return tutorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Tutor> listarActivos() {
        return tutorRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Tutor> buscarPorId(Long idUsuario) {
        return tutorRepository.findById(idUsuario);
    }

    @Transactional
    public Tutor guardarConAsignaturas(Long idUsuario, String biografia, boolean activo, List<Long> asignaturaIds) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + idUsuario));

        Tutor tutor = tutorRepository.findById(idUsuario).orElseGet(Tutor::new);
        tutor.setUsuario(usuario);
        tutor.setBiografia(biografia);
        tutor.setActivo(activo);

        Set<Asignatura> asignaturas = new HashSet<>();
        if (asignaturaIds != null && !asignaturaIds.isEmpty()) {
            asignaturas.addAll(asignaturaRepository.findAllById(asignaturaIds));
        }
        tutor.setAsignaturas(asignaturas);

        return tutorRepository.save(tutor);
    }

    @Transactional
    public void eliminar(Long idUsuario) {
        tutorRepository.deleteById(idUsuario);
    }

}
