package ar.uba.fi.ingsoft1.persistance;

import ar.uba.fi.ingsoft1.domain.Order;
import ar.uba.fi.ingsoft1.domain.Product;
import ar.uba.fi.ingsoft1.domain.Product.ProductFactory;
import ar.uba.fi.ingsoft1.persistance.TemporaryOrderDataBase;
import ar.uba.fi.ingsoft1.services.ProductServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class TemporaryOrderDataBaseTest {

    private TemporaryOrderDataBase database;
    @Autowired
    private ProductServices productServices;

    // Initializes a test database before each test.
@BeforeEach
void setUp() {
    database = new TemporaryOrderDataBase();
}

    @Test
    void testGetByIdExists() {
        Order order = database.getById(1);
        assertNotNull(order);
        assertEquals(1, order.getId());
        assertEquals("fascencio@fi.uba.ar", order.getUserEmail());
    }

    @Test
    void testGetByIdDoesNotExist() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.getById(99));
        assertEquals("Order with ID 99 not found.", exception.getMessage());
    }

    @Test
    void testAddOrder() {
        Product product1 = ProductFactory.createProductWithAttributes(
                "Juice", "beverage", 0.5, 30, "Liquid", 700, Map.of("flavour", "orange")
        );
        Product product2 = ProductFactory.createProductWithAttributes(
                "Bread", "food", 0.2, 15, "Solid", 14, Map.of("type", "whole-wheat")
        );
        Map<Integer, Integer> orderedProducts = Map.of(
                1, 30,
                2, 15);
        Order newOrder = new Order(orderedProducts, "Pedido", LocalDateTime.now(), "user@domain.com");
        database.addOrder(newOrder);
        Order retrievedOrder = database.getById(4);
        assertNotNull(retrievedOrder);
        assertEquals("user@domain.com", retrievedOrder.getUserEmail());
        assertEquals(2, retrievedOrder.getOrderedProducts().size());
    }

    @Test
    void testDeleteOrderByIdExists() {
        database.deleteOrderById(1);  // Assuming ID 1 exists
        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.getById(1));
        assertEquals("Order with ID 1 not found.", exception.getMessage());
    }

    @Test
    void testDeleteOrderByIdDoesNotExist() {
        database.deleteOrderById(99);
        assertEquals(3, database.getAllOrders().size());
    }

    @Test
    void testGetOrdersByEmail() {
        List<Order> orders = database.getOrdersByEmail("fascencio@fi.uba.ar");
        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertEquals("fascencio@fi.uba.ar", orders.get(0).getUserEmail());
    }

    @Test
    void testGetOrdersByEmailNoOrders() {
        List<Order> orders = database.getOrdersByEmail("nonexistent@domain.com");
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    void testUpdateOrderValid() {
        Order order = database.getById(1);
        order.setState("EnProceso");
        database.updateOrder(order);
        Order updatedOrder = database.getById(1);
        Exception exception = assertThrows(IllegalStateException.class, updatedOrder::processOrder);
        assertEquals("El pedido ya está en proceso.", exception.getMessage());
    }

    @Test
    void testUpdateOrderNonExistent() {
        Order nonExistentOrder = new Order(new HashMap<>(), "Pedido", LocalDateTime.now(), "user@domain.com");
        nonExistentOrder.setId(99);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.updateOrder(nonExistentOrder));
        assertEquals("Order with ID 99 not found for replacement.", exception.getMessage());
    }

    @Test
    void testAutomaticIdAssignment() {
        Product product1 = ProductFactory.createProductWithAttributes(
                "Juice", "beverage", 0.5, 30, "Liquid", 700, Map.of("flavour", "orange")
        );
        Product product2 = ProductFactory.createProductWithAttributes(
                "Bread", "food", 0.2, 15, "Solid", 14, Map.of("type", "whole-wheat")
        );
        Map<Integer, Integer> orderedProducts = Map.of(
                1, 30,
                2, 15);
        Order newOrder1 = new Order(orderedProducts, "Pedido", LocalDateTime.now(), "user1@domain.com");
        Order newOrder2 = new Order(orderedProducts, "Pedido", LocalDateTime.now(), "user2@domain.com");
        database.addOrder(newOrder1);
        database.addOrder(newOrder2);
        assertEquals(4, newOrder1.getId());
        assertEquals(5, newOrder2.getId());
    }

    @Test
    void testMultipleOrders() {
        assertEquals(3, database.getAllOrders().size());
    }

    @Test
    void testOrderWithMultipleProducts() {
        Order order = database.getById(2);
        assertNotNull(order);
        assertEquals(2, order.getOrderedProducts().size());
    }

    @Test
    void testCancelOrderMoreThan24Hours() {
        Order order = database.getById(1);
        Exception exception = assertThrows(IllegalStateException.class, () -> order.cancelOrder());
        assertEquals("No se puede cancelar un pedido con más de 24 horas.", exception.getMessage());
    }
}

