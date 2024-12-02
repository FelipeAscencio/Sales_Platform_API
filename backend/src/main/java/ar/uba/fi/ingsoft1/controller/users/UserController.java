package ar.uba.fi.ingsoft1.controller.users;

import ar.uba.fi.ingsoft1.services.JwtService;
import ar.uba.fi.ingsoft1.services.UserServices;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserServices service;
    private final JwtService jwtService;
    @Value("${admin.secret}") String adminEmail;

    public UserController(UserServices service, JwtService jwtService) {
        this.service = service;
        this.jwtService = jwtService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createUser(@NonNull @RequestBody UserDTO userData, @RequestHeader("Authorization") String authHeader) {
        Credentials credentials;
        try {
            credentials = validateCredentials(authHeader);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials");
        }

        Optional<String> registrationResult = service.registerUser(
                userData.getFirstName(),
                userData.getLastName(),
                credentials.getEmail(),
                credentials.getPassword(),
                userData.getAge(),
                userData.getPhoto(),
                userData.getGender(),
                userData.getAddress(),
                userData.getMomName(),
                userData.getPetName(),
                userData.getColor()
        );

        if (registrationResult.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registrationResult.get());
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestHeader("Authorization") String authHeader) {

        Credentials credentials;
        try {
            credentials = validateCredentials(authHeader);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials");
        }

        UserDTO user = service.login(credentials.getEmail(), credentials.getPassword());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        if (credentials.getEmail().equals(adminEmail)) {
            service.makeAdmin(user);
        }
        UserDTO userDTO = service.getByEmail(credentials.getEmail()).get();
        String token = jwtService.generateToken(userDTO);
        LoginResponse response = new LoginResponse(token, user);
        return ResponseEntity.ok(response);
    }

    private Credentials validateCredentials(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }

        // Extract and decode the Base64 encoded string
        String base64Credentials = authHeader.substring("Basic ".length());
        byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        // Split the decoded string to get email and password
        String[] credentials = decodedString.split(":", 2);
        if (credentials.length != 2) {
            throw new IllegalArgumentException("Invalid Basic Authentication format");
        }
        return new Credentials(credentials[0], credentials[1]);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(@RequestHeader("Authorization") String token, @NonNull @RequestParam("userEmail") String userEmail) {
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!jwtService.validateToken(token, userEmail)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Optional<UserDTO> user = service.getByEmail(userEmail);
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> updateUser( @RequestHeader("Authorization") String token, @NonNull @RequestBody UserDTO userData) {
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!jwtService.validateToken(token, userData.getEmail()) && !jwtService.validateAdminToken(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Optional<UserDTO> previousUser = service.getByEmail(userData.getEmail());
        if (previousUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //admin can promove other users to admin
        if (jwtService.validateAdminToken(token) && userData.isAdmin() && !previousUser.get().isAdmin()) {
            service.makeAdmin(userData);
        }
        //admin can remove other users to admin
        if (jwtService.validateAdminToken(token) && !userData.isAdmin() && previousUser.get().isAdmin()) {
            service.removeAdmin(userData);
        }

        UserDTO updatedUser = service.updateUser(userData.getEmail(), userData);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PostMapping("/recovery-password")
    public ResponseEntity<?> recoverPassword(@NonNull @RequestBody RecoveryUserDTO userData) {
        Optional<String> result = service.recoverPassword(userData.getEmail(), userData.getMomName(), userData.getPetName(), userData.getColor());
        Map<String, String> response = new HashMap<>();
        response.put("message", result.orElse("An error occurred"));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestHeader("Authorization") String token) {
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!jwtService.validateAdminToken(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<UserDTO> users = service.getAllUsers().stream()
                .map(UserDTO::new)
                .toList();

        return ResponseEntity.ok(users);
    }
}
