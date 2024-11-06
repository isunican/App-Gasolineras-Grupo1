package es.unican.gasolineras.activities.points;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import es.unican.gasolineras.R;
import es.unican.gasolineras.model.InterestPoint;

/**
 * Adapter that renders the interest points in each row of a ListView
 */
public class PointsArrayAdapter extends BaseAdapter {

    /** The list of interest points to render */
    private final List<InterestPoint> points;

    /** Context of the application */
    private final Context context;

    /**
     * Constructs an adapter to handle a list of interest points
     * @param context the application context
     * @param objects the list of gas stations
     */
    public PointsArrayAdapter(@NonNull Context context, @NonNull List<InterestPoint> objects) {
        // we know the parameters are not null because of the @NonNull annotation
        this.points = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return points.size();
    }

    @Override
    public Object getItem(int position) {
        return points.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("DiscouragedApi")  // to remove warnings about using getIdentifier
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        InterestPoint point = (InterestPoint) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.activity_points_list_item, parent, false);
        }

        // logo of location
        {
            ImageView iv = convertView.findViewById(R.id.ivLocation);

            // Obtén el Drawable del vector
            Drawable drawable = Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.location)).mutate();

            // Usa DrawableCompat para garantizar compatibilidad con versiones anteriores
            Drawable wrappedDrawable = DrawableCompat.wrap(drawable);

            // Cambia el color de la imagen
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor(point.getColor()));

            // Establecemos un tag a modo de ID de color para poder manejarlo más facil
            iv.setTag(point.getColor());

            // Asigna el Drawable al ImageView
            iv.setImageDrawable(wrappedDrawable);
        }

        // name
        {
            TextView tv = convertView.findViewById(R.id.tvName);
            tv.setText(point.getName());
        }

        // latitude
        {
            TextView tv = convertView.findViewById(R.id.tvLatitude);
            double latitude = point.getLatitude();
            latitude = Math.round(latitude * 10000.0) / 10000.0;    // Formato para 4 decimales
            tv.setText(String.valueOf(latitude));
        }

        // longitude
        {
            TextView tv = convertView.findViewById(R.id.tvLongitude);
            double longitude = point.getLongitude();
            longitude = Math.round(longitude * 10000.0) / 10000.0;    // Formato para 4 decimales
            tv.setText(String.valueOf(longitude));
        }

        // radious
        {
            TextView tv = convertView.findViewById(R.id.tvRadiusValue);
            double radius = point.getRadius();
            radius = Math.round(radius * 10.0) / 10.0;    // Formato para 1 decimal
            tv.setText(String.valueOf(radius));
        }

        return convertView;
    }
}
