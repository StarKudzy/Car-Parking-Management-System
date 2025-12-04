module com.example.car_parking_management_system {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.car_parking_management_system to javafx.fxml;
    exports com.example.car_parking_management_system;
}