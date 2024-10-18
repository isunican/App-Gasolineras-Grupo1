package es.unican.gasolineras.activities.main;

import java.util.List;

import es.unican.gasolineras.common.FuelTypeEnum;
import es.unican.gasolineras.common.OrderMethodsEnum;
import es.unican.gasolineras.common.OrderTypeEnum;
import es.unican.gasolineras.model.Gasolinera;
import es.unican.gasolineras.repository.IGasolinerasRepository;

/**
 * The Presenter-View contract for the Main activity.
 * The Main activity shows a list of gas stations.
 */
public interface IMainContract {

    /**
     * Methods that must be implemented in the Main Presenter.
     * Only the View should call these methods.
     */
    public interface Presenter {

        /**
         * Links the presenter with its view.
         * Only the View should call this method
         * @param view
         */
        public void init(View view);

        /**
         * The presenter is informed that a gas station has been clicked
         * Only the View should call this method
         * @param station the station that has been clicked
         */
        public void onStationClicked(Gasolinera station);

        /**
         * The presenter is informed that the Info item in the menu has been clicked
         * Only the View should call this method
         */
        public void onMenuInfoClicked();

        /**
         * The presenter is informed that the filter item in the toolbar has been clicked
         * Only the View should call this method
         */
        public void onFiltersClicked();

        /**
         * The presenter is informed that the fuel type item in the filter popup has been clicked
         * Only the View should call this method
         */
        public void onFiltersPopUpFuelTypesSelected();



        /**
         * The presenter is informed that the fuel brand item in the filter popup has been clicked
         * Only the View should call this method
         */
        public void onFiltersPopUpBrandsSelected();




        /**
         * The presenter is informed that one of fuel type item in the fuel type filter popup has been clicked
         * Only the View should call this method
         */
        public void onFiltersPopUpFuelTypesOneSelected(int index, boolean value);


        /**
         * The presenter is informed that one of fuel type item in the fuel type filter popup has been clicked
         * Only the View should call this method
         */
        public void onFiltersPopUpBrandsOneSelected(int index, boolean value);



        /**
         * The presenter is informed that the fuel type in the filter popup has been clicked in accept
         * Only the View should call this method
         */
        public void onFiltersPopUpFuelTypesAccepted();

        /**
         * The presenter is informed that the brand in the filter popup has been clicked in accept
         * Only the View should call this method
         */
        public void onFiltersPopUpBrandsAccepted();


        /**
         * The presenter is informed that the cancel button in the filter popup has been clicked
         * Only the View should call this method
         */
        public void onFiltersPopUpCancelClicked();

        /**
         * The presenter is informed that the accept button in the filter popup has been clicked
         * Only the View should call this method
         */
        public void onFiltersPopUpAcceptClicked();

        /**
         * The presenter is informed that the clear filters button in the filter popup has been clicked
         * Only the View should call this method
         */
        public void onFiltersPopUpClearFiltersClicked();

        // Methods for Ordering story user

        /**
         * The presenter is informed that the order item in the toolbar has been clicked
         * Only the view should call this method
         */
        public void onOrderClicked();

        /**
         * The presenter is informed that the fuel type item in the order popup has been selected
         * Only the View should call this method
         */
        public void onTipoGasolinaSelected(FuelTypeEnum type);

        /**
         * The presenter is informed that the order type item in the order popup has been selected
         */
        public void onTypeOrderSelected(OrderTypeEnum selectedTypeOrder);

        /**
         * The presenter is informed that the order method  item in the order popup has been selected
         * Only the View should call this method
         */
        public void onMethodOrderSelected(OrderMethodsEnum orderMethod);

        /**
         * The presenter is informed that the accept button in the order popup has been clicked
         * Only the View should call this method
         */
        public void onOrderPopUpAcceptClicked();

        /**
         * The presenter is informed that the cancel button in the order popup has been clicked
         * Only the View should call this method
         */
        public void onOrderPopUpCancelClicked();
    }

    /**
     * Methods that must be implemented in the Main View.
     * Only the Presenter should call these methods.
     */
    public interface View {

        /**
         * Initialize the view. Typically this should initialize all the listeners in the view.
         * Only the Presenter should call this method
         */
        public void init();

        /**
         * Returns a repository that can be called by the Presenter to retrieve gas stations.
         * This method must be located in the view because Android resources must be accessed
         * in order to instantiate a repository (for example Internet Access). This requires
         * dependencies to Android. We want to keep the Presenter free of Android dependencies,
         * therefore the Presenter should be unable to instantiate repositories and must rely on
         * the view to create the repository.
         * Only the Presenter should call this method
         * @return
         */
        public IGasolinerasRepository getGasolinerasRepository();

        /**
         * The view is requested to display the given list of gas stations.
         * Only the Presenter should call this method
         * @param stations the list of charging stations
         */
        public void showStations(List<Gasolinera> stations);

        /**
         * The view is requested to display a notification indicating  that the gas
         * stations were loaded correctly.
         * Only the Presenter should call this method
         * @param stations
         */
        public void showLoadCorrect(int stations);

        /**
         * The view is requested to display a notificacion indicating that the gas
         * stations were not loaded correctly.
         * Only the Presenter should call this method
         */
        public void showLoadError();

        /**
         * The view is requested to display a notificacion indicating a message.
         * Only the Presenter should call this method
         */
        public void showInfoMessage(String message);

        /**
         * The view is requested to display the detailed view of the given gas station.
         * Only the Presenter should call this method
         * @param station the charging station
         */
        public void showStationDetails(Gasolinera station);

        /**
         * The view is requested to open the info activity.
         * Only the Presenter should call this method
         */
        public void showInfoActivity();

        /**
         * The view is requested to open the filters popup.
         * Only the Presenter should call this method
         */
        public void showFiltersPopUp();

        /**
         * The view is requested to to open the filters fuel type selector.
         * Only the Presenter should call this method
         * @param selections list of the selections of the fuel types
         */
        public void showFiltersPopUpFuelTypesSelector(List<Selection> selections);


        /**
         * The view is requested to to open the filters brand type selector.
         * Only the Presenter should call this method
         * @param selections list of the selections of the brands
         */
        public void showFiltersPopUpBrandSelector(List<Selection> selections);




        /**
         * The view is requested to to update the filters textviews.
         * Only the Presenter and View should call this method
         * @param fuelTypes the string to update the textView fuel type
         */
        public void updateFiltersPopupTextViews(String fuelTypes, String fuelBrands);

        /**
         * The view is requested to to update the selection of a fuel type selector.
         * Only the Presenter should call this method
         * @param position the position of the selection
         * @param value the new value
         */
        public void updateFiltersPopUpFuelTypesSelection(int position, boolean value);


        /**
         * The view is requested to to update the selection of a fuel type selector.
         * Only the Presenter should call this method
         * @param position the position of the selection
         * @param value the new value
         */
        public void updateFiltersPopUpBrandsSelection(int position, boolean value);



        /**
         * The view is requested to close the filters popup.
         * Only the Presenter should call this method
         */
        public void closeFiltersPopUp();


        // Methods for the Ordering story user
        /**
         * The view is requested to open the filters popup.
         * Only the Presenter should call this method
         */

        public void showOrderPopUp();

        /**
         * The view is requested to close the filters popup
         * Only the Presenter should call this method.
         */

        public void closeOrderPopUp();

    }
}
