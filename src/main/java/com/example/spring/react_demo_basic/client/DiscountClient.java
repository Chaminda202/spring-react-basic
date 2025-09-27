package com.example.spring.react_demo_basic.client;

import com.example.spring.react_demo_basic.dto.DiscountDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Service
public class DiscountClient {
    private final WebClient webClient;

    public DiscountClient(WebClient.Builder builder, @Value("${external.discount-api.base-url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<DiscountDTO> getProductDiscount(String category, String productName) {
        // Build url adding query param
        String uri = UriComponentsBuilder.fromUriString("/api/special")
                .queryParam("category", category)
                .queryParam("name", productName)
                .build()
                .toUriString();

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(DiscountDTO.class);
    }
}
