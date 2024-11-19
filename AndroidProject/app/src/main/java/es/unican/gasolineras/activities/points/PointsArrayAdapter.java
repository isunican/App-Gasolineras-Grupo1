package es.unican.gasolineras.activities.points;

import android.content.Context;
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

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import es.unican.gasolineras.R;
import es.unican.gasolineras.model.InterestPoint;

/**
 * Adapter that renders the interest points in each row of a ListView
 */
public class PointsArrayAdapter extends BaseAdapter {

    /** The list of interest points to render */
    private final List<InterestPoint> points;

    Consumer<InterestPoint> deleteFunction;
    boolean deleteState;

    /** Context of the application */
    private final Context context;

    /**
     * Constructs an adapter to handle a list of interest points
     * @param context the application context
     * @param objects the list of gas stations
     */
    public PointsArrayAdapter(@NonNull Context context, @NonNull List<InterestPoint> objects, @NonNull Consumer<InterestPoint> deleteFunction, boolean deleteState) {
        // we know the parameters are not null because of the @NonNull annotation
        this.points = objects;
        this.context = context;
        this.deleteFunction = deleteFunction;
        this.deleteState = deleteState;
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        InterestPoint point = (InterestPoint) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.activity_points_list_item, parent, false);
        }

        // logo of location
        setLocationLogo(convertView, point);

        // name
        setName(convertView, point);

        // latitude
        setLatitude(convertView, point);

        // longitude
        setLongitude(convertView, point);

        // radious
        setRaidus(convertView, point);
        {
            TextView tv = convertView.findViewById(R.id.tvRadiusValue);
            double radius = point.getRadius();
            radius = Math.round(radius * 10.0) / 10.0;    // Formato para 1 decimal
            tv.setText(String.valueOf(radius));
        }

        // Delete button
        {
            ImageView iv = convertView.findViewById(R.id.ivTrash);

            // Obtén el Drawable del vector
            Drawable drawable = Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.trash)).mutate();

            // Usa DrawableCompat para garantizar compatibilidad con versiones anteriores
            Drawable wrappedDrawable = DrawableCompat.wrap(drawable);

            // Establecemos un tag a modo de ID de la DDBB del PI para poder manejarlo más facil
            iv.setTag(point.getId());

            iv.setOnClickListener(v -> deleteFunction.accept(point));
            if (deleteState) {
                iv.setVisibility(View.VISIBLE);
            } else {
                iv.setVisibility(View.GONE);
            }

            // Asigna el Drawable al ImageView
            iv.setImageDrawable(wrappedDrawable);
        }

        return convertView;
    }

    private static void setRaidus(@NonNull View convertView, InterestPoint point) {
        TextView tv = convertView.findViewById(R.id.tvRadiusValue);
        double radius = point.getRadius();
        tv.setText(String.format(Locale.US, "%.1f",radius));
    }

    private static void setLongitude(@NonNull View convertView, InterestPoint point) {
        TextView tv = convertView.findViewById(R.id.tvLongitude);
        double longitude = point.getLongitude();
        tv.setText(String.format(Locale.US, "%.4f", longitude));
    }

    private static void setLatitude(@NonNull View convertView, InterestPoint point) {
        TextView tv = convertView.findViewById(R.id.tvLatitude);
        double latitude = point.getLatitude();
        tv.setText(String.format(Locale.US, "%.4f", latitude));
    }

    private static void setName(@NonNull View convertView, InterestPoint point) {
        TextView tv = convertView.findViewById(R.id.tvName);
        tv.setText(point.getName());
    }

    private void setLocationLogo(View convertView, InterestPoint point) {
        ImageView iv = convertView.findViewById(R.id.ivLocation);

        // Obtén el Drawable del vector
        Drawable drawable = Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.location)).mutate();

        // Usa DrawableCompat para garantizar compatibilidad con versiones anteriores
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);

        // Cambia el color de la imagen
        DrawableCompat.setTint(wrappedDrawable, point.getColorArgb());

        // Establecemos un tag a modo de ID de color para poder manejarlo más facil
        iv.setTag(point.getColor());

        // Asigna el Drawable al ImageView
        iv.setImageDrawable(wrappedDrawable);
    }
}
