package Bookstore_gui.controller;

import Bookstore_gui.repo.BookRepository; // 주입은 유지 (필요 시 메시지 등에 활용 가능)
import java.util.*;

public class CartController {
    public static final class Line {
        public final String id;
        public final String title;
        public final double price;
        public int qty;
        public Line(String id, String title, double price, int qty){
            this.id=id; this.title=title; this.price=price; this.qty=qty;
        }
        public double subtotal(){ return price * qty; }
    }

    private final Map<String, Line> map = new LinkedHashMap<>();
    private final BookRepository bookRepo;

    public CartController(BookRepository bookRepo) { this.bookRepo = bookRepo; }

    public void add(String id, String title, double price, int qty){
        
        if(qty <= 0) throw new IllegalArgumentException("Quantity must be positive.");
         
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

    public void remove(String id){ map.remove(id); }
    public void clear(){ map.clear(); }
    public List<Line> lines(){ return new ArrayList<>(map.values()); }
    public double total(){ return map.values().stream().mapToDouble(Line::subtotal).sum(); }
}