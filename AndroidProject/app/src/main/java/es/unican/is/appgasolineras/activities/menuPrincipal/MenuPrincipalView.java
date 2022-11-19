package es.unican.is.appgasolineras.activities.menuPrincipal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import es.unican.is.appgasolineras.R;
import es.unican.is.appgasolineras.activities.filtroPermanente.FiltroPermanenteView;
import es.unican.is.appgasolineras.activities.listaFavoritas.ListaFavoritasView;
import es.unican.is.appgasolineras.activities.main.MainView;
import es.unican.is.appgasolineras.common.prefs.IPrefs;
import es.unican.is.appgasolineras.common.prefs.Prefs;

public class MenuPrincipalView extends AppCompatActivity implements IMenuPrincipalContract.View {

    IPrefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        getSupportActionBar().setTitle("Menú Principal");
        prefs = Prefs.from(this);
        this.init();
    }


    @Override
    public void init() {
        prefs.putString("marca", "");
        Button botonIrALista = findViewById(R.id.btnAccederLista);
        botonIrALista.setOnClickListener(view ->
                this.openMainView()
        );
        Button botonIrAFiltrosPermanentes = findViewById(R.id.btnAccederFiltrosPermanentes);
        botonIrAFiltrosPermanentes.setOnClickListener(view ->
                this.openFiltrosPermanentesView()
        );
        Button botonIrAListaFavoritas = findViewById(R.id.btnAccederFavoritos);
        botonIrAListaFavoritas.setOnClickListener(view ->
                this.openListaFavoritasView()
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void openMainView() {
        Intent intent = new Intent(this, MainView.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void openFiltrosPermanentesView() {
        Intent intent = new Intent(this, FiltroPermanenteView.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void openListaFavoritasView() {
        Intent intent = new Intent(this, ListaFavoritasView.class);
        startActivity(intent);
        finish();
    }
}