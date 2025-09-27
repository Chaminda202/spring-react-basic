package com.example.spring.react_demo_basic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
@Schema(description = "Product details")
public class ProductDTO {
    @Schema(description = "Unique identifier of the product", example = "12345")
    private String id;
    @Schema(description = "Name of the product", example = "phone")
    private String name;
    @Schema(description = "Quantity of the product", example = "50")
    private int quantity;
    @Schema(description = "Price of the product", example = "1200.0")
    private double price;
}
