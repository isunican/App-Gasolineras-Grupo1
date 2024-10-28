package es.unican.gasolineras.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import lombok.*;

@Getter
@Setter
@Entity
public class InterestPoints {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @ColumnInfo (name = "name")
    @NonNull
    private String name;

    @ColumnInfo (name = "color")
    private String color;

    @ColumnInfo (name = "latitude")
    @NonNull
    private double latitude;

    @ColumnInfo (name = "longitude")
    @NonNull
    private double longitude;

    @ColumnInfo (name = "radius")
    @NonNull
    private double radius;

    public InterestPoints(String name, String color, double latitude, double longitude, double radius) {
        this.name = name;
        this.color = color;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

}
