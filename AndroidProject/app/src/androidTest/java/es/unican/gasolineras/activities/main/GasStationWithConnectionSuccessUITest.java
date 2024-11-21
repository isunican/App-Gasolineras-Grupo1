package es.unican.gasolineras.activities.main;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static es.unican.gasolineras.Utils.MockRepositories.getTestRepository;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.List;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import es.unican.gasolineras.R;
import es.unican.gasolineras.common.Utils;
import es.unican.gasolineras.common.database.IGasStationsDAO;
import es.unican.gasolineras.common.database.MyFuelDatabase;
import es.unican.gasolineras.injection.RepositoriesModule;
import es.unican.gasolineras.model.Gasolinera;
import es.unican.gasolineras.repository.IGasolinerasRepository;
import es.unican.gasolineras.utils.SeekBarActions;

@UninstallModules(RepositoriesModule.class)
@HiltAndroidTest
public class GasStationWithConnectionSuccessUITest {

    private View decorView;
    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    static final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @BeforeClass
    public static void beforeStartAndoid() {
        IGasStationsDAO gasStationsDAO = MyFuelDatabase.getInstance(context).getGasStationsDAO();
        // Rellenar la dao de offline con los datos de lista2
        List<Gasolinera> list2 = Utils.parseGasolineras(context, R.raw.gasolineras_us509051_lista2);
        gasStationsDAO.deleteAll();
        for (Gasolinera g : list2) {
            gasStationsDAO.addGasStation(g);
        }
    }

    @BindValue
    final IGasolinerasRepository repository = getTestRepository(context, R.raw.gasolineras_us509051_lista1);

    private MainPresenter mainPresenter;
    IGasStationsDAO gasStationsDAO;

    @Before
    public void setUp() {
        activityRule.getScenario().onActivity(activity -> {
            decorView = activity.getWindow().getDecorView();
            mainPresenter = activity.getMainPresenter(); // Aquí accedes al MainPresenter de la actividad
            SeekBarActions.setMainPresenter(mainPresenter);  // Pasas la instancia al SeekBarActions
        });
        gasStationsDAO = MyFuelDatabase.getInstance(context).getGasStationsDAO();
    }

    @Test
    public void gasStationWithConnectionSuccessUITest() {
        List<Gasolinera> list1 = Utils.parseGasolineras(context, R.raw.gasolineras_us509051_lista1);
        List<Gasolinera> daoContent = gasStationsDAO.getAll();

        // Comprobar la DAO
        assertEquals(list1.size(), daoContent.size());
        for (int i = 0; i < list1.size(); i++) {
            assertEquals(list1.get(i).getId(), daoContent.get(i).getId());
        }

        // Comprobar la vista
        for (int i = 0; i < list1.size(); i++) {
            DataInteraction elto =
                    Espresso.onData(CoreMatchers.anything())
                            .inAdapterView(withId(R.id.lvStations))
                            .atPosition(i);
            elto.onChildView(withId(R.id.tvName)).check(matches(withText(list1.get(i).getRotulo())));
        }

    }

}
