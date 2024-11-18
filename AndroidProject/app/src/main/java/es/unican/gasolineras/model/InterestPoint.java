package es.unican.gasolineras.model;



import android.graphics.Color;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Entity
public class InterestPoint implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo (name = "color")
    private int colorArgb;

    @ColumnInfo (name = "latitude")
    private double latitude;

    @ColumnInfo (name = "longitude")
    private double longitude;

    @ColumnInfo (name = "radius")
    private double radius;

    @ColumnInfo (name = "creationDate")
    private Date creationDate;

    @Ignore
    private Color color;

    private void inicializateData(String name, Color color, double latitude, double longitude, double radius) {
        this.name = name;
        this.color = color;
        this.colorArgb = color.toArgb();
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.creationDate = Calendar.getInstance().getTime();
    }


    public InterestPoint(String name, Color color, double latitude, double longitude, double radius) {
        inicializateData(name, color, latitude, longitude, radius);
    }
    public InterestPoint(String name, int colorArgb, double latitude, double longitude, double radius) {
        inicializateData(name, Color.valueOf(colorArgb), latitude, longitude, radius);
    }

    public InterestPoint(String name, String colorString, double latitude, double longitude, double radius) {
        inicializateData(name, Color.valueOf(Color.parseColor(colorString)), latitude, longitude, radius);
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
     * Gets the colorArgb of the interest point.
     *
     * @return the colorArgb of the interest point.
     */
    public int getColorArgb() {
        return colorArgb;
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

    /**
     * Gets the color of the interest point.
     *
     * @return the color of the interest point.
     */
    public Color getColor() {
        if(color == null){
            color = Color.valueOf(this.colorArgb);
        }
        return color;
    }

    /**
     * Sets the color of the interest point.
     *
     * @param color the new color of the interest point.
     */
    public void setColor(Color color) {
        this.color = color;
        this.colorArgb = color.toArgb();
    }

    /**
     * Returns the location of a gas station.
     *
     * @return the location.
     */
    public Location getLocation() {
        Location l = new Location(String.format("Interest point named %s", this.name));
        l.setLatitude(this.latitude);
        l.setLongitude(this.longitude);
        return l;
    }

    /**
     * Return if a gas station is in the interest point.
     *
     * @param g the gas station.
     *
     * @return true if is in the interest point or false if not.
     */
    public boolean isGasStationInRadius(Gasolinera g) {
        if (g == null) return false;
        float distance = this.getLocation().distanceTo(g.getLocation());
        return distance <= (this.radius * 1000);
    }

    protected InterestPoint(Parcel in) {
        id = in.readInt();
        name = in.readString();
        colorArgb = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        radius = in.readDouble();
        creationDate = new Date(in.readLong()); // Convierte long a Date
        color = Color.valueOf(colorArgb); // Recrea el color
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(colorArgb);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(radius);
        dest.writeLong(creationDate.getTime()); // Convierte Date a long
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InterestPoint> CREATOR = new Creator<InterestPoint>() {
        @Override
        public InterestPoint createFromParcel(Parcel in) {
            return new InterestPoint(in);
        }

        @Override
        public InterestPoint[] newArray(int size) {
            return new InterestPoint[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterestPoint that = (InterestPoint) o;
        return id == that.id && colorArgb == that.colorArgb && Double.compare(latitude, that.latitude) == 0 && Double.compare(longitude, that.longitude) == 0 && Double.compare(radius, that.radius) == 0 && Objects.equals(name, that.name) && Objects.equals(creationDate, that.creationDate) && Objects.equals(color, that.color);
    }

}
