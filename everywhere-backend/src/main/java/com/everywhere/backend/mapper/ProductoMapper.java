package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ProductoRequestDTO;
import com.everywhere.backend.model.dto.ProductoResponseDTO;
import com.everywhere.backend.model.entity.Producto;

import java.time.LocalDateTime; 
import java.util.UUID;


public class ProductoMapper {

    // Request → Entity
    public static Producto toEntity(ProductoRequestDTO dto) {
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
    public static ProductoResponseDTO toResponse(Producto entity) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setTipo(entity.getTipo());
        dto.setCreado(entity.getCreado());
        dto.setActualizado(entity.getActualizado());
        return dto;
    }

    // Actualizar producto existente con datos del request
    public static void updateEntity(Producto producto, ProductoRequestDTO dto) {
        producto.setDescripcion(dto.getDescripcion());
        producto.setTipo(dto.getTipo());
        producto.setActualizado(LocalDateTime.now());
    }
}