package com.example.spring.react_demo_basic.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Testcontainers
@SpringBootTest
public class DiscountClientWebTestClientIT {

    @Container
    static WireMockContainer wiremock = new WireMockContainer("wiremock/wiremock:2.35.0");

    @DynamicPropertySource
    static void registerBaseUrl(DynamicPropertyRegistry registry) {
        // Inject dynamic WireMock URL into Spring property
        registry.add("external.discount-api.base-url", wiremock::getBaseUrl);
    }

    DiscountClient discountClient;

    @BeforeEach
    void setup() {
        // Create DiscountClient pointing to WireMock
        discountClient = new DiscountClient(WebClient.builder(), wiremock.getBaseUrl());

        // Configure WireMock client to point to Testcontainers dynamic port
        WireMock.configureFor(wiremock.getHost(), wiremock.getMappedPort(8080));

        // Stub endpoint with query parameters
        stubFor(get(urlPathEqualTo("/api/special"))
                .withQueryParam("category", equalTo("Electronic"))
                .withQueryParam("name", equalTo("Phone"))
                .willReturn(okJson("{\"category\":\"Electronic\",\"name\":\"Phone\",\"percentage\":1.5}")));
    }

    @Test
    void getProductDiscountReactiveTest() {
        StepVerifier.create(discountClient.getProductDiscount("Electronic", "Phone"))
                .expectNextMatches(d -> {
                    System.out.println("Received DiscountDTO: " + d);
                    return d.name().equals("Phone") &&
                            d.category().equals("Electronic") &&
                            d.percentage() == 1.5;
                })
                .verifyComplete();
    }
}