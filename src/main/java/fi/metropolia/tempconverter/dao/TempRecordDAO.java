package fi.metropolia.tempconverter.dao;

import fi.metropolia.tempconverter.db.DBConnection;
import fi.metropolia.tempconverter.model.TempRecord;
import fi.metropolia.tempconverter.model.TemperatureUnit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TempRecordDAO {

    /** Saves the record and returns the generated id (also set on the record). */
    public int save(TempRecord record) throws SQLException {
        String sql = "INSERT INTO temp_record (input_value, from_unit_id, result_value, to_unit_id) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, record.getInputValue());
            ps.setInt(2, record.getFromUnit().getId());
            ps.setDouble(3, record.getResultValue());
            ps.setInt(4, record.getToUnit().getId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    record.setId(keys.getInt(1));
                }
            }
        }
        return record.getId();
    }

    /** All conversions, newest first, joined with both unit rows. */
    public List<TempRecord> findAll() throws SQLException {
        String sql = """
                SELECT r.id, r.input_value, r.result_value, r.created_at,
                       f.id AS f_id, f.code AS f_code, f.name AS f_name,
                       t.id AS t_id, t.code AS t_code, t.name AS t_name
                FROM temp_record r
                JOIN temperature_unit f ON r.from_unit_id = f.id
                JOIN temperature_unit t ON r.to_unit_id = t.id
                ORDER BY r.id DESC""";
        List<TempRecord> records = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TemperatureUnit from = new TemperatureUnit(
                        rs.getInt("f_id"), rs.getString("f_code"), rs.getString("f_name"));
                TemperatureUnit to = new TemperatureUnit(
                        rs.getInt("t_id"), rs.getString("t_code"), rs.getString("t_name"));
                Timestamp created = rs.getTimestamp("created_at");
                records.add(new TempRecord(
                        rs.getInt("id"),
                        rs.getDouble("input_value"), from,
                        rs.getDouble("result_value"), to,
                        created == null ? null : created.toLocalDateTime()));
            }
        }
        return records;
    }

    /** Deletes all conversions and returns how many rows were removed. */
    public int deleteAll() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {
            return st.executeUpdate("DELETE FROM temp_record");
        }
    }
}
