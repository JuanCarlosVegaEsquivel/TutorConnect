package cr.ac.fidelitas.Tutorconnect.service;

import cr.ac.fidelitas.Tutorconnect.domain.EstadoSesion;
import cr.ac.fidelitas.Tutorconnect.domain.Sesion;
import cr.ac.fidelitas.Tutorconnect.domain.Solicitud;
import cr.ac.fidelitas.Tutorconnect.repository.SesionRepository;
import cr.ac.fidelitas.Tutorconnect.repository.SolicitudRepository;
import java.time.LocalDateTime;
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
    public Optional<Sesion> buscarPorId(Long id) {
        return sesionRepository.findById(id);
    }

    @Transactional
    public Sesion guardar(Long idSolicitud, LocalDateTime fechaHora, Integer duracionMinutos, String observaciones) {

        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + idSolicitud));

        Sesion sesion = sesionRepository.findBySolicitudIdSolicitud(idSolicitud).orElseGet(Sesion::new);
        sesion.setSolicitud(solicitud);
        sesion.setFechaHora(fechaHora);
        sesion.setDuracionMinutos(duracionMinutos);
        sesion.setObservaciones(observaciones);
        if (sesion.getEstado() == null) {
            sesion.setEstado(EstadoSesion.PROGRAMADA);
        }

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
