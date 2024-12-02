package ar.uba.fi.ingsoft1.controller.orders;

public record OrderItem (
    int productId,
    int quantity
) {
    // Getters and setters
    public int getProductId() {
        return productId;
    }
    public int getQuantity() {
        return quantity;
    }
}
