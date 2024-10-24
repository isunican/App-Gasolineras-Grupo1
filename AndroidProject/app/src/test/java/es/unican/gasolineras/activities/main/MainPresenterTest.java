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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import static es.unican.gasolineras.activities.main.MockRepositories.getTestRepository;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import es.unican.gasolineras.R;
import es.unican.gasolineras.repository.IGasolinerasRepository;

@RunWith(RobolectricTestRunner.class)
public class MainPresenterTest {

    private MainPresenter presenter;
    @Mock
    private IMainContract.View mockView;
    @Mock
    private List<Selection> mockTempListSelection;
    Context context = ApplicationProvider.getApplicationContext();
    private IGasolinerasRepository repository;

    @Before
    public void setUp() {

        MockitoAnnotations.openMocks(this);
        presenter = new MainPresenter();

        repository = getTestRepository(context, R.raw.gasolineras_ccaa_06);

        when(mockView.getGasolinerasRepository()).thenReturn(repository);

        presenter.init(mockView);

        mockTempListSelection = new ArrayList<>(Arrays.asList(
                new Selection("Todos", true),
                new Selection("Marca1", false),
                new Selection("Marca2", false)
        ));

       //  Inicializa la lista de selecciones de marcas
        presenter.setTempListSelection(mockTempListSelection);

    }

    //No hago nada, de seleccionado el todos
    @Test
    public void testOnFiltersPopUpBrandsOneSelected_AllSelected() {
        // Caso: Se selecciona "Todos"
        presenter.onFiltersPopUpBrandsOneSelected(0, true);

        for (int i = 1; i < mockTempListSelection.size(); i++) {
            verify(mockView).updateFiltersPopUpBrandsSelection(i, false);
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
        //presenter.onFiltersPopUpBrandsOneSelected(2, false);

        verify(mockView).updateFiltersPopUpBrandsSelection(0, false);

        List<Selection> listaPrueba = presenter.getTempListSelection();
        assertFalse(listaPrueba.get(0).isSelected());
        assertTrue(listaPrueba.get(1).isSelected());
        assertFalse(listaPrueba.get(2).isSelected());

    }

    //Seleccionas las dos marcas y por eso se desmarcan y se marca TODOS
    @Test
    public void testOnFiltersPopUpBrandsOneSelected_SelectAllBrands() {
        // Caso: Se selecciona "Todos"
        presenter.onFiltersPopUpBrandsOneSelected(1, true);
        presenter.onFiltersPopUpBrandsOneSelected(2, true);

        verify(mockView).updateFiltersPopUpBrandsSelection(0, true);
        verify(mockView).updateFiltersPopUpBrandsSelection(1, false);
        verify(mockView).updateFiltersPopUpBrandsSelection(2, false);

        List<Selection> listaPrueba = presenter.getTempListSelection();
        assertTrue(listaPrueba.get(0).isSelected());
        assertFalse(listaPrueba.get(1).isSelected());
        assertFalse(listaPrueba.get(2).isSelected());

    }

    //Intento desmarcar el todos
    @Test
    public void testOnFiltersPopUpBrandsOneSelected_UnselectAll() {
        // Caso: Se selecciona "Todos"
        presenter.onFiltersPopUpBrandsOneSelected(0, true);
        presenter.onFiltersPopUpBrandsOneSelected(0, false);
        //presenter.onFiltersPopUpBrandsOneSelected(2, false);

        verify(mockView).updateFiltersPopUpBrandsSelection(0, true);

        List<Selection> listaPrueba = presenter.getTempListSelection();
        assertTrue(listaPrueba.get(0).isSelected());
        assertFalse(listaPrueba.get(1).isSelected());
        assertFalse(listaPrueba.get(2).isSelected());

    }


    //Desmarco uno y se marca el TODOS
    @Test
    public void testOnFiltersPopUpBrandsOneSelected_UnselectOne() {
        // Caso: Se selecciona "Todos"
        presenter.onFiltersPopUpBrandsOneSelected(0, true);
        presenter.onFiltersPopUpBrandsOneSelected(1, true);
        presenter.onFiltersPopUpBrandsOneSelected(1, false);


        verify(mockView).updateFiltersPopUpBrandsSelection(0, true);
        verify(mockView).updateFiltersPopUpBrandsSelection(1, false);

        List<Selection> listaPrueba = presenter.getTempListSelection();
        assertTrue(listaPrueba.get(0).isSelected());
        assertFalse(listaPrueba.get(1).isSelected());
        assertFalse(listaPrueba.get(2).isSelected());

    }







    // Caso UGIC.2a: tempList = ["Marca1"]
    @Test
    public void testOnFiltersPopUpBrandsSelected_UGIC_2a() {
        // Simular que el filtro contiene solo "Marca1"
        when(mockFilter.getBrands()).thenReturn(Arrays.asList(BrandsEnum.MARCA1));

        // Llamar al método que queremos probar
        presenter.onFiltersPopUpBrandsSelected();

        // Capturar el valor de la lista de selección pasada a la vista
        ArgumentCaptor<List<Selection>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).showFiltersPopUpBrandSelector(captor.capture());

        List<Selection> result = captor.getValue();

        // Validar los valores esperados
        assertEquals(3, result.size());
        assertFalse(result.get(0).isSelected());  // "Todos" no está seleccionado
        assertTrue(result.get(1).isSelected());   // "Marca1" está seleccionado
        assertFalse(result.get(2).isSelected());  // "Marca2" no está seleccionado
    }

    // Caso UGIC.2b: tempList = []
    @Test
    public void testOnFiltersPopUpBrandsSelected_UGIC_2b() {
        // Simular que el filtro contiene una lista vacía
        when(mockFilter.getBrands()).thenReturn(new ArrayList<>());

        // Llamar al método que queremos probar
        presenter.onFiltersPopUpBrandsSelected();

        // Capturar el valor de la lista de selección pasada a la vista
        ArgumentCaptor<List<Selection>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).showFiltersPopUpBrandSelector(captor.capture());

        List<Selection> result = captor.getValue();

        // Validar los valores esperados
        assertEquals(3, result.size());
        assertFalse(result.get(0).isSelected());  // "Todos" no está seleccionado
        assertFalse(result.get(1).isSelected());  // "Marca1" no está seleccionado
        assertFalse(result.get(2).isSelected());  // "Marca2" no está seleccionado
    }

    // Caso UGIC.2c: tempList = ["Marca1", "Marca 2"]
    @Test
    public void testOnFiltersPopUpBrandsSelected_UGIC_2c() {
        // Simular que el filtro contiene "Marca1" y "Marca2"
        when(mockFilter.getBrands()).thenReturn(Arrays.asList(BrandsEnum.MARCA1, BrandsEnum.MARCA2));

        // Llamar al método que queremos probar
        presenter.onFiltersPopUpBrandsSelected();

        // Capturar el valor de la lista de selección pasada a la vista
        ArgumentCaptor<List<Selection>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).showFiltersPopUpBrandSelector(captor.capture());

        List<Selection> result = captor.getValue();

        // Validar los valores esperados
        assertEquals(3, result.size());
        assertTrue(result.get(0).isSelected());   // "Todos" está seleccionado
        assertFalse(result.get(1).isSelected());  // "Marca1" no está seleccionado
        assertFalse(result.get(2).isSelected());  // "Marca2" no está seleccionado
    }
}











}
