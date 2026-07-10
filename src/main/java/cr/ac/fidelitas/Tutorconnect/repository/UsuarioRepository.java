package cr.ac.fidelitas.Tutorconnect.repository;

import cr.ac.fidelitas.Tutorconnect.domain.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    public List<Usuario> findByActivoTrue();

}