package ar.uba.fi.ingsoft1.controller.orders;

import ar.uba.fi.ingsoft1.domain.Order;

import java.util.List;
import java.util.stream.Collectors;

public record OrderDTO (
        int id,
        List<OrderItem> items,
        String userEmail,
        String status,
        String createdAt,
        String confirmedAt,
        String sentAt
) {
    public OrderDTO(Order order) {
        this(
                order.getId(),
                order.getOrderedProducts().entrySet().stream()
                        .map(entry -> new OrderItem(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList()),
                order.getUserEmail(),
                order.getState().toString(),
                order.getCreationDateTime().toString(),
                order.getProcessDateTime() != null ? order.getProcessDateTime().toString() : null,
                order.getShipDateTime() != null ? order.getShipDateTime().toString() : null
        );
    }
    // Getters and setters
    public int getId() {
        return id;
    }
    public List<OrderItem> getItems() {
        return items;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public String getStatus() {
        return status;
    }
}
