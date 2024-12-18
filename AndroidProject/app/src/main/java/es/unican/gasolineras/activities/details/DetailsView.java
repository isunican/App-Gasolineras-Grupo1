package es.unican.gasolineras.activities.details;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.parceler.Parcels;

import es.unican.gasolineras.R;
import es.unican.gasolineras.model.Gasolinera;

/**
 * View that shows the details of one gas station. Since this view does not have business logic,
 * it can be implemented as an activity directly, without the MVP pattern.
 */
public class DetailsView extends AppCompatActivity {

    /** Key for the intent that contains the gas station */
    public static final String INTENT_STATION = "INTENT_STATION";

    /**
     * @see AppCompatActivity#onCreate(Bundle)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);

        // The default theme does not include a toolbar.
        // In this app the toolbar is explicitly declared in the layout
        // Set this toolbar as the activity ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        assert bar != null;  // to avoid warning in the line below
        bar.setDisplayHomeAsUpEnabled(true);  // show back button in action bar

        // Link to view elements
        ImageView ivRotulo = findViewById(R.id.ivRotulo);
        TextView tvRotulo = findViewById(R.id.tvRotulo);
        TextView tvMunicipio = findViewById(R.id.tvMunicipio);
        TextView tvGasolina95E5 = findViewById(R.id.tvGasolina95E5);
        TextView tvGasoleoA = findViewById(R.id.tvGasoleoA);
        TextView tvSumario = findViewById(R.id.tvSumario);
        TextView tvDireccion = findViewById(R.id.tvDireccion);
        TextView tvCodigoPostal = findViewById(R.id.tvCodigoPostal);
        TextView tvHorario = findViewById(R.id.tvHorario);

        // Get Gas Station from the intent that triggered this activity
        Gasolinera gasolinera = Parcels.unwrap(getIntent().getExtras().getParcelable(INTENT_STATION));

        //Obtaining the name for searching the picture
        String rotulo = gasolinera.getRotulo().toLowerCase();
        int imageID = getResources()
                .getIdentifier(rotulo, "drawable", getPackageName());

        // Si el rotulo son sólo numeros, el método getIdentifier simplemente devuelve
        // como imageID esos números, pero eso va a fallar porque no tendré ningún recurso
        // que coincida con esos números
        if (imageID == 0 || TextUtils.isDigitsOnly(rotulo)) {
            imageID = getResources().getIdentifier("generic", "drawable", getPackageName());
        }

        if (imageID != 0) {
            ivRotulo.setImageResource(imageID);
        }

        // Set Texts
        tvRotulo.setText(gasolinera.getRotulo());
        tvMunicipio.setText(gasolinera.getMunicipio());
        tvGasolina95E5.setText(String.valueOf(Math.round(gasolinera.getGasolina95E5() * 100.0)/100.0));
        tvGasoleoA.setText(String.valueOf(Math.round(gasolinera.getGasoleoA() * 100.0)/100.0));
        Double precioGasolina = Math.round(gasolinera.getAverageGasPrice() * 100.0)/100.0;
        tvSumario.setText(precioGasolina == 0 ? "-" : String.valueOf(precioGasolina));
        tvDireccion.setText(gasolinera.getDireccion());
        tvCodigoPostal.setText(gasolinera.getCp());
        tvHorario.setText(gasolinera.getHorario());



    }

    /**
     * @see AppCompatActivity#onOptionsItemSelected(MenuItem)
     * @param item The menu item that was selected.
     *
     * @return true if we are handling the selection
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}