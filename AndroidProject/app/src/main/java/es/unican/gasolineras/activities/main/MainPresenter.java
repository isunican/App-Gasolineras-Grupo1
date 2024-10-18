package es.unican.gasolineras.activities.main;



import static es.unican.gasolineras.common.OrderMethodsEnum.Ascending;
import static es.unican.gasolineras.common.OrderMethodsEnum.Descending;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import es.unican.gasolineras.R;
import es.unican.gasolineras.common.BrandsEnum;
import es.unican.gasolineras.common.FuelTypeEnum;
import es.unican.gasolineras.common.IFilter;
import es.unican.gasolineras.common.LimitPricesEnum;
import es.unican.gasolineras.common.OrderMethodsEnum;
import es.unican.gasolineras.common.OrderTypeEnum;

import es.unican.gasolineras.model.Filter;
import es.unican.gasolineras.model.Gasolinera;
import es.unican.gasolineras.model.IDCCAAs;
import es.unican.gasolineras.model.OrderByPrice;
import es.unican.gasolineras.repository.ICallBack;
import es.unican.gasolineras.repository.IGasolinerasRepository;
import lombok.Getter;
import lombok.Setter;

/**
 * The presenter of the main activity of the application. It controls {@link MainView}
 */
public class MainPresenter implements IMainContract.Presenter {

    /** The view that is controlled by this presenter */
    private IMainContract.View view;
    private IFilter filter;
    @Getter
    @Setter
    private IFilter tempFilter;
    private List<Selection> tempListSelection;
    // get values from LimitePricesEnum converted to float and integer
    float minPriceLimit = Float.parseFloat(LimitPricesEnum.MIN_PRICE.toString());
    float maxPriceLimit = Float.parseFloat(LimitPricesEnum.MAX_PRICE.toString());
    int scalingFactor = Integer.parseInt(LimitPricesEnum.SCALING_FACTOR.toString());
    int staticSeekBarProgress = Integer.parseInt(LimitPricesEnum.STATIC_SEEKBAR_PROGRESS.toString());

    // Orden by price:
    private OrderByPrice orderByPrice = new OrderByPrice();
    private OrderTypeEnum orderType;

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
        s.add(new Selection(view.getConstantString(R.string.all_selections), allSelected));
        for (FuelTypeEnum t: FuelTypeEnum.values()) {
            s.add(
                    new Selection(t.toString(), !allSelected && f.getFuelTypes().contains(t))
            );
        }
        return s;
    }



    private List<Selection> getBrandsSelections(IFilter f) {
        List<Selection> s = new ArrayList<>();
        boolean allSelected = f.getGasBrands().size() == BrandsEnum.values().length;
        s.add(new Selection(view.getConstantString(R.string.all_selections), allSelected));
        for (BrandsEnum t: BrandsEnum.values()) {
            s.add(
                    new Selection(t.toString(), !allSelected && f.getGasBrands().contains(t))
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

    private void setFiltersPopupTextViewsSelections() {
        // Obtener la lista de selecciones de fuelTypes
        String fuelTypes = getStringOfSelections(
                getFuelTypesSelections(tempFilter));
        // Obtener la lista de selecciones de fuelBrands
        String fuelBrands = getStringOfSelections(
                getBrandsSelections(tempFilter));

        view.updateFiltersPopupTextViewsSelections(fuelTypes, fuelBrands);
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
        // Actualizar los datos de seleccion
        setFiltersPopupTextViewsSelections();
        // Actualiza los datos del precio maximo
        view.updateFiltersPopupTextViewsMaxPrice(tempFilter.getMaxPrice());
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
        tempListSelection = getBrandsSelections(tempFilter);
        view.showFiltersPopUpBrandSelector(tempListSelection);
    }

    private void filtersAlertDialogControler(int index, boolean value, int length) {
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
            if (value && numActivated < length - 1) {
                // Si se selecciona una opcion distinta de "Todos", y no esta marcando todas
                tempListSelection.get(0).setSelected(false);
                view.updateFiltersPopUpFuelTypesSelection(0, false);
            } else if (value && numActivated == length - 1) {
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
     * @see IMainContract.Presenter#onFiltersPopUpFuelTypesOneSelected(int, boolean)
     */
    @Override
    public void onFiltersPopUpFuelTypesOneSelected(int index, boolean value) {
        filtersAlertDialogControler(index, value, FuelTypeEnum.values().length);
    }


    /**
     * @see IMainContract.Presenter#onFiltersPopUpBrandsOneSelected(int, boolean)
     */
    @Override
    public void onFiltersPopUpBrandsOneSelected(int index, boolean value) {
        boolean update = true;
        if (index == 0) {
            // Si se selecciona "Todos", desmarcar todas las demas opciones
            if (value) {
                for (int i = 1; i < tempListSelection.size(); i++) {
                    tempListSelection.get(i).setSelected(false);
                    view.updateFiltersPopUpBrandsSelection(i, false);
                }
                // No se puede desmarcar todos
            } else {
                update = false;
                view.updateFiltersPopUpBrandsSelection(0, true);
            }
        } else {
            int numActivated = (int) tempListSelection.stream()
                    .skip(1)
                    .filter(Selection::isSelected)
                    .count();
            if (value && numActivated < tempListSelection.size() - 2) {
                // Si se selecciona una opcion distinta de "Todos", y no esta marcando todas
                tempListSelection.get(0).setSelected(false);
                view.updateFiltersPopUpBrandsSelection(0, false);
            } else if (value && numActivated == tempListSelection.size() - 2) {
                // Si se selecciona una opcion distinta de "Todos" y se marcan todas
                tempListSelection.get(0).setSelected(true);
                view.updateFiltersPopUpBrandsSelection(0, true);
                for (int i = 1; i < tempListSelection.size(); i++) {
                    tempListSelection.get(i).setSelected(false);
                    view.updateFiltersPopUpBrandsSelection(i, false);
                }
                update = false;
            } else if (!value && numActivated == 1) {
                tempListSelection.get(0).setSelected(true);
                view.updateFiltersPopUpBrandsSelection(0, true);
            }
        }
        if (update)
            tempListSelection.get(index).setSelected(value);
    }

    private <T> void updateSelectionToFilter(List<T> allElements,
                                         Consumer<List<T>> setter,
                                         Function<Selection, T> transformer) {
        if (tempListSelection.get(0).isSelected()) {
            setter.accept(allElements);
        } else {
            setter.accept(
                    tempListSelection.stream()
                            .filter(Selection::isSelected)
                            .map(transformer)
                            .collect(Collectors.toList())
            );
        }
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpFuelTypesAccepted()
     */
    @Override
    public void onFiltersPopUpFuelTypesAccepted() {
        updateSelectionToFilter(
                Arrays.asList(FuelTypeEnum.values()),
                tempFilter::setFuelTypes,
                e -> FuelTypeEnum.fromString(e.getValue()));
        view.updateFiltersPopupTextViewsSelections(getStringOfSelections(tempListSelection),null);
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpBrandsAccepted()
     */
    @Override
    public void onFiltersPopUpBrandsAccepted() {
        if (tempListSelection.get(0).isSelected()) {
            tempFilter.setGasBrands(Arrays.asList(BrandsEnum.values()));
        } else {
            tempFilter.setGasBrands(
                    tempListSelection.stream()
                            .filter(Selection::isSelected)
                            .map(e -> BrandsEnum.fromString(e.getValue()))
                            .collect(Collectors.toList())
            );
        }
        view.updateFiltersPopupTextViewsSelections(null, getStringOfSelections(tempListSelection));

    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpMaxPriceSeekBarChanged(int)
     */
    @Override
    public void onFiltersPopUpMaxPriceSeekBarChanged(int progress) {
        // Calcular el valor decimal del progress tipo int
        float maxPrice = minPriceLimit + (progress / (float) scalingFactor);
        // Establecer el valor maximo del filtro
        tempFilter.setMaxPrice(maxPrice);
        // Solo se muestran dos decimales en la vista
        float truncatedMaxPrice = (float) Math.round(maxPrice * 100) / 100;
        view.updateFiltersPopupTextViewsMaxPrice(truncatedMaxPrice);
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpMaxPriceSeekBarLoaded()
     */
    public void onFiltersPopUpMaxPriceSeekBarLoaded() {
        // Una regla de tres para obtener el porcentaje del valor maximo actual
        float limitPercent = staticSeekBarProgress;
        float result = (tempFilter.getMaxPrice() - minPriceLimit) / (maxPriceLimit - minPriceLimit) * limitPercent;
        // conver the float to int
        int progress = (int) result;
        view.updateFiltersPopupSeekBarProgressMaxPrice(progress);
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
        setFiltersPopupTextViewsSelections();
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
                List<Gasolinera> filtered = filter.toFilter(stations);

                if(filtered.isEmpty()){
                    view.showLoadError();
                }
                else {
                    view.showStations(filtered);
                    view.showLoadCorrect(filtered.size());
                    // Llamamos al metodo de ordenar
                    // TODO: Implementar el tipo de orden NONE.
                    Collections.sort(filtered, orderByPrice);

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
        view.showOrderPopUp();
    }

    // Definir el tipo de gasolina que se ha seleccionado
    public void onTipoGasolinaSelected(FuelTypeEnum type) {
        orderByPrice.setFuelType(type);
    }


    // Definir el tipo que se ha seleccionado.
    public void onTypeOrderSelected(OrderTypeEnum selectedTypeOrder) {
        orderType = selectedTypeOrder;
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

        if (orderByPrice.getFuelType() != null && orderByPrice.getAscending() != null) {
            // Llamar al método de carga que ya maneja la filtración y la ordenación
            load(); // Este método filtrará y ordenará según los criterios establecidos
            view.closeOrderPopUp();
        } else {
            view.closeOrderPopUp();
        }

    }

    @Override
    public void onOrderPopUpCancelClicked() {
    view.closeOrderPopUp();

    }

    public void setTempListSelection(List<Selection> selections) {
        this.tempListSelection = selections;
    }

    public List<Selection> getTempListSelection() {
        return this.tempListSelection;
    }

    public void setTempFilter(IFilter f) {
        tempFilter = f;
    }
}
