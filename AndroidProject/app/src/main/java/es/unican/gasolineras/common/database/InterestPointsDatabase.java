package es.unican.gasolineras.common.database;

import android.arch.persistence.room.Database;


import androidx.room.RoomDatabase;

import es.unican.gasolineras.model.InterestPoints;

/**
 * This abstract class returns an instance of the InterestPointsDAO Interface
 * @Author Lucia Fernandez Mancebo
 */

@Database(entities = {InterestPoints.class}, version = 1)
public abstract class InterestPointsDatabase extends RoomDatabase {

    public abstract IInterestPointsDAO getInterestPointsDAO();

}
