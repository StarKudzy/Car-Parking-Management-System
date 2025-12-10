package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StaffLoginController {

    @FXML
    private Label loginlabel;
    @FXML
    private TextField usernametextfield;
    @FXML
    private PasswordField passwordfield;
    @FXML
    private Button cancelbutton;

    public void loginMessage(ActionEvent e)  {
        System.out.println("Login button clicked!");

        if (!usernametextfield.getText().isBlank() && !passwordfield.getText().isBlank()) {
            validateLogin();
        } else {
            loginlabel.setText("Please enter Username and Password");
        }
    }

    @FXML
    private void onBackClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/demo/role-selection.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    public void cancelButtonClick(ActionEvent e) {
        Stage stage = (Stage) cancelbutton.getScene().getWindow();
        stage.close();
    }

    public void validateLogin() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String sql = "SELECT Role FROM useraccounts WHERE Username = ? AND Password = ?";

        try {
            PreparedStatement ps = connectDB.prepareStatement(sql);
            ps.setString(1, usernametextfield.getText());
            ps.setString(2, passwordfield.getText());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("Role");

                // ✔ Admin and Staff both allowed
                if (role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("Staff")) {

                    loginlabel.setText("Login Successful!");

                    // TODO: Load staff dashboard here

                } else {
                    loginlabel.setText("ACCESS DENIED.");
                }

            } else {
                loginlabel.setText("Invalid username or password.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
