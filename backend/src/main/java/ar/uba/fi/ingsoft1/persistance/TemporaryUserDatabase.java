package ar.uba.fi.ingsoft1.persistance;

import ar.uba.fi.ingsoft1.domain.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TemporaryUserDatabase implements UserRepository {
    private final List<User> users = new ArrayList<>();

    public TemporaryUserDatabase() {
        users.add(new User.Builder("Franco", "Bossi", "fbossi@fi.uba.ar", "francobossifiuba", 20, "fotofranco.jpg", "Male","Some place 123",  "Jesica", "Firulais", "Rojo")
                .build());
        users.add(new User.Builder("Matias", "Venglar", "mvenglar@fi.uba.ar", "matiasvenglarfiuba", 22, "fotomatias.jpg", "Male", "Another place 456", "Ana", "Michigan", "Violeta")
                .build());
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.stream().filter(user -> user.getEmail().equals(email)).findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public User saveUser(User user){
        boolean replaced = false;
        for (int i = 0; i < users.size(); i++) {
            if (Objects.equals(users.get(i).getEmail(), user.getEmail())) {
                users.set(i, user);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            users.add(user);
        }
        return user;
    }

    @Override
    public void delete(String email) {
        Optional<User> user = findByEmail(email);
        user.ifPresent(users::remove);
    }
}
