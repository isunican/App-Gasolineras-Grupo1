package es.unican.gasolineras.activities.points;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import es.unican.gasolineras.R;

import es.unican.gasolineras.activities.main.MainView;
import es.unican.gasolineras.activities.points.inputFilters.LatitudInputFilter;
import es.unican.gasolineras.activities.points.inputFilters.LongitudInputFilter;
import es.unican.gasolineras.activities.points.inputFilters.RadiusInputFilter;
import es.unican.gasolineras.common.database.IInterestPointsDAO;
import es.unican.gasolineras.common.database.MyFuelDatabase;
import es.unican.gasolineras.common.exceptions.LatitudInvalidaException;
import es.unican.gasolineras.common.exceptions.LongitudInvalidaException;
import es.unican.gasolineras.common.exceptions.RadioInvalidoException;
import es.unican.gasolineras.model.InterestPoint;

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
    public IInterestPointsDAO getPointsDao() {
        return MyFuelDatabase.getInstance(this).getInterestPointsDAO();
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

        //Obtiene los edit texts para poder crear luego el objeto
        EditText nameTextView = newPIView.findViewById(R.id.tvPIName);
        EditText longTextView = newPIView.findViewById(R.id.tvPILongitud);
        longTextView.setFilters(new InputFilter[]{new LongitudInputFilter()});
        EditText latTextView = newPIView.findViewById(R.id.tvPILatitud);
        latTextView.setFilters(new InputFilter[]{new LatitudInputFilter()});
        EditText radiusTextView = newPIView.findViewById(R.id.tvPIRadio);
        radiusTextView.setFilters(new InputFilter[]{new RadiusInputFilter()});

        View acceptButton = newPIView.findViewById(R.id.newPI_accept_button);

        acceptButton.setOnClickListener(v -> {

            //Comprueba que los campos no esten vacios
            if(nameTextView.getText().length() == 0){
                nameTextView.setError("El campo nombre es necesario");
            }else if(latTextView.getText().length() == 0){
                latTextView.setError("El campo latitud es necesario");
            }else if(longTextView.getText().length() == 0){
                longTextView.setError("El campo longitud es necesario");
            }else if(radiusTextView.getText().length() == 0){
                radiusTextView.setError("El campo radio es necesario");
            }else {

                //Si los campos no estan vacios crea el punto de interes
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
            }
        });

        newPIDialog.show();
        newPIDialog.setCancelable(false);
        newPIDialog.getWindow().setLayout(WRAP_CONTENT,WRAP_CONTENT);
    }

    /**
     * Crea y lanza la ventana de seleccion de color
     */
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

    }

    /**
     * Cambia el color del boton del color picker e incluye en el tag el color
     * @param colorArgb valor del color seleccionado en formato argb
     * @param needWhitePalette booleano de control del color del icono de la paleta
     * @param colorPickerDialog dialog para cerrarlo una vez terminado
     */
    private void onColorSelected(int colorArgb, boolean needWhitePalette, AlertDialog colorPickerDialog) {
        View btColorPicker = newPIView.findViewById(R.id.btColorPicker);
        btColorPicker.setBackgroundColor(colorArgb);
        btColorPicker.setForeground(
                needWhitePalette
                        ? getResources().getDrawable(R.drawable.white_palette, getTheme())
                        : getResources().getDrawable(R.drawable.palette, getTheme()));
        Color color = Color.valueOf(colorArgb);
        btColorPicker.setTag(color);
        colorPickerDialog.cancel();
    }
}