package es.unican.gasolineras.common.exceptions;

public class LongitudInvalidaException extends RuntimeException {
    public LongitudInvalidaException() {
        super("Longitud fuera de rango");
    }
}
