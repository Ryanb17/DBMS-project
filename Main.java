package PROJ;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class Main extends Application {

    private String uri = "jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:5432/postgres";
    private String user = "postgres.obqjedgldrlrxybzfkso";
    private String password = "stlLwPfrIYDIIPhh";

    private TableView<ObservableList<String>> tableView;
    private ComboBox<String> addRecordTableComboBox;
    private ComboBox<String> modifyRecordTableComboBox;
    private ComboBox<String> removeRecordTableComboBox;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Tab displayTab = new Tab("Display", createTabContent("Display"));
        displayTab.setClosable(false);

        // Create a tab for adding records
        Tab addRecordTab = new Tab("Add Record", createTabContent("Add Record"));
        addRecordTab.setClosable(false);

        // Create a tab for modifying records
        Tab modifyRecordTab = new Tab("Modify Record", createTabContent("Modify Record"));
        modifyRecordTab.setClosable(false);

        // Create a tab for removing records
        Tab removeRecordTab = new Tab("Remove Record", createTabContent("Remove Record"));
        removeRecordTab.setClosable(false);


        TabPane tabPane = new TabPane(displayTab, addRecordTab, modifyRecordTab, removeRecordTab);

        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX PostgreSQL Table Viewer");
        primaryStage.show();
    }

    private VBox createTabContent(String action) {
        if (action.equals("Display")) {
            ComboBox<String> tableComboBox = new ComboBox<>();
            tableView = new TableView<>();

            VBox tabContent = new VBox();
            tabContent.getChildren().addAll(tableComboBox, tableView);

            try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/jdbc", "postgres", "postgres")) {
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet tablesResultSet = metaData.getTables(null, null, null, new String[]{"TABLE"});

                ObservableList<String> tableNames = FXCollections.observableArrayList();
                while (tablesResultSet.next()) {
                    String tableName = tablesResultSet.getString("TABLE_NAME");
                    tableNames.add(tableName);
                }

                tableComboBox.setItems(tableNames);

                tableComboBox.setOnAction(event -> {
                    String selectedTableName = tableComboBox.getSelectionModel().getSelectedItem();
                    if (selectedTableName != null && action.equals("Display")) {
                        displayTableContents(selectedTableName);
                    }
                });

                if (!tableNames.isEmpty()) {
                    displayTableContents(tableNames.get(0));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return tabContent;
        } else if (action.equals("Add Record")) {
            addRecordTableComboBox = new ComboBox<>();
            Button addButton = new Button("Add Record");

            VBox tabContent = new VBox();
            tabContent.getChildren().addAll(addRecordTableComboBox, addButton);

            try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/jdbc", "postgres", "postgres")) {
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet tablesResultSet = metaData.getTables(null, null, null, new String[]{"TABLE"});

                ObservableList<String> tableNames = FXCollections.observableArrayList();
                while (tablesResultSet.next()) {
                    String tableName = tablesResultSet.getString("TABLE_NAME");
                    tableNames.add(tableName);
                }

                addRecordTableComboBox.setItems(tableNames);

                addButton.setOnAction(event -> {
                    String selectedTableName = addRecordTableComboBox.getSelectionModel().getSelectedItem();
                    if (selectedTableName != null) {
                        openAddRecordForm(selectedTableName);
                    }
                });

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return tabContent;
        } else if (action.equals("Modify Record")) {
            modifyRecordTableComboBox = new ComboBox<>();
            Button modifyButton = new Button("Modify Record");

            VBox tabContent = new VBox();
            tabContent.getChildren().addAll(modifyRecordTableComboBox, modifyButton);

            try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/jdbc", "postgres", "postgres")) {
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet tablesResultSet = metaData.getTables(null, null, null, new String[]{"TABLE"});

                ObservableList<String> tableNames = FXCollections.observableArrayList();
                while (tablesResultSet.next()) {
                    String tableName = tablesResultSet.getString("TABLE_NAME");
                    tableNames.add(tableName);
                }

                modifyRecordTableComboBox.setItems(tableNames);

                modifyButton.setOnAction(event -> {
                    String selectedTableName = modifyRecordTableComboBox.getSelectionModel().getSelectedItem();
                    if (selectedTableName != null) {
                        openModifyRecordForm(selectedTableName);
                    }
                });

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return tabContent;
        } else if (action.equals("Remove Record")) {
            removeRecordTableComboBox = new ComboBox<>();
            Button removeButton = new Button("Remove Record");

            VBox tabContent = new VBox();
            tabContent.getChildren().addAll(removeRecordTableComboBox, removeButton);

            try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/jdbc", "postgres", "postgres")) {
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet tablesResultSet = metaData.getTables(null, null, null, new String[]{"TABLE"});

                ObservableList<String> tableNames = FXCollections.observableArrayList();
                while (tablesResultSet.next()) {
                    String tableName = tablesResultSet.getString("TABLE_NAME");
                    tableNames.add(tableName);
                }

                removeRecordTableComboBox.setItems(tableNames);

                removeButton.setOnAction(event -> {
                    String selectedTableName = removeRecordTableComboBox.getSelectionModel().getSelectedItem();
                    if (selectedTableName != null) {
                        openRemoveRecordForm(selectedTableName);
                    }
                });

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return tabContent;
        }
        return null;
    }

    private void displayTableContents(String tableName) {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/jdbc", "postgres", "postgres")) {

            String query = "SELECT * FROM " + tableName;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Clear existing table data
            tableView.getColumns().clear();
            tableView.getItems().clear();

            // Create columns dynamically based on ResultSet metadata
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                final int index = i - 1;
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(metaData.getColumnName(i));
                column.setCellValueFactory(param -> {
                    if (param.getValue().size() > index) {
                        return new SimpleStringProperty(param.getValue().get(index));
                    } else {
                        return new SimpleStringProperty("");
                    }
                });
                tableView.getColumns().add(column);
            }

            // Populate table data
            while (resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getString(i));
                }
                tableView.getItems().add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void openModifyRecordForm(String tableName) {
        switch (tableName) {
            case "category":
                categoryform.modifyCategory();
                break;
            case "product":
                productform.modifyProduct();
                break;
            case "customer":
                customerform.modifyCustomer();
                break;
            default:
                System.out.println("not a table: " + tableName);
        }
    }
    private void openAddRecordForm(String tableName) {
        switch (tableName) {
            case "category":
                categoryform.addcategory();
                break;
            case "product":
                productform.addProduct();
            case "customer":
                customerform.addCustomer();
                break;

            default:
                System.out.println("not a table: " + tableName);
        }

    }
    private void openRemoveRecordForm(String tableName) {
        switch (tableName) {
            case "category":
                categoryform.addcategory();
                break;
            case "product":
                productform.addProduct();
            case "customer":
                customerform.removeCustomer();
                break;

            default:
                System.out.println("not a table: " + tableName);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}