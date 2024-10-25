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

    public void toFilterPrecioMaximo2a() {
        String[] nombres = {"t", "t", "f", "f"};
        Double[] diesel = {1.8, 1.0, 1.81, 0.0};
        Double[] gasolina = {1.8, 1.0, 1.81, 0.0};
        setUp(nombres, gasolina, diesel);
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
        setUp(nombres, gasolina, diesel);
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
        setUp(nombres, gasolina, diesel);
        IFilter filter = new Filter();
        l = filter.toFilter(l);
        assertEquals(l.size(), 4);
    }

    @Test
    public void toFilterPrecioMaximo2d() {
        String[] nombres = {"t", "t", "f", "f"};
        Double[] diesel = {2.0, 2.0, 2.0, 2.0};
        Double[] gasolina = {1.8, 1.0, 1.81, 0.0};
        setUp(nombres, gasolina, diesel);
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
        setUp(nombres, gasolina, diesel);
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
        assertEquals(6, l.size());
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
