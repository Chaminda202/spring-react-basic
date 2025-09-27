package com.example.spring.react_demo_basic.controller;

import com.example.spring.react_demo_basic.dto.ProductDTO;
import com.example.spring.react_demo_basic.entity.Product;
import com.example.spring.react_demo_basic.service.ProductService;
import com.example.spring.react_demo_basic.util.AppUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;

@WebFluxTest(controllers = ProductController.class)
public class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ProductService productService;  // Mock service layer

    private Product product1;
    private Product product2;
    private ProductDTO productDTO1;
    private ProductDTO productDTO2;

    @BeforeEach
    public void setup() {
        product1 = new Product("1", "Laptop", 1200, 5000.0);
        product2 = new Product("2", "Phone", 800, 9000.0);
        productDTO1 = AppUtils.toDto(product1);
        productDTO2 = AppUtils.toDto(product2);
    }

    @Test
    public void getProductsTest() {
        Mockito.when(productService.getProducts())
                .thenReturn(Flux.just(productDTO1, productDTO2));

        /*
        webTestClient.get().uri("/product")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDTO.class)
                .hasSize(2);
        */

        Flux<ProductDTO> responseBody = webTestClient.get().uri("/product")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductDTO.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNext(productDTO1)
                .expectNext(productDTO2)
                .verifyComplete();
    }

    @Test
    public void getProductByIdTest() {
        Mockito.when(productService.getProductById(any()))
                .thenReturn(Mono.just(productDTO1));

        /*
        webTestClient.get().uri("/product/{id}", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDTO.class)
                .consumeWith(resp -> {
                    ProductDTO productDTO = resp.getResponseBody();
                    assert productDTO != null;
                    assert productDTO.getId().equals("1");
                });
       */

        Flux<ProductDTO> responseBody = webTestClient.get().uri("/product/{id}", "1")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductDTO.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNext(productDTO1)
                .verifyComplete();
    }

    @Test
    public void getProductsInRageTest() {
        Mockito.when(productService.getProductsInRage(anyDouble(), anyDouble()))
                .thenReturn(Flux.just(productDTO1));

        // "/product/price-range"
        Flux<ProductDTO> responseBody = webTestClient.get().uri(uriBuilder
                        -> uriBuilder.path("/product/price-rage")
                        .queryParam("min", 5.0)
                        .queryParam("max", 50000.0)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductDTO.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNext(productDTO1)
                .verifyComplete();
    }

    @Test
    public void createProductTest() {
        Mockito.when(productService.createProduct(any()))
                .thenReturn(Mono.just(productDTO1));

        Flux<ProductDTO> responseBody = webTestClient.post().uri("/product")
                .body(Mono.just(productDTO1), ProductDTO.class)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductDTO.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNext(productDTO1)
                .verifyComplete();
    }

    @Test
    public void updateProductTest() {
        Mockito.when(productService.updateProduct(any(), anyString()))
                .thenReturn(Mono.just(productDTO2));

        Flux<ProductDTO> responseBody = webTestClient.put().uri("/product/{id}", "2")
                .body(Mono.just(productDTO2), ProductDTO.class)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductDTO.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNextMatches(dto -> dto.getName().equals("Phone"))
                .verifyComplete();
    }

    @Test
    public void deleteProductTest() {
        Mockito.when(productService.deleteProduct(anyString()))
                .thenReturn(Mono.empty());

        webTestClient.delete().uri("/product/{id}", "2")
                .exchange()
                .expectStatus().isOk();

    }
}
