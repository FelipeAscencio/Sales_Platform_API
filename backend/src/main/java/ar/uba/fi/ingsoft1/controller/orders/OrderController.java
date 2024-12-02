package ar.uba.fi.ingsoft1.controller.orders;

import ar.uba.fi.ingsoft1.domain.Order;
import ar.uba.fi.ingsoft1.services.JwtService;
import ar.uba.fi.ingsoft1.services.OrderServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {
    private final OrderServices orderServices;
    private final JwtService jwtService;

    public OrderController(OrderServices orderServices, JwtService jwtService) {
        this.orderServices = orderServices;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestHeader("Authorization") String token,
                                             @RequestBody OrderRequest orderRequest) {
        if (!jwtService.validateToken(token, orderRequest.getUserEmail())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Order order;
        try {
            order = orderServices.createOrder(orderRequest.getProductIds(), orderRequest.getQuantities(), orderRequest.getUserEmail());
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/process")
    public ResponseEntity<?> processOrder(@RequestHeader("Authorization") String token, @PathVariable int id) {
        if (!jwtService.validateAdminToken(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            orderServices.processOrder(id);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/ship")
    public ResponseEntity<?> shipOrder(@RequestHeader("Authorization") String token, @PathVariable int id) {
        if (!jwtService.validateAdminToken(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            orderServices.shipOrder(id);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@RequestHeader("Authorization") String token, @PathVariable int id) {
        Order order = orderServices.getOrderById(id);
        if (!jwtService.validateToken(token, order.getUserEmail())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            orderServices.cancelOrder(id);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@RequestHeader("Authorization") String token, @PathVariable int id) {
        Order order = orderServices.getOrderById(id);
        if (!jwtService.validateToken(token, order.getUserEmail())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user")
    public ResponseEntity<List<OrderDTO>> getAllOrdersOfAnUser(
            @RequestHeader("Authorization") String token,
            @RequestParam("userEmail") String userEmail) {
        if (!jwtService.validateToken(token, userEmail) && !jwtService.validateAdminToken(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Order> orders = orderServices.getAllOrdersOfAnUser(userEmail);
        List<OrderDTO> ordersDTO = orderServices.getAllOrdersOfAnUser(userEmail).stream()
                .map(OrderDTO::new)
                .toList();
        return ResponseEntity.ok(ordersDTO);
    }


    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(@RequestHeader("Authorization") String token) {
        if (!jwtService.validateAdminToken(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Order> orders = orderServices.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}