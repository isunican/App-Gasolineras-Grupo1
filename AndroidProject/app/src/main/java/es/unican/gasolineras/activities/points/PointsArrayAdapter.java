package es.unican.gasolineras.activities.points;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.List;

import es.unican.gasolineras.R;
import es.unican.gasolineras.model.Gasolinera;
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
                    .inflate(R.layout.activity_points_list, parent, false);
        }

        // logo of location
        {
            ImageView iv = convertView.findViewById(R.id.ivLocation);

            // Get the color string from point.getColor() (assuming it's a hex color string like "#000000")
            String colorString = point.getColor();
            int color = Color.parseColor(colorString); // Parse the color string to an int

            // Set the vector drawable tint
            Drawable drawable = iv.getDrawable();
            if (drawable != null) {
                drawable.mutate(); // To avoid affecting other instances of this drawable
                drawable.setTint(color); // Apply the color
            }
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
            DecimalFormat df = new DecimalFormat("#.####"); // Formato para 4 decimales
            tv.setText(df.format(latitude));
        }

        // longitude
        {
            TextView tv = convertView.findViewById(R.id.tvLongitude);
            double longitude = point.getLongitude();
            DecimalFormat df = new DecimalFormat("#.####"); // Formato para 4 decimales
            tv.setText(df.format(longitude));
        }

        // radious
        {
            TextView tv = convertView.findViewById(R.id.tvRadiusValue);
            double radius = point.getRadius();
            DecimalFormat df = new DecimalFormat("#.#"); // Formato para 1 decimal
            tv.setText(df.format(radius));
        }

        return convertView;
    }
}
