package fi.metropolia.tempconverter.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class TempRecordTest {

    private final TemperatureUnit c = new TemperatureUnit(1, "C", "Celsius");
    private final TemperatureUnit f = new TemperatureUnit(2, "F", "Fahrenheit");

    @Test
    void newRecordHasNoIdOrTimestamp() {
        TempRecord r = new TempRecord(100, c, 212, f);
        assertEquals(0, r.getId());
        assertNull(r.getCreatedAt());
        assertEquals(100, r.getInputValue());
        assertEquals(212, r.getResultValue());
        assertSame(c, r.getFromUnit());
        assertSame(f, r.getToUnit());
    }

    @Test
    void fullConstructorAndSetId() {
        LocalDateTime now = LocalDateTime.now();
        TempRecord r = new TempRecord(5, 0, c, 32, f, now);
        assertEquals(5, r.getId());
        assertEquals(now, r.getCreatedAt());
        r.setId(9);
        assertEquals(9, r.getId());
    }
}
