package Bookstore_gui.db;

import Bookstore_gui.model.Order;
import Bookstore_gui.repo.OrderRepository;

import java.sql.*;
import java.time.ZoneId;
import java.util.*;
import java.util.UUID;

public class DbOrderRepository implements OrderRepository {

    @Override
    public String create(String userId, java.util.List<Order.Item> items) {
        String id = UUID.randomUUID().toString();
        try (Connection c = DbManager.connect()) {
            boolean old = c.getAutoCommit();
            c.setAutoCommit(false);

            try {
                // Orders
                try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO Orders (id,userId,created) VALUES (?,?,CURRENT_TIMESTAMP)")) {
                    ps.setString(1, id);
                    ps.setString(2, userId);
                    ps.executeUpdate();
                }
                // OrderItems
                try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO OrderItems (orderId,productId,title,quantity,price) VALUES (?,?,?,?,?)")) {
                    for (Order.Item it : items) {
                        ps.setString(1, id);
                        ps.setString(2, it.productId);
                        ps.setString(3, it.title);
                        ps.setInt(4, it.qty);
                        ps.setDouble(5, it.price);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                c.commit();
                c.setAutoCommit(old);
                return id;
            } catch (Exception e) {
                try { c.rollback(); } catch (Exception ignore) {}
                c.setAutoCommit(old);
                throw new RuntimeException("Order create failed; rolled back", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Db connection failed", e);
        }
    }

    @Override
    public java.util.List<Order> findAll() {
        java.util.List<Order> list = new ArrayList<>();
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT id, userId, created FROM Orders ORDER BY created DESC")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    String userId = rs.getString("userId");
                    Timestamp ts = rs.getTimestamp("created");
                    var created = ts.toInstant().atZone(ZoneId.systemDefault()).toInstant();

                    Order o = new Order(id, userId, created);
                    o.getItems().addAll(loadItems(id));
                    list.add(o);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("findAll failed", e);
        }
        return list;
    }

    @Override
    public java.util.List<Order> findByUser(String userId) {
        java.util.List<Order> list = new ArrayList<>();
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(
                "SELECT id, created FROM Orders WHERE userId=? ORDER BY created DESC")) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    Timestamp ts = rs.getTimestamp("created");
                    var created = ts.toInstant().atZone(ZoneId.systemDefault()).toInstant();

                    Order o = new Order(id, userId, created);
                    o.getItems().addAll(loadItems(id));
                    list.add(o);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByUser failed", e);
        }
        return list;
    }

    /** 주문 취소 기능 (write #2) */
    public boolean cancelOrder(String orderId) {
        String delItems = "DELETE FROM OrderItems WHERE orderId=?";
        String delOrder = "DELETE FROM Orders WHERE id=?";
        try (Connection c = DbManager.connect()) {
            boolean old = c.getAutoCommit();
            c.setAutoCommit(false);

            try (PreparedStatement ps1 = c.prepareStatement(delItems);
                 PreparedStatement ps2 = c.prepareStatement(delOrder)) {
                ps1.setString(1, orderId);
                ps1.executeUpdate();

                ps2.setString(1, orderId);
                int rows = ps2.executeUpdate();

                c.commit();
                c.setAutoCommit(old);
                return rows > 0;
            } catch (Exception e) {
                try { c.rollback(); } catch (Exception ignore) {}
                c.setAutoCommit(old);
                throw new RuntimeException("cancelOrder failed", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("cancelOrder DB error", e);
        }
    }

    private java.util.List<Order.Item> loadItems(String orderId) throws SQLException {
        java.util.List<Order.Item> items = new ArrayList<>();
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(
                "SELECT productId, title, quantity, price FROM OrderItems WHERE orderId=?")) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new Order.Item(
                        rs.getString("productId"),
                        rs.getString("title"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                    ));
                }
            }
        }
        return items;
    }
}