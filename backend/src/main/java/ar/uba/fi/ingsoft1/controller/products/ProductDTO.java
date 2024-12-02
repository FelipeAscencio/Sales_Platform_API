package ar.uba.fi.ingsoft1.controller.products;

import ar.uba.fi.ingsoft1.domain.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public record ProductDTO (
     @Getter @Setter int id,
     @Getter @Setter String name,
     @Getter @Setter String type,
     @Getter @Setter double weight,
     @Getter @Setter float price,
     @Getter @Setter int quantity,
     @Getter @Setter String state,
     @Getter @Setter Map<String, String> extraAttributes
) {
    public ProductDTO(Product product) {
        this(product.getId(), product.getName(), product.getType(), product.getWeight(), product.getPrice(), product.getQuantity(), product.getState(), product.getAllAttributes());
    }

    public static List<ProductDTO> fromProductList(List<Product> products) {
        return products.stream().map(ProductDTO::new).toList();
    }
}
