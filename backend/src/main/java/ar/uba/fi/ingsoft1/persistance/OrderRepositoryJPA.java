package ar.uba.fi.ingsoft1.persistance;

import ar.uba.fi.ingsoft1.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepositoryJPA extends JpaRepository<Order, Integer> {
    default List<Order> getAllOrders() {
        return findAll();
    }

    List<Order> findByUserEmail(String email);
}
