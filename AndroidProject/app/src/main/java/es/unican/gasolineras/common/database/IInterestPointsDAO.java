package es.unican.gasolineras.common.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import es.unican.gasolineras.model.InterestPoint;

@Dao

/**
 * Interface for the database.
 * We define the methods to interact with the database
 * @Author Lucia Fernandez Mancebo
 */

public interface IInterestPointsDAO {

    @Query("SELECT * FROM InterestPoint")
    public List<InterestPoint> getInterestPoints();

    @Query("SELECT * FROM InterestPoint WHERE name = :name")
    public InterestPoint getInterestPointByName(String name);

    @Query("SELECT * FROM InterestPoint WHERE id = :id")
    public InterestPoint getInterestPointById(int id);

    @Insert
    public void addInterestPoint(InterestPoint interestPoint);

    @Delete
    public void deleteInterestPoint(InterestPoint interestPoint);

    @Update
    public void updateInterestPoint(InterestPoint interestPoint);

    @Query("DELETE FROM InterestPoint")
    public void deleteAll();

}
