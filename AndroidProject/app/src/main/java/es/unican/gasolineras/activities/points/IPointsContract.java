package es.unican.gasolineras.activities.points;

import java.util.List;

import es.unican.gasolineras.common.database.IInterestPointsDAO;
import es.unican.gasolineras.model.InterestPoint;

/**
 * The Presenter-View contract for the Points activity.
 * The Points activity shows a list of interest points.
 */
public interface IPointsContract {

    /**
     * Methods that must be implemented in the Points Presenter.
     * Only the View should call these methods.
     */
    public interface Presenter {

        /**
         * Links the presenter with its view. It load the points from the DDBB.
         * Only the View should call this method
         * @param view the view to control.
         */
        public void init(View view);

        /**
         * When you click on the house icon, the main page is displayed.
         */
        public void onHomeClicked();

        /**
         * When you click on the create button, a popup is displayed.
         */
        public void onCreatePointOfInterestClicked();

        /**
         * When you click on the accept button of the new Point of Interest Popup,
         * the new Point of Interest it is processed and saved if necesseary
         * also the view is refresed with the new element
         * @param newInterestPoint the new point of interest
         */
        public void onAcceptNewPointOfInterestClicked(InterestPoint newInterestPoint);

        /**
         * When you click on a interest point to see its stations
         * @param interestPoint the point of interest
         */
        public void onPointOfInterestClicked(InterestPoint interestPoint);

        /**
         * When you click on the delete mode button, the view updates to support point of interest deletion.
         */
        public void onActivateDeleteModeClicked();

        /**
         * When you click on the cancel delete mode button, the view reverts to the normal mode, disabling point of interest deletion.
         */
        public void onCancelDeleteModeClicked();

        /**
         * When you click on the trash icon of a point of interest, a popup requests confirmation for deletion.
         * @param idSelectedPoint the ID in the DDBB of the point of interest to delete.
         */
        public void onTrashIconClicked(int idSelectedPoint);

        /**
         * When you click on the confirm delete button in the popup, the point of interest is deleted.
         * @param idSelectedPoint the ID in the DDBB of the point of interest to delete.
         */
        public void onConfirmDeletionClicked(int idSelectedPoint);
    }

    /**
     * Methods that must be implemented in the Points View.
     * Only the Presenter should call these methods.
     */
    public interface View {

        /**
         * Initialize the view. Typically this should initialize all the listeners in the view.
         * Only the Presenter should call this method
         */
        public void init();

        /**
         * Get the instance of the DAO for InterestPoints.
         * @return the instance of the DAO for InterestPoints.
         */
        public IInterestPointsDAO getPointsDao();

        /**
         * The view is requested to display the given list of points.
         * Only the Presenter should call this method
         * @param points the list of charging points
         */
        public void showPoints(List<InterestPoint> points);

        /**
         * The view is requested to display a toast indicating that
         * has been loaded "numPoints" interest points.
         * Only the Presenter should call this method
         * @param numPoints the number of interest points loaded
         */
        public void showLoadCorrect(int numPoints);

        /**
         * The view is requested to display a toast indicating that
         * interest points were not loaded correctly.
         * Only the Presenter should call this method
         */
        public void showLoadError();

        /**
         * The view is requested to display a toast indicating a message.
         * Only the Presenter should call this method
         * @param message the message
         */
        public void showInfoMessage(String message);

        /**
         * The view is requested to open the main activity.
         * Only the Presenter should call this method
         */
        public void showMainPage();

        /**
         * The view is requested to display a new window to allow creation
         * of new Point of Interest
         * Only the presenter should call this method
         */
        public void showPointOfInterestPopUp();

        /**
         * The view is requested to open the main activity with a point of interest.
         * @param selectedIP the point of interest
         */
        public void launchMainActivityWith(InterestPoint selectedIP);

        /**
         * The view is requested to enter delete mode, shifting point information to reveal a trash icon for each item.
         * Additionally, the add, sort, and delete buttons are replaced at the bottom with an exit button for delete mode.
         */
        public void showDeleteMode();

        /**
         * The view is requested to revert to the default view, exiting delete mode.
         */
        public void showNormalMode();

        /**
         * The view is requested to display a popup to confirm the deletion of a selected point of interest.
         * This method notifies the presenter only if deletion is confirmed.
         * @param idSelectedPoint the ID in the DDBB of the point of interest to delete.
         */
        public void showDeleteConfirmationPopup(int idSelectedPoint);

        /**
         * The view is requested to display a toast indicating a point of interest
         * has been deleted and the name of the deleted point
         * @param name of the deleted point of interest
         */
        void showInfoDeletedPoint(String name);

        /**
         * The view is requested to display a toast with a message to notice
         * the user an error has occurred deleting a point of interest
         */
        void showDeleteError();
    }
}
