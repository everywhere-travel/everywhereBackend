package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ProductoRequestDTO;
import com.everywhere.backend.model.dto.ProductoResponseDTO;
import com.everywhere.backend.model.entity.Producto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductoMapper {

    private final ModelMapper modelMapper;

    public Producto toEntity(ProductoRequestDTO productoRequestDto) {
        Producto producto = modelMapper.map(productoRequestDto, Producto.class);
        producto.setCodigo(UUID.randomUUID().toString());
        producto.setCreado(LocalDateTime.now());
        producto.setActualizado(LocalDateTime.now());
        return producto;
    }

    public ProductoResponseDTO toResponse(Producto producto) {
        return modelMapper.map(producto, ProductoResponseDTO.class);
    }

    public void updateEntityFromDTO(ProductoRequestDTO productoRequestDto, Producto producto) {
        modelMapper.map(productoRequestDto, producto);
        producto.setActualizado(LocalDateTime.now());
    }
}
