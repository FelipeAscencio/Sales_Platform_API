package ar.uba.fi.ingsoft1.domain;

import jakarta.persistence.*;

import java.util.regex.Pattern;

@Entity
@Table(name = "users")  // Opcional: Cambia el nombre de la tabla en la base de datos
public class User {

    private static final String MALE_STR = "Male";
    private static final String FEMALE_STR = "Female";
    private static final int ZERO = 0;
    private static final int MINIMUM_AGE = 18;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String photo;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String address;

    @Column
    private  String momName;

    @Column
    private  String firstPetName;

    @Column
    private  String favoriteColor;

    @Column
    private boolean admin;


    // Constructor sin argumentos necesario para JPA
    protected User() {
    }



    // Constructor of the class.
    public User(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.password = builder.password;
        this.photo = builder.photo;
        this.age = builder.age;
        this.gender = builder.gender;
        this.address = builder.address;
        this.momName = builder.momName;
        this.firstPetName = builder.firstPetName;
        this.favoriteColor = builder.favoriteColor;
        this.admin = false;
    }

    // Pre: The user must be correctly initialized.
    // Post: Returns the user's first name.
    public String getFirstName() {
        return firstName;
    }

    // Pre: - .
    // Post: If the first name is valid, changes it, otherwise throws an exception.
    public void setFirstName(String firstName) {
        if (firstName == null) {
            throw new IllegalArgumentException("The received first name is invalid.");
        }
        this.firstName = firstName;
    }

    // Pre: The user must be correctly initialized.
    // Post: Returns the user's last name.
    public String getLastName() {
        return lastName;
    }

    // Pre: - .
    // Post: If the last name is valid, changes it, otherwise throws an exception.
    public void setLastName(String lastName) {
        if (lastName == null) {
            throw new IllegalArgumentException("The received last name is invalid.");
        }
        this.lastName = lastName;
    }

    // Pre: The user must be correctly initialized.
    // Post: Returns the user's email.
    public String getEmail() {
        return email;
    }

    // Pre: - .
    // Post: If the email is valid, changes it, otherwise throws an exception.
    public void setEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("The received email is invalid.");
        }
        this.email = email;
    }

    // Pre: The user must be correctly initialized.
    // Post: Returns the user's photo.
    public String getPhoto() {
        return photo;
    }

    // Pre: - .
    // Post: If the photo is valid, changes it, otherwise throws an exception.
    public void setPhoto(String photo) {
        if (photo == null) {
            throw new IllegalArgumentException("The received photo is invalid.");
        }
        this.photo = photo;
    }

    // Pre: The user must be correctly initialized.
    // Post: Returns the user's age.
    public int getAge() {
        return age;
    }

    // Pre: - .
    // Post: If the age is valid, changes it, otherwise throws an exception.
    public void setAge(int age) {
        if (age <= ZERO) {
            throw new IllegalArgumentException("The received age is invalid.");
        } else if (age < MINIMUM_AGE){
            throw new IllegalArgumentException("The minimum age is 18 years.");
        }
        this.age = age;
    }

    // Pre: The user must be correctly initialized.
    // Post: Returns the user's gender.
    public String getGender() {
        return gender;
    }

    // Pre: - .
    // Post: If the gender is valid, changes it, otherwise throws an exception.
    public void setGender(String gender) {
        if (!gender.equalsIgnoreCase(MALE_STR) && !gender.equalsIgnoreCase(FEMALE_STR)) {
            throw new IllegalArgumentException("The received gender is invalid.");
        }
        this.gender = gender;
    }

    // Pre: The user must be correctly initialized.
    // Post: Returns the user's address.
    public String getAddress() {
        return address;
    }

    // Pre: - .
    // Post: If the address is valid, changes it, otherwise throws an exception.
    public void setAddress(String address) {
        if (address == null) {
            throw new IllegalArgumentException("The received address is invalid.");
        }
        this.address = address;
    }

    // Pre: The user must be correctly initialized.
    // Post: Returns the user's password.
    public String getPassword() {
        return password;
    }

    // Pre: - .
    // Post: If the password is valid, changes it, otherwise throws an exception.
    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("The received password is invalid.");
        }
        this.password = password;
    }

    // Pre: -.
    // Post: Returns the mom´s name of the user.
    public String getMomName() { return momName; }

    // Pre: -.
    // Post: Returns the first pet´s name of the user.
    public String getFirstPetName() { return firstPetName; }

    // Pre: -.
    // Post: Returns the favorite´s color of the user.
    public String getFavoriteColor() { return favoriteColor; }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean nuevo_rol) {
        this.admin = nuevo_rol;
    }

    // Builder class for the user.
    public static class Builder {
        private final String firstName;
        private final String lastName;
        private final String email;
        private final String password;
        private final String photo;
        private final int age;
        private final String gender;
        private final String address;
        private final String momName;
        private final String firstPetName;
        private final String favoriteColor;

        // Pre: The "email" may not be an null String.
        // Post: Throw an exception if the email is invalid.
        private void validateEmail(String email) {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("The email cannot be empty.");
            }

            //Check that it contains '@'.
            if (!email.contains("@")) {
                throw new IllegalArgumentException("The email must contain the character'@'.");
            }

            // Check that it has a period after the '@'.
            int atIndex = email.indexOf("@");
            if (email.indexOf(".", atIndex) == -1) {
                throw new IllegalArgumentException("The email must contain a period after the '@'.");
            }

            // Verify that it does not contain special characters that are not allowed.
            String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            if (!email.matches(regex)) {
                throw new IllegalArgumentException("The email contains illegal characters.");
            }
        }

        // Method to validate first name.
        private void validateFirstName(String firstName) {
            if (firstName == null) {
                throw new IllegalArgumentException("First Name is a required field.");
            }
        }

        // Method to validate last name.
        private void validateLastName(String lastName) {
            if (lastName == null) {
                throw new IllegalArgumentException("Last Name is a required field.");
            }
        }

        // Method to validate password.
        private void validatePassword(String password) {
            if (password == null) {
                throw new IllegalArgumentException("Password is a required field.");
            }
        }

        // Method to validate age.
        private void validateAge(int age) {
            if (age < MINIMUM_AGE) {
                throw new IllegalArgumentException("Minimum age is 18 years.");
            }
        }

        // Method to validate gender.
        private void validateGender(String gender) {
            if (gender == null || gender.isEmpty()) {
                throw new IllegalArgumentException("Gender is a required field.");
            }
            if (!gender.equalsIgnoreCase(MALE_STR) && !gender.equalsIgnoreCase(FEMALE_STR)) {
                throw new IllegalArgumentException("Invalid gender, it must be 'Male' or 'Female'.");
            }
        }

        // Method to validate address.
        private void validateAddress(String address) {
            if (address == null || address.isEmpty()) {
                throw new IllegalArgumentException("Address is a required field.");
            }
        }

        // Method to validate photo.
        private void validatePhoto(String photo) {
            if (photo == null) {
                throw new IllegalArgumentException("The photo is a required field.");
            }
        }

        // Method to validate first security question.
        private void validateMomName(String name) {
            if (name == null) {
                throw new IllegalArgumentException("First security question is a required field.");
            }
        }

        // Method to validate second security question.
        private void validateFirstPetName(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Second security question is a required field.");
            }
        }

        // Method to validate third security question.
        private void validateFavoriteColor(String color) {
            if (color == null) {
                throw new IllegalArgumentException("Third security question is a required field.");
            }
        }

        // Builder constructor.
        public Builder(String firstName, String lastName, String email, String password, int age, String photo, String gender, String address, String momName, String petName, String color) {
            validateEmail(email);
            validateFirstName(firstName);
            validateLastName(lastName);
            validatePassword(password);
            validateAge(age);
            validateGender(gender);
            validateAddress(address);
            validatePhoto(photo);
            validateMomName(momName);
            validateFirstPetName(petName);
            validateFavoriteColor(color);
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
            this.age = age;
            this.photo = photo;
            this.gender = gender;
            this.address = address;
            this.momName = momName;
            this.firstPetName = petName;
            this.favoriteColor = color;
        }

        // Final builder.
        public User build() {
            return new User(this);
        }
    }
}
