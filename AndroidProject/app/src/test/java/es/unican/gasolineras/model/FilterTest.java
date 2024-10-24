package es.unican.gasolineras.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.unican.gasolineras.common.FuelTypeEnum;
import es.unican.gasolineras.common.IFilter;

@RunWith(RobolectricTestRunner.class)
public class FilterTest {

    List<Gasolinera> l;

    private void setUp(String[] nombres, Double[] precios) {
        l = new ArrayList<>();
        Gasolinera g;
        for (int i = 0; i<nombres.length-1; i++) {
            g = new Gasolinera();
            g.setGasoleoA(precios[i]);
            g.setRotulo(nombres[i]);
            l.add(g);
        }
    }

    @Test
    public void toFilterEspecificado() {
        String[] nombres = {"t", "t", "f", "f"};
        Double[] precios = {1.8, 1.0, 1.81, 0.0};
        setUp(nombres, precios);
        IFilter filter = new Filter()
                .setMaxPrice(1.8F)
                .setFuelTypes(Collections.singletonList(FuelTypeEnum.GASOLEO_A));
        l = filter.toFilter(l);
        assertEquals(l.size(), 2);
        assertEquals(
                l.stream()
                        .filter(e -> e.getRotulo().equals("t"))
                        .count(),
                l.size()
        );
    }

    @Test
    public void toFilterNoEspecificado() {
        String[] nombres = {"t", "t", "t", "f"};
        Double[] precios = {1.8, 1.0, 1.81, 0.0};
        setUp(nombres, precios);
        IFilter filter = new Filter()
                .setFuelTypes(Collections.singletonList(FuelTypeEnum.GASOLEO_A));
        l = filter.toFilter(l);
        assertEquals(l.size(), 3);
        assertEquals(
                l.stream()
                        .filter(e -> e.getRotulo().equals("t"))
                        .count(),
                l.size()
        );
    }
}
