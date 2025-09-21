package Bookstore_gui.model;

import java.util.*;
import java.util.Objects;

/**
 * Base product (encapsulation + interface implementation).
 * - equals/hashCode are id-based so Product works safely as a Map key.
 * - reduceStock returns boolean (no exceptions in normal flow).
 */
public class Product implements Rateable {
    private final String id;
    private final String name;
    private final String description;
    private final double price;
    private int stock;
    private final List<Review> reviews = new ArrayList<>();

    public Product(String id, String name, String description, double price, int stock) {
        this.id = id; 
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public String getId() {
        return id; 
    }
    
    public String getName() {
        return name; 
    }
    
    public String getDescription() {
        return description; 
    }
    
    public double getPrice() { 
        return price; 
    }
    
    public int getStock() {
        return stock; 
    }

    public boolean isInStock(int qty) {
        return qty > 0 && stock >= qty; 
    }

   /** Decrease stock safely; returns true on success. */
    public boolean reduceStock(int qty) {
        if (qty <= 0){
            return false;
        }           
        if (stock < qty){
            return false;
        }        // avoid negative stock
        stock -= qty;
        return true;
    }
/*
    @Override public void addReview(Review review) { 
        reviews.add(review); 
    }
    
    @Override public List<Review> getReviews() {
        return Collections.unmodifiableList(reviews); 
    }

    
    @Override public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
