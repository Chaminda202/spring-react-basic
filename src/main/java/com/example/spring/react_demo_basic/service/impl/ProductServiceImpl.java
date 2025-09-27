package com.example.spring.react_demo_basic.service.impl;

import com.example.spring.react_demo_basic.dto.ProductDTO;
import com.example.spring.react_demo_basic.entity.Product;
import com.example.spring.react_demo_basic.mapper.ProductMapper;
import com.example.spring.react_demo_basic.repository.ProductRepository;
import com.example.spring.react_demo_basic.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service(value = "productService")
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

   /* public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }*/

    @Override
    public Flux<ProductDTO> getProducts() {
        return this.productRepository.findAll()
                .map(productMapper::toDto);
    }

    @Override
    public Mono<ProductDTO> getProductById(String id) {
        return this.productRepository.findById(id)
                .map(productMapper::toDto);
    }

    @Override
    public Flux<ProductDTO> getProductsInRage(double min, double max) {
        return this.productRepository.findByPriceBetween(Range.closed(min, max))
                .map(productMapper::toDto);
    }

    @Override
    public Mono<ProductDTO> createProduct(Mono<ProductDTO> dto) {
        Mono<Product> map = dto.map(productMapper::toEntity);

        return dto.map(productMapper::toEntity)
                .flatMap(productRepository::insert)
                .map(productMapper::toDto);
    }

     /*@Override // Approach 01
    public Mono<ProductDTO> updateProduct(Mono<ProductDTO> dto, String id) {
        return productRepository.findById(id)
                .flatMap(p -> dto.map(productMapper::toEntity))
                .doOnNext(e -> e.setId(id))
                .flatMap(productRepository::save).map(productMapper::toDto);
    }*/

    /*
     * Using zipWith
     */
    @Override // Approach 02
    public Mono<ProductDTO> updateProduct(Mono<ProductDTO> dto, String id) {
        return productRepository.findById(id)
                .zipWith(dto.map(productMapper::toEntity), (existing, update) -> {
                    update.setId(existing.getId());
                    return update;
                })
                .flatMap(productRepository::save)
                .map(productMapper::toDto);
    }



    @Override
    public Mono<Void> deleteProduct(String id) {
        return productRepository.deleteById(id);
    }
}
