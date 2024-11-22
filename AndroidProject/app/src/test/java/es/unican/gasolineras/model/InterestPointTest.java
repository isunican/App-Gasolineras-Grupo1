package es.unican.gasolineras.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import android.graphics.Color;
import android.location.Location;

import net.bytebuddy.asm.Advice;

public class InterestPointTest {

    private InterestPoint sut;

    Location mockLocation;

    @Before
    public void setUp() {
        try {
            mockStatic(Color.class);
        } catch (Exception e) {

        }
        when(Color.valueOf(anyInt())).thenReturn(mock(Color.class)); // Retorna un objeto simulado

        mockLocation = mock(Location.class);

        sut = new InterestPoint("Punto prueba", 23, 43.4188, -3.8407, 5.0);
    }

    @Test
    public void isGasStationInRadius1a() {
        Gasolinera g = mock(Gasolinera.class);
        when(g.getLocation()).thenReturn(mockLocation);

        when(mockLocation.distanceTo(mockLocation)).thenReturn(4000f);

        assertTrue(sut.isGasStationInRadius(g, mockLocation));
    }

    @Test
    public void isGasStationInRadius1b() {
        Gasolinera g = mock(Gasolinera.class);
        when(g.getLocation()).thenReturn(mockLocation);

        when(mockLocation.distanceTo(mockLocation)).thenReturn(6000f);

        assertFalse(sut.isGasStationInRadius(g, mockLocation));
    }

    @Test
    public void isGasStationInRadius1c() {
        when(mockLocation.distanceTo(mockLocation)).thenReturn(6000f);

        assertFalse(sut.isGasStationInRadius(null, mockLocation));
    }
}
