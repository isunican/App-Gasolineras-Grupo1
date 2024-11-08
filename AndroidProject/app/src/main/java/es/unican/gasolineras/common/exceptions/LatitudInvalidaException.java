package es.unican.gasolineras.common.exceptions;

public class LatitudInvalidaException extends RuntimeException {
    public LatitudInvalidaException(){
        super("Latitud fuera de rango");
    }
}
