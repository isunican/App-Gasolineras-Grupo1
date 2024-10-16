package es.unican.gasolineras.model;

import android.os.Build;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.unican.gasolineras.common.FuelTypeEnum;
import es.unican.gasolineras.common.IFilter;

public class Filter implements IFilter {

    private List<FuelTypeEnum> fuelTypes;
    private List<String> gasBrands;
    private Double maxPrice;

    public Filter() {
        fuelTypes = null;
        gasBrands = null;
        maxPrice = null;
    }

    private Boolean typeFilter(Gasolinera g) {
        // TODO
        return true;
    }

    private Boolean brandsFilter(Gasolinera g) {
        // TODO
        return true;
    }

    @NonNull
    private Boolean priceFilter(Gasolinera g) {
        for (FuelTypeEnum t : this.fuelTypes) {
            if (this.maxPrice < g.getPrecioPorTipo(t))
                return false;
        }
        return true;
    }

    public IFilter setFuelTypes(List<FuelTypeEnum> fuelTypes) {
        this.fuelTypes = fuelTypes;
        return this;
    }

    public IFilter setGasBrands(List<String> gasBrands) {
        this.gasBrands = gasBrands;
        return this;
    }

    public IFilter setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
        return this;
    }

    public List<Gasolinera> toFilter(List<Gasolinera> g) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            return null;
        return g.stream()
                .filter(this::typeFilter)
                .filter(this::brandsFilter)
                .filter(this::priceFilter)
                .toList();
    }

    public void clear() {
        fuelTypes = null;
        gasBrands = null;
        maxPrice = null;
    }
}
