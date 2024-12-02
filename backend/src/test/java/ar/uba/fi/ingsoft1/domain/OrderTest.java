package ar.uba.fi.ingsoft1.domain;

import ar.uba.fi.ingsoft1.controller.products.ProductDTO;
import ar.uba.fi.ingsoft1.services.ProductServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderTest {

    @MockBean
    private ProductServices productServices;

    @BeforeEach
    void setUp() {
        when(productServices.addProduct(anyString(), anyString(), anyDouble(), anyInt(), anyString(), anyFloat()))
                .thenReturn(new ProductDTO(new Product("Milk", "Food", 1.0, 50, "Liquid", 2.5f)));
        when(productServices.getDTOById(anyInt()))
                .thenReturn(Optional.of(new ProductDTO(new Product("Milk", "Food", 1.0, 50, "Liquid", 2.5f))));
        when(productServices.getById(anyInt()))
                .thenReturn(Optional.of((new Product("Milk", "Food", 1.0, 50, "Liquid", 2.5f))));

    }

    @Test
    void testOrderStatePedido() {
        Order order1 = new Order(List.of(1), List.of(1), "user@example.com");

        assertFalse(order1.getOrderedProducts().isEmpty());
        assertEquals(1, order1.getOrderedProducts().get(1));
        assertEquals("PedidoState", order1.getState().getClass().getSimpleName());

        order1.processOrder();
        assertEquals("EnProcesoState", order1.getState().getClass().getSimpleName());

        Order order2 = new Order(List.of(1), List.of(1), "user@example.com");
        IllegalStateException exception = assertThrows(IllegalStateException.class, order2::shipOrder);
        assertEquals("El pedido debe estar en proceso antes de enviarse.", exception.getMessage());

        Order order3 = new Order(List.of(1), List.of(1), "user@example.com");
        order3.cancelOrder();
        assertEquals("CanceladoState", order3.getState().getClass().getSimpleName());

        exception = assertThrows(IllegalStateException.class, () -> order3.cancelOrder());
        assertEquals("El pedido ya está cancelado.", exception.getMessage());
    }

    @Test
    void testOrderStateEnProceso() {
        Order order = new Order(List.of(1), List.of(1), "user@example.com");
        order.processOrder();

        assertEquals("EnProcesoState", order.getState().getClass().getSimpleName());

        IllegalStateException exception = assertThrows(IllegalStateException.class, order::processOrder);
        assertEquals("El pedido ya está en proceso.", exception.getMessage());

        order.shipOrder();
        assertEquals("EnviadoState", order.getState().getClass().getSimpleName());

        exception = assertThrows(IllegalStateException.class, () -> order.cancelOrder());
        assertEquals("El pedido no puede cancelarse una vez enviado.", exception.getMessage());
    }

    @Test
    void testOrderStateEnviado() {
        Order order = new Order(List.of(1), List.of(1), "user@example.com");
        order.processOrder();
        order.shipOrder();

        assertEquals("EnviadoState", order.getState().getClass().getSimpleName());

        IllegalStateException exception = assertThrows(IllegalStateException.class, order::processOrder);
        assertEquals("El pedido ya fue enviado y no puede procesarse nuevamente.", exception.getMessage());

        exception = assertThrows(IllegalStateException.class, order::shipOrder);
        assertEquals("El pedido ya fue enviado.", exception.getMessage());

        exception = assertThrows(IllegalStateException.class, () -> order.cancelOrder());
        assertEquals("El pedido no puede cancelarse una vez enviado.", exception.getMessage());
    }

    @Test
    void testOrderStateCancelado() {
        Order order = new Order(List.of(1), List.of(1), "user@example.com");
        order.cancelOrder();

        assertEquals("CanceladoState", order.getState().getClass().getSimpleName());

        IllegalStateException exception = assertThrows(IllegalStateException.class, order::processOrder);
        assertEquals("El pedido ha sido cancelado y no puede procesarse.", exception.getMessage());

        exception = assertThrows(IllegalStateException.class, order::shipOrder);
        assertEquals("El pedido ha sido cancelado y no puede enviarse.", exception.getMessage());

        exception = assertThrows(IllegalStateException.class, () -> order.cancelOrder());
        assertEquals("El pedido ya está cancelado.", exception.getMessage());
    }

    @Test
    void testCancelOrderAfter24Hours() {
        Order order = new Order(List.of(1), List.of(1), "user@example.com");
        order.processOrder();
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> order.cancelOrder());
        assertEquals("El pedido no puede cancelarse una vez en proceso.", exception.getMessage());
    }
}