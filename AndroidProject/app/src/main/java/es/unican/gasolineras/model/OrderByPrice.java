package es.unican.gasolineras.model;

import org.parceler.Parcel;

import java.util.Comparator;

import es.unican.gasolineras.common.FuelTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Parcel
@Getter
@Setter
public class OrderByPrice implements Comparator<Gasolinera> {

    private FuelTypeEnum fuelType;
    private Boolean ascending;


    @Override
    public int compare(Gasolinera g1, Gasolinera g2) {
        if (g1.getPrecioPorTipo(fuelType) < g2.getPrecioPorTipo(fuelType)) {
            return ascending ? -1 : 1;
        } else if (g1.getPrecioPorTipo(fuelType) > g2.getPrecioPorTipo(fuelType)) {
            return ascending ? 1 : -1;
        } else {
            return 0;
        }
    }

}
