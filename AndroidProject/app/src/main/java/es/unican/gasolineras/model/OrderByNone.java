package es.unican.gasolineras.model;

import java.util.Comparator;

public class OrderByNone implements Comparator<Gasolinera> {

    @Override
    public int compare(Gasolinera g1, Gasolinera g2) {
        return 0;
    }
}
