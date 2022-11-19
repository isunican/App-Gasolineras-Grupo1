package es.unican.is.appgasolineras.activities.menuPrincipal;

public interface IMenuPrincipalContract {

    /**
     * A View for the Menu Principal must implement this functionality
     * These methods (excluding init), are meant to be used by the Presenter.
     */
    public interface View {
        /**
         * Initialization method
         */
        void init();

        /**
         * This method open the main view
         */
        void openMainView();

        /**
         * This method open the filtros permanentes view
         */
        void openFiltrosPermanentesView();

        /**
         * This method open the lista favoritas view
         */
        void openListaFavoritasView();
    }



}
