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
import java.sql.ResultSet;

import java.sql.Statement;

public class AdminLoginController {
    @FXML
    private Label loginlabel;
    @FXML
    private TextField usernametextfield;
    @FXML
    private PasswordField passwordfield;
    @FXML
    private Button cancelbutton;


    public void loginMessage(ActionEvent e)  {

        if (!usernametextfield.getText().isBlank() && !passwordfield.getText().isBlank()) {
           // loginlabel.setText("You try to log in! ");

            validateLogin();

        } else {
            loginlabel.setText("Please enter Username and Password ");
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

    public void validateLogin()  {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT count(1) FROM useraccounts WHERE Username ='" + usernametextfield.getText() + "' AND Password = '" + passwordfield.getText() + "'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            while (queryResult.next()) {
                if (queryResult.getInt(1) == 1) {
                    loginlabel.setText("welcome ");

                } else {
                    loginlabel.setText("Wrong login Information. Please try again! ");
                }
            }

            }catch(Exception e){
                e.printStackTrace();
            }


        }

    }



