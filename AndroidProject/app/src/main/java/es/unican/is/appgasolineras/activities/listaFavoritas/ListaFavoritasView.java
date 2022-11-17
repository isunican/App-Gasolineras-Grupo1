package es.unican.is.appgasolineras.activities.listaFavoritas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import es.unican.is.appgasolineras.R;
import es.unican.is.appgasolineras.activities.detail.GasolineraDetailView;
import es.unican.is.appgasolineras.activities.main.GasolinerasArrayAdapter;
import es.unican.is.appgasolineras.activities.menuPrincipal.MenuPrincipalView;
import es.unican.is.appgasolineras.common.Red;
import es.unican.is.appgasolineras.common.prefs.IPrefs;
import es.unican.is.appgasolineras.common.prefs.Prefs;
import es.unican.is.appgasolineras.model.Gasolinera;
import es.unican.is.appgasolineras.repository.GasolinerasRepository;
import es.unican.is.appgasolineras.repository.IGasolinerasRepository;
import es.unican.is.appgasolineras.repository.db.GasolineraDatabase;

public class ListaFavoritasView extends AppCompatActivity implements IListaFavoritasContract.View {

    IPrefs prefs;
    private IListaFavoritasContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        GasolineraDatabase db = Room.databaseBuilder(getApplicationContext(),
                GasolineraDatabase.class, "database-name").allowMainThreadQueries().build();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_favoritas);

        getSupportActionBar().setTitle("Lista gasolineras favoritas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = Prefs.from(this);
        prefs.putString("favoritas", "si");
        if (Red.isNetworkAvailable(this)) {
            presenter = new ListaFavoritasPresenter(this, db, true);
        } else {
            presenter = new ListaFavoritasPresenter(this, db, false);
        }
        presenter.init();
        this.init();
    }

    @Override
    public void init() {
        // init UI listeners
        ListView lvGasolineras = findViewById(R.id.lvGasolineras2);
        lvGasolineras.setOnItemClickListener((parent, view, position, id) ->
                presenter.onGasolineraClicked(position)
        );
    }

    @Override
    public void showGasolineras(List<Gasolinera> gasolineras) {
        GasolinerasArrayAdapter adapter = new GasolinerasArrayAdapter(this, gasolineras, prefs);
        ListView list = findViewById(R.id.lvGasolineras2);
        list.setAdapter(adapter);
    }

    @Override
    public IGasolinerasRepository getGasolineraRepository() {
        return new GasolinerasRepository(this);
    }

    @Override
    public void showLoadCorrect(int gasolinerasCount) {
        TextView error = findViewById(R.id.tvErroresFavoritas);
        error.setText("");
        String text = getResources().getString(R.string.loadCorrect);
        Toast.makeText(this, String.format(text, gasolinerasCount), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadErrorServidor() {
        TextView error = findViewById(R.id.tvErroresFavoritas);
        error.setText(getResources().getString(R.string.loadErrorServidor));
    }

    @Override
    public void showLoadErrorDAOVacia() {
        TextView error = findViewById(R.id.tvErroresFavoritas);
        error.setText("No tiene gasolineras marcadas como favoritas");
    }

    @Override
    public void showLoadErrorRed() {
        TextView error = findViewById(R.id.tvErroresFavoritas);
        error.setText(getResources().getString(R.string.loadErrorRed));
    }

    @Override
    public void openGasolineraDetails(Gasolinera gasolinera) {
        Intent intent = new Intent(this, GasolineraDetailView.class);
        intent.putExtra(GasolineraDetailView.INTENT_GASOLINERA, gasolinera);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            openMenuPrincipal();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        openMenuPrincipal();
    }

    @Override
    public void openMenuPrincipal() {
        Intent intent = new Intent(this, MenuPrincipalView.class);
        startActivity(intent);
        finish();
    }

}