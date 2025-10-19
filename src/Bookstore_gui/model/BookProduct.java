// File: src/Bookstore_gui/model/BookProduct.java
package Bookstore_gui.model;

/** 
 *  ENG: Book product. Add GUI-friendly getters */
public class BookProduct extends Product {
    private final String author;

    public BookProduct(String id, String name, String description,
                       double price, int stock, String author) {
        super(id, name, description, price, stock);
        this.author = author;
    }

    // --- existing ---
    public String getAuthor() { return author; }

    // --- NEW: GUI expects these ---
    public String getTitle() { return getName(); }   // why: GUI uses title; Product.name == title
    public double getPrice() { return super.getPrice(); }
    public int getStock() { return super.getStock(); }
}