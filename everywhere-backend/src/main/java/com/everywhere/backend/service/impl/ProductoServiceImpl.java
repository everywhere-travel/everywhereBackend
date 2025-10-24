package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.ProductoMapper;
import com.everywhere.backend.model.dto.ProductoRequestDTO;
import com.everywhere.backend.model.dto.ProductoResponseDTO;
import com.everywhere.backend.model.entity.Producto;
import com.everywhere.backend.repository.ProductoRepository;
import com.everywhere.backend.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Override
    public ProductoResponseDTO create(ProductoRequestDTO dto) {
        Producto entity = ProductoMapper.toEntity(dto);
        Producto saved = productoRepository.save(entity);
        return ProductoMapper.toResponse(saved);
    }

    @Override
    public ProductoResponseDTO update(Integer id, ProductoRequestDTO dto) {
        Producto entity = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // actualizar campos
        ProductoMapper.updateEntity(entity, dto);
        Producto updated = productoRepository.save(entity);

        return ProductoMapper.toResponse(updated);
    }

    @Override
    public Optional<ProductoResponseDTO> getById(Integer id) {
        return productoRepository.findById(id)
                .map(ProductoMapper::toResponse);
    }

    @Override
    public List<ProductoResponseDTO> getAll() {
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
    public Optional<ProductoResponseDTO> getByCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo)
                .map(ProductoMapper::toResponse);
    }

}
