package fi.metropolia.tempconverter.model;

/** A row of the temperature_unit table (Celsius, Fahrenheit, Kelvin). */
public class TemperatureUnit {

    private final int id;
    private final String code;
    private final String name;

    public TemperatureUnit(int id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /** Kelvin has no degree sign, the others do. */
    public String getSymbol() {
        return "K".equalsIgnoreCase(code) ? "K" : "°" + code;
    }

    @Override
    public String toString() {
        return name + " (" + getSymbol() + ")";
    }
}
