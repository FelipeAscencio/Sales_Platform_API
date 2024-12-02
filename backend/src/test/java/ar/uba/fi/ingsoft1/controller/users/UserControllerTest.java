package ar.uba.fi.ingsoft1.controller.users;

import ar.uba.fi.ingsoft1.controller.users.UserDTO;
import ar.uba.fi.ingsoft1.services.JwtService;
import ar.uba.fi.ingsoft1.services.UserServices;
import org.apache.logging.log4j.util.Base64Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @Mock
    private UserServices userServices;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        when(jwtService.validateToken(eq("token"), anyString())).thenReturn(true);
    }

    @Test
    public void testCreateUser_Success() {
        UserDTO userDTO = new UserDTO("John", "Doe", 30, "photo", "john.doe@example.com", "male", "address", "momName", "petName", "color", false);
        String authHeader = "Basic " + Base64.getEncoder().encodeToString("email:password".getBytes());

        when(userServices.registerUser(anyString(), anyString(), anyString(), anyString(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        ResponseEntity<String> response = userController.createUser(userDTO, authHeader);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User created successfully", response.getBody());
    }

    @Test
    public void testCreateUser_MissingAuthHeader() {
        UserDTO userDTO = new UserDTO("John", "Doe", 30, "photo", "john.doe@example.com", "male", "address", "momName", "petName", "color", false);

        ResponseEntity<String> response = userController.createUser(userDTO, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    public void testCreateUser_InvalidAuthFormat() {
        UserDTO userDTO = new UserDTO("John", "Doe", 30, "photo", "john.doe@example.com", "male", "address", "momName", "petName", "color", false);
        String authHeader = "Basic " + Base64.getEncoder().encodeToString("emailpassword".getBytes());

        ResponseEntity<String> response = userController.createUser(userDTO, authHeader);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    public void testCreateUser_RegistrationFailure() {
        UserDTO userDTO = new UserDTO("John", "Doe", 30, "photo", "john.doe@example.com", "male", "address", "momName", "petName", "color", false);
        String authHeader = "Basic " + Base64.getEncoder().encodeToString("email:password".getBytes());

        when(userServices.registerUser(anyString(), anyString(), anyString(), anyString(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of("Registration failed"));

        ResponseEntity<String> response = userController.createUser(userDTO, authHeader);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Registration failed", response.getBody());
    }

    @Test
    public void testLoginUser_Success() {
        String authHeader = "Basic " + Base64.getEncoder().encodeToString("email:password".getBytes());
        UserDTO userDTO = new UserDTO("John", "Doe", 30, "photo", "john.doe@example.com", "male", "address", "momName", "petName", "color", false);

        when(userServices.login(anyString(), anyString())).thenReturn(userDTO);
        when(userServices.getByEmail(anyString())).thenReturn(Optional.of(userDTO));
        when(jwtService.generateToken(userDTO)).thenReturn("token");

        ResponseEntity<?> response = userController.loginUser(authHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertEquals("token", loginResponse.getToken());
        assertEquals(userDTO, loginResponse.getUser());
    }

    @Test
    public void testLoginUser_MissingAuthHeader() {
        ResponseEntity<?> response = userController.loginUser(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    public void testLoginUser_InvalidAuthFormat() {
        String authHeader = "Basic " + Base64Util.encode("emailpassword");
        ResponseEntity<?> response = userController.loginUser(authHeader);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    public void testLoginUser_InvalidCredentials() {
        String authHeader = "Basic " + Base64Util.encode("email:password");

        when(userServices.login(anyString(), anyString())).thenReturn(null);

        ResponseEntity<?> response = userController.loginUser(authHeader);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    public void testUpdateUser_Success() {
        String token = "token";
        UserDTO userDTO = new UserDTO("John", "Doe", 30, "photo", "john.doe@example.com", "male", "address", "momName", "petName", "color", false);
        UserDTO updatedUserDTO = new UserDTO("John", "Doe", 31, "newPhoto", "john.doe@example.com", "male", "newAddress", "momName", "petName", "color", false);

        when(userServices.getByEmail(anyString())).thenReturn(Optional.of(userDTO));
        when(userServices.updateUser(anyString(), any())).thenReturn(updatedUserDTO);

        ResponseEntity<?> response = userController.updateUser(token, updatedUserDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUserDTO, response.getBody());
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        String token = "token";
        UserDTO userDTO = new UserDTO("John", "Doe", 30, "photo", "john.doe@example.com", "male", "address", "momName", "petName", "color", false);

        when(userServices.getByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.updateUser(token, userDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetUserByEmail_Success() {
        UserDTO userDTO = new UserDTO("John", "Doe", 30, "photo", "john.doe@example.com", "male", "address", "momName", "petName", "color", false);
        String token = "token";

        when(userServices.getByEmail(anyString())).thenReturn(Optional.of(userDTO));
        when(jwtService.validateToken(eq("token"), eq(userDTO.getEmail()))).thenReturn(true);

        ResponseEntity<UserDTO> response = userController.getProfile(token, userDTO.email());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    public void testGetUserByEmail_UserNotFound() {
        UserDTO userDTO = new UserDTO("John", "Doe", 30, "photo", "john.doe@example.com", "male", "address", "momName", "petName", "color", false);
        String token = "token";
        when(userServices.getByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<UserDTO> response = userController.getProfile(token, userDTO.email());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
