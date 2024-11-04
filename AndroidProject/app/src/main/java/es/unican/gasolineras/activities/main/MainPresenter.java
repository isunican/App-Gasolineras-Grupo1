package es.unican.gasolineras.activities.main;



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
    @Setter
    private List<Selection> tempListSelection;

    private List<Gasolinera> gasStations;
    // get values from LimitePricesEnum converted to float and integer
    float minPriceLimit;
    float maxPriceLimit;

    int staticSeekBarProgress;
    private static final int SCALING_FACTOR = 100;

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

    @Override
    public void onPointsClicked() {
        view.showPointsActivity();
    }

    private <E> List<Selection> getSelections(List<E> selections, E[] allValues) {
        List<Selection> s = new ArrayList<>();
        boolean allSelected = selections.size() == allValues.length;
        s.add(new Selection(view.getConstantString(R.string.all_selections), allSelected));
        for (E t : allValues) {
            s.add(
                    new Selection(t.toString(), !allSelected && selections.contains(t))
            );
        }
        return s;
    }

    private String getStringOfSelections(List<Selection> s) {
        s = s.stream().filter(Selection::isSelected).collect(Collectors.toList());
        String text;
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


    private void setFiltersPopupData() {
        // Obtener la lista de selecciones de fuelTypes
        String fuelTypes = getStringOfSelections(
                getSelections(tempFilter.getFuelTypes(), FuelTypeEnum.values()));
        // Obtener la lista de selecciones de fuelBrands
        String fuelBrands = getStringOfSelections(
                getSelections(tempFilter.getGasBrands(), BrandsEnum.values()));

        // Update the view
        view.updateFiltersPopupTextViewsSelections(fuelTypes, fuelBrands);
        view.updateFiltersPopupTextViewsMaxPrice(tempFilter.getMaxPrice() == Float.MAX_VALUE ? maxPriceLimit : tempFilter.getMaxPrice());
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
        setFiltersPopupData();
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpFuelTypesSelected()
     */
    @Override
    public void onFiltersPopUpFuelTypesSelected() {
        tempListSelection = getSelections(tempFilter.getFuelTypes(), FuelTypeEnum.values());
        view.showFiltersPopUpFuelTypesSelector(tempListSelection);
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpBrandsSelected()
     */
    @Override
    public void onFiltersPopUpBrandsSelected() {
        tempListSelection = getSelections(tempFilter.getGasBrands(), BrandsEnum.values());
        view.showFiltersPopUpBrandSelector(tempListSelection);
    }

    private boolean filtersSelectionsPopUpCheckAllClicked(boolean value) {
        boolean update = true;
        if (value) {
            filtersSelectionsPopUpUnSelectOptions();
        } else {
            update = false;
            view.updateFiltersPopUpFuelTypesSelection(0, true);
        }
        return update;
    }

    private boolean filtersSelectionsPopUpCheckOtherClicked(boolean value, int length) {
        boolean update = true;
        int numActivated = (int) tempListSelection.stream()
                .skip(1)
                .filter(Selection::isSelected)
                .count();
        if (value && numActivated < length - 1) {
            // Si se selecciona una opcion distinta de "Todos", y no esta marcando todas
            filtersSelectionsPopUpSelectAllOption(false);
        } else if (value && numActivated == length - 1) {
            // Si se selecciona una opcion distinta de "Todos" y se marcan todas
            filtersSelectionsPopUpSelectAllOption(true);
            filtersSelectionsPopUpUnSelectOptions();
            update = false;
        } else if (!value && numActivated == 1) {
            filtersSelectionsPopUpSelectAllOption(true);
        }
        return update;
    }

    private void filtersSelectionsPopUpSelectAllOption(boolean value) {
        tempListSelection.get(0).setSelected(value);
        view.updateFiltersPopUpFuelTypesSelection(0, value);
    }

    private void filtersSelectionsPopUpUnSelectOptions() {
        for (int i = 1; i < tempListSelection.size(); i++) {
            tempListSelection.get(i).setSelected(false);
            view.updateFiltersPopUpFuelTypesSelection(i, false);
        }
    }

    private void filtersAlertDialogControler(int index, boolean value, int length) {
        boolean update;
        if (index == 0) {
            update = filtersSelectionsPopUpCheckAllClicked(value);
        } else {
            update = filtersSelectionsPopUpCheckOtherClicked(value, length);
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
        filtersAlertDialogControler(index, value, BrandsEnum.values().length);
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
        updateSelectionToFilter(
                Arrays.asList(BrandsEnum.values()),
                tempFilter::setGasBrands,
                e -> BrandsEnum.fromString(e.getValue()));
        view.updateFiltersPopupTextViewsSelections(null, getStringOfSelections(tempListSelection));

    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpMaxPriceSeekBarChanged(int)
     */
    @Override
    public void onFiltersPopUpMaxPriceSeekBarChanged(int progress) {
        // Calcular el valor decimal del progress tipo int
        float maxPrice = minPriceLimit + (progress / (float) SCALING_FACTOR);
        // Establecer el valor maximo del filtro
        tempFilter.setMaxPrice(maxPrice);
        // Solo se muestran dos decimales en la vista
        float truncatedMaxPrice = (float) Math.floor(maxPrice * 100) / 100;
        view.updateFiltersPopupTextViewsMaxPrice(truncatedMaxPrice);
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpMaxPriceSeekBarLoaded()
     */
    public void onFiltersPopUpMaxPriceSeekBarLoaded() {
        // Una regla de tres para obtener el porcentaje del valor maximo actual

        staticSeekBarProgress = Integer.parseInt(calculateSeekbarProgress());
        float limitPercent = staticSeekBarProgress;
        float result = (tempFilter.getMaxPrice() - minPriceLimit) / (maxPriceLimit - minPriceLimit) * limitPercent;
        // conver the float to int
        int progress = (int) result;
        view.updateFiltersPopupSeekBarProgressMaxPrice(progress);
    }

    /*
     * Actualizamos los valores máximos y mínimos del SeekBar para que sean float,
     * aunque la implementación seekbar original solo permita valores int.
     * Esto se consigue mediante la siguiente fórmula.
     */
    @Override
    public String calculateSeekbarProgress() {
        return String.valueOf((int) ((Math.ceil((maxPriceLimit - minPriceLimit) * 100) / 100) * SCALING_FACTOR));
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
        setFiltersPopupData();
        view.showInfoMessage("Se han limpiado los filtros");
    }

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
                gasStations = stations;

                // set the min and the max price for the slider
                minPriceLimit = (float) getMinPrice();
                maxPriceLimit = (float) getMaxPrice();

                try {
                    filtered = filter.toFilter(stations);
                    originalFiltered = new ArrayList<>(filtered);
                } catch (Exception e) {
                    throw new RuntimeException(e);
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
                        Collections.sort(filtered,orderByPrice);
                        view.showStations(filtered);
                        view.showLoadCorrect(filtered.size());
                    }
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

    /**
     * @see IMainContract.Presenter#onOrderClicked()
     */
    public void onOrderClicked() {
        if (orderByPrice.getFuelType() == null) {
            view.showOrderPopUp(0, 0);
        } else {
        view.showOrderPopUp((orderByPrice.getFuelType()).ordinal(), orderByPrice.getAscending() ? 0 : 1  );

        }
    }

    /**
     * @see IMainContract.Presenter#onFuelTypeSelected(FuelTypeEnum)
     */

    public void onFuelTypeSelected(FuelTypeEnum type) {
        orderByPrice.setFuelType(type);
    }

    /**
     * @see IMainContract.Presenter#onMethodOrderSelected(OrderMethodsEnum)
     */

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

    /**
     * @see IMainContract.Presenter#onOrderPopUpAcceptClicked()
     */
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

    /**
     * @see IMainContract.Presenter#onOrderPopUpCancelClicked()
     */
    @Override
    public void onOrderPopUpCancelClicked() {
    view.closeOrderPopUp();

    }

    /**
     * @see IMainContract.Presenter#onOrderPopUpClearClicked()
     */
    @Override
    public void onOrderPopUpClearClicked() {
        restoreOrder = true;
        load();
        view.closeOrderPopUp();
    }

    /**
     * @see IMainContract.Presenter#setOrderByPrice(OrderByPrice o)
     */
    @Override
    public void setOrderByPrice(OrderByPrice o) {
        this.orderByPrice = o;
    }

    /**
     * @see IMainContract.Presenter#getOrderByPrice()
     */
    @Override
    public OrderByPrice getOrderByPrice() {
        return orderByPrice;
    }


    // Comprobar los conflictos de ordenación
    private boolean checkConflicts(IFilter filter, OrderByPrice orderByPrice ) {
        FuelTypeEnum fuelType = orderByPrice.getFuelType();
        if (!filter.getFuelTypes().contains(fuelType)) {
            return true;
        }
        return false;
    }

    /**
     * This public method obtains the max price for all the gasStations when the view calls it.
     * @return the max price obtained between the gas stations
     */
    public double getMaxPrice(){
        double maxPrice = 0.0;
        for (Gasolinera gasStation : gasStations) {
            if (gasStation.getGasolina95E5() > maxPrice) {
                maxPrice = gasStation.getGasolina95E5();
            } else if (gasStation.getGasoleoA() > maxPrice) {
                maxPrice = gasStation.getGasoleoA();
            }
        }
        return maxPrice;
    }

    /*
     * This public method obtains the min price for all the gasStations when the view calls it.
     * @return the min price obtained between the gas stations
     */
    public double getMinPrice(){
        double minPrice = Double.MAX_VALUE;
        for (Gasolinera gasStation : gasStations) {
                if (gasStation.getGasolina95E5() < minPrice && gasStation.getGasolina95E5() != 0.0) {
                    minPrice = gasStation.getGasolina95E5();
                } else if (gasStation.getGasoleoA() < minPrice && gasStation.getGasoleoA() != 0.0) {
                    minPrice = gasStation.getGasoleoA();
                }
        }

        if (minPrice == Double.MAX_VALUE){
            minPrice = 0.0;
        }
        return minPrice;
    }

    @Override
    public void setTempFilter(IFilter f) {
        this.tempFilter = f;
    }

    @Override
    public IFilter getTempFilter() {
        return tempFilter;
    }

    @Override
    public IFilter getFilter() {
        return filter;
    }

    @Override
    public List<Selection> getTempListSelection() {
        return tempListSelection;
    }

}
