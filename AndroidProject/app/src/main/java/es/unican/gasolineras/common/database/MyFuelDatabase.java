package es.unican.gasolineras.common.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import es.unican.gasolineras.model.Gasolinera;
import es.unican.gasolineras.model.InterestPoint;

/**
 * This abstract class returns an instance of the InterestPointsDAO Interface
 * @Author Lucia Fernandez Mancebo
 */

@Database(entities = {InterestPoint.class,
                        Gasolinera.class}, version = 2)

@TypeConverters({Converters.class})
public abstract class MyFuelDatabase extends RoomDatabase {

    private static MyFuelDatabase instance;

    public static MyFuelDatabase getInstance(Context context) {
        if(instance == null){
            instance = Room.databaseBuilder(context, MyFuelDatabase.class, "myfueldb")
                    .fallbackToDestructiveMigrationFrom(1)
                    .allowMainThreadQueries().build();
        }
        return instance;
    }

    public abstract IInterestPointsDAO getInterestPointsDAO();
    public abstract IGasStationsDAO getGasStationsDAO();
}

