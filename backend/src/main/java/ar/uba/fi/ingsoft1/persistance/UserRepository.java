package ar.uba.fi.ingsoft1.persistance;

import ar.uba.fi.ingsoft1.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmailAndPassword(String email, String password);
    User saveUser (User newUser);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void delete(String email);
}
