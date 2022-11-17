package es.unican.is.appgasolineras.activities.listaFavoritas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import es.unican.is.appgasolineras.model.Gasolinera;
import es.unican.is.appgasolineras.repository.IGasolinerasRepository;
import es.unican.is.appgasolineras.repository.db.GasolineraDao;
import es.unican.is.appgasolineras.repository.db.GasolineraDatabase;


public class jesusTest {

    @Mock
    private IListaFavoritasContract.View view; // Definición del objeto Mock
    private GasolineraDatabase db; // Definición del objeto Mock
    private IGasolinerasRepository repository;
    private ListaFavoritasPresenter sut;
    private GasolineraDao dao;



    @Before
    public void inicializa() {
        MockitoAnnotations.openMocks(this);
        view = mock(IListaFavoritasContract.View.class); // Creación del objeto Mock a partir de una interfaz
        db = mock(GasolineraDatabase.class);
        dao = mock(GasolineraDao.class);
        repository = mock(IGasolinerasRepository.class);
        when (view.getGasolineraRepository()).thenReturn(repository);

        List<Gasolinera> lista = new ArrayList<>();
        Gasolinera g1 = new Gasolinera();
        Gasolinera g2 = new Gasolinera();
        Gasolinera g3 = new Gasolinera();

        g1.setIDCCAA("06");
        g1.setId("1");
        g2.setIDCCAA("06");
        g2.setId("2");
        g3.setIDCCAA("06");
        g3.setId("3");


        lista.add(g1);
        lista.add(g2);
        lista.add(g3);

        when (repository.getGasolineras("06")).thenReturn(lista);

        List<Gasolinera> lista1 = new ArrayList<>();
        Gasolinera g4 = new Gasolinera();
        Gasolinera g5 = new Gasolinera();
        Gasolinera g6 = new Gasolinera();


        g4.setIdMun("1");
        g4.setId("4");
        g5.setIdMun("1");
        g5.setId("5");
        g6.setIdMun("1");
        g6.setId("6");

        lista1.add(g4);
        lista1.add(g5);
        lista1.add(g6);

        when (repository.gasolinerasMunicipio("1")).thenReturn(lista1);


        List<Gasolinera> lista2 = new ArrayList<>();
        Gasolinera g7 = new Gasolinera();
        g7.setId("7");
        Gasolinera g8 = new Gasolinera();
        g8.setId("8");
        Gasolinera g9 = new Gasolinera();
        g9.setId("");

        g7.setIdMun("1000");
        g8.setIdMun("1000");
        g9.setIdMun("1000");

        lista2.add(g7);
        lista2.add(g8);
        lista2.add(g9);

        when (repository.gasolinerasMunicipio("1000")).thenReturn(lista2);

        List<Gasolinera> lista3 = new ArrayList<>();
        Gasolinera g10 = new Gasolinera();
        Gasolinera g11 = new Gasolinera();
        Gasolinera g12 = new Gasolinera();

        g10.setIDCCAA("06");
        g10.setId("10");
        g11.setIDCCAA("06");
        g11.setId("11");
        g12.setIDCCAA("06");
        g12.setId("12");

        lista3.add(g10);
        lista3.add(g11);
        lista3.add(g12);

        when(db.gasolineraDao()).thenReturn(dao);

        sut = new ListaFavoritasPresenter(view, db, true);

    }

    @Test
    public void conseguirGasolinerasActualizadas() {

        //Caso 1
        Boolean MasDeUna = false;
        String id = "06";
        List<Gasolinera> listaFav = new ArrayList<>();

        sut.init();
        assertEquals(sut.conseguirGasolinerasActualizadas(MasDeUna, listaFav, id), repository.getGasolineras("06"));


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

        assertEquals(sut.conseguirGasolinerasActualizadas(MasDeUna, listaFav, id), listaDef);


        //Caso 3
        MasDeUna = true;
        listaFav.clear();
        id = "06";

        assertEquals(sut.conseguirGasolinerasActualizadas(MasDeUna, listaFav, id).size(), 0);

        //Caso 4

        MasDeUna = false;
        id = null;

        assertEquals(sut.conseguirGasolinerasActualizadas(MasDeUna, listaFav, id).size(), 0);

    }
}
