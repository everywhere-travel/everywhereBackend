package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ProductoMapper;
import com.everywhere.backend.model.dto.ProductoRequestDTO;
import com.everywhere.backend.model.dto.ProductoResponseDTO;
import com.everywhere.backend.model.entity.Producto;
import com.everywhere.backend.repository.ProductoRepository;
import com.everywhere.backend.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
        return productoMapper.toResponseDTO(productoRepository.save(producto));
    }

    @Override
    public ProductoResponseDTO update(Integer id, ProductoRequestDTO productoRequestDTO) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        if (productoRequestDTO.getTipo() != null && productoRepository.existsProductosByTipo(productoRequestDTO.getTipo())) {
            throw new DataIntegrityViolationException("Ya existe  un proveedor con el tipo: " + productoRequestDTO.getTipo());
        }
        productoMapper.updateEntityFromDTO(productoRequestDTO, producto);
        return productoMapper.toResponseDTO(productoRepository.save(producto));
    }

    @Override
    public ProductoResponseDTO getById(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return productoMapper.toResponseDTO(producto);
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
