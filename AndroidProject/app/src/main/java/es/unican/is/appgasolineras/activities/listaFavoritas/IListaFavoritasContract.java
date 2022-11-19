package es.unican.is.appgasolineras.activities.listaFavoritas;

import java.util.List;

import es.unican.is.appgasolineras.model.Gasolinera;
import es.unican.is.appgasolineras.repository.IGasolinerasRepository;

public interface IListaFavoritasContract {

    public interface Presenter {

        /**
         * Initialization method
         */
        void init();

        /**
         * This method synchronizes the favourite gas stations
         */
        void doSyncInitFavoritas();

        /**
         * This method calls the view to open detail view
         * @param index index in the list
         */
        void onGasolineraClicked(int index);

        /**
         * This method to actualize the prices of the favourites gas stations
         * @param masDeUnaComunidad una o mas comunidades
         * @param lista lista de favoritas
         * @param idComunidad id comunidad
         * @return lista con valores actualizados
         */
        List<Gasolinera> conseguirGasolinerasActualizadas(boolean masDeUnaComunidad, List<Gasolinera> lista, String idComunidad);
    }

    public interface View {

        /**
         * Initialization method
         */
        void init();

        /**
         * The View is requested to show a list of gas stations
         * @param gasolineras the list of gas stations
         */
        void showGasolineras(List<Gasolinera> gasolineras);

        /**
         * Returns the Gasolineras Repository object.
         * This object can be used to access gas stations from
         * different sources (REST API, persisted DB, etc.)
         * This method is in the View because it generally requires access to the
         * Android Context, and this is available in the View
         * @return the Repository object to access gas stations
         */
        IGasolinerasRepository getGasolineraRepository();

        /**
         * The View is requested to show an alert informing that the gas stations were loaded
         * correctly
         * @param gasolinerasCount the number of gas stations that were loaded
         */
        void showLoadCorrect(int gasolinerasCount);

        /**
         * The View is requested to show an alert informing that there was an error while
         * loading the gas stations
         */
        void showLoadErrorServidor();

        /**
         * The View is requested to show an alert informing that there was an error with the
         * network
         */
        void showLoadErrorRed();

        /**
         * This open the detail view of the gas station
         * @param gasolinera gas station to open detail
         */
        void openGasolineraDetails(Gasolinera gasolinera);

        /**
         * The View is requested to open the Menu Principal View
         */
        void openMenuPrincipal();

        /**
         * The View is requested to show an alert informing that there was an error with the
         * gas stations because none gas stations has been added yet
         */
        void showLoadErrorDAOVacia();
    }
}
