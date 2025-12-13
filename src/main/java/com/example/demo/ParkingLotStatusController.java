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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ParkingLotStatusController {



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
    public void initialize() {
        slotidcolumn.setCellValueFactory(new PropertyValueFactory<>("slotId"));
        slotnumbercolumn.setCellValueFactory(new PropertyValueFactory<>("slotNumber"));
        slottypecolumn.setCellValueFactory(new PropertyValueFactory<>("slotType"));
        statuscolumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadParkingData();
    }



    private void loadParkingData() {

        ObservableList<ParkingSlot> list = FXCollections.observableArrayList();
        DatabaseConnection connectNow = new DatabaseConnection();

        String query = "SELECT * FROM parking_slots";

        try (Connection connectDB = connectNow.getConnection();
             Statement st = connectDB.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                ParkingSlot slot = new ParkingSlot(
                        rs.getInt("slot_id"),
                        rs.getString("slot_number"),
                        rs.getString("slot_type"),
                        rs.getString("status")
                );
                list.add(slot);
            }

            tableviewid.setItems(list);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void onRefreshClick() {
        loadParkingData();
    }



    @FXML
    private void onBackClick(javafx.event.ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
                getClass().getResource("/com/example/demo/AdminMainPage.fxml")
        );
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }



    @FXML
    private void onSearchClick(javafx.event.ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
                getClass().getResource("/com/example/demo/VehicleSearch.fxml")
        );
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
