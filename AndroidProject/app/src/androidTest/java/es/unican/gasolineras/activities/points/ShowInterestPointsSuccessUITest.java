package es.unican.gasolineras.activities.points;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static es.unican.gasolineras.utils.Matchers.withListSize;
import static es.unican.gasolineras.utils.MockRepositories.getTestRepository;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import es.unican.gasolineras.R;
import es.unican.gasolineras.activities.main.MainView;
import es.unican.gasolineras.injection.RepositoriesModule;
import es.unican.gasolineras.model.InterestPoint;
import es.unican.gasolineras.repository.IGasolinerasRepository;

@UninstallModules(RepositoriesModule.class)
@HiltAndroidTest
public class ShowInterestPointsSuccessUITest {

    private View decorView;
    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    // TODO: ¿Hacer un mock repository para los puntos de interes como para las gasolineras con el JSON?
    @BindValue
    final IGasolinerasRepository repository = getTestRepository(context, R.raw.gasolineras_ccaa_06);

    @Before
    public void setUp() {
        activityRule.getScenario().onActivity(activity -> decorView = activity.getWindow().getDecorView());
    }

    @Test
    public void maxPriceFilterSuccessUITest() {

        // Define expected interest points
        List<InterestPoint> expectedPoints = new ArrayList<>();

        InterestPoint point1 = new InterestPoint("punto 1", "#ff0000", 45.0000, -123.3450, 12.4);
        point1.setId(1);
        point1.setCreationDate(parseDate("12/08/2024"));
        expectedPoints.add(point1);

        InterestPoint point2 = new InterestPoint("punto 2", "#00ff00", 65.0400, 23.3770, 6.0);
        point2.setId(2);
        point2.setCreationDate(parseDate("10/07/2024"));
        expectedPoints.add(point2);

        InterestPoint point3 = new InterestPoint("punto 3", "#0000ff", -25.6783, 3.3422, 53.2);
        point3.setId(3);
        point3.setCreationDate(parseDate("01/10/2024"));
        expectedPoints.add(point3);

        // Select the interest point option
        Espresso.onView(withId(R.id.menuPointButton))
                .perform(ViewActions.click());

        //Espresso.onView(withText("Cargados 3 puntos de interes")).inRoot(RootMatchers.withDecorView(not(decorView))).check(matches(isDisplayed()));

        // The number of showed interest points
        Espresso.onView(withId(R.id.lvPoints))
                .check(matches(withListSize(3)));

        // Verify each interest point meets the requirements using a loop
        for (int i = 0; i < expectedPoints.size(); i++) {
            InterestPoint expectedPoint = expectedPoints.get(i);

            DataInteraction elto = Espresso.onData(CoreMatchers.anything())
                    .inAdapterView(withId(R.id.lvPoints))
                    .atPosition(i);

            // Check each attribute in the list item
            //elto.onChildView(withId(R.id.tvId))
            //        .check(matches(withText(String.valueOf(expectedPoint.getId()))));

            elto.onChildView(withId(R.id.tvName))
                    .check(matches(withText(expectedPoint.getName())));

            // Check color of R.id.ivLocation drawable
            elto.onChildView(withId(R.id.ivLocation))
                    .check(matches(withDrawableColor(expectedPoint.getColor())));  // Custom matcher

            elto.onChildView(withId(R.id.tvLatitude))
                    .check(matches(withText(String.valueOf(expectedPoint.getLatitude()))));

            elto.onChildView(withId(R.id.tvLongitude))
                    .check(matches(withText(String.valueOf(expectedPoint.getLongitude()))));

            elto.onChildView(withId(R.id.tvRadiusValue))
                    .check(matches(withText(String.valueOf(expectedPoint.getRadius()))));

            //elto.onChildView(withId(R.id.tvCreationDate))
            //        .check(matches(withText(formatDate(expectedPoint.getCreationDate()))));

            Espresso.pressBack();
        }
    }

    // Helper method to parse date from string
    private Date parseDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("Date parsing failed for " + dateStr);
        }
    }

    // Custom matcher to check drawable color
    public static Matcher<View> withDrawableColor(String colorHex) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            protected boolean matchesSafely(ImageView imageView) {
                Drawable drawable = imageView.getDrawable();
                if (drawable instanceof ColorDrawable) {
                    int expectedColor = Color.parseColor(colorHex);
                    int actualColor = ((ColorDrawable) drawable).getColor();
                    return expectedColor == actualColor;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with drawable color: " + colorHex);
            }
        };
    }
}
