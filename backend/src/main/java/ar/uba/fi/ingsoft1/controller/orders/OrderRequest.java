package ar.uba.fi.ingsoft1.controller.orders;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public record OrderRequest (
    @Getter @Setter List<OrderItem> items,
    @Setter @Getter String userEmail
) {
    // Getters and setters
    public List<Integer> getProductIds() {
        return items.stream().map(OrderItem::getProductId).toList();
    }

    public List<Integer> getQuantities() {
        return items.stream().map(OrderItem::getQuantity).toList();
    }
}
