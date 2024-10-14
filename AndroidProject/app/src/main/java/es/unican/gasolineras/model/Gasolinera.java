package es.unican.gasolineras.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

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
    
    /**
     * Returns the summary price of a gas station.
     *
     * The summary price is a weighted average of the gas valid prices of gasoline and diesel, where gasoline has a weight of 2 and diesel has a weight of 1.
     *
     * @return the summary price of a gas station.
     */
    public double getPrecioGasolina() {
        double gasolinePrice = 0;
        double dieselPrice = 0;
        double gasolineWeight = 0;
        double dieselWeight = 0;
        double summaryPrice = 0;

        if (gasolina95E5 > 0 || gasoleoA > 0) {

            if (gasolina95E5 > 0) {
                gasolinePrice = gasolina95E5;
                gasolineWeight = 2;
            }
            if (gasoleoA > 0) {
                dieselPrice = gasoleoA;
                dieselWeight = 1;
            }

            summaryPrice = (gasolinePrice * gasolineWeight + dieselPrice * dieselWeight) / (gasolineWeight + dieselWeight);
        }
        return summaryPrice;
    }
}
