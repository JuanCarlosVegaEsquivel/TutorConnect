package cr.ac.fidelitas.Tutorconnect.service;

import cr.ac.fidelitas.Tutorconnect.domain.Asignatura;
import cr.ac.fidelitas.Tutorconnect.repository.AsignaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AsignaturaService {

    private final AsignaturaRepository asignaturaRepository;

    @Autowired
    public AsignaturaService(AsignaturaRepository asignaturaRepository) {
        this.asignaturaRepository = asignaturaRepository;
    }

    public List<Asignatura> listar() {
        return asignaturaRepository.findAll();
    }

    public List<Asignatura> listarActivas() {
        return asignaturaRepository.findByActivoTrue();
    }

    public Asignatura guardar(Asignatura asignatura) {
        return asignaturaRepository.save(asignatura);
    }

    public Asignatura actualizar(Long id, Asignatura asignatura) {
        Asignatura existente = asignaturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignatura no encontrada con id: " + id));
        existente.setNombre(asignatura.getNombre());
        existente.setDescripcion(asignatura.getDescripcion());
        existente.setActivo(asignatura.getActivo());
        return asignaturaRepository.save(existente);
    }

    public void eliminar(Long id) {
        asignaturaRepository.deleteById(id);
    }

    public Optional<Asignatura> buscarPorId(Long id) {
        return asignaturaRepository.findById(id);
    }
}
