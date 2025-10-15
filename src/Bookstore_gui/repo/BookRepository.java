package Bookstore_gui.repo;

import Bookstore_gui.model.BookProduct;
import java.util.List;

public interface BookRepository {
    List<BookProduct> findAll();
    List<BookProduct> findByTitleLike(String key); 
    
    

    int getStock(String productId);

    int updateStockDelta(String productId, int delta);
}