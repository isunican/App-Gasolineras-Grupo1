package es.unican.gasolineras.common;

import java.util.List;

import es.unican.gasolineras.model.Gasolinera;

public interface IFilter {

    public IFilter setFuelTypes(List<FuelTypeEnum> fuelTypes);

    public IFilter setGasBrands(List<String> gasBrands);

    public IFilter setMaxPrice(Double maxPrice);

    public List<Gasolinera> toFilter(List<Gasolinera> g);

    public void clear();

}
