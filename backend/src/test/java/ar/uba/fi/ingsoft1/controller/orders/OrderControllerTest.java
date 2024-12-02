package ar.uba.fi.ingsoft1.controller.orders;

import ar.uba.fi.ingsoft1.domain.Order;
import ar.uba.fi.ingsoft1.services.JwtService;
import ar.uba.fi.ingsoft1.services.OrderServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    @Mock
    private OrderServices orderServices;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrder_Unauthorized() {
        when(jwtService.validateToken(anyString(), anyString())).thenReturn(false);
        List<OrderItem> items = Arrays.asList(new OrderItem(1, 1), new OrderItem(2, 2));
        OrderRequest orderRequest = new OrderRequest(items, "user@example.com");

        ResponseEntity<?> response = orderController.createOrder("token", orderRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testCreateOrder_Success() {
        when(jwtService.validateToken(anyString(), anyString())).thenReturn(true);
        Order order = new Order(Arrays.asList(1, 2), Arrays.asList(1, 2), "user@example.com");
        when(orderServices.createOrder(anyList(), anyList(), anyString())).thenReturn(order);
        List<OrderItem> items = Arrays.asList(new OrderItem(1, 1), new OrderItem(2, 2));
        OrderRequest orderRequest = new OrderRequest(items, "user@example.com");

        ResponseEntity<?> response = orderController.createOrder("token", orderRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(order, response.getBody());
    }

    @Test
    public void testProcessOrder_Unauthorized() {
        when(jwtService.validateAdminToken(anyString())).thenReturn(false);

        ResponseEntity<?> response = orderController.processOrder("token", 1);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testProcessOrder_Success() {
        when(jwtService.validateAdminToken(anyString())).thenReturn(true);

        ResponseEntity<?> response = orderController.processOrder("token", 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderServices, times(1)).processOrder(1);
    }

    @Test
    public void testGetOrderById_Unauthorized() {
        Order order = new Order(Arrays.asList(1, 2), Arrays.asList(1, 2), "user@example.com");
        when(orderServices.getOrderById(anyInt())).thenReturn(order);
        when(jwtService.validateToken(anyString(), anyString())).thenReturn(false);

        ResponseEntity<Order> response = orderController.getOrderById("token", 1);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetOrderById_Success() {
        Order order = new Order(Arrays.asList(1, 2), Arrays.asList(1, 2), "user@example.com");
        when(orderServices.getOrderById(anyInt())).thenReturn(order);
        when(jwtService.validateToken(anyString(), anyString())).thenReturn(true);

        ResponseEntity<Order> response = orderController.getOrderById("token", 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(order, response.getBody());
    }

    @Test
    public void testGetAllOrdersOfAnUser_Unauthorized() {
        when(jwtService.validateToken(anyString(), anyString())).thenReturn(false);
        ResponseEntity<List<OrderDTO>> response = orderController.getAllOrdersOfAnUser("token", "user@example.com");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetAllOrdersOfAnUser_Success() {
        when(jwtService.validateToken(anyString(), anyString())).thenReturn(true);
        List<Order> orders = Arrays.asList(
            new Order(Arrays.asList(1, 2), Arrays.asList(1, 2), "user@example.com"),
            new Order(Arrays.asList(1, 2), Arrays.asList(1, 2), "user@example.com")
        );
        List<OrderDTO> ordersDTO = orders.stream()
                .map(OrderDTO::new)
                .toList();
        when(orderServices.getAllOrdersOfAnUser(anyString())).thenReturn(orders);
        ResponseEntity<List<OrderDTO>> response = orderController.getAllOrdersOfAnUser("token", "user@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ordersDTO, response.getBody());
    }

    @Test
    public void testGetAllOrders_Unauthorized() {
        when(jwtService.validateAdminToken(anyString())).thenReturn(false);

        ResponseEntity<List<Order>> response = orderController.getAllOrders("token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetAllOrders_Success() {
        when(jwtService.validateAdminToken(anyString())).thenReturn(true);
        List<Order> orders = Arrays.asList(
                new Order(Arrays.asList(1, 2), Arrays.asList(1, 2), "user@example.com"),
                new Order(Arrays.asList(1, 2), Arrays.asList(1, 2), "user@example.com")
        );
        when(orderServices.getAllOrders()).thenReturn(orders);

        ResponseEntity<List<Order>> response = orderController.getAllOrders("token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orders, response.getBody());
    }
}