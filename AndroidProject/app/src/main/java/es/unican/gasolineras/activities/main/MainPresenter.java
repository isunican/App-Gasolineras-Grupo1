package es.unican.gasolineras.activities.main;

import java.util.Arrays;
import java.util.List;

import es.unican.gasolineras.model.Gasolinera;
import es.unican.gasolineras.model.IDCCAAs;
import es.unican.gasolineras.repository.ICallBack;
import es.unican.gasolineras.repository.IGasolinerasRepository;

/**
 * The presenter of the main activity of the application. It controls {@link MainView}
 */
public class MainPresenter implements IMainContract.Presenter {

    /** The view that is controlled by this presenter */
    private IMainContract.View view;

    /**
     * @see IMainContract.Presenter#init(IMainContract.View)
     * @param view the view to control
     */
    @Override
    public void init(IMainContract.View view) {
        this.view = view;
        this.view.init();
        load();
    }

    /**
     * @see IMainContract.Presenter#onStationClicked(Gasolinera)
     * @param station the station that has been clicked
     */
    @Override
    public void onStationClicked(Gasolinera station) {
        view.showStationDetails(station);
    }

    /**
     * @see IMainContract.Presenter#onMenuInfoClicked()
     */
    @Override
    public void onMenuInfoClicked() {
        view.showInfoActivity();
    }

    ///////////////////////////////////////////////////////////////////////////////////

    /*

    [Metodo que tenia hecho para fijar texto segun las selecciones (List<Selection>)]

    selections = selections.stream().filter(Selection::isSelected).toList();
    String text = "ERROR";
    switch (selections.size()) {
        case 1:
            text = selections.get(0).getValue();
            break;
        case 2:
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                text = String.join(", ", selections
                        .stream().map(Selection::getValue).toList());
            }
            break;
        default:
            text = "Varias (" + selections.size() + ")";
            break;
    }

     */
    /**
     * @see IMainContract.Presenter#onFiltersClicked()
     */
    @Override
    public void onFiltersClicked() {
        /*
        Debe de pasar en texto que tiene seleccionado, pasaria lo siguiente:
        - 1 seleccon: El texto de la seleecion
        - 2 selecciones: El texto de las selecciones separados por una coma
        - 3 o mas: Varias (el numero de selecciones)
         */
        view.showFiltersPopUp("Todos");
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpFuelTypesSelected()
     */
    @Override
    public void onFiltersPopUpFuelTypesSelected() {
        /*
        Debe de pasar una lista con las selecciones que muestrara cuando se haga
        click en el tipo de combustible
         */
        view.showFiltersPopUpFuelTypesSelector(Arrays.asList(
                new Selection("Todos", true),
                new Selection("Gasolina", false),
                new Selection("Diesel", false)
        ));
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpFuelTypesOneSelecionated(int, boolean)
     */
    @Override
    public void onFiltersPopUpFuelTypesOneSelecionated(int index, boolean value) {
        /*
        La funcion que es llamada cuando se seleeciona una funcion, se debe comprobar:
        - Si esta marcado "todos" y se marca otra, se quita la de "todos"
        - Si hay marcadas varias, y se selecciona todos, solo debe de estar marcado "todos"
        - Si se intenta desmarcar y dejar ninguna, o que no te deje o que se marque "todos" solo
        - Si marcas todas menos "todos", se deberian de desmarcar todas y marcar solo "todos"

        Se puede cambiar el valor llamando a view.updateFiltersPopUpFuelTypesSelection(index, valor)
         */
        // Ejemplo que me dio chatgpt, no funciona en todos los casos pero es una base
        boolean[] seleccionadas = {false, true, false};
        seleccionadas[index] = value;
        if (index == 0) {
            // Si se selecciona "Todos", desmarcar todas las demas opciones
            if (value) {
                for (int i = 1; i < seleccionadas.length; i++) {
                    seleccionadas[i] = false;
                    view.updateFiltersPopUpFuelTypesSelection(i, false);
                }
            }
        } else {
            // Si se selecciona una opcion distinta de "Todos", desmarcar "Todos"
            if (value) {
                seleccionadas[0] = false;
                view.updateFiltersPopUpFuelTypesSelection(0, false);
            }
        }
        seleccionadas[index] = value;
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpFuelTypesAccepted()
     */
    @Override
    public void onFiltersPopUpFuelTypesAccepted() {
        view.updateFiltersPopupFuelTypesTextView("Todos");
    }
    ///////////////////////////////////////////////////////////////////////////////////

    /**
     * Loads the gas stations from the repository, and sends them to the view
     */
    private void load() {
        IGasolinerasRepository repository = view.getGasolinerasRepository();

        ICallBack callBack = new ICallBack() {

            @Override
            public void onSuccess(List<Gasolinera> stations) {
                view.showStations(stations);
                view.showLoadCorrect(stations.size());
            }

            @Override
            public void onFailure(Throwable e) {
                view.showLoadError();
                view.showLoadError();
            }
        };

        repository.requestGasolineras(callBack, IDCCAAs.CANTABRIA.id);
    }

}
