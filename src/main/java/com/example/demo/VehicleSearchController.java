package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class VehicleSearchController {

    /* =======================
       SEARCH CONTROLS
       ======================= */
    @FXML private ComboBox<String> searchByComboBox;
    @FXML private TextField searchValueTextField;

    /* =======================
       FILTER COMBOBOXES
       ======================= */
    @FXML private ComboBox<String> brandComboBox;
    @FXML private ComboBox<String> colourComboBox;
    @FXML private ComboBox<String> vehicleTypeComboBox;
    @FXML private ComboBox<String> slotTypeComboBox;

    /* =======================
       TABLE
       ======================= */
    @FXML private TableView<VehicleSearchRow> vehicleTable;
    @FXML private TableColumn<VehicleSearchRow, String> platenumbercolumn;
    @FXML private TableColumn<VehicleSearchRow, String> vehicletypecolumn;
    @FXML private TableColumn<VehicleSearchRow, String> brandcolumn;
    @FXML private TableColumn<VehicleSearchRow, String> colourcolumn;
    @FXML private TableColumn<VehicleSearchRow, Integer> wheelscolumn;
    @FXML private TableColumn<VehicleSearchRow, String> slotnumbercolumn;
    @FXML private TableColumn<VehicleSearchRow, String> slottypecolumn;
    @FXML private TableColumn<VehicleSearchRow, String> timeincolumn;

    private final ObservableList<VehicleSearchRow> data =
            FXCollections.observableArrayList();

    /* =======================
       BASE QUERY
       ======================= */
    private static final String BASE_QUERY = """
        SELECT
            v.plate_number,
            v.vehicle_type,
            v.brand,
            v.colour,
            v.wheels,
            ps.slot_number,
            ps.slot_type,
            s.time_in
        FROM parking_sessions s
        JOIN vehicles v ON s.vehicle_id = v.vehicle_id
        JOIN parking_slots ps ON s.slot_id = ps.slot_id
        WHERE s.time_out IS NULL
    """;

    /* =======================
       INITIALIZE
       ======================= */
    @FXML
    public void initialize() {

        // Search-by categories
        searchByComboBox.setItems(FXCollections.observableArrayList(
                "Plate Number",
                "Wheels",
                "Slot Number",
                "Time In"
        ));
        searchByComboBox.setPromptText("Select category");

        // Brands
        brandComboBox.setItems(FXCollections.observableArrayList(
                "BMW","Mercedes","Audi","Toyota","Volkswagen",
                "Honda","Ford","Hyundai","Kia","Nissan"
        ));
        brandComboBox.setPromptText("Brand");

        // Colours
        colourComboBox.setItems(FXCollections.observableArrayList(
                "Black","White","Silver","Grey","Blue",
                "Red","Green","Brown","Yellow","Orange"
        ));
        colourComboBox.setPromptText("Colour");

        // Vehicle Types
        vehicleTypeComboBox.setItems(FXCollections.observableArrayList(
                "Car","Bus","Bike","Truck"
        ));
        vehicleTypeComboBox.setPromptText("Vehicle Type");

        // Slot Types
        slotTypeComboBox.setItems(FXCollections.observableArrayList(
                "VIP","NORMAL"
        ));
        slotTypeComboBox.setPromptText("Slot Type");

        // Table bindings
        platenumbercolumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getPlateNumber()));
        vehicletypecolumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getVehicleType()));
        brandcolumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getBrand()));
        colourcolumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getColour()));
        wheelscolumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getWheels()));
        slotnumbercolumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getSlotNumber()));
        slottypecolumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getSlotType()));
        timeincolumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getTimeIn()));

        vehicleTable.setItems(data);

        loadAllVehicles();
    }

    /* =======================
       SEARCH BUTTON
       ======================= */
    @FXML
    private void onSearch() {

        StringBuilder sql = new StringBuilder(BASE_QUERY);
        List<Object> params = new ArrayList<>();

        // Search-by logic
        if (searchByComboBox.getValue() != null &&
                !searchValueTextField.getText().isBlank()) {

            switch (searchByComboBox.getValue()) {
                case "Plate Number" -> {
                    sql.append(" AND v.plate_number LIKE ?");
                    params.add("%" + searchValueTextField.getText() + "%");
                }
                case "Wheels" -> {
                    sql.append(" AND v.wheels = ?");
                    params.add(Integer.parseInt(searchValueTextField.getText()));
                }
                case "Slot Number" -> {
                    sql.append(" AND ps.slot_number LIKE ?");
                    params.add("%" + searchValueTextField.getText() + "%");
                }
                case "Time In" -> {
                    sql.append(" AND DATE(s.time_in) = ?");
                    params.add(Date.valueOf(searchValueTextField.getText()));
                }
            }
        }

        // Filters
        if (brandComboBox.getValue() != null) {
            sql.append(" AND v.brand = ?");
            params.add(brandComboBox.getValue());
        }
        if (colourComboBox.getValue() != null) {
            sql.append(" AND v.colour = ?");
            params.add(colourComboBox.getValue());
        }
        if (vehicleTypeComboBox.getValue() != null) {
            sql.append(" AND v.vehicle_type = ?");
            params.add(vehicleTypeComboBox.getValue());
        }
        if (slotTypeComboBox.getValue() != null) {
            sql.append(" AND ps.slot_type = ?");
            params.add(slotTypeComboBox.getValue());
        }

        if (params.isEmpty()) {
            showInfo("Please select at least one search criterion.");
            return;
        }

        executeSearch(sql.toString(), params, true);
    }

    /* =======================
       REFRESH BUTTON
       ======================= */
    @FXML
    private void onRefresh() {

        searchByComboBox.setValue(null);
        searchByComboBox.setPromptText("Select category");

        searchValueTextField.clear();
        searchValueTextField.setPromptText("Enter value");

        brandComboBox.setValue(null);
        brandComboBox.setPromptText("Brand");

        colourComboBox.setValue(null);
        colourComboBox.setPromptText("Colour");

        vehicleTypeComboBox.setValue(null);
        vehicleTypeComboBox.setPromptText("Vehicle Type");

        slotTypeComboBox.setValue(null);
        slotTypeComboBox.setPromptText("Slot Type");

        loadAllVehicles();
    }

    /* =======================
       DATABASE
       ======================= */
    private void loadAllVehicles() {
        executeSearch(BASE_QUERY, new ArrayList<>(), false);
    }

    private void executeSearch(String sql, List<Object> params, boolean showAlert) {

        data.clear();
        boolean found = false;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                found = true;
                data.add(new VehicleSearchRow(
                        rs.getString("plate_number"),
                        rs.getString("vehicle_type"),
                        rs.getString("brand"),
                        rs.getString("colour"),
                        rs.getInt("wheels"),
                        rs.getString("slot_number"),
                        rs.getString("slot_type"),
                        rs.getTimestamp("time_in").toString()
                ));
            }

            if (!found && showAlert) {
                showInfo("No vehicles match the selected criteria.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* =======================
       INFO ALERT
       ======================= */
    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
