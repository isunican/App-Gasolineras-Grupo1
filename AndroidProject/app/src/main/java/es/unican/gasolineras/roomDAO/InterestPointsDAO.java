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

    protected  InterestPointsDAO(Context context) {
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


}
