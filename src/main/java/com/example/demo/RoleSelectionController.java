package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RoleSelectionController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private void onAdminClick(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/com/example/demo/AdminLogin.fxml")); // your admin login page
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void onStaffClick(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("staff-login.fxml")); // your staff login page
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
