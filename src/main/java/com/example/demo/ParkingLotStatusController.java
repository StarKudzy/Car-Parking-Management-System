package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

public class ParkingLotStatusController {

    // TABLE
    @FXML
    private TableView<ParkingSlot> tableviewid;

    @FXML
    private TableColumn<ParkingSlot, Integer> slotidcolumn;

    @FXML
    private TableColumn<ParkingSlot, String> slotnumbercolumn;

    @FXML
    private TableColumn<ParkingSlot, String> slottypecolumn;

    @FXML
    private TableColumn<ParkingSlot, String> statuscolumn;

    @FXML
    private TableColumn<ParkingSlot, String> platenumbercolumn;

    @FXML
    private TableColumn<ParkingSlot, LocalDateTime> timeincolumn;

    @FXML
    public void initialize() {
        slotidcolumn.setCellValueFactory(new PropertyValueFactory<>("slotId"));
        slotnumbercolumn.setCellValueFactory(new PropertyValueFactory<>("slotNumber"));
        slottypecolumn.setCellValueFactory(new PropertyValueFactory<>("slotType"));
        statuscolumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        platenumbercolumn.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));
        timeincolumn.setCellValueFactory(new PropertyValueFactory<>("timeIn"));

        loadParkingData();
    }

    private void loadParkingData() {
        ObservableList<ParkingSlot> list = FXCollections.observableArrayList();

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT * FROM parkinglot_status";

        try (Statement st = connectDB.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("time_in");
                LocalDateTime timeIn = (ts != null) ? ts.toLocalDateTime() : null;

                ParkingSlot slot = new ParkingSlot(
                        rs.getInt("slot_id"),
                        rs.getString("slot_number"),
                        rs.getString("slot_type"),
                        rs.getString("status"),
                        rs.getString("plate_number"),
                        timeIn
                );

                list.add(slot);
            }

            tableviewid.setItems(list);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // REFRESH BUTTON
    @FXML
    private void onRefreshClick() {
        loadParkingData();
    }

    // BACK BUTTON
    @FXML
    private void onBackClick(javafx.event.ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
                getClass().getResource("/com/example/demo/AdminMainPage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
