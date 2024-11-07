package es.unican.gasolineras.model.validators;

import static org.junit.Assert.assertThrows;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.unican.gasolineras.common.exceptions.LatitudInvalidaException;
import es.unican.gasolineras.common.exceptions.LongitudInvalidaException;
import es.unican.gasolineras.common.exceptions.RadioInvalidoException;
import es.unican.gasolineras.model.InterestPoint;

@RunWith(RobolectricTestRunner.class)
public class InterestPointValidatorTest {

    InterestPoint interestPoint;

    @Before
    public void setUp() {

        interestPoint = new InterestPoint("Punto", "#0000ff", 40.0637, -82.3467, 20);
    }

    @Test
    public void checkFieldsWithValidValues(){

        InterestPointValidator.checkFields(interestPoint);
    }

    @Test
    public void checkFieldsWithLatitudePositiveInvalid(){
        interestPoint.setLatitude(91);
        assertThrows(LatitudInvalidaException.class,
                () -> InterestPointValidator.checkFields(interestPoint));
    }

    @Test
    public void checkFieldsWithLatitudeNegativeInvalid() {
        interestPoint.setLatitude(-91);
        assertThrows(LatitudInvalidaException.class,
                () -> InterestPointValidator.checkFields(interestPoint));
    }

    @Test
    public void checkFieldsWithLongitudePositiveInvalid() {
        interestPoint.setLongitude(181);
        assertThrows(LongitudInvalidaException.class,
                () -> InterestPointValidator.checkFields(interestPoint));
    }

    @Test
    public void checkFieldsWithLongitudeNegativeInvalid() {
        interestPoint.setLongitude(-181);
        assertThrows(LongitudInvalidaException.class,
                () -> InterestPointValidator.checkFields(interestPoint));
    }

    @Test
    public void checkFieldsWithRadiusInvalidZero() {
        interestPoint.setRadius(0);
        assertThrows(RadioInvalidoException.class,
                () -> InterestPointValidator.checkFields(interestPoint));
    }

    @Test
    public void checkFieldsWithRadiusInvalidNegative() {
        interestPoint.setRadius(-1);
        assertThrows(RadioInvalidoException.class,
                () -> InterestPointValidator.checkFields(interestPoint));
    }

}
