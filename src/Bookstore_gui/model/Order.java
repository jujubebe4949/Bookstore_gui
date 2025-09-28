package Bookstore_gui.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Order {
    // 기존 뷰가 직접 접근: id, createdAt, items, total()
    public final String id;
    public final String userId;
    public final Instant createdAt;
    public final List<Item> items = new ArrayList<>();
    
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public Instant getCreatedAt() { return createdAt; }
    public List<Item> getItems() { return items; }
    public double getTotal() { return total(); }

    // 뷰/레포 호환 생성자(지금 리스트로 만드는 기존 코드 지원)
    public Order(String id, String userId, List<Item> items) {
        this(id, userId, Instant.now());
        if (items != null) this.items.addAll(items);
    }

    public Order(String id, String userId, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
    }

    public double total() {
        return items.stream().mapToDouble(Item::subtotal).sum();
    }

    // 뷰/CartView가 기대하는 필드들(title, qty, subtotal)
    public static class Item {
        public final String productId;
        public final String title;
        public final int qty;
        public final double price;

        public Item(String productId, String title, int qty, double price) {
            this.productId = productId;
            this.title = title;
            this.qty = qty;
            this.price = price;
        }
        public double subtotal() { return price * qty; }
    }
}