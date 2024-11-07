package es.unican.gasolineras.activities.points.inputFilters;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LatitudInputFilter implements InputFilter {
    private Pattern coordPattern;

    public LatitudInputFilter() {
        coordPattern = Pattern.compile("-?\\d{0,2}(\\.\\d{0,4})?");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String initial = dest.subSequence(0,dstart).toString();
        String replacement = source.subSequence(start,end).toString();
        String finalS = dest.subSequence(dend,(dest.length())).toString();
        String result = initial +replacement +finalS;
        Matcher matcher = coordPattern.matcher(result);
        if (!matcher.matches()) {
            return "";
        }
        return null;
    }
}

