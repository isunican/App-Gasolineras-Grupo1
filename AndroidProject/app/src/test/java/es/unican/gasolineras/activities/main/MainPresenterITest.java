package es.unican.gasolineras.activities.main;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static es.unican.gasolineras.Utils.MockRepositories.getEmptyRepository;
import static es.unican.gasolineras.Utils.MockRepositories.getFailureRepository;
import static es.unican.gasolineras.Utils.MockRepositories.getTestRepository;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.unican.gasolineras.R;
import es.unican.gasolineras.common.FuelTypeEnum;
import es.unican.gasolineras.common.IFilter;
import es.unican.gasolineras.common.database.IGasStationsDAO;
import es.unican.gasolineras.common.database.MyFuelDatabase;
import es.unican.gasolineras.model.Filter;
import es.unican.gasolineras.model.Gasolinera;
import es.unican.gasolineras.model.OrderByPrice;
import es.unican.gasolineras.repository.IGasolinerasRepository;
import es.unican.gasolineras.Utils.MockRepositories;

@RunWith(RobolectricTestRunner.class)
public class MainPresenterITest {

    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    final IGasolinerasRepository repository = MockRepositories.getTestRepository(context, R.raw.gasolineras_ccaa_06);
    final IGasolinerasRepository repository2 = MockRepositories.getTestRepository(context, R.raw.gasolineras_test_505739);
    final IGasolinerasRepository repository3 = MockRepositories.getTestRepository(context, R.raw.gasolineras_filtro_tipo_test);

    private IGasolinerasRepository repo;

    @Mock
    private IMainContract.View view;
    @Mock
    private IMainContract.View mockMainView;
    @Mock
    private IMainContract.View mockMainView2;

    private IMainContract.Presenter sut;

    private IGasStationsDAO gasStationsDAO;

    @Captor
    ArgumentCaptor<List<Gasolinera>> listCaptor;

    @Before
    public void setUp() {
        MyFuelDatabase db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),MyFuelDatabase.class)
                .allowMainThreadQueries().build();
        gasStationsDAO = db.getGasStationsDAO();

        MockitoAnnotations.openMocks(this);
        when(view.getGasolinerasRepository()).thenReturn(repository);
        when(mockMainView.getGasolinerasRepository()).thenReturn(repository2);
        when(mockMainView2.getGasolinerasRepository()).thenReturn(repository3);
        when(view.getGasolinerasDAO()).thenReturn(gasStationsDAO);
        when(mockMainView.getGasolinerasDAO()).thenReturn(gasStationsDAO);
        when(mockMainView2.getGasolinerasDAO()).thenReturn(gasStationsDAO);
        sut = new MainPresenter();
        sut.init(view);


    }

    @Test
    public void onFiltersPopUpClearFiltersClickedInitializedITest() {
        sut.setTempFilter(new Filter()
                .setMaxPrice(1.8F)
        );
        when(view.getConstantString(R.string.all_selections)).thenReturn("Todos");
        sut.onFiltersPopUpClearFiltersClicked();
        verify(view).updateFiltersPopupTextViewsSelections("Todos", "Todos");
        IFilter f = sut.getTempFilter();
        assertEquals(Float.MAX_VALUE, (float) f.getMaxPrice(), 0.0);
    }

    @Test
    public void onFiltersPopUpClearFiltersClickedNoInitializedITest() {
        sut.setTempFilter(new Filter());
        when(view.getConstantString(R.string.all_selections)).thenReturn("Todos");
        sut.onFiltersPopUpClearFiltersClicked();
        verify(view).updateFiltersPopupTextViewsSelections("Todos", "Todos");
        IFilter f = sut.getTempFilter();
        assertEquals(Float.MAX_VALUE, (float) f.getMaxPrice(), 0.0);
    }

    @Test
    public void testOnOrderPopUpAcceptClicked() {

        sut = new MainPresenter();
        sut.init(mockMainView);

        //ID1.a
        sut.setOrderByPrice(createOrderByPrice(FuelTypeEnum.GASOLINA_95E5, true));
        sut.setTempFilter(new Filter());

        sut.onOrderPopUpAcceptClicked();

        verify(mockMainView,times (2)).showStations(listCaptor.capture());
        assertEquals("13606", listCaptor.getValue().get(0).getId());
        assertEquals("1048", listCaptor.getValue().get(3).getId());
        verify(mockMainView, times (2)).showLoadCorrect(eq(7));
        verify(mockMainView, times(1)).closeActivePopUp();

        //ID1.b
        sut.setOrderByPrice(createOrderByPrice(FuelTypeEnum.GASOLEO_A, false));
        sut.setTempFilter(new Filter());

        sut.onOrderPopUpAcceptClicked();

        verify(mockMainView,times (3)).showStations(listCaptor.capture());
        assertEquals("1048", listCaptor.getValue().get(0).getId());
        assertEquals("1039", listCaptor.getValue().get(3).getId());
        verify(mockMainView, times (3)).showLoadCorrect(eq(7));
        verify(mockMainView, times(2)).closeActivePopUp();
    }

    private OrderByPrice createOrderByPrice(FuelTypeEnum fuelType, Boolean ascending) {
        OrderByPrice orderByPrice = new OrderByPrice();
        if (fuelType != null) {
            orderByPrice.setFuelType(fuelType);
        }
        if (ascending != null) {
            orderByPrice.setAscending(ascending);
        }
        return orderByPrice;
    }

    @Test
    public void onFiltersPopUpAcceptClickedGasolina95E5() {
        sut  = new MainPresenter();
        sut.init(mockMainView2);

        // Gasolineras filtradas por Gasolina95E5
        String[] rotulos = {"CEPSA", "REPSOL", "PETRONOR", "PETRONOR V2", "GALP"};
        sut.setTempFilter(new Filter()
                .setFuelTypes(Collections.singletonList(FuelTypeEnum.GASOLINA_95E5))
        );
        sut.onFiltersPopUpAcceptClicked();
        verify(mockMainView2).closeActivePopUp();
        // Comprobamos los metodos internos de load() con las gasolineras ya filtradas
        // Son 2 veces porque "sut.init(view);" llama al load()
        verify(mockMainView2, times(1)).getGasolinerasRepository();
        verify(mockMainView2).showLoadCorrect(rotulos.length);
        verify(mockMainView2, times(2)).showStations(listCaptor.capture());
        for (int i = 0; i < rotulos.length; i++) {
            Assert.assertEquals(rotulos[i], listCaptor.getValue().get(i).getRotulo());
        }
        IFilter f = sut.getFilter();
        Assert.assertEquals(1, f.getFuelTypes().size());
        Assert.assertEquals(FuelTypeEnum.GASOLINA_95E5, f.getFuelTypes().get(0));
        IFilter tmpF = sut.getTempFilter();
        Assert.assertNull(tmpF);
        Assert.assertNull(sut.getTempListSelection());
    }

    @Test
    public void onFiltersPopUpAcceptClickedGasoleoA() {
        sut  = new MainPresenter();
        sut.init(mockMainView2);
        // Gasolineras filtradas por GasoleoA
        String[] rotulos = {"CEPSA", "REPSOL", "PETRONOR", "PETRONOR V2", "REDETRANS"};
        sut.setTempFilter(new Filter()
                .setFuelTypes(Collections.singletonList(FuelTypeEnum.GASOLEO_A))
        );
        sut.onFiltersPopUpAcceptClicked();
        verify(mockMainView2).closeActivePopUp();
        // Comprobamos los metodos internos de load() con las gasolineras ya filtradas
        // Son 2 veces porque "sut.init(view);" llama al load()
        verify(mockMainView2, times(1)).getGasolinerasRepository();
        verify(mockMainView2).showLoadCorrect(rotulos.length);
        verify(mockMainView2, times(2)).showStations(listCaptor.capture());
        for (int i = 0; i < rotulos.length; i++) {
            Assert.assertEquals(rotulos[i], listCaptor.getValue().get(i).getRotulo());
        }
        IFilter f = sut.getFilter();
        Assert.assertEquals(1, f.getFuelTypes().size());
        Assert.assertEquals(FuelTypeEnum.GASOLEO_A, f.getFuelTypes().get(0));
        // TempFilter = null
        IFilter tmpF = sut.getTempFilter();
        Assert.assertNull(tmpF);
        // tempListSelection = null
        Assert.assertNull(sut.getTempListSelection());
    }

    @Test
    public void onFiltersPopUpAcceptClickedGasolina95E5GasoleoA() {
        sut  = new MainPresenter();
        sut.init(mockMainView2);
        // Gasolineras filtradas por gasolina95E5 y gasoleoA
        String[] rotulos = {"CEPSA", "REPSOL", "PETRONOR", "PETRONOR V2"};
        sut.setTempFilter(new Filter()
                .setFuelTypes(Arrays.asList(FuelTypeEnum.GASOLEO_A, FuelTypeEnum.GASOLINA_95E5))
        );
        sut.onFiltersPopUpAcceptClicked();
        verify(mockMainView2).closeActivePopUp();
        // Son 2 veces porque "sut.init(view);" llama al load()
        verify(mockMainView2, times(1)).getGasolinerasRepository();
        verify(mockMainView2, times(2)).showLoadCorrect(6);
        verify(mockMainView2, times(2)).showStations(listCaptor.capture());
        for (int i = 0; i < rotulos.length; i++) {
            Assert.assertEquals(rotulos[i], listCaptor.getValue().get(i).getRotulo());
        }
        IFilter f = sut.getFilter();
        Assert.assertEquals(Arrays.asList(FuelTypeEnum.GASOLEO_A, FuelTypeEnum.GASOLINA_95E5), f.getFuelTypes());
        // TempFilter = null
        IFilter tmpF = sut.getTempFilter();
        Assert.assertNull(tmpF);
        // tempListSelection = null
        Assert.assertNull(sut.getTempListSelection());
    }

    // IP2.a
    @Test
    public void onLoadWithConnectionAndNoPersistenceSuccess() {
        MockitoAnnotations.openMocks(this);
        // Obtenemos el repositorio de las gasolineras, con el getTestRepository (llamada al onSuccess).
        repo = getTestRepository(context, R.raw.gasolineras_us509051_lista2);
        when(mockMainView.getGasolinerasRepository()).thenReturn(repo);
        when(mockMainView.getGasolinerasDAO()).thenReturn(gasStationsDAO);

        sut = new MainPresenter();
        gasStationsDAO.deleteAll();
        sut.init(mockMainView);

        assertEquals("CEPSA", gasStationsDAO.getAll().get(0).getRotulo());
        assertEquals("REPSOL", gasStationsDAO.getAll().get(1).getRotulo());

        verify(mockMainView).updateLocalDBDateRegister();

        ArgumentCaptor<List<Gasolinera>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockMainView).showStations(captor.capture());
        List<Gasolinera> mostradas = captor.getValue();
        assertEquals(2, mostradas.size());
        assertEquals("CEPSA", mostradas.get(0).getRotulo());
        assertEquals("REPSOL", mostradas.get(1).getRotulo());

        verify(mockMainView).showLoadCorrect(2);

    }

    // Caso IP2.b
    @Test
    public void onLoadWithConnectionAndPersistenceBeforeSuccess() {

        // Creamos las gasolineras con su marca y las metemos en la DAO.
        Gasolinera g1 = new Gasolinera();
        g1.setId("1");
        g1.setRotulo("PETROPRIX");
        Gasolinera g2 = new Gasolinera();
        g1.setId("2");
        g2.setRotulo("BALLENOIL");
        gasStationsDAO.deleteAll();
        gasStationsDAO.addGasStation(g1);
        gasStationsDAO.addGasStation(g2);

        MockitoAnnotations.openMocks(this);
        // Obtenemos el repositorio de las gasolineras, con el getTestRepository (llamada al onSuccess).
        repo = getTestRepository(context, R.raw.gasolineras_us509051_lista2);
        when(mockMainView.getGasolinerasRepository()).thenReturn(repo);
        when(mockMainView.getGasolinerasDAO()).thenReturn(gasStationsDAO);

        sut = new MainPresenter();
        sut.init(mockMainView);

        assertEquals("CEPSA", gasStationsDAO.getAll().get(0).getRotulo());
        assertEquals("REPSOL", gasStationsDAO.getAll().get(1).getRotulo());

        verify(mockMainView).updateLocalDBDateRegister();

        ArgumentCaptor<List<Gasolinera>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockMainView).showStations(captor.capture());
        List<Gasolinera> mostradas = captor.getValue();
        assertEquals(2, mostradas.size());
        assertEquals("CEPSA", mostradas.get(0).getRotulo());
        assertEquals("REPSOL", mostradas.get(1).getRotulo());

        verify(mockMainView).showLoadCorrect(2);

    }

    // Caso UP1.c
    @Test
    public void onLoadWithoutConnectionAndWithPersistenceOnFailure() {

        // Creamos las gasolineras con su marca y las metemos en la DAO.
        Gasolinera g1 = new Gasolinera();
        g1.setId("1");
        g1.setRotulo("PETROPRIX");
        Gasolinera g2 = new Gasolinera();
        g1.setId("2");
        g2.setRotulo("BALLENOIL");
        gasStationsDAO.deleteAll();
        gasStationsDAO.addGasStation(g1);
        gasStationsDAO.addGasStation(g2);

        MockitoAnnotations.openMocks(this);
        // Obtenemos el repositorio con llamada en onfailure
        repo = getFailureRepository();
        when(mockMainView.getGasolinerasRepository()).thenReturn(repo);
        when(mockMainView.getGasolinerasDAO()).thenReturn(gasStationsDAO);

        // Simulamos la fecha de ultima actualizacion.
        String simulatedDate = "17/11/2024";
        when(mockMainView.getLocalDBDateRegister()).thenReturn(simulatedDate);

        sut = new MainPresenter();
        sut.init(mockMainView);

        // Verificamos que no se actualiza la BBDD
        verify(mockMainView, never()).updateLocalDBDateRegister();
        assertEquals("PETROPRIX", gasStationsDAO.getAll().get(0).getRotulo());
        assertEquals("BALLENOIL", gasStationsDAO.getAll().get(1).getRotulo());

        ArgumentCaptor<List<Gasolinera>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockMainView).showStations(captor.capture());
        List<Gasolinera> mostradas = captor.getValue();
        assertEquals(2, mostradas.size());  // Aseguramos que la lista tiene dos elementos
        assertEquals("PETROPRIX", mostradas.get(0).getRotulo());
        assertEquals("BALLENOIL", mostradas.get(1).getRotulo());

        // Verificamos que se muestra el mensaje correcto.
        String expectedMessage = "Cargadas 2 gasolineras en fecha " + simulatedDate;
        verify(mockMainView).showInfoMessage(expectedMessage);

    }

    // Caso IP2.d
    @Test
    public void onLoadWithoutConnectionAndNoPersistenceOnFailure() {
        // Vaciamos la DAO
        gasStationsDAO.deleteAll();

        MockitoAnnotations.openMocks(this);

        // Obtemos el repositorio que llama al onfailure
        repo = getFailureRepository();
        when(mockMainView.getGasolinerasRepository()).thenReturn(repo);
        when(mockMainView.getGasolinerasDAO()).thenReturn(gasStationsDAO);

        sut = new MainPresenter();
        sut.init(mockMainView);

        // Verificamos que no se actualiza la BBDD
        verify(mockMainView, never()).updateLocalDBDateRegister();
        assertEquals(0, gasStationsDAO.getAll().size());

        ArgumentCaptor<List<Gasolinera>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockMainView).showStations(captor.capture());
        List<Gasolinera> mostradas = captor.getValue();
        assertEquals(0, mostradas.size());

        // Verificamos que se llama a view.showInfoMessage()
        String expectedMessage = "No hay datos guardados de gasolineras";
        verify(mockMainView).showInfoMessage(expectedMessage);
    }

    // Caso IP2.e, no se hace por los errores en BBDD.

    // Caso UP1.f, no se hace por los errores en BBDD.

    // IP2.g
    @Test
    public void onLoadWithEmptyDatabaseAndEmptyAPIResponse() {
        // Vaciamos la DAO
        gasStationsDAO.deleteAll();

        MockitoAnnotations.openMocks(this);

        // Obtenemos el repositorio vacio
        repo = getEmptyRepository();
        when(mockMainView.getGasolinerasRepository()).thenReturn(repo);
        when(mockMainView.getGasolinerasDAO()).thenReturn(gasStationsDAO);

        // Inicializamos el presenter
        sut.init(mockMainView);

        // Verificamos que se llama a updateLocalDBDateRegister() para actualizar la fecha de la base de datos
        verify(mockMainView).updateLocalDBDateRegister();

        // Verificamos que se llama a showStations con una lista vacía
        verify(mockMainView).showStations(new ArrayList<Gasolinera>());  // Lista vacía

        // Verificamos que se muestra el mensaje con error de carga, ya que muestra 0 gasolineras.
        verify(mockMainView).showLoadError();

    }

    // IP2.h
    @Test
    public void onLoadWithDatabaseAndEmptyAPIResponse() {
        // Creamos las gasolineras con su marca y las metemos en la DAO.
        Gasolinera g1 = new Gasolinera();
        g1.setId("1");
        g1.setRotulo("PETROPRIX");
        Gasolinera g2 = new Gasolinera();
        g1.setId("2");
        g2.setRotulo("BALLENOIL");
        gasStationsDAO.deleteAll();
        gasStationsDAO.addGasStation(g1);
        gasStationsDAO.addGasStation(g2);

        // Obtenemos el repositorio vacio
        repo = getEmptyRepository();
        when(mockMainView.getGasolinerasRepository()).thenReturn(repo);
        when(mockMainView.getGasolinerasDAO()).thenReturn(gasStationsDAO);

        // Inicializamos el presenter
        sut.init(mockMainView);

        // Verificamos que se vacia la dao
        verify(mockMainView).updateLocalDBDateRegister();
        assertEquals(0, gasStationsDAO.getAll().size());

        // Verificamos que se llama a view.showStations() con la lista vacia
        ArgumentCaptor<List<Gasolinera>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockMainView).showStations(captor.capture());
        List<Gasolinera> mostradas = captor.getValue();
        assertEquals(0, mostradas.size());

        // Verificamos que se muestra el mensaje con error de carga, ya que muestra 0 gasolineras.
        verify(mockMainView).showLoadError();

    }




}
