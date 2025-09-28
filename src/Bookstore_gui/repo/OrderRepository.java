package Bookstore_gui.repo;

import Bookstore_gui.model.Order;
import java.util.List;

public interface OrderRepository {
    String create(String userId, java.util.List<Order.Item> items);
    java.util.List<Order> findAll();
    java.util.List<Order> findByUser(String userId);
}