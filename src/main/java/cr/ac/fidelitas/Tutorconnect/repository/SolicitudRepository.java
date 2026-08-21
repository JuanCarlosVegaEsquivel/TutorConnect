package cr.ac.fidelitas.Tutorconnect.repository;

import cr.ac.fidelitas.Tutorconnect.domain.EstadoSolicitud;
import cr.ac.fidelitas.Tutorconnect.domain.Solicitud;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author (tu nombre aquí)
 */
@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findByEstudianteIdUsuario(Long idUsuario);

    List<Solicitud> findByTutorIdUsuario(Long idUsuario);

    List<Solicitud> findByEstado(EstadoSolicitud estado);

}
