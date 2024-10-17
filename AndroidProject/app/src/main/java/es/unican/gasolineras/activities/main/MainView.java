package es.unican.gasolineras.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.unican.gasolineras.R;
import es.unican.gasolineras.activities.info.InfoView;
import es.unican.gasolineras.activities.details.DetailsView;
import es.unican.gasolineras.model.Gasolinera;
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

        // instantiate presenter and launch initial business logic
        presenter = new MainPresenter();
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
        } if (itemId == R.id.menuFilterButton) {
            presenter.onFiltersClicked();
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

    private void createPopUp(int layoutId) {
        // Crear el layout del Popup
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(layoutId, null);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // Permite al usuario interactuar con los elementos del popup
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Muestra el PopupWindow en el centro de la pantalla
        ConstraintLayout rootLayout = findViewById(R.id.main);
        popupWindow.showAtLocation(rootLayout, Gravity.CENTER, 0, 0);
    }

    /**
     * @see IMainContract.View#showFiltersPopUp()
     */
    @Override
    public void showFiltersPopUp() {
        // Crea el popup
        createPopUp(R.layout.activity_filters_layout);

        // Fijar valores al TextView del tipo de combustible
        TextView typeSpinner = popupView.findViewById(R.id.typeSpinner);
        typeSpinner.setOnClickListener(v -> {
            presenter.onFiltersPopUpFuelTypesSelected();
        });

        // Fijar boton de limpiar filtros
        ImageButton filterClear = popupView.findViewById(R.id.clear_filters_bt);
        filterClear.setOnClickListener(v -> {
            presenter.onFiltersPopUpClearFiltersClicked();
        });

        // Fijar el boton de cancelar
        ImageButton cancelButton = popupView.findViewById(R.id.filters_cancel_button);
        cancelButton.setOnClickListener(v -> {
            presenter.onFiltersPopUpCancelClicked();
        });

        // Fijar el boton de aceptar
        ImageButton accpetlButton = popupView.findViewById(R.id.filters_accpet_button);
        accpetlButton.setOnClickListener(v -> {
            presenter.onFiltersPopUpAcceptClicked();
        });
    }

    /**
     * @see IMainContract.View#updateFiltersPopupTextViews(String)
     */
    @Override
    public void updateFiltersPopupTextViews(String fuelTypes) {
        if (fuelTypes != null) {
            TextView typeSpinner = popupView.findViewById(R.id.typeSpinner);
            typeSpinner.setText(fuelTypes);
        }
    }

    /**
     * @see IMainContract.View#updateFiltersPopUpFuelTypesSelection(int, boolean)
     */
    @Override
    public void updateFiltersPopUpFuelTypesSelection(int position, boolean value) {
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

    public void closeFiltersPopUp() {
        popupWindow.dismiss();
        popupView = null;
        alertDialog = null;
        popupWindow = null;
    }
}