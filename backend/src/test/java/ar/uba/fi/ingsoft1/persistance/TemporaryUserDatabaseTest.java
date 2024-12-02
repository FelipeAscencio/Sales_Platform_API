package ar.uba.fi.ingsoft1.persistance;

import ar.uba.fi.ingsoft1.domain.User;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

public class TemporaryUserDatabaseTest {
    private TemporaryUserDatabase database;

    @BeforeEach
    public void setUp() {
        database = new TemporaryUserDatabase();
    }

    @Test
    public void testFindExistingUser() {
        Optional<User> user = database.findByEmailAndPassword("fbossi@fi.uba.ar", "francobossifiuba");
        assertTrue(user.isPresent());
    }

    @Test
    public void testFindNonExistingUser() {
        Optional<User> user = database.findByEmailAndPassword("noteconozco@filo.uba.ar", "seronoser");
        assertFalse(user.isPresent());
    }

    @Test
    public void testAddUser() {
        User newUser = new User.Builder("Felipe", "Ascencio", "fascencio@fi.uba.ar", "felipeascenciofiuba", 21, "fotofelipe.jpg", "Male", "Muy muy lejano 789", "Greta", "Juana", "Naranja")
                .build();
        database.saveUser(newUser);
        Optional<User> user = database.findByEmailAndPassword("fascencio@fi.uba.ar", "felipeascenciofiuba");
        assertTrue(user.isPresent());
    }

    @Test
    public void testAddExistingUser() {
        User newUser = new User.Builder("Matias", "Venglar", "mvenglar@fi.uba.ar", "matiasvenglarfiuba", 22, "fotomatias.jpg", "Male", "Otro lugar 456", "Maria", "Pepe", "Verde")
                .build();
        database.saveUser(newUser);
        Optional<User> user = database.findByEmail("mvenglar@fi.uba.ar");
        assertTrue(user.isPresent());
        assertEquals("Otro lugar 456", user.get().getAddress());
    }

    @Test
    public void testDeleteExistingUser() {
        Optional<User> user = database.findByEmail("fbossi@fi.uba.ar");
        assertTrue(user.isPresent());
        database.delete("fbossi@fi.uba.ar");
        user = database.findByEmail("fbossi@fi.uba.ar");
        assertFalse(user.isPresent());
    }

    @Test
    public void testDeleteNonExistingUser() {
        database.delete("noexiste@ejemplo.com");
        Optional<User> userFranco = database.findByEmail("fbossi@fi.uba.ar");
        Optional<User> userMatias = database.findByEmail("mvenglar@fi.uba.ar");
        assertTrue(userFranco.isPresent());
        assertTrue(userMatias.isPresent());
    }

    @Test
    public void testGetUserByEmailExisting() {
        Optional<User> user = database.findByEmail("mvenglar@fi.uba.ar");
        assertTrue(user.isPresent());
        assertEquals("Matias", user.get().getFirstName());
    }

    @Test
    public void testGetUserByEmailNonExisting() {
        Optional<User> user = database.findByEmail("noexiste@ejemplo.com");
        assertFalse(user.isPresent());
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User.Builder("Felipe", "Ascencio", "fascencio@fi.uba.ar", "felipeascenciofiuba", 21, "fotofelipe.jpg", "Male", "Muy muy lejano 789", "Greta", "Juana", "Naranja")
                .build();
        User user2 = new User.Builder("Matias", "Venglar", "mavenglar@fi.uba.ar", "matiasvenglarfiuba", 22, "fotomatias.jpg", "Male", "Otro lugar 456", "Maria", "Pepe", "Verde")
                .build();
        database.saveUser(user1);
        database.saveUser(user2);
        List<User> users = database.findAll();
        assertEquals(4, users.size(), "There should be 4 users in the database.");
        assertTrue(users.contains(user1), "User1 should be in the list.");
        assertTrue(users.contains(user2), "User2 should be in the list.");
    }
}
