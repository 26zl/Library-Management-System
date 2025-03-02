package com.zl.librarymanagementsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHandler manages the SQLite database connection and operations for the library management system.
 * It handles creating the books table and provides methods to add, borrow, return, delete, and retrieve books.
 */

public class DatabaseHandler {

    //Instance variables
    private static final String DB_URL = "jdbc:sqlite:library.db";
    private Connection conn;

    /**
     * Constructor that initializes the database connection and creates the books table if it does not exist.
     */
    public DatabaseHandler() {
        connect();
        createTable();
    }

    //Establishes connection
    private void connect() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to SQLite database.");
        } catch (SQLException e) {
            System.err.println("DB connection failed.");
            e.printStackTrace();
        }
    }

    //Create table with all columns if it doesn't exist
    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS books ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, "
                + "author TEXT NOT NULL, "
                + "publisher TEXT NOT NULL, "
                + "borrowed INTEGER DEFAULT 0, "
                + "borrower TEXT, "
                + "date TEXT)";
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
            System.out.println("Books table is ready.");
        } catch (SQLException e) {
            System.err.println("Error creating table.");
            e.printStackTrace();
        }
    }

    //Add a new book
    public int addBook(String title, String author, String publisher) {
        String sql = "INSERT INTO books (title, author, publisher) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, publisher);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    System.out.println("Book added with id: " + newId);
                    return newId;
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to add book.");
            e.printStackTrace();
        }
        return -1;
    }

    //Mark book as borrowed
    public boolean borrowBook(int id, String borrower) {
        String sql = "UPDATE books SET borrowed = 1, borrower = ?, date = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, borrower);
            ps.setString(2, java.time.LocalDate.now().toString());
            ps.setInt(3, id);
            int upd = ps.executeUpdate();
            return upd > 0;
        } catch (SQLException e) {
            System.err.println("Failed to borrow book id: " + id);
            e.printStackTrace();
        }
        return false;
    }

    //Mark book as returned
    public boolean returnBook(int id) {
        String sql = "UPDATE books SET borrowed = 0, borrower = NULL, date = NULL WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int upd = ps.executeUpdate();
            return upd > 0;
        } catch (SQLException e) {
            System.err.println("Failed to return book id: " + id);
            e.printStackTrace();
        }
        return false;
    }

    //Delete a book by id
    public boolean deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int del = ps.executeUpdate();
            return del > 0;
        } catch (SQLException e) {
            System.err.println("Failed to delete book id: " + id);
            e.printStackTrace();
        }
        return false;
    }

    //Get all books from the database
    public List<Book> getAllBooks() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String publisher = rs.getString("publisher");
                boolean borrowed = rs.getInt("borrowed") == 1;
                String borrower = rs.getString("borrower");
                String date = rs.getString("date");
                list.add(new Book(id, title, author, publisher, borrowed, borrower, date));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching books.");
            e.printStackTrace();
        }
        return list;
    }

    //Close connection
    public void close() {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("DB closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing DB.");
            e.printStackTrace();
        }


    }
}