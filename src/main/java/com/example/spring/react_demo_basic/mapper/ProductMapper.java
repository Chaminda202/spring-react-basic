package com.example.spring.react_demo_basic.mapper;

import com.example.spring.react_demo_basic.dto.ProductDTO;
import com.example.spring.react_demo_basic.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    // ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    // @Mapping(source = "id", target = "id")
    Product toEntity(ProductDTO dto);

    ProductDTO toDto(Product entity);
}
