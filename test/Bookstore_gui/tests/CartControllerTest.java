// path: test/Bookstore_gui/tests/CartControllerTest.java
package Bookstore_gui.tests;

import Bookstore_gui.controller.CartController;
import Bookstore_gui.model.BookProduct;
import Bookstore_gui.repo.BookRepository;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/** Unit tests for CartController add/merge/validation logic. */
public class CartControllerTest {

    /** Repo stub with generous stock; keeps tests focused on cart logic. */
    private static final class StubBookRepo implements BookRepository {
        @Override public List<BookProduct> findAll() { return Collections.emptyList(); }
        @Override public List<BookProduct> findByTitleLike(String key) { return Collections.emptyList(); }
        @Override public int getStock(String productId) { return 999; }
        @Override public int updateStockDelta(String productId, int delta) { return 1; }
    }

    /** Repo stub with fixed stock for boundary tests. */
    private static BookRepository limitedRepo(int stock) {
        return new BookRepository() {
            @Override public List<BookProduct> findAll() { return Collections.emptyList(); }
            @Override public List<BookProduct> findByTitleLike(String key) { return Collections.emptyList(); }
            @Override public int getStock(String productId) { return stock; }
            @Override public int updateStockDelta(String productId, int delta) { return 1; }
        };
    }

    @Test
    public void addAndMergeSameProduct() {
        CartController cart = new CartController(new StubBookRepo());
        cart.add("B001", "Sample Book", 20.0, 2);
        cart.add("B001", "Sample Book", 20.0, 3);

        assertEquals(1, cart.lines().size());
        assertEquals(5, cart.lines().get(0).qty);
        assertEquals(100.0, cart.total(), 1e-6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRejectsNonPositiveQty() {
        CartController cart = new CartController(new StubBookRepo());
        cart.add("B001", "Sample", 10.0, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void addFailsWhenExceedingStock_singleAdd() {
        CartController cart = new CartController(limitedRepo(3));
        cart.add("B001", "Sample", 10.0, 4); // exceeds stock=3
    }

    @Test(expected = IllegalStateException.class)
    public void addFailsWhenCumulativeExceedsStock() {
        CartController cart = new CartController(limitedRepo(3));
        cart.add("B001", "Sample", 10.0, 2);
        cart.add("B001", "Sample", 10.0, 2); // cumulative 4 > stock 3
    }

    @Test
    public void totalsAcrossMultipleItems() {
        CartController cart = new CartController(new StubBookRepo());
        cart.add("B001", "Book A", 12.5, 2);  // 25.0
        cart.add("B002", "Book B",  8.0, 3);  // 24.0
        assertEquals(2, cart.lines().size());
        assertEquals(49.0, cart.total(), 1e-6);
    }
}