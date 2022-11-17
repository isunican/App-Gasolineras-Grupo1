package es.unican.is.appgasolineras.activities.menuPrincipal;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.CoreMatchers.anything;
import static es.unican.is.appgasolineras.utils.Matchers.sizeElements;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import es.unican.is.appgasolineras.R;
import es.unican.is.appgasolineras.repository.rest.GasolinerasServiceConstants;

public class FiltroMarcaUITest {

    @BeforeClass
    public static void setUp() {
        GasolinerasServiceConstants.setStaticURL2();
    }

    @AfterClass
    public static void clean() {
        GasolinerasServiceConstants.setMinecoURL();
    }

    @Rule
    public ActivityScenarioRule<MenuPrincipalView> activityRule =
            new ActivityScenarioRule(MenuPrincipalView.class);

    @Test
    public void reseteaFiltroMarcaTest() {
        onView(withId(R.id.btnAccederLista)).perform(scrollTo(), click());
        onView(withId(R.id.btnFiltroPrecio)).perform(click());

        onView(withId(R.id.spinner_marca)).perform(scrollTo(), click());
        onData(anything()).atPosition(1).perform(scrollTo(), click());
        onView(withId(R.id.spinner_marca)).check(matches(withSpinnerText("AVIA")));


        onView(withId(R.id.btnResetear)).perform(scrollTo(), click());
        onView(withId(R.id.spinner_marca)).check(matches(withSpinnerText("Todas")));
    }

    @Test
    public void modificaFiltroMarcaTest() {
        onView(withId(R.id.btnAccederLista)).perform(scrollTo(), click());
        onView(withId(R.id.btnFiltroPrecio)).perform(click());


        //comprobar todas las marcas
        onView(withId(R.id.spinner_marca)).perform(scrollTo(), click());
        onData(anything()).atPosition(0).perform(scrollTo(), click());
        onView(withId(R.id.spinner_marca)).check(matches(withSpinnerText("Todas")));
        onView(withId(R.id.btnMostrarResultados)).perform(scrollTo(), click());
        onView(withId(R.id.lvGasolineras)).check(matches(sizeElements(156)));
        onView(withId(R.id.btnFiltroPrecio)).perform(click());

        //comprobar marca AVIA
        onView(withId(R.id.spinner_marca)).perform(scrollTo(), click());
        onData(anything()).atPosition(1).perform(scrollTo(), click());
        onView(withId(R.id.spinner_marca)).check(matches(withSpinnerText("AVIA")));
        onView(withId(R.id.btnMostrarResultados)).perform(scrollTo(), click());
        onView(withId(R.id.lvGasolineras)).check(matches(sizeElements(17)));
        onView(withId(R.id.btnFiltroPrecio)).perform(click());

        //comprobar marca CAMPSA
        onView(withId(R.id.spinner_marca)).perform(scrollTo(), click());
        onData(anything()).atPosition(2).perform(scrollTo(), click());
        onView(withId(R.id.spinner_marca)).check(matches(withSpinnerText("CAMPSA")));
        onView(withId(R.id.btnMostrarResultados)).perform(scrollTo(), click());
        onView(withId(R.id.lvGasolineras)).check(matches(sizeElements(3)));
        onView(withId(R.id.btnFiltroPrecio)).perform(click());

        //comprobar marca CARREFOUR
        onView(withId(R.id.spinner_marca)).perform(scrollTo(), click());
        onData(anything()).atPosition(3).perform(scrollTo(), click());
        onView(withId(R.id.spinner_marca)).check(matches(withSpinnerText("CARREFOUR")));
        onView(withId(R.id.btnMostrarResultados)).perform(scrollTo(), click());
        onView(withId(R.id.lvGasolineras)).check(matches(sizeElements(4)));
        onView(withId(R.id.btnFiltroPrecio)).perform(click());

        //comprobar marca CEPSA
        onView(withId(R.id.spinner_marca)).perform(scrollTo(), click());
        onData(anything()).atPosition(4).perform(scrollTo(), click());
        onView(withId(R.id.spinner_marca)).check(matches(withSpinnerText("CEPSA")));
        onView(withId(R.id.btnMostrarResultados)).perform(scrollTo(), click());
        onView(withId(R.id.lvGasolineras)).check(matches(sizeElements(20)));
        onView(withId(R.id.btnFiltroPrecio)).perform(click());

        //comprobar marca GALP
        onView(withId(R.id.spinner_marca)).perform(scrollTo(), click());
        onData(anything()).atPosition(5).perform(scrollTo(), click());
        onView(withId(R.id.spinner_marca)).check(matches(withSpinnerText("GALP")));
        onView(withId(R.id.btnMostrarResultados)).perform(scrollTo(), click());
        onView(withId(R.id.lvGasolineras)).check(matches(sizeElements(6)));
        onView(withId(R.id.btnFiltroPrecio)).perform(click());

        //comprobar marca PETRONOR
        onView(withId(R.id.spinner_marca)).perform(scrollTo(), click());
        onData(anything()).atPosition(6).perform(scrollTo(), click());
        onView(withId(R.id.spinner_marca)).check(matches(withSpinnerText("PETRONOR")));
        onView(withId(R.id.btnMostrarResultados)).perform(scrollTo(), click());
        onView(withId(R.id.lvGasolineras)).check(matches(sizeElements(5)));
        onView(withId(R.id.btnFiltroPrecio)).perform(click());

        //comprobar marca REPSOL
        onView(withId(R.id.spinner_marca)).perform(scrollTo(), click());
        onData(anything()).atPosition(7).perform(scrollTo(), click());
        onView(withId(R.id.spinner_marca)).check(matches(withSpinnerText("REPSOL")));
        onView(withId(R.id.btnMostrarResultados)).perform(scrollTo(), click());
        onView(withId(R.id.lvGasolineras)).check(matches(sizeElements(38)));
        onView(withId(R.id.btnFiltroPrecio)).perform(click());

        //comprobar marca SHELL
        onView(withId(R.id.spinner_marca)).perform(scrollTo(), click());
        onData(anything()).atPosition(8).perform(scrollTo(), click());
        onView(withId(R.id.spinner_marca)).check(matches(withSpinnerText("SHELL")));
        onView(withId(R.id.btnMostrarResultados)).perform(scrollTo(), click());
        onView(withId(R.id.lvGasolineras)).check(matches(sizeElements(8)));
        onView(withId(R.id.btnFiltroPrecio)).perform(click());
    }

}
