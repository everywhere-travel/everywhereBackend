package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ProductoRequestDto;
import com.everywhere.backend.model.dto.ProductoResponse;
import com.everywhere.backend.model.entity.Producto;

import java.time.LocalDateTime;
import java.util.UUID;


public class ProductoMapper {

    // Request → Entity
    public static Producto toEntity(ProductoRequestDto dto) {
        Producto producto = new Producto();
        producto.setCodigo(UUID.randomUUID().toString()); // código aleatorio
        producto.setDescripcion(dto.getDescripcion());
        producto.setTipo(dto.getTipo());

        LocalDateTime now = LocalDateTime.now();
        producto.setCreado(now);
        producto.setActualizado(now);

        return producto;
    }

    // Entity → Response
    public static ProductoResponse toResponse(Producto entity) {
        ProductoResponse dto = new ProductoResponse();
        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setTipo(entity.getTipo());
        dto.setCreado(entity.getCreado());
        dto.setActualizado(entity.getActualizado());
        return dto;
    }

    // Actualizar producto existente con datos del request
    public static void updateEntity(Producto producto, ProductoRequestDto dto) {
        producto.setDescripcion(dto.getDescripcion());
        producto.setTipo(dto.getTipo());
        producto.setActualizado(LocalDateTime.now());
    }
}