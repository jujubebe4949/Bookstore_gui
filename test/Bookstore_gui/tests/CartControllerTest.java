package Bookstore_gui.tests;

import Bookstore_gui.controller.CartController;
import Bookstore_gui.repo.BookRepository;
import Bookstore_gui.model.BookProduct;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class CartControllerTest {

    private static class StubBookRepo implements BookRepository {

    @Override
    public List<BookProduct> findAll() {
        // 테스트에서는 빈 목록으로 충분
        return Collections.emptyList();
    }

    @Override
    public List<BookProduct> findByTitleLike(String key) {
        // 검색도 빈 결과 반환
        return Collections.emptyList();
    }

    @Override
    public int getStock(String productId) {
        // 재고 항상 넉넉하게(또는 원하는 숫자) — 테스트 목적
        return 999;
    }

    @Override
    public int updateStockDelta(String productId, int delta) {
        // 업데이트 성공했다고 가정 (영향받은 행 수를 1로)
        return 1;
    }
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
    
    @Test(expected = IllegalArgumentException.class)
    public void addFailsWhenExceedingStock() {
        // 재고가 3개뿐이라고 가정하는 스텁
        BookRepository limitedRepo = new BookRepository() {
            @Override public List<BookProduct> findAll(){ return Collections.emptyList(); }
            @Override public List<BookProduct> findByTitleLike(String k){ return Collections.emptyList(); }
            @Override public int getStock(String productId){ return 3; }
            @Override public int updateStockDelta(String productId, int delta){ return 1; }
        };

        CartController cart = new CartController(limitedRepo);
        cart.add("B001", "Sample", 10.0, 4); // 재고(3) 초과 → 예외 기대
    }
    }