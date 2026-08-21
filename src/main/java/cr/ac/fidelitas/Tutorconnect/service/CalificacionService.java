package cr.ac.fidelitas.Tutorconnect.service;

import cr.ac.fidelitas.Tutorconnect.domain.Calificacion;
import cr.ac.fidelitas.Tutorconnect.domain.Sesion;
import cr.ac.fidelitas.Tutorconnect.repository.CalificacionRepository;
import cr.ac.fidelitas.Tutorconnect.repository.SesionRepository;
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
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final SesionRepository sesionRepository;

    @Autowired
    public CalificacionService(CalificacionRepository calificacionRepository, SesionRepository sesionRepository) {
        this.calificacionRepository = calificacionRepository;
        this.sesionRepository = sesionRepository;
    }

    @Transactional(readOnly = true)
    public List<Calificacion> listar() {
        return calificacionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Calificacion> buscarPorId(Long id) {
        return calificacionRepository.findById(id);
    }

    @Transactional
    public Calificacion guardar(Long idSesion, Integer puntuacion, String comentario) {

        if (puntuacion == null || puntuacion < 1 || puntuacion > 5) {
            throw new RuntimeException("La puntuación debe estar entre 1 y 5.");
        }

        Sesion sesion = sesionRepository.findById(idSesion)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada con id: " + idSesion));

        Calificacion calificacion = calificacionRepository.findBySesionIdSesion(idSesion)
                .orElseGet(Calificacion::new);
        calificacion.setSesion(sesion);
        calificacion.setPuntuacion(puntuacion);
        calificacion.setComentario(comentario);

        return calificacionRepository.save(calificacion);
    }

    @Transactional
    public void eliminar(Long id) {
        calificacionRepository.deleteById(id);
    }

}
