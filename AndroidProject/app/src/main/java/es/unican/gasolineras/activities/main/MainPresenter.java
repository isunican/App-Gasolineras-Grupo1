package es.unican.gasolineras.activities.main;

import android.util.Log;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import es.unican.gasolineras.common.BrandsEnum;
import es.unican.gasolineras.common.FuelTypeEnum;
import es.unican.gasolineras.common.IFilter;
import es.unican.gasolineras.model.Filter;
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
    private IFilter filter;
    private IFilter tempFilter;
    private List<Selection> tempListSelection;

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



    private List<Selection> getBrandsSelections(IFilter f) {
        List<Selection> s = new ArrayList<>();
        boolean allSelected = f.getBrands().size() == BrandsEnum.values().length;
        s.add(new Selection("Todos", allSelected));
        for (BrandsEnum t: BrandsEnum.values()) {
            s.add(
                    new Selection(t.toString(), !allSelected && f.getBrands().contains(t))
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
        String fuelBrands = getStringOfSelections(
                getBrandsSelections(tempFilter));;

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
        //view.showFiltersPopUpBrandSelector(Arrays.asList(
          //      new Selection("Todos", true),
           //     new Selection("Gasolina", false),
             //   new Selection("Diesel", false)
        //));
        tempListSelection = getBrandsSelections(tempFilter);
        view.showFiltersPopUpBrandSelector(tempListSelection);

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
            if (value && numActivated < BrandsEnum.values().length - 1) {
                // Si se selecciona una opcion distinta de "Todos", y no esta marcando todas
                tempListSelection.get(0).setSelected(false);
                view.updateFiltersPopUpBrandsSelection(0, false);
            } else if (value && numActivated == BrandsEnum.values().length - 1) {
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
        //view.updateFiltersPopupTextViews(null, "Todos");
        if (tempListSelection.get(0).isSelected()) {
            tempFilter.setBrands(Arrays.asList(BrandsEnum.values()));
        } else {
            tempFilter.setBrands(
                    tempListSelection.stream()
                            .filter(Selection::isSelected)
                            .map(e -> BrandsEnum.fromString(e.getValue()))
                            .collect(Collectors.toList())
            );
        }
        view.updateFiltersPopupTextViews(null, getStringOfSelections(tempListSelection));

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
                List<Gasolinera> filtered = filter.toFilter(stations);
                if(filtered.isEmpty()){
                    view.showLoadError();
                }
                else {
                    view.showStations(filtered);
                    view.showLoadCorrect(filtered.size());
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

}
