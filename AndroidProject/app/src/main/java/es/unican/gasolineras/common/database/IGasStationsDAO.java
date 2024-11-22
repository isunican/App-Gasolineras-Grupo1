package es.unican.gasolineras.common.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import es.unican.gasolineras.model.Gasolinera;

@Dao
public interface IGasStationsDAO {
    @Query("SELECT * FROM Gasolinera")
    List<Gasolinera> getAll();

    @Query("SELECT * FROM Gasolinera WHERE id = :id")
    Gasolinera getGasStationById(int id);

    @Insert
    void addGasStation(Gasolinera gasolinera);

    @Query("DELETE FROM Gasolinera")
    void deleteAll();
}
