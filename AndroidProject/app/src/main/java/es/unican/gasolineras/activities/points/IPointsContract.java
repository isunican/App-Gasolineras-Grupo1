package es.unican.gasolineras.activities.points;

import java.util.List;

import es.unican.gasolineras.model.InterestPoint;
import es.unican.gasolineras.roomDAO.InterestPointsDAO;

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
         * When you click on the delete mode button, the view updates to support point of interest deletion.
         */
        public void onActivateDeleteModeClicked();

        /**
         * When you click on the cancel delete mode button, the view reverts to the normal mode, disabling point of interest deletion.
         */
        public void onCancelDeleteModeClicked();

        /**
         * When you click on the trash icon of a point of interest, a popup requests confirmation for deletion.
         * @param selectedIP the selected point of interest to delete.
         */
        public void onTrashIconClicked(InterestPoint selectedIP);

        /**
         * When you click on the confirm delete button in the popup, the point of interest is deleted.
         * @param selectedIP the point of interest to delete.
         */
        public void onConfirmDeletionClicked(InterestPoint selectedIP);
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
         * Get the instance of the DDBB.
         * @return the instance of the DDBB.
         */
        public InterestPointsDAO getPointsDao();

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
         * @param selectedIP the point of interest selected for deletion.
         */
        public void showDeleteConfirmationPopup(InterestPoint selectedIP);
    }
}
