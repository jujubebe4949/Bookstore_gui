// ✅ Fixed version of DbOrderRepository
package Bookstore_gui.db;

import Bookstore_gui.model.Order;
import Bookstore_gui.repo.OrderRepository;

import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** JDBC Order repo: performs stock check + stock update + order insert in ONE transaction */
public class DbOrderRepository implements OrderRepository {

    public DbOrderRepository() {
        try { DbManager.initSchema(); } catch (Exception ignore) {}
    }

    @Override
    public String create(String userId, List<Order.Item> items) {
        String orderId = UUID.randomUUID().toString();

        final String insOrder = "INSERT INTO Orders (id,userId,created) VALUES (?,?,CURRENT_TIMESTAMP)";
        final String insLine  = "INSERT INTO OrderItems (orderId,productId,title,quantity,price) VALUES (?,?,?,?,?)";
        final String selStock = "SELECT stock FROM BookProducts WHERE id=?";
        final String updStock = "UPDATE BookProducts SET stock = stock - ? WHERE id=?";

        try (Connection c = DbManager.connect()) {
            boolean oldAuto = c.getAutoCommit();
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(insOrder)) {
                    ps.setString(1, orderId);
                    ps.setString(2, userId);
                    ps.executeUpdate();
                }

                for (Order.Item it : items) {
                    int stock = 0;
                    try (PreparedStatement ps = c.prepareStatement(selStock)) {
                        ps.setString(1, it.productId);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) stock = rs.getInt(1);
                        }
                    }
                    if (stock < it.qty)
                        throw new IllegalStateException("Insufficient stock for " + it.productId);

                    try (PreparedStatement ps = c.prepareStatement(updStock)) {
                        ps.setInt(1, it.qty);
                        ps.setString(2, it.productId);
                        ps.executeUpdate();
                    }

                    try (PreparedStatement ps = c.prepareStatement(insLine)) {
                        ps.setString(1, orderId);
                        ps.setString(2, it.productId);
                        ps.setString(3, it.title);
                        ps.setInt(4, it.qty);
                        ps.setDouble(5, it.price);
                        ps.executeUpdate();
                    }
                }

                c.commit();
                c.setAutoCommit(oldAuto);
                return orderId;
            } catch (Exception e) {
                try { c.rollback(); } catch (Exception ignore) {}
                c.setAutoCommit(oldAuto);
                throw new RuntimeException("Order creation failed: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error during order create: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Order> findAll() {
        final String sql = "SELECT id,userId,created FROM Orders ORDER BY created DESC";
        List<Order> list = new ArrayList<>();
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                String uid = rs.getString("userId");
                var created = rs.getTimestamp("created").toInstant()
                        .atZone(ZoneId.systemDefault()).toInstant();
                Order o = new Order(id, uid, created);
                o.getItems().addAll(loadItems(c, id));
                list.add(o);
            }
        } catch (SQLException e) {
            throw new RuntimeException("findAll failed", e);
        }
        return list;
    }

    @Override
    public List<Order> findByUser(String userId) {
        final String sql = "SELECT id,created FROM Orders WHERE userId=? ORDER BY created DESC";
        List<Order> list = new ArrayList<>();
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    var created = rs.getTimestamp("created").toInstant()
                            .atZone(ZoneId.systemDefault()).toInstant();
                    Order o = new Order(id, userId, created);
                    o.getItems().addAll(loadItems(c, id));
                    list.add(o);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByUser failed", e);
        }
        return list;
    }

    private List<Order.Item> loadItems(Connection c, String orderId) throws SQLException {
        final String sql = "SELECT productId,title,quantity,price FROM OrderItems WHERE orderId=?";
        List<Order.Item> items = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
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
                throw new RuntimeException("cancelOrder failed: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("cancelOrder DB error: " + e.getMessage(), e);
        }
    }
}