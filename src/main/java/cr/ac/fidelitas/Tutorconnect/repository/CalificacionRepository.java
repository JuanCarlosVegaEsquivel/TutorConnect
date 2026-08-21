package cr.ac.fidelitas.Tutorconnect.repository;

import cr.ac.fidelitas.Tutorconnect.domain.Calificacion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author (tu nombre aquí)
 */
@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    Optional<Calificacion> findBySesionIdSesion(Long idSesion);

}
