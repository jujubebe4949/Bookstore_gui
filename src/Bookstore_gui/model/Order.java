package Bookstore_gui.model;

import java.time.LocalDateTime;
import java.util.*;

public class Order {
    public static final class Item {
        public final String bookId, title; public final int qty; public final double price;
        public Item(String bookId, String title, int qty, double price){
            this.bookId=bookId; this.title=title; this.qty=qty; this.price=price;
        }
        public double subtotal(){ return qty * price; }
    }

    public final String id;
    public final String userId;              // owner
    public final LocalDateTime createdAt;    // timestamp
    public final List<Item> items;           // immutable copy

    public Order(String id, String userId, List<Item> items){
        this.id = id; this.userId = userId; this.createdAt = LocalDateTime.now();
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
    }

    public double total(){ return items.stream().mapToDouble(Item::subtotal).sum(); }
}