package es.unican.is.appgasolineras.activities.detail;


public interface IDetailContract {
    public interface Presenter {
        /**
         * Initialization method
         */
        void init();

        /**
         * Calcula la media ponderada entre diesel y gasoliner
         */
        String calcula();

        void anhadeADb();
    }
    public interface View {
        /**
         * This method set the info of the gas station
         * @param municipio city of the gas station
         * @param rotulo name of the gas station
         * @param horario schedule of the gas station
         * @param normal95 price of the gasolina
         * @param dieselA price of the diesel
         * @param media price 2/3 gasolina + 1/3 diesel
         * @param direccion direction of the gas station
         */
        void setInfo(String municipio, String rotulo, String horario, String normal95,
                     String dieselA, String media, String direccion);

        /**
         * This method shows a toast to indicate that the gas station has been added
         */
        void showAnhadido();

        /**
         * This method shows a toast to indicate that the gas station had been already added
         */
        void showNoAnhadido();
    }
}

