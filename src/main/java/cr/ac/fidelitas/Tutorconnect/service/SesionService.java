package cr.ac.fidelitas.Tutorconnect.service;

import cr.ac.fidelitas.Tutorconnect.domain.EstadoSesion;
import cr.ac.fidelitas.Tutorconnect.domain.EstadoSolicitud;
import cr.ac.fidelitas.Tutorconnect.domain.Sesion;
import cr.ac.fidelitas.Tutorconnect.domain.Solicitud;
import cr.ac.fidelitas.Tutorconnect.repository.SesionRepository;
import cr.ac.fidelitas.Tutorconnect.repository.SolicitudRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SesionService {

    private final SesionRepository sesionRepository;
    private final SolicitudRepository solicitudRepository;

    @Autowired
    public SesionService(SesionRepository sesionRepository, SolicitudRepository solicitudRepository) {
        this.sesionRepository = sesionRepository;
        this.solicitudRepository = solicitudRepository;
    }

    @Transactional(readOnly = true)
    public List<Sesion> listar() {
        return sesionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Sesion> listarPorTutor(Long idTutor) {
        return sesionRepository.findBySolicitudTutorIdUsuario(idTutor);
    }

    @Transactional(readOnly = true)
    public List<Sesion> listarPorEstudiante(Long idEstudiante) {
        return sesionRepository.findBySolicitudEstudianteIdUsuario(idEstudiante);
    }

    @Transactional(readOnly = true)
    public Optional<Sesion> buscarPorId(Long id) {
        return sesionRepository.findById(id);
    }

    /**
     * Solicitudes APROBADAS que todavía no tienen una sesión creada.
     * Si idTutor viene null, se buscan de todos los tutores (uso admin);
     * si viene con valor, solo las de ese tutor.
     */
    @Transactional(readOnly = true)
    public List<Solicitud> solicitudesDisponiblesParaSesion(Long idTutor) {
        List<Solicitud> aprobadas = (idTutor != null)
                ? solicitudRepository.findByTutorIdUsuario(idTutor).stream()
                        .filter(s -> s.getEstado() == EstadoSolicitud.APROBADA)
                        .collect(Collectors.toList())
                : solicitudRepository.findByEstado(EstadoSolicitud.APROBADA);

        return aprobadas.stream()
                .filter(s -> sesionRepository.findBySolicitudIdSolicitud(s.getIdSolicitud()).isEmpty())
                .collect(Collectors.toList());
    }

    @Transactional
    public Sesion guardar(Long idSolicitud, LocalDateTime fechaHora, Integer duracionMinutos, String observaciones) {

        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + idSolicitud));

        // Antes esto se sobrescribía en silencio. Ahora se rechaza con un
        // mensaje claro: una solicitud solo puede tener UNA sesión.
        if (sesionRepository.findBySolicitudIdSolicitud(idSolicitud).isPresent()) {
            throw new RuntimeException("Esta solicitud ya tiene una sesión programada.");
        }

        Sesion sesion = new Sesion();
        sesion.setSolicitud(solicitud);
        sesion.setFechaHora(fechaHora);
        sesion.setDuracionMinutos(duracionMinutos);
        sesion.setObservaciones(observaciones);
        sesion.setEstado(EstadoSesion.PROGRAMADA);

        return sesionRepository.save(sesion);
    }

    @Transactional
    public void marcarRealizada(Long id) {
        cambiarEstado(id, EstadoSesion.REALIZADA);
    }

    @Transactional
    public void cancelar(Long id) {
        cambiarEstado(id, EstadoSesion.CANCELADA);
    }

    private void cambiarEstado(Long id, EstadoSesion estado) {
        Sesion sesion = sesionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada con id: " + id));
        sesion.setEstado(estado);
        sesionRepository.save(sesion);
    }

    @Transactional
    public void eliminar(Long id) {
        sesionRepository.deleteById(id);
    }
}