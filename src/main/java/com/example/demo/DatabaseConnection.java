package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;


public class DatabaseConnection {
    public Connection databaseLink;

    public Connection getConnection() {
        String databaseName = "car_park_system";
        String databaseUser = "root";
        String databasePassword = "Gisma2024.";
        String databaseURL = "jdbc:mysql://localhost:3306/"+databaseName;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink=DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return databaseLink;
    }

}

