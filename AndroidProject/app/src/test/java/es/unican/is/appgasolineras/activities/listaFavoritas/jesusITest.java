package es.unican.is.appgasolineras.activities.listaFavoritas;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import es.unican.is.appgasolineras.activities.main.IMainContract;
import es.unican.is.appgasolineras.activities.main.MainPresenter;
import es.unican.is.appgasolineras.common.prefs.IPrefs;
import es.unican.is.appgasolineras.common.prefs.Prefs;
import es.unican.is.appgasolineras.model.Gasolinera;
import es.unican.is.appgasolineras.repository.GasolinerasRepository;
import es.unican.is.appgasolineras.repository.IGasolinerasRepository;
import es.unican.is.appgasolineras.repository.db.GasolineraDao;
import es.unican.is.appgasolineras.repository.db.GasolineraDatabase;
import es.unican.is.appgasolineras.repository.rest.GasolinerasServiceConstants;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class jesusITest {

    private static IListaFavoritasContract.Presenter presenter;
    private static IListaFavoritasContract.View view;
    private static IGasolinerasRepository repository;
    private static GasolineraDao dao;
    private static GasolineraDatabase db;
    private static Context con;

    @Before
    public void inicializa() {
        con = ApplicationProvider.getApplicationContext();
        repository = new GasolinerasRepository(con);
        db = GasolineraDatabase.getDB(con);
        dao = db.gasolineraDao();

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
        GasolinerasServiceConstants.setStaticURL2();
    }

    @AfterClass
    public static void clean() {
        GasolinerasServiceConstants.setMinecoURL();
    }


    @Test
    public void conseguirGasolinerasActualizadas() {

        //Caso 1
        Boolean MasDeUna = false;
        String id = "06";
        List<Gasolinera> listaFav = new ArrayList<>();


        presenter.init();
        assertEquals(presenter.conseguirGasolinerasActualizadas(MasDeUna, listaFav, id), repository.getGasolineras("06"));

        /*
        //Caso 2
        MasDeUna = true;
        id = "06";

        Gasolinera g1 = new Gasolinera();
        g1.setIdMun("1");
        g1.setIDCCAA("16");
        g1.setId("4");

        Gasolinera g2 = new Gasolinera();
        g2.setIdMun("1000");
        g2.setIDCCAA("09");
        g2.setId("7");

        listaFav.add(g1);
        listaFav.add(g2);

        List<Gasolinera> listaDef = new ArrayList<>();



        listaDef.addAll(repository.gasolinerasMunicipio("1"));
        listaDef.addAll(repository.gasolinerasMunicipio("1000"));

        assertEquals(presenter.conseguirGasolinerasActualizadas(MasDeUna, listaFav, id), listaDef);
*/

        //Caso 3
        MasDeUna = true;
        listaFav.clear();
        id = "06";

        assertEquals(presenter.conseguirGasolinerasActualizadas(MasDeUna, listaFav, id).size(), 0);


        //Caso 4

        MasDeUna = false;
        id = null;
        listaFav.clear();

        assertEquals(presenter.conseguirGasolinerasActualizadas(MasDeUna, listaFav, id), null);

        dao.deleteAll();

    }


}
