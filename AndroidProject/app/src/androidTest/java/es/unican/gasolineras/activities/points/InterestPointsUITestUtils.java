package es.unican.gasolineras.activities.points;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Objects;

import es.unican.gasolineras.R;
import es.unican.gasolineras.model.InterestPoint;

public class InterestPointsUITestUtils {

    public static void checkInterestPointToUI(InterestPoint expectedPoint, DataInteraction element) {
        // Check each attribute in the list item
        element.onChildView(withId(R.id.tvName))
                .check(matches(withText(expectedPoint.getName())));

        // Check color of R.id.ivLocation drawable
        element.onChildView(withId(R.id.ivLocation))
                .check(matches(withTintColor(expectedPoint.getColorArgb())));   // Custom matcher

        element.onChildView(withId(R.id.tvLatitude))
                .check(matches(withText(String.valueOf(expectedPoint.getLatitude()))));

        element.onChildView(withId(R.id.tvLongitude))
                .check(matches(withText(String.valueOf(expectedPoint.getLongitude()))));

        element.onChildView(withId(R.id.tvRadiusValue))
                .check(matches(withText(String.valueOf(expectedPoint.getRadius()))));
    }

    public static void writeDataToCreateInterestPoint(
            String name, String latitude, String longitude, String radius
    ) {
        // Write the name
        Espresso.onView(withId(R.id.tvPIName))
                .perform(ViewActions.typeText(name));

        // Write the latitude
        Espresso.onView(withId(R.id.tvPILatitud))
                .perform(ViewActions.typeText(latitude));

        // Write the longitude
        Espresso.onView(withId(R.id.tvPILongitud))
                .perform(ViewActions.typeText(longitude));

        // Write the radius
        Espresso.onView(withId(R.id.tvPIRadio))
                .perform(ViewActions.typeText(radius));
    }

    private static Matcher<View> withTintColor(final int expectedColod) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            protected boolean matchesSafely(ImageView imageView) {
                // Get the tint color of the drawable stored as Tag
                Color actualColor = (Color) imageView.getTag();
                return (Objects.equals(actualColor.toArgb(), expectedColod));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with tint color: " + expectedColod);
            }
        };
    }

}
