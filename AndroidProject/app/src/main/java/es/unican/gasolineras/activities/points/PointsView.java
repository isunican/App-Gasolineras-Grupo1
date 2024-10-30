package es.unican.gasolineras.activities.points;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.parceler.Parcels;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import es.unican.gasolineras.R;
import es.unican.gasolineras.activities.details.DetailsView;
import es.unican.gasolineras.activities.main.PointsArrayAdapter;
import es.unican.gasolineras.model.InterestPoint;
import es.unican.gasolineras.roomDAO.InterestPointsDAO;

/**
 * The main view of the application. It shows a list of gas stations.
 */
@AndroidEntryPoint
public class PointsView extends AppCompatActivity implements IPointsContract.View {

    /** The presenter of this view */
    private PointsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_list);

        // The default theme does not include a toolbar.
        // In this app the toolbar is explicitly declared in the layout
        // Set this toolbar as the activity ActionBar
        Toolbar toolbar = findViewById(R.id.toolbarPoints);
        setSupportActionBar(toolbar);

        // instantiate presenter and launch initial business logic
        presenter = new PointsPresenter();
        presenter.init(this);
    }

    /**
     * This is called when an item in the action bar menu is selected.
     * @param item The menu item that was selected.
     *
     * @return true if we have handled the selection
     */
    /*
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // TODO: Cambiar con IDs Lucia
        int itemId = item.getItemId();
        if (itemId == R.id.menuHomeButton) {
            presenter.onHomeClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

    /**
     * @see IPointsContract.View#init()
     */
    @Override
    public void init() {
        // initialize on click listeners (when clicking on a interest point in the list)
        ListView list = findViewById(R.id.lvPoints);
        list.setOnItemClickListener((parent, view, position, id) -> {
            InterestPoint point = (InterestPoint) parent.getItemAtPosition(position);
            presenter.onPointClicked(point);
        });
    }

    /**
     * @see IPointsContract.View#showStationsInPoint(InterestPoint)
     */
    @Override
    public void showStationsInPoint(InterestPoint point) {
        // TODO Para mostrar las gasolineras de un punto de interes
        Intent intent = new Intent(this, DetailsView.class);
        intent.putExtra(DetailsView.INTENT_STATION, Parcels.wrap(point));
        startActivity(intent);
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
}