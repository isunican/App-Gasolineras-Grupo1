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
    private double priceG1;
    private double priceG2;

    @Override
    public int compare(Gasolinera g1, Gasolinera g2) {

        if (fuelType == null)  {
            return 0;
        }
         priceG1 = g1.getPrecioPorTipo(fuelType);
         priceG2 = g2.getPrecioPorTipo(fuelType);

        // Mover gasolineras con precio 0.0 al final
        if (priceG1 == 0.0 && priceG2 == 0.0) {
            return 0;
        } else if (priceG1 == 0.0) {
            return 1;
        } else if (priceG2 == 0.0) {
            return -1;
        } else if (priceG1 < priceG2) {
            return ascending ? -1 : 1;
        } else if (priceG1 > priceG2) {
            return ascending ? 1 : -1;
        } else {
            return 0;
        }
    }

}
