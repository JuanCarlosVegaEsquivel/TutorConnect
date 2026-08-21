package cr.ac.fidelitas.Tutorconnect.service;

import cr.ac.fidelitas.Tutorconnect.domain.Horario;
import cr.ac.fidelitas.Tutorconnect.repository.HorarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Vector
 */
@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;

    @Autowired
    public HorarioService(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Horario> listar() {
        return horarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Horario> listarPorTutor(Long idUsuario) {
        return horarioRepository.findByTutorIdUsuario(idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<Horario> buscarPorId(Long idHorario) {
        return horarioRepository.findById(idHorario);
    }

    @Transactional
    public Horario guardar(Horario horario) {

        validarConflicto(horario);

        return horarioRepository.save(horario);
    }

    @Transactional
    public Horario actualizar(Long idHorario, Horario horario) {

        Horario existente = horarioRepository.findById(idHorario)
                .orElseThrow(() -> new RuntimeException(
                        "Horario no encontrado con id: " + idHorario));

        horario.setIdHorario(idHorario);

        validarConflicto(horario);

        existente.setDia(horario.getDia());
        existente.setHoraInicio(horario.getHoraInicio());
        existente.setHoraFin(horario.getHoraFin());
        existente.setTutor(horario.getTutor());

        return horarioRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long idHorario) {
        horarioRepository.deleteById(idHorario);
    }

    private void validarConflicto(Horario horario) {

        if (horario.getTutor() == null) {
            throw new RuntimeException("Debe seleccionar un tutor.");
        }

        if (horario.getDia() == null || horario.getDia().isBlank()) {
            throw new RuntimeException("Debe seleccionar un día.");
        }

        if (horario.getHoraInicio() == null || horario.getHoraInicio().isBlank()) {
            throw new RuntimeException("Debe indicar la hora de inicio.");
        }

        if (horario.getHoraFin() == null || horario.getHoraFin().isBlank()) {
            throw new RuntimeException("Debe indicar la hora de fin.");
        }

        if (horario.getHoraInicio().compareTo(horario.getHoraFin()) >= 0) {
            throw new RuntimeException(
                    "La hora de inicio debe ser menor que la hora de fin.");
        }

        List<Horario> horarios = horarioRepository
                .findByTutorIdUsuarioAndDia(
                        horario.getTutor().getIdUsuario(),
                        horario.getDia());

        for (Horario existente : horarios) {

            if (horario.getIdHorario() != null
                    && horario.getIdHorario().equals(existente.getIdHorario())) {
                continue;
            }

            boolean conflicto =
                    horario.getHoraInicio().compareTo(existente.getHoraFin()) < 0
                    && horario.getHoraFin().compareTo(existente.getHoraInicio()) > 0;

            if (conflicto) {
                throw new RuntimeException(
                        "El tutor ya tiene un horario que entra en conflicto con el horario seleccionado.");
            }
        }
    }
}
