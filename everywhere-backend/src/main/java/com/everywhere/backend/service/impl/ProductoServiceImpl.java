package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.ProductoMapper;
import com.everywhere.backend.model.dto.ProductoRequestDto;
import com.everywhere.backend.model.dto.ProductoResponse;
import com.everywhere.backend.model.entity.Producto;
import com.everywhere.backend.repository.ProductoRepository;
import com.everywhere.backend.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Override
    public ProductoResponse create(ProductoRequestDto dto) {
        Producto entity = ProductoMapper.toEntity(dto);
        Producto saved = productoRepository.save(entity);
        return ProductoMapper.toResponse(saved);
    }

    @Override
    public ProductoResponse update(Integer id, ProductoRequestDto dto) {
        Producto entity = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // actualizar campos
        ProductoMapper.updateEntity(entity, dto);
        Producto updated = productoRepository.save(entity);

        return ProductoMapper.toResponse(updated);
    }

    @Override
    public Optional<ProductoResponse> getById(Integer id) {
        return productoRepository.findById(id)
                .map(ProductoMapper::toResponse);
    }

    @Override
    public List<ProductoResponse> getAll() {
        return productoRepository.findAll()
                .stream()
                .map(ProductoMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }

    @Override
    public Optional<ProductoResponse> getByCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo)
                .map(ProductoMapper::toResponse);
    }

}
