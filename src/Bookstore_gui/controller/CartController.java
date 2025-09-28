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
        if(qty <= 0) throw new IllegalArgumentException("qty > 0 required");
        Line l = map.get(id);
        if(l == null) map.put(id, new Line(id, title, price, qty));
        else l.qty += qty;
        // ❌ DB 재고 차감 제거: 원자성 보장을 위해 Checkout 트랜잭션에서만 차감
    }

    public void remove(String id){ map.remove(id); }
    public void clear(){ map.clear(); }
    public List<Line> lines(){ return new ArrayList<>(map.values()); }
    public double total(){ return map.values().stream().mapToDouble(Line::subtotal).sum(); }
}