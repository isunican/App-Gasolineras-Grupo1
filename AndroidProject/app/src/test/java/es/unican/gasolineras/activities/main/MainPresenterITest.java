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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.unican.gasolineras.R;
import es.unican.gasolineras.Utils.MockRepositories;
import es.unican.gasolineras.common.FuelTypeEnum;
import es.unican.gasolineras.common.IFilter;
import es.unican.gasolineras.model.Filter;
import es.unican.gasolineras.model.Gasolinera;
import es.unican.gasolineras.repository.IGasolinerasRepository;

@RunWith(RobolectricTestRunner.class)
public class MainPresenterITest {

    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    final IGasolinerasRepository repository = MockRepositories.getTestRepository(context, R.raw.gasolineras_filtro_tipo_test);
    @Mock
    private IMainContract.View view;

    private IMainContract.Presenter sut;

    @Captor
    ArgumentCaptor<List<Gasolinera>> listCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(view.getGasolinerasRepository()).thenReturn(repository);
        sut = new MainPresenter();
        sut.init(view);
    }

    @Test
    public void onFiltersPopUpAcceptClickedGasolina95E5() {
        // Gasolineras filtradas por Gasolina95E5
        String[] rotulos = {"CEPSA", "REPSOL", "PETRONOR", "PETRONOR V2", "GALP"};
        sut.setTempFilter(new Filter()
                .setFuelTypes(Collections.singletonList(FuelTypeEnum.GASOLINA_95E5))
        );
        sut.onFiltersPopUpAcceptClicked();
        verify(view).closeFiltersPopUp();
        // Comprobamos los metodos internos de load() con las gasolineras ya filtradas
        // Son 2 veces porque "sut.init(view);" llama al load()
        verify(view, times(2)).getGasolinerasRepository();
        // {CEPSA, REPSOL, PETRONOR, PETRONOR V2, GALP}
        verify(view).showLoadCorrect(rotulos.length);
        verify(view, times(2)).showStations(listCaptor.capture());
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
        // Gasolineras filtradas por GasoleoA
        String[] rotulos = {"CEPSA", "REPSOL", "PETRONOR", "PETRONOR V2", "REDETRANS"};
        sut.setTempFilter(new Filter()
                .setFuelTypes(Collections.singletonList(FuelTypeEnum.GASOLEO_A))
        );
        sut.onFiltersPopUpAcceptClicked();
        verify(view).closeFiltersPopUp();
        // Comprobamos los metodos internos de load() con las gasolineras ya filtradas
        // Son 2 veces porque "sut.init(view);" llama al load()
        verify(view, times(2)).getGasolinerasRepository();
        // {CEPSA, REPSOL, PETRONOR, PETRONOR V2, GALP}
        verify(view).showLoadCorrect(rotulos.length);
        verify(view, times(2)).showStations(listCaptor.capture());
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
        // Gasolineras filtradas por gasolina95E5 y gasoleoA
        String[] rotulos = {"CEPSA", "REPSOL", "PETRONOR", "PETRONOR V2"};
        sut.setTempFilter(new Filter()
                .setFuelTypes(Arrays.asList(FuelTypeEnum.GASOLEO_A, FuelTypeEnum.GASOLINA_95E5))
        );
        sut.onFiltersPopUpAcceptClicked();
        verify(view).closeFiltersPopUp();
        // Son 2 veces porque "sut.init(view);" llama al load()
        verify(view, times(2)).getGasolinerasRepository();
        // {CEPSA, REPSOL, PETRONOR, PETRONOR V2, GALP}
        verify(view, times(2)).showLoadCorrect(rotulos.length);
        verify(view, times(2)).showStations(listCaptor.capture());
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
