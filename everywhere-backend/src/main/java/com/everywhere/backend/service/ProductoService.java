package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.ProductoRequestDTO;
import com.everywhere.backend.model.dto.ProductoResponseDTO;

import java.util.List;
import java.util.Optional;

public interface ProductoService {

    ProductoResponseDTO create(ProductoRequestDTO productoRequestDTO);
    ProductoResponseDTO update(Integer id, ProductoRequestDTO productoRequestDTO);
    Optional<ProductoResponseDTO> getById(Integer id);
    List<ProductoResponseDTO> getAll();
    void delete(Integer id);
}
