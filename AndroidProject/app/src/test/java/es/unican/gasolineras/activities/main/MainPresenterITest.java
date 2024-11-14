package es.unican.gasolineras.activities.main;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import es.unican.gasolineras.R;
import es.unican.gasolineras.common.FuelTypeEnum;
import es.unican.gasolineras.model.OrderByPrice;
import java.util.Arrays;
import java.util.Collections;

import es.unican.gasolineras.common.IFilter;
import es.unican.gasolineras.model.Filter;
import es.unican.gasolineras.model.Gasolinera;
import es.unican.gasolineras.repository.IGasolinerasRepository;
import es.unican.gasolineras.utils.MockRepositories;

@RunWith(RobolectricTestRunner.class)
public class MainPresenterITest {

    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    final IGasolinerasRepository repository = MockRepositories.getTestRepository(context, R.raw.gasolineras_ccaa_06);
    final IGasolinerasRepository repository2 = MockRepositories.getTestRepository(context, R.raw.gasolineras_test_505739);
    final IGasolinerasRepository repository3 = MockRepositories.getTestRepository(context, R.raw.gasolineras_filtro_tipo_test);

    @Mock
    private IMainContract.View view;
    @Mock
    private IMainContract.View mockMainView;
    @Mock
    private IMainContract.View mockMainView2;

    private IMainContract.Presenter sut;

    @Captor
    ArgumentCaptor<List<Gasolinera>> listCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(view.getGasolinerasRepository()).thenReturn(repository);
        when(mockMainView.getGasolinerasRepository()).thenReturn(repository2);
        when(mockMainView2.getGasolinerasRepository()).thenReturn(repository3);
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
        verify(mockMainView, times(1)).closeOrderPopUp();

        //ID1.b
        sut.setOrderByPrice(createOrderByPrice(FuelTypeEnum.GASOLEO_A, false));
        sut.setTempFilter(new Filter());

        sut.onOrderPopUpAcceptClicked();

        verify(mockMainView,times (3)).showStations(listCaptor.capture());
        assertEquals("1048", listCaptor.getValue().get(0).getId());
        assertEquals("1039", listCaptor.getValue().get(3).getId());
        verify(mockMainView, times (3)).showLoadCorrect(eq(7));
        verify(mockMainView, times(2)).closeOrderPopUp();
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
        verify(mockMainView2).closeFiltersPopUp();
        // Comprobamos los metodos internos de load() con las gasolineras ya filtradas
        // Son 2 veces porque "sut.init(view);" llama al load()
        verify(mockMainView2, times(1)).getGasolinerasRepository();
        // {CEPSA, REPSOL, PETRONOR, PETRONOR V2, GALP}
        verify(mockMainView2).showLoadCorrect(rotulos.length);
        verify(mockMainView2, times(2)).showStations(listCaptor.capture());
        for (int i = 0; i < rotulos.length; i++) {
            Assert.assertEquals(rotulos[i], listCaptor.getValue().get(i).getRotulo());
        }
        // Filter = {Gasolina95E5}
        IFilter f = sut.getFilter();
        Assert.assertEquals(1, f.getFuelTypes().size());
        Assert.assertEquals(FuelTypeEnum.GASOLINA_95E5, f.getFuelTypes().get(0));
        // TempFilter = null
        IFilter tmpF = sut.getTempFilter();
        Assert.assertNull(tmpF);
        // tempListSelection = null
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
        verify(mockMainView2).closeFiltersPopUp();
        // Comprobamos los metodos internos de load() con las gasolineras ya filtradas
        // Son 2 veces porque "sut.init(view);" llama al load()
        verify(mockMainView2, times(1)).getGasolinerasRepository();
        // {CEPSA, REPSOL, PETRONOR, PETRONOR V2, GALP}
        verify(mockMainView2).showLoadCorrect(rotulos.length);
        verify(mockMainView2, times(2)).showStations(listCaptor.capture());
        for (int i = 0; i < rotulos.length; i++) {
            Assert.assertEquals(rotulos[i], listCaptor.getValue().get(i).getRotulo());
        }
        // Filter = {GasoleoA}
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
        verify(mockMainView2).closeFiltersPopUp();
        // Son 2 veces porque "sut.init(view);" llama al load()
        verify(mockMainView2, times(1)).getGasolinerasRepository();
        // {CEPSA, REPSOL, PETRONOR, PETRONOR V2, GALP}
        verify(mockMainView2, times(2)).showLoadCorrect(6);
        verify(mockMainView2, times(2)).showStations(listCaptor.capture());
        for (int i = 0; i < rotulos.length; i++) {
            Assert.assertEquals(rotulos[i], listCaptor.getValue().get(i).getRotulo());
        }
        // Filter = {Gasolina95E5, GasoleoA}
        IFilter f = sut.getFilter();
        Assert.assertEquals(Arrays.asList(FuelTypeEnum.GASOLEO_A, FuelTypeEnum.GASOLINA_95E5), f.getFuelTypes());
        // TempFilter = null
        IFilter tmpF = sut.getTempFilter();
        Assert.assertNull(tmpF);
        // tempListSelection = null
        Assert.assertNull(sut.getTempListSelection());
    }
}
