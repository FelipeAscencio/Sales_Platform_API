package ar.uba.fi.ingsoft1.controller.products;

import ar.uba.fi.ingsoft1.services.ProductServices;
import ar.uba.fi.ingsoft1.services.JwtService;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@Validated
public class ProductController {
    private final ProductServices service;
    private final JwtService jwtService;


    public ProductController (ProductServices service, JwtService jwtService) {
        this.service = service;
        this.jwtService = jwtService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = service.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ProductDTO> getProductById(@PathVariable int productId) {
        Optional<ProductDTO> product = service.getDTOById(productId);
        if (product.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(product.get(), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createProduct(@NonNull @RequestBody ProductDTO productData, @RequestHeader("Authorization") String token) {
        if (!jwtService.validateAdminToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        ProductDTO registrationResult = service.addProduct(
                productData.getName(),
                productData.getType(),
                productData.getWeight(),
                productData.getQuantity(),
                productData.getState(),
                productData.getPrice()
        );
        if (registrationResult == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A product with the name: " + productData.getName() + " already exists.");
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body("Product created successfully");
        }
    }

    @PutMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateProduct(@PathVariable int productId, @NonNull @RequestBody ProductDTO productData, @RequestHeader("Authorization") String token) {
        if (!jwtService.validateAdminToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        Optional<ProductDTO> previousProduct = service.getDTOById(productId);
        if (previousProduct.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //update stock or attributes
        if (productData.getQuantity() != 0) {
            if (productData.getQuantity() != previousProduct.get().getQuantity()) {
                service.increaseStock(productId, productData.getQuantity() - previousProduct.get().getQuantity());
            }
        }

        // iterate attributes, update the already existent, add the new ones and remove the ones that are not present in the new product
        if (productData.getExtraAttributes() != null) {
            for (String key : productData.getExtraAttributes().keySet()) {
                service.addDynamicAttribute(productId, key, productData.getExtraAttributes().get(key));
            }
            for (String key : new HashSet<>(previousProduct.get().getExtraAttributes().keySet())) {
                if (!productData.getExtraAttributes().containsKey(key)) {
                    service.removeDynamicAttribute(productId, key);
                }
            }
        }
        Optional<ProductDTO> updatedProduct = service.getDTOById(productId);
        if (updatedProduct.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedProduct.get(), HttpStatus.OK);
    }
}
