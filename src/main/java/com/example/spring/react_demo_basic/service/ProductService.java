package com.example.spring.react_demo_basic.service;

import com.example.spring.react_demo_basic.dto.ProductDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Flux<ProductDTO> getProducts();
    Mono<ProductDTO> getProductById(String id);
    Flux<ProductDTO> getProductsInRage(double min, double max);

    Mono<ProductDTO> createProduct(Mono<ProductDTO> dto);
    Mono<ProductDTO> updateProduct(Mono<ProductDTO> dto, String id);
    Mono<Void> deleteProduct(String id);
}
