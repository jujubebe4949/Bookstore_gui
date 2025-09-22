// File: src/Bookstore_gui/model/Product.java
package Bookstore_gui.model;

import java.util.Objects;

/**
 * KOR: 간단한 상품 모델(리뷰/Rateable 임시 제외).
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

    /** KOR: 재고 확인 / ENG: stock check */
    public boolean isInStock(int qty) { return qty > 0 && stock >= qty; }

    /** KOR: 재고 차감(안전) / ENG: safe stock deduction */
    public boolean reduceStock(int qty) {
        if (qty <= 0 || stock < qty) return false; // why: 음수/과차감 방지
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
