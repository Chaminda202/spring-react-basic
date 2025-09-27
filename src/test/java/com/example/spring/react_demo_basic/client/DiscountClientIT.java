package com.example.spring.react_demo_basic.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import reactor.test.StepVerifier;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class DiscountClientIT {

    @Container
    static WireMockContainer wiremock = new WireMockContainer("wiremock/wiremock:2.35.0");

    @DynamicPropertySource
    static void registerBaseUrl(DynamicPropertyRegistry registry) {
        // Inject WireMock container URL into Spring Boot property
        registry.add("external.discount-api.base-url", wiremock::getBaseUrl);
    }

    @Autowired
    DiscountClient discountClient;

    @BeforeEach
    void startWireMock() {
        // Create WireMock client pointing to the container’s host + port
        WireMock wireMockClient = new WireMock(wiremock.getHost(), wiremock.getMappedPort(8080));

        wireMockClient.register(
                WireMock.get(WireMock.urlPathEqualTo("/api/special"))
                        .withQueryParam("category", WireMock.equalTo("Electronic"))
                        .withQueryParam("name", WireMock.equalTo("Phone"))
                        .willReturn(WireMock.okJson("{\"category\":\"Electronic\",\"name\":\"Phone\",\"discount\":10}"))
        );
    }

    @Test
    void getProductDiscountTest() {
        StepVerifier.create(discountClient.getProductDiscount("Electronic", "Phone"))
                .expectNextMatches(d -> d.name().equals("Phone") && d.category().equals("Electronic"))
                .verifyComplete();
    }
}
