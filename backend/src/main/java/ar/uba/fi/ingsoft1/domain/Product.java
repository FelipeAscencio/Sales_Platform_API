package ar.uba.fi.ingsoft1.domain;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "products")
public class Product {
    private static final int ZERO = 0;
    private static final String GASEOUS = "Gaseous";
    private static final String LIQUID = "Liquid";
    private static final String SOLID = "Solid";

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Getter
    private String name;

    @Getter
    private String type;

    @Getter
    private double weight;

    @Getter
    private int quantity;

    @Getter
    private  String state;

    @Getter
    private  float price;



    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_attributes", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "attribute_name")
    @Column(name = "attribute_value")
    private  Map<String, String> extraAttributes;


    // Method to validate name.
    private void validateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name is a required field.");
        }
    }

    // Method to validate weight.
    private void validateWeight(double weight) {
        if (weight <= ZERO) {
            throw new IllegalArgumentException("The weight must be a positive value.");
        }
    }

    // Method to validate initial stock.
    private void validateQuantity(int quantity) {
        if (quantity < ZERO) {
            throw new IllegalArgumentException("The initial quantity of products must be zero or greater.");
        }
    }

    // Method to validate state.
    private void validateState(String state) {
        if (!state.equals(GASEOUS) && !state.equals(SOLID) && !state.equals(LIQUID)) {
            throw new IllegalArgumentException("The state of the product isn't valid.");
        }
    }

    // Method to validate price.
    private void validatePrice(float price) {
        if (price <= ZERO) {
            throw new IllegalArgumentException("The price must be a positive value.");
        }
    }

    // Constructor of the class.
    public Product(String name, String type, double weight, int quantity, String state, float price) {
        validateName(name);
        validateWeight(weight);
        validateQuantity(quantity);
        validateState(state);
        validatePrice(price);
        this.name = name;
        this.type = type;
        this.weight = weight;
        this.quantity = quantity;
        this.state = state;
        this.price = price;
        this.extraAttributes = new HashMap<>();
    }

    // Constructor by copy.
    public Product(Product original) {
        this.id = original.id;
        this.name = original.name;
        this.type = original.type;
        this.weight = original.weight;
        this.quantity = original.quantity;
        this.state = original.state;
        this.price = original.price;
        this.extraAttributes = new HashMap<>(original.extraAttributes);
    }
    // Constructor sin argumentos para JPA
    protected Product() {

    }

    // Pre: The quantity must be a non-negative integer.
    // Post: Updates the product’s quantity if the provided value is valid.
    public void setQuantity(int quantity) {
        if (quantity < ZERO) {
            throw new IllegalArgumentException("The received quantity is invalid.");
        }
        this.quantity = quantity;
    }

    // Pre: The key and value must be non-null.
    // Post: Adds the given key-value pair to the product's additional attributes.
    public void addAttribute(String key, String value) {
        extraAttributes.put(key, value);
    }

    // Pre: The key must be non-null.
    // Post: Returns the value associated with the specified key if it exists.
    public Object getAttribute(String key) {
        return extraAttributes.get(key);
    }

    // Pre: The key must be non-null.
    // Post: Returns true if the specified key exists in the additional attributes; otherwise, false.
    public boolean hasAttribute(String key) {
        return extraAttributes.containsKey(key);
    }

    // Pre: The key must be non-null.
    // Post: Removes the key-value pair associated with the specified key from the additional attributes if it exists.
    public void removeAttribute(String key) {
        extraAttributes.remove(key);
    }

    // Pre: The product must be correctly initialized.
    // Post: Returns a map containing all additional attributes of the product.
    public Map<String, String> getAllAttributes() {
        return extraAttributes;
    }

    // Pre: name, type, and attributes must be valid; weight and quantity must be positive.
    // Post: Returns a new Product instance with specified attributes, managed by a factory pattern.
    public static class ProductFactory {
        public static Product createBasicProduct(String name, String type, double weight, int quantity, String state, float price) {
            return new Product(name, type, weight, quantity, state, price);
        }

        public static Product createProductWithAttributes(String name, String type, double weight, int quantity, String state, float price, Map<String, String> attributes) {
            Product product = new Product(name, type, weight, quantity, state, price);
            if (attributes != null) {
                product.extraAttributes.putAll(attributes);
            }

            return product;
        }
    }
}
