package es.unican.gasolineras.activities.points;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.never;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.unican.gasolineras.common.database.IInterestPointsDAO;
import es.unican.gasolineras.common.exceptions.LatitudInvalidaException;
import es.unican.gasolineras.common.exceptions.LongitudInvalidaException;
import es.unican.gasolineras.common.exceptions.RadioInvalidoException;
import es.unican.gasolineras.model.InterestPoint;
import es.unican.gasolineras.roomDAO.InterestPointsDAO;

@RunWith(RobolectricTestRunner.class)
public class PointsPresenterTest {

    private IPointsContract.Presenter sut;

    //Mocks de la vista y de la DAO, para las pruebas de crear.
    @Mock
    private IPointsContract.View mockView;

    @Mock
    private InterestPointsDAO mockDAO;

    @Mock
    private IInterestPointsDAO IMockDAO;

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


        // Mocking ddbb and setting it up in the presenter
        when(mockView.getPointsDao()).thenReturn(mockDAO);
        when(mockDAO.getMyInterestPointsDAO()).thenReturn(IMockDAO);

        // En este caso es el sut.
        sut= new PointsPresenter();
        sut.init(mockView);

    }

    @Test
    public void initWithPointsTest() {
        //Comprobamos la lista con los puntos
        when(mockView.getPointsDao()).thenReturn(mockDAO);
        when(mockDAO.getMyInterestPointsDAO()).thenReturn(IMockDAO);
        when(IMockDAO.getInterestPoints()).thenReturn(listaConPuntos);

        // Llama al método init
        sut.init(mockView);

        // Verifica que se llama a view.init()
        verify(mockView, times(2)).init();

        // Verifica que se llama a showPoints()
        verify(mockView).showPoints(listaConPuntos);
        //Compruebo que el orden de los puntos es el correcto.
        assertEquals(listaConPuntos.get(0), point2);
        assertEquals(listaConPuntos.get(1), point1);
        assertEquals(listaConPuntos.get(2), point3);
    }

    @Test
    public void initWithOutPointsTest() {
        //Comprobamos la lista con los puntos

        when(mockView.getPointsDao()).thenReturn(mockDAO);
        when(mockDAO.getMyInterestPointsDAO()).thenReturn(IMockDAO);
        when(IMockDAO.getInterestPoints()).thenReturn(listaConPuntos);

        // Llama al método init
        sut.init(mockView);

        // Verifica que se llama a view.init()
        verify(mockView, times(2)).init();
        when(IMockDAO.getInterestPoints()).thenReturn(listaVacia);

        // Verifica que se llama a showPoints()
        verify(mockView).showPoints(listaVacia);

    }

    // Test de exito: no hay puntos en la BBDD
    @Test
    public void testOnAcceptNewPointOfInterestClicked_withValidPoint() {
        // Configuramos el punto válido
        InterestPoint validPoint = new InterestPoint("Punto", "#0000ff", 40.0637, -82.3467, 20);

        // Creamos un mock para la lista de InterestPoints
        List<InterestPoint> interestPointsList = new ArrayList<>();

        // Simulamos que getInterestPoints() devuelve la lista creada de puntos de interes.
        when(mockDAO.getMyInterestPointsDAO().getInterestPoints()).thenReturn(interestPointsList);

        //Comprobamos el tamanho
        assertEquals(0, mockDAO.getMyInterestPointsDAO().getInterestPoints().size());

        // Ejecutamos el metodo
        sut.onAcceptNewPointOfInterestClicked(validPoint);

        // Añadimos el punto manualmente a la lista para simular que se agrega al DAO
        interestPointsList.add(validPoint);

        // Usamos ArgumentCaptor para capturar la lista de InterestPoints pasada a showPoints
        ArgumentCaptor<List> captorList = ArgumentCaptor.forClass(List.class);

        verify(mockDAO.getMyInterestPointsDAO()).addInterestPoint(validPoint);
        verify(mockView, times(2)).showPoints(captorList.capture());
        // Verificar que se llama al método getInterestPoints() en el DAO
        verify(mockDAO.getMyInterestPointsDAO(), times(3)).getInterestPoints();

        // Verificar que el tamaño de la lista de puntos de interés haya aumentado a 1
        assertEquals(1, captorList.getValue().size());

        // Verifica que el punto validPoint está en la lista capturada
        assertTrue(captorList.getValue().contains(validPoint));
        assertEquals(captorList.getValue().get(0), validPoint);
    }

    // Test de exito II: Se anhade un nuevo punto, por antiguedad y se comprueba que se ha anhadido en la posicion correcta
    @Test
    public void testAddNewPointByAgeWithoutSorting() {
        // Crear el primer punto con una fecha antigua
        InterestPoint point1 = new InterestPoint("Point 1", "#0000ff", 40.0, -3.0, 100);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, Calendar.JANUARY, 1); // Fecha antigua
        point1.setCreationDate(calendar.getTime());

        // Crear el segundo punto con la fecha actual (automáticamente)
        InterestPoint point2 = new InterestPoint("Point 2", "#fff000", 41.0, -3.0, 60);

        // Simular la obtención de los puntos desde la base de datos (no ordenados)
        List<InterestPoint> pointsList = new ArrayList<>();
        // Simulamos que getInterestPoints() devuelve una lista vacía inicialmente
        when(mockDAO.getMyInterestPointsDAO().getInterestPoints()).thenReturn(pointsList);

        // Acción: se añaden los puntos
        sut.onAcceptNewPointOfInterestClicked(point1);
        sut.onAcceptNewPointOfInterestClicked(point2);

        pointsList.add(point1); // El punto con fecha antigua
        pointsList.add(point2); // El punto con fecha reciente

        // Crear el ArgumentCaptor para capturar la lista de puntos
        ArgumentCaptor<List<InterestPoint>> captor = ArgumentCaptor.forClass(List.class);

        // Verificar que se haya llamado al método showPoints() con la lista de puntos
        verify(mockView, times(3)).showPoints(captor.capture());

        // Obtener la lista capturada
        List<InterestPoint> capturedPoints = captor.getValue();

        // Comprobar que los puntos se han añadido en el orden esperado
        assertEquals("The first point should be at position 0", point1, capturedPoints.get(0)); // Punto antiguo en la posición 0
        assertEquals("The second point should be at position 1", point2, capturedPoints.get(1)); // Punto reciente en la posición 1

        // Verificar que se ha llamado al método addInterestPoint
        verify(mockDAO.getMyInterestPointsDAO()).addInterestPoint(point2);

        // Verificar que se llama al método getInterestPoints() en el DAO
        verify(mockDAO.getMyInterestPointsDAO(), times(3)).getInterestPoints();

    }

    // Test de fracaso por la latitud  >90
    @Test(expected = LatitudInvalidaException.class)
    public void testOnAcceptNewPointOfInterestClicked_withInvalidLatitudeAbove() {
        // Configuramos mockView para que devuelva mockDAO cuando se llame a getmockDAO()

        InterestPoint invalidPoint = new InterestPoint("Punto", "#0000ff", 100, -82.3467, 20);
        sut.onAcceptNewPointOfInterestClicked(invalidPoint);
        Mockito.verify(mockDAO.getMyInterestPointsDAO(), never()).addInterestPoint(invalidPoint);
        verify(mockDAO.getMyInterestPointsDAO(), never()).getInterestPoints();
        assertThrows(LatitudInvalidaException.class,
                () -> sut.onAcceptNewPointOfInterestClicked(invalidPoint));
    }

    // Test de fracaso por la latitud inferior <(-90)
    @Test(expected = LatitudInvalidaException.class)
    public void testOnAcceptNewPointOfInterestClicked_withInvalidLatitudeBelow() {
        // Configuramos mockView para que devuelva mockDAO cuando se llame a getmockDAO()
        InterestPoint invalidPoint = new InterestPoint("Punto", "#0000ff", -96, -82.3467, 20);
        sut.onAcceptNewPointOfInterestClicked(invalidPoint);
        Mockito.verify(mockDAO.getMyInterestPointsDAO(), never()).addInterestPoint(invalidPoint);
        verify(mockDAO.getMyInterestPointsDAO(), never()).getInterestPoints();
        assertThrows(LatitudInvalidaException.class,
                () -> sut.onAcceptNewPointOfInterestClicked(invalidPoint));
    }


    // Test de fracaso por la longitud  >180
    @Test(expected = LongitudInvalidaException.class)
    public void testOnAcceptNewPointOfInterestClicked_withInvalidLongitudeAbove() {
        // Configuramos mockView para que devuelva mockDAO cuando se llame a getmockDAO()
        InterestPoint invalidPoint = new InterestPoint("Punto", "#0000ff", 40.0637, 300, 20);
        sut.onAcceptNewPointOfInterestClicked(invalidPoint);
        Mockito.verify(mockDAO.getMyInterestPointsDAO(), never()).addInterestPoint(invalidPoint);
        verify(mockDAO.getMyInterestPointsDAO(), never()).getInterestPoints();
        assertThrows(LongitudInvalidaException.class,
                () -> sut.onAcceptNewPointOfInterestClicked(invalidPoint));
    }

    // Test de fracaso por la longitud  <(-180)
    @Test(expected = LongitudInvalidaException.class)
    public void testOnAcceptNewPointOfInterestClicked_withInvalidLongitudeBelow() {
        // Configuramos mockView para que devuelva mockDAO cuando se llame a getmockDAO()

        InterestPoint invalidPoint = new InterestPoint("Punto", "#0000ff", 40.0637, -234, 20);
        sut.onAcceptNewPointOfInterestClicked(invalidPoint);
        Mockito.verify(mockDAO.getMyInterestPointsDAO(), never()).addInterestPoint(invalidPoint);
        verify(mockDAO.getMyInterestPointsDAO(), never()).getInterestPoints();
        assertThrows(LongitudInvalidaException.class,
                () -> sut.onAcceptNewPointOfInterestClicked(invalidPoint));
    }

    // Test de fracaso radio igual que cero.
    @Test(expected = RadioInvalidoException.class)
    public void testOnAcceptNewPointOfInterestClicked_withZeroRadius() {
        // Configuramos mockView para que devuelva mockDAO cuando se llame a getmockDAO()

        InterestPoint invalidPoint = new InterestPoint("Punto", "#0000ff", 40.0637, -82.3467, 0);
        sut.onAcceptNewPointOfInterestClicked(invalidPoint);
        Mockito.verify(mockDAO.getMyInterestPointsDAO(), never()).addInterestPoint(invalidPoint);
        verify(mockDAO.getMyInterestPointsDAO(), never()).getInterestPoints();
        assertThrows(RadioInvalidoException.class,
                () -> sut.onAcceptNewPointOfInterestClicked(invalidPoint));
    }

    // Test de fracaso radio negativo.
    @Test(expected = RadioInvalidoException.class)
    public void testOnAcceptNewPointOfInterestClicked_withNegativeRadius() {
        // Configuramos mockView para que devuelva mockDAO cuando se llame a getmockDAO()

        InterestPoint invalidPoint = new InterestPoint("Punto", "#0000ff", 40.0637, -82.3467, -10);
        sut.onAcceptNewPointOfInterestClicked(invalidPoint);
        Mockito.verify(mockDAO.getMyInterestPointsDAO(), never()).addInterestPoint(invalidPoint);
        verify(mockDAO.getMyInterestPointsDAO(), never()).getInterestPoints();
        assertThrows(RadioInvalidoException.class,
                () -> sut.onAcceptNewPointOfInterestClicked(invalidPoint));
    }

}


