package ar.uba.fi.ingsoft1.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserTest {
    private User user;

    // Initializes a test user before each test.
    @BeforeEach
    public void setUp() {
        user = new User.Builder("Group", "10", "group.10@ingsoft1.com", "group10#forever", 23, "photo.jpg", "Male", "Paseo Colón 123", "Carla", "Doggy", "Lila")
                .build();
    }

    @Test
    public void testGetFirstName() {
        assertEquals("Group", user.getFirstName());
    }

    @Test
    public void testSetFirstNameValid() {
        user.setFirstName("Diego");
        assertEquals("Diego", user.getFirstName());
    }

    @Test
    public void testSetFirstNameInvalid() {
        assertThrows(IllegalArgumentException.class, () -> user.setFirstName(null));
    }

    @Test
    public void testGetLastName() {
        assertEquals("10", user.getLastName());
    }

    @Test
    public void testSetLastNameValid() {
        user.setLastName("San Lorenzo");
        assertEquals("San Lorenzo", user.getLastName());
    }

    @Test
    public void testSetLastNameInvalid() {
        assertThrows(IllegalArgumentException.class, () -> user.setLastName(null));
    }

    @Test
    public void testGetEmail() {
        assertEquals("group.10@ingsoft1.com", user.getEmail());
    }

    @Test
    public void testSetEmailValid() {
        user.setEmail("anotheremail@ingsoft1.ar");
        assertEquals("anotheremail@ingsoft1.ar", user.getEmail());
    }

    @Test
    public void testSetEmailInvalid() {
        assertThrows(IllegalArgumentException.class, () -> user.setEmail(null));
    }

    @Test
    public void testGetPhoto() {
        assertEquals("photo.jpg", user.getPhoto());
    }

    @Test
    public void testSetPhotoValid() {
        user.setPhoto("newphoto.png");
        assertEquals("newphoto.png", user.getPhoto());
    }

    @Test
    public void testSetPhotoInvalid() {
        assertThrows(IllegalArgumentException.class, () -> user.setPhoto(null));
    }

    @Test
    public void testGetAge() {
        assertEquals(23, user.getAge());
    }

    @Test
    public void testSetAgeValid() {
        user.setAge(20);
        assertEquals(20, user.getAge());
    }

    @Test
    public void testSetAgeInvalid() {
        assertThrows(IllegalArgumentException.class, () -> user.setAge(-3)); // Invalid age.
    }

    @Test
    public void testSetUnderage() {
        assertThrows(IllegalArgumentException.class, () -> user.setAge(15)); // Underage.
    }

    @Test
    public void testGetGender() {
        assertEquals("Male", user.getGender());
    }

    @Test
    public void testSetGenderValid() {
        user.setGender("Female");
        assertEquals("Female", user.getGender());
    }

    @Test
    public void testSetGenderInvalid() {
        assertThrows(IllegalArgumentException.class, () -> user.setGender("Undefined"));
    }

    @Test
    public void testGetAddress() {
        assertEquals("Paseo Colón 123", user.getAddress());
    }

    @Test
    public void testSetAddressValid() {
        user.setAddress("Las Heras 456");
        assertEquals("Las Heras 456", user.getAddress());
    }

    @Test
    public void testSetAddressInvalid() {
        assertThrows(IllegalArgumentException.class, () -> user.setAddress(null));
    }

    @Test
    public void testGetPassword() {
        assertEquals("group10#forever", user.getPassword());
    }

    @Test
    public void testSetPasswordValid() {
        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());
    }

    @Test
    public void testSetPasswordInvalid() {
        assertThrows(IllegalArgumentException.class, () -> user.setPassword(null));
    }

    @Test
    public void testGetMomName() {
        assertEquals("Carla", user.getMomName());
    }

    @Test
    public void testGetFirstPetName() {
        assertEquals("Doggy", user.getFirstPetName());
    }

    @Test
    public void testGetFavoriteColor() {
        assertEquals("Lila", user.getFavoriteColor());
    }

    @Test
    public void testGetAdmin() {
        assertFalse(user.getAdmin());
    }

    @Test
    public void testSetAdminValid() {
        user.setAdmin(true);
        assertTrue(user.getAdmin());
    }

    @Test
    public void testSetAdminInvalid() {
        assertDoesNotThrow(() -> user.setAdmin(false));
    }
}

