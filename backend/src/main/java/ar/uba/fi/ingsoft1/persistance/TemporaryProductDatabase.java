package ar.uba.fi.ingsoft1.persistance;

import ar.uba.fi.ingsoft1.domain.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TemporaryProductDatabase implements ProductRepository {
    private static final int ZERO = 0;
    private final List<Product> products;
    private int lastAssignedId = ZERO;

    public TemporaryProductDatabase() {
        products = new ArrayList<>();

        saveProduct(Product.ProductFactory.createProductWithAttributes(
                "T-shirt", "clothing", 0.2, 50, "Solid", 100,
                Map.of("color", "red", "size", "medium")
        ));

        saveProduct(Product.ProductFactory.createProductWithAttributes(
                "Milk", "food", 1.0, 100, "Liquid", 30.5F,
                Map.of("size", "large", "flavour", "strawberry")
        ));
    }

    @Override
    public Optional<Product> findByName(String name) {
        return Optional.ofNullable(products.stream()
                .filter(product -> product.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null));
    }

    @Override
    public Optional<Product> findById(int productId) {
        return Optional.ofNullable(products.stream()
                .filter(product -> product.getId() == productId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + productId + " not found.")));
    }

    @Override
    public void deleteProduct(Product p) {
        products.removeIf(product -> product.getName().equalsIgnoreCase(p.getName()));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    @Override
    public Product saveProduct(Product updatedProduct) {
        boolean replaced = false;
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == updatedProduct.getId()) {
                products.set(i, updatedProduct);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            updatedProduct.setId(++lastAssignedId);
            products.add(updatedProduct);
        }
        return updatedProduct;
    }
}
