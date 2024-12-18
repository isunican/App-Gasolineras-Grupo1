package es.unican.gasolineras.model;

import org.junit.Assert;
import org.junit.Test;

import es.unican.gasolineras.common.FuelTypeEnum;

public class SorterByPriceTest {

    private SorterByPrice sorterByPrice = new SorterByPrice();

    @Test
    public void compare(){

        Gasolinera repsol = createGasolinera("CEPSA",1.679,1.509);
        Gasolinera cepsa = createGasolinera("REPSOL",1.669,1.569);
        Gasolinera petronor = createGasolinera("PETRONOR",1.639,1.525);
        Gasolinera petronorCopia = createGasolinera("PETRONOR V2",1.639,1.525);
        Gasolinera redetrans = createGasolinera("REDETRANS",0.0,1.299);
        Gasolinera eroski = createGasolinera("EROSKI",1.629,0.0);
        Gasolinera alsa = createGasolinera("ALSA",1.639,1.525);

        //UD1a
        sorterByPrice.setFuelType(FuelTypeEnum.GASOLINA_95E5);
        sorterByPrice.setAscending(true);
        Assert.assertEquals(1, sorterByPrice.compare(repsol,cepsa));

        //UD1b
        Assert.assertEquals(-1, sorterByPrice.compare(cepsa,repsol));

        //UD1c
        sorterByPrice.setFuelType(FuelTypeEnum.GASOLEO_A);
        sorterByPrice.setAscending(false);
        Assert.assertEquals(1, sorterByPrice.compare(repsol,cepsa));

        //UD1d
        Assert.assertEquals(-1, sorterByPrice.compare(cepsa,repsol));

        //UD1e
        sorterByPrice.setFuelType(FuelTypeEnum.GASOLINA_95E5);
        sorterByPrice.setAscending(false);
        Assert.assertEquals(0, sorterByPrice.compare(petronor,petronorCopia));

        //UD1f
        sorterByPrice.setFuelType(FuelTypeEnum.GASOLINA_95E5);
        sorterByPrice.setAscending(false);
        Assert.assertEquals(1, sorterByPrice.compare(redetrans,eroski));

        //UD1g
        sorterByPrice.setFuelType(FuelTypeEnum.GASOLEO_A);
        sorterByPrice.setAscending(false);
        Assert.assertEquals(-1, sorterByPrice.compare(redetrans,eroski));

        //UD1h
        sorterByPrice.setFuelType(FuelTypeEnum.GASOLEO_A);
        sorterByPrice.setAscending(false);
        Assert.assertEquals(1, sorterByPrice.compare(redetrans,alsa));
    }

    private Gasolinera createGasolinera( String rotulo, double gasolina, double diesel){

        Gasolinera output = new Gasolinera();

        output.setRotulo(rotulo);
        output.setGasolina95E5(gasolina);
        output.setGasoleoA(diesel);

        return output;
    }

}