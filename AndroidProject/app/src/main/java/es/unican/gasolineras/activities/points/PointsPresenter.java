package es.unican.gasolineras.activities.points;

import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import es.unican.gasolineras.common.database.IInterestPointsDAO;
import es.unican.gasolineras.common.exceptions.LatitudInvalidaException;
import es.unican.gasolineras.common.exceptions.LongitudInvalidaException;
import es.unican.gasolineras.common.exceptions.RadioInvalidoException;
import es.unican.gasolineras.model.InterestPoint;
import es.unican.gasolineras.model.validators.InterestPointValidator;

/**
 * The presenter of the points activity of the application. It controls {@link PointsView}
 */
public class PointsPresenter implements IPointsContract.Presenter {

    /** The view that is controlled by this presenter */
    private IPointsContract.View view;
    private List<InterestPoint> points;
    private IInterestPointsDAO interestPointsDAO;

    /**
     * @see IPointsContract.Presenter#init(IPointsContract.View)
     */
    @Override
    public void init(IPointsContract.View view) {
        this.view = view;
        this.view.init();
        interestPointsDAO = view.getPointsDao();
        load();
    }

    /**
     * @see IPointsContract.Presenter#onHomeClicked()
     */
    public void onHomeClicked() {
        view.showMainPage();
    }

    /**
     * @see IPointsContract.Presenter#onCreatePointOfInterestClicked()
     */
    @Override
    public void onCreatePointOfInterestClicked() {
        view.showPointOfInterestPopUp();
    }

    /**
     * @see IPointsContract.Presenter#onAcceptNewPointOfInterestClicked(InterestPoint newInterestPoint)
     */
    @Override
    public void onAcceptNewPointOfInterestClicked(InterestPoint newInterestPoint)
            throws LatitudInvalidaException, LongitudInvalidaException, RadioInvalidoException {
        InterestPointValidator.checkFields(newInterestPoint);
        interestPointsDAO.addInterestPoint(newInterestPoint);
        load();
    }

    /**
     * @see IPointsContract.Presenter#onPointOfInterestClicked(InterestPoint interestPoint)
     */
    @Override
    public void onPointOfInterestClicked(InterestPoint interestPoint) {
        view.launchMainActivityWith(interestPoint);
    }

    /**
     * @see IPointsContract.Presenter#onActivateDeleteModeClicked()
     */
    @Override
    public void onActivateDeleteModeClicked() {
        view.showDeleteMode();
    }

    /**
     * @see IPointsContract.Presenter#onCancelDeleteModeClicked()
     */
    @Override
    public void onCancelDeleteModeClicked() {
        view.showNormalMode();
    }

    /**
     * @see IPointsContract.Presenter#onTrashIconClicked(int)
     */
    @Override
    public void onTrashIconClicked(int selectedIP) {
        view.showDeleteConfirmationPopup(selectedIP);
    }

    /**
     * @see IPointsContract.Presenter#onConfirmDeletionClicked(int)
     */
    @Override
    public void onConfirmDeletionClicked(int idSelectedPoint) {
        InterestPoint removedPoint = view.getPointsDao().getInterestPointById(idSelectedPoint);
        if(removedPoint!= null){
            try {
                view.getPointsDao().deleteInterestPoint(removedPoint);
                points.remove(removedPoint);
                view.showPoints(points);
                view.showInfoDeletedPoint(removedPoint.getName());
            } catch (SQLiteException e) {
                view.showDeleteError();
            }
        }else{
            view.showDeleteError();
        }

    }

    /**
     * Loads the interest points from the DDBB, and sends them to the view
     */
    private void load() {
        try {
        points = interestPointsDAO.getInterestPoints();
        } catch (SQLiteException e) {
            view.showLoadError();
            points = new ArrayList<>();
        }

        points.sort(Comparator.comparing(InterestPoint::getCreationDate));
        view.showPoints(points);
    }
}
