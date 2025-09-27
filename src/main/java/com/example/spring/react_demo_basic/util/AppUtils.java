package com.example.spring.react_demo_basic.util;

import com.example.spring.react_demo_basic.dto.ProductDTO;
import com.example.spring.react_demo_basic.entity.Product;
import org.springframework.beans.BeanUtils;

public class AppUtils {
    private AppUtils() {
    }

    public static Product toEntity(ProductDTO dto) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        return product;
    }

    public static ProductDTO toDto(Product entity) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
