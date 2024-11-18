package es.unican.gasolineras;


import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
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

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import es.unican.gasolineras.activities.main.MainView;
import es.unican.gasolineras.activities.main.Selection;
import es.unican.gasolineras.common.BrandsEnum;
import es.unican.gasolineras.injection.RepositoriesModule;
import es.unican.gasolineras.repository.IGasolinerasRepository;

@UninstallModules(RepositoriesModule.class)
@HiltAndroidTest
public class BrandsFilterUITest {

    private View decorView;
    @Rule(order = 0)  // the Hilt rule must execute first
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    // I need the context to access resources, such as the json with test gas stations
    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    // Mock repository that provides data from a JSON file instead of downloading it from the internet.
    @BindValue
    final IGasolinerasRepository repository = getTestRepository(context, R.raw.gasolineras_ccaa_06);

    private void checkValues(int idElement, String value) {
        Espresso.onView(withId(idElement))
                .check(matches(withText(value)));
    }

    @Before
    public void setUp() {
        activityRule.getScenario().onActivity(activity -> decorView = activity.getWindow().getDecorView());
    }

    @Test
    public void checkFilterByBrandRepsol() {

        Espresso.onView(withId(R.id.menuFilterButton))
                .perform(ViewActions.click());

        List<Selection> testFilterSeleciton = new ArrayList<>();
        testFilterSeleciton.add(new Selection(BrandsEnum.REPSOL.toString(), true));

        Espresso.onView(withId(R.id.brandSpinner))
                .perform(ViewActions.click());

        Espresso.onView(withText(BrandsEnum.REPSOL.toString()))
                .perform(ViewActions.click());

        Espresso.onView(withText("OK"))
                .perform(ViewActions.click());

        Espresso.onView(withId(R.id.filters_accept_button))
                .perform(ViewActions.click());

        //Espresso.onView(withText("Cargadas 45 gasolineras")).inRoot(RootMatchers.withDecorView(not(decorView))).check(matches(isDisplayed()));

        Espresso.onView(withId(R.id.lvStations))
                .check(matches(withListSize(45)));


        for (int i = 0; i < 44; i++) {
            DataInteraction elementoLista = Espresso
                    .onData(CoreMatchers.anything())
                    .inAdapterView(withId(R.id.lvStations))
                    .atPosition(i);
            // Entramos en la gasolinera
            elementoLista.perform(ViewActions.click());
            // Comprobamos los campos
            checkValues(R.id.tvRotulo, "REPSOL");
            Espresso.pressBack();
        }

        DataInteraction elementoLista = Espresso
                .onData(CoreMatchers.anything())
                .inAdapterView(withId(R.id.lvStations))
                .atPosition(44);
        // Entramos en la gasolinera
        elementoLista.perform(ViewActions.click());
        // Comprobamos los campos
        checkValues(R.id.tvRotulo, "REPSOL ");
        Espresso.pressBack();
    }

    @Test
    public void checkFilterByBrandRepsolBallenoil() {

        Espresso.onView(withId(R.id.menuFilterButton))
                .perform(ViewActions.click());

        List<Selection> testFilterSeleciton = new ArrayList<>();
        testFilterSeleciton.add(new Selection(BrandsEnum.REPSOL.toString(), true));

        Espresso.onView(withId(R.id.brandSpinner))
                .perform(ViewActions.click());

        Espresso.onView(withText(BrandsEnum.REPSOL.toString()))
                .perform(ViewActions.click());
        Espresso.onView(withText(BrandsEnum.BALLENOIL.toString()))
                .perform(ViewActions.click());


        Espresso.onView(withText("OK"))
                .perform(ViewActions.click());

        Espresso.onView(withId(R.id.filters_accept_button))
                .perform(ViewActions.click());

        //Espresso.onView(withText("Cargadas 51 gasolineras")).inRoot(RootMatchers.withDecorView(not(decorView))).check(matches(isDisplayed()));

        Espresso.onView(withId(R.id.lvStations))
                .check(matches(withListSize(51)));
    }
    @Test
    public void checkFilterByBrandTodos() {

        Espresso.onView(withId(R.id.menuFilterButton))
                .perform(ViewActions.click());

        List<Selection> testFilterSeleciton = new ArrayList<>();
        testFilterSeleciton.add(new Selection(BrandsEnum.REPSOL.toString(), true));

        Espresso.onView(withId(R.id.brandSpinner))
                .perform(ViewActions.click());

        Espresso.onView(withText("Todos"))
                .perform(ViewActions.click());

        Espresso.onView(withText("OK"))
                .perform(ViewActions.click());

        Espresso.onView(withId(R.id.filters_accept_button))
                .perform(ViewActions.click());

        //Espresso.onView(withText("Cargadas 164 gasolineras")).inRoot(RootMatchers.withDecorView(not(decorView))).check(matches(isDisplayed()));

        Espresso.onView(withId(R.id.lvStations))
                .check(matches(withListSize(164)));
    }

}