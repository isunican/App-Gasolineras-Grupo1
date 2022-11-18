package es.unican.is.appgasolineras.activities.listaFavoritas;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import es.unican.is.appgasolineras.model.Gasolinera;
import es.unican.is.appgasolineras.repository.GasolinerasRepository;
import es.unican.is.appgasolineras.repository.IGasolinerasRepository;
import es.unican.is.appgasolineras.repository.db.GasolineraDao;
import es.unican.is.appgasolineras.repository.db.GasolineraDatabase;
import es.unican.is.appgasolineras.repository.rest.GasolinerasServiceConstants;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class ListaFavoritasPresenterITest {

    private static IListaFavoritasContract.Presenter presenter;
    private static IListaFavoritasContract.View view;
    private static IGasolinerasRepository repository;
    private static Context con;
    private GasolineraDatabase db;

    @Before
    public void inicializa() {
        con = ApplicationProvider.getApplicationContext();
        repository = new GasolinerasRepository(con);
        db = GasolineraDatabase.getDB(con);

        view = mock(IListaFavoritasContract.View.class);
        when(view.getGasolineraRepository()).thenReturn(repository);
        presenter = new ListaFavoritasPresenter(view, db, true);
    }

    @After
    public void cierra() {
        GasolineraDatabase.closeDB();
    }

    @BeforeClass
    public static void setUp() {
        GasolinerasServiceConstants.setStaticURL();
    }

    @AfterClass
    public static void clean() {
        GasolinerasServiceConstants.setMinecoURL();
    }

    /**
     * Test implementado por Pedro Berrío
     */
    @Test
    public void onGasolineraClickedTest(){
        //Creo una gasolinera
        Gasolinera g = new Gasolinera();
        g.setId("1040");
        g.setDieselA("1,80");
        g.setNormal95("1,90");
        g.setIDCCAA("06");
        g.setDireccion("Avenida los castros, 36");

        //Inicializo base de datos
        GasolineraDao gasDao = db.gasolineraDao();
        gasDao.deleteAll();

        //No existen gasolineras por lo que no se llama al metodo openGasolineraDetails
        presenter.init();
        presenter.onGasolineraClicked(0);
        verify(view, times(0)).openGasolineraDetails(g);

        gasDao.insertAll(g);
        //No hay ninguna gasolinera con ese indice por lo que no se llama al método openGasolineraDetails
        presenter.onGasolineraClicked(5);
        verify(view, times(0)).openGasolineraDetails(g);

        // Creo una gasolinera y la añado a favoritas
        Gasolinera g1 = new Gasolinera();
        g1.setId("1041");
        g1.setDieselA("1,70");
        g1.setNormal95("1,80");
        g1.setIDCCAA("06");
        g1.setDireccion("Avenida los castros,36");
        gasDao.insertAll(g1);

        //Compruebo que se llama al método openGasolineraDetails
        presenter.init();
        presenter.onGasolineraClicked(0);
        verify(view, times(1)).openGasolineraDetails(g1);

        //Borro base de datos
        gasDao.deleteAll();
    }
}
