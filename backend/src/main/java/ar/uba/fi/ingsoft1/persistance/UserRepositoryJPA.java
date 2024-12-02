package ar.uba.fi.ingsoft1.persistance;

import ar.uba.fi.ingsoft1.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositoryJPA extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);
    default User saveUser(User newUser){
        return save(newUser);
    }
}
