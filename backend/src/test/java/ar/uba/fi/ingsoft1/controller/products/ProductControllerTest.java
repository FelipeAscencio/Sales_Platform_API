package ar.uba.fi.ingsoft1.controller.products;

import ar.uba.fi.ingsoft1.domain.Product;
import ar.uba.fi.ingsoft1.services.JwtService;
import ar.uba.fi.ingsoft1.services.ProductServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ProductControllerTest {

    @Mock
    private ProductServices productServices;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        when(jwtService.validateAdminToken("token")).thenReturn(true);
    }

    @Test
    public void testGetAllProducts() {
        ProductDTO product1 = new ProductDTO(1, "shirt", "Clothes", 1.0, 10, 10, "Solid", null);
        ProductDTO product2 = new ProductDTO(2, "pants", "denim", 2.0, 20, 10, "Solid", null);
        List<ProductDTO> products = Arrays.asList(product1, product2);

        when(productServices.getAllProducts()).thenReturn(products);

        ResponseEntity<List<ProductDTO>> response = productController.getAllProducts();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void testGetProductById() {
        ProductDTO product = new ProductDTO(1, "shirt", "solid", 1.0, 10, 10, "Solid", null);

        when(productServices.getDTOById(1)).thenReturn(Optional.of(product));

        ResponseEntity<ProductDTO> response = productController.getProductById(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("shirt", response.getBody().getName());
    }

    @Test
    public void testCreateProduct() {
        ProductDTO productDTO = new ProductDTO(1, "shirt", "clothes", 1.0, 10.0F , 5, "Solid", null);

        when(productServices.addProduct(
                productDTO.getName(),
                productDTO.getType(),
                productDTO.getWeight(),
                productDTO.getQuantity(),
                productDTO.getState(),
                productDTO.getPrice()
        )).thenReturn(productDTO);
        String token = "token";
        ResponseEntity<String> response = productController.createProduct(productDTO, token);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Product created successfully", response.getBody());
    }

    @Test
    public void testUpdateProduct() {
        int productId = 1;
        Map<String, String> extraAttributes = new HashMap<>();
        extraAttributes.put("color", "red");
        ProductDTO existingProduct = new ProductDTO(productId, "shirt", "clothes", 1.0, 10, 10, "Solid", extraAttributes);
        ProductDTO updatedProduct = new ProductDTO(productId, "shirt", "clothes", 1.0, 10, 15, "Solid", extraAttributes);

        when(productServices.getDTOById(productId)).thenReturn(Optional.of(existingProduct)).thenReturn(Optional.of(updatedProduct));
        String token = "token";
        ResponseEntity<?> response = productController.updateProduct(productId, updatedProduct, token);
        ProductDTO productResponse = (ProductDTO) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(15, productResponse.getQuantity());
    }

    @Test
    public void testUpdateProductNotFound() {
        int productId = 1;
        Map<String, String> extraAttributes = new HashMap<>();
        extraAttributes.put("color", "red");
        ProductDTO updatedProduct = new ProductDTO(productId, "shirt", "clothes", 1.0, 15, 10, "Solid", extraAttributes);

        when(productServices.getDTOById(productId)).thenReturn(Optional.empty());
        String token = "token";
        ResponseEntity<?> response = productController.updateProduct(productId, updatedProduct, token);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
