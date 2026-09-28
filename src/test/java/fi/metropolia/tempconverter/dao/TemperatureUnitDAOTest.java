package fi.metropolia.tempconverter.dao;

import fi.metropolia.tempconverter.TestDatabase;
import fi.metropolia.tempconverter.model.TemperatureUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemperatureUnitDAOTest {

    private final TemperatureUnitDAO dao = new TemperatureUnitDAO();

    @BeforeEach
    void setUp() throws SQLException {
        TestDatabase.init();
    }

    @Test
    void findAllReturnsThreeUnitsInOrder() throws SQLException {
        List<TemperatureUnit> units = dao.findAll();
        assertEquals(3, units.size());
        assertEquals("C", units.get(0).getCode());
        assertEquals("F", units.get(1).getCode());
        assertEquals("K", units.get(2).getCode());
    }

    @Test
    void findByCodeFindsExistingUnit() throws SQLException {
        Optional<TemperatureUnit> unit = dao.findByCode("F");
        assertTrue(unit.isPresent());
        assertEquals("Fahrenheit", unit.get().getName());
    }

    @Test
    void findByCodeReturnsEmptyForUnknown() throws SQLException {
        assertTrue(dao.findByCode("X").isEmpty());
    }
}
