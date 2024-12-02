package ar.uba.fi.ingsoft1.services;

import static org.junit.jupiter.api.Assertions.*;

import ar.uba.fi.ingsoft1.controller.users.UserDTO;
import ar.uba.fi.ingsoft1.domain.User;
import ar.uba.fi.ingsoft1.persistance.UserRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
@Rollback
class UserServicesTest {

        @Autowired
        private UserServices userServicesDB;

        @Autowired
        private UserRepositoryJPA userRepositoryJPA;

        // Initializes a test user database before each test.
        @BeforeEach
        void setUp() {
            User newUser= new User.Builder("Franco", "Bossi", "fbossi@fi.uba.ar", "francobossifiuba", 20, "fotofranco.jpg", "Male","Some place 123",  "Jesica", "Firulais", "Rojo")
                    .build();
            User newUser2 = new User.Builder("Matias", "Venglar", "mvenglar@fi.uba.ar", "matiasvenglarfiuba", 22, "fotomatias.jpg", "Male", "Another place 456", "Ana", "Michigan", "Violeta")
                    .build();

            userRepositoryJPA.saveUser(newUser2);
            userRepositoryJPA.saveUser(newUser);
        }



    @Test
    public void testLoginValidUser() {
        UserDTO result = userServicesDB.login("fbossi@fi.uba.ar", "francobossifiuba");
        assertNotNull(result, "Login should be successful for a valid user.");
    }

    @Test
    public void testLoginInvalidUser() {
        UserDTO result = userServicesDB.login("invalid_user@fmed.ar", "invalidPassword");
        assertNull(result, "Login should fail for an invalid user.");
    }

    @Test
    public void testLoginWithNullEmail() {
        UserDTO result = userServicesDB.login(null, "password123");
        assertNull(result, "Login should fail if the email is null.");
    }

    @Test
    public void testLoginWithNullPassword() {
        UserDTO result = userServicesDB.login("invalid_user@fmed.ar", null);
        assertNull(result, "Login should fail if the password is null.");
    }

    @Test
    public void testCheckUserLoggedInStatus() {
        userServicesDB.login("fbossi@fi.uba.ar", "francobossifiuba");
        assertTrue(userServicesDB.checkUserStatus(), "User status should be 'logged in' after a successful login.");
    }

    @Test
    public void testCheckUserNotLoggedInStatus() {
        assertTrue(userServicesDB.checkUserStatus(), "User status should be logged in' without a login.");
    }

    @Test
    public void testRegisterWithInvalidAgeBelowMinimum() {
        Optional<String> result = userServicesDB.registerUser(
                "Matias", "Venglar", "matias@fi.uba.ar", "contraseniagenerica", 17,
                "photo.jpg", "Male", "Paseo colon 123", "Carla", "Pedro", "Violeta");
        assertTrue(result.isPresent());
        assertEquals("User registration failed: Minimum age is 18 years.", result.get());
    }

    @Test
    public void testRegisterWithNullFirstName() {
        Optional<String> result = userServicesDB.registerUser(
                null, "Venglar", "matias@fi.uba.ar", "contraseniagenerica", 22,
                "photo.jpg", "Male", "Paseo colon 123", "Juana", "Pedro", "Violeta");
        assertTrue(result.isPresent());
        assertEquals("User registration failed: First Name is a required field.", result.get());
    }

    @Test
    public void testRegisterWithNullLastName() {
        Optional<String> result = userServicesDB.registerUser(
                "Matias", null, "matias@fi.uba.ar", "contraseniagenerica", 22,
                "photo.jpg", "Male", "Paseo colon 123", "Juana", "Pedro", "Violeta");
        assertTrue(result.isPresent());
        assertEquals("User registration failed: Last Name is a required field.", result.get());
    }

    @Test
    void testEmailWithoutAtSymbol() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new User.Builder("Matias", "Venglar", "matiasfi.uba.ar", "contraseniagenerica", 22, "photo.jpg", "Male", "Paseo colon 123", "Juana", "Pedro", "Violeta")
                .build());
        assertEquals("The email must contain the character'@'.", exception.getMessage());
    }

    @Test
    void testEmailWithoutDotAfterAt() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new User.Builder("Matias", "Venglar", "matias@fiubaar", "contraseniagenerica", 22, "photo.jpg", "Male", "Paseo colon 123", "Juana", "Pedro", "Violeta")
                .build());
        assertEquals("The email must contain a period after the '@'.", exception.getMessage());
    }

    @Test
    void testEmailWithInvalidCharacters() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new User.Builder("Matias", "Venglar", "matias@fi.uba.ar!!", "contraseniagenerica", 22, "photo.jpg", "Male", "Paseo colon 123", "Juana", "Pedro", "Violeta")
                .build());
        assertEquals("The email contains illegal characters.", exception.getMessage());
    }

    @Test
    void testEmptyEmail() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new User.Builder("Matias", "Venglar", "", "contraseniagenerica", 22, "photo.jpg", "Male", "Paseo colon 123", "Juana", "Pedro", "Violeta")
                .build());
        assertEquals("The email cannot be empty.", exception.getMessage());
    }

    @Test
    public void testRegisterWithNullPassword() {
        Optional<String> result = userServicesDB.registerUser(
                "Matias", "Venglar", "matias@fi.uba.ar", null, 22,
                "photo.jpg", "Male", "Paseo colon 123", "Juana", "Pedro", "Violeta");
        assertTrue(result.isPresent());
        assertEquals("User registration failed: Password is a required field.", result.get());
    }

    @Test
    public void testRegisterWithInvalidGender() {
        Optional<String> result = userServicesDB.registerUser(
                "Matias", "Venglar", "matias@fi.uba.ar", "contraseniagenerica", 22,
                "photo.jpg", "OtherGender", "Paseo colon 123", "Juana", "Pedro", "Violeta");
        assertTrue(result.isPresent());
        assertEquals("User registration failed: Invalid gender, it must be 'Male' or 'Female'.", result.get());
    }

    @Test
    public void testRegisterWithValidData() {
        Optional<String> result = userServicesDB.registerUser(
                "Matias", "Venglar", "matias@fi.uba.ar", "contraseniagenerica", 22,
                "photo.jpg", "Male", "Paseo colon 123", "Juana", "Pedro", "Violeta");
        assertFalse(result.isPresent());
    }

    @Test
    public void testDefaultUsersAreNotAdmins() {
        User user1 = new User.Builder("Carlos", "Lopez", "carlos@fi.uba.ar", "password123", 25, "photo.jpg", "Male", "Paseo colon 123", "Juana", "Pedro", "Violeta").build();
        User user2 = new User.Builder("Lucia", "Garcia", "lucia@fi.uba.ar", "password456", 30, "photo.jpg", "Male", "Paseo colon 123", "Juana", "Pedro", "Violeta").build();
        assertFalse(userServicesDB.isAdmin(user1));
        assertFalse(userServicesDB.isAdmin(user2));
    }

    @Test
    public void testMakeUserAdmin() {
        User user = new User.Builder("Fernando", "Bossi", "ferbossi@fi.uba.ar", "password789", 28, "photo.jpg", "Male", "Paseo colon 123", "Juana", "Pedro", "Violeta").build();
        userRepositoryJPA.saveUser(user);
        UserDTO userDTO = new UserDTO(user);
        User otherUser = new User.Builder("Ana", "Martinez", "amartinez@fi.uba.ar", "password101", 32, "photo.jpg", "Male", "Paseo colon 123", "Juana", "Pedro", "Violeta").build();
        userServicesDB.makeAdmin(userDTO);
        assertTrue(userServicesDB.isAdmin(user));
        assertFalse(userServicesDB.isAdmin(otherUser));
    }

    @Test
    public void testRemoveAdminPrivileges() {
        User user = new User.Builder("Fernando", "Bossi", "fernbossi@fi.uba.ar", "password789", 28, "photo.jpg", "Male", "Paseo colon 123", "Juana", "Pedro", "Violeta").build();
        userRepositoryJPA.saveUser(user);
        UserDTO userDTO = new UserDTO(user);
        userServicesDB.makeAdmin(userDTO);
        userServicesDB.removeAdmin(userDTO);
        assertFalse(userServicesDB.isAdmin(user));
    }

    @Test
    public void testChangePasswordValid() {
        userServicesDB.changePassword("fbossi@fi.uba.ar", "newPassword");
        User user_2 = userRepositoryJPA.findByEmail("fbossi@fi.uba.ar").get();
        assertEquals("newPassword",  user_2.getPassword());
    }

    @Test
    public void testChangePasswordInvalidUser() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userServicesDB.changePassword(null, "newPassword"));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = userServicesDB.getAllUsers();
        assertEquals(2, users.size(), "The list should contain 2 users.");
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("fbossi@fi.uba.ar")), "The list should contain Franco.");
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("mvenglar@fi.uba.ar")), "The list should contain Matias.");
    }

    @Test
    public void testRecoverPasswordValid() {
        Optional<String> result = userServicesDB.recoverPassword("fbossi@fi.uba.ar", "Jesica", "Firulais", "Rojo");
        assertTrue(result.isPresent());
        assertTrue(result.get().startsWith("Password successfully updated"), "Password should be successfully updated.");
    }

    @Test
    public void testRecoverPasswordInvalidData() {
        Optional<String> result = userServicesDB.recoverPassword("fbossi@fi.uba.ar", "Jesica", "Firulais", "Green");
        assertTrue(result.isPresent());
        assertEquals("The provided details do not match our records.", result.get(), "Should return an error message.");
    }

    @Test
    public void testRecoverPasswordUserNotFound() {
        Optional<String> result = userServicesDB.recoverPassword("nonexistent@fi.uba.ar", "Jesica", "Firulais", "Rojo");
        assertTrue(result.isPresent());
        assertEquals("No user was found with the email provided.", result.get(), "Should return an error message.");
    }
}

