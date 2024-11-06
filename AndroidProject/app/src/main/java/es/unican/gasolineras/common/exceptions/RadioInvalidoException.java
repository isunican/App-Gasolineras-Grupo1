package es.unican.gasolineras.common.exceptions;

public class RadioInvalidoException extends RuntimeException {
    public RadioInvalidoException() {
        super("El radio no puede ser 0");
    }
}
