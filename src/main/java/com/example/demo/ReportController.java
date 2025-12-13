package com.example.demo;

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
import java.time.LocalDate;

public class ReportController {


    @FXML private ComboBox<String> reportTypeBox;
    @FXML private DatePicker reportDatePicker;

    @FXML private TableView<ReportRow> reportTable;
    @FXML private TableColumn<ReportRow, String> periodCol;
    @FXML private TableColumn<ReportRow, Integer> totalVehiclesCol;
    @FXML private TableColumn<ReportRow, Integer> vipCountCol;
    @FXML private TableColumn<ReportRow, Integer> normalCountCol;

    private final ObservableList<ReportRow> data =
            FXCollections.observableArrayList();


    @FXML
    public void initialize() {

        reportTypeBox.setItems(FXCollections.observableArrayList(
                "Daily", "Weekly", "Yearly"
        ));

        periodCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getPeriod()));

        totalVehiclesCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(
                        c.getValue().getTotalVehicles()).asObject());

        vipCountCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(
                        c.getValue().getVipCount()).asObject());

        normalCountCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(
                        c.getValue().getNormalCount()).asObject());

        reportTable.setItems(data);
    }


    @FXML
    private void onGenerateReport() {

        if (reportTypeBox.getValue() == null || reportDatePicker.getValue() == null) {
            showAlert("Please select report type and date.");
            return;
        }

        data.clear();

        String type = reportTypeBox.getValue();
        LocalDate date = reportDatePicker.getValue();

        switch (type) {
            case "Daily" -> loadDailyReport(date);
            case "Weekly" -> loadWeeklyReport(date);
            case "Yearly" -> loadYearlyReport(date);
        }
    }


    private void loadDailyReport(LocalDate date) {

        String sql = """
            SELECT 
              COUNT(*) AS total,
              SUM(ps.slot_type='VIP') AS vip,
              SUM(ps.slot_type='NORMAL') AS normal
            FROM parking_sessions s
            JOIN parking_slots ps ON s.slot_id = ps.slot_id
            WHERE DATE(s.time_in) = ?
        """;

        executeReport(sql, date.toString(), "Date: " + date);
    }


    private void loadWeeklyReport(LocalDate date) {

        String sql = """
            SELECT 
              COUNT(*) AS total,
              SUM(ps.slot_type='VIP') AS vip,
              SUM(ps.slot_type='NORMAL') AS normal
            FROM parking_sessions s
            JOIN parking_slots ps ON s.slot_id = ps.slot_id
            WHERE YEARWEEK(s.time_in, 1) = YEARWEEK(?, 1)
        """;

        executeReport(sql, date.toString(), "Week of " + date);
    }


    private void loadYearlyReport(LocalDate date) {

        String sql = """
            SELECT 
              COUNT(*) AS total,
              SUM(ps.slot_type='VIP') AS vip,
              SUM(ps.slot_type='NORMAL') AS normal
            FROM parking_sessions s
            JOIN parking_slots ps ON s.slot_id = ps.slot_id
            WHERE YEAR(s.time_in) = YEAR(?)
        """;

        executeReport(sql, date.toString(), "Year " + date.getYear());
    }


    private void executeReport(String sql, String param, String periodLabel) {

        DatabaseConnection db = new DatabaseConnection();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, param);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                data.add(new ReportRow(
                        periodLabel,
                        rs.getInt("total"),
                        rs.getInt("vip"),
                        rs.getInt("normal")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error generating report.");
        }
    }


    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
    @FXML
    private void onBack(javafx.event.ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getResource("/com/example/demo/AdminMainPage.fxml")
        );
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
