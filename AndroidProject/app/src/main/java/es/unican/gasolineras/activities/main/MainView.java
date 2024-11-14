package es.unican.gasolineras.activities.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.parceler.Parcels;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.unican.gasolineras.R;
import es.unican.gasolineras.activities.info.InfoView;
import es.unican.gasolineras.activities.details.DetailsView;
import es.unican.gasolineras.activities.points.PointsView;
import es.unican.gasolineras.common.LimitPricesEnum;
import es.unican.gasolineras.common.FuelTypeEnum;
import es.unican.gasolineras.common.OrderMethodsEnum;
import es.unican.gasolineras.model.Gasolinera;
import es.unican.gasolineras.model.InterestPoint;
import es.unican.gasolineras.model.OrderByPrice;
import es.unican.gasolineras.repository.IGasolinerasRepository;

/**
 * The main view of the application. It shows a list of gas stations.
 */
@AndroidEntryPoint
public class MainView extends AppCompatActivity implements IMainContract.View {

    private View popupView;
    PopupWindow popupWindow;
    private AlertDialog alertDialog;
    boolean[] selectcionArray;
    // get values from LimitePricesEnum converted to float and integer
    float minPriceLimit;
    float maxPriceLimit;


    /** The presenter of this view */
    private MainPresenter presenter;

    /** The repository to access the data. This is automatically injected by Hilt in this class */
    @Inject
    IGasolinerasRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popupView = null;
        alertDialog = null;
        popupWindow = null;
        setContentView(R.layout.activity_main);

        // The default theme does not include a toolbar.
        // In this app the toolbar is explicitly declared in the layout
        // Set this toolbar as the activity ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the interest point
        InterestPoint point = getIntent().getParcelableExtra("interestPoint");

        // Set the correct tool bar
        if (point == null) {
            LinearLayout toolbarContent = findViewById(R.id.layoutToolbarIP);
            toolbarContent.setVisibility(View.GONE);
        } else {
            ActionBar bar = Objects.requireNonNull(getSupportActionBar());
            bar.setTitle("");
        }

        // instantiate presenter and launch initial business logic
        if (point == null)
            presenter = new MainPresenter();
        else
            presenter = new MainPresenter(point);
        presenter.init(this);

    }

    /**
     * This creates the menu that is shown in the action bar (the upper toolbar)
     * @param menu The options menu in which you place your items.
     *
     * @return true because we are defining a new menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * This is called when an item in the action bar menu is selected.
     * @param item The menu item that was selected.
     *
     * @return true if we have handled the selection
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menuItemInfo) {
            presenter.onMenuInfoClicked();
            return true;
        }

        if (itemId == R.id.menuFilterButton) {
            presenter.onFiltersClicked();
            return true;
        }

        if (itemId == R.id.menuOrderButton)  {
            presenter.onOrderClicked();
            return true;
        }

        if (itemId == R.id.menuPointButton) {
           presenter.onPointsClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @see IMainContract.View#init()
     */
    @Override
    public void init() {
        // initialize on click listeners (when clicking on a station in the list)
        ListView list = findViewById(R.id.lvStations);
        list.setOnItemClickListener((parent, view, position, id) -> {
            Gasolinera station = (Gasolinera) parent.getItemAtPosition(position);
            presenter.onStationClicked(station);
        });
    }

    /**
     * @see IMainContract.View#getGasolinerasRepository()
     * @return the repository to access the data
     */
    @Override
    public IGasolinerasRepository getGasolinerasRepository() {
        return repository;
    }

    /**
     * @see IMainContract.View#showStations(List) 
     * @param stations the list of charging stations
     */
    @Override
    public void showStations(List<Gasolinera> stations) {
        ListView list = findViewById(R.id.lvStations);
        GasolinerasArrayAdapter adapter = new GasolinerasArrayAdapter(this, stations);
        list.setAdapter(adapter);
    }

    /**
     * @see IMainContract.View#showLoadCorrect(int)
     * @param stations
     */
    @Override
    public void showLoadCorrect(int stations) {
        Toast.makeText(this, "Cargadas " + stations + " gasolineras", Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IMainContract.View#showLoadError()
     */
    @Override
    public void showLoadError() {
        Toast.makeText(this, "Error cargando las gasolineras", Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IMainContract.View#showInfoMessage(String)
     */
    public void showInfoMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IMainContract.View#showStationDetails(Gasolinera)
     * @param station the charging station
     */
    @Override
    public void showStationDetails(Gasolinera station) {
        Intent intent = new Intent(this, DetailsView.class);
        intent.putExtra(DetailsView.INTENT_STATION, Parcels.wrap(station));
        startActivity(intent);
    }

    /**
     * @see IMainContract.View#showInfoActivity()
     */
    @Override
    public void showInfoActivity() {
        Intent intent = new Intent(this, InfoView.class);
        startActivity(intent);
    }

    /**
     * @see IMainContract.View#showPointsActivity(boolean deleteActual)
     */
    @Override
    public void showPointsActivity(boolean deleteActual) {
        Intent intent = new Intent(this, PointsView.class);
        if (deleteActual) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        startActivity(intent);
        if (deleteActual) {
            finish();
        }
    }


    private void createPopUp(int layoutId) {
        // Crear el layout del Popup
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(layoutId, null);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // Permite al usuario interactuar con los elementos del popup
        popupWindow = new PopupWindow(popupView, width, height, focusable);


        // Muestra el PopupWindow en el centro de la pantalla
        LinearLayout rootLayout = findViewById(R.id.main);
        popupWindow.showAtLocation(rootLayout, Gravity.CENTER, 0, 0);
    }

    /**
     * @see IMainContract.View#showFiltersPopUp()
     */
    @Override
    public void showFiltersPopUp() {
        // Crea el popup
        createPopUp(R.layout.activity_filters_layout);

        // Fijar listener al TextView del tipo de combustible
        TextView typeSpinner = popupView.findViewById(R.id.typeSpinner);
        typeSpinner.setOnClickListener(v -> {
            presenter.onFiltersPopUpFuelTypesSelected();
        });

        // Fijar listener al TextView de la marca de gasolineras
        TextView brandSpinner = popupView.findViewById(R.id.brandSpinner);
        brandSpinner.setOnClickListener(v -> {
            presenter.onFiltersPopUpBrandsSelected();
        });

        // Establece la barra de progreso del precio maximo con el valor almacenado en el filtro
        presenter.onFiltersPopUpMaxPriceSeekBarLoaded();

        // Buscar el SeekBar en el layout
        SeekBar maxPriceSeekBar = popupView.findViewById(R.id.MaxPriceSeekBar);
        // Establece el rango del seekbar
        maxPriceSeekBar.setMax(Integer.parseInt(presenter.calculateSeekbarProgress()));

        // Fija listener al SeekBar del precio maximo de gasolineras para obtener el valor decimal
        maxPriceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                presenter.onFiltersPopUpMaxPriceSeekBarChanged(progress);
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No se requieren acciones
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No se requiere acciones
            }

        });

        // Fijar listener al boton de limpiar filtros
        ImageButton filterClear = popupView.findViewById(R.id.clear_filters_bt);
        filterClear.setOnClickListener(v -> {
            presenter.onFiltersPopUpClearFiltersClicked();
        });

        // Fijar listener al boton de cancelar
        ImageButton cancelButton = popupView.findViewById(R.id.filters_cancel_button);
        cancelButton.setOnClickListener(v -> {
            presenter.onFiltersPopUpCancelClicked();
        });

        // Fijar listener al boton de aceptar
        ImageButton accpetlButton = popupView.findViewById(R.id.filters_accept_button);
        accpetlButton.setOnClickListener(v -> {
            presenter.onFiltersPopUpAcceptClicked();
        });
    }



    /**
     * @see IMainContract.View#updateFiltersPopupTextViewsSelections(String, String)
     */
    @Override
    public void updateFiltersPopupTextViewsSelections(String fuelTypes, String fuelBrands) {
        if (fuelTypes != null) {
            TextView typeSpinner = popupView.findViewById(R.id.typeSpinner);
            typeSpinner.setText(fuelTypes);
        }

        if (fuelBrands != null) {
            TextView brandSpinner = popupView.findViewById(R.id.brandSpinner);
            brandSpinner.setText(fuelBrands);
        }

    }

    /**
     * @see IMainContract.View#updateFiltersPopupTextViewsMaxPrice(float)
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void updateFiltersPopupTextViewsMaxPrice(float truncatedMaxPrice) {
        // Actualizamos el label que muestra el valor maximo actual
        TextView lbSelectedMaxPrice = popupView.findViewById(R.id.lbSelectedMaxPrice);
        lbSelectedMaxPrice.setText(String.format("%.2f", truncatedMaxPrice));
    }

    /**
     * @see IMainContract.View#updateFiltersPopupSeekBarProgressMaxPrice(int progress, float minPriceLimit, float maxPriceLimit)
     */
    @Override
    public void updateFiltersPopupSeekBarProgressMaxPrice(int progress, float minPriceLimit, float maxPriceLimit) {
        // Fijar los labels
        TextView minPriceLabel = popupView.findViewById(R.id.minPriceLabel);
        minPriceLabel.setText(String.valueOf(minPriceLimit));
        TextView maxPriceLabel = popupView.findViewById(R.id.maxPriceLabel);
        maxPriceLabel.setText(String.valueOf(maxPriceLimit));

        // Buscar el SeekBar en el layout
        SeekBar maxPriceSeekBar = popupView.findViewById(R.id.MaxPriceSeekBar);

        // Actualizamos el progress del SeekBar
        maxPriceSeekBar.setProgress(progress);
    }

    /**
     * @see IMainContract.View#updateFiltersPopUpSelection(int, boolean)
     */
    @Override
    public void updateFiltersPopUpSelection(int position, boolean value) {
        selectcionArray[position] = value;
        alertDialog.getListView().setItemChecked(position, value);
    }

    /**
     * @see IMainContract.View#showFiltersPopUpFuelTypesSelector(List)
     */
    @Override
    public void showFiltersPopUpFuelTypesSelector(List<Selection> selections) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainView.this);
        builder.setTitle("Seleccione opciones");

        String[] options = new String[selections.size()];
        selectcionArray = new boolean[selections.size()];
        for (int i = 0; i < selections.size(); i++) {
            options[i] = selections.get(i).getValue();
            selectcionArray[i] = selections.get(i).isSelected();
        }

        // Actualizar el estado de selección en el array
        builder.setMultiChoiceItems(options, selectcionArray, (dialog, which, isChecked) -> {
            presenter.onFiltersPopUpFuelTypesOneSelected(which, isChecked);
        });

        // Botón "OK"
        builder.setPositiveButton("OK", (dialog, which) ->
            presenter.onFiltersPopUpFuelTypesAccepted()
        );

        // Botón "Cancelar"
        builder.setNegativeButton("Cancelar", null);

        // Mostrar el diálogo
        alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * @see IMainContract.View#showFiltersPopUpBrandSelector(List)
     */
    @Override
    public void showFiltersPopUpBrandSelector(List<Selection> selections) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainView.this);
        builder.setTitle("Seleccione opciones");

        String[] options = new String[selections.size()];
        selectcionArray = new boolean[selections.size()];
        for (int i = 0; i < selections.size(); i++) {
            options[i] = selections.get(i).getValue();
            selectcionArray[i] = selections.get(i).isSelected();
        }

        // Actualizar el estado de selección en el array
        builder.setMultiChoiceItems(options, selectcionArray, (dialog, which, isChecked) -> {
            presenter.onFiltersPopUpBrandsOneSelected(which, isChecked);

        });

        // Botón "OK"
        builder.setPositiveButton("OK", (dialog, which) ->
                presenter.onFiltersPopUpBrandsAccepted()
        );

        // Botón "Cancelar"
        builder.setNegativeButton("Cancelar", null);

        // Mostrar el diálogo
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void closeFiltersPopUp() {
        popupWindow.dismiss();
        popupView = null;
        alertDialog = null;
        popupWindow = null;
    }

    public void showOrderPopUp(int typeIndex, int methodIndex) {
        createPopUp(R.layout.activity_sort_layout);
        // Configurar los Spinners
        Spinner typeOrderSpinner = popupView.findViewById(R.id.typeOrderSpinner);

        Spinner orderMethodSpinner = popupView.findViewById(R.id.orderPriceMethodSpinner);

        // Llenar los spinners con valores del enumerado
        ArrayAdapter<FuelTypeEnum> typeFuelAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                FuelTypeEnum.values()
        );
        typeFuelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeOrderSpinner.setAdapter(typeFuelAdapter);
        typeOrderSpinner.setSelection(typeIndex);

        ArrayAdapter<OrderMethodsEnum> orderMethodAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                OrderMethodsEnum.values()
        );

        orderMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderMethodSpinner.setAdapter(orderMethodAdapter);
        orderMethodSpinner.setSelection(methodIndex);


        // Implementamos los listeners de los elementos interaccionables

        // Asignar listeners a los spinners
        typeOrderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FuelTypeEnum selectedType = (FuelTypeEnum) parent.getItemAtPosition(position);
                // Notificar al presentador sobre la selección
                presenter.onFuelTypeSelected(selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO: ver que hacer cuando no se selecciona nada.
            }
        });

        orderMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OrderMethodsEnum selectedMethodOrder = (OrderMethodsEnum) parent.getItemAtPosition(position);
                presenter.onMethodOrderSelected(selectedMethodOrder);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO: ver que hacer cuando no se selecciona nada.
            }
        });

        // Manejo de los botones de aceptar y cancelar
        ImageButton acceptButton = popupView.findViewById(R.id.order_accept_button);
        acceptButton.setOnClickListener (v -> { presenter.onOrderPopUpAcceptClicked();
        });

        ImageButton cancelButton = popupView.findViewById(R.id.order_cancel_button);
        cancelButton.setOnClickListener(v -> { presenter.onOrderPopUpCancelClicked();
        });

        // TODO: Hacer el clear de los filtros.
        ImageButton clearButton = popupView.findViewById(R.id.bt_clear_order);
        clearButton.setOnClickListener(v -> { presenter.onOrderPopUpClearClicked();
        });

    }

    public void closeOrderPopUp(){
        popupWindow.dismiss();
        popupView = null;
        alertDialog = null;
        popupWindow = null;
    }


    public String getConstantString(int id) {
        return getString(id);
    }

    public MainPresenter getMainPresenter() {
        return presenter;
    }

    /**
     * @see IMainContract.View#showInterestPointInfo(InterestPoint ip, int loaded)
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void showInterestPointInfo(InterestPoint ip, int loaded) {
        // Create the bar
        LayoutInflater inflater = LayoutInflater.from(this);
        View horizontalLayout = inflater.inflate(R.layout.activity_points_information, null, false);
        FrameLayout placeholder = findViewById(R.id.interest_point_placeholder);
        placeholder.addView(horizontalLayout);

        // Set the values
        TextView name = findViewById(R.id.ip_name_tv);
        name.setText(ip.getName());
        TextView radius = findViewById(R.id.ip_radius_tv);
        radius.setText(String.format("Radio: %.1f km", ip.getRadius()));
        TextView loadedTv = findViewById(R.id.ip_loaded_tv);
        loadedTv.setText(String.format("Encontradas: %d", loaded));
    }
}