package PROJ;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.*;

public class customerform {

    public static void addCustomer() {
        // Create form for adding records to the specified table
        GridPane addRecordForm = new GridPane();
        addRecordForm.setHgap(10);
        addRecordForm.setVgap(10);

        // Add form fields for "email", "address", "postal code", "password", and dropdown menu for category selection
        TextField emailField = new TextField();
        TextField addressField = new TextField();
        TextField postalCodeField = new TextField();
        PasswordField passwordField = new PasswordField();

        // Add labels and fields to the form
        addRecordForm.addRow(0, new Label("Email:"), emailField);
        addRecordForm.addRow(1, new Label("Address:"), addressField);
        addRecordForm.addRow(2, new Label("Postal Code:"), postalCodeField);
        addRecordForm.addRow(3, new Label("Password:"), passwordField);

        // Add submit button
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            // Get values from fields
            String email = emailField.getText();
            String address = addressField.getText();
            String postalCode = postalCodeField.getText();
            String password = passwordField.getText();
            // Insert values into the specified table
            insertRecord(email, address, postalCode, password);
            ((Stage) submitButton.getScene().getWindow()).close();
        });
        addRecordForm.add(submitButton, 1, 4); // Adjust the row as needed

        // Create a new scene for the form
        Scene addRecordScene = new Scene(addRecordForm, 400, 300);
        Stage addRecordStage = new Stage();
        addRecordStage.setScene(addRecordScene);
        addRecordStage.setTitle("Add Customer");
        addRecordStage.show();
    }

    public static void modifyCustomer() {
        // Create form for modifying records in the specified table
        GridPane modifyRecordForm = new GridPane();
        modifyRecordForm.setHgap(10);
        modifyRecordForm.setVgap(10);

        // Add dropdown menu to choose customer to modify
        ComboBox<String> customerComboBox = new ComboBox<>();
        // Fetch customer emails from the database and populate the dropdown menu
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/jdbc", "postgres", "postgres");
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT email FROM customer")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String customerEmail = resultSet.getString("email");
                customerComboBox.getItems().add(customerEmail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        modifyRecordForm.addRow(0, new Label("Choose Customer:"), customerComboBox);

        // Add form fields for "email", "address", "postal code", and "password"
        TextField emailField = new TextField();
        TextField addressField = new TextField();
        TextField postalCodeField = new TextField();
        PasswordField passwordField = new PasswordField();

        // Add labels and fields to the form
        modifyRecordForm.addRow(1, new Label("Email:"), emailField);
        modifyRecordForm.addRow(2, new Label("Address:"), addressField);
        modifyRecordForm.addRow(3, new Label("Postal Code:"), postalCodeField);
        modifyRecordForm.addRow(4, new Label("Password:"), passwordField);

        // Add submit button
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            // Get selected customer and new values from fields
            String selectedCustomer = customerComboBox.getSelectionModel().getSelectedItem();
            String newEmail = emailField.getText();
            String newAddress = addressField.getText();
            String newPostalCode = postalCodeField.getText();
            String newPassword = passwordField.getText();
            // Update record in the specified table
            updateRecord(selectedCustomer, newEmail, newAddress, newPostalCode, newPassword);
            ((Stage) submitButton.getScene().getWindow()).close();
        });
        modifyRecordForm.add(submitButton, 1, 5); // Adjust the row as needed

        // Create a new scene for the form
        Scene modifyRecordScene = new Scene(modifyRecordForm, 400, 300);
        Stage modifyRecordStage = new Stage();
        modifyRecordStage.setScene(modifyRecordScene);
        modifyRecordStage.setTitle("Modify Customer");
        modifyRecordStage.show();
    }

    public static void removeCustomer() {
        // Create form for removing records from the specified table
        GridPane removeRecordForm = new GridPane();
        removeRecordForm.setHgap(10);
        removeRecordForm.setVgap(10);

        // Add dropdown menu to choose customer to remove
        ComboBox<String> customerComboBox = new ComboBox<>();
        // Fetch customer emails from the database and populate the dropdown menu
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/jdbc", "postgres", "postgres");
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT email FROM customer")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String customerEmail = resultSet.getString("email");
                customerComboBox.getItems().add(customerEmail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        removeRecordForm.addRow(0, new Label("Choose Customer:"), customerComboBox);

        // Add submit button
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            // Get selected customer from dropdown
            String selectedCustomer = customerComboBox.getSelectionModel().getSelectedItem();
            // Remove record from the specified table
            removeRecord(selectedCustomer);
            ((Stage) submitButton.getScene().getWindow()).close();
        });
        removeRecordForm.add(submitButton, 1, 1); // Adjust the row as needed

        // Create a new scene for the form
        Scene removeRecordScene = new Scene(removeRecordForm, 400, 200);
        Stage removeRecordStage = new Stage();
        removeRecordStage.setScene(removeRecordScene);
        removeRecordStage.setTitle("Remove Customer");
        removeRecordStage.show();
    }

    private static void insertRecord(String email, String address, String postalCode, String password) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/jdbc", "postgres", "postgres");
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO customer (email, address, postal_code, password) VALUES (?, ?, ?, ?)")) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, postalCode);
            preparedStatement.setString(4, password);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateRecord(String customerEmail, String newEmail, String newAddress, String newPostalCode, String newPassword) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/jdbc", "postgres", "postgres");
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE customer SET email = ?, address = ?, postal_code = ?, password = ? WHERE email = ?")) {

            preparedStatement.setString(1, newEmail);
            preparedStatement.setString(2, newAddress);
            preparedStatement.setString(3, newPostalCode);
            preparedStatement.setString(4, newPassword);
            preparedStatement.setString(5, customerEmail);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void removeRecord(String customerEmail) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/jdbc", "postgres", "postgres");
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM customer WHERE email = ?")) {

            preparedStatement.setString(1, customerEmail);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


