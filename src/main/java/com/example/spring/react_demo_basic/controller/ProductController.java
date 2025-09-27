package com.example.spring.react_demo_basic.controller;

import com.example.spring.react_demo_basic.dto.ProductDTO;
import com.example.spring.react_demo_basic.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/product")
@AllArgsConstructor
@Tag(name = "Products", description = "Reactive CRUD operations for products")
public class ProductController {
    private final ProductService productService;

    /*
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    */

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve a list of all products")
    public Flux<ProductDTO> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by id", description = "Retrieve a product by passing id")
    public Mono<ProductDTO> getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @GetMapping("/price-rage")
    @Operation(summary = "Get product by price range", description = "Retrieve a list of products by price range")
    public Flux<ProductDTO> getProductsInRage(@RequestParam("min") double min, @RequestParam("max") double max) {
        return productService.getProductsInRage(min, max);
    }

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Create product", description = "Create a product")
    public Mono<ProductDTO> createProduct(@RequestBody Mono<ProductDTO> dto) {
        return productService.createProduct(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update a product")
    public Mono<ProductDTO> updateProduct(@RequestBody Mono<ProductDTO> dto, @PathVariable String id) {
        return productService.updateProduct(dto, id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete a product")
    public Mono<Void> deleteProduct(@PathVariable String id) {
        return productService.deleteProduct(id);
    }
}
