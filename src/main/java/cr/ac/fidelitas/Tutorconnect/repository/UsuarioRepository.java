package cr.ac.fidelitas.Tutorconnect.repository;

import cr.ac.fidelitas.Tutorconnect.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    public List<Usuario> findByActivoTrue();

//Necesario para el login: Spring Security busca al usuario por su correo.
    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
}