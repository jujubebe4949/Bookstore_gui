package Bookstore_gui.repo;

import Bookstore_gui.model.Order;
import java.util.List;

/** Contract for order persistence (no implementation here). */
public interface OrderRepository {
    String create(String userId, List<Order.Item> items);
    List<Order> findAll();
    List<Order> findByUser(String userId);
}