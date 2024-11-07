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


