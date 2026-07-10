package cr.ac.fidelitas.Tutorconnect.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/*
 * @author (tu nombre aquí)
 */

@Entity
@Table(name = "estudiante")
public class Estudiante {

    @Id
    @Column(name = "id_usuario")
    private Long idUsuario;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(length = 100)
    private String carrera;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "estudiante_asignatura",
        joinColumns = @JoinColumn(name = "id_usuario"),
        inverseJoinColumns = @JoinColumn(name = "id_asignatura")
    )
    private Set<Asignatura> asignaturasInteres = new HashSet<>();

    public Estudiante() {
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Set<Asignatura> getAsignaturasInteres() {
        return asignaturasInteres;
    }

    public void setAsignaturasInteres(Set<Asignatura> asignaturasInteres) {
        this.asignaturasInteres = asignaturasInteres;
    }
}
