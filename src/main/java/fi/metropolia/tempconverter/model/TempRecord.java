package fi.metropolia.tempconverter.model;

import java.time.LocalDateTime;

/** A row of the temp_record table: one saved conversion. */
public class TempRecord {

    private int id;
    private final double inputValue;
    private final TemperatureUnit fromUnit;
    private final double resultValue;
    private final TemperatureUnit toUnit;
    private final LocalDateTime createdAt;

    /** Constructor for a new record that is not yet saved. */
    public TempRecord(double inputValue, TemperatureUnit fromUnit,
                      double resultValue, TemperatureUnit toUnit) {
        this(0, inputValue, fromUnit, resultValue, toUnit, null);
    }

    public TempRecord(int id, double inputValue, TemperatureUnit fromUnit,
                      double resultValue, TemperatureUnit toUnit, LocalDateTime createdAt) {
        this.id = id;
        this.inputValue = inputValue;
        this.fromUnit = fromUnit;
        this.resultValue = resultValue;
        this.toUnit = toUnit;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getInputValue() {
        return inputValue;
    }

    public TemperatureUnit getFromUnit() {
        return fromUnit;
    }

    public double getResultValue() {
        return resultValue;
    }

    public TemperatureUnit getToUnit() {
        return toUnit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
