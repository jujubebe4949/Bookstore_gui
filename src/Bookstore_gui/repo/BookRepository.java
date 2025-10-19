// path: src/Bookstore_gui/repo/BookRepository.java
package Bookstore_gui.repo;

import Bookstore_gui.model.BookProduct;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for BookProduct persistence and queries.
 * Provides CRUD operations and stock management.
 */
public interface BookRepository {
   
    List<BookProduct> findAll();
    List<BookProduct> findByTitleLike(String key);
    Optional<BookProduct> findById(String id);

    
    int getStock(String productId);
    int updateStockDelta(String productId, int delta);

    
    void add(BookProduct p);
    void update(BookProduct p);
    void delete(String id);
}