package ar.uba.fi.ingsoft1.services;

import ar.uba.fi.ingsoft1.controller.products.ProductDTO;
import ar.uba.fi.ingsoft1.domain.Order;
import ar.uba.fi.ingsoft1.domain.Product;
import ar.uba.fi.ingsoft1.persistance.ProductRepositoryJPA;
import ar.uba.fi.ingsoft1.persistance.OrderRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServicesTest {

    @Autowired
    private OrderServices orderServices;

    @MockBean
    private ProductServices productServices;

    @MockBean
    private ProductRepositoryJPA productRepositoryJPA;

    @MockBean
    private OrderRepositoryJPA orderRepository;

    private Order order;

    @BeforeEach
    public void setUp() {
        // Initialize order
        Product product1 = new Product("Milk", "Food", 1.0, 50, "Liquid", 2.5f);
        Product product2 = new Product("Laptop", "Electronics", 3.0, 30, "Solid", 1000.0f);
        Map<Integer, Integer> products = Map.of(
                1, 50,
                2, 30);
        order = new Order(products, "Pedido", null, "test@example.com");

        // Mock behaviors
        when(productServices.addProduct(anyString(), anyString(), anyDouble(), anyInt(), anyString(), anyFloat()))
                .thenReturn(new ProductDTO(product1));
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(productRepositoryJPA.findAll()).thenReturn(List.of(product1, product2));
        when(productRepositoryJPA.findById(1)).thenReturn(Optional.of(product1));
        when(productRepositoryJPA.findById(2)).thenReturn(Optional.of(product2));
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(order));
        when(orderRepository.getAllOrders()).thenReturn(List.of(order));
        when(orderRepository.findByUserEmail(anyString())).thenReturn(List.of(order));
    }

    @Test
    public void testCreateOrder() {
        List<Integer> productIds = List.of(1, 2);
        List<Integer> quantities = List.of(1, 2);
        when(productServices.checkStock(1, 1)).thenReturn(true);
        when(productServices.checkStock(2, 2)).thenReturn(true);
        when(productServices.getById(1)).thenReturn(Optional.of((new Product("Milk", "Food", 1.0, 50, "Liquid", 2.5f))));
        when(productServices.getById(2)).thenReturn(Optional.of((new Product("Laptop", "Electronics", 3.0, 30, "Solid", 1000.0f))));
        when(productServices.getDTOById(1)).thenReturn(Optional.of(new ProductDTO(new Product("Milk", "Food", 1.0, 50, "Liquid", 2.5f))));
        when(productServices.getDTOById(2)).thenReturn(Optional.of(new ProductDTO(new Product("Laptop", "Electronics", 3.0, 30, "Solid", 1000.0f))));
        when(productServices.getProductsById(productIds)).thenReturn(Map.of(
                1, new Product("Milk", "Food", 1.0, 50, "Liquid", 2.5f),
                2, new Product("Laptop", "Electronics", 3.0, 30, "Solid", 1000.0f)));
        Order createdOrder = orderServices.createOrder(productIds, quantities, "test@example.com");

        assertNotNull(createdOrder);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testDeleteOrder() {
        orderServices.deleteOrder(1);

        verify(orderRepository, times(1)).deleteById(1);
    }

    @Test
    public void testGetOrderById() {
        Order retrievedOrder = orderServices.getOrderById(1);
        assertNotNull(retrievedOrder);
    }

    @Test
    public void testGetOrderById_NotFound() {
        // Configura el mock para devolver un Optional vacío
        when(orderRepository.findById(2000)).thenReturn(Optional.empty());
    
        // Verifica que se lanza la excepción esperada
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderServices.getOrderById(2000);
        });
    
        // Opcional: verifica el mensaje de la excepción
        assertEquals("Order not found with ID: 2000", exception.getMessage());
    }

    @Test
    public void testGetAllOrders() {
        List<Order> allOrders = orderServices.getAllOrders();
        assertNotNull(allOrders, "The list of orders should not be null.");
            assertEquals(1, allOrders.size(), "The number of orders should be 1.");
    }

    @Test
    public void testGetAllOrdersOfAnUser() {
        String email = "fascencio@fi.uba.ar";
        List<Order> ordersForUser = orderServices.getAllOrdersOfAnUser(email);
        assertNotNull(ordersForUser, "The list of orders should not be null.");
        assertEquals(1, ordersForUser.size(), "The number of orders for the user should be 1.");
    }

    @Test
    public void testProcessOrder() {
        int orderId = 1;
        assertDoesNotThrow(() -> orderServices.processOrder(orderId));
    }

    @Test
    public void testShipOrder() {
        int orderId = 3;
    
        // Configura el estado de la orden como "EnProceso"
        Order order = new Order(null, "EnProceso", LocalDateTime.now(), "test@example.com");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    
        // Verifica que no lanza excepciones
        assertDoesNotThrow(() -> orderServices.shipOrder(orderId));
    }
    

    @Test
    public void testGetCreationDateTime() {
        int orderId = 1;
        LocalDateTime expectedDateTime = LocalDateTime.of(2024, 6, 3, 0, 0, 0, 0);
    
        // Configura el mock para devolver un Optional<Order>
        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(new Order(null, "Pedido", expectedDateTime, "")));
    
        // Llama al método bajo prueba
        LocalDateTime actualDateTime = orderServices.getCreationDateTime(orderId);
    
        // Verifica el resultado
        assertEquals(expectedDateTime, actualDateTime);
    }
    
    @Test
    public void testGetOrderedProducts() {
        int orderId = 1;
        Map<Integer, Integer> actualProducts = orderServices.getOrderedProducts(orderId);
        assertEquals(2, actualProducts.size());
    }

    // Tests for dynamic rules.
    @Test
    public void NoCompatibilityTypes() {
        Product product1 = new Product("Water", "Food", 1.0, 50, "Liquid", 2.5f);
        Product product2 = new Product("Helium", "Gas", 3.0, 30, "Gaseous", 1000.0f);
        when(productRepositoryJPA.findAll()).thenReturn(List.of(product1, product2));
        List<Integer> numbers1 = Arrays.asList(3, 4);
        List<Integer> numbers2 = Arrays.asList(10, 20);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderServices.createOrder(numbers1, numbers2, "fascencio@fi.uba.ar");
        });
    }

    @Test
    public void TooMuchWeight() {
        Product product = new Product("Iron Ingots", "Mineral", 600.0, 50, "Solid", 200.5f);
        when(productRepositoryJPA.findAll()).thenReturn(List.of(product));
        List<Integer> numbers1 = Arrays.asList(3);
        List<Integer> numbers2 = Arrays.asList(1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderServices.createOrder(numbers1, numbers2, "fascencio@fi.uba.ar");
        });
    }

    @Test
    public void TooMuchQuantity() {
        Product product1 = new Product("Golden Nuggets", "Food", 1.0, 10000, "Solid", 2.5f);
        Product product2 = new Product("Chicken Nuggets", "Food", 1.0, 10000, "Solid", 2.5f);
        Product product3 = new Product("Goat Nuggets", "Food", 1.0, 10000, "Solid", 2.5f);
        when(productRepositoryJPA.findAll()).thenReturn(List.of(product1, product2, product3));
        List<Integer> numbers1 = Arrays.asList(3, 4, 5);
        List<Integer> numbers2 = Arrays.asList(2, 2, 2);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderServices.createOrder(numbers1, numbers2, "fascencio@fi.uba.ar");
        });
    }
}
