package com.example.spring.react_demo_basic.controller;

import com.example.spring.react_demo_basic.dto.ProductDTO;
import com.example.spring.react_demo_basic.entity.Product;
import com.example.spring.react_demo_basic.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
public class ProductControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository repository;

    // Start a disposable MongoDB container
    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer("mongo:8.0.14");

    @DynamicPropertySource
    static void configureMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    /**
     * Delete All
     */
    @BeforeEach
    void setup() {
        StepVerifier.create( repository.deleteAll())
                .verifyComplete();
    }

    @Test
    void crudFlowTest() {
        // Create product
        ProductDTO productDTO = new ProductDTO(null, "Laptop", 1200, 500011.0);

        Mono<Product> createdProdMono = webTestClient.post().uri("/product")
                .body(Mono.just(productDTO), ProductDTO.class)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Product.class)
                .getResponseBody()
                .single()
                .cache();

        StepVerifier.create(createdProdMono)
                .assertNext(p -> {
                    assert p.getId() != null;
                    assert p.getName().equals("Laptop");
                })
                .verifyComplete();

        // Read all products
        StepVerifier.create(repository.findAll())
                .expectNextCount(1)
                .verifyComplete();

        // Update product
        ProductDTO updatedproductDTO = new ProductDTO(null, "Laptop Note",
                1300, 56000.50);

        Mono<Product> updateProductMono = createdProdMono.flatMap(createdProd -> webTestClient.put().uri("/product/{id}", createdProd.getId())
                .body(Mono.just(updatedproductDTO), ProductDTO.class)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Product.class)
                .getResponseBody()
                .single())
                .cache();

        StepVerifier.create(updateProductMono)
                .assertNext(p -> {
                    assert p.getId() != null;
                    assert updatedproductDTO.getName().equals(p.getName());
                    assert updatedproductDTO.getPrice() == p.getPrice();
                })
                .verifyComplete();

        StepVerifier.create(repository.findAll())
                .expectNextCount(1)
                .verifyComplete();


        // Delete Product
        StepVerifier.create(updateProductMono.flatMap(prod ->
                        webTestClient.delete()
                                .uri("/product/{id}", prod.getId())
                                .exchange()
                                .expectStatus().isOk()
                                .returnResult(Void.class)
                                .getResponseBody()
                                .then()))
                .verifyComplete();

        // Verify DB is cleared
        StepVerifier.create(repository.findAll()).verifyComplete();
    }
}
