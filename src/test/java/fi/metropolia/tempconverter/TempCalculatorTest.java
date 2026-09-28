package fi.metropolia.tempconverter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TempCalculatorTest {

    private static final double DELTA = 1e-9;
    private final TempCalculator calc = new TempCalculator();

    @Test
    void celsiusToFahrenheit() {
        assertEquals(212.0, calc.convert(100, "C", "F"), DELTA);
        assertEquals(32.0, calc.convert(0, "C", "F"), DELTA);
    }

    @Test
    void fahrenheitToCelsius() {
        assertEquals(0.0, calc.convert(32, "F", "C"), DELTA);
        assertEquals(-40.0, calc.convert(-40, "F", "C"), DELTA);
    }

    @Test
    void celsiusToKelvinAndBack() {
        assertEquals(273.15, calc.convert(0, "C", "K"), DELTA);
        assertEquals(0.0, calc.convert(273.15, "K", "C"), DELTA);
    }

    @Test
    void fahrenheitToKelvin() {
        assertEquals(373.15, calc.convert(212, "F", "K"), DELTA);
    }

    @Test
    void sameUnitReturnsSameValue() {
        assertEquals(25.5, calc.convert(25.5, "C", "C"), DELTA);
    }

    @Test
    void codesAreCaseInsensitive() {
        assertEquals(212.0, calc.convert(100, "c", " f "), DELTA);
    }

    @Test
    void absoluteZeroIsAllowed() {
        assertEquals(-273.15, calc.convert(0, "K", "C"), DELTA);
    }

    @Test
    void belowAbsoluteZeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> calc.convert(-300, "C", "F"));
        assertThrows(IllegalArgumentException.class, () -> calc.convert(-1, "K", "C"));
    }

    @Test
    void unknownUnitThrows() {
        assertThrows(IllegalArgumentException.class, () -> calc.convert(1, "X", "C"));
        assertThrows(IllegalArgumentException.class, () -> calc.fromCelsius(1, "R"));
    }

    @Test
    void nullUnitThrows() {
        assertThrows(IllegalArgumentException.class, () -> calc.convert(1, null, "C"));
    }
}
