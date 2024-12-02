package ar.uba.fi.ingsoft1.services;

import ar.uba.fi.ingsoft1.controller.products.ProductDTO;
import ar.uba.fi.ingsoft1.domain.Order;
import ar.uba.fi.ingsoft1.domain.Product;
import ar.uba.fi.ingsoft1.persistance.ProductRepositoryJPA;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServices {
    private static final int ZERO = 0;
    private final ProductRepositoryJPA productRepository;

    // Constructor de la clase
    public ProductServices(ProductRepositoryJPA productRepository) {
        this.productRepository = productRepository;
    }

    // Obtener producto por nombre
    public Optional<Product> getByName(String name) {
        return productRepository.findByName(name);
    }

    // Obtener producto por ID
    public Optional<ProductDTO> getDTOById(int productId) {
        return productRepository.findById(productId).map(ProductDTO::new);
    }

    // Obtener producto por ID
    public Optional<Product> getById(int productId) {
        return productRepository.findById(productId);
    }

    // Obtener productos por lista de IDs
    public Map<Integer, Product> getProductsById(List<Integer> productIds) {
        HashMap<Integer, Product> products = new HashMap<>();
        for (Integer id : productIds) {
            Optional<Product> product = productRepository.findById(id);
            if (product.isPresent()) {
                products.put(id, product.get());
            }
        }
        return products;
    }

    // Obtener todos los productos
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return ProductDTO.fromProductList(products);
    }

    // Obtener cantidad del producto por ID
    public int getProductQuantity(int productId) {
        return productRepository.findById(productId)
                .map(Product::getQuantity)
                .orElse(ZERO);
    }

    // Verificar stock
    public boolean checkStock(int productId, int quantity) {
        return productRepository.findById(productId)
                .map(product -> product.getQuantity() >= quantity)
                .orElse(false);
    }

    // Añadir atributo dinámico
    public void addDynamicAttribute(int productId, String key, String value) {
        productRepository.findById(productId).ifPresent(product -> {
            product.addAttribute(key, value);
            productRepository.saveProduct(product);
        });
    }

    // Eliminar atributo dinámico
    public void removeDynamicAttribute(int productId, String key) {
        productRepository.findById(productId).ifPresent(product -> {
            product.removeAttribute(key);
            productRepository.saveProduct(product);
        });
    }

    // Cambiar valor de atributo dinámico
    public void changeDynamicAttributeValue(int productId, String key, String newValue) {
        productRepository.findById(productId).ifPresent(product -> {
            product.addAttribute(key, newValue);
            productRepository.saveProduct(product);
        });
    }

    // Cambiar stock
    public void changeStock(int productId, int quantity) {
        productRepository.findById(productId).ifPresent(product -> {
            product.setQuantity(quantity);
            productRepository.saveProduct(product);
        });
    }

    // Incrementar stock
    public void increaseStock(int productId, int quantity) {
        productRepository.findById(productId).ifPresent(product -> {
            int newQuantity = product.getQuantity() + quantity;
            product.setQuantity(newQuantity);
            productRepository.saveProduct(product);
        });
    }

    // Cancelar pedido e Incrementar el stock de los productos de ese pedido
    public void increaseStockOfProducts(Map<Integer, Integer> products) {
        for (Integer key : products.keySet()) {
            increaseStock(key, products.get(key));
        }
    }

    // Disminuir stock
    public void decreaseStock(int productId, int quantity) {
        productRepository.findById(productId).ifPresentOrElse(product -> {
            int newQuantity = product.getQuantity() - quantity;
            if (newQuantity < ZERO) {
                throw new IllegalArgumentException("Stock cannot be negative after decrease.");
            }
            product.setQuantity(newQuantity);
            productRepository.saveProduct(product);
        }, () -> {
            throw new IllegalArgumentException("Product with ID " + productId + " does not exist.");
        });
    }

    // Añadir producto
    public ProductDTO addProduct(String name, String type, double weight, int quantity, String state, float price) {
        if (name == null || type == null) {
            throw new IllegalArgumentException("Product name and type cannot be null.");
        }

        if (weight < ZERO || quantity < ZERO) {
            throw new IllegalArgumentException("Product weight and quantity must be non-negative.");
        }

        Product newProduct = new Product(name, type, weight, quantity, state, price);
        productRepository.saveProduct(newProduct);
        return new ProductDTO(newProduct);
    }

    // Eliminar producto por ID
    public void deleteProductById(int productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            productRepository.deleteProduct(product.get());
        } else {
            throw new IllegalArgumentException("Product with ID " + productId + " does not exist.");
        }
    }
}
