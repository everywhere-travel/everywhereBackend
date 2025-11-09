package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.ViajeroRequestDTO;
import com.everywhere.backend.model.dto.ViajeroResponseDTO;
import com.everywhere.backend.model.entity.Viajero;
import com.everywhere.backend.repository.ViajeroRepository;
import com.everywhere.backend.service.ViajeroService;
import com.everywhere.backend.exceptions.ResourceNotFoundException; 
import com.everywhere.backend.mapper.ViajeroMapper;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
 
import java.util.List; 

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "viajeros")
@Transactional(readOnly = true)
public class ViajeroServiceImpl implements ViajeroService {

    private final ViajeroRepository viajeroRepository;
    private final ViajeroMapper viajeroMapper;

    @Override
    @Cacheable
    public List<ViajeroResponseDTO> findAll() {
        return mapToResponseList(viajeroRepository.findAll());
    }

    @Override
    @Cacheable(value = "viajeroById", key = "#id")
    public ViajeroResponseDTO findById(Integer id) {
        Viajero viajero = viajeroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viajero no encontrado con ID: " + id));
        return viajeroMapper.toResponseDTO(viajero);
    }

    @Override
    @Cacheable(value = "viajerosByNacionalidad", key = "#nacionalidad")
    public List<ViajeroResponseDTO> findByNacionalidad(String nacionalidad) {
        return mapToResponseList(viajeroRepository.findByNacionalidadIgnoreAccents(nacionalidad));
    }

    @Override
    @Cacheable(value = "viajerosByResidencia", key = "#residencia")
    public List<ViajeroResponseDTO> findByResidencia(String residencia) {
        return mapToResponseList(viajeroRepository.findByResidenciaIgnoreAccents(residencia));
    }

    @Override
    @Transactional
    @CacheEvict(
        value = {
            "viajeros", "viajerosByNacionalidad", "viajerosByResidencia",
            "detallesLiquidacion", "detalleLiquidacionById", "detallesPorLiquidacionId", "detallesSinLiquidacionPorId",
            "liquidacionConDetalles"
        }, allEntries = true)
    public ViajeroResponseDTO save(ViajeroRequestDTO viajeroRequestDTO) {
        Viajero viajero = viajeroMapper.toEntity(viajeroRequestDTO); 
        return viajeroMapper.toResponseDTO(viajeroRepository.save(viajero));
    }

    @Override
    @CachePut(value = "viajeroById", key = "#id")
    @Transactional
    @CacheEvict(
        value = {
            "viajeros", "viajerosByNacionalidad", "viajerosByResidencia",
            "detallesLiquidacion", "detalleLiquidacionById", "detallesPorLiquidacionId", "detallesSinLiquidacionPorId",
            "liquidacionConDetalles"
        }, allEntries = true)
    public ViajeroResponseDTO patch(Integer id, ViajeroRequestDTO viajeroRequestDTO) {
        if (!viajeroRepository.existsById(id))
            throw new ResourceNotFoundException("Viajero no encontrado con ID: " + id);

        Viajero existingViajero = viajeroRepository.findById(id).get();
        viajeroMapper.updateEntityFromDTO(viajeroRequestDTO, existingViajero);
        existingViajero = viajeroRepository.save(existingViajero);
        return viajeroMapper.toResponseDTO(existingViajero);
    }

    @Override
    @Transactional
    @CacheEvict(
        value = {
            "viajeros", "viajeroById", "viajerosByNacionalidad", "viajerosByResidencia",
            "detallesLiquidacion", "detalleLiquidacionById", "detallesPorLiquidacionId", "detallesSinLiquidacionPorId",
            "liquidacionConDetalles"
        }, 
        allEntries = true)
    public void deleteById(Integer id) {
        if (!viajeroRepository.existsById(id)) throw new ResourceNotFoundException("Viajero no encontrado con ID: " + id);
        viajeroRepository.deleteById(id);
    }

    private List<ViajeroResponseDTO> mapToResponseList(List<Viajero> viajeros) {
        return viajeros.stream().map(viajeroMapper::toResponseDTO).toList();
    }
}