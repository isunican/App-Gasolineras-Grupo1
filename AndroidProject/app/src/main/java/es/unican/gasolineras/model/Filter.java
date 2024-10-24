package es.unican.gasolineras.model;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import es.unican.gasolineras.R;
import es.unican.gasolineras.common.BrandsEnum;
import es.unican.gasolineras.common.FuelTypeEnum;
import es.unican.gasolineras.common.IFilter;
import es.unican.gasolineras.common.LimitPricesEnum;
import lombok.Getter;

@Getter
public class Filter implements IFilter {

    private List<FuelTypeEnum> fuelTypes;
    private List<BrandsEnum> gasBrands;
    private Float maxPrice;

    public Filter() {
        fuelTypes = Arrays.asList(FuelTypeEnum.values());
        gasBrands = Arrays.asList(BrandsEnum.values());
        maxPrice = Float.MAX_VALUE;
    }

    private Boolean typeFilter(Gasolinera g) {
        for (FuelTypeEnum f : this.fuelTypes) {
            if (g.getPrecioPorTipo(f) == 0.0)
                return false;
        }
        return true;
    }

    private Boolean brandsFilter(Gasolinera g) {
        if (g == null) return false;
        return gasBrands.contains(g.getBrand());
    }

    @SuppressLint("UseValueOf")
    @NonNull
    private Boolean priceFilter(Gasolinera g) {
        if (g == null) return false;
        double p;
        for (FuelTypeEnum t : this.fuelTypes) {
            p = (float) g.getPrecioPorTipo(t);
            if (this.maxPrice < p || p == 0.0 )
                return false;
        }
        return true;
    }

    public IFilter setFuelTypes(List<FuelTypeEnum> fuelTypes) {
        this.fuelTypes = fuelTypes;
        return this;
    }

    public IFilter setGasBrands(List<BrandsEnum> brands) {
        this.gasBrands = brands;
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
        gasBrands = Arrays.asList(BrandsEnum.values());
        maxPrice = Float.MAX_VALUE;
    }

    public IFilter toCopy() {
        return new Filter()
                .setFuelTypes(fuelTypes)
                .setMaxPrice(maxPrice)
                .setGasBrands(gasBrands);

    }
}
