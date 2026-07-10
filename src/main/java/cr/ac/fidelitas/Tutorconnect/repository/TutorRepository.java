package cr.ac.fidelitas.Tutorconnect.repository;

import cr.ac.fidelitas.Tutorconnect.domain.Tutor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
 * @author Vector
 */

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {

    List<Tutor> findByActivoTrue();

}
