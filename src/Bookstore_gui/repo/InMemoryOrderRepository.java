package Bookstore_gui.repo;

import Bookstore_gui.model.Order;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryOrderRepository implements OrderRepository {
    private final List<Order> orders = new ArrayList<>();

    @Override
    public synchronized String create(String userId, List<Order.Item> items){
        String id = UUID.randomUUID().toString();
        orders.add(new Order(id, userId, items));
        return id;
    }

    @Override
    public synchronized List<Order> findAll(){
        return new ArrayList<>(orders);
    }

    @Override
    public synchronized List<Order> findByUser(String userId){
        return orders.stream()
                .filter(o -> Objects.equals(o.getUserId(), userId))
                .collect(Collectors.toList());
    }
}