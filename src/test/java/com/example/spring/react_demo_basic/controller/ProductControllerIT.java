package com.example.spring.react_demo_basic.controller;

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
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
public class ProductControllerIT {
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

    private final Product product1 = new Product("1", "Laptop", 1200, 500011.0);
    private final Product product2 = new Product("2", "Phone", 800, 900011.0);

    /**
     * Fully reactive setup: delete all + seed data without blocking
     */
    @BeforeEach
    void setup() {
        repository.deleteAll()
                .thenMany(Flux.just(product1, product2))
                .flatMap(repository::save)
                .then()
                .as(StepVerifier::create)  // ensures setup completes
                .verifyComplete();
    }

    @Test
    void getProductsTest() {
        webTestClient.get().uri("/product")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .hasSize(2)
                .consumeWith(resp -> resp.getResponseBody().forEach(System.out::println));
    }

    @Test
    void crudFlowTest() {

    }
}
