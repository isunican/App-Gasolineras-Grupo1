package es.unican.is.appgasolineras.activities.filtroPermanente;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import es.unican.is.appgasolineras.R;
import es.unican.is.appgasolineras.activities.menuPrincipal.MenuPrincipalView;
import es.unican.is.appgasolineras.common.prefs.IPrefs;
import es.unican.is.appgasolineras.common.prefs.Prefs;

public class FiltroPermanenteView extends AppCompatActivity implements IPermanenteContract.View {
    Spinner spnCombustible;
    Spinner spnCCAA;

    IPrefs prefs;

    IPermanenteContract.Presenter presenter;
    FiltroPermanenteMapper mapper;

    CheckBox checkSi;
    CheckBox checkNo;

    private static boolean pruebas = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros_permanentes);

        prefs = Prefs.from(this);
        mapper = new FiltroPermanenteMapper();

        getSupportActionBar().setTitle("Filtros permanentes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presenter = new FiltroPermanentePresenter(prefs);
        presenter.init();
        this.init();
    }

    @Override
    public void init() {
        spnCCAA = findViewById(R.id.spinner_CCAA);
        //Configuracion del spinner de las provincias
        ArrayAdapter<CharSequence> adapterCCAA = ArrayAdapter.createFromResource(this,
                R.array.comunidadesArray, android.R.layout.simple_spinner_item);
        adapterCCAA.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnCCAA.setAdapter(adapterCCAA);
        int comunidadGuardada = mapper.getCCAAIndex(prefs.getString("idComunidadName"));
        spnCCAA.setSelection(comunidadGuardada);
        Log.d("DEBUG", "1");

        spnCombustible = findViewById(R.id.spinner_combustible);
        //Configuracion del spinner de los combustibles
        ArrayAdapter<CharSequence> adapterCombustibles = ArrayAdapter.createFromResource(this,
                R.array.combustiblesArray, android.R.layout.simple_spinner_item);
        adapterCombustibles.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnCombustible.setAdapter(adapterCombustibles);
        int combustibleGuardado = mapper.getCombustibleIndex(prefs.getString("tipoGasolina"));
        spnCombustible.setSelection(combustibleGuardado);
        Log.d("DEBUG", "2");

        checkSi = findViewById(R.id.checkBoxSi);
        checkSi.setOnClickListener(view -> {
            checkNo.setChecked(false);
            checkSi.setChecked(true);
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && androidx.core.content.ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && !pruebas) {
                String permisos[] = {Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(this, permisos, 0);
            }
        });
        Log.d("DEBUG", "3");

        checkNo = findViewById(R.id.checkBoxNo);
        checkNo.setOnClickListener(view -> {
            if (checkSi.isChecked()) {
                checkSi.setChecked(false);
            }
            checkNo.setChecked(true);
        });
        Log.d("DEBUG", "4");


        if (prefs.getString("ubicacion").equals("si")) {
            checkSi.setChecked(true);
            checkNo.setChecked(false);
        } else {
            checkNo.setChecked(true);
            checkSi.setChecked(false);
        }
        Log.d("DEBUG", "5");
        Button btnGuardarPermanentes = findViewById(R.id.btnGuardarPermanentes);
        btnGuardarPermanentes.setOnClickListener(view -> {
            presenter.guardaFiltroPermanente(spnCCAA.getSelectedItemPosition(), spnCombustible.getSelectedItemPosition(), checkSi.isChecked());
            openMainView();
        });
        Log.d("DEBUG", "6");
        Button btnResetPermanentes = findViewById(R.id.btnResetearPermanentes);
        btnResetPermanentes.setOnClickListener(view -> {
            presenter.reseteaFiltroPermanente();
            this.init();
        });
        Log.d("DEBUG", "7");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == -1) {
            checkSi.setChecked(false);
            checkNo.setChecked(true);
        } else {
            checkSi.setChecked(true);
            checkNo.setChecked(false);
        }
    }

    @Override
    public void onBackPressed() {
        openMainView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            openMainView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void openMainView() {
        Intent myIntent = new Intent(this, MenuPrincipalView.class);
        startActivity(myIntent);
        finish();
    }

    public static void setPruebas(boolean p) {
        pruebas = p;
    }
}

