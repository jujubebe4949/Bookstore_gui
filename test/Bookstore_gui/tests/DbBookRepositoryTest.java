package Bookstore_gui.tests;

import Bookstore_gui.db.DbBookRepository;
import Bookstore_gui.db.DbManager;
import Bookstore_gui.model.BookProduct;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.Assert.*;

public class DbBookRepositoryTest {

    private final DbBookRepository repo = new DbBookRepository();
    private static final String ID = "T999";

    @Before
    public void clean() throws Exception {
        DbManager.initSchema();
        try (Connection c = DbManager.connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM BookProducts WHERE id=?")) {
            ps.setString(1, ID);
            ps.executeUpdate();
        }
    }

    @Test
    public void addFindUpdateDelete() {
        // add
        BookProduct p = new BookProduct(ID, "JUnit In Action", "test row", 12.34, 7, "Tester");
        repo.add(p);

        // find
        BookProduct got = repo.findById(ID).orElse(null);
        assertNotNull(got);
        assertEquals("JUnit In Action", got.getName());
        assertEquals(7, got.getStock());

        // stock delta
        int changed = repo.updateStockDelta(ID, -2);
        assertEquals(1, changed);
        int stock = repo.getStock(ID);
        assertEquals(5, stock);

        // update
        BookProduct upd = new BookProduct(ID, "JUnit Better", "desc", 15.0, stock, "Tester");
        repo.update(upd);
        assertEquals("JUnit Better", repo.findById(ID).orElseThrow().getName());

        // delete
        repo.delete(ID);
        assertFalse(repo.findById(ID).isPresent());
    }
    @Test
    public void searchByTitleLike() {
        // 준비: 보장
        repo.add(new BookProduct(ID, "JUnit In Action", "desc", 10.0, 3, "Tester"));

        // 대소문자 무시 부분검색
        List<BookProduct> hit = repo.findByTitleLike("junit");
        assertTrue(hit.stream().anyMatch(b -> ID.equals(b.getId())));
    }
}