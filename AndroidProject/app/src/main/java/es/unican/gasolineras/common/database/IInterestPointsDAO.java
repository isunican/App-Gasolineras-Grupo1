package es.unican.gasolineras.common.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import es.unican.gasolineras.model.InterestPoints;

@Dao

/**
 * Interface for the database.
 * We define the methods to interact with the database
 * @Author Lucia Fernandez Mancebo
 */

public interface IInterestPointsDAO {

    @Query("SELECT * FROM interestpoints")
    List<InterestPoints> getInterestPoints();

    @Query("SELECT * FROM interestpoints WHERE name = :name")
    InterestPoints getInterestPointByName(String name);

    @Query("SELECT * FROM interestpoints WHERE id = :id")
    InterestPoints getInterestPointById(int id);

    @Insert
    void addInterestPoint(InterestPoints interestPoints);

    @Delete
    void deleteInterestPoint(InterestPoints interestPoint);

    @Update
    void updateInterestPoint(InterestPoints interestPoint);

}
