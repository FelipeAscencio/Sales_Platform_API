package ar.uba.fi.ingsoft1.services;

import ar.uba.fi.ingsoft1.controller.users.UserDTO;
import ar.uba.fi.ingsoft1.domain.User;
import ar.uba.fi.ingsoft1.persistance.UserRepository;
import ar.uba.fi.ingsoft1.persistance.UserRepositoryJPA;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class UserServices {
    private static final int NEW_PASSWORD_LEN = 8;
    private final UserRepositoryJPA userRepository;
    private User loggedInUser;

    // Constructor of the class.
    public UserServices(UserRepositoryJPA userRepository) {
        this.userRepository = userRepository;
    }

    // Pre: The model must be correctly initialized.
    // Post: Returns true if a user is logged in, false otherwise.
    public boolean checkUserStatus() {
        return loggedInUser != null;
    }

    // Pre: The model must be correctly initialized.
    // Post: Returns true and saves the logged-in user in the model if found in
    //       the Database, or false otherwise.
    public UserDTO login(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        Optional<User> userOptional = userRepository.findByEmailAndPassword(email, password);
        if (userOptional.isPresent()) {
            this.loggedInUser = userOptional.get();
            return new UserDTO(this.loggedInUser);
        }
        return null;
    }

    // Pre: The model must be correctly initialized.
    // Post: Returns true if a user is registered successfully or False if there's an error.
    public Optional<String> registerUser(String firstName, String lastName, String email, String password,
                                         int age, String photo, String gender, String address, String momName, String petName, String color) {
        try {
            User newUser = new User.Builder(firstName, lastName, email, password, age, photo, gender, address, momName, petName, color)
                    .build();

            userRepository.saveUser(newUser);
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional.of("User registration failed: " + e.getMessage());
        }
    }

    // Method to generate a random password of given length
    private String generateRandomPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(NEW_PASSWORD_LEN);
        for (int i = 0; i < NEW_PASSWORD_LEN; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }

        return password.toString();
    }

    // Pre: All parameters received must be valid.
    // Post: If the user exists with the data entered, the password is changed,
    // otherwise, for any other scenario, the user is notified of what happened.
    public Optional<String> recoverPassword(String email, String momName, String firstPetName, String favoriteColor) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User actualUser = userOptional.get();
            if (actualUser.getEmail().equalsIgnoreCase(email) &&
                    actualUser.getMomName().equalsIgnoreCase(momName) &&
                    actualUser.getFirstPetName().equalsIgnoreCase(firstPetName) &&
                    actualUser.getFavoriteColor().equalsIgnoreCase(favoriteColor)) {

                String newPassword;
                newPassword = generateRandomPassword();
                actualUser.setPassword(newPassword);
                userRepository.saveUser(actualUser);
                return Optional.of("Password successfully updated. Your new password is: " + newPassword);
            } else {
                return Optional.of("The provided details do not match our records.");
            }
        }

        return Optional.of("No user was found with the email provided.");
    }

    public Optional<UserDTO> getByEmail(String email) {
        Optional<User> user= userRepository.findByEmail(email);
        if (user.isPresent()){
            return Optional.of(new UserDTO(user.get()));
        }
        return Optional.empty();
    }

    // Pre: The user repository must be correctly initialized.
    // Post: Returns a list of all users currently in the repository.
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Pre: The user must be correctly initialized.
    // Post: The provided user is now an admin.
    public void makeAdmin(UserDTO userDTO) {
        if (userDTO == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        User user = userRepository.findByEmail(userDTO.getEmail()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setAdmin(true);
        userRepository.saveUser(user);
    }

    // Pre: The user must be correctly initialized.
    // Post: The provided user is no longer an admin.
    public void removeAdmin(UserDTO userDTO) {
        if (userDTO == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        User user = userRepository.findByEmail(userDTO.getEmail()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setAdmin(false);
        userRepository.saveUser(user);
    }

    // Pre: The user must be correctly initialized.
    // Post: Returns true if the provided user is an admin, false otherwise.
    public boolean isAdmin(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        return user.getAdmin();
    }

    public void changePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPassword(newPassword);
        userRepository.saveUser(user);
    }

    public UserDTO updateUser(String email, UserDTO userData) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setAge(userData.getAge());
        user.setPhoto(userData.getPhoto());
        user.setGender(userData.getGender());
        user.setAddress(userData.getAddress());
        userRepository.saveUser(user);
        return new UserDTO(user);
    }
}
