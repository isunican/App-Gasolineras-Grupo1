package es.unican.gasolineras.activities.points;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.not;
import static es.unican.gasolineras.utils.Matchers.withListSize;
import static es.unican.gasolineras.utils.MockRepositories.getTestRepository;

import android.content.Context;
import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import es.unican.gasolineras.R;
import es.unican.gasolineras.activities.main.MainView;
import es.unican.gasolineras.common.database.IInterestPointsDAO;
import es.unican.gasolineras.common.database.MyFuelDatabase;
import es.unican.gasolineras.injection.RepositoriesModule;
import es.unican.gasolineras.model.InterestPoint;
import es.unican.gasolineras.repository.IGasolinerasRepository;

@UninstallModules(RepositoriesModule.class)
@HiltAndroidTest
public class DeleteInterestPointSuccesUITest {

    private View decorView;

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    // Mock repository with JSON data (is required, but we didn't use it in this case)
    @BindValue
    final IGasolinerasRepository repository = getTestRepository(context, R.raw.gasolineras_ccaa_06);

    @Before
    public void setUp() {
        // Initialize decorView and DAO
        activityRule.getScenario().onActivity(activity -> decorView = activity.getWindow().getDecorView());
        IInterestPointsDAO interestPointsDAO = MyFuelDatabase.getInstance(context).getInterestPointsDAO();
        initializeData(interestPointsDAO);
    }
    private void initializeData(IInterestPointsDAO interestPointsDAO){
        interestPointsDAO.deleteAll();
        InterestPoint interestPoint = new InterestPoint("Zona Norte","#999999", 40.0637, -82.3467,20.0);
        interestPoint.setId(1);
        interestPointsDAO.addInterestPoint(interestPoint);
        InterestPoint interestPoint2 = new InterestPoint("Zona Central","#EE639E", 87.1234, -34.0987,10.0);
        interestPoint2.setId(2);
        interestPointsDAO.addInterestPoint(interestPoint2);
        InterestPoint interestPoint3 = new InterestPoint("Zona sur","#783f04", 34.1526, 12.3456,20.0);
        interestPoint3.setId(3);
        interestPointsDAO.addInterestPoint(interestPoint3);
    }


    @Test
    public void DeleteInterestPointsSuccessUITest() {
        // Select the interest point option
        Espresso.onView(withId(R.id.menuPointButton))
                .perform(ViewActions.click());

        // Verify that 3 points of interest are loaded
        Espresso.onView(withId(R.id.lvPoints))
                .check(matches(withListSize(3)));

        Espresso.onData(anything()).inAdapterView(withId(R.id.lvPoints)).atPosition(0).
                onChildView(withId(R.id.tvName)).
                check(matches(withText("Zona Norte")));
        Espresso.onData(anything()).inAdapterView(withId(R.id.lvPoints)).atPosition(1).
                onChildView(withId(R.id.tvName)).
                check(matches(withText("Zona Central")));
        Espresso.onData(anything()).inAdapterView(withId(R.id.lvPoints)).atPosition(2).
                onChildView(withId(R.id.tvName)).
                check(matches(withText("Zona sur")));

        // Select the delete interest point button, check view changed and delete one interest point
        Espresso.onView(withId(R.id.btn_delete))
                .perform(ViewActions.click());

        Espresso.onView(withId(R.id.btn_exit_delete_mode)).
                check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        Espresso.onData(anything()).inAdapterView(withId(R.id.lvPoints)).atPosition(0).
                onChildView(withId(R.id.ivTrash)).
                check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        Espresso.onData(anything()).inAdapterView(withId(R.id.lvPoints)).atPosition(1).
                onChildView(withId(R.id.ivTrash)).
                perform(ViewActions.click());

        //Check confirmation popup and click confirm

        Espresso.onView(withText(R.string.point_confirmation_title)).
                check(matches(isDisplayed()));
        Espresso.onView(withText(R.string.point_confirmation_description)).
                check(matches(isDisplayed()));
        Espresso.onView(withText(R.string.point_confirmation_cancel_button)).
                check(matches(isDisplayed()));
        Espresso.onView(withText(R.string.point_confirmation_ok_button)).
                check(matches(isDisplayed())).
                perform(ViewActions.click());

        // Verify that now the list of points of interest show 2 items and not the deleted one
        Espresso.onView(withId(R.id.lvPoints))
                .check(matches(withListSize(2)));

        Espresso.onData(anything()).inAdapterView(withId(R.id.lvPoints)).atPosition(0).
                onChildView(withId(R.id.tvName)).
                check(matches(withText("Zona Norte")));
        Espresso.onData(anything()).inAdapterView(withId(R.id.lvPoints)).atPosition(1).
                onChildView(withId(R.id.tvName)).
                check(matches(withText("Zona sur")));

        //check view is still in deleteMode

        Espresso.onView(withId(R.id.btn_exit_delete_mode)).
                check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        Espresso.onData(anything()).inAdapterView(withId(R.id.lvPoints)).atPosition(0).
                onChildView(withId(R.id.ivTrash)).
                check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        //Espresso.onView(withText("Se ha eliminado el punto de interes 'Zona Central'")).inRoot(RootMatchers.withDecorView(not(decorView))).check(matches(isDisplayed()));

        //exit delete mode and check normal mode is displaying

        Espresso.onView(withId(R.id.btn_exit_delete_mode)).
                perform(ViewActions.click());

        Espresso.onView(withId(R.id.btn_exit_delete_mode)).
                check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        Espresso.onData(anything()).inAdapterView(withId(R.id.lvPoints)).atPosition(0).
                onChildView(withId(R.id.ivTrash)).
                check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        Espresso.onView(withId(R.id.btn_delete)).
                check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(withId(R.id.btn_add)).
                check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(withId(R.id.img_center)).
                check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

    }
}
