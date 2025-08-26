package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.ProductoRequestDto;
import com.everywhere.backend.model.dto.ProductoResponse;

import java.util.List;
import java.util.Optional;

public interface ProductoService {

    ProductoResponse create(ProductoRequestDto dto);

    ProductoResponse update(Integer id, ProductoRequestDto dto);

    Optional<ProductoResponse> getById(Integer id);

    List<ProductoResponse> getAll();

    void delete(Integer id);

    Optional<ProductoResponse> getByCodigo(String codigo);

}
