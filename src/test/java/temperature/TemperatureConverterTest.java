package temperature;

import static org.junit.jupiter.api.Assertions.*;

class TemperatureConverterTest {

    @org.junit.jupiter.api.Test
    void fahrenheitToCelsius() {
        TemperatureConverter converter = new TemperatureConverter();
        double result = converter.fahrenheitToCelsius(32);
        assertEquals(0, result);
    }

    @org.junit.jupiter.api.Test
    void celsiusToFahrenheit() {
        TemperatureConverter converter = new TemperatureConverter();
        double result = converter.celsiusToFahrenheit(0);
        assertEquals(32, result);
    }

    @org.junit.jupiter.api.Test
    void isExtremeTemperature() {
        TemperatureConverter converter = new TemperatureConverter();
        assertTrue(converter.isExtremeTemperature(-100));
        assertTrue(converter.isExtremeTemperature(150));
        assertFalse(converter.isExtremeTemperature(25));
    }
}