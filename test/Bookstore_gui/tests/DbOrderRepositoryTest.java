package Bookstore_gui.tests;

import Bookstore_gui.db.DbManager;
import Bookstore_gui.db.DbOrderRepository;
import Bookstore_gui.db.DbUserRepository;
import Bookstore_gui.model.Order;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class DbOrderRepositoryTest {

    private final DbOrderRepository orders = new DbOrderRepository();
    private final DbUserRepository users = new DbUserRepository();
    private String userId;

    @Before
    public void setup() throws Exception {
        DbManager.initSchema();
        userId = users.findOrCreate("Buyer", "buyer@example.com");

        // 유저의 기존 주문 정리
        try (Connection c = DbManager.connect()) {
            try (PreparedStatement ps = c.prepareStatement(
                    "DELETE FROM OrderItems WHERE orderId IN (SELECT id FROM Orders WHERE userId=?)")) {
                ps.setString(1, userId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM Orders WHERE userId=?")) {
                ps.setString(1, userId);
                ps.executeUpdate();
            }
        }
    }

    @Test
    public void createAndFindByUser() {
        List<Order.Item> items = Arrays.asList(
                new Order.Item("B001", "Book A", 2, 10.0),
                new Order.Item("B002", "Book B", 1, 15.0)
        );
        String orderId = orders.create(userId, items);
        assertNotNull(orderId);

        List<Bookstore_gui.model.Order> list = orders.findByUser(userId);
        assertEquals(1, list.size());
        Bookstore_gui.model.Order o = list.get(0);
        assertEquals(3, o.getItems().stream().mapToInt(it -> it.qty).sum());
        assertEquals(35.0, o.getTotal(), 1e-6);
    }
}