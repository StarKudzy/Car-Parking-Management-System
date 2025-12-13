package com.example.demo;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class StaffVehicleManagementController {

    /* ================= INPUTS ================= */
    @FXML private TextField plateField;
    @FXML private ComboBox<String> vehicleTypeBox;
    @FXML private ComboBox<String> brandBox;
    @FXML private ComboBox<String> colourBox;
    @FXML private ComboBox<Integer> wheelsBox;
    @FXML private ComboBox<String> slotNumberBox;
    @FXML private ComboBox<String> slotTypeBox;

    /* ================= TABLE ================= */
    @FXML private TableView<VehicleSearchRow> vehicleTable;
    @FXML private TableColumn<VehicleSearchRow, String> plateCol;
    @FXML private TableColumn<VehicleSearchRow, String> typeCol;
    @FXML private TableColumn<VehicleSearchRow, String> brandCol;
    @FXML private TableColumn<VehicleSearchRow, String> colourCol;
    @FXML private TableColumn<VehicleSearchRow, Integer> wheelscolumn;
    @FXML private TableColumn<VehicleSearchRow, String> slotCol;
    @FXML private TableColumn<VehicleSearchRow, String> slotTypeCol;
    @FXML private TableColumn<VehicleSearchRow, String> timeCol;

    private final ObservableList<VehicleSearchRow> data = FXCollections.observableArrayList();

    /* ================= INITIALIZE ================= */
    @FXML
    public void initialize() {

        vehicleTypeBox.setItems(FXCollections.observableArrayList("Car","Bus","Bike","Truck"));
        brandBox.setItems(FXCollections.observableArrayList(
                "BMW","Mercedes","Audi","Toyota","Volkswagen",
                "Honda","Ford","Hyundai","Kia","Nissan"
        ));
        colourBox.setItems(FXCollections.observableArrayList(
                "Black","White","Silver","Grey","Blue",
                "Red","Green","Brown","Yellow","Orange"
        ));
        wheelsBox.setItems(FXCollections.observableArrayList(2,4,6));
        slotTypeBox.setItems(FXCollections.observableArrayList("VIP","NORMAL"));

        plateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPlateNumber()));
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVehicleType()));
        brandCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBrand()));
        colourCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getColour()));
        wheelscolumn.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getWheels()).asObject());
        slotCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSlotNumber()));
        slotTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSlotType()));
        timeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTimeIn()));

        vehicleTable.setItems(data);
        vehicleTable.setOnMouseClicked(e -> fillFormFromTable());

        loadAvailableSlots();
        loadTable();
    }

    /* ================= TABLE → FORM ================= */
    private void fillFormFromTable() {
        VehicleSearchRow v = vehicleTable.getSelectionModel().getSelectedItem();
        if (v == null) return;

        plateField.setText(v.getPlateNumber());
        vehicleTypeBox.setValue(v.getVehicleType());
        brandBox.setValue(v.getBrand());
        colourBox.setValue(v.getColour());
        wheelsBox.setValue(v.getWheels());
        slotNumberBox.setValue(v.getSlotNumber());
        slotTypeBox.setValue(v.getSlotType());
    }

    /* ================= ADD ================= */
    @FXML
    private void onAdd() {

        if (plateField.getText().isBlank()) {
            showAlert("Plate number is required.");
            return;
        }

        DatabaseConnection db = new DatabaseConnection();

        try (Connection conn = db.getConnection()) {

            // 🔹 Check duplicate plate
            PreparedStatement checkStmt =
                    conn.prepareStatement("SELECT 1 FROM vehicles WHERE plate_number=?");
            checkStmt.setString(1, plateField.getText());
            ResultSet plateRs = checkStmt.executeQuery();

            if (plateRs.next()) {
                showAlert("This plate number already exists.");
                return;
            }

            // 🔹 Insert vehicle
            PreparedStatement vStmt = conn.prepareStatement(
                    "INSERT INTO vehicles (plate_number, vehicle_type, brand, colour, wheels) VALUES (?,?,?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            vStmt.setString(1, plateField.getText());
            vStmt.setString(2, vehicleTypeBox.getValue());
            vStmt.setString(3, brandBox.getValue());
            vStmt.setString(4, colourBox.getValue());
            vStmt.setInt(5, wheelsBox.getValue());
            vStmt.executeUpdate();

            ResultSet keys = vStmt.getGeneratedKeys();
            keys.next();
            int vehicleId = keys.getInt(1);

            // 🔹 Find slot
            PreparedStatement slotStmt = conn.prepareStatement(
                    "SELECT slot_id FROM parking_slots WHERE slot_number=? AND slot_type=? AND status='FREE'"
            );
            slotStmt.setString(1, slotNumberBox.getValue());
            slotStmt.setString(2, slotTypeBox.getValue());
            ResultSet slotRs = slotStmt.executeQuery();

            if (!slotRs.next()) {
                showAlert("Slot not available.");
                return;
            }

            int slotId = slotRs.getInt("slot_id");

            // 🔹 Parking session
            PreparedStatement psStmt = conn.prepareStatement(
                    "INSERT INTO parking_sessions (vehicle_id, slot_id, time_in) VALUES (?,?,?)"
            );
            psStmt.setInt(1, vehicleId);
            psStmt.setInt(2, slotId);
            psStmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            psStmt.executeUpdate();

            conn.createStatement().executeUpdate(
                    "UPDATE parking_slots SET status='OCCUPIED' WHERE slot_id=" + slotId
            );

            onRefresh();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= UPDATE ================= */
    @FXML
    private void onUpdate() {

        VehicleSearchRow v = vehicleTable.getSelectionModel().getSelectedItem();
        if (v == null) {
            showAlert("Select a vehicle first.");
            return;
        }

        DatabaseConnection db = new DatabaseConnection();

        try (Connection conn = db.getConnection()) {

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE vehicles SET vehicle_type=?, brand=?, colour=?, wheels=? WHERE plate_number=?"
            );
            stmt.setString(1, vehicleTypeBox.getValue());
            stmt.setString(2, brandBox.getValue());
            stmt.setString(3, colourBox.getValue());
            stmt.setInt(4, wheelsBox.getValue());
            stmt.setString(5, v.getPlateNumber());

            stmt.executeUpdate();

            onRefresh();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= DELETE ================= */
    @FXML
    private void onDelete() {

        VehicleSearchRow v = vehicleTable.getSelectionModel().getSelectedItem();
        if (v == null) {
            showAlert("Select a vehicle first.");
            return;
        }

        DatabaseConnection db = new DatabaseConnection();

        try (Connection conn = db.getConnection()) {

            // 1️⃣ Get active session + slot
            PreparedStatement findStmt = conn.prepareStatement("""
            SELECT s.session_id, s.slot_id
            FROM parking_sessions s
            JOIN vehicles v ON s.vehicle_id = v.vehicle_id
            WHERE v.plate_number = ? AND s.time_out IS NULL
        """);
            findStmt.setString(1, v.getPlateNumber());

            ResultSet rs = findStmt.executeQuery();
            if (!rs.next()) {
                showAlert("Active parking session not found.");
                return;
            }

            int sessionId = rs.getInt("session_id");
            int slotId = rs.getInt("slot_id");

            // 2️⃣ Close parking session
            PreparedStatement closeSession = conn.prepareStatement(
                    "UPDATE parking_sessions SET time_out = NOW() WHERE session_id = ?"
            );
            closeSession.setInt(1, sessionId);
            closeSession.executeUpdate();

            // 3️⃣ Free the slot
            PreparedStatement freeSlot = conn.prepareStatement(
                    "UPDATE parking_slots SET status = 'FREE' WHERE slot_id = ?"
            );
            freeSlot.setInt(1, slotId);
            freeSlot.executeUpdate();

            // 4️⃣ Refresh UI
            onRefresh();

            showAlert("Vehicle checked out successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error while deleting vehicle.");
        }
    }


    /* ================= SEARCH ================= */
    @FXML
    private void onSearch() {
        data.removeIf(r -> !r.getPlateNumber().contains(plateField.getText()));
    }

    /* ================= REFRESH ================= */
    @FXML
    private void onRefresh() {
        onClear();
        loadAvailableSlots();
        loadTable();
    }

    /* ================= CLEAR ================= */
    @FXML
    private void onClear() {
        plateField.clear();
        vehicleTypeBox.setValue(null);
        brandBox.setValue(null);
        colourBox.setValue(null);
        wheelsBox.setValue(null);
        slotNumberBox.setValue(null);
        slotTypeBox.setValue(null);
    }

    /* ================= BACK ================= */
    @FXML
    private void onBack(javafx.event.ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getResource("/com/example/demo/StaffMainPage.fxml")
        );
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /* ================= LOADERS ================= */
    private void loadAvailableSlots() {
        slotNumberBox.getItems().clear();
        DatabaseConnection db = new DatabaseConnection();

        try (Connection conn = db.getConnection();
             ResultSet rs = conn.createStatement()
                     .executeQuery("SELECT slot_number FROM parking_slots WHERE status='FREE'")) {

            while (rs.next()) {
                slotNumberBox.getItems().add(rs.getString("slot_number"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTable() {
        data.clear();
        DatabaseConnection db = new DatabaseConnection();

        try (Connection conn = db.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("""
                SELECT v.plate_number, v.vehicle_type, v.brand, v.colour, v.wheels,
                       ps.slot_number, ps.slot_type, s.time_in
                FROM parking_sessions s
                JOIN vehicles v ON s.vehicle_id=v.vehicle_id
                JOIN parking_slots ps ON s.slot_id=ps.slot_id
                WHERE s.time_out IS NULL
             """)) {

            while (rs.next()) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
