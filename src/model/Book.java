package model;

import java.io.Serializable;

public class Book implements Serializable, Borrowable {

    private int id;
    private String title;
    private String author;
    private String isbn;
    private int copies;

    public Book(int id, String title, String author, String isbn, int copies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.copies = copies;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public int getCopies() { return copies; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setCopies(int copies) { this.copies = copies; }

    @Override
    public void borrow() {
        if (copies > 0) copies--;
    }

    @Override
    public void giveBack() {
        copies++;
    }

    @Override
    public String toString() {
        return "ID:" + id + " | " + title + " by " + author +
               " | ISBN: " + isbn + " | Available: " + copies;
    }
}
