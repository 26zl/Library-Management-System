package com.zl.librarymanagementsystem;

import java.util.List;

/**
 * LibraryManager acts as a wrapper for the DatabaseHandler,
 * providing simplified methods for managing library operations.
 */
public class LibraryManager {
    private final DatabaseHandler db;

    public LibraryManager() {
        db = new DatabaseHandler();
    }

    public void addBook(String title, String author, String publisher) {
        db.addBook(title, author, publisher);
    }

    public List<Book> getBooks() {
        return db.getAllBooks();
    }

    public List<Book> searchBooks(String query) {
        return db.searchBooks(query);
    }

    public boolean borrowBook(int id, String borrower) {
        return db.borrowBook(id, borrower);
    }

    public boolean returnBook(int id) {
        return db.returnBook(id);
    }

    public boolean deleteBook(int id) {
        return db.deleteBook(id);
    }

    public void close() {
        db.close();
    }
}