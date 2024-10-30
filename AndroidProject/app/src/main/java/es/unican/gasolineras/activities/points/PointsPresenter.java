package es.unican.gasolineras.activities.points;

import java.util.List;

import es.unican.gasolineras.model.InterestPoint;
import es.unican.gasolineras.roomDAO.InterestPointsDAO;

/**
 * The presenter of the main activity of the application. It controls {@link PointsView}
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
     * @see IPointsContract.Presenter#onPointClicked(InterestPoint)
     */
    @Override
    public void onPointClicked(InterestPoint point) {
        view.showStationsInPoint(point);
    }

    /**
     * Loads the interest points from the DDBB, and sends them to the view
     */
    private void load() {
        InterestPointsDAO ddbb = view.getPointsDao();
        ddbb.getMyInterestPointsDAO().getInterestPoints();
    }

    /**
     * When you click on the house icon, the main page is displayed.
     */
    public void onHomeClicked() {
        view.showMainPage();
    }
}
