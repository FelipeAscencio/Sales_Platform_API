package ar.uba.fi.ingsoft1.persistance;

import ar.uba.fi.ingsoft1.domain.Order;

import java.util.List;

public interface OrderRepository {
    Order getById(int orderId);
    void addOrder(Order order);
    void deleteOrderById(int orderId);
    List<Order> getAllOrders();
    List<Order> getOrdersByEmail(String email);
    void updateOrder(Order updatedOrder);
}
