package es.unican.gasolineras.activities.main;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static es.unican.gasolineras.utils.Matchers.withListSize;
import static es.unican.gasolineras.Utils.MockRepositories.getTestRepository;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

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
import es.unican.gasolineras.R;
import es.unican.gasolineras.injection.RepositoriesModule;
import es.unican.gasolineras.repository.IGasolinerasRepository;
import es.unican.gasolineras.utils.SeekBarActions;

@UninstallModules(RepositoriesModule.class)
@HiltAndroidTest
public class MaxPriceFilterSuccessUITest {

    private View decorView;
    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @BindValue
    final IGasolinerasRepository repository = getTestRepository(context, R.raw.gasolineras_ccaa_06);

    private MainPresenter mainPresenter;
    @Before
    public void setUp() {
        activityRule.getScenario().onActivity(activity -> {
            decorView = activity.getWindow().getDecorView();
            mainPresenter = activity.getMainPresenter(); // Aquí accedes al MainPresenter de la actividad
            SeekBarActions.setMainPresenter(mainPresenter);  // Pasas la instancia al SeekBarActions
        });
    }

    private void checkSeekBarValue(int idElement, double value) {
        Espresso.onView(withId(idElement))
                .check((view, noViewFoundException) -> {
                    // Conversión directa del View a TextView
                    TextView tv = (TextView) view;
                    String text = tv.getText().toString();

                    try {
                        // Convertir el texto a valor numérico
                        double actualValue = Double.parseDouble(text);

                        // Verificar que el valor actual es menor que el esperado
                        if (actualValue > value) {
                            throw new AssertionError("Valor actual (" + actualValue + ") es superior o igual al valor esperado (" + value + ")");
                        }
                    } catch (NumberFormatException e) {
                        throw new AssertionError("El texto no es un número válido: " + text);
                    }
                });
    }


    @Test
    public void maxPriceFilterSuccessUITest() {

        //Select the filters option
        Espresso.onView(withId(R.id.menuFilterButton))
                .perform(ViewActions.click());

        //A value is set in the seek bar
        Espresso.onView(withId(R.id.MaxPriceSeekBar))
                .perform(SeekBarActions.setProgress(1.50));

        //The value established in the label is checked
        Espresso.onView(withId(R.id.lbSelectedMaxPrice))
                .check(matches(withText("1.50")));

        //Press the button that accepts the filters
        Espresso.onView(withId(R.id.filters_accept_button))
                .perform(ViewActions.click());


//        Espresso.onView(withText("Cargadas 10 gasolineras")).
//                inRoot(RootMatchers.withDecorView(not(decorView)))
//                .check(matches(isDisplayed()));

        //The number of filtered gas stations is verified
        Espresso.onView(withId(R.id.lvStations))
                .check(matches(withListSize(10)));

        //It is verified that each gas station meets the requirements
        for (int i = 0; i < 10; i++) {
            DataInteraction elto =
                    Espresso.onData(CoreMatchers.anything())
                            .inAdapterView(withId(R.id.lvStations))
                            .atPosition(i);

            elto.perform(ViewActions.click());

            checkSeekBarValue(R.id.tvGasoleoA, 1.50);

            checkSeekBarValue(R.id.tvGasolina95E5, 1.50);

            Espresso.pressBack();





        }



















    }

}