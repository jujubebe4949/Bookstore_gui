package Bookstore_gui.model;

import Bookstore_gui.model.Product;

/**
 *Concrete child class to show inheritance in a bookstore domain.
 * Extends Product with book-specific attributes.
 */

public class BookProduct extends Product {
    private final String author;
    private final String publisher;
    private final String isbn;
    private final int pages;

    public BookProduct(String id, String name, String description,
                       double price, int stock,
                       String author, String publisher, String isbn, int pages) {
        super(id, name, description, price, stock);
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
        this.pages = Math.max(1, pages); 
    }

    public String getAuthor() {
        return author; 
    }
    public String getPublisher() {
        return publisher;
    }
    public String getIsbn() { 
        return isbn; 
    }
    public int getPages() {
        return pages; 
    }
}
