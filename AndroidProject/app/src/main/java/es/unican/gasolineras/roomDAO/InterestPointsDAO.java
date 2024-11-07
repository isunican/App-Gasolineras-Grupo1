package es.unican.gasolineras.roomDAO;

import android.content.Context;

import androidx.room.Room;

import es.unican.gasolineras.common.database.IInterestPointsDAO;
import es.unican.gasolineras.common.database.InterestPointsDatabase;
import lombok.Getter;

/**
 * Singleton class, whose instance is used to access the database.
 *
 * @Author Lucia Fernandez Mancebo
 */
public class InterestPointsDAO  {

    protected static InterestPointsDAO myInstance;

    @Getter
    private IInterestPointsDAO myInterestPointsDAO;

    protected  InterestPointsDAO(Context context) {
        Context appContext = context.getApplicationContext();
        // Create the database with the entity class, without creating a parallel execution thread
        InterestPointsDatabase database = Room.databaseBuilder(appContext, InterestPointsDatabase.class, "interestpoints")
                .fallbackToDestructiveMigrationFrom(1)
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
