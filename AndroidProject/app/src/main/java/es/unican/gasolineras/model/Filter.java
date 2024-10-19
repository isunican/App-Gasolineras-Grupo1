package es.unican.gasolineras.model;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import es.unican.gasolineras.common.BrandsEnum;
import es.unican.gasolineras.common.FuelTypeEnum;
import es.unican.gasolineras.common.IFilter;
import lombok.Getter;

@Getter
public class Filter implements IFilter {

    private List<FuelTypeEnum> fuelTypes;
    private List<BrandsEnum> brands;
    private List<String> gasBrands;
    private Float maxPrice;

    public Filter() {
        fuelTypes = Arrays.asList(FuelTypeEnum.values());
        brands = Arrays.asList(BrandsEnum.values());
        gasBrands = null;
        maxPrice = Float.MAX_VALUE;
    }

    private Boolean typeFilter(Gasolinera g) {
        // TODO
        return true;
    }

    private Boolean brandsFilter(Gasolinera g) {
        return brands.contains(g.getBrand());
        //return true;
    }

    @NonNull
    private Boolean priceFilter(Gasolinera g) {
        if (g == null) return false;
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
    public IFilter setBrands(List<BrandsEnum> brands) {
        this.brands = brands;
        return this;
    }

    public IFilter setGasBrands(List<String> gasBrands) {
        this.gasBrands = gasBrands;
        return this;
    }

    public IFilter setMaxPrice(Float maxPrice) {
        this.maxPrice = maxPrice;
        return this;
    }

    public List<Gasolinera> toFilter(List<Gasolinera> g) {
        return g.stream()
                .filter(this::typeFilter)
                .filter(this::brandsFilter)
                .filter(this::priceFilter)
                .collect(Collectors.toList());
    }

    public void clear() {
        fuelTypes = Arrays.asList(FuelTypeEnum.values());
        brands = Arrays.asList(BrandsEnum.values());
        gasBrands = null;
        maxPrice = Float.MAX_VALUE;
    }

    public IFilter toCopy() {
        return new Filter()
                .setFuelTypes(fuelTypes)
                .setMaxPrice(maxPrice)
                .setBrands(brands)
                .setGasBrands(gasBrands);

    }
}
