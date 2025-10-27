package Bookstore_gui.controller;

import Bookstore_gui.repo.BookRepository;
import java.util.*;

/** Handles cart operations: add/remove items and compute totals. */
public class CartController {

    /** Represents a single line in the shopping cart. */
    public static final class Line {
        public final String id;
        public final String title;
        public final double price;
        public int qty;

        public Line(String id, String title, double price, int qty) {
            this.id = id;
            this.title = title;
            this.price = price;
            this.qty = qty;
        }

        public double subtotal() { return price * qty; }
    }

    private final Map<String, Line> map = new LinkedHashMap<>();
    private final BookRepository bookRepo;

    public CartController(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    /** Add item to cart; validates quantity and stock. */
    public void add(String id, String title, double price, int qty) {
        if (qty <= 0)
            throw new IllegalArgumentException("Quantity must be positive.");

        int stock = bookRepo.getStock(id);
        if (qty > stock)
            throw new IllegalStateException("Only " + stock + " item(s) in stock.");

        Line existing = map.get(id);
        if (existing == null) {
            map.put(id, new Line(id, title, price, qty));
        } else {
            if (existing.qty + qty > stock)
                throw new IllegalStateException("Total quantity exceeds available stock.");
            existing.qty += qty;
        }
    }

    /** Remove one item line from cart. */
    public void remove(String id) {
        map.remove(id);
    }

    /** Clear all items in the cart. */
    public void clear() {
        map.clear();
    }

    /** Return current cart contents. */
    public List<Line> lines() {
        return new ArrayList<>(map.values());
    }

    /** Calculate total price of all items. */
    public double total() {
        return map.values().stream().mapToDouble(Line::subtotal).sum();
    }
}