package ar.uba.fi.ingsoft1.services;

import ar.uba.fi.ingsoft1.domain.Product;
import ar.uba.fi.ingsoft1.persistance.ProductRepositoryJPA;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class ProductServicesTest {

    @Autowired
    private ProductServices productServicesDB;
    
    @Autowired
    private ProductRepositoryJPA productRepositoryJPA;

    // Initializes a test product database before each test.
    @BeforeEach
    void setUp() {
        Product product1 = Product.ProductFactory.createProductWithAttributes(
                "T-shirt", "clothing", 0.2, 50, "Solid", 100,
                Map.of("color", "red", "size", "medium")
        );
        productRepositoryJPA.saveProduct(product1);
        System.out.println("ESTE ES EL ID DEL PRODUCT1!!!!");
        System.out.println(product1.getId());

        Product product2 = Product.ProductFactory.createProductWithAttributes(
                "Yogurt", "food", 1.0, 100, "Liquid", 30.5F,
                Map.of("size", "large", "flavour", "strawberry")
        );
        productRepositoryJPA.saveProduct(product2);

        System.out.println("ESTE ES EL ID DEL PRODUCT2!!!!");
        System.out.println(product2.getId());


    }

    @Test
    void searchByName(){
        Optional<Product> product = productRepositoryJPA.findByName("Yogurt");
        assertNotNull(product);
    }

    @Test
    void searchById(){
        Optional<Product> product = productRepositoryJPA.findById(1);
        assertNotNull(product);
    }

    @Test
    void testCheckStockProductAvailable() {
        Optional<Product> product = productServicesDB.getByName("T-shirt");
        if (product.isPresent()) {
            assertTrue(productServicesDB.checkStock(product.get().getId(), 5));
        }

    }

    @Test
    void testCheckStockProductNotAvailable() {
        int productId = 1;
        int quantity = 200;
        assertFalse(productServicesDB.checkStock(productId, quantity));
    }
//
//    @Test
//    void testAddDynamicAttribute() {
//        int productId = 1;
//        productServicesDB.addDynamicAttribute(productId, "material", "cotton");
//        Optional<Product> product = productServicesDB.getById(productId);
//        assertEquals("cotton", product.getAttribute("material"));
//    }
    @Test
    void testAddDynamicAttribute() {
        Optional<Product> product = productServicesDB.getByName("T-shirt");
        productServicesDB.addDynamicAttribute(product.get().getId(), "material", "cotton");

        assertTrue(product.isPresent());
        assertEquals("cotton", product.get().getAttribute("material"));
    }


//        @Test
//    void testRemoveDynamicAttribute() {
//        int productId = 1;
//        productServicesDB.addDynamicAttribute(productId, "material", "cotton");
//        productServicesDB.removeDynamicAttribute(productId, "material");
//        Optional<Product> product = productServicesDB.getById(productId);
//        assertNull(product.getAttribute("material"));
//    }
    @Test
    void testRemoveDynamicAttribute() {
        Optional<Product> product = productServicesDB.getByName("T-shirt");
        productServicesDB.addDynamicAttribute(product.get().getId(), "material", "cotton");
        productServicesDB.removeDynamicAttribute(product.get().getId(), "material");

        assertTrue(product.isPresent() && product.get().getAttribute("material") == null);
    }



    @Test
    void testChangeDynamicAttributeValue() {
        Optional<Product> product = productServicesDB.getByName("T-shirt");
        int productId = product.get().getId();
        productServicesDB.addDynamicAttribute(productId, "color", "red");
        productServicesDB.changeDynamicAttributeValue(productId, "color", "blue");
        assertTrue(product.isPresent() && product.get().getAttribute("color") == "blue");
    }

    @Test
    void testChangeStock() {
        Optional<Product> product = productServicesDB.getByName("T-shirt");
        int productId = product.get().getId();
        int newStock = 100;
        productServicesDB.changeStock(productId, newStock);
        int newQuantity = productServicesDB.getProductQuantity(productId);
        assertEquals(100, newQuantity);
    }

    @Test
    void testIncreaseStock() {
        Optional<Product> product = productServicesDB.getByName("T-shirt");
        int productId = product.get().getId();
        int initialQuantity = productServicesDB.getProductQuantity(productId);
        int increaseAmount = 10;
        productServicesDB.increaseStock(productId, increaseAmount);
        int newQuantity = productServicesDB.getProductQuantity(productId);
        assertEquals(initialQuantity + increaseAmount, newQuantity);
    }

    @Test
    void testDecreaseStock() {
        Optional<Product> product = productServicesDB.getByName("T-shirt");
        int productId = product.get().getId();
        int initialQuantity = productServicesDB.getProductQuantity(productId);
        int decreaseAmount = 5;
        productServicesDB.decreaseStock(productId, decreaseAmount);
        int newQuantity = productServicesDB.getProductQuantity(productId);
        assertEquals(initialQuantity - decreaseAmount, newQuantity);
    }

    @Test
    void testDecreaseStockShouldThrowException() {
        Optional<Product> product = productServicesDB.getByName("T-shirt");
        int productId = product.get().getId();
        int decreaseAmount = 1000;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> productServicesDB.decreaseStock(productId, decreaseAmount));
        assertEquals("Stock cannot be negative after decrease.", exception.getMessage());
    }

    @Test
    void testAddProductSuccessfully() {
        String name = "Laptop";
        String type = "Electronics";
        double weight = 2.5;
        int quantity = 10;
        String state = "Solid";
        float price = 120.3F;
        productServicesDB.addProduct(name, type, weight, quantity, state, price);
        Product product = productRepositoryJPA.findAll().stream()
                .filter(p -> p.getName().equals(name) && p.getType().equals(type))
                .findFirst()
                .orElse(null);

        assertNotNull(product, "Product should be added successfully.");
        assertEquals(name, product.getName());
        assertEquals(type, product.getType());
        assertEquals(weight, product.getWeight(), 0.01);
        assertEquals(quantity, product.getQuantity());
    }

    @Test
    void testAddProductNullNameShouldThrowException() {
        String type = "Electronics";
        double weight = 2.5;
        int quantity = 10;
        String state = "Solid";
        float price = 103.4F;
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> productServicesDB.addProduct(null, type, weight, quantity, state, price));
        assertEquals("Product name and type cannot be null.", exception.getMessage());
    }

    @Test
    void testAddProductNullTypeShouldThrowException() {
        String name = "Laptop";
        double weight = 2.5;
        int quantity = 10;
        String state = "Solid";
        float price = 103.4F;
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> productServicesDB.addProduct(name, null, weight, quantity, state, price));
        assertEquals("Product name and type cannot be null.", exception.getMessage());
    }

    @Test
    void testAddProductNegativeWeightShouldThrowException() {
        String name = "Laptop";
        String type = "Electronics";
        double weight = -1.0;
        int quantity = 10;
        String state = "Solid";
        float price = 103.4F;
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> productServicesDB.addProduct(name, type, weight, quantity, state, price));
        assertEquals("Product weight and quantity must be non-negative.", exception.getMessage());
    }

    @Test
    void testAddProductNegativeQuantityShouldThrowException() {
        String name = "Laptop";
        String type = "Electronics";
        double weight = 2.5;
        int quantity = -5;
        String state = "Solid";
        float price = 103.4F;
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> productServicesDB.addProduct(name, type, weight, quantity, state, price));
        assertEquals("Product weight and quantity must be non-negative.", exception.getMessage());
    }

    @Test
    void testAddProductInvalidStateShouldThrowException() {
        String name = "Laptop";
        String type = "Electronics";
        double weight = 2.5;
        int quantity = 5;
        String state = "Plasma";
        float price = 103.4F;
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> productServicesDB.addProduct(name, type, weight, quantity, state, price));
        assertEquals("The state of the product isn't valid.", exception.getMessage());
    }

    @Test
    void testAddProductNegativePriceShouldThrowException() {
        String name = "Laptop";
        String type = "Electronics";
        double weight = 2.5;
        int quantity = 5;
        String state = "Solid";
        float price = -103.4F;
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> productServicesDB.addProduct(name, type, weight, quantity, state, price));
        assertEquals("The price must be a positive value.", exception.getMessage());
    }

//    @Test
//    void testDeleteProductByIdSuccessfully() {
//        int productId = 1207;
//        productServicesDB.deleteProductById(productId);
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> productRepositoryJPA.getById(productId));
//        assertEquals("Product with ID " + productId + " not found.", exception.getMessage());
//    }

//    @Test
//    void testDeleteProductByIdNonExistentProduct() {
//        int nonExistentProductId = 999;
//        Exception exception = assertThrows(IllegalArgumentException.class,
//                () -> productServicesDB.deleteProductById(nonExistentProductId));
//        assertEquals("Product with ID " + nonExistentProductId + " not found.", exception.getMessage());
//    }
}
