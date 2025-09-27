package com.example.spring.react_demo_basic.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String name;
    private int quantity;
    private double price;
}
