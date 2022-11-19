package es.unican.is.appgasolineras.activities.main;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import es.unican.is.appgasolineras.R;
import es.unican.is.appgasolineras.common.prefs.IPrefs;
import es.unican.is.appgasolineras.model.Gasolinera;

public class GasolinerasArrayAdapter extends ArrayAdapter<Gasolinera> {
    private IPrefs prefs;
    private static final String TIPOGASOLINA = "tipoGasolina";

    public GasolinerasArrayAdapter(@NonNull Context context, @NonNull List<Gasolinera> objects, IPrefs prefs) {
        super(context, 0, objects);
        this.prefs = prefs;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Gasolinera gasolinera = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.activity_main_item, parent, false);
        }

        // logo
        setLogo(gasolinera, convertView);

        // name
        setName(gasolinera, convertView);

        // address
        setAddress(gasolinera, convertView);

        if (prefs.getString(TIPOGASOLINA).equals("") || prefs.getString("favoritas").equals("si")) {
            // 95 octanes price
            TextView tvLabel = convertView.findViewById(R.id.tv95Label);
            String label = getContext().getResources().getString(R.string.gasolina95label);
            tvLabel.setText(label + ":");

            TextView tv = convertView.findViewById(R.id.tv95);
            tv.setText(gasolinera.getNormal95());

            // diesel A price
            TextView tvLabel2 = convertView.findViewById(R.id.tvDieselALabel);
            String label2 = getContext().getResources().getString(R.string.dieselAlabel);
            tvLabel2.setText(label2 + ":");

            TextView tv2 = convertView.findViewById(R.id.tvDieselA);
            tv2.setText(gasolinera.getDieselA());
        } else {
            if (prefs.getString(TIPOGASOLINA).equals("normal95")) {
                TextView tvLabel = convertView.findViewById(R.id.tv95Label);

                String label = getContext().getResources().getString(R.string.gasolina95label);
                tvLabel.setText(label + ":");

                TextView tv = convertView.findViewById(R.id.tv95);
                tv.setText(gasolinera.getNormal95());
                setTextDieselVacio(convertView);
            } else if (prefs.getString(TIPOGASOLINA).equals("dieselA")) {
                TextView tvLabel = convertView.findViewById(R.id.tv95Label);

                String label = getContext().getResources().getString(R.string.dieselAlabel);
                tvLabel.setText(label + ":");

                TextView tv = convertView.findViewById(R.id.tv95);
                tv.setText(gasolinera.getDieselA());
                setTextDieselVacio(convertView);
            } else if (prefs.getString(TIPOGASOLINA).equals("normal95E10")) {
                TextView tvLabel = convertView.findViewById(R.id.tv95Label);

                String label = "Gasolina 95 E10";
                tvLabel.setText(label + ":");

                TextView tv = convertView.findViewById(R.id.tv95);
                tv.setText(gasolinera.getGasolina95E10());
                setTextDieselVacio(convertView);
            } else if (prefs.getString(TIPOGASOLINA).equals("normal95E5p")) {
                TextView tvLabel = convertView.findViewById(R.id.tv95Label);

                String label = "Gasolina 95 E5 Premium";
                tvLabel.setText(label + ":");

                TextView tv = convertView.findViewById(R.id.tv95);
                tv.setText(gasolinera.getNormal95Prem());
                setTextDieselVacio(convertView);
            } else if (prefs.getString(TIPOGASOLINA).equals("normal98E5")) {
                TextView tvLabel = convertView.findViewById(R.id.tv95Label);

                String label = "Gasolina 98 E5";
                tvLabel.setText(label + ":");

                TextView tv = convertView.findViewById(R.id.tv95);
                tv.setText(gasolinera.getGasolina98E5());
                setTextDieselVacio(convertView);
            } else if (prefs.getString(TIPOGASOLINA).equals("normal98E10")) {
                TextView tvLabel = convertView.findViewById(R.id.tv95Label);

                String label = "Gasolina 98 E10";
                tvLabel.setText(label + ":");

                TextView tv = convertView.findViewById(R.id.tv95);
                tv.setText(gasolinera.getGasolina98E10());
                setTextDieselVacio(convertView);
            } else if (prefs.getString(TIPOGASOLINA).equals("dieselP")) {
                TextView tvLabel = convertView.findViewById(R.id.tv95Label);

                String label = "Diesel Premium";
                tvLabel.setText(label + ":");

                TextView tv = convertView.findViewById(R.id.tv95);
                tv.setText(gasolinera.getDieselPrem());
                setTextDieselVacio(convertView);
            } else if (prefs.getString(TIPOGASOLINA).equals("dieselB")) {
                TextView tvLabel = convertView.findViewById(R.id.tv95Label);

                String label = "Diesel B";
                tvLabel.setText(label + ":");

                TextView tv = convertView.findViewById(R.id.tv95);
                tv.setText(gasolinera.getDieselB());
                setTextDieselVacio(convertView);
            } else if (prefs.getString(TIPOGASOLINA).equals("bioEtanol")) {
                TextView tvLabel = convertView.findViewById(R.id.tv95Label);

                String label = "Bioetanol";
                tvLabel.setText(label + ":");

                TextView tv = convertView.findViewById(R.id.tv95);
                tv.setText(gasolinera.getBioetanol());
                setTextDieselVacio(convertView);
            } else if (prefs.getString(TIPOGASOLINA).equals("bioDiesel")) {
                TextView tvLabel = convertView.findViewById(R.id.tv95Label);

                String label = "Biodiesel";
                tvLabel.setText(label + ":");

                TextView tv = convertView.findViewById(R.id.tv95);
                tv.setText(gasolinera.getBiodiesel());
                setTextDieselVacio(convertView);
            } else if (prefs.getString(TIPOGASOLINA).equals("glp")) {
                TextView tvLabel = convertView.findViewById(R.id.tv95Label);

                String label = "Gases licuados del petróleo";
                tvLabel.setText(label + ":");

                TextView tv = convertView.findViewById(R.id.tv95);
                tv.setText(gasolinera.getGasLicPet());
                setTextDieselVacio(convertView);
            } else if (prefs.getString(TIPOGASOLINA).equals("gasC")) {
                TextView tvLabel = convertView.findViewById(R.id.tv95Label);

                String label = "Gas Natural Comprimido";
                tvLabel.setText(label + ":");

                TextView tv = convertView.findViewById(R.id.tv95);
                tv.setText(gasolinera.getGasNatComp());
                setTextDieselVacio(convertView);
            } else if (prefs.getString(TIPOGASOLINA).equals("gasL")) {
                TextView tvLabel = convertView.findViewById(R.id.tv95Label);

                String label = "Gas Natural Licuado";
                tvLabel.setText(label + ":");

                TextView tv = convertView.findViewById(R.id.tv95);
                tv.setText(gasolinera.getGasNatLic());
                setTextDieselVacio(convertView);
            } else if (prefs.getString(TIPOGASOLINA).equals("h2")) {
                TextView tvLabel = convertView.findViewById(R.id.tv95Label);

                String label = "Hidrógeno";
                tvLabel.setText(label + ":");

                TextView tv = convertView.findViewById(R.id.tv95);
                tv.setText(gasolinera.getHidrogeno());
                setTextDieselVacio(convertView);
            }
        }
        return convertView;
    }

    private void setLogo(Gasolinera gasolinera, View convertView) {
        String rotulo = gasolinera.getRotulo().toLowerCase();

        int imageID = getContext().getResources()
                .getIdentifier(rotulo, "drawable", getContext().getPackageName());

        // Si el rotulo son sólo numeros, el método getIdentifier simplemente devuelve
        // como imageID esos números, pero eso va a fallar porque no tendré ningún recurso
        // que coincida con esos números
        if (imageID == 0 || TextUtils.isDigitsOnly(rotulo)) {
            imageID = getContext().getResources()
                    .getIdentifier("generic", "drawable", getContext().getPackageName());
        }

        if (imageID != 0) {
            ImageView view = convertView.findViewById(R.id.ivLogo);
            view.setImageResource(imageID);
        }
    }

    private void setName(Gasolinera gasolinera, View convertView) {
        TextView tv = convertView.findViewById(R.id.tvName);
        tv.setText(gasolinera.getRotulo());
    }

    private void setAddress(Gasolinera gasolinera, View convertView) {
        TextView tv = convertView.findViewById(R.id.tvAddress);
        tv.setText(gasolinera.getDireccion());
    }

    private void setTextDieselVacio(@NonNull View convertView) {
        TextView tvLabel = convertView.findViewById(R.id.tvDieselALabel);
        tvLabel.setText("");

        TextView tv = convertView.findViewById(R.id.tvDieselA);
        tv.setText("");
    }
}
