package es.unican.gasolineras.activities.points;

import android.graphics.Color;

import android.database.sqlite.SQLiteException;

import java.util.Comparator;
import java.util.List;

import es.unican.gasolineras.common.exceptions.LatitudInvalidaException;
import es.unican.gasolineras.common.exceptions.LongitudInvalidaException;
import es.unican.gasolineras.common.exceptions.RadioInvalidoException;
import es.unican.gasolineras.common.database.IInterestPointsDAO;
import es.unican.gasolineras.common.exceptions.LatitudInvalidaException;
import es.unican.gasolineras.common.exceptions.LongitudInvalidaException;
import es.unican.gasolineras.common.exceptions.RadioInvalidoException;
import es.unican.gasolineras.model.InterestPoint;
import es.unican.gasolineras.roomDAO.InterestPointsDAO;
import es.unican.gasolineras.model.validators.InterestPointValidator;

/**
 * The presenter of the points activity of the application. It controls {@link PointsView}
 */
public class PointsPresenter implements IPointsContract.Presenter {

    /** The view that is controlled by this presenter */
    private IPointsContract.View view;
    private List<InterestPoint> points;
    private InterestPointsDAO ddbb;


    /**
     * @see IPointsContract.Presenter#init(IPointsContract.View)
     */
    @Override
    public void init(IPointsContract.View view) {
        this.view = view;
        this.view.init();
        ddbb = view.getPointsDao();
        load();
    }

    /**
     * @see IPointsContract.Presenter#onHomeClicked()
     */
    public void onHomeClicked() {
        view.showMainPage();
    }

    @Override
    public void onCreatePointOfInterestClicked() {
        view.showPointOfInterestPopUp();
    }

    /**
     *
     * @see IPointsContract.Presenter#onAcceptNewPointOfInterestClicked(InterestPoint newInterestPoint)
     */
    @Override
    public void onAcceptNewPointOfInterestClicked(InterestPoint newInterestPoint)
            throws LatitudInvalidaException, LongitudInvalidaException, RadioInvalidoException {
        InterestPointValidator.checkFields(newInterestPoint);
        ddbb.getMyInterestPointsDAO().addInterestPoint(newInterestPoint);
        load();
    }

    /**
     * Loads the interest points from the DDBB, and sends them to the view
     */
    private void load() {
        try {
        points = ddbb.getMyInterestPointsDAO().getInterestPoints();
        } catch (SQLiteException e) {
            view.showLoadError();
        }
        //points.add(new InterestPoint("Prueba 1", "#ff0000", 20, 20, 20));
        //points.add(new InterestPoint("Prueba 2", "#00ff00", 20, 20, 20));
        //points.add(new InterestPoint("Prueba 3", "#0000ff", 20, 20, 20));
        points.sort(Comparator.comparing(InterestPoint::getCreationDate));
        view.showPoints(points);
    }
}
