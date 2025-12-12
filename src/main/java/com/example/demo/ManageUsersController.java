package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class ManageUsersController {

    // TABLE + COLUMNS
    @FXML
    private TableView<Staff> tableviewid;

    @FXML
    private TableColumn<Staff, String> firstnamecolumn;

    @FXML
    private TableColumn<Staff, String> lastnamecolumn;

    @FXML
    private TableColumn<Staff, String> usernamecolumn;

    @FXML
    private TableColumn<Staff, String> rolecolumn;

    @FXML
    private TableColumn<Staff, String> passwordcolumn;

    // TEXTFIELDS
    @FXML
    private TextField firstnametextfield;

    @FXML
    private TextField lastnametextfield;

    @FXML
    private TextField usernametextfield;

    @FXML
    private TextField roletextfield;

    @FXML
    private TextField passwordtextfield;

    // BUTTONS
    @FXML
    private Button addbutton;

    @FXML
    private Button updatebutton;

    @FXML
    private Button deletebutton;

    @FXML
    private Button backbutton;

    @FXML
    private Button exitbutton;


    // INITIALIZE TABLE
    @FXML
    public void initialize() {

        firstnamecolumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastnamecolumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usernamecolumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        rolecolumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        passwordcolumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        loadStaffData();

        // Fill fields when a row is selected
        tableviewid.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        firstnametextfield.setText(newSel.getFirstName());
                        lastnametextfield.setText(newSel.getLastName());
                        usernametextfield.setText(newSel.getUsername());
                        roletextfield.setText(newSel.getRole());
                        passwordtextfield.setText(newSel.getPassword());
                    }
                }
        );
    }


    // LOAD USERS FROM DATABASE
    private void loadStaffData() {
        ObservableList<Staff> list = FXCollections.observableArrayList();

        DatabaseConnection db = new DatabaseConnection();
        Connection con = db.getConnection();

        String query = "SELECT * FROM useraccounts";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Staff s = new Staff(
                        rs.getInt("iduseraccounts"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Username"),
                        rs.getString("Role"),
                        rs.getString("Password")
                );
                list.add(s);
            }

            tableviewid.setItems(list);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ADD USER
    @FXML
    private void onAddButtonClick() {

        if (!validateForm()) return;

        DatabaseConnection db = new DatabaseConnection();
        Connection con = db.getConnection();

        String sql = "INSERT INTO useraccounts (FirstName, LastName, Username, Password, Role) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, firstnametextfield.getText());
            ps.setString(2, lastnametextfield.getText());
            ps.setString(3, usernametextfield.getText());
            ps.setString(4, passwordtextfield.getText());
            ps.setString(5, roletextfield.getText());

            ps.executeUpdate();

            clearForm();
            loadStaffData();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // UPDATE USER
    @FXML
    private void onUpdateButtonClick() {

        Staff selected = tableviewid.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (!validateForm()) return;

        DatabaseConnection db = new DatabaseConnection();
        Connection con = db.getConnection();

        String sql = "UPDATE useraccounts SET FirstName=?, LastName=?, Username=?, Password=?, Role=? WHERE iduseraccounts=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, firstnametextfield.getText());
            ps.setString(2, lastnametextfield.getText());
            ps.setString(3, usernametextfield.getText());
            ps.setString(4, passwordtextfield.getText());
            ps.setString(5, roletextfield.getText());
            ps.setInt(6, selected.getId());

            ps.executeUpdate();

            clearForm();
            loadStaffData();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // DELETE USER
    @FXML
    private void onDeleteButtonClick() {

        Staff selected = tableviewid.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        DatabaseConnection db = new DatabaseConnection();
        Connection con = db.getConnection();

        String sql = "DELETE FROM useraccounts WHERE iduseraccounts=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, selected.getId());
            ps.executeUpdate();

            clearForm();
            loadStaffData();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // BACK TO ADMIN MAIN PAGE
    @FXML
    private void onBackButtonClick(javafx.event.ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/demo/AdminMainPage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }


    // EXIT PAGE
    @FXML
    private void onExitButtonClick() {
        Stage stage = (Stage) exitbutton.getScene().getWindow();
        stage.close();
    }


    // VALIDATION
    private boolean validateForm() {
        return !(firstnametextfield.getText().isBlank()
                || lastnametextfield.getText().isBlank()
                || usernametextfield.getText().isBlank()
                || passwordtextfield.getText().isBlank()
                || roletextfield.getText().isBlank());
    }

    // CLEAR FORM
    private void clearForm() {
        firstnametextfield.clear();
        lastnametextfield.clear();
        usernametextfield.clear();
        passwordtextfield.clear();
        roletextfield.clear();
        tableviewid.getSelectionModel().clearSelection();
    }
}
