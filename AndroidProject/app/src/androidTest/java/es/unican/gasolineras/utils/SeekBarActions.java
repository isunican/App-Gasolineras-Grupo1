package es.unican.gasolineras.utils;



import static org.hamcrest.Matchers.instanceOf;


import android.view.View;
import android.widget.SeekBar;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

import es.unican.gasolineras.activities.main.MainPresenter;
import es.unican.gasolineras.common.IFilter;


public class SeekBarActions {

    //private static final double MIN_PRICE = 1.24;
    //private static final double MAX_PRICE = 1.65;

    private static MainPresenter mainPresenter;  //

    // Establecer la instancia del MainPresenter
    public static void setMainPresenter(MainPresenter presenter) {
        mainPresenter = presenter;
    }

    public static ViewAction setProgress(final double v) {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return instanceOf(SeekBar.class);
            }

            @Override
            public String getDescription() {
                return "Set SeekBar progress";
            }

            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                int max = seekBar.getMax();


                double normalizedValue = (v - mainPresenter.getMinPrice()) / (mainPresenter.getMaxPrice() - mainPresenter.getMinPrice());


                int progressF = (int) (normalizedValue * max);


                seekBar.setProgress(progressF);
            }
        };
    }
}