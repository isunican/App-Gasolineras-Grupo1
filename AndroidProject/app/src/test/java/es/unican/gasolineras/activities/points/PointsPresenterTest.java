package es.unican.gasolineras.activities.points;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import android.database.sqlite.SQLiteException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.sql.Date;
import java.util.ArrayList;

import es.unican.gasolineras.common.database.IInterestPointsDAO;
import es.unican.gasolineras.model.InterestPoint;
import es.unican.gasolineras.roomDAO.InterestPointsDAO;

@RunWith(RobolectricTestRunner.class)
public class PointsPresenterTest {

    private IPointsContract.Presenter sut;
    @Mock
    private IPointsContract.View view;
    @Mock
    private InterestPointsDAO pointsDAO;
    @Mock
    private IInterestPointsDAO iPointsDAO;

    private ArrayList<InterestPoint> listaConPuntos;
    private ArrayList<InterestPoint> listaVacia;
    private InterestPoint point1;
    private InterestPoint point2;
    private InterestPoint point3;
    @Before
    public void setUp() {
        listaConPuntos = new ArrayList<>();
        listaVacia = new ArrayList<>();
        MockitoAnnotations.openMocks(this);

        point1 = new InterestPoint("punto 1", "#ff0000", 45.00000, -123.3450, 12.4);
        point1.setCreationDate(Date.valueOf("2024-08-12"));
        point2 = new InterestPoint("punto 2", "#00ff00", 65.04000, 23.3770, 6.0);
        point2.setCreationDate(Date.valueOf("2024-07-28"));
        point3 = new InterestPoint("punto 3", "#0000ff", -25.6783, 3.3422 , 53.2);
        point3.setCreationDate(Date.valueOf("2024-10-01"));
        listaConPuntos.add(point1);
        listaConPuntos.add(point2);
        listaConPuntos.add(point3);

        sut = new PointsPresenter();


    }


    @Test
    public void initWithPointsTest() {
        //Comprobamos la lista con los puntos
        when(view.getPointsDao()).thenReturn(pointsDAO);
        when(pointsDAO.getMyInterestPointsDAO()).thenReturn(iPointsDAO);
        when(iPointsDAO.getInterestPoints()).thenReturn(listaConPuntos);

        //Realizamos la inicialización
        sut.init(view);

        // Verifica que se llama a view.init()
        verify(view).init();

        // Verifica que se llama a showPoints()
        verify(view).showPoints(listaConPuntos);
        //Compruebo que el orden de los puntos es el correcto.
        assertEquals(listaConPuntos.get(0), point2);
        assertEquals(listaConPuntos.get(1), point1);
        assertEquals(listaConPuntos.get(2), point3);



    }

    @Test
    public void initWithOutPointsTest() {
        //Comprobamos la lista con los puntos
        when(view.getPointsDao()).thenReturn(pointsDAO);
        when(pointsDAO.getMyInterestPointsDAO()).thenReturn(iPointsDAO);
        when(iPointsDAO.getInterestPoints()).thenReturn(listaVacia);

        //Realizamos la inicialización
        sut.init(view);

        // Verifica que se llama a view.init()
        verify(view).init();

        // Verifica que se llama a showPoints()
        verify(view).showPoints(listaVacia);



    }

    @Test
    public void initWithOutConexionBDPointsTest() {
        //Comprobamos la lista con los puntos
        when(view.getPointsDao()).thenReturn(pointsDAO);
        when(pointsDAO.getMyInterestPointsDAO()).thenReturn(iPointsDAO);
        when(iPointsDAO.getInterestPoints()).thenThrow(new SQLiteException());

        //Realizamos la inicialización
        sut.init(view);

        // Verifica que se llama a view.init()
        verify(view).init();

        // Verifica que se llama a showLoadError()
        verify(view).showLoadError();




    }

}


