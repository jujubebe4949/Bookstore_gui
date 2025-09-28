package Bookstore_gui.repo;

import Bookstore_gui.model.BookProduct;
import java.util.List;

public interface BookRepository {
    List<BookProduct> findAll();
    List<BookProduct> findByTitleLike(String key); // 이미 있을 경우 그대로 유지

    // NEW: 재고 조회
    int getStock(String productId);

    // 이미 있다면 유지, 없다면 추가:
    int updateStockDelta(String productId, int delta);
}