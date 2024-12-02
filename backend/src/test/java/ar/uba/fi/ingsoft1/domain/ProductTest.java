package ar.uba.fi.ingsoft1.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.HashMap;

class ProductTest {

    private Product basicProduct;
    private Map<String,String> attributes;

    // Initializes a test product before each test.
    @BeforeEach
    void setUp() {
        basicProduct = Product.ProductFactory.createBasicProduct("Shampoo", "Personal Care", 0.5, 100, "Solid", 104);
        attributes = new HashMap<>();
        attributes.put("color", "blue");
        attributes.put("size", "medium");
    }

    @Test
    void testBasicProductCreation() {
        assertEquals("Shampoo", basicProduct.getName());
        assertEquals("Personal Care", basicProduct.getType());
        assertEquals(0.5, basicProduct.getWeight());
        assertEquals(100, basicProduct.getQuantity());
        assertEquals("Solid", basicProduct.getState());
        assertEquals(104.0F, basicProduct.getPrice());
        assertEquals(0, basicProduct.getId()); // Default ID should be 0 until set by database
    }

    @Test
    void testProductCreationWithAttributes() {
        Product productWithAttributes = Product.ProductFactory.createProductWithAttributes("Lotion", "Personal Care", 0.3, 50, "Liquid", 230, attributes);
        assertEquals("Lotion", productWithAttributes.getName());
        assertEquals("Personal Care", productWithAttributes.getType());
        assertEquals(0.3, productWithAttributes.getWeight());
        assertEquals(50, productWithAttributes.getQuantity());
        assertEquals("Liquid", productWithAttributes.getState());
        assertEquals(230.0F, productWithAttributes.getPrice());
        assertEquals("blue", productWithAttributes.getAttribute("color"));
        assertEquals("medium", productWithAttributes.getAttribute("size"));
    }

    @Test
    void testAddAttribute() {
        basicProduct.addAttribute("fragrance", "lavender");
        assertEquals("lavender", basicProduct.getAttribute("fragrance"));
    }

    @Test
    void testHasAttribute() {
        basicProduct.addAttribute("expiryDate", "2024-12");
        assertTrue(basicProduct.hasAttribute("expiryDate"));
        assertFalse(basicProduct.hasAttribute("nonExistentAttribute"));
    }

    @Test
    void testRemoveAttribute() {
        basicProduct.addAttribute("testKey", "testValue");
        assertTrue(basicProduct.hasAttribute("testKey"));
        basicProduct.removeAttribute("testKey");
        assertFalse(basicProduct.hasAttribute("testKey"));
    }

    @Test
    void testGetAllAttributes() {
        basicProduct.addAttribute("ingredient", "aloe");
        basicProduct.addAttribute("organic", "yes");
        Map<String, String> allAttributes = basicProduct.getAllAttributes();
        assertEquals(2, allAttributes.size());
        assertEquals("aloe", allAttributes.get("ingredient"));
        assertEquals("yes", allAttributes.get("organic"));
    }

    @Test
    void testSetValidQuantity() {
        basicProduct.setQuantity(200);
        assertEquals(200, basicProduct.getQuantity());
    }

    @Test
    void testSetInvalidQuantity() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> basicProduct.setQuantity(-10));
        assertEquals("The received quantity is invalid.", exception.getMessage());
    }

    @Test
    void testInvalidProductCreationNullName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Product.ProductFactory.createBasicProduct(null, "type", 0.5, 10, "Solid", 10));
        assertEquals("Name is a required field.", exception.getMessage());
    }

    @Test
    void testInvalidProductCreationNonPositiveWeight() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Product.ProductFactory.createBasicProduct("Soap", "Personal Care", -1.0, 10, "Solid", 10));
        assertEquals("The weight must be a positive value.", exception.getMessage());
    }

    @Test
    void testInvalidProductCreationNonPositiveQuantity() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Product.ProductFactory.createBasicProduct("Cream", "Personal Care", 0.5, -5, "Solid", 10));
        assertEquals("The initial quantity of products must be zero or greater.", exception.getMessage());
    }

    @Test
    void testInvalidProductCreationNonValidState() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Product.ProductFactory.createBasicProduct("Cream", "Personal Care", 0.5, 5, "Plasma", 10));
        assertEquals("The state of the product isn't valid.", exception.getMessage());
    }

    @Test
    void testInvalidProductCreationNonValidPrice() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Product.ProductFactory.createBasicProduct("Cream", "Personal Care", 0.5, 5, "Solid", -10));
        assertEquals("The price must be a positive value.", exception.getMessage());
    }

    @Test
    void testSetProductId() {
        basicProduct.setId(123);
        assertEquals(123, basicProduct.getId());
    }

    @Test
    void testSetMultipleAttributes() {
        basicProduct.addAttribute("brand", "NaturalCo");
        basicProduct.addAttribute("volume", "250");
        assertEquals("NaturalCo", basicProduct.getAttribute("brand"));
        assertEquals("250", basicProduct.getAttribute("volume"));
    }

    @Test
    void testDefaultExtraAttributesEmpty() {
        assertTrue(basicProduct.getAllAttributes().isEmpty());
    }
}

