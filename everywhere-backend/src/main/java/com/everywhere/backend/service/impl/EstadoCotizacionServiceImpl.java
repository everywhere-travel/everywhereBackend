package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.EstadoCotizacionMapper;
import com.everywhere.backend.model.dto.EstadoCotizacionRequestDTO;
import com.everywhere.backend.model.dto.EstadoCotizacionResponseDTO;
import com.everywhere.backend.model.entity.EstadoCotizacion;
import com.everywhere.backend.repository.EstadoCotizacionRepository;
import com.everywhere.backend.service.EstadoCotizacionService;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "estadosCotizacion")
public class EstadoCotizacionServiceImpl implements EstadoCotizacionService {

    private final EstadoCotizacionRepository estadoCotizacionRepository;
    private final EstadoCotizacionMapper estadoCotizacionMapper;

    @Override
    @Transactional
    @CacheEvict(
        value = {
            "estadosCotizacion", "estadoCotizacionById",
            "cotizaciones", "cotizacionById", "cotizacionConDetalles", "cotizacionesSinLiquidacion"
        }, allEntries = true)
    public EstadoCotizacionResponseDTO create(EstadoCotizacionRequestDTO estadoCotizacionRequestDTO) {
        EstadoCotizacion estadoCotizacion = estadoCotizacionMapper.toEntity(estadoCotizacionRequestDTO); 
        return estadoCotizacionMapper.toResponseDTO(estadoCotizacionRepository.save(estadoCotizacion));
    }


    @Override
    @Transactional
    @CachePut(value = "estadoCotizacionById", key = "#id")
    @CacheEvict(
        value = {
            "estadosCotizacion",
            "cotizaciones", "cotizacionById", "cotizacionConDetalles", "cotizacionesSinLiquidacion"
        },
        allEntries = true
    )
    public EstadoCotizacionResponseDTO update(Integer id, EstadoCotizacionRequestDTO estadoCotizacionRequestDTO) {
        if (!estadoCotizacionRepository.existsById(id))
            throw new ResourceNotFoundException("Estado de Cotización no encontrado con ID: " + id);

        EstadoCotizacion existing = estadoCotizacionRepository.findById(id).get();
        estadoCotizacionMapper.updateEntityFromDTO(estadoCotizacionRequestDTO, existing); 
        return estadoCotizacionMapper.toResponseDTO(estadoCotizacionRepository.save(existing));
    }

    @Override
    @Cacheable(value = "estadoCotizacionById", key = "#id")
    public EstadoCotizacionResponseDTO getById(Integer id) {
        return estadoCotizacionRepository.findById(id).map(estadoCotizacionMapper::toResponseDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Estado de Cotización no encontrado con ID: " + id));
    }

    @Override
    @Cacheable
    public List<EstadoCotizacionResponseDTO> getAll() {
        return mapToResponseList(estadoCotizacionRepository.findAll());
    }

    @Override
    @Transactional
    @CacheEvict(
        value = {
            "estadosCotizacion", "estadoCotizacionById",
            "cotizaciones", "cotizacionById", "cotizacionConDetalles", "cotizacionesSinLiquidacion"
        }, allEntries = true)
    public void delete(Integer id) {
        if (!estadoCotizacionRepository.existsById(id))
            throw new ResourceNotFoundException("Estado de Cotización no encontrado con ID: " + id);
        estadoCotizacionRepository.deleteById(id);
    }

    private List<EstadoCotizacionResponseDTO> mapToResponseList(List<EstadoCotizacion> estadosCotizacion) {
        return estadosCotizacion.stream().map(estadoCotizacionMapper::toResponseDTO).toList();
    }
}