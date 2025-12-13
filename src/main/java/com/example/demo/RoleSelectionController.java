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

    @FXML
    private void onAdminClick(ActionEvent event) throws IOException {
        switchScene(event, "/com/example/demo/AdminLogin.fxml");
    }

    @FXML
    private void onStaffClick(ActionEvent event) throws IOException {
        switchScene(event, "/com/example/demo/StaffLogin.fxml");
    }

    //cancel button
    @FXML
    private void onCancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();
        stage.close();   // clean exit
    }


    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(new Scene(root));
        stage.show();
    }
}
