package es.unican.is.appgasolineras.activities.main;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import android.content.Context;
import android.location.Location;
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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.unican.is.appgasolineras.common.prefs.IPrefs;
import es.unican.is.appgasolineras.common.prefs.Prefs;
import es.unican.is.appgasolineras.model.Gasolinera;
import es.unican.is.appgasolineras.repository.GasolinerasRepository;
import es.unican.is.appgasolineras.repository.IGasolinerasRepository;
import es.unican.is.appgasolineras.repository.db.GasolineraDatabase;
import es.unican.is.appgasolineras.repository.rest.GasolinerasServiceConstants;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class MainPresenterITest {

    private static IMainContract.Presenter presenter;
    private static IMainContract.View view;
    private static IGasolinerasRepository repository;
    private static IPrefs prefs;
    private static Context con;

    @Before
    public void inicializa() {
        con = ApplicationProvider.getApplicationContext();
        repository = new GasolinerasRepository(con);
        prefs = new Prefs(con, "KEY_DEFAULT_PREFS");

        view = mock(IMainContract.View.class);
        when(view.getGasolineraRepository()).thenReturn(repository);
        presenter = new MainPresenter(view, prefs, true);
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

    /**
     * Test de integración realizado por Mario Ingelmo para probar del main el filtro de marca
     * con el de precio
     * Prueba determinada en el documento US465234-BuscarGasolinerasPorMarca-TestPlan
     */
    @Test
    public void mainFiltradoPorMarcaYPrecioTest() {
        ArgumentCaptor<List<Gasolinera>> listaDevuelta = ArgumentCaptor.forClass(List.class);

        // UGIC.1a: Filtrar por la marca CAMPSA sin filtro de precio, del listado solo 3 cumplen
        // con esta propiedad. Comprobamos que el tamaño de la lista sea 3 y
        // de esas gasolineras que su rotulo sea CAMPSA
        prefs.putString("idComunidad", "");
        prefs.putString("tipoGasolina", "");
        prefs.putString("ubicacion", "no");
        prefs.putString("maxPrecio", "");
        prefs.putString("marca", "CAMPSA");
        presenter.init();
        verify(view, times(1)).showGasolineras(listaDevuelta.capture());
        assertEquals(3, listaDevuelta.getValue().size());
        for (Gasolinera g : listaDevuelta.getValue()) {
            assertEquals("CAMPSA", g.getRotulo());
        }

        // UGIC.1b: Filtrar por la marca CAMPSA y 1.80€, del listado solo 2 cumplen con esta propiedad
        // Comprobamos que el tamaño de la lista sea 2 y de esas gasolineras que su rotulo sea CAMPSA
        prefs.putString("idComunidad", "");
        prefs.putString("tipoGasolina", "");
        prefs.putString("ubicacion", "no");
        prefs.putString("maxPrecio", "1.80");
        prefs.putString("marca", "CAMPSA");
        presenter.init();
        verify(view, times(2)).showGasolineras(listaDevuelta.capture());
        assertEquals(2, listaDevuelta.getValue().size());
        for (Gasolinera g : listaDevuelta.getValue()) {
            assertEquals("CAMPSA", g.getRotulo());
        }

        // UGIC.1c: Filtrar por la marca CAMPSA y un precio muy restrictivo, ninguna gasolinera cumple
        // con la propiedad, lista vacía
        prefs.putString("idComunidad", "");
        prefs.putString("tipoGasolina", "");
        prefs.putString("ubicacion", "no");
        prefs.putString("maxPrecio", "1.0");
        prefs.putString("marca", "CAMPSA");
        presenter.init();
        verify(view, times(3)).showGasolineras(listaDevuelta.capture());
        assertEquals(0, listaDevuelta.getValue().size());

        // UGIC.1d: Filtrar con ambos filtros vacíos, se devolverá la lista completa con 156 elementos
        prefs.putString("idComunidad", "");
        prefs.putString("tipoGasolina", "");
        prefs.putString("ubicacion", "no");
        prefs.putString("maxPrecio", "");
        prefs.putString("marca", "");
        presenter.init();
        verify(view, times(4)).showGasolineras(listaDevuelta.capture());
        assertEquals(156, listaDevuelta.getValue().size());
    }

    /**
     * Test de integración realizado por Mario Ingelmo para probar del main el filtro de marca
     * con el de tipo de combustible
     * Prueba determinada en el documento US465234-BuscarGasolinerasPorMarca-TestPlan
     */
    @Test
    public void mainFiltradoPorMarcaYCombustibleTest() {
        ArgumentCaptor<List<Gasolinera>> listaDevuelta = ArgumentCaptor.forClass(List.class);

        // UGIC.2a: Filtrar por la marca REPSOL sin filtro de combustible, del listado solo 38 cumplen
        // con esta propiedad. Comprobamos que el tamaño de la lista sea 38 y
        // de esas gasolineras que su rotulo sea REPSOL
        prefs.putString("idComunidad", "");
        prefs.putString("tipoGasolina", "");
        prefs.putString("ubicacion", "no");
        prefs.putString("maxPrecio", "");
        prefs.putString("marca", "REPSOL");
        presenter.init();
        verify(view, times(1)).showGasolineras(listaDevuelta.capture());
        assertEquals(38, listaDevuelta.getValue().size());
        for (Gasolinera g : listaDevuelta.getValue()) {
            assertEquals("REPSOL", g.getRotulo());
        }

        // UGIC.2b: Filtrar por la marca REPSOL y filtro de combustible a Gasolina 95 E5 (normal95),
        // del listado solo 37 cumplen con esta propiedad. Comprobamos que el tamaño de la lista sea
        // 37 y de esas gasolineras que su rotulo sea REPSOL y no esté vacía la Gasolina 95 E5
        prefs.putString("idComunidad", "");
        prefs.putString("tipoGasolina", "normal95");
        prefs.putString("ubicacion", "no");
        prefs.putString("maxPrecio", "");
        prefs.putString("marca", "REPSOL");
        presenter.init();
        verify(view, times(2)).showGasolineras(listaDevuelta.capture());
        assertEquals(37, listaDevuelta.getValue().size());
        for (Gasolinera g : listaDevuelta.getValue()) {
            assertEquals("REPSOL", g.getRotulo());
            assertNotEquals("", g.getNormal95());
        }

        // UGIC.2c: Filtrar por la marca REPSOL y un combustible que no haya, Hidrógeno (h2),
        // ninguna gasolinera cumple con la propiedad, lista vacía
        prefs.putString("idComunidad", "");
        prefs.putString("tipoGasolina", "h2");
        prefs.putString("ubicacion", "no");
        prefs.putString("maxPrecio", "");
        prefs.putString("marca", "REPSOL");
        presenter.init();
        verify(view, times(3)).showGasolineras(listaDevuelta.capture());
        assertEquals(0, listaDevuelta.getValue().size());

        // UGIC.2d: Filtrar con ambos filtros vacíos, se devolverá la lista completa con 156 elementos
        prefs.putString("idComunidad", "");
        prefs.putString("tipoGasolina", "");
        prefs.putString("ubicacion", "no");
        prefs.putString("maxPrecio", "");
        prefs.putString("marca", "");
        presenter.init();
        verify(view, times(4)).showGasolineras(listaDevuelta.capture());
        assertEquals(156, listaDevuelta.getValue().size());
    }

    /**
     * Test realizado por Álvaro Alcántara para probar el funcionamiento
     * de la ordenación por ubicación en el MainPresenter
     */
    @Test
    public void filtradoPorUbicacionTest() {
        // UGIC.2a
        ArgumentCaptor<List<Gasolinera>> listaDevuelta = ArgumentCaptor.forClass(List.class);
        prefs.putString("idComunidad", "");
        prefs.putString("tipoGasolina", "");
        prefs.putString("ubicacion", "si");
        prefs.putString("maxPrecio", "");
        prefs.putString("marca", "");
        prefs.putString("latitud", "43.468732");
        prefs.putString("longitud", "-3.805011");
        presenter.init();
        verify(view, times(1)).showGasolineras(listaDevuelta.capture());
        Location locAct = new Location("");
        locAct.setLatitude(43.468732);
        locAct.setLongitude(-3.805011);
        for (int i = 0; i < listaDevuelta.getValue().size() - 1; i++) {
            Gasolinera g1 = listaDevuelta.getValue().get(i);
            Gasolinera g2 = listaDevuelta.getValue().get(i + 1);
            if ((!g1.getLatitud().equals("") && !g2.getLatitud().equals("") && (!g1.getLongitud().equals("") && !g2.getLongitud().equals("")))) {
                Location locg1 = new Location("");
                Location locg2 = new Location("");
                locg1.setLatitude(Double.parseDouble(g1.getLatitud().replace(',', '.')));
                locg1.setLongitude(Double.parseDouble(g1.getLongitud().replace(',', '.')));
                locg2.setLatitude(Double.parseDouble(g2.getLatitud().replace(',', '.')));
                locg2.setLongitude(Double.parseDouble(g2.getLongitud().replace(',', '.')));
                assertTrue(locAct.distanceTo(locg1) <= locAct.distanceTo(locg2));
            }
        }

        // UGIC.2b
        prefs.putString("idComunidad", ""); // en los datos estaticos todas son de cantabria
        prefs.putString("tipoGasolina", "normal95");
        prefs.putString("ubicacion", "si");
        prefs.putString("maxPrecio", "");
        prefs.putString("marca", "");
        prefs.putString("latitud", "43.468732");
        prefs.putString("longitud", "-3.805011");
        presenter.init();
        verify(view, times(2)).showGasolineras(listaDevuelta.capture());
        locAct = new Location("");
        locAct.setLatitude(43.468732);
        locAct.setLongitude(-3.805011);
        for (Gasolinera g : listaDevuelta.getValue()) {
            assertNotEquals("", g.getNormal95());
        }
        for (int i = 0; i < listaDevuelta.getValue().size() - 1; i++) {
            Gasolinera g1 = listaDevuelta.getValue().get(i);
            Gasolinera g2 = listaDevuelta.getValue().get(i + 1);
            if ((!g1.getLatitud().equals("") && !g2.getLatitud().equals("") && (!g1.getLongitud().equals("") && !g2.getLongitud().equals("")))) {
                Location locg1 = new Location("");
                Location locg2 = new Location("");
                locg1.setLatitude(Double.parseDouble(g1.getLatitud().replace(',', '.')));
                locg1.setLongitude(Double.parseDouble(g1.getLongitud().replace(',', '.')));
                locg2.setLatitude(Double.parseDouble(g2.getLatitud().replace(',', '.')));
                locg2.setLongitude(Double.parseDouble(g2.getLongitud().replace(',', '.')));
                double dist1 = locAct.distanceTo(locg1);
                double dist2 = locAct.distanceTo(locg2);
                assertTrue(dist1 <= dist2);
            }
        }
    }

    /**
     * Test de integración realizado por Mario Ingelmo y Álvaro Alcántara para probar del main
     * que los dos filtros desarrollados funcionan en conjunto y también con otros filtros
     * Prueba determinada en el documento US454113-BuscarGasolinerasCercanasMedianteLocalizacion-TestPlan
     */
    @Test
    public void mainFiltradoPorMarcaUbicacionYOtrosFiltrosTest() {
        ArgumentCaptor<List<Gasolinera>> listaDevuelta = ArgumentCaptor.forClass(List.class);
        Location locAct = new Location("");
        locAct.setLatitude(43.468732);
        locAct.setLongitude(-3.805011);

        // UGIC3.a: Filtrar por la marca REPSOL con ordenación por ubicación, se comprueba que
        // la lista sea de 38 gasolineras, todas ellas de REPSOL y que estén ordenadas.
        prefs.putString("idComunidad", "");
        prefs.putString("tipoGasolina", "");
        prefs.putString("ubicacion", "si");
        prefs.putString("maxPrecio", "");
        prefs.putString("marca", "REPSOL");
        prefs.putString("latitud", "43.468732");
        prefs.putString("longitud", "-3.805011");
        presenter.init();
        verify(view, times(1)).showGasolineras(listaDevuelta.capture());
        assertEquals(38, listaDevuelta.getValue().size());
        for (Gasolinera g : listaDevuelta.getValue()) {
            assertEquals("REPSOL", g.getRotulo());
        }
        for (int i = 0; i < listaDevuelta.getValue().size() - 1; i++) {
            Gasolinera g1 = listaDevuelta.getValue().get(i);
            Gasolinera g2 = listaDevuelta.getValue().get(i + 1);
            if ((!g1.getLatitud().equals("") && !g2.getLatitud().equals("") && (!g1.getLongitud().equals("") && !g2.getLongitud().equals("")))) {
                Location locg1 = new Location("");
                Location locg2 = new Location("");
                locg1.setLatitude(Double.parseDouble(g1.getLatitud().replace(',', '.')));
                locg1.setLongitude(Double.parseDouble(g1.getLongitud().replace(',', '.')));
                locg2.setLatitude(Double.parseDouble(g2.getLatitud().replace(',', '.')));
                locg2.setLongitude(Double.parseDouble(g2.getLongitud().replace(',', '.')));
                double dist1 = locAct.distanceTo(locg1);
                double dist2 = locAct.distanceTo(locg2);
                assertTrue(dist1 <= dist2);
            }
        }

        // UGIC3.b: Filtrar por la marca REPSOL con ordenación por ubicación y filtro de combustible,
        // se selecciona Gasolina 95 E5 (normal95), se comprueba que la lista sea de 37 gasolineras,
        // todas ellas de REPSOL, con valor en el campo de la Gasolina 95 E5 y que estén ordenadas.
        prefs.putString("idComunidad", "");
        prefs.putString("tipoGasolina", "normal95");
        prefs.putString("ubicacion", "si");
        prefs.putString("maxPrecio", "");
        prefs.putString("marca", "REPSOL");
        prefs.putString("latitud", "43.468732");
        prefs.putString("longitud", "-3.805011");
        presenter.init();
        verify(view, times(2)).showGasolineras(listaDevuelta.capture());
        assertEquals(37, listaDevuelta.getValue().size());
        for (Gasolinera g : listaDevuelta.getValue()) {
            assertEquals("REPSOL", g.getRotulo());
            assertNotEquals("", g.getNormal95());
        }
        for (int i = 0; i < listaDevuelta.getValue().size() - 1; i++) {
            Gasolinera g1 = listaDevuelta.getValue().get(i);
            Gasolinera g2 = listaDevuelta.getValue().get(i + 1);
            if ((!g1.getLatitud().equals("") && !g2.getLatitud().equals("") && (!g1.getLongitud().equals("") && !g2.getLongitud().equals("")))) {
                Location locg1 = new Location("");
                Location locg2 = new Location("");
                locg1.setLatitude(Double.parseDouble(g1.getLatitud().replace(',', '.')));
                locg1.setLongitude(Double.parseDouble(g1.getLongitud().replace(',', '.')));
                locg2.setLatitude(Double.parseDouble(g2.getLatitud().replace(',', '.')));
                locg2.setLongitude(Double.parseDouble(g2.getLongitud().replace(',', '.')));
                double dist1 = locAct.distanceTo(locg1);
                double dist2 = locAct.distanceTo(locg2);
                assertTrue(dist1 <= dist2);
            }
        }

        // UGIC3.c: Filtrar por la marca REPSOL con ordenación por ubicación y filtro de precio 1,80€,
        // se comprueba que la lista sea de 17 gasolineras todas ellas de REPSOL y que estén ordenadas.
        prefs.putString("idComunidad", "");
        prefs.putString("tipoGasolina", "");
        prefs.putString("ubicacion", "si");
        prefs.putString("maxPrecio", "1.80");
        prefs.putString("marca", "REPSOL");
        prefs.putString("latitud", "43.468732");
        prefs.putString("longitud", "-3.805011");
        presenter.init();
        verify(view, times(3)).showGasolineras(listaDevuelta.capture());
        assertEquals(17, listaDevuelta.getValue().size());
        for (Gasolinera g : listaDevuelta.getValue()) {
            assertEquals("REPSOL", g.getRotulo());
        }
        for (int i = 0; i < listaDevuelta.getValue().size() - 1; i++) {
            Gasolinera g1 = listaDevuelta.getValue().get(i);
            Gasolinera g2 = listaDevuelta.getValue().get(i + 1);
            if ((!g1.getLatitud().equals("") && !g2.getLatitud().equals("") && (!g1.getLongitud().equals("") && !g2.getLongitud().equals("")))) {
                Location locg1 = new Location("");
                Location locg2 = new Location("");
                locg1.setLatitude(Double.parseDouble(g1.getLatitud().replace(',', '.')));
                locg1.setLongitude(Double.parseDouble(g1.getLongitud().replace(',', '.')));
                locg2.setLatitude(Double.parseDouble(g2.getLatitud().replace(',', '.')));
                locg2.setLongitude(Double.parseDouble(g2.getLongitud().replace(',', '.')));
                double dist1 = locAct.distanceTo(locg1);
                double dist2 = locAct.distanceTo(locg2);
                assertTrue(dist1 <= dist2);
            }
        }

        // UGIC3.d: Filtrar por la marca REPSOL con ordenación por ubicación, filtro de precio 1,80€
        // y filtro de combustible se selecciona Gasolina 95 E5 (normal95),
        // se comprueba que la lista sea de 17 gasolineras todas ellas de REPSOL,
        // con valor en el campo de la Gasolina 95 E5 y que estén ordenadas.
        prefs.putString("idComunidad", "");
        prefs.putString("tipoGasolina", "normal95");
        prefs.putString("ubicacion", "si");
        prefs.putString("maxPrecio", "1.80");
        prefs.putString("marca", "REPSOL");
        prefs.putString("latitud", "43.468732");
        prefs.putString("longitud", "-3.805011");
        presenter.init();
        verify(view, times(4)).showGasolineras(listaDevuelta.capture());
        assertEquals(17, listaDevuelta.getValue().size());
        for (Gasolinera g : listaDevuelta.getValue()) {
            assertEquals("REPSOL", g.getRotulo());
            assertNotEquals("", g.getNormal95());
        }
        for (int i = 0; i < listaDevuelta.getValue().size() - 1; i++) {
            Gasolinera g1 = listaDevuelta.getValue().get(i);
            Gasolinera g2 = listaDevuelta.getValue().get(i + 1);
            if ((!g1.getLatitud().equals("") && !g2.getLatitud().equals("") && (!g1.getLongitud().equals("") && !g2.getLongitud().equals("")))) {
                Location locg1 = new Location("");
                Location locg2 = new Location("");
                locg1.setLatitude(Double.parseDouble(g1.getLatitud().replace(',', '.')));
                locg1.setLongitude(Double.parseDouble(g1.getLongitud().replace(',', '.')));
                locg2.setLatitude(Double.parseDouble(g2.getLatitud().replace(',', '.')));
                locg2.setLongitude(Double.parseDouble(g2.getLongitud().replace(',', '.')));
                double dist1 = locAct.distanceTo(locg1);
                double dist2 = locAct.distanceTo(locg2);
                assertTrue(dist1 <= dist2);
            }
        }

        // UGIC3.e: Filtrar por la marca REPSOL, ordenación por ubicación y un precio
        // muy restrictivo, ninguna gasolinera cumple con la propiedad, lista vacía
        prefs.putString("idComunidad", "");
        prefs.putString("tipoGasolina", "");
        prefs.putString("ubicacion", "si");
        prefs.putString("maxPrecio", "1.0");
        prefs.putString("marca", "REPSOL");
        prefs.putString("latitud", "43.468732");
        prefs.putString("longitud", "-3.805011");
        presenter.init();
        verify(view, times(5)).showGasolineras(listaDevuelta.capture());
        assertEquals(0, listaDevuelta.getValue().size());

        // UGIC3.f: Filtrar por la marca REPSOL, ordenación por ubicación y un combustible que
        // no haya, Hidrógeno (h2), ninguna gasolinera cumple con la propiedad, lista vacía
        prefs.putString("idComunidad", "");
        prefs.putString("tipoGasolina", "h2");
        prefs.putString("ubicacion", "si");
        prefs.putString("maxPrecio", "");
        prefs.putString("marca", "REPSOL");
        prefs.putString("latitud", "43.468732");
        prefs.putString("longitud", "-3.805011");
        presenter.init();
        verify(view, times(6)).showGasolineras(listaDevuelta.capture());
        assertEquals(0, listaDevuelta.getValue().size());

        // UGIC3.g: Filtrar por la marca REPSOL, ordenación por ubicación, un precio
        // muy restrictivo y un combustible que no haya, Hidrógeno (h2),
        // ninguna gasolinera cumple con la propiedad, lista vacía
        prefs.putString("idComunidad", "");
        prefs.putString("tipoGasolina", "h2");
        prefs.putString("ubicacion", "si");
        prefs.putString("maxPrecio", "1.0");
        prefs.putString("marca", "REPSOL");
        prefs.putString("latitud", "43.468732");
        prefs.putString("longitud", "-3.805011");
        presenter.init();
        verify(view, times(7)).showGasolineras(listaDevuelta.capture());
        assertEquals(0, listaDevuelta.getValue().size());
    }
}
