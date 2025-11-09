package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ProveedorMapper;
import com.everywhere.backend.model.dto.ProveedorRequestDTO;
import com.everywhere.backend.model.dto.ProveedorResponseDTO;
import com.everywhere.backend.model.entity.Proveedor;
import com.everywhere.backend.repository.ProveedorRepository;
import com.everywhere.backend.service.ProveedorService;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "proveedores")
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;

    @Override
    @Transactional
    @CacheEvict(
        value = { 
            "proveedores", 
            "cotizacionConDetalles", "detallesCotizacion", "detalleCotizacionById", "detallesPorCotizacionId",
            "liquidacionConDetalles", "detallesLiquidacion", "detalleLiquidacionById", "detallesPorLiquidacionId", "detallesSinLiquidacionPorId"
        },
        allEntries = true)
    public ProveedorResponseDTO create(ProveedorRequestDTO proveedorRequestDTO) {
        if (proveedorRequestDTO.getRuc() != null && proveedorRepository.existsByRuc(proveedorRequestDTO.getRuc()))
            throw new DataIntegrityViolationException("Ya existe un proveedor con el RUC: " + proveedorRequestDTO.getRuc());

        Proveedor proveedor = proveedorMapper.toEntity(proveedorRequestDTO);
        return proveedorMapper.toResponseDTO(proveedorRepository.save(proveedor));
    }

    @Override
    @Transactional
    @CachePut(value = "proveedorById", key = "#id")
    @CacheEvict(
        value = {
            "proveedores",
            "cotizacionConDetalles", "detallesCotizacion", "detalleCotizacionById", "detallesPorCotizacionId",
            "liquidacionConDetalles", "detallesLiquidacion", "detalleLiquidacionById", "detallesPorLiquidacionId", "detallesSinLiquidacionPorId"
        },
        allEntries = true)
    public ProveedorResponseDTO update(Integer id, ProveedorRequestDTO proveedorRequestDTO) {
        if (!proveedorRepository.existsById(id))
            throw new ResourceNotFoundException("Proveedor no encontrado con ID: " + id);

        Proveedor proveedor = proveedorRepository.findById(id).get();
        
        if (proveedorRequestDTO.getRuc() != null && 
            proveedorRepository.existsByRuc(proveedorRequestDTO.getRuc()) &&
            !proveedorRequestDTO.getRuc().equals(proveedor.getRuc())) {
            throw new DataIntegrityViolationException("Ya existe un proveedor con el RUC: " + proveedorRequestDTO.getRuc());
        }

        proveedorMapper.updateEntityFromDTO(proveedorRequestDTO, proveedor);
        return proveedorMapper.toResponseDTO(proveedorRepository.save(proveedor));
    }

    @Override
    @Cacheable(value = "proveedorById", key = "#id")
    public ProveedorResponseDTO getById(Integer id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));
        return proveedorMapper.toResponseDTO(proveedor);
    }

    @Override
    @Cacheable
    public List<ProveedorResponseDTO> getAll() {
        return mapToResponseList(proveedorRepository.findAll());
    }

    @Override
    @Transactional
    @CacheEvict(
        value = { 
            "proveedores", "proveedorById", 
            "cotizacionConDetalles", "detallesCotizacion", "detalleCotizacionById", "detallesPorCotizacionId",
            "liquidacionConDetalles", "detallesLiquidacion", "detalleLiquidacionById", "detallesPorLiquidacionId", "detallesSinLiquidacionPorId"
        },
        allEntries = true
    )
    public void delete(Integer id) {
        if (!proveedorRepository.existsById(id))
            throw new ResourceNotFoundException("Proveedor no encontrado con ID: " + id);
        proveedorRepository.deleteById(id);
    }

    private List<ProveedorResponseDTO> mapToResponseList(List<Proveedor> proveedores) {
        return proveedores.stream().map(proveedorMapper::toResponseDTO).toList();
    }
}