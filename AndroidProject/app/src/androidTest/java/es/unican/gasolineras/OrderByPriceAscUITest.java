package es.unican.gasolineras;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
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

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import es.unican.gasolineras.activities.main.MainView;
import es.unican.gasolineras.common.FuelTypeEnum;
import es.unican.gasolineras.common.OrderMethodsEnum;
import es.unican.gasolineras.injection.RepositoriesModule;
import es.unican.gasolineras.repository.IGasolinerasRepository;

@UninstallModules(RepositoriesModule.class)
@HiltAndroidTest
public class OrderByPriceAscUITest {

    private View testDecorView;
    @Rule(order = 0)  // the Hilt rule must execute first
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    // I need the context to access resources, such as the json with test gas stations
    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    // Mock repository that provides data from a JSON file instead of downloading it from the internet.
    @BindValue
    final IGasolinerasRepository repository = getTestRepository(context, R.raw.gasolineras_test_505739);

    @Before
    public void setUp() {
        activityRule.getScenario().onActivity(activity -> testDecorView = activity.getWindow().getDecorView());
    }

    @Test
    public void OrderByPriceAscSuccessTest() {
        // Click on menuOrderButton
        onView(withId(R.id.menuOrderButton)).perform(click());

        // Verifying that the popup is displayed
        onView(withId(R.id.popup_orderText)).check(matches(isDisplayed()));
        onView(withId(R.id.typeOrderSpinner)).perform(click());

        // Selecting in the fuel type spinner the fuel type
        onData(allOf(is(instanceOf(FuelTypeEnum.class)), equalTo(FuelTypeEnum.GASOLINA_95E5)))
                .inRoot(isPlatformPopup())
                .perform(click());

        // Selecting in the price method spinner the price type
        onView(withId(R.id.orderPriceMethodSpinner)).perform(click());
        onData(allOf(is(instanceOf(OrderMethodsEnum.class)),
                equalTo(OrderMethodsEnum.ASCENDING)))
                .inRoot(isPlatformPopup())
                .perform(click());

        // Accept the popup
        onView(withId(R.id.order_accept_button)).perform(click());

        // Checking if the ListView with the gas stations is displayed correctly
        onView(withId(R.id.lvStations)).check(matches(isDisplayed()));

        // We check the elements on the listview based on the JSON data, we check the first, last and middle element in the listview
        // With this proofs we assumed that the listview is correctly displayed

        // Checking the 1st element in the listview
        DataInteraction listElement = Espresso
                .onData(CoreMatchers.anything())
                .inAdapterView(withId(R.id.lvStations))
                .atPosition(0);  // Selecting the element at the current position

        // We cannot check the id because it does not appear in the view, so we check the rotulo and the price
        listElement.perform(ViewActions.click());
        checkValues(R.id.tvRotulo, "EROSKI");
        checkValues(R.id.tvGasolina95E5, "1.63");
        pressBack(); // Added because of an error in obtaining the listview element because we are in a different view

        // Checking a middle element in the listview
        listElement = Espresso
                .onData(CoreMatchers.anything())
                .inAdapterView(withId(R.id.lvStations))
                .atPosition(3);  // Selecting the element at the current position

        listElement.perform(ViewActions.click());
        checkValues(R.id.tvRotulo, "REPSOL");
        checkValues(R.id.tvGasolina95E5, "1.67");
        pressBack();

        // Checking the last element in the listview
        listElement = Espresso
                .onData(CoreMatchers.anything())
                .inAdapterView(withId(R.id.lvStations))
                .atPosition(6);  // Selecting the element at the current position

        listElement.perform(ViewActions.click());
        checkValues(R.id.tvRotulo, "ALSA");
        checkValues(R.id.tvGasolina95E5, "0.0");
        pressBack();

    }

    /**
     * Method to check if the values are correct
     * @param idElement the element in the view to check
     * @param value the value to check if it is correct
     */
    private void checkValues(int idElement, String value){
        onView(withId(idElement)).check(matches(withText(value)));
    }


}

