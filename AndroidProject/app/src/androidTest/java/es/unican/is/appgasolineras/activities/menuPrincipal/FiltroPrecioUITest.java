package es.unican.is.appgasolineras.activities.menuPrincipal;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static es.unican.is.appgasolineras.utils.Matchers.sizeElements;


import androidx.test.espresso.DataInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import es.unican.is.appgasolineras.R;
import es.unican.is.appgasolineras.repository.rest.GasolinerasServiceConstants;

public class FiltroPrecioUITest {
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
    public void reseteaFiltroPrecioTest() {
        onView(withId(R.id.btnAccederLista)).perform(scrollTo(), click());
        // Commpruebo numero de elementos en la lista inicial
        onView(withId(R.id.lvGasolineras)).check(matches(sizeElements(156)));
        onView(withId(R.id.btnFiltroPrecio)).perform(click());
        for (int i = 0; i < 10; i++) {
            onView(withId(R.id.btnBajarPrecio)).perform(scrollTo(), click());
        }
        onView(withId(R.id.btnResetear)).perform(scrollTo(), click());
        onView(withId(R.id.etPrecioLimite)).check(matches(withText("2.03")));
        // Compruebo que el filtro se ha reseteado, no se ha aplicado y el numero de elementos es el mismo
        onView(withId(R.id.btnMostrarResultados)).perform(scrollTo(), click());
        onView(withId(R.id.lvGasolineras)).check(matches(sizeElements(156)));
    }

    @Test
    public void modificaFiltroPrecioTest() {
        onView(withId(R.id.btnAccederLista)).perform(scrollTo(), click());
        // Commpruebo numero de elementos en la lista inicial
        onView(withId(R.id.lvGasolineras)).check(matches(sizeElements(156)));
        onView(withId(R.id.btnFiltroPrecio)).perform(click());

        //primero intento subir el precio para comprobar que no sobrepasa el limite superior
        for (int i = 0; i < 3; i++) {
            onView(withId(R.id.btnSubirPrecio)).perform(scrollTo(), click());
        }
        onView(withId(R.id.etPrecioLimite)).check(matches(withText("2.03")));

        //resto 10 decimales al precio
        for (int i = 0; i < 10; i++) {
            onView(withId(R.id.btnBajarPrecio)).perform(scrollTo(), click());
        }
        onView(withId(R.id.etPrecioLimite)).check(matches(withText("1.93")));

        //le sumo 3 decimales al precio
        for (int i = 0; i < 3; i++) {
            onView(withId(R.id.btnSubirPrecio)).perform(scrollTo(), click());
        }
        onView(withId(R.id.etPrecioLimite)).check(matches(withText("1.96")));
        // Compruebo que el numero de elementos al filtrar se ha reducido
        onView(withId(R.id.btnMostrarResultados)).perform(scrollTo(), click());
        onView(withId(R.id.lvGasolineras)).check(matches(sizeElements(154)));
        // Compruebo precios gasolina y diesel de la primera y ultima gasolinera viendo que aunque
        // sea mas alto el diesel, como la gasolina es mas barata no se eliminan
        DataInteraction gasEnLista;
        String precioDie1 = "1,999";
        String precioGas1 = "1,859";
        String precioDie2 = "1,879";
        String precioGas2 = "1,799";
        gasEnLista = onData(CoreMatchers.anything()).inAdapterView(withId(R.id.lvGasolineras)).atPosition(0);
        gasEnLista.onChildView(withId(R.id.tvDieselA)).check(matches(withText(precioDie1)));
        gasEnLista.onChildView(withId(R.id.tv95)).check(matches(withText(precioGas1)));
        gasEnLista = onData(CoreMatchers.anything()).inAdapterView(withId(R.id.lvGasolineras)).atPosition(153);
        gasEnLista.onChildView(withId(R.id.tvDieselA)).check(matches(withText(precioDie2)));
        gasEnLista.onChildView(withId(R.id.tv95)).check(matches(withText(precioGas2)));
        onView(withId(R.id.btnFiltroPrecio)).perform(click());

        // Bajo hasta 0 y compruebo que no se pasa
        for (int i = 0; i < 210; i++) {

            onView(withId(R.id.btnBajarPrecio)).perform(scrollTo(), click());
        }
        onView(withId(R.id.etPrecioLimite)).check(matches(withText("0.00")));
        // Compruebo que no haya elementos al poner el filtro a 0 (demasiado restrictivo)
        onView(withId(R.id.btnMostrarResultados)).perform(scrollTo(), click());
        onView(withId(R.id.lvGasolineras)).check(matches(sizeElements(0)));
    }

    @Test
    public void muestraResultadosFiltroTest() {
        onView(withId(R.id.btnAccederLista)).perform(scrollTo(), click());
        onView(withId(R.id.btnFiltroPrecio)).perform(click());
        // Compruebo que al no modificar el filtro, sigue igual el numero de elementos
        onView(withId(R.id.btnMostrarResultados)).perform(scrollTo(), click());
        onView(withId(R.id.lvGasolineras)).check(matches(sizeElements(156)));
    }

}
