package es.unican.is.appgasolineras.common;

import static org.junit.Assert.assertEquals;

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

import java.util.ArrayList;
import java.util.List;

import es.unican.is.appgasolineras.common.prefs.Prefs;
import es.unican.is.appgasolineras.repository.GasolinerasRepository;
import es.unican.is.appgasolineras.repository.db.GasolineraDatabase;
import es.unican.is.appgasolineras.repository.rest.GasolinerasServiceConstants;

import es.unican.is.appgasolineras.common.prefs.IPrefs;
import es.unican.is.appgasolineras.model.Gasolinera;
import es.unican.is.appgasolineras.repository.IGasolinerasRepository;

/**
 * IMPORTANTE
 * Aunque el uso de este test es incorrecto puesto que no se realiza integración como tal,
 * por falta de tiempo y para no tener que estar adaptando los dos test lo cuál requeriría de
 * bastante trabajo y tiempo se dejará así momentáneamente.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class FiltersITest {

    private static IGasolinerasRepository gasolineras;
    private static IPrefs prefs;
    private static Context con;
    private List<Gasolinera> listGasolineras;

    @Before
    public void inicializa() {
        con = ApplicationProvider.getApplicationContext();
        gasolineras = new GasolinerasRepository(con);
        prefs = new Prefs(con,"KEY_DEFAULT_PREFS");
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
    public static void clean() { GasolinerasServiceConstants.setMinecoURL(); }

    @Test
    public void filtraPrecioTest() {
        //caso1
        assertEquals(156, Filters.filtraPrecio(gasolineras.todasGasolineras(), "2.04", prefs.getString("tipoGasolina")).size());
        //caso 2
        assertEquals(0, Filters.filtraPrecio(gasolineras.todasGasolineras(), "-2.04", prefs.getString("tipoGasolina")).size());
        //caso2
        assertEquals(0, Filters.filtraPrecio(new ArrayList<>(), "2.04", prefs.getString("tipoGasolina")).size());
    }

    @Test
    public void maximoEntreTodasTest() {
        //caso1 ver maxima
        assertEquals("2.039", Filters.maximoEntreTodas(gasolineras.todasGasolineras(), prefs.getString("tipoGasolina")));
        //caso2 lista vacia
        assertEquals("0.00", Filters.maximoEntreTodas(new ArrayList<>(), prefs.getString("tipoGasolina")));
    }

    @Test
    public void filtraTipoTestInterfaz() {

        // Caso de prueba UGIC 1.a
        //Creamos la lista de gasolineras
        listGasolineras = new ArrayList<>();
        listGasolineras = gasolineras.todasGasolineras();

        // Caso de prueba UGIC 1.a
        assertEquals(156, Filters.filtraTipo(listGasolineras,
                "dieselA").size());

        // Caso de prueba UGIC 1.a
        //Creamos la lista de gasolineras
        listGasolineras = new ArrayList<>();
        listGasolineras = gasolineras.todasGasolineras();


        // Caso de prueba UGIC 1.a
        assertEquals(0, Filters.filtraTipo(listGasolineras,
                "bioEtanol").size());

        // Caso de prueba UGIC 1.a
        //Creamos la lista de gasolineras
        listGasolineras = new ArrayList<>();
        listGasolineras = gasolineras.todasGasolineras();

        // Caso de prueba UGIC 1.a
        assertEquals(listGasolineras.size(), Filters.filtraTipo(listGasolineras,
                "").size());
    }
}
