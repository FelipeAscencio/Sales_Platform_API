package ar.uba.fi.ingsoft1.persistance;
import ar.uba.fi.ingsoft1.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepositoryJPA extends JpaRepository<Product, Integer> {
    Optional<Product> findByName(String name);
    default Product saveProduct(Product product) {
        return save(product);
    }
    default void deleteProduct(Product product) {
        delete(product);
    }
}
