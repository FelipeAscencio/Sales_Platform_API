package ar.uba.fi.ingsoft1.persistance;

import ar.uba.fi.ingsoft1.domain.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findByName(String name);
    Optional<Product> findById(int productId);
    List<Product> findAll();
    Product saveProduct(Product product);
    void deleteProduct(Product product);
}
