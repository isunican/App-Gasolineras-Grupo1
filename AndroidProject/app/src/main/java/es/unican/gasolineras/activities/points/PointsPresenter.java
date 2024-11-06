package es.unican.gasolineras.activities.points;

import android.graphics.Color;

import java.util.Comparator;
import java.util.List;

import es.unican.gasolineras.common.database.IInterestPointsDAO;
import es.unican.gasolineras.model.InterestPoint;
import es.unican.gasolineras.roomDAO.InterestPointsDAO;

/**
 * The presenter of the points activity of the application. It controls {@link PointsView}
 */
public class PointsPresenter implements IPointsContract.Presenter {

    /** The view that is controlled by this presenter */
    private IPointsContract.View view;

    List<InterestPoint> points;

    /**
     * @see IPointsContract.Presenter#init(IPointsContract.View)
     */
    @Override
    public void init(IPointsContract.View view) {
        this.view = view;
        this.view.init();
        load();
    }

    /**
     * @see IPointsContract.Presenter#onHomeClicked()
     */
    public void onHomeClicked() {
        view.showMainPage();
    }

    /**
     * Loads the interest points from the DDBB, and sends them to the view
     */
    private void load() {
        IInterestPointsDAO ddbb = view.getPointsDao().getMyInterestPointsDAO();
        points = ddbb.getInterestPoints();
        points.add(new InterestPoint("Prueba 1", "#ff0000", 20, 20, 20));
        //points.add(new InterestPoint("Prueba 2", "#00ff00", 20, 20, 20));
        //points.add(new InterestPoint("Prueba 3", "#0000ff", 20, 20, 20));
        points.sort(Comparator.comparing(InterestPoint::getCreationDate));
        view.showPoints(points);
    }
}
