package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ProductoMapper;
import com.everywhere.backend.model.dto.ProductoRequestDTO;
import com.everywhere.backend.model.dto.ProductoResponseDTO;
import com.everywhere.backend.model.entity.Producto;
import com.everywhere.backend.repository.ProductoRepository;
import com.everywhere.backend.service.ProductoService;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors; 

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "productos")
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Override
    @Transactional
        @CacheEvict(value = {
            "productos",
            "productoById",
            "detallesPorCotizacionId",
            "detalleCotizacionById",
            "detallesCotizacion",
            "liquidacionConDetalles",
            "liquidacion"
            }, 
            allEntries = true)
    public ProductoResponseDTO create(ProductoRequestDTO productoRequestDTO) {
        Producto producto = productoMapper.toEntity(productoRequestDTO);
        return productoMapper.toResponseDTO(productoRepository.save(producto));
    }

    @Override
    @Transactional
    @CachePut(value = "productoById", key = "#id")
    @CacheEvict(value = {
            "productos",
            "cotizacionConDetalles", "detallesPorCotizacionId", "detalleCotizacionById","detallesCotizacion",
            "liquidaciones", "liquidacion", "liquidacionConDetalles", 
            "detallesLiquidacion", "detalleLiquidacionById", "detallesPorLiquidacionId", "detallesSinLiquidacionPorId"
            },
            allEntries = true)
    public ProductoResponseDTO update(Integer id, ProductoRequestDTO productoRequestDTO) {
        if (!productoRepository.existsById(id))
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);

        Producto producto = productoRepository.findById(id).get();
        
        if (productoRequestDTO.getTipo() != null && 
            productoRepository.existsProductosByTipo(productoRequestDTO.getTipo()) &&
            !productoRequestDTO.getTipo().equals(producto.getTipo())) {
            throw new DataIntegrityViolationException("Ya existe un producto con el tipo: " + productoRequestDTO.getTipo());
        }

        productoMapper.updateEntityFromDTO(productoRequestDTO, producto);
        return productoMapper.toResponseDTO(productoRepository.save(producto));
    }

    @Override
    @Cacheable(value = "productoById", key = "#id")
    public ProductoResponseDTO getById(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return productoMapper.toResponseDTO(producto);
    }

    @Override
    @Cacheable
    public List<ProductoResponseDTO> getAll() {
        return mapToResponseList(productoRepository.findAll());
    }

    @Override
    @Transactional
        @CacheEvict(
            value = {
            "productos", "productoById",
            "cotizacionConDetalles", "detallesCotizacion", "detalleCotizacionById", "detallesPorCotizacionId",
            "liquidaciones", "liquidacion", "liquidacionConDetalles", 
            "detallesLiquidacion", "detalleLiquidacionById", "detallesPorLiquidacionId", "detallesSinLiquidacionPorId"
            },
            allEntries = true)
    public void delete(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    private List<ProductoResponseDTO> mapToResponseList(List<Producto> productos) {
        return productos.stream()
                .map(productoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

}
