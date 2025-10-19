// path: test/Bookstore_gui/tests/OrderStockConsistencyTest.java
package Bookstore_gui.tests;

import Bookstore_gui.db.*;
import Bookstore_gui.model.BookProduct;
import Bookstore_gui.model.Order;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class OrderStockConsistencyTest extends TestBootstrap {
    private final DbBookRepository  books  = new DbBookRepository();
    private final DbOrderRepository orders = new DbOrderRepository();
    private final DbUserRepository  users  = new DbUserRepository();

    private String userId;
    private static final String PID = "STK100";

    @Before
    public void setup() throws Exception {
        DbManager.initSchema();
        // clean up old test data
        try (var c = DbManager.connect()) {
            try (var ps = c.prepareStatement("DELETE FROM BookProducts WHERE id=?")) {
                ps.setString(1, PID); ps.executeUpdate();
            }
        }
        // Add a product with limited stock
        books.add(new BookProduct(PID, "Stock Book", "desc", 10.0, 5, "Tester"));
        userId = users.findOrCreate("Buyer", "buyer-stock@example.com");
    }

    @Test
    public void orderReducesStock() {
        int before = books.getStock(PID);
        String orderId = orders.create(userId, List.of(new Order.Item(PID, "Stock Book", 2, 10.0)));
        assertNotNull(orderId);
        int after = books.getStock(PID);
        assertEquals(before - 2, after);
    }

    @Test(expected = RuntimeException.class)
    public void orderRollsBackWhenInsufficientStock() {
        // Current stock =5, trying to order 6 should throw exception
        try {
            orders.create(userId, List.of(new Order.Item(PID, "Stock Book", 6, 10.0)));
        } finally {
            // Verify that stock is restored(rollback succeeded)
            assertEquals(5, books.getStock(PID));
        }
    }
}