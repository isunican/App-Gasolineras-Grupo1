package es.unican.gasolineras.common.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import es.unican.gasolineras.model.InterestPoint;

/**
 * This abstract class returns an instance of the InterestPointsDAO Interface
 * @Author Lucia Fernandez Mancebo
 */

@Database(entities = {InterestPoint.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class InterestPointsDatabase extends RoomDatabase {

    public abstract IInterestPointsDAO getInterestPointsDAO();

}
