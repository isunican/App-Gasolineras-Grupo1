package es.unican.is.appgasolineras.activities.filtroPermanente;

public interface IPermanenteContract {

    public interface Presenter {
        /**
         * Initialization method
         */
        void init();

        /**
         * This method stores the filters
         * @param idComunidad position at the spinner of the CCAA
         * @param tipoGasolina position at the spinner of the fuel
         * @param ubicacion determines if ubicacion is used or not
         */
        void guardaFiltroPermanente(int idComunidad, int tipoGasolina, boolean ubicacion);

        /**
         * This method resets the filters
         */
        void reseteaFiltroPermanente();

    }


    public interface View {

        /**
         * Initialization method
         */
        void init();

        /**
         * This method open the main view
         */
        void openMainView();
    }

}
