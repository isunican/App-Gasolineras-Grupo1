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
        void showMainPage();
    }
}
