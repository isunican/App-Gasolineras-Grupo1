package es.unican.gasolineras.activities.main;

import static es.unican.gasolineras.utils.MockRepositories.getTestRepository;

import android.content.Context;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import es.unican.gasolineras.R;
import es.unican.gasolineras.injection.RepositoriesModule;
import es.unican.gasolineras.repository.IGasolinerasRepository;

@UninstallModules(RepositoriesModule.class)
@HiltAndroidTest
public class DetailsUITest {

    @Rule(order = 0)  // the Hilt rule must execute first
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    // I need the context to access resources, such as the json with test gas stations
    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    // Mock repository that provides data from a JSON file instead of downloading it from the internet.
    @BindValue
    final IGasolinerasRepository repository = getTestRepository(context, R.raw.gasolineras_ccaa_06);

    private void checkValues(int idElemnt, String value) {
        Espresso.onView(ViewMatchers.withId(idElemnt))
                .check(ViewAssertions.matches(ViewMatchers.withText(value)));
    }

    @Test
    public void checkDetailsViewNormal() {
        DataInteraction elementoLista = Espresso
                .onData(CoreMatchers.anything())
                .inAdapterView(ViewMatchers.withId(R.id.lvStations))
                .atPosition(0);
        // Entramos en la gasolinera
        elementoLista.perform(ViewActions.click());
        // Comprobamos los campos
        checkValues(R.id.tvRotulo, "CEPSA");
        checkValues(R.id.tvGasolina95E5, "1.68");
        checkValues(R.id.tvGasoleoA, "1.51");
        checkValues(R.id.tvSumario, "1.62");
        checkValues(R.id.tvMunicipio, "Alfoz de Lloredo");
        checkValues(R.id.tvDireccion, "CARRETERA 6316 KM. 10,5");
        checkValues(R.id.tvCodigoPostal, "39526");
        checkValues(R.id.tvHorario, "L-D: 08:00-21:00");
    }

}
