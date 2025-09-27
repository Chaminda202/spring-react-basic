package com.example.spring.react_demo_basic.client;

import com.example.spring.react_demo_basic.dto.InventoryDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Service
public class InventoryClient {
    private final WebClient webClient;

    public InventoryClient(WebClient.Builder builder, @Value("${external.inventory-api.base-url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<InventoryDTO> getProductInventory(String category, String productName) {
        // Build url adding query param
        String uri = UriComponentsBuilder.fromUriString("/api/quantity")
                .queryParam("category", category)
                .queryParam("name", productName)
                .build()
                .toUriString();

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(InventoryDTO.class);
    }
}
