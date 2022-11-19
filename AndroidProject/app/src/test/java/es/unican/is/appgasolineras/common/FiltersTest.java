package es.unican.is.appgasolineras.common;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


import es.unican.is.appgasolineras.common.prefs.IPrefs;
import es.unican.is.appgasolineras.model.Gasolinera;
import es.unican.is.appgasolineras.repository.IGasolinerasRepository;

public class FiltersTest {
    private static IPrefs mockPrefs;
    private ArrayList<Gasolinera> listGasolineras;
    private ArrayList<Gasolinera> listaGasolineras;
    private ArrayList<Gasolinera> listGasolinerasVacia;
    private Gasolinera gas;
    private static IGasolinerasRepository mockGasolineras;

    private static final String TIPOGASOLINA = "tipoGasolina";


    @Before
    public void inicializa() {
        mockGasolineras = mock(IGasolinerasRepository.class);
        mockPrefs = mock(IPrefs.class);

        when(mockPrefs.getString(TIPOGASOLINA)).thenReturn("dieselA");

        listGasolineras = new ArrayList<>();
        listGasolinerasVacia = new ArrayList<>();

        Gasolinera g = new Gasolinera();
        Gasolinera g2 = new Gasolinera();
        Gasolinera g3 = new Gasolinera();
        Gasolinera g4 = new Gasolinera();
        Gasolinera g5 = new Gasolinera();
        Gasolinera g6 = new Gasolinera();
        Gasolinera g7 = new Gasolinera();
        Gasolinera g8 = new Gasolinera();
        Gasolinera g9 = new Gasolinera();
        Gasolinera g10 = new Gasolinera();
        Gasolinera g11 = new Gasolinera();
        Gasolinera g12 = new Gasolinera();
        Gasolinera g13 = new Gasolinera();
        Gasolinera g14 = new Gasolinera();
        Gasolinera g15 = new Gasolinera();

        g.setId("1");
        g2.setId("2");
        g3.setId("3");
        g4.setId("4");
        g5.setId("5");
        g6.setId("6");
        g7.setId("7");
        g8.setId("8");
        g9.setId("9");
        g10.setId("10");
        g11.setId("11");
        g12.setId("12");
        g13.setId("13");
        g14.setId("14");
        g15.setId("15");
        g.setDieselA("1,99");
        g.setNormal95("1,00");
        g.setRotulo("REPSOL");
        g2.setDieselA("2,02");
        g2.setNormal95("1,00");
        g2.setRotulo("REPSOL");
        g3.setDieselA("1,99");
        g3.setNormal95("1,00");
        g3.setRotulo("REPSOL");
        g4.setDieselA("2,02");
        g4.setNormal95("1,00");
        g4.setRotulo("REPSOL");
        g5.setDieselA("1,99");
        g5.setNormal95("1,00");
        g5.setRotulo("REPSOL");
        g6.setDieselA("2,02");
        g6.setNormal95("1,00");
        g6.setRotulo("REPSOL");
        g7.setDieselA("1,99");
        g7.setNormal95("1,00");
        g7.setRotulo("REPSOL");
        g8.setDieselA("2,02");
        g8.setNormal95("1,00");
        g8.setRotulo("REPSOL");
        g9.setDieselA("1,99");
        g9.setNormal95("1,00");
        g9.setRotulo("REPSOL");
        g10.setDieselA("2,02");
        g10.setNormal95("1,00");
        g10.setRotulo("REPSOL");
        g11.setDieselA("1,99");
        g11.setNormal95("1,00");
        g11.setRotulo("REPSOL");
        g12.setDieselA("2,02");
        g12.setNormal95("1,00");
        g12.setRotulo("CAMPSA");
        g13.setDieselA("1,99");
        g13.setNormal95("1,00");
        g13.setRotulo("CAMPSA");
        g14.setDieselA("2,02");
        g14.setNormal95("1,00");
        g14.setRotulo("PETRONOR");
        g15.setDieselA("1,99");
        g15.setNormal95("1,00");
        g15.setRotulo("PETRONOR");


        listGasolineras.add(g);
        listGasolineras.add(g2);
        listGasolineras.add(g3);
        listGasolineras.add(g4);
        listGasolineras.add(g5);
        listGasolineras.add(g6);
        listGasolineras.add(g7);
        listGasolineras.add(g8);
        listGasolineras.add(g9);
        listGasolineras.add(g10);
        listGasolineras.add(g11);
        listGasolineras.add(g12);
        listGasolineras.add(g13);
        listGasolineras.add(g14);
        listGasolineras.add(g15);

    }

    @Test
    public void filtraPrecioTest() {
        assertEquals(listGasolineras,
                Filters.filtraPrecio(listGasolineras, "2.04", ""));
        assertEquals(15,
                Filters.filtraPrecio(listGasolineras, "2.00", "").size());
        assertEquals(listGasolinerasVacia.size(),
                Filters.filtraPrecio(listGasolineras, "-2.04", "").size());
        assertEquals(listGasolinerasVacia.size(),
                Filters.filtraPrecio(listGasolinerasVacia, "2.04", "").size());
    }

    @Test
    public void maximoEntreTodasTest() {
        assertEquals("2.02", Filters.maximoEntreTodas(listGasolineras, ""));
        assertEquals("0.00", Filters.maximoEntreTodas(listGasolinerasVacia, ""));
    }

    @Test
    public void filtraTipoTestUnitarias() {

        // Caso de prueba UGIC 1.a
        //Creamos la lista de gasolineras
        listaGasolineras = new ArrayList<>();
        List<Gasolinera> listDevueltas = new ArrayList<>();
        gas = new Gasolinera();
        gas.setId("1");
        gas.setDieselA("1,97");
        listaGasolineras.add(gas);
        listDevueltas.add(gas);
        gas = new Gasolinera();
        gas.setId("2");
        gas.setNormal95("1,81");
        gas.setDieselA("");
        listaGasolineras.add(gas);
        gas = new Gasolinera();
        gas.setId("3");
        gas.setDieselA("2,01");
        listaGasolineras.add(gas);
        listDevueltas.add(gas);
        gas = new Gasolinera();
        gas.setId("4");
        gas.setNormal95Prem("1,91");
        gas.setDieselA("");
        listaGasolineras.add(gas);

        when(mockGasolineras.todasGasolineras())
                .thenReturn(listaGasolineras);

        // Caso de prueba UGIC 1.a
        assertEquals(listDevueltas, Filters.filtraTipo(mockGasolineras.todasGasolineras(),
                "dieselA"));

        // Caso de prueba UGIC 1.c
        listaGasolineras = new ArrayList<>();
        listDevueltas = new ArrayList<>();
        gas = new Gasolinera();
        gas.setId("1");
        gas.setDieselA("1,97");
        gas.setDieselB("");
        listaGasolineras.add(gas);
        gas = new Gasolinera();
        gas.setId("2");
        gas.setNormal95("1,81");
        gas.setDieselB("");
        listaGasolineras.add(gas);
        gas = new Gasolinera();
        gas.setId("3");
        gas.setDieselA("2,01");
        gas.setDieselB("");
        listaGasolineras.add(gas);
        gas = new Gasolinera();
        gas.setId("4");
        gas.setNormal95Prem("1,91");
        gas.setDieselB("");
        listaGasolineras.add(gas);

        when(mockGasolineras.todasGasolineras())
                .thenReturn(listaGasolineras);

        assertEquals(listDevueltas, Filters.filtraTipo(mockGasolineras.todasGasolineras(),
                "dieselB"));
        assertEquals(listDevueltas.size(), Filters.filtraTipo(mockGasolineras.todasGasolineras(),
                "dieselB").size());

        // Caso de prueba UGIC 1.d
        listaGasolineras = new ArrayList<>();
        listDevueltas = new ArrayList<>();
        gas = new Gasolinera();
        gas.setId("1");
        gas.setDieselA("1,97");
        listaGasolineras.add(gas);
        listDevueltas.add(gas);
        gas = new Gasolinera();
        gas.setId("2");
        gas.setNormal95("1,81");
        listaGasolineras.add(gas);
        listDevueltas.add(gas);
        gas = new Gasolinera();
        gas.setId("3");
        gas.setDieselA("2,01");
        listaGasolineras.add(gas);
        listDevueltas.add(gas);
        gas = new Gasolinera();
        gas.setId("4");
        gas.setNormal95Prem("1,91");
        listaGasolineras.add(gas);
        listDevueltas.add(gas);

        when(mockGasolineras.todasGasolineras())
                .thenReturn(listaGasolineras);

        assertEquals(listDevueltas, Filters.filtraTipo(mockGasolineras.todasGasolineras(),
                ""));
    }

    /**
     * Test unitario implementado por Mario Ingelmo para probar el método de Filters, filtraMarca
     * Prueba determinada en el documento US465234-BuscarGasolinerasPorMarca-TestPlan
     */
    @Test
    public void FiltraMarcaTest() {
        List<Gasolinera> listaFiltrada;

        // Comenzamos con el caso de prueba UT.1a
        // La lista a filtrar contiene 13 gasolineras, 11 de ellas de la marca REPSOL,
        // 2 de CAMPSA y 2 de PETRONOR por lo que el tamaño de la lista resultante debería ser 11
        listaFiltrada = Filters.filtraMarca(listGasolineras, "REPSOL");
        assertEquals(11, listaFiltrada.size());
        // Tambien recorreremos la lista para comprobar que es correcto
        for (Gasolinera g : listaFiltrada) {
            assertEquals("REPSOL", g.getRotulo());
        }


        // Caso de prueba UT.1b
        // La lista a filtrar contiene 13 gasolineras, 11 de REPSOL, 2 de CAMPSA y 2 de PETRONOR
        // por lo que al filtrar por marca usando CEPSA, la lista debería estar vacía
        listaFiltrada = Filters.filtraMarca(listGasolineras, "CEPSA");
        assertEquals(0, listaFiltrada.size());
    }
}
