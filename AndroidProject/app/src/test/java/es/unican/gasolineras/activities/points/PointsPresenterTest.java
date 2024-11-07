package es.unican.gasolineras.activities.points;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

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

}



package es.unican.gasolineras.activities.points;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.never;

import com.google.common.base.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import es.unican.gasolineras.common.database.IInterestPointsDAO;
import es.unican.gasolineras.common.exceptions.LatitudInvalidaException;
import es.unican.gasolineras.common.exceptions.LongitudInvalidaException;
import es.unican.gasolineras.common.exceptions.RadioInvalidoException;
import es.unican.gasolineras.model.InterestPoint;
import es.unican.gasolineras.roomDAO.InterestPointsDAO;

public class PointsPresenterTest {

    private PointsPresenter presenter;

    //Mocks de la vista y de la DAO.
    @Mock
    private IPointsContract.View mockView;

    @Mock
    private InterestPointsDAO mockDAO;

    @Mock
    private IInterestPointsDAO IMockDAO;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configuramos mockView para que devuelva mockDAO cuando se llame a getPointsDao()
        Mockito.when(mockView.getPointsDao()).thenReturn(mockDAO);
        Mockito.when(mockDAO.getMyInterestPointsDAO()).thenReturn(IMockDAO);
        // Configuramos mockDAO para devolver un mock del método interno si es necesario
      
        presenter = new PointsPresenter();
        presenter.init(mockView);
    }

    // Test de exito
    @Test
    public void testOnAcceptNewPointOfInterestClicked_withValidPoint() {
        // TOASK: Preguntar a Patri
        InterestPoint validPoint = new InterestPoint("Punto", "AZUL", 40.0637, -82.3467, 20);

        // Creamos un mock para la lista de InterestPoints
        List<InterestPoint> interestPointsList = new ArrayList<>();

        // Simulamos que getInterestPoints() devuelve una lista vacía inicialmente
        Mockito.when(mockDAO.getMyInterestPointsDAO().getInterestPoints()).thenReturn(interestPointsList);

        // Act
        presenter.onAcceptNewPointOfInterestClicked(validPoint);

        // Usamos ArgumentCaptor para capturar el argumento pasado a addInterestPoint
        ArgumentCaptor<InterestPoint> captor = ArgumentCaptor.forClass(InterestPoint.class);
        Mockito.verify(mockDAO.getMyInterestPointsDAO()).addInterestPoint(captor.capture());

        // Añadimos el punto manualmente a la lista para simular que se agrega al DAO
        interestPointsList.add(validPoint);

        // Assert
        // Verifica que el punto capturado sea el correcto
        assertEquals(validPoint, captor.getValue());

        // Verifica que el tamaño de la lista de puntos de interés haya aumentado a 1
        assertEquals(1, mockDAO.getMyInterestPointsDAO().getInterestPoints().size());
    }

    // Test de fracaso por la latitud  >90
    @Test(expected = LatitudInvalidaException.class)
    public void testOnAcceptNewPointOfInterestClicked_withInvalidLatitudeAbove() {
        InterestPoint invalidPoint = new InterestPoint("Punto", "AZUL", 100, -82.3467, 20);
        presenter.onAcceptNewPointOfInterestClicked(invalidPoint);
        Mockito.verify(mockDAO.getMyInterestPointsDAO(), never()).addInterestPoint(invalidPoint);
        assertThrows(LatitudInvalidaException.class,
                () -> presenter.onAcceptNewPointOfInterestClicked(invalidPoint));
    }

    // Test de fracaso por la latitud inferior <(-90)
    @Test(expected = LatitudInvalidaException.class)
    public void testOnAcceptNewPointOfInterestClicked_withInvalidLatitudeBelow() {
        InterestPoint invalidPoint = new InterestPoint("Punto", "AZUL", -96, -82.3467, 20);
        presenter.onAcceptNewPointOfInterestClicked(invalidPoint);
        Mockito.verify(mockDAO.getMyInterestPointsDAO(), never()).addInterestPoint(invalidPoint);
        assertThrows(LatitudInvalidaException.class,
                () -> presenter.onAcceptNewPointOfInterestClicked(invalidPoint));
    }

    // Test de fracaso por latitud == null, no se prueba al ser double primitivo
    //@Test(expected = LatitudInvalidaException.class)
    //public void testOnAcceptNewPointOfInterestClicked_withNullLatitude() {
    //    InterestPoint invalidPoint = new InterestPoint("Punto", "AZUL", null, 40.0637, 20);
    //    presenter.onAcceptNewPointOfInterestClicked(invalidPoint);
    //    Mockito.verify(mockDAO.getMyInterestPointsDAO(), never()).addInterestPoint(invalidPoint);
    //    assertThrows(LatitudInvalidaException.class,
    //            () -> presenter.onAcceptNewPointOfInterestClicked(invalidPoint));
    //}

    // Test de fracaso por la longitud  >180
    @Test(expected = LongitudInvalidaException.class)
    public void testOnAcceptNewPointOfInterestClicked_withInvalidLongitudeAbove() {
        InterestPoint invalidPoint = new InterestPoint("Punto", "AZUL", 40.0637, 300, 20);
        presenter.onAcceptNewPointOfInterestClicked(invalidPoint);
        Mockito.verify(mockDAO.getMyInterestPointsDAO(), never()).addInterestPoint(invalidPoint);
        assertThrows(LongitudInvalidaException.class,
                () -> presenter.onAcceptNewPointOfInterestClicked(invalidPoint));
    }

    // Test de fracaso por la longitud  <(-180)
    @Test(expected = LongitudInvalidaException.class)
    public void testOnAcceptNewPointOfInterestClicked_withInvalidLongitudeBelow() {
        InterestPoint invalidPoint = new InterestPoint("Punto", "AZUL", 40.0637, -234, 20);
        presenter.onAcceptNewPointOfInterestClicked(invalidPoint);
        Mockito.verify(mockDAO.getMyInterestPointsDAO(), never()).addInterestPoint(invalidPoint);
        assertThrows(LongitudInvalidaException.class,
                () -> presenter.onAcceptNewPointOfInterestClicked(invalidPoint));
    }

    // Test de fracaso, por longitud == null, al ser un double primitivo no se prueba.
    //@Test(expected = LongitudInvalidaException.class)
    //public void testOnAcceptNewPointOfInterestClicked_withNullLongitude() {
    //    InterestPoint invalidPoint = new InterestPoint("Punto", "AZUL", 40.0637, null, 20);
    //    presenter.onAcceptNewPointOfInterestClicked(invalidPoint);
    //    Mockito.verify(mockDAO.getMyInterestPointsDAO(), never()).addInterestPoint(invalidPoint);
    //    assertThrows(LongitudInvalidaException.class,
    //            () -> presenter.onAcceptNewPointOfInterestClicked(invalidPoint));
    //}

    // Test de fracaso radio igual que cero.
    @Test(expected = RadioInvalidoException.class)
    public void testOnAcceptNewPointOfInterestClicked_withZeroRadius() {
        InterestPoint invalidPoint = new InterestPoint("Punto", "AZUL", 40.0637, -82.3467, 0);
        presenter.onAcceptNewPointOfInterestClicked(invalidPoint);
        Mockito.verify(mockDAO.getMyInterestPointsDAO(), never()).addInterestPoint(invalidPoint);
        assertThrows(RadioInvalidoException.class,
                () -> presenter.onAcceptNewPointOfInterestClicked(invalidPoint));
    }

    // Test de fracaso radio negativo.
    @Test(expected = RadioInvalidoException.class)
    public void testOnAcceptNewPointOfInterestClicked_withNegativeRadius() {
        InterestPoint invalidPoint = new InterestPoint("Punto", "AZUL", 40.0637, -82.3467, -10);
        presenter.onAcceptNewPointOfInterestClicked(invalidPoint);
        Mockito.verify(mockDAO.getMyInterestPointsDAO(), never()).addInterestPoint(invalidPoint);
        assertThrows(RadioInvalidoException.class,
                () -> presenter.onAcceptNewPointOfInterestClicked(invalidPoint));
    }

    // Test de fracaso por radio == null, al ser un double primitivo estos no se prueban.
    // @Test (expected = RadioInvalidoException.class)
   // public void testOnAcceptNewPointOfInterestClicked_withNullRadius() {
    //  InterestPoint invalidPoint = new InterestPoint("Punto", "AZUL", 82.3467, 40.0637, null);
    //  presenter.onAcceptNewPointOfInterestClicked(invalidPoint);
    //  Mockito.verify(mockDAO.getMyInterestPointsDAO(), never()).addInterestPoint(invalidPoint);
    //  assertThrows(RadioInvalidoException.class,
    //  () -> presenter.onAcceptNewPointOfInterestClicked(invalidPoint));
    // }
}


