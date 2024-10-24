package es.unican.gasolineras.activities.main;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.robolectric.RobolectricTestRunner;

import java.util.Optional;

import dagger.hilt.android.testing.BindValue;
import es.unican.gasolineras.R;
import es.unican.gasolineras.Utils.MockRepositories;
import es.unican.gasolineras.common.IFilter;
import es.unican.gasolineras.model.Filter;
import es.unican.gasolineras.repository.IGasolinerasRepository;

@RunWith(RobolectricTestRunner.class)
public class MainPresenterITest {

    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    final IGasolinerasRepository repository = MockRepositories.getTestRepository(context, R.raw.gasolineras_ccaa_06);
    @Mock
    private IMainContract.View view;

    private IMainContract.Presenter sut;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(view.getGasolinerasRepository()).thenReturn(repository);
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
        Assert.assertEquals(Float.MAX_VALUE, (float) f.getMaxPrice(), 0.0);
    }

    @Test
    public void onFiltersPopUpClearFiltersClickedNoInitializedITest() {
        sut.setTempFilter(new Filter());
        when(view.getConstantString(R.string.all_selections)).thenReturn("Todos");
        sut.onFiltersPopUpClearFiltersClicked();
        verify(view).updateFiltersPopupTextViewsSelections("Todos", "Todos");
        IFilter f = sut.getTempFilter();
        Assert.assertEquals(Float.MAX_VALUE, (float) f.getMaxPrice(), 0.0);
    }


}
