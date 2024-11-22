package es.unican.gasolineras.activities.main;

import android.database.sqlite.SQLiteException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import es.unican.gasolineras.R;
import es.unican.gasolineras.common.BrandsEnum;
import es.unican.gasolineras.common.FuelTypeEnum;
import es.unican.gasolineras.common.IFilter;
import es.unican.gasolineras.common.OrderMethodsEnum;
import es.unican.gasolineras.common.database.IGasStationsDAO;
import es.unican.gasolineras.model.Filter;
import es.unican.gasolineras.model.Gasolinera;
import es.unican.gasolineras.model.IDCCAAs;
import es.unican.gasolineras.model.InterestPoint;
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
    @Setter
    @Getter
    private InterestPoint interestPoint;

    private List<Gasolinera> gasStations;
    private List<Gasolinera> initialGasStations;

    private float minPriceLimit;
    private float maxPriceLimit;

    int staticSeekBarProgress;
    private static final int SCALING_FACTOR = 100;

    private boolean hasConnection;
    public MainPresenter(InterestPoint point) {
        interestPoint = point;
    }

    public MainPresenter() { }

    // Orden by price:
    private OrderByPrice orderByPrice = new OrderByPrice();
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
        view.showPointsActivity(interestPoint != null);
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
        updateFiltersPriceData();
    }

    /**
     * This method updates the price filter value and the seekbar when its called.
     * It is used when the user applies or restores the filters.
     * It calls the view to update al the data.
     */
    private void updateFiltersPriceData() {
        // Actualizamos el precio.
        view.updateFiltersPopupTextViewsMaxPrice(tempFilter.getMaxPrice() == Float.MAX_VALUE ? maxPriceLimit : tempFilter.getMaxPrice());
        // Actualizamos el seekbar.
        if (tempFilter.getMaxPrice() == Float.MAX_VALUE) {
            view.updateFiltersPopupSeekBarProgressMaxPrice(Integer.parseInt(calculateSeekbarProgress()), minPriceLimit, maxPriceLimit);
        }

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
            view.updateFiltersPopUpSelection(0, true);
        }
        return update;
    }

    private boolean filtersSelectionsPopUpCheckOtherClicked(boolean value) {
        boolean update = true;
        int numActivated = (int) tempListSelection.stream()
                .skip(1)
                .filter(Selection::isSelected)
                .count();
        if (value && numActivated < tempListSelection.size() - 2) {
            // Si se selecciona una opcion distinta de "Todos", y no esta marcando todas
            filtersSelectionsPopUpSelectAllOption(false);
        } else if (value && numActivated == tempListSelection.size() - 2) {
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
        view.updateFiltersPopUpSelection(0, value);
    }

    private void filtersSelectionsPopUpUnSelectOptions() {
        for (int i = 1; i < tempListSelection.size(); i++) {
            tempListSelection.get(i).setSelected(false);
            view.updateFiltersPopUpSelection(i, false);
        }
    }

    private void filtersAlertDialogControler(int index, boolean value) {
        boolean update;
        if (index == 0) {
            update = filtersSelectionsPopUpCheckAllClicked(value);
        } else {
            update = filtersSelectionsPopUpCheckOtherClicked(value);
        }
        if (update)
            tempListSelection.get(index).setSelected(value);
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpFuelTypesOneSelected(int, boolean)
     */
    @Override
    public void onFiltersPopUpFuelTypesOneSelected(int index, boolean value) {
        filtersAlertDialogControler(index, value);
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpBrandsOneSelected(int, boolean)
     */
    @Override
    public void onFiltersPopUpBrandsOneSelected(int index, boolean value) {
        filtersAlertDialogControler(index, value);
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
        view.updateFiltersPopupSeekBarProgressMaxPrice(progress, minPriceLimit, maxPriceLimit);
    }


    /**
     * ctualizamos los valores máximos y mínimos del SeekBar para que sean float,
     * aunque la implementación seekbar original solo permita valores int.
     * Esto se consigue mediante la siguiente fórmula.
     *
     * @see IMainContract.Presenter#onFiltersPopUpCancelClicked()
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
        view.closeActivePopUp();
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpAcceptClicked()
     */
    public void onFiltersPopUpAcceptClicked() {
        filter = tempFilter;
        tempFilter = null;
        tempListSelection = null;
        view.closeActivePopUp();
        applyFilters();
        showGasStations();
    }

    /**
     * @see IMainContract.Presenter#onFiltersPopUpClearFiltersClicked()
     */
    public void onFiltersPopUpClearFiltersClicked() {
        tempFilter.clear();
        setFiltersPopupData();
        view.showInfoMessage("Se han limpiado los filtros");
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
            case ASCENDING:
                orderByPrice.setAscending(true);  // Asigna directamente si es ascendente
                break;
            case DESCENDING:
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
        applyOrder();
        showGasStations();
        view.closeActivePopUp();
    }

    /**
     * @see IMainContract.Presenter#onOrderPopUpCancelClicked()
     */
    @Override
    public void onOrderPopUpCancelClicked() {
    view.closeActivePopUp();

    }

    /**
     * @see IMainContract.Presenter#onOrderPopUpClearClicked()
     */
    @Override
    public void onOrderPopUpClearClicked() {
        applyOrder();
        showGasStations();
        view.closeActivePopUp();
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
        return !filter.getFuelTypes().contains(fuelType);
    }

    /**
     * @see IMainContract.Presenter#getMaxPrice()
     */
    public double getMaxPrice(){
        double maxPrice = 0.0;
        for (Gasolinera gasStation : initialGasStations) {
            if (gasStation.getGasolina95E5() > maxPrice) {
                maxPrice = gasStation.getGasolina95E5();
            } else if (gasStation.getGasoleoA() > maxPrice) {
                maxPrice = gasStation.getGasoleoA();
            }
        }

        //round to two decimal places
        maxPrice =Math.ceil(maxPrice * 100.00) /100.00;

        return maxPrice;
    }

    /**
     * @see IMainContract.Presenter#getMinPrice()
     */
    public double getMinPrice(){
        double minPrice = Double.MAX_VALUE;
        List<FuelTypeEnum> listToIter = (filter.getFuelTypes() == null || filter.getFuelTypes().isEmpty()) ?
                Arrays.asList(FuelTypeEnum.values()) : filter.getFuelTypes();

        for (Gasolinera gasStation : gasStations) {
            double maxInTypes = Double.MIN_VALUE;
            boolean valid = true;
            for (FuelTypeEnum t : listToIter) {
                double price = gasStation.getPrecioPorTipo(t);
                if (price == 0.0)
                    valid = false;
                else if (price > maxInTypes)
                    maxInTypes = price;
            }
            if (valid && maxInTypes != Double.MIN_VALUE && maxInTypes < minPrice) {
                minPrice = maxInTypes;
            }
        }
        if (minPrice == Double.MAX_VALUE){
            minPrice = 0.0;
        }
        //round to two decimal places
        minPrice = Math.ceil(minPrice * 100.00) /100.00;
        return minPrice;
    }

    @Override
    public IFilter getFilter() {
        return filter;
    }

    @Override
    public List<Selection> getTempListSelection() {
        return tempListSelection;
    }

    private void initialiceGasStationsList(List<Gasolinera> stations) {
        if (interestPoint != null) {
            stations = stations.stream()
                    .filter(interestPoint::isGasStationInRadius)
                    .collect(Collectors.toList());
        }
        initialGasStations = stations;
        gasStations = stations;

        applyFilters();
        if (interestPoint != null) {
            view.showInterestPointInfo(interestPoint, gasStations.size());
        }
        showGasStations();
    }

    /**
     * Loads the gas stations from the repository, and sends them to the view
     */
    private void load() {
        IGasolinerasRepository repository = view.getGasolinerasRepository();

        ICallBack callBack = new ICallBack() {

            @Override
            public void onSuccess(List<Gasolinera> stations) {
                try {
                    hasConnection = true;
                    persistGasStationsOnLocalDB(stations);
                } catch (SQLiteException exception1) {
                    view.showInfoMessage("Error al guardar datos en la base de datos");
                }catch (Exception exception){
                    view.showInfoMessage("Error al mostrar las gasolineras");
                } finally {
                    initialiceGasStationsList(stations);
                }

            }

            @Override
            public void onFailure(Throwable e) {
                try {
                    hasConnection = false;
                    List<Gasolinera> stations = getGasStationsFromLocalDB();
                    initialiceGasStationsList(stations);
                    if (stations.isEmpty()) {
                        view.showInfoMessage("No hay datos guardados de gasolineras");
                    } else {

                        String date = view.getLocalDBDateRegister();
                        view.showInfoMessage("Cargadas " + stations.size() + " gasolineras en fecha " + date);
                    }
                }catch (Exception exception) {
                    initialiceGasStationsList(new ArrayList<>());
                }
            }
        };

        repository.requestGasolineras(callBack, IDCCAAs.CANTABRIA.id);
    }

    private void persistGasStationsOnLocalDB(List<Gasolinera> gasStations) {
        IGasStationsDAO gasStationsDAO = view.getGasolinerasDAO();
        gasStationsDAO.deleteAll();
        for (Gasolinera gasStation : gasStations) {
            gasStationsDAO.addGasStation(gasStation);
        }

        view.updateLocalDBDateRegister();
    }

    private List<Gasolinera> getGasStationsFromLocalDB(){
        IGasStationsDAO gasStationsDAO = view.getGasolinerasDAO();
        return gasStationsDAO.getAll();
    }

    private void applyFilters() {
        // Filter the gas stations
        gasStations = filter.toFilter(initialGasStations);

        // Update the min and the max values for the slider
        minPriceLimit = (float) getMinPrice();
        maxPriceLimit = (float) getMaxPrice();
    }

    private void applyOrder() {
        // Filter the gas stations
        gasStations.sort(orderByPrice);
    }

    private void showGasStations() {
        view.showStations(gasStations);

        if (gasStations.isEmpty()) {
            if(interestPoint != null){
                view.showInfoMessage("No se han encontrado gasolineras en el rango");
            }else{
                view.showLoadError();
            }
        } else if (hasConnection) {
            view.showLoadCorrect(gasStations.size());
        }
    }

}
