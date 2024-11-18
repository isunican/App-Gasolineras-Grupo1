package es.unican.gasolineras;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.unican.gasolineras.model.GasolinerasResponse;

@RunWith(RobolectricTestRunner.class)
public class ExampleTest {

    @Test
    public void test() {

        GasolinerasResponse gasolinerasResponse = new GasolinerasResponse();
        assertEquals(0, gasolinerasResponse.getGasolinerasCount());
    }
}
