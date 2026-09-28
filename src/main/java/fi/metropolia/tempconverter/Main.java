package fi.metropolia.tempconverter;

import fi.metropolia.tempconverter.dao.TempRecordDAO;
import fi.metropolia.tempconverter.dao.TemperatureUnitDAO;
import fi.metropolia.tempconverter.db.DBConnection;
import fi.metropolia.tempconverter.model.TempRecord;
import fi.metropolia.tempconverter.model.TemperatureUnit;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class Main extends Application {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final TempCalculator calculator = new TempCalculator();
    private final TemperatureUnitDAO unitDAO = new TemperatureUnitDAO();
    private final TempRecordDAO recordDAO = new TempRecordDAO();
    private final ObservableList<TempRecord> history = FXCollections.observableArrayList();

    private TextField inputField;
    private ComboBox<TemperatureUnit> fromBox;
    private ComboBox<TemperatureUnit> toBox;
    private Label resultLabel;
    private Label statusLabel;

    @Override
    public void start(Stage stage) {
        inputField = new TextField();
        inputField.setPromptText("e.g. 36.6");
        fromBox = new ComboBox<>();
        toBox = new ComboBox<>();
        resultLabel = new Label("—");
        resultLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        statusLabel = new Label();

        Button convertButton = new Button("Convert");
        Button clearButton = new Button("Clear history");
        convertButton.setDefaultButton(true);
        convertButton.setOnAction(e -> convert());
        clearButton.setOnAction(e -> clearHistory());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.addRow(0, new Label("Value:"), inputField);
        form.addRow(1, new Label("From:"), fromBox);
        form.addRow(2, new Label("To:"), toBox);
        form.addRow(3, new Label("Result:"), resultLabel);

        TableView<TempRecord> table = new TableView<>(history);
        table.getColumns().add(column("Time",
                r -> r.getCreatedAt() == null ? "" : r.getCreatedAt().format(TIME_FMT)));
        table.getColumns().add(column("Input", r -> formatValue(r.getInputValue(), r.getFromUnit())));
        table.getColumns().add(column("Result", r -> formatValue(r.getResultValue(), r.getToUnit())));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("No conversions yet"));

        Label title = new Label("Temperature Converter");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox root = new VBox(12, title, form, new HBox(10, convertButton, clearButton),
                statusLabel, new Label("History (from database)"), table);
        root.setPadding(new Insets(16));
        VBox.setVgrow(table, Priority.ALWAYS);

        loadData(convertButton, clearButton);

        stage.setTitle("Temperature Converter");
        stage.setScene(new Scene(root, 480, 580));
        stage.show();
    }

    private void loadData(Button convertButton, Button clearButton) {
        try {
            DBConnection.initSchema();
            List<TemperatureUnit> units = unitDAO.findAll();
            fromBox.getItems().setAll(units);
            toBox.getItems().setAll(units);
            if (units.size() >= 2) {
                fromBox.getSelectionModel().select(0);
                toBox.getSelectionModel().select(1);
            }
            history.setAll(recordDAO.findAll());
            statusLabel.setText("Connected: " + DBConnection.getUrl());
        } catch (SQLException ex) {
            statusLabel.setText("Database error: " + ex.getMessage());
            convertButton.setDisable(true);
            clearButton.setDisable(true);
        }
    }

    private void convert() {
        TemperatureUnit from = fromBox.getValue();
        TemperatureUnit to = toBox.getValue();
        if (from == null || to == null) {
            statusLabel.setText("Select both units.");
            return;
        }
        try {
            double value = parseInput(inputField.getText());
            double result = calculator.convert(value, from.getCode(), to.getCode());
            resultLabel.setText(formatValue(result, to));
            recordDAO.save(new TempRecord(value, from, result, to));
            history.setAll(recordDAO.findAll());
            statusLabel.setText("Saved to database.");
        } catch (NumberFormatException ex) {
            statusLabel.setText("Please enter a valid number.");
        } catch (IllegalArgumentException ex) {
            statusLabel.setText(ex.getMessage());
        } catch (SQLException ex) {
            statusLabel.setText("Database error: " + ex.getMessage());
        }
    }

    private void clearHistory() {
        try {
            int removed = recordDAO.deleteAll();
            history.clear();
            statusLabel.setText("Deleted " + removed + " record(s).");
        } catch (SQLException ex) {
            statusLabel.setText("Database error: " + ex.getMessage());
        }
    }

    private static TableColumn<TempRecord, String> column(String title, Function<TempRecord, String> getter) {
        TableColumn<TempRecord, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cd -> new SimpleStringProperty(getter.apply(cd.getValue())));
        return col;
    }

    /** Accepts both "12.5" and "12,5". Throws NumberFormatException for bad input. */
    public static double parseInput(String text) {
        if (text == null || text.isBlank()) {
            throw new NumberFormatException("Input is empty");
        }
        double value = Double.parseDouble(text.trim().replace(',', '.'));
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new NumberFormatException("Not a finite number");
        }
        return value;
    }

    public static String formatValue(double value, TemperatureUnit unit) {
        return String.format(Locale.ROOT, "%.2f %s", value, unit.getSymbol());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
