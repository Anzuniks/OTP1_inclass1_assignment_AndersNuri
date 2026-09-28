package fi.metropolia.tempconverter.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemperatureUnitTest {

    @Test
    void gettersReturnConstructorValues() {
        TemperatureUnit unit = new TemperatureUnit(1, "C", "Celsius");
        assertEquals(1, unit.getId());
        assertEquals("C", unit.getCode());
        assertEquals("Celsius", unit.getName());
    }

    @Test
    void symbolHasDegreeSignExceptKelvin() {
        assertEquals("°C", new TemperatureUnit(1, "C", "Celsius").getSymbol());
        assertEquals("°F", new TemperatureUnit(2, "F", "Fahrenheit").getSymbol());
        assertEquals("K", new TemperatureUnit(3, "K", "Kelvin").getSymbol());
    }

    @Test
    void toStringShowsNameAndSymbol() {
        assertEquals("Celsius (°C)", new TemperatureUnit(1, "C", "Celsius").toString());
    }
}
