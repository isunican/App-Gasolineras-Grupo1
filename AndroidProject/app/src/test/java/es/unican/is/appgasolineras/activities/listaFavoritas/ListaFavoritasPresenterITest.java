package es.unican.is.appgasolineras.activities.listaFavoritas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

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
     * Test implementado por Roberto Hernando
     */
    @Test
    public void initListaFavoritasIntegracionTest() {
        ArgumentCaptor<List<Gasolinera>> listaDevuelta = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<Gasolinera>> listaDevuelta2 = ArgumentCaptor.forClass(List.class);
        List<Gasolinera> resultados;
        Gasolinera gasolineraResultado1;
        Gasolinera gasolineraResultado2;
        Gasolinera gasolineraResultado3;

        GasolineraDao gasDao = db.gasolineraDao();
        gasDao.deleteAll();
        // Comprobamos con las gasolineras favoritas vacías
        presenter.init();
        // Comprobamos que se llama a showLoadErrorDAOVacia porque no hay gasolineras favoritas
        verify(view, times(1)).showLoadErrorDAOVacia();

        // Creo una gasolinera y la añado a favoritas
        Gasolinera g1 = new Gasolinera();
        g1.setId("1039");
        g1.setDieselA("1");
        g1.setNormal95("1");
        g1.setIDCCAA("06");
        gasDao.insertAll(g1);

        presenter.init();
        // Comprobamos que llama a showGasolineras y recogemos el valor de la lista
        verify(view, times(1)).showGasolineras(listaDevuelta.capture());
        resultados = listaDevuelta.getValue();
        gasolineraResultado1 = resultados.get(0);
        // Comprobar tamanho lista
        assertEquals(1, resultados.size());
        // Comprobar que el id es correcto
        assertEquals("1039", gasolineraResultado1.getId());
        // Comprobar que se ha actualizado el precio de la gasolina
        assertEquals("1,859", gasolineraResultado1.getNormal95());
        // Comprobar que se ha actualizado el precio del diesel
        assertEquals("1,999", gasolineraResultado1.getDieselA());
        // Comprobar que la comunidad no ha cambiado
        assertEquals("06", gasolineraResultado1.getIDCCAA());
        gasDao.deleteAll();

        // Añadimos otras dos gasolineras a favoritas (Total 3)
        Gasolinera g2 = new Gasolinera();
        Gasolinera g3 = new Gasolinera();
        g2.setId("1048");
        g2.setDieselA("1");
        g2.setNormal95("1");
        g2.setIDCCAA("06");
        g3.setId("1036");
        g3.setDieselA("1");
        g3.setNormal95("1");
        g3.setIDCCAA("06");
        gasDao.insertAll(g1);
        gasDao.insertAll(g2);
        gasDao.insertAll(g3);

        presenter.init();
        // Comprobamos que llama a showGasolineras y recogemos el valor de la lista
        verify(view, times(2)).showGasolineras(listaDevuelta2.capture());
        resultados = listaDevuelta2.getValue();
        gasolineraResultado1 = resultados.get(2);
        gasolineraResultado2 = resultados.get(1);
        gasolineraResultado3 = resultados.get(0);
        // Comprobar tamanho lista
        assertEquals(3, resultados.size());

        // Compruebo la primera gasolinera
        // Comprobar que el id es correcto
        assertEquals("1039", gasolineraResultado1.getId());
        // Comprobar que se ha actualizado el precio de la gasolina
        assertEquals("1,859", gasolineraResultado1.getNormal95());
        // Comprobar que se ha actualizado el precio del diesel
        assertEquals("1,999", gasolineraResultado1.getDieselA());
        // Comprobar que la comunidad no ha cambiado
        assertEquals("06", gasolineraResultado1.getIDCCAA());

        // Compruebo la segunda gasolinera
        // Comprobar que el id es correcto
        assertEquals("1048", gasolineraResultado2.getId());
        // Comprobar que se ha actualizado el precio de la gasolina
        assertEquals("1,819", gasolineraResultado2.getNormal95());
        // Comprobar que se ha actualizado el precio del diesel
        assertEquals("2,009", gasolineraResultado2.getDieselA());
        // Comprobar que la comunidad no ha cambiado
        assertEquals("06", gasolineraResultado2.getIDCCAA());

        // Compruebo la tercera gasolinera
        // Comprobar que el id es correcto
        assertEquals("1036", gasolineraResultado3.getId());
        // Comprobar que se ha actualizado el precio de la gasolina
        assertEquals("1,789", gasolineraResultado3.getNormal95());
        // Comprobar que se ha actualizado el precio del diesel
        assertEquals("1,969", gasolineraResultado3.getDieselA());
        // Comprobar que la comunidad no ha cambiado
        assertEquals("06", gasolineraResultado3.getIDCCAA());

        gasDao.deleteAll();
    }

    /**
     * Test implementado por Jesus Revuelta
     */
    @Test
    public void conseguirGasolinerasActualizadasTest() {
        GasolineraDao dao = db.gasolineraDao();
        //Caso 1
        //MasDeUna = false
        String id = "06";
        List<Gasolinera> listaFav = new ArrayList<>();
        presenter.init();
        assertEquals(presenter.conseguirGasolinerasActualizadas(false, listaFav, id), repository.getGasolineras("06"));

        //Caso 2
        // MasDeUna = true
        id = "06";
        Gasolinera g1 = new Gasolinera();
        g1.setIdMun("5744");
        g1.setIDCCAA("06");
        g1.setId("1039");
        Gasolinera g2 = new Gasolinera();
        g2.setIdMun("1100");
        g2.setIDCCAA("09");
        g2.setId("1673");
        listaFav.add(g1);
        listaFav.add(g2);
        List<Gasolinera> listaDef = new ArrayList<>();
        listaDef.addAll(repository.gasolinerasMunicipio("1100"));
        listaDef.addAll(repository.gasolinerasMunicipio("5744"));
        assertEquals(listaDef, presenter.conseguirGasolinerasActualizadas(true, listaFav, id));

        //Caso 3
        //MasDeUna = true
        listaFav.clear();
        id = "06";
        assertEquals(0, presenter.conseguirGasolinerasActualizadas(true, listaFav, id).size());


        //Caso 4
        //MasDeUna = false e id = null
        listaFav.clear();
        assertNull(null, presenter.conseguirGasolinerasActualizadas(false, listaFav, null));

        dao.deleteAll();
    }
}
