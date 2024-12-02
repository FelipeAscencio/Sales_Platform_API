package ar.uba.fi.ingsoft1.services;

import ar.uba.fi.ingsoft1.domain.*;
import ar.uba.fi.ingsoft1.domain.Rule.RuleEngine;
import ar.uba.fi.ingsoft1.persistance.OrderRepositoryJPA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OrderServices {
    private final OrderRepositoryJPA database;
    private final RuleEngine ruleEngine;
    private final ProductServices productServices;

    // Constructor
    public OrderServices(OrderRepositoryJPA database, ProductServices productServices, RuleEngine ruleEngine) {
        this.database = database;
        this.productServices = productServices;
        this.ruleEngine = ruleEngine;
    }

    // Verifica la disponibilidad de stock para los productos
    private void checkOrderStock(List<Integer> productIds, List<Integer> quantities) {
        for (int i = 0; i < productIds.size(); i++) {
            int productId = productIds.get(i);
            int quantity = quantities.get(i);
            if (!productServices.checkStock(productId, quantity)) {
                throw new IllegalArgumentException("No hay stock suficiente para el producto con ID: " + productId);
            }
        }
    }

    // Reduce el stock de los productos
    private void decreaseOrderStock(List<Integer> productIds, List<Integer> quantities) {
        for (int i = 0; i < productIds.size(); i++) {
            int productId = productIds.get(i);
            int quantity = quantities.get(i);
            productServices.decreaseStock(productId, quantity);
        }
    }

    @Transactional
    public Order createOrder(List<Integer> productIds, List<Integer> quantities, String userEmail) {
        checkOrderStock(productIds, quantities);
        Order newOrder = new Order(productIds, quantities, userEmail);
        Map<Integer, Product> orderProducts = productServices.getProductsById(productIds);
        ruleEngine.validate(newOrder, orderProducts);
        database.save(newOrder); // Usa save de JpaRepository
        decreaseOrderStock(productIds, quantities);
        return newOrder;
    }

    public Order getOrderById(int orderId) {
        return database.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
    }

    public void deleteOrder(int orderId) {
        database.deleteById(orderId); // Usa deleteById de JpaRepository
    }

    public List<Order> getAllOrders() {
        return database.findAll(); // Usa findAll de JpaRepository
    }

    public List<Order> getAllOrdersOfAnUser(String email) {
        return database.findByUserEmail(email); // Usa el método personalizado
    }

    public void updateOrder(Order updatedOrder) {
        database.save(updatedOrder); // save actualiza si la entidad ya existe
    }

    public void processOrder(int id) {
        Order order = getOrderById(id);
        order.processOrder();
        updateOrder(order);
    }

    public void shipOrder(int id) {
        Order order = getOrderById(id);
        order.shipOrder();
        updateOrder(order);
    }

    public void cancelOrder(int id) {
        Order order = getOrderById(id);
        order.cancelOrder();
        productServices.increaseStockOfProducts(order.getOrderedProducts());
        updateOrder(order);
    }

    public void setState(String estado, int id) {
        Order order = getOrderById(id);
        order.setState(estado);
        updateOrder(order);
    }

    public LocalDateTime getCreationDateTime(int id) {
        Order order = getOrderById(id);
        return order.getCreationDateTime();
    }

    public Map<Integer, Integer> getOrderedProducts(int id) {
        Order order = getOrderById(id);
        return order.getOrderedProducts();
    }
}
