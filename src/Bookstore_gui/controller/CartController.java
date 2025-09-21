package Bookstore_gui.controller;

import java.util.*;

public class CartController {
    // KOR: 표현용 DTO (테이블 바인딩) | ENG: DTO for table binding
    public static final class Line {
        public final String id;      // why: 제품 식별자 | product id to merge
        public final String title;   // why: 표시용 | display
        public final double price;   // why: 소계 계산 | subtotal calc
        public int qty;              // why: 누적 수량 | accumulated
        public Line(String id, String title, double price, int qty){
            this.id=id; this.title=title; this.price=price; this.qty=qty;
        }
        public double subtotal(){ return price * qty; }
    }

    private final Map<String, Line> map = new LinkedHashMap<>();

    public void add(String id, String title, double price, int qty){
        if(qty <= 0) throw new IllegalArgumentException("qty > 0 required");
        Line l = map.get(id);
        if(l == null) map.put(id, new Line(id, title, price, qty));
        else l.qty += qty; // why: 같은 상품은 병합 | merge same product
    }
    public void remove(String id){ map.remove(id); }
    public void clear(){ map.clear(); }
    public List<Line> lines(){ return new ArrayList<>(map.values()); }
    public double total(){ return map.values().stream().mapToDouble(Line::subtotal).sum(); }
}

