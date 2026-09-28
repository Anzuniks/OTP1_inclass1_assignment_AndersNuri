package fi.metropolia.tempconverter;

import fi.metropolia.tempconverter.model.TemperatureUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Tests the non-GUI helpers of Main (no JavaFX toolkit needed). */
class MainTest {

    @Test
    void parsesDotDecimal() {
        assertEquals(12.5, Main.parseInput("12.5"));
    }

    @Test
    void parsesCommaDecimalAndTrims() {
        assertEquals(12.5, Main.parseInput("  12,5 "));
        assertEquals(-40.0, Main.parseInput("-40"));
    }

    @Test
    void rejectsEmptyAndInvalidInput() {
        assertThrows(NumberFormatException.class, () -> Main.parseInput(null));
        assertThrows(NumberFormatException.class, () -> Main.parseInput("   "));
        assertThrows(NumberFormatException.class, () -> Main.parseInput("abc"));
        assertThrows(NumberFormatException.class, () -> Main.parseInput("NaN"));
        assertThrows(NumberFormatException.class, () -> Main.parseInput("Infinity"));
    }

    @Test
    void formatsValueWithUnitSymbol() {
        assertEquals("212.00 °F", Main.formatValue(212, new TemperatureUnit(2, "F", "Fahrenheit")));
        assertEquals("273.15 K", Main.formatValue(273.15, new TemperatureUnit(3, "K", "Kelvin")));
    }
}
