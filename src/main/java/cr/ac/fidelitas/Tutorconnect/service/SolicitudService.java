package cr.ac.fidelitas.Tutorconnect.service;

import cr.ac.fidelitas.Tutorconnect.domain.Asignatura;
import cr.ac.fidelitas.Tutorconnect.domain.EstadoSolicitud;
import cr.ac.fidelitas.Tutorconnect.domain.Estudiante;
import cr.ac.fidelitas.Tutorconnect.domain.Horario;
import cr.ac.fidelitas.Tutorconnect.domain.Solicitud;
import cr.ac.fidelitas.Tutorconnect.domain.Tutor;
import cr.ac.fidelitas.Tutorconnect.repository.AsignaturaRepository;
import cr.ac.fidelitas.Tutorconnect.repository.EstudianteRepository;
import cr.ac.fidelitas.Tutorconnect.repository.HorarioRepository;
import cr.ac.fidelitas.Tutorconnect.repository.SolicitudRepository;
import cr.ac.fidelitas.Tutorconnect.repository.TutorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author (tu nombre aquí)
 */
@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final EstudianteRepository estudianteRepository;
    private final TutorRepository tutorRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final HorarioRepository horarioRepository;

    @Autowired
    public SolicitudService(SolicitudRepository solicitudRepository,
                             EstudianteRepository estudianteRepository,
                             TutorRepository tutorRepository,
                             AsignaturaRepository asignaturaRepository,
                             HorarioRepository horarioRepository) {
        this.solicitudRepository = solicitudRepository;
        this.estudianteRepository = estudianteRepository;
        this.tutorRepository = tutorRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.horarioRepository = horarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Solicitud> listar() {
        return solicitudRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Solicitud> listarPorEstado(EstadoSolicitud estado) {
        return solicitudRepository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    public Optional<Solicitud> buscarPorId(Long id) {
        return solicitudRepository.findById(id);
    }

    @Transactional
    public Solicitud guardar(Long idEstudiante, Long idTutor, Long idAsignatura,
                              Long idHorario, String comentario) {

        Estudiante estudiante = estudianteRepository.findById(idEstudiante)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + idEstudiante));
        Tutor tutor = tutorRepository.findById(idTutor)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado con id: " + idTutor));
        Asignatura asignatura = asignaturaRepository.findById(idAsignatura)
                .orElseThrow(() -> new RuntimeException("Asignatura no encontrada con id: " + idAsignatura));
        Horario horario = horarioRepository.findById(idHorario)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con id: " + idHorario));

        if (!horario.getTutor().getIdUsuario().equals(tutor.getIdUsuario())) {
            throw new RuntimeException("El horario seleccionado no pertenece al tutor elegido.");
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setEstudiante(estudiante);
        solicitud.setTutor(tutor);
        solicitud.setAsignatura(asignatura);
        solicitud.setHorario(horario);
        solicitud.setComentario(comentario);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);

        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public void aprobar(Long id) {
        cambiarEstado(id, EstadoSolicitud.APROBADA);
    }

    @Transactional
    public void rechazar(Long id) {
        cambiarEstado(id, EstadoSolicitud.RECHAZADA);
    }

    @Transactional
    public void cancelar(Long id) {
        cambiarEstado(id, EstadoSolicitud.CANCELADA);
    }

    private void cambiarEstado(Long id, EstadoSolicitud estado) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + id));
        solicitud.setEstado(estado);
        solicitudRepository.save(solicitud);
    }

    @Transactional
    public void eliminar(Long id) {
        solicitudRepository.deleteById(id);
    }

}
