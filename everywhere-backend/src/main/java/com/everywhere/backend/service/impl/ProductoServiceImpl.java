package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
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
    private final ProductoMapper productoMapper;

    @Override
    public ProductoResponseDTO create(ProductoRequestDTO productoRequestDTO) {
        Producto producto = productoMapper.toEntity(productoRequestDTO);
        Producto saved = productoRepository.save(producto);
        return productoMapper.toResponseDTO(saved);
    }

    @Override
    public ProductoResponseDTO update(Integer id, ProductoRequestDTO productoRequestDTO) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        productoMapper.updateEntityFromDTO(productoRequestDTO, producto);
        Producto updated = productoRepository.save(producto);
        return productoMapper.toResponseDTO(updated);
    }

    @Override
    public Optional<ProductoResponseDTO> getById(Integer id) {
        return productoRepository.findById(id)
                .map(productoMapper::toResponseDTO);
    }

    @Override
    public List<ProductoResponseDTO> getAll() {
        return productoRepository.findAll()
                .stream()
                .map(productoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void delete(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

}
