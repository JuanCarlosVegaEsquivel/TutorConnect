package cr.ac.fidelitas.Tutorconnect.domain;

/**
 *
 * @author Vector
 */
public enum Rol {
    ADMIN("Administrador"),
    TUTOR("Tutor"),
    ESTUDIANTE("Estudiante");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getAuthority() {
        return "ROLE_" + name();
    }
}