package es.unican.gasolineras.activities.points;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import es.unican.gasolineras.activities.points.inputFilters.LatitudInputFilter;
import es.unican.gasolineras.activities.points.inputFilters.LongitudInputFilter;
import es.unican.gasolineras.activities.points.inputFilters.RadiusInputFilter;
import es.unican.gasolineras.common.exceptions.LatitudInvalidaException;
import es.unican.gasolineras.common.exceptions.LongitudInvalidaException;
import es.unican.gasolineras.common.exceptions.RadioInvalidoException;
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
    private View newPIView;

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
        this.findViewById(R.id.btn_add).setOnClickListener(v -> presenter.onCreatePointOfInterestClicked());

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
        AlertDialog.Builder builder = new AlertDialog.Builder(PointsView.this);
        LayoutInflater inflater = getLayoutInflater();

        newPIView = inflater.inflate(R.layout.activity_new_point_of_interest_layout, null);
        builder.setView(newPIView);
        AlertDialog newPIDialog = builder.create();

        View colorPickerButton = newPIView.findViewById(R.id.btColorPicker);
        colorPickerButton.setTag(Color.valueOf(getResources().getColor(R.color.gray,getTheme())));
        colorPickerButton.setOnClickListener(v -> showColorPickerPopUp());

        View cancelButton = newPIView.findViewById(R.id.newPI_cancel_button);
        cancelButton.setOnClickListener(v -> newPIDialog.cancel());

        EditText nameTextView = newPIView.findViewById(R.id.tvPIName);
        EditText longTextView = newPIView.findViewById(R.id.tvPILongitud);
        longTextView.setFilters(new InputFilter[]{new LongitudInputFilter()});
        EditText latTextView = newPIView.findViewById(R.id.tvPILatitud);
        latTextView.setFilters(new InputFilter[]{new LatitudInputFilter()});
        EditText radiusTextView = newPIView.findViewById(R.id.tvPIRadio);
        radiusTextView.setFilters(new InputFilter[]{new RadiusInputFilter()});

        View acceptButton = newPIView.findViewById(R.id.newPI_accept_button);

        acceptButton.setOnClickListener(v -> {
            InterestPoint newPointOfInterest = new InterestPoint(
                    nameTextView.getText().toString(),
                    (Color) colorPickerButton.getTag(),
                    Double.parseDouble(latTextView.getText().toString()),
                    Double.parseDouble(longTextView.getText().toString()),
                    Double.parseDouble(radiusTextView.getText().toString())
            );
            try{
                presenter.onAcceptNewPointOfInterestClicked(newPointOfInterest);
                newPIDialog.cancel();
            }catch(LongitudInvalidaException | RadioInvalidoException |
                   LatitudInvalidaException exception){
                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        newPIDialog.show();
        newPIDialog.getWindow().setLayout(WRAP_CONTENT,WRAP_CONTENT);
    }
    private void showColorPickerPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PointsView.this);
        LayoutInflater inflater = getLayoutInflater();

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
        colorPickerDialog.getWindow().setLayout(750,750);
    }

    private void onColorSelected(int colorResourceID, boolean needWhitePalette, AlertDialog colorPickerDialog) {
        View btColorPicker = newPIView.findViewById(R.id.btColorPicker);
        btColorPicker.setBackgroundColor(colorResourceID);
        btColorPicker.setForeground(
                needWhitePalette
                        ? getResources().getDrawable(R.drawable.white_palette, getTheme())
                        : getResources().getDrawable(R.drawable.palette, getTheme()));
        Color color = Color.valueOf(colorResourceID);
        btColorPicker.setTag(color);
        colorPickerDialog.cancel();
    }
}