package es.unican.gasolineras.common;

import java.util.Collection;
import java.util.List;

import es.unican.gasolineras.model.Gasolinera;

public interface IFilter {

    public IFilter setFuelTypes(List<FuelTypeEnum> fuelTypes);

    public IFilter setGasBrands(List<BrandsEnum> gasBrands);

    public List<BrandsEnum> getGasBrands();

    public IFilter setMaxPrice(Double maxPrice);

    public List<FuelTypeEnum> getFuelTypes();

    public Double getMaxPrice();

    public List<Gasolinera> toFilter(List<Gasolinera> g);

    public void clear();

    public IFilter toCopy();

}
