package es.unican.gasolineras.activities.points;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
}