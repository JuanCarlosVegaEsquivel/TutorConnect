package cr.ac.fidelitas.Tutorconnect.repository;

import cr.ac.fidelitas.Tutorconnect.domain.Estudiante;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    List<Estudiante> findByActivoTrue();

}
