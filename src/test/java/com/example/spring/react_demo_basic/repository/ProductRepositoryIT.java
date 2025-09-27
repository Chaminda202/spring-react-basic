package com.example.spring.react_demo_basic.repository;

import com.example.spring.react_demo_basic.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@Testcontainers
public class ProductRepositoryIT {
    @Autowired
    private ProductRepository productRepository;

    // Start a MongoDB container before tests run
    @Container
    @ServiceConnection //
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer("mongo:8.0.14"); // Choose a stable tag

    // Dynamically override Spring Boot MongoDB URI to use Testcontainers
    /*
    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
    */

    @Test
    void contextLoads() {
        // Just to verify the container is running
        System.out.println("MongoDB URL: " + mongoDBContainer.getReplicaSetUrl());
    }

    @Test
    void saveAndRetrieveProduct() {
        Product product = new Product(null, "Laptop", 1200, 500011.0);

        Mono<Product> savedAndFetched = productRepository.save(product)
                .flatMap(savedProd -> productRepository.findById(savedProd.getId()));

        StepVerifier.create(savedAndFetched)
                .expectNextMatches(p -> p.getName().equals("Laptop") && p.getPrice() == 500011.0)
                .verifyComplete();
    }
}
