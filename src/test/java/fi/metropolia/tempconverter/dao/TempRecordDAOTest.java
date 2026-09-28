package fi.metropolia.tempconverter.dao;

import fi.metropolia.tempconverter.TestDatabase;
import fi.metropolia.tempconverter.model.TempRecord;
import fi.metropolia.tempconverter.model.TemperatureUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TempRecordDAOTest {

    private final TempRecordDAO recordDAO = new TempRecordDAO();
    private final TemperatureUnitDAO unitDAO = new TemperatureUnitDAO();
    private TemperatureUnit celsius;
    private TemperatureUnit fahrenheit;

    @BeforeEach
    void setUp() throws SQLException {
        TestDatabase.init();
        recordDAO.deleteAll();
        celsius = unitDAO.findByCode("C").orElseThrow();
        fahrenheit = unitDAO.findByCode("F").orElseThrow();
    }

    @Test
    void saveReturnsGeneratedId() throws SQLException {
        TempRecord r = new TempRecord(100, celsius, 212, fahrenheit);
        int id = recordDAO.save(r);
        assertTrue(id > 0);
        assertEquals(id, r.getId());
    }

    @Test
    void findAllReturnsJoinedUnits() throws SQLException {
        recordDAO.save(new TempRecord(100, celsius, 212, fahrenheit));
        List<TempRecord> all = recordDAO.findAll();
        assertEquals(1, all.size());
        TempRecord r = all.get(0);
        assertEquals(100, r.getInputValue());
        assertEquals(212, r.getResultValue());
        assertEquals("C", r.getFromUnit().getCode());
        assertEquals("Fahrenheit", r.getToUnit().getName());
        assertNotNull(r.getCreatedAt());
    }

    @Test
    void findAllIsNewestFirst() throws SQLException {
        recordDAO.save(new TempRecord(1, celsius, 33.8, fahrenheit));
        recordDAO.save(new TempRecord(2, celsius, 35.6, fahrenheit));
        List<TempRecord> all = recordDAO.findAll();
        assertEquals(2, all.get(0).getInputValue());
        assertEquals(1, all.get(1).getInputValue());
    }

    @Test
    void deleteAllRemovesEverything() throws SQLException {
        recordDAO.save(new TempRecord(1, celsius, 33.8, fahrenheit));
        recordDAO.save(new TempRecord(2, celsius, 35.6, fahrenheit));
        assertEquals(2, recordDAO.deleteAll());
        assertTrue(recordDAO.findAll().isEmpty());
    }
}
