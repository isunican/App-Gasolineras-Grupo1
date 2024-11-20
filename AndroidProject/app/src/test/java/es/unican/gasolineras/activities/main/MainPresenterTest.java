package es.unican.gasolineras.activities.main;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


import static es.unican.gasolineras.utils.MockRepositories.getTestRepository;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import es.unican.gasolineras.R;
import es.unican.gasolineras.common.BrandsEnum;
import es.unican.gasolineras.common.IFilter;
import es.unican.gasolineras.model.Filter;
import es.unican.gasolineras.model.Gasolinera;
import es.unican.gasolineras.model.IDCCAAs;
import es.unican.gasolineras.model.InterestPoint;
import es.unican.gasolineras.repository.ICallBack;
import es.unican.gasolineras.repository.IGasolinerasRepository;

@RunWith(RobolectricTestRunner.class)
public class MainPresenterTest {

    private MainPresenter presenter;
    @Mock
    private IMainContract.View mockView;
    @Mock
    private List<Selection> mockTempListSelection;

    @Mock
    private List<Selection> mockTempListType;

    Context context = ApplicationProvider.getApplicationContext();
    private IGasolinerasRepository repository;

    @Before
    public void setUp() {

        MockitoAnnotations.openMocks(this);

        repository = getTestRepository(context, R.raw.gasolineras_ccaa_06);

        when(mockView.getGasolinerasRepository()).thenReturn(repository);
        when(mockView.getConstantString(R.string.all_selections)).thenReturn("Todos");




    }

    //No hago nada, de seleccionado el todos
    @Test
    public void testOnFiltersPopUpBrandsOneSelected_AllSelected() {
        presenter = new MainPresenter();
        presenter.init(mockView);

        mockTempListSelection = new ArrayList<>(Arrays.asList(
                new Selection("Todos", true),
                new Selection("Marca1", false),
                new Selection("Marca2", false)
        ));

        //  Inicializa la lista de selecciones de marcas
        presenter.setTempListSelection(mockTempListSelection);
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
        presenter = new MainPresenter();
        presenter.init(mockView);

        mockTempListSelection = new ArrayList<>(Arrays.asList(
                new Selection("Todos", true),
                new Selection("Marca1", false),
                new Selection("Marca2", false)
        ));

        //  Inicializa la lista de selecciones de marcas
        presenter.setTempListSelection(mockTempListSelection);
        // Caso: Se selecciona "Todos"
        presenter.onFiltersPopUpBrandsOneSelected(0, true);
        presenter.onFiltersPopUpBrandsOneSelected(1, true);
        //presenter.onFiltersPopUpBrandsOneSelected(2, false);

        verify(mockView).updateFiltersPopUpSelection(0, false);

        List<Selection> listaPrueba = presenter.getTempListSelection();
        assertFalse(listaPrueba.get(0).isSelected());
        assertTrue(listaPrueba.get(1).isSelected());
        assertFalse(listaPrueba.get(2).isSelected());

    }

    //Seleccionas las dos marcas y por eso se desmarcan y se marca TODOS
    @Test
    public void testOnFiltersPopUpBrandsOneSelected_SelectAllBrands() {
        presenter = new MainPresenter();
        presenter.init(mockView);

        mockTempListSelection = new ArrayList<>(Arrays.asList(
                new Selection("Todos", true),
                new Selection("Marca1", false),
                new Selection("Marca2", false)
        ));

        //  Inicializa la lista de selecciones de marcas
        presenter.setTempListSelection(mockTempListSelection);
        // Caso: Se selecciona "Todos"
        presenter.onFiltersPopUpBrandsOneSelected(1, true);
        presenter.onFiltersPopUpBrandsOneSelected(2, true);

        //verify(mockView).updateFiltersPopUpSelection(0, true);
        verify(mockView).updateFiltersPopUpSelection(1, false);
        verify(mockView).updateFiltersPopUpSelection(2, false);

        List<Selection> listaPrueba = presenter.getTempListSelection();
        assertTrue(listaPrueba.get(0).isSelected());
        assertFalse(listaPrueba.get(1).isSelected());
        assertFalse(listaPrueba.get(2).isSelected());

    }

    //Intento desmarcar el todos
    @Test
    public void testOnFiltersPopUpBrandsOneSelected_UnselectAll() {
        presenter = new MainPresenter();
        presenter.init(mockView);

        mockTempListSelection = new ArrayList<>(Arrays.asList(
                new Selection("Todos", true),
                new Selection("Marca1", false),
                new Selection("Marca2", false)
        ));

        //  Inicializa la lista de selecciones de marcas
        presenter.setTempListSelection(mockTempListSelection);
        // Caso: Se selecciona "Todos"
        presenter.onFiltersPopUpBrandsOneSelected(0, true);
        presenter.onFiltersPopUpBrandsOneSelected(0, false);
        //presenter.onFiltersPopUpBrandsOneSelected(2, false);

        verify(mockView).updateFiltersPopUpSelection(0, true);

        List<Selection> listaPrueba = presenter.getTempListSelection();
        assertTrue(listaPrueba.get(0).isSelected());
        assertFalse(listaPrueba.get(1).isSelected());
        assertFalse(listaPrueba.get(2).isSelected());

    }


    //Desmarco uno y se marca el TODOS
    @Test
    public void testOnFiltersPopUpBrandsOneSelected_UnselectOne() {
        presenter = new MainPresenter();
        presenter.init(mockView);

        mockTempListSelection = new ArrayList<>(Arrays.asList(
                new Selection("Todos", true),
                new Selection("Marca1", false),
                new Selection("Marca2", false)
        ));

        //  Inicializa la lista de selecciones de marcas
        presenter.setTempListSelection(mockTempListSelection);
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
        presenter = new MainPresenter();
        presenter.init(mockView);

        mockTempListSelection = new ArrayList<>(Arrays.asList(
                new Selection("Todos", true),
                new Selection("Marca1", false),
                new Selection("Marca2", false)
        ));

        //  Inicializa la lista de selecciones de marcas
        presenter.setTempListSelection(mockTempListSelection);
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
        presenter = new MainPresenter();
        presenter.init(mockView);

        mockTempListSelection = new ArrayList<>(Arrays.asList(
                new Selection("Todos", true),
                new Selection("Marca1", false),
                new Selection("Marca2", false)
        ));

        //  Inicializa la lista de selecciones de marcas
        presenter.setTempListSelection(mockTempListSelection);
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
        presenter = new MainPresenter();
        presenter.init(mockView);

        mockTempListSelection = new ArrayList<>(Arrays.asList(
                new Selection("Todos", true),
                new Selection("Marca1", false),
                new Selection("Marca2", false)
        ));

        //  Inicializa la lista de selecciones de marcas
        presenter.setTempListSelection(mockTempListSelection);
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
        presenter = new MainPresenter();
        presenter.init(mockView);

        mockTempListSelection = new ArrayList<>(Arrays.asList(
                new Selection("Todos", true),
                new Selection("Marca1", false),
                new Selection("Marca2", false)
        ));

        //  Inicializa la lista de selecciones de marcas
        presenter.setTempListSelection(mockTempListSelection);
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
        presenter = new MainPresenter();
        presenter.init(mockView);
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
        presenter = new MainPresenter();
        presenter.init(mockView);
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
        presenter = new MainPresenter();
        presenter.init(mockView);
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

    @Test
    public void inicializarUnPuntoConGasolineras() {
        //Creo el presenter con el constructor que me permite crear un punto de interés
        presenter = new MainPresenter(new InterestPoint("Zona 1","#0000ff",43.3192,-4.2987,2));

        //Preparo el repositorio para que devuelva la lista.
        when(mockView.getGasolinerasRepository()).thenReturn(repository);


        //Capturo para saber que devuelve el método showStations
        ArgumentCaptor<List<Gasolinera>> captor = ArgumentCaptor.forClass(List.class);

        //Inicializo el presenter.
        presenter.init(mockView);

        //Verifico que se llama al init.
        verify(mockView).init();

        //Capturo la información de showStations.
        verify(mockView).showStations(captor.capture());

        //Creo una gasolinera para comprobar.
        Gasolinera g1 = new Gasolinera();
        g1.setId("1006");

        //Lista para alamacenar las gasolineras.
        List<Gasolinera> stations = captor.getValue();


        // Verificar que las gasolinera obtenida sea la correcta.
        assertEquals(1, stations.size());
        assertEquals(g1.getId(), stations.get(0).getId());









    }

    @Test
    public void inicializarUnPuntoSinGasolineras() {
        //Creo el presenter con el constructor que me permite crear un punto de interés
        presenter = new MainPresenter(new InterestPoint("Zona 2","#008000",42.8881,-4.0025,0.5));

        //Preparo el repositorio para que devuelva la lista vacía.
        when(mockView.getGasolinerasRepository()).thenReturn(repository);

        //Capturo para saber que devuelve el método showStations
        ArgumentCaptor<List<Gasolinera>> captor = ArgumentCaptor.forClass(List.class);

        //Inicializo el presenter.
        presenter.init(mockView);

        //Verifico que se llama al init.
        verify(mockView).init();

        //Capturo la información de showStations.
        verify(mockView).showStations(captor.capture());


        //Almaceno la lista de gasolineras.
        List<Gasolinera> stations = captor.getValue();

        // Verifica que no se obitenen gasolineras.
        assertEquals(0, stations.size());

        //Verifico que se llama a showInfoMessage()
        verify(mockView).showInfoMessage("No se han encontrado gasolineras en el rango");
    }





}