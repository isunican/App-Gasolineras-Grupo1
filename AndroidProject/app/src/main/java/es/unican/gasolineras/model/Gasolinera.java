package es.unican.gasolineras.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import es.unican.gasolineras.common.BrandsEnum;
import es.unican.gasolineras.common.FuelTypeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * A Gas Station.
 *
 * Properties are defined in the <a href="https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/help/operations/PreciosEESSTerrestres#response-json">API</a>
 *
 * The #SerializedName annotation is a GSON annotation that defines the name of the property
 * as defined in the json response.
 *
 * Getters are automatically generated at compile time by Lombok.
 */
@Parcel
@Getter
@Setter
public class Gasolinera {

    @SerializedName("IDEESS")                       protected String id;

    @SerializedName("Rótulo")                       protected String rotulo;
    @SerializedName("C.P.")                         protected String cp;
    @SerializedName("Dirección")                    protected String direccion;
    @SerializedName("Municipio")                    protected String municipio;
    @SerializedName("Horario")                      protected String horario;


    @SerializedName("Precio Gasoleo A")             protected double gasoleoA;
    @SerializedName("Precio Gasolina 95 E5")        protected double gasolina95E5;


    protected BrandsEnum brand;


    public Gasolinera (){

        defineBrand();

    }

    
    /**
     * Returns the summary price of a gas station.
     *
     * The summary price is a weighted average of the gas valid prices of gasoline and diesel, where gasoline has a weight of 2 and diesel has a weight of 1.
     *
     * @return the summary price of a gas station.
     */
    public double getAverageGasPrice() {
        double gasolinePriceWeighted = gasolina95E5 > 0 ? gasolina95E5 * 2 : 0;
        double dieselPriceWeighted = gasoleoA > 0 ? gasoleoA : 0;

        int weight = (gasolina95E5 > 0 ? 2 : 0) + (gasoleoA > 0 ? 1 : 0);
        return  (gasolinePriceWeighted + dieselPriceWeighted) / (weight==0 ? 1 : weight);
    }

    public double getPrecioPorTipo(FuelTypeEnum t) {
        switch (t) {
            case GASOLEO_A: return this.gasoleoA;
            case GASOLINA_95E5: return this.gasolina95E5;
            default: return -1.0;
        }
    }


    private void defineBrand (){

        switch (rotulo) {
            case "Repsol":
                brand = BrandsEnum.REPSOL;
                break;

            case "Cepsa":
                brand = BrandsEnum.CEPSA;
                break;

            case "Petronor":
                brand = BrandsEnum.PETRONOR;
                break;

            case "Campsa":
                brand = BrandsEnum.CAMPSA;
                break;

            case "Galp":
                brand = BrandsEnum.GALP;
                break;

            case "Shell":
                brand = BrandsEnum.SHELL;
                break;
            case "Ballenoil":
                brand = BrandsEnum.BALLENOIL;
                break;
            case "Carrefour":
                brand = BrandsEnum.CARREFOUR;
                break;
            case "Eroski":
                brand = BrandsEnum.EROSKI;
                break;

            case "Avia":
                brand = BrandsEnum.AVIA;
                break;

            default :

                brand = BrandsEnum.OTROS;
        }


    }

}
