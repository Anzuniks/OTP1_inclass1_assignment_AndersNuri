package fi.metropolia.tempconverter;

import fi.metropolia.tempconverter.db.DBConnection;

import java.sql.SQLException;

/** Points DBConnection to an in-memory H2 database in MySQL mode. */
public final class TestDatabase {

    public static final String URL = "jdbc:h2:mem:tempdb;MODE=MySQL;DB_CLOSE_DELAY=-1";

    private TestDatabase() {
    }

    public static void init() throws SQLException {
        DBConnection.configure(URL, "sa", "");
        DBConnection.initSchema();
    }
}
