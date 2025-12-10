package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

import java.sql.Connection;
import java.sql.ResultSet;

import java.sql.Statement;

public class HelloController {
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



