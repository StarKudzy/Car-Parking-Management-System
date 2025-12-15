
## Project Overview:
The Car Park Management System (CPMS) is a desktop application built using JavaFX and MySQL.It allows staff to register vehicles, assign parking slots, and manage check-in/check-out, while administrators can search records and generate reports (daily, weekly, yearly).
The system is designed as a free parking system, focusing on simplicity, reliability, and clean database relationships.
The system replaces manual parking records with an automated solution that ensures accurate tracking of vehicles, parking slots and usage statistics in real time.
The parking system is intentionally designed as a frees parking system in order to focus on core operational logic such as vehicle registration, slot allocation , reporting and as well quick retrival of specific vehicle information from the system. The system supports two distinct user roles : staff and Admin, each with separate responsibilities. Staff members are responsible for day to day parking operations including vehicle check-in, parkkng slot allocation, record updates and vehicle check-out.On the other hand, Administrators focus on monitoring and analysis, with access to vehicle searches, parking status overviews and statistical reports.

## User Roles Overview
- Staff
  - Register vehicles
  - Assign parking slots
  - Update Vehicle Information
  - Checkout Vehicles 
- Admin 
  - Search vehicles
  - View parking status
  - edit information about staff and admin
  - Generate daily, weekly, and yearly reports

## Application Pages and Workflow
- Below is a step-by-step explanation of each page in the system.

## 1. Role Selection Page:
Allows the user to choose whether they are logging in as ADMIN or STAFF.

## Role based logic
- This page acts as the entry point to the system. Based on the selected role, the user is directed to the appropriate login page. This ensures role separation from the start of the application.The cancel button exits the system.

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


The image below shows the User role Page.
![img.png](img.png)


## 2. Staff Login Page
- Authenticates staff members before granting access to parking management features.

## Staff login Logic
- Allows user to access the system provided they have entered credentials similar to the ones in the database.

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

Below is the user accounts database with login details for both Admins and Staff. Both Admins and Staff members are allowed to access the Staff Login Page with their login details.


![img_2.png](img_2.png)

Below is the Staff Loin Page:


![img_1.png](img_1.png)
Staff members enter their credentials to access the system. Upon successful authentication, they are redirected to the Staff Vehicle management Page.

- Page switch Logic

@FXML
private void onLoginClick(ActionEvent event) throws IOException {
Parent root = FXMLLoader.load(getClass().getResource("/com/example/demo/StaffVehicleManagement.fxml"));
Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
Scene scene = new Scene(root);
stage.setScene(scene);
stage.sizeToScene();
stage.show();
}



Staff Vehicle management Page
Admin Login Page
Admin MAIN Page
Parking Lot Status Page
Vehicle Search Page
Manage Users Page
Reports Page




