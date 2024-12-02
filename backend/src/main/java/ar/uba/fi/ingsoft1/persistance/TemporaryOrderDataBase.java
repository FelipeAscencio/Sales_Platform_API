package ar.uba.fi.ingsoft1.persistance;

import ar.uba.fi.ingsoft1.domain.Order;
import ar.uba.fi.ingsoft1.domain.Product;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class TemporaryOrderDataBase implements OrderRepository {
    private static final int ZERO = 0;
    private final List<Order> orders;
    private int lastAssignedId = ZERO;

    public TemporaryOrderDataBase() {
        orders = new ArrayList<>();
        Product product1 = new Product("Milk", "Food", 1.0, 50, "Liquid", 2.5f);
        Product product2 = new Product("Laptop", "Electronics", 3.0, 30, "Solid", 1000.0f);
        Product product3 = new Product("Green Jeans", "Indumentary", 0.5, 40, "Solid", 15.0f);
        Map<Integer, Integer> orderedProducts1 = Map.of(
                1, 50,
                2, 30);
        Map<Integer, Integer> orderedProducts2 = Map.of(
                2, 30,
                3, 40);
        LocalDateTime creationDate1 = LocalDateTime.of(2024, 6, 3, 0, 0, 0, 0);
        LocalDateTime creationDate2 = LocalDateTime.now();
        LocalDateTime creationDate3 = LocalDateTime.now();
        Order order1 = new Order(orderedProducts1, "Pedido", creationDate1, "fascencio@fi.uba.ar");
        Order order2 = new Order(orderedProducts2, "Pedido", creationDate2, "fascencio@fi.uba.ar");
        Order order3 = new Order(orderedProducts2, "EnProceso", creationDate3, "fbossi@fi.uba.ar");
        order1.setId(++lastAssignedId);
        order2.setId(++lastAssignedId);
        order3.setId(++lastAssignedId);
        orders.add(order1);
        orders.add(order2);
        orders.add(order3);
    }

    @Override
    public Order getById(int orderId) {
        return orders.stream()
                .filter(order -> order.getId() == orderId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " not found."));
    }

    @Override
    public void addOrder(Order order) {
        order.setId(++lastAssignedId);
        orders.add(order);
    }

    @Override
    public void deleteOrderById(int orderId) {
        orders.removeIf(order -> order.getId() == orderId);
    }

    @Override
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    @Override
    public List<Order> getOrdersByEmail(String email) {
        return orders.stream()
                .filter(order -> order.getUserEmail().equalsIgnoreCase(email))
                .collect(Collectors.toList());
    }

    @Override
    public void updateOrder(Order updatedOrder) {
        boolean replaced = false;
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId() == updatedOrder.getId()) {
                orders.set(i, updatedOrder);
                replaced = true;
                break;
            }
        }

        if (!replaced) {
            throw new IllegalArgumentException("Order with ID " + updatedOrder.getId() + " not found for replacement.");
        }
    }
}

