package com.zl.librarymanagementsystem;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * LibraryManagementSystem is a JavaFX application for managing a library.
 * It provides a user interface to display books in a table and allows users to add, borrow, return,
 * and delete books. It also logs certain actions to a file.
 */

public class LibraryManagementSystem extends Application {
    private LibraryManager libMgr;
    private TableView<Book> table;
    private ObservableList<Book> data;

    @Override
    public void start(Stage stage) {
        libMgr = new LibraryManager();
        data = FXCollections.observableArrayList(libMgr.getBooks());

        //Set up table columns
        table = new TableView<>();
        TableColumn<Book, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        TableColumn<Book, String> publisherCol = new TableColumn<>("Publisher");
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        TableColumn<Book, Boolean> borCol = new TableColumn<>("Borrowed");
        borCol.setCellValueFactory(new PropertyValueFactory<>("borrowed"));
        TableColumn<Book, String> borrowerCol = new TableColumn<>("Borrower");
        borrowerCol.setCellValueFactory(new PropertyValueFactory<>("borrower"));
        TableColumn<Book, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        table.getColumns().addAll(idCol, titleCol, authorCol, publisherCol, borCol, borrowerCol, dateCol);
        table.setItems(data);

        //Section to add a new book
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField authorField = new TextField();
        authorField.setPromptText("Author");
        TextField publisherField = new TextField();
        publisherField.setPromptText("Publisher");
        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String publisher = publisherField.getText().trim();
            if (!title.isEmpty() && !author.isEmpty() && !publisher.isEmpty()) {
                libMgr.addBook(title, author, publisher);
                logBackup("Added book: " + title + " by " + author);
                refreshTable();
                titleField.clear();
                authorField.clear();
                publisherField.clear();
            }
        });

        //Section to borrow a book
        TextField borrowIdField = new TextField();
        borrowIdField.setPromptText("ID to borrow");
        TextField borrowerField = new TextField();
        borrowerField.setPromptText("Name");
        Button borrowBtn = new Button("Borrow");
        borrowBtn.setOnAction(e -> {
            try {
                int bid = Integer.parseInt(borrowIdField.getText().trim());
                String name = borrowerField.getText().trim();
                if (!name.isEmpty() && libMgr.borrowBook(bid, name)) {
                    logBackup("Borrowed book ID " + bid + " by " + name);
                    refreshTable();
                    borrowIdField.clear();
                    borrowerField.clear();
                } else {
                    showAlert("Error", "Failed to borrow.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid ID for borrowing.");
            }
        });

        //Section to return a book
        TextField returnIdField = new TextField();
        returnIdField.setPromptText("ID to return");
        Button returnBtn = new Button("Return");
        returnBtn.setOnAction(e -> {
            try {
                int bid = Integer.parseInt(returnIdField.getText().trim());
                if (libMgr.returnBook(bid)) {
                    logBackup("Returned book ID " + bid);
                    refreshTable();
                    returnIdField.clear();
                } else {
                    showAlert("Error", "Failed to return.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid ID for returning.");
            }
        });

        //Section to delete a book
        TextField deleteIdField = new TextField();
        deleteIdField.setPromptText("ID to delete");
        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            try {
                int bid = Integer.parseInt(deleteIdField.getText().trim());
                if (libMgr.deleteBook(bid)) {
                    logBackup("Deleted book ID " + bid);
                    refreshTable();
                    deleteIdField.clear();
                } else {
                    showAlert("Error", "Failed to delete.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid ID for deletion.");
            }
        });

        VBox addBox = new VBox(5, new Label("Add Book:"), titleField, authorField, publisherField, addBtn);
        VBox borrowBox = new VBox(5, new Label("Borrow:"), borrowIdField, borrowerField, borrowBtn);
        VBox returnBox = new VBox(5, new Label("Return:"), returnIdField, returnBtn);
        VBox deleteBox = new VBox(5, new Label("Delete:"), deleteIdField, deleteBtn);

        VBox root = new VBox(10, table, addBox, borrowBox, returnBox, deleteBox);
        Scene scene = new Scene(root, 600, 650);
        stage.setTitle("Library System");
        stage.setScene(scene);
        stage.show();
    }

    //Refresh table data
    private void refreshTable() {
        data.setAll(libMgr.getBooks());
    }

    //Simple error alert
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText(title);
        alert.showAndWait();
    }

    //Log action to a file
    private void logBackup(String act) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("library.log", true))) {
            bw.write(java.time.LocalDateTime.now() + " - " + act + "\n");
        } catch (IOException e) {
            System.err.println("Log write failed.");
        }
    }

    @Override
    public void stop() {
        libMgr.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}