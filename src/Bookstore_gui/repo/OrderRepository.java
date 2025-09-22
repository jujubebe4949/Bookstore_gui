package Bookstore_gui.repo;

import Bookstore_gui.model.Order;

import java.util.*;
import java.util.stream.Collectors;

public class OrderRepository {
    private final List<Order> orders = new ArrayList<>();

    public synchronized String create(String userId, List<Order.Item> items){
        String id = UUID.randomUUID().toString();
        orders.add(new Order(id, userId, items));
        return id;
    }

    public synchronized List<Order> findAll(){
        return new ArrayList<>(orders);
    }

    public synchronized List<Order> findByUser(String userId){
        return orders.stream().filter(o -> Objects.equals(o.userId, userId)).collect(Collectors.toList());
    }
}
