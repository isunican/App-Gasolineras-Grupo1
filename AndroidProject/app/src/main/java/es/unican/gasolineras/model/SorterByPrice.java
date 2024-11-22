package es.unican.gasolineras.model;

import org.parceler.Parcel;

import java.util.Comparator;

import es.unican.gasolineras.common.FuelTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Parcel
@Getter
@Setter
public class SorterByPrice implements Comparator<Gasolinera> {

    private FuelTypeEnum fuelType;
    private Boolean ascending;
    private double priceG1;
    private double priceG2;

    /**
     * Method compare.
     * @param g1 the first station object to be compared.
     * @param g2 the second station object to be compared.
     * @return the following cases can happen:
     * 0 if the fueltype is not defined (in order to show all the stations with the original order)
     * 0 if the prices are equal or both prices are 0.0
     * 1 if priceG1 is zero and priceG2 is not zero
     * -1 if priceG1 is not zero and priceG2 is zero
     * 1 if the first price is greater than the second price and the order is ascending, -1 if is descending
     * -1 if the first price is less than the second price and the order is ascending, 1 if is descending
     */
    @Override
    public int compare(Gasolinera g1, Gasolinera g2) {
        // CEPSA, REPSOL
        // REPSOL < CEPSA
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
