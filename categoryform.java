package PROJ;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.*;

public class categoryform {

    public static void addcategory() {
        // Create form for adding records to the specified table
        GridPane addRecordForm = new GridPane();
        addRecordForm.setHgap(10);
        addRecordForm.setVgap(10);

        // Add form fields for "name" and "description"
        TextField nameField = new TextField();
        TextField descriptionField = new TextField();

        // Add labels and fields to the form
        addRecordForm.addRow(0, new Label("Name:"), nameField);
        addRecordForm.addRow(1, new Label("Description:"), descriptionField);

        // Add submit button
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            // Get values from fields
            String name = nameField.getText();
            String description = descriptionField.getText();
            // Insert values into the specified table
            insertRecord(name, description);
            ((Stage) submitButton.getScene().getWindow()).close();
        });
        addRecordForm.add(submitButton, 1, 2); // Adjust the row as needed

        // Create a new scene for the form
        Scene addRecordScene = new Scene(addRecordForm, 400, 300);
        Stage addRecordStage = new Stage();
        addRecordStage.setScene(addRecordScene);
        addRecordStage.setTitle("Add Record");
        addRecordStage.show();
    }

    public static void modifyCategory() {
        // Create form for modifying records in the specified table
        GridPane modifyRecordForm = new GridPane();
        modifyRecordForm.setHgap(10);
        modifyRecordForm.setVgap(10);

        // Add dropdown menu to choose category to modify
        ComboBox<String> categoryComboBox = new ComboBox<>();
        // Fetch category names from the database and populate the dropdown menu
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/jdbc", "postgres", "postgres");
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM category")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String categoryName = resultSet.getString("name");
                categoryComboBox.getItems().add(categoryName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        modifyRecordForm.addRow(0, new Label("Choose Category:"), categoryComboBox);

        // Add form fields for "name" and "description"
        TextField nameField = new TextField();
        TextField descriptionField = new TextField();

        // Add labels and fields to the form
        modifyRecordForm.addRow(1, new Label("Name:"), nameField);
        modifyRecordForm.addRow(2, new Label("Description:"), descriptionField);

        // Add submit button
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            // Get selected category and new values from fields
            String selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
            String newName = nameField.getText();
            String newDescription = descriptionField.getText();
            updateRecord(selectedCategory, newName, newDescription);
            ((Stage) submitButton.getScene().getWindow()).close();
        });
        modifyRecordForm.add(submitButton, 1, 3); // Adjust the row as needed

        // Create a new scene for the form
        Scene modifyRecordScene = new Scene(modifyRecordForm, 400, 300);
        Stage modifyRecordStage = new Stage();
        modifyRecordStage.setScene(modifyRecordScene);
        modifyRecordStage.setTitle("Modify Record");
        modifyRecordStage.show();
    }

    private static void insertRecord(String name, String description) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/jdbc", "postgres", "postgres");
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO category (name, description) VALUES (?, ?)")) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateRecord(String categoryName, String newName, String newDescription) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/jdbc", "postgres", "postgres");
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE category SET name = ?, description = ? WHERE name = ?")) {

            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newDescription);
            preparedStatement.setString(3, categoryName);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}