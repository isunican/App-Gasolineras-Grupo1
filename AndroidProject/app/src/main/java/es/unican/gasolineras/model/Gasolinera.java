package es.unican.gasolineras.model;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import android.location.Location;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import es.unican.gasolineras.common.BrandsEnum;
import es.unican.gasolineras.common.FuelTypeEnum;

/**
 * A Gas Station.
 * <p>
 * Properties are defined in the <a href="https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/help/operations/PreciosEESSTerrestres#response-json">API</a>
 * <p>
 * The #SerializedName annotation is a GSON annotation that defines the name of the property
 * as defined in the json response.
 * <p>
 * Getters are automatically generated at compile time by Lombok.
 */
@Parcel
@Entity
public class Gasolinera{

    @PrimaryKey
    @NonNull
    @SerializedName("IDEESS")                       private String id;

    @SerializedName("Rótulo")                       private String rotulo;
    @SerializedName("C.P.")                         private String cp;
    @SerializedName("Dirección")                    private String direccion;
    @SerializedName("Municipio")                    private String municipio;
    @SerializedName("Horario")                      private String horario;

    @SerializedName("Precio Gasoleo A")             private double gasoleoA;
    @SerializedName("Precio Gasolina 95 E5")        private double gasolina95E5;

    @SerializedName("Latitud")                      private double latitud;
    @SerializedName("Longitud (WGS84)")             private double longitud;

    public Gasolinera() {
        if(id == null){
            id ="";
        }
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getRotulo() {
        return rotulo;
    }

    public void setRotulo(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public double getGasoleoA() {
        return gasoleoA;
    }

    public void setGasoleoA(double gasoleoA) {
        this.gasoleoA = gasoleoA;
    }

    public double getGasolina95E5() {
        return gasolina95E5;
    }

    public void setGasolina95E5(double gasolina95E5) {
        this.gasolina95E5 = gasolina95E5;
    }

    public double getLatitud() { return latitud; }

    public void setLatitud(double latitud) {  this.latitud = latitud;}

    public double getLongitud() { return longitud; }

    public void setLongitud(double longitud) {  this.longitud = longitud; }

    public BrandsEnum getBrand(){
        return BrandsEnum.fromString(rotulo);

    }
    /**
     * Returns the summary price of a gas station.
     * The summary price is a weighted average of the gas valid prices of gasoline and diesel,
     * where gasoline has a weight of 2 and diesel has a weight of 1.
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

    /**
     * Returns the location of a gas station.
     *
     * @return the location.
     */
    public Location getLocation() {
        Location l = new Location(String.format("Location of %s", this.rotulo));
        l.setLatitude(this.latitud);
        l.setLongitude(this.longitud);
        return l;
    }

}
