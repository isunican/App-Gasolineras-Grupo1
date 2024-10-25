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
import es.unican.gasolineras.utils.MockRepositories;
import es.unican.gasolineras.common.IFilter;
import es.unican.gasolineras.model.Filter;
import es.unican.gasolineras.model.Gasolinera;
import es.unican.gasolineras.repository.IGasolinerasRepository;

@RunWith(RobolectricTestRunner.class)
public class MainPresenterITest {

    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    final IGasolinerasRepository repository = MockRepositories.getTestRepository(context, R.raw.gasolineras_ccaa_06);

    final IGasolinerasRepository repository2 = MockRepositories.getTestRepository(context, R.raw.gasolineras_test_505739);
    @Mock
    private IMainContract.View view;
    @Mock
    private IMainContract.View mockMainView;

    private IMainContract.Presenter sut;

    @Captor
    ArgumentCaptor<List<Gasolinera>> listCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(view.getGasolinerasRepository()).thenReturn(repository);
        when(mockMainView.getGasolinerasRepository()).thenReturn(repository2);
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
        assertEquals("1036", listCaptor.getValue().get(0).getId());
        assertEquals("1039", listCaptor.getValue().get(3).getId());
        verify(mockMainView, times (2)).showLoadCorrect(eq(4));
        verify(mockMainView, times(1)).closeOrderPopUp();

        //ID1.b
        sut.setOrderByPrice(createOrderByPrice(FuelTypeEnum.GASOLEO_A, false));
        sut.setTempFilter(new Filter());

        sut.onOrderPopUpAcceptClicked();

        verify(mockMainView,times (3)).showStations(listCaptor.capture());
        assertEquals("1048", listCaptor.getValue().get(0).getId());
        assertEquals("1039", listCaptor.getValue().get(3).getId());
        verify(mockMainView, times (3)).showLoadCorrect(eq(4));
        verify(mockMainView, times(2)).closeOrderPopUp();

    }
    private OrderByPrice createOrderByPrice(FuelTypeEnum fuelType, Boolean ascending){
        OrderByPrice orderByPrice = new OrderByPrice();
        if(fuelType != null){
            orderByPrice.setFuelType(fuelType);
        }
        if(ascending != null){
            orderByPrice.setAscending(ascending);
        }
        return orderByPrice;
    }
}
