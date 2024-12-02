package ar.uba.fi.ingsoft1.persistance;

import ar.uba.fi.ingsoft1.domain.Product;
import ar.uba.fi.ingsoft1.domain.Product.ProductFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TemporaryProductDatabaseTest {

    private TemporaryProductDatabase database;

    @BeforeEach
    void setUp() {
        database = new TemporaryProductDatabase();
    }

    @Test
    void testFindByNameExists() {
        Optional<Product> product = database.findByName("T-shirt");
        assertTrue(product.isPresent());
        assertEquals("T-shirt", product.get().getName());
    }

    @Test
    void testFindByNameDoesNotExist() {
        Optional<Product> product = database.findByName("NonExistentProduct");
        assertFalse(product.isPresent());
    }

    @Test
    void testSaveProductProduct() {
        Product newProduct = ProductFactory.createProductWithAttributes(
                "Juice", "beverage", 0.5, 30, "Liquid", 700, Map.of("flavour", "orange")
        );
        database.saveProduct(newProduct);

        Optional<Product> retrievedProduct = database.findByName("Juice");
        assertTrue(retrievedProduct.isPresent());
        assertEquals("Juice", retrievedProduct.get().getName());
        assertEquals("beverage", retrievedProduct.get().getType());
        assertEquals("orange", retrievedProduct.get().getAttribute("flavour"));
    }

    @Test
    void testSaveProductDuplicateProductName() {
        Product duplicateProduct = ProductFactory.createBasicProduct("shirt", "clothing", 0.3, 40, "Solid", 30);

        database.saveProduct(duplicateProduct);
        Optional<Product> retrievedProduct = database.findByName("shirt");
        assertTrue(retrievedProduct.isPresent());
        assertEquals(duplicateProduct.getId(), retrievedProduct.get().getId());
    }

    @Test
    void testDeleteProductExists() {
        Optional<Product> product = database.findByName("Milk");
        assertTrue(product.isPresent());
        database.deleteProduct(product.get());
        assertFalse(database.findByName("Milk").isPresent());
    }

    @Test
    void testDeleteProductDoesNotExist() {
        Product nonExistentProduct = ProductFactory.createBasicProduct("NonExistentProduct", "unknown", 0.1, 0, "Solid", 1);
        database.deleteProduct(nonExistentProduct);
        assertTrue(database.findByName("T-shirt").isPresent());
    }

    @Test
    void testUpdateStockValid() {
        Optional<Product> product = database.findById(2);
        assertTrue(product.isPresent());
        Product updatedProduct = product.get();
        updatedProduct.setQuantity(120);
        database.saveProduct(updatedProduct);

        Optional<Product> milkProduct = database.findByName("Milk");
        assertTrue(milkProduct.isPresent());
        assertEquals(120, milkProduct.get().getQuantity());
    }

    @Test
    void testUpdateStockDecreaseValid() {
        Optional<Product> product = database.findById(1);
        assertTrue(product.isPresent());
        Product updatedProduct = product.get();
        updatedProduct.setQuantity(40);
        database.saveProduct(updatedProduct);

        Optional<Product> tshirtProduct = database.findByName("T-shirt");
        assertTrue(tshirtProduct.isPresent());
        assertEquals(40, tshirtProduct.get().getQuantity());
    }

    @Test
    void testUpdateStockDecreaseInvalid() {
        Optional<Product> product = database.findById(2);
        assertTrue(product.isPresent());
        Product updatedProduct = product.get();
        try {
            updatedProduct.setQuantity(-200);
        } catch (IllegalArgumentException e) {
            assertEquals("The received quantity is invalid.", e.getMessage());
        }
    }

    @Test
    void testUpdateStockNonExistentProduct() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.findById(3));
        assertEquals("Product with ID 3 not found.", exception.getMessage());
    }

    @Test
    void testAutomaticIdAssignment() {
        Product newProduct1 = ProductFactory.createBasicProduct("Juice", "beverage", 0.3, 20, "Liquid", 10);
        Product newProduct2 = ProductFactory.createBasicProduct("Bread", "food", 0.2, 15, "Solid", 14);

        database.saveProduct(newProduct1);
        database.saveProduct(newProduct2);

        assertEquals(3, newProduct1.getId());
        assertEquals(4, newProduct2.getId());
    }
}