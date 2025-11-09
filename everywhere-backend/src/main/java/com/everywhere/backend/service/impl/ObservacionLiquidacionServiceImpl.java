package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ObservacionLiquidacionMapper;
import com.everywhere.backend.model.dto.ObservacionLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.ObservacionLiquidacionResponseDTO;
import com.everywhere.backend.model.entity.Liquidacion;
import com.everywhere.backend.model.entity.ObservacionLiquidacion;
import com.everywhere.backend.repository.LiquidacionRepository;
import com.everywhere.backend.repository.ObservacionLiquidacionRepository;
import com.everywhere.backend.service.ObservacionLiquidacionService;
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
@CacheConfig(cacheNames = "observacionesLiquidacion")
public class ObservacionLiquidacionServiceImpl implements ObservacionLiquidacionService {

    private final ObservacionLiquidacionRepository observacionLiquidacionRepository;
    private final ObservacionLiquidacionMapper observacionLiquidacionMapper;
    private final LiquidacionRepository liquidacionRepository;

    @Override
    @Cacheable
    public List<ObservacionLiquidacionResponseDTO> findAll() {
        return mapToResponseList(observacionLiquidacionRepository.findAll());
    }

    @Override
    @Cacheable(value = "observacionLiquidacionById", key = "#id")
    public ObservacionLiquidacionResponseDTO findById(Long id) {
        ObservacionLiquidacion observacionLiquidacion = observacionLiquidacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Observación de liquidación no encontrada con ID: " + id));

        return observacionLiquidacionMapper.toResponseDTO(observacionLiquidacion);
    }

    @Override
    @Transactional
    @CacheEvict(
        value = {
            "observacionesLiquidacion",
            "observacionesByLiquidacionId",
            "liquidacionConDetalles"
        },
        allEntries = true)
    public ObservacionLiquidacionResponseDTO save(ObservacionLiquidacionRequestDTO observacionLiquidacionRequestDTO) { 
        if (observacionLiquidacionRequestDTO.getLiquidacionId() != null && 
            !liquidacionRepository.existsById(observacionLiquidacionRequestDTO.getLiquidacionId())) {
            throw new ResourceNotFoundException(
                    "Liquidación no encontrada con id " + observacionLiquidacionRequestDTO.getLiquidacionId());
        }

        ObservacionLiquidacion observacionLiquidacion = observacionLiquidacionMapper.toEntity(observacionLiquidacionRequestDTO);

        if (observacionLiquidacionRequestDTO.getLiquidacionId() != null) {
            Liquidacion liquidacion = liquidacionRepository.findById(observacionLiquidacionRequestDTO.getLiquidacionId()).get();
            observacionLiquidacion.setLiquidacion(liquidacion);
        }
        return observacionLiquidacionMapper.toResponseDTO(observacionLiquidacionRepository.save(observacionLiquidacion));
    }

    @Override
    @Transactional
    @CachePut(value = "observacionLiquidacionById", key = "#id")
    @CacheEvict(
        value = {
            "observacionesLiquidacion",
            "observacionesByLiquidacionId",
            "liquidacionConDetalles"
        },
        allEntries = true)
    public ObservacionLiquidacionResponseDTO update(Long id, ObservacionLiquidacionRequestDTO observacionLiquidacionRequestDTO) { 
        if (!observacionLiquidacionRepository.existsById(id))
            throw new ResourceNotFoundException("Observación de liquidación no encontrada con ID: " + id);
 
        if (observacionLiquidacionRequestDTO.getLiquidacionId() != null && 
            !liquidacionRepository.existsById(observacionLiquidacionRequestDTO.getLiquidacionId())) {
            throw new ResourceNotFoundException(
                    "Liquidación no encontrada con id " + observacionLiquidacionRequestDTO.getLiquidacionId());
        }
 
        ObservacionLiquidacion observacionLiquidacion = observacionLiquidacionRepository.findById(id).get();
        observacionLiquidacionMapper.updateEntityFromDTO(observacionLiquidacionRequestDTO, observacionLiquidacion);

        if (observacionLiquidacionRequestDTO.getLiquidacionId() != null) {
            Liquidacion liquidacion = liquidacionRepository.findById(observacionLiquidacionRequestDTO.getLiquidacionId()).get();
            observacionLiquidacion.setLiquidacion(liquidacion);
        }

        return observacionLiquidacionMapper.toResponseDTO(observacionLiquidacionRepository.save(observacionLiquidacion));
    }

    @Override
    @Transactional
    @CacheEvict(
        value = {
            "observacionesLiquidacion",
            "observacionLiquidacionById",
            "observacionesByLiquidacionId",
            "liquidacionConDetalles"
        },
        allEntries = true)
    public void deleteById(Long id) {
        if (!observacionLiquidacionRepository.existsById(id))
            throw new ResourceNotFoundException("No existe una observación de liquidación con ID: " + id);
        observacionLiquidacionRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "observacionesByLiquidacionId", key = "#liquidacionId")
    public List<ObservacionLiquidacionResponseDTO> findByLiquidacionId(Integer liquidacionId) { 
        return mapToResponseList(observacionLiquidacionRepository.findByLiquidacionId(liquidacionId));
    }

    private List<ObservacionLiquidacionResponseDTO> mapToResponseList(List<ObservacionLiquidacion> observaciones) {
        return observaciones.stream().map(observacionLiquidacionMapper::toResponseDTO).toList();
    }
}