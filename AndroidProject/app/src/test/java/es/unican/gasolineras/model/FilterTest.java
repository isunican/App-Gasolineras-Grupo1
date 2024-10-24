package es.unican.gasolineras.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
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

    private void setUp(String[] nombres, Double[] diesel, Double[] gasolina) {
        l = new ArrayList<>();
        Gasolinera g;
        for (int i = 0; i<nombres.length; i++) {
            g = new Gasolinera();
            g.setGasoleoA(diesel[i]);
            g.setGasolina95E5(gasolina[i]);
            g.setRotulo(nombres[i]);
            l.add(g);
            System.out.println(i);
        }
    }

    @Test
    public void toFilterPrecioMaximo2a() {
        String[] nombres = {"t", "t", "f", "f"};
        Double[] diesel = {1.8, 1.0, 1.81, 0.0};
        Double[] gasolina = {1.8, 1.0, 1.81, 0.0};
        setUp(nombres, diesel, gasolina);
        IFilter filter = new Filter()
                .setMaxPrice(1.8F);
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
    public void toFilterPrecioMaximo2b() {
        String[] nombres = {"f", "f", "f", "f"};
        Double[] diesel = {1.8, 2.0, 1.81, 0.0};
        Double[] gasolina = {2.0, 1.0, 2.0, 2.0};
        setUp(nombres, diesel, gasolina);
        IFilter filter = new Filter()
                .setMaxPrice(1.8F);
        l = filter.toFilter(l);
        assertEquals(l.size(), 0);
    }

    @Test
    public void toFilterPrecioMaximo2c() {
        String[] nombres = {"t1", "t2", "t3", "t4"};
        Double[] diesel = {1.8, 1.0, 1.81, 0.0};
        Double[] gasolina = {1.8, 1.0, 1.81, 0.0};
        setUp(nombres, diesel, gasolina);
        IFilter filter = new Filter();
        l = filter.toFilter(l);
        assertEquals(l.size(), 4);
    }

    @Test
    public void toFilterPrecioMaximo2d() {
        String[] nombres = {"t", "t", "f", "f"};
        Double[] diesel = {2.0, 2.0, 2.0, 2.0};
        Double[] gasolina = {1.8, 1.0, 1.81, 0.0};
        setUp(nombres, diesel, gasolina);
        IFilter filter = new Filter()
                .setMaxPrice(1.8F)
                .setFuelTypes(Collections.singletonList(FuelTypeEnum.GASOLINA_95E5));
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
    public void toFilterPrecioMaximo2e() {
        String[] nombres = {"t", "t", "f", "f"};
        Double[] diesel = {1.8, 1.0, 1.81, 0.0};
        Double[] gasolina = {2.0, 2.0, 2.0, 2.0};
        setUp(nombres, diesel, gasolina);
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

}
