package es.unican.gasolineras;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static org.junit.Assert.assertTrue;
import static es.unican.gasolineras.utils.MockRepositories.getTestRepository;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

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
    final IGasolinerasRepository repository = getTestRepository(context, R.raw.gasolineras_for_tests);

    @Before
    public void setUp() {
        activityRule.getScenario().onActivity(activity -> testDecorView = activity.getWindow().getDecorView());
    }

    // Pasos a realizar en el test de exito:

    // 1. Se selecciona la opcion de ordenar en el toolbar de la aplicacion.
    // 2. Aparece en un popup un menu de ordenación.
    // 3. Se selecciona el tipo de combustible a ordenar (spinner = GASOLINA 95 E5)
    // 4. Se selecciona el tipo de ordenacion a realizar. (spinner = Ascendente)
    // 5. Se cierra el popup
    // 6. Se comprueba que la lista se ordena correctamente.
    // (for de todos los elementos comprobando que los precios sean menores o iguales, el primero del siguiente y así)
    @Test
    public void OrderByPriceAscSuccessTest() {
        onView(withId(R.id.toolbar)).perform(click());
        // Click on menuOrderButton
        onView(withId(R.id.menuOrderButton)).perform(click());
        // Verifying that the popup is displayed
        onView(withId(R.id.popup_orderText)).check(matches(isDisplayed()));
        onView(withId(R.id.typeOrderSpinner)).perform(click());
        onData(allOf(is(instanceOf(FuelTypeEnum.class)), equalTo(FuelTypeEnum.GASOLINA_95E5))) // Ajusta según tu enumerado
                .inRoot(isPlatformPopup())
                .perform(click());
        // Selecting in the price method spinner the price type
        onView(withId(R.id.orderPriceMethodSpinner)).perform(click());
        onData(allOf(is(instanceOf(OrderMethodsEnum.class)),
                equalTo(OrderMethodsEnum.Ascending))) // Asegúrate de que esto es correcto
                .inRoot(isPlatformPopup())
                .perform(click());

        // Accept the popup
        onView(withId(R.id.order_accept_button)).perform(click());

        // Checking if the ListView with the gas stations is displayed correctly
        onView(withId(R.id.lvStations)).check(matches(isDisplayed()));

        // Obtener el tipo de combustible seleccionado

        // Variable to store the previous price
        final double[] previousPrice = {Double.MIN_VALUE};

        // In order to check if the prices are in ascending order, we iterate through the ListView
        //TODO: Checking the number of stations with the modified JSON
        int numberOfStationsJson = 4;
        for (int i = 0; i < numberOfStationsJson; i++) {

            // onData is for iterating through the ListView
            onData(anything())
                    .inAdapterView(withId(R.id.lvStations)) // Obtaining the listview
                    .atPosition(i)   // Selecting the element at the current position
                    .check(matches(isDisplayed())) // Checking if the view is displayed
                    .check((view, noViewFoundException) -> { // We need the view to get the price

                        // If the view is displayed we obtain the textview with the price.
                        TextView priceTextView;
                        priceTextView = view.findViewById(R.id.tv95);

                        // Obtaining the price and converting it to double.
                        String priceString = priceTextView.getText().toString().replace(",", ".");
                        double currentPrice = Double.parseDouble(priceString);

                        // Checking if the previous price is less than the current price
                        assertTrue(previousPrice[0] <= currentPrice );
                        Log.d( "OrderByPriceAscSuccessTest", "Previous price: " + previousPrice[0] + " Current price: " + currentPrice);
                        // Update the previous price for checking the next iteration
                        previousPrice[0] = currentPrice;
                    });
        }
    }



}

