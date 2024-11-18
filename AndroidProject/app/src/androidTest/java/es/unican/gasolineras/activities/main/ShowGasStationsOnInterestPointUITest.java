package es.unican.gasolineras.activities.main;

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
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import es.unican.gasolineras.R;
import es.unican.gasolineras.injection.RepositoriesModule;
import es.unican.gasolineras.model.InterestPoint;
import es.unican.gasolineras.repository.IGasolinerasRepository;
import es.unican.gasolineras.roomDAO.InterestPointsDAO;

@UninstallModules(RepositoriesModule.class)
@HiltAndroidTest
public class ShowGasStationsOnInterestPointUITest {

    private View decorView;

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    // Mock repository with JSON data (is required, but we didn't use it in this case)
    @BindValue
    final IGasolinerasRepository repository = getTestRepository(context, R.raw.gasolineras_ccaa_06);

    // DAO instance for storing interest points
    private InterestPointsDAO interestPointsDAO;

    @Before
    public void setUp() {
        // Initialize decorView and DAO
        activityRule.getScenario().onActivity(activity -> decorView = activity.getWindow().getDecorView());
        interestPointsDAO = InterestPointsDAO.getInstance(context);
        interestPointsDAO.getMyInterestPointsDAO().deleteAll();
    }

    @Test
    public void showGasStationsOnInterestPointsUITest() {
        // Define expected interest points
        List<InterestPoint> expectedPoints = new ArrayList<>();

        InterestPoint point1 = new InterestPoint("Zona 1", "#0000FF", 43.3192, -4.2987, 2);
        point1.setCreationDate(Date.valueOf("2024-07-10"));
        expectedPoints.add(point1);

        InterestPoint point2 = new InterestPoint("Zona 2", "#00FF00", 42.8881, -4.0025, 0.5);
        point2.setCreationDate(Date.valueOf("2024-08-12"));
        expectedPoints.add(point2);

        InterestPoint point3 = new InterestPoint("Zona 3", "#808080", 43.3846, -3.7385, 4);
        point3.setCreationDate(Date.valueOf("2024-10-01"));
        expectedPoints.add(point3);

        // Insert expected points into the DAO
        for (InterestPoint point : expectedPoints) {
            interestPointsDAO.getMyInterestPointsDAO().addInterestPoint(point);
        }

        // Select the interest point option
        Espresso.onView(withId(R.id.menuPointButton))
                .perform(ViewActions.click());

        DataInteraction elementoLista = Espresso
                .onData(CoreMatchers.anything())
                .inAdapterView(ViewMatchers.withId(R.id.lvPoints))
                .atPosition(0);
        // Entramos el punto
        elementoLista.perform(ViewActions.click());

        //Entras en el punto de interés
        elementoLista = Espresso
                .onData(CoreMatchers.anything())
                .inAdapterView(ViewMatchers.withId(R.id.lvStations))
                .atPosition(0);

        //Compruebas que haya 1 gasolineras
        Espresso.onView(withId(R.id.lvStations))
                .check(matches(withListSize(1)));


        //Comprobar que la gasolinera es la correcta por el rótulo
        elementoLista.onChildView(ViewMatchers.withId(R.id.tvAddress))
                .check(matches(withText("CN-634 km 255,7")));


        //Comprobar valores de la barra de información del punto de interés

        //Nombre
        Espresso.onView(withId(R.id.ip_name_tv))
                        .check(matches(withText(("Zona 1"))));

        //Radio
        Espresso.onView(withId(R.id.ip_radius_tv))
                .check(matches(withText(("Radio: 2,0 km"))));

        //Encontradas
        Espresso.onView(withId(R.id.ip_loaded_tv))
                .check(matches(withText(("Encontradas: 1"))));


    }

}