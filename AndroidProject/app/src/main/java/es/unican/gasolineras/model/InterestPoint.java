package es.unican.gasolineras.model;



import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

import lombok.*;

//@Getter
//@Setter

@Entity
public class InterestPoint {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @ColumnInfo(name = "name")
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

    // FIXME: Arreglar lo de la fecha de creacion para poder introducirla en la BBDD.
    @ColumnInfo (name = "creationDate")
    @NonNull
    private Date creationDate;


    public InterestPoint(String name, String color, double latitude, double longitude, double radius) {
        this.name = name;
        this.color = color;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.creationDate = Calendar.getInstance().getTime();
    }

    //generate getters and setters of attributes

    /**
     * Gets the unique identifier of the interest point.
     *
     * @return the ID of the interest point.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the interest point.
     *
     * @param id the new ID of the interest point.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name of the interest point.
     *
     * @return the name of the interest point.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the interest point.
     *
     * @param name the new name of the interest point.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the color of the interest point.
     *
     * @return the color of the interest point.
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the color of the interest point.
     *
     * @param color the new color of the interest point.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Gets the latitude of the interest point.
     *
     * @return the latitude of the interest point.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the interest point.
     *
     * @param latitude the new latitude of the interest point.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude of the interest point.
     *
     * @return the longitude of the interest point.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the interest point.
     *
     * @param longitude the new longitude of the interest point.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the radius of the interest point.
     *
     * @return the radius of the interest point.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Sets the radius of the interest point.
     *
     * @param radius the new radius of the interest point.
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Gets the creation date of the interest point.
     *
     * @return the creation date of the interest point.
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date of the interest point.
     *
     * @param creationDate the new creation date of the interest point.
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

}
