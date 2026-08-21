package cr.ac.fidelitas.Tutorconnect.repository;

import cr.ac.fidelitas.Tutorconnect.domain.Horario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {

    List<Horario> findByTutorIdUsuario(Long idUsuario);

    List<Horario> findByTutorIdUsuarioAndDia(Long idUsuario, String dia);

}