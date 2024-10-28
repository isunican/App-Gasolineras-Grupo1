package es.unican.gasolineras.roomDAO;

import android.content.Context;

import androidx.room.Room;

import java.util.Collections;
import java.util.List;

import es.unican.gasolineras.common.database.IInterestPointsDAO;
import es.unican.gasolineras.common.database.InterestPointsDatabase;
import es.unican.gasolineras.model.InterestPoints;

/**
 * Singleton class, whose instance is used to access the database.
 *
 * @Author Lucia Fernandez Mancebo
 */
public class InterestPointsDAO  {

    protected static InterestPointsDAO myInstance;

    private IInterestPointsDAO myInterestPointsDAO;

    protected InterestPointsDAO(Context context) {
        Context appContext = context.getApplicationContext();
        // Create the database with the entity class, without creating a parallel execution thread
        InterestPointsDatabase database = Room.databaseBuilder(appContext, InterestPointsDatabase.class, "interestpoints")
                .allowMainThreadQueries().build();
        myInterestPointsDAO = database.getInterestPointsDAO();

    }

    public static InterestPointsDAO getInstance(Context context){
        if(myInstance == null){
            myInstance = new InterestPointsDAO(context);
        }
        return myInstance;
    }

    /**
     * Method to call the @Query method defined in the InterestPointsDAO interface.
     * @return a list of interest points.
     */

    public List<InterestPoints> getInterestPoints() {
        return Collections.emptyList();
    }

    /**
     * Method to call the @Query method defined in the InterestPointsDAO interface.
     * @return an interest point searched by name.
     */

    public InterestPoints getInterestPointByName(String name) {
        return myInterestPointsDAO.getInterestPointByName(name);
    }

    /**
     * Method to call the @Query method defined in the InterestPointsDAO interface.
     * @return an interest point searched by id
     */

    public InterestPoints getInterestPointById(int id) {
        return myInterestPointsDAO.getInterestPointById(id);
    }

    /**
     * Method to call the @insert method defined in the InterestPointsDAO interface.
     */

    public void addInterestPoint(InterestPoints interestPoint) {
        myInterestPointsDAO.addInterestPoint(interestPoint);
    }

    /**
     * Method to call the @delete method defined in the InterestPointsDAO interface.
     */

    public void deleteInterestPoint(InterestPoints interestPoint) {
        myInterestPointsDAO.deleteInterestPoint(interestPoint);
    }

    /**
     * Method to call the @update method defined in the InterestPointsDAO interface.
     */

    public void updateInterestPoint(InterestPoints interestPoint) {
        myInterestPointsDAO.updateInterestPoint(interestPoint);
    }



}
