package es.unican.gasolineras;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static es.unican.gasolineras.utils.Matchers.withListSize;
import static es.unican.gasolineras.utils.MockRepositories.getTestRepository;

import android.content.Context;
import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
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
import es.unican.gasolineras.activities.main.MainView;
import es.unican.gasolineras.common.FuelTypeEnum;
import es.unican.gasolineras.injection.RepositoriesModule;
import es.unican.gasolineras.repository.IGasolinerasRepository;

@UninstallModules(RepositoriesModule.class)
@HiltAndroidTest
public class FuelTypesUITest {

    @Rule(order = 0)  // the Hilt rule must execute first
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    // I need the context to access resources, such as the json with test gas stations
    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    // Mock repository that provides data from a JSON file instead of downloading it from the internet.
    @BindValue
    final IGasolinerasRepository repository = getTestRepository(context, R.raw.gasolineras_filtro_tipo_test);

    private void checkValues(int idElemnt, String value) {
        Espresso.onView(ViewMatchers.withId(idElemnt))
                .check(ViewAssertions.matches(withText(value)));
    }

    View decorView;

    @Before
    public void prepareTest(){

        activityRule.getScenario().onActivity(activity -> decorView = activity.getWindow().getDecorView());
    }



    @Test
    public void checkFilterByGasolineAndDiesel() {
        // Abrir el menú de filtros
        Espresso.onView(withId(R.id.menuPointButton)).perform(ViewActions.click());

        // Seleccionar Gasolina
        Espresso.onView(withId(R.id.typeSpinner)).perform(ViewActions.click());
        Espresso.onView(withText(FuelTypeEnum.GASOLINA_95E5.toString())).perform(ViewActions.click());
        Espresso.onView(withText("OK")).perform(ViewActions.click());

        // Aplicar filtros
        Espresso.onView(withId(R.id.filters_accept_button)).perform(ViewActions.click());

        // Verificar que se muestran las gasolineras correctas
        Espresso.onView(withId(R.id.lvStations)).check(matches(withListSize(5)));


        //Verificar que el Toast muestra el mensaje correcto
        //Cargadas 5 gasolineras
        Espresso.onView(withText("Cargadas 5 gasolineras")).inRoot(RootMatchers.withDecorView(not(decorView))).check(matches(isDisplayed()));

    }



}


