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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


import static es.unican.gasolineras.utils.MockRepositories.getTestRepository;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import es.unican.gasolineras.R;
import es.unican.gasolineras.common.BrandsEnum;
import es.unican.gasolineras.common.IFilter;
import es.unican.gasolineras.model.Filter;
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

    //No hago nada, de seleccionado el todos
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

}
