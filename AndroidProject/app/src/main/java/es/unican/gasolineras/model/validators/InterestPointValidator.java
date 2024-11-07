package es.unican.gasolineras.model.validators;
import es.unican.gasolineras.common.exceptions.LatitudInvalidaException;
import es.unican.gasolineras.common.exceptions.LongitudInvalidaException;
import es.unican.gasolineras.common.exceptions.RadioInvalidoException;
import es.unican.gasolineras.model.InterestPoint;

public class InterestPointValidator {
    public static void checkFields(InterestPoint interestPoint)
            throws LongitudInvalidaException,LatitudInvalidaException,RadioInvalidoException{
        checkLatitude(interestPoint.getLatitude());
        checkLongitude(interestPoint.getLongitude());
        checkRadius(interestPoint.getRadius());
    }
    private static void checkLongitude(double longitude) throws LongitudInvalidaException{
        if(180.0<longitude || longitude<(-180.0)){
            throw new LongitudInvalidaException();
        }
    }
    private static void checkLatitude(double latitude) throws LatitudInvalidaException{
        if(90.0<latitude || latitude<(-90.0)){
            throw new LatitudInvalidaException();
        }
    }
    private static void checkRadius(double radius) throws RadioInvalidoException {
        if(radius <= 0){
            throw new RadioInvalidoException();
        }
    }
}
