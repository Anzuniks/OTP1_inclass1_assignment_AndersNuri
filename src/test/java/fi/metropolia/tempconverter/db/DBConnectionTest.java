package fi.metropolia.tempconverter.db;

import fi.metropolia.tempconverter.TestDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DBConnectionTest {

    @BeforeEach
    void setUp() throws SQLException {
        TestDatabase.init();
    }

    @AfterEach
    void restore() throws SQLException {
        TestDatabase.init();
    }

    @Test
    void connectionIsValid() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            assertTrue(conn.isValid(1));
        }
        assertEquals(TestDatabase.URL, DBConnection.getUrl());
    }

    @Test
    void initSchemaSeedsUnitsOnlyOnce() throws SQLException {
        DBConnection.initSchema();
        DBConnection.initSchema();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM temperature_unit")) {
            rs.next();
            assertEquals(3, rs.getInt(1));
        }
    }

    @Test
    void invalidUrlThrows() {
        DBConnection.configure("jdbc:doesnotexist:foo", "x", "y");
        assertThrows(SQLException.class, DBConnection::getConnection);
    }

    @Test
    void envFallsBackToDefault() {
        assertEquals("fallback", DBConnection.env("SURELY_NOT_SET_TEMP_CONVERTER_VAR", "fallback"));
    }
}
