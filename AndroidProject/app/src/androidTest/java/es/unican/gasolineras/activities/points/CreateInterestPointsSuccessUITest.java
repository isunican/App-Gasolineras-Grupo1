package es.unican.gasolineras.activities.points;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static es.unican.gasolineras.utils.Matchers.withListSize;
import static es.unican.gasolineras.utils.MockRepositories.getTestRepository;

import android.content.Context;
import android.view.View;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import es.unican.gasolineras.R;
import es.unican.gasolineras.activities.main.MainView;
import es.unican.gasolineras.injection.RepositoriesModule;
import es.unican.gasolineras.model.InterestPoint;
import es.unican.gasolineras.repository.IGasolinerasRepository;
import es.unican.gasolineras.roomDAO.InterestPointsDAO;

@UninstallModules(RepositoriesModule.class)
@HiltAndroidTest
public class CreateInterestPointsSuccessUITest {

    private View decorView;

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    // Mock repository with JSON data (is required, but we didn't use it in this case)
    @BindValue
    final IGasolinerasRepository repository = getTestRepository(context, R.raw.gasolineras_ccaa_06);

    // DAO instance for storing interest points
    private InterestPointsDAO interestPointsDAO;

    @Before
    public void setUp() {
        // Initialize decorView and DAO
        activityRule.getScenario().onActivity(activity -> decorView = activity.getWindow().getDecorView());
        interestPointsDAO = InterestPointsDAO.getInstance(context);
    }

    @Test
    public void createInterestPointsSuccessUITest() {
        // Define expected interest points
        List<InterestPoint> expectedPoints = new ArrayList<>();

        InterestPoint point1 = new InterestPoint("punto 1", "#ff0000", 45.0000, -123.3450, 12.4);
        point1.setCreationDate(Date.valueOf("2024-07-12"));
        expectedPoints.add(point1);

        InterestPoint point2 = new InterestPoint("punto 2", "#00ff00", 65.0400, 23.3770, 6.0);
        point2.setCreationDate(Date.valueOf("2024-08-10"));
        expectedPoints.add(point2);

        // Insert expected points into the DAO
        for (InterestPoint point : expectedPoints) {
            interestPointsDAO.getMyInterestPointsDAO().addInterestPoint(point);
        }

        // Select the interest point option
        Espresso.onView(withId(R.id.menuPointButton))
                .perform(ViewActions.click());

        // Select the create interest point button
        Espresso.onView(withId(R.id.btn_add))
                .perform(ViewActions.click());

        // Write the data
        InterestPointsUITestUtils.writeDataToCreateInterestPoint(
                "Zona-norte", "40.0637", "82.3467", "20"
        );

        // Click the button to create
        Espresso.onView(withId(R.id.newPI_accept_button))
                .perform(ViewActions.click());

        // Verify that 3 points of interest are loaded
        Espresso.onView(withId(R.id.lvPoints))
                .check(matches(withListSize(3)));


        // Add the point to the list
        expectedPoints.add(
                new InterestPoint("Zona-norte", "#808080", 40.0637, 82.3467, 20)
        );

        // Verify each interest point meets the requirements using a loop
        for (int i = 0; i < expectedPoints.size(); i++) {
            InterestPoint expectedPoint = expectedPoints.get(i);

            DataInteraction elto = Espresso.onData(CoreMatchers.anything())
                    .inAdapterView(withId(R.id.lvPoints))
                    .atPosition(i);

            // Check each attribute in the list item
            InterestPointsUITestUtils.checkInterestPointToUI(expectedPoint, elto);
        }

        Espresso.pressBack();
    }

}
