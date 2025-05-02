package org.naukma.dev_ice.controller;

import lombok.RequiredArgsConstructor;
import org.naukma.dev_ice.entity.Product;
import org.naukma.dev_ice.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @PostMapping
    public String addProduct(@RequestBody Product product) {
        try {
            productRepository.save(product);
            return "Product added successfully";
        } catch (Exception e) {
            return "Failed to add product: " + e.getMessage();
        }
    }

    @PutMapping("/{id}")
    public String updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        try {
            updatedProduct.setProductId(id);
            productRepository.update(updatedProduct);
            return "Product updated successfully";
        } catch (Exception e) {
            return "Failed to update product: " + e.getMessage();
        }
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        try {
            productRepository.deleteById(id);
            return "🗑Product deleted successfully";
        } catch (Exception e) {
            return "Failed to delete product: " + e.getMessage();
        }
    }
}
