package es.unican.is.appgasolineras.repository;

import java.util.List;

import es.unican.is.appgasolineras.common.Callback;
import es.unican.is.appgasolineras.model.Gasolinera;

/**
 * A Repository to access gas stations/
 * This class abstracts away the source of the gas stations (REST API, local DB, etc.)
 */
public interface IGasolinerasRepository {

    /**
     * Request gas stations asynchronously
     * This method returns immediately. Once the gas stations have been retrieved from the source,
     * the provided callback is called
     * @param cb
     */
    public void requestGasolineras(Callback<List<Gasolinera>> cb);

    /**
     * Request gas stations synchronously
     * This method returns the list of gas stations directly, therefore it may impose a delay in
     * the execution until the list is retrieved from the source.
     * @param idCCAA id de la comunidad autonoma
     * @return the list of gas stations, or null if some error occurred
     */
    public List<Gasolinera> getGasolineras(String idCCAA);

    /**
     * Request gas stations of Spain
     * @return the list of Spain gas stations, or null if some error occurred
     */
    public List<Gasolinera> todasGasolineras();

    /**
     * Request gas stations of a municipio
     * @param idMun id of the municipio
     * @return the list of the municipio gas stations, or null if some error occurred
     */
    public List<Gasolinera> gasolinerasMunicipio(String idMun);
}
