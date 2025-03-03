package com.zl.librarymanagementsystem;

import java.io.Serializable;

/**
 * Represents a Book entity with details such as title, author, publisher,
 * and borrowing status. Implements Serializable to allow object serialization.
 */
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String author;
    private String publisher;
    private boolean borrowed;
    private String borrower;
    private String date;

    // Constructor
    public Book(int id, String title, String author, String publisher, boolean borrowed, String borrower, String date) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.borrowed = borrowed;
        this.borrower = borrower;
        this.date = date;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public boolean isBorrowed() { return borrowed; }
    public String getBorrower() { return borrower; }
    public String getDate() { return date; }

    // Setters
    public void setBorrowed(boolean borrowed) { this.borrowed = borrowed; }
    public void setBorrower(String borrower) { this.borrower = borrower; }
    public void setDate(String date) { this.date = date; }

    @Override
    public String toString() {
        return id + ": " + title + " by " + author + ", Pub: " + publisher +
                (borrowed ? " [Borrowed by " + borrower + " on " + date + "]" : " [Available]");
    }
}