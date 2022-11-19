package es.unican.is.appgasolineras.repository.rest;

import es.unican.is.appgasolineras.model.GasolinerasResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Gasolineras API access using Retrofit
 * API: https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/help
 */
public interface GasolinerasAPI {

    @GET("EstacionesTerrestres/FiltroCCAA/{IDCCAA}")
    Call<GasolinerasResponse> gasolineras(@Path("IDCCAA") String ccaa);

    @GET("EstacionesTerrestres")
    Call<GasolinerasResponse> todasGasolineras();

    @GET("EstacionesTerrestres/FiltroMunicipio/{IDMunicipio}")
    Call<GasolinerasResponse> gasolinerasMunicipio(@Path("IDMunicipio") String idMunicipio);


}
