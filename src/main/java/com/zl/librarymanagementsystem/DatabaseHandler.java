package com.zl.librarymanagementsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHandler manages the SQLite database connection and operations for the library management system.
 * It handles creating the books table and provides methods to add, borrow, return, delete, and retrieve books.
 */
public class DatabaseHandler {

    private static final String DB_URL = "jdbc:sqlite:library.db";
    private Connection conn;

    public DatabaseHandler() {
        connect();
        createTable();
    }

    // Establishes connection
    private void connect() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to SQLite database.");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    // Create table if it doesn't exist
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
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    public int addBook(String title, String author, String publisher) {
        String sql = "INSERT INTO books (title, author, publisher) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, publisher);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to add book: " + e.getMessage());
        }
        return -1;
    }

    public boolean borrowBook(int id, String borrower) {
        String sql = "UPDATE books SET borrowed = 1, borrower = ?, date = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, borrower);
            ps.setString(2, java.time.LocalDate.now().toString());
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to borrow book ID: " + id + ". Error: " + e.getMessage());
        }
        return false;
    }

    public boolean returnBook(int id) {
        String sql = "UPDATE books SET borrowed = 0, borrower = NULL, date = NULL WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to return book ID: " + id + ". Error: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to delete book ID: " + id + ". Error: " + e.getMessage());
        }
        return false;
    }

    public List<Book> searchBooks(String query) {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            ps.setString(2, "%" + query + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getInt("borrowed") == 1,
                        rs.getString("borrower"),
                        rs.getString("date")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
        }
        return list;
    }

    public List<Book> getAllBooks() {
        return searchBooks(""); // Fetch all books
    }

    public void close() {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }
}