// File: src/Bookstore_gui/model/Product.java
package Bookstore_gui.model;

import java.util.Objects;

/**
 * ENG: Minimal product model (temporarily without Review/Rateable).
 */
public class Product {
    private final String id;
    private final String name;
    private final String description;
    private final double price;
    private int stock;

    public Product(String id, String name, String description, double price, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    //ENG: stock check
    public boolean isInStock(int qty) { return qty > 0 && stock >= qty; }

    // ENG: safe stock deduction 
    public boolean reduceStock(int qty) {
        if (qty <= 0 || stock < qty) return false; 
        stock -= qty;
        return true;
    }

    // equality by id (for maps/sets)
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
