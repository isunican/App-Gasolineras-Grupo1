package es.unican.is.appgasolineras.activities.listaFavoritas;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import es.unican.is.appgasolineras.model.Gasolinera;
import es.unican.is.appgasolineras.model.IDCCAAs;
import es.unican.is.appgasolineras.repository.IGasolinerasRepository;
import es.unican.is.appgasolineras.repository.db.GasolineraDao;
import es.unican.is.appgasolineras.repository.db.GasolineraDatabase;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class RoberTest {

    private GasolineraDatabase db;
    private IListaFavoritasContract.View view;
    private IGasolinerasRepository repository;
    private static ListaFavoritasPresenter presenter;
    private List<Gasolinera> listaFavoritasVarias;
    private List<Gasolinera> listaRepository;
    private List<Gasolinera> listaRepository2;
    private List<Gasolinera> listaFavoritasVacia;
    private List<Gasolinera> listaFavoritasUnaGasolinera;
    private GasolineraDao gDao;

    @Before
    public void inicializa() {
        listaFavoritasVacia = new ArrayList<>();
        listaFavoritasUnaGasolinera = new ArrayList<>();
        listaFavoritasVarias = new ArrayList<>();
        listaRepository = new ArrayList<>();
        listaRepository2 = new ArrayList<>();

        view = mock(IListaFavoritasContract.View.class);
        repository = mock(IGasolinerasRepository.class);

        db = mock(GasolineraDatabase.class);
        gDao = mock(GasolineraDao.class);
        when(db.gasolineraDao()).thenReturn(gDao);

        //gasolineras del repository
        Gasolinera g1 = new Gasolinera();
        Gasolinera g2 = new Gasolinera();
        Gasolinera g3 = new Gasolinera();

        g1.setId("1");
        g2.setId("2");
        g3.setId("3");
        g1.setNormal95("2.01");
        g2.setNormal95("2.02");
        g3.setNormal95("2.03");
        g1.setDieselA("2.11");
        g2.setDieselA("2.12");
        g3.setDieselA("2.13");

        g1.setIDCCAA(IDCCAAs.CANTABRIA.id);
        g2.setIDCCAA(IDCCAAs.CANTABRIA.id);
        g3.setIDCCAA(IDCCAAs.CANTABRIA.id);

        listaRepository.add(g1);
        listaRepository.add(g2);
        listaRepository.add(g3);
        listaRepository2.add(g1);
        listaRepository2.add(g2);
        listaRepository2.add(g3);

        when(repository.getGasolineras("06")).thenReturn(listaRepository);
        when(view.getGasolineraRepository()).thenReturn(repository);
        presenter = new ListaFavoritasPresenter(view, db, true);

        //gasolineras anhadidas a las listas
        Gasolinera g_1 = new Gasolinera();
        Gasolinera g_2 = new Gasolinera();
        Gasolinera g_3 = new Gasolinera();

        g_1.setId("1");
        g_2.setId("2");
        g_3.setId("3");
        g_1.setNormal95("1.01");
        g_2.setNormal95("1.02");
        g_3.setNormal95("1.03");
        g_1.setDieselA("1.11");
        g_2.setDieselA("1.12");
        g_3.setDieselA("1.13");
        g_1.setIDCCAA(IDCCAAs.CANTABRIA.id);
        g_2.setIDCCAA(IDCCAAs.CANTABRIA.id);
        g_3.setIDCCAA(IDCCAAs.CANTABRIA.id);

        listaFavoritasVarias.add(g_1);
        listaFavoritasVarias.add(g_2);
        listaFavoritasVarias.add(g_3);
        listaFavoritasUnaGasolinera.add(g_1);
    }

    @Test
    public void initListaFavoritasUnitariasTest() {
        ArgumentCaptor<List<Gasolinera>> listCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<Gasolinera>> listCaptor2 = ArgumentCaptor.forClass(List.class);
        List<Gasolinera> resultados;
        Gasolinera gasolineraResultado1;
        Gasolinera gasolineraResultado2;
        Gasolinera gasolineraResultado3;

        // Probamos sin gasolineras favoritas
        when(gDao.getAll()).thenReturn(listaFavoritasVacia);
        presenter.init();
        // Comprobamos que llama a getGasolineraRepository
        verify(view, times(1)).getGasolineraRepository();
        // Comprobamos que se llama a showLoadErrorVacia porque no hay gasolineras favoritas
        verify(view, times(1)).showLoadErrorDAOVacia();

        //con 1 gasolinera
        when(gDao.getAll()).thenReturn(listaFavoritasUnaGasolinera);
        presenter.init();
        // Comprobamos que llama a getGasolineraRepository
        verify(view, times(1)).getGasolineraRepository();
        verify(view, times(1)).showGasolineras(listCaptor.capture());

        resultados = listCaptor.getValue();
        gasolineraResultado1 = resultados.get(0);
        // Comprobar tamanho lista
        assertEquals(1, resultados.size());
        // Comprobar que el id es correcto
        assertEquals("1", gasolineraResultado1.getId());
        // Comprobar que se ha actualizado el precio de la gasolina
        assertEquals("2.01", gasolineraResultado1.getNormal95());
        // Comprobar que se ha actualizado el precio del diesel
        assertEquals("2.11", gasolineraResultado1.getDieselA());
        // Comprobar que la comunidad no ha cambiado
        assertEquals("06", gasolineraResultado1.getIDCCAA());

        when(repository.getGasolineras("06")).thenReturn(listaRepository2);
        //con 3 gasolineras (varias)
        when(gDao.getAll()).thenReturn(listaFavoritasVarias);
        presenter.init();
        // Comprobamos que llama a getGasolineraRepository
        verify(view, times(1)).getGasolineraRepository();
        verify(view, times(2)).showGasolineras(listCaptor2.capture());

        resultados = listCaptor2.getValue();
        gasolineraResultado1 = resultados.get(2);
        gasolineraResultado2 = resultados.get(1);
        gasolineraResultado3 = resultados.get(0);

        // Comprobar tamanho lista
        assertEquals(3, resultados.size());

        //Compruebo la primera gasolinera
        // Comprobar que el id es correcto
        assertEquals("1", gasolineraResultado1.getId());
        // Comprobar que se ha actualizado el precio de la gasolina
        assertEquals("2.01", gasolineraResultado1.getNormal95());
        // Comprobar que se ha actualizado el precio del diesel
        assertEquals("2.11", gasolineraResultado1.getDieselA());
        // Comprobar que la comunidad no ha cambiado
        assertEquals("06", gasolineraResultado1.getIDCCAA());

        //Compruebo la segunda gasolinera
        // Comprobar que el id es correcto
        assertEquals("2", gasolineraResultado2.getId());
        // Comprobar que se ha actualizado el precio de la gasolina
        assertEquals("2.02", gasolineraResultado2.getNormal95());
        // Comprobar que se ha actualizado el precio del diesel
        assertEquals("2.12", gasolineraResultado2.getDieselA());
        // Comprobar que la comunidad no ha cambiado
        assertEquals("06", gasolineraResultado2.getIDCCAA());

        //Compruebo la tercera gasolinera
        // Comprobar que el id es correcto
        assertEquals("3", gasolineraResultado3.getId());
        // Comprobar que se ha actualizado el precio de la gasolina
        assertEquals("2.03", gasolineraResultado3.getNormal95());
        // Comprobar que se ha actualizado el precio del diesel
        assertEquals("2.13", gasolineraResultado3.getDieselA());
        // Comprobar que la comunidad no ha cambiado
        assertEquals("06", gasolineraResultado3.getIDCCAA());
    }
}
