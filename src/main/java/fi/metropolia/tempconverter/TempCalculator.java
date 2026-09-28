package fi.metropolia.tempconverter;

import java.util.Locale;

/** Pure conversion logic, no GUI or database. Units are identified by code: C, F, K. */
public class TempCalculator {

    public static final double ABSOLUTE_ZERO_C = -273.15;

    public double convert(double value, String fromCode, String toCode) {
        double celsius = toCelsius(value, fromCode);
        if (celsius < ABSOLUTE_ZERO_C - 1e-9) {
            throw new IllegalArgumentException("Temperature is below absolute zero.");
        }
        return fromCelsius(celsius, toCode);
    }

    public double toCelsius(double value, String code) {
        return switch (normalize(code)) {
            case "C" -> value;
            case "F" -> (value - 32) * 5.0 / 9.0;
            case "K" -> value - 273.15;
            default -> throw new IllegalArgumentException("Unknown unit: " + code);
        };
    }

    public double fromCelsius(double celsius, String code) {
        return switch (normalize(code)) {
            case "C" -> celsius;
            case "F" -> celsius * 9.0 / 5.0 + 32;
            case "K" -> celsius + 273.15;
            default -> throw new IllegalArgumentException("Unknown unit: " + code);
        };
    }

    private static String normalize(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Unit code must not be null.");
        }
        return code.trim().toUpperCase(Locale.ROOT);
    }
}
