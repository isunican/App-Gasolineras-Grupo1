package es.unican.gasolineras.activities.main;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import es.unican.gasolineras.common.FuelTypeEnum;
import es.unican.gasolineras.common.IFilter;
import es.unican.gasolineras.common.OrderMethodsEnum;

import es.unican.gasolineras.model.Filter;
import es.unican.gasolineras.model.Gasolinera;
import es.unican.gasolineras.model.IDCCAAs;
import es.unican.gasolineras.model.OrderByPrice;
import es.unican.gasolineras.repository.ICallBack;
import es.unican.gasolineras.repository.IGasolinerasRepository;

/**
 * The presenter of the main activity of the application. It controls {@link MainView}
 */
public class MainPresenter implements IMainContract.Presenter {

    /** The view that is controlled by this presenter */
    private IMainContract.View view;
    private IFilter filter;
    private IFilter tempFilter;
    private List<Selection> tempListSelection;

    // Orden by price:
    private OrderByPrice orderByPrice = new OrderByPrice();
    private boolean restoreOrder = false;
    /**
     * @see IMainContract.Presenter#init(IMainContract.View)
     * @param view the view to control
     */
    @Override
    public void init(IMainContract.View view) {
        this.view = view;
        this.view.init();
        this.filter = new Filter();
        this.tempFilter = null;
        this.tempListSelection = null;
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

    private List<Selection> getFuelTypesSelections(IFilter f) {
        List<Selection> s = new ArrayList<>();
        boolean allSelected = f.getFuelTypes().size() == FuelTypeEnum.values().length;
        s.add(new Selection("Todos", allSelected));
        for (FuelTypeEnum t: FuelTypeEnum.values()) {
            s.add(
                    new Selection(t.toString(), !allSelected && f.getFuelTypes().contains(t))
            );
        }
        return s;
    }

    private String getStringOfSelections(List<Selection> s) {
        s = s.stream().filter(Selection::isSelected).collect(Collectors.toList());
        String text = "ERROR";
        switch (s.size()) {
            case 1:
                text = s.get(0).getValue();
                break;
            case 2:
                text = s.stream().map(Selection::getValue).collect(Collectors.joining(", "));
                break;
            default:
                text = "Varias (" + s.size() + ")";
                break;
        }
        return text;
    }

    ///////////////////////////////////////////////////////////////////////////////////

    private void setFiltersPopUpValues() {
        // Obtener la lista de selecciones de fuelTypes
        String fuelTypes = getStringOfSelections(
                getFuelTypesSelections(tempFilter));
        // Obtener la lista de selecciones de fuelBrands
        // TODO
        String fuelBrands = "TODOS";
        view.updateFiltersPopupTextViews(fuelTypes ,fuelBrands);
    }

    /**
     * @see IMainContract.Presenter#onFiltersClicked()
     */
    @Override
    public void onFiltersClicked() {
        // Crear el filtro temporal
        tempFilter = filter.toCopy();
        // Generar la ventana
        view.showFiltersPopUp();
        // Actualizar los datos
        setFiltersPopUpValues();
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpFuelTypesSelected()
     */
    @Override
    public void onFiltersPopUpFuelTypesSelected() {
        tempListSelection = getFuelTypesSelections(tempFilter);
        view.showFiltersPopUpFuelTypesSelector(tempListSelection);
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpBrandsSelected()
     */
    @Override
    public void onFiltersPopUpBrandsSelected() {
        // TODO
        view.showFiltersPopUpBrandSelector(Arrays.asList(
                new Selection("Todos", true),
                new Selection("Gasolina", false),
                new Selection("Diesel", false)
        ));
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpFuelTypesOneSelected(int, boolean)
     */
    @Override
    public void onFiltersPopUpFuelTypesOneSelected(int index, boolean value) {
        boolean update = true;
        if (index == 0) {
            // Si se selecciona "Todos", desmarcar todas las demas opciones
            if (value) {
                for (int i = 1; i < tempListSelection.size(); i++) {
                    tempListSelection.get(i).setSelected(false);
                    view.updateFiltersPopUpFuelTypesSelection(i, false);
                }
            // No se puede desmarcar todos
            } else {
                update = false;
                view.updateFiltersPopUpFuelTypesSelection(0, true);
            }
        } else {
            int numActivated = (int) tempListSelection.stream()
                    .skip(1)
                    .filter(Selection::isSelected)
                    .count();
            if (value && numActivated < FuelTypeEnum.values().length - 1) {
                // Si se selecciona una opcion distinta de "Todos", y no esta marcando todas
                tempListSelection.get(0).setSelected(false);
                view.updateFiltersPopUpFuelTypesSelection(0, false);
            } else if (value && numActivated == FuelTypeEnum.values().length - 1) {
                // Si se selecciona una opcion distinta de "Todos" y se marcan todas
                tempListSelection.get(0).setSelected(true);
                view.updateFiltersPopUpFuelTypesSelection(0, true);
                for (int i = 1; i < tempListSelection.size(); i++) {
                    tempListSelection.get(i).setSelected(false);
                    view.updateFiltersPopUpFuelTypesSelection(i, false);
                }
                update = false;
            } else if (!value && numActivated == 1) {
                tempListSelection.get(0).setSelected(true);
                view.updateFiltersPopUpFuelTypesSelection(0, true);
            }
        }
        if (update)
            tempListSelection.get(index).setSelected(value);
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpBrandsOneSelected(int, boolean)
     */
    @Override
    public void onFiltersPopUpBrandsOneSelected(int index, boolean value) {
        // TODO: Se puede refactorizar lo de arriba y hacerlo facil
        boolean[] seleccionadas = {false, true, false};
        seleccionadas[index] = value;
        if (index == 0) {
            // Si se selecciona "Todos", desmarcar todas las demas opciones
            if (value) {
                for (int i = 1; i < seleccionadas.length; i++) {
                    seleccionadas[i] = false;
                    view.updateFiltersPopUpBrandsSelection(i, false);
                }
            }
        } else {
            // Si se selecciona una opcion distinta de "Todos", desmarcar "Todos"
            if (value) {
                seleccionadas[0] = false;
                view.updateFiltersPopUpBrandsSelection(0, false);
            }
        }
        seleccionadas[index] = value;
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpFuelTypesAccepted()
     */
    @Override
    public void onFiltersPopUpFuelTypesAccepted() {
        if (tempListSelection.get(0).isSelected()) {
            tempFilter.setFuelTypes(Arrays.asList(FuelTypeEnum.values()));
        } else {
            tempFilter.setFuelTypes(
                    tempListSelection.stream()
                            .filter(Selection::isSelected)
                            .map(e -> FuelTypeEnum.fromString(e.getValue()))
                            .collect(Collectors.toList())
            );
        }
        view.updateFiltersPopupTextViews(getStringOfSelections(tempListSelection), null);
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpBrandsAccepted()
     */
    @Override
    public void onFiltersPopUpBrandsAccepted() {
        // TODO
        view.updateFiltersPopupTextViews(null, "Todos");
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpCancelClicked()
     */
    public void onFiltersPopUpCancelClicked() {
        tempFilter = null;
        tempListSelection = null;
        view.closeFiltersPopUp();
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpAcceptClicked()
     */
    public void onFiltersPopUpAcceptClicked() {
        filter = tempFilter;
        tempFilter = null;
        tempListSelection = null;
        view.closeFiltersPopUp();
        load();
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpClearFiltersClicked()
     */
    public void onFiltersPopUpClearFiltersClicked() {
        tempFilter.clear();
        setFiltersPopUpValues();
        view.showInfoMessage("Se han limpiado los filtros");
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
                List<Gasolinera> filtered = null;
                List<Gasolinera> originalFiltered = null;
                try {
                    filtered = filter.toFilter(stations);
                    originalFiltered = new ArrayList<>(filtered);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                }

                if(filtered.isEmpty()){
                    view.showLoadError();
                }
                else {
                    if (restoreOrder) {
                        view.showStations(originalFiltered);
                        view.showLoadCorrect(originalFiltered.size());
                    }
                    else {
                        Collections.sort(filtered,orderByPrice );
                        view.showStations(filtered);
                        view.showLoadCorrect(filtered.size());
                    }


                    // Llamamos al metodo de ordenar
                    // TODO: Implementar el tipo de orden NONE.

                }

            }

            @Override
            public void onFailure(Throwable e) {
                view.showLoadError();
                view.showLoadError();
            }
        };

        repository.requestGasolineras(callBack, IDCCAAs.CANTABRIA.id);
    }

    // Methods for Ordering story user

    public void onOrderClicked() {
        if (orderByPrice.getFuelType() == null) {
            view.showOrderPopUp(0, 0);
        } else {
        view.showOrderPopUp((orderByPrice.getFuelType()).ordinal(), orderByPrice.getAscending() ? 0 : 1  );

        }
    }

    // Definir el tipo de gasolina que se ha seleccionado
    public void onFuelTypeSelected(FuelTypeEnum type) {
        orderByPrice.setFuelType(type);
    }

    // Definir el orden que se ha seleccionado.
    public void onMethodOrderSelected(OrderMethodsEnum orderMethod) {
        switch (orderMethod) {
            case Ascending:
                orderByPrice.setAscending(true);  // Asigna directamente si es ascendente
                break;
            case Descending:
                orderByPrice.setAscending(false); // Asigna directamente si es descendente
                break;
            default:
                orderByPrice.setAscending(null); // Manejo de "sin orden"
                break;
        }
    }

    @Override
    public void onOrderPopUpAcceptClicked() {
        // Llamar al método de carga que ya maneja la filtración y la ordenación
        if (checkConflicts(filter, orderByPrice)) {
            // Reestablecer el filtro a todos.
            filter.setFuelTypes(Arrays.asList(FuelTypeEnum.values()));
            view.showInfoMessage("Conflicto con filtro de Combustible, se ha restablecido el filtro.");
        }
        // Este método filtrará y ordenará según los criterios establecidos
        restoreOrder = false;
        load();
        view.closeOrderPopUp();
    }

    @Override
    public void onOrderPopUpCancelClicked() {
    view.closeOrderPopUp();

    }

    @Override
    public void onOrderPopUpClearClicked() {
        restoreOrder = true;
        load();
        view.closeOrderPopUp();
    }

    // Comprobar los conflictos de ordenación
    private boolean checkConflicts(IFilter filter, OrderByPrice orderByPrice ) {
        FuelTypeEnum fuelType = orderByPrice.getFuelType();
        if (!filter.getFuelTypes().contains(fuelType)) {
            return true;
        }
        return false;
    }


}
