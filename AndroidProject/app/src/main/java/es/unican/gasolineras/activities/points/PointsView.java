package es.unican.gasolineras.activities.points;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import es.unican.gasolineras.R;

import es.unican.gasolineras.activities.main.MainView;
import es.unican.gasolineras.model.InterestPoint;
import es.unican.gasolineras.roomDAO.InterestPointsDAO;

/**
 * The points view of the application. It shows a list of interest points.
 */
@AndroidEntryPoint
public class PointsView extends AppCompatActivity implements IPointsContract.View {

    /**
     * The presenter of this view
     */
    private PointsPresenter presenter;
    private View popupViewPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_list);

        // instantiate presenter and launch initial business logic
        presenter = new PointsPresenter();
        presenter.init(this);
    }

    /**
     * @see IPointsContract.View#init()
     */
    @Override
    public void init() {
        // initialize on click listeners
        ImageView homeButton = findViewById(R.id.homeiconbutton);
        homeButton.setOnClickListener(v -> presenter.onHomeClicked());
        findViewById(R.id.btn_add).setOnClickListener(v -> presenter.onCreatePointOfInterestClicked());

    }

    /**
     * @see IPointsContract.View#getPointsDao()
     */
    @Override
    public InterestPointsDAO getPointsDao() {
        return InterestPointsDAO.getInstance(this);
    }

    /**
     * @see IPointsContract.View#showPoints(List)
     */
    @Override
    public void showPoints(List<InterestPoint> points) {
        ListView list = findViewById(R.id.lvPoints);
        PointsArrayAdapter adapter = new PointsArrayAdapter(this, points);
        list.setAdapter(adapter);
    }

    /**
     * @see IPointsContract.View#showLoadCorrect(int)
     */
    @Override
    public void showLoadCorrect(int numPoints) {
        Toast.makeText(this, "Cargados " + numPoints + " puntos de interes", Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IPointsContract.View#showLoadError()
     */
    @Override
    public void showLoadError() {
        Toast.makeText(this, "Error cargando los puntos de interes", Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IPointsContract.View#showInfoMessage(String)
     */
    public void showInfoMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IPointsContract.View#showMainPage()
     */
    @Override
    public void showMainPage() {
        Intent intent = new Intent(this, MainView.class);
        startActivity(intent);
    }

    /**
     * @see IPointsContract.View#showPointOfInterestPopUp()
     */
    public void showPointOfInterestPopUp() {
        // Crea el popup
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        popupViewPI = inflater.inflate(R.layout.activity_new_point_of_interest_layout, null);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // Permite al usuario interactuar con los elementos del popup
        PopupWindow popupWindow = new PopupWindow(popupViewPI, width, height, focusable);


        // Muestra el PopupWindow en el centro de la pantalla
        ConstraintLayout rootLayout = findViewById(R.id.points_list);
        popupWindow.showAtLocation(rootLayout, Gravity.CENTER, 0, 0);

        // Fijar listener al TextView del tipo de combustible
        View colorPickerButton = popupViewPI.findViewById(R.id.btColorPicker);
        colorPickerButton.setOnClickListener(v -> showColorPickerPopUp());

    }
    private void showColorPickerPopUp() {
        // Crea el popup
        AlertDialog.Builder builder = new AlertDialog.Builder(PointsView.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View colorPickerView = inflater.inflate(R.layout.activity_color_picker_layout, null);
        builder.setView(colorPickerView);
        AlertDialog colorPickerDialog = builder.create();


        // Fijar listener de los botones de colores
        colorPickerView.findViewById(R.id.btColorGray).setOnClickListener(v -> onColorSelected(
                getResources().getColor(R.color.gray,getTheme()),
                false,
                colorPickerDialog));
        colorPickerView.findViewById(R.id.btColorBlue).setOnClickListener(v -> onColorSelected(
                getResources().getColor(R.color.blue,getTheme()),
                true,
                colorPickerDialog));

        colorPickerView.findViewById(R.id.btColorRed).setOnClickListener(v -> onColorSelected(
                getResources().getColor(R.color.red,getTheme()),
                false,
                colorPickerDialog));

        colorPickerView.findViewById(R.id.btColorYellow).setOnClickListener(v -> onColorSelected(
                getResources().getColor(R.color.yellow,getTheme()),
                false,
                colorPickerDialog));
        colorPickerView.findViewById(R.id.btColorGreen).setOnClickListener(v -> onColorSelected(
                getResources().getColor(R.color.green,getTheme()),
                false,
                colorPickerDialog));
        colorPickerView.findViewById(R.id.btColorPurple).setOnClickListener(v -> onColorSelected(
                getResources().getColor(R.color.purple,getTheme()),
                false,
                colorPickerDialog));
        colorPickerView.findViewById(R.id.btColorOrange).setOnClickListener(v -> onColorSelected(
                getResources().getColor(R.color.orange,getTheme()),
                false,
                colorPickerDialog));
        colorPickerView.findViewById(R.id.btColorBrown).setOnClickListener(v -> onColorSelected(
                getResources().getColor(R.color.brown,getTheme()),
                true,
                colorPickerDialog));
        colorPickerView.findViewById(R.id.btColorBlack).setOnClickListener(v -> onColorSelected(
                getResources().getColor(R.color.black,getTheme()),
                true,
                colorPickerDialog));

        // Fijar listener al Boton para cancelar
        View cancelButton = colorPickerView.findViewById(R.id.colorPicker_cancel_button);
        cancelButton.setOnClickListener(v -> colorPickerDialog.cancel());

        colorPickerDialog.show();
        colorPickerDialog.getWindow().setLayout(WRAP_CONTENT,WRAP_CONTENT);
    }

    private void onColorSelected(int colorResoureID, boolean needWhitePalette, AlertDialog colorPickerDialog) {
        View btColorPicker = popupViewPI.findViewById(R.id.btColorPicker);
        btColorPicker.setBackgroundColor(colorResoureID);
        btColorPicker.setForeground(
                needWhitePalette
                        ? getResources().getDrawable(R.drawable.white_palette, getTheme())
                        : getResources().getDrawable(R.drawable.palette, getTheme()));
        colorPickerDialog.cancel();
    }
}