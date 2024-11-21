package es.unican.gasolineras.activities.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static es.unican.gasolineras.Utils.MockRepositories.getEmptyRepository;
import static es.unican.gasolineras.Utils.MockRepositories.getFailureRepository;
import static es.unican.gasolineras.Utils.MockRepositories.getTestRepository;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.unican.gasolineras.R;
import es.unican.gasolineras.common.BrandsEnum;
import es.unican.gasolineras.common.IFilter;
import es.unican.gasolineras.common.database.IGasStationsDAO;
import es.unican.gasolineras.model.Filter;
import es.unican.gasolineras.model.Gasolinera;
import es.unican.gasolineras.repository.IGasolinerasRepository;

@RunWith(RobolectricTestRunner.class)
public class MainPresenterTest {

    private MainPresenter presenter;

    @Mock
    private IMainContract.View mockView;
    @Mock
    private List<Selection> mockTempListSelection;

    @Mock
    private IGasStationsDAO mockGasStationsDAO;

    @Mock
    private List<Selection> mockTempListType;

    Context context = ApplicationProvider.getApplicationContext();
    private IGasolinerasRepository repository;

    @Before
    public void setUp() {

        MockitoAnnotations.openMocks(this);
        presenter = new MainPresenter();

        repository = getTestRepository(context, R.raw.gasolineras_ccaa_06);

        when(mockView.getGasolinerasRepository()).thenReturn(repository);
        when(mockView.getGasolinerasDAO()).thenReturn(mockGasStationsDAO);
        when(mockView.getConstantString(R.string.all_selections)).thenReturn("Todos");

        presenter.init(mockView);

        mockTempListSelection = new ArrayList<>(Arrays.asList(
                new Selection("Todos", true),
                new Selection("Marca1", false),
                new Selection("Marca2", false)
        ));

       //  Inicializa la lista de selecciones de marcas
        presenter.setTempListSelection(mockTempListSelection);
    }

    //No hago nada, de seleccionado el "TODOS"
    @Test
    public void testOnFiltersPopUpBrandsOneSelected_AllSelected() {
        // Caso: Se selecciona "Todos"
        presenter.onFiltersPopUpBrandsOneSelected(0, true);

        for (int i = 1; i < mockTempListSelection.size(); i++) {
            verify(mockView).updateFiltersPopUpSelection(i, false);
        }

        List<Selection> listaPrueba = presenter.getTempListSelection();
        assertTrue(listaPrueba.get(0).isSelected());
        assertFalse(listaPrueba.get(1).isSelected());
        assertFalse(listaPrueba.get(2).isSelected());
    }


    //Selecciono una marca
    @Test
    public void testOnFiltersPopUpBrandsOneSelected_OneSelected() {
        // Caso: Se selecciona "Todos"
        presenter.onFiltersPopUpBrandsOneSelected(0, true);
        presenter.onFiltersPopUpBrandsOneSelected(1, true);

        verify(mockView).updateFiltersPopUpSelection(0, false);

        List<Selection> listaPrueba = presenter.getTempListSelection();
        assertFalse(listaPrueba.get(0).isSelected());
        assertTrue(listaPrueba.get(1).isSelected());
        assertFalse(listaPrueba.get(2).isSelected());

    }

    //Seleccionas las dos marcas y por eso se desmarcan y se marca "TODOS"
    @Test
    public void testOnFiltersPopUpBrandsOneSelected_SelectAllBrands() {
        // Caso: Se selecciona "Todos"
        presenter.onFiltersPopUpBrandsOneSelected(1, true);
        presenter.onFiltersPopUpBrandsOneSelected(2, true);

        verify(mockView).updateFiltersPopUpSelection(1, false);
        verify(mockView).updateFiltersPopUpSelection(2, false);

        List<Selection> listaPrueba = presenter.getTempListSelection();
        assertTrue(listaPrueba.get(0).isSelected());
        assertFalse(listaPrueba.get(1).isSelected());
        assertFalse(listaPrueba.get(2).isSelected());

    }

    //Intento desmarcar el "TODOS"
    @Test
    public void testOnFiltersPopUpBrandsOneSelected_UnselectAll() {
        // Caso: Se selecciona "Todos"
        presenter.onFiltersPopUpBrandsOneSelected(0, true);
        presenter.onFiltersPopUpBrandsOneSelected(0, false);

        verify(mockView).updateFiltersPopUpSelection(0, true);

        List<Selection> listaPrueba = presenter.getTempListSelection();
        assertTrue(listaPrueba.get(0).isSelected());
        assertFalse(listaPrueba.get(1).isSelected());
        assertFalse(listaPrueba.get(2).isSelected());

    }


    //Desmarco uno y se marca el "TODOS"
    @Test
    public void testOnFiltersPopUpBrandsOneSelected_UnselectOne() {
        // Caso: Se selecciona "Todos"
        presenter.onFiltersPopUpBrandsOneSelected(0, true);
        presenter.onFiltersPopUpBrandsOneSelected(1, true);
        presenter.onFiltersPopUpBrandsOneSelected(1, false);


        verify(mockView).updateFiltersPopUpSelection(0, true);
        verify(mockView).updateFiltersPopUpSelection(1, false);

        List<Selection> listaPrueba = presenter.getTempListSelection();
        assertTrue(listaPrueba.get(0).isSelected());
        assertFalse(listaPrueba.get(1).isSelected());
        assertFalse(listaPrueba.get(2).isSelected());

    }



    // Tests para onFiltersPopUpBrandsSelected()

    // Caso UGIC.2a: tempList = ["Marca1"]
    @Test
    public void testOnFiltersPopUpBrandsSelected_UGIC_2a() {
        IFilter f = new Filter()
                .setGasBrands(Collections.singletonList(BrandsEnum.REPSOL));
        presenter.setTempFilter(f);

        presenter.onFiltersPopUpBrandsSelected();



        ArgumentCaptor<List<Selection>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).showFiltersPopUpBrandSelector(captor.capture());

        List<Selection> result = captor.getValue();
        for (Selection s : result) {
            if (s.getValue().equals(BrandsEnum.REPSOL.toString()))
                assertTrue(s.isSelected());
            else
                assertFalse(s.isSelected());
        }
    }


    // Tests para onFiltersPopUpBrandsSelected()

    // Caso UGIC.2a: tempList = ["Marca1"]
    @Test
    public void testOnFiltersPopUpBrandsSelected_UGIC_2b() {
        IFilter f = new Filter();
        presenter.setTempFilter(f);

        presenter.onFiltersPopUpBrandsSelected();

        ArgumentCaptor<List<Selection>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).showFiltersPopUpBrandSelector(captor.capture());

        List<Selection> result = captor.getValue();
        for (Selection s : result) {
            if (s.getValue().equals("Todos"))
                assertTrue(s.isSelected());
            else
                assertFalse(s.isSelected());
        }
    }


    //Tests para onFiltersPopUpFuelTypesOneSelected(int index, boolean value)
    @Test
    public void onFilterPopUpFuelTypesOneSelected_UD3_aTest() {
        mockTempListType = new ArrayList<>(Arrays.asList(
                new Selection("Todos", false),
                new Selection("Gasolina 95", false),
                new Selection("Diesel", true)
        ));

        //  Inicializa la lista de selecciones de tipos de combustible
        presenter.setTempListSelection(mockTempListType);

        presenter.onFiltersPopUpFuelTypesOneSelected(0, true);


        verify(mockView).updateFiltersPopUpSelection(1, false);
        verify(mockView).updateFiltersPopUpSelection(1, false);

        List<Selection> listaPrueba = presenter.getTempListSelection();
        assertTrue(listaPrueba.get(0).isSelected());
        assertFalse(listaPrueba.get(1).isSelected());
        assertFalse(listaPrueba.get(2).isSelected());



    }


    @Test
    public void onFilterPopUpFuelTypesOneSelected_UD3_bTest() {
        mockTempListType = new ArrayList<>(Arrays.asList(
                new Selection("Todos", true),
                new Selection("Gasolina 95", false),
                new Selection("Diesel", false)
        ));

        //  Inicializa la lista de selecciones de tipos de combustible
        presenter.setTempListSelection(mockTempListType);

        presenter.onFiltersPopUpFuelTypesOneSelected(0, false);


        verify(mockView).updateFiltersPopUpSelection(0, true);

        List<Selection> listaPrueba = presenter.getTempListSelection();
        assertTrue(listaPrueba.get(0).isSelected());
        assertFalse(listaPrueba.get(1).isSelected());
        assertFalse(listaPrueba.get(2).isSelected());



    }


    @Test
    public void onFilterPopUpFuelTypesOneSelected_UD3_cTest() {
        mockTempListType = new ArrayList<>(Arrays.asList(
                new Selection("Todos", true),
                new Selection("Gasolina 95", false),
                new Selection("Diesel", false)
        ));

        //  Inicializa la lista de selecciones de tipos de combustible
        presenter.setTempListSelection(mockTempListType);

        presenter.onFiltersPopUpFuelTypesOneSelected(2, true);


        verify(mockView).updateFiltersPopUpSelection(0, false);


        List<Selection> listaPrueba = presenter.getTempListSelection();
        assertFalse(listaPrueba.get(0).isSelected());
        assertFalse(listaPrueba.get(1).isSelected());
        assertTrue(listaPrueba.get(2).isSelected());



    }

    @Test
    public void onFilterPopUpFuelTypesOneSelected_UD3_dTest() {
        mockTempListType = new ArrayList<>(Arrays.asList(
                new Selection("Todos", false),
                new Selection("Gasolina 95", false),
                new Selection("Diesel", true)
        ));

        //  Inicializa la lista de selecciones de tipos de combustible
        presenter.setTempListSelection(mockTempListType);

        presenter.onFiltersPopUpFuelTypesOneSelected(1, true);


        verify(mockView).updateFiltersPopUpSelection(0, true);
        verify(mockView).updateFiltersPopUpSelection(1, false);
        verify(mockView).updateFiltersPopUpSelection(2, false);


        List<Selection> listaPrueba = presenter.getTempListSelection();
        assertTrue(listaPrueba.get(0).isSelected());
        assertFalse(listaPrueba.get(1).isSelected());
        assertFalse(listaPrueba.get(2).isSelected());



    }

    @Test
    public void onFilterPopUpFuelTypesOneSelected_UD3_eTest() {
        mockTempListType = new ArrayList<>(Arrays.asList(
                new Selection("Todos", false),
                new Selection("Gasolina 95", false),
                new Selection("Diesel", true)
        ));

        //  Inicializa la lista de selecciones de tipos de combustible
        presenter.setTempListSelection(mockTempListType);

        presenter.onFiltersPopUpFuelTypesOneSelected(2, false);


        verify(mockView).updateFiltersPopUpSelection(0, true);



        List<Selection> listaPrueba = presenter.getTempListSelection();
        assertTrue(listaPrueba.get(0).isSelected());
        assertFalse(listaPrueba.get(1).isSelected());
        assertFalse(listaPrueba.get(2).isSelected());

    }


    // Caso UP1.a
    @Test
    public void onLoadWithConnectionAndNoPersistenceSuccess() {
        MockitoAnnotations.openMocks(this);
        presenter = new MainPresenter();
        List<Gasolinera> gasStations = new ArrayList<>();
        // Creamos las gasolineras con su marca.
        Gasolinera g1 = new Gasolinera();
        g1.setRotulo("CEPSA");
        Gasolinera g2 = new Gasolinera();
        g2.setRotulo("REPSOL");

        // Obtenemos el repositorio de las gasolineras, con el getTestRepository (llamada al onSuccess).
        repository = getTestRepository(context, R.raw.gasolineras_us509051_lista2);
        when(mockView.getGasolinerasRepository()).thenReturn(repository);
        when(mockView.getGasolinerasDAO()).thenReturn(mockGasStationsDAO);

        // Definimos comportamiento de la DAO, devolviendonos la lista de gasolineras definida.
        when(mockGasStationsDAO.getAll()).thenReturn(gasStations);

        // Inicializamos el sut (presenter)
        presenter.init(mockView);

        // Añadimos las gasolineras a la lista de gasStations que retorna la DAO al llamar al getAll.
        gasStations.add(g1);
        gasStations.add(g2);

        // Comprobar que se llama al view.updateLocalDBRegister(), times(2) por la llamada al init en el setup.
        verify(mockView).updateLocalDBDateRegister();

        // Comprobar que se muestran las gasolineras deseadas
        ArgumentCaptor<List<Gasolinera>> captor = ArgumentCaptor.forClass(List.class);

        // Verificar que se llama 2 veces por la llamada en el init del setUp.
        verify(mockView).showStations(captor.capture());
        List<Gasolinera> mostradas = captor.getValue();
        assertEquals(2, mostradas.size());
        assertEquals("CEPSA", mostradas.get(0).getRotulo());
        assertEquals("REPSOL", mostradas.get(1).getRotulo());

        // Comprobar que en la DAO se anhaden CEPSA, REPSOL como gasolinera.
        verify(mockView.getGasolinerasDAO()).addGasStation(mostradas.get(0));
        verify(mockView.getGasolinerasDAO()).addGasStation(mostradas.get(1));

        verify(mockView).showLoadCorrect(2);
    }

    // Caso UP1.b
    @Test
    public void onLoadWithConnectionAndPersistenceBeforeSuccess() {
        MockitoAnnotations.openMocks(this);
        presenter = new MainPresenter();
        List<Gasolinera> gasStations = new ArrayList<>();
        // Creamos las gasolineras con su marca.
        Gasolinera g1 = new Gasolinera();
        g1.setRotulo("PETROPRIX");
        Gasolinera g2 = new Gasolinera();
        g2.setRotulo("BALLENOIL");

        // Añadimos las gasolineras a la lista de gasStations que retorna la DAO al llamar al getAll.
        gasStations.add(g1);
        gasStations.add(g2);

        // Obtenemos el repositorio de las gasolineras, con el getTestRepository (llamada al onSuccess).
        repository = getTestRepository(context, R.raw.gasolineras_us509051_lista2);
        when(mockView.getGasolinerasRepository()).thenReturn(repository);
        when(mockView.getGasolinerasDAO()).thenReturn(mockGasStationsDAO);

        // Definimos comportamiento de la DAO, devolviendo la lista de gasolineras definida.
        when(mockGasStationsDAO.getAll()).thenReturn(gasStations);

        // Inicializamos el sut (presenter)
        presenter.init(mockView);

        // Modificamos de la DAO los elementos que ahora se deberian retornar
        g1.setRotulo("CEPSA");
        g2.setRotulo("REPSOL");

        // Comprobar que se llama al view.updateLocalDBRegister(), times(2) por la llamada al init en el setup.
        verify(mockView).updateLocalDBDateRegister();

        // Comprobar que se muestran las gasolineras deseadas
        ArgumentCaptor<List<Gasolinera>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).showStations(captor.capture());
        List<Gasolinera> mostradas = captor.getValue();
        assertEquals(2, mostradas.size());  // Aseguramos que la lista tiene dos elementos
        assertEquals("CEPSA", mostradas.get(0).getRotulo());
        assertEquals("REPSOL", mostradas.get(1).getRotulo());

        // Comprobar que en la DAO se anhaden CEPSA, REPSOL como gasolinera.
        verify(mockView.getGasolinerasDAO()).addGasStation(mostradas.get(0));
        verify(mockView.getGasolinerasDAO()).addGasStation(mostradas.get(1));

        verify(mockView).showLoadCorrect(2);
    }

    // Caso UP1.c
    @Test
    public void onLoadWithoutConnectionAndWithPersistenceOnFailure() {

        MockitoAnnotations.openMocks(this);

        // Creamos las gasolineras persistentes con su marca.
        Gasolinera g1 = new Gasolinera();
        g1.setRotulo("PETROPRIX");
        Gasolinera g2 = new Gasolinera();
        g2.setRotulo("BALLENOIL");

        // Lista de gasolineras en la persistencia (DAO).
        List<Gasolinera> gasStations = new ArrayList<>();
        gasStations.add(g1);
        gasStations.add(g2);

        // Obtenemos el repositorio simulado que invoca el onFailure.
        repository = getFailureRepository();  // Usamos el método que forza el onFailure.
        when(mockView.getGasolinerasRepository()).thenReturn(repository);
        when(mockView.getGasolinerasDAO()).thenReturn(mockGasStationsDAO);

        // Definimos comportamiento de la DAO, devolviendo la lista de gasolineras persistentes.
        when(mockGasStationsDAO.getAll()).thenReturn(gasStations);

        // Simulamos la fecha que se obtiene de la base de datos local, para así verificar que se muestra.
        String simulatedDate = "17/11/2024";
        when(mockView.getLocalDBDateRegister()).thenReturn(simulatedDate);

        presenter = new MainPresenter();

        // Inicializamos el SUT (presenter) para simular el comportamiento de la API con fallo.
        presenter.init(mockView);

        // Se verifica que no se llama al addGasStation
        verify(mockView.getGasolinerasDAO(), never()).addGasStation(any());

        // Se verifica que se llama 1 vez por el init del setup, asi comprobamos que aqui no se ha acualizado la fecha.
        verify(mockView, never()).updateLocalDBDateRegister();

        // Verificamos que se llama a view.showStations() con las gasolineras PETROPRIX Y BALLENOIL.
        ArgumentCaptor<List<Gasolinera>> captor = ArgumentCaptor.forClass(List.class);

        // Se verifica que se llama 2 veces por el init del setup.
        verify(mockView).showStations(captor.capture());
        List<Gasolinera> mostradas = captor.getValue();
        assertEquals(2, mostradas.size());  // Aseguramos que la lista tiene dos elementos
        assertEquals("PETROPRIX", mostradas.get(0).getRotulo());
        assertEquals("BALLENOIL", mostradas.get(1).getRotulo());

        // Verificamos que se muestra el mensaje correcto.
        String expectedMessage = "Cargadas 2 gasolineras en fecha " + simulatedDate;
        verify(mockView).showInfoMessage(expectedMessage);
    }

    // Caso UP1.d
    @Test
    public void onLoadWithoutConnectionAndNoPersistenceOnFailure() {
        MockitoAnnotations.openMocks(this);

        presenter = new MainPresenter();
        List<Gasolinera> gasStations = new ArrayList<>();

        // Creamos un repositorio que forzamos para que lance un onFailure() cuando se haga la solicitud
        repository = getFailureRepository();
        when(mockView.getGasolinerasRepository()).thenReturn(repository);
        when(mockView.getGasolinerasDAO()).thenReturn(mockGasStationsDAO);

        // Simulamos que no hay gasolineras precargadas en la DAO
        when(mockGasStationsDAO.getAll()).thenReturn(gasStations);

        // Inicializamos el SUT (presenter)
        presenter.init(mockView);

        // Se verifica que se llama 1 vez por el init del setup, asi comprobamos que aqui no se ha acualizado la fecha ni se han anhadido gasolineras.
        verify(mockView, never()).updateLocalDBDateRegister();
        verify(mockView.getGasolinerasDAO(), never()).addGasStation(any());

        // Verificamos que se llama a view.showStations() con una lista vacía
        ArgumentCaptor<List<Gasolinera>> captor = ArgumentCaptor.forClass(List.class);

        // Se verifica que se llama 2 veces por el init del setup.
        verify(mockView).showStations(captor.capture());
        List<Gasolinera> mostradas = captor.getValue();
        assertTrue(mostradas.isEmpty());

        // Verificamos que se llama a view.showInfoMessage()
        String expectedMessage = "No hay datos guardados de gasolineras";
        verify(mockView).showInfoMessage(expectedMessage);
    }

    //UP1.e
    @Test
    public void onLoadWithDatabaseReadError() {
        MockitoAnnotations.openMocks(this);
        presenter = new MainPresenter();

        // Creamos el repositorio que lanza un onFailure para simular la respuesta de la API fallida
        repository = getFailureRepository();
        when(mockView.getGasolinerasRepository()).thenReturn(repository);
        when(mockView.getGasolinerasDAO()).thenReturn(mockGasStationsDAO);

        // Simulamos que al intentar acceder a la base de datos, se lanza una SQLException
        // Envuelvo la SQLException en una RuntimeException para evitar el error de comprobación de excepciones chequeadas
        when(mockGasStationsDAO.getAll()).thenThrow(new RuntimeException(new SQLException("Error de lectura en la base de datos")));

        // Inicializamos el presenter
        presenter.init(mockView);

        // Se verifica que no se ha acualizado la fecha.
        verify(mockView, never()).updateLocalDBDateRegister();

        // Verificamos que no se anhaden gasolineras.
        verify(mockView.getGasolinerasDAO(), never()).addGasStation(any());

        // Se verifica que se llama 1 vez, porque se mete una lista vacia.
        verify(mockView).showStations(new ArrayList<Gasolinera>());
        // Verificamos que se ha llamado a showLoadError()
        verify(mockView).showLoadError();
    }

    @Test
    public void onLoadWithDatabaseWriteError() {
        MockitoAnnotations.openMocks(this);
        presenter = new MainPresenter();
        List<Gasolinera> gasStations = new ArrayList<>();

        // Creamos el repositorio que lanza un onFailure para simular la respuesta de la API fallida
        repository = getTestRepository(context, R.raw.gasolineras_us509051_lista2);
        when(mockView.getGasolinerasRepository()).thenReturn(repository);
        when(mockView.getGasolinerasDAO()).thenReturn(mockGasStationsDAO);

        // Simulamos que la base de datos local tiene gasolineras precargadas (PETROPRIX, BALLENOIL)
        Gasolinera g1 = new Gasolinera();
        g1.setRotulo("PETROPRIX");
        Gasolinera g2 = new Gasolinera();
        g2.setRotulo("BALLENOIL");
        gasStations.add(g1);
        gasStations.add(g2);

        // Simulamos la fecha de actualización de la base de datos
        String simulatedDate = "17/11/2024";
        when(mockGasStationsDAO.getAll()).thenReturn(gasStations);
        when(mockView.getLocalDBDateRegister()).thenReturn(simulatedDate);

        // Simulamos el fallo en la escritura a la base de datos (simulamos una SQLException), mediante un doThrow ya que es void.
        doThrow(new RuntimeException(new SQLException("Error al guardar en la base de datos"))).when(mockGasStationsDAO).addGasStation(any());

        // Inicializamos el presenter
        presenter.init(mockView);

        // Verificamos que no se escriben gasolineras, pero se llama una vez, y ahi lanza la excepcion
        verify(mockView.getGasolinerasDAO(), times(1)).addGasStation(any());

        // Se verifica que se llama 1 vez por el init del setup, asi comprobamos que aqui no se ha actualizado la fecha.
        verify(mockView, never()).updateLocalDBDateRegister();

        // Verificamos que se llama a view.showStations() con las gasolineras persistentes.
        ArgumentCaptor<List<Gasolinera>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).showStations(captor.capture());
        List<Gasolinera> mostradas = captor.getValue();

        assertEquals(2, mostradas.size());
        assertEquals("PETROPRIX", mostradas.get(0).getRotulo());
        assertEquals("BALLENOIL", mostradas.get(1).getRotulo());

        // Verificamos que se muestra el mensaje con el número de gasolineras
        verify(mockView).showLoadCorrect(2);

        // Verificamos que se llama a showLoadError() notificando el error de BBDD
        verify(mockView).showInfoMessage("Error al guardar datos de gasolineras en la base de datos");

    }

    @Test
    public void onLoadWithEmptyDatabaseAndEmptyAPIResponse() {
        MockitoAnnotations.openMocks(this);
        presenter = new MainPresenter();

        List<Gasolinera> gasStations = new ArrayList<>();

        // Creamos el repositorio que lanza un onSuccess con una respuesta vacía
        repository = getEmptyRepository();
        when(mockView.getGasolinerasRepository()).thenReturn(repository);
        when(mockView.getGasolinerasDAO()).thenReturn(mockGasStationsDAO);

        // Simulamos que no hay gasolineras en la base de datos local
        when(mockGasStationsDAO.getAll()).thenReturn(gasStations);

        // Inicializamos el presenter
        presenter.init(mockView);

        // Verificamos que no se escriben gasolineras en la DAO
        verify(mockView.getGasolinerasDAO(), never()).addGasStation(any());

        // Verificamos que se llama a updateLocalDBDateRegister() para actualizar la fecha de la base de datos
        verify(mockView).updateLocalDBDateRegister();

        // Verificamos que se llama a showStations con una lista vacía
        verify(mockView).showStations(new ArrayList<Gasolinera>());  // Lista vacía

        // Verificamos que se muestra el mensaje con error de carga, ya que muestra 0 gasolineras.
        verify(mockView).showLoadError();
    }

    @Test
    public void onLoadWithDatabaseAndEmptyAPIResponse() {
        MockitoAnnotations.openMocks(this);
        presenter = new MainPresenter();
        List<Gasolinera> gasStations = new ArrayList<>();

        // Creamos el repositorio que lanza un onSuccess con una respuesta vacía (API con respuesta vacía)
        repository = getEmptyRepository();  // Repositorio que devuelve una lista vacía
        when(mockView.getGasolinerasRepository()).thenReturn(repository);
        when(mockView.getGasolinerasDAO()).thenReturn(mockGasStationsDAO);

        // Simulamos que la base de datos local tiene gasolineras precargadas (PETROPRIX, BALLENOIL)
        Gasolinera g1 = new Gasolinera();
        g1.setRotulo("PETROPRIX");
        Gasolinera g2 = new Gasolinera();
        g2.setRotulo("BALLENOIL");
        gasStations.add(g1);
        gasStations.add(g2);
        when(mockGasStationsDAO.getAll()).thenReturn(gasStations);

        // Inicializamos el presenter
        presenter.init(mockView);

        // Verificamos que se llama a updateLocalDBDateRegister() para actualizar la fecha de la base de datos
        verify(mockView).updateLocalDBDateRegister();

        // Verificamos que no se escriben gasolineras en la DAO
        verify(mockView.getGasolinerasDAO(), never()).addGasStation(any());

        // Verificamos que se llama a view.showStations() con la lista vacia
        ArgumentCaptor<List<Gasolinera>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).showStations(captor.capture());
        List<Gasolinera> mostradas = captor.getValue();
        assertEquals(0, mostradas.size());

        // Verificamos que se muestra el mensaje con error de carga, ya que muestra 0 gasolineras.
        verify(mockView).showLoadError();
    }

}
