package PROJ;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.*;

public class productform {

    public static void addProduct() {
        // Create form for adding records to the specified table
        GridPane addRecordForm = new GridPane();
        addRecordForm.setHgap(10);
        addRecordForm.setVgap(10);

        // Add form fields for "name", "description", "price", and dropdown menu for category selection
        TextField nameField = new TextField();
        TextField descriptionField = new TextField();
        TextField priceField = new TextField();
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

        // Add labels and fields to the form
        addRecordForm.addRow(0, new Label("Name:"), nameField);
        addRecordForm.addRow(1, new Label("Description:"), descriptionField);
        addRecordForm.addRow(2, new Label("Price:"), priceField);
        addRecordForm.addRow(3, new Label("Category:"), categoryComboBox);

        // Add submit button
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            // Get values from fields
            String name = nameField.getText();
            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());
            String selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
            // Insert values into the specified table
            insertRecord(name, description, price, selectedCategory);
            ((Stage) submitButton.getScene().getWindow()).close();
        });
        addRecordForm.add(submitButton, 1, 4); // Adjust the row as needed

        // Create a new scene for the form
        Scene addRecordScene = new Scene(addRecordForm, 400, 300);
        Stage addRecordStage = new Stage();
        addRecordStage.setScene(addRecordScene);
        addRecordStage.setTitle("Add Product");
        addRecordStage.show();
    }

    public static void modifyProduct() {
        // Create form for modifying records in the specified table
        GridPane modifyRecordForm = new GridPane();
        modifyRecordForm.setHgap(10);
        modifyRecordForm.setVgap(10);

        // Add dropdown menu to choose product to modify
        ComboBox<String> productComboBox = new ComboBox<>();
        // Fetch product names from the database and populate the dropdown menu
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/jdbc", "postgres", "postgres");
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM product")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String productName = resultSet.getString("name");
                productComboBox.getItems().add(productName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        modifyRecordForm.addRow(0, new Label("Choose Product:"), productComboBox);

        // Add form fields for "name", "description", "price", and dropdown menu for category selection
        TextField nameField = new TextField();
        TextField descriptionField = new TextField();
        TextField priceField = new TextField();
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

        // Add labels and fields to the form
        modifyRecordForm.addRow(1, new Label("Name:"), nameField);
        modifyRecordForm.addRow(2, new Label("Description:"), descriptionField);
        modifyRecordForm.addRow(3, new Label("Price:"), priceField);
        modifyRecordForm.addRow(4, new Label("Category:"), categoryComboBox);

        // Add submit button
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            // Get selected product and new values from fields
            String selectedProduct = productComboBox.getSelectionModel().getSelectedItem();
            String newName = nameField.getText();
            String newDescription = descriptionField.getText();
            double newPrice = Double.parseDouble(priceField.getText());
            String newCategory = categoryComboBox.getSelectionModel().getSelectedItem();
            // Update record in the specified table
            updateRecord(selectedProduct, newName, newDescription, newPrice, newCategory);
            ((Stage) submitButton.getScene().getWindow()).close();
        });
        modifyRecordForm.add(submitButton, 1, 5); // Adjust the row as needed

        // Create a new scene for the form
        Scene modifyRecordScene = new Scene(modifyRecordForm, 400, 300);
        Stage modifyRecordStage = new Stage();
        modifyRecordStage.setScene(modifyRecordScene);
        modifyRecordStage.setTitle("Modify Product");
        modifyRecordStage.show();
    }

    private static void insertRecord(String name, String description, double price, String category) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/jdbc", "postgres", "postgres");
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO product (name, description, price, category_id) VALUES (?, ?, ?, (SELECT category_id FROM category WHERE name = ?))")) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, price);
            preparedStatement.setString(4, category);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateRecord(String productName, String newName, String newDescription, double newPrice, String newCategory) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/jdbc", "postgres", "postgres");
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE product SET name = ?, description = ?, price = ?, category_id = (SELECT category_id FROM category WHERE name = ?) WHERE name = ?")) {

            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newDescription);
            preparedStatement.setDouble(3, newPrice);
            preparedStatement.setString(4, newCategory);
            preparedStatement.setString(5, productName);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}