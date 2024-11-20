package es.unican.gasolineras.activities.points;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.database.sqlite.SQLiteException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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

@RunWith(RobolectricTestRunner.class)
public class PointsPresenterTest {

    private IPointsContract.Presenter sut;

    //Mocks de la vista y de la DAO, para las pruebas de crear.
    @Mock
    private PointsView mockView;
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
        when(mockView.getPointsDao()).thenReturn(IMockDAO);

        // En este caso es el sut.
        sut= new PointsPresenter();
        sut= new PointsPresenter();

    }

    @Test
    public void initWithPointsTest() {
        //Comprobamos la lista con los puntos
        when(IMockDAO.getInterestPoints()).thenReturn(listaConPuntos);

        // Llama al método init
        sut.init(mockView);

        // Verifica que se llama a view.init()
        verify(mockView).init();

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
        when(IMockDAO.getInterestPoints()).thenReturn(listaVacia);

        // Llama al método init
        sut.init(mockView);

        // Verifica que se llama a view.init()
        verify(mockView).init();

        // Verifica que se llama a showPoints()
        verify(mockView).showPoints(listaVacia);

    }

    @Test
    public void initWithOutConexionBDPointsTest() {

        //Comprobamos la lista con los puntos
        when(IMockDAO.getInterestPoints()).thenThrow(new SQLiteException());

        //Realizamos la inicialización
        sut.init(mockView);

        // Verifica que se llama a view.init()
        verify(mockView).init();

        // Verifica que se llama a showLoadError()
        verify(mockView).showLoadError();
    }

    // Test de exito: no hay puntos en la BBDD
    @Test
    public void testOnAcceptNewPointOfInterestClicked_withValidPoint() {

        // Iniciar el presenter
        sut.init(mockView);

        // Configuramos el punto válido
        InterestPoint validPoint = new InterestPoint("Punto", "#0000ff", 40.0637, -82.3467, 20);

        // Creamos la lista de InterestPoints y programamos el mock de la DAO para que la devuelva.
        List<InterestPoint> interestPointsList = new ArrayList<>();

        // Simulamos que getInterestPoints() devuelve la lista creada de puntos de interes.
        when(IMockDAO.getInterestPoints()).thenReturn(interestPointsList);

        //Comprobamos el tamanho
        assertEquals(0, IMockDAO.getInterestPoints().size());

        // Ejecutamos el metodo
        sut.onAcceptNewPointOfInterestClicked(validPoint);

        // Añadimos el punto manualmente a la lista para simular que se agrega al DAO
        interestPointsList.add(validPoint);

        // Usamos ArgumentCaptor para capturar la lista de InterestPoints pasada a showPoints
        ArgumentCaptor<List> captorList = ArgumentCaptor.forClass(List.class);

        verify(mockView, times(2)).showPoints(captorList.capture());
        // Verificar metodos de la DAO.
        verify(IMockDAO).addInterestPoint(validPoint);
        verify(IMockDAO, times(3)).getInterestPoints();

        // Obtenemos la lista de puntos de interes del captor.
        List<InterestPoint> capturedPoints = captorList.getValue();
        
        // Verificar que el tamaño de la lista de puntos de interés haya aumentado a 1
        assertEquals(1, capturedPoints.size());
        // Verifica que el punto validPoint está en la lista capturada
        assertEquals(capturedPoints.get(0), validPoint);
    }

    // Test de exito II: Se anhade un nuevo punto, por antiguedad y se comprueba que se ha anhadido en la posicion correcta
    @Test
    public void testAddNewPointWithSortingByAge() {

        // Iniciar el presenter
        sut.init(mockView);

        // Crear el primer punto con una fecha antigua
        InterestPoint point1 = new InterestPoint("Punto", "#0000ff", 40.0637, -82.3467, 20);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, Calendar.JANUARY, 1); // Fecha antigua
        point1.setCreationDate(calendar.getTime());

        // Crear el segundo punto con la fecha actual (automáticamente)
        InterestPoint point2 = new InterestPoint("Punto 2", "#5e5f5f", 41.0, -3.0, 60);

        // Simular la obtención de los puntos desde la base de datos (no ordenados)
        List<InterestPoint> pointsList = new ArrayList<>();
        // Simulamos que getInterestPoints() devuelve una lista vacía inicialmente
        when(IMockDAO.getInterestPoints()).thenReturn(pointsList);

        // Acción: se añaden los puntos
        sut.onAcceptNewPointOfInterestClicked(point1);
        sut.onAcceptNewPointOfInterestClicked(point2);

        pointsList.add(point1); // El punto con fecha antigua
        pointsList.add(point2); // El punto con fecha reciente

        // Crear el ArgumentCaptor para capturar la lista de puntos
        ArgumentCaptor<List> captorList = ArgumentCaptor.forClass(List.class);

        // Verificar que se haya llamado al método showPoints() con la lista de puntos
        verify(mockView, times(3)).showPoints(captorList.capture());
        // Verificar la llamada a los metodos de la DAO
        verify(IMockDAO).addInterestPoint(point2);
        verify(IMockDAO, times(3)).getInterestPoints();

        // Obtener la lista capturada
        List<InterestPoint> capturedPoints = captorList.getValue();

        // Comprobar que los puntos se han añadido en el orden esperado
        assertEquals(2, capturedPoints.size());

        // Solucionado, el assert
        assertEquals(capturedPoints.get(0), point1);
        assertEquals(capturedPoints.indexOf(point2), 1);

    }

    // Test de fracaso por la latitud  >90
    @Test
    public void testOnAcceptNewPointOfInterestClicked_withInvalidLatitudeAbove() {

        // Iniciar el presenter
        sut.init(mockView);

        // Creamos la lista de InterestPoints y programamos el mock de la DAO para devolverla.
        List<InterestPoint> interestPointsList = new ArrayList<>();
        when(IMockDAO.getInterestPoints()).thenReturn(interestPointsList);

        InterestPoint invalidPoint = new InterestPoint("Punto", "#0000ff", 100, -82.3467, 20);
        // Verificamos que se lanza la excepcion
        assertThrows(LatitudInvalidaException.class,
                () -> sut.onAcceptNewPointOfInterestClicked(invalidPoint));

        // Verificamos que nunca se llaman a estos metodos.
        verify(IMockDAO, never()).addInterestPoint(invalidPoint);
        // Se llama 1 vez por el primer load.
        verify(IMockDAO).getInterestPoints();

        //Obtenemos la lista de puntos de interes de la DAO
        List<InterestPoint> pointsListDAO = IMockDAO.getInterestPoints();
        // Verificamos que la lista de puntos de interés esté vacía
        assertEquals(0, pointsListDAO.size());
    }

    // Test de fracaso por la latitud inferior <(-90)
    @Test
    public void testOnAcceptNewPointOfInterestClicked_withInvalidLatitudeBelow() {

        // Iniciar el presenter
        sut.init(mockView);

        // Creamos la lista de InterestPoints y programamos el mock de la DAO para devolverla.
        List<InterestPoint> interestPointsList = new ArrayList<>();
        when(IMockDAO.getInterestPoints()).thenReturn(interestPointsList);

        InterestPoint invalidPoint = new InterestPoint("Punto", "#0000ff", -96, -82.3467, 20);
        assertThrows(LatitudInvalidaException.class,
                () -> sut.onAcceptNewPointOfInterestClicked(invalidPoint));

        // Verificamos que nunca se llaman a estos metodos.
        verify(IMockDAO, never()).addInterestPoint(invalidPoint);
        // Se llama 1 vez por el primer load.
        verify(IMockDAO).getInterestPoints();

        //Obtenemos la lista de puntos de interes de la DAO
        List<InterestPoint> pointsListDAO = IMockDAO.getInterestPoints();
        // Verificamos que la lista de puntos de interés esté vacía
        assertEquals(0, pointsListDAO.size());
    }


    // Test de fracaso por la longitud  >180
    @Test
    public void testOnAcceptNewPointOfInterestClicked_withInvalidLongitudeAbove() {

        // Iniciar el presenter
        sut.init(mockView);

        // Creamos la lista de InterestPoints y programamos el mock de la DAO para devolverla.
        List<InterestPoint> interestPointsList = new ArrayList<>();
        when(IMockDAO.getInterestPoints()).thenReturn(interestPointsList);

        InterestPoint invalidPoint = new InterestPoint("Punto", "#0000ff", 40.0637, 300, 20);
        assertThrows(LongitudInvalidaException.class,
                () -> sut.onAcceptNewPointOfInterestClicked(invalidPoint));

        // Verificamos que nunca se llaman a estos metodos,
        verify(IMockDAO, never()).addInterestPoint(invalidPoint);
        // Se llama 1 vez por el primer load.
        verify(IMockDAO).getInterestPoints();

        //Obtenemos la lista de puntos de interes de la DAO
        List<InterestPoint> pointsListDAO = IMockDAO.getInterestPoints();
        // Verificamos que la lista de puntos de interés esté vacía
        assertEquals(0, pointsListDAO.size());
    }

    // Test de fracaso por la longitud  <(-180)
    @Test
    public void testOnAcceptNewPointOfInterestClicked_withInvalidLongitudeBelow() {

        // Iniciar el presenter
        sut.init(mockView);

        // Creamos la lista de InterestPoints y programamos el mock de la DAO para devolverla.
        List<InterestPoint> interestPointsList = new ArrayList<>();
        when(IMockDAO.getInterestPoints()).thenReturn(interestPointsList);

        InterestPoint invalidPoint = new InterestPoint("Punto", "#0000ff", 40.0637, -234, 20);
        assertThrows(LongitudInvalidaException.class,
                () -> sut.onAcceptNewPointOfInterestClicked(invalidPoint));

        // Verificamos que nunca se llaman a estos metodos.
        Mockito.verify(IMockDAO, never()).addInterestPoint(invalidPoint);
        // Se llama 1 vez por el primer load.
        verify(IMockDAO).getInterestPoints();

        //Obtenemos la lista de puntos de interes de la DAO
        List<InterestPoint> pointsListDAO = IMockDAO.getInterestPoints();
        // Verificamos que la lista de puntos de interés esté vacía
        assertEquals(0, pointsListDAO.size());
    }

    // Test de fracaso radio igual que cero.
    @Test
    public void testOnAcceptNewPointOfInterestClicked_withZeroRadius() {

        // Iniciar el presenter
        sut.init(mockView);

        // Creamos la lista de InterestPoints y programamos el mock de la DAO para devolverla.
        List<InterestPoint> interestPointsList = new ArrayList<>();
        when(IMockDAO.getInterestPoints()).thenReturn(interestPointsList);

        InterestPoint invalidPoint = new InterestPoint("Punto", "#0000ff", 40.0637, -82.3467, 0);
        assertThrows(RadioInvalidoException.class,
                () -> sut.onAcceptNewPointOfInterestClicked(invalidPoint));

        // Verificamos que nunca se llaman a estos metodos.
        Mockito.verify(IMockDAO, never()).addInterestPoint(invalidPoint);
        // Se llama 1 vez por el primer load.
        verify(IMockDAO).getInterestPoints();

        //Obtenemos la lista de puntos de interes de la DAO
        List<InterestPoint> pointsListDAO = IMockDAO.getInterestPoints();
        // Verificamos que la lista de puntos de interés esté vacía
        assertEquals(0, pointsListDAO.size());
    }

    // Test de fracaso radio negativo.
    @Test
    public void testOnAcceptNewPointOfInterestClicked_withNegativeRadius() {

        // Iniciar el presenter
        sut.init(mockView);

        // Creamos la lista de InterestPoints y programamos el mock de la DAO para devolverla.
        List<InterestPoint> interestPointsList = new ArrayList<>();
        when(IMockDAO.getInterestPoints()).thenReturn(interestPointsList);

        InterestPoint invalidPoint = new InterestPoint("Punto", "#0000ff", 40.0637, -82.3467, -10);

        // Verificamos que se lanza la excepcion
        assertThrows(RadioInvalidoException.class,
                () -> sut.onAcceptNewPointOfInterestClicked(invalidPoint));

        // Verificamos que nunca se llaman a estos metodos.
        Mockito.verify(IMockDAO, never()).addInterestPoint(invalidPoint);
        // Se llama 1 vez por el primer load.
        verify(IMockDAO).getInterestPoints();

        //Obtenemos la lista de puntos de interes de la DAO
        List<InterestPoint> pointsListDAO = IMockDAO.getInterestPoints();
        // Verificamos que la lista de puntos de interés esté vacía
        assertEquals(0, pointsListDAO.size());
    }

}


