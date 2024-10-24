package es.unican.gasolineras.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.unican.gasolineras.common.FuelTypeEnum;
import es.unican.gasolineras.common.IFilter;

@RunWith(RobolectricTestRunner.class)
public class FilterTest {

    List<Gasolinera> l;

    private void setUp(String[] nombres, Double[] gasolinaG5E5, Double[] gasoleoA) {
        l = new ArrayList<>();
        Gasolinera g;
        for (int i = 0; i<nombres.length; i++) {
            g = new Gasolinera();
            g.setRotulo(nombres[i]);
            g.setGasolina95E5(gasolinaG5E5[i]);
            g.setGasoleoA(gasoleoA[i]);
            l.add(g);
        }
    }

    @Test
    public void toFilterGasolinaG5Es() {
        String[] nombres = {"CEPSA", "REPSOL", "PETRONOR", "PETRONOR V2", "REDETRANS", "GALP"};
        Double[] gasolinaG5E5 = {1.679, 1.669, 1.639, 1.639, 0.0, 1.639};
        Double[] gasoleoA = {1.509, 1.569, 1.525, 1.525, 1.299, 0.0};
        setUp(nombres, gasolinaG5E5, gasoleoA);
        IFilter filter = new Filter()
                .setFuelTypes(Collections.singletonList(FuelTypeEnum.GASOLINA_95E5));
        l = filter.toFilter(l);
        assertEquals(5, l.size());
        // Contar las ocurrencias de los rotulos especificados
        long rotulosCount = l.stream()
                .filter(e -> Arrays.asList("CEPSA", "REPSOL", "PETRONOR", "PETRONOR V2", "GALP")
                        .contains(e.getRotulo()))
                .count();
        assertEquals(5, rotulosCount);
    }

    @Test
    public void toFilterGasoleoA() {
        String[] nombres = {"CEPSA", "REPSOL", "PETRONOR", "PETRONOR V2", "REDETRANS", "GALP"};
        Double[] gasolinaG5E5 = {1.679, 1.669, 1.639, 1.639, 0.0, 1.639};
        Double[] gasoleoA = {1.509, 1.569, 1.525, 1.525, 1.299, 0.0};
        setUp(nombres, gasolinaG5E5, gasoleoA);
        IFilter filter = new Filter()
                .setFuelTypes(Collections.singletonList(FuelTypeEnum.GASOLEO_A));
        l = filter.toFilter(l);
        assertEquals(5, l.size());
        // Contar las ocurrencias de los rotulos especificados
        long rotulosCount = l.stream()
                .filter(e -> Arrays.asList("CEPSA", "REPSOL", "PETRONOR", "PETRONOR V2", "REDETRANS")
                        .contains(e.getRotulo()))
                .count();
        assertEquals(5, rotulosCount);
    }

    @Test
    public void toFilterGasolinaG5EsGasoleoA() {
        String[] nombres = {"CEPSA", "REPSOL", "PETRONOR", "PETRONOR V2", "REDETRANS", "GALP"};
        Double[] gasolinaG5E5 = {1.679, 1.669, 1.639, 1.639, 0.0, 1.639};
        Double[] gasoleoA = {1.509, 1.569, 1.525, 1.525, 1.299, 0.0};
        setUp(nombres, gasolinaG5E5, gasoleoA);
        IFilter filter = new Filter();
        l = filter.toFilter(l);
        assertEquals(4, l.size());
        // Contar las ocurrencias de los rotulos especificados
        long rotulosCount = l.stream()
                .filter(e -> Arrays.asList("CEPSA", "REPSOL", "PETRONOR", "PETRONOR V2")
                        .contains(e.getRotulo()))
                .count();
        assertEquals(4, rotulosCount);
    }

    @Test
    public void toFilterSinGasolineras() {
        l = new ArrayList<>();
        IFilter filter = new Filter()
                .setFuelTypes(Collections.singletonList(FuelTypeEnum.GASOLEO_A));
        l = filter.toFilter(l);
        assertEquals(0, l.size());
        assertEquals(
                l.size(),
                l.stream()
                        .filter(e -> e.getRotulo().isEmpty())
                        .count()
        );
    }
}
