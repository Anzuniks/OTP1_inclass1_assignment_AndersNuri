package fi.metropolia.tempconverter.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Opens database connections. Settings come from environment variables
 * DB_URL, DB_USER and DB_PASSWORD (Docker sets these), with local defaults.
 */
public final class DBConnection {

    private static String url = env("DB_URL", "jdbc:mariadb://localhost:3306/tempdb");
    private static String user = env("DB_USER", "tempuser");
    private static String password = env("DB_PASSWORD", "temppass");

    private DBConnection() {
    }

    /** Overrides the connection settings (used by the tests with an H2 database). */
    public static synchronized void configure(String newUrl, String newUser, String newPassword) {
        url = newUrl;
        user = newUser;
        password = newPassword;
    }

    public static String getUrl() {
        return url;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /** Creates the two related tables if missing and inserts the three units once. */
    public static void initSchema() throws SQLException {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS temperature_unit (
                        id   INT AUTO_INCREMENT PRIMARY KEY,
                        code VARCHAR(5)  NOT NULL UNIQUE,
                        name VARCHAR(30) NOT NULL
                    )""");
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS temp_record (
                        id           INT AUTO_INCREMENT PRIMARY KEY,
                        input_value  DOUBLE NOT NULL,
                        from_unit_id INT    NOT NULL,
                        result_value DOUBLE NOT NULL,
                        to_unit_id   INT    NOT NULL,
                        created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_record_from FOREIGN KEY (from_unit_id) REFERENCES temperature_unit(id),
                        CONSTRAINT fk_record_to   FOREIGN KEY (to_unit_id)   REFERENCES temperature_unit(id)
                    )""");

            int unitCount;
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM temperature_unit")) {
                rs.next();
                unitCount = rs.getInt(1);
            }
            if (unitCount == 0) {
                st.executeUpdate("INSERT INTO temperature_unit (code, name) VALUES "
                        + "('C', 'Celsius'), ('F', 'Fahrenheit'), ('K', 'Kelvin')");
            }
        }
    }

    static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}
