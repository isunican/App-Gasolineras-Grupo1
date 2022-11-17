package es.unican.is.appgasolineras.activities.detail;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import es.unican.is.appgasolineras.model.Gasolinera;
import es.unican.is.appgasolineras.repository.db.GasolineraDatabase;

public class GasolineraDetailViewTest {

    private static IDetailContract.Presenter presenter;
    private static IDetailContract.View view;
    private static IDetailContract.View view2;
    private static IDetailContract.View view3;
    private static GasolineraDatabase db;
    private Gasolinera g1, g2, g3;

    @Before
    public void inicializa() {
        db = mock(GasolineraDatabase.class);
        view = mock(IDetailContract.View.class);
        view2 = mock(IDetailContract.View.class);
        view3 = mock(IDetailContract.View.class);
        g1 = mock(Gasolinera.class);
        g2 = mock(Gasolinera.class);
        g3 = mock(Gasolinera.class);
    }

    /**
     * Test unitario realizado por Angel Castanedo para comprobar el metodo init
     * de la clase GasolineraDetailPresenter
     */
    @Test
    public void initGasolineraDetailPresenterTest() {
        /*
        Caso de prueba UGIC.2a
         */

        // Definimos que queremos que devuelvan los metodos de la clase Gasolinera
        when(g1.getMunicipio()).thenReturn("");
        when(g1.getRotulo()).thenReturn("");
        when(g1.getHorario()).thenReturn("");
        when(g1.getDieselA()).thenReturn("");
        when(g1.getNormal95()).thenReturn("");
        when(g1.getDireccion()).thenReturn("");

        // Inicializamos el presenter
        presenter = new GasolineraDetailPresenter(g1,view, db);
        presenter.init();

        // Comprobamos que se llama a los metodos el numero correcto de veces
        verify(g1, times(1)).getMunicipio();
        verify(g1, times(1)).getRotulo();
        verify(g1, times(1)).getHorario();
        verify(g1, times(1)).getNormal95();
        verify(g1, times(1)).getDireccion();
        // Se llama dos veces a getDieselA, 1 en el propio metodo y otra en el calcula
        verify(g1, times(2)).getDieselA();

        verify(view, times(1))
                .setInfo("-","-","-","-","-","-","-");

        /*
        Caso de prueba UGIC.2b
         */

        // Definimos que queremos que devuelvan los metodos de la clase Gasolinera
        when(g2.getMunicipio()).thenReturn("Santander");
        when(g2.getRotulo()).thenReturn("CEPSA");
        when(g2.getHorario()).thenReturn("L-D:10:00-21:00");
        when(g2.getDieselA()).thenReturn("1,87");
        when(g2.getNormal95()).thenReturn("1,90");
        when(g2.getDireccion()).thenReturn("Avenida los castros, 36");

        // Inicializamos el presenter
        presenter = new GasolineraDetailPresenter(g2,view2, db);
        presenter.init();

        // Comprobamos que se llama a los metodos el numero correcto de veces
        verify(g2, times(2)).getMunicipio();
        verify(g2, times(2)).getRotulo();
        verify(g2, times(2)).getHorario();
        verify(g2, times(2)).getDireccion();

        // Estos dos se llaman 4 veces, 2 en el metodo init y dos en calcula
        verify(g2, times(4)).getDieselA();
        verify(g2, times(4)).getNormal95();

        verify(view2, times(1))
                .setInfo("Santander","cepsa","L-D:10:00-21:00","1,90 €","1,87 €", presenter.calcula(), "Avenida los castros, 36");


        /*
        Caso de prueba UGIC.2c
         */

        // Definimos que queremos que devuelvan los metodos de la clase Gasolinera
        when(g3.getMunicipio()).thenReturn("Santander");
        when(g3.getRotulo()).thenReturn("REPSOL");
        when(g3.getHorario()).thenReturn("L-D:10:00-20:00");
        when(g3.getDieselA()).thenReturn("1,8785");
        when(g3.getNormal95()).thenReturn("1,9019");
        when(g3.getDireccion()).thenReturn("Avenida los castros, 35");

        // Inicializamos el presenter
        presenter = new GasolineraDetailPresenter(g3,view3, db);
        presenter.init();

        // Comprobamos que se llama a los metodos el numero correcto de veces
        verify(g3, times(2)).getMunicipio();
        verify(g3, times(2)).getRotulo();
        verify(g3, times(2)).getHorario();
        verify(g3, times(2)).getDireccion();

        // Estos dos se llaman 4 veces, 2 en el metodo init y dos en calcula
        verify(g3, times(4)).getDieselA();
        verify(g3, times(4)).getNormal95();

        verify(view3, times(1))
                .setInfo("Santander","repsol","L-D:10:00-20:00","1,90 €","1,87 €", presenter.calcula(), "Avenida los castros, 35");
    }
}
